<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title></title>
	<jsp:include page="/admins/top.jsp" />
	<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
	<script type="text/javascript">

        $(function(){
            vip.list.ui();
            vip.list.funcName = "成交记录管理";
            vip.list.basePath = "/admin/trade/trans/";
        });

        function reload2(){
            Close();
            vip.list.reload();
        }
        function udpateRecordStatus(id){
            var market = $("#tab").val();
            Ask2({
                Msg : "确定要更新该成交记录的状态吗？",
                call : function() {
                    ajaxUrl("/admin/trade/trans/udpateS?id="+id+"&&market="+market, "json");
                }
            });
        }

        var inAjaxing = false;
        function ajaxUrl(url, dataType){
            if(inAjaxing)
                return;

            inAjaxing = true;
            $.ajax( {
                async : true,
                cache : true,
                type : "POST",
                dataType : dataType,
                data : "",
                url : url,
                error : function(json) {
                    inAjaxing = false;
                },
                timeout : 60000,
                success : function(json) {
                    inAjaxing = false;
                    if (json.isSuc) {
                        Right(json.des, {callback : "reload2()"});
                    } else{
                        Wrong(json.des);
                    }
                }
            });
        }
	</script>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div id="formSearchContainer">
					<input type="hidden" name="tab" id="tab" value="${tab }" />
					<div class="formline">
						<p class="formCloumn">
							<span class="formText">成交时间：</span>
							<span class="formContainer">
							<span class="spacing">从</span><span class="formcon mr_5">
								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})" name="startTime" id="startTime" size="15"/></span>
							<span class="spacing">到</span>
							<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})" name="endTime" id="endTime" size="15" />
						</span>
						</p>
						<span class="formtit">成交数量：</span>
						<span class="formcon mr_5">
								<input type="text" name="minCount" id="minCount" size="10" />
							</span>
						<span class="spacing">到</span>
						<span class="formcon">
								<input type="text" name="maxCount" id="maxCount" size="10" />
							</span>

						<span class="formtit">成交单价：</span>
						<span class="formcon mr_5">
								<input type="text" name="minPrice" id="minPrice" size="10" />
							</span>
						<span class="spacing">到</span>
						<span class="formcon">
								<input type="text" name="maxPrice" id="maxPrice" size="10" />
					</span>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
						<span class="formtit">成交总额：</span>
						<span class="formcon mr_5">
							<input type="text" name="minTotalPrice" id="minTotalPrice" size="10" />
						</span>
						<span class="spacing">到</span>
						<span class="formcon">
							<input type="text" name="maxTotalPrice" id="maxTotalPrice" size="10" />
						</span>

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
								<option value="-1">取消</option>
					        </select>
					        <div class="SelectGray" id="select_24962655"><span><i style="width: 111px;">全部</i></span></div>
						</span>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
						<span class="formtit">处理状态：</span>
						<span class="formcon">
							<select name="status" id="status" style="width:100px;display: none;" selectid="select_24962646">
					        	<option value="3">全部</option>
					        	<option value="2">已成功</option>
					        	<option value="1" selected="selected">已失败</option>
					        </select>
					        <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">已失败</i></span></div>
					</span>
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
					</div>
					<div style="clear: both;"></div>
				</div>
			</form>
		</div>
		<div class="tab_head">
			<c:forEach items="${markets}" var="market">
				<a href="/admin/trade/trans?tab=${market.key}" class="${market.key == tab ? 'current' : ''}">${market.key.toUpperCase()}成交记录</a>
			</c:forEach>
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>
</body>
</html>
