<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, java.util.*" %>
<%
    Room rooms = (Room) request.getAttribute("rooms");
    Bill bill = (Bill) request.getAttribute("bill");
    Contract contract = (Contract) request.getAttribute("contract");
    List<IncurredFeeType> feeTypes = (List<IncurredFeeType>) request.getAttribute("feeTypes");
    Map<Integer, java.math.BigDecimal> feeMap = (Map<Integer, java.math.BigDecimal>) request.getAttribute("feeMap");
    List<UtilityReading> readings = (List<UtilityReading>) request.getAttribute("readings");
    Map<Integer, UtilityType> utilityTypeMap = (Map<Integer, UtilityType>) request.getAttribute("utilityTypeMap");
%>

<html>
<head>
    <title>Edit Bill</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="container mt-4">

<h3>📝 Edit Bill for Room <%= contract.getRoomNumber() %></h3>

<form action="${pageContext.request.contextPath}/admin/bill" method="post">
    <input type="hidden" name="action" value="edit"/>
    <input type="hidden" name="billId" value="<%= bill.getBillID() %>"/>
    <input type="hidden" name="contractId" value="<%= contract.getContractId() %>"/>
    <input type="hidden" name="roomId" value="<%= contract.getRoomId() %>"/>

    <div class="mb-3">
        <label>Room Rent:</label>
        <input type="number" name="roomRent" step="0.01" class="form-control"
               value="<%= rooms.getRentPrice() %>" readonly/>
    </div>

    <h5 class="mt-4">🔧 Utility Readings</h5>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>Service</th>
            <th>Unit Price</th>
            <th>Old Reading</th>
            <th>New Reading</th>
            <th>Used</th>
            <th>Amount</th>
        </tr>
        </thead>
        <tbody>
        <% for (UtilityReading ur : readings) {
            UtilityType type = utilityTypeMap.get(ur.getUtilityTypeID());
            if (type == null) continue;
            boolean fixed = "month".equalsIgnoreCase(type.getUnit());
        %>
        <tr data-unitprice="<%= type.getUnitPrice() %>">
            <td><%= type.getUtilityName() %></td>
            <td><%= type.getUnitPrice() %> đ</td>
            <td><input type="number" class="form-control" value="<%= ur.getOldReading() %>" readonly/></td>
            <td><input type="number" class="form-control" value="<%= ur.getNewReading() %>" readonly/></td>
            <td><input type="text" readonly class="form-control" value="<%= ur.getRoomID() %>"/></td>
            <td><input type="text" readonly class="form-control amount-value" value="<%= ur.getPriceUsed() %>" data-amount="<%= ur.getPriceUsed() %>"/></td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <% Boolean isLastMonth = (Boolean) request.getAttribute("isLastMonth");
       if (isLastMonth != null && isLastMonth) { %>
    <div class="mb-3">
        <label>Deposit Deduction:</label>
        <input type="number" name="deposit" step="0.01" class="form-control"
               value="<%= contract.getDeposit() != null ? contract.getDeposit() : 0 %>" required/>
    </div>
    <% } %>

    <div class="mb-3">
        <label>Trash Fee:</label>
        <input type="number" class="form-control amount-value"
               value="<%= feeMap != null ? feeMap.getOrDefault(-1, java.math.BigDecimal.ZERO) : java.math.BigDecimal.ZERO %>"
               data-amount="<%= feeMap != null ? feeMap.getOrDefault(-1, java.math.BigDecimal.ZERO) : java.math.BigDecimal.ZERO %>" readonly/>
    </div>

    <div class="mb-3">
        <label>Payment Status:</label>
        <select name="status" class="form-select" required>
            <option value="Unpaid" <%= "Unpaid".equals(bill.getBillStatus()) ? "selected" : "" %>>Unpaid</option>
            <option value="Paid" <%= "Paid".equals(bill.getBillStatus()) ? "selected" : "" %>>Paid</option>
        </select>
    </div>

    <div class="mb-3">
        <strong>Total Amount: <span id="totalAmountDisplay" class="text-danger">0 ₫</span></strong>
    </div>

    <button type="submit" class="btn btn-primary">💾 Save Changes</button>
    <a href="${pageContext.request.contextPath}/admin/bill?action=list" class="btn btn-secondary">⬅ Back</a>
</form>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        updateTotal();
    });

    function updateTotal() {
        let total = 0;

        // Utility Amounts
        document.querySelectorAll(".amount-value").forEach(input => {
            const val = parseFloat(input.dataset.amount);
            if (!isNaN(val)) total += val;
        });

        // Room Rent
        const roomRent = parseFloat(document.querySelector("input[name='roomRent']")?.value || 0);
        if (!isNaN(roomRent)) total += roomRent;

        document.getElementById("totalAmountDisplay").textContent = total.toLocaleString("vi-VN") + " ₫";
    }
</script>

</body>
</html>
