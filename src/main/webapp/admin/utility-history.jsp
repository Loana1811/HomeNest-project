<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.*, model.UtilityHistoryView" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Price Change History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            margin: 0;
        }
        .sidebar {
            width: 220px;
            background-color: #1f2937;
            color: white;
            padding: 20px;
        }
        .sidebar h4 {
            font-size: 1.25rem;
            margin-bottom: 1rem;
        }
        .sidebar a {
            color: white;
            text-decoration: none;
            display: block;
            padding: 8px 0;
        }
        .sidebar a.active, .sidebar a:hover {
            background-color: #3b82f6;
            padding-left: 10px;
            border-radius: 4px;
        }
        .main-content {
            flex: 1;
            padding: 30px;
            background: #f9fafb;
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <h4>🏠 HomeNest</h4>
    <div class="mb-3 fw-bold">Admin Menu</div>
    <a href="account?action=list">Accounts</a>
    <a href="room?action=list">Rooms</a>
    <a href="tenant?action=list">Tenants</a>
    <a href="bill?action=list">Bills</a>
    <a href="utility?action=list" class="active">Utilities</a>
    <a href="usage?action=record">Record Usage</a>
    <a href="usage?action=view">View Usage List</a>
    <a href="contract?action=list">Contract</a>
</div>

<!-- Main Content -->
<div class="main-content">
    <h3 class="mb-4">📜 Price Change History</h3>

    <c:if test="${empty historyList}">
        <div class="alert alert-warning">⚠️ No history records found.</div>
    </c:if>

    <c:if test="${not empty historyList}">
        <table class="table table-bordered table-hover bg-white">
            <thead class="table-dark">
                <tr>
                    <th>Type</th>
                    <th>Name</th>
                    <th>Old Price</th>
                    <th>New Price</th>
                    <th>Changed By</th>
                    <th>Change Date</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="h" items="${historyList}">
                    <tr>
                        <td>
                            <span class="badge bg-primary">
                                <c:choose>
                                    <c:when test="${h.isUtility}">
                                        Utility
                                    </c:when>
                                    <c:otherwise>
                                        Incurred Fee
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty h.utilityName}">
                                    <span class="text-danger">[Removed]</span>
                                </c:when>
                                <c:otherwise>
                                    ${h.utilityName}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatNumber value="${h.oldPrice}" type="number" pattern="#,##0"/>₫</td>
                        <td><fmt:formatNumber value="${h.newPrice}" type="number" pattern="#,##0"/>₫</td>
                        <td>${h.changedBy}</td>
                        <td><fmt:formatDate value="${h.createdAt}" pattern="yyyy-MM-dd"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <a href="utility?action=list" class="btn btn-secondary mt-3">← Back</a>
</div>
</body>
</html>
