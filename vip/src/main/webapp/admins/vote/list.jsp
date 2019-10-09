<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>活动管理</title>
    <jsp:include page="/admins/top.jsp" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

    <style type="text/css">

    </style>
</head>
<body >
<div class="mains">
    <div class="col-main">
        <div class="form-search">
            <form autocomplete="off" name="searchForm" id="searchContaint">
                <div id="formSearchContainer">
                    <input type="hidden" name="tab" id="tab" value="${tab }" />
                    <p>
                        <span>活动标题：</span>
                        <input id="activityName"  name="activityName" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
                    </p>

                    <p class="formCloumn">
						<span >
							活动状态：
						</span>
                        <span >
							<select id="state" name="state">
								<option value="">-请选择-</option>
								<option value="0">未开始</option>
								<option value="1">进行中</option>
								<option value="2">暂停中</option>
                                <option value="3">已结束</option>
							</select>
						</span>
                    </p>


                    <p>
                        <a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
                        <a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
                        <a id="idReset1" class="search-submit" target="_blank" href="/admin/vote/aoru">添加</a>

                    </p>
                </div>

            </form>
        </div>

        <div class="tab-body" id="shopslist">
            <jsp:include page="ajax.jsp" />
        </div>
    </div>
</div>

<script type="text/javascript">
    $(function(){
        vip.list.ui();
        vip.list.funcName = "投票管理";
        vip.list.basePath = "/admin/vote/";

    });

    function changeState(ids,state){
        console.log(typeof  ids);
        Ask2({Title:"确定执行此操作？",call:function(){

            vip.ajax({url : "/admin/vote/changeState?activityId="+ids+"&state="+state , suc : function(xml){
                Right($(xml).find("Des").text());
                //vip.list.reload();
                window.location.reload();
            }});
        }});


    };




    function reload2(){
        Close();
        vip.list.reload();

    }
</script>

</body>
</html>
