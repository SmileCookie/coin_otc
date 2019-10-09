<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<c:choose>
	<c:when test="${rep != null }">
		<c:forEach items="${rep}" var="r" varStatus="state">
			<!--留言信息楼层*1-->
			<div class="msglist">
			  <!--楼主信息楼层-->
			  <div class="userMsg clearfloat" id="userMsg_${r.id }">
			      <div class="userIco ld"><a href="#"><img src="${static_domain }/statics/img/faq_say2.png" /></a></div>
			      <div class="userSay rd">
			        <div class="hd clearfloat">
			           <span class="ld"><c:if test="${aid!=null }"><a style="display: none;" href="# ${r.id }">${r.user.userName }</a></c:if> <fmt:formatDate value="${r.postTime}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
			           <span class="rd commen_oper" ids="${r.id }"><a href="javascript:vip.comment.replyS(${r.id })" class="reply_btn">${L:l(lan,'回复') }</a></span>
			        </div>
			         <div class="bd clearfloat">
			           <p>${r.content}</p>
			         </div>
			      </div>
			  </div>
			  <div class="replyBox" id="EditBox_${r.id }"></div>
			  <!--楼主信息楼层 END-->
			  <!--回复楼主信息楼层 多了样式reply-->
			  <c:forEach items="${r.sonReplys }" var="rs">
				  <div class="userMsg reply clearfloat" id="userMsg_${rs.id }">
				    <div class="userIco ld"><a href="#"><img src="${static_domain }/statics/img/faq_say3.png" /></a></div>
				    <div class="userSay rd">
				       <div class="hd clearfloat">
				         <span class="ld"><c:if test="${aid!=null }"><a style="display: none;" href="# ${rs.id }">${rs.user.userName }</a></c:if> <fmt:formatDate value="${rs.postTime}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
				         <span class="rd reply_oper" ids="${rs.id }"></span>
				       </div>
				       <div class="bd clearfloat">
				         <p>${rs.content}</p>
				       </div>
				    </div>
				  </div>
				  <div class="replyBox" id="EditBox_${rs.id }"></div>
			  </c:forEach>
			  <!--回复楼主信息楼层 多了样式reply END-->
			  <!--回复楼主信息-->
			  <div class="replyBox" id="replyBox_${r.id }"></div>
			  <!--回复楼主信息 END-->
			</div>
			<!--留言信息楼层*1 END-->
		</c:forEach>
<!-- 		<div class="page_nav" id="pagin"> -->
<!-- 			<div class="con"> -->
<!-- 			<c:if test="${pager!=null}">${pager}</c:if> -->
<!-- 			</div> -->
<!-- 		</div> -->
	</c:when>
	<c:otherwise>${L:l(lan,"暂时没有评论！")}</c:otherwise>
</c:choose>
<c:if test="${!hasNR }">
   <script>
   	$("#reply_more").removeClass("red").addClass("gray").removeAttr("onclick").parent(".fd").hide();
   </script>
</c:if>