<%@ page session="false" language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<style>
.techpic {
	position: absolute;
	right: 100px;
	top: 20px;
	z-index: 4;
}

.techpic img {
	width: 150px;
}

.techpic a {
	cursor: pointer;
}
</style>
<script type="text/javascript">
$(function(){
	vip.list.basePath = "/u/loan/record/";
});
</script>

<div class="bk-form" style="padding-top: 20px; position: relative;">
	<div class="techpic">
		<a href="https://vip.vip.com/activity/ganggan" target="_blank">
			<img src="${static_domain }/statics/img/v2/common/techpic_gangang.png">
		</a>
	</div>
	<form role="form" id="form_do_loan" class="form-horizontal" autocomplete="off">
		<input type="hidden" id="minUnit" value="${minUnit}" />
		<input type="hidden" name="fundsType" value="${fundsType.key}" />
		<div class="form-group has-feedback">
			<label class="control-label col-sm-4" for="amount" style="padding-top: 15px">
				${fundsType.value eq 'RMB'? L:l1(lan,'%%借入金额',fundsType.tag2): L:l1(lan,'%%借入数量',fundsType.tag2)}：
			</label>
			<div class="input-group col-sm-3">
				<input type="text" class="form-control form-second" id="amount" name="amount" placeholder="${fundsType.value eq 'RMB'? L:l1(lan,'请输入%%借入金额',fundsType.tag2): L:l1(lan,'请输入%%借入数量',fundsType.tag2)}" style="font-size: 14px;">
				<span id="amount_error" class="help-block" style="width: 100%; margin: 50px 0 0; color: #de211d;">
					${L:l1(lan,'当前最优日费率%%',p2pOutRate)}%
				</span>
			</div>
		</div>
		<!-- Start 免息券 -->
<%-- 					  <c:if test ="${not empty couponList}"> --%>
<!-- 						  <div class="form-group has-feedback"> -->
<%-- 						      <label class="control-label col-sm-4" for="freecouponId" style="padding-top:15px">${L:l(lan,'使用免息券')}：</label> --%>
<!-- 						      <div class="input-group col-sm-3"> -->
<!-- 						          <div class="drop-group dropdown" id="freeGroup"> -->
<!-- 						          	<div class="dropdown-toggle clearfix" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> -->
<!-- 						            	<input type="hidden" id="freecouponId" name="freecouponId"> -->
<%-- 			                  	<input id="freecouponIds" type="text" placeholder="${L:l(lan,'请选择免息券')}" onfocus="this.blur()" class="form-control form-second" style="font-size:14px;"> --%>
<!-- 			                  </div> -->
<!-- 			                  <div class="input-drop dropdown-menu" aria-labelledby="freeGroup"> -->
<!-- 								  <ul id="freecouponList"> -->
<!-- 									  <li data-value=""> -->
<%-- 										  --${L:l(lan,'不使用')}-- --%>
<!-- 									  </li> -->
<%-- 									  <c:forEach items="${couponList }" var="obj"> --%>
<%-- 										  <li data-value="${obj.id }"> --%>
<%-- 												  ${L:l1(lan,'可免息借%%',fundsType.tag)}<fmt:formatNumber value="${obj.couldUseAmount}" pattern="0.00"/>，${L:l1(lan,'免息%%天',obj.usedays)} --%>
<!-- 										  </li> -->
<%-- 									  </c:forEach> --%>
<!-- 								  </ul> -->
<!-- 			                   </div> -->
<!-- 			                </div> -->
<!-- 						      </div> -->
<!-- 						  </div> -->
<%-- 					  </c:if> --%>
		<!-- End 免息券 -->
		<!-- Start 抵扣券 -->
		
		<font class="dikouyc">
			<c:if test="${not empty dataList}">
				<div class="form-group has-feedback">
					<label class="control-label col-sm-4" for="deductcouponId" style="padding-top: 15px">${L:l(lan,'利息抵扣券')}：</label>
					<div class="input-group col-sm-3">
						<div class="drop-group dropdown" id="dikouGroup">
							<div class="dropdown-toggle clearfix" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								<input type="hidden" id="deductcouponId" name="deductcouponId">
								<input id="deductcouponIds" type="text" placeholder="${L:l(lan,'请选择抵扣券')}" onfocus="this.blur()" class="form-control form-second" style="font-size: 14px;">
							</div>
							<div class="input-drop dropdown-menu" aria-labelledby="dikouGroup">
								<ul id="deductcouponList">
									<li style="color: green;" data-value="">--${L:l(lan,'不使用')}--</li>
									<c:forEach items="${dataList }" var="dlist">
										<li data-value="${dlist.id }"><!-- amountDeg -->
											${fundsType.tag}<fmt:formatNumber value="${dlist.converAmou}" pattern="0.00##" />
											<span style="float: right;">${L:l(lan,'过期')}:<fmt:formatDate value="${dlist.endTime }"/></span>
										</li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</font>
		<!-- End 抵扣券 -->
		<div class="form-group">
			<label class="control-label col-sm-4"></label>
			<div class="input-group col-sm-4">
				<div class="checkbox text-left">
					<label> <input type="checkbox" name="isRead" id="isRead" value="false"> ${L:l(lan,'我已阅读并同意<<融资融币交易风险申明>>')} </label>
				</div>
			</div>
		</div>

		<div class="form-group" style="padding-top: 10px;">
			<label class="control-label col-sm-4"></label>
			<div class="input-group col-sm-3">
				<button id="loanBtn" type="button" onclick="dosubmit(0)" data-loading-text="Loading..." class="btn btn-outline btn-hg btn-block">${L:l(lan,'立即借款')}</button>
			</div>
		</div>
	</form>
