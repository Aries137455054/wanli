package ext.com.wanli.part.entity;

public class ExcelDataBean {
	
	private String sheetName;                                          //sheet名称
	private int sheetRowNumber;                                        //行数量
	
	//系统-子系统-产品IBA属性
	private String ProductType = "";          						   //产品分类
	private String ProductCode = "";                                   //产品代号
	private String MODELNO = ""; 									   //型号
	private String ZJS = "";										   //主机厂（所）
	private String SubordinateSystem = "";							   //所属系统
	private String Importance = "";								       //重要度
	private String KTLX = "";										   //课题类型
	private String DESIGNDEPART = "";								   //设计部门
	private String OriginalDesign = "";								   //原始设计
	private String MASTERDESIGNER = "";								   //主管设计
	private String SUPDESIGNER = "";								   //辅管设计
	private String technicalRequirement = "";						   //技术要求
	private String wanliPhase = "";									   //阶段
	private boolean ISREMODEL = false;								   //是否改型
	private String PrototypeNo = "";							       //原型代号
	private String USAGE = "";										   //用途
	private String SECRETLEVEL = "";								   //密别
	private boolean hasSoftwarePart = false;						   //是否有软件
	private String ImportanceLevel = "";							   //重要等级
	
	
	//零组件IBA属性
	private String OriginalProductCode = "";						   //原代号
	private String PARTTYPE = "";									   //（零部件）类型
	private String TUFU = "";										   //图幅
	private String GUANZHONG = "";									   //关重件
	private String GeneralTolerance = "";							   //一般公差
	private String SurfaceRoughness = "";							   //表面粗糙度
	private String Weight = "";										   //重量
	
	//电子零组件IBA属性
	private String PCBCODE = "";									   //PCB板代号
	private String PCBNAME = "";									   //PCB板名称
	private String VERSION = "";								       //PCB板版本
	private String PRINTPLATEPROCESSINGSTANDARD = ""; 				   //印制板加工标准
	private String PCBDESIGNER = "";								   //PCB板设计人员
	private String PCBLENGTH = "";									   //PCB板长
	private String PCBWIDE = "";									   //PCB板宽
	private String PCBNUMBER = "";									   //PCB板层数
	private String PCBAREA = "";								       //PCB板面积
	private String THICKNESS = "";									   //厚度
	private String PCBEFFECTSOCLENUMBER = "";						   //PCB板有效管脚数
	
	//软件（IBA属性）（软件代号：ProductCode，用途：IBA_USAGE，版本,IBA_PCBVERSION，设计部门：IBA_DESIGNDEPART，）
	private String configureItemName = "";                            //配置项名称
	private String softwareStatus = ""; 							  //软件状态
	private String SSCP = "";  										  //所属产品
	private String softwareLeader = "";  						      //项目软件组长
	private String residentHardware = "";  							  //驻留硬件
	
