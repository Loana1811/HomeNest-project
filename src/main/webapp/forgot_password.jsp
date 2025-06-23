<form action="<%= request.getContextPath()%>/forgot_password" method="post">
    <label>Email:</label>
    <input type="email" name="email" class="form-control" required>
    <button type="submit" class="btn btn-primary mt-2">G?i link ??t l?i m?t kh?u</button>
</form>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
</c:if>
