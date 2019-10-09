<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>系统自动</title>

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

    	  insertLine("大项控制"); 
    	  
    	  
    	  initButton("每个区间最多投放数量",
    	    		"默认值100 ，选择范围：0 10000   100代表1btc",
    	    		"qujianMaxNum",${rc.qujianMaxNum},0,10000,1);
    	  initButton("区间填充时叠加次数",
  	    		"比如填充2650跟2670区间 买单时，在2670挂5个1-5之间的数量，形成成交一大片的感觉） 默认值5，选择范围：0-20",
  	    		"overNum",${rc.overNum},0,20,1);
    	  initButton("价格区间最小差额",
    	    		"默认值：20，选择范围1-100；20代表2元，当两个价格超过这个价格时候会委托",
    	    		"qujianDifference",${rc.qujianDifference},1,100,1);
    	  initButton("撤销区间",
  	    		"默认值：30元，大于或者小于30元的小额委托会被撤销，同时定时器也在这个区间内部进行搜索并委托",
  	    		"qujianCancel",${rc.qujianCancel},0,100,1);
    	  initButton("每次获取买卖档位",
    	    		"每次获取多秒个买卖档位进行操作",
    	    		 "dangwei",${rc.dangwei},0,50,1);
    	  initButton("委托用户Id号",
    	    		"委托用户Id号",
    	    		 "userId",${rc.userId},100000,120000,1);
    	  
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
    		  url: "/admin/robot/saveEntrustAutoSys-ethdefault",
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
