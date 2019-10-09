<input type="hidden" id="page" name="page" value="${page!'' }" />
<table class="tb-list2"  id="ListTable" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th style="width:13%">IP</th>
			<th style="width:10%">一天总访问次数</th>
			<th style="width:34%">URL<#if limit??>(显示前5)</#if></th>
			<th style="width:10%">访问量</th>
			<th style="width:10%">访问高峰在</th>
		</tr>
	</thead>
			<#if dataList??>
				<#list dataList as list>
					<tbody class="item_list" id="line_${list.ip}">
						<tr>
							<td >
								<a target="_blank" href="http://www.ip138.com/ips138.asp?ip=${list.ip }&action=2">${list.ip }</a>
								<#if list.type??>
									<#if list.type==0>
										<a id="black-${list_index }" style="float:right;" href="javascript:;" onclick="cancel('${list.ip}')" >[取消]</a>
										<a id="black-${list_index }" style="float:right;" href="javascript:;" onclick="addWhite('${list.ip}')" >[加白]</a>
									</#if>
									<#if list.type==1>
										<a id="black-${status.index }"  style="float:right;" href="list.type==1javascript:;" onclick="cancel('${list.ip}')"  >[取消]</a>
									</#if>
									<br/><b class="<#if list.type==0>black<#else>white</#if>"><#if list.type==0>黑名单<#else>白名单</#if></b> 
											- <b>${list.expire }</b> 后恢复
								</#if>	
								<#if list.type??>
									<a id="black-${list_index }"  style="float:right;" href="javascript:;" onclick="addBlack('${list.ip}')"  >[加黑]</a>
									<a id="black-${list_index }"  style="float:right;" href="javascript:;" onclick="addWhite('${list.ip}')"  >[加白]</a>
								</#if>
							</td>
							<td >${list.total }</td>
							<td colspan="3">
								<table class="tb-list2" id="ListTable" style="width:100%;table-layout: fixed;">
								<tbody class="item_list" >
										<tr >
										<th style="width:64%"></th>
										<th style="width:18%"></th>
										<th style="width:18%"></th>
										</tr>
									<#list list.urls?if_exists as item>
										<tr>
											<td id="url-td-${item_index }">${item.url } <a id="detail-${item_index }" title="?ip=${list.ip}&url=${item.url}" style="float:right;display: none;" href="javascript:;">[参数>>]</a> </td>
											<td>${item.accessCount }</td>
											<td>${item.highest_mi?string("HH:mm")} (${item.highest_count }次)</td>
										</tr>
									</#list>
								</tbody>
							</table></td>
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


