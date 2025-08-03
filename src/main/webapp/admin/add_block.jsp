<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Add New Block</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" />
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<%
    String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap');

    :root {
        --primary-color: #1e3b8a;
        --secondary-color: #3f5fa6;
        --accent-color: #7c94d1;
        --success: #22c55e;
        --danger: #ef4444;
        --background: #f8fafc;
        --card-bg: #ffffff;
        --input-border: #d1d5db;
        --shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
        --radius: 16px;
    }

    * {
        font-family: 'Poppins', sans-serif;
    }

    body {
        background-color: var(--background);
        margin: 0;
        padding: 0;
    }

    .main {
        margin-left: 270px;
        padding: 3rem 2rem;
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }

    .wow-title {
        font-size: 2.4rem;
        font-weight: 700;
        margin-bottom: 2rem;
        position: relative;
        display: flex;
        align-items: center;
        gap: 10px;
        transition: all 0.4s ease;
    }

    .glow-text {
        background: linear-gradient(90deg, #1e3b8a, #3f5fa6, #7c94d1, #1e3b8a);
        background-size: 400% auto;
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        animation: gradientMove 6s linear infinite;
        display: inline-block;
        text-shadow: 0 0 6px rgba(30, 59, 138, 0.3);
    }

    @keyframes gradientMove {
        0% { background-position: 0% center; }
        100% { background-position: 400% center; }
    }

    .icon-gradient {
        background: linear-gradient(135deg, #60a5fa, #3f5fa6);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }

    .card {
        background-color: var(--card-bg);
        border-radius: var(--radius);
        box-shadow: var(--shadow);
        border: none;
        width: 600px;
        transition: transform 0.3s ease;
    }

    .card:hover {
        transform: translateY(-5px);
    }

    .card-header {
        background-color: var(--primary-color);
        color: white;
        padding: 1rem;
        border-top-left-radius: var(--radius);
        border-top-right-radius: var(--radius);
        font-weight: 600;
        text-align: center;
    }

    .card-body {
        padding: 2rem;
    }

    label.form-label {
        font-weight: 500;
        color: var(--primary-color);
    }

    .form-control {
        border-radius: 12px;
        border: 1px solid var(--input-border);
        padding: 0.65rem 1rem;
    }

    .form-control:focus {
        border-color: var(--accent-color);
        box-shadow: 0 0 0 0.2rem rgba(124, 148, 209, 0.25);
    }

    .btn-teal {
        background: linear-gradient(145deg, var(--primary-color), var(--secondary-color));
        color: white;
        border: none;
        border-radius: 50px;
        padding: 0.75rem 1.2rem;
        font-weight: 500;
        font-size: 1.05rem;
        transition: all 0.2s ease;
    }

    .btn-teal:hover {
        background-color: var(--accent-color);
        transform: scale(1.03);
    }

    @media (max-width: 576px) {
        .card {
            width: 90%;
        }
    }
</style>
</head>

<body>
<div class="main">
    <!-- Block Management title bên trái -->
<div class="w-100 d-flex align-items-center mb-2" style="justify-content: flex-start; margin-top: -290px;">
    <h2 class="wow-title">
        <i class="bi bi-building icon-gradient"></i>
        <span class="glow-text">Block Management</span>
    </h2>
</div>


   <!-- Form ở giữa -->
<!-- Add New Block Form -->
<div class="card mx-auto shadow" style="max-width: 500px; border-radius: 12px;">
    <div class="card-header text-white text-center fw-bold" style="background-color: #1e3b8a;">
        <i class="bi bi-building-add me-2"></i> Add New Block
    </div>
    <div class="card-body p-4">
        <form action="blocks" method="post" novalidate>
            <input type="hidden" name="action" value="insert" />
            
            <div class="mb-3">
                <label for="blockName" class="form-label fw-semibold text-dark">Block:</label>
                <input type="text" class="form-control rounded-pill px-4 py-2 border-primary-subtle shadow-sm"
                       id="blockName" name="blockName"
                       value="${fn:escapeXml(param.blockName)}" required autofocus />
            </div>

            <div class="mb-4">
                <label for="roomCount" class="form-label fw-semibold text-dark">Original room number:</label>
                <input type="number" class="form-control rounded-pill px-4 py-2 border-primary-subtle shadow-sm"
                       id="roomCount" name="roomCount" min="0"
                       value="${not empty param.roomCount ? fn:escapeXml(param.roomCount) : '0'}" required />
            </div>

            <div class="d-flex flex-column flex-md-row justify-content-between gap-2">
                <button type="submit" class="btn w-100 w-md-auto px-4 py-2 text-white fw-semibold"
                            style="background-color: #0939ba; border-radius: 30px;">
                    <i class="bi bi-plus-circle"></i> Create Block
                </button>
                <a href="${pageContext.request.contextPath}/admin/account"
                   class="btn w-100 w-md-auto px-4 py-2 fw-semibold text-white"
                   style="background-color: #6c757d; border-radius: 30px;">
                    <i class="bi bi-arrow-left"></i> Back
                </a>
            </div>
        </form>
    </div>
</div>



<c:if test="${not empty message and not empty alertType}">
    <script>
        Swal.fire({
            icon: '${alertType}',
            title: '${alertType == "success" ? "Successfully" : "Error"}',
            text: '${fn:escapeXml(message)}',
            confirmButtonColor: '#3085d6'
        });
    </script>
</c:if>

</body>
</html>
