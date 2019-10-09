<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!--留言信息主体-->
<div class="msgBox" id="msgBox">

</div>
<div class="fd"><a href="javascript:;" onclick="vip.comment.showReply();" id="reply_more" class="btn green arr-down1">${L:l(lan,"查看更多") }</a></div>
<!--发布留言信息-->
<div class="pubBox" id="pubBox">
    <div class="f_line d1 clearfloat"><span>${L:l(lan,"评论内容") }${L:l(lan,"：") }</span><textarea name="message" mytitle="${L:l(lan,'请填写评论内容(16-600字符)。') }" errormsg="${L:l(lan,'评论内容16-600字符。') }" pattern="limit(16,600)"></textarea></div>
    <div class="f_line d2 clearfloat">
            <img onclick="getCode(0)" style="cursor: pointer;" src="/imagecode/get-28-85-39" id="idCode0"/>
            <input name="code" type="text" position="s" mytitle="${L:l(lan,'请填写验证码') }" errormsg="${L:l(lan,'验证码错误') }" pattern="limit(4,4)"/>
            <span>${L:l(lan,"验证码") }${L:l(lan,"：") }</span>
    </div>
    <div class="f_line d3 clearfloat"><a href="javascript:vip.comment.reply(0, 'pubBox')" class="btn green publish" title="${L:l(lan,'发布评论') }">${L:l(lan,'发布评论') }</a></div>
</div>
<input type="hidden" id="typeId" name="typeId" value="${typeId}"/>
<!--发布留言信息 END-->
<script type="text/javascript">
$(function(){
	$("#pubBox").Ui();
	vip.comment.showReply(1);
});
function getCode(index){
   var id=numberID();
   var src = "/imagecode/get-28-85-39-"+id;
   var objs = $("input[name='code']").prev("img");
   objs.each(function(){
	   $(this).attr("src", src);
   });
} 
	
