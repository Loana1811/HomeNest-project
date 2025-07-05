/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import model.*;

@WebServlet("/admin/close-service")
public class ServiceClosingServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final UtilityReadingDAO readingDAO = new UtilityReadingDAO();
    private final BillDAO billDAO = new BillDAO();
    private final BillDetailDAO billDetailDAO = new BillDetailDAO();
    private final IncurredFeeDAO incurredFeeDAO = new IncurredFeeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String selectedMonth = req.getParameter("month"); // yyyy-MM
        try {
            Map<Block, List<RoomStatusDTO>> blockRoomMap = roomDAO.getRoomStatusByBlock(selectedMonth);
            req.setAttribute("blockRoomMap", blockRoomMap);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Error loading room data: " + e.getMessage());
        }
        req.setAttribute("selectedMonth", selectedMonth);
        req.getRequestDispatcher("/admin/service-closing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String roomIdStr = req.getParameter("roomId");
        String month = req.getParameter("month"); // yyyy-MM

        int roomId = Integer.parseInt(roomIdStr);
        LocalDate billDateLocal = YearMonth.parse(month).atEndOfMonth();
        Date billDate = Date.valueOf(billDateLocal);

        try {
            // Bước 1: Lấy hợp đồng hoạt động
            Contract contract = roomDAO.getActiveContractByRoomId(roomId);
            if (contract == null) {
                req.setAttribute("error", "No active contract for this room.");
                doGet(req, resp);
                return;
            }

            // Bước 2: Kiểm tra trùng hóa đơn
            if (billDAO.existsBillForRoomAndMonth(roomId, month)) {
                req.setAttribute("error", "Bill for this room in " + month + " already exists.");
                doGet(req, resp);
                return;
            }

            // Bước 3: Tính toán các chi phí tiện ích
            List<UtilityReading> readings = readingDAO.getReadingsForRoomAndMonth(roomId, month);

            float electricityCost = 0;
            float waterCost = 0;
            float wifiCost = 0;

            for (int i = 0; i < readings.size(); i++) {
                UtilityReading r = readings.get(i);
                float used = r.getNewReading().subtract(r.getOldReading()).floatValue();
                float cost = used * r.getOldPrice().floatValue();

                int typeId = r.getUtilityTypeID();
                if (typeId == 1) {
                    electricityCost = cost;
                } else if (typeId == 2) {
                    waterCost = cost;
                } else if (typeId == 3) {
                    wifiCost = cost;
                }
            }

            // Bước 4: Tạo Bill
            float roomRent = roomDAO.getRoomPriceByRoomId(roomId);
            float totalAmount = roomRent + electricityCost + waterCost + wifiCost;

            Bill bill = new Bill();
            bill.setContractID(contract.getContractId());
            bill.setBillDate(billDate);
            bill.setTotalAmount(totalAmount);
            bill.setBillStatus("Unpaid");

            int billId = billDAO.createBill(bill);

            // Bước 5: Tạo BillDetail
            BillDetail detail = new BillDetail();
            detail.setBillID(billId);
            detail.setRoomrent(roomRent);
            detail.setElectricityCost(electricityCost);
            detail.setWaterCost(waterCost);
            detail.setWifiCost(wifiCost);
            billDetailDAO.insertBillDetail(detail);

            // Bước 6: Ghi lại các khoản phát sinh khác
            List<IncurredFee> incurredFees = incurredFeeDAO.getDefaultFeesForRoom(roomId);
            for (int i = 0; i < incurredFees.size(); i++) {
                IncurredFee fee = incurredFees.get(i);
                fee.setBillID(billId);
                incurredFeeDAO.insertFee(fee);
            }

            // ✅ Bước 7: Đánh dấu đã chốt (nếu bạn có cột IsClosed thì kích hoạt dòng dưới)
            // roomDAO.markServiceClosed(roomId, month);

            // Quay lại màn hình chính
            resp.sendRedirect(req.getContextPath() + "/admin/close-service?month=" + month);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Error processing: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
