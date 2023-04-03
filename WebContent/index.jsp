<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
   var i =2;    //
   $(function(){
	   //新增附件案例的点击事件
	   $("#addFile").click(function(){
		   //this指调 用者，即下面'点击上传文件’按钮，this的爷爷是<tr>,下面代码作用是，选择上传文件和添加描述信息，放到this的前面
		   $(this).parent().parent().before(
				   "<tr class=\"file\">"
				    +"<td>请  选 择 上 传的第  "+i+"  个文件：</td>"
				    +"<td><input type=\"file\" name=\"file1\"/></td></tr>"
				    +"<tr class=\"desc\">"
				    +"  <td>请输入第"+i+"个的文件的描述：</td>"
				    +"  <td><input type=\"text\" name=\"desc1\"/>"
				    +"<button type=\"button\" id=\"delete"+i+"\">删除</button>"
				    +"</td> </tr>"
				   );
		            i++;
		            //alert(i);  //测试当前是第几个
		            //获取当前生成的删除按钮，并删除不想要的新增附件
		            $("#delete"+(i-1)).click(function(){
		      		  var $tr =$(this).parent().parent();
		      		  $tr.prev("tr").remove();
		      		  $tr.remove();
		      		//删除了中间的tr节点，我们要对所有的tr的节点，重新排序，不要123456成为13456要成为12345
		      		   $(".file").each(function(index){
		      			   var count=index+1;
		      			   $(this).find("td:first").text("请选择上传的第"+count+"个文件:");
		      			   $(this).find("td:last input").attr("name","file"+count);
		      		   });
		      		 $(".desc").each(function(index){
		      			   var count=index+1;
		      			   $(this).find("td:first").text("请输入第"+count+"个的文件的描述：");
		      			   $(this).find("td:last input").attr("name","desc"+count);
		      		   });
		      	   });
	   });
	   
	   
   });
</script>
<style type="text/css">
   /* tr:first-child{
       text-align:right;
   } */
    tr{
      height:45px;
   } 
   table{
        margin-left:30px;
   }
</style>
</head>
<body>
<!-- pageContext.request.contextPath获取绝对路径 -->
<%-- <form action="${pageContext.request.contextPath}/UploadHandleServlet" enctype="multipart/form-data" method="post">

选择上传的文件：<input type="file" name="file1"><br/> <!-- 文件域表单项 -->
上传文件的描述：<input type="text" name="desc"><br/>  <!-- 普通表单项 -->
<input type="submit" value="提交">
</form> --%>

<form action="${pageContext.request.contextPath}/upload.up"  enctype="multipart/form-data" method="post">
<table>
   <tr class="file">
       <td>请  选 择 上 传的第     1 个文件</td>
       <td><input type="file" name="file1"/></td>
   </tr>
   <tr class="desc">
       <td>请输入第1个文件的描述：</td>
       <td><input type="text" name="desc1"/></td>
   </tr>
   <tr>
   <td><input style="float:right;" type="submit" value="点击上传文件"/></td>
   <td><button type="button" id="addFile" >点击新增加一个附件</button></td>   <!--因此处button包在form中，所以点击它相当于提交，所以要加type属性，声明它只是按钮没有提交功能  -->
   </tr>
</table>
</form>
<br>
	<br>
	<br>
	<br> 已经上传的文件:<br><br>
	<table border="1" cellpadding="0" cellspacing="0">
		<tr>
			<td>id</td>
			<td>源文件名</td>
			<td>目的地文件名
			<td>大小</td>
			<td>描述</td>
			<td>上传日期</td>
			<td>删除/下载</td>
		</tr>
		<c:forEach var="uf" items="${upfiles}">
			<tr>
				<td>${uf.id }</td>
				<td>${uf.oldFileName }</td>
				<td>${uf.saveName }</td>
				<td>${uf.fileSize}B</td>
				<td>${uf.desc }</td>
				<td>${uf.saveTime }</td>
				<td><a href="${pageContext.request.contextPath}/deleteFile.up?id=${uf.id}">删除</a> | <a href="${pageContext.request.contextPath}/downloadFile.up?id=${uf.id}">下载</a></td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>