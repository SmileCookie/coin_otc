<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <jsp:include page="/admins/top.jsp" />

    <script type="text/javascript">
        $(function() {
            $("#add_or_update").Ui();
        });

    function ok() {
            var actionUrl = "/admin/btc/downloadexception/doaoru";
            vip.ajax( {
                formId : "add_or_update",
                url : actionUrl,
                div : "add_or_update",
                suc : function(xml) {
                    parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
                }
            });
        }

    </script>
</head>
<body>
<div id="add_or_update" class="main-bd">
    <div class="form-line">
        <div class="form-tit">编号：</div>
        <div class="form-con">
            <input type="text" id="id" name ="id" value="${download.id}" />
            <input type="hidden" id="conit" name ="conit" value="${conit}" />
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">唯一标志：</div>
        <div class="form-con">
            <input type="text" id="uuid" name ="uuid" value="${download.uuid}" style="width:260px;" />
        </div>
    </div>
    <div class="form-line">
        <div class="form-tit">批次：</div>
        <div class="form-con">
            <input type="text" id="txId" name ="txId"/>
        </div>
    </div>
    <div class="form-line">
        <div class="form-tit">批次编号：</div>
        <div class="form-con">
            <input type="text" id="txIdN" width="200px" name ="txIdN"/>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">区块高度：</div>
        <div class="form-con">
            <input type="text" id="blockHeight" name ="blockHeight"/>
        </div>
    </div>

    <div class="form-line" id="periodDiv">
        <div class="form-tit">真实手续费：</div>
        <div class="form-con">
            <input type="text" id="realFee" name ="realFee"/>
        </div>
    </div>
    <div class="form-btn" style="padding: 15px 0 0 80px;">
        <a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
        <a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
    </div>

</div>
</body>
</html>
