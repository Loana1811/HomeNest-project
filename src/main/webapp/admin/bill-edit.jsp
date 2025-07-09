<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, java.util.*" %>
<%
    Room rooms = (Room) request.getAttribute("rooms");
    Bill bill = (Bill) request.getAttribute("bill");
    BillDetail detail = (BillDetail) request.getAttribute("detail");
    Contract contract = (Contract) request.getAttribute("contract");
    List<IncurredFeeType> feeTypes = (List<IncurredFeeType>) request.getAttribute("feeTypes");
    Map<Integer, java.math.BigDecimal> feeMap = (Map<Integer, java.math.BigDecimal>) request.getAttribute("feeMap");
    List<UtilityReading> readings = (List<UtilityReading>) request.getAttribute("readings");
    Map<Integer, UtilityType> utilityTypeMap = (Map<Integer, UtilityType>) request.getAttribute("utilityTypeMap");
%>

<html>
    <head>
        <title>Chỉnh sửa hóa đơn</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    </head>
    <body class="container mt-4">
        <h3>📝 Chỉnh sửa hóa đơn phòng <%= contract.getRoomNumber()%> </h3>
        <form action="${pageContext.request.contextPath}/admin/bill" method="post">
            <input type="hidden" name="action" value="edit"/>
            <input type="hidden" name="billId" value="<%= bill.getBillID()%>"/>
            <input type="hidden" name="contractId" value="<%= contract.getContractId()%>"/>
            <input type="hidden" name="roomId" value="<%= contract.getRoomId()%>"/>

            <div class="mb-3">
                <label>Tiền phòng:</label>
                <input type="number" name="roomRent" step="0.01" class="form-control"
                       value="<%= detail.getRoomrent() > 0 ? detail.getRoomrent() : rooms.getRentPrice()%>" required/>
            </div>

            <div class="mb-3">
                <label>Tiền điện:</label>
                <input type="number" name="electricity" step="0.01" class="form-control"
                       value="<%= detail.getElectricityCost()%>" required/>
            </div>

            <div class="mb-3">
                <label>Tiền nước:</label>
                <input type="number" name="water" step="0.01" class="form-control"
                       value="<%= detail.getWaterCost()%>" required/>
            </div>

            <div class="mb-3">
                <label>Tiền wifi:</label>
                <input type="number" name="wifi" step="0.01" class="form-control"
                       value="<%= detail.getWifiCost()%>" required/>
            </div>

            <div class="mb-3">
                <label>Tiền rác:</label>
                <input type="number" name="trash" step="0.01" class="form-control"
                       value="<%= (feeMap != null ? feeMap.getOrDefault(-1, java.math.BigDecimal.ZERO) : java.math.BigDecimal.ZERO)%>"/>
            </div>

            <h5 class="mt-4">🔧 Chỉ số tiện ích</h5>
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Dịch vụ</th>
                        <th>Đơn giá</th>
                        <th>Chỉ số cũ</th>
                        <th>Chỉ số mới</th>
                        <th>Đã dùng</th>
                        <th>Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (UtilityReading ur : readings) {
                            UtilityType type = utilityTypeMap.get(ur.getUtilityTypeID());
                            if (type == null) {
                                continue;
                            }
                            boolean fixed = "month".equalsIgnoreCase(type.getUnit());
                    %>
                    <tr data-unitprice="<%= type.getUnitPrice()%>">
                        <td><%= type.getUtilityName()%></td>
                        <td><%= type.getUnitPrice()%> đ</td>
                        <td>
                            <% if (fixed) { %>
                            <input type="number" class="form-control" value="1" readonly/>
                            <% } else {%>
                            <input type="number" name="oldIndex_<%= type.getUtilityTypeID()%>" value="<%= ur.getOldReading()%>" step="0.01" class="form-control old-index"/>
                            <% } %>
                        </td>
                        <td>
                            <% if (fixed) { %>
                            <input type="number" class="form-control" value="1" readonly/>
                            <% } else {%>
                            <input type="number" name="newIndex_<%= type.getUtilityTypeID()%>" value="<%= ur.getNewReading()%>" step="0.01" class="form-control new-index"/>
                            <% }%>
                        </td>
                        <td><input type="text" readonly class="form-control used-value" value="<%= ur.getNewReading().subtract(ur.getOldReading())%>"/></td>
                        <td><input type="text" readonly class="form-control amount-value" value="<%= ur.getPriceUsed()%>" data-amount="<%= ur.getPriceUsed()%>"/></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>

            <!-- Các phụ phí đã áp dụng -->
            <div class="mb-3">
                <label>Phụ phí đã có:</label>
                <% if (feeTypes != null) {
                        for (IncurredFeeType feeType : feeTypes) {
                            int id = feeType.getIncurredFeeTypeID();
                            java.math.BigDecimal val = (feeMap != null ? feeMap.getOrDefault(id, java.math.BigDecimal.ZERO) : java.math.BigDecimal.ZERO);
                            if (val.compareTo(java.math.BigDecimal.ZERO) > 0) {
                %>
                <div class="mb-2 existing-fee">
                    <label><%= feeType.getFeeName()%>:</label>
                    <input type="number" name="extraFee_<%= id%>" step="0.01" class="form-control" value="<%= val%>"/>
                </div>
                <% }
            }
        } %>
            </div>

            <!-- Dropdown chọn thêm phụ phí mới -->
            <div class="row mt-2">
                <div class="col-8">
                    <select id="feeTypeDropdown" class="form-select">
                        <option value="">-- Chọn phụ phí để thêm --</option>
                        <% if (feeTypes != null) {
                                for (IncurredFeeType feeType : feeTypes) {
                                    int id = feeType.getIncurredFeeTypeID();
                                    if (feeMap == null || !feeMap.containsKey(id) || feeMap.get(id).compareTo(java.math.BigDecimal.ZERO) == 0) {
                        %>
                        <option value="<%= id%>" data-name="<%= feeType.getFeeName()%>" data-default="<%= feeType.getDefaultAmount()%>">
                            <%= feeType.getFeeName()%>
                        </option>
                        <% }
                    }
                } %>
                    </select>
                </div>
                <div class="col-4">
                    <button type="button" class="btn btn-outline-primary" onclick="addFee()">➕ Thêm</button>
                </div>
            </div>

            <!-- Nơi sẽ render thêm các phụ phí -->
            <div id="addedFeesContainer"></div>


            <%
                Boolean isLastMonth = (Boolean) request.getAttribute("isLastMonth");
                if (isLastMonth != null && isLastMonth) {
            %>
            <div class="mb-3">
                <label>Tiền cọc trừ:</label>
                <input type="number" name="deposit" step="0.01" class="form-control"
                       value="<%= contract.getDeposit() != null ? contract.getDeposit() : 0%>" required/>
            </div>
            <%
                }
            %>


            <div class="mb-3">
                <label>Trạng thái thanh toán:</label>
                <select name="status" class="form-select" required>
                    <option value="Unpaid" <%= "Unpaid".equals(bill.getBillStatus()) ? "selected" : ""%>>Chưa thanh toán</option>
                    <option value="Paid" <%= "Paid".equals(bill.getBillStatus()) ? "selected" : ""%>>Đã thanh toán</option>
                </select>
            </div>

            <div class="mb-3">
                <strong>Tổng cộng: <span id="totalAmountDisplay" class="text-danger">0 ₫</span></strong>
            </div>

            <button type="submit" class="btn btn-primary">💾 Lưu thay đổi</button>
            <a href="${pageContext.request.contextPath}/admin/bill?action=list" class="btn btn-secondary">⬅ Quay lại</a>
        </form>

        <script>
            document.addEventListener("DOMContentLoaded", () => {
                // Gắn sự kiện input cho chỉ số tiện ích
                document.querySelectorAll('.old-index, .new-index').forEach(input => {
                    input.addEventListener('input', () => {
                        const row = input.closest('tr');
                                const oldVal = parseFloat(row.querySelector('.old-index')?.value || 0);
                                const newVal = parseFloat(row.querySelector('.new-index')?.value || 0);
                        const unitPrice = parseFloat(row.dataset.unitprice) || 0;
                        const used = Math.max(0, newVal - oldVal);
                        const amount = used * unitPrice;

                        row.querySelector('.used-value').value = used.toFixed(2);
                        row.querySelector('.amount-value').value = amount.toLocaleString("vi-VN");
                        row.querySelector('.amount-value').dataset.amount = amount.toFixed(2);

                        updateTotal();
                    });
                });

                // Tính tổng ban đầu
                updateTotal();
            });

        // Hàm tính tổng tiền
            function updateTotal() {
                let total = 0;

                // Tiện ích
                document.querySelectorAll(".amount-value").forEach(input => {
                    const val = parseFloat(input.dataset.amount);
                    if (!isNaN(val))
                        total += val;
                });

              const roomRent = parseFloat(document.querySelector("input[name='roomRent']")?.value || 0);
if (!isNaN(roomRent)) {
    total += roomRent;
}


                // Phụ thu động
                document.querySelectorAll("input[name^='extraFee_']").forEach(input => {
                    const val = parseFloat(input.value);
                    if (!isNaN(val))
                        total += val;
                });

                document.getElementById("totalAmountDisplay").textContent = total.toLocaleString("vi-VN") + " ₫";
            }

        // Hàm thêm phụ phí từ dropdown
            function addFee() {
                const dropdown = document.getElementById("feeTypeDropdown");
                const selected = dropdown.options[dropdown.selectedIndex];
                const id = selected.value;
                const name = selected.dataset.name;
                const defaultAmount = selected.dataset.default || 0;

                if (!id)
                    return;

                const container = document.getElementById("addedFeesContainer");

                const label = document.createElement("label");
                label.innerText = name + ":";

                const input = document.createElement("input");
                input.type = "number";
                input.name = "extraFee_" + id;
                input.step = "0.01";
                input.className = "form-control";
                input.value = defaultAmount;
                input.addEventListener("input", updateTotal);

                const feeDiv = document.createElement("div");
                feeDiv.className = "mb-2 added-fee";
                feeDiv.appendChild(label);
                feeDiv.appendChild(input);

                container.appendChild(feeDiv);

                // Xóa khỏi dropdown
                selected.remove();

                updateTotal();
            }
        </script>

    </body>
</html>
