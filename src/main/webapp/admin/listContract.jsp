<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String ctx = request.getContextPath();
%>

<html>

    <head>
        <title>List of Contracts</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        
    <style>
    :root {
        --primary: #1e40af;
        --secondary: #3b82f6;
        --danger: #ef4444;
        --success: #22c55e;
        --bg: #f1f5f9;
        --white: #ffffff;
        --text-dark: #1f2937;
    }

   body {
    padding-top: 70px;  /* Đẩy xuống khỏi header cố định */
    padding-left: 260px;  /* Đẩy sang phải để tránh bị sidebar che */
    background-color: var(--bg);
}

    
.container {
    padding: 30px 40px;
    background-color: var(--white);
    border-radius: 20px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
    animation: fadeIn 0.7s ease-in-out;
}

header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 60px;  /* hoặc 70px, tùy bạn */
    z-index: 999;
    background-color: #fff;
}



    h2.text-center {
        font-size: 2rem;
        color: var(--white);
        background: linear-gradient(to right, var(--primary), var(--secondary));
        padding: 18px;
        border-radius: 14px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
        margin-bottom: 2rem;
        letter-spacing: 1px;
    }

    .alert-custom {
        border-radius: 12px;
        padding: 15px 20px;
        font-size: 1rem;
        font-weight: 500;
        box-shadow: 0 6px 20px rgba(255, 0, 0, 0.2);
        animation: slideDown 0.5s ease-in-out;
    }

    table.table {
        margin-top: 1rem;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
    }

    .table thead {
        background-color: var(--primary);
        color: var(--white);
        font-weight: bold;
        font-size: 1rem;
    }

    .table th,
    .table td {
        text-align: center;
        vertical-align: middle;
        padding: 14px;
        transition: background 0.3s;
    }

    .table tbody tr:nth-child(even) {
        background-color: #f9fafb;
    }

    .table tbody tr:hover {
        background-color: #e0f2fe;
    }

    .btn {
        border-radius: 10px;
        padding: 8px 14px;
        font-size: 0.9rem;
        font-weight: 500;
        transition: all 0.3s ease-in-out;
    }

    .btn-primary {
        background-color: var(--primary);
        border: none;
    }

    .btn-primary:hover {
        background-color: #1d4ed8;
        transform: scale(1.05);
    }

    .btn-secondary {
        background-color: var(--secondary);
        border: none;
    }

    .btn-secondary:hover {
        background-color: #2563eb;
        transform: scale(1.05);
    }

    .btn-danger {
        background-color: var(--danger);
        border: none;
    }

    .btn-danger:hover {
        background-color: #b91c1c;
        transform: scale(1.05);
    }

    .text-center.mt-4 .btn {
        margin: 8px 12px;
        font-size: 1rem;
        padding: 10px 20px;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes slideDown {
        from {
            opacity: 0;
            transform: translateY(-20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    /* Responsive table */
    @media screen and (max-width: 992px) {
        .table thead {
            display: none;
        }

        .table tr {
            display: block;
            margin-bottom: 1rem;
            background: var(--white);
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
            border-radius: 10px;
        }

        .table td {
            display: flex;
            justify-content: space-between;
            padding: 12px 16px;
            border-bottom: 1px solid #eee;
        }

        .table td::before {
            content: attr(data-label);
            font-weight: bold;
            color: var(--primary);
        }
    }
    .container {
    margin-top: 60px;         /* Đẩy xuống khỏi header */
    margin-left: 260px;       /* Đẩy sang phải khỏi sidebar */
    padding: 30px 40px;
    background-color: var(--white);
    border-radius: 20px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
    animation: fadeIn 0.7s ease-in-out;
}
.custom-header {
    text-align: center;
    font-size: 2rem;
    font-weight: 700;
    color: white;
    background: linear-gradient(135deg, var(--primary), var(--secondary));
    padding: 20px 0;
    border-radius: 16px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
    margin-bottom: 2rem;
    animation: slideIn 0.7s ease-in-out;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 14px;
}

.custom-header i {
    font-size: 2.2rem;
    color: var(--white);
    animation: floatIcon 2s infinite ease-in-out;
}

/* Hiệu ứng trượt nhẹ khi xuất hiện */
@keyframes slideIn {
    0% {
        opacity: 0;
        transform: translateY(-30px);
    }
    100% {
        opacity: 1;
        transform: translateY(0);
    }
}

/* Hiệu ứng nổi cho icon */
@keyframes floatIcon {
    0%, 100% {
        transform: translateY(0);
    }
    50% {
        transform: translateY(-6px);
    }
}
    
</style>
    
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
    </head>
    <body>
        <div class="container">
            <div class="custom-header">
  <i class="fas fa-file-contract"></i>
  <span>List of Contracts</span>
</div>


            <!-- Thông báo lỗi hiển thị NGAY SAU tiêu đề -->
            <c:if test="${not empty sessionScope.deleteError}">
                <div class="alert alert-danger text-center alert-custom">
                    ${sessionScope.deleteError}
                </div>
                <c:remove var="deleteError" scope="session"/>
            </c:if>

            <!-- Bảng hợp đồng -->
            <c:if test="${not empty contracts}">
                <table class="table table-bordered table-hover mt-3">
                    <thead class="table-dark">
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
                            <a href="<%= request.getContextPath()%>/Contracts?action=view&id=${contract.contractId}" class="btn btn-primary btn-sm mb-1">View</a>
                            <a href="<%= request.getContextPath()%>/Contracts?action=history&tenantId=${contract.tenantId}" class="btn btn-secondary btn-sm mb-1">View Contract History</a>
                            <form action="Contracts" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this contract?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="contractId" value="${contract.contractId}">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${empty contracts}">
                <p class="text-center text-danger mt-4">No contracts to display</p>
            </c:if>

            <!-- Nút tạo hợp đồng nằm dưới cùng -->
            <div class="text-center mt-4">
                <a href="<%= request.getContextPath()%>/Contracts?action=create" class="btn btn-primary">Create New Contract</a>
                <a href="<%= request.getContextPath()%>/admin/dashboard" class="btn btn-secondary">
                    ← Back to Dashboard
                </a>
            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
    