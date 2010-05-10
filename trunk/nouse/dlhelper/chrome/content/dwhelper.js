/******************************************************************************
 *            Copyright (c) 2006-2009 Michel Gutierrez. All Rights Reserved.
 ******************************************************************************/

var DWHelper={
	uuid: "{b9db16a4-6edc-47ec-a1f4-b86292ed211d}",
	core: Components.classes["@downloadhelper.net/core;1"].getService(Components.interfaces.dhICore),
	prefViewers: [
	              ["dwhelper-statusbarpanel","show-in-statusbar"],
	              ["dwhelper-toolsmenu","show-in-toolsmenu"],
	              ["dwhelper-toolsmenu-separator","show-in-toolsmenu"],
	              ["dwhelper-ctxmenu","context-menu"],
	],
	quickKeys: [
	            "quickkey",
	            "opendirkey"
	],
	startupServices: [
	                  "@downloadhelper.net/license-handler;1",
	                  "@downloadhelper.net/convconf-handler;1",
	                  "@downloadhelper.net/safe-mode-handler;1",
	                  "@downloadhelper.net/youtube-probe;1",
	                  "@downloadhelper.net/medialink-probe;1",
	                  "@downloadhelper.net/network-probe;1",
	                  "@downloadhelper.net/download-processor;1",
	                  "@downloadhelper.net/quick-download-processor;1",
	                  "@downloadhelper.net/download-convert-processor;1",
	                  "@downloadhelper.net/twitter-processor;1",
	                  "@downloadhelper.net/flashgot-download-processor;1",
	                  "@downloadhelper.net/copyurl-processor;1",
	                  "@downloadhelper.net/dump-processor;1",
	                  "@downloadhelper.net/add-to-blacklist-processor;1",
	                  "@downloadhelper.net/mp3tunes-mobile-processor;1",
	                  "@downloadhelper.net/mp3tunes-locker-processor;1",
	],
	pref: Components.classes["@mozilla.org/preferences-service;1"]
	                        .getService(Components.interfaces.nsIPrefService).getBranch("dwhelper."),
	util: Components.classes["@downloadhelper.net/util-service;1"]
	                     	.getService(Components.interfaces.dhIUtilService),
    firstSessionLaunch: true,
};

DWHelper.version=DWHelper.util.getVersion(DWHelper.uuid);

window.addEventListener("load", DWHelper_onLoad, false);
window.addEventListener("unload", DWHelper_onUnload, false);

for(var i in DWHelper.startupServices) {
	var service=DWHelper.startupServices[i];
	try {
		Components.classes[service].getService();
		//dump("[DWHelper/startup]: loaded service "+service+"\n");
	} catch(e) {
		dump("!!! [DWHelper/startup]: loading service "+service+": "+e+"\n");
	}
}

function DWHelper_onLoad(e) {

	try {
		if(DWHelper.firstSessionLaunch) {
			DWHelper_fixPreferences();
			DWHelper_updateKeys();
			DWHelper.firstSessionLaunch=false;
			var version=DWHelper.version;
			if(DWHelper.pref.getBoolPref("first-time")) {
				try {
					function welcome(version) {
						try {
							var browser = top.getBrowser();
							browser.selectedTab=browser.addTab("http://www.downloadhelper.net/welcome.php?version="+version);
						} catch(e) {
						}
					}
					setTimeout(welcome,100,version);
				} catch(e) {
				}
				DWHelper.pref.setBoolPref("first-time",false);
				DWHelper.pref.setCharPref("last-version",version);
				DWHelper_install();
			} else {
				var lastVersion="1.95";
				try {
					lastVersion=DWHelper.pref.getCharPref("last-version");
				} catch(e) {}
				if(lastVersion!=version) {
					DWHelper.pref.setCharPref("last-version",version);
					try {
						function install(lastVersion,version,cwe) {
							try {
								var browser = top.getBrowser();
								if(cwe)
									browser.selectedTab=browser.addTab("http://www.downloadhelper.net/update.php?to="+version+"&from="+lastVersion);
								else
									browser.selectedTab=browser.addTab("http://www.downloadhelper.net/update.php?from="+lastVersion+"&to="+version);
							} catch(e) {
							}
						}
						var cwe=false;
						try {
							cwe=DWHelper.pref.getBoolPref("conversion-was-enabled");
						} catch(e) {}
						setTimeout(install,100,lastVersion,version,cwe);
					} catch(e) {}
				}
			}
			for(var i in DWHelper.prefViewers) {
				DWHelper_updateWidgetVisibility(DWHelper.prefViewers[i][0],DWHelper.prefViewers[i][1]);
			}
			DWHelper.core.registerWindow(window);
		}
	} catch(e) {
		dump("!!! [DWHelper] onload(): "+e+"\n");
	}
}

function DWHelper_onUnload(e) {
	DWHelper.core.unregisterWindow(window);
}

