/**
 * 
 */
package com.lyj.download.framwork;



/**
 *自定义的一个记载下载器详细信息的类 
 */
public class LoadInfo {
	public int fileSize;// 文件大小
	private int complete;// 完成度
	private String urlstring;// 下载器标识
    private String fileName;  //游戏名
    private String iconUrl;   //游戏icon
    private int state;        //下载状态 1下载中，2暂停，3下载完成
    public LoadInfo(int fileSize, int complete, String urlstring,String fileName,String iconUrl,int state) {
        this.fileSize = fileSize;
        this.complete = complete;
        this.urlstring = urlstring;
        this.fileName=fileName;
        this.iconUrl=iconUrl;
        this.state=state;
    }
    public LoadInfo() {
    }
    public int getFileSize() {
        return fileSize;
    }
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
    public int getComplete() {
        return complete;
    }
    public void setComplete(int complete) {
        this.complete = complete;
    }
    public String getUrlstring() {
        return urlstring;
    }
    public void setUrlstring(String urlstring) {
        this.urlstring = urlstring;
    }
    @Override
    public String toString() {
        return "LoadInfo [fileSize=" + fileSize + ", complete=" + complete
                + ", urlstring=" + urlstring + "]";
    }
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the iconUrl
	 */
	public String getIconUrl() {
		return iconUrl;
	}
	/**
	 * @param iconUrl the iconUrl to set
	 */
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
    
    
}
