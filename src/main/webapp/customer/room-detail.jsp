<%-- 
    Document   : room-detail
    Created on : Jun 14, 2025, 3:45:45 PM
    Author     : ADMIN
--%>

<%@page import="model.Block"%>
<%@page import="model.UtilityType"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Room" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.List" %>
<%
    Room room = (Room) request.getAttribute("room");
    List<Room> featuredRooms = (List<Room>) request.getAttribute("featuredRooms");
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String postDateStr = (room != null && room.getPostedDate() != null)
            ? sdf.format(room.getPostedDate())
            : "N/A";
    
    List<UtilityType> utilityTypes = (List<UtilityType>) request.getAttribute("utilityTypes");
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Chi tiết phòng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet"/>
        <style>
            .room-image {
                width: 100%;
                max-width: 720px;
                height: auto;
                display: block;
                margin: 0 auto;
                border-radius: 10px;
                box-shadow: 0 4px 16px rgba(0,0,0,0.1);
            }
            .room-card {
                border: none;
                box-shadow: 0 4px 20px rgba(0,0,0,0.15);
                border-radius: 15px;
                overflow: hidden;
            }
            .room-info p {
                display: flex;
                align-items: baseline;
                gap: 0.75rem;
                font-size: 1.3rem;
                margin-bottom: 1rem;
                line-height: 1.6;
            }
            .room-info p strong {
                width: 110px;
                display: inline-block;
                font-weight: 700;
                color: #222;
                font-size: 1.35rem;
            }
            .room-price {
                font-size: 1.8rem;
                color: #ff6600;
                font-weight: bold;
                margin-top: 1rem;
            }
            .room-description {
                font-size: 1.25rem;
                padding: 1rem;
                background-color: #fdfdfd;
                border: 1px solid #ddd;
                border-radius: 8px;
                margin-top: 1rem;
                white-space: pre-line;
            }
            .contact-card, .highlight-card {
                font-size: 1.2rem;
                padding: 2rem;
                border-radius: 16px;
                box-shadow: 0 6px 20px rgba(0,0,0,0.08);
            }
            .contact-card h5,
            .highlight-card h6 {
                font-size: 1.45rem;
                font-weight: 700;
                margin-bottom: 1.5rem;
            }
            .contact-avatar {
                width: 80px;
                height: 80px;
                border-radius: 50%;
                background-color: #bbb;
            }
            .contact-card .btn {
                font-size: 1.15rem;
                padding: 12px;
                font-weight: 600;
            }
            .highlight-card p {
                font-size: 1.15rem;
            }
            .highlight-card small {
                font-size: 1rem;
                color: #555;
            }
            .highlight-tag {
                background-color: #e6f9ec;
                color: #218838;
                padding: 8px 12px;
                border-radius: 15px;
                display: inline-block;
                max-width: 150px;
                word-wrap: break-word;
                text-align: center;
                font-size: 14px;
                white-space: normal;
                line-height: 1.3;
                overflow-wrap: break-word;
            }
            .badge-tag {
                display: inline-block;
                background-color: #e6f2ff;
                color: #0066cc;
                border: 1px solid #b3daff;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 0.95rem;
                font-weight: 500;
                transition: 0.3s;
            }
            .badge-tag:hover {
                background-color: #cce6ff;
                color: #004080;
                cursor: default;
            }
            body {
                background-color: #f0fdf4;
            }
        </style>
    </head>
    </head>
    <% String cancel = request.getParameter("cancel");