</div>




<div class="table-responsive">
	<div class="bk-pageTit clearfix">
		<h4 class="pull-left">
			<i class="bk-ico assetRecord"></i>${L:l(lan,'融资融币记录')}</h4>
		<div class="pull-right mt15">
			<span class="ld" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="${L:l(lan,'资产/杠杆小于或等于120%时短信提醒，低于或等于110%时实施强制平仓')}">
			${L:l(lan,'资产/杠杆')}：
					<i class="text-primary mr10"> ${curUser.repayLevelShow2} </i>
			</span>
			<span class="ld">
				<c:if test="${calUnwindPirce[0] > 0 || calUnwindPirce[1] > 0 || calUnwindPirce[2] > 0 || calUnwindPirce[3] > 0}">
       		${L:l(lan,'预估平仓价')}
       			</c:if>
       			<c:if test="${calUnwindPirce[0] > 0}">
       		${L:l(lan,'比特币')}：
	       			<font style="color: #de211d;">
	       				<fmt:formatNumber value="${calUnwindPirce[0]}" pattern="0.00#" />
	       			</font>
				</c:if>
				<c:if test="${calUnwindPirce[1] > 0}">
       		${L:l(lan,'莱特币')}：
       				<font style="color: #de211d;">
       					<fmt:formatNumber value="${calUnwindPirce[1]}" pattern="0.00#" />
       				</font>
				</c:if>
				<c:if test="${calUnwindPirce[2] > 0}">
       		${L:l(lan,'以太币')}：
       				<font style="color: #de211d;">
       					<fmt:formatNumber value="${calUnwindPirce[2]}" pattern="0.00#" />
       				</font>
				</c:if>
				<c:if test="${calUnwindPirce[3] > 0}">
       		${L:l(lan,'ETC')}：
       				<font style="color: #de211d;">
       					<fmt:formatNumber value="${calUnwindPirce[3]}" pattern="0.00#" />
       				</font>
				</c:if>
			</span>
			<span class="ld"> &nbsp; &nbsp; ${L:l(lan,'平仓风险')}：</span>
			<div class="jindu ld" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="${L:l(lan,'当前平仓风险')}：<b> ${curUser.repayLevel}% </b>">
				<div style="width:${curUser.repayLevel}%"></div>
			</div>
			<!--<i class="ld text-primary mr15">${curUser.repayLevelShowNew }</i> -->
		</div>
	</div>
	<table class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th width="15%">${L:l(lan,'借款时间')}</th>
				<th width="10%">${L:l(lan,'类型')}</th>
				<th width="15%">${L:l(lan,'已借/已还')}</th>
				<th width="10%">${L:l(lan,'利率')}</th>
