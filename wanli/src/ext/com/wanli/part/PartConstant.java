package ext.com.wanli.part;

public class PartConstant {

	//视图
    public static final String VIEW_DESIGN = "Design";//设计视图
    
    //类型
    public static final String PARTTYPE_NAME_WTPART = "wt.part.WTPart";                                            //产品，零组件
    public static final String PARTTYPE_NAME_SYSTEMPRODUCT = "wt.part.WTPart|com.wlhkplm.SystemProduct";           //系统产品
    public static final String PARTTYPE_NAME_SUBSYSTEMPRODUCT = "wt.part.WTPart|com.wlhkplm.SubsystemProduct";     //子系统产品
    public static final String PARTTYPE_NAME_ELECTRONICPART = "wt.part.WTPart|com.wlhkplm.ElectronicPart";         //电子部件
    public static final String PARTTYPE_NAME_SOFTWAREPART = "wt.part.WTPart|com.wlhkplm.SoftwarePart";             //软件部件
    public static final String PARTTYPE_NAME_RAWMATERIAL = "wt.part.WTPart|com.wlhkplm.RawMaterial";               //原材料
    public static final String PARTTYPE_NAME_STDPART = "wt.part.WTPart|com.wlhkplm.STDPart";                       //标准件
    public static final String PARTTYPE_NAME_SUBSIDIARYMATERIAL = "wt.part.WTPart|com.wlhkplm.SubsidiaryMaterial"; //辅料
    public static final String PARTTYPE_NAME_COMPONENTPART = "wt.part.WTPart|com.wlhkplm.ComponentPart";           //元器件
    
    //EXCEL对应的sheetname
    public static final String SHEET_NAME_SHEET1 = "Sheet1";
    public static final String SHEET_NAME_EXPLAIN = "说明";
    public static final String SHEET_NAME_FILLININSTRUCTIONS = "填写说明";
    public static final String SHEET_NAME_SYSTEMPRODUCT = "系统-子系统-产品";
    public static final String SHEET_NAME_SPARECOMPONMENTPARTS = "零组件";
    public static final String SHEET_NAME_ELECTRONICSPAREPARTS = "电子零组件";
    public static final String SHEET_NAME_SOFTWARE = "软件";
    public static final String SHEET_NAME_COMPONENTPART = "元器件";
    public static final String SHEET_NAME_SUBSIDIARY = "辅料";
    public static final String SHEET_NAME_STDPART = "标准件";
    public static final String SHEET_NAME_EBOM = "EBOM结构";
    public static final String SHEET_NAME_SCHEMATICDIAGRAM = "关联原理图-外形尺寸等";
    
    //EXCEL对应的分类
    public static final String CELL_NAME_SYSTEM = "系统产品";
    public static final String CELL_NAME_SUBSYSTEM = "子系统产品";
    public static final String CELL_NAME_PRODUCT = "产品";
    public static final String CELL_NAME_SPAREPARTS = "零件";
    public static final String CELL_NAME_COMPONMENTPARTS = "组件";
    
