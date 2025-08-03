<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Customer Reports</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
    body {
        background: linear-gradient(135deg, #f8fffc 0%, #f3faff 100%);
        min-height: 100vh;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }
    .container {
        background: rgba(255,255,255,0.96);
        border-radius: 20px;
        box-shadow: 0 8px 32px rgba(120,200,180,0.10);
        padding: 40px 32px 32px 32px;
        margin-top: 50px;
    }
    h1 {
        color: #41b883;
        font-weight: 700;
        letter-spacing: 1px;
        margin-bottom: 32px !important;
        text-align: center;
    }
    .btn-primary {
        background: linear-gradient(90deg, #a8edea 0%, #41b883 100%);
        border: none;
        color: #17896b;
        font-weight: 600;
        border-radius: 12px;
        transition: background 0.25s, box-shadow 0.25s;
        box-shadow: 0 4px 16px rgba(65,184,131,0.07);
    }
    .btn-primary:hover, .btn-primary:focus {
        background: linear-gradient(90deg, #d4fc79 0%, #96e6a1 100%);
        color: #14795f;
        box-shadow: 0 8px 20px rgba(65,184,131,0.14);
    }
    .alert-success {
        background: #e5fef5;
        color: #1abc9c;
        border-radius: 10px;
        border: 1px solid #b2f2df;
        font-weight: 500;
    }
    .alert-danger {
        background: #fff0f3;
        color: #e84118;
        border-radius: 10px;
        border: 1px solid #ffd6e0;
        font-weight: 500;
    }
    .table {
        background: white;
        border-radius: 15px;
        overflow: hidden;
        box-shadow: 0 4px 16px rgba(65,184,131,0.07);
    }
    .table-bordered > :not(caption) > * > * {
        border-width: 1px 1px;
    }
    thead.table-dark {
        background: linear-gradient(90deg, #a8edea 0%, #41b883 100%) !important;
    }
    thead.table-dark th {
        color: #17896b !important;
        font-weight: 700;
        border: none;
        letter-spacing: 0.5px;
        background: transparent !important;
    }
    tbody tr {
        transition: background 0.18s;
    }
    tbody tr:hover {
        background: #e9fff7;
    }
    td, th {
        vertical-align: middle !important;
    }
    .text-center {
        color: #41b883;
        font-weight: 500;
        letter-spacing: 0.5px;
    }
    /* Responsive: */
    @media (max-width: 768px) {
        .container {
            padding: 18px 5px 15px 5px;
            margin-top: 20px;
        }
        h1 { font-size: 1.3rem; }
        .table { font-size: 0.96rem; }
    }
</style>


</head>
<body>
<div class="container mt-5">
    <h1 class="mb-4">Your Reports</h1>

    <!-- Display success or error messages -->
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success">${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger">${sessionScope.error}</div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <!-- Show Create Report button only if there are active, non-expired contracts -->
    <c:if test="${not empty roomsAndContracts}">
        <a href="CustomerReport?action=create" class="btn btn-primary mb-3">Create Report</a>
    </c:if>

    <table class="table table-bordered table-hover">
        <thead class="table-dark">
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
                    <td colspan="7" class="text-center">No reports found.</td>
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
        <a href="<%= request.getContextPath() %>/customer/room-list" class="btn btn-primary mb-3">Back to Notification</a>
          <%@include file = "/WEB-INF/inclu/footer.jsp" %>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>