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
    Boolean isEditable = (Boolean) request.getAttribute("isEditable");
    Boolean canCreateBill = (Boolean) request.getAttribute("canCreateBill");
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<html>
    <head>
        <title>2-Step Invoicing Process</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background: #f4f6f9;
                font-family: "Segoe UI", "Arial", sans-serif;
                color: #333;
            }

            .container {
                max-width: 100%;
                padding: 2rem 1rem;
                overflow-x: visible;
            }

            h2 {
                font-weight: 600;
                color: #1455b7;
                margin-bottom: 1.5rem;
            }

            .stepper {
                display: flex;
                gap: 12px;
                margin-bottom: 24px;
                flex-wrap: wrap;
            }

            .step {
                padding: 10px 20px;
                border-radius: 30px;
                background-color: #dbe4f3;
                font-weight: 500;
                font-size: 1rem;
                color: #444;
            }

            .step.active {
                background-color: #1976d2;
                color: #fff;
                box-shadow: 0 4px 10px rgba(25, 118, 210, 0.2);
            }

            label b {
                font-weight: 600;
            }

            .form-select,
            .form-control {
                border-radius: 10px;
            }

            .card {
                border: none;
                border-radius: 14px;
                transition: 0.3s;
                max-width: 100%;
                min-width: 260px;
                overflow-wrap: break-word;
            }

            .card-body {
                padding: 1rem 1rem;
            }

            .form-select {
                min-width: 200px;
            }

            .card:hover {
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            }

            .card-title {
                font-weight: 600;
                color: #1976d2;
            }

            .section-title {
                font-size: 1.2rem;
                margin-top: 1.5rem;
                margin-bottom: 0.5rem;
                border-left: 4px solid #1976d2;
                padding-left: 10px;
                color: #1976d2;
            }

            .table thead {
                background-color: #e8f0fe;
            }

            .table td,
            .table th {
                vertical-align: middle;
            }

            .amount-cell {
                font-weight: bold;
                color: #1455b7;
            }

            .btn-primary {
                background-color: #1976d2;
                border: none;
            }

            .btn-outline-secondary {
                border-radius: 30px;
            }

            .btn-danger.btn-sm {
                padding: 4px 10px;
            }

            #totalAmount {
                font-size: 1.3rem;
            }

            /* Responsive tweaks */
            @media (max-width: 768px) {
                .stepper {
                    flex-direction: column;
                }
            }

            @media (max-width: 576px) {
                .card-title {
                    font-size: 1rem;
                }

                .card-body {
                    padding: 0.75rem;
                }

                .form-select,
                .form-control {
                    font-size: 0.9rem;
                }
            }
        </style>


    </head>
    <body>

        <div class="main-content">
            <div class="container py-4">
                <c:if test="${not empty sessionScope.success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${sessionScope.success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="success" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${sessionScope.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="error" scope="session" />
                </c:if>

                <h2>2-Step Invoicing Process</h2>
                <div class="stepper">
                    <div class="step <%= "1".equals(step) ? "active" : ""%>">1. Service Closing</div>
                    <div class="step <%= "2".equals(step) ? "active" : ""%>">2. Invoicing</div>
                </div>

                <!-- ==== BƯỚC 1 ==== -->
                <c:if test="${step == '1'}">
                    <form method="get" action="${pageContext.request.contextPath}/admin/bill">
                        <input type="hidden" name="action" value="step">
                        <input type="hidden" name="step" value="1">
                        <label><b>Select Block:</b>
                            <select name="blockId" onchange="this.form.submit()" class="form-select d-inline w-auto ms-2">
                                <option value="">--Select Block--</option>
                                <c:forEach var="blk" items="${blockList}">
                                    <option value="${blk.blockID}" <c:if test="${blk.blockID == blockId || blk.blockID == param.blockId}">selected</c:if>>
                                        ${blk.blockName}
                                    </option>
                                </c:forEach>
                            </select>
                        </label>
                        <form method="get" action="bill">
    <input type="hidden" name="action" value="step" />
    <input type="hidden" name="step" value="1" />

    <label for="month">Select Room:</label>
    <input type="month" name="month" id="month" value="${selectedMonth}" onchange="this.form.submit()"/>

    <label for="blockId">Select Block:</label>
    <select name="blockId" id="blockId" onchange="this.form.submit()">
        <c:forEach var="b" items="${blockList}">
            <option value="${b.blockID}" ${b.blockID == blockId ? 'selected' : ''}>${b.blockName}</option>
        </c:forEach>
    </select>
