;(function (global) {

	if ("EventSource2" in global) return;

	var reTrim = /^(\s|\u00A0)+|(\s|\u00A0)+$/g;

	var EventSource2 = function (url,windowToken, assync) {
	  var eventsource = this,  
	      interval = 500, // polling interval  
	      lastEventId = null,
	      cacheLength = 0,
	      data = [],
	      eventType='message';

	  if (!url || typeof url != 'string') {
	    throw new SyntaxError('Not enough arguments');
	  }

	  this.URL = url;
	  this.readyState = this.CONNECTING;
	  this._pollTimer = null;
	  this.windowToken=windowToken;
	  this.assync = (assync == undefined)?true:assync;
	  this._xhr = null;
	  this._queryToSend=this.queryToSend;
	  this._windowToken=this.windowToken;
	  
	  

	  
	  
	  function pollAgain(interval) {
	    eventsource._pollTimer = setTimeout(function () {
	    	eventsource.readyState =eventsource.CONNECTING;	
	    	poll.call();
	    }, interval);
	  }
	  
	  function poll() {
	    try { // force hiding of the error message... insane?
	      if (eventsource.readyState == eventsource.CLOSED) return;

	      // NOTE: IE7 and upwards support
	      var xhr = new XMLHttpRequest();
	      xhr.open('POST', eventsource.URL, true);
	      xhr.setRequestHeader('Accept', 'text/event-stream');
	      xhr.setRequestHeader('Cache-Control', 'no-cache');
	      // we must make use of this on the server side if we're working with Android - because they don't trigger 
	      // readychange until the server connection is closed
	      xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
	      xhr.setRequestHeader('Content-Type','application/x-www-form-urlencoded');

	      if (lastEventId != null) xhr.setRequestHeader('Last-Event-ID', lastEventId);
	      cacheLength = 0;
	      data = [];
	      eventType = 'message';
	      xhr.timeout = 10000;
	      
	      xhr.ontimeout= function () {
		       	eventsource.dispatchEvent('timeout', { type: 'timeout' ,data: this.timeout});
	      }
	      xhr.onreadystatechange = function () {
	    	  //console.log('onreadystatechange: readyState=' + this.readyState+', status='+this.status);
	    	 if (this.readyState == 3 || (this.readyState == 4 && this.status == 200)) {
	          // on success
	          if (eventsource.readyState == eventsource.CONNECTING) {
	            eventsource.readyState = eventsource.OPEN;
	            eventsource.dispatchEvent('open', { type: 'open' });
	          }

	          var responseText = '';
	 
	          try {
	            responseText = this.responseText || '';
	          } catch (e) {}
	          // process this.responseText
	          var parts = responseText.substr(cacheLength).split("\n"),
	              i = 0,
	              line = '';
	            
	          cacheLength = responseText.length;
	          // TODO handle 'event' (for buffer name), retry
	          for (; i < parts.length; i++) {
	            line = parts[i].replace(reTrim, '');
	            if (line.indexOf('event') == 0) {
	                eventType = line.replace(/event:?\s*/, '');
	            } else if (line.indexOf('retry') == 0) {                           
	             retry = parseInt(line.replace(/retry:?\s*/, ''));
	              if(!isNaN(retry)) { interval = retry; }
	            } else if (line.indexOf('data') == 0) {
	              data.push(line.replace(/data:?\s*/, ''));
	            } else if (line.indexOf('id:') == 0) {
	               lastEventId = line.replace(/id:?\s*/, '');
	               //console.log("lastEventId : " + lastEventId );
	            } else if (line.indexOf('id') == 0) { // this resets the id
	              lastEventId = null;
	            } else if (line == '' ||(this.readyState == 4 && i==parts.length-1)) {
	               if (data.length) {
	            	  
	                var event = new MessageEvent(data.join('\n'), eventsource.url, lastEventId);
	                eventsource.dispatchEvent(eventType, event);
	                //console.log("lastEventId : " + lastEventId +" data:" + data);
	                data = [];
	                eventType = 'message'; 
	                
	              }
	            } else{
	            	data.push(line);
	            }
	          }

	          if (this.readyState == 4){
	              eventsource.readyState = eventsource.CLOSED;
	        	  this.abort();
	        	  eventsource.dispatchEvent('close', { type: 'close' });
	              pollAgain(interval);
	          }
	          // don't need to poll again, because we're long-loading
	        } else if (eventsource.readyState !== eventsource.CLOSED) {
	        	//console.log("eventsource.readyState="+eventsource.readyState+":xhr.readyState="+this.readyState);
	          if (this.readyState == 4 ||this.readyState == 0) { // and some other status than 200
	              eventsource.readyState = eventsource.CLOSED;
	        	  this.abort();
	        	  eventsource.dispatchEvent('error', { type: 'error' });
	              pollAgain(interval);
	          }
	        }
	      };
	      paramString='';
	      if(eventsource.windowToken!=''){
	    	  paramString="windowToken="+eventsource.windowToken;
	      }
	      if(eventsource.queryToSend.length>0){
	      	if(paramString==''){
	    	    paramString=eventsource.queryToSend.splice(0, 1);
	        }else{
	        	paramString=paramString+"&"+eventsource.queryToSend.splice(0, 1);
	        	console.log(paramString);
	        }
	      }
	      xhr.send(paramString);
	      eventsource._xhr = xhr;
	    
	    } catch (e) { // in an attempt to silence the errors
	    	eventsource.dispatchEvent('error', { type: 'error' });
	    } 
	  };
	  
	  poll(); // init now
	};

	EventSource2.prototype = {
	  close: function () {
	    // closes the connection - disabling the polling
	    this.readyState = this.CLOSED;
	    clearInterval(this._pollTimer);
	    this._xhr.abort();
	  },
	  CONNECTING: 0,
	  OPEN: 1,
	  CLOSED: 2,
	  dispatchEvent: function (type, event) {
	    var handlers = this['_' + type + 'Handlers'];
	    if (handlers) {
	      for (var i = 0; i < handlers.length; i++) {
	    	  if(this.assync){
	    		  this.assyncCall(this,handlers[i], event);
	    	  } else { 
	    		  try{ handlers[i].call(this, event);   }catch (err){   this.throwException(err);	}	 
	    	  }
	      }
	    }

	    if (this['on' + type]) {
		  	  if(this.assync){
				  this.assyncCall(this,this['on' + type], event);
			  } else { 
				  try{ this['on' + type].call(this, event);  }catch (err){   this.throwException(err);	}	 
			  }
	    }
	  },
	  addEventListener: function (type, handler) {
	    if (!this['_' + type + 'Handlers']) {
	      this['_' + type + 'Handlers'] = [];
	    }
	    
	    this['_' + type + 'Handlers'].push(handler);
	  },
	  removeEventListener: function (type, handler) {
	    var handlers = this['_' + type + 'Handlers'];
	    if (!handlers) {
	      return;
	    }
	    for (var i = handlers.length - 1; i >= 0; --i) {
	      if (handlers[i] === handler) {
	        handlers.splice(i, 1);
	        break;
	      }
	    }
	  },
	  onerror: null,
	  onmessage: null,
	  onopen: null,
	  ontimeout: null,
	  readyState: 0,
	  URL: '',
	  assync: false,
	  throwException: function (e){
			setTimeout(	(function(opts){
											console.log("e = " + opts.name);
			               					return function(){ 	throw(opts.name);}
			            				 })({name:e}), 0);  
		},
		
	  assyncCall: function (currentObj, object, event){
			var parameter = {_this: currentObj, eventsource: object, event: event};
			setTimeout( (function(opts){
				 							//console.log("opts.name.eventsource = " + opts.name.eventsource +  " , opts.name.event =" + opts.name.event);
					      					return function(){opts.name.eventsource.call(opts.name._this, opts.name.event);}
										 })({name:parameter}), 0);  
		},
	  queryToSend: [],
      send: function(paramString){
		   if(this.queryToSend.length>0){
			   if(this.queryToSend.slice(0, 1)==paramString) return;
		   }
		   
		   this.contentType  = "application/x-www-form-urlencoded;charset=UTF-8";
		   
		   this.queryToSend.push(paramString);
	   },
	   sendNow: function(paramString){
		   if(this.queryToSend.length>0){
			   if(this.queryToSend.slice(-1)==paramString) return;
		   }
		   this.queryToSend.splice(0, 0,paramString);
	   },
  	   windowToken: ''
	};

	var MessageEvent = function (data, origin, lastEventId) {
	  this.data = data;
	  this.origin = origin;
	  this.lastEventId = lastEventId || '';
	};

	MessageEvent.prototype = {
	  data: null,
	  type: 'message',
	  lastEventId: '',
	  origin: ''
	  
	};

	if ('module' in global) module.exports = EventSource2;
	global.EventSource2 = EventSource2;
	 
	})(this);
