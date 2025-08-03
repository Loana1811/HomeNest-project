<%@page import="java.text.DecimalFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="model.UtilityType"%>
<%@page import="java.time.LocalDate"%>
<%@page import="model.Block"%>
<%@ page import="model.Room" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    List<Block> blocks = (List<Block>) request.getAttribute("blocks");
    List<UtilityType> utilityTypes = (List<UtilityType>) request.getAttribute("utilityTypes");
    Map<Integer, Double> oldIndexMap = (Map<Integer, Double>) request.getAttribute("oldIndexMap");

    String selectedBlockId = (String) request.getAttribute("selectedBlockId");
    String selectedRoomId = (String) request.getAttribute("selectedRoomId");
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<style>
    :root {
        --navy: #1e3b8a;
        --navy-light: #3f5fa6;
        --gray-bg: #f4f7fb;
        --light-gray: #e5e7eb;
        --shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        --radius: 12px;
        --transition: all 0.3s ease-in-out;
        --info-bg: #e0f2fe;
        --info-color: #0c4a6e;
        --warn-bg: #fff7ed;
        --warn-color: #92400e;
    }

    body {
        background-color: var(--gray-bg);
        font-family: 'Segoe UI', sans-serif;
        color: #1f2937;
    }

    .main-content {
        padding-top: 40px;
        padding-bottom: 40px;
    }

    .container-form {
        background-color: white;
        padding: 2.5rem;
        border-radius: var(--radius);
        box-shadow: var(--shadow);
        animation: fadeIn 0.5s ease;
    }

    @keyframes fadeIn {
        from { opacity: 0; transform: translateY(10px); }
        to { opacity: 1; transform: translateY(0); }
    }

    h3 {
        color: var(--navy);
        font-weight: 700;
        margin-bottom: 2rem;
        position: relative;
    }

    h3::after {
        content: '';
        position: absolute;
        bottom: -8px;
        left: 0;
        width: 80px;
        height: 4px;
        background-color: var(--navy-light);
        border-radius: 6px;
    }

    h5 {
        color: var(--navy-light);
        font-weight: 600;
        margin-top: 2rem;
        margin-bottom: 1rem;
    }

    label.form-label {
        font-weight: 500;
        color: #374151;
    }

    select.form-select,
    input.form-control {
        border-radius: var(--radius);
        border: 1px solid var(--light-gray);
        transition: var(--transition);
        background-color: #fff;
    }

    select.form-select:focus,
    input.form-control:focus {
        border-color: var(--navy-light);
        box-shadow: 0 0 0 0.2rem rgba(30, 59, 138, 0.15);
    }

    .form-control[readonly] {
        background-color: #f9fafb;
        color: #6b7280;
    }

    .border {
        border: 1px solid var(--light-gray) !important;
    }

    .rounded {
        border-radius: var(--radius) !important;
    }

    .shadow-sm {
        box-shadow: var(--shadow) !important;
    }

    .alert {
        border-radius: var(--radius);
        padding: 0.75rem 1.25rem;
        font-size: 0.95rem;
        margin-top: 0.75rem;
        margin-bottom: 0.5rem;
        border: none;
    }

    .alert-warning {
        background-color: var(--warn-bg);
        color: var(--warn-color);
        border-left: 4px solid #facc15;
    }

    .alert-info {
        background-color: var(--info-bg);
        color: var(--info-color);
        border-left: 4px solid #38bdf8;
    }

    .alert-secondary {
        background-color: #f3f4f6;
        color: #374151;
        border-left: 4px solid #9ca3af;
    }

    .btn-primary {
        background-color: var(--navy);
        border-color: var(--navy);
        border-radius: var(--radius);
        transition: var(--transition);
    }

    .btn-primary:hover {
        background-color: var(--navy-light);
        border-color: var(--navy-light);
    }

    .btn-outline-dark {
        border-radius: var(--radius);
        transition: var(--transition);
    }

    .btn-outline-dark:hover {
        background-color: #374151;
        color: #fff;
    }

    .btn {
        padding: 0.5rem 1.2rem;
        font-weight: 500;
        font-size: 1rem;
    }

    .form-section {
        padding: 1.5rem;
        background-color: #ffffff;
        border-radius: var(--radius);
        border: 1px solid #e5e7eb;
        margin-bottom: 1.5rem;
        box-shadow: var(--shadow);
        transition: var(--transition);
    }

    .form-section:hover {
        transform: translateY(-2px);
    }

    .row > div {
        margin-bottom: 1rem;
    }
