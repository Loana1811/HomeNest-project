package controller;

import dao.BillDAO;
import dao.PaymentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Bill;
import model.Payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/admin/payment"})
public class PaymentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String billIdRaw = request.getParameter("billId");

        if (billIdRaw == null || billIdRaw.trim().isEmpty()) {
            // ✅ Quay về trang list nếu thiếu billId (hoặc có thể set thông báo lỗi)
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

        if ("collect".equals(action)) {
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
                paymentDAO.insertPayment(payment);

                // Tính tổng đã thanh toán
                List<Payment> payments = paymentDAO.getPaymentsByBillId(billId);
                BigDecimal totalPaid = BigDecimal.ZERO;
                for (Payment p : payments) {
                    if (p.getAmountPaid() != null) {
                        totalPaid = totalPaid.add(p.getAmountPaid());
                    }
                }

                // Lấy tổng tiền hóa đơn (không trừ cọc)
                BillDAO billDAO = new BillDAO();
                Bill bill = billDAO.getBillById(billId);
              BigDecimal totalAmount = bill.getTotalAmount();


                String status;
             // Sau khi update status
if (totalPaid.compareTo(totalAmount) >= 0) {
    billDAO.updateBillStatus(billId, "PAID");
} else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
    billDAO.updateBillStatus(billId, "PARTIAL");
}

// 🔁 Reload lại bill để phản ánh đúng trạng thái mới
bill = billDAO.getBillById(billId);



                // Tính còn nợ
                BigDecimal remaining = totalAmount.subtract(totalPaid);
                String msg;
                if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                    msg = "💰 Đã thu được " + totalPaid + "đ. Còn nợ: " + remaining + "đ.";
                } else {
                    msg = "✅ Khách đã thanh toán đầy đủ.";
                }

// ✅ Redirect chứ KHÔNG dùng forwardBack nữa
                HttpSession session = request.getSession();
                session.setAttribute("success", msg);
                response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "❌ Lỗi kỹ thuật: " + e.getMessage());
                forwardBack(request, response, billIdRaw);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hành động không hợp lệ");
        }
    }

    private void forwardBack(HttpServletRequest request, HttpServletResponse response, Object billIdRaw)
            throws ServletException, IOException {
        try {
            if (billIdRaw == null) {
                // ✅ Thay vì báo lỗi 400, ta quay về trang danh sách hóa đơn
                HttpSession session = request.getSession();
                session.setAttribute("error", "⚠️ Không xác định được hóa đơn cần hiển thị.");
                response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
                return;
            }

            int billId;
            if (billIdRaw instanceof Integer) {
                billId = (Integer) billIdRaw;
            } else {
                String billIdStr = String.valueOf(billIdRaw).trim();
                if (billIdStr.isEmpty()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("error", "⚠️ Mã hóa đơn không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
                    return;
                }
                billId = Integer.parseInt(billIdStr);
            }

            BillDAO billDAO = new BillDAO();
            PaymentDAO paymentDAO = new PaymentDAO();

            Bill bill = billDAO.getBillById(billId);
            if (bill == null) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "❌ Không tìm thấy hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
                return;
            }

            List<Payment> payments = paymentDAO.getPaymentsByBillId(billId);
            BigDecimal totalPaid = BigDecimal.ZERO;
            for (Payment p : payments) {
                if (p.getAmountPaid() != null) {
                    totalPaid = totalPaid.add(p.getAmountPaid());
                }
            }
BigDecimal amountRemaining = bill.getTotalAmount().subtract(totalPaid);

            request.setAttribute("bill", bill);
            request.setAttribute("totalPaid", totalPaid);
            request.setAttribute("amountRemaining", amountRemaining);
            request.getRequestDispatcher("/admin/payment.jsp").forward(request, response);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "⚠️ billId không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
        } catch (Exception ex) {
            ex.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "❌ Không thể load lại form: " + ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/bill?action=list");
        }
    }

}
