<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>活动结果</title>
    <jsp:include page="/admins/top.jsp" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

    <style type="text/css">
        .bar_warp{
            width: 500px;
            height: 10px;
            margin-bottom: 40px;
            background: #eeeeee;
            position: relative;
            display: inline-block;
        }
        .bar_warp .bar_child{
            position: absolute;
            left: 0;
            top: 0;
            height: 10px;
            background: #128dfc;
        }
    </style>
</head>
<body >
<div class="mains">
    <div class="col-main">
        <div class="form-search">
            <table class="tb-list2" style="width:100%;table-layout: fixed;">
                <tr class="hd">
                    <h3 style="line-height: 30px; text-align: center;">活动名称：${activity.activityNameJson} </h3>
                </tr>
                <tr class="hd">
                    <td colspan="5">
                        <span>创建人：${activity.createUser} </span>
                        <span>创建时间：<fmt:formatDate value="${activity.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                        <c:if test="${activity.state==1}">
                        <span>活动状态：进行中 </span>
                        </c:if>
                        <c:if test="${activity.state==2}">
                            <span>活动状态：暂停 </span>
                        </c:if>
                        <c:if test="${activity.state==3}">
                            <span>活动状态：已结束 </span>
                        </c:if>
                        <span>投票开始时间：<fmt:formatDate value="${activity.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                        <span>投票截止时间：<fmt:formatDate value="${activity.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                        <a  target="_blank"  href="/admin/vote/voteLog?activityId=${activity.activityId}">${countPeople}人参与</a>
                    </td>
                </tr>
            </table>
            <c:forEach items="${coinList}" var="coin" varStatus="statu">
                <div style="margin-bottom: 10px; height: 60px;overflow: hidden">
                    <div style="margin-bottom: 10px; height: 20px; display: block;overflow: hidden ">
                        <div style="width:100px;height:20px;display: inline-block"> 序号：${statu.index + 1}.</div>
                        <div style="width:100px;height:20px;display: inline-block">${coin.coinNameJson}</div>
                        <div style="width:100px;height:20px;display: inline-block">
                            <a  target="_blank"  href="/admin/vote/voteLog?activityId=${activity.activityId}&voteId=${coin.coinId}">${coin.realCount}票</a></div>
                        <div style="width:100px;height:20px;display: inline-block">刷票:${coin.count}</div>
                    </div>
                    <div style=" height: 40px; width: 1000px;display: block;">
                        <span style="display: inline-block; line-height: 10px;margin-right: 10px;float: left;  ">占比</span>
                        <div  class="bar_warp" style="float: left">
                            <div style="width:${coin.rate}%;" class="bar_child">
                            </div>
                        </div>
                        <div style="float: left;margin-left: 15px;margin-right: 15px;margin-top: -5px">${coin.rate}%</div>
                        <input type="hidden" id="coinId" name="coinId" value="${coin.coinId}">
                        <span style="float: left; line-height: 10px;margin-right: 10px;margin-left: 30px">刷票：</span>
                        <div style="float: left;margin-top: -5px">
                            <input style="margin-top: -5px" type="text" size="5" name="text1" id="brushVotes">
                        </div>
                        <div style="float: left;margin-top: -5px;margin-left: 10px;">
                            <button name="button1" style="padding: 5px 10px;margin-top: -5px" onclick="brush(this,'${activity.activityId}',${coin.coinId},${coin.voteCount})">确认</button>
                        </div>

                    </div>

                </div>

            </c:forEach>
        </div>


<%--
        <div class="tab-body" id="shopslist">
            <jsp:include page="ajax.jsp" />
        </div>--%>
    </div>
</div>

<script type="text/javascript">




    $(function(){
        vip.list.ui();
        vip.list.funcName = "投票结果";
        vip.list.basePath = "/admin/vote/";

    });





    function brush(btn,id,coinId,voteCount){
        var brushVotes=$(btn).parent().parent().find("input[name='text1']").val();
        if(brushVotes<0 && Math.abs(brushVotes)>voteCount){
            Wrong("输入错误");
            return;
        }
        Ask2({Title:"确定执行此操作？",call:function(){
            vip.ajax({url : "/admin/vote/brushVote?activityId="+id+"&brushVotes="+brushVotes+"&coinId="+coinId , suc : function(xml){
                Right($(xml).find("Des").text());
                //vip.list.reload();
                window.location.reload();
            }});
        }});


    };


    function reload2(){
        Close();
        vip.list.reload();

    }
</script>

</body>
</html>
