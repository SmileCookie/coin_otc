
function getUrlParam(name)
{
  var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
  var r = window.location.search.substr(1).match(reg);  //匹配目标参数
  if (r!=null) return unescape(r[2]); return null; //返回参数值
} 
$(function() {
	    getRecordFirst(entrustId);
});

//获取未完成的列表
function getRecordFirst(entityId){
	$.getJSON(entrustUrlBase+"admin/entrust/GetDetails-"+market+"-"+entityId,  function(result) {
		 showRecord(result,0);
	 });
}


/**
 * 是否显示结果
 * @param result 结果
 * @param pageIndex 大于0代表需要显示分页
 */
function showRecord(result,pageIndex){
	var record=result[0].record;
	var res = [];
	var maxLength=record.length;

	for(var i=0;i<maxLength;i++){
		
		var unitPrice=record[i][1];
		var numbers=record[i][3];
		var completeTotalMoney=record[i][2];
		var types=record[i][4];
		var submitTime=record[i][5];
	
	
		var classT = "pi";
		var nameType="买入";
		if(types == 0){
			nameType = "卖出";
			classT = "po";
		}

		var date = new Date(submitTime);
		var nowStr = date.format("yyyy-MM-dd hh:mm:ss"); 
		

		var cla='';
		if(i%2!=0)
			cla=' class="double" ';
		var row='<dd '+cla+'><span class="t1">'+nowStr+'</span><span class="t2 '+classT+'">'+nameType+'</span>'+
		'<span class="t3">'+exchangeBiNote+''+unitPrice+'</span><span class="t4">'+numberBiNote+" "+numbers+'</span>'+
		'<span class="t5">'+completeTotalMoney+'</span>'+
		'</dd>';
		res.push(row);
		 
	}

			if(record.length<1)
				$("#listFirest").html('<dd style="text-align:center;">暂时没有相关记录！</dd>');
			else
		       $("#listFirest").html(res.join(""));

}



//格式化商品,用于显示,将基础整数位的商品格式化成需要的显示
function formatNumber(num){
    num=parseFloat(num)/numberBixNormal;
	if(numberBixNormal!=numberBixShow)//不等于就说明取整数的位数
	      return Math.floor(Math.pow(10,numberBixDian)*parseFloat(num));
	else
		 return parseFloat(num.toFixed(numberBixDian));
}
//和上面过程反向,统一格式化到最小单位
function formatNumberUse(num){
	num=parseFloat(num);
	if(numberBixNormal!=numberBixShow)//不等于就说明取整数的位数
		{
		num=Math.floor(num);
	      return Math.floor(num*Math.pow(10,numberBixDian));
		}
	else
		 return Math.floor(num*numberBixNormal);
}
//格式化金钱
function formatMoney(num){
	 num=parseFloat(num)/exchangeBixNormal;
		if(exchangeBixNormal!=exchangeBixShow)//不等于就说明取整数的位数
		      return Math.floor(Math.pow(10,exchangeBixDian)*parseFloat(num));
		else
			 return parseFloat(num.toFixed(exchangeBixDian));
}
//和上面过程反向，统一格式化到最小单位
function formatMoneyUse(num){
	num=parseFloat(num);
		if(exchangeBixNormal!=exchangeBixShow)//不等于就说明取整数的位数
			{
			num=Math.floor(num);
		      return Math.floor(num*Math.pow(10,exchangeBixDian));
			}
		else
			 return Math.floor(num*exchangeBixNormal);
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


/**
 * 跳转加载新页面
 */
function ToPage(page){
	 getRecord(page,0,0,0,0,0,0,0);
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