<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, model.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String step = request.getParameter("step") == null ? "1" : request.getParameter("step");
    String blockId = request.getParameter("blockId");
    String roomId = request.getParameter("roomId");
    String ctx = request.getContextPath();
%>
<html>
    <head>
        <title>Quy trình lập hóa đơn 2 bước</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background: #f6f8fb;
                font-family: "Segoe UI", "Arial", sans-serif;
            }
            .stepper {
                margin: 32px 0 24px 0;
                display: flex;
                gap: 16px;
            }
            .stepper .step {
                padding: 12px 28px;
                border-radius: 24px;
                background: #eee;
                font-weight: 500;
                font-size: 1.08rem;
            }
            .stepper .active {
                background: #1976d2;
                color: #fff;
                box-shadow: 0 2px 12px rgba(25, 118, 210, 0.18);
            }
            .container {
                max-width: 1140px;
            }
        </style>
    </head>
    <body>
        <div class="container py-4">
            <h2>Quy trình lập hóa đơn 2 bước</h2>
            <div class="stepper">
                <div class="step <%= "1".equals(step) ? "active" : ""%>">1. Chốt dịch vụ</div>
                <div class="step <%= "2".equals(step) ? "active" : ""%>">2. Lập hóa đơn</div>
            </div>

            <!-- ==== BƯỚC 1 ==== -->
            <c:if test="${step == '1'}">
                <form method="get" action="${pageContext.request.contextPath}/admin/bill">
                    <input type="hidden" name="action" value="step">
                    <input type="hidden" name="step" value="1">
                    <label><b>Chọn Block:</b>
                        <select name="blockId" onchange="this.form.submit()" class="form-select d-inline w-auto ms-2">
                            <option value="">--Chọn block--</option>
                            <c:forEach var="blk" items="${blockList}">
                                <option value="${blk.blockID}" <c:if test="${blk.blockID == blockId || blk.blockID == param.blockId}">selected</c:if>>
                                    ${blk.blockName}
                                </option>
                            </c:forEach>
                        </select>
                    </label>
                </form>

                <c:if test="${not empty blockId}">
                    <c:choose>
                        <c:when test="${not empty roomList}">
                            <div class="row row-cols-1 row-cols-md-2 g-3">
                                <c:forEach var="room" items="${roomList}">
                                    <div class="col">
                                        <div class="card shadow-sm h-100" style="border-radius:14px;">
                                            <div class="card-body">
                                                <h5 class="card-title">🏠 ${room.roomNumber}</h5>
                                                <div class="mb-2">💸 <b><fmt:formatNumber value="${room.rentPrice}" type="currency"/></b></div>
                                                <p>
                                                    <span class="badge ${room.hasBill ? 'bg-secondary' : room.hasRecord ? 'bg-success' : 'bg-warning'}">
                                                        <c:choose>
                                                            <c:when test="${room.hasBill}">Đã lập hóa đơn</c:when>
                                                            <c:when test="${room.hasRecord}">Đã chốt dịch vụ</c:when>
                                                            <c:otherwise>Chưa chốt</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </p>
                                                <div>
                                                    <c:choose>
                                                        <c:when test="${room.hasBill}">
                                                            <button class="btn btn-outline-secondary btn-sm" disabled>Đã lập bill</button>
                                                        </c:when>
                                                        <c:when test="${room.hasRecord}">
                                                            <a href="<%= ctx%>/admin/bill?step=2&action=step&blockId=${blockId}&roomId=${room.roomID}&contractId=${room.activeContractCode}&month=${selectedMonth}" 
                                                               class="btn btn-success btn-sm">Lập hóa đơn</a>

                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="<%= ctx%>/admin/utility?action=record&blockId=${blockId}&roomId=${room.roomID}" class="btn btn-primary btn-sm">Chốt</a>

                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning mt-4">Không có phòng nào có hợp đồng hoạt động trong block này.</div>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:if>

            <!-- ==== BƯỚC 2 ==== -->
            <c:if test="${step == '2'}">
                <c:if test="${not empty room && not empty utilityTypes}">
                    <form method="post" action="<%= request.getContextPath()%>/admin/bill">

                        <input type="hidden" name="step" value="2"/>
                        <input type="hidden" name="action" value="saveBill"/>
                        <input type="hidden" name="roomId" value="${room.roomID}"/>
                        <input type="hidden" name="contractId" value="${activeContract.contractId}" />
                        <input type="hidden" name="blockId" value="${blockId}" />

                        <h3>Lập hóa đơn cho phòng: <span style="color:#1455b7">${room.roomNumber}</span></h3>

                        <!-- Thông tin chung -->
                        <div class="row g-3 mb-3">
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Lý do thu tiền</label>
                                <select name="reason" class="form-select" required>
                                    <option value="month">Thu hàng tháng</option>
                                    <option value="first">Thu tháng đầu</option>
                                    <option value="last">Thu tháng cuối</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label fw-bold">Tháng lập phiếu</label>
                                <input type="text" readonly class="form-control" value="<%= java.time.LocalDate.now().getMonthValue()%>/<%= java.time.LocalDate.now().getYear()%>">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-bold">Ngày lập hóa đơn</label>
                                <input type="date" name="issueDate" class="form-control" value="<%= java.time.LocalDate.now()%>">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-bold">Hạn đóng tiền</label>
                                <input type="date" name="dueDate" class="form-control" value="<%= java.time.LocalDate.now().plusDays(10)%>">
                            </div>
                        </div>

                        <!-- Hợp đồng -->
                        <div class="mb-2">
                            <b>Giá tiền phòng:</b> <span style="color:#1976d2;font-size:1.15em">${room.rentPrice} đ</span>
                        </div>
                        <div class="mb-2">
                            <b>Ngày vào:</b> ${activeContract.startDate} | 
                            <b>Đến ngày:</b> ${activeContract.endDate}
                        </div>

                        <!-- Tiện ích -->
                        <div class="section-title fw-bold mt-3">Chi tiết tiện ích</div>
                        <table class="table table-bordered align-middle">
                            <thead>
                                <tr>
                                    <th>Dịch vụ</th>
                                    <th>Đơn giá</th>
                                    <th>Chỉ số cũ</th>
                                    <th>Chỉ số mới</th>
                                    <th>Số sử dụng</th>
                                    <th>Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ut" items="${utilityTypes}">
                                    <tr>
                                        <td>${ut.name}</td>
                                        <td><fmt:formatNumber value="${ut.unitPrice}" pattern="#,##0.00"/> đ</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${ut.oldIndex != null}">
                                                    <fmt:formatNumber value="${ut.oldIndex}" pattern="#,##0.00"/>
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${ut.newIndex != null}">
                                                    <fmt:formatNumber value="${ut.newIndex}" pattern="#,##0.00"/>
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${ut.used}" pattern="#,##0.00"/>
                                        </td>
                                        <td class="amount-cell" data-amount="${ut.amount}">
                                            <fmt:formatNumber value="${ut.amount}" pattern="#,##0.00"/> đ
                                        </td>

                                    </tr>
                                </c:forEach>

                            </tbody>



                        </table>
                        <div id="extra-fee-list">
                            <div class="row mb-2 extra-fee-item">
                                <div class="col-md-6">
                                    <select name="extraFeeTypeIds" class="form-select" onchange="setDefaultAmount(this)">
                                        <option value="">-- Chọn loại phụ phí --</option>
                                        <c:forEach var="ft" items="${feeTypeList}">
                                            <option value="${ft.incurredFeeTypeID}" data-default="${ft.defaultAmount}">
                                                ${ft.feeName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <input type="number" class="form-control" placeholder="Số tiền (VNĐ)">
                                </div>
                                <div class="col-md-2">
                                    <input type="number" name="extraFeeAmounts" class="form-control">
                                </div>
                            </div>
                        </div>



                        <!-- Tổng -->
                        <%
                            model.Room room = (model.Room) request.getAttribute("room"); // ✅ THÊM DÒNG NÀY
                            double rent = room != null ? room.getRentPrice() : 0.0;

                            double utilities = 0.0;
                            if (request.getAttribute("totalAmount") != null) {
                                Object utilAttr = request.getAttribute("totalAmount");
                                if (utilAttr instanceof Number) {
                                    utilities = ((Number) utilAttr).doubleValue();
                                }
                            }

                            double extra = 0.0;
                            if (request.getAttribute("extraFee") != null) {
                                Object extraAttr = request.getAttribute("extraFee");
                                if (extraAttr instanceof java.math.BigDecimal) {
                                    extra = ((java.math.BigDecimal) extraAttr).doubleValue();
                                } else if (extraAttr instanceof Number) {
                                    extra = ((Number) extraAttr).doubleValue();
                                }
                            }

                            double grandTotal = rent + utilities + extra;
                        %>

                        <!-- Tổng cộng -->
                        <h5 class="text-danger mt-3">
                            Tổng cộng: <span id="totalAmount" class="fw-bold">0 ₫</span>
                        </h5>




                        <!-- Nút -->
                        <div class="mt-4">
                            <button type="submit" class="btn btn-primary">Thêm hóa đơn</button>
                            <a class="btn btn-outline-secondary" href="<%= ctx%>/admin/bill?step=1&action=step&blockId=${blockId}">Quay lại bước 1</a>

                        </div>
                    </form>
                </c:if>
                <c:if test="${empty room || empty utilityTypes}">
                    <div class="alert alert-warning">❗ Dữ liệu chưa đủ hoặc không hợp lệ. Vui lòng chọn phòng đã chốt dịch vụ từ bước 1.</div>
                    <a class="btn btn-secondary" href="${ctx}/admin/bill?step=1&action=step&blockId=${blockId}">Quay lại bước 1</a>
                </c:if>
            </c:if>
        </div>
        <script>
            // Gọi lại khi thay chọn loại phụ phí
            function setDefaultAmount(select) {
                const selectedOption = select.options[select.selectedIndex];
                const defaultAmount = selectedOption.getAttribute("data-default");
                const amountInput = select.closest(".extra-fee-item").querySelector("input[name='extraFeeAmounts']");
                if (defaultAmount && parseFloat(defaultAmount) > 0) {
                    amountInput.value = defaultAmount;
                } else {
                    amountInput.value = '';
                }
                updateTotal(); // ✅ Gọi lại tổng khi thay đổi
            }

            function updateTotal() {
                let total = 0;

                // Cộng tiện ích
                document.querySelectorAll(".amount-cell").forEach(td => {
                    const value = parseFloat(td.dataset.amount);
                    if (!isNaN(value))
                        total += value;
                });

                // Cộng các phụ phí
                document.querySelectorAll("input[name='extraFeeAmounts']").forEach(input => {
                    const val = parseFloat(input.value);
                    if (!isNaN(val))
                        total += val;
                });

                // Cộng thêm tiền phòng nếu có
                const roomRent = parseFloat("${room.rentPrice}");
                if (!isNaN(roomRent))
                    total += roomRent;

                // Cập nhật hiển thị tổng
                document.getElementById("totalAmount").textContent = total.toLocaleString("vi-VN") + " ₫";
            }

            // Gọi khi trang load xong
            document.addEventListener("DOMContentLoaded", updateTotal);
        </script>


    </body>
</html>
