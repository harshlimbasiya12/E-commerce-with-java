<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.ecommerce.model.Product" %>

<%
    Product product = (Product) request.getAttribute("product");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Product Details</title>
</head>
<body>

    <h2><%= product.getTitle() %></h2>

    <p>Price: ₹<%= product.getPrice() %></p>
    <p><%= product.getDescription() %></p>

    <% if (product.getImageUrl() != null
            && !product.getImageUrl().isEmpty()) { %>
        <img src="<%= product.getImageUrl() %>"
             alt="Product image" width="200">
    <% } %>

    <form action="${pageContext.request.contextPath}/cart"
          method="post">
        <input type="hidden" name="action" value="add">
        <input type="hidden" name="productId"
               value="<%= product.getProductId() %>">
        <input type="number" name="quantity"
               value="1" min="1" required>
        <button type="submit">Add to Cart</button>
    </form>

    <a href="${pageContext.request.contextPath}/products?action=list">
        Back to Catalog
    </a>

</body>
</html>