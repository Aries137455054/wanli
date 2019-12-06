package ext.com.wanli.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import ext.com.wanli.part.PartConstant;
import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.FolderHelper;
import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.container.WTContainerTemplate;
import wt.inf.container.WTContainerTemplateRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.ContainerTeamReference;
import wt.inf.template.ContainerTemplateHelper;
import wt.inf.template.DefaultWTContainerTemplate;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;


/**
 *@Description: 容器相关的工具体类 
 *@author zhangmingjie
 */
public class ContainerUtil implements RemoteAccess {

    private static String CLASSNAME = ContainerUtil.class.getName();

    private static Logger logger = LogR.getLogger(CLASSNAME);
    
    
    /**
     * 创建产品
     * @param name  产品名称
     * @param containerTemplateName  容器模板名称
     * @param Desc  描述
     * @param user
     * @throws WTException
     * @throws WTPropertyVetoException
     * @author zhangmingjie
     * @date 2019年12月5日 下午5:06:28
     */
    public static PDMLinkProduct createPDMLinkProduct(String name,String containerTemplateName, String Desc,
			WTPrincipal user) throws WTException, WTPropertyVetoException {
    	PDMLinkProduct product = PDMLinkProduct.newPDMLinkProduct();
		try {   
			boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
			WTPrincipal currentPrincipal = SessionHelper.manager.getPrincipal();
			try {
				WTUser me = (WTUser) SessionHelper.manager.getPrincipal();
		        WTOrganization wtOrg = OrganizationServicesHelper.manager.getOrganization(me);
				WTContainerRef orgContainerRef = WTContainerHelper.service.getOrgContainerRef(wtOrg);
				
				List<WTUser> orgAdmins =  WTPrincipalUtil.findOrgAdmins(wtOrg);
		        if (orgAdmins == null || orgAdmins.size() == 0)
		            SessionHelper.manager.setAdministrator();
		        else {
		            WTUser admin = (WTUser) orgAdmins.get(0);
		            SessionHelper.manager.setAuthenticatedPrincipal(admin.getAuthenticationName());
		        }
				
				WTContainerTemplateRef containerTemplateRef = ContainerTemplateHelper.service
						.getContainerTemplateRef(orgContainerRef, containerTemplateName, PDMLinkProduct.class);
				if (containerTemplateRef == null) {
					logger.error(" >>>>>>>> createProductContainer()  <<<<<< : could not find the product template["+ containerTemplateName + "]!");
					throw new WTException(
							" >>>>>>>> createProductContainer()  <<<<<< : could not find the product template["
									+ containerTemplateName + "]!");
				}
				product.setName(name);
				product.setDescription(Desc);
				product.setContainerReference(orgContainerRef);
				product.setContainerTemplateReference(containerTemplateRef);
				product.setCreator(user);
				product.setOwner(user);
				product = (PDMLinkProduct) WTContainerHelper.service.create(product);
				//初始化产品经理角色
				initProductManager(product, user);
			} finally {
				SessionHelper.manager.setAuthenticatedPrincipal(((WTUser) currentPrincipal).getAuthenticationName());
				SessionServerHelper.manager.setAccessEnforced(accessEnforced);
			}
        } catch (Exception e) {
        	logger.error(e.getMessage(),e); 
        }
		return product;
	}
    
