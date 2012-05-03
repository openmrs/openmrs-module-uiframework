<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<h1>UI Framework Error</h1>

<h2>Root Error</h2>
<pre>${ rootStacktrace }</pre>

<h2>Full Error</h2>
<pre>${ fullStacktrace }</pre>

<%@ include file="/WEB-INF/template/footer.jsp"%>