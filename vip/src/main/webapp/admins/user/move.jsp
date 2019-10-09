<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
<link rel="shortcut icon" href="${static_domain }/statics/img/favicon/favicon.ico?V201410" type="image/x-icon" />
<link rel="apple-touch-icon-precomposed" href="${static_domain }/statics/img/favicon/touch-icon.png"/>
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/main.css" />
<script type="text/javascript" src="${static_domain }/statics/js/common/jquery.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/common/webcommon.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>矿机管理 - 新增矿机配置</title>
<script type="text/javascript">
function showGoogleCode(){
	var ids = $("#ids").val();
	if(ids == ""){
		BwModal.alert("您还没有选择需要操作的矿机.");
		return;
	}
	var html = "";
		html += "<div id=\"GoogleCodeBox\">";
		html += "  <ul>";
		html += "    <li>";
		html += "      <span>请输入谷歌验证码：<\/span>";
		html += "      <input id=\"googleCode\" name=\"googleCode\" type=\"text\" class=\"hold\" value=\"\" autocomplete=\"off\">";
		html += "    <\/li>";
		html += "  <\/ul>";
		html += "<\/div>";
    JuaBox.info(html,{btnNum:"2",btnFuc1:"checkGoogleCode();"});
}

function checkGoogleCode(){
	var gcode = $("#googleCode").val();
	if(gcode == ""){
		JuaBox.info("请输入Google验证码");
		return;
	}
	setupConfig(gcode);
	JuaBox.close();
}

function setupConfig(gcode){
	var id = $("#minerID").val();
	$("#dodeal").hide();
	$("#dealing").show();
	$.getJSON("/admin/user/doMove?id="+id+"&mCode="+gcode+"&coint=${coint.stag}&userId=${userId }", function(json){
		if(json.isSuc){
			JuaBox.info(json.des, {btnFuc1:"location.reload()"});
		}else{
			JuaBox.info(json.des);
			$("#dodeal").show();
			$("#dealing").hide();
		}
	});
}

function toggle(obj,pid){
	$(".m-miner-list.set dd").removeClass("on");
	$(obj).addClass("on");
	$("#minerID").val(pid);
}

function editpool(id, pool, port,user){
	$("#pool").val(pool);
	$("#user").val(user);
	$("#port").val(port);
	$("#id").val(id);
	$("#s_btn").hide();
	$("#u_btn").show();
	$("#u_btn2").show();
}
function editcancel(){
	$("#pool").val("");
	$("#user").val("");
	$("#port").val("");
	$("#id").val("");
	$("#s_btn").show();
	$("#u_btn").hide();
	$("#u_btn2").hide();
}
</script>
</head>
<body>
<style>
.minerBox{width:100%;}
.minerBox .lp{width:100%;}
.minerBox .fd { background-image:none;width: 100%; margin:0;}
.minerBox .fd .line { width:100%; height:5px; background-color:#ddd; margin-bottom:15px;}
.minerBox .fd .line .bar { float:left; height:5px; background-color:#3eb084;
-ms-transition: all 0.2s ease-in;
-moz-transition: all 0.2s ease-in;
-webkit-transition: all 0.2s ease-in;
-o-transition: all 0.2s ease-in;
transition: all 0.2s ease-in;
}
.m_load { border:none; padding:0; margin-top: 10px;}
.m_load .m-bd p { margin-bottom:0;}

.m-miner-list {}
.m-miner-list dt { width:100%; height:40px; line-height:30px; font-size:18px; color:#333;}
.m-miner-list dd { width:25%; height:70px; float:left; *display:inline;}
.m-miner-list dd p { height:35px; padding:10px; line-height:35px; margin-right:10px; border:1px solid #ddd; color:#666; background-color:#f9f9f9; position:relative; cursor:pointer;}
.m-miner-list dd.on p { color:#333; border:1px solid #6cc148; background-color:#EFFCEF;}
.m-miner-list dd p .ico { width:32px; height:32px; line-height:32px; display:block; position:absolute; top:0; right:0; background-image:url(/statics/img/pool/lk-ico.png); background-position:-18px -2734px; background-repeat:no-repeat;}
.m-miner-list dd.on p .ico { background-position:-18px -2702px;}

</style>
    <%--矿池设置--%>
    <div class="minerBox clearfloat">
      <div class="ld lp">
        <div class="hd">
          <h2>设置矿池<br></h2>
        </div>
        <div class="bd" id="">
	      <div class="m-miner-list ed">
            <dl class="clearfloat">
              <dt><p>当前运行的矿池地址[区域：<font style="color:red;">${poolArea.value}</font>]</p></dt>
              <dd class="on"><p>${current.name }<i class="ico"></i></p></dd>
            </dl>
          </div>
          <hr/>
	      <div class="m-miner-list set">
            <dl class="clearfloat">
              <dt><p>可设置的矿池</p></dt>
              <input type="hidden" id="minerID" name="cbx" value="" />
              <c:forEach items="${lists }" var="list">
                <dd class="<c:if test='${list.id eq current.id }'>on</c:if>"  onclick="toggle(this,${list.id })">
                  <p>${list.name }<i class="ico"></i></p>
                </dd>
              </c:forEach>
            </dl>
	      </div>
        </div>
      </div>
      <%-- <div class="rd rp">
        <div class="hd">
          <h2>矿池管理<br><span>增加或修改矿池配置信息</span></h2>
        </div>
        <div class="bd">
          <ul>
            <li>
              <span>矿池地址：</span>
              <input id="pool" name="pool" type="text" class="hold" value="" autocomplete="off">
            </li>
            <li>
              <span>端口：</span>
              <input id="port" name="port" type="text" class="hold" value="" autocomplete="off">
            </li>
            <li>
              <span>矿池用户名：</span>
              <input id="user" name="user" type="text" class="hold" value="${user }" autocomplete="off" readonly="readonly"><font color="red" style="font-size:10px;">用户名不可编辑</font>
            </li>
            <li>
              <span style="font-size:13px;">Google验证码：</span>
              <input id="gcode" name="gcode" type="text" class="hold" value="" autocomplete="off">
            </li>
            <li>
              <span></span>
              <input type="hidden" name="id" id="id" value=""/>
              <a id="s_btn" class="btn orange" href="javascript:save();" style="width:182px; padding:0 20px; font-size:18px; height:40px; line-height:40px;">添加矿池配置</a>
              <a id="u_btn" class="btn orange" href="javascript:save();" style="display:none; width:70px; padding:0 20px; font-size:18px; height:40px; line-height:40px;">修改</a>
              <a id="u_btn2" class="btn green" href="javascript:editcancel();" style="display:none; width:70px; padding:0 20px; font-size:18px; height:40px; line-height:40px;">取消</a>
            </li>
          </ul>
        </div>
      </div> --%>
      <div class="clear"></div>
      <div class="fd" style="text-align:right;">
        <hr/>
        <a class="btn green next" href="javascript:;" id="dodeal" onclick="showGoogleCode()">立即设置</a>
        <a class="btn gray next" style="display:none;" href="javascript:;" id="dealing">正在处理...</a>
        <div class="clear"></div>
      </div>
    </div>
</body>
</html>