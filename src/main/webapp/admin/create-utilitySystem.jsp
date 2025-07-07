<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>

<%
    Map<String, List<Object[]>> blockRoomMap = (Map<String, List<Object[]>>) request.getAttribute("blockRoomMap");
    String[] units = {"kWh", "m3", "Month", "Person", "m2", "Time"};
    String error = (String) request.getAttribute("error");
%>

<!-- Modal Thêm Tiện Ích -->
<div class="modal fade" id="addUtilityModal" tabindex="-1" aria-labelledby="addUtilityModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-scrollable modal-dialog-centered">
        <form id="addUtilityForm" class="modal-content" method="POST" action="<%= request.getContextPath()%>/admin/utility">
            <input type="hidden" name="action" value="create">

            <div class="modal-header">
                <h5 class="modal-title" id="addUtilityModalLabel">➕ Add New Utility</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body">
                <!-- Hiển thị lỗi nếu có -->
                <% if (error != null) {%>
                <div class="alert alert-danger"><%= error%></div>
                <% } %>

                <!-- Block Name -->
                <div class="mb-3">
                    <div style="display: flex; align-items: flex-start; margin-bottom: 8px;">
                        <div style="border-left: 4px solid #6366f1; height: 32px; margin-right: 10px;"></div>
                        <div>
                            <div style="font-weight: bold; font-size: 1.07em;">Utility Name</div>
                            <div style="color: #666; font-style: italic;">Enter a widget name (no special characters, no numbers, 3-50 characters).</div>
                        </div>
                    </div>
                    <input type="text" name="name" id="utilityName" class="form-control" required />
                </div>

                <!-- Block Đơn vị và giá -->
                <div class="mb-3">
                    <div style="display: flex; align-items: flex-start; margin-bottom: 8px;">
                        <div style="border-left: 4px solid #22c55e; height: 32px; margin-right: 10px;"></div>
                        <div>
                            <div style="font-weight: bold; font-size: 1.07em;">Unit and Price</div>
                            <div style="color: #666; font-style: italic;">Enter unit and price information</div>
                        </div>
                    </div>
                    <!-- Unit -->
                    <div class="mb-2">
                        <label for="utilityUnit" class="form-label">Unit</label>
                        <select name="unit" id="utilityUnit" class="form-select" required onchange="showCustomUnit(this)">
                            <option value="kWh">kWh</option>
                            <option value="m3">m3</option>
                            <option value="Month">Month</option>
                            <option value="Person">Person</option>
                            <option value="m2">m2</option>
                            <option value="Time">Time</option>
                            <option value="Other">Other (custom)...</option>
                        </select>
                        <input type="text" name="customUnit" id="customUnit" class="form-control mt-2"
                               style="display:none;" placeholder="Nhập đơn vị tùy chọn">
                    </div>
                    <!-- Price -->
                    <div class="mb-2">
                        <label for="utilityPrice" class="form-label">Price (VND)</label>
                        <input type="number" step="0.01" min="0.01" name="price" id="utilityPrice" class="form-control" required />
                        <small class="form-text text-muted">
                            Price must be entered in VND</small>
                    </div>
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
                    <small class="text-muted mb-2 d-block">Grouped by block</small>

                    <% if (blockRoomMap != null && !blockRoomMap.isEmpty()) {
                            for (Map.Entry<String, List<Object[]>> entry : blockRoomMap.entrySet()) {
                                String blockName = entry.getKey();
                                List<Object[]> roomList = entry.getValue();
                                String blockId = blockName.replaceAll("\\s+", "_");
                    %>
                    <div class="mb-3 border rounded p-3 bg-light">
                        <div class="form-check mb-2">
                            <input class="form-check-input" type="checkbox" id="block_<%= blockId%>" onclick="toggleBlock('<%= blockId%>')">
                            <label class="form-check-label fw-bold" for="block_<%= blockId%>">🏢 Block <%= blockName%></label>
                        </div>

                        <div class="row ms-2">
                            <% for (Object[] room : roomList) {
                                    int roomId = (Integer) room[0];
                                    String roomName = (String) room[1];
                            %>
                            <div class="col-md-6 mb-2">
                                <div class="form-check">
                                    <input class="form-check-input block_<%= blockId%> room-checkbox"
                                           type="checkbox" name="roomIds" value="<%= roomId%>" id="room_<%= roomId%>">
                                    <label class="form-check-label" for="room_<%= roomId%>">
                                        Room <%= roomName%>
                                    </label>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                    <%   }
                    } else { %>
                    <div class="alert alert-info">⚠️ No rooms found to assign.</div>
                    <% }%>
                </div>
            </div>

            <div class="modal-footer">
                <button type="submit" class="btn btn-success">+ Add</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- SCRIPT: Ẩn/hiện custom unit & kiểm tra hợp lệ -->
<script>
// Ẩn/hiện customUnit khi chọn Other
    function showCustomUnit(sel) {
        const customInput = document.getElementById('customUnit');
        if (sel.value === "Other") {
            customInput.style.display = "block";
            customInput.required = true;
        } else {
            customInput.style.display = "none";
            customInput.required = false;
            customInput.value = "";
        }
    }

// Kiểm tra hợp lệ trường "Name"
    document.getElementById('utilityName').addEventListener('input', function () {
        const val = this.value.trim();
        const regex = /^[\w\s\-]{3,50}$/;
        if (!regex.test(val)) {
            this.setCustomValidity("Name must be 3-50 characters (letters, numbers, space, hyphen, underscore only).");
        } else if (/^\d+$/.test(val)) {
            this.setCustomValidity("Name cannot be all digits.");
        } else {
            this.setCustomValidity("");
        }
    });

// Kiểm tra hợp lệ trường "customUnit"
    document.getElementById('customUnit').addEventListener('input', function () {
        const val = this.value.trim();
        const regex = /^[\w\s\-]{1,20}$/;
        if (!regex.test(val)) {
            this.setCustomValidity("Unit must be 1-20 characters (letters, numbers, space, hyphen, underscore only).");
        } else {
            this.setCustomValidity("");
        }
    });

// Kiểm tra price lớn hơn 0
    document.getElementById('utilityPrice').addEventListener('input', function () {
        if (parseFloat(this.value) <= 0) {
            this.setCustomValidity("Price must be greater than 0.");
        } else {
            this.setCustomValidity("");
        }
    });

// Script chọn tất cả room (giữ nguyên như cũ)
    function toggleAllRooms(source) {
        document.querySelectorAll('.room-checkbox').forEach(cb => cb.checked = source.checked);
        document.querySelectorAll('input[id^="block_"]').forEach(cb => cb.checked = source.checked);
    }

    function toggleBlock(blockId) {
        const blockCheckbox = document.getElementById("block_" + blockId);
        const checkboxes = document.querySelectorAll(".block_" + blockId);
        checkboxes.forEach(cb => cb.checked = blockCheckbox.checked);
    }
</script>
