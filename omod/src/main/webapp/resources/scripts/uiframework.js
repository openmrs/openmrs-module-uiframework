// checks whether a javascript object (e.g. a json response) is empty
function isEmpty(obj) {
	for (var i in obj) {
		return false;
	}
	return true;
}

// general UI utilities

function setPageTitle(newTitle) {
	document.title = newTitle;
}

function openmrsConfirm(message, id) {
	// replace this with something actually pretty
	return confirm(message)
}

// global flash messages
function notifySuccess(html) {
	if (html && html != '') {
		$().toastmessage('showSuccessToast', html);
	}
}

function notifyError(html) {
	if (html && html != '') {
		$().toastmessage('showToast', { text: html, sticky: true, type: 'error' });
	}
}

function flashSuccess(html) { // deprecate this
	if (html && html != '') {
		$().toastmessage('showSuccessToast', html);
	}
}

function flashError(html) { // deprecate this
	if (html && html != '') {
		$('#error-message').html(html);
		$('#errors').show('fast');
	} else {
		$('#error-message').html('');
		$('#errors').hide('fast');
	}
}

// utility methods for handling error messages
function fragmentActionError(jqXHR, defaultMessage) {
	try {
		var err = $.parseJSON(jqXHR.responseText);
		for (var i = 0; i < err.globalErrors.length; ++i)
			notifyError(err.globalErrors[i]);
		for (key in err.fieldErrors) {
			for (var i = 0; i < err.fieldErrors[key].size(); ++i)
				notifyError(key + ": " + err.fieldErrors[key][i]);
		}
		return;
	} catch (ex) { }
	notifyError(defaultMessage);
}

// utility methods for js/gsp compatibility
function actionLink(fragmentName, actionName, options) {
	var ret = OPENMRS_CONTEXT_PATH + '/' + fragmentName + '/' + actionName + '.action?';
	if (options)
		for (key in options)
			ret += key + '=' + options[key] + '&';
	return ret;
}

function isTrueHelper(test) {
	if (!test)
		return false;
	if (typeof(test) == 'string')
		return test.charAt(0) == 't';
	else
		return test == true;
}

function isFalseHelper(test) {
	if (!test)
		return true;
	if (typeof(test) == 'string')
		return test.charAt(0) == 'f';
	else
		return test == false;
}

// validation for fields
function clearErrors(errorDivId) {
	if (errorDivId)
		$('#' + errorDivId).html('').hide('fast');
}

function showError(errorDivId, message) {
	$('#' + errorDivId).append(message).show('fast');
}

function validateRequired(val, errorDivId) {
	if ((val + '').length == 0)
		showError(errorDivId, 'Required');
}

function validateNumber(val, errorDivId) {
	if (!isValidNumber(val)) {
		showError(errorDivId, 'Error');
	}
}

function validateInteger(val, errorDivId) {
	if (!isValidInteger(val)) {
		showError(errorDivId, 'Error');
	}
}

function isValidNumber(val) {
	var asNum = Number(val);
	return !isNaN(asNum);
}

function isValidInteger(val) {
	var asNum = Number(val);
	return !isNaN(asNum) && (asNum == Math.round(asNum));
}

//// Event Bus support //////////////////

function publish(message, payload) {
	window.PageBus.publish(message, payload);
}

function subscribe(message, callback) {
	return window.PageBus.subscribe(message, null, callback, null);
}

//// Messaging //////////////////

function patientChanged(patientId, property) {
	jQuery.getJSON(OPENMRS_CONTEXT_PATH + '/data/getPatient.action?returnFormat=json&patientId=' + patientId, function(patient) {
		var message = "patient/" + patientId;
		if (property)
			message += "/" + property;
		message += ".changed"
		publish(message, patient);
	});
}

//// Dialog support (shared modal dialog for all pages/fragments) //////////////////

var openmrsDialogCurrentlyShown = null;
var openmrsDialogIFrame = null;

function showDivAsDialog(selector, title) {
	// There is (what I consider) a bug in jquery-ui dialog, where displaying a dialog that
	// has scripts in it re-executes the scripts. We introduce a hack to get around this, by
	// removing all scripts before we display the dialog, and then reattaching them. But we
	// have to reattach them with normal DOM, not jquery's append, since the latter would also
	// reexecute them
	// TODO determine if we can rid of the hack. If not, refactor the hack, because it currently assumes the selector matches a single dialog 
	var dialogContainer = $(selector);
	var dialogScripts = dialogContainer.find("script");
	dialogScripts.remove();
	openmrsDialogCurrentlyShown = $(selector).dialog({
			draggable: false,
			resizable: false,
			show: null,
			width: '90%',
			height: $(window).height()-50,
			modal: true,
			title: title
		});
	dialogScripts.each(function() {
		dialogContainer.get(0).appendChild(this);
	});
}

