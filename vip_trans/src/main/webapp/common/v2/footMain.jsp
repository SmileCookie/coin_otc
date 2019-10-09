<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

  <footer class="bk-footer sm-text-left">
    <div class="bk-footer-hd">
      <div class="container">
        <div class="row">
          <div class="col-xs-3">
            <div class="bk-footer-contact">
              <h5><a href="${static_domain }" target="_self"><img src="${static_domain }/statics/img/v2/common/vip_logo_foot.png" alt="" style="height:36px;width:auto;margin-bottom:20px;"/></a></h5>
              <p class="sns">
                <%--<a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="<img src='/statics/img/bitbank/home/ecode.png'><p class='text-center'>下载IOS App</p>" href="/bitbank/service/app" target="_blank"><i class="fa fa-apple"></i></a>
                <a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="<img src='/statics/img/bitbank/home/ecode.png'><p class='text-center'>下载Android App</p>" href="/bitbank/service/app" target="_blank"><i class="fa fa-android"></i></a>
                <a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="FaceBook：BitBank" href="https://www.facebook.com/pages/BitBank/569310546505271?ref=aymt_homepage_panel" target="_blank"><i class="fa fa-facebook"></i></a>
                <a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="Twitter：BitBankCom" href="https://twitter.com/BitBankCom" target="_blank"><i class="fa fa-twitter"></i></a>--%>
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="新浪微博：中国比特币官方微博" href="http://weibo.com/312328098" target="_blank"><i class="fa fa-weibo"></i></a>
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="<img src='${static_domain }/statics/img/v2/common/ma-weixin.png' style='width:120px;height:120px;'><p class='text-center'>btcwinex官方微信</p>"><i class="fa fa-weixin"></i></a>
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="企业QQ：4006166611" onClick="javascript:window.open('http://b.qq.com/webc.htm?new=0&sid=4006166611&eid=218808P8z8p8K8x8K808p&o=www.vip.com&q=7&ref='+document.location, '_blank', 'height=502,width=644,toolbar=no,scrollbars=no,menubar=no,status=no');"><i class="fa fa-qq"></i></a>
                <a class="" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="企业邮箱：4006166611@b.qq.com" href="mail:4006166611@b.qq.com" target="_self"><i class="fa fa-envelope"></i></a>
              </p>
            </div>
          </div>
          <div class="col-xs-2">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'关于我们') }</h5>
              <p class="link"><a href="${main_domain }/help/aboutus" target="_blank">公司简介</a></p>
              <p class="link"><a href="${main_domain }/help/zizhi" target="_blank">资质证明</a></p>
              <p class="link"><a href="${main_domain }/help/contact" target="_blank">联系我们</a></p>
            </div>
          </div>
          <div class="col-xs-2">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'下载支持') }</h5>
              <p class="link"><a href="${main_domain }/i/developer" target="_blank">API文档</a></p>
            </div>
          </div>
          <div class="col-xs-2">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'客户服务') }</h5>
              <p class="link"><a href="${main_domain }/help/onlineqa" target="_blank">帮助中心</a></p>
              <p class="link"><a href="${vip_domain }/u/level" target="_blank">积分等级</a></p>
              <p class="link"><a href="${main_domain }/help/service" target="_blank">服务协议</a></p>
              <p class="link"><a href="${main_domain }/help/jianyi" target="_blank">建议意见</a></p>
            </div>
          </div>
          <div class="col-xs-3">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'联系我们') }</h5>
              <p class="link"><i class="fa fa-phone-square fa-fw"></i><a href="tel:4006166611" target="_self">国内：400-616-6611</a></p>
              <p class="link"><i class="fa fa-phone-square fa-fw"></i><a href="tel:00860104006166611" target="_self">海外：+0086 010-400 616 6611</a></p>
              <p class="link"><i class="fa fa-envelope fa-fw"></i><a href="mail:4006166611@b.qq.com" target="_self">邮箱：4006166611@b.qq.com</a></p>
              <p class="link"><i class="fa fa-coffee fa-fw"></i>媒体合作：13331191183</p>
              
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-12">
            <div class="bk-footer-menu text-center">
              <p>Copyright © 2013-<script>document.write(new Date().getFullYear());</script> - vip.COM All Rights Reserved <a class="" href="http://www.miitbeian.gov.cn/" target="_blank">京ICP备12021837号-11</a></p>
              <p><a class="kexin" id="___szfw_logo___" href="https://credit.szfw.org/CX20150721010372070111.html" title="诚信网站身份验证" target="_blank">诚信网站身份验证</a></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
  
  <script>
  seajs.use(["module_market","module_common"],function(market){
	  market.init();
  });
  
  </script>
  
  
  