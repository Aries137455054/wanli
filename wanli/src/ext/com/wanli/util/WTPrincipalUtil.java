package ext.com.wanli.util;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.admin.AdministrativeDomainHelper;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;


/**
 * @Description: 参与者相关工具类 
 *@author zhangmingjie
 */
public class WTPrincipalUtil{

    private static String CLASSNAME = WTPrincipalUtil.class.getName();
    private static Logger logger = LogR.getLogger(CLASSNAME);
    
   /**
    * 检查当前用户是否是系统管理员或包括在系统管理员组中(站点管理员)
    *@Method:  isAdmin
    *@Description: TODO
    *@Author: zhangmingjie
    *@Since: 2019-6-17上午10:23:10
    *@return 
    *@return boolean
    *
    */
    public static boolean isAdmin() {
        logger.debug(CLASSNAME + ">>>> isAdmin" );
        try {
            WTPrincipal me = SessionHelper.manager.getPrincipal();
            WTPrincipal admin = SessionHelper.manager.getAdministrator();

            if (me.equals(admin))
                return true;
            DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
            WTGroup group = OrganizationServicesHelper.manager.getGroup(AdministrativeDomainHelper.ADMIN_GROUP_NAME,dcp);
            boolean result = OrganizationServicesHelper.manager.isMember(group, me);
            return result;
        }
        catch (WTException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 判断指定用户是否在groupName的（站点级）群组中
     *@Method:  isMember
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午03:57:36
     *@param groupName
     *@param user
     *@return 
     *@return boolean
     *
     */
    public static boolean isMember(String groupName, WTUser user) {
        logger.debug(CLASSNAME + ">>>> isMember: groupName=" + groupName + ", user=" + user );
        try {
            DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
            WTGroup group = OrganizationServicesHelper.manager.getGroup(groupName,dcp);
            boolean result = isMember(group, user);
            return result;
        }
        catch (WTException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 判断指定用户是否在 group 群组中
     *@Method:  isMember
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-21上午09:20:05
     *@param group
     *@param user
     *@return
     */
    public static boolean isMember(WTGroup group, WTUser user) {
        logger.debug(CLASSNAME + ">>>> isMember: group=" + group + ", user=" + user );
        try {
            boolean result = OrganizationServicesHelper.manager.isMember(group, user);
            return result;
        }
        catch (WTException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据用户名获取WTUser 对象
     *@Method:  getUserByName
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:01:31
     *@param userName
     *@return 
     *@return WTUser
     */
    public static WTUser getUserByName(String userName) {
        logger.debug(CLASSNAME + ">>>> getUserByName: userName=" + userName );
        try {
            DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
            WTUser user = OrganizationServicesHelper.manager.getUser(userName,dcp);
            return user;
        }catch(WTException  e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 根据群组名获取WTGroup 对象
     *@Method:  getGroupByName
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:04:11
     *@param groupName
     *@return 
     *@return WTGroup
     */
    public static WTGroup getGroupByName(String groupName) {
        logger.debug(CLASSNAME + ">>>> getGroupByName: groupName=" + groupName );
        try {
            DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
            WTGroup group = OrganizationServicesHelper.manager.getGroup(groupName,dcp);
            return group;
        }catch(WTException  e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * 判断 wtprincipal 是不是组织 strOrgName 的组织管理员
     *@Method:  isOrgAdministator
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17上午10:34:08
     *@param wtprincipal
     *@param strOrgName
     *@return 
     *@return boolean
     *
     */
    public static boolean isOrgAdministator(WTPrincipal wtprincipal, String strOrgName) {
        logger.debug(CLASSNAME + ">>>> isOrgAdministator: wtprincipal=" + wtprincipal + ", strOrgName=" + strOrgName );
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
            WTOrganization org = OrganizationServicesHelper.manager.getOrganization(strOrgName, dcp);
            if (org != null) {
                WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(org);
                if (wtcontainerref != null) {
                    if (WTContainerHelper.service.isAdministrator(wtcontainerref, wtprincipal)) {
                        return true;
                    }
                }
            } else {
                logger.warn("WTOrganization[" + strOrgName + "] is NOT FOUND.");
            }
        } catch (WTException e) {
            e.printStackTrace();
        }finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return false;
    }
    
    /**
     * 获取当前用户的组织容器Reference, 如果当前用户没有组织信息，则返回null.
     *@Method:  getUserOrgContainerRef
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-22下午05:23:43
     *@return
     *@throws WTException
     */
    public static WTContainerRef getOrgContainerRef() throws WTException {
        logger.debug(CLASSNAME + ">>>> getOrgContainerRef");
        WTOrganization org = SessionHelper.getPrincipal().getOrganization();
        if(org != null) {
            logger.debug("获取当前用户[" + SessionHelper.getPrincipal().getName() + "]所在组织:" + org);
            WTContainerRef wtOrgRef = WTContainerHelper.service.getOrgContainerRef(org);
            return wtOrgRef;
        }
        return null;
    }
 
    
    /**
     * 获取指定组织的管理员集合，如果 org = null , 则获取当前用户所属组织
     *@Method:  findOrgAdmins
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17上午10:46:37
     *@param org
     *@return
     *@throws Exception 
     *@return Vector<WTUser>
     *
     */
    public static Vector<WTUser> findOrgAdmins(WTOrganization org) throws Exception {
        logger.debug(CLASSNAME + ">>>> findOrgAdmins: org=" + org );
        Vector<WTUser> admins = new Vector<WTUser>();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            if (org == null) {
                org = SessionHelper.getPrincipal().getOrganization();
                logger.debug("org 为 null, 获取当前用户[" + SessionHelper.getPrincipal().getName() + "]所在组织:" + org);
            }
            OrgContainer orgCont = WTContainerHelper.service.getOrgContainer(org);
            if (orgCont != null) {
                WTGroup grp = orgCont.getAdministrators();
                for (Enumeration en = grp.members(); en.hasMoreElements(); ) {
                    WTPrincipal principal = (WTPrincipal) en.nextElement();
                    if (principal instanceof WTUser)
                        admins.add((WTUser)principal);
                }
            }
        }
        finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        
        return admins;
    }

    
    /**
     * 获取Group 里的所有成员（WTUser)，包括子Group中的成员。
     *@Method:  getGroupMembers
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:09:08
     *@param group
     *@return
     *@throws WTException 
     *@return Set<WTUser>
     */
    public static Set<WTUser> getGroupMembers(WTGroup group) throws WTException {
        logger.debug(CLASSNAME + ">>>> getGroupMembers: group=" + group );
        Set<WTUser> users = new HashSet<WTUser>();
        Enumeration member = group.members();
        while (member.hasMoreElements()) {
            WTPrincipal principal = (WTPrincipal) member.nextElement();
            if (principal instanceof WTUser) {
                users.add((WTUser) principal);
            } else if (principal instanceof WTGroup) {
                Set<WTUser> ausers = getGroupMembers((WTGroup) principal);
                users.addAll(ausers);
            }
        }
        return users;
    }
    

   /**
    * 通过登录名，全名，邮件地址 查询用户 模糊查询用户 <p>
    * 首先根据登录名模糊查询用户，如果没有用户，则根据用户全名查询用户。<br>
    * 如果用户全名也没有查询用户，则根据Email查询用户 <br>
    *@Method:  searchUser
    *@Description: TODO
    *@Author: zhangmingjie
    *@Since: 2018-12-17下午04:13:58
    *@param param 可以是：登录名、全名（中文）、邮件地址
    *@return 
    *@return Set<WTUser>
    */
    public static Set<WTUser> searchUser(String param){
        logger.debug(CLASSNAME + ">>>> searchUser: param=" + param );
        Set<WTUser> users = new HashSet<WTUser>();
        try {
            DirectoryContextProvider dcp = OrganizationServicesHelper.manager.newDirectoryContextProvider((String[]) null,
                    (String[]) null);
            Enumeration userEnm = OrganizationServicesHelper.manager.findLikeUsers(WTUser.AUTHENTICATION_NAME, param, dcp);
            if (userEnm == null || !userEnm.hasMoreElements()) {
                userEnm = OrganizationServicesHelper.manager.findLikeUsers(WTUser.FULL_NAME, param, dcp);
                if (userEnm == null || !userEnm.hasMoreElements()) {
                    userEnm = OrganizationServicesHelper.manager.findLikeUsers(WTUser.EMAIL, param, dcp);
                }
            }
            if (userEnm != null && userEnm.hasMoreElements()) {
                while(userEnm.hasMoreElements()){
                    users.add((WTUser)userEnm.nextElement());
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    
    /**
     * 获取用户所属的群组集合
     *@Method:  getParentGroupByUser
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:20:35
     *@param user 
     *@param onlyDirectGroup true: 取得用户所属直接群组, false: 取用户所属群组(包含群组的父群组)
     *@param onlySiteGroup true: 只返回站点下的群组，不返回其它产品器之类容器下的群组
     *@return Set<WTGroup>
     * @throws WTException 
     */
    public static Set<WTGroup> getParentGroupByUser(WTUser user , boolean onlyDirectGroup, boolean onlySiteGroup) throws WTException {
        logger.debug(CLASSNAME + ">>>> getParentGroupByUser: user=" + user + ",onlyDirectGroup=" + onlyDirectGroup + ",onlySiteGroup=" + onlySiteGroup );
        Set<WTGroup> parentGroupSet = new HashSet<WTGroup>();
        Enumeration enu = OrganizationServicesHelper.manager.parentGroups(user, !onlyDirectGroup);// false代表取得当前用户的直接群组
        while (enu != null && enu.hasMoreElements()) {
            Object obj = enu.nextElement();
            if (obj instanceof WTPrincipalReference) {
                WTPrincipalReference ref = (WTPrincipalReference) obj;
                WTGroup wtgroup = (WTGroup) ref.getObject();
                WTContainerRef wtcontainerRef = wtgroup.getContainerReference();
                if (wtcontainerRef != null )
                {
                    if(onlySiteGroup) {
                        if(wtcontainerRef.getObject() instanceof wt.inf.container.ExchangeContainer ) {
                            parentGroupSet.add(wtgroup);
                        }
                    }else {
                        parentGroupSet.add(wtgroup);
                    }
                }
            }
        }
        return parentGroupSet;
    }
    
    /**
     * 添加用户到群组中
     *@Method:  addUserToGroup
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:40:03
     *@param user
     *@param group
     *@return
     */
    public static boolean addUserToGroup(WTUser user , WTGroup group) {
        logger.debug(CLASSNAME + ">>>> addUserToGroup: user=" + user + ",group=" + group);
        try {
            OrganizationServicesHelper.manager.addMember(group, user);
            return true;
        }catch(WTException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 删除群组
     *@Method:  deleteGroup
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:42:05
     *@param group
     *@return
     */
    public static boolean deleteGroup(WTGroup group) {
        logger.debug(CLASSNAME + ">>>> deleteGroup: group=" + group);
        try {
            OrganizationServicesHelper.manager.delete(group);
            return true;
        }catch(WTException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * 从指定的父群组中移除 用户或子群组 
     *@Method:  removeGroupOrUser
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:44:19
     *@param group 
     *@param removedPrincipal 被移除的用户或群组
     *@return
     */
    public static boolean removeUserFromGroup(WTGroup parentGroup, WTPrincipal removedPrincipal) {
        logger.debug(CLASSNAME + ">>>> removeUserFromGroup: parentGroup=" + parentGroup + ",removedPrincipal=" + removedPrincipal);
        try {
            OrganizationServicesHelper.manager.removeMember(parentGroup, removedPrincipal);
            return true;
        }catch(WTException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 将group重命名为 newName
     *@Method:  renameGroup
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-17下午04:46:50
     *@param group
     *@param newName
     *@return
     */
    public static boolean renameGroup(WTGroup group, String newName) {
        logger.debug(CLASSNAME + ">>>> renameGroup: group=" + group + ",newName=" + newName);
        try {
            OrganizationServicesHelper.manager.rename(group, newName);
            return true;
        }catch(WTException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取 WTPrincipal 的 WTPrincipalReference
     *@Method:  getPrincipalRef
     *@Description: TODO
     *@Author: zhangmingjie
     *@Since: 2018-12-21上午10:37:54
     *@param principal
     *@return
     *@throws WTException
     */
    public static WTPrincipalReference getPrincipalRef(WTPrincipal principal) throws WTException {
        logger.debug(CLASSNAME + ">>>> getPrincipalRef: principal=" + principal);
        return WTPrincipalReference.newWTPrincipalReference(principal);
    }
}
