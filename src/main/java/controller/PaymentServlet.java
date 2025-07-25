package controller;

import dao.BillDAO;
import dao.PaymentDAO;
import dao.RevenueDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Bill;
import model.Payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/admin/payment"})
public class PaymentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String billIdRaw = request.getParameter("billId");

        if (billIdRaw == null || billIdRaw.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "⚠️ Không xác định được hóa đơn cần thu tiền.");
            response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
            return;
        }

        forwardBack(request, response, billIdRaw);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (!"collect".equals(action)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hành động không hợp lệ");
            return;
        }

        String billIdRaw = request.getParameter("billId");
        if (billIdRaw == null || billIdRaw.trim().isEmpty()) {
            request.setAttribute("error", "❌ Thiếu mã hóa đơn.");
            forwardBack(request, response, null);
            return;
        }

        try {
            int billId = Integer.parseInt(billIdRaw.trim());

            String amountRaw = request.getParameter("amountPaid");
            if (amountRaw == null || amountRaw.trim().isEmpty()) {
                request.setAttribute("error", "❌ Vui lòng nhập số tiền khách thanh toán.");
                forwardBack(request, response, billId);
                return;
            }

            BigDecimal amountPaid;
            try {
                amountPaid = new BigDecimal(amountRaw.trim());
            } catch (NumberFormatException e) {
                request.setAttribute("error", "❌ Số tiền không hợp lệ.");
                forwardBack(request, response, billId);
                return;
            }

            String method = request.getParameter("paymentMethod");
            String note = request.getParameter("paymentNote");
            Date paymentDate = Date.valueOf(request.getParameter("paymentDate"));

            Payment payment = new Payment();
            payment.setBillId(billId);
            payment.setAmountPaid(amountPaid);
            payment.setPaymentMethod(method);
            payment.setPaymentNote(note);
            payment.setPaymentDate(paymentDate);

            PaymentDAO paymentDAO = new PaymentDAO();
            BillDAO billDAO = new BillDAO();
            RevenueDAO revenueDAO = new RevenueDAO();

            // Thêm khoản thanh toán
            paymentDAO.insertPayment(payment);

            // Tính tổng đã thanh toán
            List<Payment> payments = paymentDAO.getPaymentsByBillId(billId);
            BigDecimal totalPaid = payments.stream()
                    .map(Payment::getAmountPaid)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Lấy tổng tiền hóa đơn
            String selectedMonth = java.time.LocalDate.now().toString().substring(0, 7); // yyyy-MM
            List<Map<String, Object>> billSummary = billDAO.getBillSummaryByMonth(selectedMonth);
            BigDecimal totalAmount = null;
            for (Map<String, Object> row : billSummary) {
                if ((int) row.get("BillID") == billId) {
                    totalAmount = (BigDecimal) row.get("TotalAmount");
                    break;
                }
            }

            if (totalAmount == null) {
                request.setAttribute("error", "❌ Không tìm thấy hóa đơn.");
                forwardBack(request, response, billId);
                return;
            }

            // Cập nhật trạng thái hóa đơn
            String newStatus = totalPaid.compareTo(totalAmount) >= 0 ? "PAID"
                    : totalPaid.compareTo(BigDecimal.ZERO) > 0 ? "PARTIAL" : "Unpaid";
            boolean billUpdated = billDAO.updateBillStatus(billId, newStatus);

            if (!billUpdated) {
                request.setAttribute("error", "❌ Không thể cập nhật trạng thái hóa đơn.");
                forwardBack(request, response, billId);
                return;
            }

            // Tạo phiếu thu nếu hóa đơn đã thanh toán đủ và chưa có revenue nào
            if ("PAID".equals(newStatus) && !revenueDAO.existsRevenueForBill(billId)) {
                boolean revenueCreated = revenueDAO.createRevenueForPaidBill(billId, 1);
                if (!revenueCreated) {
                    request.setAttribute("error", "❌ Không thể tạo phiếu thu cho hóa đơn.");
                    forwardBack(request, response, billId);
                    return;
                }
            }

            // Tính số tiền còn lại
            BigDecimal remaining = totalAmount.subtract(totalPaid);
            String msg = remaining.compareTo(BigDecimal.ZERO) > 0
                    ? "💰 Đã thu được " + totalPaid + "đ. Còn nợ: " + remaining + "đ."
                    : "✅ Khách đã thanh toán đầy đủ.";

            request.getSession().setAttribute("success", msg);
            response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "❌ Lỗi kỹ thuật: " + e.getMessage());
            forwardBack(request, response, billIdRaw);
        }
    }

    private void forwardBack(HttpServletRequest request, HttpServletResponse response, Object billIdRaw)
            throws ServletException, IOException {
        try {
            if (billIdRaw == null) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "⚠️ Không xác định được hóa đơn cần hiển thị.");
                response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
                return;
            }

            int billId = Integer.parseInt(billIdRaw.toString().trim());

            BillDAO billDAO = new BillDAO();
            PaymentDAO paymentDAO = new PaymentDAO();

            // Lấy thông tin hóa đơn
            String selectedMonth = java.time.LocalDate.now().toString().substring(0, 7); // yyyy-MM
            List<Map<String, Object>> billSummary = billDAO.getBillSummaryByMonth(selectedMonth);
            Map<String, Object> billData = null;
            for (Map<String, Object> row : billSummary) {
                if ((int) row.get("BillID") == billId) {
                    billData = row;
                    break;
                }
            }

            if (billData == null) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "❌ Không tìm thấy hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
                return;
            }

            // Tính tổng số tiền đã thanh toán
            List<Payment> payments = paymentDAO.getPaymentsByBillId(billId);
            BigDecimal totalPaid = payments.stream()
                    .map(Payment::getAmountPaid)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalAmount = (BigDecimal) billData.get("TotalAmount");
            BigDecimal amountRemaining = totalAmount.subtract(totalPaid);

            // Tạo đối tượng Bill
            Bill bill = new Bill();
            bill.setBillID(billId);
            bill.setTotalAmount(totalAmount);

            request.setAttribute("bill", bill);
            request.setAttribute("totalPaid", totalPaid);
            request.setAttribute("amountRemaining", amountRemaining);
            request.getRequestDispatcher("/admin/payment.jsp").forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "❌ Không thể load lại form: " + ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
        }
    }
}
