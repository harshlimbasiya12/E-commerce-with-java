<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>E-Commerce Login</title>
</head>
<body>

    <h2>Login</h2>

    <p style="color:red;">
        ${error}
    </p>

    <form action="${pageContext.request.contextPath}/login"
          method="post">

        <label>Email:</label>
        <input type="email" name="email" required>

        <br><br>

        <label>Password:</label>
        <input type="password" name="password" required>

        <br><br>

        <button type="submit">Login</button>
    </form>

</body>
</html>