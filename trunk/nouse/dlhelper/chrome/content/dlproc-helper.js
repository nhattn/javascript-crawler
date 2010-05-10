/******************************************************************************
 *            Copyright (c) 2006-2009 Michel Gutierrez. All Rights Reserved.
 ******************************************************************************/

/**
* Object constructor
*/
function DLProcHelper() {
	try {
		//dump("[DLProcHelper] constructor\n");
		var prefService=Components.classes["@mozilla.org/preferences-service;1"]
		                                   .getService(Components.interfaces.nsIPrefService);
		this.pref=prefService.getBranch("dwhelper.");
		var prefService=Components.classes["@mozilla.org/preferences-service;1"]
		                                   .getService(Components.interfaces.nsIPrefService);
		this.pref=prefService.getBranch("dwhelper.");
		this.dlMgr=Components.classes["@downloadhelper.net/download-manager;1"]
			                        	.getService(Components.interfaces.dhIDownloadMgr);
		this.cvMgr=Components.classes["@downloadhelper.net/convert-manager-component"]
		              					.getService(Components.interfaces.dhIConvertMgr);
		this.smartNamer = Components.classes["@downloadhelper.net/smart-namer;1"]
		                                .getService(Components.interfaces.dhISmartNamer);
	} catch(e) {
		dump("[DLProcHelper] !!! constructor: "+e+"\n");
	}
}

DLProcHelper.prototype = {}

DLProcHelper.prototype.canHandle=function(desc) {
	//dump("[DLProcHelper] canHandle()\n");
	if(desc.has("media-url") || desc.has("links"))
		return true;
	else
		return false;
}

DLProcHelper.prototype.requireDownload=function(desc) {
	//dump("[DLProcHelper] requireDownload()\n");
	return desc.has("media-url");
}

DLProcHelper.prototype.preDownload=function(desc,promptDownload,promptConversion) {
	try {
		//dump("[DLProcHelper] preDownload()\n");
		var filename=Util.getPropsString(desc,"file-name");
		if(filename==null)
			filename="dwhelper-video.flv";
		if(desc.has("format"))
			desc.undefine("format");
		var format=this.cvMgr.getFormat(filename,Util.getPropsString(desc,"media-url"),Util.getPropsString(desc,"page-url"));
		if(promptConversion) {
		    var windowMediator = Components.classes["@mozilla.org/appshell/window-mediator;1"]
		    		                                .getService(Components.interfaces.nsIWindowMediator);
   			var window = windowMediator.getMostRecentWindow("navigator:browser");
			var data={
					format: format
			};
			window.openDialog("chrome://dwhelper/content/convert-manual.xul",
		                 "dwhelper-convert-manual", "chrome,centerscreen,modal",data);
			format=data.format;
		} 
		if(format) {
			Util.setPropsString(desc,"format",format);
			var ext=/^(.*?)\//.exec(format)[1];
			filename=/^(.*?)(?:\.[^\.]{1,5})?$/.exec(filename)[1]+"."+ext;
			if(desc.has("dl-file"))
				desc.undefine("dl-file");
		}
		var file=this.dlMgr.getDownloadDirectory();
		file.append(filename);
		try {
			file.createUnique(Components.interfaces.nsIFile.NORMAL_FILE_TYPE, 0644);
		} catch(e) {
			Util.alertError(Util.getFText("error.cannot-create-target-file",[file.path],1));
			return false;
		}

	 	if(promptDownload) {
		 	file.remove(true);
		 	
		    var windowMediator = Components.classes["@mozilla.org/appshell/window-mediator;1"]
		                                .getService(Components.interfaces.nsIWindowMediator);
			var window = windowMediator.getMostRecentWindow("navigator:browser");
			var saveFilePicker=Components.classes["@mozilla.org/filepicker;1"]
			                                      .createInstance(Components.interfaces.nsIFilePicker);
			saveFilePicker.init(window, Util.getText("title.save-file"), 
					Components.interfaces.nsIFilePicker.modeSave);
			saveFilePicker.displayDirectory=file.parent;
			saveFilePicker.defaultString=file.leafName;
			saveFilePicker.appendFilters(Components.interfaces.nsIFilePicker.filterAll);
		 	var orgLeafName=file.leafName;
			var rs=saveFilePicker.show();
			if(rs==Components.interfaces.nsIFilePicker.returnCancel) {
				return false;
			}
			file=saveFilePicker.file;
			if(file.leafName==orgLeafName)
				this.smartNamer.incrNameStat(desc,"keep");
			else
				this.smartNamer.incrNameStat(desc,"nkeep");
			this.dlMgr.setDownloadDirectory(file.parent);
	 	}

		if(format) {
			if(this.cvMgr.checkConverter(true)==false) {
				if(file.exists())
					file.remove(true);
				return false;
			}
			desc.set("cv-file",file);
	 	} else {
		 	desc.set("dl-file",file);
	 	}
		
	 	return true;
	} catch(e) {
		dump("!!! [DLProcHelper] preDownload(): "+e+"\n");
		return false;
	}
}

