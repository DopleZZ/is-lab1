<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Регистрация</title>
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
        .divider {
            text-align: center;
            margin: 20px 0;
            color: #999;
            font-size: 14px;
        }
        .hint {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>Регистрация</h2>
    
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/register" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="text" name="username" placeholder="Имя пользователя" required autofocus 
               minlength="3" maxlength="50" value="${username}">
        <div class="hint">Минимум 3 символа</div>
        
        <input type="password" name="password" placeholder="Пароль" required minlength="4">
        <div class="hint">Минимум 4 символа</div>
        
        <button type="submit" class="btn-primary">Зарегистрироваться</button>
    </form>
    
    <div class="divider">или</div>
    
    <a href="${pageContext.request.contextPath}/login" class="btn-secondary">Уже есть аккаунт? Войти</a>
</div>
</body>
</html>
