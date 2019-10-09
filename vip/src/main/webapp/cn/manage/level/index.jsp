<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan, '帐户等级-积分等级上部-标题-1')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }">
<script type="text/javascript" src="${static_domain }/statics/js/common/jsencrypt.js?V${CH_VERSON }"></script>
<script type="text/javascript" src="${static_domain }/statics/js/bootstrap-datepicker.js?V${CH_VERSON }"></script>
<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>


</head>
<body class="">

<div class="bk-body">
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/top.jsp" />

<script type="text/javascript">
$(function(){ 
  	//vip.list.ui();
  	vip.list.basePath = "/manage/level/";
  	$(".u-level-tip").UiTitle();
  	$(".u-level-info").UiTitle();
  	var rang = ${curUser.totalJifen-currentPointStart}/${nextVip.jifen-currentPointStart};
  	$('#vip-rang').css('width',rang*100+'%');
});
function upgradePage(flag){
	if(flag==null){
		flag=0;
	}
	if(!vip.user.checkLogin('(function(){upgradePage(flag)})()')){
		return;
	}
	Iframe({
        Url : vip.vipDomain+"/manage/level/upgradePage?flag="+flag,
        Width : 580,
        Height : 358,
        scrolling : 'no',
        isShowIframeTitle : false
    });
}
	function reload(){
		parent.Close();
		parent.location.reload();
	}
	
	function showVip6(vipRate, remainDay){
		if(vipRate==6){
			if(remainDay>5){
				Msg("您的VIP6等级还有" +remainDay + "天");
			}else{
				upgradePage(1);
			}
		}else{
			upgradePage(0);
		}
	
	}
