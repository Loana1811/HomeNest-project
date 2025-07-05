<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, java.util.*, java.math.BigDecimal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    Bill bill = (Bill) request.getAttribute("bill");
    Contract contract = (Contract) request.getAttribute("contract");
    BillDetail billDetail = (BillDetail) request.getAttribute("detail");
    List<IncurredFee> incurredFees = (List<IncurredFee>) request.getAttribute("fees");
    Map<Integer, IncurredFeeType> feeTypeMap = (Map<Integer, IncurredFeeType>) request.getAttribute("feeTypeMap");
    List<UtilityReading> utilityReadings = (List<UtilityReading>) request.getAttribute("readings");
    Map<Integer, UtilityType> utilityTypeMap = (Map<Integer, UtilityType>) request.getAttribute("utilityTypeMap");

    BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;
    BigDecimal totalAmount = BigDecimal.ZERO;

    totalAmount = totalAmount.add(BigDecimal.valueOf(billDetail.getRoomrent()));
    for (UtilityReading ur : utilityReadings) {
        if (ur.getPriceUsed() != null) totalAmount = totalAmount.add(ur.getPriceUsed());
    }
    for (IncurredFee fee : incurredFees) {
        if (fee.getAmount() != null) totalAmount = totalAmount.add(fee.getAmount());
    }
    BigDecimal dueAmount = totalAmount.subtract(deposit);
%>

<html>
<head>
    <title>Chi tiết hóa đơn</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body {
            background: #f5f5f5;
            font-family: Arial, sans-serif;
            padding: 20px;
        }
        .invoice-container {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 800px;
            margin: 0 auto;
        }
        .logo {
            max-width: 150px;
            margin-bottom: 20px;
        }
        .title {
            color: #1e3a8a;
            font-size: 24px;
            font-weight: bold;
            text-align: center;
            margin-bottom: 20px;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            color: #1e3a8a;
        }
        .table th, .table td {
            vertical-align: middle;
            color: #1e3a8a;
        }
        .table th {
            background-color: #e0e7ff;
            border-color: #1e3a8a;
        }
        .table td {
            border-color: #e0e7ff;
        }
        .total-row td {
            font-weight: bold;
            background-color: #dbeafe;
            color: #1e3a8a;
        }
        .payment-info {
            margin-top: 20px;
            padding: 15px;
            background-color: #f1f5f9;
            border-radius: 5px;
            text-align: center;
        }
        .qr-code {
            max-width: 100px;
            margin-top: 10px;
        }
        .bank-details {
            margin-top: 10px;
            color: #1e3a8a;
        }
    </style>
</head>
<body>
    <div class="invoice-container">
      <img src="${pageContext.request.contextPath}/img/logo.png" alt="HomeNest Logo" class="logo">
        <h2 class="title">HÓA ĐƠN TIỀN THUÊ NHÀ</h2>

        <div class="info-row">
            <div>
                <p><strong>Phòng:</strong> <%= contract.getRoomNumber() %></p>
                <p><strong>Khách thuê:</strong> <%= contract.getTenantName() %></p>
                <p><strong>Số điện thoại:</strong> <%= contract.getPhone() %></p>
            </div>
            <div>
                <p><strong>Ngày lập hóa đơn:</strong> <fmt:formatDate value="${bill.billDate}" pattern="dd/MM/yyyy"/></p>
                <p><strong>Số hóa đơn:</strong> <%= bill.getBillID() %></p>
            </div>
        </div>

        <table class="table table-bordered mt-4">
            <thead>
                <tr>
                    <th>Hạng mục</th>
                    <th>Chi phí</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Tiền phòng</td>
                    <td><fmt:formatNumber value="${detail.roomrent}" type="currency" maxFractionDigits="0"/> đ</td>
                </tr>

                <c:forEach var="ur" items="${readings}">
                    <c:set var="utype" value="${utilityTypeMap[ur.utilityTypeID]}" />
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${utype.utilityName eq 'Electricity'}">Tiền điện</c:when>
                                <c:when test="${utype.utilityName eq 'Water'}">Tiền nước</c:when>
                                <c:when test="${utype.utilityName eq 'Wifi'}">Tiền wifi</c:when>
                                <c:when test="${utype.utilityName eq 'Trash'}">Tiền rác</c:when>
                                <c:otherwise>Tiện ích khác</c:otherwise>
                            </c:choose>
                            <c:if test="${utype.unit ne 'month'}">
                                (Từ <c:out value="${ur.oldReading}"/> đến <c:out value="${ur.newReading}"/>)
                            </c:if>
                        </td>
                        <td><fmt:formatNumber value="${ur.priceUsed}" type="currency" maxFractionDigits="0"/> đ</td>
                    </tr>
                </c:forEach>

                <c:forEach var="fee" items="${fees}">
                    <tr>
                        <td>Phụ phí: <c:out value="${feeTypeMap[fee.incurredFeeTypeID].feeName}"/></td>
                        <td><fmt:formatNumber value="${fee.amount}" type="currency" maxFractionDigits="0"/> đ</td>
                    </tr>
                </c:forEach>

                <tr>
                    <td>Tiền cọc trừ</td>
                    <td><fmt:formatNumber value="<%= deposit %>" type="currency" maxFractionDigits="0"/> đ</td>
                </tr>

                <tr class="total-row">
                    <td>Tổng cộng</td>
                    <td><fmt:formatNumber value="<%= totalAmount %>" type="currency" maxFractionDigits="0"/> đ</td>
                </tr>
                <tr class="total-row">
                    <td>Số tiền phải thu</td>
                    <td><fmt:formatNumber value="<%= dueAmount %>" type="currency" maxFractionDigits="0"/> đ</td>
                </tr>
                <tr class="total-row">
                    <td>Trạng thái</td>
                    <td><strong><%= "PAID".equals(bill.getBillStatus()) ? "Đã thanh toán" : "Chưa thanh toán" %></strong></td>
                </tr>
            </tbody>
        </table>

        <div class="payment-info">
            <h4>Thông tin thanh toán</h4>
            <p class="bank-details">
                <strong>Ngân hàng:</strong> MuoidiemprojectSWP<br>
                <strong>Chủ tài khoản:</strong> Group3_PRO<br>
                <strong>Số tài khoản:</strong> 101010101010<br>
                <strong>Nội dung chuyển khoản:</strong> SV-P5 (Tên Nhờ - Số phòng)
            </p>
            <img src="${pageContext.request.contextPath}/img/qr_manifest_group3.png" alt="QR Code" class="qr-code">
            <p>Hoặc quét mã QR để thanh toán</p>
        </div>

        <a href="<%= request.getContextPath() %>/admin/bill?action=list" class="btn btn-primary mt-3">← Quay lại danh sách</a>
    </div>
</body>
</html>