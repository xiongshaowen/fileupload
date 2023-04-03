package cn.ybzy.utils;
/**
 * 获取properties文件的键值，用到Map
 * 单例模式，节省内存，不会创建多余的类对象,由方法调用创建一个实例。
 * @author xiongshaowen
 *
 */

import java.util.HashMap;
import java.util.Map;

public class FileUploadPropertiesUtils {
    private Map<String,String> map = new HashMap<>();
    private FileUploadPropertiesUtils() 
    {    }                       ;//私有构造方法,不允许外部创建对象
    private static FileUploadPropertiesUtils instance =null;
    public static FileUploadPropertiesUtils getInstance() {
    	if(instance==null) {
    		instance = new FileUploadPropertiesUtils();
    	}
    	return instance;
    }
    /**
     * 对外入口，增加key,value
     * @param key
     * @param value
     */
    public void addPropertis(String key,String value) {
    	map.put(key, value);
    }
    /**
     * 获健的值。
     * @param key
     * @return
     */
    public String getProperty(String key) {
    	return map.get(key);
    }
}
