<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
String ctx = request.getContextPath();
%>

<html>
<head>
    <title>HỢP ĐỒNG THUÊ NHÀ Ở</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
    <style>
        body {
            font-family: 'Times New Roman', Times, serif;
            background-color: #f8f9fa;
            line-height: 1.6;
            margin: 0;
            padding: 0;
        }
        .contract-container {
            max-width: 1000px;
            margin: 20px auto;
            background: #ffffff;
            padding: 30px;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
            overflow: auto;
        }
        .contract-header {
            text-align: center;
            margin-bottom: 20px;
        }
        .contract-title {
            font-size: 1.8rem;
            font-weight: bold;
            color: #343a40;
            text-transform: uppercase;
            margin-top: 10px;
        }
        .contract-section h2 {
            font-size: 1.3rem;
            color: #343a40;
            margin-top: 20px;
            border-bottom: 2px solid #343a40;
            padding-bottom: 5px;
        }
        .info-columns {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 15px;
        }
        .info-columns > div {
            width: 48%;
            word-wrap: break-word;
        }
        .signature-section {
            display: flex;
            justify-content: space-between;
            margin-top: 30px;
            flex-wrap: wrap;
        }
        .signature-box {
            text-align: center;
            width: 45%;
            margin-bottom: 20px;
        }
        .btn-teal {
            background-color: #007bff;
            color: white;
            border: none;
        }
        .btn-teal:hover {
            background-color: #0056b3;
        }
        .text-muted {
            font-style: italic;
            color: #6c757d;
        }
        hr.dashed {
            border: 1px dashed #343a40;
            width: 250px;
            margin: 10px auto;
        }
        .contract-footer {
            text-align: center;
            margin-top: 20px;
            font-size: 0.9rem;
            color: #6c757d;
        }
        ul {
            padding-left: 20px;
        }
        @media (max-width: 768px) {
            .info-columns > div {
                width: 100%;
            }
            .signature-box {
                width: 100%;
            }
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
            <div class="contract-title">HỢP ĐỒNG THUÊ NHÀ Ở</div>
            <div class="text-muted">Số: ${contract.contractId}/HĐTN</div>
            <div class="text-muted">Địa điểm ký: TP. Hồ Chí Minh, ngày <fmt:formatDate value="${contract.contractCreatedAt}" pattern="dd/MM/yyyy" /> hoặc ${contract.contractCreatedAt == null ? 'Chưa xác định' : ''}</div>
        </div>

        <div class="contract-section">
            <p>Hôm nay, ngày <fmt:formatDate value="${contract.contractCreatedAt}" pattern="dd/MM/yyyy" /> hoặc ${contract.contractCreatedAt == null ? 'Chưa xác định' : ''}, tại TP. Hồ Chí Minh, chúng tôi gồm có:</p>
            <h2>Điều 1: Thông Tin Các Bên</h2>
            <div class="info-columns">
                <div>
                    <strong>BÊN CHO THUÊ (BÊN A)</strong><br>
                    Họ và tên: Nguyễn Văn A<br>
                    Số CCCD/CMND: 123456789<br>
                    Ngày cấp: 01/01/2010<br>
                    Nơi cấp: TP. Hồ Chí Minh<br>
                    Địa chỉ thường trú: 123 Đường ABC, Quận 1, TP. Hồ Chí Minh<br>
                    Số điện thoại: 0901xxxxxx
                </div>
                <div>
                    <strong>BÊN THUÊ (BÊN B)</strong><br>
                    Họ và tên: ${customer.customerFullName != null ? customer.customerFullName : 'Chưa cung cấp'}<br>
                    Số CCCD/CMND: ${customer.CCCD != null ? customer.CCCD : 'Chưa cung cấp'}<br>
                    Ngày sinh: <fmt:formatDate value="${customer.birthDate}" pattern="dd/MM/yyyy" /> hoặc ${customer.birthDate == null ? 'Chưa cung cấp' : ''}<br>
                    Địa chỉ thường trú: ${customer.address != null ? customer.address : 'Chưa cung cấp'}<br>
                    Số điện thoại: ${customer.phoneNumber != null ? customer.phoneNumber : 'Chưa cung cấp'}<br>
                    Email: ${customer.email != null ? customer.email : 'Chưa cung cấp'}<br>
                    Mã người thuê: ${tenant.tenantID != null ? tenant.tenantID : 'Chưa cung cấp'}<br>
                </div>
            </div>
        </div>

        <div class="contract-section">
            <h2>Điều 2: Đối Tượng Hợp Đồng</h2>
            <p>Bên A đồng ý cho Bên B thuê nhà ở với các thông tin sau:</p>
            Địa chỉ nhà ở: ${room.location != null ? room.location : 'Chưa cung cấp'}, Block ${room.blockID != null ? room.blockID : 'Chưa xác định'}<br>
            Số phòng: ${room.roomNumber != null ? room.roomNumber : 'Chưa cung cấp'}<br>
            Diện tích: ${room.area != null ? room.area : 'Chưa cung cấp'} m²<br>
            Mô tả: ${room.description != null ? room.description : 'Không có mô tả'}<br>
            Tình trạng: ${room.roomStatus != null ? room.roomStatus : 'Chưa xác định'}<br>
            
        </div>

        <div class="contract-section">
            <h2>Điều 3: Thời Hạn Thuê</h2>
            Thời gian thuê: Từ ngày <fmt:formatDate value="${contract.startDate}" pattern="dd/MM/yyyy" /> hoặc ${contract.startDate == null ? 'Chưa xác định' : ''} đến ngày <fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy" /> hoặc ${contract.endDate == null ? 'Chưa xác định' : ''}<br>
            Ghi chú: Hợp đồng có thể được gia hạn theo thỏa thuận bằng văn bản giữa hai bên, tuân thủ quy định của pháp luật.
        </div>

        <div class="contract-section">
            <h2>Điều 4: Giá Thuê và Phương Thức Thanh Toán</h2>
            Giá thuê: <fmt:formatNumber value="${room.rentPrice != null ? room.rentPrice : 0}" type="currency" currencySymbol="VND" groupingUsed="true"/> / tháng<br>
            Tiền đặt cọc: <fmt:formatNumber value="${contract.deposit != null ? contract.deposit : 0}" type="currency" currencySymbol="VND" groupingUsed="true"/> (Bằng 1 tháng tiền thuê)<br>
            Phương thức thanh toán: Chuyển khoản ngân hàng hoặc tiền mặt, thanh toán vào ngày 1 hàng tháng.<br>
            Thông tin tài khoản ngân hàng (nếu có): [Tên ngân hàng, số tài khoản của Bên A].<br>
            Ghi chú: Giá thuê không bao gồm chi phí tiện ích (điện, nước, wifi, rác) trừ trường hợp được nêu ở Điều 2.
        </div>

        <div class="contract-section">
            <h2>Điều 5: Quyền và Nghĩa Vụ của Các Bên</h2>
            <strong>5.1. Quyền và nghĩa vụ của Bên A (Bên cho thuê):</strong>
            <ul>
                <li>Giao nhà ở và tài sản kèm theo (nếu có) đúng thời điểm và trong tình trạng sử dụng tốt theo thỏa thuận.</li>
                <li>Đảm bảo quyền sử dụng nhà ở hợp pháp và ổn định cho Bên B trong thời hạn thuê.</li>
                <li>Thực hiện sửa chữa lớn hoặc cải tạo nhà ở theo thỏa thuận, trừ trường hợp do lỗi của Bên B.</li>
                <li>Cung cấp các tiện ích miễn phí như đã nêu ở Điều 2 (nếu có).</li>
                <li>Thông báo trước 30 ngày nếu muốn chấm dứt hợp đồng sớm theo quy định pháp luật.</li>
            </ul>
            <strong>5.2. Quyền và nghĩa vụ của Bên B (Bên thuê):</strong>
            <ul>
                <li>Thanh toán tiền thuê và các chi phí khác đúng hạn theo thỏa thuận.</li>
                <li>Sử dụng nhà ở đúng mục đích, giữ gìn vệ sinh, không làm hư hỏng tài sản.</li>
                <li>Chịu trách nhiệm chi trả chi phí tiện ích (điện, nước, wifi, rác) trừ các dịch vụ miễn phí.</li>
                <li>Không tự ý cho thuê lại, chuyển nhượng hoặc thay đổi người ở nếu không có sự đồng ý bằng văn bản của Bên A.</li>
                <li>Trả lại nhà ở trong tình trạng ban đầu (trừ hao mòn tự nhiên) khi kết thúc hợp đồng.</li>
            </ul>
        </div>

        <div class="contract-section">
            <h2>Điều 6: Chấm Dứt Hợp Đồng</h2>
            <ul>
                <li>Hợp đồng chấm dứt khi hết thời hạn thuê hoặc theo thỏa thuận bằng văn bản của hai bên.</li>
                <li>Bên muốn chấm dứt hợp đồng sớm phải thông báo trước ít nhất 30 ngày, trừ trường hợp pháp luật có quy định khác.</li>
                <li>Nếu Bên B chấm dứt hợp đồng sớm mà không thông báo đúng hạn, tiền đặt cọc sẽ không được hoàn lại.</li>
                <li>Nếu Bên A chấm dứt hợp đồng không đúng thỏa thuận, phải hoàn lại tiền đặt cọc và bồi thường thiệt hại (nếu có).</li>
            </ul>
        </div>

        <div class="contract-section">
            <h2>Điều 7: Giải Quyết Tranh Chấp</h2>
            <ul>
                <li>Mọi tranh chấp phát sinh từ hợp đồng sẽ được giải quyết thông qua thương lượng giữa hai bên.</li>
                <li>Nếu thương lượng không thành, tranh chấp sẽ được giải quyết tại Tòa án nhân dân có thẩm quyền tại TP. Hồ Chí Minh.</li>
            </ul>
        </div>

        <div class="contract-section">
            <h2>Điều 8: Hiệu Lực Hợp Đồng</h2>
            <ul>
                <li>Hợp đồng này có hiệu lực từ ngày <fmt:formatDate value="${contract.contractCreatedAt}" pattern="dd/MM/yyyy" /> hoặc ${contract.contractCreatedAt == null ? 'Chưa xác định' : ''}.</li>
                <li>Hợp đồng được lập thành 02 bản, mỗi bên giữ 01 bản có giá trị pháp lý như nhau.</li>
                <li>Mọi sửa đổi, bổ sung hợp đồng phải được lập thành văn bản và có chữ ký của cả hai bên.</li>
            </ul>
        </div>

        <div class="contract-section">
            <strong>Ngày ký: <fmt:formatDate value="${contract.contractCreatedAt}" pattern="dd/MM/yyyy" /> hoặc ${contract.contractCreatedAt == null ? 'Chưa xác định' : ''}</strong><br>
            <strong>Địa điểm ký: TP. Hồ Chí Minh</strong>
        </div>

        <div class="signature-section">
            <div class="signature-box">
                <strong>ĐẠI DIỆN BÊN CHO THUÊ (BÊN A)</strong><br><br>
                (Ký và ghi rõ họ tên)<br>
                Nguyễn Văn A
            </div>
            <div class="signature-box">
                <strong>ĐẠI DIỆN BÊN THUÊ (BÊN B)</strong><br><br>
                (Ký và ghi rõ họ tên)<br>
                ${customer.customerFullName != null ? customer.customerFullName : 'Chưa cung cấp'}
            </div>
        </div>

        <div class="contract-footer">
            <p><em>Hợp đồng này tuân thủ quy định của Luật Nhà ở 2014, Nghị định 99/2015/NĐ-CP và các văn bản pháp luật liên quan.</em></p>
        </div>

        <div class="text-center mt-4">
            <button onclick="window.print()" class="btn btn-teal">Print</button>
            <a href="contracts/generatePdf?id=${contract.contractId}" class="btn btn-teal ms-2" target="_blank">Download PDF</a>
            <a href="<%=ctx%>/Contracts" class="btn btn-secondary ms-2">Back</a>
        </div>
    </div>
</div>
</body>
</html>