package ext.com.wanli.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCColumnAllocation;
import com.ptc.core.lwc.server.LWCFlexAttDefinition;
import com.ptc.core.lwc.server.LWCOrganizer;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class WCUtil implements RemoteAccess, Serializable {
	/**
	 * @author zhangmingjie
	 */
	private static final long serialVersionUID = 1L;
	private static String CLASSNAME = WCUtil.class.getName();
	private static Logger logger = LogR.getLogger(CLASSNAME);

	public static Persistable getPersistableByOid(String strOid) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (Persistable) RemoteMethodServer.getDefault().invoke("getPersistableByOid",
						WCUtil.class.getName(), null, new Class[] { String.class }, new Object[] { strOid });
			} else {
				Persistable per = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (strOid != null && strOid.trim().length() > 0) {
						ReferenceFactory referencefactory = new ReferenceFactory();
						WTReference wtreference = referencefactory.getReference(strOid);
						per = wtreference != null ? wtreference.getObject() : null;
					}
				} catch (WTException e) {
					logger.error(CLASSNAME + ".getPersistableByOid:" + e);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return per;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * get WTContainer by Part
	 * 
	 * @param wtPart
	 * @return
	 * @throws WTException
	 */
	public static WTContainer getWTContainerByPart(WTPart wtPart) throws WTException {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (WTContainer) RemoteMethodServer.getDefault().invoke("getWTContainerByPart",
						WCUtil.class.getName(), null, new Class[] { WTPart.class }, new Object[] { wtPart });
			} else {
				WTContainer wtContainer = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (wtPart != null && PartUtil.isPartExist(wtPart.getNumber())) {
						wtContainer = wtPart.getContainer();
					}
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return wtContainer;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * get WTContainer by document
	 * 
	 * @param wtDoc
	 * @return
	 * @throws WTException
	 */
	public static WTContainer getWTContainerByDoc(WTDocument wtDoc) throws WTException {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (WTContainer) RemoteMethodServer.getDefault().invoke("getWTContainerByDoc",
						WCUtil.class.getName(), null, new Class[] { WTDocument.class }, new Object[] { wtDoc });
			} else {
				WTContainer wtContainer = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (wtDoc != null && PartUtil.isPartExist(wtDoc.getNumber())) {
						wtContainer = wtDoc.getContainer();
					}
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return wtContainer;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * get wtcontainerRef by WTContainer
	 * 
	 * @param wtcontainer
	 * @return
	 * @throws WTException
	 */
	public static WTContainerRef getWTContainerref(WTContainer wtcontainer) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (WTContainerRef) RemoteMethodServer.getDefault().invoke("getWTContainerref",
						WCUtil.class.getName(), null, new Class[] { WTContainer.class }, new Object[] { wtcontainer });
			} else {
				WTContainerRef wtContainerRef = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (wtcontainer != null) {
						wtContainerRef = WTContainerRef.newWTContainerRef(wtcontainer);
					}
				} catch (WTException e) {
					logger.error(CLASSNAME + ".getWTContainerref:" + e);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return wtContainerRef;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * get WTContainerRef by part
	 * 
	 * @param wtPart
	 * @return
	 * @throws WTException
	 */
	public static WTContainerRef getWTContainerrefByPart(WTPart wtPart) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (WTContainerRef) RemoteMethodServer.getDefault().invoke("getWTContainerrefByPart",
						WCUtil.class.getName(), null, new Class[] { WTPart.class }, new Object[] { wtPart });
			} else {
				WTContainerRef wtContainerRef = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (wtPart != null && PartUtil.isPartExist(wtPart.getNumber())) {
						WTContainer wtcontainer = getWTContainerByPart(wtPart);
						wtContainerRef = WTContainerRef.newWTContainerRef(wtcontainer);
					}
				} catch (WTException e) {
					logger.error(CLASSNAME + ".getWTContainerrefByPart:" + e);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return wtContainerRef;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * get WTContainerRef by document
	 * 
	 * @param wtDoc
	 * @return
	 * @throws WTException
	 */
	public static WTContainerRef getWTContainerrefByDoc(WTDocument wtDoc) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (WTContainerRef) RemoteMethodServer.getDefault().invoke("getWTContainerrefByDoc",
						WCUtil.class.getName(), null, new Class[] { WTDocument.class }, new Object[] { wtDoc });
			} else {
				WTContainerRef wtContainerRef = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (wtDoc != null && PartUtil.isPartExist(wtDoc.getNumber())) {
						WTContainer wtcontainer = getWTContainerByDoc(wtDoc);
						wtContainerRef = WTContainerRef.newWTContainerRef(wtcontainer);
					}
				} catch (WTException e) {
					logger.error(CLASSNAME + ".getWTContainerrefByDoc:" + e);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return wtContainerRef;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Get Initial start time of schedule queue task
	 *
	 * @param strHourOfDay
	 * @param strMinute
	 * @param strSecond
	 * @return
	 */
	public static Timestamp getInitialStartTime(String strHourOfDay, String strMinute, String strSecond) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (Timestamp) RemoteMethodServer.getDefault().invoke("getInitialStartTime", WCUtil.class.getName(),
						null, new Class[] { String.class, String.class, String.class },
						new Object[] { strHourOfDay, strMinute, strSecond });
			} else {
				Timestamp timestamp = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					int nHourOfDay = 0;
					int nMinute = 0;
					int nSecond = 0;
					if (strHourOfDay != null && strHourOfDay.length() > 0) {
						nHourOfDay = Integer.valueOf(strHourOfDay).intValue();
					}
					if (strMinute != null && strMinute.length() > 0) {
						nMinute = Integer.valueOf(strMinute).intValue();
					}
					if (strSecond != null && strSecond.length() > 0) {
						nSecond = Integer.valueOf(strSecond).intValue();
					}
					timestamp = getInitialStartTime(nHourOfDay, nMinute, nSecond);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return timestamp;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Get Initial start time of schedule queue task
	 *
	 * @param nHourOfDay
	 * @param nMinute
	 * @param nSecond
	 * @return
	 */
	public static Timestamp getInitialStartTime(int nHourOfDay, int nMinute, int nSecond) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (Timestamp) RemoteMethodServer.getDefault().invoke("getInitialStartTime", WCUtil.class.getName(),
						null, new Class[] { int.class, int.class, int.class },
						new Object[] { nHourOfDay, nMinute, nSecond });
			} else {
				Timestamp time = null;
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					Calendar calendar = Calendar.getInstance();
					Calendar calendar1 = Calendar.getInstance();
					if (Calendar.getInstance().getTimeZone().getID().equals("GMT")) {
						calendar1.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY + 8);
					}
					calendar.set(Calendar.HOUR_OF_DAY, nHourOfDay);
					calendar.set(Calendar.MINUTE, nMinute);
					calendar.set(Calendar.SECOND, nSecond);
					if (calendar.after(calendar1)) {
						time = new Timestamp(calendar.getTimeInMillis());
					} else {
						time = new Timestamp(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
					}
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return time;
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * get organization by name
	 * 
	 * @param name
	 * @return if name is exist in windChill,return organization object else if
	 *         name is not exist,return null else if name is empty or name is
	 *         null,return null
	 */
	@SuppressWarnings("deprecation")
	public static LWCOrganizer getLWCOrganizer(String name) {
		LWCOrganizer org = null;
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			if (!StringUtils.isEmpty(name)) {
				QuerySpec criteria = new QuerySpec(LWCOrganizer.class);
				criteria.appendWhere(
						new SearchCondition(LWCOrganizer.class, LWCOrganizer.NAME, SearchCondition.LIKE, name, false));
				QueryResult results = PersistenceHelper.manager.find(criteria);
				if (results.hasMoreElements()) {
					org = (LWCOrganizer) results.nextElement();
				}
			}
		} catch (WTException e) {
			logger.error(CLASSNAME + ".getLWCOrganizer:" + e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return org;
	}

	/**
	 * Get container by name
	 * 
	 * @param name
	 *            container name
	 * @return container
	 * @throws WTException
	 *             Windchill exception
	 */
	@SuppressWarnings("deprecation")
	public static WTContainer getWtContainerByName(String name) throws WTException {
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		WTContainer obj = null;
		try {
			if (StringUtils.isNotEmpty(name)) {
				QuerySpec qs = new QuerySpec(WTContainer.class);
				SearchCondition sc = new SearchCondition(WTContainer.class, WTContainer.NAME, "=", name);
				qs.appendWhere(sc);
				QueryResult qr = PersistenceHelper.manager.find(qs);
				while (qr.hasMoreElements()) {
					obj = (WTContainer) qr.nextElement();
				}
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "." + "getWtContainerByName" + ":" + e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return obj;

	}

	/**
	 * 获取对象的属性值
	 * 
	 * @param p
	 * @param key
	 * @return
	 * @throws WTException
	 */
	public static Object getMBAValue(Persistable p, String key) throws WTException {
		Locale loc = null;
		try {
			loc = SessionHelper.getLocale();
		} catch (WTException e) {
			logger.debug("Get IBA Value Fail" + e.getMessage());
			throw new WTException(e);
		}
		return getMBAValue(p, loc, key);
	}

	/**
	 * @param p
	 * @param key
	 * @return
	 * @throws WTException
	 */
	public static Object getMBAValue(Persistable targetObj, Locale locale, String ibaName) throws WTException {
		Object ibaValue = null;

		try {
			PersistableAdapter obj = new PersistableAdapter(targetObj, null, locale, null);
			obj.load(ibaName);
			ibaValue = obj.get(ibaName);
		} catch (WTException e) {
			e.printStackTrace();
			throw new WTException(e);
		}
		return ibaValue;
	}

	/**
	 * @Author: bjj
	 * @Date: 2016/9/14 pm
	 * @Description: Search OrgContainer by its name.
	 * @param orgName
	 * @return
	 * @throws WTException
	 */
	public static OrgContainer searchOrgContainer(String orgName) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (OrgContainer) RemoteMethodServer.getDefault().invoke("searchOrgContainer",
						UserUtil.class.getName(), null, new Class[] { String.class }, new Object[] { orgName });
			} else {
				boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
				OrgContainer org = null;
				try {
					QuerySpec qs = new QuerySpec(OrgContainer.class);
					qs.appendWhere(new SearchCondition(OrgContainer.class, "containerInfo.name", "=", orgName),
							new int[1]);
					@SuppressWarnings("deprecation")
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						org = (OrgContainer) qr.nextElement();
					}
				} catch (WTException e) {
					logger.error(CLASSNAME + ".searchOrgContainer:" + e);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return org;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @author bjj Deal with the special characters of fuzzy query value.(SQL
	 *         Skills)
	 * 
	 * @param sqlStr
	 * @return
	 */
	public static String sqlLikeValueEncode(String value) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (String) RemoteMethodServer.getDefault().invoke("sqlLikeValueEncode", WCUtil.class.getName(),
						null, new Class[] { String.class }, new Object[] { value });
			} else {
				boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (value == null || "".equals(value)) {
						return value;
					}
					if (value.startsWith("*") || value.startsWith("%")) {
						value = "%" + value.substring(1);
					}
					if (value.endsWith("*") || value.endsWith("%")) {
						value = value.substring(0, value.length() - 1) + "%";
					}
					if (value.endsWith("*") || value.endsWith("%")) {
						value = value.substring(0, value.length() - 1) + "%";
					}
					if (value.contains("[")) {
						value = value.replace("[", "\\[");
						value = "'" + value + "'" + " escape '\\'";
					} else if (value.contains("_")) {
						value = value.replace("_", "\\_");
						value = "'" + value + "'" + " escape '\\'";
					} else {
						value = "'" + value + "'";
					}
				} catch (Exception e) {
					logger.error(CLASSNAME + "." + "sqlLikeValueEncode" + ":" + e);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return value;
			}
		} catch (RemoteException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @author bjj Deal with the special characters of fuzzy query value. (Using
	 *         the advanced query in Windchill)
	 * 
	 * @param sqlStr
	 * @return
	 */
	public static String queryLikeValueFormat(String value) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (String) RemoteMethodServer.getDefault().invoke("queryLikeValueFormat", WCUtil.class.getName(),
						null, new Class[] { String.class }, new Object[] { value });
			} else {
				boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (value == null || "".equals(value)) {
						return value;
					}
					if (value.startsWith("*") || value.startsWith("%")) {
						value = "%" + value.substring(1);
					}

					if (value.endsWith("*") || value.endsWith("%")) {
						value = value.substring(0, value.length() - 1) + "%";
					}
				} catch (Exception e) {
					logger.error(CLASSNAME + "." + "queryLikeValueFormat" + ":" + e);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
				return value;
			}
		} catch (RemoteException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 获取标准属性对应的数据库字段
	 * 
	 * @param columnName
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static String getColumnIdentifer(String columnName, String type) throws WTException {
		String physicalName = "";
		QuerySpec qs = new QuerySpec();
		int index1 = qs.appendClassList(LWCColumnAllocation.class, true);
		int index2 = qs.appendClassList(LWCFlexAttDefinition.class, false);
		int index3 = qs.appendClassList(LWCTypeDefinition.class, false);
		qs.appendWhere(
				new SearchCondition(LWCColumnAllocation.class, "attributeDefReference.key.id",
						LWCFlexAttDefinition.class, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { index1, index2 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(LWCFlexAttDefinition.class, LWCFlexAttDefinition.NAME, SearchCondition.EQUAL,
				columnName), new int[] { index2 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(LWCColumnAllocation.class, "typeDefReference.key.id",
				LWCTypeDefinition.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { index1, index3 });
		qs.appendAnd();
		qs.appendWhere(
				new SearchCondition(LWCTypeDefinition.class, LWCTypeDefinition.NAME, SearchCondition.EQUAL, type),
				new int[] { index3 });
		qs.appendOrderBy(
				new OrderBy(new ClassAttribute(LWCColumnAllocation.class, "thePersistInfo.theObjectIdentifier.id"),
						true),
				new int[] { index1 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			Persistable[] persistable = (Persistable[]) qr.nextElement();
			LWCColumnAllocation lwcColumnAllocation = (LWCColumnAllocation) persistable[0];
			physicalName = lwcColumnAllocation.getPhysicalName();
		}
		return physicalName;
	}

	/**
	 * 获取标准属性对应的数据库映射字段
	 * 
	 * @param columnName
	 * @param type
	 *            子类型 com.xxxx.xxpart
	 * @return
	 * @throws WTException
	 */
	public static String getMBAColumnName(String type, String attName) throws WTException {
		TypeIdentifier typeIden = TypeIdentifierUtility
				.getTypeIdentifierFromPersistedType(type.substring(type.lastIndexOf("|") + 1, type.length()));
		TypeDefinitionReadView typeView = TypeDefinitionServiceHelper.service.getTypeDefView(typeIden);
		AttributeDefinitionReadView modelView = typeView.getAttributeByName(attName);
		String columnName = "";
		if (modelView != null) {
			columnName = modelView.getColumnAllocations().get("value");
		}
		return columnName;
	}

	/**
	 * 根据数据列的物理列名称获取数据库列的争取名称
	 * 
	 * @param columnName
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static String transferPhysicalNameToColumnName(String columnName, String type) throws WTException {
		String column = "";
		String physicalName = getColumnIdentifer(columnName, type);
		if (!StringUtils.isEmpty(physicalName)) {
			if (physicalName.contains(".")) {
				String pcolumn[] = physicalName.replace(".", ":::").split(":::");
				column = pcolumn[1] + pcolumn[0];
			}
		}
		return column;
	}

	public static String getOidByPersistable(Persistable persistable) throws WTException {
		if (persistable != null) {
			ReferenceFactory referencefactory = new ReferenceFactory();
			return referencefactory.getReferenceString(persistable);
		}
		return null;
	}
}