	//标准件IBA属性
	private String categoryCode  ="";                                  //分类（一级分类/二级分类/三级分类(标准件分类储存方式，用“/”作为分隔符)）
    private String name = "";                                          //物品中文名称
	private String enName = "";                                        //物品外文名称
	private String standardLevelName = "";                             //标准级别名称
	private String standardNumber = "";                                //标准号
	private String specifications = "";                                //规格
	private String CLPH = "";                                          //材料牌号
	private String SurfaceTreatment = "";                              //表面处理
	private String HeatTreatment = "";                                 //热处理
	private String productPattern = "";                                //产品型式
	private String subsequenceNumber = "";                             //子件序号
	private String subsequenceName = "";                               //子件名称
	private String dimensionsTolerance = "";                           //关注尺寸的公差带
	private String safetyHoleType = "";                                //保险孔型式
	private String additionalTechnicalConditions = "";                 //附加技术条件

	
	//元器件IBA属性（“分类，物品外文名称，”同上）
	private String itemCode = "";                                       //物品码
    private String seriesNo = "";                   					//型号系列
    private String generalSpecification = "";        		            //总规范
    private String detailedSpecification = "";    			            //详细规范
    private String inefficiencyLevel = "";          			        //失效率等级/质量保证等级	
    private String workingTemperature = "";         			        //工作温度
    private String ratedCapacity = "";            					    //额定容量
    private String ratedVoltage = "";              					    //额定电压
    private String deviation = "";           							//容量允许偏差
    private String encapsulationSize = "";    				            //外形尺寸/封装形式
	private String temperatureCoefficient = "";	                        //温度系数/温度特性
	private String specificationType = "";	                            //代号（规格型号）
	private String manufacturer = "";	                                //生产厂家
	private String GROUPPARTICLENO = "";	                            //集团物品码
	private String acceptancePhase = "";	                            //验收状态
	private String technicalConditions  ="";	                        //技术状态
	private String XYNO = "";                                           //成品协议号（协议编号）
	private String prtui = "";	                                        //采购单位
	private String mtlsgtr = "";	                                    //密度
	private String purCheckIt = "";	                                    //检验周期
	private String purDays = "";	                                    //采购周期
	private String unit = "";                                           //单位
	
	
	//辅料IBA属性（“ 物品码 ，协议编号，材料牌号，生产厂家，密度，检验周期，采购周期，单位”同上）
	private String protocolName = "";  									 //协议名称
	private String CLMC = "";  											 //材料名称
	private String materialSpecification = "";  				         //材料规格
	private String materialsTechnicalConditions = "";                    //材料技术条件
	private String DYCLMC = "";  					     				 //代用材料名称
	private String DYCLGG = "";                                          //代用材料规格
	public String DYCLJXTJ = "";										 //代用材料技术条件
	private String DYCLPH = "";  					     				 //代用材料牌号
	private String defaultCompany = "";        						     //默认单位
	
	
	//EBOM结构
	private String fatherNumber = "";    								 //父项编号
	private String childNumber = "";     								 //子项编号
	private double quantityAmount = 0;                                   //数量
	private String cardAddress = "";                                     //插件位置
	
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public int getSheetRowNumber() {
		return sheetRowNumber;
	}
	public void setSheetRowNumber(int sheetRowNumber) {
		this.sheetRowNumber = sheetRowNumber;
	}
	public String getProductType() {
		return ProductType;
	}
	public void setProductType(String productType) {
		ProductType = productType;
	}
	public String getProductCode() {
		return ProductCode;
	}
	public void setProductCode(String productCode) {
		ProductCode = productCode;
	}
	public String getMODELNO() {
		return MODELNO;
	}
	public void setMODELNO(String mODELNO) {
		MODELNO = mODELNO;
	}
	public String getZJS() {
		return ZJS;
	}
	public void setZJS(String zJS) {
		ZJS = zJS;
	}
	public String getSubordinateSystem() {
		return SubordinateSystem;
	}
	public void setSubordinateSystem(String subordinateSystem) {
		SubordinateSystem = subordinateSystem;
	}
	public String getImportance() {
		return Importance;
	}
	public void setImportance(String importance) {
		Importance = importance;
	}
	public String getKTLX() {
		return KTLX;
	}
	public void setKTLX(String kTLX) {
		KTLX = kTLX;
	}
	public String getDESIGNDEPART() {
		return DESIGNDEPART;
	}
	public void setDESIGNDEPART(String dESIGNDEPART) {
		DESIGNDEPART = dESIGNDEPART;
	}
	public String getOriginalDesign() {
		return OriginalDesign;
	}
	public void setOriginalDesign(String originalDesign) {
		OriginalDesign = originalDesign;
	}
	public String getMASTERDESIGNER() {
		return MASTERDESIGNER;
	}
	public void setMASTERDESIGNER(String mASTERDESIGNER) {
		MASTERDESIGNER = mASTERDESIGNER;
	}
	public String getSUPDESIGNER() {
		return SUPDESIGNER;
	}
	public void setSUPDESIGNER(String sUPDESIGNER) {
		SUPDESIGNER = sUPDESIGNER;
	}
	public String getTechnicalRequirement() {
		return technicalRequirement;
	}
	public void setTechnicalRequirement(String technicalRequirement) {
		this.technicalRequirement = technicalRequirement;
	}
	public String getWanliPhase() {
		return wanliPhase;
	}
	public void setWanliPhase(String wanliPhase) {
		this.wanliPhase = wanliPhase;
	}
	public boolean isREMODEL() {
		return ISREMODEL;
	}
	public void setISREMODEL(boolean iSREMODEL) {
		ISREMODEL = iSREMODEL;
	}
	public String getPrototypeNo() {
		return PrototypeNo;
	}
	public void setPrototypeNo(String prototypeNo) {
		PrototypeNo = prototypeNo;
	}
	public String getUSAGE() {
		return USAGE;
	}
	public void setUSAGE(String uSAGE) {
		USAGE = uSAGE;
	}
	public String getSECRETLEVEL() {
		return SECRETLEVEL;
	}
	public void setSECRETLEVEL(String sECRETLEVEL) {
		SECRETLEVEL = sECRETLEVEL;
	}
	public boolean isHasSoftwarePart() {
		return hasSoftwarePart;
	}
	public void setHasSoftwarePart(boolean hasSoftwarePart) {
		this.hasSoftwarePart = hasSoftwarePart;
	}
	public String getImportanceLevel() {
		return ImportanceLevel;
	}
	public void setImportanceLevel(String importanceLevel) {
		ImportanceLevel = importanceLevel;
	}
	public String getOriginalProductCode() {
		return OriginalProductCode;
	}
	public void setOriginalProductCode(String originalProductCode) {
		OriginalProductCode = originalProductCode;
	}
	public String getPARTTYPE() {
		return PARTTYPE;
	}
	public void setPARTTYPE(String pARTTYPE) {
		PARTTYPE = pARTTYPE;
	}
	public String getTUFU() {
		return TUFU;
	}
	public void setTUFU(String tUFU) {
		TUFU = tUFU;
	}
	public String getGUANZHONG() {
		return GUANZHONG;
	}
	public void setGUANZHONG(String gUANZHONG) {
		GUANZHONG = gUANZHONG;
	}
	public String getGeneralTolerance() {
		return GeneralTolerance;
	}
	public void setGeneralTolerance(String generalTolerance) {
		GeneralTolerance = generalTolerance;
	}
	public String getSurfaceRoughness() {
		return SurfaceRoughness;
	}
	public void setSurfaceRoughness(String surfaceRoughness) {
		SurfaceRoughness = surfaceRoughness;
	}
	public String getWeight() {
		return Weight;
	}
	public void setWeight(String weight) {
		Weight = weight;
	}
	public String getPCBCODE() {
		return PCBCODE;
	}
	public void setPCBCODE(String pCBCODE) {
		PCBCODE = pCBCODE;
	}
	public String getPCBNAME() {
		return PCBNAME;
	}
	public void setPCBNAME(String pCBNAME) {
		PCBNAME = pCBNAME;
	}
	public String getVERSION() {
		return VERSION;
	}
	public void setVERSION(String vERSION) {
		VERSION = vERSION;
	}
	public String getPRINTPLATEPROCESSINGSTANDARD() {
		return PRINTPLATEPROCESSINGSTANDARD;
	}
	public void setPRINTPLATEPROCESSINGSTANDARD(String pRINTPLATEPROCESSINGSTANDARD) {
		PRINTPLATEPROCESSINGSTANDARD = pRINTPLATEPROCESSINGSTANDARD;
	}
	public String getPCBDESIGNER() {
		return PCBDESIGNER;
	}
	public void setPCBDESIGNER(String pCBDESIGNER) {
		PCBDESIGNER = pCBDESIGNER;
	}
	public String getPCBLENGTH() {
		return PCBLENGTH;
	}
	public void setPCBLENGTH(String pCBLENGTH) {
		PCBLENGTH = pCBLENGTH;
	}
	public String getPCBWIDE() {
		return PCBWIDE;
	}
	public void setPCBWIDE(String pCBWIDE) {
		PCBWIDE = pCBWIDE;
	}
	public String getPCBNUMBER() {
		return PCBNUMBER;
	}
	public void setPCBNUMBER(String pCBNUMBER) {
		PCBNUMBER = pCBNUMBER;
	}
	public String getPCBAREA() {
		return PCBAREA;
	}
	public void setPCBAREA(String pCBAREA) {
		PCBAREA = pCBAREA;
	}
	public String getTHICKNESS() {
		return THICKNESS;
	}
	public void setTHICKNESS(String tHICKNESS) {
		THICKNESS = tHICKNESS;
	}
	public String getPCBEFFECTSOCLENUMBER() {
		return PCBEFFECTSOCLENUMBER;
	}
	public void setPCBEFFECTSOCLENUMBER(String pCBEFFECTSOCLENUMBER) {
		PCBEFFECTSOCLENUMBER = pCBEFFECTSOCLENUMBER;
	}
	public String getConfigureItemName() {
		return configureItemName;
	}
	public void setConfigureItemName(String configureItemName) {
		this.configureItemName = configureItemName;
	}
	public String getSoftwareStatus() {
		return softwareStatus;
	}
	public void setSoftwareStatus(String softwareStatus) {
		this.softwareStatus = softwareStatus;
	}
	public String getSSCP() {
		return SSCP;
	}
	public void setSSCP(String sSCP) {
		SSCP = sSCP;
	}
	public String getSoftwareLeader() {
		return softwareLeader;
	}
	public void setSoftwareLeader(String softwareLeader) {
		this.softwareLeader = softwareLeader;
	}
	public String getResidentHardware() {
		return residentHardware;
	}
	public void setResidentHardware(String residentHardware) {
		this.residentHardware = residentHardware;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getStandardLevelName() {
		return standardLevelName;
	}
	public void setStandardLevelName(String standardLevelName) {
		this.standardLevelName = standardLevelName;
	}
	public String getStandardNumber() {
		return standardNumber;
	}
	public void setStandardNumber(String standardNumber) {
		this.standardNumber = standardNumber;
	}
	public String getSpecifications() {
		return specifications;
	}
	public void setSpecifications(String specifications) {
		this.specifications = specifications;
	}
	public String getCLPH() {
		return CLPH;
	}
	public void setCLPH(String cLPH) {
		CLPH = cLPH;
	}
	public String getSurfaceTreatment() {
		return SurfaceTreatment;
	}
	public void setSurfaceTreatment(String surfaceTreatment) {
		SurfaceTreatment = surfaceTreatment;
	}
	public String getHeatTreatment() {
		return HeatTreatment;
	}
	public void setHeatTreatment(String heatTreatment) {
		HeatTreatment = heatTreatment;
	}
	public String getProductPattern() {
		return productPattern;
	}
	public void setProductPattern(String productPattern) {
		this.productPattern = productPattern;
	}
	public String getSubsequenceNumber() {
		return subsequenceNumber;
	}
	public void setSubsequenceNumber(String subsequenceNumber) {
		this.subsequenceNumber = subsequenceNumber;
	}
	public String getSubsequenceName() {
		return subsequenceName;
	}
	public void setSubsequenceName(String subsequenceName) {
		this.subsequenceName = subsequenceName;
	}
	public String getDimensionsTolerance() {
		return dimensionsTolerance;
	}
	public void setDimensionsTolerance(String dimensionsTolerance) {
		this.dimensionsTolerance = dimensionsTolerance;
	}
	public String getSafetyHoleType() {
		return safetyHoleType;
	}
	public void setSafetyHoleType(String safetyHoleType) {
		this.safetyHoleType = safetyHoleType;
	}
	public String getAdditionalTechnicalConditions() {
		return additionalTechnicalConditions;
	}
	public void setAdditionalTechnicalConditions(String additionalTechnicalConditions) {
		this.additionalTechnicalConditions = additionalTechnicalConditions;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getSeriesNo() {
		return seriesNo;
	}
	public void setSeriesNo(String seriesNo) {
		this.seriesNo = seriesNo;
	}
	public String getGeneralSpecification() {
		return generalSpecification;
	}
	public void setGeneralSpecification(String generalSpecification) {
		this.generalSpecification = generalSpecification;
	}
	public String getDetailedSpecification() {
		return detailedSpecification;
	}
	public void setDetailedSpecification(String detailedSpecification) {
		this.detailedSpecification = detailedSpecification;
	}
	public String getInefficiencyLevel() {
		return inefficiencyLevel;
	}
	public void setInefficiencyLevel(String inefficiencyLevel) {
		this.inefficiencyLevel = inefficiencyLevel;
	}
	public String getWorkingTemperature() {
		return workingTemperature;
	}
	public void setWorkingTemperature(String workingTemperature) {
		this.workingTemperature = workingTemperature;
	}
	public String getRatedCapacity() {
		return ratedCapacity;
	}
	public void setRatedCapacity(String ratedCapacity) {
		this.ratedCapacity = ratedCapacity;
	}
	public String getRatedVoltage() {
		return ratedVoltage;
	}
	public void setRatedVoltage(String ratedVoltage) {
		this.ratedVoltage = ratedVoltage;
	}
	public String getDeviation() {
		return deviation;
	}
	public void setDeviation(String deviation) {
		this.deviation = deviation;
	}
	public String getEncapsulationSize() {
		return encapsulationSize;
	}
	public void setEncapsulationSize(String encapsulationSize) {
		this.encapsulationSize = encapsulationSize;
	}
	public String getTemperatureCoefficient() {
		return temperatureCoefficient;
	}
	public void setTemperatureCoefficient(String temperatureCoefficient) {
		this.temperatureCoefficient = temperatureCoefficient;
	}
	public String getSpecificationType() {
		return specificationType;
	}
	public void setSpecificationType(String specificationType) {
		this.specificationType = specificationType;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getGROUPPARTICLENO() {
		return GROUPPARTICLENO;
	}
	public void setGROUPPARTICLENO(String gROUPPARTICLENO) {
		GROUPPARTICLENO = gROUPPARTICLENO;
	}
	public String getAcceptancePhase() {
		return acceptancePhase;
	}
	public void setAcceptancePhase(String acceptancePhase) {
		this.acceptancePhase = acceptancePhase;
	}
	public String getTechnicalConditions() {
		return technicalConditions;
	}
	public void setTechnicalConditions(String technicalConditions) {
		this.technicalConditions = technicalConditions;
	}
	public String getXYNO() {
		return XYNO;
	}
	public void setXYNO(String xYNO) {
		XYNO = xYNO;
	}
	public String getPrtui() {
		return prtui;
	}
	public void setPrtui(String prtui) {
		this.prtui = prtui;
	}
	public String getMtlsgtr() {
		return mtlsgtr;
	}
	public void setMtlsgtr(String mtlsgtr) {
		this.mtlsgtr = mtlsgtr;
	}
	public String getPurCheckIt() {
		return purCheckIt;
	}
	public void setPurCheckIt(String purCheckIt) {
		this.purCheckIt = purCheckIt;
	}
	public String getPurDays() {
		return purDays;
	}
	public void setPurDays(String purDays) {
		this.purDays = purDays;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getProtocolName() {
		return protocolName;
	}
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	public String getCLMC() {
		return CLMC;
	}
	public void setCLMC(String cLMC) {
		CLMC = cLMC;
	}
	public String getMaterialSpecification() {
		return materialSpecification;
	}
	public void setMaterialSpecification(String materialSpecification) {
		this.materialSpecification = materialSpecification;
	}
	public String getMaterialsTechnicalConditions() {
		return materialsTechnicalConditions;
	}
	public void setMaterialsTechnicalConditions(String materialsTechnicalConditions) {
		this.materialsTechnicalConditions = materialsTechnicalConditions;
	}
	public String getDYCLMC() {
		return DYCLMC;
	}
	public void setDYCLMC(String dYCLMC) {
		DYCLMC = dYCLMC;
	}
	public String getDYCLGG() {
		return DYCLGG;
	}
	public void setDYCLGG(String dYCLGG) {
		DYCLGG = dYCLGG;
	}
	public String getDYCLJXTJ() {
		return DYCLJXTJ;
	}
	public void setDYCLJXTJ(String dYCLJXTJ) {
		DYCLJXTJ = dYCLJXTJ;
	}
	public String getDYCLPH() {
		return DYCLPH;
	}
	public void setDYCLPH(String dYCLPH) {
		DYCLPH = dYCLPH;
	}
	public String getDefaultCompany() {
		return defaultCompany;
	}
	public void setDefaultCompany(String defaultCompany) {
		this.defaultCompany = defaultCompany;
	}
	public String getFatherNumber() {
		return fatherNumber;
	}
	public void setFatherNumber(String fatherNumber) {
		this.fatherNumber = fatherNumber;
	}
	public String getChildNumber() {
		return childNumber;
	}
	public void setChildNumber(String childNumber) {
		this.childNumber = childNumber;
	}
	public double getQuantityAmount() {
		return quantityAmount;
	}
	public void setQuantityAmount(double quantityAmount) {
		this.quantityAmount = quantityAmount;
	}
	public String getCardAddress() {
		return cardAddress;
	}
	public void setCardAddress(String cardAddress) {
		this.cardAddress = cardAddress;
	}
}
