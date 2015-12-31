<%@ page language="java" contentType="text/html; charset=GBK" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK" />
<title>棠棣服务监控管理</title>
<style>
.thead{
	text-align:left;
	color:#407fab;
	line-height:22px;
	margin-top:10px;
}
</style>
</head>
<body>
<c:if test="${ empty sessionScope.SERVINFO}">
<c:redirect url="tdsys?method=list&ags=-a" />
</c:if>
<Table width="80%" align="center" borderColor="#000000" style="border-collapse: collapse; margin-bottom: 5px;" border="1" >
<tr class="row1" onMouseOver="this.className='row1'" onMouseOut="this.className='row'">
	<td class='thead'>服务名</td>
	<td class='thead'>服务状态</td>
	<td class='thead'>服务描述</td>
	<td class='thead'>操作</td>
</tr>
<c:forEach items="${sessionScope.SERVINFO}" var="server">
<tr>
	<td class='svrinfo'>${server.SVRNAM}</td>
	<td class='svrinfo'>${server.SVRSYS}</td>
	<td class='svrinfo'>${server.SVRDES}</td>
	<td class='svropr'>[<a href='${pageContext.request.contextPath}/tdsys?method=start&ags=-s&appName=${server.SVRNAM}&<%=Math.random()%>'>启动</a> | 
	<a href='${pageContext.request.contextPath}/tdsys?method=stop&ags=-s&appName=${server.SVRNAM}&<%=Math.random()%>'>停止</a>]
	</td>
</tr>
</c:forEach>
</Table>
</body>