<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="model.UtilityUsageView" %>

<%
    Map<String, List<UtilityUsageView>> groupedUsages = (Map<String, List<UtilityUsageView>>) request.getAttribute("groupedUsages");
     model.User manager = (model.User) session.getAttribute("currentUser");
    Integer idUser = (Integer) session.getAttribute("idUser");

    if (manager == null || idUser == null) {
        response.sendRedirect(request.getContextPath() + "/Login.jsp");
        return;
    }

    String ctx = request.getContextPath();
  
%>


<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manager - Utility Usage</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <style>
            body {
                padding-top: 56px;
                background-color: #f8f9fa;
                font-family: 'Segoe UI', sans-serif;
            }
            .sidebar {
                width: 240px;
                position: fixed;
                top: 56px;
                left: 0;
                bottom: 0;
                background-color: #ffffff;
                border-right: 1px solid #e0e0e0;
                padding: 1rem;
                box-shadow: 2px 0 6px rgba(0, 0, 0, 0.05);
                z-index: 1030;
            }
            .sidebar h5 {
                font-weight: 700;
                margin-bottom: 1rem;
            }
            .sidebar .nav-link {
                padding: 10px 14px;
                margin-bottom: 6px;
                color: #333;
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.2s;
            }
            .sidebar .nav-link:hover,
            .sidebar .nav-link.active {
                background: #0d6efd;
                color: white;
            }
            main {
                margin-left: 260px;
                padding: 2rem 1rem;
                max-width: 1200px;
            }
            @media (max-width: 768px) {
                .sidebar {
                    display: none;
                }
                main {
                    margin-left: 0;
                }
            }
        </style>
    </head>
    <body>
        <div class="sidebar">
            <h4 class="text-center mb-4">Manager Menu</h4>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/dashboard.jsp?idManager=<%= manager.getUserID() %>">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/adminReport">Report Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/notification?idManager=<%= idUser %>">Notification Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx %>/manager/utility?idManager=<%= idUser %>">Usage Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/manage-requests?idManager=<%= idUser %>">Rental Request Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-danger" href="<%= ctx%>/Logouts">Logout</a>
                </li>
            </ul>
        </div>
        <!-- MAIN CONTENT -->
        <main>
            <div class="mb-4">
                <h3 class="fw-bold text-success">
                    <i class="bi bi-bar-chart-fill me-2"></i>Tenant Usage in the Month
                </h3>
                <p class="text-muted fst-italic">Monthly usage statistics</p>
            </div>

            <!-- FILTER FORM -->
            <form method="get" action="${pageContext.request.contextPath}/manager/usage" class="row g-3 mb-4">
                <div class="col-md-3">
                    <label class="form-label fw-semibold">Billing Month</label>
                    <input type="month" name="month" class="form-control" value="${param.month}" />
                </div>

                <div class="col-md-3">
                    <label class="form-label fw-semibold">Room Name</label>
                    <input type="text" name="room" class="form-control" value="${param.room}" placeholder="e.g., A101" />
                </div>

                <div class="col-md-2 align-self-end">
                    <button type="submit" class="btn btn-success w-100">
                        <i class="bi bi-filter-circle"></i> Filter
                    </button>
                </div>
                <div class="col-md-2 align-self-end">
                    <a href="utility?action=record" class="btn btn-outline-teal">
                        📜 Record Utility
                    </a>
                </div>
            </form>

            <% if (groupedUsages == null) { %>
            <div class="alert alert-danger">❌ Data not found.</div>
            <% } else if (groupedUsages.isEmpty()) { %>
            <div class="alert alert-warning">⚠️ No usage records available.</div>
            <% } else {
                for (Map.Entry<String, List<UtilityUsageView>> entry : groupedUsages.entrySet()) {
                    String blockName = entry.getKey();
                    List<UtilityUsageView> usageList = entry.getValue();
            %>
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-primary text-white fw-bold">
                    🏢 Block: <%= blockName%>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle">
                            <thead class="table-light">
                                <tr class="text-center align-middle">
                                    <th rowspan="2">Room Name</th>
                                    <th rowspan="2">Utility</th>
                                    <th colspan="2">Reading</th>
                                    <th rowspan="2">Used</th>
                                    <th rowspan="2">Total Price(₫)</th>
                                    <th rowspan="2">Reading Date</th>
                                    <th rowspan="2">Edit</th>
                                </tr>
                                <tr class="text-center">
                                    <th>Previous</th>
                                    <th>Current</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (UtilityUsageView u : usageList) {%>
                                <tr>
                                    <td><%= u.getRoomNumber()%></td>
                                    <td><%= u.getUtilityName()%></td>
                                    <td><%= u.getOldIndex()%></td>
                                    <td><%= u.getNewIndex()%></td>
                                    <td><%= u.getNewIndex() - u.getOldIndex()%></td>
                                    <td><%= u.getPriceUsed()%></td>
                                    <td><%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(u.getReadingDate())%></td>
                                    <td class="text-center">
                                        <% if (!u.isLocked()) { %>
                                        <a class="btn btn-sm btn-warning"
                                           href="<%= ctx %>/manager/edit-reading?readingId=<%= u.getReadingId() %>">✏️</a>
                                        <% } %>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <% }
    } %>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            window.addEventListener('load', function () {
                if (performance.navigation.type === 1 || performance.getEntriesByType("navigation")[0].type === "reload") {
                    const url = new URL(window.location.href);
                    if (url.searchParams.has('room') || url.searchParams.has('month')) {
                        url.searchParams.delete('room');
                        url.searchParams.delete('month');
                        window.location.replace(url.toString());
                    }
                }
            });
        </script>

    </body>
</html>
