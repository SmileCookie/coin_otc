<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<table class="tb-list2" style="width: 100%">
	<thead>
		<tr>
			<th width="360">图片/标题</th>
			<th>类型</th>
			<th>语言</th>
			<th>发布人</th>
			<th>公告类型</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<c:forEach items="${dataList}" var="list">
					<tbody>
						<tr class="space">
							<td colspan="5"></td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="5"><span>编号：${list.id} </span>
								<span>发布时间：<fmt:formatDate value="${list.pubTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<c:if test="${list.top }">
									<span>置顶时间：<fmt:formatDate value="${list.topTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								</c:if>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="pic_info">
									<c:if test="${list.type == 1}">
										<div class="txt"><a href="/msg/details-${list.id }" target="_blank" style="font-weight: bold;color:green;">${list.title}</a></div>
									</c:if>
									<c:if test="${list.type == 2}">
										<div class="pic"><a href="/msg/newsdetails?id=${list.id }" target="_blank"><img src="${list.photo }"/></a></div>
										<div class="txt"><a href="/msg/newsdetails?id=${list.id }" target="_blank" style="font-weight: bold;color:green;">${list.title}</a></div>
									</c:if>
								</div>
							</td>
							<td>${list.nt.value}</td>
							<td>${list.languageStr}</td>
							<td>${list.srcPublisher }</td>
							<td><c:if test="${list.noticeType == 0}">
								<div class="txt">-</div>
							</c:if>
								<c:if test="${list.noticeType == 1}">
									<div class="txt">新币上线</div>
								</c:if>
							<c:if test="${list.noticeType == 2}">
								<div class="txt">系统维护</div>
							</c:if>
							<c:if test="${list.noticeType == 3}">
								<div class="txt">最新活动</div>
							</c:if>
							<c:if test="${list.noticeType == 4}">
								<div class="txt">平台动态</div>
							</c:if></td>

							<td>
								<a target="_blank" href="/admin/msg/aoru?id=${list.id }">修改</a>
								<br/>
								<a href="javascript:vip.list.del({id : '${list.id }'});">删除</a>

								<br/>
								<c:if test="${list.top }">
									<a href="javascript:toCancel(${list.id})">取消置顶</a>
								</c:if>
								<c:if test="${!list.top }">
									<a href="javascript:toTop(${list.id})">置顶</a>
								</c:if>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="4">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager!=null}">${pager}</c:if>
								</div>
							</div>
						 </td>
					</tr>
				 </tfoot>
			</c:when>
			<c:otherwise>
				<tbody class="air-tips">
					<tr>
						<td colspan="4">
							<p>暂时没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>