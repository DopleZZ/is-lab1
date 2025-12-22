<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Вход в систему</title>
    <style>
        body { 
            font-family: 'Segoe UI', Arial, sans-serif; 
            display: flex; 
            justify-content: center; 
            align-items: center; 
            min-height: 100vh; 
            background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
            margin: 0;
        }
        .login-container { 
            background: white; 
            padding: 40px; 
            border-radius: 12px; 
            box-shadow: 0 10px 40px rgba(0,0,0,0.3); 
            width: 350px; 
        }
        h2 {
            text-align: center;
            color: #1a1a1a;
            margin-bottom: 30px;
            font-weight: 600;
        }
        input { 
            width: 100%; 
            padding: 12px 15px; 
            margin: 10px 0; 
            box-sizing: border-box; 
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        input:focus {
            outline: none;
            border-color: #1a1a1a;
        }
        .btn-primary { 
            width: 100%; 
            padding: 12px; 
            background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
            color: white; 
            border: none; 
            border-radius: 8px; 
            cursor: pointer; 
            font-size: 16px;
            font-weight: 600;
            margin-top: 15px;
            transition: all 0.3s;
        }
        .btn-primary:hover { 
            background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        }
        .btn-secondary {
            width: 100%;
            padding: 12px;
            background: white;
            color: #1a1a1a;
            border: 2px solid #1a1a1a;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            font-weight: 600;
            margin-top: 10px;
            transition: all 0.3s;
            text-decoration: none;
            display: block;
            text-align: center;
            box-sizing: border-box;
        }
        .btn-secondary:hover {
            background: #f5f5f5;
        }
        .error { 
            color: #d32f2f; 
            background: #ffebee;
            padding: 12px 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            font-size: 14px;
            border-left: 4px solid #d32f2f;
        }
        .success { 
            color: #2e7d32; 
            background: #e8f5e9;
            padding: 12px 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            font-size: 14px;
            border-left: 4px solid #2e7d32;
        }
        .divider {
            text-align: center;
            margin: 20px 0;
            color: #999;
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>Вход в систему</h2>
    
    <c:if test="${param.error != null}">
        <div class="error">Неверное имя пользователя или пароль. Пожалуйста, попробуйте снова.</div>
    </c:if>
    
    <c:if test="${param.logout != null}">
        <div class="success">Вы успешно вышли из системы.</div>
    </c:if>
    
    <c:if test="${param.registered != null}">
        <div class="success">Регистрация прошла успешно! Теперь вы можете войти.</div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="text" name="username" placeholder="Имя пользователя" required autofocus>
        <input type="password" name="password" placeholder="Пароль" required>
        <button type="submit" class="btn-primary">Войти</button>
    </form>
    
    <div class="divider">или</div>
    
    <a href="${pageContext.request.contextPath}/register" class="btn-secondary">Зарегистрироваться</a>
</div>
</body>
</html>
