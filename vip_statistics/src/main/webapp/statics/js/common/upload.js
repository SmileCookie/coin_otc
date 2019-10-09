(function($) {
/***
 * 兼容jquery 1.9
 */
$.browser = {};
$.browser.mozilla = /firefox/.test(navigator.userAgent.toLowerCase());
$.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
$.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
$.browser.msie = /msie/.test(navigator.userAgent.toLowerCase());


$.fn.ajaxSubmit = function(options) {
    if (typeof options == 'function')
        options = { success: options };

    options = $.extend({
        url:  this.attr('action') || window.location,
        type: this.attr('method') || 'GET'
    }, options || {});

    var a = this.formToArray(options.semantic);

    // give pre-submit callback an opportunity to abort the submit
    if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) return this;

    // fire vetoable 'validate' event
    var veto = {};
    $.event.trigger('form.submit.validate', [a, this, options, veto]);
    if (veto.veto)
        return this;

    var q = $.param(a);//.replace(/%20/g,'+');

    if (options.type.toUpperCase() == 'GET') {
        options.url += (options.url.indexOf('?') >= 0 ? '&' : '?') + q;
        options.data = null;  // data is null for 'get'
    }
    else
        options.data = q; // data is the query string for 'post'

    var $form = this, callbacks = [];
    if (options.resetForm) callbacks.push(function() { $form.resetForm(); });
    if (options.clearForm) callbacks.push(function() { $form.clearForm(); });

    // perform a load on the target only if dataType is not provided
    if (!options.dataType && options.target) {
        var oldSuccess = options.success;// || function(){};
        callbacks.push(function(data) {
            $(options.target).attr("innerHTML", data).evalScripts().each(oldSuccess, arguments);
        });
    }
    else if (options.success)
        callbacks.push(options.success);

    options.success = function(data, status) {
        for (var i=0, max=callbacks.length; i < max; i++)
            callbacks[i](data, status, $form);
    };

    // are there files to upload?
    var files = $('input:file', this).fieldValue();
    var found = false;
    for (var j=0; j < files.length; j++)
        if (files[j])
            found = true;

    if (options.iframe || found) // options.iframe allows user to force iframe mode
        fileUpload();
    else
        $.ajax(options);

    // fire 'notify' event
    $.event.trigger('form.submit.notify', [this, options]);
    return this;

    // private function for handling file uploads (hat tip to YAHOO!)
    function fileUpload() {
        var form = $form[0];
        var opts = $.extend({}, $.ajaxSettings, options);

        var id = 'jqFormIO' + $.fn.ajaxSubmit.counter++;
        var $io = $('<iframe  id="' + id + '" name="' + id + '" />');
        var io = $io[0];
        var op8 = $.browser.opera && window.opera.version() < 9;
        //if ($.browser.msie || op8)
        	//io.src = 'javascript:false;document.write("");';

        $io.css({ position: 'absolute', top: '-1000px', left: '-1000px' });
        var xhr = { // mock object
            responseText: null,
            responseXML: null,
            status: 0,
            statusText: 'n/a',
            getAllResponseHeaders: function() {},
            getResponseHeader: function() {},
            setRequestHeader: function() {}
        };

        var g = opts.global;
        // trigger ajax global events so that activity/block indicators work like normal
        if (g && ! $.active++) $.event.trigger("ajaxStart");
        if (g) $.event.trigger("ajaxSend", [xhr, opts]);
        var cbInvoked = 0;
        var timedOut = 0;
        // take a breath so that pending repaints get some cpu time before the upload starts
        setTimeout(function() {
        	document.domain = options.domain;
            $io.appendTo('body');

            // jQuery's event binding doesn't work for iframe events in IE
            io.attachEvent ? io.attachEvent('onload', cb) : io.addEventListener('load', cb, false);

            if(html5() && xhr.upload){
            	xhr.upload.addEventListener("progress", process, false);
            }
            // make sure form attrs are set
            var encAttr = form.encoding ? 'encoding' : 'enctype';
            var t = $form.attr('target');
            $form.attr({
                target:   id,
                method:  'POST',
                encAttr: 'multipart/form-data',
                action:   opts.url
            });
            // support timout
            if (opts.timeout)
                setTimeout(function() { timedOut = true; cb(); }, opts.timeout);
            form.submit();
            $form.attr('target', t); // reset target
        }, 10);

        function process(){
        	alert(0);
        }

        function html5() {
	 		if (typeof(Worker) !== "undefined") {
	 			return true;
	 		}else {
				return false;
	 		}
		}

        function cb() {
            if (cbInvoked++) return;
            io.detachEvent ? io.detachEvent('onload', cb) : io.removeEventListener('load', cb, false);
            var ok = true;
            try {
                if (timedOut) throw 'timeout';
                var data, doc;
                doc = io.contentWindow ? io.contentWindow.document : io.contentDocument ? io.contentDocument : io.document;
                xhr.responseText = doc.body ? doc.body.innerHTML : null;
                xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;

                if (opts.dataType == 'json' || opts.dataType == 'script') {
                    var ta = doc.getElementsByTagName('textarea')[0];
                    data = ta ? ta.value : xhr.responseText;
                    if (opts.dataType == 'json')
                        eval("data = " + data);
                    else
                        $.globalEval(data);
                }
                else if (opts.dataType == 'xml') {
                    data = xhr.responseXML;
                    if (!data && xhr.responseText != null)
                        data = toXml(xhr.responseText);
                }
                else {
                    data = xhr.responseText;
                }
            }catch(e){
                ok = false;
                //$.handleError(opts, xhr, 'error', e);
                alert("catch error:"+e.toString());
            }

            // ordering of these callbacks/triggers is odd, but that's how $.ajax does it
            if (ok) {
                opts.success(data, 'success');
                if (g) $.event.trigger("ajaxSuccess", [xhr, opts]);
            }
            if (g) $.event.trigger("ajaxComplete", [xhr, opts]);
            if (g && ! --$.active) $.event.trigger("ajaxStop");
            if (opts.complete) opts.complete(xhr, ok ? 'success' : 'error');

            // clean up
            setTimeout(function() {
                $io.remove();
                xhr.responseXML = null;
            }, 100);
        };

        function toXml(s, doc) {
            if (window.ActiveXObject) {
                doc = new ActiveXObject('Microsoft.XMLDOM');
                doc.async = 'false';
                doc.loadXML(s);
            }
            else
                doc = (new DOMParser()).parseFromString(s, 'text/xml');
            return (doc && doc.documentElement && doc.documentElement.tagName != 'parsererror') ? doc : null;
        };
    };
};
$.fn.ajaxSubmit.counter = 0; // used to create unique iframe ids


$.fn.ajaxForm = function(options) {
    return this.ajaxFormUnbind().submit(submitHandler).each(function() {
        // store options in hash
        this.formPluginId = $.fn.ajaxForm.counter++;
        $.fn.ajaxForm.optionHash[this.formPluginId] = options;
        $(":submit,input:image", this).click(clickHandler);
    });
};

$.fn.ajaxForm.counter = 1;
$.fn.ajaxForm.optionHash = {};

function clickHandler(e) {
    var $form = this.form;
    $form.clk = this;
    if (this.type == 'image') {
        if (e.offsetX != undefined) {
            $form.clk_x = e.offsetX;
            $form.clk_y = e.offsetY;
        } else if (typeof $.fn.offset == 'function') { // try to use dimensions plugin
            var offset = $(this).offset();
            $form.clk_x = e.pageX - offset.left;
            $form.clk_y = e.pageY - offset.top;
        } else {
            $form.clk_x = e.pageX - this.offsetLeft;
            $form.clk_y = e.pageY - this.offsetTop;
        }
    }
    // clear form vars
    setTimeout(function() { $form.clk = $form.clk_x = $form.clk_y = null; }, 10);
};

function submitHandler() {
    // retrieve options from hash
    var id = this.formPluginId;
    var options = $.fn.ajaxForm.optionHash[id];
    $(this).ajaxSubmit(options);
    return false;
};

/**
 * ajaxFormUnbind unbinds the event handlers that were bound by ajaxForm
 *
 * @name   ajaxFormUnbind
 * @return jQuery
 * @cat    Plugins/Form
 * @type   jQuery
 */
$.fn.ajaxFormUnbind = function() {
    this.unbind('submit', submitHandler);
    return this.each(function() {
        $(":submit,input:image", this).unbind('click', clickHandler);
    });

};

$.fn.formToArray = function(semantic) {
    var a = [];
    if (this.length == 0) return a;

    var form = this[0];
    var els = semantic ? form.getElementsByTagName('*') : form.elements;
    if (!els) return a;
    for(var i=0, max=els.length; i < max; i++) {
        var el = els[i];
        var n = el.name;
        if (!n) continue;

        if (semantic && form.clk && el.type == "image") {
            // handle image inputs on the fly when semantic == true
            if(!el.disabled && form.clk == el)
                a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
            continue;
        }

        var v = $.fieldValue(el, true);
        if (v && v.constructor == Array) {
            for(var j=0, jmax=v.length; j < jmax; j++)
                a.push({name: n, value: v[j]});
        }
        else if (v !== null && typeof v != 'undefined')
            a.push({name: n, value: v});
    }

    if (!semantic && form.clk) {
        // input type=='image' are not found in elements array! handle them here
        var inputs = form.getElementsByTagName("input");
        for(var i=0, max=inputs.length; i < max; i++) {
            var input = inputs[i];
            var n = input.name;
            if(n && !input.disabled && input.type == "image" && form.clk == input)
                a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
        }
    }
    return a;
};

$.fn.formSerialize = function(semantic) {
    //hand off to jQuery.param for proper encoding
    return $.param(this.formToArray(semantic));
};

$.fn.fieldSerialize = function(successful) {
    var a = [];
    this.each(function() {
        var n = this.name;
        if (!n) return;
        var v = $.fieldValue(this, successful);
        if (v && v.constructor == Array) {
            for (var i=0,max=v.length; i < max; i++)
                a.push({name: n, value: v[i]});
        }
        else if (v !== null && typeof v != 'undefined')
            a.push({name: this.name, value: v});
    });
    //hand off to jQuery.param for proper encoding
    return $.param(a);
};

$.fn.fieldValue = function(successful) {
    for (var val=[], i=0, max=this.length; i < max; i++) {
        var el = this[i];
        var v = $.fieldValue(el, successful);
        if (v === null || typeof v == 'undefined' || (v.constructor == Array && !v.length))
            continue;
        v.constructor == Array ? $.merge(val, v) : val.push(v);
    }
    return val;
};

$.fieldValue = function(el, successful) {
    var n = el.name, t = el.type, tag = el.tagName.toLowerCase();
    if (typeof successful == 'undefined') successful = true;

    if (successful && (!n || el.disabled || t == 'reset' || t == 'button' ||
        (t == 'checkbox' || t == 'radio') && !el.checked ||
        (t == 'submit' || t == 'image') && el.form && el.form.clk != el ||
        tag == 'select' && el.selectedIndex == -1))
            return null;

    if (tag == 'select') {
        var index = el.selectedIndex;
        if (index < 0) return null;
        var a = [], ops = el.options;
        var one = (t == 'select-one');
        var max = (one ? index+1 : ops.length);
        for(var i=(one ? index : 0); i < max; i++) {
            var op = ops[i];
            if (op.selected) {
                // extra pain for IE...
                var v = $.browser.msie && !(op.attributes['value'].specified) ? op.text : op.value;
                if (one) return v;
                a.push(v);
            }
        }
        return a;
    }
    return el.value;
};

$.fn.clearForm = function() {
    return this.each(function() {
        $('input,select,textarea', this).clearFields();
    });
};

$.fn.clearFields = $.fn.clearInputs = function() {
    return this.each(function() {
        var t = this.type, tag = this.tagName.toLowerCase();
        if (t == 'text' || t == 'password' || tag == 'textarea')
            this.value = '';
        else if (t == 'checkbox' || t == 'radio')
            this.checked = false;
        else if (tag == 'select')
            this.selectedIndex = -1;
    });
};

$.fn.resetForm = function() {
    return this.each(function() {
        // guard against an input with the name of 'reset'
        // note that IE reports the reset function as an 'object'
        if (typeof this.reset == 'function' || (typeof this.reset == 'object' && !this.reset.nodeType))
            this.reset();
    });
};

})(jQuery);


