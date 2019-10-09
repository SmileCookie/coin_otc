<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>用户自定义</title>

<link rel=stylesheet type=text/css href="${static_domain }/statics/css/jquery-ui.css" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/bootstrap.min.css" />

<script type="text/javascript">
/********************配置文件*********************/
${serijavascripparam}

</script>
<script type="text/javascript" src="${static_domain }/statics/js/jquery.min.js"></script>

<script type="text/javascript" src="${static_domain }/statics/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/bootstrap.min.js"></script>


<style type="text/css">

h4.great {
  
    font-family: 微软雅黑;
      background: none repeat scroll 0 0 #ffa202;
    padding: 11px 15px;
    }
    .price-box {
   
    width: 800px;
    }
    
    .ui-slider .ui-slider-handle {
    width:171px;
    }
    
.line{
     background: none repeat scroll 0 0 #333333;
    color: #FFFFFF;
    font-family: 微软雅黑;
    font-size: 28px;
    font-weight: bold;
    height: 50px;
    line-height: 50px;
    margin: 0 0 30px;
    text-align: center;
    width: 100%;
    }
    
    .price-slider {
      margin-bottom: 40px;
    }
label {
   display: inline-block;
    font-size: 14px;
    margin-bottom: 5px;
} 
    .price-slider span {
        color: #666666;
    display: inline-block;
    font-size: 14px;
    font-weight: 200;
    margin-bottom: 20px;
    margin-left: 5px;
    margin-top: 10px;
}
      .price-slider i{
    
         color: #ffffff;
         font-size: 30px;
         height:30px;
     
      }
      
 .price-slider span.title{
     color: #333333;
    font-family: 微软雅黑;
    font-size: 26px;
    font-weight: bold;
    margin-right: 10px;   
  }
      
</style>

</head>
<body>


 <div class="container">

      <div class="price-box">

        <form class="form-horizontal form-pricing" role="form" id="form">

        

       

          
     
        </form>
