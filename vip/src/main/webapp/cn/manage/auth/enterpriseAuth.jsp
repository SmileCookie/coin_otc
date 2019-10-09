<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="content" id="mainForm">
	<h2>${L:l(lan, '企业用户实名认证设置')}</h2>
  	 	 	   	 

        <div class="authbody">
            <div class="">
                <c:if test="${auth.status==6}">
                    <div class="vip-tip">
                        <dl>
                        	<dt>温馨提示：</dt>
                          <dd>${L:l(lan,'您已经通过初级实名认证，公司名称')}：<b class="text-third">${auth.realName}</b>${L:l(lan, "，实名认证后不能更改。您还可以进行")}
                        <a class="btn btn-primary btn-sm" href="/manage/auth/depthEnterprise" style="margin-left:15px;"><i class="fa fa-cog "></i>&nbsp; ${L:l(lan, "高级实名认证")}</a></dd>
                        </dl>
                    </div>

                </c:if>
                <c:if test="${auth.status==7}">

                    <div class="vip-tip">
                        <dl>
                        	<dt>温馨提示：</dt>
                          <dd>${L:l(lan,'您提交的实名认证请求未通过审核，原因是')}：<b class="text-third">${auth.reason}</b>。</dd>
                        </dl>
                    </div>

                </c:if>
                <c:if test="${auth.status==5}">

                    <div class="vip-tip">
                        <dl>
                        	<dt>温馨提示：</dt>
                          <dd>${L:l(lan,'您提交的实名认证请求正在审核中，请耐心等待。')}</dd>
                        </dl>
                    </div>

                </c:if>
                <c:if test="${empty auth || auth.status==0 || auth.status==4 || auth.status==7}">
                    <div class="vip-tip">
                        <dl>
                          <dd>1、${L:l(lan,'根据国家反洗钱法，所有在本站交易的用户均需要实名认证，请提供真实有效的手持本人身份证照片，虚假认证可能会导致账户被冻结，由虚假认证产生的一切后果由用户负责！')}</dd>
                          <dd>2、${L:l(lan,'如需加急认证，请直接致电xxx-xxx-xxxx。')}</dd>
                        </dl>
                    </div>

                    <form id="frmEnterprise">
                    <div id="subForm" class="bk-onekey-form ">

                        <jsp:include page="enterpriseAuthCommon.jsp" flush="true">
                            <jsp:param name="canEdit" value="true"/>
                        </jsp:include>

                        <div class="form-line row">
                            <div class="col-sm-4 textright"></div>
                            <div class="col-sm-5" style="text-align: left;">
                                <label><input type="checkbox" id="agreenMent">&nbsp;${L:l(lan, '我承诺本公司无不记名股票')}</label>
                            </div>
                        </div>


                        <div class="do row">
                        		<div class="col-sm-4 textright"></div>
                            <div class="col-sm-5" style="text-align: left;">
                                <a href="javascript:simpleSave()" class="btn btn-primary btn-block btn-lg ">${L:l(lan,'提交认证信息')}</a>
                            </div>
                        </div>
                    </div>
                    </form>
                </c:if>
            </div>
        </div>
</div>


<script type="text/javascript">
    $(function () {
        //初始化日历控件
        $("input[data-picker=true]").datepicker({format: 'yyyy-mm-dd'});
    });

    function simpleSave() {

        if( !checkBaseForm() ){
            return;
        }

        if($("#agreenMent").is(":checked") == false){
            return JuaBox.sure('${L:l(lan, "请承诺本公司无不记名股票")}');
        }

        $("#subForm").Loadding();
        var data = $('#frmEnterprise').serialize();
        $.ajax({
            type: 'POST',
            url: '/manage/auth/enterpriseSave',
            data: data,
            dataType: 'json',
            success: function (json) {
                $("#subForm").Loadding({IsShow: false});
                if (json.isSuc) {
                	JuaBox.showRight(json.des,{
										closeFun : function(){
													window.top.location.reload();
										}
									})
                } else {
                	JuaBox.sure(json.des);
                }
            },
            error: function () {
                $("#subForm").Loadding({IsShow: false});
                JuaBox.sure('${L:l(lan, "网络访问出错，请稍后重试")}');
            }
        });
    }
</script>
</body>
</html>
