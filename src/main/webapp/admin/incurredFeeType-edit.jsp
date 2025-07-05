<%-- 
    Document   : incurredFeeType-edit
    Created on : Jun 28, 2025, 11:25:04 PM
    Author     : kloane
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="model.IncurredFeeType" %>
<%
    IncurredFeeType feeType = (IncurredFeeType) request.getAttribute("feeType");
%>
<form action="admin/feeType" method="post">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="feeTypeId" value="<%= feeType.getIncurredFeeTypeID() %>">
    <div class="modal-header">
        <h5 class="modal-title">✏️ Edit Incurred Fee</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
    </div>
    <div class="modal-body">
        <div class="mb-3">
            <label class="form-label">Fee Name</label>
            <input type="text" name="feeName" value="<%= feeType.getFeeName() %>" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Default Amount (VND)</label>
            <input type="number" name="defaultAmount" min="0" value="<%= feeType.getDefaultAmount() %>" class="form-control" required>
        </div>
    </div>
    <div class="modal-footer">
        <button class="btn btn-primary" type="submit">Save changes</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
    </div>
</form>
