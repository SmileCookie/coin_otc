<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/common/head.jsp" />
<style type="text/css">
.loan-info { width: 95%; margin: 0 auto; position: relative; zoom: 1; font-family:"微软雅黑"; }
.loan-info h1 { font-size: 14px; font-weight: 500; font-family: 微软雅黑; height: 30px; line-height: 30px; padding: 0 0 0 1px; }
.loan-info-table td { padding: 10px 10px; border: 1px #eeeeee solid; color: #666666; text-align: left; font-size:20px; font-family:"微软雅黑"; }
.loan-info-table td.tit { width: 105px; text-align: right; background: #fafafa; color: #888888; font-size:12px; }
.loan-info-table td span { color: #CC0000; }
.loan-info-table td span.schedule { display: inline-block; overflow: hidden; vertical-align: -2px; }
.loan-info-table td span.schedule i { background: #ddd; border-radius: 3px 3px 3px 3px; display: inline-block; height: 12px; width: 100px; }
.loan-info-table td span.schedule i u { background: #F06000; border-radius: 3px 3px 3px 3px; display: inline-block; height: 12px; width: 50px; }
.loan-info-list { padding-top: 15px; }
.loan-info-list li { height: 34px; line-height: 34px; padding-bottom: 35px; color: #666666; text-align: left; }
.loan-info-list li .red { font-size: 20px; font-family: "微软雅黑"; }
.loan-info-list li span.tit { width: 105px; text-align: right; display: inline-block; padding: 0 10px; color: #999999; }
.loan-info-list li input { width: 220px; padding: 5px 10px; height: 32px; line-height: 22px; font-size: 18px; color: #666; }
.loan-info-list li span.prompt { color: #666666; }
.loan-info-list li span.red { color: #CC0000; }
.loan-info-list li a.btn { background: #F9F9F9; border: 1px solid #D9D9D9; border-radius: 5px 5px 5px 5px; box-shadow: 0 1px 0 rgba(255, 255, 255, 0.3) inset, 0 1px 1px rgba(50, 50, 50, 0.05); color: #666666; display: inline-block; line-height: 24px; padding: 0 5px; text-align: center; width: 60px; text-decoration: none; }
.loan-info-list li a { color: #3366CC; }
.loan-info-button { padding-top: 10px; text-align: center; }
.loan-info-button a { background: #B61D00; border: 1px solid #AA1800; border-radius: 5px 5px 5px 5px; box-shadow: 0 1px 0 rgba(255, 255, 255, 0.3) inset, 0 1px 1px rgba(50, 50, 50, 0.05); color: #FFFFFF; display: inline-block; font-family: 微软雅黑; font-size: 18px; font-weight: 500; line-height: 38px; padding: 0 10px; text-align: center; text-decoration: none; text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2); width: 150px; }
.loan-info-caption { background: #FFFCF0; position: absolute; bottom: -10px; right: 0; padding: 10px; color: #666666; }
.loan-info-caption strong { display: block; color: #333333; padding: 10px 0; }
.loan-info-caption table { width: 300px; }
.loan-info-caption td { border: 1px #eeeeee solid; background: #fff; padding: 5px; text-align: center; }
.loan-info-caption .txt td { color: #999999; }
/*******control.css end****/
.header { }
.Toolbar { width: 100%; text-align: left; }
.Toolbar h2 { border-bottom: 1px solid #F2F2F2; height: 36px; line-height: 36px; text-indent: 12px; font-size: 14px; font-weight: bold; }
.ps1-close { color: #D5D5D5; cursor: pointer; font-size: 14px; height: 16px; line-height: 16px; position: absolute; right: 10px; text-align: center; top: 10px; width: 16px; }
.ps1-close:hover { color: #666666; text-decoration: none; }
.jqTransformRadioWrapper { margin-top: 10px; margin-right:5px; }
.do { padding:5px 0px 25px; text-align:center;}
</style>
</head>
<body style="background: #FFFFFF;">
<div class="header">
  <div class="Toolbar">
    <h2>
    	${param.flag==1? '续费VIP6' : '升级为VIP6' }
    </h2>
    <div class="ps1-close" onclick="parent.Close();" style="font-weight:bold;">×</div>
  </div>
</div>
<div class="loan-info" id="uimain">
  <ul class="loan-info-list">
    <li style="font-size: 14px;">
    	<span class="tit">提示：</span>
    	<c:if test="${param.flag==1 }">
    		续费VIP6，叠加<span style="color: orange;">30</span>天免提现手续费，将花费<span style="color: red;">888元</span>
    	</c:if>
    	<c:if test="${param.flag==0 }">
    		升级为VIP6，享<span style="color: orange;">30</span>天免提现手续费，将花费<span style="color: red;">888元</span>
    	</c:if>
    	
    </li>
    <li><span class="tit">资金安全密码：</span>
      <input name="payPass" type="password" size="30" pattern="limit(4,18)" errormsg="请输入合法的资金安全密码"/> 
      <a href="${vip_domain}/service/self/forgetsafepwd" target="_blank">&nbsp;忘记资金安全密码</a> </li>
      </ul>
  <hr/>
  <div class="do">
    <a class="alibtn_orange35" tabindex="5" href="javascript:parent.Close();">
    <h4>取消</h4>
    </a> 
    <a tabindex="8" style="margin-left:36px;" href="javascript:dosubmit();" class="alibtn_orange35"/>
    <h4>提交</h4>
    </a> </div>
</div>

<script type="text/javascript">
	$("#uimain").Ui();

	function dosubmit(){
		if(!chbtc.user.checkLogin()){
			return;
		}
		var datas = FormToStr("uimain"); 
		if(datas == null){return;}
		chbtc.ajax({
			 needLogin : true, 
			 formId : "uimain",
			 div : "uimain",
			 url : "/u/level/doUpgrade" ,
			 dataType : "json",
			 suc : function(json){
				parent.Right(json.des , {callback : "reload();"});
			 },
			 err : function(json){
				Wrong(json.des , {isKill : false} );
			 }
		});
	} 
	
	function reload(){
		parent.Close();
		parent.location.reload();
	}
	
</script>
</body>
</html>
