<%
	def user = config.user ?: context.authenticatedUser
%>

<% if (user) { %>
	${ ui.message("uiframework.helloUser", user.personName) }
<% } else { %>
	Not logged in.
<% } %>