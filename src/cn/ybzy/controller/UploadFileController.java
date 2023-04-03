package cn.ybzy.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ybzy.model.UploadFile;
import cn.ybzy.service.FactoryService;
import cn.ybzy.service.UploadFileService;
@SuppressWarnings("unused")
@WebServlet(urlPatterns = { "*.up" })
public class UploadFileController extends HttpServlet{
   private static final long serialVersionUID = 1L;
   UploadFileService ufs= FactoryService.getUploadFileService();
 @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String mn = req.getServletPath();
		mn = mn.substring(1);
		mn = mn.substring(0, mn.length() - 3);
		try {
			Method method = this.getClass().getDeclaredMethod(mn, HttpServletRequest.class, HttpServletResponse.class);
			method.invoke(this, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//resp.getWriter().println(FileUploadPropertiesUtils.getInstance().getProperty("sizeMax"));  //测试：打印接收到一个properties中的一个键的值。
		
   }
	private void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<UploadFile> list = ufs.getUploadFiles();
		req.setAttribute("upfiles", list);
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}
	
	protected void upload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//这controller层,接收index.jsp页面发送来的文件信息,文件本身,描述信息
		//保存接收到的文件的工作,不在控制层里实现, 转发到service,实现保存文件
		try {
			ufs.saveFile(req, resp);
			//这里没有抓到异常,上传文件,成功,要到index.up执行，要把保存的上传信息对象放到域空间里（request),转送出去让显示页面获取，显示在页面上
			resp.sendRedirect(req.getContextPath()+"/index.up");
		} catch (Exception e) {
			//让服务层去实现保存文件具体业务逻辑的功能代码, 单个文件, 总的文件, 类型
			//这里,获取到异常信息,注入jsp页面, 显示
			//System.out.println("contoller's error:" + e.getMessage());
			req.setAttribute("errorMsg", e.getMessage());
			req.getRequestDispatcher("/error.jsp").forward(req, resp);
		}
	}
	private void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int id = Integer.parseInt(req.getParameter("id"));
		//String filePath = req.getParameter("fp");  //用不着，因为由&fp=${fn:replace(uf.savePath,'\\','%5c')}%5c${uf.saveName}">删除</a>传路径过来会不安全 
		//System.out.println(filePath);
		ufs.deletUploadFile(id);
		resp.sendRedirect(req.getContextPath()+"/index.up");
	}
	
	private void downloadFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取要下载的文件的绝对路径
		int id = Integer.parseInt(req.getParameter("id"));
		UploadFile uf = ufs.getUploadFileById(id);
		String filePath = uf.getSavePath()+"\\"+uf.getSaveName();
		String userAgent = req.getHeader("User-Agent");
		//2.获取要下载的文件名
		String fileName = uf.getOldFileName();
		//针对IE或者以IE为内核的浏览器：主要是解决,下载的时候,文件名是中文乱码
        if (userAgent.contains("MSIE")||userAgent.contains("Trident")) {
        	fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
        //非IE浏览器的处理：
        	fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
        }
      //3.设置content-disposition响应头控制浏览器以下载的方式打开文件
        resp.setHeader("content-disposition","attachment;filename="+fileName);
        
      //4.获取要下载的文件输入流
        InputStream in = new FileInputStream(filePath);
        int len = 0;
        //5.创建输入流缓冲区
        byte[] buffer = new byte[1024];
        //6.通过response对象获取OutputStream输出流对象
        OutputStream os = resp.getOutputStream();
        //7.将FileInputStream流对象写入到buffer缓冲区
        while((len=in.read(buffer))>0){
            os.write(buffer,0,len);
        }
        //8.关闭流
        in.close();
        os.close();
	}
	

}
