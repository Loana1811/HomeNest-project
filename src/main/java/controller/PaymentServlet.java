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
                BigDecimal totalAmount = BigDecimal.valueOf(bill.getTotalAmount());

                if (totalPaid.compareTo(totalAmount) >= 0) {
                    billDAO.updateBillStatus(billId, "PAID");
                }

                // Tính còn nợ
                BigDecimal remaining = totalAmount.subtract(totalPaid);
                String msg;
                if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                    msg = "💰 Đã thu được " + totalPaid + "đ. Còn nợ: " + remaining + "đ.";
                } else {
                    msg = "✅ Khách đã thanh toán đầy đủ.";
                }

                request.setAttribute("success", msg);
                forwardBack(request, response, billId);

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
            int billId;
            if (billIdRaw == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu billId khi reload");
                return;
            }

            if (billIdRaw instanceof Integer) {
                billId = (Integer) billIdRaw;
            } else {
                String billIdStr = String.valueOf(billIdRaw).trim();
                if (billIdStr.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "billId rỗng");
                    return;
                }
                billId = Integer.parseInt(billIdStr);
            }

            BillDAO billDAO = new BillDAO();
            PaymentDAO paymentDAO = new PaymentDAO();

            Bill bill = billDAO.getBillById(billId);
            if (bill == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn");
                return;
            }

            List<Payment> payments = paymentDAO.getPaymentsByBillId(billId);
            BigDecimal totalPaid = BigDecimal.ZERO;
            for (Payment p : payments) {
                if (p.getAmountPaid() != null) {
                    totalPaid = totalPaid.add(p.getAmountPaid());
                }
            }

            request.setAttribute("bill", bill);
            request.setAttribute("totalPaid", totalPaid);
            request.getRequestDispatcher("/admin/payment.jsp").forward(request, response);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "billId không hợp lệ");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể load lại form: " + ex.getMessage());
        }
    }
}
