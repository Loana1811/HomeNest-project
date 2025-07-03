<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>View Electricity Usage</title>

  <!-- Bootstrap -->
  <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
  <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>

  <!-- Tailwind CDN -->
  <script src="https://cdn.tailwindcss.com/3.4.16"></script>
  <script>
    tailwind.config = {
      theme: {
        extend: {
          colors: {
            primary: '#4CAF50', // green
            secondary: '#0ea5e9'
          },
          borderRadius: {
            button: '8px'
          }
        }
      }
    }
  </script>

  <!-- Remixicon + Fonts -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/4.6.0/remixicon.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">

  <!-- Custom Style cho Month Picker -->
  <style>
    .month-picker-group {
      display: flex;
      align-items: center;
      width: 270px;
      background: #f8fafc;
      border-radius: 8px;
      border: 1px solid #e5e7eb;
      padding: 0 0 0 12px;
      box-sizing: border-box;
      position: relative;
    }
    .month-picker-group input[type="month"] {
      border: none;
      background: transparent;
      font-size: 1.15em;
      color: #222;
      outline: none;
      width: 110px;
      padding: 18px 0 6px 0;
      margin-right: 10px;
      font-weight: bold;
    }
    .calendar-icon {
      width: 48px;
      height: 48px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #9ca3af;
      border-left: 1px solid #e5e7eb;
      background: #f8fafc;
      cursor: pointer;
    }
    .month-label {
      position: absolute;
      font-size: 0.98em;
      color: #bdbdbd;
      left: 18px;
      top: 6px;
      pointer-events: none;
      font-family: Arial, sans-serif;
    }
    .relative {
      position: relative;
    }
  </style>
</head>

<body class="bg-gray-100">

  <!-- HEADER -->
  <header class="bg-primary text-white shadow-lg">
    <div class="flex items-center h-16 px-4 w-full">
      <div class="font-['Pacifico'] text-3xl mr-8">logo</div>

      <nav class="flex space-x-6 text-sm">
        <a href="#" class="hover:text-white flex items-center">
          <i class="ri-dashboard-line mr-1"></i> DashBoard for Manager
        </a>
        <a href="#" class="hover:text-white flex items-center">
          <i class="ri-building-2-line mr-1"></i> Room Management
        </a>
        <a href="#" class="hover:text-white flex items-center">
          <i class="ri-user-settings-line mr-1"></i> Customer Management
        </a>
        <a href="#" class="text-white font-semibold flex items-center">
          <i class="ri-flashlight-line mr-1"></i> Utility Management
        </a>
        <a href="#" class="text-white font-semibold flex items-center">
          <i class="ri-flashlight-line mr-1"></i> Incurred Fees Management
        </a>
        <a href="#" class="hover:text-white flex items-center">
          <i class="ri-file-list-3-line mr-1"></i> Invoices Management
        </a>
        <a href="#" class="hover:text-white flex items-center">
          <i class="ri-file-list-3-line mr-1"></i> Revenues Statistics
        </a>
        <a href="#" class="hover:text-white flex items-center">
          <i class="ri-file-list-3-line mr-1"></i> Category Management
        </a>
        <a href="#" class="hover:text-white flex items-center">
          <i class="ri-notification-2-line mr-1"></i> Notification Management
        </a>
      </nav>

      <div class="ml-auto flex items-center space-x-4">
        <a href="#" class="hover:text-white">
          <i class="ri-user-line text-xl"></i>
        </a>
      </div>
    </div>
  </header>

  <!-- Place this inside your <body> where you want the month picker to appear -->
<div class="flex items-center justify-end w-full max-w-2xl mt-8 mb-6">
  <div class="relative w-64">
  
 

    
  </div>
</div>

</body>
</html>
