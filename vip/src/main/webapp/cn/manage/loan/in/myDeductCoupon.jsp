<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,"融资融币")}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.loan.css?V${CH_VERSON }">

<script type="text/javascript">

	function obtainKey(){
		var secretkey = $("#secretkey").val();
		
		if(secretkey == ""){
			JuaBox.sure("${L:l(lan,'请输入有效的抵扣券密钥!')}");
			return;
		}
		vip.ajax({
				url : "/manage/loan/deductcoupon/obtainKey?secretkey=" + secretkey,
				dataType : "json", 
				suc : function(json){
					if(json.isSuc){
						JuaBox.sure(json.des , 
								{
								closeFun :function(){
									$("#secretkey").val("");
									window.location.reload();
									}
								});
				    }else{
				    	JuaBox.sure(json.des);
					}
				} ,
				err:function(json){
					JuaBox.sure(json.des);
				}
		});
	}
	/* function reload2(pageNo){
		if(!pageNo)
			pageNo = $("#pNo").val();
		Close();
		vip.list.ajaxPage({
				needLoading : false,
				url: "/manage/loan/myDeductcoupon?page=" + pageNo,
				suc : function(){
				}
		});
	} */
	function enableKey(id){
		//Ask2({Msg:"您确定要激活这个抵扣券吗?", callback:"doActivity("+id+")"});
		JuaBox.sure("您确定要激活这个抵扣券吗?",
				{
					closeFun:function(){
						vip.ajax({
							url : "/manage/loan/deductcoupon/enableKeys?id=" + id,
							dataType : "json", 
							suc : function(json){
								if(json.isSuc){
									JuaBox.sure(json.des , {closeFun:function(){ window.location.reload();}});
							    }else{
							    	JuaBox.sure(json.des);
								}
							},
							err:function(json){
								JuaBox.sure(json.des);
							}
						});
					}
				});
	}
	/* function doActivity(id){
		vip.ajax({
				url : "/manage/loan/deductcoupon/enableKeys?id=" + id,
				dataType : "json", 
				suc : function(json){
					if(json.isSuc){
						JuaBox.sure(json.des , {callback:"reload2()"});
				    }else{
				    	JuaBox.sure(json.des);
					}
				}
		});
	}
	 */
</script>

