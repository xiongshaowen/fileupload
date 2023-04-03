package cn.ybzy.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ybzy.model.UploadFile;

public interface UploadFileService {
	    //新增
		public void addFileInfo(UploadFile uploadFile);
		//获取到所有上传到服务器上的文件的信息列表
		public List<UploadFile> getUploadFiles();
		//删除信息，根据id
	    public void deletUploadFile(int id);
	    //保存文件到服务器上的方法,肯定涉及到HTTP,所以参数有req,resp,现在理解到了服务层与Dao层的关系了，服务层不管数据操作，减少耦合
	    public void saveFile(HttpServletRequest req,HttpServletResponse resp);
	    //删除文件
	    public void deleteFile(String savePath);
	    //获取一条上传文件的信息用于，显示在下载或删除中。
		public UploadFile getUploadFileById(int id);
}
