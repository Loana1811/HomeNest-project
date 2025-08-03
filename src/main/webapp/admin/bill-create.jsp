<%@page import="java.time.temporal.ChronoUnit"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="
         java.util.*, 
         java.time.*, 
         model.Block,
         model.Contract,
         model.UtilityType,
         model.UtilityReading
         " %>
<%!
private LocalDate toLocalDate(java.util.Date date) {
    if (date == null) return null;
    if (date instanceof java.sql.Date) {
        return ((java.sql.Date) date).toLocalDate(); // ✅ dùng cách hỗ trợ sẵn
    } else {
        return new java.sql.Timestamp(date.getTime()).toLocalDateTime().toLocalDate(); // ✅ sửa chỗ này
    }
}
%>


<%
List<Block> blocks = (List<Block>) request.getAttribute("blocks");
if (blocks == null) blocks = new ArrayList<>();
List<Object[]> rooms = (List<Object[]>) request.getAttribute("rooms");
if (rooms == null) rooms = new ArrayList<>();

// Lấy các thông tin đầu vào (ưu tiên request parameter trước, fallback sang attribute)
String selectedBlockId = request.getParameter("blockId");
if (selectedBlockId == null || selectedBlockId.isEmpty()) {
    selectedBlockId = (String) request.getAttribute("selectedBlockId");
}
String selectedRoomId = request.getParameter("roomId");
if (selectedRoomId == null || selectedRoomId.isEmpty()) {
    selectedRoomId = (String) request.getAttribute("selectedRoomId");
}
String selectedMonth = request.getParameter("month");
if (selectedMonth == null || selectedMonth.isEmpty()) {
    selectedMonth = (String) request.getAttribute("selectedMonth");
}
// ✅ Fix lỗi null-01
if (selectedMonth == null || selectedMonth.isEmpty() || "null".equalsIgnoreCase(selectedMonth)) {
    selectedMonth = java.time.LocalDate.now().toString().substring(0, 7);
}

String selectedContractId = request.getParameter("contractId");
if (selectedContractId == null || selectedContractId.isEmpty()) {
    selectedContractId = (String) request.getAttribute("selectedContractId");
}

List<Contract> activeContracts = (List<Contract>) request.getAttribute("contracts");
if (activeContracts == null) activeContracts = new ArrayList<>();

List<UtilityType> allTypes = (List<UtilityType>) request.getAttribute("utilityTypes");
if (allTypes == null) allTypes = new ArrayList<>();

List<UtilityReading> readings = (List<UtilityReading>) request.getAttribute("readings");
if (readings == null) readings = new ArrayList<>();

List<model.IncurredFee> fees = (List<model.IncurredFee>) request.getAttribute("fees");
if (fees == null) fees = new ArrayList<>();

List<model.IncurredFeeType> feeTypeList = (List<model.IncurredFeeType>) request.getAttribute("feeTypeList");
if (feeTypeList == null) feeTypeList = new ArrayList<>();

Map<Integer, String> feeTypeNames = (Map<Integer, String>) request.getAttribute("feeTypeNames");
Boolean billExists = (Boolean) request.getAttribute("billExists");
if (billExists == null) billExists = false;

Contract contract = (Contract) request.getAttribute("selectedContract");

// ==== Chuẩn bị danh sách tiện ích cố định (hiển thị box) ====
List<UtilityType> fixed = new ArrayList<>();
for (UtilityType ut : allTypes) {
    String n = ut.getUtilityName().toLowerCase();
    if (n.contains("điện") || n.contains("electric") ||
        n.contains("nước") || n.contains("water") ||
        n.contains("wifi") || n.contains("rác") || n.contains("trash")) {
        fixed.add(ut);
    }
}

// ==== Map các tiện ích đã ghi chỉ số (theo ID) ====
Map<Integer, UtilityReading> readingMap = new HashMap<>();
for (UtilityReading ur : readings) {
    readingMap.put(ur.getUtilityTypeID(), ur);
}

// ==== Tính tiền phòng nếu có hợp đồng ====
LocalDate billFromDate = null, billToDate = null;
int daysInMonth = 0, realDays = 0;
double rentPrice = 0;