<!-- <ul class="fabu"> -->
<!-- 	<div class="ctips" style="margin-bottom: 10px; font-family: '宋体';"> -->
<!-- 		<p> -->
<%-- 			<span>${L:l(lan,'重要提示')}：</span><br /> <em> * --%>
<%-- 				${L:l(lan,'积分兑换比例')}<br /> * ${L:l(lan,'积分兑换最低额（1000分）')}<br /> * --%>
<%-- 				${L:l(lan,'积分兑换抵扣券功能暂时下线。')} --%>
<!-- 			</em> <br /> -->
<!-- 		</p> -->
<!-- 		<p class="mx_btn"> -->
<%-- 			<a href="javascript:;" onclick="receiveFreeCoupon()">${L:l(lan,'免费领取抵扣券')}</a> --%>
<!-- 		</p> -->
<!-- 	</div> -->
<!-- </ul> -->
</head>
<body>
	<div class="bk-body">
		<jsp:include page="/common/top.jsp" />
				<!--页面中部内容开始-->
		<div class="container">
			<div class="lo-nav">
				<ul>
					<li><a href="/manage/loan">${L:l(lan,'融币融资') }</a></li>
					 <c:if test="${p2pUser.loanOutStatus==1 }">
					<li ><a href="/manage/loan/out">${L:l(lan,'投资') }</a></li>
					</c:if>
					
					<li class="current"><a href="/manage/loan/coupon">${L:l(lan,'抵扣券') }</a></li>
					<li>
			        <c:if test='${p2pUser.loanOutStatus!=1}'>
			        	<c:choose>
			        		<c:when test="${empty apply }">
					        	<a id="apply" >${L:l(lan,'申请放贷')}</a>
			        		</c:when>
			        		<c:when test="${apply.status==1 }">
					        	<a id="apply" >${L:l(lan,'申请放贷')}</a>
			        		</c:when>
			        		<c:otherwise></c:otherwise>
			        	</c:choose>
			        </c:if>
			        </li>
				</ul>
			</div>
				<!--页面中部内容开始-->
				<div class="d_dikou">
					<h4>${L:l(lan,'我有抵扣券密钥')}</h4>
					<p>
						<input type="text" class="form-control" name="secretkey" id="secretkey" placeholder="${L:l(lan,'请输入密钥')}" /><a 
						class="btn btn-primary btn-lg" href="javascript:obtainKey();">${L:l(lan,'领取抵扣券')}</a>
					</p>
				</div>
				
				<div class="d_mxquan">
					<input type="hidden" id="pNo" value="${pNo }" />
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th>${L:l(lan,'标题')}</th>
								<th>${L:l(lan,'获取类型')}</th>
								<th>${L:l(lan,'抵扣券类型')}</th>
				<%-- 				<th>${L:l(lan,'抵扣币种')}</th> --%>
								<th>${L:l(lan,'抵扣额度')}</th>
								<th>${L:l(lan,'使用时间')}</th>
								<th>${L:l(lan,'过期时间')}</th>
								<th>${L:l(lan,'状态')}</th>
							</tr>
						</thead>
				
						<c:choose>
							<c:when test="${(not empty(list)) and (fn:length(list) gt 0)}">
								<tbody>
									<c:forEach items="${list }" var="list">
										<tr>
											<td>${L:l(lan,list.title) }</td>
											<td>
											<c:choose>
													<c:when test="${list.getWay eq 1 }">${L:l(lan,'活动赠送')}</c:when>
													<c:when test="${list.getWay eq 2 }">${L:l(lan,'系统赠送')}</c:when>
											</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${list.couponType eq 1 }">${L:l(lan,'抵扣券')}</c:when>
													<c:when test="${list.couponType eq 2 }">${L:l(lan,'打折券')}</c:when>
													<c:when test="${list.couponType eq 3 }">${L:l(lan,'限额抵扣券')}</c:when>
													<c:when test="${list.couponType eq 4 }">${L:l(lan,'限额打折券')}</c:when>
												</c:choose>
											</td>
				<!-- 							<td> -->
				<%-- 								<c:choose> --%>
				<%-- 									<c:when test="${list.fundsType eq 1 }">RMB</c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 2 }">BTC</c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 3 }">LTC</c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 4 }">ETH</c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 5 }">ETC</c:when> --%>
				<%-- 								</c:choose> --%>
				<!-- 							</td> -->
											<td>
												<c:choose>
												<c:when test="${list.amountDeg > 0 }">฿ <fmt:formatNumber value="${list.amountDeg }" pattern="#,##0.0#######" /></c:when>
												<c:otherwise>0</c:otherwise>
				<%-- 									<c:when test="${list.fundsType eq 1 }">¥ <fmt:formatNumber value="${list.amountDeg }" pattern="#,##0.0#" /></c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 2 }">฿ <fmt:formatNumber value="${list.amountDeg }" pattern="#,##0.0#" /></c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 3 }">Ł <fmt:formatNumber value="${list.amountDeg }" pattern="#,##0.0#" /></c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 4 }">E <fmt:formatNumber value="${list.amountDeg }" pattern="#,##0.0#" /></c:when> --%>
				<%-- 									<c:when test="${list.fundsType eq 5 }">e <fmt:formatNumber value="${list.amountDeg }" pattern="#,##0.0#" /></c:when> --%>
												</c:choose>
											</td>
											<td>
											<c:choose>
													<c:when test="${list.useTime != null}">
															${list.useFormatTime }
													</c:when>
													<c:otherwise>
														--
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${list.endTime != null}">
															${list.endFormatTime }
													</c:when>
													<c:otherwise>
														--
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${list.useState eq 0}">
														<a href="javascript:enableKey(${list.id })"><font color="blue">${L:l(lan,'现在激活')}</font></a>
													</c:when>
													<c:when test="${list.useState eq 1 }"><font style="color: #339900;">${L:l(lan,'未使用')}</font></c:when>
													<c:when test="${list.useState eq 2 }"><font style="color: red;">${L:l(lan,'已使用')}</font></c:when>
													<c:when test="${list.useState eq 3 }"><font style="color: red;">${L:l(lan,'已过期')}</font></c:when>
													<c:when test="${list.useState eq 4 }"><font style="color: #c0c0c0;">${L:l(lan,'禁止使用')}</font></c:when>
													<c:when test="${list.useState eq 5 }"><font style="color: orange;">${L:l(lan,'使用中')}</font></c:when>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</c:when>
							<c:otherwise>
								<tbody>
									<tr>
										<td colspan="7">
											<div class="bk-norecord">
												<p><i class="bk-ico info"></i>${L:l(lan,'暂时没有相关记录。')}</p>
											</div>
										</td>
									</tr>
								</tbody>
							</c:otherwise>
						</c:choose>
					</table>
					<div id="page_navA" class="page_nav" style="margin: 20px 30px; clear: both;">
						<div class="con_" style="float: right;">
							<c:if test="${pager!=null}">${pager}</c:if>
						</div>
					</div>
				</div>
	</div>
</div>
<jsp:include page="/common/foot.jsp" />

<script type="text/javascript">
 $(document).ready(function(){
	  $("#apply").click(function(){
		  JuaBox.info("",{
	    		btnNum : 1,
	    		btnName1 : bitbank.L("申请放贷"),
	    		endFun : function(JuaId){
	    			$("#JuaBox_" + JuaId + " .body").load("/manage/applyForm",function(){
	    				JuaBox.position();
	    			});
	    		},
	    		btnFun1 : function(JuaId){
	    			$.ajax({
	    				url:  "/manage/doInvestorApply",
	    				type: 'post',
	    				dataType:'json',
	    				data: $("#investForm").serializeArray(),
	    				success: function(json) {
	    					if (json.isSuc) {
	    						JuaBox.close(JuaId, function(){
	    							//提交后的操作
	    							JuaBox.sure(json.des);
	    						});
	    					} else {
	    						JuaBox.sure(json.des);
	    					}
	    				}
	    			});
	    		}
	 		 });
	  });
	  
	});

 
 
</script> 
</body>
</html>
