<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>添加矿机</title>
		<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>
 
		<#include "/admins/top.ftl" />

			
   <script src="${static_domain }/statics/js/admin/jquery.js"></script>
<!--    <script src="${static_domain }/statics/js/charts/highcharts.js"></script> -->
   <script src="${static_domain }/statics/js/charts/highstock.js"></script>
   <script src="${static_domain }/statics/js/charts/exporting.js"></script>
   <script src="${static_domain }/statics/js/charts/jquerypatch.js"></script>
  	
		<script type="text/javascript">
	$(function() {
		//使用本地时区
		Highcharts.setOptions({
			global: {
				useUTC: false
			}
		});
		$.getJSON('/admin/log/chart/getSiteVisitChart', function(json) {
			showChart(json.datas);
		});
	});
	

	function showChart(datas) {
		$('#container').highcharts('StockChart', {
			chart : {
			
            	height: 550,
				events : {
					load : function() {
						setInterval(function(){
							$.getJSON('/admin/log/chart/getSiteVisitChart', function(json) {
								var chart = $('#container').highcharts();
       							 chart.series[0].setData(json.datas);
							});
						}, 3 * 1000);
				}}},
			rangeSelector: {
				buttons: [{
					count: 1,
					type: 'hour',
					text: '1Hr'
				}, {
					count: 6,
					type: 'hour',
					text: '6Hr'
				}, {
					type: 'all',
					text: '全部'
				}],
				inputEnabled: false,
				selected: 0
			},
			credits:{
	        	enabled:false,
	        },
			title : {
				text : '全站访问量'
			},
			
			exporting: {
				enabled: false
			},
			series : [{
				name : '全站访问量',
				type: 'area',
				fillColor: {
	                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                    stops: [
	                        [0, Highcharts.getOptions().colors[0]],
	                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
	                    ]
	                },
	            tooltip: {
					valueDecimals: 0,
					valueSuffix: '次'
				},
				data : datas
			}]
		});
		
			
	}


</script>
	</head>
	
	<body>
		<div id="add_or_update" class="main-bd">
			<div id="container">访问图表</div>
		
		</div>
	</body>
</html>
