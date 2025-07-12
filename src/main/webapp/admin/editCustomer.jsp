<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Customer</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>

            :root {
                --primary: #3498db;
                --secondary: #2c3e50;
                --success: #28a745;
                --danger: #dc3545;
                --warning: #ffc107;
                --info: #17a2b8;
                --light: #f8f9fa;
                --dark: #343a40;
            }

            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .container {
                max-width: 800px;
                margin-top: 30px;
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                padding: 40px;
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                border: 1px solid rgba(255, 255, 255, 0.2);
            }

            .form-header {
                text-align: center;
                margin-bottom: 40px;
                position: relative;
            }

            .form-header::before {
                content: '';
                position: absolute;
                bottom: -10px;
                left: 50%;
                transform: translateX(-50%);
                width: 80px;
                height: 4px;
                background: linear-gradient(90deg, var(--primary), var(--info));
                border-radius: 2px;
            }

            .form-header h2 {
                color: var(--dark);
                font-weight: 700;
                font-size: 2.5rem;
                margin-bottom: 10px;
                background: linear-gradient(135deg, var(--primary), var(--info));
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }

            .form-header p {
                color: #6c757d;
                font-size: 1.1rem;
                margin: 0;
            }

            .form-section {
                background: rgba(248, 249, 250, 0.8);
                padding: 25px;
                border-radius: 15px;
                margin-bottom: 25px;
                border-left: 4px solid var(--primary);
            }

            .section-title {
                color: var(--dark);
                font-weight: 600;
                font-size: 1.3rem;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .form-floating {
                margin-bottom: 20px;
            }

            .form-floating > .form-control,
            .form-floating > .form-select {
                height: 60px;
                border: 2px solid #e9ecef;
                border-radius: 12px;
                font-size: 16px;
                transition: all 0.3s ease;
            }

            .form-floating > .form-control:focus,
            .form-floating > .form-select:focus {
                border-color: var(--primary);
                box-shadow: 0 0 0 0.25rem rgba(52, 152, 219, 0.15);
                transform: translateY(-2px);
            }

            .form-floating > label {
                font-weight: 500;
                color: #6c757d;
                padding-left: 1rem;
            }

            .required {
                color: var(--danger);
                font-weight: bold;
            }

            .btn-group-custom {
                display: flex;
                gap: 15px;
                justify-content: center;
                margin-top: 30px;
                flex-wrap: wrap;
            }

            .btn {
                padding: 12px 30px;
                border-radius: 25px;
                font-weight: 600;
                font-size: 16px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                transition: all 0.3s ease;
                border: none;
                position: relative;
                overflow: hidden;
            }

            .btn::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
                transition: left 0.5s;
            }

            .btn:hover::before {
                left: 100%;
            }

            .btn-primary {
                background: linear-gradient(135deg, var(--primary), var(--info));
                color: white;
                min-width: 150px;
            }

            .btn-primary:hover {
                background: linear-gradient(135deg, var(--info), var(--primary));
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(52, 152, 219, 0.3);
            }

            .btn-secondary {
                background: linear-gradient(135deg, #6c757d, #495057);
                color: white;
                min-width: 120px;
            }

            .btn-secondary:hover {
                background: linear-gradient(135deg, #495057, #6c757d);
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(108, 117, 125, 0.3);
            }

            .alert {
                border-radius: 12px;
                border: none;
                padding: 15px 20px;
                margin-bottom: 25px;
                font-weight: 500;
            }

            .alert-danger {
                background: linear-gradient(135deg, rgba(220, 53, 69, 0.1), rgba(220, 53, 69, 0.05));
                color: var(--danger);
                border-left: 4px solid var(--danger);
            }

            .status-badge {
                padding: 8px 16px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .status-potential {
                background: linear-gradient(135deg, rgba(255, 193, 7, 0.2), rgba(255, 193, 7, 0.1));
                color: #856404;
                border: 1px solid rgba(255, 193, 7, 0.3);
            }

            .status-converted {
                background: linear-gradient(135deg, rgba(40, 167, 69, 0.2), rgba(40, 167, 69, 0.1));
                color: #155724;
                border: 1px solid rgba(40, 167, 69, 0.3);
            }

            @media (max-width: 768px) {
                .container {
                    margin: 20px;
                    padding: 25px;
                }

                .form-header h2 {
                    font-size: 2rem;
                }

                .btn-group-custom {
                    flex-direction: column;
                    align-items: center;
                }

                .btn {
                    width: 100%;
                    max-width: 200px;
                }
            }

            .input-icon {
                position: relative;
            }

            .input-icon i {
                position: absolute;
                right: 15px;
                top: 50%;
                transform: translateY(-50%);
                color: #6c757d;
                z-index: 5;
            }

            .animate-form {
                animation: slideInUp 0.6s ease-out;
            }

            @keyframes slideInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }
        </style>


    </head>
    <body>
        <div class="container animate-form">
            <div class="form-header">
                <h2><i class="fas fa-user-edit"></i> Edit Customer</h2>
                <p>Update customer information and details</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    ${error}
                </div>
            </c:if>

            <form id="editCustomerForm" action="${pageContext.request.contextPath}/admin/account" method="post" novalidate>
                <input type="hidden" name="action" value="edit"/>
                <input type="hidden" name="customerID" value="${customer.customerID}"/>
                <!-- Truyền password cũ từ server xuống -->
                <input type="hidden" id="oldCustomerPassword" value="${customer.customerPassword}"/>

                <div class="form-section">
                    <div class="section-title">
                        <i class="fas fa-user"></i> Basic Information
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="text" id="customerFullName" name="fullName" class="form-control"
                                       placeholder="Full Name" required value="${customer.customerFullName}"/>
                                <label for="customerFullName">Full Name <span class="required">*</span></label>
                                <div class="invalid-feedback"></div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="email" id="email" name="email" class="form-control"
                                       placeholder="Email Address" required value="${customer.email}"/>
                                <label for="email">Email Address <span class="required">*</span></label>
                                <div class="invalid-feedback"></div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control"
                                       placeholder="Phone Number" required value="${customer.phoneNumber}"/>
                                <label for="phoneNumber">Phone Number <span class="required">*</span></label>
                                <div class="invalid-feedback"></div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating mb-3">
                                <input type="text" id="cccd" name="cccd" class="form-control"
                                       placeholder="ID Card Number" value="${customer.CCCD}"/>
                                <label for="cccd">ID Card Number (CCCD)</label>
                                <div class="invalid-feedback"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <div class="section-title">
                        <i class="fas fa-info-circle"></i> Personal Details
                    </div>
                    <div class="row">
                        <div class="col-md-4">
                            <div class="form-floating mb-3">
                                <select id="gender" name="gender" class="form-select">
                                    <option value="">Select Gender</option>
                                    <option value="Male" <c:if test="${customer.gender == 'Male'}">selected</c:if>>Male</option>
                                    <option value="Female" <c:if test="${customer.gender == 'Female'}">selected</c:if>>Female</option>
                                    <option value="Other" <c:if test="${customer.gender == 'Other'}">selected</c:if>>Other</option>
                                    </select>
                                    <label for="gender">Gender</label>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-floating mb-3">
                                    <input type="date" id="birthDate" name="birthDate" class="form-control"
                                           placeholder="Birth Date"
                                           value="<c:out value='${customer.birthDay}'/>"/>
                                <label for="birthDate">Birth Date</label>
                                <div class="invalid-feedback"></div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-floating mb-3">
                                <select id="customerStatus" name="status" class="form-select" required>
                                    <option value="">Select Status</option>
                                    <option value="Active" <c:if test="${customer.customerStatus == 'Active'}">selected</c:if>>Active</option>
                                    <option value="Inactive" <c:if test="${customer.customerStatus == 'Inactive'}">selected</c:if>>Inactive</option>
                                    </select>

                                    <label for="customerStatus">Status <span class="required">*</span></label>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="password" id="customerPassword" name="customerPassword" class="form-control"
                                           placeholder="Password"/>
                                    <label for="customerPassword">Password (Leave blank to keep current)</label>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                        </div>
                        <div class="form-floating mb-3">
                            <textarea id="address" name="address" class="form-control" placeholder="Address"
                                      style="height: 100px">${customer.address}</textarea>
                        <label for="address">Address</label>
                        <div class="invalid-feedback"></div>
                    </div>
                </div>

                <div class="btn-group-custom">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i> Update Customer
                    </button>
                    <a href="${pageContext.request.contextPath}/viewListAccount" class="btn btn-secondary">
                        <i class="fas fa-arrow-left me-2"></i> Back to List
                    </a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const form = document.getElementById('editCustomerForm');
                if (!form)
                    return;

                form.addEventListener('submit', function (e) {
                    let isValid = true;

                    // Full Name
                    const fullName = document.getElementById('customerFullName');
                    if (fullName.value.trim() === "") {
                        showError(fullName, "Full name is required.");
                        isValid = false;
                    } else {
                        hideError(fullName);
                    }

                    // Email
                    const email = document.getElementById('email');
                    if (!/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
                        showError(email, "Email is not valid.");
                        isValid = false;
                    } else {
                        hideError(email);
                    }

                    // Phone number
                    const phone = document.getElementById('phoneNumber');
                    if (!/^\d{10,11}$/.test(phone.value.trim())) {
                        showError(phone, "Phone number must be 10-11 digits.");
                        isValid = false;
                    } else {
                        hideError(phone);
                    }

                    // Password (nếu có nhập) - kiểm tra độ dài và trùng cũ
                    const pass = document.getElementById('customerPassword');
                    const oldPass = document.getElementById('oldCustomerPassword').value;
                    if (pass.value && pass.value.length > 0) {
                        if (pass.value.length < 6) {
                            showError(pass, "Password must be at least 6 characters.");
                            isValid = false;
                        } else if (md5(pass.value) === oldPass) {
                            showError(pass, "Password must be different from the old password.");
                            isValid = false;
                        } else {
                            hideError(pass);
                        }
                    } else {
                        hideError(pass);
                    }

                    // Trạng thái
                    const status = document.getElementById('customerStatus');
                    if (!status.value) {
                        showError(status, "Status is required.");
                        isValid = false;
                    } else {
                        hideError(status);
                    }

                    if (!isValid) {
                        e.preventDefault();
                        e.stopPropagation();
                    }
                });

                function showError(input, msg) {
                    input.classList.add('is-invalid');
                    const err = input.parentElement.querySelector('.invalid-feedback');
                    if (err)
                        err.innerText = msg;
                }

                function hideError(input) {
                    input.classList.remove('is-invalid');
                    const err = input.parentElement.querySelector('.invalid-feedback');
                    if (err)
                        err.innerText = "";
                }

                function md5(str) {
                  
                    return CryptoJS.MD5(str).toString();
                }
            });

        
        </script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
    </body>
</html>
