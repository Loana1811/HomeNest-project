

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="java.util.*" %>

<%

String ctx = request.getContextPath();

%>

<!DOCTYPE html>

<html>

<head>

<title>Statistical</title>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">


<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
 <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<style>
:root {
    --primary-color: #1e3a8a;
    --secondary-color: #3f51b5;
    --accent-color: #e0f2ff;
    --light-bg: #f4f7fb;
    --border-color: #d1dce8;
    --text-dark: #1f2937;
    --success-color: #14b8a6;
    --danger-color: #ef4444;
    --warning-color: #f59e0b;
}

    body {
    overflow-x: hidden;
    overflow-y: auto !important;
    margin-left: 300px;
}

    a {
        text-decoration: none;
        color: var(--primary-color);
    }
    a:hover {
        color: var(--secondary-color);
        text-decoration: underline;
    }

   .main-container {
    position: relative;
    z-index: 1;
    overflow: visible !important;
}

/* Filter Bar */
.nav-time {
    background: linear-gradient(135deg, #ffffff, var(--accent-color));
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(30, 58, 138, 0.15);
    padding: 12px 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    justify-content: center;
    margin-bottom: 20px;
}

.nav-time .btn {
    border-radius: 8px;
    font-weight: 500;
    padding: 8px 16px;
    border: 1.5px solid transparent;
}

/* Button Styles */
.btn-success {
    background-color: var(--success-color);
    border-color: var(--success-color);
    color: white;
}
.btn-success:hover {
    background-color: #0d9488;
}

.btn-outline-light {
    border-color: #dbeafe;
    color: var(--primary-color);
}
.btn-outline-light:hover {
    background-color: #dbeafe;
}

.btn-link {
    color: var(--primary-color);
}
.btn-link:hover {
    text-decoration: underline;
}

/* Stat Box */
.stat-box {
    min-width: 220px;
    padding: 20px;
    border-radius: 12px;
    background: white;
    box-shadow: 0 4px 14px rgba(30, 64, 175, 0.15);
    text-align: center;
    color: var(--text-dark);
    transition: all 0.3s ease;
}
.stat-box:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(30, 64, 175, 0.25);
}
.stat-box h4 {
    font-size: 1.5rem;
    font-weight: 600;
}
.text-success {
    color: var(--success-color) !important;
}
.text-danger {
    color: var(--danger-color) !important;
}
.text-warning {
    color: var(--warning-color) !important;
}

/* Inputs */
.form-select, .form-control {
    border-radius: 8px;
    border: 1px solid var(--border-color);
    padding: 10px;
    font-size: 0.95rem;
}
.form-label {
    font-weight: 600;
    color: var(--primary-color);
}
.form-control:focus, .form-select:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 0.2rem rgba(30, 58, 138, 0.25);
}

/* Card & Table */
.card {
    border-radius: 12px;
    box-shadow: 0 6px 20px rgba(30, 64, 175, 0.12);
    border: none;
    background: white;
}
.card-header {
    background: #e0e7ff;
    font-weight: bold;
    padding: 15px 20px;
    color: var(--primary-color);
}
.table thead th {
    background: #dbeafe;
    color: var(--primary-color);
    font-weight: 700;
}
.table tbody tr:hover {
    background-color: #e0f2fe;
}

/* Dropdown */
.dropdown-menu {
    z-index: 1050 !important; /* cao hơn sidebar */
}
.dropdown-item:hover {
    background-color: #e0f2fe;
    color: var(--primary-color);
}

/* Buttons - Actions */
.btn-action {
    min-width: 140px;
    font-weight: 600;
    border-radius: 8px;
    padding: 10px 20px;
    border: 1.5px solid var(--primary-color);
    color: var(--primary-color);
    background-color: transparent;
}
.btn-action:hover {
    background-color: var(--primary-color);
    color: white;
}

/* Print CSS */
@media print {
    body * { visibility: hidden; }
    #printable-transaction-list, #printable-transaction-list * {
        visibility: visible;
    }
    #printable-transaction-list {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
    }
    .card, .table { box-shadow: none; border: none; }
    th, td {
        border: 1px solid #000;
        padding: 8px;
    }
}
.btn-group {
    position: relative;
    z-index: 10;
}
.dropdown-menu.show {
    display: block;
    z-index: 2000 !important;
    top: 100%;
    left: 0;
    transform: translateY(0.5rem);
    visibility: visible;
}

</style>

