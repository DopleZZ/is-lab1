<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Система управления транспортными средствами</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value='/resources/css/common.css?v=3'/>">
</head>
<body>
    <div class="container">
        <h1>Система управления транспортными средствами</h1>
        <p>Добро пожаловать в систему управления транспортными средствами. Используйте меню ниже для навигации:</p>
        <ul class="menu">
            <li><a href="<c:url value='/vehicles'/>">Панель управления - Просмотр всех транспортных средств</a></li>
            <li><a href="<c:url value='/special'/>">Специальные операции</a></li>
        </ul>
    </div>
</body>
</html>
