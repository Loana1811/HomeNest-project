<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, java.util.*, java.math.BigDecimal, java.util.Base64" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="vi_VN"/>

<%
    Bill bill = (Bill) request.getAttribute("bill");
    Contract contract = (Contract) request.getAttribute("contract");
    List<IncurredFee> incurredFees = (List<IncurredFee>) request.getAttribute("fees");
    Map<Integer, IncurredFeeType> feeTypeMap = (Map<Integer, IncurredFeeType>) request.getAttribute("feeTypeMap");
    List<UtilityReading> utilityReadings = (List<UtilityReading>) request.getAttribute("readings");
    Map<Integer, UtilityType> utilityTypeMap = (Map<Integer, UtilityType>) request.getAttribute("utilityTypeMap");
    List<PaymentConfirmation> confirmations = (List<PaymentConfirmation>) request.getAttribute("confirmations");
    String userRole = (String) request.getAttribute("userRole");
    String success = (String) session.getAttribute("success");
    String error = (String) session.getAttribute("error");
    session.removeAttribute("success");
    session.removeAttribute("error");
%>

<html>
<head>
    <title>Bill Details</title>
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
        .upload-form {
            margin-top: 20px;
            padding: 15px;
            background-color: #f1f5f9;
            border-radius: 5px;
        }
        .proof-image {
            max-width: 200px;
            margin-top: 10px;
        }
        .alert {
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="invoice-container">
        <img src="${pageContext.request.contextPath}/img/logo.png" alt="HomeNest Logo" class="logo">
        <h2 class="title">RENT BILL</h2>

        <% if (success != null) { %>
            <div class="alert alert-success" role="alert"><%= success %></div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert"><%= error %></div>
        <% } %>

        <div class="info-row">
            <div>
                <p><strong>Room:</strong> <%= bill.getRoomNumber() %></p>
                <p><strong>Tenant:</strong> <%= contract.getTenantName() %></p>
                <p><strong>Phone:</strong> <%= contract.getPhone() %></p>
            </div>
            <div>
                <p><strong>Bill date:</strong> <fmt:formatDate value="${bill.billDate}" pattern="dd/MM/yyyy"/></p>
                <p><strong>Bill number:</strong> <%= bill.getBillID() %></p>
            </div>
        </div>

        <table class="table table-bordered mt-4">
            <thead>
                <tr>
                    <th>Item</th>
                    <th>Cost</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${bill.roomRent > 0}">
                    <tr>
                        <td>Room rent</td>
                        <td><fmt:formatNumber value="${bill.roomRent}" type="number" maxFractionDigits="0"/> đ</td>
                    </tr>
                </c:if>
                <c:forEach var="ur" items="${readings}">
                    <c:set var="utype" value="${utilityTypeMap[ur.utilityTypeID]}" />
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${utype.utilityName eq 'Electricity'}">Electricity Amount</c:when>
                                <c:when test="${utype.utilityName eq 'Water'}">Water Amount</c:when>
                                <c:when test="${utype.utilityName eq 'Wifi'}">Wifi Amount</c:when>
                                <c:when test="${utype.utilityName eq 'Trash'}">Trash Amount</c:when>
                                <c:otherwise>Other utilities</c:otherwise>
                            </c:choose>
                            <c:if test="${utype.unit ne 'month'}">
                                (From <c:out value="${ur.oldReading}"/> to <c:out value="${ur.newReading}"/>)
                            </c:if>
                        </td>
                        <td><fmt:formatNumber value="${ur.priceUsed}" type="number" maxFractionDigits="0"/> đ</td>
                    </tr>
                </c:forEach>
                <c:if test="${isLastMonth}">
                    <tr>
                        <td>Deposit deducted</td>
                        <td><fmt:formatNumber value="${contract.deposit}" type="number" maxFractionDigits="0"/> đ</td>
                    </tr>
                </c:if>
                <tr class="total-row">
                    <td>Total</td>
                    <td><fmt:formatNumber value="${bill.totalAmount}" type="number" maxFractionDigits="0"/> đ</td>
                </tr>
                <tr class="total-row">
                    <td>Amount receivable</td>
                 <td><fmt:formatNumber value="${amountDue}" type="number" maxFractionDigits="0"/> đ</td>
                </tr>
                <tr class="total-row">
                    <td>Status</td>
                    <td><strong><%= "PAID".equals(bill.getBillStatus()) ? "PAID" : "UNPAID" %></strong></td>
                </tr>
            </tbody>
        </table>

        <div class="payment-info">
            <h4>Payment information</h4>
            <p class="bank-details">
                <strong>Bank:</strong> MuoidiemprojectSWP<br>
                <strong>Account holder:</strong> Group3_PRO<br>
                <strong>Account number:</strong> 101010101010<br>
                <strong>Transfer content:</strong> SV-P5 (House name - Room number)
            </p>
            <img src="${pageContext.request.contextPath}/img/qr_manifest_group3.png" alt="QR Code" class="qr-code">
            <p>Or scan the QR code to pay</p>
        </div>

        <% if ("admin".equals(userRole) || "manager".equals(userRole)) { %>
            <% if (!"PAID".equals(bill.getBillStatus())) { %>
                <form action="${pageContext.request.contextPath}/admin/bill" method="post" class="mt-3">
                    <input type="hidden" name="action" value="markPaid">
                    <input type="hidden" name="billID" value="${bill.billID}">
                    <button type="submit" class="btn btn-success">✔ Mark as Paid</button>
                </form>
            <% } %>
        <% } else if ("customer".equals(userRole)) { %>
            <div class="upload-form">
                <h4>Upload Payment Proof</h4>
                <form action="${pageContext.request.contextPath}/admin/bill" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="uploadProof">
                    <input type="hidden" name="billId" value="${bill.billID}">
                    <div class="mb-3">
                        <label for="proofImage" class="form-label">Select image file (JPEG/PNG):</label>
                        <input type="file" class="form-control" id="proofImage" name="proofImage" accept="image/jpeg,image/png" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Upload</button>
                </form>

                <c:if test="${not empty confirmations}">
                    <h5 class="mt-3">Uploaded Proofs:</h5>
                    <c:forEach var="conf" items="${confirmations}">
                        <div class="card mb-2">
                            <div class="card-body">
                                <p>Uploaded at: <fmt:formatDate value="${conf.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></p>
                                <img src="data:image/jpeg;base64,<%= Base64.getEncoder().encodeToString(((PaymentConfirmation) pageContext.getAttribute("conf")).getImageData()) %>" alt="Proof Image" class="proof-image">
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        <% } %>
    </div>
</body>
</html>