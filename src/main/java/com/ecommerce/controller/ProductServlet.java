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
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    private User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("loggedInUser");
    }

    private boolean isOwner(User user) {
        return user != null
                && "OWNER".equalsIgnoreCase(user.getRole());
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

        if (action == null || action.equals("list")) {

            List<Product> products = productDAO.getAllProducts();

            request.setAttribute("products", products);
            request.getRequestDispatcher("/WEB-INF/views/catalog.jsp")
                   .forward(request, response);

        } else if (action.equals("detail")) {

            int id = Integer.parseInt(request.getParameter("id"));
            Product product = productDAO.getProductById(id);

            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("product", product);
            request.getRequestDispatcher(
                "/WEB-INF/views/product-detail.jsp"
            ).forward(request, response);

        } else if (action.equals("owner")) {

            if (!isOwner(user)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            List<Product> products =
                    productDAO.getProductsByOwner(user.getUserId());

            request.setAttribute("products", products);
            request.getRequestDispatcher(
                "/WEB-INF/views/owner-dashboard.jsp"
            ).forward(request, response);

        } else if (action.equals("new")) {

            if (!isOwner(user)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            request.getRequestDispatcher(
                "/WEB-INF/views/product-form.jsp"
            ).forward(request, response);

        } else if (action.equals("edit")) {

            if (!isOwner(user)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            int id = Integer.parseInt(request.getParameter("id"));
            Product product = productDAO.getProductById(id);

            if (product == null || product.getOwnerId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("product", product);
            request.getRequestDispatcher(
                "/WEB-INF/views/product-form.jsp"
            ).forward(request, response);

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        User user = getUser(request);

        if (!isOwner(user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {

                Product product = readProduct(request);
                product.setOwnerId(user.getUserId());

                boolean success = productDAO.addProduct(product);

                if (!success) {
                    request.setAttribute("error", "Could not add product.");
                    request.getRequestDispatcher(
                        "/WEB-INF/views/product-form.jsp"
                    ).forward(request, response);
                    return;
                }

            } else if ("update".equals(action)) {

                int id = Integer.parseInt(request.getParameter("productId"));

                Product existing = productDAO.getProductById(id);

                if (existing == null
                        || existing.getOwnerId() != user.getUserId()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                Product product = readProduct(request);
                product.setProductId(id);
                product.setOwnerId(user.getUserId());

                if (!productDAO.updateProduct(product)) {
                    request.setAttribute("error", "Could not update product.");
                    request.setAttribute("product", product);
                    request.getRequestDispatcher(
                        "/WEB-INF/views/product-form.jsp"
                    ).forward(request, response);
                    return;
                }

            } else if ("delete".equals(action)) {

                int id = Integer.parseInt(request.getParameter("productId"));

                // DAO must scope deletion by BOTH product ID and owner ID.
                productDAO.deleteProduct(id, user.getUserId());

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            response.sendRedirect(
                request.getContextPath() + "/products?action=owner"
            );

        } catch (NumberFormatException ex) {
            request.setAttribute("error", "Please enter valid product details.");
            request.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", "Please enter valid product details.");
            request.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(request, response);
        }
    }

    private Product readProduct(HttpServletRequest request) {
        String title = request.getParameter("title");
        String priceText = request.getParameter("price");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");

        if (title == null || title.trim().isEmpty()
                || priceText == null || priceText.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing product details");
        }

        BigDecimal price = new BigDecimal(priceText);

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        Product product = new Product();
        product.setTitle(title.trim());
        product.setPrice(price.doubleValue());
        product.setDescription(description);
        product.setImageUrl(imageUrl);

        return product;
    }
}