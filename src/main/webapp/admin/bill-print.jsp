<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, java.util.*, java.math.BigDecimal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    Bill bill = (Bill) request.getAttribute("bill");
    Contract contract = (Contract) request.getAttribute("contract");
    List<IncurredFee> incurredFees = (List<IncurredFee>) request.getAttribute("fees");
    Map<Integer, IncurredFeeType> feeTypeMap = (Map<Integer, IncurredFeeType>) request.getAttribute("feeTypeMap");
    List<UtilityReading> utilityReadings = (List<UtilityReading>) request.getAttribute("readings");
    Map<Integer, UtilityType> utilityTypeMap = (Map<Integer, UtilityType>) request.getAttribute("utilityTypeMap");

    BigDecimal roomRent = bill.getRoomRent() != null ? bill.getRoomRent() : BigDecimal.ZERO;
    BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;

    BigDecimal totalAmount = roomRent;
    for (UtilityReading ur : utilityReadings) {
        if (ur.getPriceUsed() != null) {
            totalAmount = totalAmount.add(ur.getPriceUsed());
        }
    }
    for (IncurredFee fee : incurredFees) {
        if (fee.getAmount() != null) {
            totalAmount = totalAmount.add(fee.getAmount());
        }
    }

    boolean isLastMonth = Boolean.TRUE.equals(request.getAttribute("isLastMonth"));
    BigDecimal dueAmount = isLastMonth ? totalAmount.subtract(deposit) : totalAmount;
%>

<html>
<head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f9fafb;
            font-family: "Segoe UI", sans-serif;
            padding: 30px;
        }

        .invoice-box {
            max-width: 850px;
            margin: auto;
            background: #fff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0,0,0,0.1);
        }

        .logo {
            height: 80px;
        }

        .invoice-title {
            font-size: 24px;
            font-weight: bold;
            color: #1e3a8a;
        }

        .info-row {
            display: flex;
            justify-content: space-between;
            margin: 25px 0;
        }

        .info-row p {
            margin: 4px 0;
        }

        .table th {
            background-color: #eff6ff;
            color: #1e3a8a;
        }

        .table td {
            color: #1e293b;
        }

        .total-row td {
            font-weight: bold;
            background-color: #e0f2fe;
        }

        .payment-info {
            margin-top: 40px;
            padding: 20px;
            background-color: #f1f5f9;
            border-radius: 8px;
        }

        .qr-code {
            width: 120px;
            height: auto;
        }

        @media print {
            .no-print {
                display: none;
            }
        }
        @page {
            margin: 1cm;
            size: auto;
        }

    </style>
</head>

<body onload="window.print()">
<div class="invoice-box">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <img src="${pageContext.request.contextPath}/img/logo.png" class="logo">
        <h2 class="invoice-title text-center flex-grow-1">HÓA ĐƠN TIỀN THUÊ NHÀ</h2>
    </div>

    <div class="info-row">
        <div>
            <p><strong>Room:</strong> <%= contract.getRoomNumber()%></p>
            <p><strong>Tenant:</strong> <%= contract.getTenantName()%></p>
            <p><strong>Phone:</strong> <%= contract.getPhone()%></p>
        </div>
        <div>
            <p><strong>Invoice date:</strong> <fmt:formatDate value="${bill.billDate}" pattern="dd/MM/yyyy"/></p>
            <p><strong>Invoice number:</strong> <%= bill.getBillID()%></p>
        </div>
    </div>

    <table class="table table-bordered">
        <thead>
        <tr>
                <th> Category</th>
            <th>Expense</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Room Price</td>
            <td><fmt:formatNumber value="<%= roomRent %>" type="currency" maxFractionDigits="0"/> đ</td>
        </tr>

        <c:forEach var="ur" items="${readings}">
            <c:set var="utype" value="${utilityTypeMap[ur.utilityTypeID]}" />
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${utype.utilityName eq 'Electricity'}">Electricity Price</c:when>
                        <c:when test="${utype.utilityName eq 'Water'}">Water Price</c:when>
                        <c:when test="${utype.utilityName eq 'Wifi'}">Wifi Price</c:when>
                        <c:when test="${utype.utilityName eq 'Trash'}">Trash Price</c:when>
                        <c:otherwise>Other utilities</c:otherwise>
                    </c:choose>
                    <c:if test="${utype.unit ne 'month'}">
                        (From <c:out value="${ur.oldReading}"/> To <c:out value="${ur.newReading}"/>)
                    </c:if>
                </td>
                <td><fmt:formatNumber value="${ur.priceUsed}" type="currency" maxFractionDigits="0"/> đ</td>
            </tr>
        </c:forEach>

        <c:forEach var="fee" items="${fees}">
            <c:set var="type" value="${feeTypeMap[fee.feeTypeID]}"/>
            <tr>
                <td><c:out value="${type.feeName}"/></td>
                <td><fmt:formatNumber value="${fee.amount}" type="currency" maxFractionDigits="0"/> đ</td>
            </tr>
        </c:forEach>

        <% if (isLastMonth) { %>
        <tr>
            <td>Deposit deducted</td>
            <td><fmt:formatNumber value="<%= deposit %>" type="currency" maxFractionDigits="0"/> đ</td>
        </tr>
        <% } %>

        <tr class="total-row">
            <td>Total</td>
            <td><fmt:formatNumber value="<%= totalAmount %>" type="currency" maxFractionDigits="0"/> đ</td>
        </tr>

        <tr class="total-row">
            <td>Amount receivable</td>
            <td><fmt:formatNumber value="<%= dueAmount %>" type="currency" maxFractionDigits="0"/> đ</td>
        </tr>

        <tr class="total-row">
            <td>Status</td>
            <td><strong><%= "PAID".equals(bill.getBillStatus()) ? "Đã thanh toán" : "Chưa thanh toán"%></strong></td>
        </tr>
        </tbody>
    </table>

    <div class="payment-info d-flex justify-content-between align-items-start">
    <div class="bank-details">
        <h5>Payment Information</h5>
        <p>
            <strong>Bank:</strong> MuoidiemprojectSWP<br>
            <strong>Account Holder:</strong> Group3_PRO<br>
            <strong>Account Number:</strong> 101010101010<br>
            <strong>Transfer Note:</strong> SV-P5 (Your Name - Room Number)
        </p>
    </div>
    <div class="text-center">
      <img src="${pageContext.request.contextPath}/img/qr_manifest_group3.png" alt="QR Code Group 3" width="200"/>

        <div>Scan the QR code to make a payment</div>
    </div>
</div>

</div>
</body>
</html>
