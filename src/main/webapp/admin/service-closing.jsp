<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Block, model.RoomStatusDTO" %>
<%
    Map<Block, List<RoomStatusDTO>> blockRoomMap = (Map<Block, List<RoomStatusDTO>>) request.getAttribute("blockRoomMap");
    String ctx = request.getContextPath();
    String selectedMonth = (String) request.getAttribute("selectedMonth");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>🧾 Service Closing</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h3>🧾 Service Closing - <%= selectedMonth != null ? selectedMonth : "Select Month" %></h3>

    <!-- Filter -->
    <form class="row g-3 mb-4" method="get" action="<%= ctx %>/admin/close-service">
        <div class="col-auto">
            <input type="month" name="month" class="form-control" value="<%= selectedMonth %>" required />
        </div>
        <div class="col-auto">
            <button class="btn btn-primary">🔍 Filter</button>
        </div>
    </form>

    <!-- Block & Room list -->
    <% if (blockRoomMap != null && !blockRoomMap.isEmpty()) {
        for (Map.Entry<Block, List<RoomStatusDTO>> entry : blockRoomMap.entrySet()) {
            Block block = entry.getKey();
            List<RoomStatusDTO> rooms = entry.getValue();
    %>
    <div class="card mb-4">
        <div class="card-header">Block <%= block.getBlockName() %></div>
        <div class="card-body">
            <% for (RoomStatusDTO room : rooms) { %>
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span><strong>Room <%= room.getRoomName() %></strong></span>
                <% if (room.isClosed()) { %>
                    <span class="text-success">☑️ Closed</span>
                <% } else { %>
                    <button class="btn btn-success btn-sm"
                            onclick="loadRecordForm(<%= block.getBlockID() %>, <%= room.getRoomId() %>)">
                        Chốt
                    </button>
                <% } %>
            </div>
            <% } %>
        </div>
    </div>
    <% } } else { %>
    <div class="alert alert-info">📭 No room data available.</div>
    <% } %>
</div>

<!-- Modal -->
<div class="modal fade" id="recordModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content" id="recordModalContent">
      <!-- Form từ utility-record.jsp sẽ được load tại đây -->
    </div>
  </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function loadRecordForm(blockId, roomId) {
    const month = document.querySelector('input[name="month"]').value;
    const url = `<%= ctx %>/admin/record-reading?blockId=${blockId}&roomId=${roomId}&month=${month}`;

    fetch(url)
      .then(res => res.text())
      .then(html => {
        document.getElementById('recordModalContent').innerHTML = html;
        new bootstrap.Modal(document.getElementById('recordModal')).show();
      });
}
</script>
</body>
</html>
