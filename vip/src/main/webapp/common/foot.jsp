<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<link rel="stylesheet" href="${static_domain }/statics/fonts/iconfont_3.css">

<style type="text/css">
	@font-face {
		font-family: "iconfont";
		src: url('/common/fonts/iconfont.eot?v=${CH_VERSON }');
		/* IE9*/
		src: url('/common/fonts/iconfont.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'),
		/* IE6-IE8 */
		url('/common/fonts/iconfont.woff?v=${CH_VERSON }') format('woff'),
		/* chrome, firefox */
		url('/common/fonts/iconfont.ttf?v=${CH_VERSON }') format('truetype'),
		/* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
		url('/common/fonts/iconfont.svg?v=${CH_VERSON }#iconfont') format('svg');
		/* iOS 4.1- */
	}

	@font-face {
		font-family: "iconfont2";
		src: url('/common/fonts/iconfont2.eot?v=${CH_VERSON }');
		/* IE9*/
		src: url('/common/fonts/iconfont2.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'),
		/* IE6-IE8 */
		url('/common/fonts/iconfont2.woff?v=${CH_VERSON }') format('woff'),
		/* chrome, firefox */
		url('/common/fonts/iconfont2.ttf?v=${CH_VERSON }') format('truetype'),
		/* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
		url('/common/fonts/iconfont2.svg?v=${CH_VERSON}#iconfont') format('svg');
		/* iOS 4.1- */
	}
	@font-face {
		font-family: "iconfont3";
		src: url('/common/fonts/iconfont_3.eot?v=${CH_VERSON }');
		/* IE9*/
		src: url('/common/fonts/iconfont_3.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'),
			/* IE6-IE8 */
		url('/common/fonts/iconfont_3.woff?v=${CH_VERSON }') format('woff'),
			/* chrome, firefox */
		url('/common/fonts/iconfont_3.ttf?v=${CH_VERSON }') format('truetype'),
			/* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
		url('/common/fonts/iconfont_3.svg?v=${CH_VERSON}#iconfont') format('svg');
		/* iOS 4.1- */
	}

	@font-face {
		font-family: 'FontAwesome';
		src: url('/common/fonts/fontawesome-webfont.eot?v=4.3.0');
		src: url('/common/fonts/fontawesome-webfont.eot?#iefix&v=4.3.0') format('embedded-opentype'),
		url('/common/fonts/fontawesome-webfont.woff2?v=4.3.0') format('woff2'),
		url('/common/fonts/fontawesome-webfont.woff?v=4.3.0') format('woff'),
		url('/common/fonts/fontawesome-webfont.ttf?v=4.3.0') format('truetype'),
		url('/common/fonts/fontawesome-webfont.svg?v=4.3.0#fontawesomeregular') format('svg');
		font-weight: normal;
		font-style: normal;
	}

	@font-face {
		font-family: 'Glyphicons Halflings';
		src: url('/common/fonts/glyphicons-halflings-regular.eot');
		src: url('/common/fonts/glyphicons-halflings-regular.eot?#iefix') format('embedded-opentype'),
		url('/common/fonts/glyphicons-halflings-regular.woff2') format('woff2'),
		url('/common/fonts/glyphicons-halflings-regular.woff') format('woff'),
		url('/common/fonts/glyphicons-halflings-regular.ttf') format('truetype'),
		url('/common/fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular') format('svg');
	}

	.navbar-nav>li.nav-right-tab>.dropdown-menu{
		border-radius:0;
		margin-top:0;
		height:auto;
		overflow: hidden;
	}
	.navbar-collapse .nav>li.logined {
		margin-top: 10px;
		margin-left: 0;
		position: relative;
	}

