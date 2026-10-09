package com.ecommerce.controller;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    private User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("loggedInUser");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        User user = getUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");

        if (!"checkout".equals(action)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        HttpSession session = request.getSession();

        Map<Integer, Integer> cart =
            (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        boolean success =
            orderDAO.placeCartOrder(user.getUserId(), cart);

        if (success) {

            cart.clear();

            response.sendRedirect(
                request.getContextPath() + "/orders?action=history&success=1"
            );

        } else {

            response.sendRedirect(
                request.getContextPath() + "/cart?error=checkout"
            );
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        User user = getUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("history".equals(action)) {

            if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            request.setAttribute(
                "orders",
                orderDAO.getOrdersByCustomer(user.getUserId())
            );

            request.getRequestDispatcher(
                "/WEB-INF/views/customer-orders.jsp"
            ).forward(request, response);

        } else if ("owner".equals(action)) {

            if (!"OWNER".equalsIgnoreCase(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            request.setAttribute("orders", orderDAO.getAllOrders());

            request.getRequestDispatcher(
                "/WEB-INF/views/owner-orders.jsp"
            ).forward(request, response);

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}