DLProcHelper.prototype.handle=function(desc,promptDownload) {
	//dump("[DLProcHelper] handle()\n");
	try {
		if(!desc.has("media-url")) {
			//dump("[DLProcHelper] handle inside()\n");
			var dir=this.dlMgr.getDownloadDirectory();
			var dirFileName=Util.getPropsString(desc,"file-name");
			if(dirFileName && dirFileName.length>0)
				dir.append(dirFileName);
			else
				dir.append("medialink");
		 	dir.createUnique(Components.interfaces.nsIFile.DIRECTORY_TYPE, 0755);
		 	
		 	if(promptDownload) {
			 	dir.remove(true);
	
			    var windowMediator = Components.classes["@mozilla.org/appshell/window-mediator;1"]
			        	                                .getService(Components.interfaces.nsIWindowMediator);
	    		var window = windowMediator.getMostRecentWindow("navigator:browser");
	    		var saveFilePicker=Components.classes["@mozilla.org/filepicker;1"]
	    		                                      .createInstance(Components.interfaces.nsIFilePicker);
	    		saveFilePicker.init(window, Util.getText("title.save-file"), 
	    				Components.interfaces.nsIFilePicker.modeSave);
	    		saveFilePicker.displayDirectory=dir.parent;
	    		saveFilePicker.defaultString=dir.leafName;
	    		saveFilePicker.appendFilters(Components.interfaces.nsIFilePicker.filterAll);
	
	    		var rs=saveFilePicker.show();
	    		if(rs==Components.interfaces.nsIFilePicker.returnCancel) {
	    			return;
	    		}
	    		var dir=saveFilePicker.file;
	    		if(dir.exists())
	    			dir.remove(true);
			 	dir.createUnique(Components.interfaces.nsIFile.DIRECTORY_TYPE, 0755);
	    		this.dlMgr.setDownloadDirectory(dir.parent);
		 	}
		 	
		 	var links=desc.get("links",Components.interfaces.nsIArray);

		 	var doIndexPrefix=true;
			var indexPrefix=0;
			try {
				doIndexPrefix=this.pref.getBoolPref("medialink-index-prefix");
			} catch(e) {}
			if(links.length<2)
				doIndexPrefix=false;

		 	var i=links.enumerate();
		 	while(i.hasMoreElements()) {
		 		var entry=i.getNext().QueryInterface(Components.interfaces.nsIProperties);
		 		var mediaUrl=Util.getPropsString(entry,"media-url");
		    	var fileName=/.*\/(.*?)$/.exec(mediaUrl)[1];
				if(doIndexPrefix) {
					indexPrefix++;
					var prefix="0000".substring(0,4-(""+indexPrefix).length)+indexPrefix;
					fileName=prefix+"-"+fileName;
				}
				var file=dir.clone();
				file.append(fileName);
				entry.set("dl-file",file);
				Util.setPropsString(entry,"label",fileName);
				this.dlMgr.download(null,entry,null);
		 	}
		}
	} catch(e) {
		dump("!!! [QDLProcHelper] handle(): "+e+"\n");
	}
}
