<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>后台管理中心</title>
<#include "/admins/top.ftl" />
<script type="text/javascript" src="${static_domain }/statics/js/admin/menu.js"></script>
<script type="text/javascript">
//用于标示是否登录
 var isInTop=true;
$(function(){
	 $("#leftList,#rightIframe,#leftArea").css({"height":$(window).height()});
	
	// $("#leftArea").css({"height":$(document).height()});
	 $(window).resize(function() {
		 var heights=$(window).height();
		 $("#leftList").css({"height":heights});
		 $("#rightIframe").css({"height":heights});
		 $("#leftArea").css({"height":heights});
	 });
})
</script>
<style type="text/css">
html { overflow-x: hidden; overflow-y: auto; }
body {ovserflow:hidden;}
.logo, .menubar .title a, .menubar .group li.current, .copyright {
    background-image: url(${static_domain }/statics/img/admin/leftArea.png);
    background-repeat: no-repeat;
}
 #leftList{
  overflow: hidden;
  OVERFLOW-X:hidden;
 }
 #leftArea{
   overflow: scroll;
   OVERFLOW-X:hidden;
   width:215px;
    padding: 0;
 }
 
 #rightIframe{
  overflow: hidden;
 OVERFLOW-X:hidden;
 }
 @-moz-document url-prefix() {
 #Main{
 OVERFLOW-X:hidden;
 }
 } 
 
 
li.current {
    background-position: 0 -66px;
}

li.current {
    background-image: url(${static_domain }/statics/img/admin/leftArea.png);
    background-repeat: no-repeat;
}

body{font-family:Arial;font-size:12px;line-height:20px;}
body,div,ul,li,dl,dt,dd,ol,h1,h2,h3,h4,h5,h6,form,input,p,table,th,td,button{margin:0;padding:0;}
img,button{border:none;}
ul,li{list-style:none;}
a{text-decoration:none;}
a:hover{text-decoration:underline;}
.container {overflow:hidden;}
.container table{border-collapse:collapse;border-spacing:0;width:100%;}
.container table td{vertical-align:top;}
.container table td.left{
	width:170px;
	background:#25221d;
}
.leftArea{
	background-color:#2e363f;
	width:200px;
	padding-top: 20px;
	padding-right: 0;
	padding-bottom: 20px;
	padding-left: 0;
}
.logo,.menubar .title a,.menubar .group li.current,.copyright{
	background-image: url(${static_domain }/statics/img/admin/leftArea.png);
	background-repeat: no-repeat;
}

