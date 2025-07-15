<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%@ page import="model.UtilityType" %>

<%
    UtilityType utility = (UtilityType) request.getAttribute("utility");
    Map<String, List<Object[]>> blockRoomMap = (Map<String, List<Object[]>>) request.getAttribute("blockRoomMap");
    String[] units = {"kWh", "m3", "Month", "Person", "m2", "Time"};
    String error = (String) request.getAttribute("error");
%>

<form id="editUtilityForm" class="modal-content" method="POST" action="<%= request.getContextPath()%>/admin/utility">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="<%= utility.getUtilityTypeID()%>">

    <div class="modal-header">
        <h5 class="modal-title">✏️ Edit Utility</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
    </div>

    <div class="modal-body">
        <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <!-- Utility Name -->
        <div class="mb-3">
            <label for="utilityName" class="form-label fw-bold">Utility Name</label>
            <input type="text" name="name" id="utilityName" class="form-control"
                   value="<%= utility.getUtilityName() %>" required minlength="3" maxlength="50" />
        </div>

        <!-- Unit and Price -->
        <div class="mb-3">
            <label for="utilityUnit" class="form-label fw-bold">Unit</label>
            <select name="unit" id="utilityUnit" class="form-select" required onchange="showCustomUnit(this)">
                <% for (String unit : units) { %>
                    <option value="<%= unit %>" <%= unit.equals(utility.getUnit()) ? "selected" : "" %>><%= unit %></option>
                <% } %>
                <option value="Other" <%= java.util.Arrays.asList(units).contains(utility.getUnit()) ? "" : "selected" %>>Other (custom)...</option>
            </select>
            <input type="text" name="customUnit" id="customUnit"
                   class="form-control mt-2"
                   style="<%= java.util.Arrays.asList(units).contains(utility.getUnit()) ? "display:none;" : "" %>"
                   value="<%= java.util.Arrays.asList(units).contains(utility.getUnit()) ? "" : utility.getUnit() %>"
                   placeholder="Enter custom unit" maxlength="20">
        </div>

        <div class="mb-2">
            <label class="form-label">Current Price (VND)</label>
            <input type="text" class="form-control" readonly style="background:#f3f4f6;"
                   value="<%= String.format("%,.0f", utility.getUnitPrice().doubleValue()) %>">
        </div>

        <div class="mb-2">
            <label for="utilityPrice" class="form-label">New Price (VND)</label>
            <input type="number" step="0.01" min="0.01" name="price" id="utilityPrice" class="form-control" required />
        </div>

        <!-- Select Rooms -->
        <div class="mb-3">
            <label class="form-label fw-bold d-flex justify-content-between align-items-center">
                <span>✅ Select rooms to apply this utility</span>
                <span>
                    <input type="checkbox" id="selectAll" class="form-check-input me-1" onclick="toggleAllRooms(this)">
                    <label for="selectAll" class="form-check-label">Select all</label>
                </span>
            </label>

            <% if (blockRoomMap != null && !blockRoomMap.isEmpty()) {
                for (Map.Entry<String, List<Object[]>> entry : blockRoomMap.entrySet()) {
                    String blockName = entry.getKey();
                    List<Object[]> roomList = entry.getValue();
                    String blockId = blockName.replaceAll("\\s+", "_");
            %>
            <div class="mb-3 border rounded p-3 bg-light">
                <div class="form-check mb-2">
                    <input class="form-check-input" type="checkbox" id="block_<%= blockId %>" onclick="toggleBlock('<%= blockId %>')">
                    <label class="form-check-label fw-bold" for="block_<%= blockId %>">🏢 Block <%= blockName %></label>
                </div>
                <div class="row ms-2">
                    <% for (Object[] room : roomList) {
                        int roomId = (Integer) room[0];
                        String roomName = (String) room[1];
                        boolean isChecked = (Boolean) room[2];
                    %>
                    <div class="col-md-6 mb-2">
                        <div class="form-check">
                            <input class="form-check-input block_<%= blockId %> room-checkbox"
                                   type="checkbox" name="roomIds" value="<%= roomId %>"
                                   id="room_<%= roomId %>" <%= isChecked ? "checked" : "" %>>
                            <label class="form-check-label" for="room_<%= roomId %>">
                                Room <%= roomName %>
                            </label>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>
            <% }
            } else { %>
            <div class="alert alert-info">⚠️ No rooms found.</div>
            <% } %>
        </div>
    </div>

    <div class="modal-footer">
        <button type="submit" class="btn btn-primary">💾 Save Changes</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
    </div>
</form>

<script>
document.getElementById('editUtilityForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const form = this;
    const formData = new FormData(form);

    fetch(form.action, {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(html => {
        // Nếu server trả về trang lỗi => replace modal content
        if (html.includes('<form') && html.includes('id="editUtilityForm"')) {
            document.querySelector('#editUtilityModal .modal-content').innerHTML = html;
        } else {
            // Thành công => reload list & đóng modal
            document.getElementById('editUtilityModal').classList.remove('show');
            document.querySelector('.modal-backdrop')?.remove();
            document.body.classList.remove('modal-open');
            document.body.style = '';
            // 👉 Bạn có thể dùng AJAX để reload list hoặc reload toàn trang:
            location.reload();
        }
    })
    .catch(err => {
        alert('❌ Failed to update. Please try again.');
        console.error(err);
    });
});
</script>
