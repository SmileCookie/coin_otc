<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>成交记录</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
	
   <script type="text/javascript" src="${static_domain }/statics/js/admin/admin.js"></script>
<script type="text/javascript">
${params }
</script>
<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}

.form-search .formline{float:left;}
.form-search p{float:none;}

.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
.tb-list2 .po {
    color: #4775A9;
}
.tb-list2 .pi {
    color: #D75A46;
}

.form-search span.formtit {
    margin-left: 10px;
}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<div class="form-search" id="searchContainer">
				<div id="add_or_update" class="main-bd">
				<form autocomplete="off" name="searchForm" id="formSearchContainer">
					<div class="formline">
						<span class="formtit">新增ip：</span> 
						<span class="formcon"><input id="newIp"/></span>
						<span class="formtit">每分钟的限制次数：</span> 
						<span class="formcon"><input id="newLimit"/></span>
						<p>
							<a class="search-submit" id="idSave" style="width:200px;" href="javascript:savenew();">新增或修改到名单</a> 
						</p>
					</div>
					<div style="clear: both;">&nbsp;</div><div style="clear: both;">&nbsp;</div><div style="clear: both;">&nbsp;</div><div style="clear: both;">&nbsp;</div><div style="clear: both;">&nbsp;</div>
					<div class="formline">
						<span class="formtit">IP：</span> 
						<span class="formcon"><input id="searchIP"/></span>
						<span class="formtit">LIMIT：</span> 
						<span class="formcon"><input id="searchLimit"/></span>
						<p>
							<a class="search-submit" id="idSearch" style="width:200px;" href="javascript:getList();">搜索</a> 
						</p>
					</div>
				</form>
				</div>
			</div>
		</div>
		<div class="tab-body" id="shopslist">
			<table class="tb-list2" style="width:100%;table-layout: fixed;">
				<thead>
					<tr>
						<th>IP</th>
						<th>次数</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${dataList!=null}">
						<c:forEach items="${dataList}" var="list">
							<tbody>
								<tr class="space">
									<td colspan="3"></td>
								</tr>
							</tbody>
							<tbody class="item_list" id="line_${list.id}">
								<tr>
									<td>${list.ip}</td>
									<td>${list.limit}</td>
								</tr>
							</tbody>
						</c:forEach>
						<tfoot>
							<tr>
								<td colspan="8">
									<div class="page_nav" id="pagin">
										<div class="con">
										</div>
									</div>
								 </td>
							</tr>
						 </tfoot>
					</c:when>
					<c:otherwise>
						<tbody class="air-tips">
							<tr>
								<td colspan="6">
									<p>暂时没有符合要求的记录！</p>
								</td>
							</tr>
						</tbody>
					</c:otherwise>
				</c:choose>
			</table>
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
 	$("#searchContainer").Ui();
	vip.list.funcName = "IP白名单";
	vip.list.basePath = "/admins/api/iplist/";
	
	//getList();
});

var inAjaxing = false;
function ajaxUrl(url, dataType){
	if(inAjaxing)
		return;
	
	inAjaxing = true;
	$.ajax( {
		async : true,
		cache : true,
		type : "POST",
		dataType : dataType,
		data : "",
		url : url,
		error : function(json) {
			inAjaxing = false;
		},
		timeout : 60000,
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				Right(json.des, {callback : "reload2()"});
			} else{
				Wrong(json.des);
			}
		}
	});
}

function savenew(mCode){
	/* if(!couldPass){
		googleCode("savenew", true);
		return;
	}
	couldPass = false; */
	var actionUrl = "/admin/api/iplist/savenew?mCode=" + mCode+"&ip="+$("#newIp").val()+"&limit="+$("#newLimit").val();
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			location.reload();
		}
	});
}

function reload2(){
	Close();
	getList();
}

/**
 * 跳转加载新页面
 */
function ToPage(page){
	$("#page").val(page);
	getList();
}

/**
 * 跳转加载新页面
 */
