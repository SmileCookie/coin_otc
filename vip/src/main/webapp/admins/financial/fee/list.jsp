<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>手续费收益</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
	<script type="text/javascript">
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
            startDate.value = b.format('yyyy-MM-dd');
            document.getElementById("loadFlag").value("");
        }
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
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContaint">
					<div class="formline">
						<span style="float:left;" class="formtit">货币类型：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="currency" id="currency" style="width:110px;display: none;" selectid="select_24962646">
								 <option value="">---请选择---</option>
								 <c:forEach items="${coinMap}" var="coin">
								 	<option value="${coin.key}">${coin.value}/${coin.key}</option>
								 </c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span style="float:left;" class="formtit">费用类型：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="feeType" id="feeType" style="width:110px;display: none;" selectid="select_24962645">
					           	<option value="0">---全部---</option>
				             	<option value="1">交易手续费</option>
				             	<option value="2">借贷手续费</option>
				             	<option value="3">提现手续费</option>
					         </select>
					         <div class="SelectGray" id="select_24962645"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span class="formtit">统计类型：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="surverType" id="surverType" style="width:110px;display: none;" selectid="select_24962644">
					           	<option value="1">按天统计</option>
					           	<option value="2" selected>按周统计</option>
				             	<option value="3">按月统计</option>
				             	<option value="4">按年统计</option>
					         </select>
					         <div class="SelectGray" id="select_24962644"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						<span class="formtit">开始日期：</span> 
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="startDate" name="startDate"  onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;"/>
						</span> 
						<span class="formtit">&nbsp;&nbsp;&nbsp;</span> 
						<span class="formtit">结束日期：</span> 
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="endDate" name="endDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;"/>
						</span>
						<input type="hidden" id="loadFlag" name="loadFlag" value="1"/>
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
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
		vip.list.funcName = "手续费收益";
		vip.list.basePath = "/admin/financial/fee/";
	});
</script>
</body>
</html>
