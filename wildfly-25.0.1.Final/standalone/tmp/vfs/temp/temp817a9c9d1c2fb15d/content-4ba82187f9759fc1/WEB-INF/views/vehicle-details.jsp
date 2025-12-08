<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Детали транспортного средства</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value='/resources/css/common.css?v=3'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/details.css?v=3'/>">
</head>
<body>
    <div class="details-container">
        <div class="menu">
            <a href="<c:url value='/vehicles'/>">Назад к панели управления</a>
            <a href="<c:url value='/vehicles/${vehicle.id}/edit'/>">Редактировать</a>
        </div>
        
        <h1>Детали транспортного средства</h1>
        
        <dl class="details">
            <dt>ID:</dt>
            <dd>${vehicle.id}</dd>
            
            <dt>Название:</dt>
            <dd>${vehicle.name}</dd>
            
            <dt>Тип:</dt>
            <dd>${vehicle.type}</dd>
            
            <dt>Координаты:</dt>
            <dd>(${vehicle.coordinates.x}, ${vehicle.coordinates.y})</dd>
            
            <dt>Мощность двигателя:</dt>
            <dd>${vehicle.enginePower != null ? vehicle.enginePower : 'Н/Д'}</dd>
            
            <dt>Количество колёс:</dt>
            <dd>${vehicle.numberOfWheels}</dd>
            
            <dt>Вместимость:</dt>
            <dd>${vehicle.capacity}</dd>
            
            <dt>Пробег:</dt>
            <dd><fmt:formatNumber value="${vehicle.distanceTravelled}" pattern="#.##"/></dd>
            
            <dt>Расход топлива:</dt>
            <dd><fmt:formatNumber value="${vehicle.fuelConsumption}" pattern="#.##"/></dd>
            
            <dt>Тип топлива:</dt>
            <dd>${vehicle.fuelType}</dd>
            
            <dt>Дата создания:</dt>
            <dd><fmt:formatDate value="${vehicle.creationDate}" pattern="yyyy-MM-dd HH:mm:ss"/></dd>
        </dl>
    </div>
</body>
</html>
