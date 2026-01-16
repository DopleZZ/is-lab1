<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Import Vehicles</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 800px; margin: 0 auto; }
        .message { padding: 10px; margin-bottom: 20px; border-radius: 5px; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
<div class="container">
    <h1>Import Vehicles</h1>
    
    <a href="${pageContext.request.contextPath}/vehicles">Back to Dashboard</a>

    <c:if test="${not empty success}">
        <div class="message success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="message error">${error}</div>
    </c:if>

    <div style="margin-top: 20px; padding: 20px; border: 1px solid #ccc; border-radius: 5px;">
        <h3>Upload CSV File</h3>
        <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/import">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="file" name="file" accept=".csv" required>
            <button type="submit">Import</button>
        </form>
        <p><small>Format: name,x,y,type,enginePower,numberOfWheels,capacity,distanceTravelled,fuelConsumption,fuelType</small></p>
    </div>

    <h3>Import History</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>User</th>
                <th>Status</th>
                <th>Added Count</th>
                <th>Timestamp</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${history}" var="h">
                <tr>
                    <td>${h.id}</td>
                    <td>${h.user.username}</td>
                    <td>
                        <c:choose>
                            <c:when test="${h.status}"><span style="color: green;">Success</span></c:when>
                            <c:otherwise><span style="color: red;">Failed</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>${h.addedCount}</td>
                    <td>${h.timestamp}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
