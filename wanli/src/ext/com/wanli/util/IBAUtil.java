package ext.com.wanli.util;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCNormalizedObject;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.meta.server.TypeIdentifierUtility;

import wt.csm.navigation.litenavigation.ClassificationNodeDefaultView;
import wt.csm.navigation.service.ClassificationHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.WTObject;
import wt.iba.definition.DefinitionLoader;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.RatioDefView;
import wt.iba.definition.litedefinition.ReferenceDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.litedefinition.URLDefView;
import wt.iba.definition.litedefinition.UnitDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.ReferenceValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.LoadValue;
import wt.iba.value.service.StandardIBAValueService;
import wt.method.RemoteMethodServer;
import wt.session.SessionHelper;
import wt.units.service.MeasurementSystemCache;
import wt.units.service.QuantityOfMeasureDefaultView;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class IBAUtil {

    Hashtable ibaContainer;

    String measurementSystem = "SI";
    
    private static final Logger logger = Logger.getLogger(IBAUtil.class.getName());

    public IBAUtil(IBAHolder ibaholder) {
        initializeIBAHolder(ibaholder);
    }
    
    public  Hashtable  getIBAValuesByibaholder(){
    	return ibaContainer;
    }

    public static void setValueForStandAndGlobalIBA(WTObject targetObject, String targetAttributeName, Object ibaValue) throws WTException {
        try {
            // AttributeDefDefaultView attributeDefDeaulteView =
            // IBAUtility.getAttributeDefinition(targetAttributeName,
            // false);
            String databaseColumnsLabel = null;
            TypeIdentifier ti = TypeIdentifierUtilityHelper.service.getTypeIdentifier(targetObject);
            if (ti != null) {
                TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(ti);
                if (tv != null) {
                    AttributeDefinitionReadView av = tv.getAttributeByName(targetAttributeName);
                    if (av != null)
                        databaseColumnsLabel = av.getDatabaseColumnsLabel();
                }
            }
            // if targetAttribute is StandardAttribute
            if (!StringUtil.nullOrBlank(databaseColumnsLabel))
                updateStandardAttributeIBAValue((IBAHolder) targetObject, targetAttributeName, ibaValue);
            else
                setIBAAnyValue(targetObject, targetAttributeName, ibaValue);
        } catch (Exception e) {
            throw new WTException(e, "Set Attribute [" + targetAttributeName + "]:" + ibaValue + " failed.");
        }
    }

    /**
     * set attributeValue (globeAttribute/StandardAttribute) <br>
     * <b>Revision History</b><br>
     * <b>Rev:</b>1.0 - 2017-4-2, whuyan<br>
     * <b>Comment:</b><br>
     * 
     * @param targetObject
     * @param targetAttributeName
     *            map ibaName/StandardAttributeName
     * @param ibaValue
     * @throws WTException
     */
    public static void setValueForStandAndGlobalIBA(WTObject targetObject, Map<String, Object> ibaDataMap) throws WTException {
        String targetAttributeName = "";
        Object ibaValue = "";
        try {
            // AttributeDefDefaultView attributeDefDeaulteView =
            // IBAUtility.getAttributeDefinition(targetAttributeName,
            // false);
            for (Map.Entry<String, Object> entry : ibaDataMap.entrySet()) {
                targetAttributeName = entry.getKey();
                ibaValue = entry.getValue();
                String databaseColumnsLabel = null;
                TypeIdentifier ti = TypeIdentifierUtilityHelper.service.getTypeIdentifier(targetObject);
                if (ti != null) {
                    TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(ti);
                    if (tv != null) {
                        AttributeDefinitionReadView av = tv.getAttributeByName(targetAttributeName);
                        if (av != null)
                            databaseColumnsLabel = av.getDatabaseColumnsLabel();
                    }
                }
                // if targetAttribute is StandardAttribute
                if (!StringUtil.nullOrBlank(databaseColumnsLabel))
                    updateStandardAttributeIBAValue((IBAHolder) targetObject, targetAttributeName, ibaValue);
                else
                    setIBAAnyValue(targetObject, targetAttributeName, ibaValue);
            }
        } catch (Exception e) {
            throw new WTException(e, "Set Attribute [" + targetAttributeName + "]:" + ibaValue + " failed.");
        }
    }
    
    /**
     * Set IBA Any Value Without Exception
     * @param obj
     * @param ibaName
     * @param newValue
     */
    public static void setIBAAnyValueWithoutException(WTObject obj, String ibaName, Object newValue){
    	try {
			setIBAAnyValue(obj, ibaName, newValue);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("set iba value failed!ibaName = " + ibaName);
		}
    }
    

    /**
     * Set IBA Any Value
     * 
     * @param obj
     * @param ibaName
     *            : iba name
     * @param newValue
     *            : iba value
     * @exception WTException
     *                , RemoteException, WTPropertyVetoException, ParseException
     */
    public static void setIBAAnyValue(WTObject obj, String ibaName, Object newValue) throws WTException, RemoteException, WTPropertyVetoException, ParseException {
        AttributeDefDefaultView attributedefdefaultview = IBAUtility.getAttributeDefinition(ibaName, false);
        IBAHolder ibaholder = (IBAHolder) obj;

        String ibaClass = "";
        if (attributedefdefaultview instanceof FloatDefView) {
            ibaClass = "wt.iba.definition.FloatDefinition";
        } else if (attributedefdefaultview instanceof StringDefView) {
            ibaClass = "wt.iba.definition.StringDefinition";
        } else if (attributedefdefaultview instanceof IntegerDefView) {
            ibaClass = "wt.iba.definition.IntegerDefinition";
        } else if (attributedefdefaultview instanceof RatioDefView) {
            ibaClass = "wt.iba.definition.RatioDefinition";
        } else if (attributedefdefaultview instanceof TimestampDefView) {
            ibaClass = "wt.iba.definition.TimestampDefinition";
        } else if (attributedefdefaultview instanceof BooleanDefView) {
            ibaClass = "wt.iba.definition.BooleanDefinition";
        } else if (attributedefdefaultview instanceof URLDefView) {
            ibaClass = "wt.iba.definition.URLDefinition";
        } else if (attributedefdefaultview instanceof ReferenceDefView) {
            ibaClass = "wt.iba.definition.ReferenceDefinition";
        } else if (attributedefdefaultview instanceof UnitDefView) {
            ibaClass = "wt.iba.definition.UnitDefinition";
        }

        ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM", null, null);

        ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) (ibaholder).getAttributeContainer();

        Vector vAbstractvalueview = IBAUtility.getIBAValueViews(defaultattributecontainer, ibaName, ibaClass);
        for (int i = 0; i < vAbstractvalueview.size(); i++) {
            AbstractValueView abstractvalueview = (AbstractValueView) vAbstractvalueview.get(i);
            defaultattributecontainer.deleteAttributeValue(abstractvalueview);
            StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaholder, null, null, null);
            ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM", null, null);
        }

        if (newValue != null && !newValue.equals("")) {
            if (attributedefdefaultview instanceof FloatDefView) {
                IBAUtility.setIBAFloatValue(obj, ibaName, newValue.toString());
            } else if (attributedefdefaultview instanceof StringDefView) {
                if (newValue instanceof String) {
                    String strValue = (String) newValue;
                    IBAUtility.setIBAStringValue(obj, ibaName, strValue);
                } else if (newValue instanceof Object[]) {
                    Object[] newMultiObject = (Object[]) newValue;
                    String[] newMultiString = new String[newMultiObject.length];
                    for (int i = 0; i < newMultiString.length; i++) {
                        newMultiString[i] = (String) newMultiObject[i];
                    }
                    IBAUtility.setIBAStringValues(obj, ibaName, newMultiString);
                }
            } else if (attributedefdefaultview instanceof IntegerDefView) {
                IBAUtility.setIBAIntegerValue(obj, ibaName, (Integer) newValue);
            } else if (attributedefdefaultview instanceof RatioDefView) {
                IBAUtility.setIBARatioValue(obj, ibaName, (Double) newValue);
            } else if (attributedefdefaultview instanceof TimestampDefView) {
                IBAUtility.setIBATimestampValue(obj, ibaName, (Timestamp) newValue);
            } else if (attributedefdefaultview instanceof BooleanDefView) {
                IBAUtility.setIBABooleanValue(obj, ibaName, (Boolean) newValue);
            } else if (attributedefdefaultview instanceof URLDefView) {
                IBAUtility.setIBAURLValue(obj, ibaName, (String) newValue);
            } else if (attributedefdefaultview instanceof ReferenceDefView) {
            } else if (attributedefdefaultview instanceof UnitDefView) {
                IBAUtility.setIBAUnitValue(obj, ibaName, (Double) newValue);
            }
        }
    }

    public static void updateStandardAttributeIBAValue(IBAHolder ibaHolder, String attName, Object attValue) throws WTException {
        try {
            if (!RemoteMethodServer.ServerFlag) {
                Class[] classes = { IBAHolder.class, String.class, Object.class };
                Object[] objs = { ibaHolder, attName, attValue };
                RemoteMethodServer.getDefault().invoke("updateStandardAttributeIBAValue", IBAUtil.class.getName(), null, classes, objs);
            } else {
                Persistable persistable = (Persistable) ibaHolder;
                LWCNormalizedObject obj = new LWCNormalizedObject((Persistable) ibaHolder, null, Locale.US, new UpdateOperationIdentifier());

                TypeIdentifier typeIden = TypeIdentifierUtility.getTypeIdentifier(persistable);
                TypeDefinitionReadView tdrv = TypeDefinitionServiceHelper.service.getTypeDefView(typeIden);
                if (tdrv != null) {
                    AttributeDefinitionReadView attView = tdrv.getAttributeByName(attName);
                    if (attView != null) {
                        obj.load(attName);
                        obj.set(attName, attValue);
                        persistable = obj.apply();
                        PersistenceServerHelper.manager.update(persistable);
                    }
                }
            }
        } catch (Exception e) {
            throw new WTException(e, "Update [" + attName + "]:" + attValue + " failed.");
        }
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        Enumeration enumeration = ibaContainer.keys();
        try {
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView) ((Object[]) ibaContainer.get(s))[1];
                stringbuffer.append(s + " - " + IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, SessionHelper.manager.getLocale()));
                stringbuffer.append('\n');
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return stringbuffer.toString();
    }

    public String getIBAValue(String s) {
        try {
            return getIBAValue(s, SessionHelper.manager.getLocale());
        } catch (WTException wte) {
            wte.printStackTrace();
        }
        return null;
    }

    /**
     * @Information:Set an object Attribute value
     * @param theObject
     * @param key
     * @param value
     * @Author: ZhangJie
     * @Date: 2014-6-19
     */
    public static void setIBAValue(Persistable theObject, String key, String value) {
        try {
            Locale locale = SessionHelper.getLocale();
            LWCNormalizedObject lwcObject = new LWCNormalizedObject(theObject, null, locale, new UpdateOperationIdentifier());
            lwcObject.set(key, value);
            lwcObject.apply();
            Persistable newP = PersistenceHelper.manager.modify(theObject);
        } catch (WTException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get an object Attribute value
     * 
     * @param String
     * @param attributeName
     * @return
     * @throws WTException
     */
    public static String getIBAValue(Persistable theObject, String attributeName) throws WTException {
        LWCNormalizedObject genericObj = new LWCNormalizedObject(theObject, null, null, null);
        genericObj.load(attributeName);
        return (String) genericObj.get(attributeName);
    }

    public static Object getIBAObjectValue(Persistable theObject, String attributeName){
        Object o;
        try {
        	LWCNormalizedObject genericObj = new LWCNormalizedObject(theObject, null, null, null);
			genericObj.load(attributeName);
			o = genericObj.get(attributeName);
		} catch (WTException e) {
		    e.printStackTrace();
			String str = "获取属性出错！";
			o = str;
		}
        return o;
    }
    
    public String getIBAValue(String s, Locale locale) {

        try {
            Object[] obj = (Object[]) ibaContainer.get(s);
            if (obj == null)
                return null;
            AbstractValueView abstractvalueview = (AbstractValueView) obj[1];
            return IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, locale);
        } catch (WTException wte) {
            wte.printStackTrace();
        }
        return null;
    }

    private void initializeIBAHolder(IBAHolder ibaholder) {
        ibaContainer = new Hashtable();
        try {
            ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
            DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder.getAttributeContainer();
            if (defaultattributecontainer != null) {
                AttributeDefDefaultView aattributedefdefaultview[] = defaultattributecontainer.getAttributeDefinitions();
                for (int i = 0; i < aattributedefdefaultview.length; i++) {
                    AbstractValueView aabstractvalueview[] = defaultattributecontainer.getAttributeValues(aattributedefdefaultview[i]);
                    if (aabstractvalueview != null) {
                        Object aobj[] = new Object[2];
                        aobj[0] = aattributedefdefaultview[i];
                        aobj[1] = aabstractvalueview[0];
                        ibaContainer.put(aattributedefdefaultview[i].getName(), ((Object) (aobj)));
                    }
                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setIBAValue(String s, String s1) throws WTPropertyVetoException {
        AbstractValueView abstractvalueview = null;
        AttributeDefDefaultView attributedefdefaultview = null;
        Object aobj[] = (Object[]) ibaContainer.get(s);
        if (aobj != null) {
            abstractvalueview = (AbstractValueView) aobj[1];
            attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
        }
        if (abstractvalueview == null)
            attributedefdefaultview = getAttributeDefinition(s);
        if (attributedefdefaultview == null) {
            // logger.debug("definition is null ...");
            return;
        }

        if (attributedefdefaultview instanceof UnitDefView)
            s1 = parseMultiUnits((UnitDefView) attributedefdefaultview, s1);

        abstractvalueview = internalCreateValue(attributedefdefaultview, s1);
        if (abstractvalueview == null) {
            // logger.debug("after creation, iba value is null ..");
            return;
        } else {
            abstractvalueview.setState(1);
            Object aobj1[] = new Object[2];
            aobj1[0] = attributedefdefaultview;
            aobj1[1] = abstractvalueview;
            ibaContainer.put(attributedefdefaultview.getName(), ((Object) (aobj1)));
            return;
        }
    }

    public ClassificationNodeDefaultView getClassificationNodeDefaultView() throws WTException {
        if (ibaContainer == null || ibaContainer.size() == 0)
            return null;

        Object[] objs = (Object[]) ibaContainer.get("PartClassification");
        if (objs == null || objs.length != 2)
            return null;

        AbstractValueView abstractValueView = (AbstractValueView) objs[1];
        if (abstractValueView == null)
            return null;

        try {
            ClassificationNodeDefaultView classificationNodeDefaultView = ClassificationHelper.service.getClassificationNodeDefaultView(((ReferenceValueDefaultView) abstractValueView)
                    .getLiteIBAReferenceable());
            return classificationNodeDefaultView;
        } catch (RemoteException e) {
            throw new WTException(e);
        }
    }

    public IBAHolder updateIBAHolder(IBAHolder ibaholder) throws Exception {
        DefaultAttributeContainer defaultAttributeContainer = (DefaultAttributeContainer) (IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaholder)).getAttributeContainer();
        for (Enumeration enumeration = ibaContainer.elements(); enumeration.hasMoreElements();) {
            try {
                Object aobj[] = (Object[]) enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView) aobj[1];
                AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
                if (abstractvalueview.getState() == 1) {
                    defaultAttributeContainer.deleteAttributeValues(attributedefdefaultview);
                    abstractvalueview.setState(3);
                    defaultAttributeContainer.addAttributeValue(abstractvalueview);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        defaultAttributeContainer.setConstraintParameter(new String("CSM"));
        ibaholder.setAttributeContainer(defaultAttributeContainer);
        return ibaholder;
    }

    public static IBAHolder saveIBAHolder(IBAHolder ibaHolder) throws WTException {
        if (ibaHolder == null)
            return ibaHolder;
        DefaultAttributeContainer defaultAttributeContainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
        IBAValueDBService ibavaluedbservice = new IBAValueDBService();
        defaultAttributeContainer = (DefaultAttributeContainer) ibavaluedbservice.updateAttributeContainer(ibaHolder,
                defaultAttributeContainer != null ? defaultAttributeContainer.getConstraintParameter() : null, null, null);
        ibaHolder.setAttributeContainer(defaultAttributeContainer);
        return ibaHolder;
    }

    private static AttributeDefDefaultView getAttributeDefinition(String s) {
        AttributeDefDefaultView attributedefdefaultview = null;
        try {
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if (attributedefdefaultview == null) {
                AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader.getAttributeDefinition(s);
                if (abstractattributedefinizerview != null)
                    attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView) abstractattributedefinizerview);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return attributedefdefaultview;
    }

    private AbstractValueView internalCreateValue(AbstractAttributeDefinizerView abstractattributedefinizerview, String s) {
        AbstractValueView abstractvalueview = null;
        if (abstractattributedefinizerview instanceof FloatDefView)
            abstractvalueview = LoadValue.newFloatValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof StringDefView)
            abstractvalueview = LoadValue.newStringValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof IntegerDefView)
            abstractvalueview = LoadValue.newIntegerValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof RatioDefView)
            abstractvalueview = LoadValue.newRatioValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof TimestampDefView)
            abstractvalueview = LoadValue.newTimestampValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof BooleanDefView)
            abstractvalueview = LoadValue.newBooleanValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof URLDefView)
            abstractvalueview = LoadValue.newURLValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof ReferenceDefView)
            abstractvalueview = LoadValue.newReferenceValue(abstractattributedefinizerview, "ClassificationNode", s);
        else if (abstractattributedefinizerview instanceof UnitDefView)
            abstractvalueview = LoadValue.newUnitValue(abstractattributedefinizerview, s, null);
        return abstractvalueview;
    }

    private String parseMultiUnits(UnitDefView unitDefView, String value) {
        if (value != null)
            value = value.trim();

        if (value == null || value.equals("") || value.length() < 1)
            return "0";

        try {
            double d = Double.valueOf(value).doubleValue();
        } catch (Exception e) {
            return value;
        }

        value = value + "\t" + getDefaultUnit(unitDefView);

        return value;
    }

    private String getDefaultUnit(UnitDefView unitDefView) {
        if (measurementSystem == null)
            measurementSystem = MeasurementSystemCache.getCurrentMeasurementSystem();

        QuantityOfMeasureDefaultView quantityofmeasuredefaultview = unitDefView.getQuantityOfMeasureDefaultView();
        String defaultUnit = quantityofmeasuredefaultview.getBaseUnit();
        if (measurementSystem != null) {
            String s = unitDefView.getDisplayUnitString(measurementSystem);
            if (s == null)
                s = quantityofmeasuredefaultview.getDisplayUnitString(measurementSystem);
            if (s == null)
                s = quantityofmeasuredefaultview.getDefaultDisplayUnitString(measurementSystem);
            if (s != null)
                defaultUnit = s;
        }
        if (defaultUnit == null)
            defaultUnit = "";

        return defaultUnit;
    }

    public static void setIBAStringValue(IBAHolder ibaHolder, String ibaName, String newValue) throws WTException, RemoteException, WTPropertyVetoException {
        String ibaClass = "wt.iba.definition.StringDefinition";
        DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
        if (defaultattributecontainer == null) {
            defaultattributecontainer = new DefaultAttributeContainer();
            ibaHolder.setAttributeContainer(defaultattributecontainer);
        }

        newValue = newValue == null ? "" : newValue;
        StringValueDefaultView abstractvaluedefaultview = (StringValueDefaultView) getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
        if (abstractvaluedefaultview != null) {
            abstractvaluedefaultview.setValue(newValue);
            defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
        } else {
            AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName);
            StringValueDefaultView abstractvaluedefaultview1 = new StringValueDefaultView((StringDefView) attributedefdefaultview, newValue);
            defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
        }
        ibaHolder.setAttributeContainer(defaultattributecontainer);
        StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
        ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
    }

    public static DefaultAttributeContainer getContainer(IBAHolder ibaHolder) throws WTException, RemoteException {
        ibaHolder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaHolder);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
        return defaultattributecontainer;
    }

    public static AbstractValueView getIBAValueView(DefaultAttributeContainer dac, String ibaName, String ibaClass) throws WTException {
        AbstractValueView aabstractvalueview[] = null;
        AbstractValueView avv = null;
        aabstractvalueview = dac.getAttributeValues();
        for (int j = 0; j < aabstractvalueview.length; j++) {
            String thisIBAName = aabstractvalueview[j].getDefinition().getName();
            String thisIBAClass = (aabstractvalueview[j].getDefinition()).getAttributeDefinitionClassName();
            if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass)) {
                avv = aabstractvalueview[j];
                break;
            }
        }
        return avv;
    }

    public static Persistable setIBAValues(Persistable theObject, Hashtable attributes) throws WTException {
    	if ((attributes == null) || (attributes.isEmpty()) || attributes.size() <= 0)
            return theObject;
        Locale locale = SessionHelper.getLocale();
        LWCNormalizedObject lwcObject = new LWCNormalizedObject(theObject, null, locale, new UpdateOperationIdentifier());

        Enumeration names = attributes.keys();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = (String) attributes.get(name);
            lwcObject.set(name, value);
        }
        lwcObject.apply();
        theObject = PersistenceHelper.manager.modify(theObject);
        return theObject;
    }
    
    /**
     *  获取IBA属性类型是带单位的实数时，获取覆盖单位的值
     * @param s
     * @return
     */
    public String getIBAUnitValue(String s) {
        Object[] obj = (Object[]) ibaContainer.get(s);
        if (obj == null) {
        	return null;
        }
        AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView) obj[0];
        
        if(attributedefdefaultview instanceof UnitDefView) {        	
        	return getDefaultUnit((UnitDefView)attributedefdefaultview);
        }
        return "";

    }
}
