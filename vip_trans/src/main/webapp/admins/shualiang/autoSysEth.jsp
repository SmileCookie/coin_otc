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
    	  
    	  
    	  initButton("K线类型",
    	    		"只参考 OKCOIN K线成交量数据，取值范围(分钟)：1、3、5、15、30，不在范围内默认5",
    	    		"ktype",${rc.ktype},1,30,1);
    	  initButton("默认数量",
  	    		"取不到OKCOIN成交量时，赋予一个可参考要刷的量",
  	    		"dfAmount",${rc.dfAmount},0,100000,1);
    	  initButton("刷量打折",
    	    		"如果量太大，可以折算一下，比如只要60%就够了，值等于0.6",
    	    		"rate",${rc.rate},0.1,1,0.1);
    	    		
    	    		
    	  initButton("随机波动最小值",
    	    		"从100以内挑选一个随机数最小值",
    	    		"startWave",${rc.startWave},1,100,1);
    	    		
    	  initButton("随机波动最大值",
    	    		"从100以内挑选一个随机数最大值",
    	    		"endWave",${rc.endWave},1,100,1);
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
    		  url: "/admin/shualiang/saveAutoSys-ethdefault",
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
