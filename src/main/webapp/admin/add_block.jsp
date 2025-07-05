<%-- 
    Document   : add_block.jsp
    Created on : Jun 15, 2025, 3:58:21 PM
    Author     : Admin
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Create New Block</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" />
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <style>
            :root {
                --teal: rgb(0, 128, 128);
            }
            body {
                font-family: 'Segoe UI', sans-serif;
                background-color: #f5f7fa;
            }
            .sidebar {
                width: 260px;
                height: 100vh;
                background-color: var(--teal);
                color: white;
                position: fixed;
                top: 0;
                left: 0;
                padding: 2rem 1rem;
            }
            .sidebar h4 {
                font-weight: bold;
                margin-bottom: 2rem;
            }
            .sidebar a {
                color: white;
                text-decoration: none;
                display: block;
                margin: 1rem 0;
                font-weight: 500;
            }
            .sidebar a:hover {
                text-decoration: underline;
            }
            .main {
                margin-left: 270px;
                padding: 2rem;
            }
            .card-header {
                background-color: var(--teal);
                color: white;
            }
            .btn-teal {
                background-color: var(--teal);
                color: white;
            }
            .btn-teal:hover {
                background-color: #006666;
                color: white;
            }
        </style>
    </head>
    <body>

        <!-- Sidebar -->
        <div class="sidebar">
            <h4>ADMIN</h4>
            <a href="rooms?action=list"><i class="bi bi-layout-text-window"></i> Manage Room</a>
            <a href="blocks?action=list"><i class="bi bi-building"></i> Manage Blocks</a>
        </div>

        <!-- Main Content -->
        <div class="main">
            <div class="container">
                <div class="card shadow">
                    <div class="card-header">
                        <h4 class="mb-0">Create Block</h4>
                    </div>
                    <div class="card-body">
                        <form action="blocks" method="post" novalidate>
                            <input type="hidden" name="action" value="insert" />

                            <div class="mb-3">
                                <label for="blockName" class="form-label">Block Name:</label>
                                <input
                                    type="text"
                                    class="form-control"
                                    id="blockName"
                                    name="blockName"
                                    value="${fn:escapeXml(param.blockName)}"
                                    required
                                    autofocus
                                    />
                            </div>

                            <div class="mb-3">
                                <label for="maxRooms" class="form-label">Max Number of Rooms:</label>
                                <input
                                    type="number"
                                    class="form-control"
                                    id="maxRooms"
                                    name="maxRooms"
                                    min="1"
                                    value="${not empty param.maxRooms ? fn:escapeXml(param.maxRooms) : '20'}"
                                    required
                                    />
                            </div>

                            <button type="submit" class="btn btn-teal w-100">Create Block</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- SweetAlert2 popup if message from Servlet -->
        <c:if test="${not empty message and not empty alertType}">
            <script>
                Swal.fire({
                    icon: '${alertType}',
                    title: '${alertType == "success" ? "Success" : "Error"}',
                    text: '${fn:escapeXml(message)}',
                    confirmButtonColor: '#3085d6'
                });
            </script>
        </c:if>

    </body>
</html>