    //文件夹路径
    public static final String FOLDER_PATH_DEFAULT = "/Default";
    public static final String FOLDER_PATH_SPAREPARTS = "/Default/零部件";   
    
    
    //系统-子系统-产品（IBA属性）
    public static final String IBA_PRODUCTTYPE = "ProductType";          					//产品分类
    public static final String IBA_PRODUCTCODE = "ProductCode";     						//产品代号
    public static final String IBA_MODELNO = "MODELNO";     								//型号
    public static final String IBA_ZJS = "ZJS";             								//主机厂（所）
    public static final String IBA_SUBORDINATESYSTEM = "SubordinateSystem";     		    //所属系统
    public static final String IBA_IMPORTANCE = "Importance";     						    //重要度
    public static final String IBA_KTLX = "KTLX";     										//课题类型
    public static final String IBA_DESIGNDEPART = "DESIGNDEPART";     						//设计部门
    public static final String IBA_ORIGINALDESIGN = "OriginalDesign";       				//原始设计
    public static final String IBA_MASTERDESIGNER = "MASTERDESIGNER";       				//主管设计
    public static final String IBA_SUPDESIGNER = "SUPDESIGNER";     						//辅管设计
    public static final String IBA_TECHNICALREQUIREMENT = "technicalRequirement";     		//技术要求
    public static final String IBA_WANLIPHASE = "wanliPhase";                               //阶段
    public static final String IBA_ISREMODEL = "ISREMODEL";                   			    //是否改型
    public static final String IBA_PROTOTYPENO = "PrototypeNo";                   			//原型代号
    public static final String IBA_USAGE = "USAGE";                   						//用途
    public static final String IBA_SECRETLEVEL = "SECRETLEVEL";                   			//密别
    public static final String IBA_HASSOFTWAREPART = "hasSoftwarePart";                   	//是否有软件
    public static final String IBA_IMPORTANCELEVEL = "ImportanceLevel";                   	//重要等级
    
    
    //零组件（IBA属性）
    public static final String IBA_ORIGINALPRODUCTCODE = "OriginalProductCode";     		//原代号
    public static final String IBA_PARTTYPE = "PARTTYPE";         							//（零部件）类型
    public static final String IBA_TUFU = "TUFU";                        					//图幅
    public static final String IBA_GUANZHONG = "GUANZHONG";                					//关重件
    public static final String IBA_GENERALTOLERANCE = "GeneralTolerance";          			//一般公差
    public static final String IBA_SURFACEROUGHNESS = "SurfaceRoughness";          			//表面粗糙度
    public static final String IBA_WEIGHT = "Weight";                                       //重量
    
    
    //电子零组件（IBA属性）
    public static final String IBA_PCBCODE = "PCBCODE";      								//PCB板代号
    public static final String IBA_PCBNAME = "PCBNAME";      								//PCB板名称
    public static final String IBA_VERSION = "VERSION";    								    //PCB板版本
    public static final String IBA_PRINTPLATEPROCESSINGSTANDARD = "PRINTPLATEPROCESSINGSTANDARD";    //印制板加工标准
    public static final String IBA_PCBDESIGNER = "PCBDESIGNER";    							//PCB板设计员
    public static final String IBA_PCBLENGTH = "PCBLENGTH";    								//PCB板长
    public static final String IBA_PCBWIDE = "PCBWIDE";    									//PCB板宽
    public static final String IBA_PCBNUMBER = "PCBNUMBER";    								//PCB板层数
    public static final String IBA_PCBAREA = "PCBAREA";    									//PCB板面积
    public static final String IBA_THICKNESS = "THICKNESS";    								//厚度
    public static final String IBA_PCBEFFECTSOCLENUMBER = "PCBEFFECTSOCLENUMBER";   		//PCB板有效管脚数
    
    
    //软件（IBA属性）（软件代号：ProductCode，用途：IBA_USAGE，版本,IBA_PCBVERSION，设计部门：IBA_DESIGNDEPART，）
    public static final String IBA_CONFIGUREITEMNAME = "configureItemName";                 //配置项名称
    public static final String IBA_SOFTWARESTATUS = "softwareStatus";                       //软件状态
    public static final String IBA_SSCP = "SSCP";  											//所属产品
    public static final String IBA_SOFTWARELEADER = "softwareLeader";       				//项目软件组长
    public static final String IBA_RESIDENTHARDWARE = "residentHardware";       			//驻留硬件
    
    
    //标准件（IBA属性）
    public static final String IBA_CATEGORYCODE  ="CategoryCode";                                 //分类（一级分类/二级分类/三级分类(标准件分类储存方式，用“/”作为分隔符)）
    public static final String IBA_ENNAME = "enName";                                             //物品外文名称
    public static final String IBA_STANDARDLEVELNAME = "standardLevelName";                       //标准级别名称
    public static final String IBA_STANDARDNUMBER = "standardNumber";                             //标准号
    public static final String IBA_SPECIFICATIONS = "specifications";                             //规格
    public static final String IBA_CLPH = "CLPH";                                                 //材料牌号
    public static final String IBA_SURFACETREATMENT = "SurfaceTreatment";                         //表面处理
    public static final String IBA_HEATTREATMENT = "HeatTreatment";                               //热处理
    public static final String IBA_PRODUCTPATTERN = "productPattern";                             //产品型式
    public static final String IBA_SUBSEQUENCENUMBER = "subsequenceNumber";                       //子件序号
    public static final String IBA_SUBSEQUENCENAME = "subsequenceName";                           //子件名称
    public static final String IBA_DIMENSIONSTOLERANCE = "dimensionsTolerance";                   //关注尺寸的公差带
    public static final String IBA_SAFETYHOLETYPE = "safetyHoleType";                             //保险孔型式
    public static final String IBA_ADDITIONALTECHNICALCONDITIONS = "additionalTechnicalConditions";             //附加技术条件
    
    
    //元器件（IBA属性）（“分类，物品外文名称，”同上）
    public static final String IBA_ITEMCODE = "itemCode";                                         //物品码
    public static final String IBA_SERIESNO = "seriesNo";                   					  //型号系列
    public static final String IBA_GENERALSPECIFICATION = "generalSpecification";        		  //总规范
    public static final String IBA_DETAILEDSPECIFICATION = "detailedSpecification";    			  //详细规范
    public static final String IBA_INEFFICIENCYLEVEL = "inefficiencyLevel";          			  //失效率等级/质量保证等级	
    public static final String IBA_WORKINGTEMPERATURE = "workingTemperature";         			  //工作温度
    public static final String IBA_RATEDCAPACITY = "ratedCapacity";            					  //额定容量
    public static final String IBA_RATEDVOLTAGE = "ratedVoltage";              					  //额定电压
    public static final String IBA_DEVIATION = "deviation";           							  //容量允许偏差
    public static final String IBA_ENCAPSULATIONSIZE = "encapsulationSize";    				      //外形尺寸/封装形式
	public static final String IBA_TEMPERATURECOEFFICIENT = "temperatureCoefficient";	          //温度系数/温度特性
	public static final String IBA_SPECIFICATIONTYPE = "specificationType";	                      //代号（规格型号）
	public static final String IBA_MANUFACTURER = "manufacturer";	                              //生产厂家
	public static final String IBC_GROUPPARTICLENO = "GROUPPARTICLENO";	                          //集团物品码
	public static final String IBA_ACCEPTANCEPHASE = "acceptancePhase";	                          //验收状态
	public static final String IBA_TECHNICALCONDITIONS  ="technicalConditions";	                  //技术状态
	public static final String IBA_XYNO = "XYNO";                                                 //成品协议号（协议编号）
	public static final String IBA_PRTUI = "prtui";	                                              //采购单位
	public static final String IBA_MTLSGTR = "mtlsgtr";	                                          //密度
	public static final String IBA_PURCHECKIT = "purCheckIt";	                                  //检验周期
	public static final String IBA_PURDAYS = "purDays";	                                          //采购周期
	
	
	//辅料（IBA属性）（“ 物品码 ，协议编号，材料牌号，生产厂家，密度，检验周期，采购周期”同上）
	public static final String IBA_PROTOCOLNAME = "protocolName";	                              //协议名称
	public static final	String IBA_CLMC = "CLMC";			                                      //材料名称
	public static final	String IBA_MATERIALSPECIFICATION = "materialSpecification";			      //材料规格
	public static final	String IBA_MATERIALSTECHNICALCONDITIONS = "materialsTechnicalConditions"; //材料技术条件
	public static final	String IBA_DYCLMC = "DYCLMC";											  //代用材料名称
	public static final	String IBA_DYCLGG = "DYCLGG";											  //代用材料规格
	public static final	String IBA_DYCLJXTJ = "DYCLJXTJ";										  //代用材料技术条件
	public static final	String IBA_DYCLPH = "DYCLPH";											  //代用材料牌号
	public static final	String IBA_DEFAULTCOMPANY = "defaultCompany";							  //默认单位

	//单位
	public static final String UNIT_TAI = "台";
	public static final String UNIT_TAO = "套";
	public static final String UNIT_EA = "个";
	public static final String UNIT_JIAN = "件";
	public static final String UNIT_TAI_VALUE = "tai";
	public static final String UNIT_TAO_VALUE = "tao";
	public static final String UNIT_EA_VALUE = "ea";
	public static final String UNIT_JIAN_VALUE = "jian";
	
	//WTPartUsageLink
	public static final String cardAddress = "cardAddress";                                                  //插件位置
	
	
}
