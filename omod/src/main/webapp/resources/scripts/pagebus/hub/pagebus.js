/**
 * TIBCO PageBus(TM) version 2.0.0
 * 
 * Copyright (c) 2006-2009, TIBCO Software Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 *
 *
 * Includes code from the official reference implementation of the OpenAjax
 * Hub that is provided by OpenAjax Alliance. Specification is available at:
 *
 *  http://www.openajax.org/member/wiki/OpenAjax_Hub_Specification
 *
 * Copyright 2006-2009 OpenAjax Alliance
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 *
 ******************************************************************************/

if(!window["PageBus"])
	window.PageBus = {};
 
// Insert a debugger breakpoint in Dev builds of PageBus only. The debugger line should be removed in production builds.
PageBus._debug = function() {
	// debugger; // REMOVE ON BUILD
};

PageBus._esc = function(s) {
	return s.replace(/\./g,"!");
};

PageBus._assertPubTopic = function(topic) {
    if ((topic == null) || (topic == "") || (topic.indexOf("*") != -1) || (topic.indexOf("..") != -1) || 
        (topic.charAt(0) == ".") || (topic.charAt(topic.length-1) == "."))
    {
        throw new Error(OpenAjax.hub.Error.BadParameters);
    }
};

PageBus._assertSubTopic = function(topic) {
	if((topic == null) || (topic == ""))
		throw new Error(OpenAjax.hub.Error.BadParameters);
    var path = topic.split(".");
    var len = path.length;
    for (var i = 0; i < len; i++) {
        var p = path[i];
        if ((p == "") ||
           ((p.indexOf("*") != -1) && (p != "*") && (p != "**"))) {
            throw new Error(OpenAjax.hub.Error.BadParameters);
        }
        if ((p == "**") && (i < len - 1)) {
            throw new Error(OpenAjax.hub.Error.BadParameters);
        }
    }
};

PageBus._copy = function(obj) {
	var c;
	if( typeof(obj) == "object" ) {
		if(obj == null)
			return null;
		else if(obj.constructor == Array) {
			c = [];
			for(var i = 0; i < obj.length; i++)
				c[i] = PageBus._copy(obj[i]);
			return c;
		}
		else if(obj.constructor == Date) {
			c = new Date();
			c.setDate(obj.getDate());
			return c;
		}
		c = {};
		for(var p in obj) 
			c[p] = PageBus._copy(obj[p]);
		return c;
	}
	else {
		return obj;
	}
};

PageBus._TopicMatcher = function() {
	this._items = {};
};

PageBus._TopicMatcher.prototype.store = function( topic, val ) {
    var path = topic.split(".");
    var len = path.length;
    _recurse = function(tree, index) {
        if (index == len)
    		tree["."] = { topic: topic, value: val };
        else { 
            var token = path[index];
            if (!tree[token])
                tree[token] = {}; 
            _recurse(tree[token], index + 1);
        }
    };
    _recurse( this._items, 0 );
};

PageBus._TopicMatcher.prototype.match = function( topic, exactMatch ) {
    var path = topic.split(".");
    var len = path.length;
	var res = [];
    _recurse = function(tree, index) {
    	if(!tree)
    		return;
    	var node;
        if (index == len)
            node = tree;
        else {	
            _recurse(tree[path[index]], index + 1);
            if(exactMatch)
            	return;
            if(path[index] != "**") 
            	_recurse(tree["*"], index + 1);
            node = tree["**"];
        }
        if ( (!node) || (!node["."]) )
        	return;
        res.push(node["."]);
    };
    _recurse( this._items, 0 );
    return res;
};

PageBus._TopicMatcher.prototype.exists = function( topic, exactMatch ) {
    var path = topic.split(".");
    var len = path.length;
	var res = false;
    _recurse = function(tree, index) {
    	if(!tree)
    		return;
    	var node;
        if (index == len)
            node = tree;
        else {	
            _recurse(tree[path[index]], index + 1);
            if(res || exactMatch)
            	return;
            if(path[index] != "**") {
            	_recurse(tree["*"], index + 1);
	            if(res)
	            	return;
            }
            node = tree["**"];
        }
        if ( (!node) || (!node["."]) )
        	return;
        res = true;
    };
    _recurse( this._items, 0 );
    return res;
};

