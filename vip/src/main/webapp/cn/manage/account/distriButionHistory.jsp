<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>
<head>
    <jsp:include page="/common/head.jsp" />
    <title>分发记录-${WEB_NAME }-${WEB_TITLE}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
    <link rel="stylesheet" href="${static_domain }/statics/css/web.asset.css?V${CH_VERSON }">
    <style>
            .table-history thead tr th{
                padding-left:20px;
            }
            .table-history thead tr th:first-child{
                width: 224px;
            }
            .table-history thead tr th:nth-of-type(2){
                width: 160px;
            }
            .table-history thead tr th:nth-of-type(3){
                padding-left:80px;
                width: 160px;
            }
            .table-history thead tr th:nth-of-type(4){
                width: 260px;
            }
            .table-history thead tr th:last-child{
                width: 395px;
            }
            .table-history tbody tr td:nth-of-type(3){
                padding-left:80px;
            }
    </style>
   
</head>
<body>
    <div class="bk-body">
        <jsp:include page="/common/top.jsp" />
        <div class="mainer">
            <div class="container2">
                <div class="content"> 
                    <div class="record-head tit-space">
                        <h2 class="assets-title left">${L:l(lan,'分发记录')}</h2>
                    </div>
                    <div class="record-box">
                        <table class="table-history">
                            <thead>
                                <tr>
                                    <th>${L:l(lan,'日期_2')}</th>
                                    <th style="text-align:center">${L:l(lan,'类型_2')}</th>
                                    <th>${L:l(lan,'币种_2')}</th>
                                    <th style="text-align:right;padding-right:60px;">${L:l(lan,'数量_2')}</th>
                                    <th>${L:l(lan,'备注_2')}</th>
                                </tr>
                            </thead>
                            <tbody id="butionHistory">
                                
                            </tbody>
                        </table>
                        <div class="pageCon" id="butionHistory_Page"></div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="/common/foot.jsp" />
    </div>

<script type="text/javascript">
   
    require(['module_asset'],function(asset){
        asset.distriButionHistory();
    });
</script>
  <script type="text/x-tmpl" id="tmpl-butionHistory">
    {% for (var i = 0; i <= rs.length -1; i++) { %}
          <tr>
              <td>{%=rs[i].sendTime%}</td>
              <td style="text-align:center">{%=rs[i].typeView%}</td>
              <td>{%=rs[i].coinView%}</td>
              <td  style="text-align:right;padding-right:60px;">
                  {%=rs[i].amount%}			
              </td>
              <td>
                {%=rs[i].sourceRemark%}			
            </td>
          </tr>
    {% } %}
  </script>



</body>
</html>