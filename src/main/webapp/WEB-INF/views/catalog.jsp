
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Product" %>

<%!
    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    private boolean safeImageUrl(String url) {
        if (url == null) return false;
        return url.startsWith("https://") ||
               url.startsWith("http://");
    }
%>

<%
    List<Product> products =
        (List<Product>) request.getAttribute("products");

    com.ecommerce.model.User loggedInUser =
        (com.ecommerce.model.User) session.getAttribute("loggedInUser");

    String customerName = loggedInUser != null
        ? loggedInUser.getName()
        : "Customer";
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>ShopEase | Online Shopping</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {
            background: #f7f8fc;
            color: #222;
        }

        header {
            background: white;
            padding: 18px 7%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 12px #00000008;
            position: sticky;
            top: 0;
            z-index: 10;
            gap: 20px;
            flex-wrap: wrap;
        }

        .logo {
            font-size: 27px;
            font-weight: bold;
            color: #222;
            text-decoration: none;
        }

        .logo span {
            color: #2875e8;
        }

        .nav {
            display: flex;
            align-items: center;
            gap: 25px;
        }

        .nav a {
            color: #444;
            text-decoration: none;
            font-size: 14px;
        }

        .nav a:hover {
            color: #2875e8;
        }

        .welcome {
            color: #666;
            font-size: 14px;
        }

        .hero {
            margin: 35px 7%;
            background: linear-gradient(120deg, #1d4ed8, #60a5fa);
            padding: 65px 45px;
            border-radius: 18px;
            color: white;
        }

        .hero h1 {
            font-size: 38px;
            margin-bottom: 15px;
        }

        .hero p {
            font-size: 16px;
            line-height: 1.6;
            max-width: 550px;
        }

        .products-section {
            padding: 20px 7% 60px;
        }

        .section-title {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
        }

        .section-title h2 {
            font-size: 27px;
        }

        .section-title p {
            color: #888;
            font-size: 14px;
        }

        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(225px, 1fr));
            gap: 25px;
        }

        .product-card {
            background: white;
            border-radius: 13px;
            overflow: hidden;
            box-shadow: 0 3px 15px #00000008;
            transition: transform .2s;
        }

        .product-card:hover {
            transform: translateY(-5px);
        }

        .product-image {
            height: 190px;
            background: #eef2f8;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
        }

        .product-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .placeholder {
            color: #9aa5b5;
            font-size: 48px;
        }

        .product-info {
            padding: 18px;
        }

        .product-info h3 {
            font-size: 17px;
            margin-bottom: 10px;
        }

        .description {
            color: #888;
            font-size: 13px;
            line-height: 1.5;
            min-height: 39px;
        }

        .price {
            font-size: 21px;
            font-weight: bold;
            color: #222;
            margin: 15px 0;
        }

        .product-actions {
            display: flex;
            gap: 8px;
        }

        .btn {
            padding: 11px 12px;
            border-radius: 7px;
            border: none;
            cursor: pointer;
            font-size: 13px;
            text-decoration: none;
            text-align: center;
        }

        .primary {
            background: #2875e8;
            color: white;
            flex: 1;
        }

        .secondary {
            background: #edf3ff;
            color: #2875e8;
        }

        .empty {
            grid-column: 1 / -1;
            background: white;
            text-align: center;
            padding: 60px 20px;
            border-radius: 12px;
            color: #777;
        }

        footer {
            background: #171c2c;
            color: #ccc;
            text-align: center;
            padding: 25px;
            font-size: 13px;
        }

        @media(max-width: 600px) {
            header {
                padding: 15px 5%;
            }

            .nav {
                gap: 13px;
                flex-wrap: wrap;
            }

            .hero {
                margin: 20px 5%;
                padding: 40px 25px;
            }

            .hero h1 {
                font-size: 29px;
            }

            .products-section {
                padding: 20px 5% 40px;
            }
        }
    </style>
</head>

<body>

<header>

    <a class="logo" href="<%= request.getContextPath() %>/products?action=list">
        Shop<span>Ease</span>
    </a>

    <nav class="nav">

        <a href="<%= request.getContextPath() %>/products?action=list">
            Home
        </a>

        <a href="<%= request.getContextPath() %>/cart">
            🛒 My Cart
        </a>

        <a href="<%= request.getContextPath() %>/orders?action=history">
            My Orders
        </a>

        <a href="<%= request.getContextPath() %>/logout">
            Logout
        </a>

    </nav>

    <div class="welcome">
        Hello, <strong><%= esc(customerName) %></strong>
    </div>

</header>

<section class="hero">

    <h1>Discover Products You'll Love.</h1>

    <p>
        Explore our collection of products at great prices.
        Find what you need and enjoy a simple shopping experience.
    </p>

</section>

<section class="products-section">

    <div class="section-title">
        <div>
            <h2>Explore Products</h2>
            <p style="margin-top:8px;">
                Find something perfect for you.
            </p>
        </div>

        <p>
            <%= products == null ? 0 : products.size() %> Products
        </p>
    </div>

    <div class="product-grid">

        <% if (products == null || products.isEmpty()) { %>

            <div class="empty">
                <h2>No products available</h2>
                <p style="margin-top:10px;">
                    Please check back later.
                </p>
            </div>

        <% } else {
            for (Product p : products) { %>

            <div class="product-card">

                <div class="product-image">

                    <% if (safeImageUrl(p.getImageUrl())) { %>

                        <img src="<%= esc(p.getImageUrl()) %>"
                             alt="<%= esc(p.getTitle()) %>"
                             loading="lazy">

                    <% } else { %>

                        <div class="placeholder">▧</div>

                    <% } %>

                </div>

                <div class="product-info">

                    <h3><%= esc(p.getTitle()) %></h3>

                    <p class="description">
                        <%= esc(p.getDescription()) %>
                    </p>

                    <div class="price">
                        ₹<%= p.getPrice() %>
                    </div>

                    <div class="product-actions">

                        <a class="btn secondary"
                           href="<%= request.getContextPath() %>/products?action=detail&id=<%= p.getProductId() %>">
                            Details
                        </a>

                        <form action="<%= request.getContextPath() %>/cart"
                              method="post"
                              style="flex:1;">

                            <input type="hidden"
                                   name="action"
                                   value="add">

                            <input type="hidden"
                                   name="productId"
                                   value="<%= p.getProductId() %>">

                            <input type="hidden"
                                   name="quantity"
                                   value="1">

                            <button class="btn primary"
                                    type="submit"
                                    style="width:100%;">
                                Add to Cart
                            </button>

                        </form>

                    </div>

                </div>

            </div>

        <%  }
           } %>

    </div>

</section>

<footer>
    © 2026 ShopEase. All rights reserved.
</footer>

</body>
</html>