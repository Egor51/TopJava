<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 01.06.2023
  Time: 22:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<html>
<head>
    <title>Meals</title>
    <style>
        table {
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid black;
            padding: 10px;
        }

        .excess-cell {
            color: red;
        }

        .normal-cell {
            color: green;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<h3><a href="addmeal.html">Add Meals</a></h3>
<table>
    <tr>
        <th>DateTime</th>
        <th>Description</th>
        <th>Calories</th>
        <th>Edit</th>
        <th>Delete</th>
    </tr>
    <jsp:useBean id="meals" scope="request" type="java.util.List"/>
    <c:forEach var="meal" items="${meals}">
        <tr>
            <c:set var="cellClass" value="${meal.excess ? 'excess-cell' : 'normal-cell'}"/>
            <td class="${cellClass}"><javatime:format value="${meal.dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td class="${cellClass}">${meal.description}</td>
            <td class="${cellClass}">${meal.calories}</td>
            <td class="${cellClass}"><a href="editmeal.html?meal=${meal.id}">Edit</a></td>
            <td class="${cellClass}"><a href="#" class="delete-link" data-id="${meal.id}">Delete</a></td>
        </tr>
    </c:forEach>
</table>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        var deleteLinks = document.querySelectorAll('.delete-link');
        console.log(deleteLinks);
        deleteLinks.forEach(function (link) {
            link.addEventListener('click', function (e) {
                e.preventDefault();
                var id = e.target.getAttribute('data-id');
                console.log(id);
                fetch('meals?action=delete&id=' + id, {
                    method: 'DELETE',
                }).then(function (response) {
                    console.log(response);
                    if (response.status === 200) {
                        location.reload();
                    }
                }).catch(function (error) {
                    console.error('Error:', error);
                });
            });
        });
    });
</script>
</body>
</html>
