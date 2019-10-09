<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/common/head.jsp" />
        <title>${L:l(lan,'登录日志-登录日志上部-标题-1')}-${WEB_NAME }-${WEB_TITLE }</title>
        <meta name="keywords" content="${WEB_KEYWORD }" />
        <meta name="description" content="${WEB_DESC }" />
        <link rel="stylesheet" href="${static_domain}/statics/css/web.user.css?V${CH_VERSON }">
    </head>
    <body>
    	   <jsp:include page="/common/top.jsp" />
		<!-- Common TopMenu End -->
		<!-- Body From mainPage Begin -->
		<div class="mainer2">
			<div class="container">
				<jsp:include page="/common/trend.jsp" />
				<div class="cont-row">
					<div class="user-panel">
						<jsp:include page="/cn/manage/auth/menu.jsp"/>
						<div class="content">
						    <h2>${L:l(lan,'登录日志-登录日志上部-标题-1')}</h2>
						    <div class="login-table">
							<table class="table table-striped table-bordered text-left mt30">
								<thead>
									<tr>
										<th>${L:l(lan,'登录日志-登录日志中部-登录日志表头-1')}</th>
										<th>${L:l(lan,'登录日志-登录日志中部-登录日志表头-2')}</th>
										<th>${L:l(lan,'登录日志-登录日志中部-登录日志表头-3')}</th>
										<th>${L:l(lan,'备注')}</th>
									</tr>
								</thead>
								<tbody id="loginLogsDetail">
									
								</tbody>
							</table>
							</div>
							<div class="bk-tabList-fd bk-pageNav" id="loginLogsDetail_Page">
								
							</div>
						</div>
					</div>
			    </div>
			</div>
	    </div>
	    <jsp:include page="/common/foot.jsp" />
    <script type="text/x-tmpl" id="tmpl-loginLogsDetail">
    		{% for (var i = 0; i <= rs.length -1; i++) { %}
					<tr>
						<td>{%=rs[i].Time%}</td>
						<td>{%=rs[i].type%}</td>
						<td>{%=rs[i].ip%} </td>
						<td>{%=rs[i].status%} </td>
					</tr>
	       {% } %}
    	</script>
    	<script type="text/javascript">
	 	require(["module_user"],function(user){
			user.loginLogsInit();
		});
	 </script>
 	</body>
</html>