<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dashboard Customer</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            :root {
                --pastel-green: #7ed6a5;
                --pastel-blue: #6ec6f3;
                --pastel-teal: #4fc3a1;
                --pastel-bg: rgba(255,255,255,0.95);
                --pastel-border: #b2f7ef;
                --pastel-hover: #e0f7fa;
                --pastel-success: #a8e6cf;
                --pastel-danger: #ffb6b9;
            }
            body {
                background: linear-gradient(135deg, #e0f7fa 0%, #f1f8e9 100%);
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            .dashboard-container {
                background: var(--pastel-bg);
                border-radius: 20px;
                box-shadow: 0 8px 32px rgba(76, 195, 161, 0.08);
                backdrop-filter: blur(8px);
                padding: 40px;
                margin-top: 50px;
            }
            .welcome-title {
                color: var(--pastel-teal);
                font-weight: 700;
                font-size: 2.5rem;
                margin-bottom: 30px;
                text-align: center;
                letter-spacing: 1px;
            }
            .welcome-title i {
                color: var(--pastel-green);
            }
            .action-buttons {
                display: flex;
                gap: 20px;
                justify-content: center;
                margin-bottom: 30px;
                flex-wrap: wrap;
            }
            .btn-custom {
                padding: 15px 30px;
                border-radius: 15px;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
                border: 2px solid var(--pastel-teal);
                background: #fff;
                color: var(--pastel-teal);
                position: relative;
                overflow: hidden;
                box-shadow: 0 2px 8px rgba(76, 195, 161, 0.07);
            }
            .btn-custom i {
                color: var(--pastel-green);
            }
            .btn-custom:hover, .btn-custom:focus {
                background: var(--pastel-teal);
                color: #fff;
                border-color: var(--pastel-teal);
            }
            .btn-custom:hover i, .btn-custom:focus i {
                color: #fff;
            }
            .notification-btn {
                border-color: var(--pastel-green);
                color: var(--pastel-green);
            }
            .notification-btn:hover, .notification-btn:focus {
                background: var(--pastel-green);
                color: #fff;
                border-color: var(--pastel-green);
            }
            .notification-btn i {
                color: var(--pastel-teal);
            }
            .notification-badge {
                position: absolute;
                top: -8px;
                right: -8px;
                background: var(--pastel-blue);
                color: #fff;
                border-radius: 50%;
                width: 25px;
                height: 25px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 12px;
                font-weight: bold;
                animation: pulse 2s infinite;
                border: 2px solid #fff;
            }
            @keyframes pulse {
                0% { transform: scale(1);}
                50% { transform: scale(1.1);}
                100% { transform: scale(1);}
            }
            /* Popup thông báo */
            .notification-popup {
                position: fixed;
                top: 80px;
                right: -400px;
                width: 380px;
                max-height: 500px;
                background: #fff;
                border-radius: 15px;
                box-shadow: 0 20px 40px rgba(76, 195, 161, 0.10);
                z-index: 1050;
                transition: right 0.3s ease;
                border: 1px solid var(--pastel-border);
            }
            .notification-popup.show {
                right: 20px;
            }
            .popup-header {
                background: linear-gradient(45deg, var(--pastel-teal), var(--pastel-green));
                color: #fff;
                padding: 20px;
                border-radius: 15px 15px 0 0;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .popup-header h5 {
                margin: 0;
                font-weight: 600;
            }
            .close-popup {
                background: none;
                border: none;
                color: #fff;
                font-size: 20px;
                cursor: pointer;
                padding: 0;
                width: 30px;
                height: 30px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: background 0.3s ease;
            }
            .close-popup:hover {
                background: rgba(255,255,255,0.15);
            }
            .popup-body {
                padding: 20px;
                max-height: 400px;
                overflow-y: auto;
            }
            .notification-item {
                padding: 15px;
                border-bottom: 1px solid var(--pastel-border);
                border-radius: 10px;
                margin-bottom: 10px;
                background: #f8fffb;
                transition: all 0.3s ease;
                color: var(--pastel-teal);
            }
            .notification-item:hover {
                background: var(--pastel-hover);
                transform: translateX(5px);
            }
            .notification-item:last-child {
                border-bottom: none;
                margin-bottom: 0;
            }
            .notification-item h6, .notification-item p, .notification-item small {
                color: var(--pastel-teal);
            }
            .notification-item i {
                color: var(--pastel-blue);
            }
            .alert-custom {
                border-radius: 15px;
                border: 2px solid var(--pastel-teal);
                padding: 20px;
                margin-bottom: 20px;
                font-weight: 500;
                background: #fff;
                color: var(--pastel-teal);
            }
            .alert-success {
                border-color: var(--pastel-green);
                color: var(--pastel-green);
                background: var(--pastel-success);
            }
            .alert-danger {
                border-color: var(--pastel-blue);
                color: var(--pastel-blue);
                background: var(--pastel-danger);
            }
            /* Overlay */
            .notification-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(76, 195, 161, 0.08);
                z-index: 1040;
                opacity: 0;
                visibility: hidden;
                transition: all 0.3s ease;
            }
            .notification-overlay.show {
                opacity: 1;
                visibility: visible;
            }
            /* Responsive */
            @media (max-width: 768px) {
                .notification-popup {
                    width: 90%;
                    right: -95%;
                }
                .notification-popup.show {
                    right: 5%;
                }
                .dashboard-container {
                    margin: 20px;
                    padding: 20px;
                }
                .welcome-title {
                    font-size: 2rem;
                }
                .action-buttons {
                    flex-direction: column;
                    align-items: center;
                }
            }
            /* Custom scrollbar */
            .popup-body::-webkit-scrollbar {
                width: 6px;
            }
            .popup-body::-webkit-scrollbar-track {
                background: #f1f1f1;
                border-radius: 10px;
            }
            .popup-body::-webkit-scrollbar-thumb {
                background: linear-gradient(45deg, var(--pastel-teal), var(--pastel-green));
                border-radius: 10px;
            }
            /* Empty notification */
            .popup-body .fa-bell-slash {
                color: var(--pastel-blue) !important;
            }
            .popup-body .text-muted {
                color: var(--pastel-teal) !important;
                opacity: 0.7;
            }
        </style>
    </head>
    <body>
        <!-- Overlay -->
        <div class="notification-overlay" id="notificationOverlay"></div>
        <div class="container">
            <div class="dashboard-container">
                <h1 class="welcome-title">
                    <i class="fas fa-tachometer-alt"></i>
                    Dashboard Khách hàng
                </h1>
                <!-- Hiển thị thông báo lỗi hoặc thành công -->
                <c:if test="${not empty sessionScope.message}">
                    <div class="alert alert-success alert-custom">
                        <i class="fas fa-check-circle me-2"></i>
                        ${sessionScope.message}
                    </div>
                    <c:remove var="message" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger alert-custom">
                        <i class="fas fa-exclamation-circle me-2"></i>
                        ${sessionScope.error}
                    </div>
                    <c:remove var="error" scope="session"/>
                </c:if>
                <!-- Action Buttons -->
                <div class="action-buttons">
                    <!-- Nút Báo cáo -->
                    <a href="${pageContext.request.contextPath}/CustomerReport"
                       class="btn-custom btn-report">
                        <i class="fas fa-chart-bar me-2"></i>
                        Xem Báo cáo
                    </a>
                    <!-- Nút Thông báo -->
                    <button type="button" class="btn-custom notification-btn" 
                            id="notificationBtn">
                        <i class="fas fa-bell me-2"></i>
                        Thông báo
                        <c:if test="${not empty notifications && notifications.size() > 0}">
                            <span class="notification-badge">${notifications.size()}</span>
                        </c:if>
                    </button>
                </div>
            </div>
        </div>
        <!-- Popup Thông báo -->
        <div class="notification-popup" id="notificationPopup">
            <div class="popup-header">
                <h5>
                    <i class="fas fa-bell me-2"></i>
                    Thông báo của bạn
                </h5>
                <button class="close-popup" id="closePopup">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="popup-body">
                <c:choose>
                    <c:when test="${not empty notifications}">
                        <c:forEach var="notification" items="${notifications}">
                            <div class="notification-item">
                                <div class="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h6 class="mb-1">
                                            <i class="fas fa-info-circle text-primary me-2"></i>
                                            ${notification.title}
                                        </h6>
                                        <p class="mb-1 text-muted">${notification.message}</p>
                                        <small class="text-muted">
                                            <i class="fas fa-clock me-1"></i>
                                            <fmt:formatDate value="${notification.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-4">
                            <i class="fas fa-bell-slash fa-3x text-muted mb-3"></i>
                            <p class="text-muted">Không có thông báo nào</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const notificationBtn = document.getElementById('notificationBtn');
                const notificationPopup = document.getElementById('notificationPopup');
                const notificationOverlay = document.getElementById('notificationOverlay');
                const closePopup = document.getElementById('closePopup');
                // Mở popup
                notificationBtn.addEventListener('click', function() {
                    notificationPopup.classList.add('show');
                    notificationOverlay.classList.add('show');
                });
                // Đóng popup
                function closeNotificationPopup() {
                    notificationPopup.classList.remove('show');
                    notificationOverlay.classList.remove('show');
                }
                closePopup.addEventListener('click', closeNotificationPopup);
                notificationOverlay.addEventListener('click', closeNotificationPopup);
                // Đóng popup khi nhấn ESC
                document.addEventListener('keydown', function(e) {
                    if (e.key === 'Escape') {
                        closeNotificationPopup();
                    }
                });
            });
        </script>
    </body>
</html>