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
<h2>
    <c:choose>
        <c:when test="${meal.id != null}">Edit Meal</c:when>
        <c:otherwise>Add Meal</c:otherwise>
    </c:choose>
</h2>
<form id="mealForm" method="post" action="meals">
    <label for="dateTime">DateTime:</label><br>
    <input type="datetime-local" id="dateTime" name="dateTime" value="${meal.dateTime}" /><br>
    <label for="description">Description:</label><br>
    <input type="text" id="description" name="description" value="${meal.description}"><br>
    <label for="calories">Calories:</label><br>
    <input type="number" id="calories" name="calories" value="${meal.calories}"><br>
    <input type="hidden" id="mealId" name="id" value="${meal.id}">
    <input type="submit" value="<c:choose><c:when test='${meal.id != null}'>Update</c:when><c:otherwise>Create</c:otherwise></c:choose>">
</form>
<button onclick="goBack()">Back</button>
<script>
    function goBack() {
        window.history.back();
    }
</script>
</body>
</html>
