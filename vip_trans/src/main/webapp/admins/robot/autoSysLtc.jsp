<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>莱特币系统自动</title>

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
    	  
    	  initButton("是否启动交易",
    	    		"0代表关闭 1代表启动，如果关闭，可再次恢复",
    	    		"isStartTrade",${rc.isStartTrade},0,1,1); 
      	  
    	  
    	  
    	  insertLine("大项控制"); 
    	  
    	  
    	  initButton("涨跌势",
    	    		"1-100说明涨的幅度   -1-100说明跌的幅度",
    	    		"entrustQuShi",${rc.entrustQuShi},-100,100,1);
    	  initButton("骚空距离",
  	    		"距离真实买一卖一之间的价差（系统本身不会超过买一卖一）",
  	    		"safePriceQuJian",${rc.safePriceQuJian},0,100,1);
    	  
    	  initButton("内外委托界定",
    	    		"内外委托界定，分段委托控制的",
    	    		"entrustRobotQuJian",${rc.entrustRobotQuJian},0,30,1);
    	  
    	  
    	  initButton("显示增量",
  	    		"显示的成交量增加的数量",
  	    		"baseShowAdd",${rc.baseShowAdd},0,5000000,1000);
  
    	  insertLine("AI成交");
    	  
    	  initButton("每日成交单总额",
    	    		"AI成交的部分",
    	    		"totalTransEveryDay",${rc.totalTransEveryDay},1,5000000,100);
    	  
    	  initButton("自动成交时间间隔",
  	    		"自动成交时间间隔",
  	    		"timeSpaceTM",${rc.timeSpaceTM},1,300,1);

    		  
    	  insertLine("内围买盘控制"); 	
    		
    	  
		
			
    	  
    	  //内围买盘
    	  initButton("内围买盘-数量",
    	    		"entrustRobotQuJian以下的买盘最大委托数量",
    	    		"numberTotalBuyIn",${rc.numberTotalBuyIn},0,1000000,10);
    	  initButton("内围买盘-档位",
  	    		"entrustRobotQuJian以下的买盘最大档位数",
  	    		"maxDangweiBuyIn",${rc.maxDangweiBuyIn},0,200,1);
    
    	  
    	  initButton("内围买盘-时间间隔",
  	    		"外围买盘的委托时间间隔",
  	    		"timesSpaceBuyIn",${rc.timesSpaceBuyIn},1,6000,1);
          
    	  initButton("内围买盘-分割数量",
    	    		"单次请求的最大分割数量",
    	    		"splitNumberBuyIn",${rc.splitNumberBuyIn},1,300,1);
    	  
    	  initButton("内围买盘-对齐百分比",
  	    		"越小越集中于买一",
  	    		"duiQiBaiFenBiBuyIn",${rc.duiQiBaiFenBiBuyIn},1,100,1);
    	  
    	  insertLine("内围卖盘控制"); 	
    	  
    	//内围卖盘
    	  initButton("内围卖盘-数量",
    	    		"entrustRobotQuJian以上的卖盘最大委托数量",
    	    		"numberTotalSellIn",${rc.numberTotalSellIn},0,5000,1);
  
    	  initButton("内围卖盘-最大档位",
    	    		"最多保有多少个委托单",
    	    		"maxDangweiSellIn",${rc.maxDangweiSellIn},0,200,1);
    	  
    	  initButton("内围卖盘-时间间隔",
  	    		"外围买盘的委托时间间隔",
  	    		"timesSpaceSellIn",${rc.timesSpaceSellIn},1,600,1);
          
    	  initButton("内围卖盘-分割数量",
    	    		"单次请求的最大分割数量",
    	    		"splitNumberSellIn",${rc.splitNumberSellIn},1,30,1);
    	  
    	  initButton("内围卖盘-对齐百分比",
  	    		"越小越集中于买一",
  	    		"duiQiBaiFenBiSellIn",${rc.duiQiBaiFenBiSellIn},1,100,1);
    	  
    	  insertLine("外围买盘控制"); 	
    	  
    	  
    		
    	  //外围买盘
    	  initButton("外围买盘-数量",
    	    		"entrustRobotQuJian以下的买盘最大委托数量",
    	    		"numberTotalBuyOut",${rc.numberTotalBuyOut},0,50000,1);
    	  initButton("外围买盘-档位",
  	    		"entrustRobotQuJian以下的买盘最大档位数",
  	    		"maxDangweiBuyOut",${rc.maxDangweiBuyOut},0,20000,10);
    	  initButton("外围买盘-最低价",
    	    		"不能高于当前买一价",
    	    		"lowPriceBuyOut",${rc.lowPriceBuyOut},0,5,10);
    	  
    	  initButton("外围买盘-时间间隔",
  	    		"外围买盘的委托时间间隔",
  	    		"timesSpaceBuyOut",${rc.timesSpaceBuyOut},1,600,1);
          
    	  initButton("外围买盘-分割数量",
    	    		"单次请求的最大分割数量",
    	    		"splitNumberBuyOut",${rc.splitNumberBuyOut},1,30,1);
    	  
    	  initButton("外围买盘-对齐百分比",
  	    		"越小越集中于买一",
  	    		"duiQiBaiFenBiBuyOut",${rc.duiQiBaiFenBiBuyOut},1,100,1);
    	  
    	  
    	  insertLine("外围卖盘控制"); 	
    	  
    	//外围卖盘
    	  initButton("外围卖盘-数量",
    	    		"entrustRobotQuJian以上的卖盘最大委托数量",
    	    		"numberTotalSellOut",${rc.numberTotalSellOut},0,100000,10);
    	  initButton("外围卖盘-档位",
  	    		"entrustRobotQuJian以上的卖盘最大档位数",
  	    		"maxDangweiSellOut",${rc.maxDangweiSellOut},0,1900,10);
    	  
    	  initButton("外围卖盘-最高价",
    	    		"必须高于当前卖一价",
    	    		"highPriceSellOut",${rc.highPriceSellOut},300,1000,100);
    	  
    	  initButton("外围卖盘-时间间隔",
  	    		"外围买盘的委托时间间隔",
  	    		"timesSpaceSellOut",${rc.timesSpaceSellOut},1,600,1);
          
    	  initButton("外围卖盘-分割数量",
    	    		"单次请求的最大分割数量",
    	    		"splitNumberSellOut",${rc.splitNumberSellOut},1,30,1);
    	  
    	  initButton("外围卖盘-对齐百分比",
  	    		"越小越集中于买一",
  	    		"duiQiBaiFenBiSellOut",${rc.duiQiBaiFenBiSellOut},1,100,1);
    	  
    
    	  
    	  
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
    		  url: "/admin/robot/saveautoSys-ltcdefault",
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
