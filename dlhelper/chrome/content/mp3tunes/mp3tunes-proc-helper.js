/******************************************************************************
 *            Copyright (c) 2006 Michel Gutierrez. All Rights Reserved.
 ******************************************************************************/

/**
* Object constructor
*/
function MTProcHelper(mobile) {
	try {
		this.mobile=mobile;
		this.freeLimit=3;
		//dump("[MTProcHelper] constructor\n");
		var prefService=Components.classes["@mozilla.org/preferences-service;1"]
		                                   .getService(Components.interfaces.nsIPrefService);
		this.pref=prefService.getBranch("dwhelper.mp3tunes.");
		this.dlMgr=Components.classes["@downloadhelper.net/download-manager;1"]
			                        	.getService(Components.interfaces.dhIDownloadMgr);
		this.cvMgr=Components.classes["@downloadhelper.net/convert-manager-component"]
		              					.getService(Components.interfaces.dhIConvertMgr);
		this.mtMgr=Components.classes["@downloadhelper.net/mp3tunes-manager;1"]
		                               	.getService(Components.interfaces.dhIMP3Tunes);
	} catch(e) {
		dump("[MTProcHelper] !!! constructor: "+e+"\n");
	}
}

MTProcHelper.prototype = {
	get enabled() { return this.pref.getBoolPref("enabled"); }
}

MTProcHelper.prototype.canHandle=function(desc) {
	//dump("[MTProcHelper] canHandle()\n");
	if(desc.has("media-url"))
		return true;
	else
		return false;
}

MTProcHelper.prototype.requireDownload=function(desc) {
	//dump("[MTProcHelper] requireDownload()\n");
	//return desc.has("media-url");
	return false;
}

MTProcHelper.prototype.preDownload=function(desc,sendToMobile) {
 	return true;
}

MTProcHelper.prototype.handle=function(desc,promptDownload) {
	//dump("[MTProcHelper] handle()\n");
	
	var browserCompatible=false;
	
	try {
		var browserVersion=Components.classes["@mozilla.org/xre/app-info;1"]
				    		                   .getService(Components.interfaces.nsIXULAppInfo).platformVersion;
		var comparator=Components.classes["@mozilla.org/xpcom/version-comparator;1"]
		                                  .getService(Components.interfaces.nsIVersionComparator);
		if(comparator.compare(browserVersion,"1.9")>=0)
			browserCompatible=true;
	} catch(e) {}
	
	if(!browserCompatible) {
		Util.alertError(Util.getText("mp3tunes.error.browser-incompatible"));
		return;
	}
	
	try {
		
		var skipConversion=false;
		var extension="";
		try {
			extension=/\.([^\.]{1,5})$/.exec(Util.getPropsString(desc,"file-name"))[1];
		} catch(e) {}

		if(["mp3","aac","wav"].indexOf(extension)>=0) {
			skipConversion=true;
		} else if(["flv","mp4","mpeg","mpeg4","avi","wmv"].indexOf(extension)>=0 && this.pref.getBoolPref("allow-video-upload")) {
			
			var onSend=this.pref.getCharPref("upload."+(this.mobile?"mobile":"locker"));

			switch(onSend) {
				case "video":
					skipConversion=true;
					break;
				case "ask":
				    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
				                                .getService(Components.interfaces.nsIWindowMediator);
					var window = wm.getMostRecentWindow("navigator:browser");
				    var options="chrome,centerscreen,toolbar,modal";
				    var data={ target: this.mobile?"mobile":"locker" }
				    window.openDialog("chrome://dwhelper/content/mp3tunes/conversion-choice.xul",'',options, data );
				    if(data.choice==null)
				    	return;
				    if(data.choice=="video")
				    	skipConversion=true;
			}
		} 
		
		if(skipConversion==false) {
			if(!this.cvMgr.checkConverter(false)) {
				if(this.pref.getBoolPref("show-no-converter-warning")==true) {
				    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
				                                .getService(Components.interfaces.nsIWindowMediator);
					var window = wm.getMostRecentWindow("navigator:browser");
				    var options="chrome,centerscreen,toolbar,modal";

				    var data={};
				    window.openDialog("chrome://dwhelper/content/mp3tunes/warn-no-converter.xul",'',options, data );
				    if(data.result==0)
				    	return;
				    else if(data.result==1)
						skipConversion=true;
				    else if(data.result==2) {
					    var options="chrome,centerscreen,toolbar,modal";
					    var data={ selectedPanel: "panel-conversion" }
					    window.openDialog("chrome://dwhelper/content/preferences-new.xul",'',options, data );
				    }
				}
			}
		}

		if(skipConversion==false) {
			if(!this.cvMgr.checkConverter(true)) {
				return;
			}
		}

		var sSkipConversion=Components.classes["@mozilla.org/supports-PRBool;1"].createInstance(Components.interfaces.nsISupportsPRBool);
		sSkipConversion.data=skipConversion;
		desc.set("mp3tunes-skip-conversion",sSkipConversion);
		
		function AccountStatusObserver(client,entry) {
			this.client=client;
			this.entry=entry;
		}
		AccountStatusObserver.prototype={
			observe: function(subject,topic,data) {
				if(topic=="mp3tunes-account-status") {
				    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
				                                .getService(Components.interfaces.nsIWindowMediator);
					var window = wm.getMostRecentWindow("navigator:browser");
					window.setTimeout(function(_this) {
						_this.client.authResult(_this.entry,data);
					},0,this,data);
				}
			}
		}
		var user=this.pref.getCharPref("username");
		while(user.length==0) {
		    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
		                                .getService(Components.interfaces.nsIWindowMediator);
			var window = wm.getMostRecentWindow("navigator:browser");
			if(window.confirm(Util.getText("mp3tunes.confirm.configure-account"))==false)
				return;
			this.openPreferences();
			user=this.pref.getCharPref("username");
		}
		var password=Util.getPassword("mp3tunes");
		if(password==null) // no password set, just let server reports the error
			password=""; 
		this.mtMgr.accountStatus(user,password,new AccountStatusObserver(this,desc));
	} catch(e) {
		dump("!!! [QMTProcHelper] handle(): "+e+"\n");
	}
}

