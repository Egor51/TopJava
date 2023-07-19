<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>Meals</title>
    <c:url value="/resources/css/style.css" var="mainCssUrl" />
    <link rel="stylesheet" href="${mainCssUrl}" />
    <jsp:include page="/WEB-INF/jsp/fragments/headTag.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/bodyHeader.jsp" />

<section>
    <h3><a href="${pageContext.request.contextPath}"><spring:message code="app.home"></spring:message></a></h3>
    <hr/>
    <h2>Meals</h2>
    <form method="get" action="${pageContext.request.contextPath}/meals/filter">
        <input type="hidden" name="action" value="filter">
        <dl>
            <dt><spring:message code="date.from.inclusive"/></dt>
            <dd><input type="date" name="startDate" value="${param.startDate}"></dd>
        </dl>
        <dl>
            <dt><spring:message code="date.to.inclusive"/></dt>
            <dd><input type="date" name="endDate" value="${param.endDate}"></dd>
        </dl>
        <dl>
            <dt><spring:message code="time.from.inclusive"/></dt>
            <dd><input type="time" name="startTime" value="${param.startTime}"></dd>
        </dl>
        <dl>
            <dt><spring:message code="time.to.exclusive"/></dt>
            <dd><input type="time" name="endTime" value="${param.endTime}"></dd>
        </dl>
        <button type="submit"><spring:message code="common.filter"></spring:message></button>
    </form>
    <hr/>
    <a href="${pageContext.request.contextPath}/meals/create"><spring:message code="common.addMeal"></spring:message></a>
    <br><br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th><spring:message code="meal.date"></spring:message></th>
            <th><spring:message code="meal.description"></spring:message></th>
            <th><spring:message code="meal.calories"></spring:message></th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${requestScope.meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr data-meal-excess="${meal.excess}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="${pageContext.request.contextPath}/meals/update/${meal.id}"><spring:message code="common.update"></spring:message></a></td>
                <td><a href="${pageContext.request.contextPath}/meals/delete/${meal.id}"><spring:message code="common.delete"></spring:message></a></td>
            </tr>
        </c:forEach>
    </table>
</section>
<jsp:include page="/WEB-INF/jsp/fragments/footer.jsp" />
</body>
</html>