<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

  <footer class="bk-footer sm-text-left">
    <div class="bk-footer-hd">
      <div class="container">
        <div class="row">
          <div class="col-md-3">
            <div class="bk-footer-contact">
              <h5><a href="${main_domain }" target="_self"><img src="${static_domain }/statics/img/v2/common/vip_logo_foot.png" alt="" style="height:36px;width:auto;margin-bottom:20px;"/></a></h5>
              <p class="sns">
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="新浪微博：btcwinex官方微博" href="http://weibo.com/312328098" target="_blank"><i class="fa fa-weibo"></i></a>
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="<img src='${static_domain }/statics/img/v2/common/ma-weixin.png' style='width:120px;height:120px;'><p class='text-center'>btcwinex官方微信</p>"><i class="fa fa-weixin"></i></a>
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="企业QQ：4006166611" onClick="javascript:window.open('http://b.qq.com/webc.htm?new=0&sid=4006166611&eid=218808P8z8p8K8x8K808p&o=www.vip.com&q=7&ref='+document.location, '_blank', 'height=502,width=644,toolbar=no,scrollbars=no,menubar=no,status=no');"><i class="fa fa-qq"></i></a>
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="企业邮箱：support@vip.com" href="mail:support@vip.com" target="_self"><i class="fa fa-envelope"></i></a>
              </p>
            </div>
          </div>
          <div class="col-md-2">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'关于我们') }</h5>
              <p class="link"><a href="${main_domain }/i" target="_blank">${L:l(lan,'公司简介') }</a></p>
              <p class="link"><a href="${main_domain }/i/team" target="_blank">${L:l(lan,'管理团队') }</a></p>
              <p class="link"><a href="${main_domain }/i/history" target="_blank">${L:l(lan,'大事记') }</a></p>
              <p class="link"><a href="${main_domain }/i/contact" target="_blank">${L:l(lan,'联系我们') }</a></p>
            </div>
          </div>
          <div class="col-md-2">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'下载支持') }</h5>
              <p class="link"><a href="${main_domain }/i/developer" target="_blank">${L:l(lan,'API文档') }</a></p>
              <p class="link"><a href="${main_domain }/mobile/app" target="_blank">${L:l(lan,'APP下载') }</a></p>
            </div>
          </div>
          <div class="col-md-2">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'客户服务') }</h5>
              <p class="link"><a href="${main_domain }/i/help" target="_blank">${L:l(lan,'使用教程') }</a></p>
              <p class="link"><a href="${main_domain }/i/faq" target="_blank">${L:l(lan,'常见问题') }</a></p>
              <p class="link"><a href="${main_domain }/i/document" target="_blank">${L:l(lan,'服务协议') }</a></p>
              <p class="link"><a href="${main_domain }/i/document?item=11" target="_blank">${L:l(lan,'积分/费率') }</a></p>
            </div>
          </div>
          <div class="col-md-3">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'联系我们') }</h5>
              <p class="link"><i class="fa fa-phone-square fa-fw"></i>${L:l(lan,'国内') }：<a href="tel:4006166611" target="_self">400-616-6611</a></p>
              <p class="link"><i class="fa fa-phone-square fa-fw"></i>${L:l(lan,'海外') }：<a href="tel:00860104006166611" target="_self">+0086 010-4006166611</a></p>
              <p class="link"><i class="fa fa-envelope fa-fw"></i>${L:l(lan,'邮箱') }：<a href="mail:support@vip.com" target="_self">support@vip.com</a></p>
              <p class="link"><i class="fa fa-qq fa-fw"></i>${L:l(lan,'QQ群') }：<a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=c9d1bc4439f52b75a68e4ac4aa00efe1c603e27fca409033953a7ce30b4cdb81" title="${L:l(lan,'btcwinex官方⑩群') }">370439658</a></p>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
            <div class="bk-footer-menu text-left">
            	<p class="footlang pull-right" style="margin-top:-10px;">
              	<a class="btncn" href="javascript:vip.setLan('cn');" title="中文"></a><a class="btnen" href="javascript:vip.setLan('en');" title="English"></a>
              </p>
              <p>
              	Copyright © 2013-<script>document.write(new Date().getFullYear());</script> - vip.COM All Rights Reserved <a class="ml10 mr10" href="http://www.miitbeian.gov.cn/" target="_blank">${L:l(lan,'京ICP备12021837号-11') }</a>
              </p>
              <p>
              	<a class="kexin kx ml0" id="" href="https://ss.knet.cn/verifyseal.dll?sn=e16060311010563779odqi000000&ct=df&a=1&pa=0.2100171889178455" title="${L:l(lan,'可信网站身份验证') }" target="_blank">${L:l(lan,'可信网站身份验证') }</a>
              	<a class="kexin qx" id="" href="http://ec.eqixin.com/?sn=QX8180063206171465352258" title="${L:l(lan,'企业电子身份验证') }" target="_blank">${L:l(lan,'企业电子身份验证') }</a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
  
  <jsp:include page="/common/quickMenu.jsp" />

<script type="text/javascript">
seajs.use(["module_market","module_asset","module_common"],function(market,asset){
	market.init();
	asset.init();
});
</script>
<script type="text/javascript">
vip.vipDomain = "${vip_domain}";
vip.p2pDomain = "${p2p_domain}";
vip.transDomain = "${trans_domain}";
vip.staticDomain = "${static_domain}";
//百度统计
var _hmt = _hmt || [];
(function() {
	setTimeout(function(){
			var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?8edd8561f890e3dfdc82c315469718b3";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
	},3000)
})();
</script>
