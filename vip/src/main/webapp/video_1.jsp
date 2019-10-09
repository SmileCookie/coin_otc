<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>视频</title>
<script type="text/javascript">
document.domain = '${baseDomain}';  
function v_c(){
	parent.Close();
	$("#PlayEMBED").remove();
}
</script>
</head>

<body style=" padding:0; margin:0;">
<%
	String url = request.getParameter("url");
	String heights = request.getParameter("heights");
	String widths = request.getParameter("widths");
	String title = request.getParameter("titles");
	request.setAttribute("url", url);
	request.setAttribute("heights", heights);
%>
<div style="color:#FFFFFF;margin: 3px;width:100%;text-align: center;font-size: 20px;">无法观看？<a href="http://v.youku.com/v_show/id_XNzMzNjE4Nzky.html" target="_blank" style="color:#ffffb5;">直接到优酷查看</a></div>
<div class="player" id="PlayEMBED">
  <embed id="play" height="${heights}" width="<%=widths %>" menu="true" wmode="window" allowfullscreen="true" allowscriptaccess="always" type="application/x-shockwave-flash" src="${static_domain }/statics/flash/player.swf?type=http&amp;file=${url }" starttime="00:10"></embed>
</div>
</body>
</html>
