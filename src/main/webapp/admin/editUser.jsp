<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .edit-container {
            max-width: 600px;
            margin: 40px auto;
            background: #fff;
            border-radius: 18px;
            box-shadow: 0 8px 32px rgba(102,166,255,0.1);
            padding: 38px 36px 30px 36px;
            position: relative;
            animation: slideIn 0.6s;
        }
        @keyframes slideIn {
            from {
                opacity:0;
                transform: translateY(30px);
            }
            to {
                opacity:1;
                transform: translateY(0);
            }
        }
        .edit-header {
            text-align: center;
            margin-bottom: 30px;
        }
        .edit-header h2 {
            color: #2471a3;
            font-weight: 800;
            letter-spacing: 1px;
            margin-bottom: 6px;
        }
        .edit-header p {
            color: #5a5a5a;
            font-size: 1.1rem;
        }
        .form-section {
            margin-bottom: 20px;
        }
        .form-floating > label {
            font-weight: 500;
        }
        .required {
            color: #dc3545;
            font-weight: bold;
        }
        .btn-custom {
            min-width: 140px;
            font-weight: 600;
            border-radius: 25px;
            padding: 10px 32px;
            font-size: 1rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .btn-primary {
            background: linear-gradient(90deg,#2471a3,#66a6ff);
            border: none;
        }
        .btn-primary:hover {
            background: linear-gradient(90deg,#66a6ff,#2471a3);
        }
        .btn-secondary {
            background: #888;
            border: none;
        }
        .alert-info {
            margin-bottom: 18px;
        }
        .role-badge {
            margin-left: 7px;
            font-size: 13px;
            padding: 3px 12px;
            border-radius: 14px;
            background: #e1f5fe;
            color: #1a237e;
            font-weight: 600;
            letter-spacing: 0.5px;
        }
    </style>
</head>
<body>
    <div class="edit-container">
        <div class="edit-header">
            <h2>Edit User</h2>
            <p>Update manager account information</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${user.role.roleName eq 'Admin'}">
            <div class="alert alert-info">
                Admin account cannot be edited. (View only)
            </div>
        </c:if>

        <form 
            action="${pageContext.request.contextPath}/admin/account" 
            method="post" 
            novalidate
            <c:if test="${user.role.roleName eq 'Admin'}">onsubmit="return false;"</c:if>
        >
            <input type="hidden" name="action" value="edit"/>
            <input type="hidden" name="userID" value="${user.userID}"/>
            <input type="hidden" name="roleID" value="${user.role.roleID}"/>
            <!-- Full Name -->
            <div class="form-section">
                <div class="form-floating mb-3">
                    <input type="text" 
                           id="userFullName" 
                           name="fullName" 
                           class="form-control"
                           placeholder="Full Name" 
                           value="${user.userFullName}"
                           <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                           required />
                    <label for="userFullName">Full Name <span class="required">*</span></label>
                </div>
            </div>
            <!-- Email -->
            <div class="form-section">
                <div class="form-floating mb-3">
                    <input type="email"
                           id="email"
                           name="email"
                           class="form-control"
                           placeholder="Email"
                           value="${user.email}"
                           <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                           required />
                    <label for="email">Email <span class="required">*</span></label>
                </div>
            </div>
            <!-- Phone Number -->
            <div class="form-section">
                <div class="form-floating mb-3">
                    <input type="text"
                           id="phoneNumber"
                           name="phoneNumber"
                           class="form-control"
                           placeholder="Phone Number"
                           value="${user.phoneNumber}"
                           <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                           required />
                    <label for="phoneNumber">Phone Number <span class="required">*</span></label>
                </div>
            </div>
            <!-- Role (readonly, just for info) -->
            <div class="form-section">
                <div class="form-floating mb-3">
                    <input type="text"
                           class="form-control"
                           id="role"
                           name="role"
                           value="${user.role.roleName}" readonly
                           style="background-color:#eaf4fa;"/>
                    <label for="role">Role</label>
                    <span class="role-badge">${user.role.roleName}</span>
                </div>
            </div>
           
            <c:if test="${user.role.roleName eq 'Manager'}">
                <div class="form-section">
                    <div class="form-floating mb-3">
                        <select id="blockID" name="blockID" class="form-select" required>
                            <option value="">Select Block</option>
                            <c:forEach var="block" items="${blockList}">
                                <option value="${block.blockID}" 
                                    <c:if test="${user.block != null && user.block.blockID == block.blockID}">selected</c:if>>
                                    ${block.blockName}
                                </option>
                            </c:forEach>
                        </select>
                        <label for="blockID">Block <span class="required">*</span></label>
                    </div>
                </div>
            </c:if>
          
            <div class="form-section">
                <div class="form-floating mb-3">
                    <input type="password"
                           id="password"
                           name="password"
                           class="form-control"
                           placeholder="Password"
                           <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                    />
                    <label for="password">Password (leave blank to keep current)</label>
                </div>
            </div>
            <!-- Status -->
            <div class="form-section">
                <div class="form-floating mb-3">
                    <select id="status" name="status" class="form-select" <c:if test="${user.role.roleName eq 'Admin'}">disabled</c:if>>
                        <option value="Active" <c:if test="${user.userStatus eq 'Active'}">selected</c:if>>Active</option>
                        <option value="Inactive" <c:if test="${user.userStatus eq 'Inactive'}">selected</c:if>>Inactive</option>
                    </select>
                    <label for="status">Status</label>
                </div>
            </div>
            <!-- Action Buttons -->
            <div class="d-flex justify-content-center gap-3">
                <button type="submit" class="btn btn-primary btn-custom"
                    <c:if test="${user.role.roleName eq 'Admin'}">disabled</c:if>
                >Save</button>
                <a href="${pageContext.request.contextPath}/viewListAccount" class="btn btn-secondary btn-custom">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</body>
</html>