.logo{
	 background-position: 0 -223px;
    height: 70px;
    text-align: left;
    width: 200px;
}
.administratorinfo{padding:10px 20px 10px 0;text-align:right;  border-bottom: 1px solid #1F262D; }
.administratorinfo a{color:#ffa500;display:inline-block; font-size:12px;font-family: Arial,"宋体";}
.menubar{
	width:200px;
	text-align:center;
	
}
.menubar .title{line-height:32px;font-size:12px; border-bottom: 1px solid #1F262D;
    border-top: 1px solid #37414B;}
.menubar .title a{
	display:block;
	height:32px;
	color:#f8f8f8;
	padding-right:20px;
	background:none;
 padding: 8px 0 8px 0px;

}

.menubar .title a:hover{
	background-color:#27a9e3;
	text-decoration:none;
	color:white;
	
}
.menubar .group li{height:40px;padding-right:10px;background-color:#1e242b;}
.menubar .group li:hover{background-color:#28b779;}
.menubar .group li a{color:#eaecee;display:inline-block;margin-top:10px; font-size:12px; font-family:微软雅黑;}
.menubar .group li.current{
	background:none;
	background-color:#28b779;
	color:white;
}
.menubar .group li.current a{	color:white;}
.copyright{
	height:115px;
	color:#fff;


	margin:10px 0 10px 10px;
	background:none;
	
}
.copyright h3{font-size:12px;padding:15px 0;}
.rightArea{overflow:hidden;padding:10px 10px 10px 0;}
.rightArea .categories_other {
	height: 1px;
	width: 800px;
	overflow: hidden;
}
.categories {
	margin-bottom: 10px;
}

.categories  .categories_hd  .hdl,.categories  .categories_hd  .hdr,.categories  .categories_hd  .hdcon,.categories  .categories_hd  h3  button,.categories  .categories_ft  .ftl,.categories  .categories_ft  .ftr,.categories  .categories_ft  .ftcon{background:url(/admin/images/categories.png) no-repeat;}
.categories  .categories_hd{height:40px;overflow:hidden;}
.categories  .categories_hd  .hdl{background-position:0 0;height:40px;width:3px;overflow:hidden;float:left;}
.categories  .categories_hd  .hdr{height:40px;width:3px;background-position:right 0;float:right;overflow: hidden;}
.categories  .categories_hd  .hdcon{
	_float:left;
	background-repeat:repeat-x;
	background-position:0 -41px;
	height:40px;
	padding-right: 5px;
	padding-left: 5px;
}
.categories  .categories_hd  h3{
	position:relative;
	float:left;
	color:#fff;
	font-size:16px;
	line-height:40px;
	padding-left:25px;
}
.categories   .categories_hd   h3   button.open,.categories   .categories_hd   h3   button.close,.categories   .categories_hd   h3   button.disable{
	background-color:transparent;
	position:absolute;
	height:11px;
	width:11px;
	overflow:hidden;
	left:5px;
	top:14px;
	cursor:pointer;
}
.categories  .categories_hd  h3  button.open{background-position:-4px 0;}
.categories  .categories_hd  h3  button.close{background-position:-4px -12px;}
.categories  .categories_hd  h3  button.disable{background-position:-4px -24px;}
.categories  .categories_hd  .operation{
	height:40px;
	float: right;
	overflow: hidden;
}
.categories .categories_hd .categories_tab {
	height: 30px;
	padding-top: 10px;
	float: right;
}
.categories  .categories_hd  .categories_tab a {
	float: left;
	background-image: url(../images/categories.png);
	background-repeat: no-repeat;
	height: 30px;
	background-position: right -158px;
	color: #fff;
	text-decoration: none;
	padding-right: 3px;
	line-height: 30px;
	margin-left: 1px;
}
.categories .categories_hd .categories_tab a span {
	background-image: url(../images/categories.png);
	background-repeat: no-repeat;
	background-position: 0px -96px;
	float: left;
	height: 30px;
	cursor:pointer;
	padding-right: 7px;
	padding-left: 10px;
}
.categories   .categories_hd   .categories_tab   a:hover,.categories  .categories_hd  .categories_tab  a.current {
	background-position: right -189px;
	color: #082641;
	text-decoration: none;
}
.categories   .categories_hd   .categories_tab   a:hover   span,.categories  .categories_hd  .categories_tab  a.current  span {
	background-position: 0px -127px;
}
.categories .categories_hd .categories_tab a.current {
	margin-right: 1px;
	margin-left: 1px;
}




.categories  .categories_bd{border-left:1px #d5d5d5 solid;border-right:1px #d5d5d5 solid;padding:10px;zoom:1;}
.categories  .categories_ft{height:5px;overflow:hidden;margin-top:-3px;}
.categories  .categories_ft  .ftl,.categories  .categories_ft  .ftr{height:5px;width:5px;overflow:hidden}
.categories  .categories_ft  .ftl{background-position:0 -82px;float:left;}
.categories  .categories_ft  .ftr{background-position:right -82px;float:right;}
.categories  .categories_ft  .ftcon{background-repeat:repeat-x;background-position:0 -90px;_float:left;height:5px;overflow:hidden;}
.categories  .categories_ft  .ftrow{height:5px;}


/****用于title****/
.divTips{border:1px solid #ffc600;color:#666666;position:absolute;z-index: 100000;display: none;width:240px;}  
.divTips .tl{position:absolute;background:url(/body/images/divTips.png) no-repeat;height:17px;width:9px;left:-9px;top:10px;}
.divTips .tTop{position:absolute;background:url(/body/images/divTipsTop.png) no-repeat;height:9px;width:17px;left:9px;top:-9px;}
.divTips .tRightsBotom{position:absolute;background:url(/body/images/divTipsRights.png) no-repeat;height:17px;width:9px;left:240px;top:-9px;} 
.divTips .divTipsCon{border:3px solid #fff9d4;background:#fff;padding:10px;}
/****/



 
</style>

</head>
<body>
<table height="100%" width="100%" cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>
         <td width="185px" valign="top" id="leftList">
			<div class="leftArea" id="leftArea">
	          <div class="logo"></div>
	          <div class="administratorinfo" id="userMsg"><a href="#"></a><a href="#"></a><a href="#"></a></div>
	          <div class="menubar" id="mainmenu"></div>
	          <div class="copyright" style="display: none;">
	            <li class="content"> <span>事物处理进度</span>
      <div class="progress progress-danger">
        <div style="width: 77%;" class="bar"></div>
      </div>
      <span class="percent">77%</span>
      <div class="stat">3 step / 4 Step</div>
    </li>
    <li class="content" style="margin-top:20px;"> <span>充值强度</span>
      <div class="progress progress-striped">
        <div style="width: 87%;" class="bar"></div>
      </div>
      <span class="percent">87%</span>
      <div class="stat">$8000 / $122330</div>
    </li>
	          </div>
			</div>
         </td>
         <td id="middle" class="resizeBar" style=" background-color: #ededed;width:10px;"> 
             <img width="8" onMouseOut="this.className=''" onMouseOver="this.className='sp_m_over'" src="${static_domain }/statics/img/admin/splitbar_collapse_h.gif" id="sp_img" class="" />
         </td>
         <td style=" vertical-align: top; background-color: white;" id="rightIframe">
             <iframe scrolling="yes" height="100%" frameborder="0" width="100%" id="Main" name="Main"  src="/admin/tipsmsg"></iframe>
		 </td>
</tr>
</tbody>
</table>
</body>
</html>
