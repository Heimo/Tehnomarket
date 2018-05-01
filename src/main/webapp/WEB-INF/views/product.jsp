
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.tehnomarket.model.Product"%>
<%@page import="com.tehnomarket.model.User"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

	<%
	Product product = (Product) request.getAttribute("product");
   %>
	   
<body>

	<jsp:include page="header.jsp"/>
	

	<div style="height: 300px;width: 400px;">
		<div align="left">
			<br>
				Name:<%=product.getName() %><br>
				Brand:<%=product.getBrand() %><br>
				
		</div>
		
		<div align="right" style="font-size: 40px;">
			Price:<%=product.getPrice() %><br>
			<form action="add_to_cart" method="get">
				<button id="button" name ="id" type="submit" value="<%= product.getId() %>">Add to Basket</button>
			</form>
		</div>
	
	<table>
	<c:forEach var="product" items="${ characeristics }"  >
		<tr>
			<td>${product.name }</td>
			<td>${product.input }</td>
			
		</tr>
	</c:forEach>
	</table>
	
	
</body>
</html>