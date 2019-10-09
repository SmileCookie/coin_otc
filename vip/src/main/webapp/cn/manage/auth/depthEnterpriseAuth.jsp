<%@ page session="false" language="java" import="java.util.*"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
    <title>${L:l(lan,'安全中心-CHBTC兑换中心-比特币交易-比特币价格-用心服务每一刻')}</title>
    <jsp:include page="/common/head.jsp"/>
    <link rel=stylesheet type=text/css href="${static_domain }/statics/css/en/upload.css?V${CH_VERSON }"/>
    <script type="text/javascript" src="${static_domain }/statics/js/common/upload.js?V${CH_VERSON }"></script>
    <style>
        #businessLicenseImgDiv .popover, #taxRegistrationCertificateImgDiv .popover, #organizationCodeImgDiv .popover { width:563px!important;
        height:399px!important; min-width: 563px; max-width: 563px;
        overflow: hidden;   }
    </style>

</head>
<body>
<div class="bk-body">
    <jsp:include page="/common/top.jsp"/>
    <!--页面中部内容开始-->
    <div class="bk-onekey">
       <div class="bk-safe">
        <div class="container">
         
  	 	   <div class="safe-rd snav-rd">
  	 	 	   <div class="hd">
  	 	 	   	   <h3><i class="fa fa-user"></i><b>${L:l(lan, '高级实名认证')}</b></h3>
  	 	 	   </div>
       

        <div class="authbody">

            <div class="container">
                <input type="hidden" id="simplePass" value="${auth.simplePass}"/>

                <c:if test="${auth.status == 3}">
                    <div class="user_main_title1">
                            ${L:l(lan,'您提交的高级实名认证请求未通过审核，原因是')}：<b style="color:#f00000;">${auth.reason}</b>。
                    </div>
                </c:if>
                <c:if test="${auth.status == 1}">

                    <div class="user_main_title1">
                            ${L:l(lan,'您提交的高级实名认证请求正在审核中，请耐心等待。')}
                    </div>

                </c:if>
                <c:if test="${auth.status == 2}">

                    <div class="user_main_title1">
                            ${L:l(lan,'您已经通过高级实名认证，公司名称')}：<b
                            style="color:#f00000;">${auth.shortRealName}</b>，${L:l(lan,'实名认证后不能更改。')}
                    </div>

                </c:if>

                <c:if test="${auth.status != 1 && auth.status != 2}">
                    <style>
                        #businessLicenseImg1 { width:533px; height:378px; margin:0 auto; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "businessLicensedemo.jpg")}) center center no-repeat; cursor:pointer; float:none;}
                        #taxRegistrationCertificateImg1 { width:533px; height:378px; margin:0 auto; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "taxRegistrationCertificate.jpg")}) center center no-repeat; cursor:pointer; float:none;}
                        #organizationCodeImg1 { width:533px; height:378px; margin:0 auto; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "organizationCode.jpg")}) center center no-repeat; cursor:pointer; float:none;}
                        #frontalImg1,#linkerFrontalImg1 { width:100%; height:237px; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "zhengdemo2.jpg")}) center center no-repeat; cursor:pointer; float:none;}
                        #backImg1,#linkerBackImg1 { width:100%; height:237px; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "zhengdemo1.jpg")}) center center no-repeat; cursor:pointer; float:none;}
                    </style>

                    <div class="ctips user_main_title2">
                        <p>
                            1、${L:l(lan,'根据国家反洗钱法，所有在本站交易的企业用户均需要实名认证，请提供真实有效的手持法人本人身份证照片，虚假认证可能会导致账户被冻结，由虚假认证产生的一切后果由用户负责！')}
                            <br/> 2、${L:l(lan,'工作人员需要手动填写证件信息，请尽可能保证证件照清晰，如需加急认证，请直接致电xxx-xxx-xxxx')}。    </i>
                            <!-- <br /> 3、<i class="text-primary">${L:l(lan,'根据国家监管部门相关规定，海外用户不能在本站进行人民币充值、提现操作')}。 -->
                        </p>
                        <div class="close" title="${L:l(lan,'关闭')}">×</div>
                    </div>


                    <div class="bk-onekey-form mopich">
                        <h3>1、${L:l(lan,'企业信息')}</h3>
                        <div>
                            <jsp:include page="enterpriseAuthCommon.jsp" flush="true">
                                <jsp:param name="canEdit" value="false"/>
                            </jsp:include>
                        </div>
                    </div>

                    <div class="bk-onekey-form mopich">
                        <h3>2、${L:l(lan,'营业执照')}</h3>
                        <div class="row uploadipic">
                            <div id="businessLicenseImgDiv" class="col-sm-12">
                                <p>
                                        <a data-toggle="popover" data-placement="top"
                                                              data-trigger="hover" data-html="true"
                                                              data-content="<img src='${static_domain }/statics/img/v2/user/businessLicensedemo2.jpg'  style='width:533px; height:378px'>">${L:l(lan,'查看实例')}</a>
                                </p>
                                <div class="pm-itemcont" id="businessLicenseImg1" errorName="${L:l(lan,'营业执照')}"></div>
                            </div>
                        </div>
                    </div>

                    <div class="bk-onekey-form mopich">
                        <h3>3、${L:l(lan,'税务登记证')}</h3>
                        <div class="row uploadipic">
                            <div id="taxRegistrationCertificateImgDiv" class="col-sm-12">
                                <p>
                                    <a data-toggle="popover" data-placement="top"
                                       data-trigger="hover" data-html="true"
                                       data-content="<img src='${static_domain }/statics/img/v2/user/taxRegistrationCertificate2.jpg'  style='width:533px; height:378px'>">${L:l(lan,'查看实例')}</a>
                                </p>
                                <div class="pm-itemcont" id="taxRegistrationCertificateImg1" errorName="${L:l(lan,'税务登记证')}"></div>
                            </div>
                        </div>
                    </div>

                    <div class="bk-onekey-form mopich">
                        <h3>4、${L:l(lan,'组织机构代码证')}</h3>
                        <div class="row uploadipic">
                            <div id="organizationCodeImgDiv" class="col-sm-12">
                                <p>
                                    <a data-toggle="popover" data-placement="top"
                                       data-trigger="hover" data-html="true"
                                       data-content="<img src='${static_domain }/statics/img/v2/user/organizationCode2.jpg'  style='width:533px; height:378px'>">${L:l(lan,'查看实例')}</a>
                                </p>
                                <div class="pm-itemcont" id="organizationCodeImg1" errorName="${L:l(lan,'组织机构代码证')}"></div>
                            </div>
                        </div>
                    </div>

                    <div class="bk-onekey-form mopich">
                        <h3>5、${L:l(lan,'法人身份证')}</h3>
                        <div class="row uploadipic">
                            <div id="frontalImgDiv" class="col-sm-6">
                                <p>
                                        ${L:l(lan,'证件正面图')}(<a data-toggle="popover" data-placement="top"
                                                               data-trigger="hover" data-html="true"
                                                               data-content="<img src='${static_domain }/statics/img/v2/user/zhengdemo2qx.jpg'  style='width:237px; height:237px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="frontalImg1" errorName="${L:l(lan,'证件正面图')}"></div>
                            </div>
                            <div id="backImgDiv" class="col-sm-6">
                                <p>
                                        ${L:l(lan,'证件背面图')}(<a data-toggle="popover" data-placement="top"
                                                               data-trigger="hover" data-html="true"
                                                               data-content="<img src='${static_domain }/statics/img/v2/user/zhengdemo1qx.jpg'   style='width:237px; height:237px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="backImg1" errorName="${L:l(lan,'证件背面图')}"></div>
                            </div>
                        </div>
                    </div>

                    <div class="bk-onekey-form mopich">
                        <h3>6、${L:l(lan,'联系人身份证')}</h3>
                        <div class="row uploadipic">
                            <div id="linkerFrontalImgDiv" class="col-sm-6">
                                <p>
                                        ${L:l(lan,'证件正面图')}(<a data-toggle="popover" data-placement="top"
                                                               data-trigger="hover" data-html="true"
                                                               data-content="<img src='${static_domain }/statics/img/v2/user/zhengdemo2qx.jpg'  style='width:237px; height:237px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="linkerFrontalImg1" errorName="${L:l(lan,'证件正面图')}"></div>
                            </div>
                            <div id="linkerBackImgDiv" class="col-sm-6">
                                <p>
                                        ${L:l(lan,'证件背面图')}(<a data-toggle="popover" data-placement="top"
                                                               data-trigger="hover" data-html="true"
                                                               data-content="<img src='${static_domain }/statics/img/v2/user/zhengdemo1qx.jpg'   style='width:237px; height:237px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="linkerBackImg1" errorName="${L:l(lan,'证件背面图')}"></div>
                            </div>
                        </div>
                    </div>

                    <div class="do mb15">
                        <a href="javascript:depthSave()" class="btn btn-outsecond btn-lg "><i
                                class="fa fa-check fa-lg  fa-fw"></i>&nbsp; ${L:l(lan,'提交认证信息')}</a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
    </div> 
</div> 
    <jsp:include page="/common/foot.jsp"/>
</div>


<script type="text/javascript">
    $(function () {
        document.domain = "${baseDomain}";//isPicFile : false
        $("#businessLicenseImg1").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "businessLicenseImg",
            isPicFile: true
        });
        $("#taxRegistrationCertificateImg1").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "taxRegistrationCertificateImg",
            isPicFile: true
        });
        $("#organizationCodeImg1").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "organizationCodeImg",
            isPicFile: true
        });
        $("#frontalImg1").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "frontalImg",
            isPicFile: true
        });
        $("#backImg1").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "backImg",
            isPicFile: true
        });
        $("#linkerFrontalImg1").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "linkerFrontalImg",
            isPicFile: true
        });
        $("#linkerBackImg1").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "linkerBackImg",
            isPicFile: true
        });
    });

    function depthSave() {
        var businessLicenseImg = $('#businessLicenseImgDiv .J_PicUrl').val();
        var taxRegistrationCertificateImg = $('#taxRegistrationCertificateImgDiv .J_PicUrl').val();
        var organizationCodeImg = $('#organizationCodeImgDiv .J_PicUrl').val();
        var frontalImg = $('#frontalImgDiv .J_PicUrl').val();
        var backImg = $('#backImgDiv .J_PicUrl').val();
        var linkerFrontalImg = $('#linkerFrontalImgDiv .J_PicUrl').val();
        var linkerBackImg = $('#linkerBackImgDiv .J_PicUrl').val();

        /*if( !checkBaseForm() ){
            return;
        }*/

        if(isNull(businessLicenseImg) && isNull("${auth.businessLicenseImg}") ){
            JuaBox.sure("${L:l(lan, '请上传营业执照')}");
            return;
        }
        if(isNull(taxRegistrationCertificateImg) && isNull("${auth.taxRegistrationCertificateImg}") ){
            JuaBox.sure("${L:l(lan, '请上传税务登记证')}");
            return;
        }
        if(isNull(organizationCodeImg) && isNull("${auth.organizationCodeImg}") ){
            JuaBox.sure("${L:l(lan, '请上传组织机构代码证')}");
            return;
        }

        if(isNull(frontalImg) && isNull("${auth.frontalImg}") ){
            JuaBox.sure("${L:l(lan, '请上传法人身份证正面照')}");
            return;
        }
        if(isNull(backImg) && isNull("${auth.backImg}")){
            JuaBox.sure("${L:l(lan, '请上传法人身份证背面照')}");
            return;
        }

        if(isNull(linkerFrontalImg) && isNull("${auth.linkerFrontalImg}")){
            JuaBox.sure("${L:l(lan, '请上传联系人身份证正面照')}");
            return;
        }
        if(isNull(linkerBackImg) && isNull("${auth.linkerBackImg}")){
            JuaBox.sure("${L:l(lan, '请上传联系人身份证背面照')}");
            return;
        }

        var data = {
            businessLicenseImg: businessLicenseImg,
            taxRegistrationCertificateImg: taxRegistrationCertificateImg,
            organizationCodeImg: organizationCodeImg,
            frontalImg: frontalImg,
            backImg: backImg,
            linkerFrontalImg: linkerFrontalImg,
            linkerBackImg: linkerBackImg
        };
        $.ajax({
            type: 'POST',
            url: '/u/auth/depthEnterpriseSave',
            data: data,
            dataType: 'json',
            success: function (json) {
                if (json.isSuc) {
                    Right(json.des, {
                        call: function () {
                            window.location.reload();
                        }
                    });
                } else {
                    Wrong(json.des);
                }
            },
            error: function () {
                Wrong('${L:l(lan, "网络访问出错，请稍后重试")}');
            }
        });
    }
</script>
</body>
</html>
