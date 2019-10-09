<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>

<jsp:include page="/admins/top.jsp" />

<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function dosubmit(mCode){
	var amount = $("#amount").val();
	if(!vip.tool.isFloat(amount)){
		Wrong("请输入转出金额。");
		return;
	}
	
	if(!couldPass){
		googleCode("dosubmit", true);
		return;
	}
	var actionUrl = "/admin/btc/wallet/doOut?mCode="+mCode+"&coint=${coint.propTag }";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			parent.Right($(xml).find("MainData").text(), {
				callback : "reload2()"
			});
		}
	});
}

function reload2(){
	Close();
	vip.list.reload();
}

</script>

<style type="text/css">
.bid .bd{overflow:hidden;padding:15px;zoom:1;}

    .inputW{background-color: #fff; border: 1px solid #CCCCCC;box-shadow: 1px 1px 2px #E6E6E6 inset;height: 30px;line-height: 30px;text-indent: 2px;}
	.form-tit{ width:72px;}
	.form-con .tips{ color:#999999;}
	.form-con .tips span{ color:#B90F0D;}
	.form-con .mar{ padding:0 0 0 5px;}
	
	.form-btn{ padding-left:72px;}
	
	.jqTransformRadioWrapper{margin: 8px 5px 0 6px;}
</style>
</head>
<body>
	<div id="add_or_update" class="main-bd">
		<div class="bid" id="">
			<div class="bd">
				<div class="form-line">
					<div class="form-tit">钱包余额：</div>
					<div class="form-con">
						${curData.btcs / 100000000 }
					</div>
				</div>
			
				<div class="form-line">
					<div class="form-tit">转入地址：</div>
					<div class="form-con">
						<a style="color: #006699;" href="https://blockchain.info/zh-cn/address/${curData.privateKey }" target="_blank">${curData.privateKey }</a>
						<font style="color:red;">请核实地址后确认转出</font>
						<input type="hidden" name="walletId" value="${curData.walletId }"/>
					</div>
				</div>
				
				<div class="form-line">
					<div class="form-tit">转出数量：</div>
					<div class="form-con">
					<input type="text" style="width:260px;height: 30px;" mytitle="请输入转出数量" errormsg="转出数量只能为数字类型" pattern="num()" id="amount" name="amount" class="txt"/>
					</div>
				</div>
				
				<div class="form-btn" id="FormButton">
                   <a class="btn" href="javascript:dosubmit();"><span class="cont">确定</span></a>
            	   <a class="btn btn-gray" href="javascript:parent.Close();"><span class="cont">取消</span></a>
				</div>
			</div>

		</div>
	</div>
</body>
</html>
