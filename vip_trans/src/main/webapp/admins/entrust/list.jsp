<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>BTC委托记录</title>
   <jsp:include page="/admins/top.jsp" />
   	<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
<script type="text/javascript">
${market}
</script>
<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}

.form-search .formline{float:left;}
.form-search p{float:none;}

.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
.tb-list2 .po {
    color: #4775A9;
}
.tb-list2 .pi {
    color: #D75A46;
}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="formSearchContainer">
				<div class="form-search" id="searchContainer">
					<input type="hidden" id="tab" name="tab" value="${tab }" />
					<input type="hidden" id="page" name="page" value="${page }" />
					
					<div class="formline">
						<span class="formtit">委托时间：</span> 
						<span class="spacing">从</span>
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="startTime" name="startTime" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span> 
						<span class="spacing">到</span> 
						<span class="formcon">
							<input type="text" class="inputW2 Wdate" id="endTime" name="endTime" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span>
						
						<span class="formtit">委托数量：</span> 
						<span class="formcon mr_5">
							<input type="text" name="minCount" id="minCount" size="10" />
						</span> 
						<span class="spacing">到</span> 
						<span class="formcon">
							<input type="text" name="maxCount" id="maxCount" size="10" />
						</span>
					
						<span class="formtit">委托单价：</span> 
						<span class="formcon mr_5">
							<input type="text" name="minPrice" id="minPrice" size="10" />
						</span> 
						<span class="spacing">到</span> 
						<span class="formcon">
							<input type="text" name="maxPrice" id="maxPrice" size="10" />
						</span>
					</div>

					<div class="formline">
						<span class="formtit">委托编号：</span> 
						<span class="formcon">
							<input id="userId" mytitle="委托编号要求填写一个长度小于50的字符串" name="entrustId" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span class="formtit">用户Id：</span> 
						<span class="formcon">
							<input id="userId" mytitle="用户名要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span class="formtit">委托类型：</span> 
						<span class="formcon">
							<select name="type" id="type" style="width:100px;display: none;" selectid="select_24962655">
					        	<option value="">全部</option>
					        	<option value="1">买入</option>
					        	<option value="0">卖出</option>
					        </select>
					        <div class="SelectGray" id="select_24962655"><span><i style="width: 111px;">全部</i></span></div>
						</span>

						<span class="formtit">委托状态：</span> 
						<span class="formcon">
							<select name="status" id="status" style="width:100px;display: none;" selectid="select_24962646">
					        	<option value="">全部</option>
					        	<option value="3">待成交</option>
					        	<option value="2">已成交</option>
			             		<option value="1">已取消</option>
			             		<option value="-1">计划中</option>
					        </select>
					        <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>

						<p>
							<a class="search-submit" id="idSearch" href="javascript:getList();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:resetData();">重置</a>
						</p>
					</div>
					<div style="clear: both;"></div>
				</div>
		
			</form>
		</div>
		<div class="tab_head" id="userTab">
			<c:forEach items="${markets}" var="m">
				<a href="entrust?tab=${m.key}" id="${m.key}" <c:if test="${tab==m.key }">class="current"</c:if>><span>${m.value.numberBiEn}/${m.value.exchangeBiEn}委托记录</span></a>
			</c:forEach>
		</div>
		<div class="tab-body" id="shopslist">
			<table class="tb-list2" style="width:100%;table-layout: fixed;">
				<thead>
					<tr>
						<th style="width:200px;">委托用户Id</th>
						<th>类型</th>
						<th>委托单价</th>
						<th>委托数量</th>
						<th>成交数量</th>
						<th>成交金额</th>
						<th>均价</th>
						<th>状态</th>
						<th style="width: 100px;">操作</th>
					</tr>
				</thead>
				<tbody id="rows">
					
				</tbody>
				<tfoot>
					<tr>
						<td colspan="9">
							<div class="page_nav" id="pagin">
							</div>
						 </td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
 	$("#searchContainer").Ui();
	vip.list.funcName = "委托记录";
	vip.list.basePath = "/admin/entrust/";
	
	getList();
});

function resetForm(){
	$("#formSearchContainer").each(function(){
		this.reset();
	});
}

function resetData(){
	resetForm();
	$("#searchContainer").Ui();
	getList();
}

var isRunning = false;
function getList(){
	if(isRunning)
		return;
	$("#shopslist").Loadding({OffsetXGIF:0,OffsetYGIF:0});
	var datas = FormToStr("searchContainer");
	
    isRunning=true; 
    $.getJSON(vip.list.basePath+"entrust-"+market+"?"+datas+"&jsoncallback=?",  function(result) {
    	showRecord(result, $("#page").val());
	}); 
    $("#shopslist").Loadding({IsShow:false});
}

