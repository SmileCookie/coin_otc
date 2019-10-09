<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<script type="text/javascript">
$(function() {
//	vip.list.ui();
//	vip.list.basePath = "/manage/account/downrecord/";
//  vip.list.defaultDiv = "vipListTbody";
//	vip.list.ajaxPage({url : vip.list.basePath+"ajax/${coint.stag}", suc : function(){}});
    vip.list.ajaxPage = function(){
         
	}
});
function reload2(){
	Close();
	vip.list.reload();
}

function cancelOut(did) {
	JuaBox.info("${L:l(lan,'确定要取消吗？')}", {
		btnFun1:function(JuaId){
			window.top.JuaBox.close(JuaId,function(){
				confirmCancel(did);
			});
		}
	});
}


function confirmCancel(did){
	var actionUrl = vip.vipDomain + "/manage/account/downrecord/confirmCancel?did="+did+"&coint=${coint.tag}";
	vip.ajax({
		url : actionUrl , 
		dataType : "xml",
		suc : function(xml){
			location.reload();
		},
		err : function(xml){
			BwModal.alert($(xml).find("Des").text(), {width:300});
		}
	});
}

</script>
<table class="table table-striped table-bordered table-hover">
  <thead>
    <tr>
      <th>${L:l(lan,"时间")}</th>
      <th>${L:l(lan,"接收地址")}</th>
      <th>${L:l(lan,"处理时间")}</th>
      <th>${L:l(lan,"状态")}</th>
      <th>${L:l(lan,"金额")}(${coint.tag})</th>
      <th>${L:l(lan,"实到金额")}(${coint.tag})</th>
      <th>${L:l(lan,"操作")}</th>
    </tr>
  </thead>
   <tbody id="vipListTbody">
  <c:choose>
    <c:when test="${dataList != null&&fn:length(dataList)>0}">
      <c:forEach items="${dataList}" var="pm" varStatus="status">
          <tr>
          	<td>
                <fmt:formatDate value="${pm.submitTime }" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm:ss':'yyyy-MM-dd HH:mm:ss'}"/>
            </td>
            <td>${pm.toAddress}</td>
            <td>
                <c:choose>
                    <c:when test="${pm.status<=0 || pm.status>=3}"><span class="gray">-</span></c:when>
                    <c:when test="${pm.status==1}">
                       <fmt:formatDate value="${pm.manageTime }" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm:ss':'yyyy-MM-dd HH:mm:ss'}"/>
                    </c:when>
                    <c:when test="${pm.status==2}">
                        <fmt:formatDate value="${pm.manageTime }" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm:ss':'yyyy-MM-dd HH:mm:ss'}"/>
                    </c:when>
                </c:choose>
            </td>
            <td><c:choose>
                <c:when test="${pm.status==0 || pm.status > 3}">
                  <c:if test="${pm.commandId>0}"><span class="red">${L:l(lan,"打币中")}</span></c:if>
                  <c:if test="${pm.commandId<=0}"><span class="orange">${L:l(lan,"待处理") }</span></c:if>
                </c:when>
                <c:when test="${pm.status==1}"><span class="red">${L:l(lan,"失败")}</span></c:when>
                <c:when test="${pm.status==2}"><span class="green">${L:l(lan,"成功")}</span></c:when>
                <c:when test="${pm.status==3}"><span class="gray">${L:l(lan,"已取消")}</span></c:when>
              </c:choose>
            </td>
            <td><fmt:formatNumber value=" ${pm.amount}"  pattern="#0.000#####" /></td>
            <td><fmt:formatNumber value="${pm.afterAmount}"  pattern="#0.000#####" /></td>
            <td><c:choose>
                <c:when test="${pm.status<=0 && pm.commandId<=0}"><a href="javascript:cancelOut('${pm.id}')">${L:l(lan,"取消")}</a></c:when>
                <c:otherwise><span class="gray">-</span></c:otherwise>
              </c:choose>
            </td>
          </tr>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <tr>
        <td colspan="7">
        	<div class="bk-norecord">
            <p><i class='iconfont2 mr5'>&#xe653;</i>${L:l(lan,'暂时没有相关记录')}</p>
          </div>
        </td>
        </tr>
    </c:otherwise>
  </c:choose>
 </tbody>
</table>
<c:if test="${fn:length(dataList)>0}">
<div class="lk_page">
  <div id="page_navA" class="page_nav">
    <div class="con">
      <c:if test="${pager!=null}">${pager}</c:if>
      </div>
  </div>
</div>
</c:if>