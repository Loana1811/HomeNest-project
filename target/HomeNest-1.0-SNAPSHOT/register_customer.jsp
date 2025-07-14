<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Customer Registration</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f5f7f8;
                margin: 0;
                padding: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                min-height: 100vh;
            }

            form {
                background-color: #ffffff;
                padding: 30px 40px;
                border-radius: 12px;
                box-shadow: 0 8px 20px rgba(0, 128, 128, 0.15);
                width: 100%;
                max-width: 500px;
                border-top: 5px solid rgb(0, 128, 128);
            }

            form h2 {
                text-align: center;
                color: rgb(0, 128, 128);
                margin-bottom: 25px;
            }

            input[type="text"],
            input[type="email"],
            input[type="password"],
            input[type="date"] {
                width: 100%;
                padding: 12px 15px;
                margin: 10px 0;
                border: 1px solid #ccc;
                border-radius: 6px;
                font-size: 16px;
                box-sizing: border-box;
            }

            input:focus {
                outline: none;
                border-color: rgb(0, 128, 128);
                box-shadow: 0 0 5px rgba(0, 128, 128, 0.3);
            }

            button {
                width: 100%;
                padding: 12px;
                background-color: rgb(0, 128, 128);
                color: #fff;
                border: none;
                border-radius: 6px;
                font-size: 18px;
                cursor: pointer;
                margin-top: 15px;
                transition: background-color 0.3s ease;
            }

            button:hover {
                background-color: #006666;
            }

            .back-button {
                background-color: #ccc;
                color: #333;
                margin-top: 10px;
            }

            .back-button:hover {
                background-color: #bbb;
            }

            p {
                text-align: center;
                margin-top: 15px;
                font-weight: bold;
            }

            p[style*="red"] {
                color: red !important;
            }

            p[style*="green"] {
                color: green !important;
            }
            select {
                width: 100%;
                padding: 12px 15px;
                margin: 10px 0;
                border: 1px solid #ccc;
                border-radius: 6px;
                font-size: 16px;
                box-sizing: border-box;
                background-color: #fff;
                appearance: none;
                -webkit-appearance: none;
                -moz-appearance: none;
            }

            select:focus {
                outline: none;
                border-color: rgb(0, 128, 128);
                box-shadow: 0 0 5px rgba(0, 128, 128, 0.3);
            }

        </style>
    </head>
    <body>
        <form action="RegisterCustomer" method="post">
            <h2>Register New Customer</h2>
            <c:if test="${not empty error}">
                <p style="color:red;">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p style="color:green;">${success}</p>
            </c:if>
            <input type="text" name="fullname" placeholder="Full Name" required>
            <input type="text" name="phone" placeholder="Phone Number" required>
            <input type="text" name="cccd" placeholder="National ID (CCCD)" required>
            <select name="gender" required>
                <option value="" disabled selected>Select Gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
            </select>

            <input type="date" name="birthdate" required>
            <input type="text" name="address" placeholder="Address" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>
            <input type="password" name="confirm" placeholder="Confirm Password" required>
            <button type="submit">Register</button>
            <a href="<%= request.getContextPath()%>/Login"><button type="button" class="back-button">Back to Login</button></a>


        </form>
    </body>
</html>