<%-- 				<th style="display: none;" width="10%">${L:l(lan,'免息券')}</th> --%>
				<th width="10%">${L:l(lan,'已还利息')}<font class="dikouyc"><hr style="border: 1px double #e8e8e8; margin: 0px;" /><font style="color: red;">${L:l(lan,'抵扣利息')}</font></font></th>
				<th width="10%">${L:l(lan,'应还总额')}<font class="dikouyc"><hr style="border: 1px double #e8e8e8; margin: 0px;" /><font style="color: red;">${L:l(lan,'应还利息')}</font></font></th>
				<th width="10%">${L:l(lan,'状态')}</th>
				<th width="10%">${L:l(lan,'操作')}</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${fn:length(lists)>0}">
					<c:forEach items="${lists }" var="item" varStatus="stat">
						<tr>
							<td title="<fmt:formatDate value="${item.createTime }" pattern="yyyy-MM-dd HH:mm"/>">
								<fmt:formatDate value="${item.createTime }" pattern="yyyy-MM-dd HH:mm" />
							</td>
							<td>${item.fundsType.value }</td>

							<td>
								<fmt:formatNumber var="hasRepay" value="${item.hasRepay }" pattern="0.00####" />
								<fmt:formatNumber var="amount" value="${item.amount + item.hasRepay }" pattern="0.00####" />
							${item.fundsType.tag }${amount }/
								<font class="green">${hasRepay }</font>
							</td>
							<td> <fmt:formatNumber value="${item.rateShow }" pattern="0.0##" />%</td>
<%--  						<td style="display: none;"><c:choose><c:when test="${item.withoutLxDays gt 0}"><div class="bk-divider"></div>${item.fundsType.tag}<fmt:formatNumber value="${item.withoutLxAmount }" pattern="0.0##" /> --%>
<%--              			免息<i class="red" title="共计${item.balanceWithoutLxDays }天免息">${item.withoutLxDays }</i>日</c:when> <c:otherwise> - </c:otherwise> </c:choose> </td> --%>

							<td>
								<c:choose>
									<c:when test="${item.status le 1 }">
										<c:choose>
											<c:when test="${item.hasLx > 0 }">
												<fmt:formatNumber value="${item.hasLx }" pattern="0.00####" />
											</c:when>
											<c:otherwise> — </c:otherwise>
										</c:choose>
										<font class="dikouyc" style="color: red;">
											<hr style="border: 1px double #e8e8e8; margin: 0px;" />
											<font style="color: red;">
											<c:choose>
													<c:when test="${item.dikouLx >0}">
														<fmt:formatNumber value="${item.dikouLx }" pattern="0.00####" />
													</c:when>
													<c:otherwise> — </c:otherwise>
												</c:choose>
											</font>
										</font>
									</c:when>

									<c:when test="${item.status le 3 }">
										<c:if test="${item.hasLx > 0 }">
											<fmt:formatNumber value="${item.hasLx }" pattern="0.00####" />
										</c:if>
										
										<font class="dikouyc">
											<hr style="border: 1px double #e8e8e8; margin: 0px;" />
											<font style="color: red;">
												<c:if test="${item.dikouLx >0}">
													<fmt:formatNumber value="${item.dikouLx }" pattern="0.00####" />
												</c:if>
											</font>
										</font>
									</c:when>
									
									<c:otherwise> — </c:otherwise>
								</c:choose>
							</td>
							<!-- 本金*日息*天数 -->

						<%-- <td>
            					<c:choose>
            						<c:when test="${item.withoutLxDays gt 0}">
            					  		${item.fundsType.tag}<fmt:formatNumber value="${item.dikou }" pattern="0.0##"/> 
            					  		免息<i class="red" title="共计${item.balanceWithoutLxDays }天免息">${item.withoutLxDays }</i>日
            						</c:when>
            						<c:otherwise> - </c:otherwise>
            					</c:choose>
            				</td> --%>

							<td>
								<c:choose>
									<c:when test="${item.status == 1}">
										<fmt:formatNumber value="${item.shouldRepayBX }" pattern="0.00####" />
										<font class="dikouyc">
											<hr style="border: 1px double #e8e8e8; margin: 0px;" />
											<font style="color: red;">
												<c:if test="${item.needLx >0}">
													<fmt:formatNumber value="${item.needLx }" pattern="0.00####" />
												</c:if>
											</font>
										</font>
									</c:when>
									<c:otherwise> — </c:otherwise>
								</c:choose>
							</td>

							<!-- 本金+利息 -->
							<td>
								<font class="${item.recordStatus.color}">${L:l(lan,item.recordStatus.value) }</font>
							</td>
							<td>
								<c:if test="${isIn }">
									<c:choose>
										<c:when test="${item.status == 1}">
											<a href="javascript:;" qx="${item.arrearsLx }" onclick="vip.p2p.repay(${item.id },this, ${pageNo })">${L:l(lan,'还款')}</a>
											<c:if test="${item.hasLx >0 }">
												<a href="javascript:;" onclick="JuaBox.frame('/u/repay?id=${item.id }' , {width:680});">${L:l(lan,'查看')}</a>
											</c:if>
										</c:when>
										<c:when test="${item.status== 2 || item.status == 4}">
											<a href="javascript:;" onclick="JuaBox.frame('/u/repay?id=${item.id }' , {width:680});">${L:l(lan,'查看')}</a>
										</c:when>
										<c:otherwise> - </c:otherwise>
									</c:choose>
								</c:if>
								<c:if test="${!isIn }">
									<a href="javascript:;" onclick="JuaBox.frame('/u/repay?id=${item.id }' , {width:680});">${L:l(lan,'查看')}</a>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="9">
							<div class="bk-norecord">
								<p> <i class="bk-ico info"></i>${L:l(lan,'暂时没有相关记录。')}</p>
							</div>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	<c:if test="${pager!=''}">
		<div id="page_navA" class="page_nav">
			<div class="con">${pager}</div>
		</div>
	</c:if>
