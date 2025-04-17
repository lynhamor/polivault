<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${data.title}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/styles/speech.css">
</head>
<body>
<div class="speech-card">
    <h1 class="speech-title">${data.title}</h1>
    <p class="speech-meta">
        <span>Authored By: ${data.author}</span> |
        <span>${data.eventAt}</span>
    </p>
    <p class="speech-content">${data.content}</p>

    <div class="tags">
        <c:forEach var="tag" items="${data.keywords}">
            <span class="tag">#${tag}</span>
        </c:forEach>
    </div>
</div>
<footer>
    <div>
        <p>&copy; 2025 Speech Vault</p>
    </div>
</footer>
</body>
</html>
