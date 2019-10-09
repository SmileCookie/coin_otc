<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>内存管理</title>
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
						<span class="formtit" style="margin-left: 10px;">键名：</span> 
						<span class="formcon">
							<input type="text" id="key" name="key" style="width:150px;"/>
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
	vip.list.funcName = "内存管理";
	vip.list.basePath = "/admin/system/memcache/";
});

function reload2(){
	Close();
	vip.list.reload();
}

function see(key, obj){
	var _this = $(obj).next("div");
	if(_this.is(":visible")==false){
		search(_this, key);

		$(obj).text("隐藏");
		_this.show();
	}else{
		$(obj).text("显示值");
		_this.hide();
	}
}

function search(_this, key){
	if(_this.text().length > 0){
		return;
	}
	vip.ajax({
		url : "/admin/system/memcache/searchKey?key="+key,
		dataType : "json",
		suc : function(json){
			_this.text(json.des);
		}
	});
}
</script>
<#include "/admins/code.ftl" />
</body>
</html>
