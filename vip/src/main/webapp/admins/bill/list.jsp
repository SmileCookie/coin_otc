<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>账单明细</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 
		<script type="text/javascript">
			$(function(){ 
			 	vip.list.ui();
			 	vip.list.basePath = "/admin/bill/";
			});
			
			function reload2() {
				Close();
				vip.list.reload();
			}
			
			
		//全选按钮的方法
		function selectAll(){
			
			changeCheckBox('delAll'); 
			$(".item_list_bd .checkbox").trigger("click");
		}
		</script>
		<style type="text/css">
		label.checkbox{  margin: 3px 6px 0 7px;}
		label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
		.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
		tbody.operations  td{ padding:0; border:0 none;}
		tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
		</style>
		<script type="text/javascript">
			function exportUser(mCode){
				if(!couldPass){
					googleCode("exportUser", true);
					return;
				}
				couldPass = false;
				Close();
				var actionUrl = "/admin/bill/exportUser?mCode="+mCode;
				var datas = FormToStr("searchContaint");
				location.href = actionUrl+"&"+datas;
			}

            Date.prototype.format = function(format){
                var o = {
                    "M+": this.getMonth() + 1, //month
                    "d+": this.getDate(), //day
                    "h+": this.getHours(), //hour
                    "m+": this.getMinutes(), //minute
                    "s+": this.getSeconds(), //second
                    "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
                    "S": this.getMilliseconds() //millisecond
                }
                if (/(y+)/.test(format))
                    format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
                for (var k in o)
                    if (new RegExp("(" + k + ")").test(format))
                        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
                return format;
            }
            window.onload=function() {
                var startDate = document.getElementById("startDate");
                var endDate = document.getElementById("endDate");
                var b = new Date();
                endDate.value = b.format('yyyy-MM-dd 23:59:59');
                var a = new Date();
                a.setDate(1);
                startDate.value = a.format('yyyy-MM-dd 00:00:00');
                document.getElementById("loadFlag").value("");
            }
		</script>
</head>
<body>
<div class="mains">
	<div class="col-main">
		<%-- <jsp:include page="/admins/topTab.jsp" /> --%>
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<input type="hidden" id="tab" name="tab" value="${tab }" />
				<input type="hidden" id="loadFlag" name="loadFlag" value="1"/>
				<div id="formSearchContainer">
					<p>
						<span>流水号：</span>
						<input id="txnId" name="txnId" size="15" type="text"/>
					</p>
					<p>
						<span>账单类型：</span>
						<select name="type" id="type" selectid="select_14618208" style="display: none;">
							<option value="0">--请选择--</option>
							<c:forEach items="${types}" var="t">
								<option value="${t.key}">${t.value}</option>
							</c:forEach>
						</select>
						<div class="SelectGray" id="select_14618208">
							<span><i style="width: 70px;">--请选择--</i></span>
						</div>
					</p>
					<p>
						<span>资金类型：</span>
						<select name="fundsType" id="fundsType" selectid="select_14618209" style="display: none;">
							<option value="0">--请选择--</option>
							<c:forEach var="ft" items="${ft }">
								<option value="${ft.value.fundsType}">${ft.value.propTag}</option>
				            </c:forEach>
						</select>
						<div class="SelectGray" id="select_14618209">
							<span><i style="width: 70px;">--请选择--</i></span>
						</div>
					</p>
					<p class="ormCloumn">
						<span class="formText">
							时间：
						</span>
						<span class="formContainer">
							<span class="spacing">从</span><span class="formcon mr_5">
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  name="startDate" id="startDate" size="15"/></span>
   								<span class="spacing">到</span> 
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"  name="endDate" id="endDate" size="15" />
						</span>
					</p>
					<p>
						<span>用户ID：</span>
						<input id="userId" mytitle="填写用户ID" name="userId"	pattern="limit(0,10)" size="15" type="text"/>
					</p>
					<p>
						<span>备注：</span>
						<input id="memo" mytitle="字段要求填写一个长度小于50的字符串" name="memo"	pattern="limit(0,50)" size="20" type="text"/>
					</p>
	
					<p>
						<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						<a class="search-submit" href="javascript:exportUser('');">导出excel</a>
					</p>
				</div>
		
			</form>
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab : 'all'});" class="current" id="all"><span>所有记录</span></a>
			<a href="javascript:vip.list.search({tab : 'charge'});" id="charge"><span>充值</span></a>
			<a href="javascript:vip.list.search({tab : 'download'});" id="download"><span>提现</span></a>
			<a href="javascript:vip.list.search({tab : 'sysCharge'});" id="sysCharge"><span>系统充值</span></a>
			<a href="javascript:vip.list.search({tab : 'sysDeduct'});" id="sysDeduct"><span>系统扣除</span></a>
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>
</body>
</html>