var outerSupport = {
	curFileUploadIndex : 0
};

 ///检测文件数是否满足
 function fileNumIsRight(obj , min , hiddenId){
	//errormsg="你至少上传'+settings.minNum+'张图片"
	$("#"+hiddenId).attr("errormsg" , "你至少上传"+min+"张图片");

	if(min <= 0){
		return true;
	}else{
		var paths = $("#"+hiddenId).val(),
			size = 0;
		if(paths && paths.length > 0)
			size = paths.split(" ").length
		if(size >= min){
			return true;
		}else{
			return false;
		}
	}
 }
 //检测是否有上传中的图片
 function fileIsUploading(obj,hiddenId){
	 var isTrue = true,
	 	 hiddenInput = $("#"+hiddenId);
	 	 upFileForms = hiddenInput.parent("div");//当前上传容器
	 if(upFileForms){
		$("#"+hiddenId).attr("errormsg" , "图片上传中，请稍后提交...");
		var forms = upFileForms.find("form");
		forms.each(function(i){
			var tform = $(this),
			stat = tform.find("input[name='file_upload_stat']").val();//0 未上传 1上传中 2上传完成
			if(stat == 1){
				isTrue = false;
				return false
			}
		});
	 }
	 return isTrue;
 }

 (function($){
	$.fn.initFileUpload=function(options){
		var ptc = window.location.protocol,
			myServer = '';
		myServer = ptc + "//img1."+document.domain;
		var settings = {
			server : myServer,
			domain : document.domain,
			initShowNum : 2,//初始化显示上传数量
			isProcess : true,//是否显示上传进度,
			needAdd : true,//需要添加
			maxNum : 6,
			noUpDefaultText : "请上传",//默认未上传的显示文本
			noUpShowTexts : [],//自定义未上传的显示文本
			upUrl : "/fileaction",//上传 的url
			getServer : function(){
				return this.server + this.upUrl;
			},
			isEdite : false,
			pics : "",
			picsPathHiddenName : "upload_pics_path_hidden_name",//存储上传图片的隐藏表单名称
			minNum : 0, //至少上传的文件数量
			plan : "goods",
			userType : 1,////上传者的所属类型  1.默认普通网站用户  2.管理员
			isPicFile : false,///是否限制为图片上传 ，true时只能上传图片文件 ， false 时不限
			picExt : ".jpg.jpeg.gif.png.bmp",//"jpg", "bmp", "gif", "png","jpeg"
			savePicSize : false,///图片名称中是否保存图片尺寸
			className : '',
			bgSize : '88x88',
			isAuth : false,
			success:function(param){

			}
		}
		options = $.extend(settings, options);

		outerSupport.curFileUploadIndex++;

		if(outerSupport.curFileUploadIndex > 1){//有大于一个组件同时使用
			if(settings.picsPathHiddenName == options.picsPathHiddenName){//用户没有自定义picsPathHiddenName
				options.picsPathHiddenName += "_"+outerSupport.curFileUploadIndex;//防止表单名称重复
			}
		}
//		fileNumIsRight.picsPathHiddenName = settings.picsPathHiddenName;
		var $this = $(this),
			hiddenInputId = 'upload_pics_path_hidden_id_' + outerSupport.curFileUploadIndex,
			userId = options.userType == 1 ? $.cookie(vip.cookiKeys.uid) : $.cookie(vip.cookiKeys.uname),
			baseFileHtm = '<form name="uploadForm_1" action="'+settings.getServer()+'" enctype="multipart/form-data" method="post" target="uploadTar"><div class="item '+settings.className+'"><span class="preview"><a>'+settings.noUpDefaultText+'</a><span class="deal-file"><em class="left"></em><em class="right"></em><em class="del"></em></span></span><span class="upload "><input type="hidden" value="0" name="file_upload_stat"><input type="hidden" value="'+settings.plan+'" name="plan_task_name"><input type="hidden" value="'+userId+'" name="userId"><input type="hidden" value="'+options.userType+'" name="userType"><input type="hidden" value="'+options.savePicSize+'" name="savePicSize"><input type="hidden" value="'+options.isAuth+'" name="auth"><a href="javascript:;" ><input type="file" name="_fma.pu._0.ima"></a></span>'+
			'<input type="hidden" value="" name="fileUrl" class="J_PicUrl"></div></form>',
			initShowHtm = '<input style="width:0px;height:0px;margin:0 0 0 100px;border:none;padding:0px;" position="s" type="text" pattern="fileIsUploading(\''+hiddenInputId+'\');fileNumIsRight('+settings.minNum+' , \''+hiddenInputId+'\')" errormsg="你至少上传'+settings.minNum+'张图片" id="'+hiddenInputId+'" name="'+settings.picsPathHiddenName+'">',
			upFilesInputs = null,
			addBtnHtm = '<div class="item"><span class="preview"></span><span class="add"><a href="javascript:;" class="addBtnA btn blue">添加</a></span></div>';

		//$this.removeAttr("style");/////移除默认样式

		var editePics = settings.pics;
		var picsArr = [];
		if(editePics.length > 0){
			picsArr = editePics.split(" ");
			if(picsArr.length > 0){
				settings.initShowNum = picsArr.length;
				settings.isEdite = true;
			}
		}

		for(var i=0;i<settings.initShowNum;i++){
			var curHtm=baseFileHtm;
			if(settings.noUpShowTexts[i]){
				curHtm=baseFileHtm.replace(settings.noUpDefaultText,settings.noUpShowTexts[i]);
			}
			initShowHtm+=curHtm;
		}
		$this.append(initShowHtm);
		if(settings.isEdite && picsArr.length > 0){
			$("#"+hiddenInputId).val(settings.pics);///隐藏域赋值
			var forms = $this.find("form");
			forms.each(function(i){
				var pi = toHex(picsArr[i].split("_")[0]),
					folder = pi.split("%3A")[2];
				var tform = $(this),
				curFile = tform.find("input:file");
				previewA = $(this).find(".preview a");
				var urlb = '';
				if(options.isAuth){//https://img1.vip.com/picauth?file=313A313A31_31-88x88.jpg
					urlb = options.server + "/picauth?file=" + picsArr[i].replace('.' , '-'+options.bgSize+'.');
				}else{
					urlb = options.server + "/up/"+folder+"/s/" + picsArr[i].replace('.' , '-'+options.bgSize+'.');
				}
				previewA.css({background:'url("'+ urlb +'") no-repeat scroll 50% 50% transparent',border:"1px solid #FF8E43"}).text("上传完成");
	         	tform.find("input:hidden[name='fileUrl']").val(picsArr[i]);
	         	//has-up  已上传按钮
	         	var upA=tform.find(".upload a"),
	         	dealFileBtns=previewA.next(".deal-file");
	         	upA.removeClass().addClass("has-up");
	         	checkLrBtn(curFile);
	         	//deal-file
	         	//regMouseEv(upA,dealFileBtns);
	         	//regMouseEv(previewA,dealFileBtns);
	         	//regMouseEv(dealFileBtns,dealFileBtns);
			});
		}

		if(settings.needAdd){
			$this.append(addBtnHtm);
			$this.find(".addBtnA").click(function(){
				if($this.find("form").length < settings.maxNum){
					$(this).parents(".item").before(baseFileHtm);
					var nf = $(this).parents(".item").prev("form").find("input:file");
					bindChange(nf);
					if($this.find("form").length == settings.maxNum)$(this).parents(".item").remove();
				}else{
					$(this).parents(".item").remove();
				}
			});
		}

		upFilesInputs=$this.find("input:file");
		upFilesInputs.each(function(){
			bindChange($(this));
		});

		function bindChange(curFile){
			var previewA=curFile.parents(".upload").prev(".preview").find("a"),
				curForm=curFile.parents("form[target='uploadTar']"),
				curStatInput = curForm.find("input[name='file_upload_stat']"),
				name = curFile.val();
			curFile.change(function(){
				///判断是否有用户登录
				if(options.userType == 1 && !vip.user.checkLogin()){
					return;
				}
				if(options.isPicFile){
					var fileType = curFile.val().substring(curFile.val().lastIndexOf(".") + 1).toLocaleLowerCase();
					if(options.picExt.indexOf(fileType) < 0){
						Wrong("请确认您的上传是否为图片");
						return;
					}
				}

				curStatInput.val(1);
				if(settings.isProcess){//上传进度条
					previewA.text("开始上传");
					setTimeout(function(){
						getProcess(previewA , options.server);
					}, 1000);
				}else{
					previewA.addClass("ajaxing").text("");
				}

				var ops = {
				  domain : settings.domain ,
				  type:"POST",
			      url: settings.getServer(),
			      dataType: 'json',
			      success: function(res) {
					 curStatInput.val(2);
			         if(res.isSuc){
			        	if(isPic(name)){
			        		var bg = res.small;
			        		bg = bg.replace("&amp;" , "&");
			        		if(settings.bgSize != '88x88'){
			        			bg = bg.replace("88x88" , settings.bgSize);
			        		}
			        		var protocal = bg.substring(0, bg.indexOf("://"));

			        		var srcProtocal = options.server.substring(0, options.server.indexOf("://"));

			        		if(protocal != srcProtocal){
			        			bg = bg.replace(protocal, srcProtocal);
			        		}
			        		previewA.css({background:'url("'+ bg +'") no-repeat scroll 50% 50% transparent',border:"1px solid #FF8E43"}).text("上传完成");
			        	}else{
			        		previewA.css({border:"1px solid #FF8E43"}).text("上传完成");
			        	}

			         	curForm.find("input:hidden[name='fileUrl']").val(res.fileName);
			         	//has-up  已上传按钮
			         	var upA=curFile.parent("a"),
			         	dealFileBtns=previewA.next(".deal-file");
			         	upA.removeClass().addClass("has-up");
			         	checkLrBtn(curFile);
			         	//deal-file
			         	//regMouseEv(upA,dealFileBtns);
			         	//regMouseEv(previewA,dealFileBtns);
			         	//regMouseEv(dealFileBtns,dealFileBtns);

			         	$("#"+hiddenInputId).val(allPath($this));///重新赋值
			         	if(fileNumIsRight(null , settings.minNum)){
			         		$(".tipsy-s").remove();
			         	}
			         	options.success(res);
			         }else{
			        	previewA.css({background:'#FFFFFF',border:"1px solid #FF0000"}).text("上传失败");
			         	Wrong(res.alertInfo);
				     }
				   },
				   error:function(error){
					   curStatInput.val(0);
                       //alert("lasterror:"+error.toString());
                   }
				 };

 				 curForm.ajaxSubmit(ops);
			});
		}

	   function isPic(name){///判断上传文件是否为图片
			var ext = name.substr(name.indexOf("."));
			return options.picExt.indexOf(ext) >= 0;
	   }

	   function checkLrBtn(curFile){//移动按钮事件
			var curForm=curFile.parents("form"),
			leftBtn=curForm.find(".deal-file .left"),
			rightBtn=curForm.find(".deal-file .right");
			valid(curForm);
			//left button
			leftBtn.click(function(){
				lPrevForm=curForm.prev("form"),
				curForm.after(lPrevForm);
				valid(curForm);
				valid(lPrevForm);
			});
			//right button
			rightBtn.click(function(){
				rNextForm=curForm.next("form"),
				curForm.before(rNextForm);
				valid(curForm);
				valid(rNextForm);
			});
		}

		function toHex(data){
			if(data == 'null' || data.length <= 0){return ''}
			if (data.length % 2) return '';
			var tmp='';
			for(i=0;i<data.length;i+=2){
				tmp += '%' + data.charAt(i) + data.charAt(i+1);
			}
			return decodeURI(decodeURI(tmp));
		}

		function valid(curForm){
			validBtn(curForm,true);
			validBtn(curForm,false);
		}

		function validBtn(curForm,isLeft){//button valid of curForm
			if(!curForm||curForm.length<=0)return;
			var lPrevForm=curForm.prev("form"),
				rNextForm=curForm.next("form"),
				leftBtn=curForm.find(".deal-file .left"),
				rightBtn=curForm.find(".deal-file .right"),
				avilabel=true;
			if(isLeft){//valid left btn
				if(lPrevForm.length<=0){
					leftBtn.css({backgroundPosition:"-54px -14px"});
					avilabel=false;
				}else{
					leftBtn.css({backgroundPosition:"-54px -2px"});
				}
			}else{//vilid right button

				if(rNextForm.length<=0){//valid right btn
					rightBtn.css({backgroundPosition:"-66px -14px"});
					avilabel=false;
				}else{
					rightBtn.css({backgroundPosition:"-66px -2px"});
				}
			}
			return avilabel;
		}

		function regMouseEv(obj,target){
			var timer=null,lazy=100;
			obj.mouseover(function(){
				clearTimeout(timer);
				timer=setTimeout(function(){
					target.show();
				},lazy);
         	}).mouseleave(function(){
         		clearTimeout(timer);
				timer=setTimeout(function(){
					target.hide();
				},lazy);
         	});
		}

		function getProcess(show , server){
			$.getJSON(server+"/filepro?jsoncallback=?",{"userId":1}, function(msg) {
			 	 $("#des").append("Data Saved: ,isSuc:"+msg.hasUp+",isGo:"+msg.totalUp+",items:"+msg.items+"<br>");
			     if(!upComplete(msg.hasUp,msg.totalUp)){//没有上传完成 继续监听
			     	setTimeout(function(){
			     		getProcess(show);
			     	}, 1000);
			     }
		 	});
    		//是否上传完成
    		function upComplete(hasUp,totalUp){
    			var pro=((hasUp/totalUp).toFixed(1))*100;
    			//显示已上传的百分比
    			if(hasUp>=totalUp){
    				show.text("上传完成");
    				return true;
    			}else{
    				show.text(pro+"%");
    			}
    			return false;
    		}
		}
	};
	$.fn.getFilesPath=function(){
		var $this = $(this);
		return allPath($this);
	}
	function allPath(container){
		var $this = container,
			path='';
		$this.find("form").each(function(i){
			var fileUrl=$(this).find("input:hidden[name='fileUrl']").val();
			if(fileUrl && fileUrl.length>0){
				path+=' '+fileUrl;
			}
		});
		if(path.length>0){
			path=path.substr(1);
		}
		return path;
	}
})(jQuery);
