<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Create Incurred Fee</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/4.6.0/remixicon.min.css">
        <script src="https://cdn.tailwindcss.com/3.4.16"></script>
        <script>
            // Nếu muốn dùng màu primary, thêm config cho Tailwind
            tailwind.config = {
                theme: {
                    extend: {
                        colors: {
                            primary: '#22c55e' // xanh lá, bạn có thể đổi màu khác
                        }
                    }
                }
            }
        </script>
    </head>
    <body class="bg-gray-50 min-h-screen">
        <div class="max-w-xl mx-auto mt-10 bg-white p-8 rounded-xl shadow">
            <h2 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2">
                <i class="ri-add-circle-line text-primary"></i> Create New Incurred Fee
            </h2>
            <form action="CreateIncurredFees" method="post" class="space-y-5">
                <!-- Block -->
                <div>
                    <label for="block" class="block text-sm font-medium text-gray-700 mb-1">Block</label>
                    <select id="block" name="block" class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary">
                        <option value="">-- Select Block --</option>
                        <!-- Nếu có backend, dùng JSTL để lặp danh sách block -->
                        <%-- 
                        <c:forEach var="block" items="${blockList}">
                            <option value="${block.blockID}">${block.blockName}</option>
                        </c:forEach>
                        --%>
                        <option value="A">Block A</option>
                        <option value="B">Block B</option>
                    </select>
                </div>
                <!-- Room -->
                <div>
                    <label for="room" class="block text-sm font-medium text-gray-700 mb-1">Room</label>
                    <select id="room" name="room" class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary">
                        <option value="">-- Select Room --</option>
                        <%-- 
                        <c:forEach var="room" items="${roomList}">
                            <option value="${room.roomID}">${room.roomNumber}</option>
                        </c:forEach>
                        --%>
                        <option value="101">101</option>
                        <option value="202">202</option>
                    </select>
                </div>
                <!-- Bill Date -->
                <div>
                    <label for="billDate" class="block text-sm font-medium text-gray-700 mb-1">Bill Date</label>
                    <input type="date" id="billDate" name="billDate" class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary">
                </div>
                <!-- Fee Name -->
                <div>
                    <label for="feeName" class="block text-sm font-medium text-gray-700 mb-1">Fee Name</label>
                    <select id="feeName" name="feeName" class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary">
                        <option value="">-- Select Fee Type --</option>
                        <%-- 
                        <c:forEach var="fee" items="${feeTypeList}">
                            <option value="${fee.incurredFeeTypeID}">${fee.feeName}</option>
                        </c:forEach>
                        --%>
                        <option value="1">Cleaning Fee</option>
                        <option value="2">Parking Fee</option>
                    </select>
                </div>
                <!-- Amount -->
                <div>
                    <label for="amount" class="block text-sm font-medium text-gray-700 mb-1">Amount</label>
                    <input type="number" id="amount" name="amount" min="0" step="1000" placeholder="Enter amount (VND)" class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary">
                </div>
                <!-- Submit -->
                <div class="pt-2">
                    <button type="submit"
                            class="w-full bg-primary text-white py-2 rounded-lg font-semibold hover:bg-green-600 transition flex items-center justify-center">
                        <i class="ri-save-line mr-1"></i>
                        Save Incurred Fee
                    </button>
                </div>
            </form>
        </div>
    </body>
</html>
