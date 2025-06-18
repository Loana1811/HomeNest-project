<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    Map<String, List<Object[]>> blockRoomMap = (Map<String, List<Object[]>>) request.getAttribute("blockRoomMap");
    String[] units = {"kWh", "m3", "Month", "Person", "m2", "Time"};
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Utility</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">
    <h3 class="mb-3">➕ Add New Utility</h3>

    <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
    <% } %>

    <form action="utility?action=create" method="post" onsubmit="return validateForm()">
        <div class="mb-3">
            <label>Name</label>
            <input type="text" name="name" class="form-control" required />
        </div>

        <div class="mb-3">
            <label>Unit</label>
            <select name="unit" class="form-select" required>
                <% for (String unit : units) { %>
                    <option value="<%= unit %>"><%= unit %></option>
                <% } %>
            </select>
        </div>

        <div class="mb-3">
            <label>Price (VND)</label>
            <input type="number" step="0.01" min="0" name="price" class="form-control" required />
        </div>

        <div class="mb-3">
            <label class="form-label fw-bold d-flex justify-content-between align-items-center">
                <span>✅ Select rooms to apply this utility</span>
                <span>
                    <input type="checkbox" id="selectAll" class="form-check-input me-1" onclick="toggleAllRooms(this)">
                    <label for="selectAll" class="form-check-label">Select all</label>
                </span>
            </label>
            <small class="text-muted mb-2 d-block">Grouped by block</small>

            <% if (blockRoomMap != null) {
                for (Map.Entry<String, List<Object[]>> entry : blockRoomMap.entrySet()) {
                    String blockName = entry.getKey();
                    List<Object[]> roomList = entry.getValue();
                    String blockId = blockName.replaceAll("\\s", "_");
            %>
            <div class="mb-3 border rounded p-3 bg-light">
                <div class="form-check mb-2">
                    <input class="form-check-input" type="checkbox" id="block_<%= blockId %>" onclick="toggleBlock('<%= blockId %>')" />
                    <label class="form-check-label fw-bold" for="block_<%= blockId %>">
                        🏢 Block <%= blockName %>
                    </label>
                </div>

                <div class="row ms-2">
                    <% for (Object[] room : roomList) {
                        int roomId = (Integer) room[0];
                        String roomName = (String) room[1];
                    %>
                    <div class="col-md-6 mb-2">
                        <div class="form-check">
                            <input class="form-check-input block_<%= blockId %> room-checkbox"
                                   type="checkbox" name="roomIds" value="<%= roomId %>" id="room_<%= roomId %>">
                            <label class="form-check-label" for="room_<%= roomId %>">
                                Room <%= roomName %>
                            </label>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>
            <% } } %>
        </div>

        <div class="mt-4">
            <button type="submit" class="btn btn-success">✅ Create</button>
            <a href="utility?action=list" class="btn btn-secondary">Cancel</a>
        </div>
    </form>

    <script>
        function toggleAllRooms(source) {
            const allRoomCheckboxes = document.querySelectorAll('.room-checkbox');
            allRoomCheckboxes.forEach(cb => cb.checked = source.checked);

            const allBlockCheckboxes = document.querySelectorAll('input[id^="block_"]');
            allBlockCheckboxes.forEach(cb => cb.checked = source.checked);
        }

        function toggleBlock(blockId) {
            const blockCheckbox = document.getElementById("block_" + blockId);
            const checkboxes = document.querySelectorAll(".block_" + blockId);
            checkboxes.forEach(cb => cb.checked = blockCheckbox.checked);
        }

        function validateForm() {
            const price = document.querySelector('input[name="price"]').value;
            const name = document.querySelector('input[name="name"]').value;
            if (isNaN(price) || parseFloat(price) < 0 || price.trim() === "" || name.trim() === "") {
                alert("⚠️ Please fill in all required fields correctly.");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>
