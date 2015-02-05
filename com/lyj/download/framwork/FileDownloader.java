/**
 * 
 */
package com.lyj.download.framwork;

/**
 * �ļ����� Downloader.java
 * �����ˣ� ���ǽ�
 * ����ʱ�䣺2014-11-4
 * ��Ȩ������ά������Ƽ����޹�˾��91�棩
 */

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class FileDownloader {
	private String urlstr;// 下载的地址
	private String localfile;// 保存路径
//	private int threadcount=1;// 线程数
	private String fileName;   
	private String iconurl;
	private DownloadHelper dao;// 工具类
	private int fileSize;// 所要下载的文件的大小
	private LoadInfo loadInfo;
	private static final int INIT = 1;//定义三种下载的状态：初始化状态，正在下载状态，暂停状态̬
	private static final int DOWNLOADING = 2;
	private static final int PAUSE = 3;
	private int state = INIT;

	public FileDownloader(Context context,String urlstr, String localfile,String gamename,String iconurl) {
		this.urlstr = urlstr;
		this.localfile = localfile;
		this.fileName=gamename;
		this.iconurl=iconurl;
//		this.threadcount = threadcount;
		dao = DownloadHelper.getInstance(context);
	}

	//
	/**
	 * 判断是否正在下载 
	 */
	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	/**
	 *得到downloader里的信息
       * 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
       * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束为止，文件大小等），并将下载信息返回给下载器
	 */
	public void getDownloaderInfors() {
		if (isFirst(urlstr)) {
			Log.v("TAG", "isFirst");
			init();
			 //创建一个LoadInfo对象记载下载器的具体信息
		    loadInfo = new LoadInfo(fileSize, 0, urlstr,fileName,iconurl,0);
			dao.saveInfos(loadInfo);
		} else {
			//得到数据库中已有的urlstr的下载器的具体信息
			Log.v("TAG", "not isFirst size=" + fileSize);
		    loadInfo=dao.getInfos(urlstr);
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		try {
			URL url = new URL(urlstr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();
//			Log.i("FileDownloader", fileSize+"");

			File file = new File(localfile);
			if (!file.exists()) {
				file.createNewFile();
			}
			// 本地访问文件
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			accessFile.setLength(fileSize);
			accessFile.close();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  判断是否是第一次 下载
	 */
	private boolean isFirst(String urlstr) {
		return dao.isHasInfors(urlstr);
	}

	/**
	 *利用线程开始下载数据
	 */
	public void download() {
		new AsyncTask<Integer, Integer, String>(){

			@Override
			protected String doInBackground(Integer... params) {
				getDownloaderInfors();
				return "";
			}
		
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (loadInfo != null) {
					if (state == DOWNLOADING)
						return;
					state = DOWNLOADING;
					new MyThread(loadInfo.getFileSize(), loadInfo.getComplete(), loadInfo.getUrlstring()).start();
				}
			}
			
		}.execute();
		
	}

	public class MyThread extends Thread {
//		private int threadId;
		private int startPos=0;
		private int endPos;
		private int compeleteSize;
		private String urlstr;

		public MyThread(int endPos, int compeleteSize, String urlstr) {
			this.endPos = endPos;
			this.compeleteSize = compeleteSize;
			this.urlstr = urlstr;
		}

		@Override
		public void run() {
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				// 设置范围，格式为Range：bytes x-y;
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + compeleteSize) + "-" + endPos);

				randomAccessFile = new RandomAccessFile(localfile, "rwd");
				randomAccessFile.seek(startPos + compeleteSize);
				// 将要下载的文件写到保存在保存路径下的文件中
				is = connection.getInputStream();
				byte[] buffer = new byte[4096];
				int length = -1;
				dao.updataInfoState(0, urlstr);
				while ((length = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					// 更新数据库中的下载信息
					dao.updataInfos(compeleteSize, urlstr);
					Log.i(fileName,compeleteSize+"..");
					if (state == PAUSE) {
						return;
					}
				}
//				delete(urlstr);
//				reset();
				dao.updataInfoState(2, urlstr);
				Log.i("fileDownload", "download done");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
					randomAccessFile.close();
					connection.disconnect();
					// dao.closeDb();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	//删除数据库中urlstr对应的下载器信息
	public void delete(String urlstr) {
		dao.delete(urlstr);
	}

	//设置暂停
	public void pause() {
		state = PAUSE;
		dao.updataInfoState(1, urlstr);
	}

	//重置下载状态״̬
	public void reset() {
		state = INIT;
	}
}
