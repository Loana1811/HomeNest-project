    <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>  
    <%
        String ctx = request.getContextPath();
    %>  
    <%@ page import="java.util.*, model.Block" %>
    <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

    <%
        List<Block> blockList = (List<Block>) request.getAttribute("blockList");
    %>
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <meta charset="UTF-8" />
            <title>Manage Blocks</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
            <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
            <style>
               @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap');

    :root {
      --primary-color: #1e3b8a;
      --secondary-color: #3f5fa6;
      --accent-color: #7c94d1;
      --text-color: #1e293b;
      --white: #ffffff;
      --background-color: #f8fafc;
      --table-header-bg: #1e3b8a;
      --table-header-text: #ffffff;
      --success: #16a34a;
      --danger: #dc2626;
      --hover-row: #e8f0fe;
      --highlight: #f1f5f9;
      --shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
      --border-radius: 12px;
    }

    * {
      font-family: 'Poppins', sans-serif;
      transition: all 0.3s ease;
    }

    body {
      background-color: var(--background-color);
      color: var(--text-color);
      font-size: 1.05rem;
      margin: 0;
      padding: 0;
    }

    .main {
      margin-left: 270px;
      padding: 3rem;
    }

    h2.text-dark {
      font-size: 2rem;
      font-weight: 600;
      color: var(--primary-color);
      margin-bottom: 1rem;
    }

    .btn-teal {
      background-color: var(--primary-color);
      color: var(--white);
      border-radius: 30px;
      font-size: 1rem;
      padding: 0.6rem 1.3rem;
      font-weight: 500;
      box-shadow: var(--shadow);
    }

    .btn-teal:hover {
      background-color: var(--accent-color);
    }

    .btn-outline-dark {
      font-size: 1rem;
      padding: 0.6rem 1.3rem;
      border-radius: 30px;
      font-weight: 500;
    }

    .btn-outline-dark:hover {
      background-color: var(--primary-color);
      color: var(--white);
    }

    /* Card */
    .card {
      border-radius: var(--border-radius);
      box-shadow: var(--shadow);
      overflow: hidden;
      background-color: var(--white);
    }

    /* Table */
    .table thead th {
      background-color: var(--table-header-bg);
      color: var(--table-header-text);
      font-size: 1.1rem;
      padding: 1rem;
      border-bottom: none;
    }

    .table-hover tbody tr:hover {
      background-color: var(--hover-row);
      transform: scale(1.01);
      cursor: pointer;
    }

    .table tbody td {
      padding: 1rem;
      font-size: 1rem;
      vertical-align: middle;
    }

    /* Badges */
    .badge {
      font-size: 0.95rem;
      padding: 8px 14px;
      border-radius: 20px;
      font-weight: 500;
    }

    .badge-full {
      background-color: var(--danger);
      color: var(--white);
    }

    .badge-available {
      background-color: var(--success);
      color: var(--white);
    }

    /* Responsive */
    @media (max-width: 768px) {
      .main {
        margin-left: 250px; /* Đồng bộ với sidebar */
        padding: 40px;
        padding-top: 60px;
    }


      .btn-teal,
      .btn-outline-dark {
        font-size: 0.95rem;
        padding: 0.5rem 1rem;
      }

      h2.text-dark {
        font-size: 1.5rem;
      }

      .table thead th,
      .table tbody td {
        font-size: 0.95rem;
        padding: 0.75rem;
      }
    }
    .table-hover tbody tr:hover {
        background-color: #f0f4ff;
        cursor: pointer;
    }

    .badge-available {
        background-color: #22c55e;
        padding: 5px 12px;
        border-radius: 20px;
        font-weight: 500;
        color: white;
    }

    .badge-full {
        background-color: #ef4444;
        padding: 5px 12px;
        border-radius: 20px;
        font-weight: 500;
        color: white;
    }
 .wow-title {
  font-size: 2.4rem;
  font-weight: 700;
  position: relative;
  display: inline-block;
  margin-bottom: 1rem;
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
  transition: transform 0.4s ease, text-shadow 0.4s ease;
}

.wow-title:hover .glow-text {
  transform: translateY(-3px) scale(1.03);
  text-shadow: 0 0 10px rgba(30, 59, 138, 0.5), 0 0 20px rgba(63, 95, 166, 0.4);
}

@keyframes gradientMove {
  0% {
    background-position: 0% center;
  }
  100% {
    background-position: 400% center;
  }
}

.icon-gradient {
  background: linear-gradient(135deg, #60a5fa, #3f5fa6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}


            </style>
        </head>
        <body>

            <!-- MAIN CONTENT -->
           <div class="main-content">

   <div class="px-2 mb-4">
  <div class="d-flex justify-content-between align-items-start flex-wrap">
  <h2 class="wow-title">
  <i class="bi bi-building me-2 icon-gradient"></i>
  <span class="glow-text">Block Management</span>
</h2>


  </div>

  <div class="text-end mt-3">
    <a href="<%= ctx %>/admin/blocks?action=new" class="btn btn-teal">
      <i class="bi bi-building-add"></i> Add Block
    </a>
  </div>
</div>

                <div class="card shadow-sm">
                    <!-- Removed: <div class="card-header">List of Blocks</div> -->
                    <div class="card-body p-0">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th><i class="bi bi-building"></i> Block Name</th>
                                    <th><i class="bi bi-grid-3x3-gap"></i> Total Rooms</th>
                                    <th><i class="bi bi-check-circle"></i> Available Rooms</th>
                                    <th><i class="bi bi-info-circle"></i> Status</th>

                                </tr>

                            </thead>

                            <tbody>
                                <% if (blockList != null && !blockList.isEmpty()) {
                                    for (Block b : blockList) {
                                        boolean isFull = b.getAvailableRooms() == 0;
                                %>
                                <tr>
                                    <td><%= b.getBlockName() %></td>
                                    <td><%= b.getRoomCount() %></td>
                                    <td><%= b.getAvailableRooms() %></td>
                                    <td>
                                        <span class="badge <%= isFull ? "badge-full" : "badge-available" %>">
    <%= isFull ? "Full" : "Available" %>
    </span>
                                    </td>

                                </tr>
                                <% }
                        } else { %>
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-4">No blocks found.</td>
                                </tr>
                                <% } %>
                            </tbody>


                        </table>

                    </div>

                </div>

            </div>


            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        </body>
    </html>
