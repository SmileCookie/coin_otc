<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<jsp:include page="/admins/top.jsp" />

	<script type="text/javascript">
	$(function(){
	  $("#admin_user_update").Ui();
	});//结束body load部分
	
	function save(){
		vip.ajax({formId : "admin_user_update" , url : "/admin/financial/sign/doAoru" , div : "admin_user_update" , suc : function(xml){
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
                                  数量：
               </div>
               <div class="form-con">
                  <input
                     errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
                     id="num" name="num"
                     pattern="limit(0,50)" size="20" type="text" value="${curData.num}"
                     />
               </div>
            </div>		
		
			<div class="form-line">
				<div class="form-tit">
					签名：
				</div>
				<div class="form-con">
					<textArea errormsg="字段长度小于9000的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
						id="sign" name="sign" type="text" style="width:400px;height:100px;"
						>${curData.sign}</textArea>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					状态：
				</div> 
				<div class="form-con">
					<input name="status"  type="radio" value="未使用" <c:if test="${curData.status == '未使用' }">checked="checked"</c:if> /><span style="float: left;">未使用</span>
                    <input  type="radio" name="status" value="已使用" <c:if test="${curData.status == '已使用' }">checked="checked"</c:if> /><span style="float: left;">已使用</span>
                    <input  type="radio" name="status" value="已过期" <c:if test="${curData.status == '已过期' }">checked="checked"</c:if> /><span style="float: left;">已过期</span>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-con">
					<input id="id" name="id" type="hidden" value="${curData.myId}"/>
				</div>
			</div>
			
			<div class="form-btn">
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <!--<a href="#" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>-->
			</div>
		</div>

	</body>
</html>
