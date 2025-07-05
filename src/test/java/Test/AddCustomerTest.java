/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Test;

import controller.AdminAccountServlet;
import dao.CustomerDAO;
import model.Customer;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AddCustomerTest {
    private AdminAccountServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // create a spy so we can stub email sending
        servlet = spy(new AdminAccountServlet());

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        customerDAO = mock(CustomerDAO.class);

        // inject the mocked DAO
        Field f = AdminAccountServlet.class.getDeclaredField("customerDAO");
        f.setAccessible(true);
        f.set(servlet, customerDAO);

        // stub common
        when(request.getContextPath()).thenReturn("");
        doNothing().when(servlet).sendStatusChangeEmail(anyString(), anyString(), anyString(), anyString());
    }

    /* ------------- 1. SUCCESS ------------- */
    @Test
    void editAccount_CustomerSuccess() throws Exception {
        when(request.getParameter("customerID")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0987654321");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("reason")).thenReturn("Reactivation");
        when(request.getParameter("cccd")).thenReturn("123456789012");

        Customer existing = new Customer();
        existing.setCustomerID(1);
        existing.setCustomerStatus("Inactive");
        when(customerDAO.getCustomerById(1)).thenReturn(existing);
        when(customerDAO.updateCustomer(any(Customer.class))).thenReturn(true);
        when(customerDAO.isEmailExists(anyString())).thenReturn(false);
        when(customerDAO.isPhoneNumberExists(anyString())).thenReturn(false);
        when(customerDAO.isCCCDExists(anyString())).thenReturn(false);

        servlet.editAccount(request, response);
        verify(response).sendRedirect(contains("/admin/account"));
    }

    /* ------------- 2. FAIL – thiếu IDs ------------- */
    @Test
    void editAccount_Fail_MissingIDs() throws Exception {
        when(request.getParameter("customerID")).thenReturn(null);
        when(request.getParameter("userID")).thenReturn(null);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.editAccount(request, response);
        verify(request).setAttribute(eq("error"), contains("User ID or Customer ID is required"));
        verify(dispatcher).forward(request, response);
    }

    /* ------------- 3. FAIL – email trùng ------------- */
    @Test
    void editAccount_Fail_EmailExists() throws Exception {
        when(request.getParameter("customerID")).thenReturn("2");
        when(request.getParameter("fullName")).thenReturn("Jane");
        when(request.getParameter("email")).thenReturn("dup@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0911000000");
        when(request.getParameter("status")).thenReturn("Active");

        Customer c = new Customer();
        c.setCustomerID(2);
        c.setEmail("old@example.com");
        when(customerDAO.getCustomerById(2)).thenReturn(c);
        when(customerDAO.isEmailExists("dup@example.com")).thenReturn(true);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.editAccount(request, response);
        verify(request).setAttribute(eq("error"), contains("Email already exists"));
        verify(dispatcher).forward(request, response);
    }

    /* ------------- 4. FAIL – CCCD sai định dạng ------------- */
    @Test
    void editAccount_Fail_InvalidCCCD() throws Exception {
        when(request.getParameter("customerID")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("Nam");
        when(request.getParameter("email")).thenReturn("nam@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0988888888");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("cccd")).thenReturn("abc123");

        Customer c = new Customer();
        c.setCustomerID(3);
        when(customerDAO.getCustomerById(3)).thenReturn(c);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.editAccount(request, response);
        verify(request).setAttribute(eq("error"), contains("CCCD must be 12 digits"));
        verify(dispatcher).forward(request, response);
    }

    /* ------------- 5. FAIL – đổi status nhưng không reason ------------- */
    @Test
    void editAccount_Fail_StatusChangeNoReason() throws Exception {
        when(request.getParameter("customerID")).thenReturn("4");
        when(request.getParameter("fullName")).thenReturn("Lan");
        when(request.getParameter("email")).thenReturn("lan@example.com");
        when(request.getParameter("phoneNumber")).thenReturn("0999999999");
        when(request.getParameter("status")).thenReturn("Inactive"); // status khác
        when(request.getParameter("reason")).thenReturn("");          // missing

        Customer c = new Customer();
        c.setCustomerID(4);
        c.setCustomerStatus("Active");
        when(customerDAO.getCustomerById(4)).thenReturn(c);
        when(customerDAO.isEmailExists(anyString())).thenReturn(false);
        when(customerDAO.isPhoneNumberExists(anyString())).thenReturn(false);
        when(customerDAO.updateCustomer(any(Customer.class))).thenReturn(true);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.editAccount(request, response);
        verify(request).setAttribute(eq("error"), contains("Reason is required for status change"));
        verify(dispatcher).forward(request, response);
    }
} 
