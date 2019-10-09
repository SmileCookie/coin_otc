<!doctype html>
<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${L:l(lan,'新增比特币接收地址-Bitbank-比特币理财首选')}</title>
<jsp:include page="/common/head.jsp" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/fast.css" />

<script type="text/javascript" charset="utf-8">
  try{
    var oldDomain=document.domain;
    var ind=oldDomain.indexOf('${baseDomain}');
    document.domain = oldDomain.substring(ind,oldDomain.length)
  } catch(msg) {
    document.domain = '${baseDomain}';  
  }
</script>
<script type="text/javascript">
function delAddr(id){
	//Ask2({Msg:"确定要删除该地址吗？", callback:"toDel("+id+")"});
	JuaBox.info("${L:l(lan,'确定要删除该地址吗？')}",{
		btnFun1:function(JuaId){
			window.top.JuaBox.close(JuaId,function(){
				toDel(id);
			});
		}
	})
}


function reload2(){
	Close();
	location.reload();
}


function send(id){
	parent.$("#receiveAddress").val($.trim($("#text_"+id).text()));
	parent.JuaBox.closeAll();
}

function toDel(id){
	vip.ajax( {
		url : "/manage/account/download/doDel/${coint.stag}?receiveId="+id,
		suc : function(xml) {
			JuaBox.info("${L:l(lan,'删除成功')}", {btnFun1:function(JuaId) {
				 window.top.JuaBox.close(JuaId,function(){
					 var frames = $(window.top.document.getElementsByTagName('iframe'));
					 $(frames[1]).attr('src', $(frames[1]).attr('src') + '&v='+ new Date().getTime());
				 });
			}});
		}
	});
}

function toDelAddress(id){
	Iframe({Url:"/manage/account/download/del/${coint.stag}?id=" + id,
		Width:560,
		Height:300,
		Title:bitbank.L("删除比特币地址"),
		isShowIframeTitle:false,
		isIframeAutoHeight:false,
		scrolling:"auto"
	});
}

function memo(id){
	if(!vip.user.checkLogin('(function(){memo('+id+')})()')){
		return;
	}
	parent.JuaBox.frame(vip.vipDomain+"/manage/account/download/memo?id="+id, {
        width : 580,
    });
}

function addAddr(id){
	if(!vip.user.checkLogin('(function(){addAddr()})()')){
		return;
	}
	parent.JuaBox.frame(vip.vipDomain+"/manage/account/download/add/${coint.stag}", {
        width : 580,
    });
}

function sendAuthEmail(id) {
	$.ajax({
		async : true,
		cache : false,
		type : "POST",
		dataType : "json",
		data : {
			id: id,
			currency: '${coint.stag}'
		},
		url : vip.vipDomain+"/manage/account/download/sendAddrAuthEmail",
		error : function(json) {
		},
		success : function(json) {
			JuaBox.sure(json.des);
		}
	});
}

</script>
</head>

<body style="background-color:#fff;">
<div class="" id="bankBox">
<a class="btn btn-primary btn-sm pull-left" href="javascript:addAddr()">${L:l1(lan,'添加%%地址',coint.tag)}</a>
  <table id="ListTable" class="table table-striped table-bordered table-hover">
    <thead>
    <tr>
      <th>${L:l(lan,'编号')}</th>
      <th>${L:l(lan,'认证')}</th>
      <th>${L:l(lan,'地址')}</th>
      <th style="text-align:center;">${L:l(lan,'备注')}</th>
      <th width="20%" style="text-align:center;">${L:l(lan,'操作')}</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
      <c:when test="${fn:length(myBanks) > 0}">
        <c:forEach items="${myBanks}" var="list" varStatus="statu">
            <tr class="item_list" id="line_${list.id}">
              <td> ${list.id } </td>
              <td> ${ L:l(lan,list.auth==1?'是':'否') } </td>
								<td style="text-align:left;"
									id="text_${list.id}"><a
									href="javascript:send('${list.id}')">${list.address}</a>
								</td>
								<td style="text-align:left;">${list.memo}</td>
								<td>
									<a href="javascript:send('${list.id}')">${L:l(lan,'发送')}</a>
									<a href="javascript:delAddr('${list.id}')">${L:l(lan,'删除')}</a>
									<c:if test="${list.auth==0}">
									<a href="javascript:sendAuthEmail('${list.id}')">${L:l(lan,'认证')}</a>
									</c:if>
								</td>
							</tr>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <tr>
          <td colspan="9"><div class="bk-norecord">
              <p>${L:l(lan,'没有符合要求的记录！')}</p>
            </div></td>
        </tr>
      </c:otherwise>
    </c:choose>
    </tbody>
  </table>
  <div class="page_nav" id="pagin">
    <div class="con"> ${L:l1(lan,'共%%项',fn:length(myBanks))} </div>
  </div>
</div>
</body>
</html>