</style>
  <div class="bk-footer">
		<div class="bk-footer-hd">
			<div class="container">

				<div class="bk-footer-hd-right  clearfix" >
					<a class="navbar-brand  bk-footer-logo " href="${vip_domain }/">
						<img style="width: 157px;height: auto;margin-bottom: 40px;" src="${static_domain }/statics/images/winexlogo.png" alt="">
					</a>
					<ul>
							<li>
								<a href="/terms/service/">
									<i class="iconfont" style="font-size: 11px;">&#xe68b;</i>
								${L:l(lan,'服务条款') }
								</a>
							</li>
							<li>
								<a href="/bw/chargeList/leve">
									<i class="iconfont" style="font-size: 11px;">&#xe68a;</i>
									${L:l(lan,'手续费') }
								</a>
							</li>
							<li>
								<a href="/terms/termsPrivacy/">
									<i class="iconfont">&#xe688;</i>
									${L:l(lan,'隐私政策') }
								</a>
							</li>
							<li>
								<a href="/terms/relief/">
									<i class="iconfont">&#xe67a;</i>
									${L:l(lan,'免责声明') }
								</a>
							</li>
							<li>
								<a href="https://www.btcwinex.com/login/zendesk/" target="_blank">
									<i class="iconfont">&#xe689;</i>
									${L:l(lan,'帮助中心') }
								</a>
							</li>
							<li>
								<a href="https://github.com/btcwinex/btcwinex-api" target="_blank">
									<i class="iconfont3 icon-apiwendang"></i>
									API
								</a>
							</li>

						    ${"cn".equals(lan) ? '<li><a target="_blank" href="/sitemap" title="网站地图"><i class="iconfont">&#59113;</i> 网站地图</a></li>' : ''}

						</ul>
					<div class="bk-footer-hd-center" style="font-size: 14px;color: #C8CDD9;">
						<span>${L:l(lan,'商务：') }</span> business@btcwinex.com
						&nbsp;
						<span>${L:l(lan,'客服：') }</span> support@btcwinex.com
					</div>
					<div class="share_it clearfix">
						<a href="https://www.facebook.com/BtcwinexTrading/" target="_blank" class="item">
							<i class="iconfont">&#xe691;</i>
						</a>
						<a class="item qq" href="https://t.me/BtcwinexOfficial">
							<%--<div class="qrcode"></div>--%>
							<i class="iconfont">&#xe694;</i>
						</a>
						<a href="https://twitter.com/Btcwinex" target="_blank" class="item">
							<i class="iconfont icon-twitter-icon-moren"></i>
						</a>
						<!-- <a  class="item weixin">
							<div class="qrcode"></div>
							<i class="iconfont">&#xe636;</i>
						</a> -->
						<%--<a class="item qq">--%>
							<%--<div class="qrcode"></div>--%>
							<%--<i class="iconfont">&#xe694;</i>--%>
						<%--</a>--%>
						<%--<a href="${vip_domain}/v2/mg/contactUs" class="item">--%>
							<%--<i class="iconfont">&#xe692;</i>--%>
						<%--</a>--%>
						<%--<a href="http://weibo.com/HelloBitglobal" target="_blank" class="item">--%>
							<%--<i class="iconfont">&#xe695;</i>--%>
						<%--</a>--%>
					</div>

				</div>
			</div>
			<!-- <dl class="link_wp2 clearfix">
				<dt>合作伙伴：</dt>
				<dd id="lk_wp">

				</dd>
			</dl> -->
		</div>
      	<div class="bk-footer-info">
            <p>Copyright © 2019 btcwinex.</p>
		</div>
      </div>
<script type="text/javascript">

if(location.href.indexOf("/trade") != -1){
	require(["module_market","module_asset","module_trans","module_common"],function(market,asset,trans){
		market.init(2000);
		asset.init();
		trans.pageIndexInit("${market}",2000);
	});
}else if(location.href.indexOf("/entrust") != -1){
	require(["module_market","module_asset","module_trans","module_common"],function(market,asset,trans){
		market.init(2000);
		asset.init();
		// trans.pageRecordInit("${market}",5000);
		trans.getEntrustInit()
	});
}else{
	require(["module_market","module_asset","module_common"],function(market,asset){
		market.init(2000);
		asset.init();
	});
}

