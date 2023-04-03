package cn.ybzy.dao;

public class FactoryDao {
	public static UploadFileDao  getUploadFileDao() {
		return new UploadFileDaoImpl();
	}

}
