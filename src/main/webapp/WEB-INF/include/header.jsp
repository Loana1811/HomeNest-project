<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Header</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <style>
            .header {
                background-color: rgb(0, 128, 128);
                padding: 12px 20px;
                color: white;
            }

            .header-container {
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
            <div class="header-container">
                <div class="header__right">
                    <c:choose>
                        <c:when test="${not empty sessionScope.customer}">
                            <span class="welcome-text">
                                <i class="fa fa-user"></i> Hello, ${sessionScope.customer.customerFullName}!
                            </span>

                            <!-- ? Nút Thông báo -->
                            <a href="${pageContext.request.contextPath}/customer/notifications"
                               class="btn btn-outline-warning btn-sm position-relative">
                                <i class="fa fa-bell"></i>
                                <c:if test="${unreadCount > 0}">
                                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                        ${unreadCount}
                                    </span>
                                </c:if>
                            </a>

                            <!-- ? Nút View Profile -->
                            <a href="${pageContext.request.contextPath}/customer/view-profile" class="btn btn-outline-light btn-sm">
                                <i class="fa fa-id-card"></i> View Profile
                            </a>

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