PageBus._TopicMatcher.prototype.clear = function( topic ) {
    var path = topic.split(".");
    var len = path.length;
    _recurse = function(tree, index) {
    	if(!tree)
    		return;
        if (index == len) {
            if (tree["."])
            	delete tree["."];
        }
        else {	
        	_recurse(tree[path[index]], index + 1);
            for(var x in tree[path[index]]) {
            	return;
            }
        	delete tree[path[index]];
        }
    };
    _recurse( this._items, 0 );
};

PageBus._TopicMatcher.prototype.wildcardClear = function( topic ) {
    var path = topic.split(".");
    var len = path.length;    
    _clean = function(node, tok) {
		for(m in node[tok])
			return;
		delete node[tok];
    };
    _recurse = function(tree, index) {
    	if(!tree)
    		return;
    	
        if (index == len) {	
        	if (tree["."])
        		delete tree["."];
        	return;
        }
        else {	
        	var tok = path[index];
        	var n;        	
        	if(tree[tok]) {	
        		_recurse(tree[tok], index + 1);
        		_clean(tree, tok);
	        }
        	if(tok == "*") {
            	for(n in tree) {
            		if(( n != "**" ) && (n != ".") ) {
            			_recurse(tree[n], index + 1);
            			_clean(tree, n);
            		}
            	}
            } 
            else if(tok == "**") {
            	for(n in tree) {
            		delete tree[n];
            	}
            }
        }
        return;
    };
    _recurse( this._items, 0 );
};

PageBus._TopicMatcher.prototype.wildcardMatch = function( topic ) {
    var path = topic.split(".");
    var len = path.length;
    var res = [];
	_recurse = function( tree, index ) {
		var tok = path[index];
		var node;
		if( (!tree) || (index == len) )
			return;		
		if( tok == "**" ) {
			for( var n in tree ) {
				if( n != "." ) {
					node = tree[n];
					if( node["."] )
						res.push( node["."] );
					_recurse( node, index );
				}
			}
		}
		else if( tok == "*" ) {
			for( var n in tree ) {
				if( (n != ".") && (n != "**") ){
					node = tree[n];
					if( index == len - 1 ) {
						if( node["."] )			
							res.push( node["."] );
					}
					else
						_recurse( node, index + 1 );
				}
			}
		} 
		else {
			node = tree[tok];
			if(!node)
				return;
			if( index == len - 1 ) {
				if( node["."] )
					res.push( node["."] );
			}
			else 
				_recurse( node, index + 1 );
		}
	};
    _recurse( this._items, 0 );
    return res;
};


////////////////////////////////////////////////////////////////////////////////////


PageBus.policy = {
	Ops: {
		Publish: "p",
		Subscribe: "s"
	},
	Error: {
		BadParameters: "PageBus.policy.Error.BadParameters"
	},
	_assertName: function(topic) {
		if((topic == null) || (topic == ""))
			throw new Error(OpenAjax.hub.Error.BadParameters);
	    var path = topic.split(".");
	    var len = path.length;
	    for (var i = 0; i < len; i++) {
	        var p = path[i];
	        if ((p == "") ||
	           ((p.indexOf("*") != -1) && (p != "*") && (p != "**"))) {
	            throw new Error(PageBus.policy.Error.BadParameters);
	        }
	        if ((p == "**") && (i < len - 1)) {
	            throw new Error(PageBus.policy.Error.BadParameters);
	        }
	    }
	}
};

PageBus.policy.HubPolicy = function( params ) {
	if(!params)
		params = {};
	this._cfg = params;
	this._log = params["log"];
	this._topicMgr = new PageBus._TopicMatcher();	
};

