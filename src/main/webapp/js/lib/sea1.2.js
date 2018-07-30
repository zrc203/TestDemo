; (function($) {
    $.fn.ajaxSubmit = function(options) {
        if (!this.length) {
            log('ajaxSubmit: skipping submit process - no element selected');
            return this;
        }
        if (typeof options == 'function') {
            options = {
                success: options
            };
        }
        var action = this.attr('action');
        var url = (typeof action === 'string') ? $.trim(action) : '';
        url = url || window.location.href || '';
        if (url) {
            url = (url.match(/^([^#]+)/) || [])[1];
        }
        options = $.extend(true, {
            url: url,
            success: $.ajaxSettings.success,
            type: this[0].getAttribute('method') || 'GET',
            iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false': 'about:blank'
        },
        options);
        var veto = {};
        this.trigger('form-pre-serialize', [this, options, veto]);
        if (veto.veto) {
            log('ajaxSubmit: submit vetoed via form-pre-serialize trigger');
            return this;
        }
        if (options.beforeSerialize && options.beforeSerialize(this, options) === false) {
            log('ajaxSubmit: submit aborted via beforeSerialize callback');
            return this;
        }
        var n, v, a = this.formToArray(options.semantic);
        if (options.data) {
            options.extraData = options.data;
            for (n in options.data) {
                if (options.data[n] instanceof Array) {
                    for (var k in options.data[n]) {
                        a.push({
                            name: n,
                            value: options.data[n][k]
                        });
                    }
                } else {
                    v = options.data[n];
                    v = $.isFunction(v) ? v() : v;
                    a.push({
                        name: n,
                        value: v
                    });
                }
            }
        }
        if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) {
            log('ajaxSubmit: submit aborted via beforeSubmit callback');
            return this;
        }
        this.trigger('form-submit-validate', [a, this, options, veto]);
        if (veto.veto) {
            log('ajaxSubmit: submit vetoed via form-submit-validate trigger');
            return this;
        }
        var q = $.param(a);
        if (options.type.toUpperCase() == 'GET') {
            options.url += (options.url.indexOf('?') >= 0 ? '&': '?') + q;
            options.data = null;
        } else {
            options.data = q;
        }
        var $form = this,
        callbacks = [];
        if (options.resetForm) {
            callbacks.push(function() {
                $form.resetForm();
            });
        }
        if (options.clearForm) {
            callbacks.push(function() {
                $form.clearForm();
            });
        }
        if (!options.dataType && options.target) {
            var oldSuccess = options.success ||
            function() {};
            callbacks.push(function(data) {
                var fn = options.replaceTarget ? 'replaceWith': 'html';
                $(options.target)[fn](data).each(oldSuccess, arguments);
            });
        } else if (options.success) {
            callbacks.push(options.success);
        }
        options.success = function(data, status, xhr) {
            var context = options.context || options;
            for (var i = 0,
            max = callbacks.length; i < max; i++) {
                callbacks[i].apply(context, [data, status, xhr || $form, $form]);
            }
        };
        var fileInputs = $('input:file', this).length > 0;
        var mp = 'multipart/form-data';
        var multipart = ($form.attr('enctype') == mp || $form.attr('encoding') == mp);
        if (options.iframe !== false && (fileInputs || options.iframe || multipart)) {
            if (options.closeKeepAlive) {
                $.get(options.closeKeepAlive,
                function() {
                    fileUpload(a);
                });
            } else {
                fileUpload(a);
            }
        } else {
            $.ajax(options);
        }
        this.trigger('form-submit-notify', [this, options]);
        return this;
        function fileUpload(a) {
            var form = $form[0],
            i,
            s,
            g,
            id,
            $io,
            io,
            xhr,
            sub,
            n,
            timedOut,
            timeoutHandle;
            if (a) {
                for (i = 0; i < a.length; i++) {
                    $(form[a[i].name]).attr('disabled', false);
                }
            }
            if ($(':input[name=submit],:input[id=submit]', form).length) {
                alert('Error: Form elements must not have name or id of "submit".');
                return;
            }
            s = $.extend(true, {},
            $.ajaxSettings, options);
            s.context = s.context || s;
            id = 'jqFormIO' + (new Date().getTime());
            if (s.iframeTarget) {
                $io = $(s.iframeTarget);
                n = $io.attr('name');
                if (n == null) $io.attr('name', id);
                else id = n;
            } else {
                $io = $('<iframe name="' + id + '" src="' + s.iframeSrc + '" />');
                $io.css({
                    position: 'absolute',
                    top: '-1000px',
                    left: '-1000px'
                });
            }
            io = $io[0];
            xhr = {
                aborted: 0,
                responseText: null,
                responseXML: null,
                status: 0,
                statusText: 'n/a',
                getAllResponseHeaders: function() {},
                getResponseHeader: function() {},
                setRequestHeader: function() {},
                abort: function(status) {
                    var e = (status === 'timeout' ? 'timeout': 'aborted');
                    log('aborting upload... ' + e);
                    this.aborted = 1;
                    $io.attr('src', s.iframeSrc);
                    xhr.error = e;
                    s.error && s.error.call(s.context, xhr, e, status);
                    g && $.event.trigger("ajaxError", [xhr, s, e]);
                    s.complete && s.complete.call(s.context, xhr, e);
                }
            };
            g = s.global;
            if (g && !$.active++) {
                $.event.trigger("ajaxStart");
            }
            if (g) {
                $.event.trigger("ajaxSend", [xhr, s]);
            }
            if (s.beforeSend && s.beforeSend.call(s.context, xhr, s) === false) {
                if (s.global) {
                    $.active--;
                }
                return;
            }
            if (xhr.aborted) {
                return;
            }
            sub = form.clk;
            if (sub) {
                n = sub.name;
                if (n && !sub.disabled) {
                    s.extraData = s.extraData || {};
                    s.extraData[n] = sub.value;
                    if (sub.type == "image") {
                        s.extraData[n + '.x'] = form.clk_x;
                        s.extraData[n + '.y'] = form.clk_y;
                    }
                }
            }
            var CLIENT_TIMEOUT_ABORT = 1;
            var SERVER_ABORT = 2;
            function getDoc(frame) {
                var doc = frame.contentWindow ? frame.contentWindow.document: frame.contentDocument ? frame.contentDocument: frame.document;
                return doc;
            }
            function doSubmit() {
                var t = $form.attr('target'),
                a = $form.attr('action');
                form.setAttribute('target', id);
                if (form.getAttribute('method') != 'POST') {
                    form.setAttribute('method', 'POST');
                }
                if (form.getAttribute('action') != s.url) {
                    form.setAttribute('action', s.url);
                }
                if (!s.skipEncodingOverride) {
                    $form.attr({
                        encoding: 'multipart/form-data',
                        enctype: 'multipart/form-data'
                    });
                }
                if (s.timeout) {
                    timeoutHandle = setTimeout(function() {
                        timedOut = true;
                        cb(CLIENT_TIMEOUT_ABORT);
                    },
                    s.timeout);
                }
                function checkState() {
                    try {
                        var state = getDoc(io).readyState;
                        log('state = ' + state);
                        if (state.toLowerCase() == 'uninitialized') setTimeout(checkState, 50);
                    } catch(e) {
                        log('Server abort: ', e, ' (', e.name, ')');
                        cb(SERVER_ABORT);
                        timeoutHandle && clearTimeout(timeoutHandle);
                        timeoutHandle = undefined;
                    }
                }
                var extraInputs = [];
                try {
                    if (s.extraData) {
                        for (var n in s.extraData) {
                            extraInputs.push($('<input type="hidden" name="' + n + '" />').attr('value', s.extraData[n]).appendTo(form)[0]);
                        }
                    }
                    if (!s.iframeTarget) {
                        $io.appendTo('body');
                        io.attachEvent ? io.attachEvent('onload', cb) : io.addEventListener('load', cb, false);
                    }
                    setTimeout(checkState, 15);
                    form.submit();
                } finally {
                    form.setAttribute('action', a);
                    if (t) {
                        form.setAttribute('target', t);
                    } else {
                        $form.removeAttr('target');
                    }
                    $(extraInputs).remove();
                }
            }
            if (s.forceSync) {
                doSubmit();
            } else {
                setTimeout(doSubmit, 10);
            }
            var data, doc, domCheckCount = 50,
            callbackProcessed;
            function cb(e) {
                if (xhr.aborted || callbackProcessed) {
                    return;
                }
                try {
                    doc = getDoc(io);
                } catch(ex) {
                    log('cannot access response document: ', ex);
                    e = SERVER_ABORT;
                }
                if (e === CLIENT_TIMEOUT_ABORT && xhr) {
                    xhr.abort('timeout');
                    return;
                } else if (e == SERVER_ABORT && xhr) {
                    xhr.abort('server abort');
                    return;
                }
                if (!doc || doc.location.href == s.iframeSrc) {
                    if (!timedOut) return;
                }
                io.detachEvent ? io.detachEvent('onload', cb) : io.removeEventListener('load', cb, false);
                var status = 'success',
                errMsg;
                try {
                    if (timedOut) {
                        throw 'timeout';
                    }
                    var isXml = s.dataType == 'xml' || doc.XMLDocument || $.isXMLDoc(doc);
                    log('isXml=' + isXml);
                    if (!isXml && window.opera && (doc.body == null || doc.body.innerHTML == '')) {
                        if (--domCheckCount) {
                            log('requeing onLoad callback, DOM not available');
                            setTimeout(cb, 250);
                            return;
                        }
                    }
                    var docRoot = doc.body ? doc.body: doc.documentElement;
                    xhr.responseText = docRoot ? docRoot.innerHTML: null;
                    xhr.responseXML = doc.XMLDocument ? doc.XMLDocument: doc;
                    if (isXml) s.dataType = 'xml';
                    xhr.getResponseHeader = function(header) {
                        var headers = {
                            'content-type': s.dataType
                        };
                        return headers[header];
                    };
                    if (docRoot) {
                        xhr.status = Number(docRoot.getAttribute('status')) || xhr.status;
                        xhr.statusText = docRoot.getAttribute('statusText') || xhr.statusText;
                    }
                    var dt = s.dataType || '';
                    var scr = /(json|script|text)/.test(dt.toLowerCase());
                    if (scr || s.textarea) {
                        var ta = doc.getElementsByTagName('textarea')[0];
                        if (ta) {
                            xhr.responseText = ta.value;
                            xhr.status = Number(ta.getAttribute('status')) || xhr.status;
                            xhr.statusText = ta.getAttribute('statusText') || xhr.statusText;
                        } else if (scr) {
                            var pre = doc.getElementsByTagName('pre')[0];
                            var b = doc.getElementsByTagName('body')[0];
                            if (pre) {
                                xhr.responseText = pre.textContent ? pre.textContent: pre.innerHTML;
                            } else if (b) {
                                xhr.responseText = b.innerHTML;
                            }
                        }
                    } else if (s.dataType == 'xml' && !xhr.responseXML && xhr.responseText != null) {
                        xhr.responseXML = toXml(xhr.responseText);
                    }
                    try {
                        data = httpData(xhr, s.dataType, s);
                    } catch(e) {
                        status = 'parsererror';
                        xhr.error = errMsg = (e || status);
                    }
                } catch(e) {
                    log('error caught: ', e);
                    status = 'error';
                    xhr.error = errMsg = (e || status);
                }
                if (xhr.aborted) {
                    log('upload aborted');
                    status = null;
                }
                if (xhr.status) {
                    status = (xhr.status >= 200 && xhr.status < 300 || xhr.status === 304) ? 'success': 'error';
                }
                if (status === 'success') {
                    s.success && s.success.call(s.context, data, 'success', xhr);
                    g && $.event.trigger("ajaxSuccess", [xhr, s]);
                } else if (status) {
                    if (errMsg == undefined) errMsg = xhr.statusText;
                    s.error && s.error.call(s.context, xhr, status, errMsg);
                    g && $.event.trigger("ajaxError", [xhr, s, errMsg]);
                }
                g && $.event.trigger("ajaxComplete", [xhr, s]);
                if (g && !--$.active) {
                    $.event.trigger("ajaxStop");
                }
                s.complete && s.complete.call(s.context, xhr, status);
                callbackProcessed = true;
                if (s.timeout) clearTimeout(timeoutHandle);
                setTimeout(function() {
                    if (!s.iframeTarget) $io.remove();
                    xhr.responseXML = null;
                },
                100);
            }
            var toXml = $.parseXML ||
            function(s, doc) {
                if (window.ActiveXObject) {
                    doc = new ActiveXObject('Microsoft.XMLDOM');
                    doc.async = 'false';
                    doc.loadXML(s);
                } else {
                    doc = (new DOMParser()).parseFromString(s, 'text/xml');
                }
                return (doc && doc.documentElement && doc.documentElement.nodeName != 'parsererror') ? doc: null;
            };
            var parseJSON = $.parseJSON ||
            function(s) {
                return window['eval']('(' + s + ')');
            };
            var httpData = function(xhr, type, s) {
                var ct = xhr.getResponseHeader('content-type') || '',
                xml = type === 'xml' || !type && ct.indexOf('xml') >= 0,
                data = xml ? xhr.responseXML: xhr.responseText;
                if (xml && data.documentElement.nodeName === 'parsererror') {
                    $.error && $.error('parsererror');
                }
                if (s && s.dataFilter) {
                    data = s.dataFilter(data, type);
                }
                if (typeof data === 'string') {
                    if (type === 'json' || !type && ct.indexOf('json') >= 0) {
                        data = parseJSON(data);
                    } else if (type === "script" || !type && ct.indexOf("javascript") >= 0) {
                        $.globalEval(data);
                    }
                }
                return data;
            };
        }
    };
    $.fn.ajaxForm = function(options) {
        if (this.length === 0) {
            var o = {
                s: this.selector,
                c: this.context
            };
            if (!$.isReady && o.s) {
                log('DOM not ready, queuing ajaxForm');
                $(function() {
                    $(o.s, o.c).ajaxForm(options);
                });
                return this;
            }
            log('terminating; zero elements found by selector' + ($.isReady ? '': ' (DOM not ready)'));
            return this;
        }
        return this.ajaxFormUnbind().bind('submit.form-plugin',
        function(e) {
            if (!e.isDefaultPrevented()) {
                e.preventDefault();
                $(this).ajaxSubmit(options);
            }
        }).bind('click.form-plugin',
        function(e) {
            var target = e.target;
            var $el = $(target);
            if (! ($el.is(":submit,input:image"))) {
                var t = $el.closest(':submit');
                if (t.length == 0) {
                    return;
                }
                target = t[0];
            }
            var form = this;
            form.clk = target;
            if (target.type == 'image') {
                if (e.offsetX != undefined) {
                    form.clk_x = e.offsetX;
                    form.clk_y = e.offsetY;
                } else if (typeof $.fn.offset == 'function') {
                    var offset = $el.offset();
                    form.clk_x = e.pageX - offset.left;
                    form.clk_y = e.pageY - offset.top;
                } else {
                    form.clk_x = e.pageX - target.offsetLeft;
                    form.clk_y = e.pageY - target.offsetTop;
                }
            }
            setTimeout(function() {
                form.clk = form.clk_x = form.clk_y = null;
            },
            100);
        });
    };
    $.fn.ajaxFormUnbind = function() {
        return this.unbind('submit.form-plugin click.form-plugin');
    };
    $.fn.formToArray = function(semantic) {
        var a = [];
        if (this.length === 0) {
            return a;
        }
        var form = this[0];
        var els = semantic ? form.getElementsByTagName('*') : form.elements;
        if (!els) {
            return a;
        }
        var i, j, n, v, el, max, jmax;
        for (i = 0, max = els.length; i < max; i++) {
            el = els[i];
            n = el.name;
            if (!n) {
                continue;
            }
            if (semantic && form.clk && el.type == "image") {
                if (!el.disabled && form.clk == el) {
                    a.push({
                        name: n,
                        value: $(el).val()
                    });
                    a.push({
                        name: n + '.x',
                        value: form.clk_x
                    },
                    {
                        name: n + '.y',
                        value: form.clk_y
                    });
                }
                continue;
            }
            v = $.fieldValue(el, true);
            if (v && v.constructor == Array) {
                for (j = 0, jmax = v.length; j < jmax; j++) {
                    a.push({
                        name: n,
                        value: v[j]
                    });
                }
            } else if (v !== null && typeof v != 'undefined') {
                a.push({
                    name: n,
                    value: v
                });
            }
        }
        if (!semantic && form.clk) {
            var $input = $(form.clk),
            input = $input[0];
            n = input.name;
            if (n && !input.disabled && input.type == 'image') {
                a.push({
                    name: n,
                    value: $input.val()
                });
                a.push({
                    name: n + '.x',
                    value: form.clk_x
                },
                {
                    name: n + '.y',
                    value: form.clk_y
                });
            }
        }
        return a;
    };
    $.fn.formSerialize = function(semantic) {
        return $.param(this.formToArray(semantic));
    };
    $.fn.fieldSerialize = function(successful) {
        var a = [];
        this.each(function() {
            var n = this.name;
            if (!n) {
                return;
            }
            var v = $.fieldValue(this, successful);
            if (v && v.constructor == Array) {
                for (var i = 0,
                max = v.length; i < max; i++) {
                    a.push({
                        name: n,
                        value: v[i]
                    });
                }
            } else if (v !== null && typeof v != 'undefined') {
                a.push({
                    name: this.name,
                    value: v
                });
            }
        });
        return $.param(a);
    };
    $.fn.fieldValue = function(successful) {
        for (var val = [], i = 0, max = this.length; i < max; i++) {
            var el = this[i];
            var v = $.fieldValue(el, successful);
            if (v === null || typeof v == 'undefined' || (v.constructor == Array && !v.length)) {
                continue;
            }
            v.constructor == Array ? $.merge(val, v) : val.push(v);
        }
        return val;
    };
    $.fieldValue = function(el, successful) {
        var n = el.name,
        t = el.type,
        tag = el.tagName.toLowerCase();
        if (successful === undefined) {
            successful = true;
        }
        if (successful && (!n || el.disabled || t == 'reset' || t == 'button' || (t == 'checkbox' || t == 'radio') && !el.checked || (t == 'submit' || t == 'image') && el.form && el.form.clk != el || tag == 'select' && el.selectedIndex == -1)) {
            return null;
        }
        if (tag == 'select') {
            var index = el.selectedIndex;
            if (index < 0) {
                return null;
            }
            var a = [],
            ops = el.options;
            var one = (t == 'select-one');
            var max = (one ? index + 1 : ops.length);
            for (var i = (one ? index: 0); i < max; i++) {
                var op = ops[i];
                if (op.selected) {
                    var v = op.value;
                    if (!v) {
                        v = (op.attributes && op.attributes['value'] && !(op.attributes['value'].specified)) ? op.text: op.value;
                    }
                    if (one) {
                        return v;
                    }
                    a.push(v);
                }
            }
            return a;
        }
        return $(el).val();
    };
    $.fn.clearForm = function() {
        return this.each(function() {
            $('input,select,textarea', this).clearFields();
        });
    };
    $.fn.clearFields = $.fn.clearInputs = function() {
        return this.each(function() {
            var t = this.type,
            tag = this.tagName.toLowerCase();
            if (t == 'text' || t == 'password' || tag == 'textarea') {
                this.value = '';
            } else if (t == 'checkbox' || t == 'radio') {
                this.checked = false;
            } else if (tag == 'select') {
                this.selectedIndex = -1;
            }
        });
    };
    $.fn.resetForm = function() {
        return this.each(function() {
            if (typeof this.reset == 'function' || (typeof this.reset == 'object' && !this.reset.nodeType)) {
                this.reset();
            }
        });
    };
    $.fn.enable = function(b) {
        if (b === undefined) {
            b = true;
        }
        return this.each(function() {
            this.disabled = !b;
        });
    };
    $.fn.selected = function(select) {
        if (select === undefined) {
            select = true;
        }
        return this.each(function() {
            var t = this.type;
            if (t == 'checkbox' || t == 'radio') {
                this.checked = select;
            } else if (this.tagName.toLowerCase() == 'option') {
                var $sel = $(this).parent('select');
                if (select && $sel[0] && $sel[0].type == 'select-one') {
                    $sel.find('option').selected(false);
                }
                this.selected = select;
            }
        });
    };
    function log() {
        var msg = '[jquery.form] ' + Array.prototype.join.call(arguments, '');
        if (window.console && window.console.log) {
            window.console.log(msg);
        } else if (window.opera && window.opera.postError) {
            window.opera.postError(msg);
        }
    };
})(jQuery);; (function(b) {
    b.gritter = {};
    b.gritter.options = {
        position: "",
        class_name: "",
        fade_in_speed: "medium",
        fade_out_speed: 1000,
        time: 6000
    };
    b.gritter.add = function(f) {
        try {
            return a.add(f || {})
        } catch(d) {
            var c = "Gritter Error: " + d; (typeof(console) != "undefined" && console.error) ? console.error(c, f) : alert(c)
        }
    };
    b.gritter.remove = function(d, c) {
        a.removeSpecific(d, c || {})
    };
    b.gritter.removeAll = function(c) {
        a.stop(c || {})
    };
    var a = {
        position: "",
        fade_in_speed: "",
        fade_out_speed: "",
        time: "",
        _custom_timer: 0,
        _item_count: 0,
        _is_setup: 0,
        _tpl_close: '<div class="gritter-close"></div>',
        _tpl_title: '<span class="gritter-title">[[title]]</span>',
        _tpl_item: '<div id="gritter-item-[[number]]" class="gritter-item-wrapper [[item_class]]" style="display:none"><div class="gritter-top"></div><div class="gritter-item">[[close]][[image]]<div class="[[class_name]]">[[title]]<p>[[text]]</p></div><div style="clear:both"></div></div><div class="gritter-bottom"></div></div>',
        _tpl_wrap: '<div id="gritter-notice-wrapper"></div>',
        add: function(g) {
            if (typeof(g) == "string") {
                g = {
                    text: g
                }
            }
            if (g.text === null) {
                throw 'You must supply "text" parameter.'
            }
            if (!this._is_setup) {
                this._runSetup()
            }
            var k = g.title,
            n = g.text,
            e = g.image || "",
            l = g.sticky || false,
            m = g.class_name || b.gritter.options.class_name,
            j = b.gritter.options.position,
            d = g.time || "";
            this._verifyWrapper();
            this._item_count++;
            var f = this._item_count,
            i = this._tpl_item;
            b(["before_open", "after_open", "before_close", "after_close"]).each(function(p, q) {
                a["_" + q + "_" + f] = (b.isFunction(g[q])) ? g[q] : function() {}
            });
            this._custom_timer = 0;
            if (d) {
                this._custom_timer = d
            }
            var c = (e != "") ? '<img src="' + e + '" class="gritter-image" />': "",
            h = (e != "") ? "gritter-with-image": "gritter-without-image";
            if (k) {
                k = this._str_replace("[[title]]", k, this._tpl_title)
            } else {
                k = ""
            }
            i = this._str_replace(["[[title]]", "[[text]]", "[[close]]", "[[image]]", "[[number]]", "[[class_name]]", "[[item_class]]"], [k, n, this._tpl_close, c, this._item_count, h, m], i);
            if (this["_before_open_" + f]() === false) {
                return false
            }
            b("#gritter-notice-wrapper").addClass(j).append(i);
            var o = b("#gritter-item-" + this._item_count);
            o.fadeIn(this.fade_in_speed,
            function() {
                a["_after_open_" + f](b(this))
            });
            if (!l) {
                this._setFadeTimer(o, f)
            }
            b(o).bind("mouseenter mouseleave",
            function(p) {
                if (p.type == "mouseenter") {
                    if (!l) {
                        a._restoreItemIfFading(b(this), f)
                    }
                } else {
                    if (!l) {
                        a._setFadeTimer(b(this), f)
                    }
                }
                a._hoverState(b(this), p.type)
            });
            b(o).find(".gritter-close").click(function() {
                a.removeSpecific(f, {},
                null, true)
            });
            return f
        },
        _countRemoveWrapper: function(c, d, f) {
            d.remove();
            this["_after_close_" + c](d, f);
            if (b(".gritter-item-wrapper").length == 0) {
                b("#gritter-notice-wrapper").remove()
            }
        },
        _fade: function(g, d, j, f) {
            var j = j || {},
            i = (typeof(j.fade) != "undefined") ? j.fade: true,
            c = j.speed || this.fade_out_speed,
            h = f;
            this["_before_close_" + d](g, h);
            if (f) {
                g.unbind("mouseenter mouseleave")
            }
            if (i) {
                g.animate({
                    opacity: 0
                },
                c,
                function() {
                    g.animate({
                        height: 0
                    },
                    300,
                    function() {
                        a._countRemoveWrapper(d, g, h)
                    })
                })
            } else {
                this._countRemoveWrapper(d, g)
            }
        },
        _hoverState: function(d, c) {
            if (c == "mouseenter") {
                d.addClass("hover");
                d.find(".gritter-close").show()
            } else {
                d.removeClass("hover");
                d.find(".gritter-close").hide()
            }
        },
        removeSpecific: function(c, g, f, d) {
            if (!f) {
                var f = b("#gritter-item-" + c)
            }
            this._fade(f, c, g || {},
            d)
        },
        _restoreItemIfFading: function(d, c) {
            clearTimeout(this["_int_id_" + c]);
            d.stop().css({
                opacity: "",
                height: ""
            })
        },
        _runSetup: function() {
            for (opt in b.gritter.options) {
                this[opt] = b.gritter.options[opt]
            }
            this._is_setup = 1
        },
        _setFadeTimer: function(f, d) {
            var c = (this._custom_timer) ? this._custom_timer: this.time;
            this["_int_id_" + d] = setTimeout(function() {
                a._fade(f, d)
            },
            c)
        },
        stop: function(e) {
            var c = (b.isFunction(e.before_close)) ? e.before_close: function() {};
            var f = (b.isFunction(e.after_close)) ? e.after_close: function() {};
            var d = b("#gritter-notice-wrapper");
            c(d);
            d.fadeOut(function() {
                b(this).remove();
                f()
            })
        },
        _str_replace: function(v, e, o, n) {
            var k = 0,
            h = 0,
            t = "",
            m = "",
            g = 0,
            q = 0,
            l = [].concat(v),
            c = [].concat(e),
            u = o,
            d = c instanceof Array,
            p = u instanceof Array;
            u = [].concat(u);
            if (n) {
                this.window[n] = 0
            }
            for (k = 0, g = u.length; k < g; k++) {
                if (u[k] === "") {
                    continue
                }
                for (h = 0, q = l.length; h < q; h++) {
                    t = u[k] + "";
                    m = d ? (c[h] !== undefined ? c[h] : "") : c[0];
                    u[k] = (t).split(l[h]).join(m);
                    if (n && u[k] !== t) {
                        this.window[n] += (t.length - u[k].length) / l[h].length
                    }
                }
            }
            return p ? u: u[0]
        },
        _verifyWrapper: function() {
            if (b("#gritter-notice-wrapper").length == 0) {
                b("body").append(this._tpl_wrap)
            }
        }
    }
})(jQuery);; !
function(e) {
    if ("object" == typeof exports && "undefined" != typeof module) module.exports = e();
    else if ("function" == typeof define && define.amd) define([], e);
    else {
        var t;
        t = "undefined" != typeof window ? window: "undefined" != typeof global ? global: "undefined" != typeof self ? self: this,
        t.Clipboard = e()
    }
} (function() {
    var e, t, n;
    return function e(t, n, o) {
        function i(a, c) {
            if (!n[a]) {
                if (!t[a]) {
                    var l = "function" == typeof require && require;
                    if (!c && l) return l(a, !0);
                    if (r) return r(a, !0);
                    var u = new Error("Cannot find module '" + a + "'");
                    throw u.code = "MODULE_NOT_FOUND",
                    u
                }
                var s = n[a] = {
                    exports: {}
                };
                t[a][0].call(s.exports,
                function(e) {
                    var n = t[a][1][e];
                    return i(n ? n: e)
                },
                s, s.exports, e, t, n, o)
            }
            return n[a].exports
        }
        for (var r = "function" == typeof require && require,
        a = 0; a < o.length; a++) i(o[a]);
        return i
    } ({
        1 : [function(e, t, n) {
            function o(e, t) {
                for (; e && e.nodeType !== i;) {
                    if (e.matches(t)) return e;
                    e = e.parentNode
                }
            }
            var i = 9;
            if ("undefined" != typeof Element && !Element.prototype.matches) {
                var r = Element.prototype;
                r.matches = r.matchesSelector || r.mozMatchesSelector || r.msMatchesSelector || r.oMatchesSelector || r.webkitMatchesSelector
            }
            t.exports = o
        },
        {}],
        2 : [function(e, t, n) {
            function o(e, t, n, o, r) {
                var a = i.apply(this, arguments);
                return e.addEventListener(n, a, r),
                {
                    destroy: function() {
                        e.removeEventListener(n, a, r)
                    }
                }
            }
            function i(e, t, n, o) {
                return function(n) {
                    n.delegateTarget = r(n.target, t),
                    n.delegateTarget && o.call(e, n)
                }
            }
            var r = e("./closest");
            t.exports = o
        },
        {
            "./closest": 1
        }],
        3 : [function(e, t, n) {
            n.node = function(e) {
                return void 0 !== e && e instanceof HTMLElement && 1 === e.nodeType
            },
            n.nodeList = function(e) {
                var t = Object.prototype.toString.call(e);
                return void 0 !== e && ("[object NodeList]" === t || "[object HTMLCollection]" === t) && "length" in e && (0 === e.length || n.node(e[0]))
            },
            n.string = function(e) {
                return "string" == typeof e || e instanceof String
            },
            n.fn = function(e) {
                var t = Object.prototype.toString.call(e);
                return "[object Function]" === t
            }
        },
        {}],
        4 : [function(e, t, n) {
            function o(e, t, n) {
                if (!e && !t && !n) throw new Error("Missing required arguments");
                if (!c.string(t)) throw new TypeError("Second argument must be a String");
                if (!c.fn(n)) throw new TypeError("Third argument must be a Function");
                if (c.node(e)) return i(e, t, n);
                if (c.nodeList(e)) return r(e, t, n);
                if (c.string(e)) return a(e, t, n);
                throw new TypeError("First argument must be a String, HTMLElement, HTMLCollection, or NodeList")
            }
            function i(e, t, n) {
                return e.addEventListener(t, n),
                {
                    destroy: function() {
                        e.removeEventListener(t, n)
                    }
                }
            }
            function r(e, t, n) {
                return Array.prototype.forEach.call(e,
                function(e) {
                    e.addEventListener(t, n)
                }),
                {
                    destroy: function() {
                        Array.prototype.forEach.call(e,
                        function(e) {
                            e.removeEventListener(t, n)
                        })
                    }
                }
            }
            function a(e, t, n) {
                return l(document.body, e, t, n)
            }
            var c = e("./is"),
            l = e("delegate");
            t.exports = o
        },
        {
            "./is": 3,
            delegate: 2
        }],
        5 : [function(e, t, n) {
            function o(e) {
                var t;
                if ("SELECT" === e.nodeName) e.focus(),
                t = e.value;
                else if ("INPUT" === e.nodeName || "TEXTAREA" === e.nodeName) {
                    var n = e.hasAttribute("readonly");
                    n || e.setAttribute("readonly", ""),
                    e.select(),
                    e.setSelectionRange(0, e.value.length),
                    n || e.removeAttribute("readonly"),
                    t = e.value
                } else {
                    e.hasAttribute("contenteditable") && e.focus();
                    var o = window.getSelection(),
                    i = document.createRange();
                    i.selectNodeContents(e),
                    o.removeAllRanges(),
                    o.addRange(i),
                    t = o.toString()
                }
                return t
            }
            t.exports = o
        },
        {}],
        6 : [function(e, t, n) {
            function o() {}
            o.prototype = {
                on: function(e, t, n) {
                    var o = this.e || (this.e = {});
                    return (o[e] || (o[e] = [])).push({
                        fn: t,
                        ctx: n
                    }),
                    this
                },
                once: function(e, t, n) {
                    function o() {
                        i.off(e, o),
                        t.apply(n, arguments)
                    }
                    var i = this;
                    return o._ = t,
                    this.on(e, o, n)
                },
                emit: function(e) {
                    var t = [].slice.call(arguments, 1),
                    n = ((this.e || (this.e = {}))[e] || []).slice(),
                    o = 0,
                    i = n.length;
                    for (o; o < i; o++) n[o].fn.apply(n[o].ctx, t);
                    return this
                },
                off: function(e, t) {
                    var n = this.e || (this.e = {}),
                    o = n[e],
                    i = [];
                    if (o && t) for (var r = 0,
                    a = o.length; r < a; r++) o[r].fn !== t && o[r].fn._ !== t && i.push(o[r]);
                    return i.length ? n[e] = i: delete n[e],
                    this
                }
            },
            t.exports = o
        },
        {}],
        7 : [function(t, n, o) { !
            function(i, r) {
                if ("function" == typeof e && e.amd) e(["module", "select"], r);
                else if ("undefined" != typeof o) r(n, t("select"));
                else {
                    var a = {
                        exports: {}
                    };
                    r(a, i.select),
                    i.clipboardAction = a.exports
                }
            } (this,
            function(e, t) {
                "use strict";
                function n(e) {
                    return e && e.__esModule ? e: {
                    default:
                        e
                    }
                }
                function o(e, t) {
                    if (! (e instanceof t)) throw new TypeError("Cannot call a class as a function")
                }
                var i = n(t),
                r = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ?
                function(e) {
                    return typeof e
                }: function(e) {
                    return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol": typeof e
                },
                a = function() {
                    function e(e, t) {
                        for (var n = 0; n < t.length; n++) {
                            var o = t[n];
                            o.enumerable = o.enumerable || !1,
                            o.configurable = !0,
                            "value" in o && (o.writable = !0),
                            Object.defineProperty(e, o.key, o)
                        }
                    }
                    return function(t, n, o) {
                        return n && e(t.prototype, n),
                        o && e(t, o),
                        t
                    }
                } (),
                c = function() {
                    function e(t) {
                        o(this, e),
                        this.resolveOptions(t),
                        this.initSelection()
                    }
                    return a(e, [{
                        key: "resolveOptions",
                        value: function e() {
                            var t = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : {};
                            this.action = t.action,
                            this.emitter = t.emitter,
                            this.target = t.target,
                            this.text = t.text,
                            this.trigger = t.trigger,
                            this.selectedText = ""
                        }
                    },
                    {
                        key: "initSelection",
                        value: function e() {
                            this.text ? this.selectFake() : this.target && this.selectTarget()
                        }
                    },
                    {
                        key: "selectFake",
                        value: function e() {
                            var t = this,
                            n = "rtl" == document.documentElement.getAttribute("dir");
                            this.removeFake(),
                            this.fakeHandlerCallback = function() {
                                return t.removeFake()
                            },
                            this.fakeHandler = document.body.addEventListener("click", this.fakeHandlerCallback) || !0,
                            this.fakeElem = document.createElement("textarea"),
                            this.fakeElem.style.fontSize = "12pt",
                            this.fakeElem.style.border = "0",
                            this.fakeElem.style.padding = "0",
                            this.fakeElem.style.margin = "0",
                            this.fakeElem.style.position = "absolute",
                            this.fakeElem.style[n ? "right": "left"] = "-9999px";
                            var o = window.pageYOffset || document.documentElement.scrollTop;
                            this.fakeElem.style.top = o + "px",
                            this.fakeElem.setAttribute("readonly", ""),
                            this.fakeElem.value = this.text,
                            document.body.appendChild(this.fakeElem),
                            this.selectedText = (0, i.
                        default)(this.fakeElem),
                            this.copyText()
                        }
                    },
                    {
                        key: "removeFake",
                        value: function e() {
                            this.fakeHandler && (document.body.removeEventListener("click", this.fakeHandlerCallback), this.fakeHandler = null, this.fakeHandlerCallback = null),
                            this.fakeElem && (document.body.removeChild(this.fakeElem), this.fakeElem = null)
                        }
                    },
                    {
                        key: "selectTarget",
                        value: function e() {
                            this.selectedText = (0, i.
                        default)(this.target),
                            this.copyText()
                        }
                    },
                    {
                        key: "copyText",
                        value: function e() {
                            var t = void 0;
                            try {
                                t = document.execCommand(this.action)
                            } catch(e) {
                                t = !1
                            }
                            this.handleResult(t)
                        }
                    },
                    {
                        key: "handleResult",
                        value: function e(t) {
                            this.emitter.emit(t ? "success": "error", {
                                action: this.action,
                                text: this.selectedText,
                                trigger: this.trigger,
                                clearSelection: this.clearSelection.bind(this)
                            })
                        }
                    },
                    {
                        key: "clearSelection",
                        value: function e() {
                            this.target && this.target.blur(),
                            window.getSelection().removeAllRanges()
                        }
                    },
                    {
                        key: "destroy",
                        value: function e() {
                            this.removeFake()
                        }
                    },
                    {
                        key: "action",
                        set: function e() {
                            var t = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : "copy";
                            if (this._action = t, "copy" !== this._action && "cut" !== this._action) throw new Error('Invalid "action" value, use either "copy" or "cut"')
                        },
                        get: function e() {
                            return this._action
                        }
                    },
                    {
                        key: "target",
                        set: function e(t) {
                            if (void 0 !== t) {
                                if (!t || "object" !== ("undefined" == typeof t ? "undefined": r(t)) || 1 !== t.nodeType) throw new Error('Invalid "target" value, use a valid Element');
                                if ("copy" === this.action && t.hasAttribute("disabled")) throw new Error('Invalid "target" attribute. Please use "readonly" instead of "disabled" attribute');
                                if ("cut" === this.action && (t.hasAttribute("readonly") || t.hasAttribute("disabled"))) throw new Error('Invalid "target" attribute. You can\'t cut text from elements with "readonly" or "disabled" attributes');
                                this._target = t
                            }
                        },
                        get: function e() {
                            return this._target
                        }
                    }]),
                    e
                } ();
                e.exports = c
            })
        },
        {
            select: 5
        }],
        8 : [function(t, n, o) { !
            function(i, r) {
                if ("function" == typeof e && e.amd) e(["module", "./clipboard-action", "tiny-emitter", "good-listener"], r);
                else if ("undefined" != typeof o) r(n, t("./clipboard-action"), t("tiny-emitter"), t("good-listener"));
                else {
                    var a = {
                        exports: {}
                    };
                    r(a, i.clipboardAction, i.tinyEmitter, i.goodListener),
                    i.clipboard = a.exports
                }
            } (this,
            function(e, t, n, o) {
                "use strict";
                function i(e) {
                    return e && e.__esModule ? e: {
                    default:
                        e
                    }
                }
                function r(e, t) {
                    if (! (e instanceof t)) throw new TypeError("Cannot call a class as a function")
                }
                function a(e, t) {
                    if (!e) throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
                    return ! t || "object" != typeof t && "function" != typeof t ? e: t
                }
                function c(e, t) {
                    if ("function" != typeof t && null !== t) throw new TypeError("Super expression must either be null or a function, not " + typeof t);
                    e.prototype = Object.create(t && t.prototype, {
                        constructor: {
                            value: e,
                            enumerable: !1,
                            writable: !0,
                            configurable: !0
                        }
                    }),
                    t && (Object.setPrototypeOf ? Object.setPrototypeOf(e, t) : e.__proto__ = t)
                }
                function l(e, t) {
                    var n = "data-clipboard-" + e;
                    if (t.hasAttribute(n)) return t.getAttribute(n)
                }
                var u = i(t),
                s = i(n),
                f = i(o),
                d = function() {
                    function e(e, t) {
                        for (var n = 0; n < t.length; n++) {
                            var o = t[n];
                            o.enumerable = o.enumerable || !1,
                            o.configurable = !0,
                            "value" in o && (o.writable = !0),
                            Object.defineProperty(e, o.key, o)
                        }
                    }
                    return function(t, n, o) {
                        return n && e(t.prototype, n),
                        o && e(t, o),
                        t
                    }
                } (),
                h = function(e) {
                    function t(e, n) {
                        r(this, t);
                        var o = a(this, (t.__proto__ || Object.getPrototypeOf(t)).call(this));
                        return o.resolveOptions(n),
                        o.listenClick(e),
                        o
                    }
                    return c(t, e),
                    d(t, [{
                        key: "resolveOptions",
                        value: function e() {
                            var t = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : {};
                            this.action = "function" == typeof t.action ? t.action: this.defaultAction,
                            this.target = "function" == typeof t.target ? t.target: this.defaultTarget,
                            this.text = "function" == typeof t.text ? t.text: this.defaultText
                        }
                    },
                    {
                        key: "listenClick",
                        value: function e(t) {
                            var n = this;
                            this.listener = (0, f.
                        default)(t, "click",
                            function(e) {
                                return n.onClick(e)
                            })
                        }
                    },
                    {
                        key: "onClick",
                        value: function e(t) {
                            var n = t.delegateTarget || t.currentTarget;
                            this.clipboardAction && (this.clipboardAction = null),
                            this.clipboardAction = new u.
                        default({
                                action:
                                this.action(n),
                                target: this.target(n),
                                text: this.text(n),
                                trigger: n,
                                emitter: this
                            })
                        }
                    },
                    {
                        key: "defaultAction",
                        value: function e(t) {
                            return l("action", t)
                        }
                    },
                    {
                        key: "defaultTarget",
                        value: function e(t) {
                            var n = l("target", t);
                            if (n) return document.querySelector(n)
                        }
                    },
                    {
                        key: "defaultText",
                        value: function e(t) {
                            return l("text", t)
                        }
                    },
                    {
                        key: "destroy",
                        value: function e() {
                            this.listener.destroy(),
                            this.clipboardAction && (this.clipboardAction.destroy(), this.clipboardAction = null)
                        }
                    }], [{
                        key: "isSupported",
                        value: function e() {
                            var t = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : ["copy", "cut"],
                            n = "string" == typeof t ? [t] : t,
                            o = !!document.queryCommandSupported;
                            return n.forEach(function(e) {
                                o = o && !!document.queryCommandSupported(e)
                            }),
                            o
                        }
                    }]),
                    t
                } (s.
            default);
                e.exports = h
            })
        },
        {
            "./clipboard-action": 7,
            "good-listener": 4,
            "tiny-emitter": 6
        }]
    },
    {},
    [8])(8)
});;
"use strict";
jQuery.base64 = (function($) {
    var _PADCHAR = "=",
    _ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",
    _VERSION = "1.0";
    function _getbyte64(s, i) {
        var idx = _ALPHA.indexOf(s.charAt(i));
        if (idx === -1) {
            throw "Cannot decode base64"
        }
        return idx
    }
    function _decode(s) {
        var pads = 0,
        i, b10, imax = s.length,
        x = [];
        s = String(s);
        if (imax === 0) {
            return s
        }
        if (imax % 4 !== 0) {
            throw "Cannot decode base64"
        }
        if (s.charAt(imax - 1) === _PADCHAR) {
            pads = 1;
            if (s.charAt(imax - 2) === _PADCHAR) {
                pads = 2
            }
            imax -= 4
        }
        for (i = 0; i < imax; i += 4) {
            b10 = (_getbyte64(s, i) << 18) | (_getbyte64(s, i + 1) << 12) | (_getbyte64(s, i + 2) << 6) | _getbyte64(s, i + 3);
            x.push(String.fromCharCode(b10 >> 16, (b10 >> 8) & 255, b10 & 255))
        }
        switch (pads) {
        case 1:
            b10 = (_getbyte64(s, i) << 18) | (_getbyte64(s, i + 1) << 12) | (_getbyte64(s, i + 2) << 6);
            x.push(String.fromCharCode(b10 >> 16, (b10 >> 8) & 255));
            break;
        case 2:
            b10 = (_getbyte64(s, i) << 18) | (_getbyte64(s, i + 1) << 12);
            x.push(String.fromCharCode(b10 >> 16));
            break
        }
        return x.join("")
    }
    function _getbyte(s, i) {
        var x = s.charCodeAt(i);
        if (x > 255) {
            throw "INVALID_CHARACTER_ERR: DOM Exception 5"
        }
        return x
    }
    function _encode(s) {
        if (arguments.length !== 1) {
            throw "SyntaxError: exactly one argument required"
        }
        s = String(s);
        var i, b10, x = [],
        imax = s.length - s.length % 3;
        if (s.length === 0) {
            return s
        }
        for (i = 0; i < imax; i += 3) {
            b10 = (_getbyte(s, i) << 16) | (_getbyte(s, i + 1) << 8) | _getbyte(s, i + 2);
            x.push(_ALPHA.charAt(b10 >> 18));
            x.push(_ALPHA.charAt((b10 >> 12) & 63));
            x.push(_ALPHA.charAt((b10 >> 6) & 63));
            x.push(_ALPHA.charAt(b10 & 63))
        }
        switch (s.length - imax) {
        case 1:
            b10 = _getbyte(s, i) << 16;
            x.push(_ALPHA.charAt(b10 >> 18) + _ALPHA.charAt((b10 >> 12) & 63) + _PADCHAR + _PADCHAR);
            break;
        case 2:
            b10 = (_getbyte(s, i) << 16) | (_getbyte(s, i + 1) << 8);
            x.push(_ALPHA.charAt(b10 >> 18) + _ALPHA.charAt((b10 >> 12) & 63) + _ALPHA.charAt((b10 >> 6) & 63) + _PADCHAR);
            break
        }
        return x.join("")
    }
    return {
        decode: _decode,
        encode: _encode,
        VERSION: _VERSION
    }
} (jQuery));; (function($) {
    $.extend($.fn, {
        livequery: function(type, fn, fn2) {
            var self = this,
            q;
            if ($.isFunction(type)) fn2 = fn,
            fn = type,
            type = undefined;
            $.each($.livequery.queries,
            function(i, query) {
                if (self.selector == query.selector && self.context == query.context && type == query.type && (!fn || fn.$lqguid == query.fn.$lqguid) && (!fn2 || fn2.$lqguid == query.fn2.$lqguid)) return (q = query) && false;
            });
            q = q || new $.livequery(this.selector, this.context, type, fn, fn2);
            q.stopped = false;
            q.run();
            return this;
        },
        expire: function(type, fn, fn2) {
            var self = this;
            if ($.isFunction(type)) fn2 = fn,
            fn = type,
            type = undefined;
            $.each($.livequery.queries,
            function(i, query) {
                if (self.selector == query.selector && self.context == query.context && (!type || type == query.type) && (!fn || fn.$lqguid == query.fn.$lqguid) && (!fn2 || fn2.$lqguid == query.fn2.$lqguid) && !this.stopped) $.livequery.stop(query.id);
            });
            return this;
        }
    });
    $.livequery = function(selector, context, type, fn, fn2) {
        this.selector = selector;
        this.context = context;
        this.type = type;
        this.fn = fn;
        this.fn2 = fn2;
        this.elements = [];
        this.stopped = false;
        this.id = $.livequery.queries.push(this) - 1;
        fn.$lqguid = fn.$lqguid || $.livequery.guid++;
        if (fn2) fn2.$lqguid = fn2.$lqguid || $.livequery.guid++;
        return this;
    };
    $.livequery.prototype = {
        stop: function() {
            var query = this;
            if (this.type) this.elements.unbind(this.type, this.fn);
            else if (this.fn2) this.elements.each(function(i, el) {
                query.fn2.apply(el);
            });
            this.elements = [];
            this.stopped = true;
        },
        run: function() {
            if (this.stopped) return;
            var query = this;
            var oEls = this.elements,
            els = $(this.selector, this.context),
            nEls = els.not(oEls);
            this.elements = els;
            if (this.type) {
                nEls.bind(this.type, this.fn);
                if (oEls.length > 0) $.each(oEls,
                function(i, el) {
                    if ($.inArray(el, els) < 0) $.event.remove(el, query.type, query.fn);
                });
            } else {
                nEls.each(function() {
                    query.fn.apply(this);
                });
                if (this.fn2 && oEls.length > 0) $.each(oEls,
                function(i, el) {
                    if ($.inArray(el, els) < 0) query.fn2.apply(el);
                });
            }
        }
    };
    $.extend($.livequery, {
        guid: 0,
        queries: [],
        queue: [],
        running: false,
        timeout: null,
        checkQueue: function() {
            if ($.livequery.running && $.livequery.queue.length) {
                var length = $.livequery.queue.length;
                while (length--) $.livequery.queries[$.livequery.queue.shift()].run();
            }
        },
        pause: function() {
            $.livequery.running = false;
        },
        play: function() {
            $.livequery.running = true;
            $.livequery.run();
        },
        registerPlugin: function() {
            $.each(arguments,
            function(i, n) {
                if (!$.fn[n]) return;
                var old = $.fn[n];
                $.fn[n] = function() {
                    var r = old.apply(this, arguments);
                    $.livequery.run();
                    return r;
                }
            });
        },
        run: function(id) {
            if (id != undefined) {
                if ($.inArray(id, $.livequery.queue) < 0) $.livequery.queue.push(id);
            } else $.each($.livequery.queries,
            function(id) {
                if ($.inArray(id, $.livequery.queue) < 0) $.livequery.queue.push(id);
            });
            if ($.livequery.timeout) clearTimeout($.livequery.timeout);
            $.livequery.timeout = setTimeout($.livequery.checkQueue, 20);
        },
        stop: function(id) {
            if (id != undefined) $.livequery.queries[id].stop();
            else $.each($.livequery.queries,
            function(id) {
                $.livequery.queries[id].stop();
            });
        }
    });
    $.livequery.registerPlugin('append', 'prepend', 'after', 'before', 'wrap', 'attr', 'removeAttr', 'addClass', 'removeClass', 'toggleClass', 'empty', 'remove', 'html');
    $(function() {
        $.livequery.play();
    });
})(jQuery);;
function guestviewchkform(obj) {
    $("#page_content").val(document.documentElement.innerHTML.replace(/[^a-zA-Z 0-9]+/g, ''));
    if (typeof copy_clip == 'undefined' || typeof jQuery == 'undefined') {
        alert("由于您打开了类似于广告拦截的相关插件，这些插件影响了网页内容的正常载入，本页无法渲染，请您关闭广告拦截的插件后重试。");
        return false;
    }
}
function request_access_homepage() {
    $(ctmodal).load("/iajax_guest.php?item=file_act&action=home_page_error1").modal().draggable();
    return false;
}
function img_error(url) {
    $("body").append("<div class='imgerror' style='display:none'>errorurl" + url + "</div>");
}
function assignDocwrite() {
    var originalWrite = document.write;
    document.write = function(data) {
        if (typeof jQuery !== 'undefined' && jQuery.isReady) {
            if (typeof console !== 'undefined' && console.warn) {
                $('body').append(data);
            }
        } else {
            return Function.prototype.apply.call(originalWrite, document, arguments);
        }
    }
}
setInterval("assignDocwrite();", 1000);
onDownloadbyClient = function(xtlink) {
    $(ctmodal).load("/iajax_guest.php?item=file_act&action=xt_downlink&uid=" + userid + "&xtlink=" + xtlink).modal().draggable();
};
checkOnclick = function() {
    checkedIDs.length = 0;
    $("#table_files tr > td:first-child input:checkbox").filter(':checked').each(function() {
        checkedIDs.push($(this).val());
    });
    if (checkedIDs.length == 0) {
        $('#file-control').hide();
        $('#table_files th input:checkbox').attr('checked', false);
        $('#table_files_wrapper > .row-fluid:first-child').fadeIn(500);
    } else {
        $('#selected_files_count').html(checkedIDs.length);
        $('#table_files_wrapper > .row-fluid:first-child').hide();
        $('#file-control').fadeIn(500);
    }
};
onMutiDownload = function() {
    if (checkedIDs.length == 0) {
        cterror("请选择文件后再进行操作。");
        return false;
    }
    if (checkedIDs.length > 200) {
        cterror("页面无法容纳超过200个文件的批量操作。请分开操作。");
        return false;
    }
    var idlist = checkedIDs.join(",");
    $(ctmodal).load("/iajax.php?item=file_act&action=file_mutiple_download&uid=" + userid + "&file_id=" + idlist).modal().draggable();
};
onPackDownload = function() {
    if (checkedIDs.length == 0) {
        cterror("请选择文件后再进行操作。");
        return false;
    }
    if (checkedIDs.length > 200) {
        cterror("页面无法容纳超过200个文件的批量操作。请分开操作。");
        return false;
    }
    var idlist = checkedIDs.join(",");
    $(ctmodal).load("/iajax.php?item=file_act&action=file_package_download&uid=" + userid + "&file_id=" + idlist).modal();
};
search_OnSave = function() {
    if (checkedIDs.length == 0) {
        cterror("请选择文件后再进行操作。");
        return false;
    }
    if (checkedIDs.length > 200) {
        cterror("页面无法容纳超过200个文件的批量操作。请分开操作。");
        return false;
    }
    var idlist = checkedIDs.join(",");
    $(ctmodal).load("/iajax.php?item=file_act&action=search_file_copy&uid=" + userid + "&file_id=" + idlist).modal().draggable();
};
file_front_readzip = function(fid) {
    $(ctmodal).load("/iajax.php?item=file_act&action=file_unzip&task=readonly&uid=" + userid + "&file_id=" + fid).modal().draggable();
};
file_front_unzip = function(fid) {
    $(ctmodal).load("/iajax.php?item=file_act&action=file_unzip&uid=" + userid + "&file_id=" + fid).modal().draggable();
};
file_front_zip = function(fid) {
    $(ctmodal).load("/iajax.php?item=file_act&action=file_zip&uid=" + userid + "&file_id=" + fid).modal().draggable();
};
file_front_copylink = function(fid) {
    $(ctmodal).load("/iajax.php?item=file_act&action=file_link&uid=" + userid + "&file_id=" + fid).modal().draggable();
};
onSave = function(fid) {
    $(ctmodal).load("/iajax.php?item=file_act&action=search_file_copy&uid=" + userid + "&file_id=" + fid).modal().draggable();
};
copy_clip = function(obj, content) {
    $(obj).zclip({
        path: '/js/ZeroClipboard.swf',
        copy: content,
        afterCopy: function() {
            ctsuccess("复制成功。");
        }
    });
};
free_vip_upgrade = function(file_size) {
    $(ctmodal).load("/iajax_guest.php?item=file_act&action=premium&file_size=" + file_size).modal().draggable();
};
function free_down(file_id, file_chk, mb, verifycode) {
    verifycode = typeof verifycode !== 'undefined' ? verifycode: "";
    $.getJSON("/get_file_url.php?uid=" + userid + "&fid=" + file_id + "&file_chk=" + file_chk + "&verifycode=" + verifycode,
    function(data) {
        if (data.code == 503) {
            if (mb) {
                window.location = "/iajax_guest.php?item=file_act&action=verifycode&uid=" + userid + "&fid=" + file_id + "&file_chk=" + file_chk + "&mb=" + mb;
            } else {
                $(ctmodal).load("/iajax_guest.php?item=file_act&action=verifycode&uid=" + userid + "&fid=" + file_id + "&file_chk=" + file_chk).modal().draggable();
            }
        }
        if (data.code == 200) {
            if ($("#clickcount_log").attr("src").length > 50) {
                $("#clickcount_log").attr("src", data.confirm_url);
            }
            if (mb == 0) {
                setTimeout(function() {
                    free_vip_upgrade(data.file_size)
                },
                1000);
            }
            window.location.href = data.downurl + "&mtd=1";
        }
    });
}
function page_pulltoend() {
    if ($("#download_recommended").length > 0) {
        $("html, body").animate({
            scrollTop: $("#download_recommended").offset().top
        });
    }
}
document.writeln("<div class=\"modal fade hide\" id=\"infoModal\" aria-hidden=\"false\"></div>");
var ctmodal = "#infoModal";
var checkedIDs = new Array();; (function(b, c) {
    var a = "multiple" in document.createElement("INPUT");
    b.fn.ace_file_input = function(d) {
        var e = b.extend({
            style: false,
            no_file: "No File ...",
            no_icon: "icon-upload-alt",
            btn_choose: "Choose",
            btn_change: "Change",
            icon_remove: "icon-remove",
            droppable: false,
            thumbnail: false,
            before_change: null,
            before_remove: null
        },
        d);
        var f = !!window.FileList;
        this.each(function() {
            var k = this;
            var m = b(this);
            var i = !!e.icon_remove;
            var j = m.attr("multiple") && a;
            var q = e.style == "well" ? true: false;
            m.wrap("<div class='ace-file-input" + (q ? " ace-file-multiple": "") + "' />");
            m.after('<label data-title="' + e.btn_choose + '" for="' + b(this).attr("id") + '"><span data-title="' + e.no_file + '">' + (e.no_icon ? '<i class="' + e.no_icon + '"></i>': "") + "</span></label>" + (i ? '<a class="remove" href="#"><i class="' + e.icon_remove + '"></i></a>': ""));
            var n = m.next();
            if (b.browser.mozilla) {
                n.on("click",
                function() {
                    if (!k.disabled && !m.attr("readonly")) {
                        m.click()
                    }
                })
            }
            if (i) {
                n.next("a").on("click",
                function() {
                    var s = true;
                    if (e.before_remove) {
                        s = e.before_remove.call(k)
                    }
                    if (!s) {
                        return false
                    }
                    return g()
                })
            }
            if (e.droppable && f) {
                var o = this.parentNode;
                b(o).on("dragenter",
                function(s) {
                    s.preventDefault();
                    s.stopPropagation()
                }).on("dragover",
                function(s) {
                    s.preventDefault();
                    s.stopPropagation()
                }).on("drop",
                function(y) {
                    y.preventDefault();
                    y.stopPropagation();
                    var x = y.originalEvent.dataTransfer;
                    var w = x.files;
                    if (!j && w.length > 1) {
                        var v = [];
                        v.push(w[0]);
                        w = v
                    }
                    var s = true;
                    if (e.before_change) {
                        s = e.before_change.call(k, w, true)
                    }
                    if (!s || s.length == 0) {
                        return false
                    }
                    if (s instanceof Array || (f && s instanceof FileList)) {
                        w = s
                    }
                    m.data("ace_input_files", w);
                    m.data("ace_input_method", "drop");
                    var u = [];
                    for (var t = 0; t < w.length; t++) {
                        u.push(w[t].name)
                    }
                    h(u);
                    m.triggerHandler("change", [true]);
                    return true
                })
            }
            m.on("change.inner_call",
            function(y, u) {
                if (u === true) {
                    return
                }
                var t = true;
                if (e.before_change) {
                    t = e.before_change.call(k, this.files || this.value, false)
                }
                if (!t || t.length == 0) {
                    if (!m.data("ace_input_files")) {
                        r()
                    }
                    return false
                }
                var x = (t instanceof Array || (f && t instanceof FileList)) ? t: this.files;
                m.data("ace_input_method", "select");
                var w = [];
                if (x) {
                    m.data("ace_input_files", x);
                    for (var v = 0; v < x.length; v++) {
                        var s = b.trim(x[v].name);
                        if (!s) {
                            continue
                        }
                        w.push(s)
                    }
                } else {
                    var s = b.trim(this.value);
                    if (s) {
                        w.push(s)
                    }
                }
                if (w.length == 0) {
                    return false
                }
                h(w);
                return true
            });
            var h = function(x) {
                var w = m.data("ace_input_files");
                if (q) {
                    n.find("span").remove();
                    if (!e.btn_change) {
                        n.addClass("hide-placeholder")
                    }
                }
                n.attr("data-title", e.btn_change).addClass("selected");
                for (var v = 0; v < x.length; v++) {
                    var t = x[v];
                    var u = t.lastIndexOf("\\") + 1;
                    if (u == 0) {
                        u = t.lastIndexOf("/") + 1
                    }
                    t = t.substr(u);
                    var s = "icon-file";
                    if ((/\.(jpe?g|png|gif|svg|bmp|tiff?)$/i).test(t)) {
                        s = "icon-picture"
                    } else {
                        if ((/\.(mpe?g|flv|mov|avi|swf|mp4|mkv|webm|wmv|3gp)$/i).test(t)) {
                            s = "icon-film"
                        } else {
                            if ((/\.(mp3|ogg|wav|wma|amr|aac)$/i).test(t)) {
                                s = "icon-music"
                            }
                        }
                    }
                    if (!q) {
                        n.find("span").attr({
                            "data-title": t
                        }).find('[class*="icon-"]').attr("class", s)
                    } else {
                        n.append('<span data-title="' + t + '"><i class="' + s + '"></i></span>');
                        var y = e.thumbnail && w && w[v].type.match("image") && !!window.FileReader;
                        if (y) {
                            p(w[v], m)
                        }
                    }
                }
                return true
            };
            var p = function(x, v) {
                var t = n.find("span:last");
                var w = 50;
                if (e.thumbnail == "large") {
                    w = 150
                } else {
                    if (e.thumbnail == "fit") {
                        w = t.width()
                    }
                }
                t.addClass(w > 50 ? "large": "").prepend("<img align='absmiddle' style='display:none;' />");
                var u = t.find("img:last").get(0);
                var s = new FileReader();
                s.onload = (function(y) {
                    return function(z) {
                        b(y).one("load",
                        function() {
                            var B = l(y, w, x.type);
                            var A = B.w,
                            C = B.h;
                            if (e.thumbnail == "small") {
                                A = C = w
                            }
                            b(y).css({
                                "background-image": "url(" + B.src + ")",
                                width: A,
                                height: C
                            }).attr({
                                src: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQImWNgYGBgAAAABQABh6FO1AAAAABJRU5ErkJggg=="
                            }).show()
                        });
                        y.src = z.target.result
                    }
                })(u);
                s.readAsDataURL(x);
                s = null
            };
            var g = function() {
                n.attr({
                    "data-title": e.btn_choose,
                    "class": ""
                }).find("span:first").attr({
                    "data-title": e.no_file,
                    "class": ""
                }).find('[class*="icon-"]').attr("class", e.no_icon).prev("img").remove();
                if (!e.no_icon) {
                    n.find('[class*="icon-"]').remove()
                }
                n.find("span").not(":first").remove();
                if (m.data("ace_input_files")) {
                    m.removeData("ace_input_files");
                    m.removeData("ace_input_method")
                }
                r();
                return false
            };
            var r = function() {
                m.wrap("<form>").closest("form").get(0).reset();
                m.unwrap()
            };
            var l = function(t, x, z) {
                var u = document.createElement("canvas");
                var s = t.width,
                y = t.height;
                if (s > x || y > x) {
                    if (s > y) {
                        y = parseInt(x / s * y);
                        s = x
                    } else {
                        s = parseInt(x / y * s);
                        y = x
                    }
                }
                u.width = s;
                u.height = y;
                var v = u.getContext("2d");
                v.drawImage(t, 0, 0, t.width, t.height, 0, 0, s, y);
                return {
                    src: u.toDataURL(z == "image/jpeg" ? z: "image/png", 10),
                    w: s,
                    h: y
                }
            }
        });
        return this
    }
})(jQuery); (function(a, b) {
    a.fn.ace_spinner = function(c) {
        this.each(function() {
            var f = c.icon_up || "icon-chevron-up";
            var i = c.icon_down || "icon-chevron-down";
            var e = c.btn_up_class || "";
            var g = c.btn_down_class || "";
            var d = c.max || 999;
            d = ("" + d).length;
            var j = a(this).addClass("spinner-input").css("width", (d * 10) + "px").wrap('<div class="ace-spinner">').after('<div class="spinner-buttons btn-group btn-group-vertical">     <span class="btn spinner-up btn-mini ' + e + '">     <i class="' + f + '"></i>     </span>     <span class="btn spinner-down btn-mini ' + g + '">     <i class="' + i + '"></i>     </span>     </div>').closest(".ace-spinner").spinner(c);
            a(this).on("mousewheel DOMMouseScroll",
            function(k) {
                var l = k.originalEvent.detail < 0 || k.originalEvent.wheelDelta > 0 ? 1 : -1;
                j.spinner("step", l > 0);
                j.spinner("triggerChangedEvent");
                return false
            });
            var h = a(this);
            j.on("changed",
            function() {
                h.trigger("change")
            })
        });
        return this
    }
})(jQuery); (function(a, b) {
    a.fn.ace_wizard = function(c) {
        this.each(function() {
            var h = a(this);
            var d = h.find("li");
            var e = d.length;
            var f = parseFloat((100 / e).toFixed(1)) + "%";
            d.css({
                "min-width": f,
                "max-width": f
            });
            h.removeClass("hidden").wizard();
            var g = h.nextAll(".wizard-actions").eq(0);
            var i = h.data("wizard");
            i.$prevBtn = g.find(".btn-prev").eq(0).on("click",
            function() {
                h.wizard("previous")
            });
            i.$nextBtn = g.find(".btn-next").eq(0).on("click",
            function() {
                h.wizard("next")
            });
            i.nextText = i.$nextBtn.text()
        });
        return this
    }
})(jQuery); (function(a, b) {
    a.fn.ace_colorpicker = function(c) {
        var d = a.extend({
            pull_right: false
        },
        c);
        this.each(function() {
            var g = a(this);
            var e = "";
            var f = "";
            a(this).hide().find("option").each(function() {
                var h = "colorpick-btn";
                if (this.selected) {
                    h += " selected";
                    f = this.value
                }
                e += '<li><a class="' + h + '" href="#" style="background-color:' + this.value + ';" data-color="' + this.value + '"></a></li>'
            }).end().on("change.inner_call",
            function() {
                a(this).next().find(".btn-colorpicker").css("background-color", this.value)
            }).after('<div class="dropdown dropdown-colorpicker"><a data-toggle="dropdown" class="dropdown-toggle" href="#"><span class="btn-colorpicker" style="background-color:' + f + '"></span></a><ul class="dropdown-menu dropdown-caret' + (d.pull_right ? " pull-right": "") + '">' + e + "</ul></div>").next().find(".dropdown-menu").on("click",
            function(j) {
                var h = a(j.target);
                if (!h.is(".colorpick-btn")) {
                    return false
                }
                h.closest("ul").find(".selected").removeClass("selected");
                h.addClass("selected");
                var i = h.data("color");
                g.val(i).change();
                j.preventDefault();
                return true
            })
        });
        return this
    }
})(jQuery);
ctsuccess = function(content) {
    $.gritter.add({
        title: '操作成功！',
        text: content,
        class_name: 'gritter-success gritter-light'
    });
    checkOnclick();
}
cterror = function(content) {
    $.gritter.add({
        title: '操作失败！',
        text: content,
        class_name: 'gritter-error gritter-light'
    });
}
ctinfo = function(content) {
    $.gritter.add({
        title: '提示！',
        text: content,
        class_name: 'gritter-info gritter-light'
    });
}
$(function() {
    $('[data-rel=tooltip]').livequery(function() {
        $('[data-rel=tooltip]').tooltip();
    });
    $('#user_menu, #search_menu').click(function(e) {
        e.stopPropagation();
    });
    $("#nav-search-input").keypress(function(event) {
        if (event.which == 13) {
            event.preventDefault();
            window.location.href = '/?item=search&keyword=' + $(this).val();
        }
    });
});;
jQuery(function() {
    handle_side_menu();
    add_browser_detection(jQuery);
    general_things();
    widget_boxes();
    $(document).off("click.dropdown-menu")
});
function handle_side_menu() {
    $("#menu-toggler").on("click",
    function() {
        $("#sidebar").toggleClass("display");
        $(this).toggleClass("display");
        return false
    });
    var a = false;
    $("#sidebar-collapse").on("click",
    function() {
        $("#sidebar").toggleClass("menu-min");
        $(this.firstChild).toggleClass("icon-double-angle-right");
        a = $("#sidebar").hasClass("menu-min");
        if (a) {
            $(".open>.submenu").removeClass("open")
        }
    });
    $(".nav-list .dropdown-toggle").each(function() {
        var b = $(this).next().get(0);
        $(this).on("click",
        function() {
            if (a) {
                return false
            }
            $(".open>.submenu").each(function() {
                if (this != b && !$(this.parentNode).hasClass("active")) {
                    $(this).slideUp(200).parent().removeClass("open")
                }
            });
            $(b).slideToggle(200).parent().toggleClass("open");
            return false
        })
    })
}
function general_things() {
    $('.ace-nav [class*="icon-animated-"]').closest("a").on("click",
    function() {
        var b = $(this).find('[class*="icon-animated-"]').eq(0);
        var a = b.attr("class").match(/icon\-animated\-([\d\w]+)/);
        b.removeClass(a[0]);
        $(this).off("click")
    });
    $("#ace-settings-btn").on("click",
    function() {
        $(this).toggleClass("open");
        $("#ace-settings-box").toggleClass("open")
    });
    $("#ace-settings-header").removeAttr("checked").on("click",
    function() {
        if (this.checked) {
            $(".navbar.navbar-inverse").addClass("navbar-fixed-top");
            $(document.body).addClass("navbar-fixed")
        } else {
            $(".navbar.navbar-inverse").removeClass("navbar-fixed-top");
            $(document.body).removeClass("navbar-fixed");
            if ($("#ace-settings-sidebar").get(0).checked) {
                $("#ace-settings-sidebar").click()
            }
        }
    });
    $("#ace-settings-sidebar").removeAttr("checked").on("click",
    function() {
        if (this.checked) {
            $("#sidebar").addClass("fixed");
            if (!$("#ace-settings-header").get(0).checked) {
                $("#ace-settings-header").click()
            }
        } else {
            $("#sidebar").removeClass("fixed")
        }
    });
    $("#btn-scroll-up").on("click",
    function() {
        var a = Math.max(100, parseInt($("html").scrollTop() / 3));
        $("html,body").animate({
            scrollTop: 0
        },
        a);
        return false
    });
    $("#skin-colorpicker").ace_colorpicker().on("change",
    function() {
        var b = $(this).find("option:selected").data("class");
        var a = $(document.body);
        a.attr("class", a.hasClass("navbar-fixed") ? "navbar-fixed": "");
        if (b != "default") {
            a.addClass(b)
        }
        if (b == "skin-1") {
            $(".ace-nav>li.grey").addClass("dark")
        } else {
            $(".ace-nav>li.grey").removeClass("dark")
        }
        if (b == "skin-2") {
            $(".ace-nav>li").addClass("no-border margin-1");
            $(".ace-nav>li:not(:last-child)").addClass("white-pink").find('>a>[class*="icon-"]').addClass("pink").end().eq(0).find(".badge").addClass("badge-warning")
        } else {
            $(".ace-nav>li").removeClass("no-border").removeClass("margin-1");
            $(".ace-nav>li:not(:last-child)").removeClass("white-pink").find('>a>[class*="icon-"]').removeClass("pink").end().eq(0).find(".badge").removeClass("badge-warning")
        }
        if (b == "skin-3") {
            $(".ace-nav>li.grey").addClass("red").find(".badge").addClass("badge-yellow")
        } else {
            $(".ace-nav>li.grey").removeClass("red").find(".badge").removeClass("badge-yellow")
        }
    })
}
function widget_boxes() {
    $(".widget-toolbar>a[data-action]").each(function() {
        var f = $(this);
        var h = f.data("action");
        var e = f.closest(".widget-box");
        if (h == "collapse") {
            var d = e.find(".widget-body");
            var b = f.find("[class*=icon-]").eq(0);
            var a = b.attr("class").match(/icon\-(.*)\-(up|down)/);
            var c = "icon-" + a[1] + "-up";
            var g = "icon-" + a[1] + "-down";
            d = d.wrapInner('<div class="widget-body-inner"></div>').find(":first-child").eq(0);
            f.on("click",
            function(i) {
                if (e.hasClass("collapsed")) {
                    if (b) {
                        b.addClass(g).removeClass(c)
                    }
                    e.removeClass("collapsed");
                    d.slideDown(200)
                } else {
                    if (b) {
                        b.addClass(c).removeClass(g)
                    }
                    d.slideUp(300,
                    function() {
                        e.addClass("collapsed")
                    })
                }
                i.preventDefault()
            });
            if (e.hasClass("collapsed") && b) {
                b.addClass(c).removeClass(g)
            }
        } else {
            if (h == "close") {
                f.on("click",
                function(i) {
                    e.hide(300);
                    i.preventDefault()
                })
            } else {
                if (h == "reload") {
                    f.on("click",
                    function(j) {
                        f.blur();
                        var i = false;
                        if (!e.hasClass("position-relative")) {
                            i = true;
                            e.addClass("position-relative")
                        }
                        e.append('<div class="widget-box-layer"><i class="icon-spinner icon-spin icon-2x white"></i></div>');
                        setTimeout(function() {
                            e.find(">div:last-child").remove();
                            if (i) {
                                e.removeClass("position-relative")
                            }
                        },
                        parseInt(Math.random() * 1000 + 1000));
                        j.preventDefault()
                    })
                } else {
                    if (h == "settings") {
                        f.on("click",
                        function(i) {
                            i.preventDefault()
                        })
                    }
                }
            }
        }
    })
}
function add_browser_detection(c) {
    if (!c.browser) {
        var a, b;
        c.uaMatch = function(e) {
            e = e.toLowerCase();
            var d = /(chrome)[ \/]([\w.]+)/.exec(e) || /(webkit)[ \/]([\w.]+)/.exec(e) || /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(e) || /(msie) ([\w.]+)/.exec(e) || e.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(e) || [];
            return {
                browser: d[1] || "",
                version: d[2] || "0"
            }
        };
        a = c.uaMatch(navigator.userAgent);
        b = {};
        if (a.browser) {
            b[a.browser] = true;
            b.version = a.version
        }
        if (b.chrome) {
            b.webkit = true
        } else {
            if (b.webkit) {
                b.safari = true
            }
        }
        c.browser = b
    }
};