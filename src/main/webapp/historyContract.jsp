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
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Contract ID</th>
                        <th>Room ID</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                        <th>Created At</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="contract" items="${contracts}">
                        <tr>
                            <td>${contract.contractId}</td>
                            <td>${contract.roomId}</td>
                            <td>${contract.startDate}</td>
                            <td>${contract.endDate}</td>
                            <td>${contract.contractstatus}</td>
                            <td>${contract.contractcreatedAt}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </body>
</html>
