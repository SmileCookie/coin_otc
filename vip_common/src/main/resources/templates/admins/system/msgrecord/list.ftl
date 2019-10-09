<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>消息日志</title>
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
</style>
</head>
<body >
<#if error??>
<#else>
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContaint">

					<div class="formline">
						<span class="formtit" style="margin-left: 10px;">用户ID：</span>
						<span class="formcon">
							<input type="text" id="userId" name="userId" style="width:150px;"/>
							<input type="hidden" id="userName" name="userName" style="width:150px;"/>
						</span>
						<span class="formtit" style="margin-left: 10px;">标题：</span> 
						<span class="formcon">
							<input type="text" name="title" mytitle="请输入标题内容"  id="title" style="width:200px;" />
						</span>
					
						<span style="float:left;" class="formtit">发送状态：</span> 
						<span style="float:left;margin: 6px 20px 0 0px;" class="formcon">
							<select name="sendStat" id="sendStat" style="width:100px;display: none;" selectid="select_33818967">
					           <option value="">全部</option>
   			           		   <option value="0">未处理</option>
   			           		   <option value="2">已成功</option>
   			           		   <option value="3">已失败</option>
					         </select>
					         <div class="SelectGray" id="select_33818967"><span><i style="width: 71px;">全部</i></span></div>
						</span>
					</div>
					<div class="formline">
						<span style="float:left;" class="formtit">发送类型：</span> 
                        <span style="float:left;margin: 6px 20px 0 0px;" class="formcon">
                           <select name="type" id="type" style="width:100px;display: none;" selectid="select_33818968">
                                <option value="">全部</option>
                                <option value="2">短信</option>
                                <option value="1">邮件</option>
                           </select>
                           <div class="SelectGray" id="select_33818968"><span><i style="width: 71px;">全部</i></span></div>
                        </span>

						<span class="formtit" style="margin-left: 10px;">接收手机：</span> 
						<span class="formcon">
							<input type="text" name="phone" mytitle="请输入接收短信的手机"  id="cont" style="width:200px;" />
						</span>

						<span class="formtit" style="margin-left: 10px;">接收邮箱：</span> 
						<span class="formcon">
							<input type="text" name="email" mytitle="请输入接收邮件的邮箱"  id="cont" style="width:200px;" />
						</span>
                     </div>
                     <div class="formline">
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a href="javascript:vip.list.resetForm();" id="idReset" class="search-submit">重置</a> 
						</p>
					</div>
						
				</div>	
			</form>
		</div>
		
		<div class="tab-body" id="shopslist">
			<#include "ajax.ftl" />
		</div>
	</div>
</div>	
</#if>
<script type="text/javascript">
$(function(){
 	vip.list.ui();
	vip.list.funcName = "消息日志";
	vip.list.basePath = "/admin/system/msgrecord/";
});

function reload2(){
	Close();
	vip.list.reload();
}

function reset(ids){
	if (!couldPass) {
		if (ids)
			commids = ids;
		else
			commids = "";
		googleCode("reset", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要重新发送吗？",
		url : "/admin/system/msgrecord/reSend?id=" + commids + "&mCode="+ ids
	});
} 

</script>
<#include "/admins/code.ftl" />
</body>
</html>
