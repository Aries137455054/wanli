package ext.com.wanli.bom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ptc.core.lwc.server.LWCNormalizedObject;

import ext.com.wanli.part.PartConstant;
import ext.com.wanli.part.entity.ExcelDataBean;
import ext.com.wanli.util.ContainerUtil;
import ext.com.wanli.util.ExcelUtil;
import ext.com.wanli.util.IBAUtil;
import ext.com.wanli.util.PartUtil;
import ext.com.wanli.util.UserUtil;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.Foldered;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTProperties;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;

/**
 * 
 * @Title: BomStructureImport.java
 * @Package ext.com.wanli.bom
 * @Description: 导入初始数据
 * @author zhangmingjie
 * @date 2019年11月27日 下午5:21:13
 * @version V1.0
 */
@SuppressWarnings("deprecation")
public class BomStructureImport implements RemoteAccess {

	private static Logger logger = Logger.getLogger(BomStructureImport.class);
	// 元器件，辅料，标准件
	private static final String[] libNameArr = new String[] { "元器件", "辅料", "标准件" };
	// 产品名称
	private static final String productName = "322";
	// 产品模板名称
	private static final String productTemplateName = "Product Design";
	// 存储库模板名称
	private static final String libraryTemplateName = "General Library";
	// 组织用户
	private static final String orgUserName = "wlhk";
	// 组织密码
	private static final String oraUserPwd = "1";
	// Excel
	private static String filePath = "";