if ("success".equals(cancel)) { %>
    <div class="alert alert-warning text-center container mt-4">
        <i class="bi bi-x-circle-fill me-2"></i>
        Yêu cầu của bạn đã được hủy thành công.
    </div>
    <% } %>

   <main class="container my-5 flex-grow-1">
    <% if (room != null) { %>
    <div class="row align-items-stretch">
        <!-- Cột trái -->
        <div class="col-md-8">
            <div class="card room-card p-4 mb-4">
                <div class="text-center mb-4">
                    <img src="<%= request.getContextPath()%>/<%= room.getImagePath() != null ? room.getImagePath() : "/uploads/default.jpg"%>"
                         alt="Ảnh phòng"
                         class="room-image"
                         onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/uploads/default.jpg';" />
                </div>
            </div>

            <!-- Thông tin phòng -->
            <div class="card p-4 mb-4" style="background-color: #ffffff; border-radius: 15px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                <div class="room-info">
                    <h3 class="fw-bold text-center mb-4">Phòng: <%= room.getRoomNumber()%></h3>
                    <p><strong>Diện tích:</strong> <%= room.getArea()%> m²</p>
                    <p><strong>Tình trạng:</strong> <%= room.getRoomStatus()%></p>
                    <%
                        List<Block> blockList = (List<Block>) request.getAttribute("blockList");
                        String blockName = "N/A";
                        if (blockList != null) {
                            for (Block b : blockList) {
                                if (b.getBlockID() == room.getBlockID()) {
                                    blockName = b.getBlockName();
                                    break;
                                }
                            }
                        }
                    %>
                    <p><strong>Khu:</strong> <%= blockName%></p>
                    <p><strong>Vị trí:</strong> <%= room.getLocation() != null ? room.getLocation() : "N/A"%></p>
                    <p><strong>Ngày đăng:</strong> <%= postDateStr%></p>
                    <p class="room-price"><strong>Giá thuê:</strong> <%= String.format("%,.0f", room.getRentPrice())%> ₫ / tháng</p>
                </div>

                <!-- Thông tin tiện ích -->
                <div >
                    <h5 class="fw-bold mb-3 text-warning"><i class="bi bi-lightning-charge-fill me-2"></i>Thông tin tiện ích</h5>
                    <%
                        UtilityType dien = null, nuoc = null, wifi = null, rac = null;
                        for (UtilityType ut : utilityTypes) {
                            String name = ut.getUtilityName().toLowerCase();
                            if (name.contains("electricity")) dien = ut;
                            else if (name.contains("water")) nuoc = ut;
                            else if (name.contains("wifi")) wifi = ut;
                            else if (name.contains("trash")) rac = ut;
                        }
                    %>

                    <p style="font-size: 1.2rem;">
                        <strong>Điện:</strong>
                        <%= (room.getIsElectricityFree() == 1 && dien != null)
                            ? String.format("%,.0f", dien.getUnitPrice()) + " ₫ / " + dien.getUnit()
                            : "Miễn phí" %>
                    </p>

                    <p style="font-size: 1.2rem;">
                        <strong>Nước:</strong>
                        <%= (room.getIsWaterFree() == 1 && nuoc != null)
                            ? String.format("%,.0f", nuoc.getUnitPrice()) + " ₫ / " + nuoc.getUnit()
                            : "Miễn phí" %>
                    </p>

                    <p style="font-size: 1.2rem;">
                        <strong>Wifi:</strong>
                        <%= (room.getIsWifiFree() == 1 && wifi != null)
                            ? String.format("%,.0f", wifi.getUnitPrice()) + " ₫ / " + wifi.getUnit()
                            : "Miễn phí" %>
                    </p>

                    <p style="font-size: 1.2rem;">
                        <strong>Rác:</strong>
                        <%= (room.getIsTrashFree() == 1 && rac != null)
                            ? String.format("%,.0f", rac.getUnitPrice()) + " ₫ / " + rac.getUnit()
                            : "Miễn phí" %>
                    </p>
                </div>

                <!-- Mô tả -->
                <div class="card p-4 mb-4" style="background-color: #fff9e6; border-radius: 15px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                    <p style="font-size: 1.35rem; font-weight: 700;"><strong>Mô tả:</strong></p>
                    <div class="room-description">
                        <%= room.getDescription() != null ? room.getDescription() : "Không có mô tả."%>
                    </div>
                </div>

                <!-- Gửi hoặc hủy yêu cầu -->
                <%
                    Boolean alreadyRequested = (Boolean) request.getAttribute("alreadyRequested");
                    if (alreadyRequested == null) alreadyRequested = false;
                %>

                <% if (!alreadyRequested) { %>
                <div class="d-grid gap-2 mt-4">
                    <button type="button" class="btn btn-success btn-lg" data-bs-toggle="modal" data-bs-target="#confirmModal">
                        <i class="bi bi-send-fill"></i> Gửi yêu cầu thuê phòng
                    </button>
                </div>

                <!-- Modal gửi yêu cầu -->
                <div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content rounded-4 shadow">
                            <div class="modal-header">
                                <h5 class="modal-title" id="confirmModalLabel">Xác nhận yêu cầu thuê phòng</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body fs-5 text-center">
                                Bạn có chắc chắn muốn gửi yêu cầu thuê phòng này không?
                                <p class="text-muted mt-3" style="font-size: 0.95rem;">Bạn có thể hủy nếu chưa chắc chắn.</p>
                            </div>
                            <div class="modal-footer justify-content-center">
                                <form id="submitRequestForm" action="<%= request.getContextPath()%>/request-room" method="post">
                                    <input type="hidden" name="roomId" value="<%= room.getRoomID()%>" />
                                </form>
                                <button type="button" class="btn btn-success px-4" onclick="document.getElementById('submitRequestForm').submit();">Gửi yêu cầu</button>
                                <button type="button" class="btn btn-outline-danger px-4" data-bs-dismiss="modal">Không, quay lại</button>
                            </div>
                        </div>
                    </div>
                </div>
                <% } else { %>
                <div class="alert alert-info mt-4 text-center">
                    <i class="bi bi-info-circle-fill me-2"></i>
                    Yêu cầu của bạn đang chờ xét duyệt.
                </div>

                <!-- Hủy yêu cầu -->
                <div class="text-center mt-3">
                    <button type="button" class="btn btn-outline-danger btn-sm" data-bs-toggle="modal" data-bs-target="#confirmCancelModal">
                        <i class="bi bi-x-circle-fill me-1"></i> Hủy yêu cầu
                    </button>
                </div>

                <!-- Modal hủy -->
                <div class="modal fade" id="confirmCancelModal" tabindex="-1" aria-labelledby="confirmCancelLabel" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0" style="background-color: #e9fbe9; color: #000000;">
                            <div class="modal-header border-0">
                                <h5 class="modal-title" id="confirmCancelLabel">Xác nhận hủy yêu cầu</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body fs-5 text-center">
                                Bạn có chắc chắn muốn hủy yêu cầu thuê phòng này không?
                            </div>
                            <div class="modal-footer justify-content-center border-0">
                                <button type="button" class="btn btn-outline-secondary me-2 px-4" data-bs-dismiss="modal">Không</button>
                                <form method="post" action="<%= request.getContextPath() %>/customer/cancel-request">
                                    <input type="hidden" name="roomId" value="<%= room.getRoomID() %>"/>
                                    <button type="submit" class="btn btn-danger px-4">Có, hủy ngay</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>

        <!-- Cột phải -->
        <div class="col-md-4 d-flex flex-column gap-3 h-100">
            <div class="card contact-card mb-4">
                <h5 class="fw-bold mb-3">Liên hệ</h5>
                <div class="d-flex align-items-center mb-3">
                    <img src="<%= request.getContextPath()%>/uploads/default.jpg" alt="Avatar" class="contact-avatar me-3" style="object-fit: cover;">
                    <div><p class="mb-0 fw-bold">HomeNest</p></div>
                </div>
                <a href="tel:0909123456" class="btn btn-success w-100 mb-2">
                    <i class="bi bi-telephone-fill me-2"></i>0889469948
                </a>
            </div>

            <div class="card highlight-card">
                <h6 class="fw-bold mb-3">Phòng nổi bật</h6>
                <% if (featuredRooms != null) {
                    for (Room r : featuredRooms) {
                        String posted = (r.getPostedDate() != null) ? sdf.format(r.getPostedDate()) : "N/A";
                %>
                <a href="room-detail?id=<%= r.getRoomID()%>" class="text-decoration-none text-dark">
                    <div class="d-flex justify-content-between mb-3 border-bottom pb-2">
                        <img src="<%= request.getContextPath()%>/<%= r.getImagePath() != null ? r.getImagePath() : "uploads/default.jpg"%>"
                             alt="thumbnail" class="rounded" style="width: 64px; height: 64px; object-fit: cover;" />
                        <div class="flex-grow-1 d-flex flex-column">
                            <div class="d-flex justify-content-between">
                                <span class="fw-semibold text-dark"><%= String.format("%.3f", r.getRentPrice() / 1_000_000)%> triệu VNĐ/tháng</span>
                                <small class="text-muted" style="white-space: nowrap; min-width: 90px; text-align: right; font-size: 0.75rem; font-style: italic;"><%= posted %></small>
                            </div>
                        </div>
                    </div>
                </a>
                <% }
                } else { %>
                <p class="text-muted">Không có phòng nổi bật.</p>
                <% } %>
            </div>
        </div>
    </div>
    <% } else { %>
    <div class="alert alert-danger mt-5">Không tìm thấy phòng.</div>
    <% } %>
</main>
    

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>