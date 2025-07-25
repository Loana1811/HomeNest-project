<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Contract History</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="container mt-5">
            <h2>Contract History</h2>

            <c:if test="${empty contracts}">
                <p>No contract history available.</p>
            </c:if>

            <c:if test="${not empty contracts}">
                <table class="table table-striped">
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
                                <td>${contract.contractStatus}</td>
                                <td>${contract.contractCreatedAt}</td>
                                <td>
                                    <a href="<%= request.getContextPath()%>/Contracts?action=view&id=${contract.contractId}" class="btn btn-sm btn-primary">View</a>
                                </td>
                                <c:if test="${not empty sessionScope.deleteError}">
                            <div class="alert alert-danger">${sessionScope.deleteError}</div>
                            <c:remove var="deleteError" scope="session"/>
                        </c:if>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <a href="<%= request.getContextPath()%>/Contracts?action=list" class="btn btn-secondary">Back</a>
        </div>
    </body>
</html>
