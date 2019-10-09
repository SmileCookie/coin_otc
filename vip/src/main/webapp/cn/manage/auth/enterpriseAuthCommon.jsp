<%@ page session="false" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--<div class="form-line row">
                            <div class="col-sm-4 textright">${L:l(lan,'企业注册区域')}：</div>
                            <div class="col-sm-4">
                                <div class="drop-group dropdown" id="countryGroup">
                                    <div class="dropdown-toggle clearfix" data-toggle="dropdown" aria-haspopup="true"
                                         aria-expanded="false">
                                        <input name="areaInfo" id="areaInfo" type="text"
                                               placeholder="${L:l(lan,'请选择企业所在区域') }"
                                               pattern="limit(1,40)" value=""
                                               class="form-control form-second smallfont" readonly/>
                                        <input id="areaInfoHid" name="areaInfoHid" type="hidden" value="${auth.areaInfo}">
                                        <input id="countryCodeHid" name="countryCodeHid" type="hidden" value="${auth.countryCode}">
                                    </div>
                                    <div class="input-drop dropdown-menu" aria-labelledby="countryGroup"
                                         style="max-height:300px;">
                                        <ul id="areaInfoList">
                                            <li data-value="+86">+86 <span>[中国][China]</span></li>
                                            <li data-value="+852">+852 <span>[香港][香港]</span></li>
                                            <li data-value="+853">+853 <span>[澳门 ][澳門]</span></li>
                                            <li data-value="+886">+886 <span>[台湾 ][台灣]</span></li>
                                            <div class="bk-divider">--------------------------</div>
                                            <c:forEach items="${country}" var="coun">
                                                <li data-value="${coun.code}"
                                                    <c:if test="${coun.code eq auth.countryCode}">class="active"</c:if> >${coun.code}
                                                    <span>[${coun.name}][${coun.des}]</span></li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>--%>

<div class="form-line row">
    <div class="col-sm-4 textright">${L:l(lan,'企业名称')}：</div>
    <div class="col-sm-5">
        <input type="text"
               class="form-control form-second pull-left inputlong ft16"
               name="realName" id="realName" value="${auth.realName}" position="s"
               maxlength="120"  ${param.canEdit == "true" ? "" : "readonly"} />
    </div>
</div>

<div class="form-line row">
    <div class="col-sm-4 textright">${L:l(lan,'法人')}：</div>
    <div class="col-sm-5">
        <input type="text"
               class="form-control form-second pull-left inputlong ft16"
               name="legalPersonName" id="legalPersonName" value="${auth.legalPersonName}" position="s"
               maxlength="20" ${param.canEdit == "true" ? "" : "readonly"} />
    </div>
</div>

<div class="form-line row">
    <div class="col-sm-4 textright">${L:l(lan,'企业注册号')}：</div>
    <div class="col-sm-5">
        <input type="text"
               class="form-control form-second pull-left inputlong ft16"
               name="enterpriseRegisterNo" id="enterpriseRegisterNo" value="${auth.enterpriseRegisterNo}" position="s"
               maxlength="30" ${param.canEdit == "true" ? "" : "readonly"}/>
    </div>
</div>

<div class="form-line row">
    <div class="col-sm-4 textright">${L:l(lan,'组织机构代码')}：</div>
    <div class="col-sm-5">
        <input type="text"
               class="form-control form-second pull-left inputlong ft16"
               name="organizationCode" id="organizationCode" value="${auth.organizationCode}" position="s"
               maxlength="30" ${param.canEdit == "true" ? "" : "readonly"}/>
    </div>
</div>

<div class="form-line row">
    <div class="col-sm-4 textright">${L:l(lan,'注册日期')}：</div>
    <div class="col-sm-5">
        <input type="text" data-picker="true" readonly
               class="form-control form-second pull-left inputlong ft16"
               name="enterpriseRegisterDate" id="enterpriseRegisterDate"
               value="<fmt:formatDate value="${auth.enterpriseRegisterDate}" pattern="yyyy-MM-dd"/>" position="s"
        ${param.canEdit == "true" ? "" : "readonly"}/>
    </div>
</div>

<div class="form-line row">
    <div class="col-sm-4 textright">${L:l(lan,'注册地址')}：</div>
    <div class="col-sm-5">
        <input type="text"
               class="form-control form-second pull-left inputlong ft16"
               name="enterpriseRegisterAddr" id="enterpriseRegisterAddr" value="${auth.enterpriseRegisterAddr}" position="s"
               maxlength="30" ${param.canEdit == "true" ? "" : "readonly"}/>
    </div>
</div>


<script type="text/javascript">
    /*$('#areaInfoList li').on('click', function () {
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
     });*/

    function checkBaseForm(){
        /*var country = $('#countryCodeHid').val();
         if (!country) {
         JuaBox.sure('${L:l(lan, "请选择所在区域")}');
         return false;
         }*/

        var realName = $('#realName').val();
        if(isNull(realName)){
            JuaBox.sure('${L:l(lan, "请填写企业名称")}');
            return false;
        }

        var legalPersonName = $('#legalPersonName').val();
        if(isNull(legalPersonName)){
            JuaBox.sure("${L:l(lan, '请填写法人')}");
            return false;
        }

        var enterpriseRegisterNo = $('#enterpriseRegisterNo').val();
        if(isNull(enterpriseRegisterNo)){
            JuaBox.sure('${L:l(lan, "请填写企业注册号")}');
            return false;
        }

        var organizationCode = $('#organizationCode').val();
        if(isNull(organizationCode)){
            JuaBox.sure('${L:l(lan, "请输入组织机构代码")}');
            return false;
        }

        var enterpriseRegisterDate = $('#enterpriseRegisterDate').val();
        if(isNull(enterpriseRegisterDate)){
            JuaBox.sure('${L:l(lan, "请填写注册日期")}');
            return false;
        }

        var enterpriseRegisterAddr = $('#enterpriseRegisterAddr').val();
        if(isNull(enterpriseRegisterAddr)){
            JuaBox.sure('${L:l(lan, "请填写注册地址")}');
            return false;
        }

        return true;
    }

    function isNull(value){
        return typeof value == "undefined" || value == null || value == "" || $.trim(value).length < 1;
    }
</script>