function DWHelper_fixPreferences() {
	var downloadMode="onebyone";
	try {
		downloadMode=DWHelper.pref.getCharPref("download-mode");
	} catch(e) {}
	if(downloadMode=="flashgot") {
		try {
			Components.classes["@maone.net/flashgot-service;1"].
				getService(Components.interfaces.nsISupports).wrappedJSObject;
			DWHelper.pref.setCharPref("processor-keymap","0:flashgot-download,2:convert-choice,3:quick-download");
		} catch(e) {}
		DWHelper.pref.setCharPref("download-mode","onebyone");
	}
	
	function fixPrefKey(prefId) {
		try {
			DWHelper.pref.getCharPref(prefId);
		} catch(e) {
			var key=DWHelper.pref.getCharPref(prefId+".key");
			var modifier=DWHelper.pref.getCharPref(prefId+".modifier");
			var modifiers=modifier.split(" ");
			modifier=0;
			for(var i=0;i<modifiers.length;i++) {
				if(modifiers[i]=="control")
					modifier|=1;
				if(modifiers[i]=="shift")
					modifier|=2;
				if(modifiers[i]=="alt")
					modifier|=4;
			}
			DWHelper.pref.setCharPref(prefId,modifier+";"+key);
		}
	}
	fixPrefKey("quickkey");
	fixPrefKey("opendirkey");
	
	try {
		DWHelper.pref.getIntPref("menu-expiration");
	} catch(e) {
		var expiration=60;
		try {
			expiration=parseInt(DWHelper.pref.getIntPref("menu-http-expiration"))/1000;
			expiration=parseInt(""+expiration);
		} catch(e) {}
		DWHelper.pref.setIntPref("menu-expiration",expiration);
	}

	try {
		DWHelper.pref.getIntPref("mediaweight");
	} catch(e) {
		var mediaweightEnabled=DWHelper.pref.getBoolPref("mediaweight-enabled");
		var mediaweightThreshold=DWHelper.pref.getIntPref("mediaweight-threshold");
		var mediaweight="";
		if(mediaweightEnabled) {
			mediaweight=""+parseInt(""+(mediaweightThreshold/1024));
		}
		DWHelper.pref.setCharPref("mediaweight",mediaweight);
	}
	
	var encoders=["ffmpeg","mencoder"];
	for(var i in encoders) {
		var encoder=encoders[i];
		try {
			DWHelper.pref.getCharPref("converter-path-"+encoder);
		} catch(e) {
			try {
				var encoderPath=DWHelper.pref.getCharPref(encoder+"-path");
				DWHelper.pref.setCharPref("converter-path-"+encoder,encoderPath);
			} catch(e) {}
		}
	}
}

function DWHelper_install() {
	try {
		var dwhelperId="dwhelper-toolbaritem";
		var afterId="urlbar-container";
		var afterElem=document.getElementById(afterId);
		if(afterElem) {
			var navBar=afterElem.parentNode;
			if(document.getElementById(dwhelperId)==null) {
				// waiting for firefox bug 403959 to be solved
				//navBar.insertItem(dwhelperId,afterElem.nextSibling);
				navBar.insertItem(dwhelperId,afterElem);
				navBar.setAttribute("currentset", navBar.currentSet );
				document.persist("nav-bar", "currentset");
			}
		}		
	} catch(e) {
	}
}

function DWHelper_observer() {
}
DWHelper_observer.prototype={
	observe: function(subject, topic , data) {
		if(topic=="nsPref:changed") {
			for(var i in DWHelper.prefViewers) {
				if(DWHelper.prefViewers[i][1]==data) {
					DWHelper_updateWidgetVisibility(DWHelper.prefViewers[i][0],DWHelper.prefViewers[i][1]);
				}
			}
			for(var i in DWHelper.quickKeys) {
				var key="dwhelper."+DWHelper.quickKeys[i];
				if(data.substr(0,key.length)==key) {
					DWHelper_updateKeys();
					break;
				}
			}
		}
	},
	QueryInterface: function(iid) {
	    if(
	    	iid.equals(Components.interfaces.nsIObserver)==false &&
	    	iid.equals(Components.interfaces.nsISupports)==false
		) {
	            throw Components.results.NS_ERROR_NO_INTERFACE;
	        }
	    return this;
	}
	
}

DWHelper.observer=new DWHelper_observer();
DWHelper.prefBranch2=Components.classes["@mozilla.org/preferences-service;1"]
                                        .getService(Components.interfaces.nsIPrefService)
                                        .getBranch("dwhelper.")
                                        .QueryInterface(Components.interfaces.nsIPrefBranch2);
DWHelper.prefBranch2.addObserver("", DWHelper.observer, false);

function DWHelper_updateWidgetVisibility(widgetId,prefId) {
	//dump("DWHelper_updateWidgetVisibility("+widgetId+","+prefId+")\n");
	var widget=document.getElementById(widgetId);
	if(widget) {
		var enabled=false;
		try {
			enabled=DWHelper.pref.getBoolPref(prefId);
		} catch(e) {}
		//dump("=>"+enabled+"\n");
		widget.setAttribute("hidden",""+!enabled);
	} 
}

function DWHelper_updateKeys() {
	function updateKey(defKey,keyElemId,prefName) {
		var key=defKey;
		try {
			key=DWHelper.pref.getCharPref(prefName);
		} catch(e) {}
		var keyParts=key.split(";",2);
		var key=keyParts[1];
		var modifier=keyParts[0];
		if(!/^VK_/.test(key))
			key="VK_"+key;
	
		var keyElem=document.getElementById(keyElemId);
		if(keyElem) {
			if(/^VK_.$/.test(key)) {
				var m=/^VK_(.)$/.exec(key);
				keyElem.removeAttribute("keycode");
				keyElem.setAttribute("key",m[1]);
			} else {
				keyElem.removeAttribute("key");
				keyElem.setAttribute("keycode",key);
			}
			var modifiers=[];
			if(modifier&1)
				modifiers.push("control");
			if(modifier&2)
				modifiers.push("shift");
			if(modifier&4)
				modifiers.push("alt");
			keyElem.setAttribute("modifiers",modifiers.join(" "));
		}
	}
	updateKey("3;VK_Q","dwhelper-quick-key","quickkey");
	updateKey("3;VK_O","dwhelper-opendir-key","opendirkey");
}

