<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>币库查看</title>
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
                        <span>选项名称：</span>
                        <input id="coinName"  name="coinName" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
                    </p>

                    <p class="formCloumn">
						<span >
							国际化：
						</span>
                        <span >
							<select id="lan" name="lan">
								<option value="cn">简体中文</option>
								<option value="hk">繁体中文</option>
								<option value="en">English</option>
							</select>
						</span>
                    </p>


                    <p>
                        <a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
                        <a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>

                    </p>
                </div>

            </form>
        </div>

        <div class="tab-body" id="shopslist">
            <jsp:include page="coinAjax.jsp" />
        </div>
    </div>
</div>

<script type="text/javascript">
    $(function(){
        vip.list.ui();
        vip.list.funcName = "币库查看";
        vip.list.basePath = "/admin/vote/coin/";

    });


    function reload2(){
        Close();
        vip.list.reload();

    }
</script>

</body>
</html>