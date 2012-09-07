<style>
.problems_smallFont{
	font-size: xx-small; font-style: italic;
}
</style>
<b><u>Problem List</u></b>
<ul>
<%problems.each { %>
 	<li>${it.concept.name} <span class="problems_smallFont">${it.startDate}</span></li>
<% } %>
</ul>