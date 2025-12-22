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
            <div class="error-box">
                <span>${error}</span>
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="success-box">
                <span>${success}</span>
            </div>
        </c:if>
        
        <form method="post" action="<c:url value='${vehicle.id == 0 ? "/vehicles/create" : "/vehicles/" += vehicle.id += "/update"}'/>">
            <div class="form-group">
                <label for="name">Название <span class="required">*</span></label>
                <input type="text" id="name" name="name" value="${vehicle.name}" required 
                       placeholder="Введите название транспорта"
                       title="Название должно быть уникальным">
                <small class="hint">Название должно быть уникальным в системе</small>
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
                <label for="type">Тип транспортного средства <span class="required">*</span></label>
                <select id="type" name="type" required onchange="updateFormRestrictions()"
                        title="Выберите тип транспортного средства">
                    <option value="">-- Выберите тип --</option>
                    <option value="SUBMARINE" ${vehicle.type == 'SUBMARINE' ? 'selected' : ''}>Подводная лодка (до 150 чел.)</option>
                    <option value="BOAT" ${vehicle.type == 'BOAT' ? 'selected' : ''}>Лодка (до 50 чел.)</option>
                    <option value="CHOPPER" ${vehicle.type == 'CHOPPER' ? 'selected' : ''}>Вертолёт (до 12 чел.)</option>
                </select>
                <small class="hint">Тип влияет на ограничения: колёса и вместимость</small>
            </div>
            
            <div class="form-group">
                <label for="enginePower">Мощность двигателя (л.с.)</label>
                <input type="number" id="enginePower" name="enginePower" value="${vehicle.enginePower}" 
                       min="1" placeholder="Например: 200"
                       onchange="calculateFuelConsumption()">
                <small class="hint">Расход топлива рассчитывается автоматически: мощность × 0.05</small>
            </div>
            
            <div class="form-group" id="wheelsGroup">
                <label for="numberOfWheels">Количество колёс <span class="required">*</span></label>
                <input type="number" id="numberOfWheels" name="numberOfWheels" value="${vehicle.numberOfWheels}" 
                       min="0" required placeholder="0">
                <small class="hint" id="wheelsHint">Для лодок и подводных лодок колёса = 0 (устанавливается автоматически)</small>
            </div>
            
            <div class="form-group">
                <label for="capacity">Вместимость (чел.) <span class="required">*</span></label>
                <input type="number" id="capacity" name="capacity" value="${vehicle.capacity}" 
                       min="1" required placeholder="Количество пассажиров">
                <small class="hint" id="capacityHint">Максимум: подводная лодка — 150, лодка — 50, вертолёт — 12</small>
            </div>
            
            <div class="form-group">
                <label for="distanceTravelled">Пробег (км) <span class="required">*</span></label>
                <input type="number" step="0.01" id="distanceTravelled" name="distanceTravelled" 
                       value="${vehicle.distanceTravelled}" min="1" required 
                       placeholder="Например: 1500.5">
                <small class="hint">Укажите общий пробег транспортного средства</small>
            </div>
            
            <div class="form-group">
                <label for="fuelConsumption">Расход топлива (л/100км) <span class="required">*</span></label>
                <input type="number" step="0.01" id="fuelConsumption" name="fuelConsumption" 
                       value="${vehicle.fuelConsumption}" min="0.1" required
                       placeholder="Например: 10.5">
                <small class="hint">Рассчитывается автоматически при указании мощности двигателя</small>
            </div>
            
            <div class="form-group">
                <label for="fuelType">Тип топлива <span class="required">*</span></label>
                <select id="fuelType" name="fuelType" required title="Выберите тип топлива">
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
        
        function updateFormRestrictions() {
            const type = document.getElementById('type').value;
            const wheelsInput = document.getElementById('numberOfWheels');
            const capacityInput = document.getElementById('capacity');
            const wheelsHint = document.getElementById('wheelsHint');
            
            if (type === 'SUBMARINE' || type === 'BOAT') {
                wheelsInput.value = 0;
                wheelsInput.setAttribute('readonly', 'readonly');
                wheelsInput.style.backgroundColor = '#f0f0f0';
                wheelsHint.style.color = '#d32f2f';
            } else {
                wheelsInput.removeAttribute('readonly');
                wheelsInput.style.backgroundColor = '';
                wheelsHint.style.color = '';
                if (wheelsInput.value == 0) {
                    wheelsInput.value = 1;
                }
            }
            
            let maxCapacity = 10000;
            if (type === 'SUBMARINE') maxCapacity = 150;
            else if (type === 'BOAT') maxCapacity = 50;
            else if (type === 'CHOPPER') maxCapacity = 12;
            
            capacityInput.setAttribute('max', maxCapacity);
            if (parseInt(capacityInput.value) > maxCapacity) {
                capacityInput.value = maxCapacity;
            }
        }
        
        function calculateFuelConsumption() {
            const enginePower = document.getElementById('enginePower').value;
            const fuelConsumption = document.getElementById('fuelConsumption');
            
            if (enginePower && parseInt(enginePower) > 0) {
                const calculated = (parseInt(enginePower) * 0.05).toFixed(2);
                fuelConsumption.value = calculated;
            }
        }
        
        function showFieldError(fieldId, message) {
            const field = document.getElementById(fieldId);
            if (!field) return;
            
            field.classList.add('error');
            
            const existingError = field.parentElement.querySelector('.field-error');
            if (existingError) existingError.remove();
            
            const errorDiv = document.createElement('div');
            errorDiv.className = 'field-error';
            errorDiv.textContent = message;
            field.parentElement.appendChild(errorDiv);
        }
        
        function clearFieldError(fieldId) {
            const field = document.getElementById(fieldId);
            if (!field) return;
            
            field.classList.remove('error');
            const existingError = field.parentElement.querySelector('.field-error');
            if (existingError) existingError.remove();
        }
        
        function validateForm() {
            let isValid = true;
            
            document.querySelectorAll('.field-error').forEach(e => e.remove());
            document.querySelectorAll('.error').forEach(e => e.classList.remove('error'));
            
            const name = document.getElementById('name').value.trim();
            if (!name) {
                showFieldError('name', 'Название не может быть пустым');
                isValid = false;
            }
            
            const type = document.getElementById('type').value;
            if (!type) {
                showFieldError('type', 'Выберите тип транспортного средства');
                isValid = false;
            }
            
            const createNew = document.querySelector('input[name="createNewCoordinates"][value="true"]').checked;
            if (createNew) {
                const coordY = document.getElementById('newCoordY').value;
                if (!coordY) {
                    showFieldError('newCoordY', 'Координата Y обязательна');
                    isValid = false;
                } else if (parseFloat(coordY) > 820) {
                    showFieldError('newCoordY', 'Координата Y не может быть больше 820');
                    isValid = false;
                }
            } else {
                const coordId = document.getElementById('coordinatesId').value;
                if (!coordId) {
                    showFieldError('coordinatesId', 'Выберите координаты из списка');
                    isValid = false;
                }
            }
            
            const wheels = document.getElementById('numberOfWheels').value;
            if (type !== 'SUBMARINE' && type !== 'BOAT') {
                if (!wheels || parseInt(wheels) < 1) {
                    showFieldError('numberOfWheels', 'Укажите количество колёс (минимум 1)');
                    isValid = false;
                }
            }
            
            const capacity = document.getElementById('capacity').value;
            if (!capacity || parseInt(capacity) < 1) {
                showFieldError('capacity', 'Вместимость должна быть больше 0');
                isValid = false;
            } else {
                let maxCap = 10000;
                if (type === 'SUBMARINE') maxCap = 150;
                else if (type === 'BOAT') maxCap = 50;
                else if (type === 'CHOPPER') maxCap = 12;
                
                if (parseInt(capacity) > maxCap) {
                    showFieldError('capacity', 'Максимальная вместимость для этого типа: ' + maxCap);
                    isValid = false;
                }
            }
            
            const distance = document.getElementById('distanceTravelled').value;
            if (!distance || parseFloat(distance) < 1) {
                showFieldError('distanceTravelled', 'Пробег должен быть больше 0');
                isValid = false;
            }
            
            const enginePower = document.getElementById('enginePower').value;
            const fuelConsumption = document.getElementById('fuelConsumption').value;
            
            if ((!enginePower || parseInt(enginePower) < 1) && (!fuelConsumption || parseFloat(fuelConsumption) < 0.1)) {
                showFieldError('fuelConsumption', 'Укажите мощность двигателя или расход топлива');
                isValid = false;
            } else if (enginePower && parseInt(enginePower) > 0 && fuelConsumption && parseFloat(fuelConsumption) > 0) {
                const expectedFuel = parseInt(enginePower) * 0.05;
                const minAllowed = expectedFuel * 0.9;
                const maxAllowed = expectedFuel * 1.1;
                const actualFuel = parseFloat(fuelConsumption);
                
                if (actualFuel < minAllowed || actualFuel > maxAllowed) {
                    showFieldError('fuelConsumption', 
                        'Расход топлива должен соответствовать мощности. Допустимо: ' + 
                        minAllowed.toFixed(1) + ' - ' + maxAllowed.toFixed(1) + ' л/100км');
                    isValid = false;
                }
            }
            
            const fuelType = document.getElementById('fuelType').value;
            if (!fuelType) {
                showFieldError('fuelType', 'Выберите тип топлива');
                isValid = false;
            }
            
            if (!isValid) {
                const firstError = document.querySelector('.field-error');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
            
            return isValid;
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            toggleCoordinates();
            updateFormRestrictions();
            
            document.getElementById('enginePower').addEventListener('input', calculateFuelConsumption);
            
            document.querySelector('form').addEventListener('submit', function(e) {
                if (!validateForm()) {
                    e.preventDefault();
                }
            });
            
            document.querySelectorAll('input, select').forEach(function(field) {
                field.addEventListener('input', function() {
                    clearFieldError(this.id);
                });
                field.addEventListener('change', function() {
                    clearFieldError(this.id);
                });
            });
        });
    </script>
</body>
</html>
