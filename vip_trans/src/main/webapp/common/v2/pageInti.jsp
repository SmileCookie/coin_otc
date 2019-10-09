<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<!--[if lt IE 9]>
  <div class="alert alert-warning text-left" role="alert" style="margin-bottom:0; border-radius:0;">
    <p><i class="fa fa-exclamation-triangle fa-fw"></i>你的浏览器不支持Bitbank的一些新特性，请升级或更换你的浏览器至以下新版本。</p>
    <p>
      <a class='alert-link' href="http://rj.baidu.com/soft/detail/14744.html" target="_blank">(1)、谷歌浏览器(百度下载)</a><br>
      <a class='alert-link' href="https://www.google.com/intl/zh-CN/chrome/browser/" target="_blank">(2)、谷歌浏览器(官方下载)</a><br>
      <a class='alert-link' href="http://www.firefox.com.cn/download/" target="_blank">(3)、火狐浏览器</a><br>
      <a class='alert-link' href="http://chrome.360.cn/" target="_blank">(4)、360极速浏览器</a>
    </p>
    <p>HTML5时代已经来临，是时候换个浏览器了...</p>
  </div>
<![endif]-->
<div class="weixinImg" style="width:0; height:0; overflow: hidden;">
  <img src="/statics/img/favicon/app-icon-512.png" title="vip.COM" alt="vip.COM"> 
</div>
<div class="bk-animationload">
  <div class="bk-preloader"><p>${L:l(lan,'载入动画提示语句') }</p></div>
</div>