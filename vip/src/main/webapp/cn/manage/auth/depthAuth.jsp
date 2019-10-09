<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>

				<div class="content authbody" id="">
				<h2>${L:l(lan, '高级实名认证')}</h2>

                <input type="hidden" id="simplePass" value="${auth.simplePass}"/>

                <c:if test="${auth.status == 3}">
                    <div class="vip-tip">
                        <dl>
                        	<dt>温馨提示：</dt>
                          <dd>${L:l(lan,'您提交的高级实名认证请求未通过审核，原因是')}：<b class="text-third">${auth.reason}</b>。</dd>
                        </dl>
                    </div>
                </c:if>
                <c:if test="${auth.status == 1}">

                    <div class="vip-tip">
                        <dl>
                        	<dt>温馨提示：</dt>
                          <dd>${L:l(lan,'您提交的高级实名认证请求正在审核中，请耐心等待。')}</dd>
                        </dl>
                    </div>

                </c:if>
                <c:if test="${auth.status == 2}">

                    <div class="vip-tip">
                        <dl>
                        	<dt>温馨提示：</dt>
                          <dd>${L:l(lan,'您已经通过高级实名认证，真实姓名')}：<b class="text-third">${auth.shortRealName}</b>，${L:l(lan,'实名认证后不能更改。')}</dd>
                        </dl>
                    </div>

                </c:if>
                <c:if test="${auth.status != 1 && auth.status != 2}">

                    <div class="vip-tip">
                        <dl>
                          <dd>1、${L:l(lan,'根据国家反洗钱法，所有在本站交易的用户均需要实名认证，请提供真实有效的手持本人身份证照片，虚假认证可能会导致账户被冻结，由虚假认证产生的一切后果由用户负责！')}</dd>
                          <dd>2、${L:l(lan,'工作人员需要手动填写证件信息，请尽可能保证证件照清晰，如需加急认证，请直接致电xxx-xxx-xxxx')}。  </dd>
                        </dl>
                    </div>


                    <div class="bk-onekey-form mopich">
                        <style>
