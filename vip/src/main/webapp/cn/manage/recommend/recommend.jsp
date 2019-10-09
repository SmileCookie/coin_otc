<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<title>推荐人管理-${WEB_NAME }-${WEB_TITLE }</title>
<jsp:include page="/common/head.jsp" />
<script type="text/javascript">
 $(function(){
	 if((navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)))
	 {
	     $("body").addClass("mob"); 
	 }
});
</script>

<style>
body { background:#FFF; }
.bk-onekey { background:#FFF; }
.navbar { margin-bottom:0px; }
.recomwban { background:url(${static_domain }/statics/img/v2/recommend/rembanner2.jpg) top center no-repeat #ffd401; width:100%;   height:615px;  }

.relistpic { width:1170px; overflow:hidden; }
.relistpic div { float:left; width:390px;   }
.relistpic div img { float:left; }
.relistpic div .ld { float:left; width:160px;  }
.relistpic div .rd { width:200px; }
.relistpic div .rd img,.relistpic div .rd span { display:block; }
.relistpic div .rd span { width:200px; text-align:left; padding:5px 10px; font-size:14px;   }
.bk-recom h3 { padding:40px 0px; margin:0px 0px 0px 0px;font-size:22px; font-weight:100; color:#54687b; }
.rebd > div{ overflow:hidden; margin-bottom:30px; height:290px; width:49%; float:left; font-size:14px; }
.rebd div .lileft { float:left; width:280px; }
.rebd div .limid { float:left; width:254px; padding-right:30px; text-align:left; color:#54687b;  }
.rebd div .liright { float:left; width:545px;  }
.rebd div .limid h4 { text-align:left; padding:40px 0px 0px 0px; font-size:20px; }
.rebd div .liright {  height:70px; margin-top:0px; font-size:20px;  }
.rebd div .liright .pinput { width:515px; height:52px; background:#FFF; border-radius:3px; border:1px solid #f0f1f2; margin-left:15px; overflow:hidden; }
.rebd div .liright .pinput input { font-size:28px; color:#ee3726; border:none; background:none; height:52px; line-height:52px; width:430px; float:left; margin-left:10px; }
.rebd div .liright .pinput a {  display:inline-block; height:28px; width:65px; font-size:20px; line-height:28px; text-align:center; background:#ee3726; color:#FFF; border-radius:3px; margin-top:10px; font-weight:100; cursor:pointer; }
.rebd div .liright .pinput a:hover { text-decoration: none; background:#df2d1c; }
.rebd div .liright .recontli h5 { font-size:22px; padding-top:5px; height:40px; line-height:40px; }
.rebd div.list02 .liright { padding-top:0px; text-align:left;  }
.rebd div.list02 .liright p a { display:block; margin:15px auto 0px auto; width:180px; height:38px; line-height:35px; border:1px solid #df2d1c; border-radius:4px; font-size:20px; cursor:pointer; }
.rebd div.list02 .liright p a i { margin-right:5px; }
.rebd div.list02 .liright p a:hover { color:#FFF; text-decoration: none; background:#df2d1c;  }
.rebd div.list03 .liright .recontli { padding-top:0px; color:#54687b;  }
.rebd div.list03 .liright .recontli input { color:#df2d1c; border:1px solid #f0f1f2; width:180px; background:#FFF; border-radius:5px; margin:0px 10px; height:40px; line-height:40px; padding-left:10px;  }
.retgli span { height:70px; display:block; float:left; padding-top:10px;   }
.retgli span img { margin-left:50px; margin-right:40px; margin-top:0px;   }
.rebd a.lrbtn {  display:inline-block; height:50px; width:230px; font-size:20px; line-height:50px; text-align:center; background:#f6f6f6; color:#df2d1c; border-radius:3px; margin-top:10px; font-weight:100; cursor:pointer; border:1px solid #e8e8e8;  }
.rebd a.lrbtn:hover { text-decoration: none; background:#df2d1c; color:#FFF; }
.recontli img { margin-top:10px; margin-bottom:10px; display:block; }
.paihang span { font-size:16px; display:block; width:100%; height:40px; line-height:40px; text-align:center; font-size:18px; font-weight:bold; color:#54687b; }
.paihang img { margin-top:25px; margin-bottom:5px;  }
.phlist { margin:10px 0px 0px 0px; }
.phlist li { height:30px; line-height:30px; font-size:14px; }
.phlist li span { width:100px; display:inline-block; margin:0px 10px; }
.relistpic div .rd span.usemob { display:none; }
.list01 .recontli { margin-top:22px; }


.mob .recomwban { background:url(${static_domain }/statics/img/v2/recommend/rembanner3.jpg) top center no-repeat #ffd401; width:100%;   height:515px;  }
.mob .relistpic > div { width:100%; display:block; float:none; clear:both; margin-bottom:2.5em; overflow:hidden; }
.mob .relistpic .rd img { display:none; }
.mob .relistpic .ld { width:16%; }
.mob .relistpic .ld img { width:50%; margin:20px auto; float:none; }
.mob .relistpic .rd { width:84%; font-size:1.5em; }
.mob .relistpic .rd .usemob { display:block; font-size:2em;  color:#54687b; }
.mob .relistpic .rd .usemob b { color:#ee3726; font-size:1.3em;    }
.mob .bk-recom { padding-bottom:3em;  }
.mob .relistpic div .rd span { font-size:1.5em; width:75%; display:inline-block; float:left; }
.mob .bk-onekey h3 { font-size:4em; }
.mob .rebd > div { width:100%; float:none; clear:both; }
.mob .rebd > div .lileft { width: 20%; }
.mob .rebd > div .lileft img { width:50%; margin:20px auto; float:none; }
.mob .rebd > div .limid { font-size:1.8em; width:80%; }
.mob .rebd > div .limid h4 { padding:0px; font-size:1.5em; }
.mob .rebd > div .limid span { color:#99abb7;  }
.mob .rebd > div .liright { width:100%; }
.mob .rebd div .liright .pinput { width:100%; margin:0px auto; font-size:2em;  }
.mob .rebd div .liright .retgli { font-size:1.5em; width:84%; margin:0px auto; }
.mob .rebd div .liright .retgli span { float:right; font-size:2em;  }
.mob .rebd div .liright .recontli { font-size:1.5em; width:84%; margin:0px auto; text-align:right; padding-top:1.3em; padding-right:40px; }
.mob .rebd div.list01  .liright .recontli { margin-top:0px; padding:0px; }

.sbannerw {  background:url(${static_domain }/statics/img/v2/recommend/new/rembanner3.png) top center no-repeat #FFF; text-align:center;  }
.sbannerw img { width:900px; margin:0px auto; }

.recontli {  width: 1000px;height: 50px;border: 1px solid #dddddd;margin: 50px auto 100px auto; font-size:24px; line-height:50px;  }
.recontli input { color:#df2d1c; border:none; width:580px; background:#FFF; border-radius:5px; margin:0px 10px; height:40px; line-height:40px; padding-left:0px;  }
.recontli a { outline: 0 none; background: #FF0000;border: 1px solid #ddd;font-family: "微软雅黑";color: #FFFFFF;font-size: 14px; display:inline-block; float:right; cursor:pointer; padding:0px 50px; height:50px; line-height:50px; text-decoration: none; }
.recontli a:hover { background:#D00; }
.relistpic { width:960px; margin:0px auto; }
.relistpic .list { margin: 10px ; width: 240px; height: 300px; border: 1px solid #dddddd; padding-top: 10px;padding-bottom: 10px; float:left;  }
.relistpic .list img { display:block; float:none; margin:0px auto; }
.relistpic .list h3 { font-size:14px; color:#D00; margin:0px; padding:10px 0px 0px 0px; }
.relistpic .list h4 { font-size:12px; padding:0px 10px; line-height:20px; }
.relistpic .listarrow { width:80px; float:left; text-align:center; }
.relistpic .listarrow img { margin:150px 0px 0px 20px; }

.bk-onelist { margin-top:60px; }

</style>


</head>
<body>
<div class="bk-body">
  <jsp:include page="/common/top.jsp" />
   
			
  <div class="sbannerw">
  	<img src="${static_domain }/statics/img/v2/recommend/new/rembanner6.png">
  </div>
  
 <div class="recontli" >
	<p class="pinput">
	
		<c:if test="${noLogin==0 }">
         	推荐人链接：<input id="myLink" value="${main_domain}/${curUser.id}" readonly="readonly">
         	<span>
         		<a id="copyBtn" class="lrbtn"><i class="fa fa-share-alt"></i> 复制推广</a>
         	</span>
         	
				
    	</c:if>
     <c:if test="${noLogin==1 }">
     	<a onclick="javascript:window.location.href='${vip_domain}/user/login'"  >登录后获取推荐人链接</a>
     </c:if>
	
	</p>
</div>
  
  
   <div class="bk-onekey bk-recom" style=" min-height:420px;">
    <div class="container">
    	<h3>佣金计划</h3>
    	<div class="relistpic">
    		<div class="list">
    			<img  src="${static_domain }/statics/img/v2/recommend/new/row-screenshot_01.png">
    			<h3>复制连接</h3>
    			<h4>点击上方按钮，复制您的推荐链接</h4>
    		</div>
    		<div class="listarrow"><img src="${static_domain }/statics/img/v2/recommend/new/row_arrows.png"></div>
    		<div class="list">
    			<img  src="${static_domain }/statics/img/v2/recommend/new/row-screenshot_02.png">
    			<h3>邀请好友</h3>
    			<h4>发送链接好友，帮助好友成功注册</h4>
    		</div>
    		<div class="listarrow"><img src="${static_domain }/statics/img/v2/recommend/new/row_arrows.png"></div>
    		<div class="list">
    			<img  src="${static_domain }/statics/img/v2/recommend/new/row-screenshot_03.png">
    			<h3>拿现金＝靠谱的兼职</h3>
    			<h4>好友每充值一笔RMB，您随即获得平台奖励1‰现金奖。</h4>
    		</div>
    		
    	</div>
    	</div>
    </div>
    	 
    
  
  <div class="bk-onekey hide">
    <div class="container">
		  <div class="bk-tabList">
		    <div class="bk-tabList-hd clearfix">
		      <div class="btn-group bk-btn-group" role="group">
		        <a href="javascript:;" class="btn active" role="button">${L:l(lan,"推荐人管理") }</a>
		      </div>
		      <div class="pull-right bk-tabRight">
		        <a href="${vip_domain }/u/payin/cny" class="btn btn-primary btn-sm" role="button"><i class="bk-ico incoin"></i>充值/充币</a>
		        <a href="${vip_domain }/u/payout/btc" class="btn btn-second btn-sm" role="button"><i class="bk-ico outcoin"></i>提现/提币</a>
		      </div>
		    </div>
		    <div class="bk-tabList-bd" >
		      <!-- 一键买币开始 -->
		      <div class="table-responsive">
		        <div class="alert alert-well alert-dismissible" role="alert">
						  <!-- <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button> -->
						  <p>复制您的推荐地址给qq、微信或者微博好友，用户点击链接进行注册并完成首次充值，推荐人和被推荐人都可获得<span class="text-primary ft18">10元</span>首次充值奖励。<br/>活动期间被推荐人进行RMB充值，推荐人还可获得<span class="text-primary ft18">0.1%</span>的额外奖励！</p>
						</div>
		        <div class="bk-onekey-form">
		          <form role="form" id="buyForm" class="form-horizontal" method="post" action="" autocomplete="off">
					    <div class="form-group has-feedback line-1">
					        <label class="control-label col-sm-4">累计获得奖励：</label>
					        <div class="input-group col-sm-8">
					            <p class="form-control-static">
					              <span id="total" class="bkNum bkNum-mo">
					              <c:if test="${noLogin==0 }">${sumReward}</c:if>
					              <c:if test="${noLogin==1 }">--</c:if>
					              </span>CNY
					            </p>
					        </div>
					    </div>
					    <div class="form-group has-feedback line-2">
					        <label class="control-label col-sm-4" for="newAddr">定制您的推荐人地址：</label>
					        <div class="input-group col-sm-8">
					        <c:if test="${noLogin==0 }">
					        	<c:if test="${curUser.subDomainTimes < 1}">
					            <input type="text" class="form-control form-second pull-left" id="newAddr" name="newAddr" 
					            pattern="" errormsg="" aria-describedby="newAddr_error" value="${curUser.subDomain }">
					            <div class="pull-left ft36"><span class="text-third">.vip.com</span></div>
					            <div class="pull-left mt10 ml20"><a id="setDomainBtn1" role="button" class="btn btn-primary btn-sm">立即定制</a></div>
					            </c:if>
					            <c:if test="${curUser.subDomainTimes >= 1}">
					            <input type="hidden" id="newAddr" value="${curUser.subDomain }" />
					            <div class="pull-left ft36"><span class="text-third">${curUser.subDomain }.vip.com</span></div>
					            </c:if>
					        </c:if>
					        <c:if test="${noLogin==1 }">
					        	<div class="pull-left ft36">--</div>
					        </c:if>
					        </div>
					        <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
					        <span id="newAddr_error" class="help-block text-danger"></span>
					    </div>
					    <div class="form-group line-4">
					        <label class="control-label col-sm-4"></label>
					        <div class="input-group col-sm-8">
					        <c:if test="${noLogin==0 }">
					            <button id="copyBtn1" type="button" data-loading-text="Loading..." class="btn btn-outline btn-hg"><i class="bk-ico share"></i>复制推广</button>
				            </c:if>
					        <c:if test="${noLogin==1 }">
					            <button type="button" data-loading-text="Loading..." class="btn btn-outline btn-hg" onclick="javascript:window.location.href='${vip_domain}/user/login'">登录后定制</button>
				            </c:if>
					        </div>
					    </div>
					</form>
		        </div>
		      </div>
		      
		    </div>
		  </div>
	  </div>
  </div>
  <div class="bk-onelist">
    <div class="container">
      <div class="table-responsive">
        <table id="rewardRecord" class="table table-striped table-bordered table-hover">
          <thead>
            <tr>
              <th width="15%">时间</th>
              <th width="15%">用户名</th>
              <th width="25%">奖励类型</th>
              <th width="35%">充值</th>
              <th width="15%">奖励</th>
            </tr>
          </thead>
          <tbody>
            
          </tbody>
        </table>
        <div class="morelist">
			<a id="moreRecordbtn" style="display:none">更多纪录<i class="glyphicon glyphicon-menu-down"></i></a>
		</div>
        <input type="hidden" id="pageIndex" value="1" />
      </div>
    </div>
  </div>

<script type="text/javascript" src="${static_domain}/statics/js/common/zeroclipboard/ZeroClipboard.min.js"></script>
<script type="text/javascript">
$(function() {
	// 复制插件配置
	ZeroClipboard.config( { swfPath: "${static_domain}/statics/js/common/zeroclipboard/ZeroClipboard.swf" } );
	
	$('#setDomainBtn').on('click', function() {
		var newAddr = $('#newAddr').val();
		if (!checkAddr(newAddr)) {
			Wrong('推荐地址格式不正确，请重新输入。');
			return;
		}
		$.ajax({
			url: '/u/recommend/setSubDomain',
			type: 'POST',
			data: {newAddr: newAddr},
			dataType: 'json',
			success: function(json) {
				if (json.isSuc) {
					Right(json.des, {call:function() {
						window.location.reload();
					}});
				} else {
					Wrong(json.des);
				}
			},
			error: function() {
				Wrong('网络访问出错，请稍后重试。');
			}
		});
	});
	
	rewardRecord(1);
	$('#moreRecordbtn').on('click', function() {
		var pageIndex = parseInt($('#pageIndex').val());
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		rewardRecord(pageIndex+1);
	});
	
	var client = new ZeroClipboard($("#copyBtn"));
    client.on("copy", function (event) {
    	var ptype = window.location.protocol;
		var clipboard = event.clipboardData;
// 		var url = ptype + "//" + $("#newAddr").val() + ".vip.com";
		var url = $('#myLink').val();
		clipboard.setData("text/plain", url);
		Right('复制成功！');
	});
    
});

function rewardRecord(pageIndex) {
	$.ajax({
		url: '/u/recommend/rewardRecord',
		type: 'POST',
		data: {pageIndex: pageIndex},
		dataType: 'json',
		success: function(json) {
			if (json.isSuc) {
				//Right(json.des);
				$('#pageIndex').val(json.datas.pageIndex);
				if ((!json.datas.list || json.datas.list.length <= 0) && pageIndex == 1) {
					$("#rewardRecord tbody").html("<tr><td colspan='5'>"+bitbank.L("通用没有任何记录")+"</td><tr>");
					$('#moreRecordbtn').hide();
				} else {
					var str = '';
					for (var i in json.datas.list) {
						var data = json.datas.list[i];
						if (data) {
							var date = new Date(data.date);
							date = date.format('yyyy-MM-dd hh:mm:ss');
							var typeName = '';
							if (data.type == 6) {
								typeName = '注册奖励';
							} else if (data.type == 7) {
								typeName = '推荐注册奖励';
							} else if (data.type == 8) {
								typeName = '首次充值奖励';
							} else if (data.type == 9) {
								typeName = '推荐充值奖励';
							} else if (data.type == 10) {
								typeName = '充值活动奖励';
							}
							str += '<tr>';
							str += '<td>'+date+'</td>';
							str += '<td><p class="text-muted">'+data.launchUserName+'</p></td>';
							str += '<td><p class="text-muted">'+typeName+'</p></td>';
							str += '<td><p><span class="bkNum">'+data.launchRmb+'</span>CNY</p></td>';
							str += '<td><p><span class="bkNum">'+data.rmb+'</span>CNY</p></td>';
					        str += '</tr>';
						}
					}
					$('#rewardRecord tbody').append(str);
					$('#moreRecordbtn').show();
					formatNum();
				}
			} else {
				Wrong(json.des);
			}
		},
		error: function() {
			Wrong('网络访问出错，请稍后重试。');
		}
	});
}
function checkAddr(str) {
	var reg = /^[0-9a-zA-Z]+$/
	if(!reg.test(str)){
		return false;
	}
	if (str.length < 4 || str.length > 20) {
		return false;
	}
	return true;
}

</script>
  <!--页面中部内容结束-->
  <jsp:include page="/common/foot.jsp" />
</div>
</body>
</html>