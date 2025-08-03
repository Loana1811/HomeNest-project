<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, java.util.*, java.math.BigDecimal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="vi_VN"/>

<%
    List<Bill> tenantBills = (List<Bill>) request.getAttribute("tenantBills");
    List<PaymentConfirmation> confirmations = (List<PaymentConfirmation>) request.getAttribute("confirmations");
    Bill bill = (Bill) request.getAttribute("bill");
    String success = (String) session.getAttribute("success");
    String error = (String) session.getAttribute("error");
    session.removeAttribute("success");
    session.removeAttribute("error");
%>

<html>
<head>
    <title>Hóa đơn của bạn</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body { background: #f5f5f5; font-family: Arial, sans-serif; padding: 20px; }
        .invoice-container { background: #fff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 30px; max-width: 800px; margin: 0 auto; }
        .title { color: #1e3a8a; font-size: 24px; font-weight: bold; text-align: center; margin-bottom: 20px; }
        .info-row { display: flex; justify-content: space-between; margin-bottom: 10px; color: #1e3a8a; }
        .table th, .table td { vertical-align: middle; color: #1e3a8a; }
        .table th { background-color: #e0e7ff; border-color: #1e3a8a; }
        .table td { border-color: #e0e7ff; }
        .total-row td { font-weight: bold; background-color: #dbeafe; color: #1e3a8a; }
        .upload-form { margin-top: 20px; padding: 15px; background-color: #f1f5f9; border-radius: 5px; }
        .alert { margin-top: 10px; }
        .proof-image { max-width: 200px; margin-top: 10px; }
    </style>
</head>
<body>
    <div class="invoice-container">
        <h2 class="title">Hóa đơn của bạn</h2>

        <% if (success != null) { %>
            <div class="alert alert-success" role="alert"><%= success %></div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert"><%= error %></div>
        <% } %>

        <% if (bill != null) { %>
            <div class="info-row">
                <div>
                    <p><strong>Phòng:</strong> <%= bill.getRoomNumber() %></p>
                    <p><strong>Tenant:</strong> <%= bill.getTenantName() %></p>
                    <p><strong>Số điện thoại:</strong> <%= bill.getPhone() %></p>
                </div>
                <div>
                    <p><strong>Ngày hóa đơn:</strong> <fmt:formatDate value="${bill.billDate}" pattern="dd/MM/yyyy"/></p>
                    <p><strong>Số hóa đơn:</strong> <%= bill.getBillID() %></p>
                </div>
            </div>

            <table class="table table-bordered mt-4">
                <thead>
                    <tr>
                        <th>Mục</th>
                        <th>Chi phí</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${bill.roomRent > 0}">
                        <tr>
                            <td>Tiền phòng</td>
                            <td><fmt:formatNumber value="${bill.roomRent}" type="number" maxFractionDigits="0"/> đ</td>
                        </tr>
                    </c:if>
                    <c:forEach var="ur" items="${bill.utilityReadings}">
                        <c:set var="utype" value="${utilityTypeMap[ur.utilityTypeID]}" />
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${utype.utilityName eq 'Electricity'}">Tiền điện</c:when>
                                    <c:when test="${utype.utilityName eq 'Water'}">Tiền nước</c:when>
                                    <c:when test="${utype.utilityName eq 'Wifi'}">Tiền WiFi</c:when>
                                    <c:when test="${utype.utilityName eq 'Trash'}">Tiền rác</c:when>
                                    <c:otherwise>Khác</c:otherwise>
                                </c:choose>
                                <c:if test="${utype.unit ne 'month'}">
                                    (Từ <c:out value="${ur.oldReading}"/> đến <c:out value="${ur.newReading}"/>)
                                </c:if>
                            </td>
                            <td><fmt:formatNumber value="${ur.priceUsed}" type="number" maxFractionDigits="0"/> đ</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${bill.isLastMonth}">
                        <tr>
                            <td>Tiền cọc đã trừ</td>
                            <td><fmt:formatNumber value="${bill.deposit}" type="number" maxFractionDigits="0"/> đ</td>
                        </tr>
                    </c:if>
                    <tr class="total-row">
                        <td>Tổng cộng</td>
                        <td><fmt:formatNumber value="${bill.totalAmount}" type="number" maxFractionDigits="0"/> đ</td>
                    </tr>
                    <tr class="total-row">
                        <td>Số tiền phải trả</td>
                        <td><fmt:formatNumber value="${bill.dueAmount}" type="number" maxFractionDigits="0"/> đ</td>
                    </tr>
                    <tr class="total-row">
                        <td>Trạng thái</td>
                        <td><strong><%= "PAID".equals(bill.getBillStatus()) ? "Đã thanh toán" : "Chưa thanh toán"%></strong></td>
                    </tr>
                </tbody>
            </table>

            <!-- Form upload chứng minh thanh toán -->
            <div class="upload-form">
                <h4>Upload chứng minh thanh toán</h4>
                <form action="${pageContext.request.contextPath}/tenant/bill" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="uploadProof">
                    <input type="hidden" name="billId" value="<%= bill.getBillID() %>">
                    <div class="mb-3">
                        <label for="proofImage" class="form-label">Chọn file ảnh (JPEG/PNG):</label>
                        <input type="file" class="form-control" id="proofImage" name="proofImage" accept="image/jpeg,image/png" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Upload</button>
                </form>

                <!-- Hiển thị các chứng minh đã upload -->
                <c:if test="${not empty confirmations}">
                    <h5 class="mt-3">Chứng minh đã gửi:</h5>
                    <c:forEach var="conf" items="${confirmations}">
                        <div class="card mb-2">
                            <div class="card-body">
                                <p>Ngày gửi: <fmt:formatDate value="${conf.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></p>
                                <img src="data:image/jpeg;base64,<%= java.util.Base64.getEncoder().encodeToString(conf.imageData) %>" alt="Proof Image" class="proof-image">
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        <% } else { %>
            <div class="alert alert-warning" role="alert">Không tìm thấy hóa đơn nào cho bạn trong tháng này.</div>
        <% } %>
    </div>
</body>
</html>