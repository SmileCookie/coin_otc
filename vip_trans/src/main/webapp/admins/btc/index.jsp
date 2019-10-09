<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>委托深度</title>
	<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>
	
	<jsp:include page="/admins/top.jsp" />
			
<!--    <script src="${static_domain }/statics/js/charts/highcharts.js"></script> -->
   	<script src="${static_domain }/statics/js/charts/highstock.js"></script>
   	<script src="${static_domain }/statics/js/charts/exporting.js"></script>
   	<script src="${static_domain }/statics/js/charts/jquerypatch.js"></script>
  	<script type="text/javascript">
	${params }
	</script>
	<script type="text/javascript">
	$(function() { 
		//使用本地时区
		Highcharts.setOptions({
			global: {
				useUTC: false
			}
		});
		getData(lastTime);
	});
	
	var actionUrl = "/admin/btc/deepth/getDeepth-"+market+"?jsoncallback=?&buy=230&sell=300&trade=1000";
	function getData(lastTime){
		$.getJSON(actionUrl+"&lastTime="+lastTime, function(json) {
			showChart(json);
		});
	}
	
	var lastTime = 0;
	function showChart(json){
		var nowTime=json.lastTime;
    	if(nowTime==lastTime) 
        	return;   
    	lastTime=nowTime;
    	
    	var datas = json.datas;
		$('#container').highcharts({
			chart : {
				type: 'area',
            	height: 550,
            	events : {
<%--					load : function() {--%>
<%--						setInterval(function(){--%>
<%--							getData(lastTime);--%>
<%--						}, 10 * 1000);--%>
<%--					}--%>
				}
            },
			credits:{
	        	enabled:false,
	        },
			title : {
				text : '委托深度'
			},
			exporting: {
				enabled: false
			},
			xAxis: {
	            labels: {
	                formatter: function() {
	                    return exchangeBiNote + this.value;
	                }
	            }
	        },
			yAxis: {
	            title: {
	                text: '委托数量'
	            },
	            labels: {
	                formatter: function() {
	                    return this.value + numberBiNote;
	                }
	            }
	        },
	        tooltip: {
		        headerFormat: '<b>{series.name}</b><br/>',
	            pointFormat: exchangeBiNote + '{point.x}: {point.y}' + numberBiNote
	            //valueSuffix: numberBiNote
	        },
	        plotOptions: {
	            area: {
	                lineWidth: 2,
	                marker: {
	                    enabled: false
	                },
	                states: {
	                    hover: {
	                        lineWidth: 2
	                    }
	                },
	                fillOpacity: 0.5
	            },
	            spline: {
	                lineWidth: 1,
	                marker: {
	                    enabled: false
	                },
	                states: {
	                    hover: {
	                        lineWidth: 1
	                    }
	                }
	            }
	        },
			series : [{
				name : '累积买单',
				/* fillColor: {
	                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                    stops: [
	                        [0, '#78b9ff'],
	                        [1, Highcharts.Color('#78b9ff').setOpacity(0).get('rgba')]
	                    ]
	            }, */
				data : datas[0],
				color : '#78b9ff'
			},{
				type: 'spline',
				name : '价格曲线',
				data : datas[1],
				color : '#9c6'
			},{
				name : '累积卖单',
				/* fillColor: {
	                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                    stops: [
	                        [0, '#c43e44'],
	                        [1, Highcharts.Color('#c43e44').setOpacity(0).get('rgba')]
	                    ]
	            }, */
				data : datas[2],
				color : '#c43e44'
			}]
		});
	}

</script>
</head>
	
<body>
	<div id="add_or_update" class="main-bd">
		<div class="tab_head" id="userTab">
			<a href="deepth?tab=btcdefault" id="btcdefault" <c:if test="${tab=='btcdefault' }">class="current"</c:if>><span>BTC委托深度</span></a>
			<a href="deepth?tab=ltcdefault" id="ltcdefault" <c:if test="${tab=='ltcdefault' }">class="current"</c:if>><span>LTC委托深度</span></a>
			<a href="deepth?tab=ethdefault" id="ethdefault" <c:if test="${tab=='ethdefault' }">class="current"</c:if>><span>ETH委托深度</span></a>
			<a href="deepth?tab=ethbtcdefault" id="ethbtcdefault" <c:if test="${tab=='ethbtcdefault' }">class="current"</c:if>><span>ETHBTC委托深度</span></a>
			<a href="deepth?tab=btqdefault" id="btqdefault" <c:if test="${tab=='btqdefault' }">class="current"</c:if>><span>BTQ委托深度</span></a>
		</div>
		
		<div id="container"></div>
	</div>
</body>
</html>
