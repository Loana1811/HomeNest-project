<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="model.IncurredFeeType" %>

<%
    IncurredFeeType feeType = (IncurredFeeType) request.getAttribute("feeType");
%>

<form class="modal-content" method="post" action="${pageContext.request.contextPath}/admin/feeType">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="feeTypeId" value="<%= feeType.getIncurredFeeTypeID() %>">

    <div class="modal-header">
        <h5 class="modal-title">✏️ Edit Incurred Fee</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>

    <div class="modal-body">
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="mb-3">
            <label class="form-label fw-bold">Fee Name</label>
            <input type="text" name="feeName" id="feeName" class="form-control"
                   value="<%= feeType.getFeeName() %>" required>
            <small class="form-text text-muted">3–50 characters, no special symbols, not all digits</small>
        </div>

        <div class="mb-3">
            <label class="form-label fw-bold">Default Amount (VND)</label>
            <input type="number" name="defaultAmount" min="0" step="0.01"
                   value="<%= feeType.getDefaultAmount() %>" class="form-control" required>
            <small class="form-text text-muted">This is the base amount used when applying to bills</small>
        </div>
    </div>

    <div class="modal-footer">
        <button type="submit" class="btn btn-primary">💾 Save changes</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
    </div>
</form>

<script>
    document.getElementById('feeName').addEventListener('input', function () {
        const val = this.value.trim();
        const regex = /^[\w\s\-]{3,50}$/;
        if (!regex.test(val)) {
            this.setCustomValidity("Name must be 3–50 characters (letters, numbers, hyphens)");
        } else if (/^\d+$/.test(val)) {
            this.setCustomValidity("Name cannot be all digits.");
        } else {
            this.setCustomValidity("");
        }
    });
</script>
