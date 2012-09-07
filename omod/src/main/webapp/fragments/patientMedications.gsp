<b><u>Medication List</u></b>
<ul>
<%medications.each { %>
                <li>${ it.drug ? it.drug.concept.name : it.concept.name }</li>
 <% } %>
</ul>