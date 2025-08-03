<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Notification" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    String view = request.getParameter("view") != null ? request.getParameter("view") : "notifications";
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Notifications & Reports</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet"/>
        <style>
            :root {
                --primary-color: #1e3b8a; /* Navy blue */
                --pastel-blue: #b3d4fc; /* Soft pastel blue for gradient */
                --accent-color: #2a4d69;
                --text-color: #111827;
                --white: #ffffff;
                --background-color: #f5f9fc;
                --sidebar-color: #003459;
                --shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                --border-radius: 12px;
                --success: #1e3b8a; /* Navy blue for buttons */
                --warning: #d97706;
                --danger: #b91c1c;
                --light-gray: #f3f4f6;
                --success-bg: #e0f2fe;
                --pending-bg: #ffedd5;
                --active-bg: #e0f2fe;
                --table-header-bg: #1e3b8a;
                --table-header-text: #ffffff;
            }

            html, body {
                height: 100%;
                margin: 0;
                padding: 0;
                font-family: 'Segoe UI', sans-serif;
                color: var(--text-color);
                font-size: 1.1rem;
                background-color: var(--background-color);
            }

            body {
                display: flex;
                flex-direction: column;
                min-height: 100vh;
                padding-top: 50px;
                overflow-x: hidden;
            }

            .navbar {
                background: linear-gradient(to right, var(--primary-color), var(--pastel-blue));
                border-bottom: 1px solid var(--light-gray);
                padding: 12px 24px;
                position: fixed;
                top: 0;
                width: 100%;
                z-index: 1030;
            }

            .container {
                background: var(--white);
                padding: 40px 24px;
                margin: 10px auto; /* Center the container with auto margins */
                max-width: 1800px; /* Set a reasonable max-width */
                flex: 1 0 auto;
            }

            h1, h3 {
                color: var(--primary-color);
                font-weight: 700;
                text-align: center;
                margin-bottom: 32px;
            }

            .btn-navy {
                background-color: var(--primary-color);
                border: none;
                font-weight: 600;
                color: var(--white);
                border-radius: 20px;
                padding: 0.7rem 1.2rem;
                transition: 0.3s ease;
            }

            .btn-navy:hover {
                background-color: var(--accent-color);
                color: var(--white);
            }

            .btn-outline-navy {
                border: 1px solid var(--primary-color);
                color: var(--primary-color);
                background-color: transparent;
                border-radius: 20px;
                padding: 0.6rem 1.2rem;
                font-size: 1rem;
                transition: 0.3s ease;
            }

            .btn-outline-navy:hover {
                background-color: #e6f0ff;
                color: var(--primary-color);
            }

            .btn-outline-navy.active {
                background-color: var(--primary-color);
                color: var(--white);
                border-color: var(--primary-color);
            }

            .list-group-item {
                border: 1px solid var(--light-gray);
                border-radius: var(--border-radius);
                margin-bottom: 10px;
                padding: 20px;
                background-color: var(--white);
                transition: background-color 0.3s ease;
            }

            .list-group-item-warning {
                background-color: var(--pending-bg);
            }

            .list-group-item:hover {
                background-color: #f0f5ff;
            }

            .badge.bg-danger {
                background-color: var(--danger) !important;
                border-radius: 15px;
                padding: 8px 12px;
                font-size: 0.9rem;
            }

            .fw-bold {
                color: var(--primary-color);
            }

            .text-muted {
                font-size: 0.9rem;
            }

            .alert-info, .alert-success {
                background-color: var(--success-bg);
                color: var(--primary-color);
                border-left: 4px solid var(--pastel-blue);
                border-radius: var(--border-radius);
                padding: 15px;
                margin-bottom: 20px;
            }

            .alert-danger {
                background-color: #fee2e2;
                color: var(--danger);
                border-left: 4px solid #f87171;
                border-radius: var(--border-radius);
                padding: 15px;
                margin-bottom: 20px;
            }

            .table {
                background: var(--white);
                border-radius: var(--border-radius);
                overflow: hidden;
                box-shadow: var(--shadow);
            }

            .table th {
                background-color: var(--table-header-bg);
                color: var(--table-header-text);
                text-align: center;
                font-weight: 600;
                border: none;
            }

            .table td {
                vertical-align: middle;
            }

            .table tbody tr:hover {
                background-color: #f0f5ff;
            }

            .text-center {
                color: var(--primary-color);
                font-weight: 500;
            }

            footer {
                background: linear-gradient(to right, var(--primary-color), var(--pastel-blue));
                color: var(--white);
                padding: 0 0;
                text-align: center;
                width: 100%;
                position: relative;
                bottom: 0;
                flex-shrink: 0;
                z-index: 1000;
            }

            footer p, footer a {
                color: var(--white);
                margin: 0;
                font-size: 1rem;
            }

            footer a:hover {
                color: var(--accent-color);
                text-decoration: none;
            }

            @media (max-width: 768px) {
                .container {
                    padding: 20px 10px;
                    margin-top: 20px;
                }
                h1, h3 {
                    font-size: 1.3rem;
                }
                .list-group-item, .table {
                    font-size: 0.95rem;
                    padding: 15px;
                }
                .badge.bg-danger {
                    font-size: 0.8rem;
                    padding: 6px 10px;
                }
                footer {
                    padding: 15px 0;
                    font-size: 0.9rem;
                }
            }
        </style>
    </head>
    <body>
        <!-- Navbar -->
        <nav class="navbar navbar-light shadow-sm fixed-top px-4 py-2">
            <div class="container-fluid d-flex justify-content-between align-items-center">
                <!-- Logo -->
                 <a href="<%= request.getContextPath() %>/customer/room-list" class="btn btn-back">
                    <i class="bi bi-arrow-left-circle"></i> Back
                </a>

                <!-- Title -->
                <span class="fs-5 fw-bold" style="color: var(--white);">Notifications & Reports</span>
                <!-- Include User Info -->
                <%@include file="/WEB-INF/include/header_cus.jsp" %>
            </div>
        </nav>

        <div class="container">
            <!-- Toggle Buttons -->
            <div class="d-flex gap-2 mb-4 justify-content-center">
                <button class="btn btn-outline-navy <%= "notifications".equals(view) ? "active" : "" %>" onclick="toggleView('notifications')">Notifications</button>
                <button class="btn btn-outline-navy <%= "reports".equals(view) ? "active" : "" %>" onclick="toggleView('reports')">Reports</button>
            </div>

            <!-- Notifications View -->
            <div id="notifications-view" class="<%= "notifications".equals(view) ? "" : "d-none" %>">


                <c:if test="${not empty sessionScope.message}">
                    <div class="alert alert-success">${sessionScope.message}</div>
                    <c:remove var="message" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger">${sessionScope.error}</div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <% if (notifications == null || notifications.isEmpty()) { %>
                <div class="alert alert-info">You have no notifications.</div>
                <% } else { %>
                <ul class="list-group">
                    <% for (Notification n : notifications) { %>
                    <li class="list-group-item d-flex justify-content-between align-items-start
                        <%= n.isRead() ? "" : "list-group-item-warning" %>">
                        <div class="ms-2 me-auto">
                            <div class="fw-bold"><%= n.getTitle() %></div>
                            <%= n.getMessage() %><br/>
                            <small class="text-muted">From: <%= n.getSentBy() %> | At: <%= n.getNotificationCreatedAt() %></small>
                        </div>
                        <% if (!n.isRead()) { %>
                        <span class="badge bg-danger rounded-pill">New</span>
                        <% } %>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </div>

            <!-- Reports View -->
            <div id="reports-view" class="<%= "reports".equals(view) ? "" : "d-none" %>">
                

                <c:if test="${not empty roomsAndContracts}">
                    <a href="<%= request.getContextPath() %>/CustomerReport?action=create" class="btn btn-navy mb-3">Create Report</a>
                </c:if>

                <table class="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th>Room Number</th>
                            <th>Issue Description</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th>Resolved By</th>
                            <th>Resolved Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty reports}">
                                <tr>
                                    <td colspan="6" class="text-center">No reports found.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="report" items="${reports}">
                                    <tr>
                                        <td><c:out value="${report.roomNumber}"/></td>
                                        <td><c:out value="${report.issueDescription}"/></td>
                                        <td>${report.reportStatus}</td>
                                        <td><fmt:formatDate value="${report.reportCreatedAt}" pattern="dd-MM-yyyy HH:mm"/></td>
                                        <td>${report.resolvedBy != null ? report.resolvedBy : "Not yet processed"}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty report.resolvedDate}">
                                                    <fmt:formatDate value="${report.resolvedDate}" pattern="dd-MM-yyyy HH:mm"/>
                                                </c:when>
                                                <c:otherwise>Not yet processed</c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <%@include file="/WEB-INF/inclu/footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
        <script>
                    function toggleView(view) {
                        const notificationsView = document.getElementById('notifications-view');
                        const reportsView = document.getElementById('reports-view');
                        const notificationsBtn = document.querySelector('button[onclick="toggleView(\'notifications\')"]');
                        const reportsBtn = document.querySelector('button[onclick="toggleView(\'reports\')"]');

                        if (view === 'notifications') {
                            notificationsView.classList.remove('d-none');
                            reportsView.classList.add('d-none');
                            notificationsBtn.classList.add('active');
                            reportsBtn.classList.remove('active');
                        } else {
                            notificationsView.classList.add('d-none');
                            reportsView.classList.remove('d-none');
                            notificationsBtn.classList.remove('active');
                            reportsBtn.classList.add('active');
                        }

                        // Update URL without reloading
                        history.pushState(null, '', '<%= request.getContextPath() %>/customer/notifications?view=' + view);
                    }
        </script>
    </body>
</html>