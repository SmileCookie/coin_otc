<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>活动管理</title>
    <jsp:include page="/admins/top.jsp" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
    <style type="text/css">
    </style>
</head>
<body >
<div class="mains">
    <div class="col-main">
        <div class="form-search">
            <form autocomplete="off" name="searchForm" id="searchContaint">
                <div id="formSearchContainer">

                    <p class="formCloumn">
						<span >
							创建人：${eventInfo.createUserName}
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建时间：<fmt:formatDate value="${eventInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 活动状态：${eventInfo.statusView}
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;开始时间：<fmt:formatDate value="${eventInfo.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;结束时间：<fmt:formatDate value="${eventInfo.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
						</span>
                    </p>
                    <div style="clear: both;"></div>
                    <p class="formCloumn">
						<span >
                            已参加人数：${factUser}人
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;已中出：${occurAmount}ABCDEF
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;实际中出：${factAmount}ABCDEF
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;翻倍：${doubleAmount}ABCDEF
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 刷出：${brush}ABCDEF
							刷量：
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="brushAmount" name="brushAmount" style=" width:80px;" size="20" type="text" />
                            <input id="luckyId" name="luckyId" style=" width:80px;" size="20" type="hidden" value="${luckyRule.luckyId}"/>
						</span>
                        <a class="search-submit" id="brush" href="javascript:brush(${luckyRule.ruleId});">确定</a>
                    </p>
                    <div style="clear: both;"></div>
                    <p class="formCloumn">
						<span >
                            奖项规则：
                            &nbsp;&nbsp;&nbsp;${startSize}&nbsp;-&nbsp;${endSize}&nbsp;&nbsp;ABCDEF
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;已中出：${occurCount}个
						</span>
                    </p>
                    <div style="clear: both;"></div>
                    <p class="formCloumn">
						<span >
							用户编号：
                            <input id="userName" name="userName" style=" width:80px;" size="20" type="text"/>
                            <input id="eventId" name="eventId"  value="${eventInfo.eventId}" type="hidden"/>
						</span>
                    </p>
                    <p>
                        <a class="search-submit" id="idSearch" href="javascript:searchRult();">查找</a>
                        <a id="idReset" class="search-submit" href="javascript:searchRult('2');">重置</a>
                    </p>
                </div>
            </form>

        </div>

        <div class="tab-body" id="shopslist">
            <jsp:include page="detailInfo.jsp" />
        </div>
    </div>
</div>

<script type="text/javascript">
    $(function(){
        vip.list.ui();
        vip.list.funcName = "抽奖管理";
        vip.list.basePath = "/admin/lucky/";
    });

    function brush(ruleId){
        var brushAmount =$("#brushAmount").val();
        var luckyId =$("#luckyId").val();
        var title = "确定进行刷量吗";
        Ask2({Title:title,call:function(){
            vip.ajax({url : "/admin/lucky/brush?luckyIds="+luckyId+"&brushAmount="+brushAmount+"&ruleId="+ruleId, suc : function(xml){
                Right($(xml).find("Des").text());
                window.location.reload();
            }});
        }});
    };

    function searchRult(status){
        var userName =$("#userName").val();
        var eventId =$("#eventId").val();
        if("2"==status){
            userName = "";
        }
        location.href = "${main_domain}/admin/lucky/getResultInfo?eventId="+eventId+"&userId="+userName

    };
    function reload2(){
        Close();
        vip.list.reload();

    }

    function showDetail(luckyIdss,userId) {
        Iframe({
            Url : '/admin/lucky/showDetail?luckyIdss='+luckyIdss+"&userId="+userId,
            Width : 600,
            Height : 500,
            isShowIframeTitle: true,
            Title:"用户领取明细"
        });
    }
</script>
</body>
</html>
