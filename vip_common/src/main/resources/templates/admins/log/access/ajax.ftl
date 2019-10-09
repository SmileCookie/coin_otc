<input type="hidden" id="page" name="page" value="${page!''}" />
<table class="tb-list2"  id="ListTable" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th style="width:13%">IP</th>
			<th style="width:10%">路径</th>
			<th style="width:34%">用户</th>
			<th style="width:10%">访问量</th>
			<th style="width:10%">时间</th>
		</tr>
	</thead>
			<#if dataList??>
				<#list dataList as list>
					<tbody class="item_list" id="line_${list.ip}">
						<tr>
							<td >
								<a target="_blank" href="http://www.ip138.com/ips138.asp?ip=${list.ip }&action=2">${list.ip }</a>
							</td>
							<td >${list.urls }${list.params }</td>
							<td>${list.userName }|${list.adminName }</td>
							<td >${list.times }</td>
							<td >${list.date }</td>
						</tr>
					</tbody>
				</#list>
				<tfoot>
					<tr>
						<td colspan="5">
							<div class="page_nav" id="pagin">
								<div class="con">
									<#if itemCount??>共${itemCount}项</#if>
									<#if pager??>${pager}</#if>
								</div>
							</div>
						 </td>
					</tr>
				 </tfoot>
			<#else>
				<tbody class="air-tips">
					<tr>
						<td colspan="5">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</#if>

</table>
<script type="text/javascript">
	$(function(){
	
		//显示url参数
		$("a[id^='detail-']").bind("click",function(){
			var a = $(this);
			var div = $("#container");
			if(div.size()==0){
				div =$("<div>").attr("id","container").css("display","none");
				div.css("background-color","rgb(248, 248, 248)");
			}
			
			if(div.parent().attr("id")==a.parent().attr("id")){
				div.toggle();
			}else{
				div.load("/admin/log/detailParams" +this.title,function(){
					div.show();
				});
			}
			div.insertAfter(a);
		});
		
		$("td[id^='url-td-']").bind("mouseover",function(){
			var as = $("a[id^='detail-']");
			as.hide();
			$(this).find("a").show();
		});
		
	});
	
	

</script>