vip.comment = {
	nextRIndex : 1,
	showReply : function(){
		var _this = this;
		vip.ajax({//type=2  首页认购类型
			url : "/comment/moreReply?pId=${product.id}&typeId="+$("#typeId").val()+"&page="+_this.nextRIndex,
			dataType : "text",
			suc : function(xml) {
				$("#msgBox").append(xml);
				vip.list.defaultDiv = "msgBox";
				vip.comment.adminOper.showButton();
				_this.nextRIndex++;
			}
		});
	},
	reply : function(parentId, div){
		var _this = this;
		if(!vip.user.checkLogin())
			return;
		var datas = FormToStr(div);
		if(datas == null){
			return;
		}
		
		var actionUrl = "/comment/doReply?productId=${product.id}&pid="+parentId+"&typeId="+$("#typeId").val();
		vip.ajax( {
			formId : div,
			url : actionUrl,
			dataType : "json",
			suc : function(xml) {
				//_this.showReply($("#pages").val());
				var obj = xml.datas;
				if(parentId > 0){
					$("#replyBox_"+parentId).before(_this.getReplyContentHtml(parentId, obj));
				}else{
					if($("#msgBox").html().indexOf("${L:l(lan,'暂时没有评论！')}")>=0){
						$("#msgBox").html(_this.getReplyContentHtml(parentId, obj));
					}else{
						$("#msgBox").append(_this.getReplyContentHtml(parentId, obj));
					}
				}
				vip.comment.adminOper.showButton();
				_this.resetText(parentId, div);
			},
			err : function(xml) {
				$("input[name='code']").val("");
				getCode(parentId);
				JuaBox.info(xml.des);
			}
		});
	},
	replyS : function(id){
	
		var editObj = $("#EditBox_" + id);
		if(editObj.hasClass("edit")){
			editObj.slideUp("fast").removeClass("edit");
			editObj.empty();
		}
		
		var replyObj = $("#replyBox_" + id);
		if(replyObj.hasClass("down")){
			replyObj.slideUp("fast").removeClass("down");
		}else{
			replyObj.addClass("down");
			if (replyObj.html().length <= 0)
				replyObj.append(this.getReplyHtml(id));
			getCode(id);
			replyObj.Ui();
			replyObj.slideDown("fast");
			//location.href="#a_"+id;
			$("#replyDiv_"+id).ScrollTo({Top:500});
		}
	},
	replyE : function(id){
		var replyObj = $("#replyBox_" + id);
		if(replyObj.hasClass("down")){
			replyObj.slideUp("fast").removeClass("down");
		}
		
		var editObj = $("#EditBox_" + id);
		if(editObj.hasClass("edit")){
			editObj.slideUp("fast").removeClass("edit");
			editObj.empty();
		}else{
			editObj.addClass("edit");
			var content = $("#userMsg_"+id+" .bd p").html();
			editObj.append(this.getEditHtml(id, content));
			getCode(id);
			editObj.Ui();
			editObj.slideDown("fast");
		}
	},
	deleReply : function(id){
		var _this = this;
		Ask2({Msg:"${L:l(lan,'您确定要删除该回复吗？')}", call:function(){
			var actionUrl = "/comment/deleReply?id="+id;
			vip.ajax( {
				url : actionUrl,
				dataType : "json",
				suc : function(xml) {
					Close();
					$("#userMsg_"+id).remove();
					//_this.showReply($("#pageNo").val());
				},
				err : function(xml) {
					JuaBox.info(xml.des);
				}
			});
		}});
	},
	editReply : function(id, div){
		if(!vip.user.checkLogin()){
			return;
		}
	
		var _this = this;
		Ask2({Msg:"${L:l(lan,'您确定要修改该回复内容吗？')}", call:function(){
			var actionUrl = "/comment/doReply?id="+id;
			vip.ajax( {
				formId : div,
				url : actionUrl,
				dataType : "json",
				suc : function(xml) {
					Close();
					var innerHtm = $("#editDiv_"+id).find("textarea[name='message']").val();
					$("#userMsg_"+id + " p").html(innerHtm);
					$("#EditBox_"+id).empty().removeClass("edit");
					//_this.showReply($("#pageNo").val());
					getCode();
				},
				err : function(xml) {
					JuaBox.info(xml.des);
					getCode();
				}
			});
		}});
	},
	getReplyContentHtml : function(pid, obj){
		var htmls = "";
		var replyC = "";
		if(pid > 0){
			replyC = "reply";
		}else{
			htmls+='<div class="msglist">';
		}
		var uname = "";
		if($.cookie(JsCommon.aid) > 0){
			uname = '<a href="# '+obj.userId+'">'+obj.userName+'</a>';
		}
		
		htmls+='<div class="userMsg '+replyC+' clearfloat" id="userMsg_'+obj.id+'">'
				    +'<div class="userIco ld"><a href=""><img src="${static_domain }/statics/img/faq_say2.png" /></a></div>'
				    +'<div class="userSay rd">'
				       +'<div class="hd clearfloat">'
				         +'<span class="ld">'+uname+' '+obj.postTime+'</span>'
				         +'<span class="rd reply_oper" ids="'+obj.id+'"></span>'
				       +'</div>'
				       +'<div class="bd clearfloat">'
				         +'<p>'+obj.content+'</p>'
				       +'</div>'
				    +'</div>'
				  +'</div>'
				  +'<div class="replyBox" id="EditBox_'+obj.id+'"></div>';
			if(pid == 0){
				htmls += '<div class="replyBox" id="replyBox_'+obj.id+'"></div></div>';				
			}		  
		
		return htmls;
	},
	getReplyHtml : function(id){
		var htmls = "";
		htmls+='<div class="f_line d1 clearfloat" id="replyDiv_'+id+'"><span>${L:l(lan,'回复内容') }${L:l(lan,'：') }</span><textarea name="message" class="rd" mytitle="${L:l(lan,'请填写回复内容(16-600字符)。')}" errormsg="${L:l(lan,'回复内容16-600字符。')}" pattern="limit(16,600)"></textarea></div>'
			     +'<div class="f_line d2 clearfloat">'
			         +'<img onclick="getCode('+id+')" style="cursor: pointer;" src="/imagecode/get-28-85-39" id="idCode'+id+'"/>'
			         +'<input name="code" type="text" position="s" mytitle="${L:l(lan,'请填写验证码') }" errormsg="${L:l(lan,'验证码错误') }" pattern="limit(4,4)"/>'
			         +'<span>${L:l(lan,"验证码")}${L:l(lan,"：")}</span>'
			     +'</div>'
			     +'<div class="f_line d3 clearfloat"><a href="javascript:vip.comment.reply('+id+', \'replyBox_'+id+'\')" class="btn green reply" title="${L:l(lan,'回复') }">${L:l(lan,'回复') }</a></div>';
		return htmls;
	},
	getEditHtml : function(id, content){
		var htmls = "";
		htmls+='<div class="f_line d1 clearfloat" id="editDiv_'+id+'"><span>${L:l(lan,'编辑内容') }${L:l(lan,'：') }</span><textarea name="message" class="rd" mytitle="${L:l(lan,'请填写回复内容(16-600字符)。')}" errormsg="${L:l(lan,'请填写回复内容(16-600字符)。')}" pattern="limit(16,600)">'+content+'</textarea></div>'
			     +'<div class="f_line d2 clearfloat">'
			         +'<img onclick="getCode('+id+')" style="cursor: pointer;" src="/imagecode/get-28-85-39" id="idCode'+id+'"/>'
			         +'<input name="code" type="text" position="s" mytitle="${L:l(lan,'请填写验证码') }" errormsg="${L:l(lan,'验证码错误') }" pattern="limit(4,4)"/>'
			         +'<span>${L:l(lan,"验证码")}${L:l(lan,"：")}</span>'
			     +'</div>'
			     +'<div class="f_line d3 clearfloat"><a href="javascript:vip.comment.editReply('+id+', \'EditBox_'+id+'\')" class="btn green reply" title="${L:l(lan,'修改') }">${L:l(lan,'修改') }</a></div>';
		return htmls;
	},
	resetText : function(parentId, div){
		$("textarea[name='message']").val("");
		$("input[name='code']").val("");
		getCode(parentId);
	},
	adminOper : {
		showButton : function(){
			if($.cookie(JsCommon.aid) <= 0){
				$(".userMsg .hd .ld a").remove();
				return;
			}
			$(".userMsg .hd .ld a").show();
			var _this = this;
			$("#msgBox .commen_oper").each(function(i){
				var id = $(this).attr("ids");
				$(this).append(_this.editHtml(id));
				$(this).append(_this.deleHtml(id));
				$(this).removeClass("commen_oper");
			});
			$("#msgBox .reply_oper").each(function(){
				var id = $(this).attr("ids");
				$(this).append(_this.editHtml(id));
				$(this).append(_this.deleHtml(id));
				$(this).removeClass("reply_oper");
			});
		},
		editHtml : function(id){
			return '<a href="javascript:vip.comment.replyE('+id+')" class="edit_btn">${L:l(lan,'编辑') }</a>';
		},
		deleHtml : function(id){
			return '<a href="javascript:vip.comment.deleReply('+id+')" class="dele_btn">${L:l(lan,'删除') }</a>';
		}
	}
}
</script>