</style>


<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Record Utility Usage</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <script>
            function reloadOnBlockChange() {
                const blockId = document.getElementById('blockId').value;
                window.location.href = '<%= ctx %>/admin/utility?action=record&blockId=' + encodeURIComponent(blockId);
            }
            function onRoomChange() {
                const block = document.getElementById('blockId').value;
                const room = document.getElementById('roomId').value;
                const month = document.getElementById('readingMonth').value;
                if (block && room) {
                    window.location.href = '<%= ctx %>/admin/utility?action=record'
                            + '&blockId=' + encodeURIComponent(block)
                            + '&roomId=' + encodeURIComponent(room)
                            + '&readingMonth=' + encodeURIComponent(month);
                }
            }
        </script>
<%
    String selectedMonth = request.getParameter("readingMonth");
    if ((selectedMonth == null || selectedMonth.isEmpty()) && request.getAttribute("readingMonth") != null) {
        selectedMonth = request.getAttribute("readingMonth").toString();
    }
    if (selectedMonth == null || selectedMonth.isEmpty()) {
        selectedMonth = java.time.LocalDate.now().toString().substring(0, 7); // yyyy-MM
    }
%>

</head>
    <body>
        <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

        <!-- MAIN CONTENT -->
        <main class="main-content container my-4" style="margin-left: 220px;">
            <div class="container-form">
                <h3>📅 Record Utility Usage</h3>

                <% if (error != null) { %>
                <div class="alert alert-danger"><%= error %></div>
                <% } %>

                <form method="post" action="<%= ctx %>/admin/record-reading">
                    <input type="hidden" name="action" value="record" />

                    <!-- SECTION 1: CHOOSE LOCATION -->
                    <div class="mb-4">
                        <h5 class="text-primary border-bottom pb-1">📌 Select Location</h5>
                        <div class="mb-3">
                            <label class="form-label" for="blockId">Block</label>
                            <select name="blockId" id="blockId" class="form-select" onchange="reloadOnBlockChange()" required>
                                <option value="">-- Select Block --</option>
                                <% for (Block b : blocks) {
                                    String id = String.valueOf(b.getBlockID());
                                    String name = b.getBlockName();
                                    String sel = id.equals(selectedBlockId) ? "selected" : "";
                                %>
                                <option value="<%= id %>" <%= sel %>><%= name %></option>
                                <% } %>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="roomId">Room</label>
                            <select name="roomId" id="roomId" class="form-select" onchange="onRoomChange()" required>
                                <option value="">-- Select Room --</option>
                                <% if (rooms != null) {
            for (Room r : rooms) {
                String id = String.valueOf(r.getRoomID());
                String name = r.getRoomNumber();
                String sel = id.equals(selectedRoomId) ? "selected" : "";
                                %>
                                <option value="<%= id %>" <%= sel %>><%= name %></option>
                                <%  }
} %>

                            </select>
                        </div>
                    </div>

                    <!-- SECTION 2: Kỳ (Tháng) -->
                  
 

<div class="mb-3 row">
    <div class="col-md-6">
        <label for="readingMonth" class="form-label">Month</label>
      <input type="month" id="readingMonth" name="readingMonth" class="form-control"
       value="<%= selectedMonth %>" onchange="onRoomChange()" required />


    </div>
