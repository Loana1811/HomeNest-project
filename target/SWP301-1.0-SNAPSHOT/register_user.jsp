<form action="register-user" method="post">
    <input type="text" name="fullname" placeholder="H? tên" required>
    <input type="email" name="email" placeholder="Email" required>
    <input type="text" name="phone" placeholder="S? ?i?n tho?i" required>
    <input type="password" name="password" placeholder="M?t kh?u" required>
    <input type="password" name="confirm" placeholder="Xác nh?n m?t kh?u" required>
    <button type="submit">T?o User</button>
    <c:if test="${not empty error}"><p style="color:red;">${error}</p></c:if>
    <c:if test="${not empty success}"><p style="color:green;">${success}</p></c:if>
</form>
