/**
 * 
 */
package com.lyj.download.framwork;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//import com.weedong.corps.model.DownloadPartInfo;

public class DownloadHelper extends SQLiteOpenHelper {
	// private DBHelper dbHelper;
	//
	// public DownloadHelper(Context context) {
	// dbHelper = new DBHelper(context);
	// }
	private SQLiteDatabase db;
	// private SimpleDateFormat mDateFormat;
	private static DownloadHelper sInstance;

	private DownloadHelper(Context context) {
		super(context, "demo.db", null, 3);
		// mDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.CHINA);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(USERNAME_TABLE_CREATE);
		creatDownloadTable(db);
//		db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
		
	}
	
	private void creatDownloadTable(SQLiteDatabase db){
		  db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT,"
	                + "filesize integer, compelete_size integer,url char,filename char,iconurl char,state integer)");
	}

	public static synchronized DownloadHelper getInstance(Context context) {
		if (null == sInstance) {
			sInstance = new DownloadHelper(context);
		}
		return sInstance;
	}

	/**
	 * 查看数据库中是否有数据
	 */
	public boolean isHasInfors(String urlstr) {
		// SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select count(*)  from download_info where url=?";
		Cursor cursor = db.rawQuery(sql, new String[] { urlstr });
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count == 0;
	}
	public boolean isHasInforByname(String urlstr) {
		// SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select count(*)  from download_info where filename=?";
		Cursor cursor = db.rawQuery(sql, new String[] { urlstr });
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count == 1;
	}

	/**
	 * 保存 下载的具体信息
	 */
	public void saveInfos(LoadInfo info) {
		// SQLiteDatabase database = dbHelper.getWritableDatabase();
		// for (DownloadPartInfo info : infos) {
		String sql = "insert into download_info(filesize,compelete_size,url,filename,iconurl) values (?,?,?,?,?)";
		Object[] bindArgs = { info.getFileSize(), info.getComplete(),
				info.getUrlstring(), info.getFileName(), info.getIconUrl()};
		db.execSQL(sql, bindArgs);
		// }
	}

	/**
	 * 得到下载具体信息
	 */
	public LoadInfo getInfos(String urlstr) {
//		List<DownloadPartInfo> list = new ArrayList<DownloadPartInfo>();
		// SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select filesize,compelete_size,url,filename,iconurl,state from download_info where url=?";
		Cursor cursor = db.rawQuery(sql, new String[] { urlstr });
		cursor.moveToFirst();
//		while (cursor.moveToNext()) {
//			DownloadPartInfo info = new DownloadPartInfo(cursor.getInt(0),
//					cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
//					cursor.getString(4), cursor.getString(5),
//					cursor.getString(6));
//			list.add(info);
//		}
		LoadInfo loadInfo=new LoadInfo(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getInt(5));
		cursor.close();
		return loadInfo;
	}
	/**
	 * 得到下载具体信息
	 */
	public LoadInfo getInfoByName(String urlstr) {
//		List<DownloadPartInfo> list = new ArrayList<DownloadPartInfo>();
		// SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select filesize,compelete_size,url,filename,iconurl,state from download_info where filename=?";
		Cursor cursor = db.rawQuery(sql, new String[] { urlstr });
		cursor.moveToFirst();
//		while (cursor.moveToNext()) {
//			DownloadPartInfo info = new DownloadPartInfo(cursor.getInt(0),
//					cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
//					cursor.getString(4), cursor.getString(5),
//					cursor.getString(6));
//			list.add(info);
//		}
		LoadInfo loadInfo=new LoadInfo(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getInt(5));
		cursor.close();
		return loadInfo;
	}

	/**
	 * 得到所有下载条目
	 * 
	 * @return
	 */

	public List<LoadInfo> getAllDownLoadTask() {
		List<LoadInfo> list = new ArrayList<LoadInfo>();
		String sql = "select filesize,compelete_size, url,filename,iconurl,state from download_info GROUP BY url";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
//			String url = cursor.getString(0);
//
//			List<DownloadPartInfo> partinfos = getInfos(url);
//			int size = 0;
//			int compeleteSize = 0;
//			for (DownloadPartInfo partinfo : partinfos) {
//				compeleteSize += partinfo.getCompeleteSize();
//				size += partinfo.getEndPos() - partinfo.getStartPos() + 1;
//			}
			LoadInfo loadInfo=new LoadInfo(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getInt(5));
			list.add(loadInfo);
		}
		cursor.close();
		return list;
	}

	/**
	 * 更新数据库中的下载信息
	 */
	public void updataInfos(int compeleteSize, String urlstr) {
		// SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "update download_info set compelete_size=? where url=?";
		Object[] bindArgs = { compeleteSize, urlstr };
		db.execSQL(sql, bindArgs);
	}
	/**
	 * 更新数据库中的下载状态
	 */
	public void updataInfoState(int state, String urlstr) {
		// SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "update download_info set state=? where url=?";
		Object[] bindArgs = { state, urlstr };
		db.execSQL(sql, bindArgs);
	}

	// /**
	// * 关闭数据库
	// */
	// public void closeDb() {
	// dbHelper.close();
	// }

	/**
	 * 下载完成后删除数据库中的数据
	 */
	public void delete(String url) {
		// SQLiteDatabase database = dbHelper.getReadableDatabase();
		db.delete("download_info", "url=?", new String[] { url });
		// database.close();
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion==2){
			creatDownloadTable(db);
		}
	}
}