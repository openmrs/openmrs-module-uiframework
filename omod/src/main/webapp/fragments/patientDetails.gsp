<%
//Apparently the some properties are null at this point, Why? probably due to lazy initialization
def initializedPatient = context.patientService.getPatient(patient.patientId);%>
<i><b><span style="font-size: large;">${initializedPatient.personName}</span></b></i>
<br />
${initializedPatient.age} year-old ${initializedPatient.gender == "M"  ?  "male" : "female"}
<br /><br />

<%if(firstEncounter != null){%>
<b><u>First Encounter on:</u></b> ${context.dateFormat.format(firstEncounter.encounterDatetime)}
<br />
<ul style="list-style-type: none;">
<%firstEncounter.obs.each{%>
 	<li>${it.concept.name}  : ${it.getValueAsString(context.locale)}</li>
<%  } %>
</ul>
<%}%>