<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>活跃用户数据统计</title>
 <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
    
		<script type="text/javascript">
            $(function(){
                vip.list.ui();
                vip.list.basePath = "/admin/aliveuserdatacount/";

            });

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
						<span class="formText">
							统计时间：
						</span>
						<span class="formContainer">
							<span class="spacing">起始时间</span><span class="formcon mr_5">
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd ',lang : 'cn'})" name="startTime" id="startTime" size="20"/></span>
							<span class="spacing">结束时间</span>
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd ',lang : 'cn'})" name="endTime" id="endTime" size="20" />
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

