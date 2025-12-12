<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Панель управления транспортными средствами</title>
    <meta charset="UTF-8">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <link rel="stylesheet" href="<c:url value='/resources/css/common.css?v=3'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/dashboard.css?v=3'/>">
</head>
<body>
    <div class="container">
        <div class="menu">
            <a href="<c:url value='/'/>">Главная</a>
            <a href="<c:url value='/special'/>">Специальные операции</a>
            <a href="<c:url value='/vehicles/create'/>">Создать новое транспортное средство</a>
        </div>
        
        <h1>Панель управления транспортными средствами</h1>
        
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        
        <c:if test="${param.created == 'true'}">
            <div class="success-message">
                Транспортное средство успешно создано!
            </div>
        </c:if>
        
        <c:if test="${param.updated == 'true'}">
            <div class="info-message">
                Транспортное средство успешно обновлено!
            </div>
        </c:if>
        
        <c:if test="${param.deleted == 'true'}">
            <div class="error-box">
                Транспортное средство успешно удалено!
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div class="error">${param.error}</div>
        </c:if>
        
        <div class="filters">
            <form method="get" action="<c:url value='/vehicles'/>">
                <label>Фильтр по названию:</label>
                <input type="text" name="nameFilter" value="${nameFilter}" placeholder="Введите название...">
                <label>Сортировать по:</label>
                <select name="sortField">
                    <option value="name" ${sortField == 'name' ? 'selected' : ''}>Название</option>
                    <option value="type" ${sortField == 'type' ? 'selected' : ''}>Тип</option>
                    <option value="creationDate" ${sortField == 'creationDate' ? 'selected' : ''}>Дата создания</option>
                </select>
                <select name="sortAscending">
                    <option value="true" ${sortAscending ? 'selected' : ''}>По возрастанию</option>
                    <option value="false" ${!sortAscending ? 'selected' : ''}>По убыванию</option>
                </select>
                <button type="submit" class="btn btn-primary">Применить фильтры</button>
                <a href="<c:url value='/vehicles'/>" class="btn btn-primary">Очистить</a>
            </form>
        </div>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Название</th>
                    <th>Тип</th>
                    <th>Координаты</th>
                    <th>Мощность двигателя</th>
                    <th>Колёса</th>
                    <th>Вместимость</th>
                    <th>Пробег</th>
                    <th>Расход топлива</th>
                    <th>Тип топлива</th>
                    <th>Дата создания</th>
                    <th>Действия</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="vehicle" items="${vehicles}">
                    <tr>
                        <td>${vehicle.id}</td>
                        <td>${vehicle.name}</td>
                        <td>${vehicle.type}</td>
                        <td>(${vehicle.coordinates.x}, ${vehicle.coordinates.y})</td>
                        <td>${vehicle.enginePower != null ? vehicle.enginePower : 'Н/Д'}</td>
                        <td>${vehicle.numberOfWheels}</td>
                        <td>${vehicle.capacity}</td>
                        <td><fmt:formatNumber value="${vehicle.distanceTravelled}" pattern="#.##"/></td>
                        <td><fmt:formatNumber value="${vehicle.fuelConsumption}" pattern="#.##"/></td>
                        <td>${vehicle.fuelType}</td>
                        <td><fmt:formatDate value="${vehicle.creationDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>
                            <a href="<c:url value='/vehicles/${vehicle.id}'/>" class="btn btn-primary">Просмотр</a>
                            <a href="<c:url value='/vehicles/${vehicle.id}/edit'/>" class="btn btn-success">Редактировать</a>
                            <form method="post" action="<c:url value='/vehicles/${vehicle.id}/delete'/>" class="inline-form">
                                <button type="submit" class="btn btn-danger" onclick="return confirm('Вы уверены?')">Удалить</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <div class="pagination">
            <c:if test="${currentPage > 0}">
                <a href="<c:url value='/vehicles?page=${currentPage - 1}&size=${pageSize}&nameFilter=${nameFilter}&sortField=${sortField}&sortAscending=${sortAscending}'/>">Предыдущая</a>
            </c:if>
            <span>Страница ${currentPage + 1} из ${totalPages} (Всего: ${totalCount})</span>
            <c:if test="${currentPage < totalPages - 1}">
                <a href="<c:url value='/vehicles?page=${currentPage + 1}&size=${pageSize}&nameFilter=${nameFilter}&sortField=${sortField}&sortAscending=${sortAscending}'/>">Следующая</a>
            </c:if>
        </div>
    </div>
</body>
</html>
