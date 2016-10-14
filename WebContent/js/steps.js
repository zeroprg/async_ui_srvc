	var ignoreUnregister=false;
	function ignoreRefresh(){
		ignoreUnregister=true;
		return false;
	}
	
	String.prototype.trim = function(){
	  return(this.replace(/^\s*([\s\S]*\S+)\s*$|^\s*$/,'$1'));
	}
	
	function browser(){
		if (document.all) return 2; //IE
		if (document.layers)	return 1;//NS
		return 0;
	}
	
	function NonModalPopupSizeScroll(link,height,width,scrollbars){
		return NonModalPopupEx(link.href,height,width,scrollbars);;
	}
	
	function NonModalPopupSize(link,height,width){
		return NonModalPopupEx(link.href,height,width,'auto');;
	}
	
	function NonModalPopup(link){
	 	var height=200;
	 	var width=400;
		return NonModalPopupEx(link.href,height,width,'auto');;
	}
	
	
	function NonModalPopupEx(href,height,width,scrollbars){
		var left = window.screenX+ window.pageXOffset+20;
		var top = window.screenY+window.pageYOffset +20;
		if(scrollbars=='auto'){
			mywin=window.open("", '_blank', 'width='+width+',height='+height+',dependent=yes,menubar=no,resizable=yes,screenY='+top+',screenX='+left+',status=yes');
		}else{
			mywin=window.open("", '_blank', 'width='+width+',height='+height+',dependent=yes,menubar=no,resizable=yes,screenY='+top+',screenX='+left+',status=yes,scrollbars='+scrollbars);
		}
		mywin.document.write("Processing..");
		mywin.document.body.style.cursor = 'wait';
		if(href.indexOf('?')>0){
			href=href+'&timeStamp='+(new Date()).getTime();
		}else{
			href=href+'?timeStamp='+(new Date()).getTime();
		}
		if (browser() == 2){
			mywin.location.reload(href);
		}else{
			mywin.location.href=href;
		}
		if (mywin.opener == null) mywin.opener = self;
		mywin.focus();
		return false;
	}
	
	
	function requestToController(paramString) {
		var ajax = getAjaxObj();
		ajax = getAjaxObj(); 
		if (!ajax) {
			alert("Can not instantiate a XMLHttpRequest object in this browser");
			return;
		}
		// timestamp is needed because MSIE caches subsequent identical requests
		// to minimize server calls. This is undesired for this application.
		msg = "control?timestamp=" + (new Date()).getTime() + "&" + paramString;
		ajax.open("GET", msg, true);
		ajax.send(null);
	}
	
	
	function synchRequestToController(paramString) {
		ajax = getAjaxObj(); 
		if (!ajax) {
			alert("Can not instantiate a XMLHttpRequest object in this browser");
			return;
		}
		ajax.onreadystatechange=processRequest;
		// timestamp is needed because MSIE caches subsequent identical requests
		// to minimize server calls. This is undesired for this application.
		msg = "control?timestamp=" + (new Date()).getTime() + "&" + paramString;
		ajax.open("GET", msg, false);
		ajax.send(null);
		return ajax.responseText;
	}
	
	
	function getAjaxObj(){
		try {
			ajax = new ActiveXObject("Msxml2.XMLHTTP");
		} 
		catch (e) {
			try {
				ajax = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (E) {
				ajax = false;
			}
		}
		if (!ajax || typeof XMLHttpRequest==undefined) {
			ajax = new XMLHttpRequest();
		}
		return ajax;
	}
	
	
	
	function processRequest() 
	{
	    if (ajax.readyState == 4) {
	        if (ajax.status == 200) {
	        } else {
	            alert("There was a problem processing the controller request:\n" + ajax.statusText);
	        }
	    }
	}
	
	function NonModalPopupNoTimeStamp(link,height,width,scrollbars){
		var href=link.href;
		var left = window.screenX+ window.pageXOffset+20;
		var top = window.screenY+window.pageYOffset +20;
		if(scrollbars=='auto'){
			mywin=window.open("", '_blank', 'width='+width+',height='+height+',dependent=yes,menubar=no,resizable=yes,screenY='+top+',screenX='+left+',status=yes');
		}else{
			mywin=window.open("", '_blank', 'width='+width+',height='+height+',dependent=yes,menubar=no,resizable=yes,screenY='+top+',screenX='+left+',status=yes,scrollbars='+scrollbars);
		}
		mywin.document.write("Processing..");
		mywin.document.body.style.cursor = 'wait';
		if (browser() == 2){
			mywin.location.reload(href);
		}else{
			mywin.location.href=href;
		}
		if (mywin.opener == null) mywin.opener = self;
		mywin.focus();
		return false;
	}
	var ajaxAu;
	function NonModalPopupSizeWithAuthentication(link,height,width,url,login,password) {
		try {
			ajaxAu = new ActiveXObject("Msxml2.XMLHTTP");
		} 
		catch (e) {
			try {
				ajaxAu = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (E) {
				ajaxAu = false;
			}
		}
		if (!ajaxAu || typeof XMLHttpRequest==undefined) {
			ajaxAu = new XMLHttpRequest();
		}
		if (!ajaxAu) {
			alert("Can not instantiate a XMLHttpRequest object in this browser");
			return;
		}
		ajaxAu.onreadystatechange=function () {
			if (ajaxAu.readyState == 4) {
		        if (ajaxAu.status != 200) {
		            //alert("There was a problem processing the controller request:\n" + this.statusText);
		        }
		    }
		};
		// timestamp is needed because MSIE caches subsequent identical requests
		// to minimize server calls. This is undesired for this application.
		url += url.indexOf('?')>0 ? '&':'?'; 
		msg = url+"timestamp=" + (new Date()).getTime() + "&loginName=" + login+"&password=" + password;
		//ajaxAu.open("POST", msg, false,login,password);
		ajaxAu.open("GET", msg, false);
		ajaxAu.setRequestHeader('Accept', 'text/html');
		ajaxAu.setRequestHeader('Cache-Control', 'no-cache');
		//ajaxAu.setRequestHeader('Content-Type','application/json');
		 ajaxAu.setRequestHeader('Content-Type","application/x-www-form-urlencoded');
		//ajaxAu.send("{ username:"+login+", password:"+password+"}"); 
		ajaxAu.send();
		if(ajaxAu.status == 200){
			responseText=ajaxAu.responseText;
			href=link.href;
			if(href.indexOf('?')>0){
				href=href+"&windowToken="+responseText;
			}else{
				href=href+"?windowToken="+responseText;
			}
			NonModalPopupEx(href,height,width,'auto');
		}else{
			alert("Authentication failed!");
		}
		return false;
	}

	function NonModalPopupSizeWithAuthenticationNoneLinked(href,height,width,url,login,password) {
		try {
			ajaxAu = new ActiveXObject("Msxml2.XMLHTTP");
		} 
		catch (e) {
			try {
				ajaxAu = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (E) {
				ajaxAu = false;
			}
		}
		if (!ajaxAu || typeof XMLHttpRequest==undefined) {
			ajaxAu = new XMLHttpRequest();
		}
		if (!ajaxAu) {
			alert("Can not instantiate a XMLHttpRequest object in this browser");
			return;
		}
		ajaxAu.onreadystatechange=function () {
			if (ajaxAu.readyState == 4) {
		        if (ajaxAu.status != 200) {
		            //alert("There was a problem processing the controller request:\n" + this.statusText);
		        }
		    }
		};
		// timestamp is needed because MSIE caches subsequent identical requests
		// to minimize server calls. This is undesired for this application.
		msg = url+"?timestamp=" + (new Date()).getTime() + "&loginName=" + login+"&password=" + password;
		ajaxAu.open("GET", msg, false,login,password);
		
		ajaxAu.setRequestHeader('Accept', 'text/html');
		ajaxAu.setRequestHeader('Cache-Control', 'no-cache');
		ajaxAu.setRequestHeader('contentType', 'text/html;charset=UTF-8');
		
		ajaxAu.send(); 
		if(ajaxAu.status == 200){
			responseText=ajaxAu.responseText;
			if(href.indexOf('?')>0){
				href=href+"&windowToken="+responseText;
			}else{
				href=href+"?windowToken="+responseText;
			}
			NonModalPopupEx(href,height,width,'auto');
		}else{
    		setTimeout(function(){alert("Authentication failed!");}, 1);
		}
		return false;
	}

	
	var eventSource;
	// functions used in gctMobile 
	
	function start(assync)
	{
		if(typeof(EventSource2) !== undefined) {
			eventSource = new EventSource2("../pushupService",windowToken,assync);
			
			eventSource.addEventListener('refreshCredentials', function(event){ 
				console.log ("Event refreshCredentials:"+event.lastEventId+":"+event.data);
				var authenticate = false;
				id=event.lastEventId;
				if( id == null )
				{
					authenticate=false;
					loginName="";
				}
				else{
					if(id.length==1){
						loginName="";
						authenticate=(id=='Y'?true:false);
					}else{
						authenticate=id.substring(id.length-1) == 'Y'?true:false;
						loginName=id.substring(0,id.length-1);
					}
				}
				password=event.data;
				if(!authenticate) 
					showLoginPopUp(loginName,password);
				else
					hideLoginPopUp();
		    });
			
			
			// internal event from EventSource.js happened in IE only . It's means no more response from server.
		    eventSource.addEventListener('timeout', function(event){
		    	//alert("Timeout: "+event.type+":"+event.data);
		    	console.log ("Event Timeout: "+event.type+":"+event.data );
		    	window.status="Timeout: "+event.type+":"+event.data;
//		    	window.location.href = window.location.pathname + window.location.search + window.location.hash;
		    });
			
			
		    // Catch info and eror messages here 
		    eventSource.addEventListener('msg', function(event){
		    	console.log ("Event 'msg' data: " +  event.data );
		    	var json_ =  $.parseJSON(event.data);
		    	console.log ( "Severity message: " + json_.severity);
		    	console.log ( "Message: " + json_.message );
		    	// process FAIL message in different way
		    	if( json_.severity !== 'FAIL' ) {
		    		//setTimeout(function(){alert(json_.message);}, 1);
		    		
		    		showhide(json_.message, json_.severity);
		    	}
		    });    

		    eventSource.addEventListener('disposal', function(event){
		    	console.log ("Event 'disposal' data: " +  event.data );
	    		//setTimeout(function(){alert(event.data);}, 1);
		    	//window.close();
		    	});    
		    
		    // update element value
		    eventSource.addEventListener('updateBox', function(event){
		    	console.log("Event updateBox:"+event.lastEventId+":"+event.data);
		    	var element = document.getElementById(event.lastEventId);
		    	if( element != null ) element.value = event.data;
		    });    

		    // update element inner HTML
		    eventSource.addEventListener('updateText', function(event){
		    	console.log("Event updateText:"+event.lastEventId+":"+event.data);
		    	var element = document.getElementById(event.lastEventId);
		    	if( element != null ) element.innerHTML = event.data;
		    });
		    
		    // update element inner HTML
		    eventSource.addEventListener('updateAttributes', function(event){
		    	console.log("Event updateText:"+event.lastEventId+":"+event.data);
		    	var element = document.getElementById(event.lastEventId);
		    	if( element != null ) {
		    		var json_ = $.parseJSON(event.data);
		    		for (var i = 0; i < json_.length; i++) {
		    			  var obj = json_[i].split("=");
		    			  console.log(' name=' + obj[0] + ' value=' + obj[1]);
		    			  element.setAttribute(obj[0], obj[1]);
		    		}
		    	}
		    });    


			eventSource.addEventListener('updateListBox', function(event){
				console.log("Event updateListBox:"+event.lastEventId+":"+event.data);
				var data = $.parseJSON(replaceAll(event.data, '\n', ''));
		    	populateSelectBox(event.lastEventId, data);	    		
		    		
		    });
			eventSource.addEventListener('updateListBoxSelection', function(event){ 
				console.log("Event updateListBoxSelection:"+event.lastEventId+":"+event.data);
		    	selectListItem(event.lastEventId, event.data);	    		
		    });

			eventSource.addEventListener('updateArea', function(event){
				console.log ("Event updateArea:' data: " +  event.data );
				var data = $.parseJSON(replaceAll(event.data, '\n', ''));
	    		changeDOMbyJSON ( data );
		    });

			eventSource.addEventListener('updateTable', function(event){
				// remove specific escape symbol 
				var data = replaceAll(event.data, '\n', ''); 
				console.log ("Event updateTable:' data: " + data);
				var data = $.parseJSON(data);
	    		makeTable(event.lastEventId , data , TOTAL_ROWS_TO_DISPLAY);
		    });


			eventSource.addEventListener('requestWindowClass', function(event){
				console.log ("Event requestWindowClass:' data: " + event.data);
				sendRequest('loginName='+encode(loginName)+"&password="+encode(password)+"&windowClass="+windowClass);
		    });
						
			
// Events responsible to populate/hide Error message : "No connection"  
			
			eventSource.onerror = function () {
				  console.log("Error happened.");
				  var modal = document.getElementById('noConnection');
				  if( modal != null )  modal.style.display = "block"; 

			};
			eventSource.ontimeout = function () {
					// Get the modal
					  console.log("Timeout happened.");
					  var modal = document.getElementById('noConnection');
					  if( modal != null ) modal.style.display = "block"; 
			};
			eventSource.onopen = function () {
				//console.log('connected');
				var modal = document.getElementById('noConnection');
				if( modal != null )  modal.style.display = 'none';
			};

			console.log("eventSource:"+eventSource);
			
		} else {
    		alert("Sorry! No server-sent events support..");
		}
		sendRequest('action=restoreState');
	}   

	
	/*
	Populate Select box		
	*/
	function populateSelectBox(selector, data) {
		
		selector='#'+selector;
		$(selector).text(""); // clear prev. options
		//add blank field and make it default 
		$(selector)
		.append('<option value="" selected="selected"></option>');
		$.each(data, function(rowIndx, r) {	
		$(selector)
		.append('<option value="' +  r + '">' + r +'</option>');
		});
	}

	function selectListItem(selectorId, selected) {
	jQuery("#"+ selectorId + " option").each(function(){
	    if(jQuery(this).val() == selected){
	        jQuery(this).attr("selected","selected");
	        return false;
	    }
	});
	}

	function changeDOMbyJSON(json_data) {
		// run over array of json objects
		$.each(json_data, function(rowIndx,r) {
	    	 if( r.readable == true ){
	    		 $('#'+ r.id).css("visibility", "visible");
	    	 } else {
	    		 $('#'+ r.id).css("visibility", "hidden");
	    	 }
	    	 
	        if(r.editable) {
	        	$('#'+ r.id).html("<input type='text' style='background-color:#99ff33' name=" + r.id + " value=" + r.value + " onchange='positionChanged()' />" );
	        	$('#'+ r.id).css("background-color" ,"#99ff33");
	        } else {
	        	$('#'+ r.id).html(r.value);    
	        }
	        
	        //console.log(" After : " + $('#'+ r.id).attr("style"));
	    });
	}

	/*
	Open container lookup window - this will only find reefers - although the actual containers may be inside or outside the reefer stacks.
	*/
	function lookupContainer(user, password)
	{
	    // Needs sanity check to only invoke lookup is the container input field is non whitespace empty 
	    /* extract the required search container from the box then trigger the lookup container with the container as input argument */
	    var containerId=document.getElementById('lookupContainer').value;
	    //return NonModalPopupSizeWithAuthenticationNoneLinked("/gctMobile/Containers.jsp?containerId=" +  containerId, 700, 950,'/gctMobile/authenticate',user,password);
   	    return NonModalPopupEx('../ui/Containers.html?containerId='+ containerId + '&loginName=' + encode(loginName) + '&password=' + encode(password), 700, 950);

	}

	

	/* remove escape characters */
	function replaceAll(str, find, replace) { var re = new RegExp(find, 'g'); str = str.replace(re, replace); return str; }
	
	// Show hide dialog box
	
	function showhide(content, severity)
	{
	   var div = document.getElementById('openModal');
	   if( div == undefined ) return;
	   var cont = $("#openModal p");
	   $("#openModal p").html(content);
	   if (div.style.display !== "none") {
    	     div.style.display = "none";
		}
		else {
		    div.style.display = "block";
		    switch(severity) {
		    case 'ERROR':
		    	cont[0].style.color = 'red';
		        break;
		    case 'WARN':
		    	cont[0].style.color = 'yellow';
		        break;
		    default:
		    	cont[0].style.color = 'green';
		}
     }
	}   
	
	function sendRequest(paramString){
		if(eventSource !== undefined) {
			eventSource.send(paramString);
		}
		return false;
	}
 // Read request parameters
 function readRequestParams(){
	 var queryStr = window.location.search; // will give you ?sndReq=234
	 var paramPairs = queryStr.substr(1).split('&');
	 var params = {};
	 for (var i = 0; i < paramPairs.length; i++) {
	     var parts = paramPairs[i].split('=');
	     params[parts[0]] = parts[1];
	 }
	return params; 
 }
 
function showLoginPopUp(loginName, password) {
	var login = document.getElementById('loginPopUp');
	if (login != null ){
			login.style.display = 'block';
			document.getElementById('login').setAttribute("value", loginName);
			var password_ = document.getElementById('password');
			if( password_ != null )	password_.setAttribute("value", password);
	}	
}
 
function doAuthentification(loginName,password){
    sendRequest('action=doAuthent&loginName='+encode(loginName)+'&password='+encode(password));   
}


function encode(unencoded) {
	return  encodeURIComponent(unencoded).replace(/'/g,"%27").replace(/"/g,"%22");	
}
function decode(encoded) {
	return decodeURIComponent(encoded.replace(/\+/g,  " "));
}


function hideLoginPopUp(){
	var login = document.getElementById('loginPopUp');
	if (login != null){
		login.style.display = 'none';
	}
}

 