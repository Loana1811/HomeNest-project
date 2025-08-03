<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.UtilityType, model.IncurredFeeType" %>

<%
    List<UtilityType> systemList = (List<UtilityType>) request.getAttribute("systemList");
    List<IncurredFeeType> feeList = (List<IncurredFeeType>) request.getAttribute("feeList");
  String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");
%>

<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
        <!-- MAIN CONTENT -->
      <div class="main-content">
            <h3 class="mb-3">🧾 Utilities Management</h3>
            <% if (error != null) { %>
            <div class="alert alert-danger"><%= error %></div>
            <% } %>

              <div class="mb-3 d-flex gap-3">
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addUtilityModal">➕ Add Utility</button>


            </div>

<!-- Placeholder chứa modal sẽ load qua AJAX -->
<div id="addUtilityModalContainer"></div>



            <!-- SYSTEM UTILITIES -->
            <h5 class="mt-4">🔐 System-defined Utilities</h5>
            <% if (systemList != null && !systemList.isEmpty()) { %>
            <table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Unit</th>
                        <th>Price (VND)</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (UtilityType u : systemList) { %>
                    <tr>
                        <td><%= u.getUtilityName() %></td>
                        <td><%= u.getUnit() %></td>
                        <td><%= String.format("%,.0f", u.getUnitPrice().doubleValue()) %></td>
                        <td>
                            <a href="#" class="btn btn-sm btn-outline-primary"
                               data-bs-toggle="modal"
                               data-bs-target="#editUtilityModal"
                               onclick="openEditModal(<%= u.getUtilityTypeID() %>)">✏️</a>
                            <span class="text-warning" title="Default utility cannot be deleted.">⚠️</span>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="alert alert-info">No system-defined utilities found.</div>
            <% } %>

           

            <!-- PRICE HISTORY BUTTON -->
            <div class="mt-4">
                <a href="utility?action=history" class="btn btn-outline-secondary">📜 View Price History</a>
            </div>

            <!-- MODALS & SCRIPTS -->

            <!-- MODAL: ADD UTILITY (nếu bạn có modal này riêng thì giữ nguyên hoặc include) -->
            <jsp:include page="create-utilitySystem.jsp" /> <!-- Sửa thành createUtility.jsp nếu là tiện ích -->

            <!-- MODAL: EDIT UTILITY (dynamic load, giống cũ) -->
            <div class="modal fade" id="editUtilityModal" tabindex="-1">
                <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                    <div class="modal-content" id="editModalContent">
                        <!-- Content from server will be loaded here -->
                    </div>
                </div>
            </div>

            <!-- MODAL: ADD FEE -->

            <jsp:include page="incurredFeeType-add.jsp" />


            <!-- MODAL: EDIT FEE (dynamic load) -->
            <div class="modal fade" id="editFeeModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content" id="editFeeModalContent">
                        <!-- Nội dung sẽ được load động -->
                    </div>
                </div>
            </div>

            <!-- MODAL: DELETE FEE -->
            <div class="modal fade" id="confirmDeleteFeeModal" tabindex="-1" aria-labelledby="confirmDeleteFeeModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <form action="<%= request.getContextPath() %>/admin/feeType" method="get">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" id="deleteFeeId">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title text-danger">⚠️ Confirm Delete</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                Are you sure you want to delete this fee?
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-danger">✅ Yes, delete</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">❌ Cancel</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- JS SUPPORT -->
            <script>
                function setDeleteFeeId(id) {
                    document.getElementById('deleteFeeId').value = id;
                }

                function openEditFeeModal(id) {
                    fetch('<%= request.getContextPath()%>/admin/feeType?action=editModal&id=' + id)
                            .then(response => {
                                if (!response.ok)
                                    throw new Error("HTTP error " + response.status);
                                return response.text();
                            })
                            .then(html => {
                                const modalContent = document.getElementById('editFeeModalContent');
                                modalContent.innerHTML = html;
                                if (window.editFeeModalObj) {
                                    window.editFeeModalObj.hide();
                                    window.editFeeModalObj.dispose();
                                }
                                window.editFeeModalObj = new bootstrap.Modal(document.getElementById('editFeeModal'));
                                window.editFeeModalObj.show();
                            })
                            .catch(err => {
                                alert("⚠️ Cannot load edit form for fee.");
                            });
                }
            </script>

            <!-- SCRIPT: OPEN EDIT UTILITY MODAL (giống code cũ của bạn) -->
            <script>
                function openEditModal(id) {
                    fetch('<%= request.getContextPath()%>/admin/utility?action=editModal&id=' + id)
                            .then(response => {
                                if (!response.ok)
                                    throw new Error("HTTP error " + response.status);
                                return response.text();
                            })
                            .then(html => {
                                const modalContent = document.getElementById('editModalContent');
                                modalContent.innerHTML = html;
                                if (window.editModalObj) {
                                    window.editModalObj.hide();
                                    window.editModalObj.dispose();
                                }
                                window.editModalObj = new bootstrap.Modal(document.getElementById('editUtilityModal'));
                                window.editModalObj.show();
                            })
                            .catch(err => {
                                console.error("⚠️ Failed to load modal:", err);
                                alert("⚠️ Cannot load edit form. Check console for details.");
                            });
                }
            </script>

            <div class="mt-4">
                <a href="<%= ctx%>/admin/dashboard" class="btn btn-outline-secondary">
                    ← Back to Dashboard
                </a>
            </div>
                   
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
 
</body>
   
</html>
