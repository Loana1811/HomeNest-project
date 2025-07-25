<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.*" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi Tiết Khách Hàng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .container {
                max-width: 1200px;
                margin: 40px auto;
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                padding: 40px;
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                border: 1px solid rgba(255, 255, 255, 0.2);
            }

            h1 {
                color: #343a40;
                text-align: center;
                font-weight: 700;
                font-size: 2.5rem;
                margin-bottom: 40px;
                background: linear-gradient(135deg, #3498db, #17a2b8);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
            }

            h4, h5 {
                color: #343a40;
                border-bottom: 2px solid #dee2e6;
                padding-bottom: 10px;
                margin-bottom: 20px;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .card {
                border: none;
                box-shadow: 0 3px 8px rgba(0,0,0,0.05);
                margin-bottom: 30px;
                background-color: #fff;
                border-radius: 15px;
                overflow: hidden;
            }

            .card-body {
                padding: 25px;
            }

            .table {
                width: 100%;
                margin-bottom: 0;
                border-collapse: collapse;
                font-size: 0.9rem;
            }

            .table th {
                background-color: #007bff;
                color: white;
                text-align: left;
                padding: 12px;
            }

            .table td, .table th {
                padding: 12px;
                vertical-align: middle;
                border: 1px solid #dee2e6;
                text-align: left;
            }

            .table-striped tbody tr:nth-of-type(odd) {
                background-color: rgba(0, 0, 0, .05);
            }

            .alert-info {
                background-color: #e7f1ff;
                color: #31708f;
                border: 1px solid #bce8f1;
                padding: 15px;
                text-align: center;
                border-radius: 8px;
                margin-bottom: 20px;
            }

            ul {
                list-style-type: none;
                padding-left: 0;
                margin: 0;
            }

            ul li {
                margin-bottom: 15px;
                font-size: 1.1rem;
                display: flex;
                justify-content: space-between;
                border-bottom: 1px solid #eee;
                padding-bottom: 10px;
            }

            ul li b {
                color: #495057;
                width: 40%;
            }

            .btn-secondary {
                background-color: #6c757d;
                border-color: #6c757d;
                font-size: 14px;
                margin-top: 20px;
                display: block;
                width: 200px;
                margin: 20px auto 0;
                text-align: center;
            }

            .btn-secondary:hover {
                background-color: #5a6268;
                border-color: #545b62;
            }

            @media (max-width: 768px) {
                .container {
                    padding: 20px;
                }
                .table {
                    font-size: 0.8rem;
                }
                ul li {
                    flex-direction: column;
                }
                ul li b {
                    width: 100%;
                }
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Chi Tiết Khách Hàng</h1>
            <c:choose>
                <c:when test="${not empty details}">
                    <%
                        List<Map<String, Object>> details = (List<Map<String, Object>>) request.getAttribute("details");
                        Map<String, Object> customerRow = details != null && !details.isEmpty() ? details.get(0) : null;

                        // Chuẩn bị các set chứa ID duy nhất cho từng bảng
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
                    <!-- Thông Tin Khách Hàng -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h4><i class="fas fa-user me-2"></i>Thông Tin Khách Hàng</h4>
                            <ul>
                                <li><b>ID:</b> <%= customerRow != null ? customerRow.get("CustomerID") : "" %></li>
                                <li><b>Họ và Tên:</b> <%= customerRow != null ? customerRow.get("CustomerFullName") : "" %></li>
                                <li><b>Số Điện Thoại:</b> <%= customerRow != null ? customerRow.get("PhoneNumber") : "" %></li>
                                <li><b>CCCD:</b> <%= customerRow != null ? customerRow.get("CCCD") : "" %></li>
                                <li><b>Giới Tính:</b> <%= customerRow != null ? customerRow.get("Gender") : "" %></li>
                                <li><b>Ngày Sinh:</b> <%= customerRow != null ? customerRow.get("BirthDate") : "" %></li>
                                <li><b>Địa Chỉ:</b> <%= customerRow != null ? customerRow.get("Address") : "" %></li>
                                <li><b>Email:</b> <%= customerRow != null ? customerRow.get("Email") : "" %></li>
                                <li><b>Trạng Thái:</b> 
                                    <% 
                                    Object customerStatus = customerRow != null ? customerRow.get("CustomerStatus") : null;
                                    String customerStatusStr = "N/A";
                                    if (customerStatus != null) {
                                        String status = (String) customerStatus;
                                        if ("Active".equals(status)) {
                                            customerStatusStr = "Hoạt động";
                                        } else if ("Disable".equals(status)) {
                                            customerStatusStr = "Vô hiệu hóa";
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
                            <h5><i class="fas fa-user-group me-2"></i>Người Thuê</h5>
                            <% if (tenantIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có người thuê</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID Người Thuê</th><th>Ngày Tham Gia</th>
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
                            <h5><i class="fas fa-file-contract me-2"></i>Hợp Đồng</h5>
                            <% if (contractIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có hợp đồng</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID Hợp Đồng</th><th>ID Phòng</th><th>Ngày Bắt Đầu</th><th>Ngày Kết Thúc</th><th>Trạng Thái</th><th>Ngày Tạo</th><th>Tiền Cọc</th>
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
                            <h5><i class="fas fa-building me-2"></i>Phòng và Khu</h5>
                            <% if (contractIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có phòng hoặc khu liên quan</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Số Phòng</th><th>Giá Thuê</th><th>Diện Tích</th><th>Trạng Thái Phòng</th>
                                        <th>Tên Khu</th><th>Số Phòng Tối Đa</th><th>Số Phòng Hiện Có</th><th>Trạng Thái Khu</th>
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
                                        <td><%= row.get("MaxRoom") != null ? row.get("MaxRoom") : "N/A" %></td>
                                        <td><%= row.get("RoomCount") != null ? row.get("RoomCount") : "N/A" %></td>
                                        <td>
                                            <% 
                                            Object blockStatus = row.get("BlockStatus");
                                            String blockStatusStr = "N/A";
                                            if (blockStatus != null) {
                                                String status = (String) blockStatus;
                                                if ("Active".equals(status)) {
                                                    blockStatusStr = "Hoạt động";
                                                } else {
                                                    blockStatusStr = status; // Giữ nguyên nếu không khớp
                                                }
                                            }
                                            %>
                                            <%= blockStatusStr %>
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

                    <!-- Hóa Đơn -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5><i class="fas fa-file-invoice-dollar me-2"></i>Hóa Đơn</h5>
                            <% if (billIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có hóa đơn</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID Hóa Đơn</th><th>Ngày Hóa Đơn</th><th>Tổng Số Tiền</th><th>Trạng Thái Hóa Đơn</th>
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
                            <h5><i class="fas fa-home me-2"></i>Yêu Cầu Thuê</h5>
                            <% if (requestIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có yêu cầu thuê</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID Yêu Cầu</th><th>Ngày Yêu Cầu</th><th>Trạng Thái Yêu Cầu</th><th>Người Duyệt</th><th>Ngày Duyệt</th>
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
                            <h5><i class="fas fa-file-alt me-2"></i>Báo Cáo</h5>
                            <% if (reportIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có báo cáo</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID Báo Cáo</th><th>Mô Tả Vấn Đề</th><th>Trạng Thái Báo Cáo</th><th>Ngày Tạo Báo Cáo</th><th>Người Giải Quyết</th><th>Ngày Giải Quyết</th>
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
                            <h5><i class="fas fa-bell me-2"></i>Thông Báo</h5>
                            <% if (notificationIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có thông báo</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID Thông Báo</th><th>Tiêu Đề</th><th>Nội Dung</th><th>Ngày Tạo</th><th>Người Gửi</th><th>Liên Kết Chuyển Hướng</th>
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
                                        <td><%= row.get("RedirectUrl") != null ? row.get("RedirectUrl") : "N/A" %></td>
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
                            <h5><i class="fas fa-envelope me-2"></i>Nhật Ký Email</h5>
                            <% if (emailLogIds.isEmpty()) { %>
                            <div class="alert alert-info">Chưa có nhật ký email</div>
                            <% } else { %>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID Nhật Ký Email</th><th>Chủ Đề</th><th>Nội Dung</th><th>Thời Gian Gửi</th><th>Trạng Thái</th>
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
                    <div class="alert alert-info">Không tìm thấy chi tiết của khách hàng này.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>