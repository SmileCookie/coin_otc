<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:300px;">键名</th>
			<th>缓存值</th>
		</tr>
	</thead>
		<#if dataList??>
				<tbody>
					<tr class="space">
						<td colspan="2">
						</td>
					</tr>
				</tbody>
			
				<#list dataList as list>
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="txt">
									${list.key}
								</div>
							</td>
							<td>
								<a onclick="see('${list.key}',this)" href="javascript:;">显示值</a>
								<div style="display:none;"></div>
							</td>
						</tr>
					</tbody>
				</#list>
				<tfoot>
					<tr>
						<td colspan="2">
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
						<td colspan="2">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</#if>

</table>