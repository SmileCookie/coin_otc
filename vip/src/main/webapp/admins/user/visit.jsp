<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="/admins/top.jsp" />
<style type="text/css">
.form-con .txt{float:left;margin:0 5px 0 3px;}
</style>
</head>
<body>
   <div id="add_or_update" class="main-bd">
      <input type="hidden" id="userId" name="userId" value="${userId }"/>
      <div class="form-line">
         <div class="form-tit">标记：</div>
         <div class="form-con">
            <input type="radio" name="mark" ${fn:split(memo,'О')[0]==1 ? "checked" :"" } value="1"/><img style="float:left; margin:10px 8px 0 0;" src="${static_domain }/statics/img/cn/user/memo/memo_1.png"/>
            <span class="txt">拜访过有交易</span>
			<input type="radio" name="mark" value="2" ${fn:split(memo,'О')[0]==2 ? "checked" :"" }/><img style="float:left; margin:10px 8px 0 0;" src="${static_domain }/statics/img/cn/user/memo/memo_2.png"/>
            <span class="txt">拜访过未交易</span>
			<input type="radio" name="mark" value="3" ${fn:split(memo,'О')[0]==3 ? "checked" :"" }/><img style="float:left; margin:10px 8px 0 0;" src="${static_domain }/statics/img/cn/user/memo/memo_3.png"/>
            <span class="txt">已拜访</span>
<%--			<input type="radio" name="mark" value="4" ${fn:split(memo,'О')[0]==4 ? "checked" :"" }/><img style="float:left; margin:10px 8px 0 0; display: none;" src="${static_domain }/statics/img/cn/user/memo/memo_4.png"/>--%>
<%--			<input type="radio" name="mark" value="5" ${fn:split(memo,'О')[0]==5 ? "checked" :"" }/><img style="float:left; margin:10px 8px 0 0; display: none;" src="${static_domain }/statics/img/cn/user/memo/memo_5.png"/>--%>
         </div>
      </div>
      <div class="form-line">
         <div class="form-tit">拜访记录：</div>
         <div class="form-con">
            <textarea name="memo" id="memo" rows="3" cols="50">${memo }</textarea>
         </div>
      </div>
      <div class="form-btn">
         <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
         <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
      </div>
   </div>
<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function ok(){
	var mark=0;
    $("input[name='mark']").each(function(){
       if($(this).attr("checked")==true){
          mark=$(this).val();
       }
    });

    if(mark==0){
       Wrong("请选择一个标记！");
       return;
    }
	
    var memoVal=$("#memo").val();
    if(memoVal==null||memoVal==""){
       Wrong("请输入备注信息！");
       return;
    }
    //长度限制
    if(memoVal.length>100){
       Wrong("备注信息长度不能超过100个字符，一个汉字占用两个字符！");
       return;
    }
    
    $("#memo").val(mark+"О"+memoVal.replace("О","o"));
    
	var actionUrl = "/admin/user/addVisit";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
		}
	});
}
</script>                
</body>
</html>