<body>
    <div class="main-container">
        <div class="col-md-12">
            <%
            int selectedYear = (request.getAttribute("selectedYear") != null)
                ? (Integer) request.getAttribute("selectedYear")
                : java.time.LocalDate.now().getYear();
            int yearStart = selectedYear - 2;
            int yearEnd = selectedYear + 2;
            request.setAttribute("yearStart", yearStart);
            request.setAttribute("yearEnd", yearEnd);
            request.setAttribute("selectedYear", selectedYear);
            %>

            <!-- Navigation Filter -->
            <div class="d-flex justify-content-center mb-4">
                <div class="d-flex align-items-center flex-wrap gap-2 px-3 py-3 nav-time">
                    <a href="?type=${type}&value=${value}&year=${selectedYear - 1}" class="btn btn-link text-dark">
                        <i class="bi bi-arrow-left"></i> Last year
                    </a>
                    <c:choose>
                        <c:when test="${type == 'month'}">
                            <c:forEach begin="1" end="12" var="m">
                                <a href="?type=month&value=${m}&year=${selectedYear}"
                                    class="btn ${m == value ? 'btn-success' : 'btn-outline-light'}">
                                    T.${m} <br><small class="text-muted">${selectedYear}</small>
                                </a>
                            </c:forEach>
                        </c:when>
                        <c:when test="${type == 'quarter'}">
                            <c:forEach begin="1" end="4" var="q">
                                <a href="?type=quarter&value=${q}&year=${selectedYear}"
                                    class="btn ${q == value ? 'btn-success' : 'btn-outline-light'}">
                                    Q${q} <br><small class="text-muted">${selectedYear}</small>
                                </a>
                            </c:forEach>
                        </c:when>
                        <c:when test="${type == 'year'}">
                            <c:forEach begin="${yearStart}" end="${yearEnd}" var="y">
                                <a href="?type=year&value=${y}&year=${y}"
                                    class="btn ${y == value ? 'btn-success' : 'btn-outline-light'}">
                                    ${y}
                                </a>
                            </c:forEach>
                        </c:when>
                    </c:choose>
                    <a href="?type=${type}&value=${value}&year=${selectedYear + 1}" class="btn btn-link text-dark">
                        Next year <i class="bi bi-arrow-right"></i>
                    </a>
                </div>
            </div>

            <!-- Bộ lọc thời gian -->
            <form method="get" class="d-flex align-items-center flex-wrap mb-4">
                <select class="form-select w-auto me-2" name="type" onchange="this.form.submit()">
                    <option value="month" ${type == 'month' ? 'selected' : ''}>By month</option>
                    <option value="quarter" ${type == 'quarter' ? 'selected' : ''}>Quarterly</option>
                    <option value="year" ${type == 'year' ? 'selected' : ''}>By year</option>
                </select>
            </form>

            <!-- Form lọc -->
            <form method="get" class="row g-3 align-items-center mb-3">
                <input type="hidden" name="type" value="${type}">
                <input type="hidden" name="value" value="${value}">
                <input type="hidden" name="year" value="${selectedYear}">
                <input type="hidden" name="fromDate" value="${fromDate}">
                <input type="hidden" name="toDate" value="${toDate}">
                <div class="col-auto">
                    <label class="form-label mb-0">Type:</label>
                    <select class="form-select" name="filterType" onchange="this.form.submit()">
                        <option value="all" <c:if test="${filterType eq 'all'}">selected</c:if>>All</option>
                        <option value="income" <c:if test="${filterType eq 'income'}">selected</c:if>>Receipt</option>
                        <option value="expense" <c:if test="${filterType eq 'expense'}">selected</c:if>>Payment slip</option>
                    </select>
                </div>
                <c:if test="${filterType eq 'income' or filterType eq 'all' or showRevenueCategory}">
                    <div class="col-auto">
                        <label class="form-label mb-0">Collection list:</label>
                        <select class="form-select" name="filterCategoryId" onchange="this.form.submit()">
                            <option value="all" <c:if test="${filterCategoryId eq 'all'}">selected</c:if>>All</option>
                            <c:forEach var="cat" items="${revenueCategories}">
                                <option value="${cat.revenueCategoryID}"
                                    <c:if test="${filterCategoryId eq cat.revenueCategoryID.toString()}">selected</c:if>>
                                    ${cat.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
                <c:if test="${filterType eq 'expense' or filterType eq 'all' or showExpenseCategory}">
                    <div class="col-auto">
                        <label class="form-label mb-0">Expense list:</label>
                        <select class="form-select" name="filterExpenseCategoryId" onchange="this.form.submit()">
                            <option value="all" <c:if test="${filterExpenseCategoryId eq 'all'}">selected</c:if>>All</option>
                            <c:forEach var="cat" items="${expenseCategories}">
                                <option value="${cat.expenseCategoryID}"
                                    <c:if test="${filterExpenseCategoryId eq cat.expenseCategoryID.toString()}">selected</c:if>>
                                    ${cat.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
            </form>

            <!-- Tổng hợp nhanh -->
            <div class="d-flex flex-wrap gap-3 mb-4">
                <div class="stat-box border text-success">
                    <div>Total revenue</div>
                    <h4 class="mt-2">+ ${totalIncome} đ</h4>
                </div>
                <div class="stat-box border text-danger">
                    <div>Total expenditure</div>
                    <h4 class="mt-2">- ${totalExpense} đ</h4>
                </div>
                <div class="stat-box border ${profit >= 0 ? 'text-success' : 'text-danger'}">
                    <div>Profit</div>
                    <h4 class="mt-2">${profit} đ</h4>
                </div>
            </div>

            <!-- Nút chức năng -->
            <div class="d-flex flex-wrap gap-2 mb-4">
                <div class="btn-group">
                   <button type="button" class="btn btn-dark dropdown-toggle btn-action" data-bs-toggle="dropdown">
  Category management ⏷
</button>

                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#addRevenueCategoryModal">Add collection category</a></li>
                        <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#addExpenseCategoryModal">Add expense category</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#listRevenueCategoryModal">List of revenue categories</a></li>
                        <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#listExpenseCategoryModal">List of categories</a></li>
                    </ul>
                </div>
                <button class="btn btn-success btn-action" data-bs-toggle="modal" data-bs-target="#addRevenueModal">Add receipt</button>
                <button class="btn btn-warning btn-action" data-bs-toggle="modal" data-bs-target="#addExpenseModal">Add payment voucher</button>
                <button onclick="exportReport('excel')" class="btn btn-outline-success btn-action">
                    <i class="bi bi-file-earmark-excel"></i> Export Excel
                </button>
                <button onclick="printTransactionList()" class="btn btn-outline-secondary btn-action">
                    <i class="bi bi-printer"></i> Print report
                </button>
            </div>

            <!-- Modal Thêm phiếu thu -->
            <div class="modal fade" id="addRevenueModal" tabindex="-1" aria-labelledby="addRevenueLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <form id="addRevenueForm">
                        <div class="modal-content">
                            <div class="modal-header bg-success bg-opacity-25">
                                <h5 class="modal-title">
                                    <i class="bi bi-wallet2 text-success"></i> Add receipt
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <div class="mb-3">
                                    <label class="form-label">Receipt name</label>
                                    <input type="text" name="title" class="form-control" placeholder="Nhập tên phiếu thu" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Amount</label>
                                    <input type="number" name="amount" class="form-control" placeholder="Nhập số tiền" required min="1000" step="1000">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Collection list</label>
                                    <select name="categoryId" class="form-select" required>
                                        <option value="">-- Select collection category --</option>
                                        <c:forEach var="cat" items="${revenueCategories}">
                                            <option value="${cat.revenueCategoryID}">${cat.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Autumn day</label>
                                    <input type="date" name="revenueDate" class="form-control" value="<%= java.time.LocalDate.now()%>" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Submitter</label>
                                    <input type="text" name="payer" class="form-control" placeholder="Nhập tên người nộp" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Note</label>
                                    <textarea name="notes" rows="3" class="form-control" placeholder="Ghi chú thêm (nếu có)"></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-success">Save</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Modal Thêm phiếu chi -->
            <div class="modal fade" id="addExpenseModal" tabindex="-1" aria-labelledby="addExpenseLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <form id="addExpenseForm">
                        <div class="modal-content">
                            <div class="modal-header bg-warning bg-opacity-25">
                                <h5 class="modal-title">
                                    <i class="bi bi-cash-stack text-warning"></i> Add payment voucher
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <div class="mb-3">
                                    <label class="form-label">Payment slip name</label>
                                    <input type="text" name="title" class="form-control" placeholder="Nhập tên phiếu chi" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Amount</label>
                                    <input type="number" name="amount" class="form-control" placeholder="Nhập số tiền" required min="1000" step="1000">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Expense list</label>
                                    <select name="categoryId" class="form-select" required>
                                        <option value="">-- Select the expense category --</option>
                                        <c:forEach var="cat" items="${expenseCategories}">
                                            <option value="${cat.expenseCategoryID}">${cat.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Date spent</label>
                                    <input type="date" name="expenseDate" class="form-control" value="<%= java.time.LocalDate.now()%>" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Submitter</label>
                                    <input type="text" name="spender" class="form-control" placeholder="Nhập tên người chi" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Note</label>
                                    <textarea name="notes" rows="3" class="form-control" placeholder="Ghi chú thêm (nếu có)"></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-warning">Save</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Modal Thêm danh mục thu -->
            <div class="modal fade" id="addRevenueCategoryModal" tabindex="-1">
                <div class="modal-dialog">
                    <form id="addRevenueCategoryForm">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5>Add collection category</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <input type="text" name="name" class="form-control" placeholder="Tên danh mục" required>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-success">Save</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Modal Thêm danh mục chi -->
            <div class="modal fade" id="addExpenseCategoryModal" tabindex="-1">
                <div class="modal-dialog">
                    <form id="addExpenseCategoryForm">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5>Add expense category</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <input type="text" name="name" class="form-control" placeholder="Tên danh mục" required>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-success">Save</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Modal Danh mục thu -->
            <div class="modal fade" id="listRevenueCategoryModal" tabindex="-1" aria-labelledby="listRevenueCategoryLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header bg-light">
                            <h5 class="modal-title"><i class="bi bi-tags-fill text-success"></i> Collection list</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                        </div>
                        <div class="modal-body">
                            <c:if test="${empty revenueCategories}">
                                <div class="text-center text-muted">
                                    <img src="https://cdn-icons-png.flaticon.com/512/4076/4076504.png" width="100" alt="Empty">
                                    <p class="mt-2">No collection categories yet!</p>
                                </div>
                            </c:if>
                            <c:if test="${not empty revenueCategories}">
                                <div class="row g-3">
                                    <c:forEach var="cat" items="${revenueCategories}">
                                        <div class="col-md-6">
                                            <div class="border rounded p-3 d-flex align-items-start bg-white shadow-sm h-100 category-card">
                                                <div class="me-3">
                                                    <i class="bi bi-tag text-success fs-3"></i>
                                                </div>
                                                <div>
                                                    <h6 class="mb-1">${cat.categoryName}</h6>
                                                    <small class="text-danger">System generated, not editable</small>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modal Danh mục chi -->
            <div class="modal fade" id="listExpenseCategoryModal" tabindex="-1" aria-labelledby="listExpenseCategoryLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header bg-light">
                            <h5 class="modal-title"><i class="bi bi-tags"></i> Expense list</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                        </div>
                        <div class="modal-body">
                            <c:if test="${empty expenseCategories}">
                                <div class="text-center text-muted">
                                    <img src="https://cdn-icons-png.flaticon.com/512/4076/4076504.png" width="100" alt="Empty">
                                    <p class="mt-2">No categories yet!</p>
                                </div>
                            </c:if>
                            <c:if test="${not empty expenseCategories}">
                                <div class="row g-3">
                                    <c:forEach var="cat" items="${expenseCategories}">
                                        <div class="col-md-6">
                                            <div class="border rounded p-3 d-flex align-items-start bg-white shadow-sm h-100 category-card">
                                                <div class="me-3">
                                                    <i class="bi bi-tag fs-3 text-secondary"></i>
                                                </div>
                                                <div>
                                                    <h6 class="mb-1">${cat.categoryName}</h6>
                                                    <small class="text-danger">System generated, not editable</small>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Danh sách phiếu thu/chi -->
            <div id="printable-transaction-list">
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-light">
                        <h6 class="mb-0"><i class="bi bi-table"></i> List of receipts/payments</h6>
                    </div>
                    <div class="card-body table-responsive">
                        <table class="table table-bordered align-middle text-center">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Voucher Name</th>
                                    <th>Type</th>
                                    <th>Amount</th>
                                    <th>Category</th>
                                    <th>Date</th>
                                    <th>Related Person</th>
                                    <th>Notes</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${transactions}" varStatus="loop">
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td>${item.title}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.type eq 'income'}">
                                                    <span class="badge bg-success">Thu</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark">Chi</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${item.amount} đ</td>
                                        <td>${item.categoryName}</td>
                                        <td>${item.date}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.type eq 'income'}">${item.payer}</c:when>
                                                <c:otherwise>${item.spender}</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${item.notes}</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty transactions}">
                                    <tr>
                                        <td colspan="11" class="text-muted">No revenue/expenditure data yet.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="stamp">
                    <img src="<%= ctx %>/images/stamp.jpg" style="float: right; width: 100px;">
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Thêm danh mục thu
        document.getElementById('addRevenueCategoryForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const name = this.name.value.trim();
            if (!name)
                return alert('Vui lòng nhập tên danh mục');
            fetch('<c:url value="/admin/statistical"/>', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: new URLSearchParams({target: 'revenueCategory', action: 'add', name})
            }).then(res => res.json()).then(data => {
                if (data.status === 'success') {
                    alert('Đã thêm danh mục thu');
                    bootstrap.Modal.getInstance(addRevenueCategoryModal).hide();
                    location.reload();
                } else {
                    alert(data.message || 'Lỗi');
                }
            });
        });

        // Thêm danh mục chi
        document.getElementById('addExpenseCategoryForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const name = this.name.value.trim();
            if (!name)
                return alert('Vui lòng nhập tên danh mục');
            fetch('<c:url value="/admin/statistical"/>', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: new URLSearchParams({target: 'expenseCategory', action: 'add', name})
            }).then(res => res.json()).then(data => {
                if (data.status === 'success') {
                    alert('Đã thêm danh mục chi');
                    bootstrap.Modal.getInstance(addExpenseCategoryModal).hide();
                    location.reload();
                } else {
                    alert(data.message || 'Lỗi');
                }
            });
        });

        // Thêm phiếu thu
        document.getElementById('addRevenueForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const title = this.title.value.trim();
            const amount = this.amount.value.trim();
            const categoryId = this.categoryId.value;
            const revenueDate = this.revenueDate.value;
            const payer = this.payer.value.trim();
            const notes = this.notes.value.trim();
            if (!title || !amount || !categoryId || !revenueDate || !payer) {
                alert('Vui lòng nhập đầy đủ thông tin!');
                return;
            }
            fetch('<c:url value="/admin/statistical"/>', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: new URLSearchParams({
                    target: 'revenueItem',
                    action: 'add',
                    revenueName: title,
                    amount: amount,
                    revenueCategoryID: categoryId,
                    revenueDate: revenueDate,
                    payer: payer,
                    notes: notes
                })
            })
            .then(res => res.json())
            .then(data => {
                if (data.status === 'success') {
                    alert('Đã thêm phiếu thu thành công');
                    bootstrap.Modal.getInstance(addRevenueModal).hide();
                    location.reload();
                } else {
                    alert(data.message || 'Có lỗi xảy ra!');
                }
            })
            .catch(err => {
                alert('Lỗi hệ thống');
                console.error(err);
            });
        });

        // Thêm phiếu chi
        document.getElementById('addExpenseForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const title = this.title.value.trim();
            const amount = this.amount.value.trim();
            const categoryId = this.categoryId.value;
            const expenseDate = this.expenseDate.value;
            const spender = this.spender.value.trim();
            const notes = this.notes.value.trim();
            if (!title || !amount || !categoryId || !expenseDate || !spender) {
                alert('Vui lòng nhập đầy đủ thông tin!');
                return;
            }
            fetch('<c:url value="/admin/statistical"/>', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: new URLSearchParams({
                    target: 'expenseItem',
                    action: 'add',
                    expenseName: title,
                    amount: amount,
                    expenseCategoryID: categoryId,
                    expenseDate: expenseDate,
                    payer: spender,
                    notes: notes
                })
            })
            .then(res => res.json())
            .then(data => {
                if (data.status === 'success') {
                    alert('Đã thêm phiếu chi thành công');
                    bootstrap.Modal.getInstance(addExpenseModal).hide();
                    location.reload();
                } else {
                    alert(data.message || 'Có lỗi xảy ra!');
                }
            })
            .catch(err => {
                alert('Lỗi hệ thống');
                console.error(err);
            });
        });

        function exportReport(type) {
            const params = new URLSearchParams(window.location.search);
            params.set("filterType", type);
            window.location.href = window.location.pathname + "?" + params.toString();
        }

        function printTransactionList() {
            const contentElement = document.getElementById("printable-transaction-list");
            if (!contentElement) {
                alert("Không tìm thấy phần tử cần in.");
                return;
            }
            if (contentElement.innerHTML.trim() === "") {
                alert("Danh sách phiếu thu/chi đang trống, không thể in!");
                return;
            }
            window.print();
        }
    </script>
    <a href="${pageContext.request.contextPath}/admin/dashboard"
   class="btn btn-secondary mb-4 ms-2">
    ⬅️ Back to Dashboard
</a>

</body>

</html>