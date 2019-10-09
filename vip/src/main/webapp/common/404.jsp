<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<!doctype html>
<html>
	<head>
		<meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
		<title>404 Error Page</title>
		<link type="text/css" rel="stylesheet" href="${static_domain }/statics/css/web.error.css"/>
	</head>
	<body>
		<!--=== Error V6 ===-->
		<div class="container valign__middle">
			<!--Error Block-->
					<div class="error-v6 valign__middle">
						<h1>That's an error!</h1>
						<span class="sorry">The requested URL was not found on this server.<br> That's all we know.</span>
						<strong class="h1">4 <img src="${static_domain }/statics/img/common/o-icon.png" alt="error"> 4</strong>
							<span class="input-group-btn">
								<a href="/" class="btn-u btn-u-red">BACK HOME</a>
							</span>
					</div>
		</div><!--/container-->
		<!--End Error Block-->
		
		<!--=== Sticky Footer ===-->
		<div class="container sticky-footer">
			<p class="copyright-space">
				Copyright &copy;2016-<script>document.write(new Date().getFullYear());</script> - btcwinex.Com All Rights Reserved.
			</p>
		</div>
		<!--=== End Sticky Footer ===-->

	</body>
</html>
