<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'帐号冻结')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link href="${static_domain }/statics/css/web.log.css?V${CH_VERSON }" rel="stylesheet" type="text/css" />

</head>
<body class="body-bg">

<div class="bk-body">
		<jsp:include page="/common/top.jsp" />
		<!--页面中部内容开始-->
		<div class="page-signup ng-scope" ng-controller="authCtrl">
			<div id="logBox" class="wrapper" style="margin-top: 100px;">
    
        <div class="main-body">
            <div class="body-inner">
            
                <div class="card">
                    <div class="card-content">
                        <div class="hd">
                        	<h2>账号冻结</h2>
                        </div>
                        <div class="bd">
                          <div class="bk-sign-main" style="height:100px;">
		 
			<div class="bk-sign-item">
				<p class="text-success" style="margin-top:40px; font-size:18px; line-height:36px;">您的账号存在异常，目前处于冻结状态，<br/>详情请咨询在线客服。</p>
			</div>
		 
	</div>

                    
						
                </div> 
                
               

            </div>
        </div>
    </div>

	
	
	</div>
</div>
<div class="bk-siderBar">
  <ul>
    <li class="old"><a role="button" title="返回旧版" href="https://ow.vip.com/" target="_blank"><span>返回</br>旧版</span></a></li>
    <li class="app"><a role="button" title="APP下载" href="${main_domain }/mobile/app" target="_blank"><i class="fa fa-mobile ft36 mt5"></i></a><div class="showDiv"></div></li>
    <li class="qq"><a role="button" title="在线客服" onclick="window.open('http://b.qq.com/webc.htm?new=0&sid=4006166611&eid=218808P8z8p8K8x8K808p&o=www.vip.com&q=7&ref='+document.location, '_blank', 'height=502,width=644,toolbar=no,scrollbars=no,menubar=no,status=no');"><i class="fa fa-qq"></i></a></li>
    <li class="phone"><a role="button" title="24H客服电话"><i class="fa fa-phone"></i></a><div class="showDiv"><i class="fa fa-phone fa-fw"></i>xxx-xxx-xxxx</div></li>
    <li class="weixin"><a role="button" title="官方微信"><i class="fa fa-weixin"></i></a><div class="showDiv"></div></li>
    <li class="sina"><a role="button" title="官方微博" href="http://weibo.com/312328098" target="_blank"><i class="fa fa-weibo"></i></a></li>
    <li class="top"><a role="button" title="${L:l(lan,'返回顶部')}" id="topScroll"><i class="fa fa-angle-up"></i></a></li>
  </ul>
</div>
</body>
</html>