if (contract != null) {
    LocalDate contractStart = toLocalDate(contract.getStartDate());
    LocalDate contractEnd = toLocalDate(contract.getEndDate());

    LocalDate monthStart = LocalDate.parse(selectedMonth + "-01");
    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

    billFromDate = (contractStart != null && contractStart.isAfter(monthStart)) ? contractStart : monthStart;
    billToDate = (contractEnd != null && contractEnd.isBefore(monthEnd)) ? contractEnd : monthEnd;
    daysInMonth = monthStart.lengthOfMonth();
    realDays = (int) ChronoUnit.DAYS.between(billFromDate, billToDate) + 1;
    rentPrice = contract.getRoomRent();
}
%>

    <!DOCTYPE html>
    <html>
    <head>
        <title>Create Bill</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
    </head>
    <body class="container mt-4">
        <h3>🧾 Create Bill For Room</h3>

        <!-- 1. Chọn Block, Phòng, Hợp đồng, Tháng -->
        <form method="get" action="<%=request.getContextPath()%>/admin/bill">
            <input type="hidden" name="action" value="create"/>
            <div class="row mb-2">
                <!-- Block -->
                <div class="col-md-3">
                    <label>Block</label>
                    <select name="blockId" class="form-select" onchange="this.form.submit()">
                        <option value="">-- Select Block --</option>
                        <% for (Block b : blocks) { %>
                        <option value="<%=b.getBlockID()%>" <%=String.valueOf(b.getBlockID()).equals(selectedBlockId) ? "selected" : ""%>>
                            <%=b.getBlockName()%>
                        </option>
                        <% } %>
                    </select>
                </div>
                <!-- Phòng (chỉ hiện khi đã chọn block) -->
                <div class="col-md-3">
                    <label>Room</label>
                    <select name="roomId" class="form-select" onchange="this.form.submit()">
                        <option value="">-- Select Room --</option>
                        <% if (selectedBlockId != null && !selectedBlockId.isEmpty()) {
                            for (Object[] r : rooms) { %>
                            <option value="<%=r[0]%>" <%=String.valueOf(r[0]).equals(selectedRoomId) ? "selected" : ""%>>
                                <%=r[1]%>
                            </option>
                        <% } } %>
                    </select>
                </div>
                <!-- Tháng hóa đơn -->
                <div class="col-md-3">
                    <label>Bill Month</label>
                    <input type="month" name="month" class="form-control" value="<%=selectedMonth%>" onchange="this.form.submit()"/>
                </div>
                <!-- Hợp đồng (chỉ hiện khi đã chọn phòng) -->
                <div class="col-md-3">
                    <label>Contract</label>
                    <select name="contractId" class="form-select" onchange="this.form.submit()">
                        <option value="">-- Choose contract --</option>
                        <% if (selectedRoomId != null && !selectedRoomId.isEmpty()) {
                            for (Contract c : activeContracts) { %>
                            <option value="<%=c.getContractId()%>" <%=String.valueOf(c.getContractId()).equals(selectedContractId) ? "selected" : ""%>>
                                HĐ #<%=c.getContractId()%> (<%=c.getStartDate()%> - <%=c.getEndDate() != null ? c.getEndDate() : "..."%>)
                            </option>
                        <% } } %>
                    </select>
                </div>
            </div>
        </form>

        <% if (billExists) { %>
        <div class="alert alert-danger">⚠ This month's bill is out for this room!</div>
        <% } %>

        <% if (contract != null) { %>
        <!-- 2. Ngày ở thực tế + Tiền phòng -->
        <div class="card mb-3">
            <div class="card-header bg-info text-white">Tiền phòng</div>
            <div class="card-body">
                <p><strong>StartDate:</strong> <%=billFromDate%></p>
                <p><strong>EndDate:</strong> <%=billToDate%></p>
                <p><strong>Number of billing days:</strong> <%=realDays%> / <%=daysInMonth%></p>
                <p><strong>Monthly unit price:</strong> <%=String.format("%,.0f đ", rentPrice)%></p>
                <p>
                    <strong>Make money:</strong> 
                    <%=String.format("%,.0f đ", rentPrice * realDays / daysInMonth)%>
                    <% if (realDays < daysInMonth) { %>
                    <span class="text-warning">(Calculated based on actual number of days)</span>
                    <% } %>
                </p>
            </div>
        </div>
        <!-- 3. Tiện ích -->
        <div class="row">
            <% for (UtilityType ut : fixed) {
                String name = ut.getUtilityName().toLowerCase();
                UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
            %>
            <div class="col-md-6 mb-3">
                <div class="card">
                    <div class="card-header bg-light">
                        <strong><%=ut.getUtilityName()%></strong>
                        (<%=ut.getUnit()%> – <%=String.format("%,.0f đ", ut.getUnitPrice())%>)
                    </div>
                    <div class="card-body">
                        <% if (name.contains("điện") || name.contains("nước")) { %>
                        <% if (ur != null) { %>
                        <p>Old Index: <%=ur.getOldReading()%></p>
                        <p>New Index: <%=ur.getNewReading()%></p>
                        <p>Usage: <strong><%=ur.getNewReading().subtract(ur.getOldReading())%> <%=ut.getUnit()%></strong></p>
                        <p>Make money: <strong><%=String.format("%,.0f đ", ur.getPriceUsed())%></strong></p>
                        <% } else { %>
                            <div class="alert alert-warning">⚠ No index entered this month! 
                           <a href="<%=request.getContextPath()%>/admin/bill?action=record&blockId=<%=selectedBlockId%>&roomId=<%=selectedRoomId%>&month=<%=selectedMonth%>" class="btn btn-sm btn-primary ms-2">Ghi tiện ích</a>

                        </div>
                        <% } %>
                        <% } else { // wifi/rác %>
                        <p><strong>Fixed fee:</strong> <%=String.format("%,.0f đ", ut.getUnitPrice())%> / <%=ut.getUnit()%></p>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
        <!-- 4. Phụ phí -->
        <div class="card mb-3">
        <div class="card-header">➕ Phụ phí</div>
        <div class="card-body">
            <form method="post" action="<%=request.getContextPath()%>/admin/fee-add">
                <input type="hidden" name="roomId" value="<%=selectedRoomId%>"/>
                <input type="hidden" name="month" value="<%=selectedMonth%>"/>
                <div class="row mb-2 align-items-end">
                    <div class="col-md-6">
                        <label class="form-label">Chọn loại phụ phí có sẵn</label>
                        <select name="incurredFeeTypeId" class="form-select" onchange="updateDefaultAmount()" id="incurredFeeTypeSelect">
                            <option value="">-- Chọn loại phụ phí --</option>
                            <% for (model.IncurredFeeType t : feeTypeList) { %>
                            <option value="<%=t.getIncurredFeeTypeID()%>" data-amount="<%=t.getDefaultAmount()%>">
                                <%=t.getFeeName()%> (<%=String.format("%,.0f đ", t.getDefaultAmount())%>)
                            </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Hoặc mô tả phụ phí khác</label>
                        <input type="text" name="feeDesc" class="form-control" placeholder="Mô tả phụ phí"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Số tiền (đ)</label>
                        <input type="number" name="amount" class="form-control" placeholder="Số tiền (đ)" min="0" id="feeAmount"/>
                    </div>
                </div>
                <div class="row mb-2">
                    <div class="col-md-12">
                        <button class="btn btn-success w-100">Thêm phụ phí</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

        <!-- 5. Tổng hợp và xác nhận -->
        <div class="card mb-3">
            <div class="card-header bg-success text-white">Consolidate invoices</div>
            <div class="card-body">
               <%
    double totalAmount = rentPrice * realDays / daysInMonth;
    for (UtilityType ut : fixed) {
        UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
        if (ur != null) {
            totalAmount += ur.getPriceUsed().doubleValue();
        } else if (!(ut.getUtilityName().toLowerCase().contains("điện") || ut.getUtilityName().toLowerCase().contains("nước"))) {
            totalAmount += ut.getUnitPrice().doubleValue();  // ✅ Sửa chỗ này
        }
    }
    for (model.IncurredFee f : fees) {
        totalAmount += f.getAmount().doubleValue();
    }
    %>


                <ul>
                    <p><strong>Total: <%=String.format("%,.0f đ", totalAmount)%></strong></p>

                    <li>Room Price: <%=String.format("%,.0f đ", rentPrice * realDays / daysInMonth)%></li>
                    <% for (UtilityType ut : fixed) {
                        UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
                        if (ur != null) { %>
                    <li><%=ut.getUtilityName()%>: <%=String.format("%,.0f đ", ur.getPriceUsed())%></li>
                    <%  } else if (!(ut.getUtilityName().toLowerCase().contains("điện") || ut.getUtilityName().toLowerCase().contains("nước"))) { %>
                    <li><%=ut.getUtilityName()%>: <%=String.format("%,.0f đ", ut.getUnitPrice())%></li>
                    <%  } } %>
                    <% if (!fees.isEmpty()) { 
                        for (model.IncurredFee f : fees) { %>
                    <li>Phụ phí: 
                        <%= (feeTypeNames != null && feeTypeNames.get(f.getIncurredFeeTypeID()) != null)
                                ? feeTypeNames.get(f.getIncurredFeeTypeID())
                                : "ID #" + f.getIncurredFeeTypeID()%>
                        (<%=String.format("%,.0f đ", f.getAmount())%>)
                    </li>
                    <% } } %>
                </ul>
                <form method="post" action="<%=request.getContextPath()%>/admin/bill">
                    <input type="hidden" name="action" value="save"/>
                    <input type="hidden" name="blockId" value="<%=selectedBlockId%>"/>
                    <input type="hidden" name="roomId" value="<%=selectedRoomId%>"/>
                    <input type="hidden" name="contractId" value="<%=contract.getContractId()%>"/>
                    <input type="hidden" name="month" value="<%=selectedMonth%>"/>
                    <button class="btn btn-primary" <%= (readingMap.size() < 2) ? "disabled" : ""%>>Save invoice</button>
                </form>
            </div>
        </div>
        <% } %>
    </body>
    </html>
