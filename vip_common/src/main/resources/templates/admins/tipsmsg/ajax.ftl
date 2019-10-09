<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<#include "/admins/top.ftl" />
<script type="text/javascript" src="${static_domain }/statics/js/admin/admin.js"></script>
<script type="text/javascript">
	function see(id){
		Iframe({
	        Url : "/admin/tipsmsg/see?id="+id,
	        Width : 580,
	        Height : 320,
	        scrolling : 'no'
    	});
	}
</script>	
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
					<th>操作</th>
				</tr>
			</thead>
				<#if list??>
						<#list list as list>
							<tbody>
								<tr class="space">
									<td colspan="4"></td>
								</tr>
							</tbody>
							<tbody class="item_list" id="line_${list.id}">
									<#if list.status == 0>
										<tr>
											<th>${list.content }</th>
											<th>${list.addTime?string("yyyy-MM-dd HH:mm:ss")}</th>
											<th>${list.seeTimes }</th>
											<th>${(list.seeTime?string("yyyy-MM-dd HH:mm:ss")) ! '-'}</th>
											<th>
												<a href="javascript:;" onclick="see(${list.id})">查看</a>
											</th>
										</tr>
									<#else>
										<tr>
											<td>${list.content }</td>
											<td>${list.addTime?string("yyyy-MM-dd HH:mm:ss")}</td>
											<td>${list.seeTimes }</td>
											<td><fmt:formatDate value="${list.seeTime }" pattern="yy-MM-dd HH:mm"/></td>
											<td>
												<a href="javascript:;" onclick="see(${list.id})">查看</a>
											</td>
										</tr>
									</#if>
							</tbody>
						</#list>
						<tfoot>
							<tr>
								<td colspan="8">
									<div class="page_nav" id="pagin">
										<div class="con">
											<#if pager??>${pager}</#if>
										</div>
									</div>
								 </td>
							</tr>
						 </tfoot>
					<#else>
						<tbody class="air-tips">
							<tr>
								<td colspan="8">
									<p>暂时没有符合要求的记录！</p>
								</td>
							</tr>
						</tbody>
					</#if>
		</table>
	</div>
</div>
</div>	
</body>
</html>
