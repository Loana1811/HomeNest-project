<form action="reset-password" method="post">
    <input type="hidden" name="token" value="${token}">
    <label>M?t kh?u m?i:</label>
    <input type="password" name="password" class="form-control" required>
    <label>Xác nh?n m?t kh?u:</label>
    <input type="password" name="confirm" class="form-control" required>
    <button type="submit" class="btn btn-success mt-2">??t l?i m?t kh?u</button>
</form>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
</c:if>
