<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<c:set var="isIndividualUser" value="${empty au || au.type != 2}" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>${isIndividualUser ? "个人用户照片" : "企业用户明细"}</title>
		<jsp:include page="/admins/top.jsp" />
	</head>
	<body>
		<table style="border:1px;width:96%;margin-left:10px;">
			<tr><td colspan="3">认证次数：${authTimes }</td></tr>
			<tr><td colspan="3"><hr /></td></tr>
			<c:forEach items="${dataList }" var="data">
				<tr>
					<td style="width:33%"><span>${data.authType==1?'初级认证':'高级认证'}</span></td>
					<td style="width:33%"><span><fmt:formatDate value="${data.submitTime }" pattern="yyyy-MM-dd HH:mm:ss" /></span></td>
					<td style="width:33%"><span>
					<c:choose>
						<c:when test="${data.status!=5 && data.status!=6 && data.status!=7 }">
						审核人：${data.aUser.admName }(${data.aUser.id })
						</c:when>
						<c:otherwise>
							系统自动通过
						</c:otherwise>
					</c:choose>
					</span></td>
				</tr>
				<c:if test="${data.status!=5 && data.status!=6 && data.status!=7 }">
				<tr>
					<td colspan="3">
					<c:choose>
						<c:when test="${data.type != 2 }">
							<img width="100" src="${imagePrefix}${data.frontalImg}" alt="身份证正面" title="身份证正面"/>
							<img width="100" src="${imagePrefix}${data.backImg}" alt="身份证反面" title="身份证反面"/>
							<img width="100" src="${imagePrefix}${data.loadImg}" alt="手持身份证" title="手持身份证"/>
							<img width="100" src="${imagePrefix}${data.proofAddressImg}" alt="住址证明" title="住址证明"/>
						</c:when>
						<c:otherwise>
							<img width="100" src="${imagePrefix}${au.businessLicenseImg}" alt="营业执照" title="营业执照"/>
							<img width="100" src="${imagePrefix}${au.taxRegistrationCertificateImg}" alt="税务登记证" title="税务登记证"/>
							<img width="100" src="${imagePrefix}${au.organizationCodeImg}" alt="组织机构代码证" title="组织机构代码证"/>
							<img width="100" src="${imagePrefix}${au.frontalImg}" alt="法人身份证正面照" title="法人身份证正面照"/>
							<img width="100" src="${imagePrefix}${au.backImg}" alt="法人身份证背面照" title="法人身份证背面照"/>
							<img width="100" src="${imagePrefix}${au.linkerFrontalImg}" alt="联系人身份证正面照" title="联系人身份证正面照"/>
							<img width="100" src="${imagePrefix}${au.linkerBackImg}" alt="联系人身份证正面照" title="联系人身份证正面照"/>
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
				</c:if>
				<tr>
					<td colspan="3">
						<c:choose>
							<c:when test="${data.status==1 }">高级认证-待审核</c:when>
							<c:when test="${data.status==2 }">高级认证-通过</c:when>
							<c:when test="${data.status==3 }">高级认证-不通过：${data.reason }</c:when>
							<c:when test="${data.status==5 }">初级认证-待审核</c:when>
							<c:when test="${data.status==6 }">初级认证-通过</c:when>
							<c:when test="${data.status==7 }">初级认证-不通过：${data.reason }</c:when>
							<c:otherwise>-</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr><td colspan="3"><hr /></td></tr>
			</c:forEach>
		</table>
	</body>
</html>
