<script type="text/javascript">
var curRequestUrl = "";
$(function(){
	curRequestUrl = document.location.pathname;
	if("${error?if_exists}"!=""){
		googleCode("ajax", true);
		return;
	}
});	

function ajax(mCode){
	location.href=curRequestUrl+"?mCode="+mCode;
};
</script>
