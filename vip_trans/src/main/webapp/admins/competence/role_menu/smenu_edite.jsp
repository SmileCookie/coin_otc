<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="${static_domain }/statics/css/admin/global.css" rel="stylesheet" type="text/css"/>
<link href="${static_domain }/statics/css/admin/control.css" rel="stylesheet" type="text/css" /> 
<script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/admin/global.js"></script>

	<script type="text/javascript">
	$(function(){
		$("input[name='has']").each(function(){
			var $this = $(this);
			$this.click(function(){
				if($this.attr("checked")){
					$this.parents(".left").next(".right").find("input[name='shas']").attr("checked" , true); 
				}else{
					$this.parents(".left").next(".right").find("input[name='shas']").attr("checked" , false);
				}
			});
		});
	});
	function save(){
		Ask2({Msg : "确定要保存吗？", call:function(){
			var ids = '';
			$("input[name='shas']").each(function(){
				if($(this).attr("checked")){//当前保存
					if($(this).attr("save") == "false"){//数据库未保存  保存
						ids += ',' + $(this).val() + ":" + true;
					}
				}else{///不保存
					if($(this).attr("save") == "true"){//数据库已保存   删除
						ids += ',' + $(this).val() + ":" + false;
					}
				}
			});
			if(ids.length > 0){
				$("#ids").val(ids.substring(1));
			}
			vip.ajax({formId : "admin_user_update" , url : "/admin/competence/role_menu/doSmenus" , div : "admin_user_update" , suc : function(xml){
		    	Right($(xml).find("Des").text());
			}});
		}});
	}
	
	function plate(url){
		vip.list.aoru({url : "/admin/competence/role_menu/plate?id=${curData.id}&mid=${mid}&url="+encodeURIComponent(url)})
	}
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="win-form-line">
				<h3>当前角色：${curData.roleName}</h3>
			</div>
		
		
			<div class="win-form-line top">
				<span class="left" style="text-align: center;color: #000000;">
					视图
				</span>
				<span class="right">
					子功能
				</span>
			</div>
			<c:forEach items="${groups}" var="data">
				<div class="win-form-line">
					<span class="left">
						${data.value.des}
						<c:if test="${data.value.plate}">
							<a href="javascript:plate('${data.value.url}');">[板块]</a>
						</c:if>
						<span class="group-input"><input type="checkbox" value="${sd.url }" name="has" <c:if test="${sd.inRole}">checked="checked"</c:if> alt="全选/反选"/></span>
						
					</span>
					<span class="right">
						<c:forEach items="${data.value.functions}" var="sd">
							<span class="input-name">
								<span class="group-input"><input type="checkbox" value="${sd.url }" save="${sd.inRole}" name="shas" <c:if test="${sd.inRole}">checked="checked"</c:if>/></span> 
								<span class="group-name">${sd.des} 
									<c:if test="${sd.vc.plate}">
										<a href="javascript:;">[板块]</a>
									</c:if>
								</span>
							</span>
						</c:forEach>
					</span>
				</div>
			</c:forEach>
			<div class="form-btn">
					<input id="id" name="id" type="hidden" value="${id}"/>
					<input id="mid" name="mid" type="hidden" value="${mid}"/>
					<input id="ids" name="ids" type="hidden" value=""/>
					<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">保存</span><i class="right"></i></a> 
					<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">退出</span><i class="right"></i></a>
			</div>
			<div class="form-line"></div>
		</div>

	</body>
</html>