</div>


                    <!-- SECTION 3: DISPLAY UTILITIES -->
                    <h5 class="text-success border-bottom pb-1">⚡ Utility Information</h5>
                    <%
                        Room currentRoom = null;
                        for (Room r : rooms) {
                            if (String.valueOf(r.getRoomID()).equals(selectedRoomId)) {
                                currentRoom = r;
                                break;
                            }
                        }
                    %>
                    <% for (UtilityType u : utilityTypes) {
    int id = u.getUtilityTypeID();
    String name = u.getUtilityName().toLowerCase();
   Map<Integer, BigDecimal> effectivePriceMap = (Map<Integer, BigDecimal>) request.getAttribute("effectivePriceMap");
BigDecimal price = (effectivePriceMap != null && effectivePriceMap.containsKey(id))
    ? effectivePriceMap.get(id)
    : u.getUnitPrice();

    String unit = u.getUnit();
    Double old = oldIndexMap.getOrDefault(id, 0.0);
    DecimalFormat df = new DecimalFormat("0.##");
    String formattedOld = df.format(old);

    boolean isElectric = name.contains("electricity");
    boolean isWater = name.contains("water");
    boolean isWifi = name.contains("wifi");
    boolean isTrash = name.contains("trash");
                    %>
                    <div class="border p-3 rounded mb-3 shadow-sm">
                        <h6 class="text-dark mb-2"><%= u.getUtilityName() %> - <%= price %> đ / <%= unit %></h6>

                        <input type="hidden" name="typeIds" value="<%= id %>" />

                        <%-- Xử lý từng loại riêng --%>
                        <% if (isElectric) { %>
                        <% if (currentRoom != null && currentRoom.getIsElectricityFree() == 0) { %>
                        <div class="alert alert-warning mb-0">
                           Free Electricity - No meter entry required
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="0" />
                        <% } else { %>
                        <div class="row">
                            <div class="col-md-6">
                                <label>Old:</label>
                                <input type="text" name="old_<%= id %>" class="form-control" readonly value="<%= formattedOld %>" />
                            </div>
                            <div class="col-md-6">
                                <label>New:</label>
                                <input type="number" name="new_<%= id %>" class="form-control" min="0" step="0.01" required />
                            </div>
                        </div>
                        <% } %>

                        <% } else if (isWater) { %>
                        <% if (currentRoom != null && currentRoom.getIsWaterFree() == 0) { %>
                        <div class="alert alert-warning mb-0">
                           Free Water - No meter entry required
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="0" />
                        <% } else { %>
                        <div class="row">
                            <div class="col-md-6">
                                <label>Old:</label>
                                <input type="text" name="old_<%= id %>" class="form-control" readonly value="<%= formattedOld %>" />
                            </div>
                            <div class="col-md-6">
                                <label>New:</label>
                                <input type="number" name="new_<%= id %>" class="form-control" min="0" step="0.01" required />
                            </div>
                        </div>
                        <% } %>

                        <% } else if (isWifi) { %>
                        <% if (currentRoom != null && currentRoom.getIsWifiFree() == 0) { %>
                        <div class="alert alert-warning mb-0">
                            Free Wifi - No Charge
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="0" />
                        <% } else { %>
                        <div class="alert alert-info mb-0">
                                Fixed monthly package: <b><%= price %> đ</b>
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="1" />
                        <% } %>

                        <% } else if (isTrash) { %>
                        <% if (currentRoom != null && currentRoom.getIsTrashFree() == 0) { %>
                        <div class="alert alert-warning mb-0">
                           Free Junk - No Charges
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="0" />
                        <% } else { %>
                        <div class="alert alert-info mb-0">
                           Monthly garbage collection fee: <b><%= price %> đ</b>
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="1" />
                        <% } %>

                        <% } else { %>
                        <div class="alert alert-secondary">
                          Unknown extension - Please check again.
                        </div>
                        <% } %>
                    </div>
                    <% } %>



                    <!-- SECTION 4: ACTIONS -->
                    <div class="d-flex gap-2 mt-4 border-top pt-3">
                        <button type="submit" class="btn btn-primary">📅 Save</button>
                        <a href="<%= ctx %>/admin/usage" class="btn btn-outline-dark">❌ Cancel</a>
                    </div>
                </form>
            </div>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
    