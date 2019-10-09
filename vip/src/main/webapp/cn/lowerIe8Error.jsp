<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>温馨提示</title>
<style>
body{background:#e8f3ff;text-align:center;}
.IeError{padding-top:200px;font-family:"微软雅黑";}
.IeError h2{margin-bottom:40px;}
.IeError h2 a{display:block;background:url(../statics/img/logo2.png) no-repeat center center;height:33px;}
.IeError h3{font-size:30px;color:#333;}
.IeError .info{color:#666;margin:20px 0 40px 0;}
.IeError .button a{display:inline-block;margin:0 10px;padding:10px 15px;color:#fff;}
.IeError .button a.ie{background:#5c2d91;}
.IeError .button a.chrome{background:#0373ec;}
.IeError .button a.firefox{background:#669524;}


</style>
</head>
<body>
<div class="IeError">
<h2><a href="http://www.vip.com" title="${WEB_NAME }"></a></h2>
<h3>尊敬的用户，您的浏览器版本过低</h3>
<div class="info">请升级至IE9或以上，或下载Chrome、Firefox，以获得最佳访问体验</div>
<div class="button">
	<a href="http://windows.microsoft.com/zh-cn/internet-explorer/download-ie" target="_blank" class="ie">升级IE浏览器</a>
	<a href="http://www.google.cn/chrome" target="_blank" class="chrome">下载Chrome(谷歌浏览器)</a>
	<a href="http://www.firefox.com.cn/download" target="_blank" class="firefox">下载Firefox(火狐浏览器)</a>
</div>
</div>
</body>
</html>