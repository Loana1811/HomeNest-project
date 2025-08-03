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

<!DOCTYPE html>
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
    </head>
    <body>
        <!-- Sidebar -->
        <div class="sidebar">
            <h4 class="text-center mb-4">Manager Menu</h4>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/dashboard.jsp?idManager=<%= manager.getUserID() %>">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/adminReport">Report Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/notification?idManager=<%= idUser %>">Notification Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx %>/manager/utility?idManager=<%= idUser %>">Usage Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/manage-requests?idManager=<%= idUser %>">Rental Request Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-danger" href="<%= ctx%>/Logouts">Logout</a>
                </li>
            </ul>
        </div>

        <!-- Main Content -->

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
                            <label for="readingMonth" class="form-label">Kỳ (Tháng)</label>
                            <input type="month" id="readingMonth" name="readingMonth" class="form-control"
                                   value="<%= request.getAttribute("readingMonth") != null ? request.getAttribute("readingMonth") : LocalDate.now().toString().substring(0,7) %>" required />
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
    BigDecimal price = u.getUnitPrice();
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
                            Miễn phí Điện - Không cần nhập chỉ số
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
                            Miễn phí Nước - Không cần nhập chỉ số
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
                            Miễn phí Wifi - Không tính phí
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="0" />
                        <% } else { %>
                        <div class="alert alert-info mb-0">
                            Gói cước cố định hàng tháng: <b><%= price %> đ</b>
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="1" />
                        <% } %>

                        <% } else if (isTrash) { %>
                        <% if (currentRoom != null && currentRoom.getIsTrashFree() == 0) { %>
                        <div class="alert alert-warning mb-0">
                            Miễn phí Rác - Không tính phí
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="0" />
                        <% } else { %>
                        <div class="alert alert-info mb-0">
                            Phí thu gom rác hàng tháng: <b><%= price %> đ</b>
                        </div>
                        <input type="hidden" name="new_<%= id %>" value="1" />
                        <% } %>

                        <% } else { %>
                        <div class="alert alert-secondary">
                            Tiện ích không xác định - Vui lòng kiểm tra lại.
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
