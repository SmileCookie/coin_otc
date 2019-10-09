<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <c:set var="isIndividualUser" value="${empty au || au.type != 2}"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${isIndividualUser ? "个人用户照片" : "企业用户明细"}</title>
    <jsp:include page="/admins/top.jsp"/>
    <script type="text/javascript" src="${static_domain}/statics/js/admin/jquery.js"></script>
    <style type="text/css">
        div {
            margin: 10px;
        }

        .title {
            font-weight: bold;
        }

        .imgTitle {
            margin: 40px 0 10px 0;
        }

        .left {
            float: left;
            display: inline;
        }

        .center {
            float: left;
            display: inline;
        }

        .right {
            float: right;
            display: inline;
        }

        .clearfix {
            overflow: hidden;
        }

        .main {
            padding: 20px 30px;
            background: #e0e0e0;
        }

        /*select{
            display: inline-block;
        }*/
    </style>
</head>
<body>
<div id="outerdiv"
     style="position:fixed;top:0;left:0;background:rgba(0,0,0,0.7);z-index:2;width:100%;height:100%;display:none;">
    <div id="innerdiv" style="position:absolute;">
        <img id="bigimg" style="border:5px solid #fff;" src=""/>
    </div>
</div>
<%--<c:if test="${!isIndividualUser}">
    <div><span class="title">企业注册区域</span>：${au.showArea}</div>
    <div><span class="title">企业名称</span>：${au.realName}</div>
    <div><span class="title">法人</span>：${au.legalPersonName}</div>
    <div><span class="title">企业注册号</span>：${au.enterpriseRegisterNo}</div>
    <div><span class="title">组织机构代码</span>：${au.organizationCode}</div>
    <div><span class="title">注册日期</span>：<fmt:formatDate value="${au.enterpriseRegisterDate}" pattern="yyyy-MM-dd"/> </div>
    <div><span class="title">注册地址</span>：${au.enterpriseRegisterAddr}</div>
