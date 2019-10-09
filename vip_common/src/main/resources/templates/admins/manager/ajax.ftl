<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>登录名/编号</th>
			<th>真实姓名</th>
			<th>邮箱</th>
			<th>电话</th>
			<th>所属角色</th>
			<th>最后登录时间</th>
			<th>性别</th>
			<th>账户状态</th>
			<th>操作</th>
		</tr>
	</thead>
		<#if dataList??>
			<#list dataList as list>
				<tbody class="item_list" id="line_${list.admId}" >
				<tr>
					<td>
						<div class="pic_info">
							<div class="txt"><a href="#" id="text_${list.admId}">${list.admName} </a></div>
							<div class="txt">编号：${list.admId}</div>
						</div>
					</td>
					<td>
						${list.admUName}
					</td>
					<td>
						${list.email}
					</td>
					<td>
						${list.telphone}
					</td>
					<td>
						${(list.ar.roleName) ! ''}
					</td>
					<td>
						${(list.lastLoginTime) ! ''}
						<br>
						<font color="red">[IP: ${(list.lastLoginIp) ! ''}]</font>
					</td>
					<td>
						<#if list.admSex==1> 男
						<#elseif list.admSex==2> 女
						<#else>阴阳人
						</#if>
					</td>
					<td>
						${list.status}
						<br/>
						<#if !(list.secret)?has_content>
							<font style="color:orange;">未添加谷歌</font>
						</#if>
					</td>
					<td>
						<a href="javascript:doDel('${list.admId }');">删除</a> &nbsp;
						<a href="javascript:vip.list.aoru({id : '${list.admId }', width : 600 , height : 660});">修改</a>
						<#if (list.secret)?has_content>
							<br/>
							<a href="javascript:unLocked('${list.admId }');" >解锁谷歌验证锁定</a>
						</#if>
					</td>
				</tr>
				</tbody>
			</#list>
			<tfoot>
	        <tr>
	          <td colspan="9">
		         <div class="page_nav" id="pagin">
                     <div class="con">
		                <span class="page_num">共${itemCount}项</span>
		                <#if pager??>${pager}</#if>
		             </div>
                 </div>
			  </td>
			</tr>
	   </tfoot>
		<#else>
			<tbody>
				<tr>
					<td colspan="9">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
	</#if>
</table>