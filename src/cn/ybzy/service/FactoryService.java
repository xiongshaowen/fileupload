package cn.ybzy.service;

public class FactoryService {
    public static UploadFileService getUploadFileService() {
    	return new UploadFileServiceImpl();
    }
}
