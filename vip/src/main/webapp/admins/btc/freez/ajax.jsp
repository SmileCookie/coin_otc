<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="tb-list2" id="ListTable" style="width: 100%">
		<tr>
			<th style="200px">用户</th>
			<th >冻结</th>
               <th >解冻</th>
               <th >状态</th>
               <th >备注</th>
               <th >冻结金额</th>
               <th class="commodity_action">操作</th>
		</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<tbody class="operations">
				<tr>
					<td colspan="7">
						<div class="operation">
							<label><input type="checkbox" id="DeleAllSel" class="DeleAllSel" name="checkbox"><label id="ck_DeleAllSel" class="checkbox" onclick="changeCheckBox('DeleAllSel')"></label>全/反选</label>
						    <a href="javascript:void(0)" id="up_Btn1" shadow="true" stylename="yellow_button" class="AButton yellow_button">批量上架</a>
							<a href="javascript:void(0)" id="del_Btn" shadow="true" stylename="blue_button" class="del_Btn  AButton blue_button">批量删除</a>
						</div>
					</td>
				</tr>
			</tbody> 
			<c:forEach items="${dataList}" var="list">
			  <tbody id="row${list.freezeId}">

				<tr class="hd">
					<td colspan="7">
						<span>冻结编号:${list.freezeId}</span><span>时间：<fmt:formatDate value="${list.freezeTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
					</td>
				</tr>
				<tr class="item_list_bd item_list_bgcolor">
					<td class="commodity_info">
						<div class="pic_info">
							<div class="txt"><a href="javascript:showUser('${list.user.id }')" style="font-weight: bold;color:green;" id="text_${list.user.id }">${list.user.userName }</a></div>
						</div>
					</td>
					<td class="b_gray">
						<span style="color:red;">${list.freezShow }</span>
					</td>
					<td class="commodity_price b_gray">
						<font color="green">${list.unFreezShow }</font>
					</td>
					<td class="b_gray">
						${list.statShow }
					</td>
					<td class="b_gray">
						${list.reMark }
					</td>
					<td>
						<font color="#000">${list.freez }</font>
					</td>
					
					<td class="commodity_action br_color">
						<c:if test="${list.statu==0}">
							<a href="javascript:unfreez(${list.freezeId});" ids="${list.freezeId}" class="unfreez AButton gray_button" >解冻</a>
						</c:if>
					</td>
				</tr>
				</tbody>
			</c:forEach>
			<tbody class="operations">
				<tr>
					<td colspan="7">
						<div id="page_navA" class="page_nav">
							<div class="con">
								<c:if test="${pager!=null}">${pager}</c:if>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
		</c:when>
		<c:otherwise>
			<tbody class="air-tips">
				<tr>
					<td colspan="7">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>