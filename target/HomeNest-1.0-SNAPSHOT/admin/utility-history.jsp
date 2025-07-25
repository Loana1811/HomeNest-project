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
        <div class="alert alert-warning">⚠️ No history records found.</div>
    </c:if>

    <c:if test="${not empty historyList}">
        <table class="table table-bordered table-hover bg-white">
            <thead class="table-dark">
                <tr>
                    <th>Type</th>
                    <th>Name</th>
                    <th>Old Price</th>
                    <th>New Price</th>
                    <th>Changed By</th>
                    <th>Change Date</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="h" items="${historyList}">
                    <tr>
                        <td>
                            <span class="badge bg-primary">
                                <c:choose>
                                    <c:when test="${h.isUtility}">
                                        Utility
                                    </c:when>
                                    <c:otherwise>
                                        Incurred Fee
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty h.utilityName}">
                                    <span class="text-danger">[Removed]</span>
                                </c:when>
                                <c:otherwise>
                                    ${h.utilityName}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatNumber value="${h.oldPrice}" type="number" pattern="#,##0"/>₫</td>
                        <td><fmt:formatNumber value="${h.newPrice}" type="number" pattern="#,##0"/>₫</td>
                        <td>${h.changedBy}</td>
                        <td><fmt:formatDate value="${h.createdAt}" pattern="yyyy-MM-dd"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <a href="utility?action=list" class="btn btn-secondary mt-3">← Back</a>
</div>
</body>
</html>
