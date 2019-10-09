<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

  <footer class="bk-footer sm-text-left">
    <div class="bk-footer-hd">
      <div class="container">
        <div class="row">
          <div class="col-sm-4 col-xs-12">
            <div class="bk-footer-about">
              <h5><a href="/" target="_self"><img data-src="/statics/img/bitbank/common/logo-f.png" alt=""/></a></h5>
              <p>${L:l(lan,"比银集团介绍") }</p>
              <p><a class="hideen" href="/bitbank/aboutus" target="_self">${L:l(lan,'了解更多') }</a></p>
            </div>
          </div>
          <div class="col-sm-4 col-xs-12">
            <div class="bk-footer-eagle">
              <h5>${L:l(lan,"比特币人物") }</h5>
              <ul class="clearfix">
                <li><a href="http://weibo.com/u/3992186000" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing1.jpg" alt=""/></a></li>
                <li><a href="http://weibo.com/fangfangbtckan" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing2.jpg" alt=""/></a></li>
                <li><a href="http://weibo.com/huobidashu" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing3.jpg" alt=""/></a></li>
                <li><a href="http://weibo.com/u/5375160751" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing4.jpg" alt=""/></a></li>
                <li><a href="http://weibo.com/u/3244140934" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing5.jpg" alt=""/></a></li>
                <li><a href="http://weibo.com/u/1679177602" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing6.jpg" alt=""/></a></li>
                <li><a href="http://weibo.com/nanning1234" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing7.jpg" alt=""/></a></li>
                <li><a href="http://weibo.com/u/3178119113" target="_blank"><span></span><img data-src="/statics/img/bitbank/common/wing8.jpg" alt=""/></a></li>
              </ul>
            </div>
          </div>
          <div class="col-sm-4 col-xs-12">
            <div class="bk-footer-contact">
              <h5>${L:l(lan,'联系我们') }</h5>
              <p class="link hideen"><i class="fa fa-phone fa-fw"></i><a href="tel:4000678456" target="_self">4000-678-456</a></p>
              <p class="link"><i class="fa fa-envelope fa-fw"></i><a href="mail:Support@Bitbank.com" target="_self">Support@Bitbank.com</a></p>
              <p class="link hideen"><i class="fa fa-map-marker fa-fw"></i><a href="/bitbank/contact" target="_blank">${L:l(lan,'深圳南山区高新中四街31号研祥科技大厦10A') }</a></p>
              <p class="sns">
                <%--<a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="<img src='/statics/img/bitbank/home/ecode.png'><p class='text-center'>下载IOS App</p>" href="/bitbank/ac/app" target="_blank"><i class="fa fa-apple"></i></a>
                <a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="<img src='/statics/img/bitbank/home/ecode.png'><p class='text-center'>下载Android App</p>" href="/bitbank/ac/app" target="_blank"><i class="fa fa-android"></i></a>--%>
                <a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="FaceBook：BitBank" href="https://www.facebook.com/pages/BitBank/569310546505271?ref=aymt_homepage_panel" target="_blank"><i class="fa fa-facebook"></i></a>
                <a role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="Twitter：BitBankCom" href="https://twitter.com/BitBankCom" target="_blank"><i class="fa fa-twitter"></i></a>
                <a class="hideen" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="新浪微博：Bitbank比特币银行" href="http://weibo.com/5553210320/profile?topnav=1&wvr=6" target="_blank"><i class="fa fa-weibo"></i></a>
                <a class="hideen" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="<img src='/statics/img/bitbank/home/ecode.png'><p class='text-center'>Bitbank官方微信</p>"><i class="fa fa-weixin"></i></a>
                <a class="hideen" role="button" data-toggle="popover" data-placement="top" data-html="true" data-trigger="hover" data-content="企业QQ：4006166611" onClick="javascript:window.open('http://b.qq.com/webc.htm?new=0&sid=4006166611&eid=218808P8z8p8K8x8K808p&o=www.vip.com&q=7&ref='+document.location, '_blank', 'height=502,width=644,toolbar=no,scrollbars=no,menubar=no,status=no');"><i class="fa fa-qq"></i></a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="bk-footer-fd">
      <div class="container">
        <div class="bk-footer-copy text-center">
          <p>Copyright © 2013-<script>document.write(new Date().getFullYear());</script> - Bitbank.Com  All Rights Reserved <a class="hideen" href="http://www.miitbeian.gov.cn/" target="_blank">粤ICP备15069390号</a></p>
        </div>
      </div>
    </div>
  </footer>
  
  <script>
  seajs.use("module_market",function(market){
	  market.init();
  })
  
  </script>
  
  
  