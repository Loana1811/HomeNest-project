<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Contract History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            background-color: #f4f7fc;
            padding-top: 80px;
            padding-left: 250px;
        }

        .container {
            max-width: 1100px;
            margin: 0 auto;
            padding: 30px;
            background-color: #ffffff;
            border-radius: 15px;
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.07);
            animation: fadeIn 0.6s ease-in-out;
            transition: all 0.3s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(25px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h2 {
            color: #1e3b8a;
            font-weight: 800;
            margin-bottom: 30px;
            text-align: center;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .table {
            border-radius: 10px;
            overflow: hidden;
        }

        .table thead {
            background: linear-gradient(to right, #1e3b8a, #3f5fa6);
            color: white;
            text-align: center;
        }

        .table tbody tr {
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .table tbody tr:hover {
            background-color: #eef2ff;
            transform: scale(1.01);
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }

        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
            display: inline-block;
        }

        .status-active {
            background-color: #d1fae5;
            color: #065f46;
        }

        .status-ended {
            background-color: #fee2e2;
            color: #991b1b;
        }

        .status-pending {
            background-color: #fef9c3;
            color: #92400e;
        }

        .btn-primary {
            background-color: #3f5fa6;
            border: none;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(63, 95, 166, 0.3);
            transition: background-color 0.3s ease, transform 0.2s;
        }

        .btn-primary:hover {
            background-color: #2c468f;
            transform: translateY(-2px);
        }

        .btn-secondary {
            margin-top: 20px;
            border-radius: 8px;
            background-color: #9ca3af;
            border: none;
            padding: 8px 16px;
            transition: background-color 0.3s;
        }

        .btn-secondary:hover {
            background-color: #6b7280;
        }

        .alert {
            margin-top: 20px;
            font-weight: 500;
            border-radius: 8px;
            animation: fadeIn 0.3s ease;
        }
    </style>
</head>
<body>

    <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

    <div class="container">
        <h2>Contract History</h2>

        <c:if test="${empty contracts}">
            <p>No contract history available.</p>
        </c:if>

        <c:if test="${not empty contracts}">
            <table class="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Contract ID</th>
                        <th>Tenant ID</th>
                        <th>Tenant Name</th>
                        <th>Room Number</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                        <th>Created At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="contract" items="${contracts}">
                        <tr>
                            <td>${contract.contractId}</td>
                            <td>${contract.tenantId}</td>
                            <td>${contract.tenantName}</td>
                            <td>${contract.roomNumber}</td>
                            <td>${contract.startDate}</td>
                            <td>${contract.endDate}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${contract.contractStatus == 'Active'}">
                                        <span class="status-badge status-active">Active</span>
                                    </c:when>
                                    <c:when test="${contract.contractStatus == 'Ended'}">
                                        <span class="status-badge status-ended">Ended</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge status-pending">${contract.contractStatus}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${contract.contractCreatedAt}</td>
                            <td>
                                <a href="<%= request.getContextPath()%>/Contracts?action=view&id=${contract.contractId}" class="btn btn-sm btn-primary" title="View Contract">
                                    <i class="fas fa-eye"></i> 
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${not empty sessionScope.deleteError}">
            <div class="alert alert-danger text-center">${sessionScope.deleteError}</div>
            <c:remove var="deleteError" scope="session"/>
        </c:if>

        <a href="${ctx}/Contracts?action=list" class="btn btn-secondary">Back</a>
    </div>
</body>
</html>
