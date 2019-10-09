<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
    <jsp:include page="/admins/top.jsp" />
    <style type="text/css">

        .form-tit {
            float:left;
            line-height:32px;
            width:100px;
            padding-right: 10px;
            text-align: right;
        }

        .form-con{float:left;}

        .form-line {margin-left:20px;}
        .col-main .prompt.b_yellow.remind2.pl_35 {
            padding:0 10px 20px 10px;
        }
        .form-btn {
            padding: 15px 0 0 100px;
        }
        .select_wrap select {
            color: #6D6D6D;
            float: left;

            padding: 5px;
        }

        .bankbox{ padding:15px;}
        .bankbox .bd {
            padding-right: 20px;
            padding-left: 20px;
        }
        span.txt{float:left;margin-right: 5px;}
    </style>

    <script type="text/javascript">
        $(function(){
            $("#bankBox").Ui();
        });

        function ok() {
            var actionUrl = "/admin/jobdatedeal/doAoru";
            vip.ajax( {
                formId : "bankBox",
                url : actionUrl,
                div : "bankBox",
                suc : function(xml) {
                    parent.Right($(xml).find("MainData").text(), {
                        callback : "reload2()"
                    });
                }
            });
        }
    </script>
</head>

<body >
<div class="bankbox" id="bankBox">
    <div class="bankbox_bd">
        <div class="form-line">
            <div class="form-tit">作业名称：</div>
            <div class="form-con">
                ${job.jobName }
                <%--<input id="jobName" name="jobName"  size="25" type="text" value="${job.jobName }" readonly="true"/>--%>
            </div>
        </div>
        <div class="form-line">
            <div class="form-tit">开始执行时间：</div>
            <div class="form-con">
                <input class="text" type="text" value="${job.jobStartTime}"pattern="true"  name="jobStartTime" onFocus="WdatePicker({dateFmt:'HH:mm:ss'})" id="jobStartTime" errormsg="请选择开始时间"/>
            </div>
        </div>
        <div class="form-line">
            <div class="form-tit">结束执行时间：</div>
            <div class="form-con">
                <input class="text" type="text" value="${job.jobEndTime}"pattern="true"  name="jobEndTime" onFocus="WdatePicker({dateFmt:'HH:mm:ss'})" id="jobEndTime" errormsg="请选择结束时间"/>
            </div>
        </div>
        <div class="form-line">
            <div class="form-tit">执行类：</div>
            <div class="form-con">
                ${job.jobClass }
                <%--<input id="jobClass" name ="jobClass" size="25" class="input" type="text" value="${job.jobClass }"/>--%>
            </div>
        </div>
        <div class="form-line">
            <div class="form-tit">时间间隔秒：</div>
            <div class="form-con">
                ${job.jobInterval }
                <%--<input id="jobInterval" name="jobInterval"  size="25" class="input" type="text" value="${job.jobInterval }"/>--%>
            </div>
        </div>
        <div class="form-line">
            <div class="form-tit">作业状态：</div>
            <div class="form-con">
                <select name="jobStatus" id="jobStatus" style="width:140px;">
                    <option value="1" <c:if test="${job.jobStatus==1 }">selected="selected"</c:if>>启用</option>
                    <option value="0" <c:if test="${job.jobStatus==0 }">selected="selected"</c:if>>停用</option>
                </select>
            </div>
        </div>
        <div class="form-line">
            <div class="form-tit">备注：</div>
            <div class="form-con">
                <input id="remark" name="remark" size="25" class="input" type="textarea" value="${job.remark }"/>
            </div>
        </div>

        <div class="form-btn">
            <input type="hidden" name="id" value="${job.id }"/>
            <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
            <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
        </div>
    </div>
</div>

</body>
</html>


