<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Специальные операции</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value='/resources/css/common.css?v=3'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/special.css?v=3'/>">
</head>
<body>
    <div class="container">
        <div class="menu">
            <a href="<c:url value='/'/>">Главная</a>
            <a href="<c:url value='/vehicles'/>">Панель управления</a>
        </div>
        
        <h1>Специальные операции</h1>
        
        <div class="operation">
            <h3>1. Подсчитать количество транспортных средств по расходу топлива</h3>
            <form method="post" action="<c:url value='/special/count-by-fuel-consumption'/>">
                <label>Расход топлива:</label>
                <input type="number" step="0.01" name="fuelConsumption" min="1" required>
                <button type="submit" class="btn">Подсчитать</button>
            </form>
        </div>
        
        <div class="operation">
            <h3>2. Найти транспортные средства по префиксу названия</h3>
            <form method="post" action="<c:url value='/special/find-by-name-prefix'/>">
                <label>Префикс названия:</label>
                <input type="text" name="prefix" required>
                <button type="submit" class="btn">Найти</button>
            </form>
        </div>
        
        <div class="operation">
            <h3>3. Найти транспортные средства по типу топлива (меньше заданного)</h3>
            <form method="post" action="<c:url value='/special/find-by-fuel-type-less'/>">
                <label>Тип топлива:</label>
                <select name="fuelType" required>
                    <option value="GASOLINE">Бензин</option>
                    <option value="KEROSENE">Керосин</option>
                    <option value="ELECTRICITY">Электричество</option>
                    <option value="ANTIMATTER">Антиматерия</option>
                </select>
                <button type="submit" class="btn">Найти</button>
            </form>
        </div>
        
        <div class="operation">
            <h3>4. Сбросить пробег до нуля</h3>
            <form method="post" action="<c:url value='/special/reset-distance'/>">
                <label>ID транспортного средства:</label>
                <input type="number" name="id" min="1" required>
                <button type="submit" class="btn">Сбросить</button>
            </form>
        </div>
        
        <div class="operation">
            <h3>5. Добавить колёса транспортному средству</h3>
            <form method="post" action="<c:url value='/special/add-wheels'/>">
                <label>ID транспортного средства:</label>
                <input type="number" name="id" min="1" required>
                <label>Колёс для добавления:</label>
                <input type="number" name="wheelsToAdd" min="1" required>
                <button type="submit" class="btn">Добавить</button>
            </form>
        </div>
        
        <c:if test="${not empty result}">
            <div class="result">${result}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="error-box">${error}</div>
        </c:if>
        
        <c:if test="${not empty vehicles}">
            <h2>Результаты:</h2>
            <table class="results-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Тип</th>
                        <th>Мощность двигателя</th>
                        <th>Колёса</th>
                        <th>Вместимость</th>
                        <th>Пробег</th>
                        <th>Расход топлива</th>
                        <th>Тип топлива</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="vehicle" items="${vehicles}">
                        <tr>
                            <td>${vehicle.id}</td>
                            <td>${vehicle.name}</td>
                            <td>${vehicle.type}</td>
                            <td>${vehicle.enginePower != null ? vehicle.enginePower : 'Н/Д'}</td>
                            <td>${vehicle.numberOfWheels}</td>
                            <td>${vehicle.capacity}</td>
                            <td><fmt:formatNumber value="${vehicle.distanceTravelled}" pattern="#.##"/></td>
                            <td><fmt:formatNumber value="${vehicle.fuelConsumption}" pattern="#.##"/></td>
                            <td>${vehicle.fuelType}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</body>
</html>
