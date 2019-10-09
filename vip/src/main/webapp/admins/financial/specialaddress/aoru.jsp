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
            var actionUrl = "/admin/financial/specialaddress/doaoru";
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
        <div class="form-tit" style="width:100px;">类型：</div>
        <div class="form-con">
            <input type="hidden" id="id" name="id" value="${commAttrBean.attrId}">
            <input type="text" id="attrType" name="attrType" value="${commAttrBean.attrType}">
        </div>
    </div>
    <div class="form-line">
        <div class="form-tit" style="width:100px;">类型描述：</div>
        <div class="form-con">
            <input type="text" id="attrTypeDesc" name="attrTypeDesc" value="${commAttrBean.attrTypeDesc}">
        </div>
    </div>
    <div class="form-line">
        <div class="form-tit" style="width:100px;">详细类型：</div>
        <div class="form-con">
            <input type="text" id="paraCode" name="paraCode" value="${commAttrBean.paraCode}" style="width:135px;">
        </div>
    </div>
    <div class="form-line">
        <div class="form-tit" style="width:100px;">详细类型描述：</div>
        <div class="form-con">
            <input type="text" id="paraName" name="paraName" value="${commAttrBean.paraName}" style="width:135px;">
        </div>
    </div>
    <div class="form-line">
        <div class="form-line">
            <div class="form-tit" style="width:100px;">字典值：</div>
            <div class="form-con">
                <input type="text" id="paraValue" name="paraValue" value="${commAttrBean.paraValue}" style="width:135px;">
            </div>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit" style="width:100px;">是否启用：</div>
        <div class="form-con">
            <select name="attrState" id="attrState" style="width:135px;">
                <option value="1" <c:if test="${commAttrBean.attrState==1 }">selected="selected"</c:if>>启用</option>
                <option value="0" <c:if test="${commAttrBean.attrState==0 }">selected="selected"</c:if>>停用</option>
            </select>
        </div>
    </div>

    <div class="form-line">
        <div class="form-line">
            <div class="form-tit" style="width:100px;" >备注：</div>
            <div class="form-con">
                <textarea id="paraDesc" name="paraDesc" style="width:135px;">${commAttrBean.paraDesc}</textarea>
            </div>
        </div>
    </div>

    <div class="form-btn" style="padding: 15px 0 0 80px;">
        <a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
        <a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
    </div>
</div>
</body>
</html>
