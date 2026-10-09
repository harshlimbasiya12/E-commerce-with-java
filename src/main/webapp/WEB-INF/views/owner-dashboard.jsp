
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
%>

<%
    List<Product> products =
        (List<Product>) request.getAttribute("products");

    int totalProducts = products == null ? 0 : products.size();
    String ownerName = (String) session.getAttribute("ownerName");

    if (ownerName == null) {
        ownerName = "Store Owner";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Owner Dashboard | ShopEase</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {
            background: #f5f6fa;
            color: #222;
        }

        .layout {
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 245px;
            background: #171c2c;
            color: white;
            padding: 28px 18px;
            position: fixed;
            height: 100vh;
        }

        .brand {
            font-size: 25px;
            font-weight: bold;
            margin-bottom: 45px;
        }

        .brand span {
            color: #65a7ff;
        }

        .nav a {
            display: block;
            padding: 14px;
            color: #c8cfdd;
            text-decoration: none;
            border-radius: 8px;
            margin-bottom: 10px;
        }

        .nav a:hover, .nav a.active {
            background: #2b3650;
            color: white;
        }

        .main {
            margin-left: 245px;
            width: calc(100% - 245px);
            padding: 30px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            flex-wrap: wrap;
            gap: 15px;
        }

        .topbar h1 {
            font-size: 27px;
        }

        .user {
            background: white;
            padding: 12px 18px;
            border-radius: 10px;
            box-shadow: 0 2px 10px #00000008;
        }

        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            padding: 23px;
            border-radius: 12px;
            box-shadow: 0 3px 15px #00000008;
        }

        .stat-card p {
            color: #777;
            margin-bottom: 12px;
        }

        .stat-card h2 {
            font-size: 29px;
        }

        .section {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 3px 15px #00000008;
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            flex-wrap: wrap;
            gap: 12px;
        }

        .btn {
            border: none;
            padding: 11px 17px;
            border-radius: 7px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
        }

        .primary {
            background: #286de0;
            color: white;
        }

        .edit {
            background: #eaf1ff;
            color: #286de0;
        }

        .delete {
            background: #ffe9e9;
            color: #d52b2b;
        }

        .table-wrapper {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 600px;
        }

        th, td {
            text-align: left;
            padding: 16px 12px;
            border-bottom: 1px solid #eee;
        }

        th {
            color: #777;
            font-size: 13px;
            background: #fafafa;
        }

        td {
            font-size: 14px;
        }

        .empty {
            text-align: center;
            padding: 40px;
            color: #777;
        }

        .actions {
            display: flex;
            gap: 8px;
        }

        .actions form {
            display: inline;
        }

        @media(max-width: 750px) {
            .sidebar {
                width: 65px;
                padding: 20px 8px;
            }

            .brand {
                font-size: 0;
                margin-bottom: 30px;
            }

            .brand span {
                font-size: 23px;
            }

            .nav a {
                font-size: 0;
                text-align: center;
            }

            .main {
                margin-left: 65px;
                width: calc(100% - 65px);
                padding: 18px;
            }
        }
    </style>
</head>

<body>

<div class="layout">

    <aside class="sidebar">
        <div class="brand">Shop<span>Ease</span></div>

        <nav class="nav">
            <a class="active" href="<%= request.getContextPath() %>/products?action=owner">
                ◈ Dashboard
            </a>

            <a href="<%= request.getContextPath() %>/products?action=new">
                ＋ Add Product
            </a>

            <a href="<%= request.getContextPath() %>/orders?action=owner">
                ▤ Orders
            </a>

            <a href="<%= request.getContextPath() %>/logout">
                ⇥ Logout
            </a>
        </nav>
    </aside>

    <main class="main">

        <div class="topbar">
            <div>
                <h1>Dashboard</h1>
                <p style="color:#777;margin-top:7px;">
                    Manage your store and products.
                </p>
            </div>

            <div class="user">
                Welcome, <strong><%= esc(ownerName) %></strong>
            </div>
        </div>

        <div class="stats">

            <div class="stat-card">
                <p>Total Products</p>
                <h2><%= totalProducts %></h2>
            </div>

            <div class="stat-card">
                <p>Store Status</p>
                <h2 style="font-size:22px;color:#16a36a;">
                    Active
                </h2>
            </div>

        </div>

        <section class="section">

            <div class="section-header">
                <div>
                    <h2>My Products</h2>
                    <p style="color:#888;margin-top:6px;">
                        Manage your store inventory.
                    </p>
                </div>

                <a class="btn primary"
                   href="<%= request.getContextPath() %>/products?action=new">
                    + Add Product
                </a>
            </div>

            <div class="table-wrapper">

                <table>
                    <thead>
                        <tr>
                            <th>Product ID</th>
                            <th>Product Name</th>
                            <th>Price</th>
                            <th>Actions</th>
                        </tr>
                    </thead>

                    <tbody>

                    <% if (products == null || products.isEmpty()) { %>

                        <tr>
                            <td colspan="4" class="empty">
                                No products available. Add your first product!
                            </td>
                        </tr>

                    <% } else {
                        for (Product p : products) { %>

                        <tr>
                            <td>#<%= p.getProductId() %></td>

                            <td>
                                <strong><%= esc(p.getTitle()) %></strong>
                            </td>

                            <td>
                                ₹<%= p.getPrice() %>
                            </td>

                            <td>
                                <div class="actions">

                                    <a class="btn edit"
                                       href="<%= request.getContextPath() %>/products?action=edit&id=<%= p.getProductId() %>">
                                        Edit
                                    </a>

                                    <form action="<%= request.getContextPath() %>/products"
                                          method="post"
                                          onsubmit="return confirm('Are you sure you want to delete this product?');">

                                        <input type="hidden"
                                               name="action"
                                               value="delete">

                                        <input type="hidden"
                                               name="productId"
                                               value="<%= p.getProductId() %>">

                                        <button class="btn delete"
                                                type="submit">
                                            Delete
                                        </button>

                                    </form>

                                </div>
                            </td>
                        </tr>

                    <%  }
                       } %>

                    </tbody>
                </table>

            </div>

        </section>

    </main>

</div>

</body>
</html>