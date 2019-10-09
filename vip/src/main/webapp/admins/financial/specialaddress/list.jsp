<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>资金监控</title>
    <jsp:include page="/admins/top.jsp" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

    <script type="text/javascript">
        $(function(){
            vip.list.ui();
            vip.list.basePath = "/admin/financial/specialaddress/";

        });

        function reload2() {
            Close();
            vip.list.reload();
        }
        function aoru(attrId){
            Iframe({
                        Url:"/admin/financial/specialaddress/aoru?attrId=" + attrId,//下载
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

        function sendAddress(){
            var url = "/admin/financial/specialaddress/sendAddress";
            vip.ajax({
                url : url,
                dataType : "json",
                suc : function(json) {
                    parent.Right(json.des, {
                    });
                },
                err : function(json) {
                    Wrong(json.des);
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
                <p class="formCloumn">
                    <span>类型：</span>
                    <input name="attrTypeDesc" id="attrTypeDesc"/>
                </p>

                <p class="formCloumn">
                    <span class="formText">
                        字典值：
                    </span>
                    <span class="formContainer">
                        <input type="text"  name="paraValue" id="paraValue" size="20"/></span>
                    </span>
                </p>
                <p>
                    <a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
                    <a class="search-submit" href="javascript:vip.list.resetForm();" id="idReset">重置</a>
                    <a id="idAdd" style="width: 68px" class="search-submit" href="javascript:vip.list.aoru({id : 0, width : 400 , height : 550});">添加</a>
                    <a class="search-submit" id="idSearch" href="javascript:sendAddress()">推送地址</a>
                </p>
            </form>
        </div>

        <div class="tab-body" id="shopslist">
            <jsp:include page="ajax.jsp" />
        </div>

    </div>
</div>
</body>
</html>