PageBus.policy.HubPolicy.prototype.onPublish = function( topic, data, pc, sc ) {
	var res = true;
	var origin;
	if(sc != null) {
		origin = sc.getPartnerOrigin();
		if(!origin)
			return false;
		res = this.isAllowed.call(this, origin, PageBus.policy.Ops.Subscribe, topic);
	}
	if( res && (pc != null) ) {
		origin = pc.getPartnerOrigin();
		if(!origin)
			return false;
		res = this.isAllowed.call(this, origin, PageBus.policy.Ops.Publish, topic);
	}
	if(this._log) {
		var sid = sc ? sc.getClientID() : "(Mgr)";
		var pid = pc ? pc.getClientID() : "(Mgr)";
		this._log( "(PageBus.policy) [" + pid + ", " + sid + "] onPublish: " + 
				(res ? "ALLOWED " : "DENIED  ") + topic );		
	}
	return res;
};

PageBus.policy.HubPolicy.prototype.onSubscribe = function( topic, sc ) {
	var res = true;
	var origin;
	if(sc != null) {
		origin = sc.getPartnerOrigin();
		if(!origin)
			return false;
		res = this.isAllowed.call(this, origin, PageBus.policy.Ops.Subscribe, topic);
	}
	if(this._log) {
		var cid = sc ? sc.getClientID() : "(Mgr)";
		this._log("(PageBus.policy) [" + cid + "] onSubscribe: " + 
				(res ? "ALLOWED " : "DENIED  ") + topic );
	}
	return res;
};

PageBus.policy.HubPolicy.prototype.onUnsubscribe = function( topic, sc ) { };

PageBus.policy.HubPolicy.prototype.onSend = function( topic, data, origin) {
	var res = this.isAllowed(origin, PageBus.policy.Ops.Subscribe, topic);
	if(this._log)
		this._log("(PageBus.policy) [" + origin + "] onSend: " + (res ? "ALLOWED " : "DENIED  ") + topic);
	return res;
};

PageBus.policy.HubPolicy.prototype.onReceive = function( topic, data, origin) {
	var res = this.isAllowed(origin, PageBus.policy.Ops.Publish, topic);
	if(this._log)
		this._log("(PageBus.policy) [" + origin + "] onReceive: " + (res ? "ALLOWED " : "DENIED  ") + topic);
	return res;
};

