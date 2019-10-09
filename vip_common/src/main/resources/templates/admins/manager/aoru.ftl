<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>更新用户</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
   <#include "/admins/top.ftl" />
	
   <script type="text/javascript" src="${static_domain }/statics/js/admin/admin.js"></script>
  
   <script type="text/javascript" src="${static_domain }/statics/js/common/upload.js"></script>
   <link href="${static_domain }/statics/css/en/upload.css" rel="stylesheet" type="text/css" />

	<script type="text/javascript">
		$(function(){
		    $("#admin_user_update").Ui();
		});
		function save(){
			vip.ajax({formId : "admin_user_update" , url : "/admin/manager/doAoru" , div : "admin_user_update" , suc : function(xml){
				parent.vip.list.reload();
			    parent.Right($(xml).find("Des").text());
			}});
		}
	</script>
	<style type="text/css">
	.form-tit{width: 95px;}
	
	.form-btn {
	    padding: 15px 0 0 95px;
	}
	</style>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit">
					用户名：
				</div>
				<div class="form-con">
					<input errormsg="请检查字段AdmName长度小于20的字符串(每个中文算两个字符),注意,本字段功能如下: 用户名"
						id="admName" mytitle="AdmName要求填写一个长度小于20的字符串" name="admName"
						pattern="limit(0,20)" size="20" type="text" value="${(curData.admName) ! ''}"
						valueDemo="请输入用户名"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">
					真实姓名：
				</div>
				<div class="form-con">
					<input
						errormsg="请检查字段AdmUName长度小于30的字符串(每个中文算两个字符),注意,本字段功能如下: 真实姓名"
						id="admUName" mytitle="AdmUName要求填写一个长度小于30的字符串" name="admUName"
						pattern="limit(0,30)" size="20" type="text" value="${(curData.admUName) ! ''}"
						valueDemo="例：参数例子"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">
					电话：
				</div>
				<div class="form-con">
					<input
						id="telphone" mytitle="请输入管理员电话号码" name="telphone"
						size="30" type="text" value="${(curData.telphone) ! ''}"
						valueDemo="请输入管理员电话号码"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">
					邮箱：
				</div>
				<div class="form-con">
					<input id="email" mytitle="请输入管理员邮箱" name="email"
						size="30" type="text" value="${(curData.email) ! ''}"
						valueDemo="请输入管理员邮箱"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">
					密码：
				</div>
				<div class="form-con">
					<input
						errormsg="请检查字段AdmPassword长度小于100的字符串(每个中文算两个字符),注意,本字段功能如下: 密码"
						id="admPassword" mytitle="用户密码已经不可逆的加密了,所以无法在这进行编辑,如果输入了密码就代表要更改原来的用户密码为现在密码"
						name="admPassword"   size="20" type="password"
						value="" />
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">
					角色编号：
				</div>
				<div class="form-con">
					   <select id="admRoleId" name="admRoleId" >
					     <#if roles??>
					        <#list roles as list>
					            <option  <#if ((curData.admRoleId)?? && list.id?number == curData.admRoleId)>selected="selected"</#if> value="${list.id}">${list.roleName}</option>
					        </#list>
					     </#if> 
						
					    </select>
				</div>
			</div>
					
			<div class="form-line">
				<div class="form-tit">
					备注：
				</div>
				<div class="form-con">
						<textarea errormsg="长度太长，不能超过50个字符"
							id="admDes" mytitle="可以添加一些描述性文字" name="admDes"
							pattern="limit(0,50)" size="50" type="text" valueDemo="例：参数例子" cols="30" rows="3">${(curData.admDes) ! ''}</textarea>
				</div>
				
			</div>
			
			<#if secret??>
				<div class="form-line">
					<div class="form-tit ">
						管理员标示：
					</div>
					<div class="form-con"> 
						<img src="${url!''}" width="120px" height="120px" style="float:left;"/>
						<input type="text" id="secret" name="secret" style="margin-left: 10px;" value="${secret!''}"/>
					</div>
				</div>
				<div class="form-line">
					<div class="form-tit ">
						输入验证码：
					</div>
					<div class="form-con">
						<input type="text" id="aCode" name="aCode" value="" mytitle="请输入验证码。" errormsg="输入验证码。" pattern="limit(0,10)"/>
					</div>
					<div class="formDes" style="float: left;">
					</div>
				</div>
			</#if>
			<#if (curData.secret)??>
				<div class="form-line">
					<div class="form-tit ">
						密钥已添加：
					</div>
					<div class="form-con">
						<a href="javascript:seeF(${(curData.admId) ! '' })">查看</a>
					</div>
				</div>
			</#if>
			
			<div class="form-line">
				<div class="form-tit">
					管理员性别：
				</div>
				<div class="form-con">
					 <input name="admSex"  type="radio" value="1" <#if (curData.admSex)?? && (curData.admSex)==1>checked='checked'</#if> /><span style="float: left;">男</span>
                      <input  type="radio" name="admSex" value="2"  <#if (curData.admSex)?? && (curData.admSex)==2>checked='checked'</#if>/><span style="float: left;">女</span>
				</div>
			</div>
	
				
					<div class="form-line" style="display:none;">
					<div class="form-tit">
						所属部门：
					</div>
					<div class="form-con">
						<select id="admPartId" name="admPartId" type="text" valueDemo="例如：3">
							<option value="0" <#if (curData.admPartId)?? && curData.admPartId==0>"selected"</#if> >
								客服
							</option>
							<option value="10" <#if (curData.admPartId)?? && curData.admPartId==10>"selected"</#if>>
								销售
							</option>
							<option value="2" <#if (curData.admPartId)?? && curData.admPartId==2>"selected"</#if>>
								人事
							</option>
							<option value="3" <#if (curData.admPartId)?? && curData.admPartId==3>"selected"</#if>>
								主管
							</option>
							<option value="5" <#if (curData.admPartId)?? && curData.admPartId==5>"selected"</#if>>
								招商
							</option>
							<option value="4" <#if (curData.admPartId)?? &&curData.admPartId==4>"selected"</#if>>
								编外
							</option>
						</select>
					</div>
				</div>
			
	
		
			<div class="form-line">
				<div class="form-tit">
					是否被锁定：
				</div>
				<div class="form-con">
					<select id="isLocked" name="isLocked" valueDemo="例如：3">
						<option value="0" <#if curData?? && curData.isLocked == 0>selected="selected"</#if>>				
							没有被锁定
						</option>
						<option value="1" <#if curData?? && curData.isLocked == 1>selected="selected"</#if>>
							已经被锁定
						</option>
					
					</select>
				</div>
				<div class="formDes">
					是否被锁定了,锁定后就不能再登录了
				</div>
			</div>
			
			<div class="form-line">
		    	<div  class="form-tit">您的短信验证码:</div>
			    <div class="form-con">
			    	<input type="password" id="code" name="code" style="width:100px;" class="input" position="n" mytitle="请输入发送到您手机上的验证码" errormsg="验证码错误" errorName="验证码" pattern="limit(4,10)"/>
			    	<button type="button" id="ajax_phone_get">获取验证码</button>	
			    </div>
		    </div>
			
			<div class="form-line">
		    	<div  class="form-tit">您的谷歌验证码:</div>
			    <div class="form-con">
			    	<input type="password" class="input" style="width:100px;" name="mCode" id="mCode" value="" mytitle="请输入移动设备上生成的验证码。" errormsg="验证码错误" pattern="limit(4,10)"/>
			    </div>
		    </div>
			
			<div class="form-line">
				<div class="form-con">
					<input id="admId" name="admId" type="hidden" value="${(curData.admId) ! ''}"/>
				</div>
			</div>
			
			<div class="form-btn">
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <!--<a href="#" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>-->
			</div>
		</div>
<script type="text/javascript">

function seeF(id){
	Iframe({
	    Url:"/admin/manager/seeF?admId="+id,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:460,
        Height:380,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:"安全验证"
	});
}

function seeS(sec){
	Iframe({
	    Url:"/admin/manager/seeSecret?secU="+encodeURIComponent(sec),
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:460,
        Height:380,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:"查看密钥"
	});
}

</script>
	</body>
</html>