function details(id,clas){
	var typeN = clas=="pi"?"买入":"卖出";
	vip.list.funcName = "<b>"+numberBi+"委托"+typeN+"</b>成交记录详情";
	vip.list.look({url : entrustUrlBase+"admin/entrust/details-"+market+"-"+id,height:460,width:680,title:""});
}

/**
 * 是否显示结果
 * @param result 结果
 * @param pageIndex 大于0代表需要显示分页
 */
function showRecord(result, pageIndex){
	if(result[0].count < 0){
		Wrong("获取失败");
		return;
	}
	var record=result[0].record;
	var res = [];
	var maxLength=record.length;
	if(pageIndex==0&&maxLength>10&&!sellDao)
		maxLength=10;
	for(var i=0;i<maxLength;i++){
		var entrustId=record[i][0];
		var unitPrice=record[i][1];
		var numbers=record[i][2];
		var completeNumber=record[i][3];
		var completeTotalMoney=record[i][4];
		var types=record[i][5];
		var submitTime=record[i][6];
		var status=record[i][7];
		var userId=record[i][8];
		
		var classT = "pi";
		var nameType="买入";
		if(status == -1)
			nameType = "计划买入";
		if(types == 0){
			nameType = "卖出";
			if(status == -1)
				nameType = "计划卖出";
			classT = "po";
		}
		
        var compercent = (completeNumber/numbers*100).toFixed(0);

		var cl = '';
		//if(status == 0 || status == 3 || status == -1) 
		//	cl += '<a id="cancel1_'+entrustId+'" href="javascript:cancle('+entrustId+' , '+types+',1);">取消</a>';
	    if(completeNumber>0)
		    cl += ' <a href="javascript:details(\''+entrustId+'\',\''+classT+'\');">查看</a>';

	    if(cl=='')
	    	cl='—';
		if(status==0)
			status="-";
		else if(status==3)
			status="<font style='color:gray;'>待成交</font>";
		else if(status==2)
			status="<font style='color:orange;'>已成交</font>";
		
		else if(status==1)
			status="<font style='color:red;'>已取消</font>";
		else if(status==-1)
			status="<font style='color:orange;'>计划中</font>";

		var date = new Date(submitTime);
		var nowStr = date.format("yyyy-MM-dd hh:mm:ss"); 
		
		var junjia='-';
		if(completeNumber>0)
			junjia=formatMoney((completeTotalMoney*exchangeBixNormal)/completeNumber);
	
		var comp='-';
		if(completeTotalMoney>0)
			comp=completeTotalMoney;
		var cla='';
		if(i%2!=0)
			cla=' class="double" ';
		
		var rows = "";
		rows+='<tr class="space">'+
					'<td colspan="9">'+
					'</td>'+
				'</tr>'+
				'<tr class="hd">'+
					'<td colspan="9">'+
						'<input type="checkbox" style="display:none;" value="'+entrustId+'" id="'+i+'" class="checkItem"/><label id="ck_'+i+'" class="checkbox" onclick="changeCheckBox('+i+')"></label>'+
						'<span>委托编号：'+entrustId+' </span>'+
						'<span>委托时间：'+nowStr+'</span>'+
					'</td>'+
				'</tr>'+
		
				'<tr>'+
					'<td>'+
						'<div class="pic_info">'+
							'<div class="txt">'+
								'<a href="javascript:showUser('+userId+')" style="font-weight: bold;color:green;" id="text_'+userId+'">'+userId+'</a>'+
							'</div>'+
						'</div>'+
					'</td>'+
					'<td class="'+classT+'">'+nameType+'</td>'+
					'<td>'+unitPrice+'</td>'+
					'<td>'+numbers+'</td>'+
					'<td>'+completeNumber+'</td>'+
					'<td>'+completeTotalMoney+'</td>'+
					'<td>'+junjia+'</td>'+
					'<td>'+status+'</td>'+
					'<td>'+cl+'</td>'+
				'</tr>';
			
			res.push(rows);
	}

	if(pageIndex>0){
		if(record.length<1){
			var result = '<tbody class="air-tips">'+
				'<tr>'+
					'<td colspan="9">'+
						'<p>没有符合要求的记录！</p>'+
					'</td>'+
				'</tr>'+
			'</tbody>';
			
			$("#rows").html(result);
			 $("#pagin").html('');
		}else{
		   $("#rows").html(res.join(""));
		   var rtn=GetPaper(pageIndex,result[0].count);
		   $("#pagin").html("<div class=\"con\">"+rtn+"</div>");
	  	}
	}else{
		if(record.length<1){
			$("#listFirest").html('<div class="air-tips"><p>暂时没有符合要求的记录</p></div>');
		}else{
	       $("#listFirest").html(res.join(""));
		}
	}
	isRunning = false;
}

