<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>编号</th>
			<th>名称</th>
			<th>路径</th>
			<th>功能描述</th>
			<th>视图</th>
			<th>操作</th>
		</tr>
	</thead>
		<#if dataList??>
			<#list dataList as list>
				<tbody>
					<tr class="space">
						<td colspan="6"></td>
					</tr>
				</tbody>
				<tbody>
					<tr class="hd">
						<td colspan="6">
							<span>组编号：${list_index+1} </span>
							<span>功能描述：${list.value.des}</span>
							<span>${list.key}</span>
						</td>
					</tr>
				</tbody>
			
				<#list list.value.functions as fs>
					<tbody class="item_list">
					<tr>
						<td>
							${fs_index + 1}
						</td>
						<td>
							${fs.vc.name}
						</td>
						<td>
							${fs.vc.path}
						</td>
						<td>
							${fs.des}
						</td>
						<td>
							${fs.vc.viewerPath}
						</td>
						<td>
							<a href="javascript:vip.list.aoru({id : '${fs.vc.name }', width : 600 , height : 660});">修改</a>
						</td>
					</tr>
					</tbody>
				</#list>
			
			</#list>
			<tfoot>
	        <tr>
	          <td colspan="6">
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
					<td colspan="6">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</#if>
</table>