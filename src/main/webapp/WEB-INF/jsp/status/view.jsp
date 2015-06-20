<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/static/js/status.view.js"></script>
</head>
<body>
    <div id="status-id" class="hidden">${status.id}</div>
    <h1><spring:message code="label.status.view.page.title"/></h1>
    <div class="well page-content">
        <h2 id="status-title"><c:out value="${status.title}"/></h2>
        <div>
            <p><c:out value="${status.description}"/></p>
        </div>
        <div class="action-buttons">
            <a href="/status/update/${status.id}" class="btn btn-primary"><spring:message code="label.update.status.link"/></a>
            <a id="delete-status-link" class="btn btn-primary"><spring:message code="label.delete.status.link"/></a>
        </div>
    </div>
    <script id="template-delete-status-confirmation-dialog" type="text/x-handlebars-template">
        <div id="delete-status-confirmation-dialog" class="modal">
            <div class="modal-header">
                <button class="close" data-dismiss="modal">×</button>
                <h3><spring:message code="label.status.delete.dialog.title"/></h3>
            </div>
            <div class="modal-body">
                <p><spring:message code="label.status.delete.dialog.message"/></p>
            </div>
            <div class="modal-footer">
                <a id="cancel-status-button" href="#" class="btn"><spring:message code="label.cancel"/></a>
                <a id="delete-status-button" href="#" class="btn btn-primary"><spring:message code="label.delete.status.button"/></a>
            </div>
        </div>
    </script>
</body>
</html>