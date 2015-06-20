<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title></title>
</head>
<body>
    <h1><spring:message code="label.status.list.page.title"/></h1>
    <div>
        <a href="/status/add" id="add-button" class="btn btn-primary"><spring:message code="label.add.status.link"/></a>
    </div>
    <div id="status-list" class="page-content">
        <c:choose>
            <c:when test="${empty statuses}">
                <p><spring:message code="label.status.list.empty"/></p>
            </c:when>
            <c:otherwise>
                <c:forEach items="${ statuses}" var="status">
                    <div class="well well-small">
                        <a href="/status/${status.id}"><c:out value="${status.title}"/></a>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>