<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Customer"%>
<%@page import="model.User"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Notification</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --primary: #007bff;
            --secondary: #6c757d;
            --success: #28a745;
            --danger: #dc3545;
            --light: #f8f9fa;
        }
        body {
            background-color: #e9ecef;
            font-family: 'Roboto', sans-serif;
        }
        .header {
            background: linear-gradient(90deg, var(--primary), var(--secondary));
            color: white;
            padding: 1.5rem 0;
            margin-bottom: 1.5rem;
            border-radius: 0 0 15px 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 15px;
        }
        .form-group {
            margin-bottom: 1.5rem;
        }
        .alert {
            margin-bottom: 15px;
        }
        .error-message {
            color: var(--danger);
            font-size: 0.9rem;
            display: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1>Create Notification</h1>
        </div>

        <!-- Success Message -->
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form id="notificationForm" action="${pageContext.request.contextPath}/admin/notification" method="post" onsubmit="return validateForm()">
            <input type="hidden" name="action" value="createNotification">
            <input type="hidden" name="idUser" value="${idUser}">

            <div class="form-group">
                <label for="title" class="form-label">Title</label>
                <input type="text" class="form-control" id="title" name="title" required>
                <div id="titleError" class="error-message">Title must be at least 5 characters long.</div>
            </div>

            <div class="form-group">
                <label for="message" class="form-label">Message</label>
                <textarea class="form-control" id="message" name="message" rows="3" required></textarea>
                <div id="messageError" class="error-message">Message must be at least 10 characters long.</div>
            </div>

            <div class="form-group">
                <label class="form-label">Select Customers</label>
                <div class="form-check">
                    <input type="checkbox" class="form-check-input" id="selectAllCustomers" onchange="toggleSelectAll('customers')">
                    <label class="form-check-label" for="selectAllCustomers">Select All Customers</label>
                </div>
                <c:forEach var="customer" items="${customerList}">
                    <div class="form-check">
                        <input type="checkbox" class="form-check-input customer-checkbox" name="customerIds" value="${customer.customerID}" id="customer${customer.customerID}" onchange="updateSelectAllState('customers')">
                        <label class="form-check-label" for="customer${customer.customerID}">${customer.customerFullName}</label>
                    </div>
                </c:forEach>
            </div>

            <div class="form-group">
                <label class="form-label">Select Managers</label>
                <div class="form-check">
                    <input type="checkbox" class="form-check-input" id="selectAllManagers" onchange="toggleSelectAll('managers')">
                    <label class="form-check-label" for="selectAllManagers">Select All Managers</label>
                </div>
                <c:forEach var="manager" items="${managerList}">
                    <div class="form-check">
                        <input type="checkbox" class="form-check-input manager-checkbox" name="managerIds" value="${manager.userID}" id="manager${manager.userID}" onchange="updateSelectAllState('managers')">
                        <label class="form-check-label" for="manager${manager.userID}">${manager.userFullName}</label>
                    </div>
                </c:forEach>
                <div id="recipientError" class="error-message">Please select at least one customer or manager.</div>
            </div>

            <button type="submit" class="btn btn-primary">Create Notification</button>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Function to toggle all checkboxes for a given group
        function toggleSelectAll(type) {
            const selectAllCheckbox = document.getElementById(`selectAll${type.charAt(0).toUpperCase() + type.slice(1)}`);
            const checkboxClass = type === 'customers' ? 'customer-checkbox' : 'manager-checkbox';
            const checkboxes = document.getElementsByClassName(checkboxClass);
            
            if (selectAllCheckbox.checked) {
                // Confirm before selecting all
                const confirmMessage = `Are you sure you want to select all ${type}?`;
                if (confirm(confirmMessage)) {
                    // Select all checkboxes
                    for (let checkbox of checkboxes) {
                        checkbox.checked = true;
                    }
                } else {
                    // Revert if user cancels
                    selectAllCheckbox.checked = false;
                }
            } else {
                // Unselect all checkboxes
                for (let checkbox of checkboxes) {
                    checkbox.checked = false;
                }
            }
        }

        // Function to update select all state when individual checkboxes change
        function updateSelectAllState(type) {
            const selectAllCheckbox = document.getElementById(`selectAll${type.charAt(0).toUpperCase() + type.slice(1)}`);
            const checkboxClass = type === 'customers' ? 'customer-checkbox' : 'manager-checkbox';
            const checkboxes = document.getElementsByClassName(checkboxClass);
            
            let allChecked = true;
            let anyChecked = false;
            
            for (let checkbox of checkboxes) {
                if (checkbox.checked) {
                    anyChecked = true;
                } else {
                    allChecked = false;
                }
            }
            
            if (allChecked && anyChecked) {
                selectAllCheckbox.checked = true;
                selectAllCheckbox.indeterminate = false;
            } else if (anyChecked) {
                selectAllCheckbox.checked = false;
                selectAllCheckbox.indeterminate = true;
            } else {
                selectAllCheckbox.checked = false;
                selectAllCheckbox.indeterminate = false;
            }
        }

        // Function to validate the form
        function validateForm() {
            let isValid = true;

            // Validate title (minimum 5 characters)
            const title = document.getElementById('title').value.trim();
            const titleError = document.getElementById('titleError');
            if (title.length < 5) {
                titleError.style.display = 'block';
                isValid = false;
            } else {
                titleError.style.display = 'none';
            }

            // Validate message (minimum 10 characters)
            const message = document.getElementById('message').value.trim();
            const messageError = document.getElementById('messageError');
            if (message.length < 10) {
                messageError.style.display = 'block';
                isValid = false;
            } else {
                messageError.style.display = 'none';
            }

            // Validate at least one recipient (customer or manager)
            const customerCheckboxes = document.getElementsByName('customerIds');
            const managerCheckboxes = document.getElementsByName('managerIds');
            const recipientError = document.getElementById('recipientError');
            let hasSelected = false;
            
            for (let checkbox of customerCheckboxes) {
                if (checkbox.checked) {
                    hasSelected = true;
                    break;
                }
            }
            
            if (!hasSelected) {
                for (let checkbox of managerCheckboxes) {
                    if (checkbox.checked) {
                        hasSelected = true;
                        break;
                    }
                }
            }
            
            if (!hasSelected) {
                recipientError.style.display = 'block';
                isValid = false;
            } else {
                recipientError.style.display = 'none';
            }

            return isValid;
        }

        // Initialize select all states on page load
        document.addEventListener('DOMContentLoaded', function() {
            updateSelectAllState('customers');
            updateSelectAllState('managers');
        });
    </script>
</body>
</html>