<%--$(function(){//废弃接口--%>
	<%--$.ajax({--%>
		<%--url:"${vip_domain }/manage/popout",--%>
		<%--type:"GET",--%>
		<%--dataType:'json',--%>
		<%--success:function(data){--%>
			<%--if(data.isSuc && data.datas != ""){--%>
				<%--draw_surprise({--%>
					<%--num:data.datas--%>
				<%--});--%>
			<%--}--%>
		<%--},--%>
		<%--error:function(err){--%>
			<%--console.log(err)--%>
		<%--}--%>
	<%--})--%>

<%--})--%>
function draw_surprise(data) { //抽奖翻倍弹窗 废弃接口
		return;
        var docW = $(document).width();
        var docH = $(document).height();
        var winW = $(window).width();
        var winH = $(window).height();
        var html = '<div class="foot_vote_alert"></div>';
        var html_home = '<div class="foot_home_box">' +
							'<div class="draw_sup"></div>'+
                            '<h3>'+data.num+' ABCDEF</h3>' +
							'<div class="text_box">${L:l(lan,'恭喜您投中新币，奖励翻倍！')}</div>'+
                            '<div class="foot_btn">' + bitbank.L("知道了") + '</div>' +
                        '</div>';
        $("body").append(html);
        $("body").append(html_home);
        $(".foot_vote_alert").css({
            "width": Math.max(docW, winW),
            "min-width": "320px",
            "height": Math.max(docH, winH),
            "z-index": 10000
        });

        $(".foot_btn").on("click",function(){
            $(".foot_vote_alert").remove();
            $(".foot_home_box").remove();
        })
 }

//vote_btn();//投票入口
function vote_btn() {
    return;
    $.ajax({   //投票入口 废弃接口
        url:"${vip_domain }/vote/activity",
        type:"GET",
        dataType:"json",
        success:function(data){
            if( data.isSuc ){
                var datas = data.datas;
                $(".vote_status").attr("state",datas.state);
				$(".vote_status").attr("activityId",datas.activityId);
                if( datas.state == 1 || datas.state == 2 || datas.state == 3 ){
                    $(".vote_status").attr("href",datas.url+"/");
                    $(".vote_status").css("display",'inline-block');
                }
                else{
                    $(".vote_status").hide();
                }
            }
            else{
                 $(".vote_status").hide();
            }

        },
        error: function (err) {
            console.log(err);
        }
    })
 }
 //抽奖入口
 //draw_fun();
 function draw_fun() {
     return; //废弃接口
	 $.ajax({
            url:"${vip_domain }/lucky/getluckyInfo",
            type:"GET",
            dataType:"json",
            success: function(data){
                if(data.isSuc){
					if( data.datas.isShow == 1 ){
						$(".draw_li").show();
					}else{
						$(".draw_li").hide();
					}
                }else{
					$(".draw_li").hide();
				}

            },
            error:function(err){
                console.log(err);
            }
        })
 }

	if(JuaBox.isMobile()){
		$(".share_it .weixin").click(function(){
			 $(".share_it .qq .qrcode").hide()
            $(".share_it .weixin .qrcode").toggle()
		})
		$(".share_it .qq").click(function(){
			$(".share_it .weixin .qrcode").hide()
            $(".share_it .qq .qrcode").toggle()
        })
	}
	else{
		$(".share_it .weixin").mouseover(function(){
            $(".share_it .weixin .qrcode").css({'display':'block'})
        })
        $(".share_it .weixin").mouseleave(function(){
            $(".share_it .weixin .qrcode").css({'display':'none'})
        })
        $(".share_it .qq").mouseover(function(){
            $(".share_it .qq .qrcode").css({'display':'block'})
        })
        $(".share_it .qq").mouseleave(function(){
            $(".share_it .qq .qrcode").css({'display':'none'})
        })
	}

</script>
<script>
		// get friend link
		(function(){
			var link_wp = $('#lk_wp'),
				str = '';
			if(window.location.pathname === '/'){
				$.ajax({
					url: '/getFriendUrl',
					type: 'GET',
					dataType: 'json',
					success: function(res){
						var result = res.datas;
						if(result.length){
							result.forEach(function(v,k){
								str += '<a href="'+v.url+'" target="_blank">'+v.name+'</a>'
							})
						}

						link_wp.html(str);
					}
				})
			} else {
				var firwp = link_wp.parent();
				firwp.prev().css({
					paddingBottom:'60px'
				});
				firwp.remove();
			}
		})();
	</script>
