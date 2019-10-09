<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<#include "/admins/top.ftl" />
</head>
<body >
<div class="mains">
<div class="col-main">
	<div class="tab-body" id="shopslist">
		<table class="tb-list2" style="width: 100%">
			<thead>
				<tr>
					<th>提示内容</th>
					<th>提交时间</th>
					<th>查看次数</th>
					<th>最后查看时间</th>
				</tr>
			</thead>
			<tr>
				<td>${msg.content }</td>
				<td>${msg.addTime }</td>
				<td>${msg.seeTimes }</td>
				<td>${msg.seeTime }</td>
			</tr>
		</table>
	</div>
</div>
</div>	
</body>
</html>