/**
 * opts must define url or fragment, and may define title and successCallback.
 */
function showDialog(opts) {
	var url = opts.url;
	if (opts.fragment) {
		url = '/' + OPENMRS_CONTEXT_PATH + '/pages/fragment.page?fragment=' + opts.fragment;
		if (opts.config) {
			for (var param in opts.config) {
				url += "&" + param + "=" + opts.config[param];
			}
		}
	}
	if (!openmrsDialogIFrame) {
		openmrsDialogIFrame = document.createElement('iframe');
		openmrsDialogIFrame.width = '100%';
		openmrsDialogIFrame.height = '100%';
		openmrsDialogIFrame.marginWidth = 0;
		openmrsDialogIFrame.marginHeight = 0;
		openmrsDialogIFrame.frameBorder = 0;
		openmrsDialogIFrame.scrolling = 'auto';
		$('#openmrsDialog').append(openmrsDialogIFrame);
	}
	$("#openmrsDialog > iframe").attr("src", url);

	if (!opts.title)
		opts.title = "";
	
	openmrsDialogSuccessCallback = opts.successCallback; // TODO attach this to the close button
	$('#openmrsDialog')
		.dialog('option', 'title', opts.title)
		.dialog('option', 'height',$(window).height()-50) // TODO resize dialog on window resize?
		.dialog('open');
	openmrsDialogCurrentlyShown = $('#openmrsDialog');
}

function closeDialog(doCallback) {
	if (openmrsDialogCurrentlyShown && openmrsDialogCurrentlyShown.length > 0) {
		openmrsDialogCurrentlyShown.dialog('close');
		var callMe = openmrsDialogSuccessCallback;
		openmrsDialogSuccessCallback = null;
		if (doCallback && callMe) {
			callMe.call();
		}
	} else if (window.parent && window.parent != window) {
		window.parent.closeDialog(doCallback);
	}
}

//// InfoBox //////////////////

function InfoBox(id) {
	var rootId = id;
	
	this.setTitle = function(html) {
		jQuery('#' + rootId + ' > .title').html(html);
	}
	
	this.setContent = function(html) {
		jQuery('#' + rootId + ' > .content').html(html);
	}
	
	this.showEncounterById = function(encounterId) {
		var me = this;
		jQuery.getJSON(OPENMRS_CONTEXT_PATH + '/infobox/getEncounterJson.action?returnFormat=json&encounterId=' + encounterId, function(encounter, textStatus) {
			var title = encounter.encounterType + '<br/>';
			title += 'Date: ' + encounter.encounterDatetime + '<br/>';
			title += 'Location: ' + encounter.location + '<br/>';
			me.setTitle(title);
			var content = '<table>';
			for (var i = 0; i < encounter.obs.length; ++i) {
				content += '<tr><td class="small">' + encounter.obs[i].concept + ':</td><td>' + encounter.obs[i].value + '</td></tr>';
			}
			content += '</table>';
			me.setContent(content);
		});
	}
}

function standardUiDecorations() {
	$('.button').button();
	$('.add-tool-tip').tipTip({ delay: 100 });
	decorateTables();
}

function decorateTables() {
	$('table.decorated').addClass("ui-widget");
	$('table.decorated tr.alternate-shading-odd').removeClass('alternate-shading-odd');
	$('table.decorated tr.alternate-shading-even').removeClass('alternate-shading-even');
	$('table.decorated tr:odd').addClass("alternate-shading-odd");
	$('table.decorated tr:even').addClass("alternate-shading-even");
	$('table.decorated thead').addClass("ui-widget-header");
	$('table.decorated tbody').addClass("ui-widget-content");
}

function escapeJs(string) {
	string = string.replace(/'/g, "\\'");
	string = string.replace(/"/g, '\\"');
	return string;
}

function escapeHtml(string) {
	string = string.replace(/</g, "&lt;");
	string = string.replace(/>/g, "&gt;");
	return string;
}