</script>        	
	<!-- Common TopMenu End -->
	<!-- Body From mainPage Begin -->
	<div class="zh-trade">
		<div class="mainer2">
			<div class="container">
				<jsp:include page="/common/trend.jsp" />
				<div class="cont-row">
					<div class="user-panel">
						<jsp:include page="/cn/manage/auth/menu.jsp"/>
						<div class="content" id="">
						<h2>${L:l(lan,'帐户等级-积分等级上部-标题-1')}</h2>
						<h4 class="sub-tit mt30">${L:l(lan,'帐户等级-升级进度条-标签-1')}${L:l(lan,'：')} VIP-${curUser.vipRate}</h4>
						<c:if test="${curUser.vipRate < 10 }">
		                <div class="vip-tip vip-tip2">
			            <dl class="clearfix ft16">
				            <dt class="left">
 								VIP-${curUser.vipRate}
				          	</dt>
				          	<dd class="reentry-jifen right">
				          	 <c:if test="${curUser.vipRate < 10 }">
				          	    <c:if test="${nextVip.jifen-curUser.totalJifen <=0}">
				          	      ${L:l(lan,'您的积分已达到升级至')}
				          	      VIP-${nextVip.vipRate}
				          	      ${L:l(lan,'系统会在10分钟内完成升级过程')}
						        </c:if>
				          	    <c:if test="${nextVip.jifen-curUser.totalJifen >0}">
				          	      ${L:l(lan,'再获得')}
				          	      <i class="text-primary">
				          	      	<fmt:formatNumber maxFractionDigits="0">${nextVip.jifen-curUser.totalJifen}</fmt:formatNumber>
				          	      </i>
				          	      ${L:l(lan,'积分即可升级至')} 
				          	      VIP-${nextVip.vipRate}
				          	    </c:if>
				          	  </c:if>
				          	</dd>
			          	</dl>
			          	<div class="vip-rang">
			          		<em class="figure-left">
			          			<c:if test="${currentPointStart == null}">
			          				0
			          			</c:if>
			          			<c:if test="${currentPointStart > 0}">
			          				<fmt:formatNumber maxFractionDigits="0">${currentPointStart}</fmt:formatNumber>
			          			</c:if>
			          		</em>
			          		<span id="vip-rang"></span>
			          		<em class="figure-right"><fmt:formatNumber maxFractionDigits="0">${nextVip.jifen}</fmt:formatNumber></em>
			          	</div>
			          	<div class="total-jifen text-depgray bot12">
			          		${L:l(lan,'帐户等级-升级进度条-标签-3')}
			          		<em class="figure-right"><fmt:formatNumber maxFractionDigits="0">${curUser.totalJifen}</fmt:formatNumber></em>
			          	</div>
				        </div>
				      </c:if>
				      <c:if test="${curUser.vipRate==10 }">
					      <div class="vip-tip vip-tip2">
					          <dl class="ft16">
					            <dt>${L:l(lan,'帐户等级-帐户等级的作用-帐户等级表头-1')}${L:l(lan,'：')}&nbsp;VIP-${curUser.vipRate}</dt>
					          </dl>
					          <div class="vip-rang">
					          	  <em class="figure-left"><fmt:formatNumber maxFractionDigits="0">2000000</fmt:formatNumber></em>
					              <span></span>
					          </div>
					          <div class="total-jifen bot12">
				          		${L:l(lan,'帐户等级-升级进度条-标签-3')}
				          		<em class="figure-right"><fmt:formatNumber maxFractionDigits="0">${curUser.totalJifen}</fmt:formatNumber></em>
				          	  </div>
					      </div>
				      </c:if>
							<h4 class="sub-tit">${L:l(lan,'帐户等级-帐户等级的作用-标题-1')}</h4>
							<table class="table table-striped table-bordered text-left table-level">
								<thead>
									<tr>
										<th>${L:l(lan,'帐户等级-帐户等级的作用-帐户等级表头-1') }</th>
										<th>${L:l(lan,'帐户等级-帐户等级的作用-帐户等级表头-2') }</th>
										<th>${L:l(lan,'帐户等级-帐户等级的作用-帐户等级表头-3') }</th>
										<th>${L:l(lan,'帐户等级-帐户等级的作用-帐户等级表头-4') }</th>
									</tr>
								</thead>
								<tbody>
								<c:choose>
									<c:when test="${userVipList.size()>0}">
										<c:forEach items="${userVipList}" var="vipRate" varStatus="index">
											<tr>
												<td>VIP-${vipRate.vipRate}</td>
												<td>${vipRate.jifen}</td>
												<td>
													<c:if test="${vipRate.discount==0}">
														${L:l(lan,'免费') }
													</c:if>
													<c:if test="${vipRate.discount>0}">
														${vipRate.discount}%
													</c:if>
													</td>
												<td>--</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
										<td colspan="4">${L:l(lan,'暂无内容') }</td>
										</tr>
									</c:otherwise>
								</c:choose>
								</tbody>
							</table>
							<h4 class="sub-tit">${L:l(lan,'帐户等级-如何获得积分-标题-1')}</h4>
							<table class="table table-striped table-bordered text-left table-level">
								<thead>
									<tr>
										<th>${L:l(lan,'帐户等级-如何获得积分-如何获得积分表头-1')}</th>
										<th>${L:l(lan,'帐户等级-如何获得积分-如何获得积分表头-2')}</th>
										<th>${L:l(lan,'帐户等级-如何获得积分-如何获得积分表头-3')}</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-1')}</td>
										<td>1000</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-1')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-2')}</td>
										<td>10</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-2')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-3')}</td>
										<td>100</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-3')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-4')}</td>
										<td>100</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-4')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-5')}</td>
										<td>100</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-5')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-6')}</td>
										<td>2000</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-6')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-7')}</td>
										<td>25</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-7')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-8')}</td>
										<td>5000</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-8')}</td>
									</tr>
								
									<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-9')}</td>
										<td>25</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-9')}</td>
									</tr>
								
									<!-- <tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-10')}</td>
										<td>10</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-10')}</td>
									</tr> -->
								
									<!--<tr>
										<td>${L:l(lan,'帐户等级-如何获得积分-如何获得积分条件-11')}</td>
										<td>1000</td>
										<td>${L:l(lan,'帐户等级-如何获得积分-积分规则说明-11')}</td>-->
									</tr>
								</tbody>
							</table>
					  <h4 class="sub-tit">${L:l(lan,'帐户等级-积分明细-标题-1')}</h4>
			          <div class="cztab_head hide" id="userTab"> 
				          <a href="javascript:void(0);" onclick="vip.list.search({tab : 'all',needLoading:false})" id="all" class="current"><span>${L:l(lan,"所有")}</span></a> 
				          <a href="javascript:void(0);" onclick="vip.list.search({tab : 'in',needLoading:false})" id="in"><span>${L:l(lan,"获得")}</span></a> 
				          <a href="javascript:void(0);" onclick="vip.list.search({tab : 'out',needLoading:false})" id="out"><span>${L:l(lan,"消费")}</span></a> 
				        </div>
				        <div class="" id="shopslist">
			            <jsp:include page="ajax.jsp" />
			     </div>
		      </div>
		    </div>
		  </div>				
			</div>
		</div>
</div>

<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>

</body>
</html>

