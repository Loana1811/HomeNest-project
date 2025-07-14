<%-- manager/managerCreateNotification.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Notification for Customers</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="container">
        <h1>Create Notification</h1>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/manager/notification?action=createNotification" method="post">
            <div class="mb-3">
                <label for="title" class="form-label">Title</label>
                <input type="text" class="form-control" id="title" name="title" required>
            </div>
            <div class="mb-3">
                <label for="message" class="form-label">Message</label>
                <textarea class="form-control" id="message" name="message" rows="5" required></textarea>
            </div>
            <div class="mb-3">
                <label>Customers</label>
                <div>
                    <input type="checkbox" id="selectAll"> Select All
                </div>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Select</th>
                            <th>Name</th>
                            <th>Phone</th>
                            <th>Email</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="customer" items="${customers}">
                            <tr>
                                <td><input type="checkbox" name="customerIds" value="${customer.customerID}" class="customerCheckbox"></td>
                                <td>${customer.customerFullName}</td>
                                <td>${customer.phoneNumber}</td>
                                <td>${customer.email}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty customers}">
                            <tr><td colspan="4">No customers found in your block.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <button type="submit" class="btn btn-primary">Send Notification</button>
        </form>
    </div>

    <script>
        document.getElementById('selectAll').addEventListener('click', function () {
            let checkboxes = document.querySelectorAll('.customerCheckbox');
            for (let checkbox of checkboxes) {
                checkbox.checked = this.checked;
            }
        });
    </script>
</body>
</html>