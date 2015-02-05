/**
 * 
 */
package com.lyj.download.framwork;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



import android.content.Context;


/**
 * 文件名： DownloadManager.java 创建人： 汤亚杰 创建时间：2014-11-4 版权：广州维动网络科技有限公司（91玩）
 */
public class MyDownloadManager {
	// 存放各个下载器
	private static Map<String, FileDownloader> downloaders = new HashMap<String, FileDownloader>();
//	private static final int threadcount = 1;

	public static void startDownload(Context context,final String urlstr,String gamename,String iconurl) {
		String localfile =context.getExternalFilesDir("") + "/"
				+ urlstr.substring(urlstr.lastIndexOf("/"));
		File file = new File("");
		if (!file.exists()) {
			file.mkdirs();
		}
		FileDownloader downloader = downloaders.get(urlstr);
		if (downloader == null) {
			downloader = new FileDownloader(context,urlstr, localfile,gamename,iconurl);
			downloaders.put(urlstr, downloader);
		}
		if (downloader.isdownloading())
		{
			return;
		}
			
//		 得到下载信息类的个数组成集合
//		LoadInfo loadInfo = downloader.getDownloaderInfors();
//		 显示进度条
//		showProgress(loadInfo, urlstr, v);
//		 调用方法开始下载
		downloader.download();
	}

	/**
	 * 响应暂停下载按钮的点击事件
	 */
	public static void pauseDownload(String urlstr) {
		if (downloaders.containsKey(urlstr)) {
			 downloaders.get(urlstr).pause();
		}
	}

}
