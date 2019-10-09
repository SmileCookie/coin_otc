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
	<title>新建/编辑活动</title>
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
            var a=$("#activityLimit").val();
            $("#activityLimited").val(a);
            changeTab(1);
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
            var actionUrl;
			if($("#activityId").val()=="" ||$("#activityId").val()==null){
                actionUrl = "${main_domain}/admin/vote/insert";
			}else{
                actionUrl = "${main_domain}/admin/vote/update";
            }
//            var content = $("#editor-trigger").clone().html();
            $("#activityContentSimple").val($("#editor-trigger").clone().html());
            $("#activityRuleSimple").val($("#editor-trigger1").clone().html());
            $("#activityContentTraditional").val($("#editor-trigger2").clone().html());
            $("#activityRuleTraditional").val($("#editor-trigger3").clone().html());
            $("#activityContentEnglish").val($("#editor-trigger4").clone().html());
            $("#activityRuleEnglish").val($("#editor-trigger5").clone().html());

            vip.ajax({
                formId: "add_or_update",
                url: actionUrl,
                div: "add_or_update",
                suc: function (xml) {
                    Right($(xml).find("MainData").text(), {
                        call: function () {
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



        function changeTab(ob){
            if(ob == 1){ //简体
                $("#editor-container1").hide();
                $("#editor-container2").hide();
                $("#editor-container").show();
                $("#tt1").hide();
                $("#tt2").hide();
                $("#tt").show();
            }else if(ob==2){ //繁体
                $("#editor-container").hide();
                $("#editor-container2").hide();
                $("#editor-container1").show()
                $("#tt1").show();
                $("#tt2").hide();
                $("#tt").hide();
			}else if(ob==3){ //英文
                $("#editor-container").hide();
                $("#editor-container1").hide();
                $("#editor-container2").show()
                $("#tt2").show();
                $("#tt1").hide();
                $("#tt").hide();
			}



		}
        function getCode() {
            var id = numberID();
            $("#idCode").attr("src", "/imagecode/get-28-85-39-" + id);
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


<div id="add_or_update" class="main-bd">

	<div class="form-line">
		<div class="form-tit">发布时间：</div>
		<div class="form-con">
			<input class="txt Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',lang : '${lan}'})" type="text"
				   name="startTime" id="startTime" mytitle="请选择活动的开始时间"
				   value="<fmt:formatDate value='${n.startTime }' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
			<input class="txt Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',lang : '${lan}'})" type="text"
				   name="endTime" id="endTime" mytitle="请选择活动的结束时间"
				   value="<fmt:formatDate value='${n.endTime }' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
		</div>

	</div>
	<div class="form-line">
		<div class="form-tit">投票方式：</div>
		<div class="form-con">
			<input type="hidden" id="activityLimit" value="${n.activityLimit}">
			<select id="activityLimited" name="activityLimit">
				<option value="1" >按活动区间</option>
				<option value="2" >按日投票</option>
			</select>
		</div>

		<div class="form-tit">票数限制：</div>
		<div class="form-con">
			<input type="text" value="${n.selectCount}" id="selectCount" name="selectCount"
				   onkeyup="if(this.value.length==1){this.value=this.value.replace(/[^0-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}"
				   onafterpaste="if(this.value.length==1){this.value=this.value.replace(/[^0-9]/g,'0')}else{this.value=this.value.replace(/\D/g,'')}" />
		</div>

		<div class="form-tit">币库：</div>
		<div class="form-con">
			<select id="language" name="language">
				<option value="新币库" >新币库</option>
			</select>
			<a id="idReset"  target="_blank" href="/admin/vote/coin">查看币库内容</a>
		</div>
	</div>

	<div class="form-line">

	</div>
	<div class="form-line">
		<div class="form-con">
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:changeTab(1)" id="cn"><span>简体</span></a>
			<a href="javascript:changeTab(2)"><span>繁体</span></a>
			<a href="javascript:changeTab(3);"><span>英文</span></a>
		</div>

	</div>

	<div class="form-line">

	</div>


	<div class="form-line" id="tt">
		<div class="form-tit">活动标题：</div>
		<div class="form-con"><input class="txt" type="text" style="width:400px;" name="activityNameSimple" id="activityNameSimple"
									 mytitle="请填写活动标题。" pattern="limit(4, 500)" errmsg="标题过短过着过长。"
									 value="${n.activityNameSimple }"/></div>
		<div class="form-tips">请输入有效的标题</div>

	</div>
	<div class="form-line" id="tt1">
		<div class="form-tit">活动标题：</div>
		<div class="form-con"><input class="txt" type="text" style="width:400px;" name="activityNameTraditional" id="activityNameTraditional"
									 mytitle="请填写活动标题。" pattern="limit(4, 500)" errmsg="标题过短过着过长。"
									 value="${n.activityNameTraditional }"/></div>
		<div class="form-tips">请输入有效的标题</div>

	</div>
	<div class="form-line" id="tt2">
		<div class="form-tit">活动标题：</div>
		<div class="form-con"><input class="txt" type="text" style="width:400px;" name="activityNameEnglish" id="activityNameEnglish"
									 mytitle="请填写活动标题。" pattern="limit(4, 500)" errmsg="标题过短过着过长。"
									 value="${n.activityNameEnglish }"/></div>
		<div class="form-tips">请输入有效的标题</div>

	</div>

	<div class="form-line">
		<div class="form-con">
			<jsp:include page="editor.jsp"/>
		</div>
	</div>








	<input id="activityId" type="hidden" name="activityId" value="${n.activityId}"/>
	<input type="hidden" id="activityContentSimple" name="activityContentSimple"/>
	<input type="hidden" id="activityRuleSimple" name="activityRuleSimple"/>
	<input type="hidden" id="activityContentTraditional" name="activityContentTraditional"/>
	<input type="hidden" id="activityRuleTraditional" name="activityRuleTraditional"/>
	<input type="hidden" id="activityContentEnglish" name="activityContentEnglish"/>
	<input type="hidden" id="activityRuleEnglish" name="activityRuleEnglish"/>

	<div class="submit" style="padding-bottom: 20px;"><a id="doLoginSimple" href="javascript:ok();">发布活动</a></div>
</div>
</body>
</html>
