<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="content authbody" id="mainForm">
	<h2>实名认证</h2>

                <c:if test="${auth.status==6}">

                    <div class="vip-tip">
                    	<dl>
                    		<dt>${L:l(lan, "温馨提示")}：</dt>
                        <dd>${L:l(lan,'您已经通过初级实名认证，真实姓名')}：<b class="text-third">${auth.realName}</b>${L:l(lan, "，实名认证后不能更改。您还可以进行")}
                        	<a class="btn btn-primary btn-sm" href="/manage/auth/depth" style="margin-left:15px;"><i class="fa fa-cog "></i>&nbsp; ${L:l(lan, "高级实名认证")}</a>
                        </dd>
                       </dl>
                    </div>

                </c:if>
                <c:if test="${auth.status==7}">

                    <div class="vip-tip">
                    <dl>
                    		<dt>${L:l(lan, "温馨提示")}：</dt>
                        <dd>${L:l(lan,'您提交的初级实名认证请求未通过审核，原因是')}：<b class="text-third">${auth.reason}</b>。
                    		</dd>
                       </dl>
                    </div>

                </c:if>
                <c:if test="${auth.status==5}">

                    <div class="vip-tip">
                    	<dl>
                    		<dt>${L:l(lan, "温馨提示")}：</dt>
                        <dd>${L:l(lan,'您提交的初级实名认证请求正在审核中，请耐心等待。')}</dd>
                       </dl>
                    </div>

                </c:if>
                <c:if test="${empty auth || auth.status==0 || auth.status==4 || auth.status==7}">
                
                
                		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan, "身份认证")}：</dt>
				<dd>${L:l(lan, "根据国家监管部门规定，您需要做实名信息认证，认证信息一经验证不能修改。")}</dd>
			</dl>
		</div>

                    <div id="subForm" class="bk-onekey-form ">
                        <div class="form-line row">
                            <div class="col-sm-4 textright">${L:l(lan,'证件所在区域')}：</div>
                            <div class="col-sm-5">
                                <div class="drop-group dropdown" id="countryGroup">
                                    <div class="dropdown-toggle clearfix" data-toggle="dropdown" aria-haspopup="true"
                                         aria-expanded="false">
                                        <input name="areaInfo" id="areaInfo" type="text"
                                               placeholder="${L:l(lan,'请选择证件所在区域') }"
                                               pattern="limit(1,40)" value=""
                                               class="form-control form-second smallfont" readonly/>
                                        <input id="areaInfoHid" type="hidden" value="${auth.areaInfo}">
                                        <input id="countryCodeHid" type="hidden" value="${auth.countryCode}">
                                    </div>
                                    <div class="input-drop dropdown-menu" aria-labelledby="countryGroup"
                                         style="max-height:300px;">
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
                            <div class="col-sm-4 textright">${L:l(lan,'姓名')}：</div>
                            <div class="col-sm-5">
                                <input type="text"
                                       class="form-control form-second pull-left inputlong ft16"
                                       name="realName" id="realName" value="${auth.realName}" position="s"
                                       mytitle="${L:l(lan,'请填写您的真实姓名,以便为您提供更好的服务')}" errormsg="${L:l(lan,'真实姓名错误')}"
                                       pattern="limit(4,30)"/>
                            </div>
                        </div>
                        <div class="form-line row">
                            <div class="col-sm-4 textright">${L:l(lan,'身份证号')}：</div>
                            <div class="col-sm-5">
                                <input type="text"
                                       class="form-control form-second pull-left inputlong ft16"
                                       name="cardId" id="cardId" value="${auth.cardId}" position="s"
                                       mytitle="${L:l(lan,'请填写有效的身份证号码')}" errormsg="${L:l(lan,'身份证号错误')}"
                                       pattern="limit(4,18)"/>
                            </div>
                        </div>
                        <div class="do row">
                        		<div class="col-sm-4 textright"></div>
                            <div class="col-sm-5">
                                <a href="javascript:simpleSave()" class="btn btn-primary btn-lg btn-block ">${L:l(lan,'提交认证信息')}</a>
                            </div>

                            

                            <%--<a href="/u/auth/depth" class="btn btn-outsecond btn-lg ">
                                <i class="fa fa-check fa-lg  fa-fw"></i>&nbsp; ${L:l(lan,'高级实名认证')}
                            </a>--%>
                        </div>
                    </div>
                </c:if>
            </div>





<script type="text/javascript">
    $(function () {
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

        $('#areaInfoList li').on('click', function () {
            $('#countryCodeHid').val($(this).data('value'));
        });
    });

    function simpleSave() {
        var realName = $('#realName').val();
        var cardId = $('#cardId').val();
        var area = $('#areaInfoHid').val();
        var country = $('#countryCodeHid').val();
        var data = {
            realName: realName,
            cardId: cardId,
            area: area,
            country: country
        };
        if (!country) {
            JuaBox.sure("${L:l(lan,'请选择证件所在区域')}");
            return;
        }
        $("#subForm").Loadding();
        $.ajax({
            type: 'POST',
            url: '/manage/auth/simpleSave',
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