//扩展Date的format方法
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    }
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}

//格式化金钱
function formatMoney(num){
	 num=parseFloat(num)/exchangeBixNormal;
		if(exchangeBixNormal!=exchangeBixShow)//不等于就说明取整数的位数
		      return Math.floor(Math.pow(10,exchangeBixDian)*parseFloat(num));
		else
			 return parseFloat(num.toFixed(exchangeBixDian));
}

/**
 * 跳转加载新页面
 */
function ToPage(page){
	$("#page").val(page);
	getList();
}
/**
 * 跳转加载新页面
 */
function ToPage2(){
	
	var now=parseInt($("#PagerInput").val());
	var max=$("#PagerInput").attr("maxSize");
	if(parseInt(max)<now){
		Alert("输入页码过大");
		  $("#PagerInput").val(num);
	}else
	  ToPage(now);
}

/**
 * 当前页面，最大页面
 */
function  GetPaper(curpage,numbers)
{

	var showItems=3;
	var getPages=numbers/10;
	if(numbers%10==0){
		getPages=parseInt(numbers/10);
	}else{
		getPages=parseInt(numbers/10)+1;
	}
	
	curpage = parseInt(curpage);
	getPages = parseInt(getPages);

	//初始化一个字符串缓冲区
    var rtn=[];
	/**
	 * 按钮
	 */
	//如果是第一页,定制按钮
	if(curpage == 1){
		rtn.push("<span class=\"Pbtn first\">第一页</span><span class=\"Pbtn pre\"><i>&lt;</i> 上一页</span>");
	}else{
		rtn.push("<a class=\"Pbtn first\"   href=\"javascript:ToPage(1)\">第一页</a>");
		rtn.push("<a class=\"Pbtn pre\"  href=\"javascript:ToPage("+(curpage-1)+")\"><i>&lt;</i> 上一页</a>");
	}
	/*
	 * 前面部分
	 */ 
	if(curpage>(showItems+1))
	{//如果前面是满的
		
		var start=curpage-showItems;
		if((curpage+showItems)>getPages)
		{
			start=getPages-2*showItems;
			if(start<=0)
				start=1;
		}
		else
			start=curpage-showItems;
		if(start>1)
			rtn.push("<span class=\"ellipsis\">...</span>");
		
	    for(var i=start;i<curpage;i++){
	    	rtn.push("<a href=\"javascript:ToPage("+i+")\"   class=\"num\" >"+i+"</a>");
	    }
	}
	else
	{
		 for(var i=1;i<curpage;i++){
			 rtn.push("<a href=\"javascript:ToPage("+i+")\"   class=\"num\"  >"+i+"</a>");
		  }
	}
	//中间部分
	rtn.push("<a class='num current'   >"+curpage+"</a>");
	//后面部分
	if(getPages>(curpage+showItems))
	{
		var end=showItems;
		if(curpage<=showItems)//刚开始，左边本身没有显示完全
		{
			if(getPages>(2*showItems+1))
				end=2*showItems+2;
			else
				end=getPages+1;
		}
		else
			end=curpage+showItems+1;
		for(var i=(curpage+1);i<end;i++){
			rtn.push("<a href=\"javascript:ToPage("+i+")\"    class=\"num\" >"+i+"</a>");
	       }
		if(getPages>(2*showItems+1))
			rtn.push("<span class=\"ellipsis\">...</span>");
	}
	else
	{
		for(var i=(curpage+1);i<(getPages+1);i++){
			rtn.push("<a  href=\"javascript:ToPage("+i+")\"    class=\"num\" >"+i+"</a>");
		  }
	}
	/**
	 * 最后一页
	 */ 
	//如果是最后一页
	if(curpage == getPages){
		rtn.push("<span class=\"Pbtn next\">下一页<i>&gt;</i></span>");
	}else{
		rtn.push("<a class=\"Pbtn next\"    href=\"javascript:ToPage("+(curpage+1)+")\">下一页 <i>&gt;</i></a>");
	}
	//bar.append("</div>");
	//如果现实搜索 
	 
		rtn.push("<div class=\"go_page\"><input type=\"text\" position=\"s\"　 id=\"PagerInput\" size=2 maxSize=\""+getPages+"\" mytitle=\"最多"+getPages+"页\" TitlePosition=\"Left\"  pattern=\"num()\" errmsg=\"最多"+getPages+"页\"  value=\""+curpage+"\" /><a href=\"javascript:ToPage2()\" id=\"JumpButton\" class=\"Pbtn jump\">跳转</a></div>");
	
	return rtn.join(""); 
}
</script>

</body>
</html>
