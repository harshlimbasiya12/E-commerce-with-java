<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.ecommerce.model.Product" %>

<%
    Product product = (Product) request.getAttribute("product");
    boolean editing = product != null;
%>

<!DOCTYPE html>
<html>
<head>
    <title><%= editing ? "Edit Product" : "Add Product" %></title>
</head>
<body>

    <h2><%= editing ? "Edit Product" : "Add Product" %></h2>

    <p style="color:red;">${error}</p>

    <form action="${pageContext.request.contextPath}/products"
          method="post">

        <input type="hidden" name="action"
               value="<%= editing ? "update" : "add" %>">

        <% if (editing) { %>
            <input type="hidden" name="productId"
                   value="<%= product.getProductId() %>">
        <% } %>

        <label>Title</label>
        <input type="text" name="title" required
               value="<%= editing ? product.getTitle() : "" %>">

        <br><br>

        <label>Price</label>
        <input type="number" name="price" min="0.01"
               step="0.01" required
               value="<%= editing ? product.getPrice() : "" %>">

        <br><br>

        <label>Description</label>
        <textarea name="description"><%= editing ? product.getDescription() : "" %></textarea>

        <br><br>

        <label>Image URL</label>
        <input type="url" name="imageUrl"
               value="<%= editing ? product.getImageUrl() : "" %>">

        <br><br>

        <button type="submit">Save Product</button>
    </form>

</body>
</html>