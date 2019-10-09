<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>BTC${L:l(lan,"冻结明细")}</title>
<jsp:include page="/common/head.jsp" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/fast.css" />

<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
$(function() {
	vip.list.ui();
	vip.list.basePath = "/u/btc/freezedetails/";
});

function reload2() {
	Close();
	vip.list.reload();
}
</script>
</head>
<body>
<!-- 头部 -->
<jsp:include page="/common/top.jsp" />
	
	<div class="h_ucenter">
  		<div class="wrap clearfloat">
  			<div class="side_l ld" style="height: 1057px;">
  			<%-- 	<jsp:include page="/common/left.jsp"><jsp:param value="asset" name="type"/></jsp:include> --%>
			</div>
  
    		<div class="side_r ld">
            <jsp:include page="/common/tab.jsp" />
            <div class="main-bd">
            <div class="tab_head" id="userTab">BTC ${L:l(lan,"冻结明细")}</div>
				<div class="col-main">
					<form autocomplete="off" name="searchForm" id="searchContaint">
					<div class="formSearchContainer" id="formSearchContainer">
						<div class="formline">
<%--							<span class="formtit">${L:l(lan,"备注")}:</span> <span class="formcon"><input--%>
<%--								type="text" name="searchKey" id="searchKey" style="width:245px;"--%>
<%--								position="s" --%>
<%--								mytitle='${L:l(lan,"请输入备注信息")}' />--%>
<%--							</span>--%>
                            <span class="formtit" style="margin-left:0;">${L:l(lan,"金额")}
								:</span> <span class="spacing">${L:l(lan,"从")}</span> <span
								class="formcon mr_5"><input style="width:60px;"
								type="text" name="sMoney" id="sMoney" size="12" position="s"
								 pattern="num()" />
							</span> <span class="spacing" style="margin-left:5px;">${L:l(lan,"到")}</span> <span
								class="formcon mr_5"><input style="width:60px;"
								type="text" name="lMoney" id="lMoney" size="12" position="s"
								pattern="num()" />
							</span> <span class="spacing"></span>
							<span class="formtit">${L:l(lan,"创建时间")}：</span> <span class="spacing">${L:l(lan,"从")}</span>
							<span class="formcon mr_5"><input type="text"
								style="width:120px;"
								onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : '${lan}'})"
								name="startDate" id="startDate" class="inputW2 Wdate" />
							</span> <span class="spacing">${L:l(lan,"到")}</span> <span class="formcon"><input
								type="text" style="width:120px;"
								onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : '${lan}'})"
								name="endDate" id="endDate" class="inputW2 Wdate" />
							</span>

							<div class="searchC">
								<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">${L:l(lan,"搜索")}</a>
								<a class="search-submit" id="idReset" href="javascript:vip.list.resetForm();">${L:l(lan,"重置")}</a>
							</div>
						</div>
					</div>
					</form>
					<div class="cztab_head" id="userTab">
						<a href="javascript:vip.list.search({tab:'all'})" id="all" class="current">${L:l(lan,"所有")}</a>
						<a href="javascript:vip.list.search({tab:'freezing'})" id="freezing">${L:l(lan,"冻结")}</a>
						<a href="javascript:vip.list.search({tab:'relieved'})" id="relieved">${L:l(lan,"解冻")}</a>
						<a href="javascript:vip.list.search({tab:'cancled'})" id="cancled">${L:l(lan,"已取消")}</a>
					</div> 
 
					<div class="tab-body" id="shopslist">
						<jsp:include page="listAjax.jsp" />
					</div>
				</div>
            </div>
			</div>
		</div>
	</div>

<!-- 头部 -->
<jsp:include page="/common/foot.jsp" />
</body>
</html>