	// 编号
	private static String numberStr = "0000000000";
	private static int numberInt = 640;

	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName(orgUserName);
		rms.setPassword(oraUserPwd);
		rms.invoke("ImportBomStructure", BomStructureImport.class.getName(), null, null, null);
	}

	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String wtHome = wtproperties.getProperty("wt.home");
			filePath = wtHome + File.separator + "codebase" + File.separator + "ext" + File.separator + "com"
					+ File.separator + "wanli" + File.separator + "util" + File.separator + "importExcelData.xlsx";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入数据(产品，存储库，零部件，BOM结构)
	 * 
	 * @throws Exception
	 * @author zhangmingjie
	 * @date 2019年12月6日 上午11:12:57
	 */
	public static void ImportBomStructure() throws Exception {
		Transaction transaction = null;
		try {
			transaction = new Transaction();
			transaction.start();

			WTContainerRef pdmLinkProductContainerRef = ContainerUtil.getPDMLinkProductRef(productName);
			if(pdmLinkProductContainerRef == null){
				// 创建产品
				WTPrincipal user = UserUtil.getWTUserByName(orgUserName);
				ContainerUtil.createPDMLinkProduct(productName, productTemplateName, "历史数据导入",user);
			}
			
			// 创建存储库
			for (String libName : libNameArr) {
				String libDescription = libName;
				WTContainerRef libraryContainerRef = ContainerUtil.getLibraryRef(libName);
				if (libraryContainerRef == null) {
					ContainerUtil.createLibrary(libName, libDescription, libraryTemplateName);
				}
			}
			// 导入WTPart，创建link关系
			loadWTPartAndBOM();

			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
			transaction = null;
		}
	}

	/**
	 * 
	 * 创建存储库，零部件
	 * @author zhangmingjie
	 * @throws Exception 
	 * @date 2019年12月2日 下午6:02:45
	 */
	public static void loadWTPartAndBOM() throws Exception {
		// 读取EXCEL
		Map<String, List<ExcelDataBean>> excelDataMap = ExcelUtil.readExcel(filePath);
		for (String key : excelDataMap.keySet()) {
			List<ExcelDataBean> excelDataBeanList = excelDataMap.get(key);
			for (int i = 0; i < excelDataBeanList.size(); i++) {
				ExcelDataBean excelDataBean = excelDataBeanList.get(i);
				String sheetName = excelDataBean.getSheetName();

				if (PartConstant.SHEET_NAME_SYSTEMPRODUCT.equals(sheetName)
						|| PartConstant.SHEET_NAME_SPARECOMPONMENTPARTS.equals(sheetName)
						|| PartConstant.SHEET_NAME_ELECTRONICSPAREPARTS.equals(sheetName)
						|| PartConstant.SHEET_NAME_SOFTWARE.equals(sheetName)
						|| PartConstant.SHEET_NAME_COMPONENTPART.equals(sheetName)
						|| PartConstant.SHEET_NAME_SUBSIDIARY.equals(sheetName)
						|| PartConstant.SHEET_NAME_STDPART.equals(sheetName)) {
					createWTPart(excelDataBeanList.get(i));
				}
			}
		}

		// 创建父子关系
		for (String key : excelDataMap.keySet()) {
			List<ExcelDataBean> excelDataBeanList = excelDataMap.get(key);
			for (int i = 0; i < excelDataBeanList.size(); i++) {
				ExcelDataBean excelDataBean = excelDataBeanList.get(i);
				String sheetName = excelDataBean.getSheetName();
				if (PartConstant.SHEET_NAME_EBOM.equals(sheetName)) {
					logger.debug(">>>>>父项编号=" + excelDataBean.getFatherNumber().toUpperCase() + 
							", 子项编号="+ excelDataBean.getChildNumber().toUpperCase() + 
							", 数量=" + excelDataBean.getQuantityAmount() + 
							", 单位="+ excelDataBean.getUnit() + 
							", 插件位置=" + excelDataBean.getCardAddress());
					
					String fatherNumber = excelDataBean.getFatherNumber();
					String childNumber = excelDataBean.getChildNumber();
					WTPart fatherPart = PartUtil.getLatestPartByNumber(fatherNumber.toUpperCase());
					WTPartMaster chilsPartMaster = PartUtil.getPartMasterByNumber(childNumber.toUpperCase());

					if (fatherPart != null && chilsPartMaster != null) {
						WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(fatherPart, chilsPartMaster);
						Quantity quantity = new Quantity();
						quantity.setAmount(excelDataBean.getQuantityAmount());
						quantity.setUnit(QuantityUnit.toQuantityUnit(excelDataBean.getUnit()));
						usageLink.setQuantity(quantity);

						LWCNormalizedObject obj = new LWCNormalizedObject(usageLink, null, null, null);
						obj.load(PartConstant.cardAddress);
						obj.set(PartConstant.cardAddress, excelDataBean.getCardAddress());
						obj.apply();
						PersistenceHelper.manager.save(usageLink);
					} else {
						logger.debug("fatherPart is null or chilsPartMaster is null.");
					}
				}
			}
		}
	}

	/**
	 * @param name 名称
	 * @param number 编码
	 * @param libName 存储库
	 * @throws Exception
	 * @return WTPart
	 * @author zhangmingjie
	 * @date 2019年11月29日 下午6:02:37
	 */
	public static WTPart createWTPart(ExcelDataBean excelDataBean) throws Exception {
		logger.debug(">>>>>createWTPart begin >>>>>");
		String tempPartType = "";

		WTContainerRef wtcontainerRef = null;
		String sheetName = excelDataBean.getSheetName();
		WTPart wtpart = WTPart.newWTPart();

		// 系统-子系统-产品
		if (PartConstant.SHEET_NAME_SYSTEMPRODUCT.equals(sheetName)) {
			String productType = excelDataBean.getProductType();
			// 系统产品
			if (PartConstant.CELL_NAME_SYSTEM.equals(productType)) {
				TypeDefinitionReference tdr = ClientTypedUtility
						.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_SYSTEMPRODUCT);
				wtpart.setTypeDefinitionReference(tdr);
			}
			// 子系统产品
			else if (PartConstant.CELL_NAME_SUBSYSTEM.equals(productType)) {
				TypeDefinitionReference tdr = ClientTypedUtility
						.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_SUBSYSTEMPRODUCT);
				wtpart.setTypeDefinitionReference(tdr);
			}
			// 产品
			else if (PartConstant.CELL_NAME_PRODUCT.equals(productType)) {
				TypeDefinitionReference tdr = ClientTypedUtility
						.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_WTPART);
				wtpart.setTypeDefinitionReference(tdr);
			}
			wtpart.setName(excelDataBean.getName());

			String unitValue = ExcelUtil.unitToValue(excelDataBean.getUnit());
			QuantityUnit qtyUnit = QuantityUnit.toQuantityUnit(unitValue);
			wtpart.setDefaultUnit(qtyUnit);

			// 编码如果带字母，默认转大写了？？？？？？？
			wtpart.setNumber(excelDataBean.getProductCode()); // number = 产品代号

			wtcontainerRef = ContainerUtil.getPDMLinkProductRef(productName);
			tempPartType = PartConstant.SHEET_NAME_SYSTEMPRODUCT;
		}
		// 零组件
		else if (PartConstant.SHEET_NAME_SPARECOMPONMENTPARTS.equals(sheetName)) {
			TypeDefinitionReference tdr = ClientTypedUtility
					.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_WTPART);
			wtpart.setTypeDefinitionReference(tdr);

			wtpart.setName(excelDataBean.getName());
			wtpart.setNumber(excelDataBean.getProductCode()); // number = 产品代号

			wtcontainerRef = ContainerUtil.getPDMLinkProductRef(productName);
			tempPartType = PartConstant.SHEET_NAME_SPARECOMPONMENTPARTS;
		}
		// 电子零组件
		else if (PartConstant.SHEET_NAME_ELECTRONICSPAREPARTS.equals(sheetName)) {
			TypeDefinitionReference tdr = ClientTypedUtility
					.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_ELECTRONICPART);
			wtpart.setTypeDefinitionReference(tdr);

			wtpart.setName(excelDataBean.getName());
			String unitValue = ExcelUtil.unitToValue(excelDataBean.getUnit());
			QuantityUnit qtyUnit = QuantityUnit.toQuantityUnit(unitValue);
			wtpart.setDefaultUnit(qtyUnit);
			wtpart.setNumber(excelDataBean.getProductCode()); // number = 产品代号

			wtcontainerRef = ContainerUtil.getPDMLinkProductRef(productName);
			tempPartType = PartConstant.SHEET_NAME_ELECTRONICSPAREPARTS;
		}
		// 软件
		else if (PartConstant.SHEET_NAME_SOFTWARE.equals(sheetName)) {
			TypeDefinitionReference tdr = ClientTypedUtility
					.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_SOFTWAREPART);
			wtpart.setTypeDefinitionReference(tdr);

			wtpart.setName(excelDataBean.getConfigureItemName());
			wtpart.setNumber(excelDataBean.getProductCode()); // number = 产品代号

			wtcontainerRef = ContainerUtil.getPDMLinkProductRef(productName);
			tempPartType = PartConstant.SHEET_NAME_SOFTWARE;
		}
		// 标准件
		else if (PartConstant.SHEET_NAME_STDPART.equals(sheetName)) {
			TypeDefinitionReference tdr = ClientTypedUtility
					.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_STDPART);
			wtpart.setTypeDefinitionReference(tdr);

			wtpart.setName(excelDataBean.getName());
			String number = excelDataBean.getItemCode();
			if ("".equals(number)) {
				numberInt += 1;
				number = numberStr + numberInt;
				logger.debug(">>>>>>>>>>>>>>>>>number=" + number);
			}
			wtpart.setNumber(number); // 标准件编码？？？？？

			wtcontainerRef = ContainerUtil.getLibraryRef(sheetName);
			tempPartType = PartConstant.SHEET_NAME_STDPART;
		}
		// 元器件
		else if (PartConstant.SHEET_NAME_COMPONENTPART.equals(sheetName)) {
			TypeDefinitionReference tdr = ClientTypedUtility
					.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_COMPONENTPART);
			wtpart.setTypeDefinitionReference(tdr);

			wtpart.setName(excelDataBean.getName());
			String unitValue = ExcelUtil.unitToValue(excelDataBean.getUnit());
			QuantityUnit qtyUnit = QuantityUnit.toQuantityUnit(unitValue);
			wtpart.setDefaultUnit(qtyUnit);
			wtpart.setNumber(excelDataBean.getItemCode()); // number = 物品码

			wtcontainerRef = ContainerUtil.getLibraryRef(sheetName);
			tempPartType = PartConstant.SHEET_NAME_COMPONENTPART;
		}
		// 辅料
		else if (PartConstant.SHEET_NAME_SUBSIDIARY.equals(sheetName)) {
			TypeDefinitionReference tdr = ClientTypedUtility
					.getTypeDefinitionReference(PartConstant.PARTTYPE_NAME_SUBSIDIARYMATERIAL);
			wtpart.setTypeDefinitionReference(tdr);

			wtpart.setName(excelDataBean.getName());
			String unitValue = ExcelUtil.unitToValue(excelDataBean.getUnit());
			QuantityUnit qtyUnit = QuantityUnit.toQuantityUnit(unitValue);
			wtpart.setDefaultUnit(qtyUnit);
			wtpart.setNumber(excelDataBean.getItemCode()); // number = 物品码

			wtcontainerRef = ContainerUtil.getLibraryRef(sheetName);
			tempPartType = PartConstant.SHEET_NAME_COMPONENTPART;
		}

		// 设置视图
		View designView = ViewHelper.service.getView(PartConstant.VIEW_DESIGN);
		ViewReference viewRef = ViewReference.newViewReference(designView);
		if (designView != null) {
			wtpart.setView(viewRef);
		}
		
		// 设置容器、文件夹、域
		Folder folder = FolderHelper.service.saveFolderPath(PartConstant.FOLDER_PATH_DEFAULT, wtcontainerRef);
		FolderHelper.assignLocation((Foldered) wtpart, folder);
		
		wtpart = (WTPart) PersistenceHelper.manager.save(wtpart);

		// 设置IBA
		setPartIBAValues(wtpart, excelDataBean, tempPartType);
		return wtpart;
	}

	public static void setPartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean, String tempPartType)
			throws Exception {
		if (PartConstant.SHEET_NAME_SYSTEMPRODUCT.equals(tempPartType)) {
			setSystemPartIBAValues(wtpart, excelDataBean);
		} else if (PartConstant.SHEET_NAME_SPARECOMPONMENTPARTS.equals(tempPartType)) {
			setSpareComponmentPartIBAValues(wtpart, excelDataBean);
		} else if (PartConstant.SHEET_NAME_ELECTRONICSPAREPARTS.equals(tempPartType)) {
			setElectronicPartIBAValues(wtpart, excelDataBean);
		} else if (PartConstant.SHEET_NAME_SOFTWARE.equals(tempPartType)) {
			setSoftwarePartIBAValues(wtpart, excelDataBean);
		} else if (PartConstant.SHEET_NAME_COMPONENTPART.equals(tempPartType)) {
			setComponentPartIBAValues(wtpart, excelDataBean);
		} else if (PartConstant.SHEET_NAME_SUBSIDIARY.equals(tempPartType)) {
			setSubsidiaryMaterialPartIBAValues(wtpart, excelDataBean);
		} else if (PartConstant.SHEET_NAME_STDPART.equals(tempPartType)) {
			setSTDPartIBAValues(wtpart, excelDataBean);
		}
	}

	/**
	 * 设置IBA（系统-子系统-产品）
	 * 
	 * @param wtpart
	 * @param attributes
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @throws Exception
	 * @date 2019年12月4日 上午10:59:24
	 */
	public static void setSystemPartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean) throws Exception {
		Hashtable<String, Object> attributes = new Hashtable<>();
		attributes.put(PartConstant.IBA_PRODUCTCODE, excelDataBean.getProductCode());// 产品代号
		attributes.put(PartConstant.IBA_MODELNO, excelDataBean.getMODELNO());// 型号
		attributes.put(PartConstant.IBA_ZJS, excelDataBean.getZJS());// 主机厂（所）
		attributes.put(PartConstant.IBA_SUBORDINATESYSTEM, excelDataBean.getSubordinateSystem());// 所属系统
		attributes.put(PartConstant.IBA_IMPORTANCE, excelDataBean.getImportance());// 重要度
		attributes.put(PartConstant.IBA_KTLX, excelDataBean.getKTLX());// 课题类型
		attributes.put(PartConstant.IBA_DESIGNDEPART, excelDataBean.getDESIGNDEPART());// 设计部门
		attributes.put(PartConstant.IBA_ORIGINALDESIGN, excelDataBean.getOriginalDesign());// 原始设计
		attributes.put(PartConstant.IBA_MASTERDESIGNER, excelDataBean.getMASTERDESIGNER());// 主管设计
		attributes.put(PartConstant.IBA_SUPDESIGNER, excelDataBean.getSUPDESIGNER());// 辅管设计
		attributes.put(PartConstant.IBA_TECHNICALREQUIREMENT, excelDataBean.getTechnicalRequirement());// 技术要求
		attributes.put(PartConstant.IBA_WANLIPHASE, excelDataBean.getWanliPhase());// 阶段
		attributes.put(PartConstant.IBA_ISREMODEL, excelDataBean.isREMODEL());// 是否改型
		attributes.put(PartConstant.IBA_PROTOTYPENO, excelDataBean.getPrototypeNo());// 原型代号
		attributes.put(PartConstant.IBA_USAGE, excelDataBean.getUSAGE());// 用途
		attributes.put(PartConstant.IBA_SECRETLEVEL, excelDataBean.getSECRETLEVEL());// 密别
		// 物品码？？？？？
		attributes.put(PartConstant.IBA_ITEMCODE, excelDataBean.getItemCode());
		attributes.put(PartConstant.IBA_HASSOFTWAREPART, excelDataBean.isHasSoftwarePart());// 是否有软件
		attributes.put(PartConstant.IBA_IMPORTANCELEVEL, excelDataBean.getImportanceLevel());// 重要等级
		setIBAValues(wtpart, attributes);
	}

	/**
	 * 设置IBA（零组件）
	 * 
	 * @param wtpart
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @throws Exception
	 * @date 2019年12月4日 上午11:14:29
	 */
	public static void setSpareComponmentPartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean) throws Exception {
		Hashtable<String, Object> attributes = new Hashtable<>();
		attributes.put(PartConstant.IBA_PRODUCTCODE, excelDataBean.getProductCode());// 产品代号
		attributes.put(PartConstant.IBA_ORIGINALPRODUCTCODE, excelDataBean.getOriginalProductCode());// 原代号
		attributes.put(PartConstant.IBA_PARTTYPE, excelDataBean.getPARTTYPE());// （零部件）类型
		attributes.put(PartConstant.IBA_WANLIPHASE, excelDataBean.getWanliPhase());// 阶段
		attributes.put(PartConstant.IBA_TUFU, excelDataBean.getTUFU());// 图幅
		attributes.put(PartConstant.IBA_DESIGNDEPART, excelDataBean.getDESIGNDEPART());// 设计部门
		attributes.put(PartConstant.IBA_GUANZHONG, excelDataBean.getGUANZHONG());// 关重件
		attributes.put(PartConstant.IBA_ORIGINALDESIGN, excelDataBean.getOriginalDesign());// 原始设计
		attributes.put(PartConstant.IBA_MASTERDESIGNER, excelDataBean.getMASTERDESIGNER());// 主管设计
		attributes.put(PartConstant.IBA_SUPDESIGNER, excelDataBean.getSUPDESIGNER());// 辅管设计
		attributes.put(PartConstant.IBA_TECHNICALREQUIREMENT, excelDataBean.getTechnicalRequirement());// 技术要求
		attributes.put(PartConstant.IBA_GENERALTOLERANCE, excelDataBean.getGeneralTolerance());// 一般公差
		attributes.put(PartConstant.IBA_SURFACEROUGHNESS, excelDataBean.getSurfaceRoughness());// 表面粗糙度
		attributes.put(PartConstant.IBA_HEATTREATMENT, excelDataBean.getHeatTreatment()); // 热处理
		attributes.put(PartConstant.IBA_SURFACETREATMENT, excelDataBean.getSurfaceTreatment()); // 表面处理
		attributes.put(PartConstant.IBA_CLMC, excelDataBean.getCLMC()); // 材料名称
		attributes.put(PartConstant.IBA_MATERIALSPECIFICATION, excelDataBean.getMaterialSpecification()); // 材料规格
		attributes.put(PartConstant.IBA_MATERIALSTECHNICALCONDITIONS, excelDataBean.getMaterialsTechnicalConditions()); // 材料技术条件
		attributes.put(PartConstant.IBA_CLPH, excelDataBean.getCLPH()); // 材料牌号
		attributes.put(PartConstant.IBA_DYCLMC, excelDataBean.getDYCLMC()); // 材料待用名称
		attributes.put(PartConstant.IBA_DYCLGG, excelDataBean.getDYCLGG()); // 代用材料规格
		attributes.put(PartConstant.IBA_DYCLJXTJ, excelDataBean.getDYCLJXTJ()); // 代用材料技术条件
		attributes.put(PartConstant.IBA_DYCLPH, excelDataBean.getDYCLPH()); // 代用材料牌号
		attributes.put(PartConstant.IBA_WEIGHT, excelDataBean.getWeight());// 重量
		setIBAValues(wtpart, attributes);
	}

	/**
	 * 设置IBA（电子零组件）
	 * 
	 * @param wtpart
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @throws Exception
	 * @date 2019年12月4日 上午11:14:29
	 */
	public static void setElectronicPartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean) throws Exception {
		Hashtable<String, Object> attributes = new Hashtable<>();
		attributes.put(PartConstant.IBA_PRODUCTCODE, excelDataBean.getProductCode());// 产品代号
		attributes.put(PartConstant.IBA_ORIGINALPRODUCTCODE, excelDataBean.getOriginalProductCode());// 原代号
		attributes.put(PartConstant.IBA_MODELNO, excelDataBean.getMODELNO());// 型号
		attributes.put(PartConstant.IBA_PARTTYPE, excelDataBean.getPARTTYPE());// （零部件）类型
		attributes.put(PartConstant.IBA_WANLIPHASE, excelDataBean.getWanliPhase());// 阶段
		attributes.put(PartConstant.IBA_ORIGINALDESIGN, excelDataBean.getOriginalDesign());// 原始设计
		attributes.put(PartConstant.IBA_MASTERDESIGNER, excelDataBean.getMASTERDESIGNER());// 主管设计
		attributes.put(PartConstant.IBA_SUPDESIGNER, excelDataBean.getSUPDESIGNER());// 辅管设计
		attributes.put(PartConstant.IBA_TECHNICALREQUIREMENT, excelDataBean.getTechnicalRequirement());// 技术要求
		attributes.put(PartConstant.IBA_TUFU, excelDataBean.getTUFU());// 图幅
		attributes.put(PartConstant.IBA_DESIGNDEPART, excelDataBean.getDESIGNDEPART());// 设计部门
		attributes.put(PartConstant.IBA_SSCP, excelDataBean.getSSCP());// 所属产品
		attributes.put(PartConstant.IBA_GUANZHONG, excelDataBean.getGUANZHONG());// 关重件
		attributes.put(PartConstant.IBA_SECRETLEVEL, excelDataBean.getSECRETLEVEL());// 密别
		attributes.put(PartConstant.IBA_CLMC, excelDataBean.getCLMC()); // 材料名称
		attributes.put(PartConstant.IBA_MATERIALSPECIFICATION, excelDataBean.getMaterialSpecification()); // 材料规格
		attributes.put(PartConstant.IBA_MATERIALSTECHNICALCONDITIONS, excelDataBean.getMaterialsTechnicalConditions()); // 材料技术条件
		attributes.put(PartConstant.IBA_CLPH, excelDataBean.getCLPH()); // 材料牌号
		attributes.put(PartConstant.IBA_DYCLMC, excelDataBean.getDYCLMC()); // 代用材料名称
		attributes.put(PartConstant.IBA_DYCLGG, excelDataBean.getDYCLGG()); // 代用材料规格
		attributes.put(PartConstant.IBA_DYCLJXTJ, excelDataBean.getDYCLJXTJ()); // 代用材料技术条件
		attributes.put(PartConstant.IBA_DYCLPH, excelDataBean.getDYCLPH()); // 代用材料牌号
		attributes.put(PartConstant.IBA_PCBCODE, excelDataBean.getPCBCODE());// PCB板代号
		attributes.put(PartConstant.IBA_PCBNAME, excelDataBean.getPCBNAME());// PCB板名称
		attributes.put(PartConstant.IBA_VERSION, excelDataBean.getVERSION());// PCB板版本
		attributes.put(PartConstant.IBA_PRINTPLATEPROCESSINGSTANDARD, excelDataBean.getPRINTPLATEPROCESSINGSTANDARD());// 印制板加工标准
		attributes.put(PartConstant.IBA_PCBDESIGNER, excelDataBean.getPCBDESIGNER());// PCB板设计人员
		attributes.put(PartConstant.IBA_PCBLENGTH, excelDataBean.getPCBLENGTH());// PCB板长
		attributes.put(PartConstant.IBA_PCBWIDE, excelDataBean.getPCBWIDE());// PCB板宽
		attributes.put(PartConstant.IBA_PCBNUMBER, excelDataBean.getPCBNUMBER());// PCB板层数
		attributes.put(PartConstant.IBA_PCBAREA, excelDataBean.getPCBAREA());// PCB板面积
		attributes.put(PartConstant.IBA_THICKNESS, excelDataBean.getTHICKNESS());// 厚度
		attributes.put(PartConstant.IBA_PCBEFFECTSOCLENUMBER, excelDataBean.getPCBEFFECTSOCLENUMBER());// PCB板有效管脚数
		attributes.put(PartConstant.IBA_WEIGHT, excelDataBean.getWeight());// 重量
		setIBAValues(wtpart, attributes);
	}

	/**
	 * 设置IBA（软件）
	 * 
	 * @param wtpart
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @throws Exception
	 * @date 2019年12月4日 上午11:14:29
	 */
	public static void setSoftwarePartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean) throws Exception {
		Hashtable<String, Object> attributes = new Hashtable<>();
		attributes.put(PartConstant.IBA_PRODUCTCODE, excelDataBean.getProductCode());// 产品代号
		attributes.put(PartConstant.IBA_CONFIGUREITEMNAME, excelDataBean.getConfigureItemName());// 配置项名称
		attributes.put(PartConstant.IBA_SOFTWARESTATUS, excelDataBean.getSoftwareStatus());// 软件状态
		attributes.put(PartConstant.IBA_VERSION, excelDataBean.getVERSION());// 版本
		attributes.put(PartConstant.IBA_USAGE, excelDataBean.getUSAGE());// 用途
		attributes.put(PartConstant.IBA_DESIGNDEPART, excelDataBean.getDESIGNDEPART());// 设计部门
		attributes.put(PartConstant.IBA_SSCP, excelDataBean.getSSCP());// 所属产品
		attributes.put(PartConstant.IBA_SOFTWARELEADER, excelDataBean.getSoftwareLeader());// 项目软件组长
		attributes.put(PartConstant.IBA_RESIDENTHARDWARE, excelDataBean.getResidentHardware());// 驻留硬件
		setIBAValues(wtpart, attributes);
	}

	/**
	 * 设置IBA（标准件）
	 * 
	 * @param wtpart
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @date 2019年12月4日 上午11:41:34
	 */
	public static void setSTDPartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean) throws Exception {
		Hashtable<String, Object> attributes = new Hashtable<>();
		attributes.put(PartConstant.IBA_CATEGORYCODE, excelDataBean.getCategoryCode()); // 分类
		attributes.put(PartConstant.IBA_ENNAME, excelDataBean.getEnName()); // 物品外文名称
		attributes.put(PartConstant.IBA_STANDARDLEVELNAME, excelDataBean.getStandardLevelName()); // 标准级别名称
		attributes.put(PartConstant.IBA_STANDARDNUMBER, excelDataBean.getStandardNumber()); // 标准号
		attributes.put(PartConstant.IBA_SPECIFICATIONS, excelDataBean.getSpecifications()); // 规格
		attributes.put(PartConstant.IBA_CLPH, excelDataBean.getCLPH()); // 材料牌号
		attributes.put(PartConstant.IBA_SURFACETREATMENT, excelDataBean.getSurfaceTreatment()); // 表面处理
		attributes.put(PartConstant.IBA_HEATTREATMENT, excelDataBean.getHeatTreatment()); // 热处理
		attributes.put(PartConstant.IBA_PRODUCTPATTERN, excelDataBean.getProductPattern()); // 产品型式11
		attributes.put(PartConstant.IBA_SUBSEQUENCENUMBER, excelDataBean.getSubsequenceNumber()); // 子件序号
		attributes.put(PartConstant.IBA_SUBSEQUENCENAME, excelDataBean.getSubsequenceName()); // 子件名称
		attributes.put(PartConstant.IBA_DIMENSIONSTOLERANCE, excelDataBean.getDimensionsTolerance()); // 关注尺寸的公差带
		attributes.put(PartConstant.IBA_SAFETYHOLETYPE, excelDataBean.getSafetyHoleType()); // 保险孔型式
		attributes.put(PartConstant.IBA_ADDITIONALTECHNICALCONDITIONS,
				excelDataBean.getAdditionalTechnicalConditions()); // 附加技术条件
		setIBAValues(wtpart, attributes);
	}

	/**
	 * 设置IBA（元器件）
	 * 
	 * @param wtpart
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @date 2019年12月4日 上午11:41:34
	 */
	public static void setComponentPartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean) throws Exception {
		Hashtable<String, Object> attributes = new Hashtable<>();
		attributes.put(PartConstant.IBA_CATEGORYCODE, excelDataBean.getCategoryCode()); // 分类
		attributes.put(PartConstant.IBA_ITEMCODE, excelDataBean.getItemCode()); // 物品码
		attributes.put(PartConstant.IBA_ENNAME, excelDataBean.getEnName()); // 物品外文名称
		attributes.put(PartConstant.IBA_SERIESNO, excelDataBean.getSeriesNo()); // 型号系列
		attributes.put(PartConstant.IBA_GENERALSPECIFICATION, excelDataBean.getGeneralSpecification()); // 总规范
		attributes.put(PartConstant.IBA_DETAILEDSPECIFICATION, excelDataBean.getDetailedSpecification()); // 详细规范
		attributes.put(PartConstant.IBA_INEFFICIENCYLEVEL, excelDataBean.getInefficiencyLevel()); // 失效率等级/质量保证等级
		attributes.put(PartConstant.IBA_WORKINGTEMPERATURE, excelDataBean.getWorkingTemperature()); // 工作温度
		attributes.put(PartConstant.IBA_RATEDCAPACITY, excelDataBean.getRatedCapacity()); // 额定容量
		attributes.put(PartConstant.IBA_RATEDVOLTAGE, excelDataBean.getRatedVoltage()); // 额定电压
		attributes.put(PartConstant.IBA_DEVIATION, excelDataBean.getDeviation()); // 容量允许偏差
		attributes.put(PartConstant.IBA_ENCAPSULATIONSIZE, excelDataBean.getEncapsulationSize()); // 外形尺寸/封装形式
		attributes.put(PartConstant.IBA_TEMPERATURECOEFFICIENT, excelDataBean.getTemperatureCoefficient()); // 温度系数/温度特性
		attributes.put(PartConstant.IBA_SPECIFICATIONTYPE, excelDataBean.getSpecificationType()); // 代号（规格型号）
		attributes.put(PartConstant.IBA_MANUFACTURER, excelDataBean.getManufacturer()); // 生产厂家
		attributes.put(PartConstant.IBC_GROUPPARTICLENO, excelDataBean.getGROUPPARTICLENO()); // 集团物品码
		attributes.put(PartConstant.IBA_ACCEPTANCEPHASE, excelDataBean.getAcceptancePhase()); // 验收状态
		attributes.put(PartConstant.IBA_TECHNICALCONDITIONS, excelDataBean.getTechnicalConditions()); // 技术状态
		attributes.put(PartConstant.IBA_XYNO, excelDataBean.getXYNO()); // 成品协议号（协议编号）
		attributes.put(PartConstant.IBA_PRTUI, excelDataBean.getPrtui()); // 采购单位
		attributes.put(PartConstant.IBA_MTLSGTR, excelDataBean.getMtlsgtr()); // 密度
		attributes.put(PartConstant.IBA_PURCHECKIT, excelDataBean.getPurCheckIt()); // 检验周期
		attributes.put(PartConstant.IBA_PURDAYS, excelDataBean.getPurDays()); // 采购周期
		setIBAValues(wtpart, attributes);
	}

	/**
	 * 设置IBA属性（辅料）
	 * 
	 * @param attributes
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @throws Exception
	 * @date 2019年12月3日 上午9:05:20
	 */
	public static void setSubsidiaryMaterialPartIBAValues(WTPart wtpart, ExcelDataBean excelDataBean) throws Exception {
		Hashtable<String, Object> attributes = new Hashtable<>();
		attributes.put(PartConstant.IBC_GROUPPARTICLENO, excelDataBean.getItemCode()); // 物品码
		attributes.put(PartConstant.IBA_PROTOCOLNAME, excelDataBean.getProtocolName()); // 协议名称
		attributes.put(PartConstant.IBA_XYNO, excelDataBean.getXYNO()); // 成品协议号（协议编号）
		attributes.put(PartConstant.IBA_CLMC, excelDataBean.getCLMC()); // 材料名称
		attributes.put(PartConstant.IBA_MATERIALSPECIFICATION, excelDataBean.getMaterialSpecification()); // 材料规格
		attributes.put(PartConstant.IBA_MATERIALSTECHNICALCONDITIONS, excelDataBean.getMaterialsTechnicalConditions()); // 材料技术条件
		attributes.put(PartConstant.IBA_CLPH, excelDataBean.getCLPH()); // 材料牌号
		attributes.put(PartConstant.IBA_DYCLMC, excelDataBean.getDYCLMC()); // 代用材料名称
		attributes.put(PartConstant.IBA_DYCLGG, excelDataBean.getDYCLGG()); // 代用材料规格
		attributes.put(PartConstant.IBA_DYCLJXTJ, excelDataBean.getDYCLJXTJ()); // 代用材料技术条件
		attributes.put(PartConstant.IBA_DYCLPH, excelDataBean.getDYCLPH()); // 代用材料牌号
		attributes.put(PartConstant.IBA_DEFAULTCOMPANY, excelDataBean.getDefaultCompany()); // 默认单位
		attributes.put(PartConstant.IBA_MANUFACTURER, excelDataBean.getManufacturer()); // 生产厂家
		attributes.put(PartConstant.IBA_MTLSGTR, excelDataBean.getMtlsgtr()); // 密度
		attributes.put(PartConstant.IBA_PURCHECKIT, excelDataBean.getPurCheckIt()); // 检验周期
		attributes.put(PartConstant.IBA_PURDAYS, excelDataBean.getPurDays()); // 采购周期
		setIBAValues(wtpart, attributes);
	}

	/**
	 * 设置iba属性
	 * 
	 * @param wtpart
	 * @param attributes
	 * @author zhangmingjie
	 * @date 2019年12月3日 上午9:05:42
	 */
	@SuppressWarnings("static-access")
	public static IBAHolder setIBAValues(WTPart wtpart, Map<String, Object> attributes) throws Exception {
		if ((attributes == null) || (attributes.isEmpty()) || attributes.size() <= 0)
			return wtpart;
		IBAUtil ibaUtil = new IBAUtil((IBAHolder) wtpart);
		@SuppressWarnings("rawtypes")
		Iterator names = attributes.keySet().iterator();
		while (names.hasNext()) {
			String name = (String) names.next();
			Object value = attributes.get(name);
			logger.debug(name + ":" + value + "----name:value");
			if (value == null) {
				continue;
			}
			ibaUtil.setIBAAnyValue(wtpart, name, value);
		}
		wtpart = (WTPart) ibaUtil.updateIBAHolder((IBAHolder) wtpart);
		return wtpart;
	}
	
}
