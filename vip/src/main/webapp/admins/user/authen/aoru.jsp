<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/admins/top.jsp" />
		
		<script type="text/javascript">
			$(function() {
				$("#add_or_update").Ui();
				setDomain();
			});
			function ok() {
				
				var actionUrl = "/admin/user/authen/doAoru";
				vip.ajax( {
					formId : "add_or_update",
					url : actionUrl,
					div : "add_or_update",
					suc : function(xml) {
						parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
					}
				});
			}
		</script>
		<style type="text/css">
			.form-tit{
				width : 120px;
			}
		</style>
	</head>
	<body>
		<div id="add_or_update" class="main-bd">

			<input name="userId" value="${data.userId }" type="hidden"/>
			<input name="type" value="${type}" type="hidden"/>
			<div class="form-line">
				<div class="form-tit">RMB-A1充值额度：</div>
				<div class="form-con">
					<input type="text" name="a1Recharge" mytitle="请输入RMB-A1充值额度。" value="${data.a1Recharge}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a1Recharge }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">RMB-A2充值额度：</div>
				<div class="form-con">
					<input type="text" name="a2Recharge" mytitle="请输入RMB-A2充值额度。" value="${data.a2Recharge}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a2Recharge }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">RMB-A1提现额度：</div>
				<div class="form-con">
					<input type="text" name="a1Download" mytitle="请输入RMB-A1提现额度。" value="${data.a1Download}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a1Download }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">RMB-A2提现额度：</div>
				<div class="form-con">
					<input type="text" name="a2Download" mytitle="请输入RMB-A2提现额度。" value="${data.a2Download}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a2Download }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">BTC-A1提现额度：</div>
				<div class="form-con">
					<input type="text" name="a1BtcDownload" mytitle="请输入BTC-A1提现额度。" value="${data.a1BtcDownload}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a1BtcDownload }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">BTC-A2提现额度：</div>
				<div class="form-con">
					<input type="text" name="a2BtcDownload" mytitle="请输入Btc-A2提现额度。" value="${data.a2BtcDownload}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a2BtcDownload }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">LTC-A1提现额度：</div>
				<div class="form-con">
					<input type="text" name="a1LtcDownload" mytitle="请输入LTC-A1提现额度。" value="${data.a1LtcDownload}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a1LtcDownload }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">LTC-A2提现额度：</div>
				<div class="form-con">
					<input type="text" name="a2LtcDownload" mytitle="请输入LTC-A2提现额度。" value="${data.a2LtcDownload}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.a2LtcDownload }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">小额提现额度：</div>
				<div class="form-con">
					<input type="text" name="cashLimit" mytitle="请输入小额提现额度。" value="${data.cashLimit}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.cashLimit }</c:if>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">每日提现额度：</div>
				<div class="form-con">
					<input type="text" name="dayLimit" mytitle="请输入每日提现额度。" value="${data.dayLimit}"/>
					<c:if test="${not empty defaultData  }">默认为：${defaultData.dayLimit }</c:if>
				</div>
			</div>

			<%--TODO 额度开关弃用，默认都是开启的
			<c:if test="${data.userId != 0}">
			<div class="form-line">
				<div class="form-tit">额度开关：</div>
				<div class="form-con">
					<select>
						<option value="1" <c:if test="${data.limitSwitch==1}">selected</c:if>>开</option>
						<option value="0" <c:if test="${data.limitSwitch==0}">selected</c:if>>关</option>
					</select>
				</div>
			</div>
			</c:if>--%>

			<div class="form-btn" style="padding: 15px 0 0 80px;">
				<input id="id" name="id" type="hidden" value="${data.id}" />
				<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
			</div>
		</div>
	</body>
</html>
