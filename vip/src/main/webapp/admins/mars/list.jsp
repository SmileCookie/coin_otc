<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>火星表统计</title>
 <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
    
		<script type="text/javascript">
            $(function(){
                vip.list.ui();
                vip.list.basePath = "/admin/mars/";

            });
            function setUpv(id) {
                Iframe({
                    Url : "/admin/mars/showMars?id="+id,
                    zoomSpeedIn : 200,
                    zoomSpeedOut : 200,
                    Width : 550,
                    Height : 750,
                    scrolling : 'no',
                    isIframeAutoHeight : false,
                    isShowIframeTitle:true,
                    Title : "编辑火星表"
                });
            };

            function check(id){
                Ask2({Title:"确定执行此操作？",call:function(){

                    vip.ajax({url : "/admin/mars/check?id="+id , suc : function(xml){
                        Right($(xml).find("Des").text());
                        //vip.list.reload();
                        window.location.reload();
                    }});
                }});


            };

            function reload2() {
                Close();
                vip.list.reload();
            }






		</script>
      <style type="text/css">
      .commodity_action a{padding: 0 5px;}
	  .mains{
		  overflow: auto;
	  }
	  .tab-body{
		  width: 100%;
		  box-sizing: border-box;
		  overflow: auto;
	  }
      </style>
	</head>
<body>
<div class="mains">

	<div class="col-main">

			<div class="form-search" id="listSearch">
				<form autocomplete="off" name="searchForm" id="searchContaint">

					<p class="formCloumn">
						<span>资金类型：</span>
						<select name="fundsType" id="fundsType">
							<option value="">--请选择--</option>
							<c:forEach var="ft" items="${ft }">
								<option value="${ft.value.fundsType}">${ft.value.propTag}</option>
							</c:forEach>
						</select>
					</p>

					<p class="formCloumn">
						<span class="formText">
							交易时间：
						</span>
						<span class="formContainer">
							<span class="spacing">起始时间</span><span class="formcon mr_5">
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd ',lang : 'cn'})" name="begin" id="begin" size="20"/></span>
							<span class="spacing">结束时间</span>
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd ',lang : 'cn'})" name="end" id="end" size="20" />
						</span>
					</p>
					<p class="formCloumn">
						<span >
							变动位置：
						</span>
						<span >
							<select id="changePosition" name="changePosition">
								<option value="">-请选择-</option>
								<option value="1">平台</option>
								<option value="2">钱包</option>
							</select>
						</span>
					</p>
					<p class="formCloumn">
						<span >
							变动类型：
						</span>
						<span >
							<select id="changeType" name="changeType">
								<option value="">-请选择-</option>
								<option value="1">冷到其他</option>
								<option value="2">其他到热提</option>
								<option value="3">后台充值</option>
                                <option value="4">其他到冷</option>
								<option value="5">资金划转</option>
								<option value="6">后台扣除</option>
								<option value="7">用户充值</option>
								<option value="8">用户提现</option>
								<option value="9">热提到其他</option>
								<option value="10">系统分发</option>
								<option value="11">ABCDEF发行</option>
								<option value="12">兑换ABCDEF</option>
								<option value="13">其他</option>
							</select>
						</span>
					</p>
					<p class="formCloumn">
						<span >
							公司资金变动类型：
						</span>
						<span >
							<select id="companyChangeType" name="companyChangeType">
								<option value="">-请选择-</option>
								<option value="1">公司资金减少（T)</option>
								<option value="2">公司资金增加（T)</option>
								<option value="3">公司资金减少（F)</option>
                                <option value="4">公司资金增加（F)</option>
                                <option value="5">不影响公司资金</option>
							</select>
						</span>
					</p>
					<p class="formCloumn">
						<span >
							统计类型：
						</span>
						<span >
							<select id="accountingType" name="accountingType">
								<option value="">-请选择-</option>
								<option value="1">资金减少（T)</option>
								<option value="2">资金增加（T)</option>
								<option value="3">资金减少（F)</option>
                                <option value="4">资金增加（F)</option>
								<option value="5">不影响本表</option>
							</select>
						</span>
					</p>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
						<a class="search-submit" href="javascript:vip.list.resetForm();" id="idReset">重置</a>
						<a class="search-submit" id="newMars"  href="javascript:setUpv('');">添加</a>
					</p>
				</form>
			</div>
			
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />	
			</div>
			
	</div>
</div>
</body>
</html>