</form>

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
                                                                <c:when test="${room.hasBill}">Invoiced</c:when>
                                                                <c:when test="${room.hasRecord}">
Service Closed</c:when>
                                                                <c:otherwise>Chưa chốt</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                    </p>
                                                    <div>
                                                        <c:choose>
                                                            <c:when test="${room.hasBill}">
                                                                <button class="btn btn-outline-secondary btn-sm" disabled>Invoice has been created</button>
                                                            </c:when>
                                                            <c:when test="${room.hasRecord}">
                                                                <a href="<%= ctx%>/admin/bill?step=2&action=step&blockId=${blockId}&roomId=${room.roomID}&contractId=${room.activeContractCode}&month=${selectedMonth}" 
                                                                   class="btn btn-success btn-sm">Make an invoice</a>

                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="<%= ctx%>/admin/utility?action=record&blockId=${blockId}&roomId=${room.roomID}" class="btn btn-primary btn-sm">Service closure</a>

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
                                    <div class="alert alert-warning mt-4">There are no rooms with active contracts in this block.</div>
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

                            <h3>Billing for rooms: <span style="color:#1455b7">${room.roomNumber}</span></h3>

                            <!-- Thông tin chung -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">Reason for collection</label>
                                    <select name="reason" class="form-select" required>
                                        <option value="month">Monthly collection</option>
                                        <option value="first">Fall first month</option>
                                        <option value="last">Fall last month</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label fw-bold">Voting month</label>
                                    <input type="text" readonly class="form-control" value="<%= java.time.LocalDate.now().getMonthValue()%>/<%= java.time.LocalDate.now().getYear()%>">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-bold">Invoice date</label>
                                    <input type="date" name="issueDate" class="form-control" value="<%= java.time.LocalDate.now()%>">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-bold">Payment deadline</label>
                                    <input type="date" name="dueDate" class="form-control" value="<%= java.time.LocalDate.now().plusDays(10)%>">
                                </div>
                            </div>

                            <!-- Hợp đồng -->
                            <div class="mb-2">
                                <b>Room price:</b> <span style="color:#1976d2;font-size:1.15em">${room.rentPrice} đ</span>
                            </div>
                            <div class="mb-2">
                                <b>Entry date:</b> ${activeContract.startDate} | 
                                <b>Come day:</b> ${activeContract.endDate}
                            </div>

                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th>Electricity</th>
                                        <th>Water</th>
                                        <th>Wifi</th>
                                        <th>Rác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>
                                            <span class="badge ${room.isElectricityFree == 1 ? 'bg-success' : 'bg-secondary'}">
                                                ${room.isElectricityFree == 1 ? 'Miễn phí' : 'Tính phí'}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge ${room.isWaterFree == 1 ? 'bg-success' : 'bg-secondary'}">
                                                ${room.isWaterFree == 1 ? 'Miễn phí' : 'Tính phí'}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge ${room.isWifiFree == 1 ? 'bg-success' : 'bg-secondary'}">
                                                ${room.isWifiFree == 1 ? 'Miễn phí' : 'Tính phí'}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge ${room.isTrashFree == 1 ? 'bg-success' : 'bg-secondary'}">
                                                ${room.isTrashFree == 1 ? 'Miễn phí' : 'Tính phí'}
                                            </span>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>


                            <!-- Tiện ích -->
                            <div class="section-title fw-bold mt-3">Utility details</div>
                            <table class="table table-bordered align-middle">
                                <thead>
                                    <tr>
                                        <th>Service</th>
                                        <th>Price</th>
                                        <th>Old index</th>
                                        <th>New index</th>
                                        <th>Usage number</th>
                                        <th>Make money</th>
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
<!--                             ✅ Hiển thị toàn bộ phụ phí cố định 
                            <div class="section-title fw-bold mt-4">Phụ phí phát sinh</div>
                            <div class="mb-3">
                                <c:forEach var="feeType" items="${feeTypeList}">
                                    <div class="row mb-2 align-items-center">
                                        <div class="col-md-1">
                                            <input type="checkbox"
                                                   class="form-check-input fee-checkbox"
                                                   data-target="fee_${feeType.incurredFeeTypeID}"
                                                   onchange="toggleFeeInput(this)">
                                        </div>
                                        <div class="col-md-4">${feeType.feeName}</div>
                                        <div class="col-md-6">
                                            <input type="number"
                                                   class="form-control extra-fee-input"
                                                   name="extraFee_${feeType.incurredFeeTypeID}"
                                                   id="fee_${feeType.incurredFeeTypeID}"
                                                   value="${feeType.defaultAmount}"
                                                   disabled
                                                   oninput="updateTotal()" />
                                        </div>
                                    </div>
                                </c:forEach>

                            </div>-->

                            <!-- ✅ Vùng để JS thêm phụ phí: ĐƯA VÀO TRONG FORM -->
                            <div id="selectedFeesContainer"></div>

                            <!-- Tổng cộng -->
                            <h5 class="text-danger mt-3">
                                Tổng cộng: <span id="totalAmount" class="fw-bold">0 ₫</span>
                            </h5>

                            <!-- Nút -->
                            <div class="mt-4">
                                <button type="submit" class="btn btn-primary">Create Bill</button>
                                <a class="btn btn-outline-secondary" href="<%= ctx%>/admin/bill?step=1&action=step&blockId=${blockId}">Back to step 1</a>
                            </div>
                        </form>

                    </c:if>
                    <c:if test="${empty room || empty utilityTypes}">
                        <div class="alert alert-warning">❗ Insufficient or invalid data. Please select the room that has been booked from step 1..</div>
                        <a class="btn btn-secondary" href="${ctx}/admin/bill?step=1&action=step&blockId=${blockId}">Back to step 1</a>
                    </c:if>
                </c:if>
            </div>
            <script>
                const addedFeeIds = new Set();

                function toggleFeeInput(checkbox) {
                    const inputId = checkbox.getAttribute("data-target");
                    const input = document.getElementById(inputId);
                    input.disabled = !checkbox.checked;
                    updateTotal();
                }

                function addSelectedFee() {
                    const select = document.getElementById("feeDropdown");
                    const selectedOption = select.options[select.selectedIndex];

                    const id = selectedOption?.value?.trim();
                            const name = selectedOption?.getAttribute("data-name")?.trim() || "(Không tên)";
                            const defaultAttr = selectedOption?.getAttribute("data-default");
                    const defaultAmount = parseFloat((defaultAttr || "0").replace(/,/g, ""));

                    console.log("Selected option:", {id, name, defaultAttr, defaultAmount});

                    if (!id || isNaN(defaultAmount) || addedFeeIds.has(id)) {
                        select.selectedIndex = 0;
                        return;
                    }

                    addedFeeIds.add(id);

                    const container = document.getElementById("selectedFeesContainer");

                    const row = document.createElement("div");
                    row.className = "row mb-2 align-items-center";

                    const colName = document.createElement("div");
                    colName.className = "col-md-5";
                    colName.textContent = name;

                    const colInput = document.createElement("div");
                    colInput.className = "col-md-6";
                    const input = document.createElement("input");
                    input.type = "number";
                    input.className = "form-control extra-fee-input";
                    input.name = `extraFee_${id}`;
                    input.value = defaultAmount.toFixed(2);
                    input.setAttribute("data-id", id);
                    input.oninput = updateTotal;
                    colInput.appendChild(input);

                    const colRemove = document.createElement("div");
                    colRemove.className = "col-md-1";
                    const btn = document.createElement("button");
                    btn.type = "button";
                    btn.className = "btn btn-danger btn-sm";
                    btn.textContent = "X";
                    btn.onclick = function () {
                        removeFee(btn, id);
                    };
                    colRemove.appendChild(btn);

                    row.appendChild(colName);
                    row.appendChild(colInput);
                    row.appendChild(colRemove);

                    container.appendChild(row);
                    updateTotal();
                    select.selectedIndex = 0;
                }

                function updateTotal() {
                    let total = 0;

                    // Tiện ích
                    document.querySelectorAll(".amount-cell").forEach(td => {
                        const val = parseFloat(td.dataset.amount);
                        if (!isNaN(val))
                            total += val;
                    });

                    // Tiền phòng
                    const rent = parseFloat("${room.rentPrice}");
                    if (!isNaN(rent))
                        total += rent;

                    // Phụ phí (chỉ tính nếu input không bị disabled)
                    document.querySelectorAll(".extra-fee-input").forEach(input => {
                        if (!input.disabled) {
                        const val = parseFloat(input.value?.replaceAll(",", "") || "0");
                        if (!isNaN(val) && val > 0) {
                            total += val;
                        }
                        }
                    });

                    document.getElementById("totalAmount").textContent =
                            total.toLocaleString("vi-VN") + " ₫";
                }

                window.onload = function () {
                    updateTotal();
                };
            </script>
    </body>
</html>
