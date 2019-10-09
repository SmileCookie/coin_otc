<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>编号</th>
			<th>发送名称</th>
			<th>发送邮箱</th>
			<th>HOST/端口</th>
			<th>账号/密码</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<#if dataList??>
		<#list dataList as list>
				<tbody>
					<tr class="space">
						<td colspan="7"></td>
					</tr>
				</tbody>
				<tbody class="item_list">
				<tr>
					<td>
						${list.myId}
					</td>
					<td>
						${list.sendName}
					</td>
					<td>
						${list.fromAddr}
					</td>
					<td>
						${list.mailServerHost} / ${list.mailServerHost}
					</td>
					<td>
						${list.emailUserName} / **
					</td>
					<td>
						${list.status}
					</td>
					<td>
						<a href="javascript:vip.list.del({id : '${list.id }'});">删除</a> &nbsp;
						<a href="javascript:vip.list.aoru({id : '${list.myId}', width : 500 , height : 500});">修改</a>
						
					</td>
				</tr>
				</tbody>
			
			</#list>
			<tfoot>
	        <tr>
	          <td colspan="7">
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
					<td colspan="7">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</#if>
</table>