<%-- 
    Document   : roomdetails_admin
    Created on : Jul 23, 2025, 6:13:49 PM
    Author     : Admin
--%>

<%-- 
    Document   : roomdetails_admin
    Created on : Jul 23, 2025, 6:13:49 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Room" %>
<%
    Room room = (Room) request.getAttribute("room");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Phòng</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 40px auto;
            padding: 20px;
            max-width: 800px;
            background-color: #ecf2f4;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            color: #2c3e50;
        }
        h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 20px;
        }
        .room-image {
            text-align: center;
            margin-bottom: 30px;
        }
        .room-image img {
            width: 100%;
            max-width: 600px;
            border-radius: 10px;
            border: 1px solid #ccc;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        .room-detail {
            display: grid;
            grid-template-columns: 150px 1fr;
            row-gap: 12px;
            column-gap: 10px;
            font-size: 16px;
        }
        .room-detail .label {
            font-weight: bold;
            color: #4b6584;
        }
        .button-group {
            text-align: center;
            margin-top: 30px;
        }
        .button-group a {
            display: inline-block;
            margin: 0 10px;
            text-decoration: none;
            padding: 10px 20px;
            background-color: #4a69bd;
            color: #fff;
            border-radius: 6px;
            transition: background-color 0.3s ease;
        }
        .button-group a:hover {
            background-color: #3b5998;
        }
    </style>
</head>
<body>
    <h2><div>Phòng <%= room.getRoomNumber() %></div></h2>

    <div class="room-image">
        <% if (room.getImagePath() != null && room.getImagePath().length > 0) { %>
            <img src="<%= request.getContextPath() %>/admin/rooms?action=room-image&id=<%= room.getRoomID() %>" 
                 alt="Ảnh phòng">
        <% } else { %>
            <p>Không có hình ảnh.</p>
        <% } %>
    </div>

    <div class="room-detail">
        <div class="label">Giá thuê:</div>
        <div><%= room.getRentPrice() %> VNĐ</div>

        <div class="label">Diện tích:</div>
        <div><%= room.getArea() %> m²</div>

        <div class="label">Vị trí:</div>
        <div><%= room.getLocation() %></div>

        <div class="label">Block:</div>
        <div><%= room.getBlockID() %></div>
    </div>

    <div class="button-group">
        <a href="rooms?action=list">← Quay lại</a>
        <a href="rooms?action=edit&id=<%= room.getRoomID() %>">Chỉnh Sửa</a>
        
    </div>
</body>
</html>
    