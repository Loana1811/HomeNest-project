<%@page import="model.Block"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%
    List<Object[]> rooms = (List<Object[]>) request.getAttribute("rooms");
    List<Object[]> types = (List<Object[]>) request.getAttribute("types");
    List<Block> blocks = (List<Block>) request.getAttribute("blocks");

    String selectedBlockId = (String) request.getAttribute("selectedBlockId");
    String selectedRoomId = (String) request.getAttribute("selectedRoomId");
    String selectedTypeId = (String) request.getAttribute("selectedTypeId");
    Double oldIndex = (Double) request.getAttribute("oldIndex");
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>📥 Record Utility Usage</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        .btn {
            font-weight: 500;
            font-size: 1rem;
            border-radius: 8px;
            transition: all 0.2s ease-in-out;
        }
        .btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        }
        .form-control {
            border-radius: 8px;
            font-size: 1.05rem;
        }
        .form-label {
            font-weight: 600;
        }
        body {
            background: #f8f9fc;
        }
        .container {
            background: #ffffff;
            padding: 2rem 2.5rem;
            border-radius: 16px;
            box-shadow: 0 2px 24px rgba(0, 0, 0, 0.05);
            max-width: 800px;
            margin-top: 2rem;
        }
        h3 {
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 1.5rem;
        }
    </style>

    <script>
        function reloadOnBlockChange() {
            var blockId = document.getElementById('blockId').value;
            window.location.href = '<%= ctx%>/admin/record-reading?blockId=' + blockId;
        }

        function reloadOnTypeChange() {
            var block = document.getElementById('blockId').value;
            var room = document.getElementById('roomId').value;
            var typeId = document.getElementById('typeId').value;
            if (!room) {
                alert('Vui lòng chọn phòng trước');
                document.getElementById('typeId').value = '';
                return;
            }
            window.location.href = '<%= ctx%>/admin/record-reading'
                + '?blockId=' + block
                + '&roomId=' + room
                + '&typeId=' + typeId;
        }
    </script>
</head>
<body class="container mt-4">

    <h3>📥 Record Utility Usage</h3>

    <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
    <% } %>

    <form method="post" action="<%= ctx %>/admin/record-reading">
        <!-- Hidden blockId for POST -->

        <div class="mb-3">
            <label class="form-label">Block</label>
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
            <label class="form-label">Room</label>
            <select name="roomId" id="roomId" class="form-select" required>
                <option value="">-- Select Room --</option>
                <% if (rooms != null) {
                    for (Object[] r : rooms) {
                        String id = String.valueOf(r[0]);
                        String name = String.valueOf(r[1]);
                        String sel = id.equals(selectedRoomId) ? "selected" : "";
                %>
                        <option value="<%= id %>" <%= sel %>><%= name %></option>
                <%  } } %>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Utility Type</label>
            <select name="typeId" id="typeId" class="form-select" onchange="reloadOnTypeChange()" required>
                <option value="">-- Select Utility --</option>
                <% for (Object[] t : types) {
                    String id = String.valueOf(t[0]);
                    String name = String.valueOf(t[1]);
                    String sel = id.equals(selectedTypeId) ? "selected" : "";
                %>
                    <option value="<%= id %>" <%= sel %>><%= name %></option>
                <% } %>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Old Index</label>
            <input type="number" name="oldIndex" class="form-control" readonly value="<%= oldIndex != null ? oldIndex : "" %>" />
        </div>

        <div class="mb-3">
            <label class="form-label">New Index</label>
            <input type="number" name="newIndex" class="form-control" step="0.01" min="0" required />
        </div>

        <div class="d-flex gap-2 mt-4">
            <button type="submit" class="btn btn-primary">💾 Save</button>
            <a href="<%= ctx %>/admin/utility?action=list" class="btn btn-outline-dark">❌ Cancel</a>
            <a href="<%= ctx %>/admin/dashboard" class="btn btn-outline-secondary">⬅️ Back to Dashboard</a>
        </div>
    </form>

</body>
</html>
