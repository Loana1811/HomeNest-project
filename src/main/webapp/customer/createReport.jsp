<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Create Report</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --pastel-green: #7ed6a5;
            --pastel-blue: #6ec6f3;
            --pastel-teal: #4fc3a1;
            --pastel-bg: rgba(255,255,255,0.97);
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
        .container {
            max-width: 600px;
        }
        .container.mt-5 {
            background: var(--pastel-bg);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(76, 195, 161, 0.08);
            padding: 40px 30px 30px 30px;
            margin-top: 50px !important;
        }
        h1 {
            color: var(--pastel-teal);
            font-weight: 700;
            letter-spacing: 1px;
            text-align: center;
        }
        label.form-label {
            color: var(--pastel-green);
            font-weight: 600;
        }
        .form-control, .form-control-static, textarea.form-control {
            border-radius: 12px;
            border: 1.5px solid var(--pastel-border);
            background: #fff;
            color: var(--pastel-teal);
            font-size: 1rem;
            transition: border-color 0.2s;
        }
        .form-control:focus, textarea.form-control:focus {
            border-color: var(--pastel-teal);
            box-shadow: 0 0 0 0.15rem rgba(76, 195, 161, 0.15);
        }
        .form-control-static {
            padding: 0.5rem 0.75rem;
            background: #f8fffb;
            border: none;
            color: var(--pastel-teal);
        }
        .form-text {
            color: var(--pastel-blue);
        }
        .alert {
            border-radius: 12px;
            border: none;
            font-weight: 500;
        }
        .alert-danger {
            background: var(--pastel-danger);
            color: #fff;
        }
        .alert-warning {
            background: var(--pastel-hover);
            color: var(--pastel-teal);
            border: 1.5px solid var(--pastel-blue);
        }
        .btn-primary {
            background: var(--pastel-teal);
            border: none;
            color: #fff;
            font-weight: 600;
            border-radius: 12px;
            padding: 10px 28px;
            transition: background 0.2s, color 0.2s;
        }
        .btn-primary:hover, .btn-primary:focus {
            background: var(--pastel-green);
            color: #fff;
        }
        .btn-secondary {
            background: #fff;
            color: var(--pastel-teal);
            border: 1.5px solid var(--pastel-teal);
            font-weight: 600;
            border-radius: 12px;
            padding: 10px 28px;
            margin-left: 10px;
            transition: background 0.2s, color 0.2s;
        }
        .btn-secondary:hover, .btn-secondary:focus {
            background: var(--pastel-hover);
            color: var(--pastel-teal);
        }
        @media (max-width: 600px) {
            .container.mt-5 {
                padding: 20px 8px 15px 8px;
            }
            h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
<div class="container mt-5">
    <h1 class="mb-4"><i class="fas fa-file-alt me-2"></i>Create New Report</h1>

    <!-- Check if user is logged in -->
    <c:if test="${empty sessionScope.idCustomer or sessionScope.userType != 'Customer'}">
        <c:redirect url="/Login"/>
    </c:if>

    <!-- Display error messages -->
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger">${sessionScope.error}</div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <!-- Check if there are active, non-expired contracts -->
    <c:if test="${empty roomsAndContracts}">
        <div class="alert alert-warning">No active and non-expired contracts found. You cannot create a report at this time.</div>
        <a href="CustomerReport" class="btn btn-secondary">Back to Reports</a>
    </c:if>

    <c:if test="${not empty roomsAndContracts}">
        <c:set var="contract" value="${roomsAndContracts[0]}" />
        <form action="CustomerReport" method="post" id="reportForm">
            <div class="mb-3">
                <label class="form-label">Room</label>
                <p class="form-control-static">${contract.RoomNumber}</p>
                <input type="hidden" name="roomID" value="${contract.RoomID}" />
            </div>
            <div class="mb-3">
                <label class="form-label">Contract</label>
                <p class="form-control-static">
                    Contract for ${contract.RoomNumber} (Start: <fmt:formatDate value="${contract.StartDate}" pattern="dd-MM-yyyy"/>
                    <c:if test="${not empty contract.EndDate}">, End: <fmt:formatDate value="${contract.EndDate}" pattern="dd-MM-yyyy"/></c:if>)
                </p>
                <input type="hidden" name="contractID" value="${contract.ContractID}" />
            </div>
            <div class="mb-3">
                <label for="issueDescription" class="form-label">Issue Description</label>
                <textarea class="form-control" id="issueDescription" name="issueDescription" rows="5" maxlength="1000" required></textarea>
                <div class="form-text">Maximum 1000 characters.</div>
            </div>
            <button type="submit" class="btn btn-primary"><i class="fas fa-paper-plane me-1"></i>Submit Report</button>
            <a href="CustomerReport" class="btn btn-secondary">Cancel</a>
        </form>
    </c:if>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Client-side validation for issueDescription
    document.getElementById('reportForm')?.addEventListener('submit', function(event) {
        const issueDescription = document.getElementById('issueDescription').value;
        if (issueDescription.length === 0) {
            event.preventDefault();
            alert('Issue Description is required.');
        } else if (issueDescription.length > 1000) {
            event.preventDefault();
            alert('Issue Description cannot exceed 1000 characters.');
        }
    });
</script>
</body>
</html>