/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.StatisticalDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 *
 * @author Admin
 */
@WebServlet("/admin/statistical")
public class StatisticalServlet extends HttpServlet {

    private StatisticalDAO statisticalDAO = new StatisticalDAO();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet StatisticalServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StatisticalServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String type = request.getParameter("type");
            String valueParam = request.getParameter("value");
            String yearParam = request.getParameter("year");

            if (type == null || yearParam == null || (type.equals("month") || type.equals("quarter")) && valueParam == null) {
                // Nếu thiếu tham số, load mặc định
                type = "month";
                valueParam = String.valueOf(java.time.LocalDate.now().getMonthValue());
                yearParam = String.valueOf(java.time.LocalDate.now().getYear());
            }

            int value = Integer.parseInt(valueParam);
            int year = Integer.parseInt(yearParam);

            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;

            switch (type) {
                case "month":
                    totalIncome = statisticalDAO.getIncomeByMonth(value, year);
                    totalExpense = statisticalDAO.getExpenseByMonth(value, year);
                    break;
                case "quarter":
                    totalIncome = statisticalDAO.getIncomeByQuarter(value, year);
                    totalExpense = statisticalDAO.getExpenseByQuarter(value, year);
                    break;
                case "year":
                    totalIncome = statisticalDAO.getIncomeByYear(year);
                    totalExpense = statisticalDAO.getExpenseByYear(year);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type: " + type);
            }

            BigDecimal profit = totalIncome.subtract(totalExpense);

            request.setAttribute("type", type);
            request.setAttribute("value", value);
            request.setAttribute("year", year);
            request.setAttribute("totalIncome", totalIncome);
            request.setAttribute("totalExpense", totalExpense);
            request.setAttribute("profit", profit);

            request.getRequestDispatcher("/admin/statistical.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal error: " + e);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
