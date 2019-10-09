<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>资金监控</title>
 <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
    
		<script type="text/javascript">
            $(function(){
                vip.list.ui();
                vip.list.basePath = "/admin/capmonitor/";
            });

            function reload2() {
                Close();
                vip.list.reload();
            }

            function aoru(ucmId){
                Iframe({
                    Url:"/admin/capmonitor/aoru?ucmId=" + ucmId,//下载
                    zoomSpeedIn		: 200,
                    zoomSpeedOut	: 200,
                    Width:600,
                    Height:500,
                    scrolling:"no",
                    isIframeAutoHeight:false,
                    isShowIframeTitle: true,
                    Title:"监控错误详情"
                });
            }
			//处理备注
            dealRemark = function(obj) {
                $(obj).parents("td").prev().find("textarea").attr("disabled",false).focus();
			}

			//保存备注
            saveRemark = function(obj,id) {
				var remark = $(obj).parents("td").prev().find("textarea").val();
                $(obj).parents("td").prev().find("textarea").attr("disabled",true);
                var actionUrl = "/admin/capmonitor/saveRemark?id=" + id+ "&dealreamark=" + remark;
                vip.ajax({
                    url : actionUrl,
                    dataType : "json",
                    suc : function(json) {
                        Right(json.des);
                    },
                    err : function(json){
                        Right(json.des);
                    }
                });
            }

		</script>
      <style type="text/css">
      .commodity_action a{padding: 0 5px;}
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
							<option value="0">--请选择--</option>
							<c:forEach var="ft" items="${ft }">
								<option value="${ft.value.fundsType}">${ft.value.propTag}</option>
							</c:forEach>
						</select>
					</p>
					<p class="formCloumn">
						<span class="formText">
							检查结果：
						</span>
						<span class="formContainer">
							<select id="checkResult" name="checkResult">
								<option value="0">全部</option>
								<option value="1">正常</option>
								<option value="2">异常</option>
							</select>
						</span>
					</p>
					<p class="formCloumn">
						<span class="formText">
							监控时间：
						</span>
						<span class="formContainer">
							<span class="spacing">从</span><span class="formcon mr_5">
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" name="startTime" id="startTime" size="20"/></span>
   								<span class="spacing">到</span>
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" name="endTime" id="endTime" size="20" />
						</span>
					</p>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a class="search-submit" href="javascript:vip.list.resetForm();" id="idReset">重置</a>
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

