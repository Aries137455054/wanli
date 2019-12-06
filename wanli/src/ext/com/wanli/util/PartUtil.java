package ext.com.wanli.util;

import org.apache.commons.lang.StringUtils;

import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

public class PartUtil {

	/**
	 * 获取PartMaster
	 * @param partNo
	 * @throws WTException
	 * @author zhangmingjie
	 * @date 2019年12月5日 下午3:14:55
	 */
	public static WTPartMaster getPartMasterByNumber(String number) throws WTException {
		QuerySpec querySpec = new QuerySpec(WTPartMaster.class);
		SearchCondition searchCondition = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER,
				SearchCondition.EQUAL, number, false);
		querySpec.appendSearchCondition(searchCondition);
		QueryResult queryResult = PersistenceHelper.manager.find(querySpec);
		while (queryResult.hasMoreElements()) {
			WTPartMaster partMaster = (WTPartMaster) queryResult.nextElement();
			return partMaster;
		}
		return null;
	}
	
	/**
	 * 获取最新版本的part
	 * @param number
	 * @param accessControlled
	 * @return
	 * @throws WTException
	 * WTPart
	 * @author zhangmingjie
	 * @date 2019年12月5日 下午3:21:08
	 */
	public static WTPart getLatestPartByNumber(String number) throws WTException {
        WTPart part = null;
        WTPartMaster partMaster = (WTPartMaster) getPartMasterByNumber(number);
        if (partMaster != null) {
            return (WTPart) getLatestObject(partMaster);
        }
        return part;
    }
	
	/**
     * 根据Master取得最新版本版序对象
     *
     * @param master
     *            Master
     * @return RevisionControlled
     * @throws WTException
     */
    public static RevisionControlled getLatestObject(Master master) throws WTException {
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
        return getLatestObject(queryResult);
    }
    
    /**
     * 从一组集合中返回最新版本版序的对象
     * @param queryresult QueryResult
     * @return RevisionControlled
     * @throws WTException
     */
    public static RevisionControlled getLatestObject(QueryResult queryresult) throws WTException {
        RevisionControlled rc = null;
        while (queryresult.hasMoreElements()) {
            RevisionControlled obj = ((RevisionControlled) queryresult.nextElement());
            if (rc == null || obj.getVersionIdentifier().getSeries().greaterThan(rc.getVersionIdentifier().getSeries()))
                rc = obj;
        }
        if (rc != null)
            return (RevisionControlled) VersionControlHelper.getLatestIteration(rc);
        else
            return rc;
    }
    
    public static Boolean isPartExist(String strNumber) throws WTException {
		boolean flag = false;
     	boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
    	try {
    		if (!StringUtils.isEmpty(strNumber)) {
	    		WTPartMaster wtpartmaster = getPartMasterByNumber(strNumber);
				if (wtpartmaster != null) {
					flag = true;
		        }
	        }
    	} finally {
             SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    	return flag;
    }
}
