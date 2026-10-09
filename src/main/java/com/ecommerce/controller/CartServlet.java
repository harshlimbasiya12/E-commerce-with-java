package com.ecommerce.controller;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getCart(HttpSession session) {
        Map<Integer, Integer> cart =
            (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        return cart;
    }

    private User getCustomer(HttpServletRequest request,
                             HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        User user = getCustomer(request, response);
        if (user == null) {
            return;
        }

        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = getCart(session);

        // Retrieve current product information from the database.
        Map<Integer, Product> cartProducts = new HashMap<>();

        for (Integer productId : cart.keySet()) {
            Product product = productDAO.getProductById(productId);

            if (product != null) {
                cartProducts.put(productId, product);
            }
        }

        request.setAttribute("cart", cart);
        request.setAttribute("cartProducts", cartProducts);

        request.getRequestDispatcher("/WEB-INF/views/cart.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        User user = getCustomer(request, response);
        if (user == null) {
            return;
        }

        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = getCart(session);

        String action = request.getParameter("action");

        try {
            int productId = Integer.parseInt(
                request.getParameter("productId")
            );

            if ("add".equals(action)) {

                int quantity = Integer.parseInt(
                    request.getParameter("quantity")
                );

                Product product = productDAO.getProductById(productId);

                if (product == null || quantity < 1) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                int oldQuantity = cart.getOrDefault(productId, 0);
                cart.put(productId, oldQuantity + quantity);

            } else if ("update".equals(action)) {

                int quantity = Integer.parseInt(
                    request.getParameter("quantity")
                );

                if (quantity < 1) {
                    cart.remove(productId);
                } else if (productDAO.getProductById(productId) != null) {
                    cart.put(productId, quantity);
                }

            } else if ("remove".equals(action)) {

                cart.remove(productId);

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}