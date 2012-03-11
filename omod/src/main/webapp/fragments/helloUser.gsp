<% if (context.authenticatedUser) { %>
	${ ui.message("uiframework.hello", context.authenticatedUser.personName) }
<% } else { %>
	Not logged in.
<% } %>