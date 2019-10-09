<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.world.util.qiniu.QiNiuUtil" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String uptoken = QiNiuUtil.getUpToken();
    String domain1 = QiNiuUtil.getHost();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>发布新闻</title>
    <jsp:include page="/admins/top.jsp"/>
    <link href="${static_domain }/statics/css/en/upload.css" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>
    <script type="text/javascript"
            src="${static_domain }/statics/js/admin/wangeditor/js/lib/jquery-1.10.2.min.js"></script>
    <!--不能删除,为了引进另一个版本的Jquery再次引入-->
    <script type="text/javascript" src="${static_domain }/statics/js/admin/wangeditor/global.js"></script>

    <style type="text/css">
        .header {
            border-bottom: 1px #e2e2e2 solid;
        }

        .Toolbar {
            width: 800px;
        }

        .ClassifySelect {
            background: none;
        }

        .Login-tips {
            float: right;
            font-family: 微软雅黑;
            margin-top: 5px;
        }

        .Login-tips span {
            color: #666666;
            float: left;
            line-height: 32px;
            margin-right: 10px;
        }

        .Login-tips a {
            float: left;
            background: url(${static_domain }/statics/img/dl.png) no-repeat;
            width: 79px;
            height: 32px;
            line-height: 32px;
            text-align: center;
            color: #fff;
            text-decoration: none;
            font-size: 14px;
        }

        .Register-fill {
            border-top: 1px #fff solid;
            font-family: 微软雅黑;
        }

        .Register-con {
            margin: 0 auto;
            padding-top: 25px;
        }

        .form-line {
            overflow: hidden;
            zoom: 1;
            padding-bottom: 10px;
        }

        .form-tit {
            float: left;
            line-height: 40px;
            color: #666666;
            margin-right: 3px;
            width: 66px;
            text-align: right;
        }

        .form-con {
            float: left;
            line-height: 40px;
        }

        .form-tips {
            float: left;
            line-height: 40px;
            color: #999999;
            padding-left: 10px;
        }

        .form-con .txt {
            height: 40px;
            background: #fff;
            border: 1px #dbdbdb solid;
            border-left: 1px #cccccc solid;
            border-top: 1px #cccccc solid;
            width: 240px;
            padding: 0 5px;
        }

        .rules {
            padding: 5px 0 15px 214px;
            color: #999999;
        }

        .rules a {
            color: #999999;
        }

        .rules a:hover {
            color: #333333;
        }

        .rules label.checkbox {
            margin: 2px 3px 0 0;
        }

        .submit {
            height: 38px;
            padding-left: 214px;
        }

        .submit a {
            background: url(${static_domain }/statics/img/zc.png) no-repeat;
            width: 242px;
            height: 38px;
            color: #fff;
            text-align: center;
            line-height: 38px;
            font-size: 14px;
            text-decoration: none;
            display: block;
        }

        .color {
            color: red;
            font-wei
        }

        .form-con .jqTransformRadioWrapper {
            margin: 5px 0 5px 5px;
        }

        .main-bd {
            padding: 20px;
        }

        .pm-itemcont .item .preview a {
            font-size: 14px;
        }
    </style>

    <script type="text/javascript">
        document.domain = "${baseDomain}";
        $(function () {
            var languageCode = $("#languageCode").val();
            $($("#language")[0]).find("option[value='"+languageCode+"']").attr("selected","selected");

            $("#add_or_update").Ui();

            var loginCode = $.cookie("LoginCode");
            if (loginCode == "1") {
                $("#codeContainer").show();
                getCode();
            }

            optionChange();
        });

        function ok() {
            var type = $("#type").val();

            var actionUrl = "${main_domain}/admin/msg/doaoru";
//            var content = $("#editor-trigger").clone().html();
            $("#newContent").val($("#editor-trigger").clone().html());
            vip.ajax({
                formId: "add_or_update",
                url: actionUrl,
                div: "add_or_update",
                suc: function (xml) {
                    Right($(xml).find("MainData").text(), {
                        call: function () {
                            debugger;
                            if(type == 1){
                                location.href = "${main_domain}/msg";
                            }else{
                                location.href = "${main_domain}/msg/newslist";
                            }

                        }
                    });
                }
            });
        }

        function getCode() {
            var id = numberID();
            $("#idCode").attr("src", "/imagecode/get-28-85-39-" + id);
        }

        //类型改变事件️
        function optionChange() {
            var type = $("#type").val();
            if (type == 2) {//新闻
                $("div[id^='rt']").css("width", "78px");
                $("div[id^='rt']").css("height", "60px");
                $("#sourceDiv").show();
                $("#jg").show();
            } else {
                $("#sourceDiv").hide();
                $("#jg").hide();
            }
        }

        $.fn.Ui = function(){
            var arr = [];
            if($(this)[0]){
                arr=$(this)[0].getElementsByTagName("*");
            }
            for(var i=0;i < arr.length;i++)
            {
                if(arr[i].tagName!=undefined)
                {
                    var tagName=arr[i].tagName.toLowerCase();

                    var controlId=arr[i].getAttribute("id");
                    if(tagName=="input" ||tagName=="textarea")
                    {//处理文本框
                        if(arr[i].type.toLowerCase()=="text"||arr[i].type.toLowerCase()=="hidden"|| arr[i].type.toLowerCase()=="password"||arr[i].tagName.toLowerCase()=="textarea")
                        {
                            $(arr[i]).UiText();
                        }

                        else if(arr[i].type.toLowerCase()=="radio")
                        {
                            $(arr[i]).UiRadio();
                        }
                        else if(arr[i].type.toLowerCase()=="checkbox")
                        {
                            UICheckbox(arr[i]);
                        }
                        else if(arr[i].type.toLowerCase()=="button")
                        {
                            $(arr[i]).UIButton();
                        }
                    }
                    else if(tagName=="select")
                    {
                        $(arr[i]).UiSelect();
                    }

                }
            }
        }


    </script>

