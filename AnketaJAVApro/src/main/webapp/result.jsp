<%--
  Created by IntelliJ IDEA.
  User: Администратор
  Date: 29.12.2022
  Time: 20:49
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Result</title>
</head>
<body>
<table>
    <c:forEach items = "${answerList}" var = "0">

        <tr>
        <td><c:out value = "0"/></td>
        </tr>
        </c:forEach>
</table>
</body>
</html>
