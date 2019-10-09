<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>投票记录</title>
    <jsp:include page="/admins/top.jsp" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

    <script type="text/javascript">
        $(function(){
            vip.list.ui();
            vip.list.basePath = "/admin/vote/voteLog/";
            GetRequest();
        });

        function reload2() {
            Close();
            vip.list.reload();
        }
        function GetRequest() {
            var url = location.search;
            var theRequest = new Object();
            if (url.indexOf("?") != -1) {
                var str = url.substr(1);
                var strs= new Array();
                var voteId= new Array();
                strs = str.split('&');
                var activityId=strs[0].substring(11).trim();
                if(strs.length>1){
                     voteId=(strs[1].substring(7));
                }
                $("#activityId").val(activityId);
                $("#voteId").val(voteId);

            }
        }
        function aoru(ucmId){
            Iframe({
                Url:"/admin/capmonitor/aoru?ucmId=" + ucmId,//下载
                zoomSpeedIn		: 200,
                zoomSpeedOut	: 200,
                Width:600,
                Height:500,
                scrolling:"no",
                isIframeAutoHeight:false,
                isShowIframeTitle: true,
                Title:"监控错误详情"
            });
        }
        //处理备注
        dealRemark = function(obj) {
            $(obj).parents("td").prev().find("textarea").attr("disabled",false).focus();
        }

        //保存备注
        saveRemark = function(obj,id) {
            var remark = $(obj).parents("td").prev().find("textarea").val();
            $(obj).parents("td").prev().find("textarea").attr("disabled",true);
            var actionUrl = "/admin/capmonitor/saveRemark?id=" + id+ "&dealreamark=" + remark;
            vip.ajax({
                url : actionUrl,
                dataType : "json",
                suc : function(json) {
                    Right(json.des);
                },
                err : function(json){
                    Right(json.des);
                }
            });
        }

    </script>
    <style type="text/css">
        .commodity_action a{padding: 0 5px;}
    </style>
</head>
<body>
<div class="mains">
    <div class="col-main">
        <div class="form-search" id="listSearch">
            <form autocomplete="off" name="searchForm" id="searchContaint">
                <div id="formSearchContainer">
                    <input type="hidden" name="tab" id="tab" value="${tab}" />
                    <input type="hidden" name="activityId" id="activityId" />
                    <input type="hidden" name="voteId" id="voteId" />
                </div>
            </form>



        </div>

        <div class="tab-body" id="shopslist">
            <jsp:include page="logAjax.jsp" />
        </div>

    </div>
</div>
</body>
</html>

