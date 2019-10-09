<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>默认值管理</title>
<jsp:include page="/admins/top.jsp" />

<style type="text/css">
label.checkbox {
	margin: 3px 6px 0 7px;
}

label.checkbox em {
	padding-left: 18px;
	line-height: 15px;
	float: left;
	font-style: normal;
}

.page_nav {
	margin-top: 10px;
}

.form-search .formline {
	float: left;
}

.form-search p {
	float: none;
}

.operation {
	height: 20px;
	line-height: 20px;
	text-align: left;
	margin-top: 10px;
	padding-left: 10px;
}

tbody.operations  td {
	padding: 0;
	border: 0 none;
}

tbody.operations  td label.checkbox {
	margin-top: 10px;
	width: 55px;
}

.infunds {
	color: #0088CC;
	font-size: 14px;
	font-weight: bold;
}

.outfunds {
	color: #B94A48;
	font-size: 14px;
	font-weight: bold;
}

</style>
</head>
<body>

	<div class="mains">
		<div class="col-main">
			<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div class="form-search" id="searchContainer">

						<div class="formline">
							
							<span class="formtit">类型名称：</span>
							<span class="formcon">
								<select name="typeName" id="typeName" style="width: 100px; display: none;">
 									<option value="">全部</option>
									<c:forEach items="${typeName }" var="typeName">
									<option value="${typeName.typeName }">${typeName.typeName }</option>
									</c:forEach>
							</select>
							</span>
							
							<p>
								<a id="idSearch" class="search-submit" href="javascript:vip.list.search();">查找</a>
								<a id="idAdd" class="search-submit" onclick="addDeFaLimit()">添加</a>
								<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();" onclick="reset()">重置</a>
							</p>
							
						</div>
						<div style="clear: both;"></div>
						<div class="formline"></div>
					</div>
				</form>
			</div>

			<div class="tab_head" id="userTab">
				<!-- <a href="javascript:vip.list.search({tab:'all'})" class="current" id="all"><span>aaa</span></a> -->
				<!-- <a href="javascript:vip.list.search({tab:'111'})" id="111"><span>bbb</span></a> -->
			</div>
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />
			</div>
		</div>
	</div>

	<script type="text/javascript">
	/* 路径&返回值		Start */
		$(function() {
			vip.list.ui();
			vip.list.funcName = "默认值管理";
			vip.list.basePath = "/admin/loan/p2puser/defaultvalue/";
		});
		function reload2(){
			Close();
			document.location.reload();
			}
		 function reset(){
		        document.getElementsByName('typeName')[0].selectedIndex = document.getElementsByName('typeName')[0].value;
		    }
	/* 路径&返回值		End */
	

	/* 修改默投资认值 	Start*/
		function updateDeFaLimit(ids, typeName, keyName) {
			if (ids == null || $.trim(ids) == "" || typeName == null || $.trim(typeName) == "" || keyName == null || $.trim(keyName) == "") {
				Wrong("内容不能为空！");
				return;
			}
			Iframe({
				Url : "/admin/loan/p2puser/defaultvalue/delimitvalue?ids=" + ids + "&fundsType=" + typeName +"&fkey=" + keyName,
				zoomSpeedIn : 200,
				zoomSpeedOut : 200,
				Width : 550,
				Height : 450,
                isShowIframeTitle: true,
				Title : "修改默认值"
			});
		}
	/* 修改系统默认值	End */

	/* 添加默投资认值 	Start*/
		function addDeFaLimit() {
			Iframe({
				Url : "/admin/loan/p2puser/defaultvalue/AddDeLimit",
				zoomSpeedIn : 200,
				zoomSpeedOut : 200,
				Width : 550,
				Height : 450,
                isShowIframeTitle: true,
				Title : "添加默认值"
			});
		}
	/* 添加系统默认值	End */

	/* 全选按钮的方法 	Start */
		function selectAll() {
			changeCheckBox('delAll');
			$(".hd .checkbox").trigger("click");
		}
	/* 全选按钮的方法	End */

	/* 删除系统配置	Start */
		function deleteDeFaLimit(ids, typeName, keyName) {
			if (ids == null || $.trim(ids) == "" || typeName == null || $.trim(typeName) == "" || keyName == null || $.trim(keyName) == "") {
				Wrong("编号或者状态不能为空");
				return;
			}
			var msg = "确定删除系统默认配置吗？";

			vip.list.reloadAsk({
				"title" : msg,
				url : "/admin/loan/p2puser/defaultvalue/deleteDeFaLimit?ids=" + ids + "&fundsType=" + typeName + "&fkey=" + keyName
			});
		}
	/* 删除系统配置	End */
	</script>
</body>
</html>
