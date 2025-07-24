<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.UtilityReading" %>
<%
    List<UtilityReading> historyList = (List<UtilityReading>) request.getAttribute("historyList");
    String ctx = request.getContextPath();
%>

<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

<h3>🔍 Lịch sử chỉnh sửa tiện ích</h3>
<table class="table table-bordered table-striped">
    <thead class="table-dark text-center">
        <tr>
            <th>#</th>
            <th>Old Reading</th>
            <th>New Reading</th>
            <th>Giá sử dụng</th>
            <th>Ghi chú</th>
            <th>Người chỉnh</th>
            <th>Thời gian</th>
        </tr>
    </thead>
    <tbody>
    <% int index = 1;
       for (UtilityReading r : historyList) { %>
        <tr class="text-center">
            <td><%= index++ %></td>
            <td><%= r.getOldReading() %></td>
            <td><%= r.getNewReading() %></td>
            <td><%= r.getPriceUsed() %></td>
            <td><%= r.getChangedBy() %></td>
            <td><%= r.getChangedBy().contains("|") ? r.getChangedBy().split("\\|")[1] : "" %></td>
            <td><%= r.getUtilityReadingCreatedAt() %></td>
        </tr>
    <% } %>
    </tbody>
</table>

<a href="<%= ctx %>/admin/usage" class="btn btn-secondary">🔙 Trở lại</a>
