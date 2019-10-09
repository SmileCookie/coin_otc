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
	
	var actionUrl = "/admin/user/level/doaoru";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			Right($(xml).find("Des").text(), {call:function(){
				parent.vip.list.reload();
				parent.Close();
			}});
		},
		err : function(xml) {
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
	span.txt{float: left;}
	.jqTransformRadioWrapper{margin: 8px 5px 0 6px;}
</style>
</head>
<body>
	<div id="add_or_update" class="main-bd">
		<h1>${param.title}</h1>
		<div class="bid" id="">
			<div class="bd">
				<div class="form-line">
					<div class="form-tit">用户名：</div>
					<div class="form-con">
						<input type="text" style="width:200px;height: 30px;"
							mytitle="请输入用户名" errormsg="请输入用户名"
							pattern="limit(2,100)" name="userName" class="txt"
							value="" />
						<input type="hidden" name="addOrDel" value="${param.addOrDel }"/>
					</div>
				</div>
				
				<div class="form-line">
					<div class="form-tit">积分数量：</div>
					<div class="form-con">
						<input type="text" style="width:200px;height: 30px;"
							mytitle="请输入要添加的积分数量" errormsg="请输入要添加的积分数量" pattern="num()"
							name="jifen" class="txt" value="" />
					</div>
				</div>
				<div class="form-line">
					<div class="form-tit">描述：</div>
					<div class="form-con">
	                 	<textarea style="width: 300px;height: 100px;" name="desc" mytitle="请填写描述信息" errormsg="请填写描述信息" pattern="limit(1,100)"></textarea>
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
