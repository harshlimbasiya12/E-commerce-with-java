<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.ecommerce.model.Product" %>

<%
    Map<Integer, Integer> cart =
        (Map<Integer, Integer>) request.getAttribute("cart");

    Map<Integer, Product> cartProducts =
        (Map<Integer, Product>) request.getAttribute("cartProducts");

    double grandTotal = 0;
%>

<!DOCTYPE html>
<html>
<head>
    <title>Shopping Cart</title>
</head>
<body>

    <h2>Your Shopping Cart</h2>

    <a href="${pageContext.request.contextPath}/products?action=list">
        Continue Shopping
    </a>

    <hr>

    <% if (cart == null || cart.isEmpty()) { %>

        <p>Your cart is empty.</p>

    <% } else { %>

        <% for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {

            Product product = cartProducts.get(entry.getKey());

            if (product == null) {
                continue;
            }

            int quantity = entry.getValue();
            double lineTotal = product.getPrice() * quantity;
            grandTotal += lineTotal;
        %>

            <div>
                <h3><%= product.getTitle() %></h3>

                <p>Price: ₹<%= product.getPrice() %></p>
                <p>Quantity: <%= quantity %></p>
                <p>Subtotal: ₹<%= lineTotal %></p>

                <form action="${pageContext.request.contextPath}/cart"
                      method="post">

                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="productId"
                           value="<%= product.getProductId() %>">

                    <input type="number" name="quantity"
                           value="<%= quantity %>" min="1" required>

                    <button type="submit">Update</button>
                </form>

                <form action="${pageContext.request.contextPath}/cart"
                      method="post">

                    <input type="hidden" name="action" value="remove">
                    <input type="hidden" name="productId"
                           value="<%= product.getProductId() %>">

                    <button type="submit">Remove</button>
                </form>
            </div>

            <hr>

        <% } %>

        <h3>Grand Total: ₹<%= grandTotal %></h3>

        <form action="${pageContext.request.contextPath}/orders"
              method="post">
            <input type="hidden" name="action" value="checkout">
            <button type="submit">Place Order</button>
        </form>

    <% } %>

</body>
</html>