<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>资金统计</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

<style type="text/css">
.tb-list2 td{
	border-left: 1px solid #DADADA;
	border-right: 1px solid #DADADA;
	width: 25%;
}
.tb-list2 th{
	border-left: 1px solid #DADADA;
	border-right: 1px solid #DADADA;
}
.tb-list2{background: none repeat scroll 0 0 #F9F9F9;}

.tb-list2 .hd td span {
    margin: 0 2px 0 2px;
}
tr.rmb td{color: #D75A46;}
tr.btc td{color: #8DC03C;}
tr.ltc td{color: orange;}
tr.btq td{color: #ff0000;}

.operate a:hover {
    background: none repeat scroll 0 0 #6DC03C;
    color: #FFFFFF;
}
.operate a {
    background: none repeat scroll 0 0 #8DC03C;
    border-radius: 3px 3px 3px 3px;
    color: #FFFFFF;
    height: 31px;
    line-height: 32px;
    padding: 5px;
}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search" style="padding:0">
         <form autocomplete="off" name="searchForm" id="searchContaint">
            <div id="formSearchContainer">
               <span class="formtit" style="margin-left: 10px;">谷歌码：</span> 
               <span class="formcon">
                  <input type="text" name="mCode" id="mCode" value="" pattern="limit(0,10)"/>
               </span>
   
               <p style="float: none;">
                  <a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
                  <a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
               </p>
            </div>
      
         </form>
        </div>
		<div class="tab_head" id="userTab" style="display: none;">
			<a href="javascript:vip.list.search({tab:'real'})" class="current" id="real"><span>所有</span></a>
		</div>
		<div class="tab-body" id="sysuser" style="display:none;margin-top: 30px;padding:30px 30px 0; font-size: 16px;color: #ff0000;">
			<div class="operate">
				<a href="javascript:add();">添加</a>
			</div>
			本次统计的用户ID大于60000。<br/>
			<c:if test="${sysUsers!=null}">
				不包含的用户：
				<c:forEach items="${sysUsers}" var="user">
					<a href="javascript:del(this,'${user.userId}')">【${user.userId}, ${user.userName}】</a>
				</c:forEach>
			</c:if>
			<c:if test="${sysUsers==null}">
				包含所有用户
			</c:if>
		</div>
		<div id="shopslist" style="padding:10px 30px;font-size: 18px;">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "用户";
	vip.list.basePath = "/admin/system/";
});

function add(){
	Iframe({
	    Url:"/admin/system/aoru",
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:460,
        Height:208,
        scrolling:"no",
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"添加不统计的用户"
	});
}

function reload2(){
	Close();
	window.location.reload();
}

function del(obj,id){
	Ask2({Msg:"您确定要删除该用户吗？", call:function(){
		var actionUrl = "/admin/system/doDel?id="+id;
		vip.ajax( {
			url : actionUrl,
			suc : function(xml) {
				Right($(xml).find("MainData").text(), {callback:"reload2()"});
			}
		});
	}});
}
</script>

</body>
</html>
