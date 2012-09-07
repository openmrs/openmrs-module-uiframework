<style>
TH.obs_table_header{
	background-color: #CCCCCC;
}

.alignCenter{
	text-align: center;
}

.flow_sheet_smallFont{
	font-size: xx-small; font-style: italic;
}
</style>

<b><u>Flowsheet</u></b>
<br /><br />
<table class="obs_table" cellpadding="3" cellspacing="0" border="1">
	<tr>
		<%conceptObsMap.keySet().each{%>
		<th class="obs_table_header">${it.name}</th>
		<%}%>
	</tr>
	<tr>
		<%conceptObsMap.values().each{%>
		<td class="alignCenter" valign="top">
			<%it.each{%>
				${it.getValueAsString(context.locale)} 
				<br /> <span class="flow_sheet_smallFont">${it.obsDatetime}</span>
				<br /><br />
			<%}%>
		</td>
		<%}%>
	</tr>
</table>