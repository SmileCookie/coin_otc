<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>

<#include "/admins/top.ftl" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 

<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function dosubmit(){
	
	var actionUrl = "/admin/log/doaoru";
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
	span.txt{float: left;}
	.jqTransformRadioWrapper{margin: 8px 5px 0 6px;}
</style>
</head>
<body>
	<div id="add_or_update" class="main-bd">
<!-- 		<h1>添加/编辑黑白名单</h1> -->
		<div class="bid" id="">
			<div class="bd">
				<div class="form-line">
					<div class="form-tit">目标IP：</div>
					<div class="form-con">
						<input type="text" style="width:200px;height: 30px;" mytitle="请输入要添加的IP,7-15个字" errormsg="IP,7-15个字" pattern="limit(7,15)" name="ip" class="txt" value="${ip }"/>
					</div>
				</div>
				
				<div class="form-line">
					<div class="form-tit">过期时间：</div>
					<div class="form-con">
					<input type="text" class="Wdate" onFocus="WdatePicker({readOnly:false,dateFmt:'yyyy-MM-dd HH:mm'})"   value="${expire}" name="expire" id="expire" size="34" pattern="limit(10,30)" />
					<span style="color: #bf2405;margin-left: 5px;"> 默认为24小时</span>
					</div>
				</div>
				
				<div class="form-line">
					<div class="form-tit">类型：</div>
					<div class="form-con">
	                 	<span class="jqTransformRadioWrapper" style="margin: 2px 5px 0 3px;">
	                 		<a style="cursor:pointer;" class="jqTransformRadio"></a>
	                 		<input type="radio" name="type" value="0" class="radio" <#if type?? && type=='0'>checked="checked"</#if> style="display: none;"/>
	                 	</span><span class="txt">黑名单</span>
	                 	
	                 	<span class="jqTransformRadioWrapper" style="margin: 2px 5px 0 3px;">
	                 		<a style="cursor:pointer;" class="jqTransformRadio"></a>
	                 		<input type="radio" name="type" value="1" class="radio" <#if type?if_exists=='1'>checked="checked"</#if> style="display: none;"/>
	                 	</span><span class="txt">白名单</span>
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
