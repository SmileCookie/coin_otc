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

function dosubmit(){
	
	var actionUrl = "/admin/btc/wallet/addAddress"+"?coint=${coint.propTag }";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			Right($(xml).find("Des").text(), {call:function(){
				parent.vip.list.reload();
				parent.Close();
			}});
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
		<h1>添加接收地址</h1>
		<div class="bid" id="">
			<div class="bd">
				<div class="form-line">
					<div class="form-tit">钱包名称：</div>
					<div class="form-con">
						<input type="text" style="width:200px;height: 30px;" mytitle="请输入要添加的模块名称,2-30个字" disabled="disabled" errormsg="模块名称,2-30个字" pattern="limit(2,30)" name="name" class="txt" value="${curData.name }"/>
						<input type="hidden" name="id" value="${id }"/>
					</div>
				</div>
				
				<div class="form-line">
					<div class="form-tit">地址添加数：</div>
					<div class="form-con">
					<input type="text" style="width:200px;height: 30px;" mytitle="请输入最多有多少个接受地址" errormsg="接搜地址只能为数字类型" pattern="num()" name="num" class="txt" value="${curData.maxKeyNums - curData.hasUsedNums }"/>
					</div>
				</div>
				
				<div class="form-btn" id="FormButton">
					<a class="btn" href="javascript:dosubmit();" id="setOk"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
					<a class="btn" href="javascript:parent.Close();" id="setOk"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
				</div>
			</div>

		</div>
	</div>
</body>
</html>
