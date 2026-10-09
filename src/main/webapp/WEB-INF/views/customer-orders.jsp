<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Order" %>

<%
    List<Order> orders =
        (List<Order>) request.getAttribute("orders");
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Orders</title>
</head>
<body>

    <h2>My Orders</h2>

    <a href="${pageContext.request.contextPath}/products?action=list">
        Continue Shopping
    </a>

    <hr>

    <% if (orders == null || orders.isEmpty()) { %>

        <p>You have not placed any orders yet.</p>

    <% } else {
        for (Order order : orders) { %>

            <div>
                <p>Order ID: <%= order.getOrderId() %></p>
                <p>Product ID: <%= order.getProductId() %></p>
                <p>Quantity: <%= order.getQuantity() %></p>
                <p>Total: ₹<%= order.getTotalPrice() %></p>
                <p>Date: <%= order.getOrderDate() %></p>
            </div>

            <hr>

    <%  }
       } %>

</body>
</html>