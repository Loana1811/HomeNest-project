<%-- 
    Document   : statistical
    Created on : Jun 23, 2025, 8:45:55 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Financial Summary - Rental Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    </head>
    <body class="bg-light p-3">
        <div class="container">
            <h4 class="mb-3">Financial Summary Report</h4>

            <form class="row g-3 mb-4" id="filterForm">
                <div class="col-md-3">
                    <label class="form-label">Statistic Type</label>
                    <select class="form-select" id="statType" name="type">
                        <option value="month">By Month</option>
                        <option value="quarter">By Quarter</option>
                        <option value="year">By Year</option>
                    </select>
                </div>
                <div class="col-md-2" id="valueContainer">
                    <label class="form-label" id="valueLabel">Month</label>
                    <input type="number" class="form-control" name="value" id="valueInput" min="1" max="12">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Year</label>
                    <input type="number" class="form-control" name="year" id="yearInput" min="2000" value="2025">
                </div>
                <div class="col-md-2 align-self-end">
                    <button type="submit" class="btn btn-primary w-100"><i class="fas fa-search"></i> View</button>
                </div>
            </form>

            <div class="row mb-4">
                <div class="col">
                    <div class="card text-success">
                        <div class="card-body d-flex align-items-center">
                            <i class="fas fa-coins fa-2x me-3"></i>
                            <div>
                                <small>Total Income</small>
                                <h5 id="totalIncome">0 đ</h5>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="card text-danger">
                        <div class="card-body d-flex align-items-center">
                            <i class="fas fa-money-bill-wave fa-2x me-3"></i>
                            <div>
                                <small>Total Expense</small>
                                <h5 id="totalExpense">0 đ</h5>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="card text-warning">
                        <div class="card-body d-flex align-items-center">
                            <i class="fas fa-chart-line fa-2x me-3"></i>
                            <div>
                                <small>Profit</small>
                                <h5 id="profit">0 đ</h5>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="d-flex gap-2 mb-3">
                <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addRevenueModal"><i class="fas fa-plus"></i> Add Revenue</button>
                <button class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#addExpenseModal"><i class="fas fa-plus"></i> Add Expense</button>
            </div>

            <h6>Revenue Records:</h6>
            <table class="table table-bordered table-sm">
                <thead class="table-success">
                    <tr><th>#</th><th>Category</th><th>Amount</th><th>Date</th><th>Notes</th><th>Action</th></tr>
                </thead>
                <tbody id="revenueTable"></tbody>
            </table>

            <h6 class="mt-4">Expense Records:</h6>
            <table class="table table-bordered table-sm">
                <thead class="table-danger">
                    <tr><th>#</th><th>Category</th><th>Amount</th><th>Date</th><th>Notes</th><th>Action</th></tr>
                </thead>
                <tbody id="expenseTable"></tbody>
            </table>
        </div>

        <!-- Add Revenue Modal -->
        <div class="modal fade" id="addRevenueModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header"><h5>Add Revenue</h5></div>
                    <div class="modal-body">
                        <input type="text" id="revCategory" class="form-control mb-2" placeholder="Category">
                        <input type="number" id="revAmount" class="form-control mb-2" placeholder="Amount">
                        <textarea id="revNotes" class="form-control" placeholder="Notes"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-success" id="saveRevenueBtn">Save</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Add Expense Modal -->
        <div class="modal fade" id="addExpenseModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header"><h5>Add Expense</h5></div>
                    <div class="modal-body">
                        <input type="text" id="expCategory" class="form-control mb-2" placeholder="Category">
                        <input type="number" id="expAmount" class="form-control mb-2" placeholder="Amount">
                        <textarea id="expNotes" class="form-control" placeholder="Notes"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-danger" id="saveExpenseBtn">Save</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
            $(document).ready(function () {
                loadData();

                $('#filterForm').submit(function (e) {
                    e.preventDefault();
                    loadData();
                });

                $('#saveRevenueBtn').click(function () {
                    $.post('statistical', {
                        action: 'addRevenue',
                        category: $('#revCategory').val(),
                        amount: $('#revAmount').val(),
                        notes: $('#revNotes').val()
                    }, loadData);
                    $('#addRevenueModal').modal('hide');
                });

                $('#saveExpenseBtn').click(function () {
                    $.post('statistical', {
                        action: 'addExpense',
                        category: $('#expCategory').val(),
                        amount: $('#expAmount').val(),
                        notes: $('#expNotes').val()
                    }, loadData);
                    $('#addExpenseModal').modal('hide');
                });
            });

            function loadData() {
                $.get('statistical', $('#filterForm').serialize(), function (res) {
                    $('#totalIncome').text(res.totalIncome + ' đ');
                    $('#totalExpense').text(res.totalExpense + ' đ');
                    $('#profit').text(res.profit + ' đ');

                    let revHtml = '';
                    res.revenues.forEach((r, i) => {
                        revHtml += `<tr><td>${i+1}</td><td>${r.category}</td><td>${r.amount} đ</td><td>${r.date}</td><td>${r.notes}</td><td><button class='btn btn-sm btn-danger' onclick='deleteRevenue(${r.id})'>Delete</button></td></tr>`;
                    });
                    $('#revenueTable').html(revHtml);

                    let expHtml = '';
                    res.expenses.forEach((e, i) => {
                        expHtml += `<tr><td>${i+1}</td><td>${e.category}</td><td>${e.amount} đ</td><td>${e.date}</td><td>${e.notes}</td><td><button class='btn btn-sm btn-danger' onclick='deleteExpense(${e.id})'>Delete</button></td></tr>`;
                    });
                    $('#expenseTable').html(expHtml);
                }, 'json');
            }

            function deleteRevenue(id) {
                if (confirm('Delete this revenue?'))
                    $.post('statistical', {action: 'deleteRevenue', id}, loadData);
            }

            function deleteExpense(id) {
                if (confirm('Delete this expense?'))
                    $.post('statistical', {action: 'deleteExpense', id}, loadData);
            }
        </script>
    </body>
</html>