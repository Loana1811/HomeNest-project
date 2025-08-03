    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@page import="java.util.*" %>
    <%
    String ctx = request.getContextPath();
    %>

    <!DOCTYPE html>
    <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <title>Chi Tiết Khách Hàng</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
:root {
  --primary-color: #1e3a8a;
  --secondary-color: #3b82f6;
  --tertiary-color: #60a5fa;
  --light-color: #dbeafe;
  --background-color: #f4f7fb;
  --text-color: #1f2937;
  --white: #ffffff;
  --shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  --border-radius: 14px;
  --transition: all 0.3s ease-in-out;
}

body {
  font-family: 'Segoe UI', sans-serif;
  background-color: var(--background-color);
  color: var(--text-color);
  margin: 0;
  padding: 0;
}

/* Main Layout */
.main-content {
  margin-left: 250px;
  padding: 40px;
  background-color: var(--background-color);
  min-height: 100vh;
}

.container {
  background-color: var(--white);
  border-radius: var(--border-radius);
  padding: 30px;
  box-shadow: var(--shadow);
  animation: fadeIn 0.4s ease-in-out;
}

/* Heading */
h1 {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 30px;
  color: var(--primary-color);
}

/* Section Headers */
.card-body h4,
.card-body h5 {
  color: var(--primary-color);
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.card-body h5 i,
.card-body h4 i {
  margin-right: 10px;
}

/* Card */
.card {
  background-color: var(--white);
  border: none;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  transition: var(--transition);
  margin-bottom: 25px;
}

.card:hover {
  transform: scale(1.01);
}

/* Alert Info */
.alert-info {
  background-color: #e0edff;
  color: #1e3a8a;
  padding: 14px 18px;
  border-left: 5px solid var(--primary-color);
  border-radius: var(--border-radius);
  font-size: 14.5px;
  box-shadow: var(--shadow);
}

/* Table */
.table {
  width: 100%;
  border-collapse: collapse;
  border-radius: var(--border-radius);
  overflow: hidden;
  margin-top: 10px;
}

.table thead {
  background-color: var(--primary-color);
  color: white;
  text-transform: uppercase;
  font-size: 14px;
}

.table thead th {
  padding: 12px;
  border-bottom: 1px solid #ccc;
}

.table-striped tbody tr:nth-of-type(odd) {
  background-color: #f0f7ff;
}

.table td {
  padding: 12px;
  vertical-align: middle;
  font-size: 14px;
  color: #374151;
}

/* Icon Styles */
i.text-secondary {
  color: #6c757d;
}

/* Badge status */
.badge {
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 20px;
}

.badge.bg-success {
  background-color: #28a745;
  color: white;
}

.badge.bg-danger {
  background-color: #dc3545;
  color: white;
}

.badge.bg-warning {
  background-color: #ffc107;
  color: #212529;
}

/* Animation */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>




        </head>
        <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
        <body>
            <div class="main-content">
            <div class="container">
              <h1>Customer Details</h1>
<c:choose>
    <c:when test="${not empty details}">
        <%
            List<Map<String, Object>> details = (List<Map<String, Object>>) request.getAttribute("details");
            Map<String, Object> customerRow = details != null && !details.isEmpty() ? details.get(0) : null;

            // Prepare unique ID sets for each table
            Set<Integer> tenantIds = new LinkedHashSet<>();
            Set<Integer> contractIds = new LinkedHashSet<>();
            Set<Integer> billIds = new LinkedHashSet<>();
            Set<Integer> requestIds = new LinkedHashSet<>();
            Set<Integer> reportIds = new LinkedHashSet<>();
            Set<Integer> notificationIds = new LinkedHashSet<>();
            Set<Integer> emailLogIds = new LinkedHashSet<>();

            for (Map<String, Object> row : details) {
                if (row.get("TenantID") != null) tenantIds.add((Integer) row.get("TenantID"));
                if (row.get("ContractID") != null) contractIds.add((Integer) row.get("ContractID"));
                if (row.get("BillID") != null) billIds.add((Integer) row.get("BillID"));
                if (row.get("RequestID") != null) requestIds.add((Integer) row.get("RequestID"));
                if (row.get("ReportID") != null) reportIds.add((Integer) row.get("ReportID"));
                if (row.get("NotificationID") != null) notificationIds.add((Integer) row.get("NotificationID"));
                if (row.get("EmailLogID") != null) emailLogIds.add((Integer) row.get("EmailLogID"));
            }
        %>
        <!-- Customer Information -->
        <div class="card mb-3">
            <div class="card-body">
                <h4><i class="fas fa-user me-2"></i>Customer Information</h4>
                <ul>
                    <li><b>ID:</b> <%= customerRow != null ? customerRow.get("CustomerID") : "" %></li>
                    <li><b>Full Name:</b> <%= customerRow != null ? customerRow.get("CustomerFullName") : "" %></li>
                    <li><b>Phone Number:</b> <%= customerRow != null ? customerRow.get("PhoneNumber") : "" %></li>
                    <li><b>National ID:</b> <%= customerRow != null ? customerRow.get("CCCD") : "" %></li>
                    <li><b>Gender:</b> <%= customerRow != null ? customerRow.get("Gender") : "" %></li>
                    <li><b>Date of Birth:</b> <%= customerRow != null ? customerRow.get("BirthDate") : "" %></li>
                    <li><b>Address:</b> <%= customerRow != null ? customerRow.get("Address") : "" %></li>
                    <li><b>Email:</b> <%= customerRow != null ? customerRow.get("Email") : "" %></li>
                    <li><b>Status:</b> 
                        <% 
                        Object customerStatus = customerRow != null ? customerRow.get("CustomerStatus") : null;
                        String customerStatusStr = "N/A";
                        if (customerStatus != null) {
                            String status = (String) customerStatus;
                            if ("Active".equals(status)) {
                                customerStatusStr = "Active";
                            } else if ("Disable".equals(status)) {
                                customerStatusStr = "Disabled";
                            }
                        }
                        %>
                        <%= customerStatusStr %>
                    </li>
                </ul>
            </div>
        </div>

                        <!-- Người Thuê -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5><i class="fas fa-user-group me-2"></i>Tenant</h5>
                                <% if (tenantIds.isEmpty()) { %>
                                <div class="alert alert-info">There are currently no tenants.</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID Tenant</th><th>StartDate</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer tid : tenantIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (tid.equals(row.get("TenantID"))) { %>
                                        <tr>
                                            <td><%= row.get("TenantID") %></td>
                                            <td><%= row.get("JoinDate") != null ? row.get("JoinDate") : "N/A" %></td>
                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>

                        <!-- Hợp Đồng -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5><i class="fas fa-file-contract me-2"></i>Contract</h5>
                                <% if (contractIds.isEmpty()) { %>
                                <div class="alert alert-info">No contract yet</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID Contract</th><th>ID Room</th><th>StartDate</th><th>EndDate</th><th>Status</th><th>Date Created</th><th>Deposit</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer cid : contractIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (cid.equals(row.get("ContractID"))) { %>
                                        <tr>
                                            <td><%= row.get("ContractID") %></td>
                                            <td><%= row.get("RoomID") != null ? row.get("RoomID") : "N/A" %></td>
                                            <td><%= row.get("StartDate") != null ? row.get("StartDate") : "N/A" %></td>
                                            <td><%= row.get("EndDate") != null ? row.get("EndDate") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object contractStatus = row.get("ContractStatus");
                                                String contractStatusStr = "N/A";
                                                if (contractStatus != null) {
                                                    String status = (String) contractStatus;
                                                    if ("Active".equals(status)) {
                                                        contractStatusStr = "Hoạt động";
                                                    } else if ("Pending".equals(status)) {
                                                        contractStatusStr = "Đang chờ";
                                                    } else if ("Terminated".equals(status)) {
                                                        contractStatusStr = "Đã chấm dứt";
                                                    } else if ("Expired".equals(status)) {
                                                        contractStatusStr = "Đã hết hạn";
                                                    } else if ("Ended".equals(status)) {
                                                        contractStatusStr = "Đã kết thúc";
                                                    }
                                                }
                                                %>
                                                <%= contractStatusStr %>
                                            </td>
                                            <td><%= row.get("ContractCreatedAt") != null ? row.get("ContractCreatedAt") : "N/A" %></td>
                                            <td><%= row.get("Deposit") != null ? row.get("Deposit") : "N/A" %></td>
                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>

                        <!-- Phòng và Khu -->
                        <div class="card mb-3">
                            <div class="card-body">
                                    <h5><i class="fas fa-building me-2"></i>Room and Block</h5>
                                <% if (contractIds.isEmpty()) { %>
                                <div class="alert alert-info">No related rooms or block yet.</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>Room Number</th><th>Rent</th><th>Acreage</th><th>Room Status</th>
                                            <th>BlockName</th><th>Number of available rooms</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer cid : contractIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (cid.equals(row.get("ContractID"))) { %>
                                        <tr>
                                            <td><%= row.get("RoomNumber") != null ? row.get("RoomNumber") : "N/A" %></td>
                                            <td><%= row.get("RentPrice") != null ? row.get("RentPrice") : "N/A" %></td>
                                            <td><%= row.get("Area") != null ? row.get("Area") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object roomStatus = row.get("RoomStatus");
                                                String roomStatusStr = "N/A";
                                                if (roomStatus != null) {
                                                    String status = (String) roomStatus;
                                                    if ("Available".equals(status)) {
                                                        roomStatusStr = "Có sẵn";
                                                    } else if ("Occupied".equals(status)) {
                                                        roomStatusStr = "Đã thuê";
                                                    }
                                                }
                                                %>
                                                <%= roomStatusStr %>
                                            </td>

                                            <td><%= row.get("BlockName") != null ? row.get("BlockName") : "N/A" %></td>

                                            <td><%= row.get("RoomCount") != null ? row.get("RoomCount") : "N/A" %></td>

                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>

                        <!-- Hóa Đơn -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5><i class="fas fa-file-invoice-dollar me-2"></i>Bill</h5>
                                <% if (billIds.isEmpty()) { %>
                                <div class="alert alert-info">No invoice yet</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID Bill</th><th>Bill Date</th><th>Total Amount</th><th>Invoice Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer bid : billIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (bid.equals(row.get("BillID"))) { %>
                                        <tr>
                                            <td><%= row.get("BillID") %></td>
                                            <td><%= row.get("BillDate") != null ? row.get("BillDate") : "N/A" %></td>
                                            <td><%= row.get("TotalAmount") != null ? row.get("TotalAmount") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object billStatus = row.get("BillStatus");
                                                String billStatusStr = "N/A";
                                                if (billStatus != null) {
                                                    String status = (String) billStatus;
                                                    if ("Cancel".equals(status)) {
                                                        billStatusStr = "Đã hủy";
                                                    } else if ("Unpaid".equals(status)) {
                                                        billStatusStr = "Chưa thanh toán";
                                                    } else if ("Paid".equals(status)) {
                                                        billStatusStr = "Đã thanh toán";
                                                    }
                                                }
                                                %>
                                                <%= billStatusStr %>
                                            </td>

                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>

                        <!-- Yêu Cầu Thuê -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5><i class="fas fa-home me-2"></i>Rental Request</h5>
                                <% if (requestIds.isEmpty()) { %>
                                <div class="alert alert-info">No rental requests yet</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>Request ID</th><th>Request Date</th><th>Request Status</th><th>By</th><th>Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer rqid : requestIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (rqid.equals(row.get("RequestID"))) { %>
                                        <tr>
                                            <td><%= row.get("RequestID") %></td>
                                            <td><%= row.get("RequestDate") != null ? row.get("RequestDate") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object requestStatus = row.get("RequestStatus");
                                                String requestStatusStr = "N/A";
                                                if (requestStatus != null) {
                                                    String status = (String) requestStatus;
                                                    if ("Expired".equals(status)) {
                                                        requestStatusStr = "Đã hết hạn";
                                                    } else if ("Rejected".equals(status)) {
                                                        requestStatusStr = "Bị từ chối";
                                                    } else if ("Approved".equals(status)) {
                                                        requestStatusStr = "Đã phê duyệt";
                                                    } else if ("Pending".equals(status)) {
                                                        requestStatusStr = "Đang chờ";
                                                    }
                                                }
                                                %>
                                                <%= requestStatusStr %>
                                            </td>
                                            <td>
                                                <% 
                                                Object approvedBy = row.get("ApprovedBy");
                                                String approvedByStr = "N/A";
                                                if (approvedBy != null) {
                                                    int id = (Integer) approvedBy;
                                                    if (id == 1) {
                                                        approvedByStr = "admin";
                                                    } else if (id == 2) {
                                                        approvedByStr = "manager";
                                                    }
                                                }
                                                %>
                                                <%= approvedByStr %>
                                            </td>
                                            <td><%= row.get("ApprovedDate") != null ? row.get("ApprovedDate") : "N/A" %></td>
                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>

                        <!-- Báo Cáo -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5><i class="fas fa-file-alt me-2"></i>Report</h5>
                                <% if (reportIds.isEmpty()) { %>
                                <div class="alert alert-info">Not Report</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID Report</th><th>Problem description</th><th>Report Status</th><th>Report creation date</th><th>Resolver</th><th>Resolution date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer repid : reportIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (repid.equals(row.get("ReportID"))) { %>
                                        <tr>
                                            <td><%= row.get("ReportID") %></td>
                                            <td><%= row.get("IssueDescription") != null ? row.get("IssueDescription") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object reportStatus = row.get("ReportStatus");
                                                String reportStatusStr = "N/A";
                                                if (reportStatus != null) {
                                                    String status = (String) reportStatus;
                                                    if ("Closed".equals(status)) {
                                                        reportStatusStr = "Đã đóng";
                                                    } else if ("Resolved".equals(status)) {
                                                        reportStatusStr = "Đã giải quyết";
                                                    } else if ("InProgress".equals(status)) {
                                                        reportStatusStr = "Đang tiến hành";
                                                    } else if ("Pending".equals(status)) {
                                                        reportStatusStr = "Đang chờ";
                                                    }
                                                }
                                                %>
                                                <%= reportStatusStr %>
                                            </td>
                                            <td><%= row.get("ReportCreatedAt") != null ? row.get("ReportCreatedAt") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object resolvedBy = row.get("ResolvedBy");
                                                String resolvedByStr = "N/A";
                                                if (resolvedBy != null) {
                                                    int id = (Integer) resolvedBy;
                                                    if (id == 1) {
                                                        resolvedByStr = "admin";
                                                    } else if (id == 2) {
                                                        resolvedByStr = "manager";
                                                    }
                                                }
                                                %>
                                                <%= resolvedByStr %>
                                            </td>
                                            <td><%= row.get("ResolvedDate") != null ? row.get("ResolvedDate") : "N/A" %></td>
                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>

                        <!-- Thông Báo -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5><i class="fas fa-bell me-2"></i>Notification</h5>
                                <% if (notificationIds.isEmpty()) { %>
                                <div class="alert alert-info">No announcements yet</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID Notification</th><th>Title</th><th>Description</th><th>Creation date</th><th>Sender</th><th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer nid : notificationIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (nid.equals(row.get("NotificationID"))) { %>
                                        <tr>
                                            <td><%= row.get("NotificationID") %></td>
                                            <td><%= row.get("NotificationTitle") != null ? row.get("NotificationTitle") : "N/A" %></td>
                                            <td><%= row.get("NotificationMessage") != null ? row.get("NotificationMessage") : "N/A" %></td>

                                            <td><%= row.get("NotificationCreatedAt") != null ? row.get("NotificationCreatedAt") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object sentBy = row.get("NotificationSentBy");
                                                String sentByStr = "N/A";
                                                if (sentBy != null) {
                                                    int id = (Integer) sentBy;
                                                    if (id == 1) {
                                                        sentByStr = "admin";
                                                    } else if (id == 2) {
                                                        sentByStr = "manager";
                                                    }
                                                }
                                                %>
                                                <%= sentByStr %>
                                            </td>

                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>

                        <!-- Nhật Ký Email -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5><i class="fas fa-envelope me-2"></i>Email Log</h5>
                                <% if (emailLogIds.isEmpty()) { %>
                                <div class="alert alert-info">No email log yet</div>
                                <% } else { %>
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>Email Log ID</th><th>Title</th><th>Description</th><th>Time</th><th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Integer elid : emailLogIds) { %>
                                        <% for (Map<String, Object> row : details) { %>
                                        <% if (elid.equals(row.get("EmailLogID"))) { %>
                                        <tr>
                                            <td><%= row.get("EmailLogID") %></td>
                                            <td><%= row.get("EmailSubject") != null ? row.get("EmailSubject") : "N/A" %></td>
                                            <td><%= row.get("EmailMessage") != null ? row.get("EmailMessage") : "N/A" %></td>
                                            <td><%= row.get("SentAt") != null ? row.get("SentAt") : "N/A" %></td>
                                            <td>
                                                <% 
                                                Object emailStatus = row.get("EmailStatus");
                                                String emailStatusStr = "N/A";
                                                if (emailStatus != null) {
                                                    String status = (String) emailStatus;
                                                    if ("Failed".equals(status)) {
                                                        emailStatusStr = "Thất bại";
                                                    } else if ("Sent".equals(status)) {
                                                        emailStatusStr = "Đã gửi";
                                                    }
                                                }
                                                %>
                                                <%= emailStatusStr %>
                                            </td>

                                        </tr>
                                        <% break; } %>
                                        <% } %>
                                        <% } %>
                                    </tbody>
                                </table>
                                <% } %>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">No details found for this customer.   </div>
                    </c:otherwise>
                </c:choose>
            </div>
            </div>
        </body>
    </html>