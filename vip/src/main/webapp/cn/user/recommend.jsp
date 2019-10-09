<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>推荐人来交易领取抽奖号</title>
<jsp:include page="/common/head.jsp" />
<script type="text/javascript" src="${static_domain }/statics/js/common/zclip.js"></script>
<style>
.Toolbar { width: 100%; text-align: left; }
.Toolbar h2 { color:#333; border-bottom: 1px solid #ddd; background-color:#f6f6f6; height: 36px; line-height: 36px; text-indent: 12px; font-size: 14px; font-weight: bold; }
.ps1-close { color: #666; cursor: pointer; font-size: 14px; height: 16px; line-height: 16px; position: absolute; right: 10px; text-align: center; top: 10px; width: 16px; }
.ps1-close:hover { color: #f60; text-decoration: none; }

/*t推荐人*/
body { background-color:#fff;}

.b_tuijianren table { background-color:#a8dbe6; font-family:"微软雅黑"; margin:10px auto; }
.b_tuijianren table th { padding:0; font-size:18px; text-align:center; height:35px; line-height:35px; background-color:#d9edf7;}
.b_tuijianren table td { font-size:14px; text-align:center; height:35px; line-height:35px; background-color:#fff;}

.h_tuijian .dz { margin:10px 30px; padding:20px 50px; background-color:#ffae46; text-align:left;}
input#chargeAddrest { width:90%; height:50px; margin-bottom:15px; padding:5px 10px; font-size:24px; color:#39F; margin-top:10px; }
.h_tuijian .noloin { margin:60px 30px; text-align:center;}
.h_tuijian .noloin a { width:550px; background-color: #0f7dca; color:#fff; font-size:24px; padding:10px 20px; border-radius:5px; text-shadow:0px 0px 2px #000; display:inline-block;}
.h_tuijian .noloin a { *display:block;}
.h_tuijian .noloin a:hover { background-color:#f2ab11; text-decoration:none;}

.wzt { width:96%; margin:0 auto; font-family:"微软雅黑"; line-height:40px; text-align:center; background-color:#e6e6e6; color:#333; font-size:24px;}
</style>
</head>
<body>
<div class="header">
  <div class="Toolbar">
    <h2>推荐人来交易领取抽奖号</h2>
    <div class="ps1-close" onclick="parent.Close();" style="font-weight:bold;">×</div>
  </div>
</div>

<div class="h_tuijian">

<div class="noloin" id="help3"><a href="javascript:;" id="recomlog">登录后才可查看推荐人地址和被推荐人信息</a></div>
<div id="help2" style="display:none;">
<div class="ctips" style="width:85%; margin-top:10px; margin-bottom:10px; clear:both;">
      <p><span>您的推荐人地址：</span>
      <input type="text" id="chargeAddrest" value=""/>
      </p>
      <p><span>推荐人规则：</span>
      复制您的推荐人地址给其他人，如果他访问您的推荐地址并注册交易，他<b class="c_red">每通过交易获得一个抽奖号，您也可以获得一个抽奖号</b>，一次推荐永久享受！赶快推荐给您的小伙伴让他来交易吧，每天至少一个币等着您！
      </p>
</div>

</div>

<div class="b_tuijianren" id="table" style="display:none;">
<div class="wzt">===您推荐的用户===</div>
<table width="96%" border="0" cellspacing="1" cellpadding="1" align="center">
  <tr>
    <th width="50%">用户名</th>
    <th width="50%">注册时间</th>
  </tr>
  <tbody id="tUser">
    <tr>
        <td class="air-tips" colspan="2">
        <p>还没有推荐人，快去推荐吧~</p>
        </td>
    </tr>
  </tbody>
</table>
</div>

<br/>
<br/>


</div>

<script type="text/javascript">
	var move=false;
	$(function(){
		
		try{
			var oldDomain=document.domain;
			var ind=oldDomain.indexOf('vip');
			document.domain = oldDomain.substring(ind,oldDomain.length)
		} catch(msg) {
			document.domain = 'vip.com';  
		}
		
		$("#recomlog").Iframe({
			Title:"用户登录",
			Url:"/user/loginAuthentication",  
			isShowIframeTitle:true,
			isShowClose:false,
			Width:450, 
			Height:350, 
			scrolling:"no",
			fromObj:T$("recomlog"),
			call:function(data){ 
		   	} 	
		});
		
		var cooUid = $.cookie(vip.cookiKeys.uid);
		if(cooUid!=null && cooUid.length){
			$("#chargeAddrest").val("${vip_domain}/register/"+cooUid);
			$("#help2").show();
			$("#help3").hide();
			$("#table").show();
		}else{
			$("#chargeAddrest")
			$("#help2").hide();
			$("#help3").show();
			$("#table").hide();
		}
		
		showU();
	});
	
	function showU(){
		$.getJSON("/user/getRecommendUser",function(json) {
				var list = json.datas;
				var shtml = "";
				if(list.length > 0){
					for(var i = 0; i < list.length; i ++){
						var u = list[i];
					shtml +=
						'<tr>'
						+'<td>'+u.userName+'</td>'
							+'<td>'+u.domain+'</td>'
						+'</tr>';
					}
					$("#tUser").html(shtml);
				}
			}
		);
	}
</script>
</body>
</html>
