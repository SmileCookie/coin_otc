<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>委托成交明细</title>
  <jsp:include page="/admins/top.jsp" />

<script type="text/javascript">
/********************配置文件*********************/
${serijavascripparam}
var entrustId=${entrustId};
</script>
<script type="text/javascript" src="${static_domain }/statics/js/common/jquery.js"></script>

<script type="text/javascript" src="${static_domain }/statics/js/admin/entrust/details.js"></script>
<style type="text/css">
.trad_box .ch_table .t1 {
    
    width: 25%;
}
.trad_box .ch_table .t5 {
    
    width: 27%;
}
.ch_table .t2{display: none;}
.pi {
    color: #FD8403;
}
.po {
    color: #4775A9;
}
</style>
</head>
<body style="background-color:#FBFAF8;">

<!--页面中部内容开始-->
<div class="h_ucenter">
 
   
      
      <div class="b_tradtab_pro clearfloat">
  
        <div class="bd">
      
    
          <div class="trad_box" id="listFirestDiv">
            <div class="trad_list" style="margin-top:0;over-flow:hidden;">
              <dl class="ch_table">
                <dt><span class="t1">成交时间</span><span class="t2">类型</span><span class="t3">成交价</span><span class="t4">数量</span><span class="t5">总额</span></dt>
               <div id="listFirest" >
             
             
              </div>
              </dl>
            </div>
          </div>
        
          </div>
        </div>
      
      
  
  </div>
<!--页面中部内容结束-->

</body>
</html>
