ABC is easy as 
<% [1, 2, 3].each { %>
	${ it }
<% } %>

${ ui.includeFragment("fragment", [id: "1", name: "first"]) }

${ ui.includeFragment("fragment", [id: "2", name: "second"]) }