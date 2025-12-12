<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>${vehicle.id == 0 ? 'Создание' : 'Редактирование'} транспортного средства</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value='/resources/css/common.css?v=3'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/form.css?v=3'/>">
</head>
<body>
    <div class="form-container">
        <h1>${vehicle.id == 0 ? 'Создание' : 'Редактирование'} транспортного средства</h1>
        
        <c:if test="${not empty error}">
            <div class="error-box">${error}</div>
        </c:if>
        
        <form method="post" action="<c:url value='${vehicle.id == 0 ? "/vehicles/create" : "/vehicles/" += vehicle.id += "/update"}'/>">
            <div class="form-group">
                <label for="name">Название *</label>
                <input type="text" id="name" name="name" value="${vehicle.name}" required>
            </div>
            
            <div class="form-group">
                <label>Координаты *</label>
                <div class="radio-group">
                    <input type="radio" name="createNewCoordinates" id="selectExisting" value="false" checked onchange="toggleCoordinates()">
                    <label for="selectExisting">Выбрать существующие</label>
                    <input type="radio" name="createNewCoordinates" id="createNew" value="true" onchange="toggleCoordinates()">
                    <label for="createNew">Создать новые</label>
                </div>
                <div id="existingCoords">
                    <select name="coordinatesId" id="coordinatesId">
                        <option value="">-- Выберите координаты --</option>
                        <c:forEach var="coord" items="${availableCoordinates}">
                            <option value="${coord.id}" ${vehicle.coordinates != null && vehicle.coordinates.id == coord.id ? 'selected' : ''}>
                                (${coord.x}, ${coord.y})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div id="newCoords" class="hidden">
                    <label>X:</label>
                    <input type="number" step="0.01" name="newCoordX" id="newCoordX" value="${vehicle.coordinates != null ? vehicle.coordinates.x : ''}">
                    <label>Y (макс. 820):</label>
                    <input type="number" step="0.01" name="newCoordY" id="newCoordY" value="${vehicle.coordinates != null ? vehicle.coordinates.y : ''}" max="820">
                </div>
            </div>
            
            <div class="form-group">
                <label for="type">Тип транспортного средства *</label>
                <select id="type" name="type" required>
                    <option value="">-- Выберите тип --</option>
                    <option value="SUBMARINE" ${vehicle.type == 'SUBMARINE' ? 'selected' : ''}>Подводная лодка</option>
                    <option value="BOAT" ${vehicle.type == 'BOAT' ? 'selected' : ''}>Лодка</option>
                    <option value="CHOPPER" ${vehicle.type == 'CHOPPER' ? 'selected' : ''}>Вертолёт</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="enginePower">Мощность двигателя</label>
                <input type="number" id="enginePower" name="enginePower" value="${vehicle.enginePower}" min="1">
            </div>
            
            <div class="form-group">
                <label for="numberOfWheels">Количество колёс *</label>
                <input type="number" id="numberOfWheels" name="numberOfWheels" value="${vehicle.numberOfWheels}" min="1" required>
            </div>
            
            <div class="form-group">
                <label for="capacity">Вместимость *</label>
                <input type="number" id="capacity" name="capacity" value="${vehicle.capacity}" min="1" required>
            </div>
            
            <div class="form-group">
                <label for="distanceTravelled">Пробег *</label>
                <input type="number" step="0.01" id="distanceTravelled" name="distanceTravelled" value="${vehicle.distanceTravelled}" min="1" required>
            </div>
            
            <div class="form-group">
                <label for="fuelConsumption">Расход топлива *</label>
                <input type="number" step="0.01" id="fuelConsumption" name="fuelConsumption" value="${vehicle.fuelConsumption}" min="1" required>
            </div>
            
            <div class="form-group">
                <label for="fuelType">Тип топлива *</label>
                <select id="fuelType" name="fuelType" required>
                    <option value="">-- Выберите тип топлива --</option>
                    <option value="GASOLINE" ${vehicle.fuelType == 'GASOLINE' ? 'selected' : ''}>Бензин</option>
                    <option value="KEROSENE" ${vehicle.fuelType == 'KEROSENE' ? 'selected' : ''}>Керосин</option>
                    <option value="ELECTRICITY" ${vehicle.fuelType == 'ELECTRICITY' ? 'selected' : ''}>Электричество</option>
                    <option value="ANTIMATTER" ${vehicle.fuelType == 'ANTIMATTER' ? 'selected' : ''}>Антиматерия</option>
                </select>
            </div>
            
            <div class="form-buttons">
                <button type="submit" class="btn btn-primary">${vehicle.id == 0 ? 'Создать' : 'Обновить'}</button>
                <a href="<c:url value='/vehicles'/>" class="btn btn-secondary">Отмена</a>
            </div>
        </form>
    </div>
    
    <script>
        function toggleCoordinates() {
            const createNew = document.querySelector('input[name="createNewCoordinates"][value="true"]').checked;
            const existingCoords = document.getElementById('existingCoords');
            const newCoords = document.getElementById('newCoords');
            const newCoordY = document.getElementById('newCoordY');
            const coordinatesSelect = document.querySelector('select[name="coordinatesId"]');
            
            if (createNew) {
                existingCoords.classList.add('hidden');
                newCoords.classList.remove('hidden');
                newCoordY.setAttribute('required', 'required');
                coordinatesSelect.removeAttribute('required');
            } else {
                existingCoords.classList.remove('hidden');
                newCoords.classList.add('hidden');
                newCoordY.removeAttribute('required');
                coordinatesSelect.setAttribute('required', 'required');
            }
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            toggleCoordinates();
        });
    </script>
</body>
</html>
