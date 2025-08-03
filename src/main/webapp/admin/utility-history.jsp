<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.*, model.UtilityHistoryView" %>

<%
    String ctx = request.getContextPath();
%>

<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!-- Main Content -->
<div class="main-content">
    <h3 class="mb-4">📜 Price Change History</h3>

    <c:if test="${empty historyList}">
        <div class="alert alert-info">There are no pending utility prices.</div>
    </c:if>

    <c:if test="${not empty historyList}">
        <table class="table table-bordered table-hover">
            <thead class="table-light">
                <tr>
                    <th>Utility type</th>
                
                    <th>Prices coming soon</th>
                    <th>Date changes</th>
                    <th>Date of application</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="h" items="${historyList}">
                    <tr>
                        <td>${h.utilityName}</td>
                       
                        <td><fmt:formatNumber value="${h.newPrice}" pattern="#,##0"/> ₫</td>
                        <td><fmt:formatDate value="${h.createdAt}" pattern="yyyy-MM-dd"/></td>
                        <td><fmt:formatDate value="${h.applyAt}" pattern="yyyy-MM-dd"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <a href="utility?action=list" class="btn btn-secondary mt-3">← Back</a>
</div>
</body>
</html>