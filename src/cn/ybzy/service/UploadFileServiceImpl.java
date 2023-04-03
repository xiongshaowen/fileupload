package cn.ybzy.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import cn.ybzy.dao.FactoryDao;
import cn.ybzy.dao.UploadFileDao;
import cn.ybzy.model.UploadFile;
import cn.ybzy.utils.FileUploadPropertiesUtils;



public class UploadFileServiceImpl implements UploadFileService {
	UploadFileDao uploadFileDao = FactoryDao.getUploadFileDao(); // 减藕
   //下面是获取properties文件中的6个键值对中的值，分别用于设置上传文件大小，存放路径
	private String savePath = FileUploadPropertiesUtils.getInstance().getProperty("savePath");
	private String tempPath = FileUploadPropertiesUtils.getInstance().getProperty("tempPath");
	private String sizeThreshold = FileUploadPropertiesUtils.getInstance().getProperty("sizeThreshold");
	private String sizeMax = FileUploadPropertiesUtils.getInstance().getProperty("sizeMax");
	private String fileSizeMax = FileUploadPropertiesUtils.getInstance().getProperty("fileSizeMax");
	private String fileEx = FileUploadPropertiesUtils.getInstance().getProperty("fileEx");

	@Override
	public void addFileInfo(UploadFile uploadFile) {
		// 把上传来的文件的信息，保存到数据库之前，我们肯定是要先把文件存到服务器上savePath,下面的saveFile()方法就是干这个的
		uploadFileDao.addFileInfo(uploadFile);

	}

	@Override
	public List<UploadFile> getUploadFiles() {
		
		return uploadFileDao.getUploadFiles();
	}

	@Override
	public void deletUploadFile(int id) {
		UploadFile uFile = uploadFileDao.get(id);
		// 先把数据库里的信息删除
		uploadFileDao.deletUploadFile(id);
		//还得把服务器磁盘上的文件删除
		deleteFile(uFile.getSavePath()+"//" + uFile.getSaveName());

	}

