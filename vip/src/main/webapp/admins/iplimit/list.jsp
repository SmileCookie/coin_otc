<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>黑白名单</title>
<jsp:include page="/admins/top.jsp" />

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
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="formline">
					<span class="formtit">IP地址：</span> 
					<span class="formcon">
						<input id="userName" mytitle="要求填写一个长度小于50的字符串" name="ip" pattern="limit(0,50)" size="20" type="text"/>
					</span>
				</div>
				<div class="formline">
					<p>
						<a id="idSearch" class="search-submit" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						<a id="idSearch" style="width: 68px" class="search-submit" href="javascript:vip.list.aoru({id:0});">添加白名单</a>
						<a id="idReset" style="width: 68px" class="search-submit" href="javascript:vip.list.aoru({id:1});">添加黑名单</a>
					</p>
				</div>
			</form>
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'white'})" class="current" id="white"><span>白名单</span></a>
			<a href="javascript:vip.list.search({tab:'black'})" id="black"><span>黑名单</span></a>
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "黑白名单";
	vip.list.basePath = "/admin/iplimit/";
});

function reload2(){
	Close();
	vip.list.reload();
}

function update(id){
	Ask2({Msg:"确定要更改IP类型吗？", call:function(){
		vip.ajax({
			url : "/admin/iplimit/update?id="+id,
			dataType : "json",
			suc : function(json) {
				Right(json.des, {callback:"reload2()"});
			}
		});
	}});
}

//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

function delMore(){
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

	Ask2({Msg:"确定要删除选中的项吗？", call:function(){
		vip.ajax({
			url : "/admin/iplimit/delMore?ids="+ids,
			dataType : "json",
			suc : function(json) {
				Right(json.des, {callback:"reload2()"});
			}
		});
	}});
}
</script>

</body>
</html>
