<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>角色编号</th>
			<th>名称</th>
			<th>添加时间</th>
			<th>描述</th>
			<th>父角色</th>
			<th>操作</th>
		</tr>
	</thead>
		<#if dataList??>
			<#list dataList as list>
				<tbody class="item_list" id="line_${list.id}">
				<tr>
					<td>
						${list.id}
					</td>
					<td>
						${list.roleName}
					</td>
					<td>
						${list.date}
					</td>
					<td>
						${list.des}
					</td>
					<td>
						<#if !list.prole??>-</#if>
						<#if list.prole??>
							${list.prole.roleName}
						</#if>
					</td>
					<td>
						<a href="javascript:doDel('${list.id }');">删除</a> &nbsp;
						<a href="javascript:vip.list.aoru({id : '${list.id }', width : 600 , height : 398});">修改</a>
					</td>
				</tr>
				</tbody>
			</#list>
			<tfoot>
	        <tr>
	          <td colspan="6">
		         <div class="page_nav" id="pagin">
                     <div class="con">
		                <#if pager??>${pager}</#if>
		             </div>
                 </div>
			  </td>
			</tr>
	   </tfoot>
		<#else>
			<tbody>
				<tr>
					<td colspan="6">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
	</#if>
</table>