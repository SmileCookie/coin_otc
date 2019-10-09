<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>编号</th>
			<th>名称</th>
			<th>子菜单</th>
			<th>描述</th>
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
						${list.name}
					</td>
					<td>
						
					</td>
					<td>
						${list.des}
					</td>
					<td>
						<a href="javascript:doDel('${list.id }');">删除</a> &nbsp;
						<a href="javascript:vip.list.aoru({id : '${list.id }', width : 600 , height : 300});">修改</a>
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