;(function (global) {

if ("EventSource" in global) return;
 
var reTrim = /^(\s|\u00A0)+|(\s|\u00A0)+$/g;

var EventSource = function (url, assync) {
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
  this._xhr = null;
  this.assync = (assync == 'undefined')?false:assync;
  
  function pollAgain(interval) {
    eventsource._pollTimer = setTimeout(function () {
      poll.call(eventsource);
    }, interval);
  }
  

  function poll() {
    try { // force hiding of the error message... insane?
      if (eventsource.readyState == eventsource.CLOSED) return;

      // NOTE: IE7 and upwards support
      var xhr = new XMLHttpRequest();
      xhr.open('GET', eventsource.URL, true);
      xhr.setRequestHeader('Accept', 'text/event-stream');
      xhr.setRequestHeader('Cache-Control', 'no-cache');
      // we must make use of this on the server side if we're working with Android - because they don't trigger 
      // readychange until the server connection is closed
      xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');

      if (lastEventId != null) xhr.setRequestHeader('Last-Event-ID', lastEventId);
      cacheLength = 0;
      data = [];
      eventType = 'message';
      xhr.timeout = 50000;
      
      xhr.ontimeout = function (e) {
          eventsource.dispatchEvent('timeout', { type: 'server' ,data: xhr.timeout });
      };
      
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
          	  xhr.abort();
              eventsource.readyState = eventsource.CONNECTING;
 			  eventsource.dispatchEvent('error', { type: 'error' });
              pollAgain(interval);
          }
          // don't need to poll again, because we're long-loading
        } else if (eventsource.readyState !== eventsource.CLOSED) {
          if (this.readyState == 4) { // and some other status
            // dispatch error
            eventsource.readyState = eventsource.CONNECTING;
            eventsource.dispatchEvent('error', { type: 'error' });
            pollAgain(interval);
          } else if (this.readyState == 0) { // likely aborted
            pollAgain(interval);
          } else {
          }
        }
      };
      xhr.send();
    
    } catch (e) { // in an attempt to silence the errors
       //dispatchFlagEventInNewThread(eventsource, 'error');
       eventsource.dispatchEvent('error', { type: 'error' });
    } 
  };
  
  poll(); // init now
};

EventSource.prototype = {
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
	}
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

if ('module' in global) module.exports = EventSource;
global.EventSource = EventSource;
 
})(this);