<%@page import="java.text.DecimalFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="model.UtilityType"%>
<%@page import="java.time.LocalDate"%>
<%@page import="model.Block"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    List<Object[]> rooms = (List<Object[]>) request.getAttribute("rooms");
    List<Block> blocks = (List<Block>) request.getAttribute("blocks");
    List<UtilityType> utilityTypes = (List<UtilityType>) request.getAttribute("utilityTypes");
    Map<Integer, Double> oldIndexMap = (Map<Integer, Double>) request.getAttribute("oldIndexMap");

    String selectedBlockId = (String) request.getAttribute("selectedBlockId");
    String selectedRoomId = (String) request.getAttribute("selectedRoomId");
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>📅 Record Utility Usage</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                padding-top: 56px;
                background: #f8f9fc;
                font-family: 'Segoe UI', sans-serif;
            }
            main {
                margin-left: 260px;
                padding: 2rem 1rem;
                max-width: 800px;
            }
            .container-form {
                background: #ffffff;
                padding: 2rem 2.5rem;
                border-radius: 16px;
                box-shadow: 0 2px 24px rgba(0, 0, 0, 0.05);
                max-width: 800px;
            }
            h3 {
                font-weight: 700;
                color: #2c3e50;
                margin-bottom: 1.5rem;
            }
        </style>
        <script>
            function reloadOnBlockChange() {
                const blockId = document.getElementById('blockId').value;
                window.location.href = '<%= ctx%>/admin/utility?action=record&blockId=' + blockId;
            }
            function onRoomChange() {
                const block = document.getElementById('blockId').value;
                const room = document.getElementById('roomId').value;
                const month = document.getElementById('readingMonth').value;
                if (block && room) {
                    window.location.href = '<%= ctx%>/admin/utility?action=record'
                            + '&blockId=' + encodeURIComponent(block)
                            + '&roomId=' + encodeURIComponent(room)
                            + '&readingMonth=' + encodeURIComponent(month);
                }
            }
        </script>
    </head>
    <body>
        <main>
            <div class="container-form">
                <h3>📅 Record Utility Usage</h3>
                <% if (error != null) {%>
                <div class="alert alert-danger"><%= error%></div>
                <% }%>

              <form method="post" action="${pageContext.request.contextPath}/admin/record-reading">

                    <input type="hidden" name="action" value="record" />

                    <!-- 🔷 SECTION 1: CHOOSE LOCATION -->
                    <div class="mb-4">
                        <h5 class="text-primary border-bottom pb-1">📌 Select Location</h5>
                        <div class="mb-3">
                            <label class="form-label">Block</label>
                            <select name="blockId" id="blockId" class="form-select" onchange="reloadOnBlockChange()" required>
                                <option value="">-- Select Block --</option>
                                <% for (Block b : blocks) {
                                        String id = String.valueOf(b.getBlockID());
                                        String name = b.getBlockName();
                                        String sel = id.equals(selectedBlockId) ? "selected" : "";
                                %>
                                <option value="<%= id%>" <%= sel%>><%= name%></option>
                                <% } %>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Room</label>
                            <select name="roomId" id="roomId" class="form-select" onchange="onRoomChange()" required>
                                <option value="">-- Select Room --</option>
                                <% if (rooms != null) {
                                        for (Object[] r : rooms) {
                                            String id = String.valueOf(r[0]);
                                            String name = String.valueOf(r[1]);
                                            String sel = id.equals(selectedRoomId) ? "selected" : "";
                                %>
                                <option value="<%= id%>" <%= sel%>><%= name%></option>
                                <% }
                                    }%>
                            </select>
                        </div>
                    </div>

                    <!-- Section 2: Kỳ (Tháng) -->
                    <div class="mb-3 row">
                        <div class="col-md-6">
                            <label for="readingMonth" class="form-label">Kỳ (Tháng)</label>
                            <input type="month" id="readingMonth" name="readingMonth" class="form-control"
                                   value="<%= request.getAttribute("readingMonth") != null ? request.getAttribute("readingMonth") : LocalDate.now().toString().substring(0, 7)%>" required />
                        </div>
                    </div>

                    <!-- 🔷 SECTION 2: DISPLAY UTILITIES -->
                    <!-- 🔷 SECTION 2: DISPLAY UTILITIES -->
                    <h5 class="text-success border-bottom pb-1">⚡ Utility Information</h5>
                    <% for (UtilityType u : utilityTypes) {
                            int id = u.getUtilityTypeID();
                            String name = u.getUtilityName().toLowerCase();
                            BigDecimal price = u.getUnitPrice();
                            String unit = u.getUnit();
                            Double old = oldIndexMap.getOrDefault(id, 0.0);
                            DecimalFormat df = new DecimalFormat("0.##");
                            String formattedOld = df.format(old);
                    %>
                    <div class="border p-3 rounded mb-3 shadow-sm">
                        <h6 class="text-dark mb-2"><%= u.getUtilityName()%> - <%= price%> đ / <%= unit%></h6>
                        <input type="hidden" name="typeIds" value="<%= id%>" />

                        <% if (name.equals("wifi") || name.equals("trash")) {%>
                        <div class="alert alert-info mb-0">
                            Price: <b><%= price%> đ</b> (Default)
                        </div>
                        <% } else {%>
                        <div class="row">
                            <div class="col-md-6">
                                <label>Old:</label>
                                <input type="text" name="old_<%= id%>" class="form-control" readonly value="<%= formattedOld%>" />
                            </div>
                            <div class="col-md-6">
                                <label>New:</label>
                                <input type="number" name="new_<%= id%>" class="form-control" min="0" step="0.01" required />
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% }%>


                    <!-- 🔷 SECTION 3: ACTIONS -->
                    <div class="d-flex gap-2 mt-4 border-top pt-3">
                        <button type="submit" class="btn btn-primary">📅 Save</button>
                        <a href="<%= ctx%>/admin/usage" class="btn btn-outline-dark">❌ Cancel</a>
                    </div>
                </form>
            </div>
        </main>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>