</c:if>--%>
<div class="main">
    <a href="/admin/user/authen" target="Main" aid="579">返回列表</a>
    <div>用户名：${u.userName}</div>
    <div>认证次数：${authTimes}</div>
    <div>认证地区：
        <c:if test="${au.areaInfo == 1}"> 大陆地区</c:if>
        <c:if test="${au.areaInfo == 2}"> 港澳台地区</c:if>
        <c:if test="${au.areaInfo == 3}"> 海外地区</c:if>
    </div>
    <div>认证国家：${au.countryName}</div>
    <div>认证类型：
        <c:if test="${au.cardType eq '1'}"> 身份证</c:if>
        <c:if test="${au.cardType eq '2'}"> 护照</c:if>
    </div>
    <div>证件号码：${au.cardId}</div>
    <div>真实姓名：${au.realName}</div>
    <div>认证时间：<fmt:formatDate value="${au.submitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></div>
    <div>证件有效期开始时间：${au.startDate}</div>
    <div>证件有效期截止时间：${au.endDate}</div>
    <div>用户上传图片：</div>
    <div>
        <c:choose>
            <c:when test="${isIndividualUser}">
                <c:if test="${au.cardType eq '1'}">
                    <div class="clearfix">
                        <div class="left">
                            <div>身份证正面</div>
                            <img class="pimg" src="${au.frontalImg}" height="200" width="200" alt="身份证正面"
                                 title="身份证正面"/>
                        </div>
                        <div class="left">
                            <div>身份证反面</div>
                                <%--<img src="${imagePrefix}${au.backImg}" alt="身份证反面" title="身份证反面"/>--%>
                            <img class="pimg" src="${au.backImg}" id="img2" height="200" width="200"
                                 alt="身份证反面" title="身份证反面"/>
                        </div>
                        <div class="left">
                            <div>手持身份证</div>
                                <%--<img src="${imagePrefix}${au.loadImg}" alt="手持身份证" title="手持身份证"/>--%>
                            <img class="pimg" src="${au.loadImg}" id="img3" height="200" width="200"
                                 alt="手持身份证" title="手持身份证"/>
                        </div>
                    </div>


                </c:if>
                <c:if test="${au.cardType eq '2'}">
                    <div class="clearfix">
                        <div class="left">
                            <div>护照正面</div>
                            <img class="pimg" src="${au.frontalImg}" height="200" width="200" alt="身份证正面"
                                 title="身份证正面"/>
                        </div>
                        <div class="left">
                            <div>手持护照</div>
                                <%--<img src="${imagePrefix}${au.loadImg}" alt="手持身份证" title="手持身份证"/>--%>
                            <img class="pimg" src="${au.loadImg}" id="img3" height="200" width="200"
                                 alt="手持身份证" title="手持身份证"/>
                        </div>
                    </div>


                </c:if>


                <%--<div>
                    <div class="imgTitle">住址证明</div>
                    &lt;%&ndash;<img src="${imagePrefix}${au.addrImg}" alt="住址证明" title="住址证明"/>&ndash;%&gt;
                    <img src="${au.addrImg}" alt="住址证明" title="住址证明"/>
                </div>--%>

                <%--<div>--%>
                <%--<div class="imgTitle">身份证正面</div>--%>
                <%--<img src="${imagePrefix}${au.frontalImg}" alt="身份证正面" title="身份证正面"/>--%>
                <%--</div>--%>
                <%--<div>--%>
                <%--<div class="imgTitle">身份证反面</div>--%>
                <%--<img src="${imagePrefix}${au.backImg}" alt="身份证反面" title="身份证反面"/>--%>
                <%--</div>--%>
                <%--<div>--%>
                <%--<div class="imgTitle">手持身份证</div>--%>
                <%--<img src="${imagePrefix}${au.loadImg}" alt="手持身份证" title="手持身份证"/>--%>
                <%--</div>--%>
                <%--<div>--%>
                <%--<div class="imgTitle">查询返回的结果照片</div>--%>
                <%--<img src="${imagePrefix}${au.photo}" alt="查询返回的结果照片" title="查询返回的结果照片"/>--%>
                <%--</div>--%>
                <%--<div>--%>
                <%--<div class="imgTitle">住址证明</div>--%>
                <%--<img src="${imagePrefix}${au.proofAddressImg}" alt="住址证明" title="住址证明"/>--%>
                <%--</div>--%>

                <%--<br/>--%>
                <%--<input id="loadIdCardImgBtn" type="button" onclick="loadidCardPhoto();" value="显示返照" />--%>
                <%--<input type="hidden" id="uId" value="${uId}" />--%>
                <%--<br/>--%>
                <%--<img id="idcardPhoto" src="data:image/jpg;base64,${idcardImg}" alt="查询返回的结果照片" title="查询返回的结果照片"/>--%>
                <%--<span id="idcardPhotoErr"></span>--%>
            </c:when>

            <%--<c:otherwise>
                <div>
                    <div class="imgTitle">营业执照</div>
                    <img src="${imagePrefix}${au.businessLicenseImg}" alt="营业执照" title="营业执照"/>
                </div>
                <div>
                    <div class="imgTitle">税务登记证</div>
                    <img src="${imagePrefix}${au.taxRegistrationCertificateImg}" alt="税务登记证" title="税务登记证"/>
                </div>
                <div>
                    <div class="imgTitle">组织机构代码证</div>
                    <img src="${imagePrefix}${au.organizationCodeImg}" alt="组织机构代码证" title="组织机构代码证"/>
                </div>
                <div>
                    <div class="imgTitle">法人身份证正面照</div>
                    <img src="${imagePrefix}${au.frontalImg}" alt="法人身份证正面照" title="法人身份证正面照"/>
                </div>
                <div>
                    <div class="imgTitle">法人身份证背面照</div>
                    <img src="${imagePrefix}${au.backImg}" alt="法人身份证背面照" title="法人身份证背面照"/>
                </div>
                <div>
                    <div class="imgTitle">联系人身份证正面照</div>
                    <img src="${imagePrefix}${au.linkerFrontalImg}" alt="联系人身份证正面照" title="联系人身份证正面照"/>
                </div>
                <div>
                    <div class="imgTitle">联系人身份证正面照</div>
                    <img src="${imagePrefix}${au.linkerBackImg}" alt="联系人身份证正面照" title="联系人身份证正面照"/>
                </div>
            </c:otherwise>--%>
        </c:choose>
        <div>审核:
            <input name="state" type="radio" value="1" checked="checked"/>通过
            <input name="state" type="radio" value="2"/>不通过
            <input name="id" id="id" type="hidden" value="${au.id}"/>
        </div>
        <div id="unPassReason">原因
            <select name="reason" id="checkSelect" style="display: inline-block">
                <option value="" selected>=请选择不通过原因=</option>
                <option value="8">1、图像经过处理</option>
                <option value="9">2、图像不清晰</option>
                <option value="10">3、证件图像类型不符</option>
                <option value="11">4、平台仅支持满16周岁的用户进行交易</option>
            </select>
        </div>
        <div><input type="button" value="审核" id="checkBtn"/></div>
    </div>

    <script type="text/javascript">
        var canLoad = true;

        function loadidCardPhoto() {
            if (!canLoad) {
                return;
            }
            canLoad = false;
            $.ajax({
                type: 'POST',
                url: '/admin/user/authen/seeIdCardImg?id=' + $('#uId').val(),
                data: '',
                dataType: 'json',
                success: function (json) {
                    $('#loadIdCardImgBtn').attr('disabled', 'disabled');
                    if (json.isSuc) {
                        $('#idcardPhoto').attr('src', 'data:image/jpg;base64,' + json.des);
                    } else {
                        $('#idcardPhotoErr').html(json.des);
                    }
                },
                error: function () {

                }
            });
        }
    </script>
    <script type="text/javascript">

        $(function () {
            $("#unPassReason").css("display", "none");
            $(":radio").click(function () {
                if (2 == $(this).val()) {
                    $("#unPassReason").css("display", "inline-block")
                } else {
                    $("#unPassReason").css("display", "none")
                }
            });

            $("#checkBtn").click(function () {
                var id = $("#id").val();
                var $checkSelect = $("#checkSelect").val();
                var id = $("#id").val();
                var $checkRadio = $("input:radio[name='state']:checked").val();
                agree(id, $checkRadio, $checkSelect);
            })


        });

        function agree(id, state, reason) {
            var title = "确定要执行该审核操作？";
            Ask2({
                Msg: title, call: function () {
                    vip.ajax({
                        url: "/admin/user/authen/pass?vid=" + id + "&reason=" + reason + "&state=" + state,
                        dataType: "json",
                        suc: function (json) {
                            Right(json.des, {callback: "reload2()"});
                        }

                    });
                }
            });

        };


        $(function () {
            $(".pimg").click(function () {
                var _this = $(this);//将当前的pimg元素作为_this传入函数
                imgShow("#outerdiv", "#innerdiv", "#bigimg", _this);
            });
        });

        function reload2() {
            Close();
            window.location.href = '/admin/user/authen';
            vip.list.reload();
        }

        function imgShow(outerdiv, innerdiv, bigimg, _this) {
            var src = _this.attr("src");//获取当前点击的pimg元素中的src属性
            $(bigimg).attr("src", src);//设置#bigimg元素的src属性

            /*获取当前点击图片的真实大小，并显示弹出层及大图*/
            $("<img/>").attr("src", src).load(function () {
                var windowW = $(window).width();//获取当前窗口宽度
                var windowH = $(window).height();//获取当前窗口高度
                var realWidth = this.width;//获取图片真实宽度
                var realHeight = this.height;//获取图片真实高度
                var imgWidth, imgHeight;
                var scale = 0.8;//缩放尺寸，当图片真实宽度和高度大于窗口宽度和高度时进行缩放

                if (realHeight > windowH * scale) {//判断图片高度
                    imgHeight = windowH * scale;//如大于窗口高度，图片高度进行缩放
                    imgWidth = imgHeight / realHeight * realWidth;//等比例缩放宽度
                    if (imgWidth > windowW * scale) {//如宽度扔大于窗口宽度
                        imgWidth = windowW * scale;//再对宽度进行缩放
                    }
                } else if (realWidth > windowW * scale) {//如图片高度合适，判断图片宽度
                    imgWidth = windowW * scale;//如大于窗口宽度，图片宽度进行缩放
                    imgHeight = imgWidth / realWidth * realHeight;//等比例缩放高度
                } else {//如果图片真实高度和宽度都符合要求，高宽不变
                    imgWidth = realWidth;
                    imgHeight = realHeight;
                }
                $(bigimg).css("width", imgWidth);//以最终的宽度对图片缩放

                var w = (windowW - imgWidth) / 2;//计算图片与窗口左边距
                var h = (windowH - imgHeight) / 2;//计算图片与窗口上边距
                $(innerdiv).css({"top": h, "left": w});//设置#innerdiv的top和left属性
                $(outerdiv).fadeIn("fast");//淡入显示#outerdiv及.pimg
            });

            $(outerdiv).click(function () {//再次点击淡出消失弹出层
                $(this).fadeOut("fast");
            });
        }


    </script>
</body>
</html>
