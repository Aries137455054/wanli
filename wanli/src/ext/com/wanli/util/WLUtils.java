package ext.com.wanli.util;

import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;

public class WLUtils {

	/**
	 * 获取目录，若获取不到则自动创建
	 * 
	 * @param wtcontainer
	 * @return
	 * @throws WTException
	 */
	public static Folder getFolder(WTContainer wtcontainer, String folderPath) throws WTException {
		WTContainerRef wtcontainerRef = WTContainerRef.newWTContainerRef(wtcontainer);
		// 该方法先获取传的路径，获取不到则创建
		return FolderHelper.service.saveFolderPath(folderPath, wtcontainerRef);
	}

}