<div class="form-group">
            <div class="col-sm-12">
              <button type="submit" onclick="tosubmit()" class="btn btn-primary btn-lg btn-block">Proceed <span class="glyphicon glyphicon-chevron-right pull-right" style="padding-right: 10px;"></span></button>
            </div>
          </div>
       
      </div>

    </div>


  

    <script>
      $(document).ready(function() {
    
    	  insertLine("启动开关"); 
    	  
          
    	  initButton("是否启动",
  	    		"0代表关闭 1代表启动，如果关闭，可再次恢复",
  	    		"isStart",${rc.isStart},0,1,1);
    	  
    	 
    	  
    	  
    	  insertLine("大项控制"); 
    	  
    	  
    	  initButton("涨跌势",
    	    		"1-100说明涨的幅度   -1-100说明跌的幅度",
    	    		"entrustQuShi",${rc.entrustQuShi},-100,100,1);
    	 
    	  initButton("委托安全区间",
  	    		"距离当前最新成交价的价格距离",
  	    		"safePriceQuJian",${rc.safePriceQuJian},0,1000,1);
  	 

    	  initButton("主动委托价格区间",
    	    		"主动委托的价格区间",
    	    		"entrustQuJian",${rc.entrustQuJian},50,1000,1);
    	  
    	  insertLine("委托数量控制"); 
    	  
    	  
    	  
    	  initButton("最多委托卖单百分比",
  	    		"最多委托卖单的数量占到总资产可卖的百分比",
  	    		"entrustMaxSell",${rc.entrustMaxSell},0,100,1);
    	  
    	  initButton("最多委托买单百分比",
    	    		"最多委托买单的占到总资产的百分比",
    	    		"extrustMaxBuy",${rc.extrustMaxBuy},0,100,1);
    	  
    	  
    	  initButton("最多委托档位数",
  	    		"最多委托档位数",
  	    		"entrustMaxDangWei",${rc.entrustMaxDangWei},30,1000,1);
    	  
    	  
    	  insertLine("做多相关控制"); 
    	  
    	  
    	  //针对已经买入的订单部分，上涨获利设置可以卖出，如果反向下跌一定额度需要卖出止损
    	  initButton("最大止盈上涨",
    	    		"盈利：最大上涨多少会卖掉  针对行情上涨趋势的时候",
    	    		"maxUpToSell",${rc.maxUpToSell},30,1000,1);	   
    	  initButton("最小止盈上涨",
  	    		"盈利：最少上涨多少会卖  针对行情上涨趋势的时候",
  	    		"minUpToSell",${rc.minUpToSell},30,1000,1);	   
    	  initButton("止损下跌控制",
  	    		"止损：最大下跌多少会卖",
  	    		"maxDownToSell",${rc.maxDownToSell},200,2000,1);	   
    	  initButton("追涨多少可撤单",
  	    		"最大上涨多少后还没成交的单子应该取消掉",
  	    		"maxUpPriceSpaceToCancle",${rc.maxUpPriceSpaceToCancle},100,1000,1);	   
    	  
    	  
    	  insertLine("做空相关控制"); 
    	  
    	  initButton("最大下跌多少会做空买入",
  	    		"做空盈利：最低下降多少会买入 针对行情下降趋势的时候  ",
  	    		"maxDownToBuy",${rc.maxDownToBuy},30,1000,1);	   
  	      initButton("最小下跌多少会考虑做空买入",
	    		"做空盈利：最少下降多少会卖 针对行情下降趋势的时候",
	    		"minDownToBuy",${rc.minDownToBuy},30,1000,1);	   
  	      initButton("止损上涨多少需要出手",
	    		"止损：最大上涨多少会买",
	    		"maxUpToBuy",${rc.maxUpToBuy},200,2000,1);
  	      
  	      initButton("追跌多少可撤单", 
	    		"最大下跌多少后还没成交的单子应该取消掉",
	    		"maxDownPriceSpaceToCancle",${rc.maxDownPriceSpaceToCancle},100,1000,1);	  
  	       
  	  
  	    insertLine("其他控制");
  	    
  	    initButton("委托操作的单子超过多久没有成交可以撤单", 
	    		"最大多长时间如果单子没有成交掉，即使原价也可以交易掉",
	    		"maxTimeToTransNoMoney",${rc.maxTimeToTransNoMoney},10,1000,1);
  	    
  	    initButton("委托最小时间间隔", 
	    		"委托最小时间间隔",
	    		"timeSpace",${rc.timeSpace},1,300,1);	 
    			
    				
    	  
    	  
      });
      
      
      function insertLine(txt){
    	  $("#form").append("<div class='line'>"+txt+"</div>");
      }
      function initButton(title,message,id,value,min,max,step){
    	  $("#form").append('<div class="price-slider"> <h4 class="great" style="display:none;">'+title+'</h4> <span class="msg"><span class="title">'+title+'</span>:'+message+'</span>  <div class="col-sm-12"> <div id="'+id+'"></div></div>  </div>');
    	  $("#"+id).slider({
              range: "min",
              animate: true,
              value:value,
              min: min,
              max: max,
              step:step,
              slide: function(event, ui) {
                update(id,ui.value,title); //changed
              }
          });
          update(id,value,title); 
      }
      //changed. now with parameter
      function update(slider,val,title) {

         $('#'+slider+' a.ui-slider-handle').html('<label><i ids="'+slider+'">'+val+'</i></label>');
     }
 
      function tosubmit(){
    	  
    	  var datas="";
    	  $("#form i[ids]").each(function(){
    		  datas=datas+"&"+$(this).attr("ids")+"="+$(this).text()
    	  });
    	 
    	  $.ajax({
    		  type: 'POST',
    		  url: "/admin/robot/saveAutoUser-btcdefault",
    		  data: datas.substring(1),
    		  dataType: "xml",
    		  success: function(xml){
    			  alert($(xml).find("Des").text());
    		  }
    		 
    		});
    	  
      }
    </script>
</body>
</html>
