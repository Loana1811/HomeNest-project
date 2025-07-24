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

    // Tiền cọc
    BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;

    // Tổng cộng
    BigDecimal totalAmount = BigDecimal.ZERO;
    if (billDetail != null && billDetail.getRoomrent() != 0) {
        totalAmount = totalAmount.add(BigDecimal.valueOf(billDetail.getRoomrent()));
    }

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

    // Tháng cuối -> trừ tiền cọc
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
                    <p><strong>Phòng:</strong> <%= contract.getRoomNumber()%></p>
                    <p><strong>Khách thuê:</strong> <%= contract.getTenantName()%></p>
                    <p><strong>SĐT:</strong> <%= contract.getPhone()%></p>
                </div>
                <div>
                    <p><strong>Ngày lập hóa đơn:</strong> <fmt:formatDate value="${bill.billDate}" pattern="dd/MM/yyyy"/></p>
                    <p><strong>Số hóa đơn:</strong> <%= bill.getBillID()%></p>
                </div>
            </div>

            <table class="table table-bordered">
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



                    
                    <% if (isLastMonth) {%>
                    <tr>
                        <td>Tiền cọc trừ</td>
                        <td><fmt:formatNumber value="<%= deposit%>" type="currency" maxFractionDigits="0"/> đ</td>
                    </tr>
                    <% }%>


                    <tr class="total-row">
                        <td>Tổng cộng</td>
                        <td><fmt:formatNumber value="<%= totalAmount%>" type="currency" maxFractionDigits="0"/> đ</td>
                    </tr>

                    <tr class="total-row">
                        <td>Số tiền phải thu</td>
                        <td><fmt:formatNumber value="<%= dueAmount%>" type="currency" maxFractionDigits="0"/> đ</td>
                    </tr>

                    <tr class="total-row">
                        <td>Trạng thái</td>
                        <td><strong><%= "PAID".equals(bill.getBillStatus()) ? "Đã thanh toán" : "Chưa thanh toán"%></strong></td>
                    </tr>
                </tbody>
            </table>

            <div class="payment-info d-flex justify-content-between align-items-start">
                <div class="bank-details">
                    <h5>Thông tin thanh toán</h5>
                    <p>
                        <strong>Ngân hàng:</strong> MuoidiemprojectSWP<br>
                        <strong>Chủ tài khoản:</strong> Group3_PRO<br>
                        <strong>Số tài khoản:</strong> 101010101010<br>
                        <strong>Nội dung chuyển khoản:</strong> SV-P5 (Tên Nhờ - Số phòng)
                    </p>
                </div>
                <div class="text-center">
                    <img src="${pageContext.request.contextPath}/img/qr_manifest_group3.png" alt="QR Code" class="qr-code mb-2">
                    <div>Quét mã QR để thanh toán</div>
                </div>
            </div>
        </div>
    </body>
</html>