PageBus.policy.HubPolicy.prototype._getMyOrigin = function() {
	var o = window.location.href.match(/[^:]*:\/\/[^:\/\?#]*/);
	return o[0];
};

PageBus.policy.HubPolicy.prototype.grant = function( origin, op, name ) {
	if( (!origin) || (!op) || (!name) )
		throw new Error(PageBus.policy.Error.BadParameters);
	var t = PageBus._esc(origin) + "." + op + "." + name;
	PageBus.policy._assertName(t);
	this._topicMgr.store(t, { dm: origin, op: op, tp: name });	
	if(this._log)
		this._log("(PageBus.policy) [" + origin + "] grant: " + op + " on " + name);
	var cacheName = "_pagebus.cache.s." + name;
	this._topicMgr.store(PageBus._esc(origin) + "." + op + "." + cacheName, { dm: origin, op: op, tp: cacheName });
	if(this._log)
		this._log("(PageBus.policy) [" + origin + "] implicit grant: " + op + " on " + cacheName);
};

PageBus.policy.HubPolicy.prototype.revoke = function( origin, op, name ) {
	if( (!origin) || (!op) || (!name) )
		throw new Error(PageBus.policy.Error.BadParameters);
	var t = PageBus._esc(origin) + "." + op + "." + name;
	PageBus.policy._assertName(t);
	this._topicMgr.clear(t);
	if(this._log)
		this._log("(PageBus.policy) [" + origin + "] revoke: " + op + " on " + name);
	var cacheName = "_pagebus.cache.s." + name;
	this._topicMgr.clear(PageBus._esc(origin) + "." + op + "." + cacheName);
	if(this._log)
		this._log("(PageBus.policy) [" + origin + "] implicit revoke: " + op + " on " + cacheName);
};

PageBus.policy.HubPolicy.prototype.revokeAll = function( origin ) {
	if( (!origin) )
		throw new Error(PageBus.policy.Error.BadParameters);
	this._topicMgr.wildcardClear(PageBus._esc(origin) + ".**");
	if(this._log)
		this._log("(PageBus.policy) [" + origin + "] revokeAll");
};

PageBus.policy.HubPolicy.prototype.isAllowed = function( origin, op, name ) {
	if((!origin) || (!op) || (!name))
		throw new Error(PageBus.policy.Error.BadParameters);
	var t = PageBus._esc(origin) + "." + op + "." + name;
	return this._topicMgr.exists(t, false);
};

PageBus.policy.HubPolicy.prototype.listAllowed = function( origin, op ) {
	if( (!origin) || (!op) )
		throw new Error(PageBus.policy.Error.BadParameters);
	var qr = this._topicMgr.wildcardMatch(PageBus._esc(origin) + "." + op + ".**");
	var res = [];
	for(var r in qr) {
		if(qr[r].value.tp.substring(0,9) != "_pagebus.")
			res.push(qr[r].value.tp);
	}
	return res;	
};


PageBus.cache = {};

PageBus.cache.Error = {
	// This topic is not being cached by the local hub instance
	NoCache: "PageBus.cache.Error.NoCache"	
};

PageBus._cache = {};

PageBus._cache.isCacheable = function( subData ) {
	return ( (subData) && (typeof subData == "object") && (subData["PageBus"]) && (subData.PageBus["cache"]) );
};

PageBus._cache.Cache = function() {
	this._refs = {};
	this._doCache = new PageBus._TopicMatcher();
	this._caches = new PageBus._TopicMatcher();
};

PageBus._cache.Cache.prototype.add = function( topic, subID ) {
	var dc;
	var dca = this._doCache.match(topic, true);
	if(dca.length > 0) 
		dc = dca[0].value;
	else {
		dc = { rc: 0 };
		this._doCache.store(topic, dc);
	}
	dc.rc++;
	this._refs[subID] = topic;
};

PageBus._cache.Cache.prototype.remove = function( subID ) {
	var topic = this._refs[subID];
	if(!topic)
		return;
	delete this._refs[subID];
	var dca = this._doCache.match(topic, true);
	if(dca.length == 0) 
		return;	
	dca[0].value.rc--;
	if(dca[0].value.rc == 0) {			
		this._doCache.clear(topic);
		var caches = this._caches.wildcardMatch(topic);
		for(var i = 0; i < caches.length; i++) {
			if( !(this._doCache.exists(caches[i].topic, false)) )
				this._caches.clear(caches[i].topic);
		}
	}
};

PageBus._cache.Cache.prototype.storeCopy = function( topic, value ) {
	PageBus._assertPubTopic(topic);
	var copy = PageBus._copy(value);
	this._caches.store(topic, copy);
};

PageBus._cache.Cache.prototype.clear = function( topic, value ) {
	PageBus._assertPubTopic(topic);
	this._caches.clear(topic);
};

PageBus._cache.Cache.prototype.query = function( topic ) {
	PageBus._assertSubTopic(topic); 
	return this._caches.wildcardMatch(topic);
};

PageBus._cache.Cache.prototype.isCaching = function( topic ) {
	return this._doCache.exists(topic, false);
};


PageBus._enableMH = function() {
	var MHClass = OpenAjax.hub.ManagedHub;	
	OpenAjax.hub.ManagedHub = function( params ) {
		if(!params) {
			throw new Error(OpenAjax.hub.Error.BadParameters);
		}
		if( (!params.onPublish) || (!params.onSubscribe) ) {
			throw new Error(OpenAjax.hub.Error.BadParameters);
		}		
		var defaultParams = {};
		var defaultPolicy = null;
		if(!params["PageBus"]) 
			params.PageBus = {};
		if(!params.PageBus["policy"]) 
			params.PageBus.policy = defaultPolicy;
		if(!params["scope"])
			params.scope = window;		
		this._pagebus = {
			_params: {},
			_getActualParameters: function() { return this._pagebus._params; },
			_policy: params.PageBus.policy,
			_hub: this
		};

		// newly added 
		this.pagebus = { _hub: this };
		
		var pb = this._pagebus;
		pb._cache = new PageBus._cache.Cache();
		this.pagebus.query = function( topic ) {
			return this._hub._pagebus._cache.query( topic );
		};
		this.pagebus.store = function( topic, data ) {
			if(this._hub._pagebus._cache.isCaching(topic)) 
				this._hub.publish( topic, data );
		};
		this.pagebus.clear = function( topic ) {
			if(this._hub._pagebus._cache.isCaching(topic)) 
				this._hub.publish( topic, null );
		};
		var appScope = params.scope ? params.scope : window;
		var params2 = this._pagebus._params;
		for(var pn in params) {
			params2[pn] = params[pn];
		}
		params2.scope = params.scope ? params.scope : window;
		params2.onPublish = function(topic, data, pcont, scont) {
			try {
				var pol = params2.PageBus.policy;
				if(pol) {
					var res = pol.onPublish.call(pol, topic, data, pcont, scont);
					if(!res)
						return false;
				}
				return params.onPublish.call(appScope, topic, data, pcont, scont);
			}
			catch(e) {
				return false;
			}
		};
		params2.onSubscribe = function(topic, scont) {
			try {
				var pol = params2.PageBus.policy;
				if(pol) {
					var res = pol.onSubscribe.call(pol, topic, scont);
					if(!res)
						return false;
				}
				res = params.onSubscribe.call(appScope, topic, scont);
				return res;
			}
			catch(e) {
				return false;
			}
		};
		params2.onUnsubscribe = function(topic, scont) {
			try {
				var pol = params2.PageBus.policy;
				if(pol) {
					pol.onUnsubscribe.call(pol, params.scope, topic, scont);
				}
				params.onUnsubscribe.call(appScope, topic, scont);
			}
			catch(e) {
				return;
			}
		};
		if(!pb._policy) {			
			pb._policy = null; // new PageBus.policy.HubPolicy(params);
		}		
		MHClass.call( this, params2 );
		this.getParameters = function() { return params; };
	};
	OpenAjax.hub.ManagedHub.prototype = MHClass.prototype;	
	var p = OpenAjax.hub.ManagedHub.prototype.publish;
	var s = OpenAjax.hub.ManagedHub.prototype.subscribe;
	var u = OpenAjax.hub.ManagedHub.prototype.unsubscribe;
	var p4c = OpenAjax.hub.ManagedHub.prototype.publishForClient;
	var s4c = OpenAjax.hub.ManagedHub.prototype.subscribeForClient;
	var u4c = OpenAjax.hub.ManagedHub.prototype.unsubscribeForClient;
	var gs = OpenAjax.hub.ManagedHub.prototype.getScope;	
	OpenAjax.hub.ManagedHub.prototype.publish = function( topic, data ) {
		PageBus._assertPubTopic(topic);
		if(this._pagebus._cache.isCaching(topic)) {
			if(data == null)
				this._pagebus._cache.clear(topic);
			else
				this._pagebus._cache.storeCopy(topic, data);
		}
		p.call( this, topic, data );
	};
	OpenAjax.hub.ManagedHub.prototype.subscribe = function( topic, onData, scope, onComplete, subscriberData ) {
		PageBus._assertSubTopic(topic);
		var sid = s.call( this, topic, onData, scope, onComplete, subscriberData );
		if(PageBus._cache.isCacheable(subscriberData)) {
			var cache = this._pagebus._cache;
			cache.add(topic, sid);
			var vals = cache.query(topic);
			for (var i = 0; i < vals.length; i++) {
				try {
					onData.call(scope ? scope : window, vals[i].topic, vals[i].value, subscriberData);
				}
				catch(e) {
					PageBus._debug();
				}
			}
		}
		return sid;
	};
	OpenAjax.hub.ManagedHub.prototype.unsubscribe = function( subID, onComplete, scope ) {
		var cache = this._pagebus._cache;
		cache.remove(subID); 
		u.call( this, subID, onComplete, scope );
	};
	OpenAjax.hub.ManagedHub.prototype.getScope = function() {
		return gs.call( this );
	};	
	OpenAjax.hub.ManagedHub.prototype.publishForClient = function( container, topic, data ) {
		PageBus._assertPubTopic(topic);
		if( (!this._pagebus._policy) || 
		    ( this._pagebus._policy.isAllowed(container.getPartnerOrigin(), PageBus.policy.Ops.Publish, topic) ) ) {
			if(this._pagebus._cache.isCaching(topic)) { 
				if(data == null)
					this._pagebus._cache.clear(topic);
				else
					this._pagebus._cache.storeCopy(topic, data);
			}
		}
		p4c.call( this, container, topic, data );
	};
	OpenAjax.hub.ManagedHub.prototype.subscribeForClient = function( container, topic, containerSubID ) {
		PageBus._assertSubTopic(topic);
		var mgrSubID = s4c.call( this, container, topic, containerSubID ); 
		if(topic.substring(0, 17) == "_pagebus.cache.s.") {
			var t = topic.substring(17);
			this._pagebus._cache.add(t, mgrSubID);
			var vals = this._pagebus._cache.query(t);
			
			function _sendValues( ) {
				for(var i = 0; i < vals.length; i++) {
					container.sendToClient(vals[i].topic, vals[i].value, containerSubID);
				}
			}
			setTimeout( _sendValues, 0 );
		}
		return mgrSubID;
	};
	OpenAjax.hub.ManagedHub.prototype.unsubscribeForClient = function( container, managerSubID ) {
		this._pagebus._cache.remove(managerSubID); 
		try {
			var sdata = this.getSubscriberData(managerSubID);
			if(PageBus._cache.isCacheable(sdata)) {
				if(this._pagebus._cacheSids[managerSubID]) { 
					this.unsubscribe(this._pagebus._cacheSids[managerSubID], null, null);
					delete this._pagebus._cacheSids[managerSubID];
					this._pagebus._cache.remove(managerSubID);		
				}
			}
			u4c.call( this, container, managerSubID );
		}
		catch(e) {
			PageBus._debug();
		}
	};
};
PageBus._enableMH();

PageBus.HubClientExtender = function( hub, params ) {
	var that = this;
	this._hub = hub;
	
	// Set up default parameters
	
	this._params = params;
	if(!params["PageBus"])
		this._params.PageBus = { log: params.log };
	if(!params.PageBus["policy"]) {
		params.PageBus.policy = null;
	}
	
	// Initialize this HubExtender
	
	this._policy = params.PageBus.policy;
	this._cache = new PageBus._cache.Cache();
	this._cacheSids = {};
	
	// Store references to the hub's sub, unsub and publish functions:
	
	this._wrappedSubscribe = hub.subscribe;
	this._wrappedUnsubscribe = hub.unsubscribe;
	this._wrappedPublish = hub.publish;
	this._wrappedDisconnect = hub.disconnect;
	
	// Replace the hub's pub, sub and uns functions with wrappers:
	
	hub.publish = function( topic, data ) {
		that._publishWrapper( topic, data );
	};
	
	hub.subscribe = function( topic, onData, scope, onComplete, subData ) {
		return that._subscribeWrapper( topic, onData, scope, onComplete, subData );
	};
	
	hub.unsubscribe = function( subscriptionID, onComplete, scope ) {
		that._unsubscribeWrapper( subscriptionID, onComplete, scope );
	};
	
	hub.disconnect = function( onComplete, scope ) {
		that._disconnectWrapper( onComplete, scope );
	}
};

PageBus.HubClientExtender.prototype._publishWrapper = function( topic, data ) {

	if(!this._hub.isConnected())
		throw new Error(OpenAjax.hub.Error.Disconnected);

	PageBus._assertPubTopic(topic);
	if(this._cache.isCaching(topic)) {
		if(data == null)
			this._cache.clear(topic);
		else
			this._cache.storeCopy(topic, data);
	}
	
	var origin = this._hub.getPartnerOrigin();
	if(!origin)
		throw new Error(OpenAjax.hub.Error.Disconnected);
	if( this._policy && (! this._policy.onSend( topic, data, origin )) )
		return;
	this._wrappedPublish.call( this._hub, topic, data );
};

PageBus.HubClientExtender.prototype._subscribeWrapper = function( topic, onData, scope, onComplete, subData ) {	
	var that = this;
	if(!this._hub.isConnected())
		throw new Error(OpenAjax.hub.Error.Disconnected);
	PageBus._assertSubTopic(topic);
	if(!onData)
		throw new Error(OpenAjax.hub.Error.BadParameters);

	var policy = this._policy;
	var origin = this._hub.getPartnerOrigin();
	if(!origin)
		throw new Error(OpenAjax.hub.Error.Disconnected);
	if(policy && (! policy.onReceive.call( policy, topic, null, origin )) ) 
		throw new Error(OpenAjax.hub.Error.NotAllowed);
	
	dataHook = function( t, d, sd ) {			
		var policy = that._policy;
		var origin = that._hub.getPartnerOrigin();
		if(policy && (! policy.onReceive.call( policy, t, d, origin )) ) 
			return;
		if(PageBus._cache.isCacheable(sd)) {
			if(that._cache.isCaching(t)) {
				if(d == null)
					that._cache.clear(t);
				else
					that._cache.storeCopy(t, d);
			}
		}				
		try {
			var s = scope ? scope : window;
			onData.call(s, t, d, sd);
		}
		catch(e) {
			PageBus._debug();
		}
	};
	completeHook = function( item, suc, err ) {
		if(!suc) {			
			if(that._cacheSids[item]) { 
				that._hub.unsubscribe(that._cacheSids[item], null, null);
				delete that._cacheSids[item];
				that._cache.remove(item);		
			}
		}
		
  		try {
			var s = scope ? scope : window;
			onComplete.call(s, item, suc, err);
		}
		catch(e) {
			PageBus._debug();
		}
	};
	
	var sid = this._wrappedSubscribe.call( this._hub, topic, dataHook, scope, completeHook, subData );		
	
	try {
		this._hub.getSubscriberData( sid );
	} catch(e) {
		if( e.message == OpenAjax.hub.Error.NoSubscription ) {
			// unsubscribe was synchronously called within completeHook
			return sid;
		}
	}
	
	if( PageBus._cache.isCacheable(subData) ) {
		this._cache.add( topic, sid );
		this._cacheSids[sid] = this._hub.subscribe(
			"_pagebus.cache.s." + topic, 
			function(t,d,sd) { 
				var policy = this._policy;
				var origin = this._hub.getPartnerOrigin();
				if(policy && (! this._policy.onReceive( t, d, origin )) ) 
					return;
				if(PageBus._cache.isCacheable(subData)) {
					if(this._cache.isCaching(t)) {
						if(d == null)
							this._cache.clear(t);
						else
							this._cache.storeCopy(t, d);
					}
				}
				try {
					var s = scope ? scope : window;
					onData.call( s, t, d, subData );
				}
				catch(e) {
					PageBus._debug();
				}
			}, 
			this, 
			function(item, suc, err) {}, 
			null);
	}
	return sid;
};

PageBus.HubClientExtender.prototype._unsubscribeWrapper = function( subscriptionID, onComplete, scope ) {
	if(!this._hub.isConnected())
		throw new Error(OpenAjax.hub.Error.Disconnected);
	if( (subscriptionID == null) || (subscriptionID == "") )
		throw new Error(OpenAjax.hub.Error.BadParameters);
	var sdata = this._hub.getSubscriberData(subscriptionID);
	if(PageBus._cache.isCacheable(sdata)) {
		if(this._cacheSids[subscriptionID]) { 
			this._hub.unsubscribe(this._cacheSids[subscriptionID], null, null);
			delete this._cacheSids[subscriptionID];
			this._cache.remove(subscriptionID);		
		}
	}
	this._wrappedUnsubscribe.call( this._hub, subscriptionID, onComplete, scope );
};

PageBus.HubClientExtender.prototype._disconnectWrapper = function( onComplete, scope ) {
	this._cache._caches.wildcardClear("**");
	this._cacheSids = {};	
	this._wrappedDisconnect.call( this._hub, onComplete, scope );
};

PageBus.HubClientExtender.prototype.query = function( topic ) {
	return this._cache.query( topic ); // do not throw NoCache; can use query across broad ranges that are partly cached.
};

PageBus.HubClientExtender.prototype.store = function( topic, data ) {
	if(this._cache.isCaching(topic)) 
		this._hub.publish( topic, data );
	else
		throw new Error( PageBus.cache.Error.NoCache );
};

PageBus.HubClientExtender.prototype.clear = function( topic ) {
	if(this._cache.isCaching(topic)) 
		this._hub.publish( topic, null );
	else
		throw new Error( PageBus.cache.Error.NoCache );
};


/**
 * enableHubClientClass
 * Global function that prepares a HubClient class so that when it is 
 * instantiated via the "new" operator, the resulting instance is 
 * PageBus-enabled.
 */
PageBus.enableHubClientClass = function( hubClass ) {	
	hubClass.prototype._pagebusWrappedConnect = hubClass.prototype.connect;
	
	hubClass.prototype.connect = function( onComplete, scope ) {
		var hub = this;
		if( !hub.pagebus ) {
			hub.pagebus = new PageBus.HubClientExtender( hub, hub.getParameters() );
		}
		hub._pagebusWrappedConnect.call( hub, onComplete, scope );
	};

};


//////////////////////////////////////////////////////////////////////

//PageBus-enable the reference implementation:

if(!OpenAjax.hub) {
	debugger;
}

if(OpenAjax.hub.InlineHubClient)
	PageBus.enableHubClientClass(OpenAjax.hub.InlineHubClient);
if(OpenAjax.hub.IframeHubClient)
	PageBus.enableHubClientClass(OpenAjax.hub.IframeHubClient);

OpenAjax.hub._hub = new OpenAjax.hub.ManagedHub({ 
    onSubscribe: function(topic, ctnr) { return true; },
    onPublish: function(topic, data, pcont, scont) { return true; }
});

OpenAjax.hub.subscribe = function(topic, onData, scope, subscriberData) {
    if ( typeof onData === "string" ) {
        scope = scope || window;
        onData = scope[ onData ] || null;
    }    
    return OpenAjax.hub._hub.subscribe( topic, onData, scope, null, subscriberData );
}

OpenAjax.hub.unsubscribe = function(subscriptionID) {
    return OpenAjax.hub._hub.unsubscribe( subscriptionID );
}

OpenAjax.hub.publish = function(topic, data) {
    OpenAjax.hub._hub.publish(topic, data);
}

//////////////////////////////////////////////////////////////////////

PageBus.publish = function(topic, data) {
	OpenAjax.hub.publish(topic, data);
};
PageBus.subscribe = function(topic, scope, onData, subscriberData) {
	return OpenAjax.hub.subscribe(topic, onData, scope, subscriberData);
};
PageBus.unsubscribe = function(sub) {
	OpenAjax.hub.unsubscribe(sub);
};
PageBus.store = function(topic, data) {
	OpenAjax.hub._hub.pagebus.store(topic, data);
};
PageBus.query = function(topic) {
	return OpenAjax.hub._hub.pagebus.query(topic);
};

OpenAjax.hub.registerLibrary("PageBus", "http://pagebus.org/pagebus", "2.0", {});

