package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Bill;
import model.BillFeedback;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/AcceptFeedbackServlet")
public class AcceptFeedbackServlet extends HttpServlet {
    private final BillFeedbackDAO feedbackDAO = new BillFeedbackDAO();
    private final BillDAO billDAO = new BillDAO();
    private final BillHistoryDAO historyDAO = new BillHistoryDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));

        try {
            // Lấy phản hồi
            BillFeedback feedback = feedbackDAO.getFeedbackById(feedbackId);
            if (feedback == null || !"Pending".equalsIgnoreCase(feedback.getStatus())) {
                response.sendRedirect("manage-feedback.jsp?error=notfound");
                return;
            }

            // Lấy bill cũ
            Bill oldBill = billDAO.getBillById(feedback.getBillId());
            if (oldBill == null) {
                response.sendRedirect("manage-feedback.jsp?error=billnotfound");
                return;
            }

            // Hủy bill cũ
            billDAO.updateBillStatus(oldBill.getBillID(), "Cancel");

            // Tạo bill mới giống bill cũ nhưng cập nhật ngày hiện tại
            Bill newBill = new Bill();
            newBill.setContractID(oldBill.getContractID());
            newBill.setBillDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
            newBill.setTotalAmount(oldBill.getTotalAmount());
            newBill.setBillStatus("Unpaid");

            // Dùng createBill để nhận lại ID bill mới
            int newBillId = billDAO.createBill(newBill);
            if (newBillId <= 0) {
                response.sendRedirect("manage-feedback.jsp?error=insertfail");
                return;
            }

            // Ghi lịch sử chỉnh sửa bill
            historyDAO.logBillEdit(oldBill.getBillID(), newBillId, feedback.getReason(), feedback.getUserId());

            // Cập nhật trạng thái phản hồi
            feedbackDAO.updateStatus(feedbackId, "Accepted");

            response.sendRedirect("manage-feedback.jsp?success=1");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("manage-feedback.jsp?error=sql");
        }
    }
}
