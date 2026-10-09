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
    <title>All Orders</title>
</head>
<body>

    <h2>All Customer Orders</h2>

    <a href="${pageContext.request.contextPath}/products?action=owner">
        Back to Dashboard
    </a>

    <hr>

    <% if (orders == null || orders.isEmpty()) { %>

        <p>No orders have been placed.</p>

    <% } else {
        for (Order order : orders) { %>

            <div>
                <p>Order ID: <%= order.getOrderId() %></p>
                <p>Customer: <%= order.getCustomerName() %></p>
                <p>Product: <%= order.getProductTitle() %></p>
                <p>Quantity: <%= order.getQuantity() %></p>
                <p>Total: ₹<%= order.getTotalPrice() %></p>
                <p>Date: <%= order.getOrderDate() %></p>
            </div>

            <hr>

    <%  }
       } %>

</body>
</html>