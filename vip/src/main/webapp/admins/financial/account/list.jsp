<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>账户管理</title>
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
				<div class="form-search" id="searchContaint">
					<div class="formline">
						<span style="float:left;" class="formtit">账户选择：</span> 
						<span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<select name="accountId" id="accountId" style="width:200px;display: none;" selectid="select_24962646">
					           <option value="">全部</option>
					           <c:forEach var="account" items="${accounts}">
					             	<option value="${account.id}">${account.name}</option>
				           		</c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>

						<span style="float:left;" class="formtit">管理员：</span> 
						<span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<select name="adminId" id="adminId" style="width:140px;display: none;" selectid="select_56967696">
					           <option value="">全部</option>
					           <c:forEach items="${admins}" var="admin">
					           		<option value="${admin.id }">${admin.admName }</option>
					           </c:forEach>
					         </select>
					         <div class="SelectGray" id="select_56967696"><span><i style="width: 171px;">全部</i></span></div>
						</span>

						<span style="float:left;" class="formtit">资金类型：</span> 
						<span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<select name="fundType" id="fundType" onchange="changeType()" style="width:130px;display: none;" selectid="select_28644876">
					           <option value="">全部</option>
					           <c:forEach var="ft" items="${ft }">
						           <option value="${ft.value.fundsType }">${ft.value.propCnName }</option>
					           </c:forEach>
					         </select>
					         <div class="SelectGray" id="select_28644876"><span style="margin-right: 10px;"><i style="width: 171px;">人民币</i></span></div>
						</span>
					
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<c:if test="${logAdmin.rid==1}">
								<a id="" class="search-submit" href="javascript:vip.list.aoru({id:0,height:618});">添加</a>
							</c:if>
						</p>
					</div>
	
				</div>
		
			</form>
		</div>
		
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){
 	vip.list.ui();
	vip.list.funcName = "公司账户";
	vip.list.basePath = "/admin/financial/account/";
	
	$(".item_list").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
	
	if(${alert}){
		Alert("您今天还有需要进行上班结算的账户，请记得结算哦。");
	}
});

function setDefault(id, stat){
	var tit = "";
	var setStat = false;
	if(stat == 1){
		tit = "您确定要设置当前账户为默认账户吗？";
		setStat = true;
	}else{
		tit = "您确定要取消当前账户为默认账户吗？";
	}
	Ask2({
		Msg : tit,
		call : function(){
			vip.ajax({url : "/admin/financial/account/setdefault?id="+id+"&setStat="+setStat , suc : function(xml){
				Right($(xml).find("MainData").text() , {call : function(){
					reload2();
				}});
			}}); 
		}
	});
}

/*上下班结算*/
function balance(id, daytag, type){
	Iframe({
	    Url:"/admin/financial/balance/aoru?accountId="+id+"&dayTag="+daytag+"&accType="+type,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:1100,
        Height:820,
        scrolling:"yes",
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"当日结算"
	});
}

function reload2(){
	Close();
	vip.list.reload();
}
</script>

</body>
</html>