    /**
     * 初始化产品经理角色
     * @param prod
     * @param prodCreator
     * @throws WTException
     * @author zhangmingjie
     * @date 2019年12月5日 下午5:08:46
     */
    public  static void initProductManager(PDMLinkProduct prod, WTPrincipal prodCreator) throws WTException {
    	boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(prod);
		Role pm = Role.toRole("PRODUCT MANAGER");
		try {
		ContainerTeamHelper.service.addMember(containerTeam, pm, prodCreator);
		Enumeration<?> enumeration = containerTeam.getPrincipalTarget(pm);
		WTPrincipal principal;
		while (enumeration.hasMoreElements()) {
			principal = ((WTPrincipalReference) enumeration.nextElement()).getPrincipal();
			if (!principal.equals(prodCreator)){
				ContainerTeamHelper.service.removeMember(containerTeam, pm,principal);
			}
		}
		} catch (WTException e) {
			logger.error(CLASSNAME+".initProductManager:" + e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(accessEnforced);
		}
	}
    
    
    /**
     * 根据容器名和容器class 查询容器
     * 容器类型有：
     *  <br>ExchangeContainer
     *  <br>OrgContainer
     *  <br>PDMLinkProduct
     *  <br>Project2
     *  <br>WTLibrary
     *@Method:  findContainer
     *@Description: TODO
     *@Author: zhangmingjie
     *@param cclass
     *@param name
     *@return
     *@throws Exception 
     *@return QueryResult
     *
     */
    public static QueryResult findContainer(@SuppressWarnings("rawtypes") Class cclass, String name) throws Exception {
        logger.debug(CLASSNAME + ">>>>findContainer: cclass=" + cclass + ",name=" + name);
        QueryResult qr=null;
        WTPrincipal prin=wt.session.SessionHelper.manager.getPrincipal();
        wt.session.SessionHelper.manager.setAdministrator();
        try {
            QuerySpec qs = new QuerySpec(cclass);
            if (name != null && !name.equals("")) {
                qs.appendWhere(new SearchCondition(cclass,
                        WTContainer.NAME, SearchCondition.EQUAL, name), new int[]{0});
            }
            qr = PersistenceHelper.manager.find((StatementSpec) qs);
        }
        finally {
            wt.session.SessionHelper.manager.setPrincipal(prin.getName());
        }
        return qr;
    }
    
    /**
     * 获取pp的容器Reference, 如果pp本身就是WTContainer 则返回自身的Reference
     *@Method:  getContainerRef
     *@Description: TODO
     *@Author: zhangmingjie
     *@param pp
     *@return
     *@throws WTException 
     *@return WTContainerRef
     *
     */
    public static WTContainerRef getContainerRef(Persistable pp) throws WTException {
        logger.debug(CLASSNAME + ">>>>getContainerRef: pp=" + pp);
        WTContainerRef conRef = null;
        if(pp instanceof WTContainer) {
            conRef = WTContainerRef.newWTContainerRef((WTContainer)pp);
        }else if(pp instanceof WTContained) {
            WTContained wtc = (WTContained)pp;
            conRef = wtc.getContainerReference();
        }
        
        return conRef;
    }
    
    /**
     * 获取 Persistable 的容器对象
     *@Method:  getContainer
     *@Description: TODO
     *@Author: zhangmingjie
     *@param pp
     *@return
     */
    public static WTContainer getContainer(Persistable pp) {
        logger.debug(CLASSNAME + ">>>>getContainer: pp=" + pp);
        WTContainer con = null;
        if(pp instanceof WTContainer) {
            return (WTContainer)pp;
        }else if(pp instanceof WTContained) {
            WTContained wtc = (WTContained)pp;
            con = wtc.getContainer();
        }
        return con;
    }
    
    /**
     * 得到站点容器
     *@Method:  getExchangeContainer
     *@Description: TODO
     *@Author: zhangmingjie
     *@return
     *@throws WTException 
     *@return ExchangeContainer
     *
     */
    public static ExchangeContainer getExchangeContainer() throws WTException {
        logger.debug(CLASSNAME + ">>>>getExchangeContainer");
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            return WTContainerHelper.service.getExchangeContainer();
        }
        catch (Exception e) {
            throw new WTException(e);
        }
        finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    
    /**
     * 得到InternetDomain 
     *@Method:  getInternetDomain
     *@Description: TODO
     *@Author: zhangmingjie
     *@return
     *@throws WTException 
     *@return String
     *
     */
    public static String getInternetDomain()  throws WTException {
        logger.debug(CLASSNAME + ">>>>getExchangeContainer");
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            return WTContainerHelper.service.getExchangeContainer().getInternetDomain();
        }
        catch (Exception e) {
            throw new WTException(e);
        }
        finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    
    /**
     * 返回当前用户所在组织的组织容器参考
     *@Method:  getOrgContainerRef
     *@Description: TODO
     *@Author: zhangmingjie
     *@return WTContainerRef
     * @throws WTException 
     */
    public static WTContainerRef getOrgContainerRef() throws WTException {
        logger.debug(CLASSNAME + ">>>>getOrgContainerRef");
        WTUser me = (WTUser) SessionHelper.manager.getPrincipal();
        WTOrganization org = OrganizationServicesHelper.manager.getOrganization(me);
        WTContainerRef orgContainerRef = WTContainerHelper.service.getOrgContainerRef(org);
        return orgContainerRef;
    }
    
    /**
     * 获取站点容器Reference
     *@Method:  getExchangeRef
     *@Description: TODO
     *@Author: zhangmingjie
     *@return
     *@throws WTException
     */
    public static WTContainerRef getExchangeRef() throws WTException {
        logger.debug(CLASSNAME + ">>>> getExchangeRef");
        WTContainerRef exchangeRef = WTContainerHelper.service.getExchangeRef();
        return exchangeRef;
    }
   
   /**
    * 获取当前用户所做组织的指定名称的存储库的reference
    *@Method:  getLibraryRef
    *@Description: TODO
    *@Author: zhangmingjie
    *@param libName 存储库名称
    *@return WTContainerRef
    *
    */
    public static WTContainerRef getLibraryRef(String libName) {
        logger.debug(CLASSNAME + ">>>>getLibraryRef：libName=" + libName);
        try {
            WTPrincipal me = SessionHelper.manager.getPrincipal();
            WTOrganization org = OrganizationServicesHelper.manager.getOrganization(me);
            return getLibraryRef(org.getName(), libName);
        }
        catch (WTException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取指定组织名和存储库名的存储库Reference
     * 
     * @param orgName   组织名称
     * @param libName   存储库名称
     * @return          存储库Reference
     */
    public static WTContainerRef getLibraryRef(String orgName, String libName) {
        logger.debug(CLASSNAME + ">>>>getLibraryRef：orgName=" +  orgName + ", libName=" + libName);
        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        String path = "/wt.inf.container.OrgContainer=" + orgName
                + "/wt.inf.library.WTLibrary=" + libName;
        try {
            WTContainerRef ref = WTContainerHelper.service.getByPath(path);
            WTContainer cont = ref.getReferencedContainer();
            WTPrincipal me = SessionHelper.getPrincipal();
            if (!AccessControlHelper.manager.hasAccess(me, cont, AccessPermission.READ)) {
                Role role = Role.toRole("MEMBERS");
                ContainerTeamReference teamRef = ((WTLibrary) cont).getContainerTeamReference();
                TeamHelper.service.addRolePrincipalMap(role, me, (ContainerTeam)teamRef.getObject());
            }
            return ref;
        }
        catch (WTException e) {
        	logger.debug(CLASSNAME + ">>>>getLibraryRef：存储库 ”"+libName+"“ 不存在");
            e.printStackTrace();
            return null;
        }
        finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
    }

    /**
     * 新建一个Library容器, 使用常规存储库模板, 如没有, 则创建默认存储库模板
     * 
     * @param libName           容器名称
     * @param libDescription    容器描述
     * @return                  容器的reference
     * @throws Exception
     */
    public static WTContainerRef createLibrary(String libName, String libDescription)
            throws Exception {
        logger.debug(CLASSNAME + ">>>>createLibrary：libName=" +  libName + ", libDescription=" + libDescription);
        
        WTContainerRef containerRef = getLibraryRef(libName);
        if(containerRef != null){
        	String containerName = containerRef.getName();
        	logger.debug("containerName="+containerName);
        	return containerRef;
        }else{
        	return createLibrary(libName, libDescription, "常规存储库");
        }
    }
    
    /**
     * 新建一个Library容器, 需指定容器模板名称
     * 
     * @param libName           容器名称
     * @param libDescription    容器描述
     * @param templatename      容器模板名称
     * @return                  容器的reference
     * @throws Exception
     */
    public static WTContainerRef createLibrary(String libName, String libDescription, String templateName) throws Exception {
        logger.debug(CLASSNAME + ">>>>createLibrary：libName=" +  libName + ", libDescription=" + libDescription + ", templateName=" + templateName);
        WTUser me = (WTUser) SessionHelper.manager.getPrincipal();
        WTOrganization org = OrganizationServicesHelper.manager.getOrganization(me);
        
        //获取组织
        //DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
        //WTOrganization org = OrganizationServicesHelper.manager.getOrganization("wlhk", dcp);
        
        WTContainerRef orgContainerRef = WTContainerHelper.service.getOrgContainerRef(org);

        WTLibrary lib = WTLibrary.newWTLibrary();
        WTContainerRef libRef = null;
        
        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        Vector<WTUser> orgAdmins =  WTPrincipalUtil.findOrgAdmins(org);
        if (orgAdmins == null || orgAdmins.size() == 0)
            SessionHelper.manager.setAdministrator();
        else {
            WTUser admin = (WTUser) orgAdmins.get(0);
            SessionHelper.manager.setAuthenticatedPrincipal(admin.getAuthenticationName());
        }
        try {
            lib.setName(libName);
            if (libDescription != null){
            	lib.setDescription(libDescription);
            }
            WTContainerTemplateRef containerTemplateRef = ContainerTemplateHelper.service.getContainerTemplateRef(
                    orgContainerRef, templateName, WTLibrary.class);
            if (containerTemplateRef == null) {
                WTContainerTemplate containerTemplate = DefaultWTContainerTemplate.newDefaultWTContainerTemplate(
                        "默认存储库模板", WTLibrary.class.getName());
                containerTemplate = ContainerTemplateHelper.service.createContainerTemplate(
                        orgContainerRef, containerTemplate);
                containerTemplateRef = WTContainerTemplateRef.newWTContainerTemplateRef(containerTemplate);
            }
            lib.setContainerTemplateReference(containerTemplateRef);
            lib.setContainerReference(orgContainerRef);
            lib = (WTLibrary) WTContainerHelper.service.create(lib);
            lib = (WTLibrary) WTContainerHelper.service.makePublic(lib);
            libRef = WTContainerRef.newWTContainerRef(lib);
            
            try{
            	FolderHelper.service.createSubFolder(PartConstant.FOLDER_PATH_DEFAULT, libRef);
            }catch(Exception e){
            	e.printStackTrace();
            }
        }
        finally {
            SessionHelper.manager.setAuthenticatedPrincipal(me.getAuthenticationName());
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }

        return libRef;
    }

    /**
     * 获取当前用户所做组织的指定名称的产品的reference
     *@Method:  getPDMLinkProductRef
     *@Description: TODO
     *@Author: zhangmingjie
     *@param prdName 产品名称
     *@return WTContainerRef
     *
     */
     public static WTContainerRef getPDMLinkProductRef(String prdName) {
         logger.debug(CLASSNAME + ">>>>getPDMLinkProductRef：prdName=" + prdName);
         try {
             WTPrincipal me = SessionHelper.manager.getPrincipal();
             WTOrganization org = OrganizationServicesHelper.manager.getOrganization(me);
             return getPDMLinkProductRef(org.getName(), prdName);
         }
         catch (WTException e) {
             e.printStackTrace();
             return null;
         }
     }

     /**
      * 获取指定组织名和产品名的产品库Reference
      * 
      * @param orgName   组织名称
      * @param prdName   产品名称
      * @return          产品Reference
      */
     public static WTContainerRef getPDMLinkProductRef(String orgName, String prdName) {
         logger.debug(CLASSNAME + ">>>>getPDMLinkProductRef：orgName=" +  orgName + ", prdName=" + prdName);
         boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
         try {
             String path = "/wt.inf.container.OrgContainer=" + orgName
                     + "/wt.pdmlink.PDMLinkProduct=" + prdName;
             WTContainerRef ref = WTContainerHelper.service.getByPath(path);
             WTContainer cont = ref.getReferencedContainer();
             WTPrincipal me = SessionHelper.getPrincipal();
             if (!AccessControlHelper.manager.hasAccess(me, cont, AccessPermission.READ)) {
                 Role role = Role.toRole("MEMBERS");
                 ContainerTeamReference teamRef = ((WTLibrary) cont).getContainerTeamReference();
                 TeamHelper.service.addRolePrincipalMap(role, me, (ContainerTeam)teamRef.getObject());
             }
             return ref;
         }
         catch (WTException e) {
             e.printStackTrace();
             return null;
         }
         finally {
             SessionServerHelper.manager.setAccessEnforced(accessEnforced);
         }
     }
    
     /**
      * 给 ContainerTeamManaged 团队中的 role 添加参与者。如果role不在团队中，则会自动添加。
      *@Method:  addRolePrincipalToContainerTeam
      *@Description: TODO
      *@Author: zhangmingjie
      *@param contTeamed
      *@param role
      *@param addPrincipalList
      *@return boolean
      *@throws WTRuntimeException
      *@throws WTException
      *@throws RemoteException
      *@throws InvocationTargetException
      */
     public static boolean addRolePrincipalToContainerTeam(ContainerTeamManaged contTeamed, Role role, List<WTPrincipal> addPrincipalList) throws WTRuntimeException, WTException, RemoteException, InvocationTargetException {
         logger.debug(CLASSNAME + ">>>>addRolePrincipalToContainerTeam：contTeamed=" +  contTeamed + ", role=" + role + ", addPrincipalList=" + addPrincipalList);
         boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
         try {
             ContainerTeamReference teamRef = contTeamed.getContainerTeamReference();
             
             ContainerTeam containerTeam = (ContainerTeam)teamRef.getObject();
             for(WTPrincipal prin : addPrincipalList) {
                 //TeamHelper.service.addRolePrincipalMap(role,prin,(ContainerTeam)teamRef.getObject()); not work in windchill 10.2
                 containerTeam.addPrincipal(role, prin);
             }
             return true;
         }finally {
             SessionServerHelper.manager.setAccessEnforced(accessEnforced);
         }
     }
     
     /**
      * 
      *@Method:  isTeamRoleMember
      *@Description: user是否是role中的成员
      *@Author: zhangyan
      *@param teamed
      *@param role
      *@param user
      *@return
      *@throws WTException 
      *@return boolean
      *
      */
     public static boolean isTeamRoleMember(ContainerTeamManaged contTeamed , Role role,WTUser user)throws WTException{
         ContainerTeamReference teamRef = contTeamed.getContainerTeamReference();
         ContainerTeam containerTeam = (ContainerTeam)teamRef.getObject();
         Enumeration pEnum = containerTeam.getPrincipalTarget(role);
         while(pEnum.hasMoreElements()) {
             WTPrincipalReference tempPrinRef = (WTPrincipalReference)pEnum.nextElement();
             WTPrincipal princial = tempPrinRef.getPrincipal();
//             Debug.P(">>>>  search role:"+role.getDisplay() +" princial of role:"+princial.getName() + "  current user:"+user.getName());
             if(princial instanceof WTUser){
                 if(princial.equals(user)){
                     return true;
                 }
             }else if(princial instanceof WTGroup){
                 //如果princial 是组，则应该找到该组下所有人员，在判断
                 Set<WTUser> userSet = WTPrincipalUtil.getGroupMembers((WTGroup)princial);
                 
                 for(WTUser wtUser : userSet) {
                     if(wtUser.equals(user)){
                         return true;
                     }
                 }
                 // end 
                 
             }
         }
         return false;
     }
     
     /**
      * user是否是XX容器中的角色roleAry中的一员
      * @param contTeamed
      * @param roleAry
      * @param user
      * @return true:用户在roleAry中，false:用户不在roleAry中
      * @throws WTException
      */
     public static boolean isTeamRoleMember(ContainerTeamManaged contTeamed ,String[] roleAry,WTUser user)throws WTException{
    	 if(roleAry == null || roleAry.length == 0){
    		 return false;
    	 }
    	 for(String roleStr:roleAry){
    		 Role role = Role.toRole(roleStr);
    		 if(isTeamRoleMember(contTeamed,role,user)){
    			 return true;
    		 }
    	 }
    	 return false;
     }

     /**
      * 删除 ContainerTeamManaged 团队下的 role 包含role中的参与者
      *@Method:  deleteRolePrincipalFromContainerTeam
      *@Description: TODO
      *@Author: zhangmingjie
      *@param contTeamed
      *@param deleteRole
      *@return boolean
      *@throws WTRuntimeException
      *@throws WTException
      *@throws RemoteException
      *@throws InvocationTargetException
      */
     public static boolean deleteRoleFromContainerTeam(ContainerTeamManaged contTeamed, Role deleteRole) throws WTRuntimeException, WTException, RemoteException, InvocationTargetException {
         logger.debug(CLASSNAME + ">>>>deleteRoleFromContainerTeam：contTeamed=" +  contTeamed + ", deleteRole=" + deleteRole );
         boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
         try {
             ContainerTeamReference teamRef = contTeamed.getContainerTeamReference();
             ContainerTeam containerTeam = (ContainerTeam)teamRef.getObject();
             
             logger.debug(">>>> deleteRole:" + deleteRole);
             containerTeam.deleteRole(deleteRole);
             //TeamHelper.service.deleteRole(deleteRole,(ContainerTeam)teamRef.getObject());//not work
             return true;
         }finally {
             SessionServerHelper.manager.setAccessEnforced(accessEnforced);
         }
     }
     
     /**
      * 删除  ContainerTeamManaged 团队下的 role 角色下的参与者 deletePrincipalList 
      *@Method:  deleteRolePrincipalFromContainerTeam
      *@Description: TODO
      *@Author: zhangmingjie
      *@param contTeamed
      *@param role
      *@param deletePrincipalList
      *@return boolean
      *@throws WTRuntimeException
      *@throws WTException
      *@throws RemoteException
      *@throws InvocationTargetException
      */
     public static boolean deleteRolePrincipalFromContainerTeam(ContainerTeamManaged contTeamed, Role role, List<WTPrincipal> deletePrincipalList) throws WTRuntimeException, WTException, RemoteException, InvocationTargetException {
         logger.debug(CLASSNAME + ">>>>deleteRolePrincipalFromContainerTeam：contTeamed=" +  contTeamed + ", role=" + role + ", deletePrincipalList=" + deletePrincipalList);
         boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
         try {
             ContainerTeamReference teamRef = contTeamed.getContainerTeamReference();
             ContainerTeam containerTeam = (ContainerTeam)teamRef.getObject();
             for(WTPrincipal prin : deletePrincipalList) {
            	 logger.debug(">>>> deleteRolePrincipalMap:" + prin.getName());
                 //TeamHelper.service.deleteRolePrincipalMap(role,prin,(ContainerTeam)teamRef.getObject());//not work
                 containerTeam.deletePrincipalTarget(role,prin);
             }
             return true;
         }finally {
             SessionServerHelper.manager.setAccessEnforced(accessEnforced);
         }
     }
     
     
     public static void main(String[] args) throws WTException, WTRuntimeException, RemoteException, InvocationTargetException {
         
         RemoteMethodServer rms = RemoteMethodServer.getDefault();
         rms.setUserName("wcadmin");
         rms.setPassword("wcadmin");
         
         
         ReferenceFactory rf = new ReferenceFactory();
         ContainerTeamManaged tted = (ContainerTeamManaged)rf.getReference(args[0].replaceAll("%3A",":")).getObject();
         Role role = Role.toRole(args[1]);
         WTUser user = WTPrincipalUtil.getUserByName(args[2]);
         
         String command = args[3];
         List<WTPrincipal> userList = new ArrayList<WTPrincipal>();
         userList.add(user);
         
         if("addUser".equalsIgnoreCase(command)) {
             addRolePrincipalToContainerTeam(tted,role,userList);
         }else if("delUser".equalsIgnoreCase(command)) {
             deleteRolePrincipalFromContainerTeam(tted,role,userList);
         }else if("delRole".equalsIgnoreCase(command)) {
             deleteRoleFromContainerTeam(tted,role);
         }
         
         System.out.println(">>>>>>>>>>> ok");
         
     }
     
    /**
     * 
     *@Method:  getAllContainer
     *@Description: 查询系统中的容器
     *@Author: zhangyan
     *@param type 若为空表查所有的，"PDMLinkProduct":只查询产品容器，
     *                              “WTLibrary”：只查询存储库
     *@return
     *@throws WTException
     *@throws RemoteException
     *@throws InvocationTargetException 
     *@return List<WTContainer>
     *
     */
     public static List<WTContainer> getAllContainer(String type) throws WTException, RemoteException, InvocationTargetException{
         List<WTContainer> result = new ArrayList<WTContainer>();
         //设置权限
         boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
         try {
             if(StringUtils.isEmpty(type) || "PDMLinkProduct".equals(type)){
                 //查询产品容器
                 QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
                 QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
                 while(qr.hasMoreElements()){
                     PDMLinkProduct pp = (PDMLinkProduct)qr.nextElement();
                     result.add(pp);
                 }
             }
             
             if(StringUtils.isEmpty(type) || "WTLibrary".equals(type)){
                 //查询存储库
                 QuerySpec qs = new QuerySpec(WTLibrary.class);
                 QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
                 while(qr.hasMoreElements()){
                     WTLibrary pp = (WTLibrary)qr.nextElement();
                     result.add(pp);
                 }
             }
         }
         finally {
             SessionServerHelper.manager.setAccessEnforced(flag);
         }
         return result;
     }
     
     
     public static boolean isTeamRoleMember(ContainerTeamManaged contTeamed ,WTUser user)throws WTException{
         ContainerTeamReference teamRef = contTeamed.getContainerTeamReference();
         ContainerTeam containerTeam = (ContainerTeam)teamRef.getObject();
         //Enumeration pEnum = containerTeam.getPrincipalTarget(role);
         Map<WTGroup, Set<WTPrincipalReference>> pMap = containerTeam.getMembersMap();
         
         Iterator<WTGroup> it = pMap.keySet().iterator();
         while(it.hasNext()) {
             WTGroup group = it.next();
             Set<WTPrincipalReference> principalSet = pMap.get(group);
             
             for(WTPrincipalReference principal : principalSet) {
                 WTPrincipal princial = principal.getPrincipal();
                 
                 if(princial instanceof WTUser){
                     if(princial.equals(user)){
                         return true;
                     }
                 }else if(princial instanceof WTGroup){
                     Set<WTUser> userSet = WTPrincipalUtil.getGroupMembers((WTGroup)princial);
                     
                     for(WTUser wtUser : userSet) {
                         if(wtUser.equals(user)){
                             return true;
                         }
                     }
                 }
             }
         }

         return false;
     }
     
}
