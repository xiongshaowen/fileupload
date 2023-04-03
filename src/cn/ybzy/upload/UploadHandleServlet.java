package cn.ybzy.upload;

import java.io.File;
/**
 * 该类是测试用的，最开始的简单上传，没有实批量上传的时候。
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;

import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



import java.util.List;
import java.util.UUID;
@WebServlet(urlPatterns= {"/UploadHandleServlet"})
public class UploadHandleServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doGet()");
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	   //System.out.println(req.getParameter("file1")); //拿不到表单参数
		     //首先要导外包：commons-fileupload-1.2.1.jar，commons-io-2.0.jar
		
//		❶获取和创建保存文件的最终目录和监时目录。
		String savePath = req.getSession().getServletContext().getRealPath("/WEB-INF/upload");  //保存文件的服务器上的绝对路径,预先创建的（不是操作系统的）
		String tempPath = req.getSession().getServletContext().getRealPath("/WEB-INF/temp");    //保存文件的临时目录，动态创建的
		File tempFile = new File(tempPath);
		if(!tempFile.exists()) {                                                                 //如果临时目录不存在则创建 之。
			tempFile.mkdirs();
		}
//		❷  解析 request请求
		//1.创建一个工厂类。 
		DiskFileItemFactory factory = new DiskFileItemFactory();
		    //public DiskFileItemFactory(int sizeThreshold,File repository)
		    //sizeThreshold:  服务器里内存，有资源上限，例如：上传文件大小超过物理内存，会死机
	        //                 sizeThreshold临界值：600KB，上传文件小于600KB，我就直接把文件放在内存中，这样很快
	        //                 传来的文件大于600KB，把它分成一块一块的，大于600KB的多余的放在磁盘中。程序需要时再去取之。
		    //repository:      指定磁盘存放文件的文件夹。
		factory.setSizeThreshold(100*1024);                   //100KB,上传的文件小100KB，放在内存中，大100KB放进tempPath
		factory.setRepository(tempFile);                      //设置临时目录
		//2.创建request请求的解析器。
		ServletFileUpload  sfu=new ServletFileUpload(factory);
		    //sfu这个解析器，也是可以设置对上传文件的大小的限制
		    //sfu.setFileSizedMax()  总的文件大小
		    //sfu.setSizeMax()       单个文件的大小
		sfu.setFileSizeMax(200*1204*1024);                    //限制上传单个文件的大小在20M以内
		sfu.setHeaderEncoding("UTF-8");                      //防止中文乱码
		sfu.setSizeMax(400*1204*1024);                        //上传所有文件的大小400M
		sfu.setProgressListener(new ProgressListener() {     //上传文件进度临听器
			@Override
			public void update(long yUploadFileSize, long uploadFileSize, int arg) {
				System.out.println("上传文件总大小为： "+uploadFileSize+",已上传文件大小: "+yUploadFileSize);
				
			}
		});
		//3.解析request请求，返回List<FileItem>
		if(!ServletFileUpload.isMultipartContent(req)) {
			return;                                      //如果不是multipart/form-data数据编码方式，则退出程序
		}
		OutputStream out = null;
		InputStream  in=null;
		try {
			List<FileItem> filelist = sfu.parseRequest(req);  //FileItem就是封装一个个form提交过来的表单项：普通表单项/文件域表单项
			if(filelist!=null && filelist.size()>0) {
			for(FileItem fileItem:filelist){
				
				if(fileItem.isFormField()) {              //如果是普通表单项,则只输出打印相关信息
					String name=fileItem.getFieldName();  //拿到表单项的value如<input type="text" name="username" value="姓名">相当于键值对的键
					String value = fileItem.getString("UTF-8");  //拿到表单中的内容
					System.out.println("普通的表单项，名为："+name+",内容为: "+value);
				}else {                    //若是文件域表单
					String fileName = fileItem.getName();      //拿到文件的名字 名字带扩展名如:xiong.txt
					          //注意：fileName,IE中带绝对地址如'd:\abc\xiong.txt'，火狐中只显示  'xiong.txt'
					String fileType=fileItem.getContentType(); //拿到文件的类型
					long fileSize=fileItem.getSize();          //文件的大小
					fileName = fileName.substring(fileName.lastIndexOf("\\")+1);   //从最后一个'\'查找截取，这样就避免了不同浏览器的格式。
					if(fileName==null || fileName.trim().equals("")) {
						continue;                                     //如文件名为空或去掉首尾空格为空字符串，则退出本次循环，可以继续。。。
					}
					String fileNameEx =  fileName.substring(fileName.lastIndexOf(".")+1);   //拿到文件的后缀名即扩展名。
					if(fileNameEx.equals("rar")||fileNameEx.equals("zip")) {
						throw new RuntimeException("禁止上传压缩文件");
					}
					//将文件流写入保存的目录中（生成新的文件名，避免一个目录中文件太多而生成新的存储目录）
					String saveFileName = makeFileName(fileName);  //确保产生的文件名不重复
					String realSavePath = makePath(saveFileName,savePath);
					
					//先创建一个输出流
					out = new FileOutputStream(realSavePath+"\\"+saveFileName);
					in =fileItem.getInputStream();                  //拿到输入流
					//建立缓冲器，建立般运流的勺子。
					byte[] buffer =new byte[1024];
					int len =0;
					while((len=in.read(buffer))>0) {
						out.write(buffer,0,len);   //写出去了
					}
				    in.close();
				    out.close();
				}
			}
			
		}	
			
		} catch (FileUploadBase.FileSizeLimitExceededException e) {
			System.out.println("单个文件的大小超出限制");
		}catch(FileUploadBase.SizeLimitExceededException e2) {
			System.out.println("总文件超出限制大小！");
		}catch(Exception e) {
			System.out.println("上传文件失败！");
			e.printStackTrace();
		}finally {                      //最后，万一上面执行不成功，流没关闭不好，
			if(in!=null)
				in.close();
			if(out!=null)
				out.close();
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
		String dir = savePath+"\\"+dir1+"\\"+dir2;   
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

}
