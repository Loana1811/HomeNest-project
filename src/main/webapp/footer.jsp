<!DOCTYPE html>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Footer HomNest - Nhà Trọ</title>
  <!-- Google Fonts: Roboto cho tiếng Việt -->
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    body {
      font-family: 'Roboto', 'Segoe UI', Arial, Tahoma, Geneva, Verdana, sans-serif;
      background: #f8fafc; /* gray-50 */
    }
    .footer-bg {
      background: linear-gradient(90deg, #e8f5e9 0%, #c8e6c9 100%);
    }
    .footer-text {
      color: #2e7d32;
    }
    .footer-link:hover {
      text-decoration: underline;
      color: #145214;
    }
    .icon-img {
      width: 32px;
      height: 32px;
      object-fit: contain;
      filter: drop-shadow(0 0 1px rgba(0,0,0,0.13));
    }
  </style>
</head>
<body>
  <footer class="footer-bg py-10 px-6 sm:px-12 md:px-20">
    <div class="max-w-7xl mx-auto grid grid-cols-1 sm:grid-cols-3 gap-8">
      <!-- Logo và thương hiệu -->
      <div class="flex flex-col items-center sm:items-start text-center sm:text-left space-y-3">
        <div class="w-16 h-16 rounded-md bg-green-100 flex justify-center items-center">
          <svg xmlns="http://www.w3.org/2000/svg" fill="#2e7d32" viewBox="0 0 24 24" class="w-10 h-10" aria-label="HomNest Logo">
            <path d="M3 11.5L12 3l9 8.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1v-8.5zM12 5.5 6 10v7.5h4v-6h4v6h4V10l-6-4.5z"/>
            <rect x="10" y="11" width="4" height="4" rx="0.5" ry="0.5" />
          </svg>
        </div>
        <h1 class="text-2xl font-bold footer-text">HomNest</h1>
        <a href="mailto:support@homenest.com" class="footer-text footer-link text-base">support@homenest.com</a>
      </div>

      <!-- Dịch vụ & Liên kết -->
      <div class="flex flex-col space-y-5 text-center sm:text-left">
        <!-- Điện -->
        <div class="flex items-center space-x-3 justify-center sm:justify-start">
          <img
            src="https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/c73a22a6-7c52-4325-8b64-1c5a3fe1a959.png"
            alt="Logo EVN Việt Nam"
            onerror="this.onerror=null; this.src='https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/f703e470-256f-4d6f-b77b-4c6fc6b567ae.png';"
            class="icon-img"
          />
          <span class="footer-text font-semibold text-lg">Điện: EVN Việt Nam</span>
        </div>
        <!-- Nước -->
        <div class="flex items-center space-x-3 justify-center sm:justify-start">
          <img
            src="https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/f2d872c9-4d8c-4089-a089-7ee3f4fdc6b6.png"
            alt="Logo CANTHOWASSCO"
            onerror="this.onerror=null; this.src='https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/e55d4500-5187-4e74-aa5e-7325f5d3a6c9.png';"
            class="icon-img"
          />
          <span class="footer-text font-semibold text-lg">Nước: CANTHOWASSCO</span>
        </div>
        <!-- An toàn PCCC -->
        <div class="flex items-center justify-center sm:justify-start">
          <svg class="icon-img" style="color:#e53935" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" aria-label="Icon PCCC">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 2C8.13 7.03 10 12 10 12a6 6 0 1 0 4 0s1.87-4.97-2-10z"/>
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 14v8"/>
          </svg>
          <span class="footer-text font-semibold text-lg ml-3">An toàn phòng cháy chữa cháy</span>
        </div>
      </div>

      <!-- Địa chỉ và điện thoại -->
      <div class="flex flex-col items-center sm:items-start text-center sm:text-left space-y-3">
        <h2 class="text-xl font-bold footer-text">Liên hệ</h2>
        <address class="not-italic footer-text text-base space-y-1">
          <p>Địa chỉ: Ninh Kiều, Cần Thơ</p>
          <p>Điện thoại: <a href="tel:0333890949" class="footer-link">0333890949</a></p>
        </address>
      </div>
    </div>
    <div class="text-center mt-8 text-sm text-gray-600">
      &copy; 2025 HomNest. All rights reserved.
    </div>
  </footer>
</body>
</html>
