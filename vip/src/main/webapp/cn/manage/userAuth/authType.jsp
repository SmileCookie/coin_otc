<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<div class="content" id="mainForm">
	<h2>${L:l(lan,'身份认证')}</h2>
	<div class="user_auth_type">
		<div class="from_prev">
			<a href="/manage/auth/authentication"> <span> < </span> ${L:l(lan,'返回上一步')} </a>
		</div>
		<div class="user_auth_content">
			<div class="country_head">
				<div class="country_head_title">
					${L:l(lan,'选择发证国家')}
				</div>
				<div class="country_select clearfix">
					<span class="guojia">${L:l(lan,'国家')}:</span>
					<div class="input-group-btn dropdown" id="countryGroup">
						<div class="btn-group dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							<input type="hidden" id="countryCode" name="countryCode" value="">
							<button type="button" class="btn text-nowrap" id="countryText"></button>
							<i class="iconfont2">&#xe600;</i>
						</div>
						<ul class="dropdown-menu" aria-labelledby="countryGroup" style="left:auto;right:0; width:300px; height:400px; overflow-x:hidden; overflow-y:auto;">
							<c:forEach items="${country}" var="coun">
	                    		<li data-value="${coun.code}" data-counname="${coun.name}" data-name="${coun.des}">
									<a><span>${coun.des}</span></a>
								</li>
	                    	</c:forEach>
	                  </ul>
					</div>
				</div>
				<div class="country_type">
					<div class="country_type_head">
						${L:l(lan,'选择证件类型')}
					</div>
					<div class="country_type_body clearfix">
						<a href="/manage/auth/passportauth" class="count_item">
							<img class="user_auth_img" src="${static_domain }/statics/img/user/user_id_3.png"/>
							<div class="user_auth_a_text">${L:l(lan,'护照')}</div>
						</a>
						<a href="/manage/auth/idcardauth" class="count_item mar0">
							<img class="user_auth_img_2" src="${static_domain }/statics/img/user/user_id_2.png"/>
							<div class="user_auth_a_text">${L:l(lan,'身份证')}</div>
						</a>
						<div class="count_item_1">
							<h5>${L:l(lan,'提示：')}</h5>
							<p>
								${L:l(lan,'同一身份证/护照只能认证一个帐号')}
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">

	
	
 	
 	var backName = sessionStorage.getItem("countryname");
 	if(!backName){
 		sessionStorage.setItem("countryCode","+86");
		sessionStorage.setItem("countName",'中国');
		$("#countryCode").val("+86");
		$("#countryText").text("中国");
	}
	else{
		var backVal = sessionStorage.getItem("countryCode");
		$("#countryCode").val(backVal);
		$("#countryText").text(backName);
	}

	var li_list = $("#countryGroup .dropdown-menu li");
	var now_text = $("#countryText").text();
	for (var i = 0; i < li_list.length; i++) {
		var that_name = li_list[i].getAttribute("data-name");
		if(now_text == that_name){
			li_list[i].className = "active";
		}
	}

	$("#countryGroup .dropdown-menu").on("click","li",function(){
		var this_val = $(this).data("value");
		var this_name = $(this).data("name");
		var this_countName = $(this).data("counname");
		$("#countryCode").val(this_val);
		$("#countryText").text(this_name);
		sessionStorage.setItem("countryCode",this_val);
		sessionStorage.setItem("countryname",this_name);
		sessionStorage.setItem("countName",this_countName)
		$("#countryGroup .dropdown-menu li").removeClass("active");
		$(this).addClass("active");
	});
</script>