</div>

<script type="text/javascript">

  $(function(){
	  $("#freecouponList").on("click","li",function(){
		  $("#freecouponIds").val($.trim($(this).text()));
		  $("#freecouponId").val($(this).data("value"));
	  });
	  
  })
  
  /* 显示抵扣券 */
  $(function(){
	  $("#deductcouponList").on("click","li",function(){
		  $("#deductcouponIds").val($.trim($(this).text()));
		  $("#deductcouponId").val($(this).data("value"));
	  });
	  
  })

	function dosubmit(confirm){
		if(!vip.user.checkLogin()){
			return;
		}
		if(!$("#isRead").prop("checked")){
			return JuaBox.showWrong(bitbank.L("请您同意<<融资融币交易风险申明>>"));
		}
		var datas = FormToStr("form_do_loan"); 
		if(datas == null){return;}
		var minUnit = $("#minUnit").val();
		if($("#amount").val() < minUnit){
			JuaBox.showWrong(bitbank.L("您的最低借入金额不能小于xx!", minUnit));
			return;
		}
		vip.ajax({
			 needLogin : true, //需要登录
			 formId : "form_do_loan",//表单ID
			 div : "form_do_loan",//lodding的div
			 url : "/u/loan/doLoan?confirm=" + confirm ,
			 dataType : "jsonp",
			 suc : function(json){
				 JuaBox.sure(json.des,{
				 	closeFun:function(){
				 		window.top.location.reload();
				 	}
				 })
			 },
			 err:function(json){
			 	if(json.datas == "needConfirm"){
			 		JuaBox.info(json.des,{
			 			btnNum:2,
			 			btnFun1:function(JuaId){
			 				JuaBox.close(JuaId,function(){
			 					dosubmit(1);
			 				})
			 			}
			 		})
			 	}else{
				 	JuaBox.sure(json.des );
			 	}
			 }
		});
	} 
	
	function huan(id){
		vip.list.reloadAsk({title : "${L:l(lan,'确定要还款吗？')}" , url : "/users/admin/finance/repayofqi/huan?id="+id});
	}
	
	function chooseFreeCoupon(o, fundType){
			var id = $(o).find("option:selected").val();
			if(id != 0){
			
				vip.ajax({
					 url : "/u/freecoupon/freeCoupon?id=" + id + "&fundType=" + fundType, 
					 dataType : "json",
					 suc : function(json){
						 if(json.isSuc){
							 var data = json.datas;
							 var couldUseAmount = data.couldUseAmount;
							 var endDate = data.endFormatDate;
							 //$("#couldUseAmount").text(couldUseAmount);
							 $("#endDate").text(endDate);
							 $("#freeTips").show();
							 $("#freeText").show();
							 $("#useamount").attr("pattern", "limit(1,10);num()");
						 }else{
							 Wrong(json.des);
						 }
					  }
				});
				$("#freecouponId").val(id);
			}else{
				$("#freeTips").hide();
				$("#freeText").hide();
				$("#freecouponId").val(0);
				$("#useamount").val('');
				$("#useamount").removeAttr("pattern");
			}
		}
	
	function amoutCheck(v , min , max){
		var amount = $("input[name='amount']").val();
		if(amount < min){
			return false;
		}
// 		if(amount > ${curLoan.balance }){
// 			return false;
// 		}
		if(amount > max){
			return false;
		}
		return true;
	}
	
	function xswCheck(obj , max , type){//小数位验证
		var m = max + 1;
		var iv = $(obj).val();
		if(iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m){
				$(obj).val(iv.substring(0 , iv.indexOf(".") + m));
		}
	}
	function reload2(){
		Close();
		document.location.reload();
	}
	
</script>
