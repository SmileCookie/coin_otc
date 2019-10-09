<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<title>${L:l(lan,'推荐人管理')}-${WEB_NAME }-${WEB_TITLE }</title>
<jsp:include page="/common/head.jsp" />
</head>
<body>
<div class="bk-body">
  <jsp:include page="/common/top.jsp" />
  <!--页面中部内容开始-->
  <div class="bk-onekey">
    <div class="container">
		  <div class="bk-tabList">
		    <div class="bk-tabList-hd clearfix">
		      <div class="btn-group bk-btn-group" role="group">
		        <a href="javascript:;" class="btn active" role="button">${L:l(lan,"推荐人管理") }</a>
		      </div>
		      <div class="pull-right bk-tabRight">
		        <a href="${vip_domain }/u/payin/cny" class="btn btn-primary btn-sm" role="button"><i class="bk-ico incoin"></i>${L:l(lan,'充值/充币')}</a>
		        <a href="${vip_domain }/u/payout/btc" class="btn btn-second btn-sm" role="button"><i class="bk-ico outcoin"></i>${L:l(lan,'提现/提币')}</a>
		      </div>
		    </div>
		    <div class="bk-tabList-bd" >
		      <!-- 一键买币开始 -->
		      <div class="table-responsive">
		        <div class="alert alert-well alert-dismissible" role="alert">
						  <!-- <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button> -->
						  <p>${L:l(lan,'复制您的推荐地址给qq、微信或者微博好友，用户点击链接进行注册并完成首次充值，推荐人和被推荐人都可获得')}<span class="text-primary ft18">${L:l(lan,"10元") }</span>${L:l(lan,"首次充值奖励") }。<br/>${L:l(lan,"活动期间被推荐人进行RMB充值，推荐人还可获得") }<span class="text-primary ft18">0.1%</span>${L:l(lan,"的额外奖励") }！</p>
						</div>
		        <div class="bk-onekey-form">
		          <form role="form" id="buyForm" class="form-horizontal" method="post" action="" autocomplete="off">
					    <div class="form-group has-feedback line-1">
					        <label class="control-label col-sm-4">${L:l(lan,'累计获得奖励')}：</label>
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
					        <label class="control-label col-sm-4" for="newAddr">${L:l(lan,'定制您的推荐人地址')}：</label>
					        <div class="input-group col-sm-8">
					        <c:if test="${noLogin==0 }">
					        	<c:if test="${curUser.subDomainTimes < 1}">
					            <input type="text" class="form-control form-second pull-left" id="newAddr" name="newAddr" 
					            pattern="" errormsg="" aria-describedby="newAddr_error" value="${curUser.subDomain }">
					            <div class="pull-left ft36"><span class="text-third">.vip.com</span></div>
					            <div class="pull-left mt10 ml20"><a id="setDomainBtn" role="button" class="btn btn-primary btn-sm">${L:l(lan,'立即定制')}</a></div>
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
					            <button id="copyBtn" type="button" data-loading-text="Loading..." class="btn btn-outline btn-hg"><i class="bk-ico share"></i>${L:l(lan,'复制推广')}</button>
				            </c:if>
					        <c:if test="${noLogin==1 }">
					            <button type="button" data-loading-text="Loading..." class="btn btn-outline btn-hg" onclick="javascript:window.location.href='${vip_domain}/user/login'">${L:l(lan,'登录后定制')}</button>
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
              <th width="15%">${L:l(lan,'时间')}</th>
              <th width="15%">${L:l(lan,'用户名')}</th>
              <th width="25%">${L:l(lan,'奖励类型')}</th>
              <th width="35%">${L:l(lan,'充值')}</th>
              <th width="15%">${L:l(lan,'奖励')}</th>
            </tr>
          </thead>
          <tbody>
            
          </tbody>
        </table>
        <div class="morelist">
			<a id="moreRecordbtn" style="display:none">${L:l(lan,'更多纪录')}<i class="glyphicon glyphicon-menu-down"></i></a>
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
			Wrong('${L:l(lan,"推荐地址格式不正确，请重新输入")}。');
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
				Wrong('${L:l(lan,"网络访问出错，请稍后重试")}。');
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
    client.on( "copy", function (event) {
    	var ptype = window.location.protocol;
		var clipboard = event.clipboardData;
		var url = ptype + "//" + $("#newAddr").val() + ".vip.com";
		clipboard.setData( "text/plain", url);
		Right('${L:l(lan,"复制成功")}！');
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
								typeName = '${L:l(lan,"注册奖励")}';
							} else if (data.type == 7) {
								typeName = '${L:l(lan,"推荐注册奖励")}';
							} else if (data.type == 8) {
								typeName = '${L:l(lan,"首次充值奖励")}';
							} else if (data.type == 9) {
								typeName = '${L:l(lan,"推荐充值奖励")}';
							} else if (data.type == 10) {
								typeName = '${L:l(lan,"充值活动奖励")}';
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
			Wrong('${L:l(lan,"网络访问出错，请稍后重试")}。');
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