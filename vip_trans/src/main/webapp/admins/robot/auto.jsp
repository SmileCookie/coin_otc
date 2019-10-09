<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>自动交易</title>

<link rel=stylesheet type=text/css href="${static_domain }/statics/css/jquery-ui.css" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/bootstrap.min.css" />

<script type="text/javascript">
/********************配置文件*********************/
${serijavascripparam}
var sellDao=true;
var length=5; 
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
  
</style>

</head>
<body>


 <div class="container">

      <div class="price-box">

        <form class="form-horizontal form-pricing" role="form" id="form">

        

       

          
     
        </form>
<div class="form-group">
            <div class="col-sm-12">
              <button type="submit" class="btn btn-primary btn-lg btn-block">Proceed <span class="glyphicon glyphicon-chevron-right pull-right" style="padding-right: 10px;"></span></button>
            </div>
          </div>
       
      </div>

    </div>


  

    <script>
      $(document).ready(function() {
    	  initButton("价格区间",
    		"仅仅在当前成交价格上下指定浮动区间进行委托",
    		"entrustQuJian",30,10,1000,10 );
    	  
    	  initButton("整体趋势",
    	    		"系统会根据涨跌进行判断是做多还是做空，数字越大代表力度越大",
    	    		"entrustQuShi",0,-100,100,1);
    	  
    	  initButton("最大卖单总金额",
  	    		"最大同时挂单出售的最大总额",
  	    		"entrustMaxSell",0.1,0.1,500,0.1);

    	  initButton("最大买单总金额",
    	    		"最大同时挂单买入的最大总额",
    	    		"extrustMaxBuy",0.1,0.1,500,0.1);

    	  initButton("最大买卖单数量",
  	    		"最大同时挂单出售的委托数量",
  	    		"entrustMaxDangWei",10,1,100,1);
 

    	  initButton("可出售的最大上涨",
  	    		"盈利：最大上涨多少会卖掉  针对行情上涨趋势的时候",
  	    		"maxUpToSell",10,1,100,1);
    	  initButton("可出售的最小上涨",
    	    		"盈利：最少上涨多少会卖  针对行情上涨趋势的时候",
    	    		"minUpToSell",10,1,100,1);
    	  initButton("最大下跌多少会止损卖掉",
  	    		"止损：最大下跌多少会卖",
  	    		"maxDownToSell",10,1,100,1);
    	  
    	  initButton("最大上涨取消跨度",
  	    		"最大上涨多少后还没成交的单子应该取消掉",
  	    		"maxUpPriceSpaceToCancle",10,1,100,1);

    	  
    	  initButton("最大下跌可买",
    	    		"做空盈利：最低下降多少会买入 针对行情下降趋势的时候  ",
    	    		"maxDownToBuy",10,1,100,1);
      	  initButton("最小下跌可买",
      	    		"做空盈利：最少下降多少会卖 针对行情下降趋势的时候",
      	    		"minDownToBuy",10,1,100,1);
      	  initButton("上涨止损可买",
    	    		"止损：最大上涨多少会买",
    	    		"maxUpToBuy",10,1,100,1);
      	  
      	  initButton("最大下跌取消跨度",
    	    		"最大下跌多少后还没成交的单子应该取消掉",
    	    		"maxDownPriceSpaceToCancle",10,1,100,1);


          
      
          
          
          
          
        //  private int maxTimeToTransNoMoney=0;//最大多长时间如果单子没有成交掉，即使原价也可以交易掉
  		//private int timeSpace=10;//委托最小时间间隔
  		
  	//	private long AutoId;//自动化id 0新增 1 编辑
  		
  	//	private String autoName;//用户自定义的名称
  		
  		
      

      });

      function initButton(title,message,id,value,min,max,step){
    	  $("#form").append('<div class="price-slider"> <h4 class="great">'+title+'</h4> <span>'+message+'</span>  <div class="col-sm-12"> <div id="'+id+'"></div></div> </div>');
    	  $("#"+id).slider({
              range: "min",
              animate: true,
              value:value,
              min: min,
              max: max,
              step:step,
              slide: function(event, ui) {
                update(id,ui.value); //changed
              }
          });
          update(id,value); 
      }
      //changed. now with parameter
      function update(slider,val) {

         $('#'+slider+' a').html('<label><span class="glyphicon glyphicon-chevron-left"><</span> '+val+' <span class="glyphicon glyphicon-chevron-right">></span></label>');
     }

    </script>
</body>
</html>