MTProcHelper.prototype.openPreferences=function() {
    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
                                .getService(Components.interfaces.nsIWindowMediator);
	var window = wm.getMostRecentWindow("navigator:browser");
    var options="chrome,centerscreen,toolbar,modal";
    var data={ selectedPanel: "panel-services", selectedTab: "tab-mp3tunes" }
    window.openDialog("chrome://dwhelper/content/preferences-new.xul",'',options, data );	
}

MTProcHelper.prototype.authResult=function(entry,status,message) {
    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
                                .getService(Components.interfaces.nsIWindowMediator);
	var window = wm.getMostRecentWindow("navigator:browser");
	if(status=="free") {
		var today=new Date();
		var date=""+today.getDate()+"-"+(today.getMonth()+1)+"-"+today.getFullYear();
		var freeLimit="0;"+date;
		try {
			freeLimit=this.pref.getCharPref("free-limit");
		} catch(e) {}
		var parts=freeLimit.split(";");
		var count=parseInt(parts[0]);
		if(date==parts[1] && count>=this.freeLimit) {
			var rc=window.confirm(Util.getFText("mp3tunes.notification.limit-reached",[""+this.freeLimit],1));
			if(rc)
				window.open("http://www.downloadhelper.net/mp3tunes-upgrade.php");
			return;
		}
		this.download(entry,null);		
	} else if(status=="premium") {
		this.download(entry,null);		
	} else {
		Util.alertError(Util.getFText("mp3tunes.error.failed-auth",[status],1));
		this.openPreferences();
	}
}

MTProcHelper.prototype.download=function(entry,ctx) {
    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
                                .getService(Components.interfaces.nsIWindowMediator);
	var window = wm.getMostRecentWindow("navigator:browser");
    if(this.mobile) {
    	var phonenumber=this.pref.getCharPref("phonenumber");
    	while(phonenumber.length==0) {
    		if(!window.confirm(Util.getText("mp3tunes.confirm.no-phonenumber")))
    			return;
    		this.openPreferences();
    		phonenumber=this.pref.getCharPref("phonenumber");
    	}
    }
    var options="chrome,centerscreen,toolbar,modal";
    var data={ entry: entry }
    window.openDialog("chrome://dwhelper/content/mp3tunes/title.xul",'',options, data );
    if(!data.ok)
    	return;

	this.dlMgr.download(this,entry,ctx);
}

MTProcHelper.prototype.downloadFinished=function(status, request, entry, ctx) {
	//dump("[MTProcHelper] downloadFinished()\n");
	if(status==0) {
		if(entry.get("mp3tunes-skip-conversion",Components.interfaces.nsISupportsPRBool).data) {
			var file=entry.get("dl-file",Components.interfaces.nsILocalFile);
			this.mtMgr.uploadFile(file,entry,new UploadFileObserver(this,entry));
		} else {
		 	var file=Components.classes["@mozilla.org/file/directory_service;1"]
			 	                        .getService(Components.interfaces.nsIProperties)
			 	                        .get("TmpD", Components.interfaces.nsIFile);
	 		file.append("mp3tunes.mp3");
		 	file.createUnique(Components.interfaces.nsIFile.NORMAL_FILE_TYPE, 0644);
		 	entry.set("cv-file",file);
			var format="mp3/-ab 128k -f mp3";
			this.cvMgr.addConvert(entry.get("dl-file",Components.interfaces.nsILocalFile),file,format,true,this,entry,ctx);
		}
	}
}

