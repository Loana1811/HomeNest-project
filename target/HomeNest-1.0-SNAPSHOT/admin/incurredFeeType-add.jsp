<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    String error = (String) request.getAttribute("error");
%>

<!-- Modal Add New Incurred Fee -->
<div class="modal fade" id="addFeeModal" tabindex="-1" aria-labelledby="addFeeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content" method="POST" action="${pageContext.request.contextPath}/admin/feeType">
            <input type="hidden" name="action" value="create">

            <div class="modal-header">
                <h5 class="modal-title" id="addFeeModalLabel">➕ Add New Incurred Fee</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="mb-3">
                    <label class="form-label fw-bold">Fee Name</label>
                    <input type="text" name="feeName" id="feeName" class="form-control" required>
                    <small class="form-text text-muted">3–50 characters, no special symbols, not all digits</small>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Default Amount (VND)</label>
                    <input type="number" name="defaultAmount" min="0" step="0.01" class="form-control" required>
                    <small class="form-text text-muted">Enter the base amount for this fee</small>
                </div>
            </div>

            <div class="modal-footer">
                <button type="submit" class="btn btn-success">+ Add</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Inline validation -->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const feeNameInput = document.getElementById('feeName');
        if (feeNameInput) {
            feeNameInput.addEventListener('input', function () {
                const val = this.value.trim();
                const regex = /^[\w\s\-]{3,50}$/;
                if (!regex.test(val)) {
                    this.setCustomValidity("Name must be 3–50 characters (letters, numbers, hyphens, no special chars)");
                } else if (/^\d+$/.test(val)) {
                    this.setCustomValidity("Name cannot be all digits.");
                } else {
                    this.setCustomValidity("");
                }
            });
        }
    });
</script>
