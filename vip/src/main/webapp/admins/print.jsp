<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>账务录入</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}
.form-search .formline{float:left;}
.form-search p{float:none;}
.operation { height: 20px; line-height: 20px; text-align: left;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{width:55px;}

.form-search .formline{width: 98%;}

.tb-list2 th, .tb-list2 td {
    padding: 0;
}
.tb-list2 .hd td {
    padding: 0;
}

label.checkbox {
    margin: 3px 6px 0 7px;
}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="tab-body" id="shopslist">
			${ajaxPage }
		</div>
	</div>
</div>	

</body>
</html>
