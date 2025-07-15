<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Chi Tiết Hợp Đồng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f1fdfd;
            }

            .contract-container {
                max-width: 960px;
                margin: 40px auto;
                background: #ffffff;
                padding: 40px;
                border: 1px solid #ccc;
                border-radius: 8px;
                box-shadow: 0 0 12px rgba(0, 0, 0, 0.08);
                line-height: 1.8;
            }

            .contract-header {
                text-align: center;
                margin-bottom: 30px;
            }

            .contract-title {
                font-size: 1.6rem;
                font-weight: bold;
                color: #008080;
                text-decoration: underline;
                margin-top: 10px;
            }

            .contract-section h2 {
                font-size: 1.2rem;
                color: #008080;
                margin-top: 30px;
                border-bottom: 1px dashed #aaa;
                padding-bottom: 5px;
            }

            .info-columns {
                display: flex;
                justify-content: space-between;
                flex-wrap: wrap;
            }

            .info-columns > div {
                width: 48%;
            }

            .signature-section {
                display: flex;
                justify-content: space-between;
                margin-top: 50px;
            }

            .signature-box {
                text-align: center;
                width: 45%;
            }

            .btn-teal {
                background-color: #008080;
                color: white;
            }

            .btn-teal:hover {
                background-color: #006666;
            }

            .text-muted {
                font-style: italic;
            }

            hr.dashed {
                border: 1px dashed #000;
                width: 200px;
                margin: 10px auto;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="contract-container">

                <div class="contract-header">
                    <strong>CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</strong><br>
                    <strong>Độc lập - Tự do - Hạnh phúc</strong><br>
                    <hr class="dashed">
                    <div class="contract-title">HỢP ĐỒNG CHO THUÊ NHÀ</div>
                    <div class="text-muted">(Thuê ngắn hạn)</div>
                </div>

                <div class="contract-section">
                    <h2>1. Thông Tin Các Bên</h2>
                    <div class="info-columns">
                        <div>
                            <strong>Bên Cho Thuê</strong><br>
                            Họ và Tên: Nguyễn Văn A<br>
                            CCCD: 123456789<br>
                            SĐT: 0901xxxxxx<br>
                            Địa chỉ: 123 Đường ABC, Quận 1, TP. Hồ Chí Minh
                        </div>
                        <div>
                            <strong>Bên Thuê</strong><br>
                            Họ và Tên: ${customer.customerFullName}<br>
                            CCCD: ${customer.CCCD}<br>
                            SĐT: ${customer.phoneNumber}<br>
                            Giới tính: ${customer.gender}<br>
                            Ngày sinh: ${customer.birthDate}<br>
                            Địa chỉ: ${customer.address}<br>
                            Email: ${customer.email}<br>
                            Mã người thuê: ${tenant.tenantID}<br>
                            Ngày tham gia: ${tenant.joinDate}<br>
                        </div>
                    </div>
                </div>

                <div class="contract-section">
                    <h2>2. Thông Tin Phòng Thuê</h2>
                    Số phòng: ${room.roomNumber}<br>
                    Vị trí: ${room.location}<br>
                    Diện tích: ${room.area} m²<br>
                    Tình trạng: Sạch sẽ, sẵn sàng sử dụng
                </div>

                <div class="contract-section">
                    <h2>3. Thời Hạn Thuê</h2>
                    Ngày bắt đầu: ${contract.startDate}<br>
                    Ngày kết thúc: ${contract.endDate}<br>
                    Loại hợp đồng: Thuê ngắn hạn
                </div>

                <div class="contract-section">
                    <h2>4. Giá Thuê & Thanh Toán</h2>
                    Giá thuê: ${contract.amount} VND / tháng<br>
                    Tiền đặt cọc: ${contract.amount} VND (1 tháng tiền thuê)<br>
                    Phương thức thanh toán: Vào ngày 1 hàng tháng, bằng tiền mặt hoặc chuyển khoản
                </div>

                <div class="contract-section">
                    <h2>5. Quyền Lợi & Nghĩa Vụ</h2>
                    Người thuê chịu trách nhiệm chi phí điện nước, giữ vệ sinh và không làm hư hỏng tài sản.<br>
                    Chủ nhà đảm bảo an toàn tài sản và thực hiện sửa chữa nhỏ.<br>
                    Cấm cho thuê lại hoặc thay đổi người ở nếu không có sự đồng ý.
                </div>

                <div class="contract-section">
                    <h2>6. Chấm Dứt Hợp Đồng</h2>
                    Một trong hai bên phải báo trước ít nhất 15 ngày nếu muốn chấm dứt sớm.<br>
                    Nếu người thuê tự ý rời đi không báo trước, tiền cọc sẽ không được hoàn lại.<br>
                    Phòng phải được trả lại trong tình trạng ban đầu khi hết hạn thuê.
                </div>

                <div class="contract-section">
                    <h2>7. Hiệu Lực</h2>
                    Hợp đồng có hiệu lực kể từ ngày ký.<br>
                    Cả hai bên cam kết thực hiện nghiêm túc. Mọi tranh chấp sẽ được giải quyết bằng thương lượng trước.
                </div>

                <div class="contract-section">
                    <strong>Ngày ký: ${contract.contractCreatedAt}</strong>
   
                </div>

                <div class="signature-section">
                    <div class="signature-box">
                        <strong>Bên Cho Thuê</strong><br><br>
                        (Ký tên)<br>
                        Donal
                    </div>
                    <div class="signature-box">
                        <strong>Bên Thuê</strong><br><br>
                        (Ký tên)<br>
                        ${customer.customerFullName}
                    </div>
                </div>

                <div class="text-center mt-4">
                    <button onclick="window.print()" class="btn btn-teal">In hợp đồng</button>
                    <a href="contracts/generatePdf?id=${contract.contractId}" class="btn btn-teal ms-2" target="_blank">Xem PDF</a>
                    <a href="Contracts" class="btn btn-secondary ms-2">Quay lại</a>
                </div>

            </div>
        </div>
    </body>
</html>
