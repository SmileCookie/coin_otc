<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="content user_auth_status" id="mainForm">
<input type="hidden" class="authStatus" value="${authStatus}"/>
	<h2> ${L:l(lan,'身份认证')}</h2>
	<c:choose>
		<c:when test="${authStatus eq 6}">
			<!-- 身份认证成功需要自动跳转到账户页面 -->
		</c:when>
		<c:when test="${authStatus eq 7}">
			<!-- 认证失败 authStatus eq 7-->
			<div class="user_auth_under_warp">
				<section class="user_auth_under fail_user_auth">
					<span class="user_svg_bg user_auth_id_bg_2"></span>
					<br/>
					${L:l(lan,'认证失败，请尝试重新认证。') }
					<div class="user_auth_fail">
						<h5>
							${L:l(lan,'详细信息：') }
						</h5>
						${L:l(lan,'失败原因：')}${reason}
					</div>
					<c:choose>
						<c:when test="${isBlack}">
						<a class="a_authtypeno">${L:l(lan,'重新认证')}</a>
						<p class="user_auth_explain"><i class="iconfont2 mr5"></i>${L:l(lan,'黑名单用户')}</p>
						</c:when>
						<c:when test="${isLock && !isBlack}">
							<a class="a_authtypeno">${L:l(lan,'重新认证')}</a>
							<p class="user_auth_explain"><i class="iconfont2 mr5"></i>${L:l(lan,'锁定认证72小时-1')}<fmt:formatDate value="${lockTime}" pattern="yyyy-MM-dd HH:mm:ss"/>${L:l(lan,'锁定认证72小时-2')}</p>
						</c:when>
						<c:otherwise>
							<a class="a_authtype" href="/manage/auth/authtype">${L:l(lan,'重新认证')}</a>
						</c:otherwise>
				</c:choose>	
					<!-- <a class="a_authtype" href="/manage/auth/authtype">${L:l(lan,'重新认证')}</a> -->
				</section>
			</div>
		</c:when>
		<c:when test="${authStatus eq 5}">
			<!-- 认证中 authStatus eq 5-->
			<div class="user_auth_under_warp">
				<section class="user_auth_under">
					<span class="user_svg_bg user_auth_id_bg_1"></span>
					<br/>
					${L:l(lan,'您的认证文件已提交，请耐心等待工作人员审核。')}
				</section>
			</div>
		</c:when>
		<c:otherwise> <!-- 未认证 4-->
			<div class="user_auth_statement_warp"> 
				<section class="user_auth_statement">
					<h5>${L:l(lan,'声明：')}</h5>
					<div class="sttement_div">
						<p>
							1、${L:l(lan,'声明-1')}
						</p>
						<p>
							2、${L:l(lan,'声明-2')}
						</p>
						<p>
							3、${L:l(lan,'声明-3')}
						</p>
						<p>
							4、${L:l(lan,'声明-4')}
						</p>
						<p>
							5、${L:l(lan,'声明-5')}
						</p>
						<p>
							6、${L:l(lan,'声明-6')}
						</p>
					</div>
				</section>
				<c:choose>
						<c:when test="${isBlack}">
						<a class="a_authtypeno">${L:l(lan,'重新认证')}</a>
						<p class="user_auth_explain"><i class="iconfont2 mr5"></i>${L:l(lan,'黑名单用户')}</p>
						</c:when>
						<c:when test="${isLock && !isBlack}">
							<a class="a_authtypeno">${L:l(lan,'重新认证')}</a>
							<p class="user_auth_explain"><i class="iconfont2 mr5"></i>${L:l(lan,'锁定认证72小时-1')}<fmt:formatDate value="${lockTime}" pattern="yyyy-MM-dd HH:mm:ss"/>${L:l(lan,'锁定认证72小时-2')}</p>
						</c:when>
						<c:otherwise>
								<a class="a_authtype" href="/manage/auth/authtype">${L:l(lan,'开始验证')}</a>
						</c:otherwise>
				</c:choose>
				
			</div>
		</c:otherwise>
	</c:choose>	
		
</div>
<script>
	$(function(){
		(function(){
			var auth_status = $(".authStatus").val();
			if( auth_status == 6 ){
				window.top.location.href="/manage/";
			}
		})()
	})
</script>


