/******************************************************************************
 *            Copyright (c) 2006-2009 Michel Gutierrez. All Rights Reserved.
 ******************************************************************************/

var pref=Components.classes["@mozilla.org/preferences-service;1"]
	.getService(Components.interfaces.nsIPrefService).getBranch("dwhelper.");
var Util=Components.classes["@downloadhelper.net/util-service;1"]
	.getService(Components.interfaces.dhIUtilService);

function onLoad() {

	updateBCMedialink();
	updateBCSmartName();
	updateBCHistory();
	updateBCConversion();
	updateBCPlatform();
	updateBCMP3Tunes();
	updateBCYTInPage();
	updateBCTwitter();

	window.sizeToContent();

	var mp3tunesVisible=false;
	try {
		mp3tunesVisible=pref.getBoolPref("mp3tunes.visible");
	} catch(e) {}
	document.getElementById("tab-mp3tunes").setAttribute("collapsed",""+!mp3tunesVisible);
	
	if(window.arguments && window.arguments[0]) {
		if(window.arguments[0].selectedPanel) {
			var panel=document.getElementById(window.arguments[0].selectedPanel);
			if(panel)
				document.getElementById("dwhelper-preferences-new").showPane(panel);
		}
		if(window.arguments[0].selectedTab) {
			var tab=document.getElementById(window.arguments[0].selectedTab);
			if(tab)
				tab.parentNode.parentNode.selectedTab=tab;
		}
	}
	
	if(Util.priorTo19()) {
		document.getElementById("tab-mp3tunes").collapsed=true;
		document.getElementById("tab-twitter").collapsed=true;
	}
}

function setConversionUse() {
	if(document.getElementById("cb-conversion-enabled").checked)
		pref.setBoolPref("conversion-was-enabled",true);
}

function changeStorageDir() {
	var element=document.getElementById("dwhelper-storagedir");
	var nsIFilePicker = Components.interfaces.nsIFilePicker;
	var fp = Components.classes["@mozilla.org/filepicker;1"]
	        .createInstance(nsIFilePicker);
	fp.init(window, Util.getText("prompt.select-storage-dir"), nsIFilePicker.modeGetFolder);
	var res=null;
	res = fp.show();
	if (res == nsIFilePicker.returnOK){
		pref.setCharPref("storagedirectory",fp.file.path);
	}
}

function updateBCPlatform() {
	var bcWindows=document.getElementById("bc-platform-windows");
	var bcNotWindows=document.getElementById("bc-platform-notwindows");
	try {
		Components.classes["@mozilla.org/windows-registry-key;1"]
			.createInstance(Components.interfaces.nsIWindowsRegKey);
		bcWindows.setAttribute("collapsed","false");
		bcNotWindows.setAttribute("collapsed","true");
	} catch(e) {
		bcWindows.setAttribute("collapsed","true");
		bcNotWindows.setAttribute("collapsed","false");
	}

}

function updateBCMedialink() {
	var enableItem=document.getElementById("cb-enable-medialink-method");
	var bcItem=document.getElementById("bc-enable-medialink-method");
	bcItem.setAttribute("collapsed",""+!enableItem.checked);
}

function updateBCSmartName() {
	var enableItem=document.getElementById("cb-enable-smartname");
	var bcItem=document.getElementById("bc-enable-smartname");
	bcItem.setAttribute("collapsed",""+!enableItem.checked);
}

function updateBCHistory() {
	var enableItem=document.getElementById("cb-history-enabled");
	var bcItem=document.getElementById("bc-history-enabled");
	bcItem.setAttribute("collapsed",""+!enableItem.checked);
}

function updateBCMP3Tunes() {
	var enableItem=document.getElementById("cb-mp3tunes-enabled");
	var bcItem=document.getElementById("bc-mp3tunes-enabled");
	bcItem.setAttribute("collapsed",""+!enableItem.checked);
}

function updateBCYTInPage() {
	var enableItem=document.getElementById("cb-ytinpage-enabled");
	var bcItem=document.getElementById("bc-ytinpage-enabled");
	bcItem.setAttribute("collapsed",""+!enableItem.checked);	
}

