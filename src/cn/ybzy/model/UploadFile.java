package cn.ybzy.model;

import java.util.Date;

public class UploadFile {
	/**
	 * 上传文件的ID         注意，真正做工程的时候要写上注释
	 */
	private int id;
	private String oldFileName;
	private String fileType;
	private String fileSize;
	private String savePath;
	private Date saveTime;
	private String desc;
	private String saveName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOldFileName() {
		return oldFileName;
	}
	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public Date getSaveTime() {
		return saveTime;
	}
	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getSaveName() {
		return saveName;
	}
	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}
	@Override
	public String toString() {
		return "UploadFile [id=" + id + ", oldFileName=" + oldFileName + ", fileType=" + fileType + ", fileSize="
				+ fileSize + ", savePath=" + savePath + ", saveTime=" + saveTime + ", desc=" + desc + ", saveName="
				+ saveName + "]";
	}

}
