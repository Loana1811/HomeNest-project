/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Test;

import controller.AdminAccountServlet;
import dao.CustomerDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit tests for AdminAccountServlet#editAccount
 */
/**
 *
 * @author Admin
 */
public class EditAccountTest {

    private AdminAccountServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // use a spy so we can stub sendStatusChangeEmail (avoid real email)
        servlet = spy(new AdminAccountServlet());

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        customerDAO = mock(CustomerDAO.class);

        // inject DAO by reflection
        Field customerField = AdminAccountServlet.class.getDeclaredField("customerDAO");
        customerField.setAccessible(true);
        customerField.set(servlet, customerDAO);

        // stub common header
        when(request.getContextPath()).thenReturn("");

        // ignore real email sending
        doNothing().when(servlet).sendStatusChangeEmail(anyString(), anyString(), anyString(), anyString());
    }

    /* =================================
       1. SUCCESS PATH – update customer
       ================================= */
    @Test
    void editAccount_CustomerSuccess() throws Exception {
        // input params
        when(request.getParameter("customerID")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john.doe@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0987654321");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("reason")).thenReturn("Reactivation");
        when(request.getParameter("cccd")).thenReturn("123456789012");

        // existing customer data
        Customer c = new Customer();
        c.setCustomerID(1);
        c.setEmail("old@example.com");
        c.setPhoneNumber("0123456789");
        c.setCustomerStatus("Inactive");
        when(customerDAO.getCustomerById(1)).thenReturn(c);

        // validations
        when(customerDAO.isEmailExists("john.doe@example.com")).thenReturn(false);
        when(customerDAO.isPhoneNumberExists("0987654321")).thenReturn(false);
        when(customerDAO.isCCCDExists("123456789012")).thenReturn(false);
        when(customerDAO.updateCustomer(any(Customer.class))).thenReturn(true);

        servlet.editAccount(request, response);

        verify(response).sendRedirect(anyString());
    }

    /* =============================================
       2. FAILURE – thiếu cả userID & customerID
       ============================================= */
    @Test
    void editAccount_Fail_MissingIDs() throws Exception {
        when(request.getParameter("customerID")).thenReturn(null);
        when(request.getParameter("userID")).thenReturn(null);
        when(request.getRequestDispatcher("/admin/editCustomer.jsp")).thenReturn(dispatcher);

        servlet.editAccount(request, response);

        verify(request).setAttribute(eq("error"), contains("User ID or Customer ID is required"));
        verify(dispatcher).forward(request, response);
    }

    /* =============================================
       3. FAILURE – Email đã tồn tại
       ============================================= */
    @Test
    void editAccount_Fail_EmailExists() throws Exception {
        when(request.getParameter("customerID")).thenReturn("2");
        when(request.getParameter("fullName")).thenReturn("Jane Doe");
        when(request.getParameter("email")).thenReturn("existing@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0987654321");
        when(request.getParameter("status")).thenReturn("Active");

        Customer c = new Customer();
        c.setCustomerID(2);
        c.setEmail("other@example.com");
        c.setPhoneNumber("0123456789");
        c.setCustomerStatus("Active");
        when(customerDAO.getCustomerById(2)).thenReturn(c);

        when(customerDAO.isEmailExists("existing@example.com")).thenReturn(true);
        when(request.getRequestDispatcher("/admin/editCustomer.jsp")).thenReturn(dispatcher);

        servlet.editAccount(request, response);

        verify(request).setAttribute(eq("error"), contains("Email already exists"));
        verify(dispatcher).forward(request, response);
    }

    /* =============================================
       4. FAILURE – CCCD sai định dạng
       ============================================= */
    @Test
    void editAccount_Fail_InvalidCCCD() throws Exception {
        when(request.getParameter("customerID")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("Nam Tran");
        when(request.getParameter("email")).thenReturn("nam@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0988888888");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("cccd")).thenReturn("abc123"); // sai định dạng

        Customer c = new Customer();
        c.setCustomerID(3);
        c.setEmail("nam@example.com");
        c.setPhoneNumber("0988888888");
        c.setCustomerStatus("Inactive");
        when(customerDAO.getCustomerById(3)).thenReturn(c);
        when(customerDAO.isEmailExists("nam@example.com")).thenReturn(false);
        when(request.getRequestDispatcher("/admin/editCustomer.jsp")).thenReturn(dispatcher);

        servlet.editAccount(request, response);

        verify(request).setAttribute(eq("error"), contains("CCCD must be 12 digits"));
        verify(dispatcher).forward(request, response);
    }

    /* =======================================================
       5. FAILURE – Thay đổi status nhưng không nhập reason
       ======================================================= */
    @Test
    void editAccount_Fail_StatusChangeNoReason() throws Exception {
        when(request.getParameter("customerID")).thenReturn("4");
        when(request.getParameter("fullName")).thenReturn("Mai Lan");
        when(request.getParameter("email")).thenReturn("mai@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0999999999");
        when(request.getParameter("status")).thenReturn("Inactive"); // status thay đổi
        when(request.getParameter("reason")).thenReturn("");        // không lý do

        Customer c = new Customer();
        c.setCustomerID(4);
        c.setEmail("mai@example.com");
        c.setPhoneNumber("0999999999");
        c.setCustomerStatus("Active");
        when(customerDAO.getCustomerById(4)).thenReturn(c);
        when(customerDAO.isEmailExists("mai@example.com")).thenReturn(false);
        when(customerDAO.isPhoneNumberExists("0999999999")).thenReturn(false);
        when(request.getRequestDispatcher("/admin/editCustomer.jsp")).thenReturn(dispatcher);

        servlet.editAccount(request, response);

        verify(request).setAttribute(eq("error"), contains("Reason is required for status change"));
        verify(dispatcher).forward(request, response);
    }
}
