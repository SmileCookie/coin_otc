<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<div class="content" id="mainForm">
	<h2>${L:l(lan,'身份认证')}</h2>
	<div class="user_auth_idcard">
		<div class="from_prev">
			<a href="/manage/auth/authtype"> <span> < </span> ${L:l(lan,'返回上一步')} </a>
		</div>
		<div class="user_auth_content">
			<div class="user_auth_id_text">
				<h4>${L:l(lan,'基本信息-1')}</h4>
				<div class="id_text_wrap clearfix">
					<div class="id_item">
						<span class="name_text name_text_2 ${lan}">${L:l(lan,'姓氏')}：</span>
						<div class="id_text_input_wrap">
							<input id="lastName" class="id_text_input name_toUpCase" type="text" name="id_name" value="" autocomplete="off"/>
						</div>
					</div>
					<div class="id_item marr_100 marr_${lan} clearfix">
						<span class="name_text name_text_1 left ${lan}" >${L:l(lan,'名字')}：</span>
						<div class="id_text_input_wrap left">
							<input id="firstName" class="id_text_input name_toUpCase" type="text" name="id_name" value="" autocomplete="off" />
						</div>
					</div>
					<div class="id_item">
						<span class="name_text name_text_2 ${lan}">${L:l(lan,'身份证号码')}：</span>
						<div class="id_text_input_wrap">
							<input id="idCard" class="id_text_input" type="text" name="id_name" value="" autocomplete="off" />
						</div>
					</div>
					<div class="id_item marr_100 marr_${lan} clearfix">
						<span class="name_text name_text_1 ${lan} left">${L:l(lan,'有效期：')}</span>
						<div class="id_text_input_wrap clearfix">
							<div class="laydate_warp">
								<input readonly="readonly" type="text" class="laydate" id="layDate_1">
							</div>
							<div class="laydate_jiantou">
								-
							</div>
							<div class="laydate_warp">
								<input readonly="readonly" type="text" class="laydate" id="layDate_2">
							</div>
						</div>
					</div>
				</div>
				<div class="id_imgfile_warp">
					<h3 class="h3_file">${L:l(lan,'上传身份证照片')}</h3>
					<div class="file_item clearfix" id="qiniu_1">
						<div class="file_img_item">
							<img class="id_imgs" src="${static_domain }/statics/img/user/user_is_7.png" alt="">
						</div>
						<div class="file_jiantou">
							<span class="user_id_you"></span>
							${L:l(lan,'示例')}
						</div>
						<div class="file_img_item">
							<span class="id_user_id"></span>
							<div class="file_text_1">${L:l(lan,'请上传身份证正面照片')}</div>
							<div class="file_img" id="file_img_1"></div>
						</div>
						<div class="file_text">
							<div class="files">
								<input class="file_img_1" type="file" id="file_input_1" name="file" value="">
								<div class="file_warp">
									${L:l(lan,'选择文件')}
								</div>
							</div>
							<div class="files_text_foot">
								 ${L:l(lan,'上传限制-5')}
							</div>
						</div>
					</div>
					<div class="file_item clearfix"  id="qiniu_2">
						<div class="file_img_item">
							<img class="id_imgs" src="${static_domain }/statics/img/user/user_id_4.png" alt="">
						</div>
						<div class="file_jiantou">
							<span class="user_id_you"></span>
							${L:l(lan,'示例')}
						</div>
						<div class="file_img_item">
							<span class="id_user_id"></span>
							<div class="file_text_2">${L:l(lan,'请上传身份证背面照片')}</div>
							<div class="file_img" id="file_img_2"></div>
						</div>
						<div class="file_text">
							<div class="files">
								<input type="file" class="file_img_2" name="file" id="file_input_2" value="">
								<div class="file_warp">
									${L:l(lan,'选择文件')}
								</div>
							</div>
							<div class="files_text_foot">
								${L:l(lan,'上传限制-5')}
							</div>
						</div>
					</div>
					<div class="file_item clearfix" id="qiniu_3">
						<div class="file_img_item">
							<img class="id_imgs" src="${static_domain }/statics/img/user/user_id_5.png" alt="">
						</div>
						<div class="file_jiantou">
							<span class="user_id_you"></span>
							${L:l(lan,'示例')}
						</div>
						<div class="file_img_item">
							<span class="id_user_id"></span>
							<div class="file_text_3">${L:l(lan,'请上传手持身份证照片')}</div>
							<div class="file_img" id="file_img_3"></div>
						</div>
						<div class="file_text">
							<div class="files">
								<input type="file" class="file_img_3" id="file_input_3" name="file" value="">
								<div class="file_warp">
									${L:l(lan,'选择文件')}
								</div>
							</div>
							<div class="files_text_foot">
								${L:l(lan,'上传限制-5')}
							</div>
						</div>
					</div>
				</div>
				<section class="foot_user_text">
					<p>1. ${L:l(lan,'上传限制-1')}</p>
					<p>2. ${L:l(lan,'上传限制-2')}</p>
					<p>3. ${L:l(lan,'上传限制-3')}</p>
					<p>4. ${L:l(lan,'上传限制-4')}</p>
				</section>
				<section class="user_auth_submit">
					${L:l(lan,'提交申请')}
				</section>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		require(['module_authType'],function(auth){
			auth.init();
			auth.idCardInit();
		});
	});
</script>