function ToPage2(){
	
	var now=parseInt($("#PagerInput").val());
	var max=$("#PagerInput").attr("maxSize");
	if(parseInt(max)<now){
		Alert("输入页码过大");
		  $("#PagerInput").val(num);
	}else
	  ToPage(now);
}

/**
 * 当前页面，最大页面
 */
function  GetPaper(curpage,numbers)
{

	var showItems=3;
	var getPages=numbers/10;
	if(numbers%10==0){
		getPages=parseInt(numbers/10);
	}else{
		getPages=parseInt(numbers/10)+1;
	}
	
	curpage = parseInt(curpage);
	getPages = parseInt(getPages);

	//初始化一个字符串缓冲区
    var rtn=[];
	/**
	 * 按钮
	 */
	//如果是第一页,定制按钮
	if(curpage == 1){
		rtn.push("<span class=\"Pbtn first\">第一页</span><span class=\"Pbtn pre\"><i>&lt;</i> 上一页</span>");
	}else{
		rtn.push("<a class=\"Pbtn first\"   href=\"javascript:ToPage(1)\">第一页</a>");
		rtn.push("<a class=\"Pbtn pre\"  href=\"javascript:ToPage("+(curpage-1)+")\"><i>&lt;</i> 上一页</a>");
	}
	/*
	 * 前面部分
	 */ 
	if(curpage>(showItems+1))
	{//如果前面是满的
		
		var start=curpage-showItems;
		if((curpage+showItems)>getPages)
		{
			start=getPages-2*showItems;
			if(start<=0)
				start=1;
		}
		else
			start=curpage-showItems;
		if(start>1)
			rtn.push("<span class=\"ellipsis\">...</span>");
		
	    for(var i=start;i<curpage;i++){
	    	rtn.push("<a href=\"javascript:ToPage("+i+")\"   class=\"num\" >"+i+"</a>");
	    }
	}
	else
	{
		 for(var i=1;i<curpage;i++){
			 rtn.push("<a href=\"javascript:ToPage("+i+")\"   class=\"num\"  >"+i+"</a>");
		  }
	}
	//中间部分
	rtn.push("<a class='num current'   >"+curpage+"</a>");
	//后面部分
	if(getPages>(curpage+showItems))
	{
		var end=showItems;
		if(curpage<=showItems)//刚开始，左边本身没有显示完全
		{
			if(getPages>(2*showItems+1))
				end=2*showItems+2;
			else
				end=getPages+1;
		}
		else
			end=curpage+showItems+1;
		for(var i=(curpage+1);i<end;i++){
			rtn.push("<a href=\"javascript:ToPage("+i+")\"    class=\"num\" >"+i+"</a>");
	       }
		if(getPages>(2*showItems+1))
			rtn.push("<span class=\"ellipsis\">...</span>");
	}
	else
	{
		for(var i=(curpage+1);i<(getPages+1);i++){
			rtn.push("<a  href=\"javascript:ToPage("+i+")\"    class=\"num\" >"+i+"</a>");
		  }
	}
	/**
	 * 最后一页
	 */ 
	//如果是最后一页
	if(curpage == getPages){
		rtn.push("<span class=\"Pbtn next\">下一页<i>&gt;</i></span>");
	}else{
		rtn.push("<a class=\"Pbtn next\"    href=\"javascript:ToPage("+(curpage+1)+")\">下一页 <i>&gt;</i></a>");
	}
	//bar.append("</div>");
	//如果现实搜索 
	 
		rtn.push("<div class=\"go_page\"><input type=\"text\" position=\"s\"　 id=\"PagerInput\" size=2 maxSize=\""+getPages+"\" mytitle=\"最多"+getPages+"页\" TitlePosition=\"Left\"  pattern=\"num()\" errmsg=\"最多"+getPages+"页\"  value=\""+curpage+"\" /><a href=\"javascript:ToPage2()\" id=\"JumpButton\" class=\"Pbtn jump\">跳转</a></div>");
	
	return rtn.join(""); 
}
</script>

</body>
</html>