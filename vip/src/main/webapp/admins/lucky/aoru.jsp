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
		.form-tit2 {
			float: left;
			line-height: 40px;
			color: #666666;
			margin-right: 3px;
			width: 120px;
			text-align: right;
			font-family: 微软雅黑;
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
            if(${eventInfo.eventId != ""}){
				if(${luckyEvent.cycleLimitType=="03"}){
                    $("#cycleLimitCount03").attr("checked","checked");
                }else if(${luckyEvent.cycleLimitType=="04"}){
                    $("#cycleLimitCount03").attr("checked","checked");
                    if(${luckyEvent.isDouble=="02"}){
                        $("#isDoubleCheck").attr("checked","checked");
					}
                    if(${luckyEvent.isHighest=="02"}){
                        $("#isHighestCheck").attr("checked","checked");
                    }
                }
			}
            changeTab(1);
            changeTab2(1);
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
            if($("#eventId").val()=="" ||$("#eventId").val()==null){
                actionUrl = "${main_domain}/admin/lucky/insert";
            }else{
                actionUrl = "${main_domain}/admin/lucky/update";
            }
            $("#eventContentCN").val($("#editor-trigger").clone().html());
            $("#eventRuleCN").val($("#editor-trigger1").clone().html());
            $("#eventContentHK").val($("#editor-trigger2").clone().html());
            $("#eventRuleHK").val($("#editor-trigger3").clone().html());
            $("#eventContentEN").val($("#editor-trigger4").clone().html());
            $("#eventRuleEN").val($("#editor-trigger5").clone().html());
            $("#isHighest").val("01");
            if(document.getElementById("isHighestCheck").checked){
                $("#isHighest").val("02");
			}
            if(document.getElementById("isDoubleCheck").checked){
                $("#isDouble").val("02");
            }

            vip.ajax({
                formId: "add_or_update",
                url: actionUrl,
                div: "add_or_update",
                suc: function (xml) {
                    Right($(xml).find("MainData").text(), {
                        call: function () {
                        	location.href = "${main_domain}/admin/lucky/getView?eventId="+$(xml).find("MainData").text();
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

        function changeTab2(ob){
            if(ob == 1){ //简体
                $("#editor-container_01").show();
                $("#editor-container_02").hide();
            }else if(ob==2){ //繁体
                $("#editor-container_01").hide();
                $("#editor-container_02").show();
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
	<style>
		.luck-form-box{
			width: 100%;
			vertical-align: middle;
		}
		.luck-form-line span{
			display: inline-block;
			vertical-align: middle;
		}
		.luck-form-line span li{
			float: left;
		}
		#isHighestCheck{
			background: none;
		}
		.luck-piaoshu{
			position: relative;
			width: 560px;
			margin-left: 30px;
		}
		.luck-piaoshu span{
			position: absolute;
			left:0px;
		}
		.luck-piaoshu label.checkbox{
			position: absolute;
			left:130px;
			width: 30px;
			top:8px;
		}
	</style>

</head>

<body>


<div id="add_or_update" class="main-bd">

	<div class="form-line">
		<div class="form-tit">活动时间：</div>
		<div class="form-con">
			<input class="txt Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',lang : '${lan}'})" type="text"
				   name="startTime" id="startTime" mytitle="请选择活动的开始时间"
				   value="<fmt:formatDate value='${eventInfo.startTime }' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
			<input class="txt Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',lang : '${lan}'})" type="text"
				   name="endTime" id="endTime" mytitle="请选择活动的结束时间"
				   value="<fmt:formatDate value='${eventInfo.endTime }' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
		</div>
	</div>
	<hr style="height:0px;border:none;border-top:1px dotted #185598;"/>
	<div class="form-line luck-form-line">

		<div class="luck-form-box">
			<span>抽奖限制：</span>
			<span style="margin-right: 30px;"><input name="cycleLimitType"  type="radio" value="01" >每天：<input  type="text" value="${luckyEvent.cycleLimitCount}" id="cycleLimitCount01" name="cycleLimitCount" style="width:40px;" onkeyup="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}" onafterpaste="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}" /> 次(暂不可用)</span>
			<span style="margin-right: 30px"><input name="cycleLimitType" type="radio" value="02" >活动期间只抽<input type="text" value="${luckyEvent.cycleLimitCount}" id="cycleLimitCount02" name="cycleLimitCount" style="width:40px;"  onkeyup="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}" onafterpaste="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}" /> 次(暂不可用)</span>
			<span>
				<ul>
					<li><input name="cycleLimitType" type="radio" value="03" id="cycleLimitCount03" style="display: inline-block;">关联其他活动：</li>
					<li>
						<select id="relateEventId" name="relateEventId" style="display: inline-block;">
							<option value="">-请选择-</option>
							<c:forEach items="${activityLinkMap}" var="entry">
								<option <c:if test="${entry.key == luckyEvent.relateEventId}">selected="checked"</c:if> value=${entry.key}>${entry.value}</option>
							</c:forEach>
						</select>
					</li>
					<li class="luck-piaoshu">
						<span>再赋予一次抽奖机会：</span><input name="isHighestCheck" type="checkbox" value="" id="isHighestCheck" style="display: none;margin-top:10px;"/>
					</li>
				</ul>

			</span>
			<span >
				<span>奖金翻倍：</span><input name="isDoubleCheck" type="checkbox" value="" id="isDoubleCheck" style="display: none;margin-top:10px;"/>
			</span>
			<div class="shangxian">
				<span>每位用户抽奖上限：</span>
				<span>
					<input type="text" value="${luckyEvent.limitCount}" id="limitCount" name="limitCount" style="width:40px;" onkeyup="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}" onafterpaste="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}"  />次
				</span>
			</div>

		</div>

	</div>

	<hr style="height:0px;border:none;border-top:1px dotted #185598;"/>
	<div class="form-line">
		<div class="form-tit">奖金规则：</div>
		<label>
			<input style="float:left;" name="ruleType" onclick="changeTab2(1)" type="radio" value="01" checked="checked"><span style="float:left;" >设置上限</span></input>
			<input style="float:left;" name="ruleType" onclick="changeTab2(2)" type="radio" value="02" ><span style="float:left;">组合规则(暂不可用)</span></input>
		</label>
		<div id="editor-container_01" class="container">
			<div id="simple01">
				<span style="float:left;">奖金总额度</span> &nbsp;
				<input type="text" value="<fmt:formatNumber value="${luckyRule.jackpotSize}" pattern="0.######"/>" id="jackpotSize" name="jackpotSize" style="width:100px;float:left" onkeyup="value=value.replace(/[^\d.]/g,'')"   />
				<span style="float:left;">ABCDEF，每次抽奖奖金范围：</span>
				<input type="text" value="<fmt:formatNumber value="${luckyRule.startSize}" pattern="0.######"/>"  id="startSize" name="startSize" style="width:100px;float:left" onkeyup="value=value.replace(/[^\d.]/g,'')"/>
				<span style="float:left;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
				<input type="text" value="<fmt:formatNumber value="${luckyRule.endSize}" pattern="0.######"/>"  id="endSize" name="endSize" style="width:100px;float:left" onkeyup="value=value.replace(/[^\d.]/g,'')"/>
				<span style="float:left;">允许几位小数：</span>
				<input type="text" value="<fmt:formatNumber value="${luckyRule.radixPoint}" pattern="0.######"/>" onkeyup="value=value.replace(/[^\d]/g,'')" id="radixPoint" name="radixPoint" style="width:100px;float:left"/>
				<span style="float:left;">0为不可生成小数</span>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
		</div>
		<div id="editor-container_02" class="container">
			<div id="simple02">
				<div >组合规则：</div>
			</div>
		</div>
	</div>
	<hr style="height:0px;border:none;border-top:1px dotted #185598;"/>
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
		<div class="form-con">
			<input class="txt" type="text" style="width:400px;" name="eventTitleCN" id="eventTitleCN" mytitle="请填写活动标题。"
				   pattern="limit(4, 500)" errmsg="标题过短过着过长。" value="${eventInfo.eventTitleCN }"/></div>
		<div class="form-tips">请输入有效的标题</div>
	</div>
	<div class="form-line" id="tt1">
		<div class="form-tit">活动标题：</div>
		<div class="form-con">
			<input class="txt" type="text" style="width:400px;" name="eventTitleHK" id="eventTitleHK" mytitle="请填写活动标题。"
				   pattern="limit(4, 500)" errmsg="标题过短过着过长。" value="${eventInfo.eventTitleHK }"/></div>
		<div class="form-tips">请输入有效的标题</div>
	</div>
	<div class="form-line" id="tt2">
		<div class="form-tit">活动标题：</div>
		<div class="form-con">
			<input class="txt" type="text" style="width:400px;" name="eventTitleEN" id="eventTitleEN" mytitle="请填写活动标题。"
				   pattern="limit(4, 500)" errmsg="标题过短过着过长。" value="${eventInfo.eventTitleEN }"/></div>
		<div class="form-tips">请输入有效的标题</div>
	</div>

	<div class="form-line">
		<div class="form-con">
			<jsp:include page="editor.jsp"/>
		</div>
	</div>

	<input id="eventId" type="hidden" name="eventId" value="${eventInfo.eventId}"/>

	<input type="hidden" id="eventContentCN" name="eventContentCN"/>
	<input type="hidden" id="isHighest" name="isHighest"/>
	<input type="hidden" id="isDouble" name="isDouble"/>
	<input type="hidden" id="eventRuleCN" name="eventRuleCN"/>
	<input type="hidden" id="eventContentHK" name="eventContentHK"/>
	<input type="hidden" id="eventRuleHK" name="eventRuleHK"/>
	<input type="hidden" id="eventContentEN" name="eventContentEN"/>
	<input type="hidden" id="eventRuleEN" name="eventRuleEN"/>

	<div class="submit" style="padding-bottom: 20px;"><a id="doLoginSimple" href="javascript:ok();">发布活动</a></div>
</div>
</body>
</html>
