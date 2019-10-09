<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<div class="bk-quickMenu" style="display:none;">
  <div class="menu-warp">
		<div class="menu-bar">
		  <div class="menu-bar-top"></div>
		  <div class="menu-bar-mid">
		    <div class="menu-item menu-item-unfold">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6e0;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'展开菜单')}</dt>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-qq">
		      <div class="menu-item-ico" onclick="window.open('http://b.qq.com/webc.htm?new=0&sid=4006166611&eid=218808P8z8p8K8x8K808p&o=www.vip.com&q=7&ref='+document.location, '_blank', 'height=502,width=644,toolbar=no,scrollbars=no,menubar=no,status=no');"><i class="iconfont">&#xe6dd;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'在线客服')}</dt>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-tel">
		      <div class="menu-item-ico"><i class="iconfont">&#xe678;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>400-616-6611</dt>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-app">
		      <div class="menu-item-ico">
		        <a href="${main_domain }/mobile/app" target="_self"><i class="iconfont">&#xe6ce;</i></a>
		      </div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'APP下载')}</dt>
		          <dd><img src="${vip_domain}/service/qrcode?code=${main_domain}/mobile/download&width=120&height=120"></dd>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-line"></div>
		    <div class="menu-item menu-item-asset">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6d4;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'财务管理')}</dt>
		          <dd><a href="${vip_domain }/u/asset" target="_self">${L:l(lan,'账户资产')}</a></dd>
		          <dd><a href="${vip_domain }/u/bill" target="_self">${L:l(lan,'综合账单')}</a></dd>
		          <dd><a href="${main_domain }/sxb" target="_self">${L:l(lan,'资产凭证')}</a></dd>
		          <dd><a href="${p2p_domain }/u/loan" target="_self">${L:l(lan,'融资融币')}</a></dd>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-payin">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6d7;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'充值业务')}</dt>
		          <dd><a href="${vip_domain }/u/payin/cny" target="_self">${L:l(lan,'人民币充值')}</a></dd>
		          <dd><a href="${vip_domain }/u/payin/btc" target="_self">${L:l(lan,'比特币充值')}</a></dd>
		          <dd><a href="${vip_domain }/u/payin/ltc" target="_self">${L:l(lan,'莱特币充值')}</a></dd>
		          <dd><a href="${vip_domain }/u/payin/eth" target="_self">${L:l(lan,'ETH充值')}</a></dd>
		          <dd><a href="${vip_domain }/u/payin/etc" target="_self">${L:l(lan,'ETC充值')}</a></dd>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-payout">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6d6;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'提现业务')}</dt>
		          <dd><a href="${vip_domain }/u/payout/cny" target="_self">${L:l(lan,'人民币提现')}</a></dd>
		          <dd><a href="${vip_domain }/u/payout/btc" target="_self">${L:l(lan,'比特币提现')}</a></dd>
		          <dd><a href="${vip_domain }/u/payout/ltc" target="_self">${L:l(lan,'莱特币提现')}</a></dd>
		          <dd><a href="${vip_domain }/u/payout/eth" target="_self">${L:l(lan,'ETH提现')}</a></dd>
		          <dd><a href="${vip_domain }/u/payout/etc" target="_self">${L:l(lan,'ETC提现')}</a></dd>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-address hide">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6d5;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'钱包管理')}</dt>
		          <dd><a href="" target="_self">${L:l(lan,'银行卡账户')}</a></dd>
		          <dd><a href="" target="_self">${L:l(lan,'比特币地址')}</a></dd>
		          <dd><a href="" target="_self">${L:l(lan,'莱特币地址')}</a></dd>
		          <dd><a href="" target="_self">${L:l(lan,'以太币地址')}</a></dd>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-line"></div>
		    <div class="menu-item menu-item-trans">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6d9;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dd><a href="${trans_domain }/btc" target="_self">${L:l(lan,'BTC/CNY交易')}</a></dd>
		          <dd><a href="${trans_domain }/ltc" target="_self">${L:l(lan,'LTC/CNY交易')}</a></dd>
		          <dd><a href="${trans_domain }/eth" target="_self">${L:l(lan,'ETH/CNY交易')}</a></dd>
		          <dd><a href="${trans_domain }/etc" target="_self">${L:l(lan,'ETC/CNY交易')}</a></dd>
		          <dd><a href="${trans_domain }/ethbtc" target="_self">${L:l(lan,'ETH/BTC交易')}</a></dd>
		          <dd class="btqDisplay" style="display:none"><a href="${trans_domain }/btq" target="_self">${L:l(lan,'BTQ/BTC交易')}</a></dd>
		          <dd><a href="${main_domain }/i/developer" target="_self">${L:l(lan,'API交易')}</a></dd>
		          <dt>${L:l(lan,'专业交易')}</dt>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-onekey">
		      <div class="menu-item-ico">
		        <a href="${vip_domain }/onekey" target="_self"><i class="iconfont">&#xe6d1;</i></a>
		      </div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'一键买卖')}</dt>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-line"></div>
		    <div class="menu-item menu-item-safe">
		      <div class="menu-item-ico">
		        <a href="${vip_domain }/u/safe" target="_self"><i class="iconfont">&#xe62b;</i></a>
		      </div>
		      <div class="menu-item-open">
		        <dl>
		          <dd><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'安全信息')}</a></dd>
              <dd><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'安全策略')}</a></dd>
              <dd><a href="${vip_domain }/u/level" target="_self">${L:l(lan,'积分等级')}</a></dd>
              <dd><a href="${vip_domain }/recommend" target="_self">${L:l(lan,'推荐人管理')}</a></dd>
		          <dt>${L:l(lan,'用户中心')}</dt>
		        </dl>
		      </div>
		    </div>
		    <div class="menu-item menu-item-level hide">
		      <div class="menu-item-ico">
		        <a href="${vip_domain }/u/level" target="_self"><i class="iconfont">&#xe6cb;</i></a>
		      </div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'我的积分')}</dt>
		        </dl>
		      </div>
		    </div>
		  </div>
		  <div class="menu-bar-foot">
		    <div class="menu-item menu-item-top">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6d3;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'返回顶部')}</dt>
		        </dl>
		      </div>
		    </div>
		  </div>
		</div>
		<div class="menu-plus">
		  <div class="menu-bar-top"></div>
		  <div class="menu-item menu-item-close">
	      <div class="menu-item-ico" onclick=""><i class="iconfont">&#xe6e1;</i></div>
	      <div class="menu-item-open">
	        <dl>
	          <dt>${L:l(lan,'收起菜单')}</dt>
	        </dl>
	      </div>
	    </div>
	    <div class="menu-list">
        <dl class="unfold">
          <dt><i class="iconfont">&#xe6d4;</i> ${L:l(lan,'财务管理')}<i class="iconfont icon-swift">&#xe6df;</i></dt>
          <dd><a href="${vip_domain }/u/asset" target="_self">${L:l(lan,'账户资产')}</a></dd>
          <dd><a href="${vip_domain }/u/bill" target="_self">${L:l(lan,'综合账单')}</a></dd>
          <dd><a href="${main_domain }/sxb" target="_self">${L:l(lan,'资产凭证')}</a></dd>
          <dd><a href="${p2p_domain }/u/loan" target="_self">${L:l(lan,'融资融币')}</a></dd>
        </dl>
        <dl class="unfold">
          <dt><i class="iconfont">&#xe6d7;</i> ${L:l(lan,'充值业务')}<i class="iconfont icon-swift">&#xe6df;</i></dt>
          <dd><a href="${vip_domain }/u/payin/cny" target="_self">${L:l(lan,'人民币充值')}</a></dd>
          <dd><a href="${vip_domain }/u/payin/btc" target="_self">${L:l(lan,'比特币充值')}</a></dd>
          <dd><a href="${vip_domain }/u/payin/ltc" target="_self">${L:l(lan,'莱特币充值')}</a></dd>
          <dd><a href="${vip_domain }/u/payin/eth" target="_self">${L:l(lan,'ETH充值')}</a></dd>
          <dd><a href="${vip_domain }/u/payin/etc" target="_self">${L:l(lan,'ETC充值')}</a></dd>
        </dl>
        <dl class="unfold">
          <dt><i class="iconfont">&#xe6d6;</i> ${L:l(lan,'提现业务')}<i class="iconfont icon-swift">&#xe6df;</i></dt>
          <dd><a href="${vip_domain }/u/payout/cny" target="_self">${L:l(lan,'人民币提现')}</a></dd>
          <dd><a href="${vip_domain }/u/payout/btc" target="_self">${L:l(lan,'比特币提现')}</a></dd>
          <dd><a href="${vip_domain }/u/payout/ltc" target="_self">${L:l(lan,'莱特币提现')}</a></dd>
          <dd><a href="${vip_domain }/u/payout/eth" target="_self">${L:l(lan,'ETH提现')}</a></dd>
          <dd><a href="${vip_domain }/u/payout/etc" target="_self">${L:l(lan,'ETC提现')}</a></dd>
        </dl>
        <dl class="unfold">
          <dt><i class="iconfont">&#xe6d9;</i> ${L:l(lan,'专业交易')}<i class="iconfont icon-swift">&#xe6df;</i></dt>
          <dd><a href="${trans_domain }/btc" target="_self">${L:l(lan,'BTC/CNY交易')}</a></dd>
          <dd><a href="${trans_domain }/ltc" target="_self">${L:l(lan,'LTC/CNY交易')}</a></dd>
          <dd><a href="${trans_domain }/eth" target="_self">${L:l(lan,'ETH/CNY交易')}</a></dd>
          <dd><a href="${trans_domain }/etc" target="_self">${L:l(lan,'ETC/CNY交易')}</a></dd>
          <dd><a href="${trans_domain }/ethbtc" target="_self">${L:l(lan,'ETH/BTC交易')}</a></dd>
          <dd class="btqDisplay" style="display:none"><a href="${trans_domain }/btq" target="_self">${L:l(lan,'BTQ/CNY交易')}</a></dd>
          <dd><a href="${main_domain }/i/developer" target="_self">${L:l(lan,'API交易')}</a></dd>
          <dd><a href="${vip_domain }/onekey" target="_self">${L:l(lan,'一键买卖')}</a></dd>
        </dl>
        <dl class="unfold">
          <dt><i class="iconfont">&#xe62b;</i> ${L:l(lan,'用户中心')}<i class="iconfont icon-swift">&#xe6df;</i></dt>
          <dd><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'安全信息')}</a></dd>
          <dd><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'安全策略')}</a></dd>
          <dd><a href="${vip_domain }/u/level" target="_self">${L:l(lan,'积分等级')}</a></dd>
          <dd><a href="${vip_domain }/recommend" target="_self">${L:l(lan,'推荐人管理')}</a></dd>
        </dl>
	    </div>
	    <div class="menu-bar-foot">
		    <div class="menu-item menu-item-top">
		      <div class="menu-item-ico"><i class="iconfont">&#xe6d3;</i></div>
		      <div class="menu-item-open">
		        <dl>
		          <dt>${L:l(lan,'返回顶部')}</dt>
		        </dl>
		      </div>
		    </div>
		  </div>
		</div>
	</div>
