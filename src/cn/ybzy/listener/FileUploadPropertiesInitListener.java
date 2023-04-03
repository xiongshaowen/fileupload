package cn.ybzy.listener;

import java.io.IOException;
import java.io.InputStream;



import cn.ybzy.utils.FileUploadPropertiesUtils;

import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener

public class FileUploadPropertiesInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//web服务器启动的时候就监听,执行该方法                                        Object.getClass()
		InputStream in =getClass().getClassLoader().getResourceAsStream("uploadfile.properties");
		Properties properties =new Properties();
		try {
			properties.load(in);
			for(Object o:properties.keySet()) {
				String key =(String)o;
				String value=properties.getProperty(key);
				//System.out.println("KEY:"+key+",value"+value);  //测试成功读取properties否   http://localhost/upload/index.jsp，点“上传文件"即可看到控制台打印的信息。
				FileUploadPropertiesUtils.getInstance().addPropertis(key,value);
				//FileUploadPropertiesUtils.getInstance().getProperty(key);  //取键的值
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