/*                             #photoA { width:100%; height:237px; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "zhengdemo3.jpg")}) center center no-repeat; cursor:pointer; float:none;} */
/*                             #photoB { width:100%; height:237px; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "zhengdemo2.jpg")}) center center no-repeat; cursor:pointer; float:none;} */
/*                             #photoC { width:100%; height:237px; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "zhengdemo1.jpg")}) center center no-repeat; cursor:pointer; float:none;} */
/*                             #photoD { width:530px; height:255px; margin:0 auto; background:url(${static_domain}/statics/img/v2/user/${L:l(lan, "addressdemo1.jpg")}) center center no-repeat; cursor:pointer; float:none;} */
                        </style>

                        <h3>1、${L:l(lan,'提交证件信息')}</h3>
                        <div>
                            <div class="form-line row">
                                <div class="col-sm-4 textright">${L:l(lan,'证件所在区域')}：</div>
                                <div class="col-sm-5">
                                    <div class="drop-group dropdown inputlong" id="countryGroup">
                                        <div class="dropdown-toggle clearfix" data-toggle="dropdown"
                                             aria-haspopup="true" aria-expanded="false">
                                            <input name="areaInfo" id="areaInfo" type="text"
                                                   placeholder="${L:l(lan,'请选择证件所在区域') }"
                                                   pattern="limit(1,40)" value=""
                                                   class="form-control form-second smallfont" ${not empty auth.countryCode && auth.simplePass?"disabled='disabled'":'' }
                                                   onfocus="this.blur()"/>
                                            <input id="areaInfoHid" type="hidden" value="${auth.areaInfo}">
                                            <input id="countryCodeHid" type="hidden" value="${auth.countryCode}">
                                        </div>
                                        <div class="input-drop dropdown-menu ${auth.simplePass?'hide':'' }"
                                             aria-labelledby="countryGroup">
                                            <ul id="areaInfoList">
                                                <li data-value="+86">+86 <span>[${lan=='cn' ? '中国' : 'China'}][China]</span></li>
                                                <li data-value="+852">+852 <span>[${lan=='cn' ? '香港' : 'Hongkong'}][香港]</span></li>
                                                <li data-value="+853">+853 <span>[${lan=='cn' ? '澳门' : 'Macau'} ][澳門]</span></li>
                                                <li data-value="+886">+886 <span>[${lan=='cn' ? '台湾' : 'Taiwan'} ][台灣]</span></li>
                                                <div class="bk-divider">--------------------------</div>
                                                <c:forEach items="${country}" var="coun">
                                                    <li data-value="${coun.code}"
                                                        <c:if test="${coun.code eq auth.countryCode}">class="active"</c:if> >${coun.code}
                                                        <span>[${lan=='cn' ? coun.name : coun.des}][${coun.des}]</span></li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="form-line row">
                                <div class="col-sm-4 textright">${L:l(lan,'证件姓名')}：</div>
                                <div class="col-sm-5">
                                    <input type="text"
                                           class="form-control form-second pull-left inputlong smallfont"
                                           name="realName" id="realName" value="${auth.realName}" position="s"
                                           mytitle="${L:l(lan,'请填写有效的银行卡号')}" errormsg="${L:l(lan,'证件姓名错误')}"
                                           pattern="limit(10,30)" ${auth.simplePass?"disabled='disabled'":'' } />
                                </div>
                            </div>
                            <div class="form-line row">
                                <div class="col-sm-4 textright">${L:l(lan,'证件号')}：</div>
                                <div class="col-sm-5">
                                    <input type="text"
                                           class="form-control form-second pull-left inputlong smallfont"
                                           name="cardId" id="cardId" value="${auth.cardId}" position="s"
                                           mytitle="${L:l(lan,'请填写有效的证件号码')}" errormsg="${L:l(lan,'证件号错误')}"
                                           pattern="limit(10,30)" ${auth.simplePass?"disabled='disabled'":'' } />
                                </div>
                            </div>
                        </div>
                        <div class="row uploadipic">
                            <div id="frontalImgDiv" class="col-sm-4">
                                <p>
                                        ${L:l(lan,'手持身份证')}(<a data-toggle="popover" data-placement="top"
                                                               data-trigger="hover" data-html="true"
                                                               data-content="<img src='${static_domain }/statics/img/v2/user/zhengdemo3qx.jpg'  style='width:237px; height:237px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="photoA" errorName="${L:l(lan,'手持证件照')}"></div>
                            </div>
                            <div id="backImgDiv" class="col-sm-4">
                                <p>
                                        ${L:l(lan,'证件正面图')}(<a data-toggle="popover" data-placement="top"
                                                               data-trigger="hover" data-html="true"
                                                               data-content="<img src='${static_domain }/statics/img/v2/user/zhengdemo2qx.jpg'  style='width:237px; height:237px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="photoB" errorName="${L:l(lan,'证件正面图')}"></div>
                            </div>
                            <div id="loadImgDiv" class="col-sm-4">
                                <p>
                                        ${L:l(lan,'证件背面图')}(<a data-toggle="popover" data-placement="top"
                                                               data-trigger="hover" data-html="true"
                                                               data-content="<img src='${static_domain }/statics/img/v2/user/zhengdemo1qx.jpg'   style='width:237px; height:237px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="photoC" errorName="${L:l(lan,'证件背面图')}"></div>
                            </div>
                        </div>

                    </div>


                    <div class="bk-onekey-form" id="bankAuthForm" ${auth.areaInfo != 1?'style="display:none"':''}>
                        <h3>2、${L:l(lan,'银行认证')}</h3>
                        <div class="form-line row">
                            <div class="col-sm-4 textright">${L:l(lan,'持卡人姓名')}：</div>
                            <div class="col-sm-5 textname">
                                <c:choose>
                                    <c:when test="${empty auth.realName}">${L:l(lan,'与证件姓名保持一致')}</c:when>
                                    <c:otherwise>${auth.realName}</c:otherwise>
                                </c:choose>
                            </div>

                        </div>
                        <div class="form-line row">
                            <div class="col-sm-4 textright">${L:l(lan,'银行')}：</div>
                            <div class="col-sm-5">
                                <div class="drop-group dropdown" id="bankGroup">
                                    <div class="dropdown-toggle clearfix" data-toggle="dropdown" aria-haspopup="true"
                                         aria-expanded="false">
                                        <input name="bankName" id="bankName" type="text"
                                               placeholder="${L:l(lan,'请选择银行') }" pattern="limit(1,40)"
                                               class="form-control form-second smallfont" readonly>
                                    </div>
                                    <div class="input-drop dropdown-menu" aria-labelledby="bankGroup">
                                        <ul id="bankNameList">
                                            <li bankkey="2" banktag="ICBC"
                                                bankvalue="${L:l(lan,'中国工商银行')}">${L:l(lan,'中国工商银行')}</li>
                                            <li bankkey="3" banktag="PSBC"
                                                bankvalue="${L:l(lan,'中国邮政银行')}">${L:l(lan,'中国邮政银行')}</li>
                                            <li bankkey="4" banktag="CCB"
                                                bankvalue="${L:l(lan,'中国建设银行')}">${L:l(lan,'中国建设银行')}</li>
                                            <li bankkey="5" banktag="ABC"
                                                bankvalue="${L:l(lan,'中国农业银行')}">${L:l(lan,'中国农业银行')}</li>
                                            <li bankkey="6" banktag="CMB"
                                                bankvalue="${L:l(lan,'中国招商银行')}">${L:l(lan,'中国招商银行')}</li>
                                            <li bankkey="7" banktag="BOC"
                                                bankvalue="${L:l(lan,'中国银行')}">${L:l(lan,'中国银行')}</li>
                                            <li bankkey="8" banktag="BOCOM"
                                                bankvalue="${L:l(lan,'交通银行')}">${L:l(lan,'交通银行')}</li>
                                            <li bankkey="9" banktag="GDB"
                                                bankvalue="${L:l(lan,'广东发展银行')}">${L:l(lan,'广东发展银行')}</li>
                                            <li bankkey="10" banktag="CNCB"
                                                bankvalue="${L:l(lan,'中信银行')}">${L:l(lan,'中信银行')}</li>
                                            <li bankkey="11" banktag="CEB"
                                                bankvalue="${L:l(lan,'光大银行')}">${L:l(lan,'光大银行')}</li>
                                            <li bankkey="12" banktag="SPDB"
                                                bankvalue="${L:l(lan,'浦发银行')}">${L:l(lan,'浦发银行')}</li>
                                            <li bankkey="13" banktag="SDB"
                                                bankvalue="${L:l(lan,'深圳发展银行')}">${L:l(lan,'深圳发展银行')}</li>
                                            <li bankkey="14" banktag="CMBC"
                                                bankvalue="${L:l(lan,'中国民生银行')}">${L:l(lan,'中国民生银行')}</li>
                                            <li bankkey="15" banktag="CIB"
                                                bankvalue="${L:l(lan,'兴业银行')}">${L:l(lan,'兴业银行')}</li>
                                            <li bankkey="16" banktag="PAB"
                                                bankvalue="${L:l(lan,'平安银行')}">${L:l(lan,'平安银行')}</li>
                                            <li bankkey="17" banktag="BCCB"
                                                bankvalue="${L:l(lan,'北京银行')}">${L:l(lan,'北京银行')}</li>
                                            <li bankkey="18" banktag="HXB"
                                                bankvalue="${L:l(lan,'华夏银行')}">${L:l(lan,'华夏银行')}</li>
                                            <li bankkey="2" banktag="ICBC"
                                                bankvalue="${L:l(lan,'中国工商银行')}">${L:l(lan,'中国工商银行')}</li>
                                            <li bankkey="3" banktag="PSBC"
                                                bankvalue="${L:l(lan,'中国邮政银行')}">${L:l(lan,'中国邮政银行')}</li>
                                            <li bankkey="4" banktag="CCB"
                                                bankvalue="${L:l(lan,'中国建设银行')}">${L:l(lan,'中国建设银行')}</li>
                                            <li bankkey="5" banktag="ABC"
                                                bankvalue="${L:l(lan,'中国农业银行')}">${L:l(lan,'中国农业银行')}</li>
                                            <li bankkey="6" banktag="CMB"
                                                bankvalue="${L:l(lan,'中国招商银行')}">${L:l(lan,'中国招商银行')}</li>
                                            <li bankkey="7" banktag="BOCSH"
                                                bankvalue="${L:l(lan,'中国银行')}">${L:l(lan,'中国银行')}</li>
                                            <li bankkey="8" banktag="BOCOM"
                                                bankvalue="${L:l(lan,'交通银行')}">${L:l(lan,'交通银行')}</li>
                                            <li bankkey="9" banktag="GDB"
                                                bankvalue="${L:l(lan,'广东发展银行')}">${L:l(lan,'广东发展银行')}</li>
                                            <li bankkey="10" banktag="CNCB"
                                                bankvalue="${L:l(lan,'中信银行')}">${L:l(lan,'中信银行')}</li>
                                            <li bankkey="11" banktag="CEB"
                                                bankvalue="${L:l(lan,'光大银行')}">${L:l(lan,'光大银行')}</li>
                                            <li bankkey="12" banktag="SPDB"
                                                bankvalue="${L:l(lan,'浦发银行')}">${L:l(lan,'浦发银行')}</li>
                                            <li bankkey="13" banktag="SDB"
                                                bankvalue="${L:l(lan,'深圳发展银行')}">${L:l(lan,'深圳发展银行')}</li>
                                            <li bankkey="14" banktag="CMBC"
                                                bankvalue="${L:l(lan,'中国民生银行')}">${L:l(lan,'中国民生银行')}</li>
                                            <li bankkey="15" banktag="CIB"
                                                bankvalue="${L:l(lan,'兴业银行')}">${L:l(lan,'兴业银行')}</li>
                                            <li bankkey="16" banktag="PAB"
                                                bankvalue="${L:l(lan,'平安银行')}">${L:l(lan,'平安银行')}</li>
                                            <li bankkey="17" banktag="BCCB"
                                                bankvalue="${L:l(lan,'北京银行')}">${L:l(lan,'北京银行')}</li>
                                            <li bankkey="18" banktag="HXB"
                                                bankvalue="${L:l(lan,'华夏银行')}">${L:l(lan,'华夏银行')}</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-line row">
                            <div class="col-sm-4 textright">${L:l(lan,'银行卡号')}：</div>
                            <div class="col-sm-5">
                                <input type="text"
                                       class="form-control form-second pull-left inputlong smallfont"
                                       name="bankCard" id="bankCard" value="${auth.bankCard}" position="s"
                                       mytitle="${L:l(lan,'请填写有效的银行卡号')}" errormsg="${L:l(lan,'银行卡号错误')}"
                                       pattern="limit(10,30)"/>
                                <p class="textname2 mb0">${L:l(lan,'请填写与使用您上传的证件信息所开通的储蓄银行卡')}</p>
                            </div>
                        </div>
                        <div class="form-line row">
                            <div class="col-sm-4 textright">${L:l(lan,'开户预留手机号')}：</div>
                            <div class="col-sm-5">
                                <input type="text"
                                       class="form-control form-second pull-left inputlong smallfont"
                                       name="bankTel" id="bankTel" value="${auth.bankTel}" position="s"
                                       mytitle="${L:l(lan,'请填写有效的银行预留手机号')}" errormsg="${L:l(lan,'开户预留手机号错误')}"
                                       pattern="limit(4,15)"/>
                                <p class="textname2 mb0">${L:l(lan,'请填写该卡开户时银行预留手机')}</p>
                            </div>
                        </div>
                    </div>

                    <div class="bk-onekey-form mopich" id="overAuthForm" ${auth.areaInfo == 1?'style="display:none"':''}>
                        <h3>2、${L:l(lan,'提交住址证明')}</h3>
                        <div class="vip-tip">
                        <dl>
                          <dd>1、${L:l(lan,'目前可接受的住址证明文件类型')}</dd>
                          <dd>2、${L:l(lan,'暂不接受的住址证明')}</dd>
                          <dd>3、${L:l(lan,'住址证明文件上的日期和住址等信息不可手写；')}</dd>
                          <dd>4、${L:l(lan,'住址证明图片整体要求清晰可见（允许水印）。')}</dd>
                        </dl>
                    </div>
                        <div class="row uploadipic">
                            <div id="addressImgDiv" class="col-sm-12">
                                <p>
                                        ${L:l(lan,'住址证明')}(<a data-toggle="popover" data-placement="top"
                                                              data-trigger="hover" data-html="true"
                                                              data-content="<img src='${static_domain }/statics/img/v2/user/${L:l(lan, "addressdemo2.jpg")}'  style='width:530px; height:255px;'>">${L:l(lan,'查看实例')}</a>)
                                </p>
                                <div class="pm-itemcont" id="photoD" errorName="${L:l(lan,'住址证明')}"></div>
                            </div>
                        </div>

                    </div>


                    <div class="row mb15">
                    		<div class="col-sm-4 textright"></div>
                        <div class="col-sm-5">
                        <a href="javascript:depthSave()" class="btn btn-primary btn-block btn-lg ">${L:l(lan,'提交认证信息')}</a>
                        </div>
                    </div>
                </c:if>
            </div>
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/en/upload.css?V${CH_VERSON }"/>
<script type="text/javascript" src="${static_domain }/statics/js/common/upload.js?V${CH_VERSON }"></script>

<script type="text/javascript">
    $(function () {
        document.domain = "${baseDomain}";//isPicFile : false
        $("#photoA").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "loadImg",
            isPicFile: true
        });
        $("#photoB").initFileUpload({

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
        $("#photoC").initFileUpload({
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
        $("#photoD").initFileUpload({
            initShowNum: 1,
            needAdd: false,
            isProcess: false,
            minNum: 1,
            pics: "",
            userType: 1,
            savePicSize: false,
            isAuth: true,
            picsPathHiddenName: "addressImg",
            isPicFile: true
        });

        $('#bankNameList li').on('click', function () {
            $('#bankName').val($(this).html());
            $('#bankNameList li').removeClass("active");
            $(this).addClass("active");
        });
        $('#areaInfoList li').on('click', function () {
            var areaType = 1;
            if ($(this).data('value') == "+86") {
                areaType = 1;
            } else if ($(this).data('value') == "+852" || $(this).data('value') == "+853") {
                areaType = 2;
            } else if ($(this).data('value') == "+886") {
                areaType = 3;
            } else {
                areaType = 4;
            }
            $('#areaInfo').val($(this).find("span").text());
            $('#areaInfoHid').val(areaType);

            $('#areaInfoList li').removeClass("active");
            $(this).addClass("active");

            if (areaType != 1) {
                $("#bankAuthForm").hide();
                $("#overAuthForm").show();
            } else {
                $("#bankAuthForm").show();
                $("#overAuthForm").hide();
            }
        });

        var hidCountryCode = $('#countryCodeHid').val();
        var countryName = $('#areaInfoList').find('li[data-value="' + hidCountryCode + '"] span').html();
        $('#areaInfo').val(countryName);
        var countryCodeHid = $('#countryCodeHid').val();
        if (!$('#areaInfo').val()) {
            $('div[aria-labelledby="countryGroup"]').removeClass('hide');
            $('#areaInfo').removeAttr('disabled');
        }
        var simplePass = $('#simplePass').val();
        if (simplePass == 'true') {
// 		$('#areaInfo').off('click');
// 		$('#areaInfo').off('focus');
// 		$('#areaInfo').off('blur');
        }

        $('#areaInfoList li').on('click', function () {
            $('#countryCodeHid').val($(this).data('value'));
            if (simplePass == 'true') {
                // $('#countryCodeHid').attr('disabled', 'disabled');
            }
        });
    });

    function depthSave() {
        var bankCard = $('#bankCard').val();
        var bankTel = $('#bankTel').val();
        var frontalImg = $('#frontalImgDiv .J_PicUrl').val();
        var backImg = $('#backImgDiv .J_PicUrl').val();
        var loadImg = $('#loadImgDiv .J_PicUrl').val();
        var addressImg = $('#addressImgDiv .J_PicUrl').val();
        var area = $('#areaInfoHid').val();
        var country = $('#countryCodeHid').val();
        var realName = $('#realName').val();
        var cardId = $('#cardId').val();
        var data = {
            bankCard: bankCard,
            bankTel: bankTel,
            frontalImg: frontalImg,
            backImg: backImg,
            addressImg: addressImg,
            loadImg: loadImg,
            area: area,
            realName: realName,
            cardId: cardId,
            country: country
        };
        $.ajax({
            type: 'POST',
            url: '/manage/auth/depthSave',
            data: data,
            dataType: 'json',
            success: function (json) {
                if (json.isSuc) {
                	JuaBox.showRight(json.des, {
                		closeFun : function(){
							window.top.location.reload();
						}
                    });
                } else {
                	JuaBox.sure(json.des);
                }
            },
            error: function () {
            	JuaBox.sure('${L:l(lan, "网络访问出错，请稍后重试")}');
            }
        });
    }
</script>

<script type="text/javascript">
 $(document).ready(function() {
	require(['module_user'],function(user){
		user.pageIndexInit();
	});
 });
 
</script>
      