</div>

<script type="text/javascript">
$(function(){
	if($.cookie(ZNAME+'quickMenu') == null) { 
		$.cookie(ZNAME+'quickMenu', "open, unfold, unfold, unfold, unfold, unfold", { expires: 7, path: '/', domain: DOMAIN_BASE});
	}
	
	var quickMenuState = $.cookie(ZNAME+'quickMenu').split(",");
	
	if(window.location.pathname.indexOf("/markets/") == -1 && window.location.pathname != "/" && JuaBox.isMySelf() && !JuaBox.isZoom() ){
		$('.bk-quickMenu').fadeIn().addClass(quickMenuState[0]);
		$(".menu-list dl").each(function(i,c){
			$(this).removeClass("unfold").addClass(quickMenuState[i+1]);
		});
	}
	$(".menu-list").on("click","dt",function(){
		$(this).parents("dl").toggleClass("unfold");
		quickMenuState[$(this).parents("dl").index()+1] = $(this).parents("dl").attr("class");
		$.cookie(ZNAME+'quickMenu', quickMenuState.join(","), { expires: 7, path: '/', domain: DOMAIN_BASE});
	});
	$(".menu-item-unfold, .menu-item-close").on("click",".menu-item-ico",function(){
		$('.bk-quickMenu').toggleClass('open');
		quickMenuState[0] = $('.bk-quickMenu').hasClass('open') ? "open" : "";
		$.cookie(ZNAME+'quickMenu', quickMenuState.join(","), { expires: 7, path: '/', domain: DOMAIN_BASE});
	});
	$(".menu-item-top").on("click",".menu-item-ico",function(){
		goScrollTo('html,body',0);
	});
});
</script>