	@Override
	public void saveFile(HttpServletRequest req, HttpServletResponse resp)  {
		// 先把文件保存下来，到服务器指定的目录。
		String savePath = req.getSession().getServletContext().getRealPath(this.savePath); // 保存文件的服务器上的绝对路径,预先创建的（不是操作系统的）
		String tempPath = req.getSession().getServletContext().getRealPath(this.tempPath); // 保存文件的临时目录，动态创建的
		File tempFile = new File(tempPath);
		if (!tempFile.exists()) { // 如果临时目录不存在则创建 之。
			tempFile.mkdirs();
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.parseInt(this.sizeThreshold)); // 100KB,上传的文件小100KB，放在内存中，大100KB放进tempPath
		factory.setRepository(tempFile);
		// .创建request请求的解析器。
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(Integer.parseInt(this.fileSizeMax)); // 限制上传单个文件的大小在20M以内
		sfu.setHeaderEncoding("UTF-8"); // 防止中文乱码
		sfu.setSizeMax(Integer.parseInt(this.sizeMax)); // 上传所有文件的大小400M
		if(!ServletFileUpload.isMultipartContent(req)) {
			throw new RuntimeException("上传文件的form的编码方式不正确！");                                      //如果不是multipart/form-data数据编码方式，则退出程序
		}
		String desc = "";
		String fileName = "";
		String fileType = "";
	    long fileSize = 0;
	    String saveFileName = "";
	    String realSavePath ="";
	    String fileEx1 ="";
	    OutputStream out = null;
		InputStream in = null;
		try {
			List<FileItem> filelist = sfu.parseRequest(req);
		    if(filelist!=null && filelist.size()>0) {
		    	for(FileItem fileItem:filelist) {
		    		if(fileItem.isFormField()) {   //若是普通表单项，如type="text"
		    			desc = fileItem.getString("UTF-8");  //拿到表单中的内容xxxx，如请求时的'请输第n个文件的描述':xxxxx
		    			
		    			//每一次为desc  赋值 :  代表着一个文件已经上来 , 意味着完成里一次文件的上传操作  
						//在这里把上传上来的文件的信息,写入数据库里
		    			if(!"".equals(fileName)) {
		    				UploadFile uf =new UploadFile();
		    				uf.setDesc(desc);
		    				uf.setFileSize(fileSize+"");
		    				uf.setFileType(fileType);
							uf.setOldFileName(fileName);
							uf.setSavePath(realSavePath);
							uf.setSaveName(saveFileName);
							uf.setSaveTime(new Date());
							addFileInfo(uf);   //本类的一个方法-保存一个上传来的文件信息到数据库中，最上面
		    				
		    			}
		    		}else {                        //若是文件域表单，则
		    			fileName = fileItem.getName();
		    			fileType = fileItem.getContentType();
		    			fileName = fileName.substring(fileName.lastIndexOf("\\")+1);   //从最后一个'\'查找截取，这样就避免了不同浏览器的格式。
		    		    fileSize =fileItem.getSize();
		    		    fileEx1 = fileName.substring(fileName.lastIndexOf(".")+1);  //拿到文件名的后缀（doc,txt,exel...)
		    		    if(this.fileEx.indexOf(fileEx1)==-1) {     //后缀串（一串以逗号隔开的文件扩展名的字符串）找不到本对象（文件后缀）即不可上传
		    		    	throw new RuntimeException("禁止上传该类型文件");
		    		    }
		    		    saveFileName = makeFileName(fileName);            //修改存后的文件名为唯一性，即保证不重名
		    		    realSavePath = makePath(saveFileName,savePath);   //上传了很多文件，打散放
		    		    System.out.println("上传文件到达的路径：  "+realSavePath);   //测试用：看看路径拿到否，没有说明程序有问题
		    		    long starttime = System.currentTimeMillis();  
		    		   //创建输入输出流 
		    		    out =new FileOutputStream(realSavePath+"\\"+saveFileName);
		    		    in = fileItem.getInputStream();                    //拿到文件的输入流--文件流中有文件的数据
		    		
		    		 // 建立缓存区,做一个搬运文件数据流的勺子
		    		    byte[] buffer = new byte[10240];  //10240本人测试，66m的文件1k缓冲区的话上传花250mills左右，而1m的话只要60mills左右,再大没效果
		    		    int len =0;
		    		    while((len=in.read(buffer))>0){  //只要读就会len>0,把文件流（输入流）数据放到buffer中，一下放1024个
		    		      	out.write(buffer,0,len);     //把buffer数据放到要保存的文件中
		    		    	}                            
		    		    	
		    	
		    		    long endtime = System.currentTimeMillis();
		    		    System.out.println("上传文件共花了  :"
		    	                + (endtime - starttime) + " millis");
		    		    in.close();
		    		    out.close();
		    		}
		    	}
		    }
		  //删除临时目录下临时文件
		    File tempd = new File(tempPath);
			for(File file:tempd.listFiles()) {
				file.delete();
			}
		
		} catch (FileUploadBase.SizeLimitExceededException e) {
			throw new RuntimeException("上传文件总大小超出了限制： "+Integer.parseInt(this.sizeMax)/(1024*1024)+"MB!");
		}catch (FileUploadBase.FileSizeLimitExceededException e) {
			throw new RuntimeException("上传单个文件大小超出了限制： "+Integer.parseInt(this.fileSizeMax)/(1024*1024)+"MB!");
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} 

	@Override
	public void deleteFile(String savePath) {
		//删除服务器上的上传文件
				System.out.println("要删除的服务器文件地址："+savePath);
				File file = new File(savePath);
				if(file.isFile()) {
					file.delete();
				}

	}
	
	/**
	 * 在真正保存文件的目录中创建目录
	 * @param saveFileName
	 * @param savePath
	 * @return
	 */
	private String makePath(String saveFileName,String savePath) {
		int hashCode = saveFileName.hashCode();  //哈希码由十进制数据组成
		int dir1= hashCode&0xf;  //dir1的值，这个与运算的结果范围为0-15，即一个目录只存放16个文件
		int dir2 = hashCode&0xf>>4; //这个与运算的结果范围为0-15
		String dir = savePath+"//"+dir1+"//"+dir2;   //这样更好，用"\\"后，liniux中运行web服务，会不创建文件夹
		File file =new File(dir);
		if(!file.exists()) {
			file.mkdirs();
			//file.mkdir();//如果你想在已经存在的文件夹(D盘下的yy文件夹)下建立新的文件夹（2019-06-17文件夹），就可以用此方法。此方法不能在不存在的文件夹下建立新的文件夹。假如想建立名字是”2019-06-17”文件夹，那么它的父文件夹必须存在。
		}
		
		return dir;
	}
	
	/**
	 * 产生一个唯一文件名UUID+fileName
	 * @param fileName
	 * @return
	 */
	private String makeFileName(String fileName) {
		//uuid
		return UUID.randomUUID().toString()+"_"+fileName;  //确保产生的文件名不重复
	}
	
	@Override
	public UploadFile getUploadFileById(int id) {
		return uploadFileDao.get(id);
	}
}
