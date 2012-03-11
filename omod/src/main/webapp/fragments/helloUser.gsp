<% if (context.authenticatedUser) { %>
	${ ui.message("uiframework.helloUser", context.authenticatedUser.personName) }
<% } else { %>
	Not logged in.
<% } %>