function updateBCConversion() {
	var enableItem=document.getElementById("cb-conversion-enabled");
	var bcItem=document.getElementById("bc-conversion-enabled");
	bcItem.setAttribute("collapsed",""+!enableItem.checked);
	var cvMgr=Components.classes["@downloadhelper.net/convert-manager-component"]
		.getService(Components.interfaces.dhIConvertMgr);
	var props=cvMgr.getInfo();
	if(props.has("license"))
		document.getElementById("conv-license-license").value=props.get("license",Components.interfaces.nsISupportsString).data;
	if(props.has("customername"))
		document.getElementById("conv-license-name").value=props.get("customername",Components.interfaces.nsISupportsString).data;
	if(props.has("customeremail"))
		document.getElementById("conv-license-email").value=props.get("customeremail",Components.interfaces.nsISupportsString).data;
	if(props.get("windows",Components.interfaces.nsISupportsPRBool).data) {
		var bcConvRegistered=document.getElementById("bc-conv-registered");
		var bcConvUnregistered=document.getElementById("bc-conv-unregistered");
		if(props.get("unregistered",Components.interfaces.nsISupportsPRBool).data) {
			bcConvRegistered.setAttribute("collapsed","true");
			bcConvUnregistered.setAttribute("collapsed","false");
		} else {
			bcConvRegistered.setAttribute("collapsed","false");
			bcConvUnregistered.setAttribute("collapsed","true");
		}
		var bcConvNotFound=document.getElementById("bc-conv-not-found");
		bcConvNotFound.setAttribute("collapsed","true");
		var bcConvBadVersion=document.getElementById("bc-conv-bad-version");
		bcConvBadVersion.setAttribute("collapsed","true");
		var exeOk=true;
		if(!props.get("exefound",Components.interfaces.nsISupportsPRBool).data) {
			exeOk=false;
			bcConvNotFound.setAttribute("collapsed","false");
		} else {
			var version="1.0";
			if(props.has("converterversion"))
				version=props.get("converterversion",Components.interfaces.nsISupportsString).data;
			var minVersion="1.0";
			if(props.has("converterminversion"))
				minVersion=props.get("converterminversion",Components.interfaces.nsISupportsString).data;
			if(parseFloat(version)<parseFloat(minVersion)) {
				exeOk=false;
				bcConvBadVersion.setAttribute("collapsed","false");
			}
		}
		var bcConvOk=document.getElementById("bc-conv-ok");
		var bcConvKo=document.getElementById("bc-conv-ko");
		if(exeOk) {
			bcConvOk.setAttribute("collapsed","false");
			bcConvKo.setAttribute("collapsed","true");
		} else {
			bcConvOk.setAttribute("collapsed","true");
			bcConvKo.setAttribute("collapsed","false");
		}
	} else {
		updateConvPath();
	}
}

function updateBCTwitter() {
	var enableItem=document.getElementById("cb-twitter-enabled");
	var bcItem=document.getElementById("bc-twitter-enabled");
	bcItem.setAttribute("collapsed",""+!enableItem.checked);
}

function onHelp(event) {
	var panelTopic=null;
	var tabTopic=null;
	var prefWindow=document.getElementById("dwhelper-preferences-new");
	var prefPane=prefWindow.currentPane;
	if(prefPane.hasAttribute("helpTopic"))
		panelTopic=prefPane.getAttribute("helpTopic");
	var node=prefPane.firstChild;
	while(node) {
		if(node.nodeName=="tabbox") {
			var selTab=node.selectedTab;
			if(selTab.hasAttribute("helpTopic"))
				tabTopic=selTab.getAttribute("helpTopic");
		}
		node=node.nextSibling;
	}
	var helpUrl="http://www.downloadhelper.net/pref-help-page.php";
	if(panelTopic) {
		helpUrl+="?topic="+panelTopic;
		if(tabTopic)
			helpUrl+=";"+tabTopic;
	}
	open(helpUrl);
}

function configConvRules() {
	var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
	                            .getService(Components.interfaces.nsIWindowMediator);
	var w = wm.getMostRecentWindow("navigator:browser");
	w.openDialog("chrome://dwhelper/content/conversion-rules.xul",
		"dwhelper-conversion-rules",
		"chrome,centerscreen,modal,resizable=yes");	
}

function registerConverter() {
	var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
	                            .getService(Components.interfaces.nsIWindowMediator);
	var w = wm.getMostRecentWindow("navigator:browser");
	/*
	w.open("http://convert.downloadhelper.net/",
		"dwhelper-convert-log","");
	*/	
	w.open("chrome://dwhelper/content/convert-register.xul",
		"dwhelper-convert-register",
		"chrome,centerscreen,resizable=yes");	
}

function showConversionLog() {
	var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
	                            .getService(Components.interfaces.nsIWindowMediator);
	var w = wm.getMostRecentWindow("navigator:browser");
	w.open("chrome://dwhelper/content/convert-log.xul",
		"dwhelper-convert-log",
		"chrome,centerscreen,width=500,height=400,resizable=yes");	
}

function updateConvPath() {
	var prefConv=document.getElementById("converter").value;
	if(document.getElementById("converter").value=="ffmpeg") {
		document.getElementById("tb-converter-path-ffmpeg").setAttribute("collapsed","false");
		document.getElementById("tb-converter-path-mencoder").setAttribute("collapsed","true");
	} else {
		document.getElementById("tb-converter-path-ffmpeg").setAttribute("collapsed","true");
		document.getElementById("tb-converter-path-mencoder").setAttribute("collapsed","false");
	}
	updateConvFound(document.getElementById("tb-converter-path-"+prefConv));
}

function updateConvFound(element) {
	var path=element.value;
	var file = Components.classes["@mozilla.org/file/local;1"]
	    .createInstance(Components.interfaces.nsILocalFile);
	try {
		file.initWithPath(path);
		if(!file.exists()) {
			element.style.color="Red";
		} else {
			element.style.color="Black";
		}
	} catch(e) {
		element.style.color="Red";
	}
}

function installWinConverter() {
	var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
	                            .getService(Components.interfaces.nsIWindowMediator);
	var w = wm.getMostRecentWindow("navigator:browser");
	w.open("http://www.downloadhelper.net/install-converter.php");
}
