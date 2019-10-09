<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>系统功能</title>
	<jsp:include page="/admins/top.jsp" />
</head>
<body >
<script type="text/javascript">
$(function(){
 	vip.list.ui();
	vip.list.funcName = "签名管理";
	vip.list.basePath = "/admin/financial/sign/";
});
function useIt(ids){
	if(!couldPass){
		commid = ids;
		googleCode("useIt", true);
		return;
	}
	couldPass = false;
	vip.list.aoru({id : commid, width : 600 , height : 410,url : "/admin/financial/sign/aoru?id="+commid+"&mCode="+ids});
}

function confirmDel(id){
	if(!couldPass){
		commid = id;
		googleCode("confirmDel", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({title : "确定删除吗！请谨慎操作..." , url : "/admin/financial/sign/doDel?id="+commid+"&mCode="+id+"&coint=${coint.propTag }"});
}

var commid ="";
function succonfirm(id){
	Iframe({
	    Url:"/admin/financial/entry/aoru?id=0",//充值
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:560,
        Height:480,
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"热钱包财务录入"
	});
}

function addMemo(id) {
	Iframe({
		Url : '/admin/financial/sign/addMemo?id=' + id,
		Width : 550,
		Height : 220,
		isShowIframeTitle: true,
		Title : "添加备注信息"
	});
}

function reload2(){
	vip.list.search();
	Close();
}
</script>
<div class="mains"> 
<div class="col-main">
<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div id="formSearchContainer">
						<p>
							<span>编号：</span>
							<input errormsg="请检查字段AdmId是否是数字类型的,注意,本字段功能如下: 管理员编号" id="admId"
								mytitle="AdmId要求填写一个数字类型的值" name="admId" pattern="num()"
								size="10" type="text" value=""/>
						</p>
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 500 , height : 410});">添加</a>
						</p>
					</div>

				</form>
			</div>
			<div class="tab_head" id="userTab">
				<a href="javascript:void(0);" onclick="vip.list.search({tab:'btc'})" id="btc" class="current"><span>BTC</span></a>
				<a href="javascript:void(0);" onclick="vip.list.search({tab:'ltc'})" id="ltc"><span>LTC</span></a>
			</div>
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />
			</div>
</div>
   </div>
	</body>
</html>
