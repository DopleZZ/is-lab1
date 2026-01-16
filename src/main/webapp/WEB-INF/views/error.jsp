<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ошибка</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value='/resources/css/common.css?v=3'/>">
</head>
<body>
    <div class="container">
        <div class="menu">
            <a href="<c:url value='/'/>">Главная</a>
            <a href="<c:url value='/vehicles'/>">Транспортные средства</a>
            <a href="<c:url value='/special'/>">Специальные операции</a>
            <a href="<c:url value='/import'/>">Импорт</a>
        </div>
        
        <h1>Произошла ошибка</h1>
        
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        
        <p><a href="<c:url value='/'/>">Вернуться на главную</a></p>
    </div>
</body>
</html>
