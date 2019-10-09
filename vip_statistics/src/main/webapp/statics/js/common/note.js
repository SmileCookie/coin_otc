var noteA=true;
$(function(){
	if(noteA)
		vip.note.lastNew();
});

vip.note = {
	allRead : function(){
		$.getJSON(vip.mainDomain + "/news/lastid?callback=?&num=",  function(json) {
			var _last = json.last;
			$.cookie(vip.cookiKeys.note, _last[0], { expires: 10, path: '/', domain: 'vip.com', secure: true });
		});
		$(".menu-bar-top").height("-=40");
		//_this.lastNew();
	},
	lastNew : function(){
		_this = this;
		var prevId = $.cookie(vip.cookiKeys.note);
		prevId = prevId==null?"":prevId;
		$.getJSON(vip.mainDomain + "/news/lastnote?callback=?&id="+prevId+"&num="+numberID(),  function(json) {
			var _last = json.last;
			if(_last.length > 0){
				var htmS = 
				'<p><a href="javascript:vip.note.allRead();" style="padding:0 10px; float:right;">【全部标记已读】</a><span>网站公告：</span><a href="'+vip.mainDomain+'/news/show-'+_last[0]+'-proclamation" title="'+_last[1]+'" target="_blank">'+_last[1]+'</a><a href="'+vip.mainDomain+'/news-proclamation" target="_blank">【更多】</a>'
					+'</p><div class="close1" title="关闭">×</div>';
				
				var ie6=!-[1,]&&!window.XMLHttpRequest;
				if (ie6) {$("#ie6").slideDown(500);};//IE6
				$("#h_import").html(htmS).show();//公告显示
				$(".menu-bar-top").height("+=40");
				_this.bindClose(_last[0]);
			}
		});
	},
	closeNote : function(id){
		$(".close1").closest('.ctips').slideUp(100);
		$.cookie(vip.cookiKeys.note, id, { expires: 10, path: '/', domain: 'vip.com', secure: true });
		$(".menu-bar-top").height("-=40");
		
	},
	bindClose : function(id){
		_this = this;
		$(".close1").click(function(){
			_this.closeNote(id);
			_this.lastNew();
		});
		$("#h_import a").click(function(){
			_this.closeNote(id);
		});
	}
}