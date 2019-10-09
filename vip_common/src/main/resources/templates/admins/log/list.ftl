<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户</title>
<#include "/admins/top.ftl" />
<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}

.form-search .formline{float:left;}
.form-search p{float:none;}

.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
.infunds{color: #0088CC;font-size: 14px;font-weight: bold;}
.outfunds{color: #B94A48;font-size: 14px;font-weight: bold;}
 .tb-list2 td{padding: 2px;  }
 .black{background-color: gray;color: white;padding: 2px;  }
 .white{background-color: rgb(89, 226, 231);color: white;padding: 2px;  }
 #white-list {
display: block;
float: right;
width: 600px;
margin-left: 10px;
width:100%;
}
 #white-list span{
	width: 110px;
	line-height: 18px;
 }
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					<div class="formline" style="position:relative; padding-left:400px;">
						<div style="width:400px; position:absolute; left:0px; top:0;">
							<span class="formtit">IP：</span> 
							<span class="formcon"><input id="ip" name="ip" pattern="limit(0,15)" size="20" type="text"/></span>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:addBlack('',0);" id="addBlackBtn" style="width:80px;">添加黑白名单</a>
						</div>
						<div id="white-list" >
							<b >全局名单:</b><br/>
						<#if whites??>
							<#list whites as item>
								<div style="float:left;border:1px solid gray;margin-left: 2px;">
									<span><a target="_blank" href="http://www.ip138.com/ips138.asp?ip=${item.ip}&action=2">${item.ip}</a></span>  
									<span><#if item.type == 1>白<#else>黑</#if></span>
									<span>
										至${item.expireDate?string("MM-dd HH:mm:ss")}
									</span>
								</div>
							</#list>
						</#if>
						</div>
							
					</div>
				</div>
			</form>
		</div>
			<div class="tab-body" id="shopslist">
				<#include "ajax.ftl" />
			</div>
		</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "";
	vip.list.basePath = "/admin/log/";
});

function reload2(){
	Close();
	vip.list.reload();
}
//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

function deletes(){
	var ids="";
	$(".checkItem").each(function(){
		var id=$(this).val();
		if($(this).attr("checked")==true){
			ids+=id+",";
		}
	});
	var list=ids.split(",");
	if(list.length==1){ 
		Wrong("请选择一项"); 
		return;
	}
	doDelMore(ids);
}
var commids = "";
function doDelMore(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doDelMore", true);
		return;
	}
	couldPass = false;
	
	vip.list.reloadAsk({
		title : "确定要删除选中的项吗？",
		url : "/admin/user/dodel?id="+commids+"&mCode="+ids
	});
}

function addBlack(ip){
	Iframe({Url:'/admin/log/aoru?ip=' + ip + '&type=0' ,Width:650,Height:430,Title:"添加黑名单"});
}
function addWhite(ip){
	Iframe({Url:'/admin/log/aoru?ip=' + ip + '&type=1' ,Width:650,Height:430,Title:"添加白名单"});
}
function cancel(ip){
	vip.list.reloadAsk({
		title : "确定要取消黑白名单？",
		url : "/admin/log/doDel?ip=" + ip
	});
}


</script>

</body>
</html>
