<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Header</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" integrity="sha512-..." crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .header {
            background-color: rgb(0, 128, 128);
            padding: 12px 20px;
            color: white;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header__right {
            display: flex;
            align-items: center;
            gap: 18px;
            flex-wrap: wrap;
        }

        .header__right a,
        .header__right .welcome-text {
            color: white;
            text-decoration: none;
            font-size: 16px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .header__right a:hover {
            text-decoration: underline;
        }

        .logout-button {
            margin-left: auto;
            font-weight: bold;
        }

        .login-button {
            font-weight: bold;
        }

        .fa-user,
        .fa-sign-out,
        .fa-sign-in {
            margin-right: 4px;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <div class="header__right">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <c:if test="${sessionScope.user.roleId == 1}">
                            <a href="${pageContext.request.contextPath}/" class="header__link">
                                Admin Panel
                            </a>
                        </c:if>
                        <span class="welcome-text">
                            <i class="fa fa-user"></i> Hello, ${sessionScope.user.userFullName}!
                        </span>
                        <a href="${pageContext.request.contextPath}/logout" class="logout-button">
                            <i class="fa fa-sign-out"></i> Log out
                        </a>
                    </c:when>

                    <c:when test="${not empty sessionScope.customer}">
                        <span class="welcome-text">
                            <i class="fa fa-user"></i> Hello, ${sessionScope.customer.customerFullName}!
                        </span>
                        <a href="${pageContext.request.contextPath}/Logouts" class="logout-button">
                            <i class="fa fa-sign-out"></i> Log out
                        </a>
                    </c:when>

                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/Login" class="login-button">
                            <i class="fa fa-sign-in"></i> Log in
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</body>
</html>
