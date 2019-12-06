package ext.com.wanli.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ext.com.wanli.part.PartConstant;
import ext.com.wanli.part.entity.ExcelDataBean;

/**
 * 
 * @Title: ExcelUtil.java 
 * @Package ext.com.wanli.util 
 * @author zhangmingjie
 * @date 2019年11月29日 下午4:15:17 
 * @version V1.0
 */
public class ExcelUtil {

	private static Logger logger = Logger.getLogger(ExcelUtil.class);
	
	/**
	 * @Title: readExcel
	 * @param @param filePath
	 * @return void
	 * @author zhangmingjie
	 * @date 2019年11月29日 下午4:16:20
	 */
	public static Map<String,List<ExcelDataBean>> readExcel(String filePath){
		Map<String,List<ExcelDataBean>> excelDataMap = new HashMap<>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			for (Sheet sheet : workbook) {
				List<ExcelDataBean> excelDataBeanList = new ArrayList<>();
				String sheetName = sheet.getSheetName();
				
				if(PartConstant.SHEET_NAME_SHEET1.equals(sheetName) ||                  //Sheet1
						PartConstant.SHEET_NAME_EXPLAIN.equals(sheetName)||             //说明
						PartConstant.SHEET_NAME_FILLININSTRUCTIONS.equals(sheetName)||  //填写说明
						//PartConstant.SHEET_NAME_EBOM.equals(sheetName)||                //EBOM结构
						PartConstant.SHEET_NAME_SCHEMATICDIAGRAM.equals(sheetName)){    //关联原理图-外形尺寸等
					continue;
				}
				logger.debug(">>>>>sheetName="+sheetName);
				
				//系统-子系统-产品
				if(PartConstant.SHEET_NAME_SYSTEMPRODUCT.equals(sheetName)){
					List<ExcelDataBean> systemList = new ArrayList<>();
					List<ExcelDataBean> subsystemList = new ArrayList<>();
					List<ExcelDataBean> productList = new ArrayList<>();
					
					//行
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						
						Row row = sheet.getRow(rowNum);
						buildExcelSystemPartData(excelDataBean,row);
						//产品分类
						String productType = excelDataBean.getProductType();
						
						//产品代号
						String productCode = excelDataBean.getProductCode();
						String modelNo = excelDataBean.getMODELNO();
						logger.debug(">>>>>productCode="+productCode+" ,modelNo="+modelNo);
						
						//系统产品
						if(PartConstant.CELL_NAME_SYSTEM.equals(productType)){
							systemList.add(excelDataBean);
						}
						//子系统产品
						else if(PartConstant.CELL_NAME_SUBSYSTEM.equals(productType)){
							subsystemList.add(excelDataBean);
						}
						//产品
						else if(PartConstant.CELL_NAME_PRODUCT.equals(productType)){
							productList.add(excelDataBean);
						}
					}
					excelDataMap.put(PartConstant.CELL_NAME_SYSTEM, systemList);
					excelDataMap.put(PartConstant.CELL_NAME_SUBSYSTEM, subsystemList);
					excelDataMap.put(PartConstant.CELL_NAME_PRODUCT, productList);
				}
				//零组件
				else if(PartConstant.SHEET_NAME_SPARECOMPONMENTPARTS.equals(sheetName)){
					List<ExcelDataBean> sparePartList = new ArrayList<>();
					List<ExcelDataBean> componmentPartList = new ArrayList<>();
					
					//行
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						
						Row row = sheet.getRow(rowNum);
						buildExcelSpareComponmentPartData(excelDataBean,row);
						
						String partType = excelDataBean.getPARTTYPE();
						//零件
						if(PartConstant.CELL_NAME_SPAREPARTS.equals(partType)){
							sparePartList.add(excelDataBean);
						}
						//组件
						else if(PartConstant.CELL_NAME_COMPONMENTPARTS.equals(partType)){
							componmentPartList.add(excelDataBean);
						}
					}
					excelDataMap.put(PartConstant.CELL_NAME_SPAREPARTS, sparePartList);
					excelDataMap.put(PartConstant.CELL_NAME_COMPONMENTPARTS, componmentPartList);
				}
				//电子零组件
				else if(PartConstant.SHEET_NAME_ELECTRONICSPAREPARTS.equals(sheetName)){
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						Row row = sheet.getRow(rowNum);
						buildExcelElectronicPartData(excelDataBean,row);
						excelDataBeanList.add(excelDataBean);
					}
					excelDataMap.put(PartConstant.PARTTYPE_NAME_ELECTRONICPART, excelDataBeanList);
				}
				//软件
				else if(PartConstant.SHEET_NAME_SOFTWARE.equals(sheetName)){
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						Row row = sheet.getRow(rowNum);
						buildExcelSoftwarePartData(excelDataBean,row);
						excelDataBeanList.add(excelDataBean);
					}
					excelDataMap.put(PartConstant.PARTTYPE_NAME_SOFTWAREPART, excelDataBeanList);
				}
				//标准件
				else if(PartConstant.SHEET_NAME_STDPART.equals(sheetName)){
					//行
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						
						Row row = sheet.getRow(rowNum);
						buildExcelSTDPartData(excelDataBean,row);
						excelDataBeanList.add(excelDataBean);
					}
					excelDataMap.put(PartConstant.PARTTYPE_NAME_STDPART, excelDataBeanList);
				}
				//元器件
				else if(PartConstant.SHEET_NAME_COMPONENTPART.equals(sheetName)){
					//行
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						
						Row row = sheet.getRow(rowNum);
						buildExcelComponentData(excelDataBean,row);
						excelDataBeanList.add(excelDataBean);
					}
					excelDataMap.put(PartConstant.PARTTYPE_NAME_COMPONENTPART, excelDataBeanList);
				}
				//辅料
				else if(PartConstant.SHEET_NAME_SUBSIDIARY.equals(sheetName)){
					//行
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						
						Row row = sheet.getRow(rowNum);
						buildExcelSubsidiaryMaterialData(excelDataBean,row);
						excelDataBeanList.add(excelDataBean);
					}
					excelDataMap.put(PartConstant.PARTTYPE_NAME_SUBSIDIARYMATERIAL, excelDataBeanList);
				}
				//EBOM结构
				else if(PartConstant.SHEET_NAME_EBOM.equals(sheetName)){
					//行
					for(int rowNum = 1; rowNum < sheet.getLastRowNum()+1 ; rowNum++ ){
						ExcelDataBean excelDataBean = new ExcelDataBean();
						excelDataBean.setSheetName(sheetName);
						excelDataBean.setSheetRowNumber(sheet.getLastRowNum()+1);
						
						Row row = sheet.getRow(rowNum);
						Cell cell = row.getCell(1);
						String fatherNumber = getStringFromCell(cell);
						if(!"".equals(fatherNumber)){
							buildExcelEBOMData(excelDataBean,row);
							excelDataBeanList.add(excelDataBean);
						}
					}
					excelDataMap.put(PartConstant.SHEET_NAME_EBOM, excelDataBeanList);
				}
				//关联原理图-外形尺寸等
				else if(PartConstant.SHEET_NAME_SCHEMATICDIAGRAM.equals(sheetName)){
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					fis = null;
					e.printStackTrace();
				}
			}
		}
		return excelDataMap;
	}
	
	/**
	 * Excel数据与POJO绑定(系统-子系统-组件)
	 * @param excelDataBean
	 * @param row
	 * @author zhangmingjie
	 * @date 2019年12月3日 上午9:58:56
	 */
	public static void buildExcelSystemPartData(ExcelDataBean excelDataBean,Row row){
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			if(cellNum == 0){
				
			}else if(cellNum == 1){
				excelDataBean.setProductType(cellValue);//产品分类
			}else if(cellNum == 2){
				cellValue = cellValueFormat(row,cellNum ,cellValue);
				excelDataBean.setProductCode(cellValue);//产品代号
			}else if(cellNum == 3){
				excelDataBean.setMODELNO(cellValue);//型号
			}else if(cellNum == 4){
				excelDataBean.setName(cellValue);//名称
			}else if(cellNum == 5){
				excelDataBean.setZJS(cellValue);//主机厂（所）
			}else if(cellNum == 6){
				excelDataBean.setSubordinateSystem(cellValue);//所属系统
			}else if(cellNum == 7){
				excelDataBean.setImportance(cellValue);//重要度
			}else if(cellNum == 8){
				excelDataBean.setKTLX(cellValue);//课题类型
			}else if(cellNum == 9){
				excelDataBean.setUnit(cellValue);//单位
			}else if(cellNum == 10){
				excelDataBean.setDESIGNDEPART(cellValue);//设计部门
			}else if(cellNum == 11){
				excelDataBean.setOriginalDesign(cellValue);//原始设计
			}else if(cellNum == 12){
				excelDataBean.setMASTERDESIGNER(cellValue);//主管设计
			}else if(cellNum == 13){
				excelDataBean.setSUPDESIGNER(cellValue);//辅管设计
			}else if(cellNum == 14){
				excelDataBean.setTechnicalRequirement(cellValue);//技术要求
			}else if(cellNum == 15){
				excelDataBean.setWanliPhase(cellValue);//阶段
			}else if(cellNum == 16){
				if("是".equals(cellValue)){    //是否改型
					excelDataBean.setISREMODEL(true);
				}else{
					excelDataBean.setISREMODEL(false);
				}
			}else if(cellNum == 17){
				excelDataBean.setPrototypeNo(cellValue);//原型代号
			}else if(cellNum == 18){
				excelDataBean.setUSAGE(cellValue);//用途
			}else if(cellNum == 19){
				excelDataBean.setSECRETLEVEL(cellValue);//密别
			}else if(cellNum == 20){
				excelDataBean.setItemCode(cellValue);//物品码
			}else if(cellNum == 21){
				if("有".equals(cellValue)){                 //是否有软件
					excelDataBean.setHasSoftwarePart(true);
				}else{
					excelDataBean.setHasSoftwarePart(false);
				}
			}else if(cellNum == 22){
				excelDataBean.setImportanceLevel(cellValue);//重要等级
			}else{
				logger.debug(">>>>>buildExcelSystemPartData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	}
	
	/**
	 * Excel数据与POJO绑定(零组件)
	 * @param excelDataBean
	 * @param row
	 * @author zhangmingjie
	 * @date 2019年12月3日 上午11:38:55
	 */
	public static void buildExcelSpareComponmentPartData(ExcelDataBean excelDataBean,Row row){
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			
			if(cellNum == 0){
				excelDataBean.setProductCode(cellValue);//代号
			}else if(cellNum == 1){
				excelDataBean.setOriginalProductCode(cellValue);//原代号
			}else if(cellNum == 2){
				excelDataBean.setName(cellValue);//名称
			}else if(cellNum == 3){
				excelDataBean.setPARTTYPE(cellValue);//类型
			}else if(cellNum == 4){
				excelDataBean.setWanliPhase(cellValue);//阶段
			}else if(cellNum == 5){
				excelDataBean.setTUFU(cellValue);//图幅
			}else if(cellNum == 6){
				excelDataBean.setDESIGNDEPART(cellValue);//设计部门
			}else if(cellNum == 7){
				excelDataBean.setGUANZHONG(cellValue);//关重件
			}else if(cellNum == 8){
				excelDataBean.setOriginalDesign(cellValue);//原始设计
			}else if(cellNum == 9){
				excelDataBean.setMASTERDESIGNER(cellValue);//主管设计
			}else if(cellNum == 10){
				excelDataBean.setSUPDESIGNER(cellValue);//辅管设计
			}else if(cellNum == 11){
				excelDataBean.setTechnicalRequirement(cellValue);//技术要求
			}else if(cellNum == 12){
				excelDataBean.setGeneralTolerance(cellValue);//一般公差
			}else if(cellNum == 13){
				excelDataBean.setSurfaceRoughness(cellValue);//表面粗糙度
			}else if(cellNum == 14){
				excelDataBean.setHeatTreatment(cellValue);//热处理
			}else if(cellNum == 15){
				excelDataBean.setSurfaceTreatment(cellValue);//表面处理
			}else if(cellNum == 16){
				excelDataBean.setCLMC(cellValue);//材料名称
			}else if(cellNum == 17){
				excelDataBean.setMaterialSpecification(cellValue);//材料规格
			}else if(cellNum == 18){
				excelDataBean.setMaterialsTechnicalConditions(cellValue);//材料技术条件
			}else if(cellNum == 19){
				excelDataBean.setCLPH(cellValue);//材料牌号
			}else if(cellNum == 20){
				excelDataBean.setDYCLMC(cellValue);//代用材料名称
			}else if(cellNum == 21){
				excelDataBean.setDYCLGG(cellValue);//代用材料规格
			}else if(cellNum == 22){
				excelDataBean.setDYCLJXTJ(cellValue);//代用材料技术条件
			}else if(cellNum == 23){
				excelDataBean.setDYCLPH(cellValue);//代用材料牌号
			}else if(cellNum == 24){
				excelDataBean.setWeight(cellValue);//重量
			}else{
				logger.debug(">>>>>buildExcelSpareComponmentPartData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	}
	
	/**
	 * Excel数据与POJO绑定(电子零组件)
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @date 2019年12月3日 下午1:56:53
	 */
	public static void buildExcelElectronicPartData(ExcelDataBean excelDataBean,Row row){
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			
			if(cellNum == 0){
				excelDataBean.setProductCode(cellValue);//代号
			}else if(cellNum == 1){
				excelDataBean.setOriginalProductCode(cellValue);//原代号
			}else if(cellNum == 2){
				excelDataBean.setMODELNO(cellValue);//型号
			}else if(cellNum == 3){
				excelDataBean.setName(cellValue);//名称
			}else if(cellNum == 4){
				excelDataBean.setPARTTYPE(cellValue);//类型
			}else if(cellNum == 5){
				excelDataBean.setWanliPhase(cellValue);//阶段
			}else if(cellNum == 6){
				excelDataBean.setOriginalDesign(cellValue);//原始设计
			}else if(cellNum == 7){
				excelDataBean.setMASTERDESIGNER(cellValue);//主管设计
			}else if(cellNum == 8){
				excelDataBean.setSUPDESIGNER(cellValue);//辅管设计
			}else if(cellNum == 9){
				excelDataBean.setTechnicalRequirement(cellValue);//技术要求
			}else if(cellNum == 10){
				excelDataBean.setTUFU(cellValue);//图幅
			}else if(cellNum == 11){
				excelDataBean.setDESIGNDEPART(cellValue);//设计部门
			}else if(cellNum == 12){
				excelDataBean.setSSCP(cellValue);//所属产品
			}else if(cellNum == 13){
				excelDataBean.setGUANZHONG(cellValue);//关重件
			}else if(cellNum == 14){
				excelDataBean.setSECRETLEVEL(cellValue);//密别
			}else if(cellNum == 15){
				excelDataBean.setCLMC(cellValue);//材料名称
			}else if(cellNum == 16){
				excelDataBean.setMaterialSpecification(cellValue);//材料规格
			}else if(cellNum == 17){
				excelDataBean.setMaterialsTechnicalConditions(cellValue);;//材料技术条件
			}else if(cellNum == 18){
				excelDataBean.setCLPH(cellValue);//材料牌号
			}else if(cellNum == 19){
				excelDataBean.setDYCLMC(cellValue);//代用材料名称
			}else if(cellNum == 20){
				excelDataBean.setDYCLGG(cellValue);//代用材料规格
			}else if(cellNum == 21){
				excelDataBean.setDYCLJXTJ(cellValue);//代用材料技术条件
			}else if(cellNum == 22){
				excelDataBean.setDYCLPH(cellValue);//代用材料牌号
			}else if(cellNum == 23){
				excelDataBean.setPCBCODE(cellValue);;//PCB板代号
			}else if(cellNum == 24){
				excelDataBean.setPCBNAME(cellValue);//PCB板名称
			}else if(cellNum == 25){
				excelDataBean.setVERSION(cellValue);//PCB板版本
			}else if(cellNum == 26){
				excelDataBean.setPRINTPLATEPROCESSINGSTANDARD(cellValue);//印制板加工标准
			}else if(cellNum == 27){
				excelDataBean.setPCBDESIGNER(cellValue);//PCB板设计人员
			}else if(cellNum == 28){
				excelDataBean.setPCBLENGTH(cellValue);//PCB板长
			}else if(cellNum == 29){
				excelDataBean.setPCBWIDE(cellValue);//PCB板宽
			}else if(cellNum == 30){
				excelDataBean.setPCBNUMBER(cellValue);//PCB板层数
			}else if(cellNum == 31){
				excelDataBean.setPCBAREA(cellValue);//PCB板面积
			}else if(cellNum == 32){
				excelDataBean.setTHICKNESS(cellValue);;//厚度
			}else if(cellNum == 33){
				excelDataBean.setPCBEFFECTSOCLENUMBER(cellValue);//PCB板有效管脚数
			}else if(cellNum == 34){
				excelDataBean.setWeight(cellValue);//重量
			}else if(cellNum == 35){
				excelDataBean.setUnit(cellValue);//单位
			}else{
				logger.debug(">>>>>buildExcelElectronicPartData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	}
	
	/**
	 * Excel数据与POJO绑定(软件)
	 * @param excelDataBean
	 * @author zhangmingjie
	 * @date 2019年12月3日 下午2:23:19
	 */
	public static void buildExcelSoftwarePartData(ExcelDataBean excelDataBean,Row row){
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			
			if(cellNum == 0){
				excelDataBean.setProductCode(cellValue);//软件代号  
			}else if(cellNum == 1){
				excelDataBean.setConfigureItemName(cellValue);//配置项名称
				excelDataBean.setName(cellValue);
			}else if(cellNum == 2){
				excelDataBean.setSoftwareStatus(cellValue);//软件状态
			}else if(cellNum == 3){
				excelDataBean .setVERSION(cellValue);//版本
			}else if(cellNum == 4){
				excelDataBean.setUSAGE(cellValue);//用途功能      
			}else if(cellNum == 5){
				excelDataBean.setDESIGNDEPART(cellValue);//设计部门                   
			}else if(cellNum == 6){
				excelDataBean.setSSCP(cellValue);//所属产品  
			}else if(cellNum == 7){
				excelDataBean.setSoftwareLeader(cellValue);//项目软件组长         
			}else if(cellNum == 8){
				excelDataBean.setResidentHardware(cellValue);//驻留硬件
			}else{
				logger.debug(">>>>>buildExcelSoftwarePartData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	}
	
	
	/**
	 * Excel数据与POJO绑定(标准件)
	 * @param excelDataBean
	 * @param row
	 * @author zhangmingjie
	 * @date 2019年12月2日 下午2:17:35
	 */
	public static void buildExcelSTDPartData(ExcelDataBean excelDataBean,Row row){
		StringBuffer sb = new StringBuffer();
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			if(!"".equals(cellValue) && cellNum < 2){
				sb.append(cellValue+"/");
			}
			if(!"".equals(cellValue) && cellNum == 2){
				sb.append(cellValue);
			}
			excelDataBean.setCategoryCode(sb.toString());             //分类
			if(cellNum == 3){
				excelDataBean.setName(cellValue);         			  //物品中文名称
			}else if(cellNum == 4){
				excelDataBean.setEnName(cellValue);                   //物品外文名称
			}else if(cellNum == 5){
				excelDataBean.setStandardLevelName(cellValue);        //标准级别名称
			}else if(cellNum == 6){
				excelDataBean.setStandardNumber(cellValue);           //标准号
			}else if(cellNum == 7){
				excelDataBean.setSpecifications(cellValue);           //规格
			}else if(cellNum == 8){
				excelDataBean.setCLPH(cellValue);                     //材料牌号
			}else if(cellNum == 9){
				excelDataBean.setSurfaceTreatment(cellValue);         //表面处理
			}else if(cellNum == 10){
				excelDataBean.setHeatTreatment(cellValue);            //热处理
			}else if(cellNum == 11){
				excelDataBean.setProductPattern(cellValue);           //产品型式
			}else if(cellNum == 12){
				excelDataBean.setSubsequenceNumber(cellValue);        //子件序号
			}else if(cellNum == 13){
				excelDataBean.setSubsequenceName(cellValue);          //子件名称
			}else if(cellNum == 14){
				excelDataBean.setDimensionsTolerance(cellValue);      //关注尺寸的公差带
			}else if(cellNum == 15){
				excelDataBean.setSafetyHoleType(cellValue);           //保险孔型式
			}else if(cellNum == 16){
				excelDataBean.setAdditionalTechnicalConditions(cellValue);//附加技术条件
			}else{
				logger.debug(">>>>>buildExcelSTDPartData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	
	}
	
	/**
	 * Excel数据与POJO绑定(辅料)
	 * @param excelDataBean
	 * @param row
	 * void
	 * @author zhangmingjie
	 * @date 2019年12月2日 下午2:18:49
	 */
	public static void buildExcelSubsidiaryMaterialData(ExcelDataBean excelDataBean,Row row){
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			if(cellNum == 0){
				excelDataBean.setItemCode(cellValue);                         //物品码
			}else if(cellNum == 1){
				excelDataBean.setXYNO(cellValue);                             //协议编号
			}else if(cellNum == 2){
				excelDataBean.setProtocolName(cellValue);                     //协议名称
			}else if(cellNum == 3){
				excelDataBean.setCLMC(cellValue);                             //材料名称
				excelDataBean.setName(cellValue);
			}else if(cellNum == 4){
				excelDataBean.setMaterialSpecification(cellValue);            //材料规格
			}else if(cellNum == 5){
				excelDataBean.setMaterialsTechnicalConditions(cellValue);     //材料技术条件
			}else if(cellNum == 6){
				excelDataBean.setCLPH(cellValue);                             //材料牌号
			}else if(cellNum == 7){
				excelDataBean.setDYCLMC(cellValue);                           //代用材料名称
			}else if(cellNum == 8){
				excelDataBean.setDYCLGG(cellValue);                           //代用材料规格
			}else if(cellNum == 9){
				excelDataBean.setDYCLJXTJ(cellValue);                         //代用材料技术条件
			}else if(cellNum == 10){
				excelDataBean.setDYCLPH(cellValue);                           //代用材料牌号
			}else if(cellNum == 11){
				excelDataBean.setDefaultCompany(cellValue);                   //默认单位
			}else if(cellNum == 12){
				excelDataBean.setManufacturer(cellValue);                     //生产厂家
			}else if(cellNum == 13){
				excelDataBean.setMtlsgtr(cellValue);                          //密度
			}else if(cellNum == 14){
				excelDataBean.setPurCheckIt(cellValue);                       //检验周期
			}else if(cellNum == 15){
				excelDataBean.setPurDays(cellValue);                          //采购周期
			}else if(cellNum == 16){
				excelDataBean.setUnit(cellValue);                             //单位
			}else{
				logger.debug(">>>>>buildExcelSubsidiaryMaterialData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	}
	
	/**
	 * Excel数据与POJO绑定(元器件)
	 * @return void
	 * @author zhangmingjie
	 * @date 2019年11月29日 下午5:43:33
	 */
	public static void buildExcelComponentData(ExcelDataBean excelDataBean,Row row){
		StringBuffer sb = new StringBuffer();
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			if(!"".equals(cellValue) && cellNum < 2){
				sb.append(cellValue+"/");
			}
			if(!"".equals(cellValue) && cellNum == 2){
				sb.append(cellValue);
			}
			
			excelDataBean.setCategoryCode(sb.toString());             //分类
			if(cellNum == 3){
				excelDataBean.setItemCode(cellValue);         		  //物品码
			}else if(cellNum == 4){
				excelDataBean.setName(cellValue);         			  //物品中文名称
			}else if(cellNum == 5){
				excelDataBean.setEnName(cellValue);                   //物品外文名称
			}else if(cellNum == 6){
				excelDataBean.setSeriesNo(cellValue);                 //型号系列
			}else if(cellNum == 7){
				excelDataBean.setGeneralSpecification(cellValue);     //总规范
			}else if(cellNum == 8){
				excelDataBean.setDetailedSpecification(cellValue);    //详细规范
			}else if(cellNum == 9){
				excelDataBean.setInefficiencyLevel(cellValue);        //失效率等级/质量保证等级	
			}else if(cellNum == 10){                              
				excelDataBean.setWorkingTemperature(cellValue);       //工作温度
			}else if(cellNum == 11){
				excelDataBean.setRatedCapacity(cellValue);            //额定容量
			}else if(cellNum == 12){
				excelDataBean.setRatedVoltage(cellValue);             //额定电压 
			}else if(cellNum == 13){
				excelDataBean.setDeviation(cellValue);                //容量允许偏差
			}else if(cellNum == 14){
 				excelDataBean.setEncapsulationSize(cellValue);        //外形尺寸/封装形式
			}else if(cellNum == 15){
				excelDataBean.setTemperatureCoefficient(cellValue);   //温度系数/温度特性
			}else if(cellNum == 16){
				excelDataBean.setSpecificationType(cellValue);        //代号（规格型号）
			}else if(cellNum == 17){
				excelDataBean.setManufacturer(cellValue);             //生产厂商
			}else if(cellNum == 18){
				excelDataBean.setGROUPPARTICLENO(cellValue);          //集团码
			}else if(cellNum == 19){
				excelDataBean.setAcceptancePhase(cellValue);          //验收状态
			}else if(cellNum == 20){
				excelDataBean.setTechnicalConditions(cellValue);      //技术状态
			}else if(cellNum == 21){
				excelDataBean.setXYNO(cellValue);                     //成品协议号
			}else if(cellNum == 22){
				excelDataBean.setPrtui(cellValue);                    //采购单位
			}else if(cellNum == 23){
				excelDataBean.setMtlsgtr(cellValue);                  //密度
			}else if(cellNum == 24){
				excelDataBean.setPurCheckIt(cellValue);               //检验周期
			}else if(cellNum == 25){
				excelDataBean.setPurDays(cellValue);                  //采购周期
			}else if(cellNum == 26){
				excelDataBean.setUnit(cellValue);                     //单位
			}else{
				logger.debug(">>>>>buildExcelComponentData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	}
	
	/**
	 * Excel数据与POJO绑定(EBOM)
	 * @param excelDataBean
	 * @param row
	 * @author zhangmingjie
	 * @date 2019年12月5日 上午10:17:35
	 */
	public static void buildExcelEBOMData(ExcelDataBean excelDataBean,Row row){
		//列
		for(int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++){
			Cell cell = row.getCell(cellNum);
			String cellValue = getStringFromCell(cell);
			if(cellNum == 0){
				         		  //序号
			}else if(cellNum == 1){
				cellValue = cellValueFormat(row,cellNum ,cellValue);
				excelDataBean.setFatherNumber(cellValue);//#父项编号
			}else if(cellNum == 2){
				cellValue = cellValueFormat(row,cellNum ,cellValue);
				excelDataBean.setChildNumber(cellValue);//#子项编号
			}else if(cellNum == 3){
				excelDataBean.setQuantityAmount(Double.valueOf(cellValue));//数量
			}else if(cellNum == 4){
				excelDataBean.setUnit(unitToValue(cellValue));//单位
			}else if(cellNum == 5){
				excelDataBean.setCardAddress(cellValue);//插件位置
			}else{
				logger.debug(">>>>>buildExcelComponentData other cellValue = "+cellNum+" ,"+cellValue);
			}
		}
	}
	
	/**
	 * Cell格式转化
	 * @Title: getStringFromCell 
	 * @param @param cell
	 * @param @return
	 * @return String
	 * @author zhangmingjie
	 * @date 2019年11月29日 下午5:15:24
	 */
	@SuppressWarnings("deprecation")
	public static String getStringFromCell(Cell cell) {
		String strResult = "";
		if (cell == null)
			return "";
		int type = cell.getCellType();
		switch (type) {
		case HSSFCell.CELL_TYPE_BOOLEAN:
			strResult = new Boolean(cell.getBooleanCellValue()).toString();
			break;
		case HSSFCell.CELL_TYPE_STRING:
			strResult = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			strResult = new Double(cell.getNumericCellValue()).toString();
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			strResult = cell.getCellFormula();
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			strResult = "";
			break;
		default:
			strResult = cell.getStringCellValue();
		}
		if (strResult != null)
			return strResult.trim();
		else
			return "";
	}
	
	
	/**
	 * 单位内部值转换
	 * @param value
	 * @author zhangmingjie
	 * @date 2019年12月4日 下午2:21:05
	 */
	public static String unitToValue(String unitStr){
		if(null == unitStr){
			return PartConstant.UNIT_EA_VALUE; 
		}
		if(unitStr.equals(PartConstant.UNIT_TAI)){
			return PartConstant.UNIT_TAI_VALUE;
		}else if(unitStr.equals(PartConstant.UNIT_TAO)){
			return PartConstant.UNIT_TAO_VALUE;
		}else if(unitStr.equals(PartConstant.UNIT_EA)){
			return PartConstant.UNIT_EA_VALUE;
		}else if(unitStr.equals(PartConstant.UNIT_JIAN)){
			return PartConstant.UNIT_JIAN_VALUE;
		}else{
			return PartConstant.UNIT_EA_VALUE;
		}
	}
	
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    if(str.indexOf(".")>0){//判断是否有小数点
	        if(str.indexOf(".")==str.lastIndexOf(".") && str.split("\\.").length==2){ //判断是否只有一个小数点
	            return pattern.matcher(str.replace(".","")).matches();
	        }else {
	            return false;
	        }
	    }else {
	        return pattern.matcher(str).matches();
	    }
	}
	
	/**
	 * //读取EXCEL时候，数字后面自动加.0的处理
	 * @param row
	 * @param cellNum
	 * @param cellValue
	 * @author zhangmingjie
	 * @date 2019年12月6日 上午10:50:40
	 */
	public static String cellValueFormat(Row row,int cellNum ,String cellValue){
		if(isNumeric(cellValue)){
			NumberFormat nf = NumberFormat.getInstance();
			String str = nf.format(row.getCell((short)cellNum).getNumericCellValue());
			cellValue=str.replace(",","");
			return cellValue;
		}
		return cellValue;
	}
}
