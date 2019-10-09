<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.world.model.entity.user.authen.AuditType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="content authbody" id="mainForm">
	<h2>${L:l(lan,'实名认证设置') }</h2>
  	

		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan, "请选择认证身份")}：</dt>
				<dd>${L:l(lan, "根据国家监管部门规定，您需要做实名信息认证，认证信息一经验证不能修改。")}</dd>
			</dl>
		</div>

		<div class="row">
					<div class="col-sm-6">
						<div class="bk-auth-type">
							<p><i class="fa fa-user fa-3x"></i></p>
							<p><b>${L:l(lan, "我是个人用户")}</b></p>
							<p><a class="btn btn-primary btn-sm btn-block" href="/manage/auth/simple?type=<%=AuditType.individual.getKey()%>">${L:l(lan, "进行个人认证")}</a></p>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="bk-auth-type">
							<p><i class="fa fa-building-o fa-3x"></i></p>
							<p><b>${L:l(lan, "我是企业用户")}</b></p>
							<p><a class="btn btn-primary btn-sm btn-block" href="/manage/auth/simple?type=<%=AuditType.corporate.getKey()%>">${L:l(lan, "进行企业认证")}</a></p>
						</div>
					</div>
			</div>
</div>

