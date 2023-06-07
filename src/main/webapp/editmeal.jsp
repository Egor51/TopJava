create <%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create</title>
</head>
<body>
<h2><a href="index.html">Home</a></h2>
<hr>
<h2>${meal.id != null ? 'Edit Meal' : 'Add Meal'}</h2>
<form id="mealForm" method="post" action="meals">
    <label for="dateTime">DateTime:</label><br>
    <input type="datetime-local" id="dateTime" name="dateTime" value="${meal.dateTime}" /><br>
    <label for="description">Description:</label><br>
    <input type="text" id="description" name="description" value="${meal.description}"><br>
    <label for="calories">Calories:</label><br>
    <input type="number" id="calories" name="calories" value="${meal.calories}"><br>
    <input type="hidden" id="mealId" name="id" value="${meal.id}">
    <input type="submit" value="${meal.id != null ? 'Update' : 'Create'}"></form>
<button onclick="goBack()">Back</button>
<script>
    function goBack() {
        window.history.back();
    }
</script>
</body>
</html>