MTProcHelper.prototype.conversionFinished=function(status, entry, ctx) {
	//dump("[MTProcHelper] conversionFinished("+status+",...)\n");
	var file=entry.get("cv-file",Components.interfaces.nsILocalFile);
	this.mtMgr.uploadFile(file,entry,new UploadFileObserver(this,entry));
}

MTProcHelper.prototype.notification=function(message,entry) {
	try {
		var alertsService = Components.classes["@mozilla.org/alerts-service;1"]
		                                       .getService(Components.interfaces.nsIAlertsService);
		var title=Util.getText("mp3tunes.notification.upload.title");
		var iconUrl="http://www.downloadhelper.net/mp3tunes-logo.php?type=";
		if(this.mobile)
			iconUrl+="mobile";
		else
			iconUrl+="locker";
		iconUrl+="&converted=";
		if(entry.get("mp3tunes-skip-conversion",Components.interfaces.nsISupportsPRBool).data)
			iconUrl+="0";
		else
			iconUrl+="1";
		alertsService.showAlertNotification(iconUrl,title,message);
	} catch(e) {}
}

MTProcHelper.prototype.uploadFinished=function(status, entry, message) {
	//dump("[MTProcHelper] uploadFinished("+status+",...)\n");
	try {
		if(entry.get("mp3tunes-skip-conversion",Components.interfaces.nsISupportsPRBool).data) {
			entry.get("dl-file",Components.interfaces.nsILocalFile).remove(false);
		} else {
			entry.get("cv-file",Components.interfaces.nsILocalFile).remove(false);
		}
	} catch(e) {}
	if(status) {
		if(!this.mobile)
			this.notification(Util.getText("mp3tunes.notification.upload.succeeded"),entry);
		this.countUploads(this.entry);
	} else {
		Util.alertError(Util.getFText("mp3tunes.notification.upload.failed",[message],1));
	}
	if(this.mobile) {
		this.sendToPhone(entry);
	}
}

MTProcHelper.prototype.sendToPhone=function(entry) {
	//dump("[MTProcHelper] sendToPhone()\n");
	function SendToPhoneObserver(client,entry,phonenumber) {
		this.client=client;
		this.entry=entry;
		this.phonenumber=phonenumber;
	}
	SendToPhoneObserver.prototype={
		observe: function(subject,topic,data) {
			if(topic=="mp3tunes-send-to-phone-succeeded") {
				//dump("[MTProcHelper] sendToPhone(): succeeded\n");
				this.client.notification(Util.getFText("mp3tunes.notification.send-to-phone.succeeded",[phonenumber],1),this.entry);
			}
			if(topic=="mp3tunes-send-to-phone-failed") {
				Util.alertError(Util.getFText("mp3tunes.notification.send-to-phone.failed",[data],1));
			}
		}
	}
	var key=Util.getPropsString(entry,"cv-md5");
	var phonenumber=this.pref.getCharPref("phonenumber");
	this.mtMgr.sendToPhone(key,phonenumber,new SendToPhoneObserver(this,entry,phonenumber));
}

MTProcHelper.prototype.countUploads=function(entry) {
	var today=new Date();
	var date=""+today.getDate()+"-"+(today.getMonth()+1)+"-"+today.getFullYear();
	var freeLimit="0;"+date;
	try {
		freeLimit=this.pref.getCharPref("free-limit");
	} catch(e) {}
	var parts=freeLimit.split(";");
	var count=parseInt(parts[0]);
	if(date!=parts[1]) 
		count=0;
	count++;
	this.pref.setCharPref("free-limit",""+count+";"+date);
}

MTProcHelper.prototype.QueryInterface = function(iid) {
    if(
    	iid.equals(Components.interfaces.dhIConversionListener)==false &&
    	iid.equals(Components.interfaces.dhIDownloadListener)==false &&
    	iid.equals(Components.interfaces.nsISupports)==false
	) {
            throw Components.results.NS_ERROR_NO_INTERFACE;
        }
    return this;
}

function UploadFileObserver(client,entry) {
	this.client=client;
	this.entry=entry;
}

UploadFileObserver.prototype={
	observe: function(subject,topic,data) {
		if(topic=="mp3tunes-upload-file-succeeded") {
			this.client.uploadFinished(true,this.entry,null);
		}
		if(topic=="mp3tunes-upload-file-failed") {
			this.client.uploadFinished(false,this.entry,data);
		}
	}
}

