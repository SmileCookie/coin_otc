<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>委托管理</title>
 <link href="${static_domain }/en/css/common.css" rel="stylesheet" type="text/css" />
    <link href="${static_domain }/en/css/fast.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${static_domain }/en/js/global.js"></script>
	<script type="text/javascript" src="${static_domain }/en/js/trans.js"></script>    



</head>
<body>


<!-- 主体内容 -->
<div id="middle">
      <div class="m_left">
          <div class="usermessage" id="userPayAccountInfo">
              <div class="l">专业版</div>
              <div class="r">
                   <div class="t"><span class="name">-</span><span class="z">专</span><span class="s">资产折合</span></div>
                   <div class="b">-</div>
              </div>
	          <ul>
	               <li><b class="totalRmb">-</b><span>元</span><a href="/u/pay/recharge" target="_blank">充值</a><div style="clear: both;"></div></li>
	               <li><b class="useRmb">-</b><span>元</span> <a href="/u/pay/cash" target="_blank">提现</a><div style="clear: both;"></div></li>
	               <li><b class="freezRmb">-</b><span>元</span><div style="clear: both;"></div></li>
	               <li><b class="totalBtc">-</b><span>BTC</span><a href="/u/pay/btcupload" target="_blank">输入</a><div style="clear: both;"></div></li>
	               <li><b class="useBtc">-</b><span>BTC</span><a href="/u/pay/btcdownload" target="_blank">输出</a><div style="clear: both;"></div></li>
	               <li><b class="freezBtc">-</b><span>BTC</span></li>
	          </ul>
          </div>
          <div id="trans">
              <div class="tb">
               <ul>
                     <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
                     <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="in"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="in"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="in"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="in"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="in"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="in"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div></li>
					 <li><div class="s">-</div><div class="p">-</div><div class="n">-</div><div   class="in"><span  style="width:0%"></span></div></li>
               </ul>
			  </div>
          </div>
       </div>
       <div class="m_right">
       		
       		<form autocomplete="off">
	           <div class="buy" id="buy">
	               <a class="c" >委托买入</a><a class="o2" href="javascript:changeSell()">委托卖出</a><a class="m" href="javascript:changeMan()">委托管理</a>
	               <div class="line"></div>
	                  <div class="buyInput">
	                        <div class="entrustmsg">您当前人民币余额：<span class="useRmb">￥0.00</span>  可买比特币：<span class="canBuy">฿0.00</span></div>
				               <div style="width:100%;margin-bottom: 5px;">
				               <span style="width:138px;">买入价（建议<b class="suggest" style="text-decoration: underline;cursor: pointer;" onclick="copyVal(this , '#buy .unitPrice')">0.00</b>）</span>
			                   <span style="width:150px;">数量（最大<b class="bigNum" onclick="copyVal(this , '#buy .btcNumInput')" title="点击可快速输入">0.00</b>）</span>
			                   <span style="width:130px;">需支付<!--(￥<b>5666.0</b>)--></span>
			                
			                </div>
			               <input type="text" style="width:91px;" class="unitPrice" onblur="priceBlur(1);" onclick="initVal(1)" onkeyup="xswCheck(this , 2 , 1)" name="unitPrice"/>
			               <div class="x"></div>
			               <input type="text" style="width:91px;" class="btcNumInput" name="btcNumber" onblur="numBlur(1);" onkeyup="xswCheck(this , 3 , 1)"/>
			               <div class="eq"></div>
			               <input type="text" style="width:91px;" name="realAccount" class="realAccount" onkeyup="xswCheck(this , 3 , 1)" onblur="xswCheck(this , 3 , 1)"/>
			      
			               <input type="hidden" name="isBuy" value="1">
			               <a id="buyButton" href="javascript:;" onclick="trans(1)">下买单</a>
			          </div>
	           </div>
           </form> 
			<form autocomplete="off">
            <div class="buy" id="sell" style="display:none;">
                  <a class="c sell" href="javascript:changeBuy()">委托买入</a><a class="o2 sell" >委托卖出</a><a class="m" href="javascript:changeMan()">委托管理</a>
                  <div class="line sell"></div>
                  <div class="buyInput sell" id="sellInput">
		               <div class="entrustmsg">您当前比特币余额：<span style="color:#51A551;" class="useBtc">฿0.0</span></div>
		               <div style="width:100%;   margin-bottom: 5px;">
			               <span style="width:136px;">卖出价（建议<b class="suggest" style="text-decoration: underline;cursor: pointer;" onclick="copyVal(this , '#sell .unitPrice')">0.0</b>）</span>
			               <span style="width:159px;">数量（最大<b  class="bigNum" onclick="copyVal(this , '#sell .btcNumInput')" title="点击可快速输入"></b>）</span>
			               <span style="width:122px;">可卖金额<!--(￥<b>5666.0</b>)--></span>
			              
		               </div>
		               <input type="text" style="width:91px;" class="unitPrice" onblur="priceBlur(0);" onclick="initVal(0)" onkeyup="xswCheck(this , 2 , 0)" name="unitPrice"/>
		               <div class="x"></div>
		               <input type="text" style="width:91px;" class="btcNumInput" name="btcNumber" onblur="numBlur(0);" onkeyup="xswCheck(this , 3 , 0)"/>
		               <div class="eq"></div>
		               <input type="text" style="width:91px;" name="realAccount" class="realAccount" onkeyup="xswCheck(this , 3 , 2)"/>
		                <input type="hidden" name="isBuy" value="0">
		               <a id="sellButton" href="javascript:;" onclick="trans(0)">下单卖</a>
		         </div>
           </div>
           </form>
           
           <input type="hidden" id="useSafe" value="${useSafe?1:0}"/>
           
             <div class="buy" id="manage" style="display:none;">
                <a class="c sell" href="javascript:changeBuy()">委托买入</a><a class="o2" href="javascript:changeSell()">委托卖出</a><a class="m sell">委托管理</a>
                <span id="showAll" style="float: right;margin: 9px 0;"><a target="_blank" style="color:#666666;" href="/u/transaction/entrustdeatils">详细记录&gt;&gt;</a></span>
                
               <div class="line manage"></div>
                       
 			<div class="mylist" id="mylist" >
     			<div id="manHeader"><div id="selectType">单价</div><div id="selectStatus">部分</div></div>
     			<div class="tb">
     				<ul><div class="noRecord">您暂时还没有任何交易记录</div></ul>
     			</div>
            </div>
                      
        <div class="page_nav" id="page_navA">
			
		</div>
      </div>

           <div id="showAll" style="display:none;"><a href="/u/transaction/EntrustBuyDeatils" style="color:#666666;" target="_blank">查看完整记录>></a></div>
           <div class="mylist" id="showList" style="margin-top:10px;float:left;">
           <div id="manHeader"><div id="selectType">单价</div><div id="selectStatus">部分</div></div>
                 <div class="tb">
	                <ul><div class="noRecord">您暂时还没有任何交易记录</div></ul>
                 </div>
           </div>

           <div id="buyin"  >
           	<div class="tb" >
               <ul>
                   <li><div class="s">1</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">2</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">3</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">4</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">5</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">6</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">7</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">8</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">9</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">10</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">11</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">12</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">13</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">14</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">15</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">16</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">17</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">18</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">19</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">20</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
               </ul>
			</div>
           </div>
           <div id="sellout" >
              <div class="tb">
               <ul>
                   <li><div class="s">1</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">2</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">3</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">4</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">5</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">6</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">7</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">8</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">9</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">10</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">11</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">12</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">13</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">14</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">15</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">16</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">17</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>

                   <li><div class="s">18</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">19</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
                   <li><div class="s">20</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>
               </ul>
			  </div>

           </div>

       </div>
      
      

  </div>
<!-- 主体内容结束 -->
<script type="text/javascript">dsc.user.exeTickerJson();</script>
<!-- 底部 -->
	
</body>
</html>