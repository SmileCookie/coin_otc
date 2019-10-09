<script type="text/javascript">

$(function(){
	$(".item_list").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
});
</script>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:300px;">管理员</th>
			<th>日志类型</th>
			<th style="width: 400px;">日志信息</th>
			<th>IP</th>
			<th>操作</th>
		</tr>
	</thead>
		<#if dataList??>
				<tbody>
					<tr class="space">
						<td colspan="5">
						</td>
					</tr>
				</tbody>
			
				<#list dataList as list>
					<tbody>
						<tr class="hd">
							<td colspan="5">
								<span>编号：${list.id} </span>
								<span>创建时间：${list.createTime?string("yyyy-MM-dd HH:mm:ss")}</span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="txt">${(list.aUser.id)?if_exists}<br/>
								<a href="javascript:;" style="font-weight: bold;color:green;">${(list.aUser.admName)?if_exists}</a></div>
							</td>
							<td>
								${(list.type.value)?if_exists}
							</td>
							<td>
								${(list.memo)?if_exists}
							</td>
							<td>
								<a href="http://www.ip138.com/ips138.asp?ip=${list.ip }&action=2" target="_blank">${list.ip}</a>
							</td>
							<td>
								-
							</td>
						</tr>
					</tbody>
				</#list>
				<tfoot>
					<tr>
						<td colspan="5">
							<div class="page_nav" id="pagin">
								<div class="con">
									<#if pager?? && pager=='' >共${itemCount }项</#if>
									${pager?if_exists}
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