</head>

<body>

<input type="hidden" value="${baseDomain}" id="curBaseDomain"/>

<div id="add_or_update" class="main-bd">

    <div class="form-line">
        <div class="form-tit">新闻：</div>
        <div class="form-con">
            <select id="type" name="type" pattern="true" errormsg="要求必须选择类型" onchange="optionChange()">
                <option value="">请选择</option>
                <c:forEach var="nt" items="${nts }">
                    <option value="${nt.key }"
                            <c:if test="${n.type==nt.key }">selected="selected"</c:if> >${nt.value }</option>
                </c:forEach>
            </select>
            <input type="hidden" id="languageCode" value=${n.language}>
        </div>
        <div class="form-tit">语言：</div>
        <div class="form-con">
            <select id="language" name="language">
                <option value="cn" >简体中文</option>
                <option value="hk" >繁體中文</option>
                <option value="en" >ENGLISH</option>
            </select>
        </div>

        <div id="sourceDiv" style="display: none">
            <div class="form-tit">来源：</div>
            <div class="form-con">
                <input class="txt" type="text" id="source" name="source" style="width:80px;height:25px" mytitle="请填写来源。"
                       pattern="limit(0, 50)" value="${n.source }"/>
            </div>
            <div class="form-tit">来源链接：</div>
            <div class="form-con">
                <input class="txt" type="text" id="sourceLink" name="sourceLink" style="width:130px;height:25px"
                       mytitle="请填写来源链接。" pattern="limit(0, 100)" value="${n.sourceLink }"/>
            </div>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">标题：</div>
        <div class="form-con"><input class="txt" type="text" style="width:400px;" name="title" id="title"
                                     mytitle="请填写新闻资讯标题。" pattern="limit(4, 500)" errmsg="标题过短过着过长。"
                                     value="${n.title }"/></div>
        <div class="form-tips">请输入有效的标题</div>
    </div>
    <div class="form-line">
    <div class="form-tit">公告类型：</div>
    <div class="form-con">
        <select id="noticeType" name="noticeType">
            <option value="">请选择</option>
            <c:forEach var="nt" items="${noticeType }">
                <option value="${nt.key }"
                        <c:if test="${n.noticeType==nt.key }">selected="selected"</c:if> >${nt.value }</option>
            </c:forEach>
        </select>
    </div>
    </div>
    <div class="form-line">
        <div class="form-tit">是否置顶：</div>
        <div class="form-con">
            <input type="radio" name="isTop" <c:if test="${!n.top}"> checked="checked" </c:if> value="false"/><span
                style="float:left;">否</span> &nbsp;
            <input type="radio" name="isTop" <c:if test="${n.top}"> checked="checked" </c:if> value="true"/><span
                style="float:left;">是</span>
        </div>
        <div class="form-tips">是否置于新闻的顶部</div>
    </div>


    <div class="form-line">
        <div class="form-tit">关键字：</div>
        <div class="form-con">

            <input class="txt" type="text" style="width:500px;" name="keyword" id="keyword" mytitle="请填写关键字,用引文逗号隔开。"
                   pattern="limit(0, 500)" errmsg="请正确填写关键字。" value="${n.keyword}"/>

        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">摘要：</div>
        <div class="form-con">
            <textarea rows="10" cols="120" style="width: 780px;height: 200px;" type="text" name="digest"
                      id="digest">${n.digest }</textarea>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">正文：</div>
        <div class="form-con">
            <jsp:include page="editor.jsp"/>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">发布时间：</div>
        <div class="form-con">
            <input class="txt Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',lang : '${lan}'})" type="text"
                                     name="pubTime" id="pubTime" mytitle="请选择新闻的发布时间"
                                     value="<fmt:formatDate value='${n.pubTime }' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
        </div>
    </div>

    <div class="form-line" id="jg" style="float: none;display: none;">
        <div class="form-tit">封面图片：</div>
        <div class="form-con" style="float: none;">
            <%--<div style="width: 60%;" class="pm-itemcont" id="photoA" errorName="封面图片"></div>--%>
            <img width="200" height="200" id="photoA" src="${n.photo}"/>
            <input type="hidden" id="photo" name="photo" value="${n.photo}"/>
            <jsp:include page="uploadImg.jsp"/>

        </div>
    </div>

    <div class="form-line" id="codeContainer" style="display:none;">
        <div class="form-tit" style="letter-spacing:6px;">验证码</div>
        <div class="form-con"><input class="txt" type="text" name="code" id="code" style="width:100px;"/></div>
        <p class="form-code"><img alt="请在左边输入图片上的4个数字或英文字母" src="/imagecode/get-28-85-39" id="idCode"/><a
                href="javascript:getCode()">刷新验证码</a></p>
</div>
    <input type="hidden" name="id" value="${n.id}"/>
    <input type="hidden" id="newContent" name="newContent"/>
    <input type="hidden" id="domain1" value='<%=domain1%>'/>
    <input type="hidden" id="uptoken" value='<%=uptoken%>'/>
    <div class="submit" style="padding-bottom: 20px;"><a id="doLoginSimple" href="javascript:ok();">发布新闻</a></div>
</div>
</body>
</html>
