<%@page import="java.util.List"%>
<%@page import="model.RentalRequest"%>
<%@page import="model.Customer"%>
<%@page import="model.Room"%>
<%@page import="model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Manager - Rental Requests</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>

<%
    // ✅ Login check
    User manager = (User) session.getAttribute("manager");
    if (manager == null) {
        response.sendRedirect(request.getContextPath() + "/Login");
        return;
    }

    List<RentalRequest> requests = (List<RentalRequest>) request.getAttribute("requests");
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    String message = request.getParameter("success");
%>

<div class="container mt-5">
    <h2 class="mb-4">Manage Rental Requests</h2>

    <% if ("approve".equals(message)) { %>
        <div class="alert alert-success">✔ Rental request approved successfully!</div>
    <% } %>

    <% if (requests != null && !requests.isEmpty()) { %>
        <table class="table table-bordered table-hover">
            <thead class="table-success">
                <tr>
                    <th>#</th>
                    <th>Customer</th>
                    <th>Room</th>
                    <th>Request Date</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            <%
                int i = 1;
                for (RentalRequest r : requests) {
                    String customerName = getCustomerName(customers, r.getCustomerID());
                    String roomNumber = getRoomNumber(rooms, r.getRoomID());
            %>
                <tr>
                    <td><%= i++ %></td>
                    <td><%= customerName %></td>
                    <td><%= roomNumber %></td>
                    <td><%= r.getRequestDate() %></td>
                    <td>
                        <% if ("Pending".equalsIgnoreCase(r.getRequestStatus())) { %>
                            <span class="badge bg-warning text-dark">Pending</span>
                        <% } else if ("Approved".equalsIgnoreCase(r.getRequestStatus())) { %>
                            <span class="badge bg-success">Approved</span>
                        <% } else { %>
                            <span class="badge bg-danger">Rejected</span>
                        <% } %>
                    </td>
                    <td>
                        <% if ("Pending".equalsIgnoreCase(r.getRequestStatus())) { %>
                            <form action="<%= request.getContextPath() %>/manager/manage-requests" method="post" class="d-inline">
                                <input type="hidden" name="requestId" value="<%= r.getRequestID() %>"/>
                                <input type="hidden" name="action" value="approve"/>
                                <button type="submit" class="btn btn-sm btn-success"
                                        onclick="return confirm('Are you sure you want to approve this request?')">
                                    Approve
                                </button>
                            </form>
                        <% } else { %>
                            <span class="text-muted">Not available</span>
                        <% } %>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
    <% } else { %>
        <div class="alert alert-info">No rental requests found.</div>
    <% } %>
</div>

</body>
</html>

<%! 
    public String getCustomerName(List<Customer> list, int id) {
        for (Customer c : list) {
            if (c.getCustomerID() == id) return c.getCustomerFullName();
        }
        return "Unknown";
    }

    public String getRoomNumber(List<Room> list, int id) {
        for (Room r : list) {
            if (r.getRoomID() == id) return r.getRoomNumber();
        }
        return "Unknown";
    }
%>
