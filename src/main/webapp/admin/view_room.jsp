<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Room" %>
<%
    Room room = (Room) request.getAttribute("room");
     String ctx = request.getContextPath();  
     
%>
   <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>    
  
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Details</title>
    <style>
        :root {
            --primary-color: #1e3b8a;
            --accent-color: #3b5998;
            --background-color: #f0f4f8;
            --card-color: #ffffff;
            --text-color: #2c3e50;
            --label-color: #4b6584;
            --shadow: 0 4px 12px rgba(0,0,0,0.1);
            --border-radius: 12px;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 40px auto;
            padding: 30px;
            max-width: 850px;
            background-color: var(--background-color);
            color: var(--text-color);
        }

        .card {
            background-color: var(--card-color);
            padding: 30px;
            border-radius: var(--border-radius);
            box-shadow: var(--shadow);
        }

        h2 {
            text-align: center;
            color: var(--primary-color);
            font-size: 2rem;
            margin-bottom: 25px;
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
            grid-template-columns: 160px 1fr;
            row-gap: 15px;
            column-gap: 12px;
            font-size: 1.1rem;
        }

        .room-detail .label {
            font-weight: 600;
            color: var(--label-color);
        }

        .button-group {
            text-align: center;
            margin-top: 35px;
        }

        .button-group a {
            display: inline-block;
            margin: 0 12px;
            text-decoration: none;
            padding: 10px 24px;
            background-color: var(--primary-color);
            color: #fff;
            border-radius: 8px;
            font-size: 1rem;
            transition: background-color 0.3s ease, transform 0.2s;
        }

        .button-group a:hover {
            background-color: var(--accent-color);
            transform: scale(1.03);
        }
    </style>
     
</head>
<body>
    <div class="card">
        <h2>Room <%= room.getRoomNumber() %></h2>

        <div class="room-image">
            <% if (room.getImagePath() != null && room.getImagePath().length > 0) { %>
            <img src="<%= request.getContextPath() %>/admin/rooms?action=room-image&id=<%= room.getRoomID() %>" alt="Room Image">
            <% } else { %>
            <p>No image available.</p>
            <% } %>
        </div>

        <div class="room-detail">
            <div class="label">Rent Price:</div>
            <div><%= String.format("%,.0f VND", room.getRentPrice()) %></div>

            <div class="label">Area:</div>
            <div><%= room.getArea() %> m²</div>

            <div class="label">Location:</div>
            <div><%= room.getLocation() %></div>

            <div class="label">Block ID:</div>
            <div><%= room.getBlockID() %></div>
        </div>

        <div class="button-group">
            <a href="rooms?action=list">← Back</a>
            <a href="rooms?action=edit&id=<%= room.getRoomID() %>">Edit</a>
        </div>
    </div>
</body>
</html>
