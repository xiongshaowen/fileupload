package cn.ybzy.dao;



import java.util.List;

import cn.ybzy.model.UploadFile;

public interface UploadFileDao {
	//新增
	public void addFileInfo(UploadFile uploadFile);
	//获取到所有上传到服务器上的文件的信息列表
	public List<UploadFile> getUploadFiles();
	//删除信息，根据id
    public void deletUploadFile(int id);
    
	UploadFile get(int id);
}
