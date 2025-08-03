<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>HomNest Footer - Rental System</title>
  <!-- Google Fonts: Roboto for Vietnamese support -->
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    :root {
      --primary-color: #1e3b8a;
      --secondary-color: #c7d2fe;
      --accent-color: #2a4d69;
      --text-color: #111827;
      --white: #ffffff;
      --background-color: #f5f9fc;
      --sidebar-color: #003459;
      --shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      --border-radius: 12px;
    }

    body {
      font-family: 'Roboto', 'Segoe UI', Arial, Tahoma, Geneva, Verdana, sans-serif;
      background: var(--background-color);
    }

    .footer-bg {
      background: linear-gradient(90deg, var(--primary-color) 0%, var(--sidebar-color) 100%);
    }

    .footer-text {
      color: var(--white);
    }

    .footer-link {
      color: var(--secondary-color);
      transition: color 0.2s ease-in-out;
    }

    .footer-link:hover {
      text-decoration: underline;
      color: var(--white);
    }

    .icon-img {
      width: 32px;
      height: 32px;
      object-fit: contain;
      filter: drop-shadow(0 0 1px rgba(0, 0, 0, 0.13));
    }
  </style>
</head>
<body>
  <footer class="footer-bg py-10 px-6 sm:px-12 md:px-20">
    <div class="max-w-7xl mx-auto grid grid-cols-1 sm:grid-cols-3 gap-8">
     <!-- Logo and Brand -->
<div class="flex flex-col items-center sm:items-start text-center sm:text-left space-y-3">
  <div class="w-16 h-16 rounded-full bg-white flex justify-center items-center shadow-md">
    <svg xmlns="http://www.w3.org/2000/svg" fill="var(--primary-color)" viewBox="0 0 24 24" class="w-10 h-10" aria-label="HomNest Logo">
      <path d="M3 11.5L12 3l9 8.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1v-8.5zM12 5.5 6 10v7.5h4v-6h4v6h4V10l-6-4.5z"/>
      <rect x="10" y="11" width="4" height="4" rx="0.5" ry="0.5" />
    </svg>
  </div>
  <h1 class="text-2xl font-bold footer-text">HomNest</h1>
  <a href="mailto:support@homenest.com" class="footer-text footer-link text-base">support@homenest.com</a>
</div>


      <!-- Services & Links -->
      <div class="flex flex-col space-y-5 text-center sm:text-left">
        <!-- Electricity -->
        <div class="flex items-center space-x-3 justify-center sm:justify-start">
          <img src="https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/c73a22a6-7c52-4325-8b64-1c5a3fe1a959.png"
               alt="EVN Vietnam Logo"
               onerror="this.onerror=null; this.src='https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/f703e470-256f-4d6f-b77b-4c6fc6b567ae.png';"
               class="icon-img" />
<span class="footer-text font-semibold text-lg">Electricity: EVN Vietnam</span>
        </div>
        <!-- Water -->
        <div class="flex items-center space-x-3 justify-center sm:justify-start">
          <img src="https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/f2d872c9-4d8c-4089-a089-7ee3f4fdc6b6.png"
               alt="CANTHOWASSCO Logo"
               onerror="this.onerror=null; this.src='https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/e55d4500-5187-4e74-aa5e-7325f5d3a6c9.png';"
               class="icon-img" />
          <span class="footer-text font-semibold text-lg">Water: CANTHOWASSCO</span>
        </div>
        <!-- Fire Safety -->
        <div class="flex items-center justify-center sm:justify-start">
          <svg class="icon-img text-red-600" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" aria-label="Fire Safety Icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 2C8.13 7.03 10 12 10 12a6 6 0 1 0 4 0s1.87-4.97-2-10z"/>
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 14v8"/>
          </svg>
          <span class="footer-text font-semibold text-lg ml-3">Fire Safety & Prevention</span>
        </div>
      </div>

      <!-- Contact Information -->
      <div class="flex flex-col items-center sm:items-start text-center sm:text-left space-y-3">
        <h2 class="text-xl font-bold footer-text">Contact</h2>
        <address class="not-italic footer-text text-base space-y-1">
          <p>Address: Ninh Kieu, Can Tho</p>
          <p>Phone: <a href="tel:0333890949" class="footer-link">0333890949</a></p>
        </address>
      </div>
    </div>
    <div class="text-center mt-8 text-sm text-white/70">
      &copy; 2025 HomNest. All rights reserved.
    </div>
  </footer>
</body>
</html>
                                                                                                                                