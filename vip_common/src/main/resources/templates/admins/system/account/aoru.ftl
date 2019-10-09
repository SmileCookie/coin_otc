<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<#include "/admins/top.ftl" />

	<script type="text/javascript">
	$(function(){
	  $("#admin_user_update").Ui();
	});//结束body load部分
	
	function save(){
		vip.ajax({formId : "admin_user_update" , url : "/admin/system/account/doAoru" , div : "admin_user_update" , suc : function(xml){
			parent.vip.list.reload();
		    parent.Right($(xml).find("Des").text());
		}});
	}
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="form-line">
               <div class="form-tit">
                                                   发送名称：
               </div>
               <div class="form-con">
                  <input
                     errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
                     id="sendName" name="sendName"
                     pattern="limit(0,50)" size="20" type="text" value="${(curData.sendName) ! ''}"
                     />
               </div>
            </div>		
		
			<div class="form-line">
				<div class="form-tit">
					发送邮箱：
				</div>
				<div class="form-con">
					<input
						errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
						id="fromAddr" name="fromAddr"
						pattern="limit(0,50)" size="20" type="text" value="${(curData.fromAddr) ! ''}"
						/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					HOST：
				</div>
				<div class="form-con">
					<input
						errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
						id="mailServerHost" name="mailServerHost"
						pattern="limit(0,50)" size="20" type="text" value="${(curData.mailServerHost) ! ''}"
						/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					端口：
				</div>
				<div class="form-con">
					<input
						errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
						id="mailServerPort" name="mailServerPort"
						pattern="limit(0,50)" size="20" type="text" value="${(curData.mailServerPort) ! ''}"
						/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					账号：
				</div>
				<div class="form-con">
					<input
						errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
						id="emailUserName" name="emailUserName"
						pattern="limit(0,50)" size="20" type="text" value="${(curData.emailUserName) ! ''}"
						/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					密码：
				</div>
				<div class="form-con">
					<input
						errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
						id="emailPassword" name="emailPassword"
						pattern="limit(0,50)" size="20" type="password" value="${(curData.emailPassword) ! ''}"
						/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					状态：
				</div> 
				<div class="form-con">
					<input name="status"  type="radio" value="1" <#if (curData.status)?? && (curData.status)==1>checked="checked"</#if>  /><span style="float: left;">可用</span>
                    <input  type="radio" name="status" value="0" <#if (curData.status)?? && (curData.status)==0>checked="checked"</#if>  /><span style="float: left;">不可用</span>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-con">
					<input id="id" name="id" type="hidden" value="${(curData.myId) ! ''}"/>
				</div>
			</div>
			
			<div class="form-btn">
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <!--<a href="#" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>-->
			</div>
		</div>

	</body>
</html>
