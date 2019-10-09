var ga_reg_password_common = new Array(
	"~!@#$%^&*()",
	"0987654321",
	"1234567890",
	"qwertyuiop",
	"asdfghjkl",
	"zxcvbnm",
	""
);

var g_reg_realname = "";
var g_reg_birthday = "";

function hash1inhash2(h1, h2)
{
	for (k in h1)
	{
		if (h2[k] == null)
		{
			return false;
		}
	}
	return true;
}

function passwordCheck(password)
{
	var intInPass=new Array(0,0,0,0);	//num,eng,ENG,others
	var hashInPass=new Array();
	var hashInPassCount=0;
	var hashInName=new Array();
	var hashInNameCount=0;
	
	var numInName=new Array();
	var numInNameCount=0;
	var numInPass=new Array();
	var numInPassCount=0;
	var engInName=new Array();
	var engInNameCount=0;
	var engInPass=new Array();
	var engInPassCount=0;
	var ENGInName=new Array();
	var ENGInNameCount=0;
	var ENGInPass=new Array();
	var ENGInPassCount=0;
	
	var Modes=0;

	for(i=0;i<g_reg_realname.length;i++){
		ch=g_reg_realname.charAt(i);
		cc=g_reg_realname.charCodeAt(i);
		if (hashInName[cc] == null)
		{
			hashInName[cc] = 0;
			hashInNameCount ++;
		}
		else
		{
			hashInName[cc] ++;
		}
		if(ch>='0'&&ch<='9'){//get number in name
			numInName[numInNameCount]=ch;
			numInNameCount++;
		}
		else if(ch>='a' && ch<='z'){//get eng in name
			engInName[engInNameCount]=ch;
			engInNameCount++;
		}
		else if(ch>='A' && ch<='Z'){//get ENG in name
			ENGInName[ENGInNameCount]=ch;
			ENGInNameCount++;
		}
	}
	for(i=0;i<password.length;i++){
		ch=password.charAt(i);
		cc=password.charCodeAt(i);
		if (hashInPass[cc] == null)
		{
			hashInPass[cc] = 0;
			hashInPassCount ++;
		}
		else
		{
			hashInPass[cc] ++;
		}
		if(ch>='0'&&ch<='9'){//get number in pass
			numInPass[numInPassCount]=ch;
			numInPassCount++;
			intInPass[0]++;
			Modes |= 1;
		}
		else if(ch>='a' && ch<='z'){//get eng in pass
			engInPass[engInPassCount]=ch;
			engInPassCount++;
			intInPass[1]++;
			Modes |= 2;
		}
		else if(ch>='A' && ch<='Z'){//get ENG in pass
			ENGInPass[ENGInPassCount]=ch;
			ENGInPassCount++;
			intInPass[2]++;
			Modes |= 4;
		}
		else
		{
			intInPass[3]++;
			Modes |= 8;
		}
	}

	if (hashInPassCount == 1)
	{
		return 4;
	}
	if (isBirth(g_reg_birthday, password))
	{
		return 4;
	}
	if (isTooCommon(password.toLowerCase()))
	{
		return 4;
	}
	if (hashInPassCount
		&& hashInNameCount
		&& (hash1inhash2(hashInPass, hashInName) || hash1inhash2(hashInName, hashInPass)))
	{
		return 4;
	}
	var btotal = bitTotal(Modes);
	if (password.length >= 10)
	{
		btotal++;
	}
	switch (btotal)
	{
	case 1:
		return 4;
	case 2:
		return 3;
	case 3:
		return 2;
	default:
		return 1;
	}
}

function bitTotal(num){
	var modes=0;
	for (i=0;i<4;i++){
		if (num & 1) modes++;
		num>>>=1;
	}
	return modes;
}

function isBirth(birth, password)
{
	if (birth.length != 8)
	{
		return false;
	}
	
	var birth1=birth.substring(2,0);//19
	var birth2=birth.substring(4,2);//85
	var birth3=birth.substring(6,4);//01
	var birth4=birth.substring(8,6);//02
	var birth33;
	if(birth3.substring(0,1)=="0"){
		birth33=birth3.substring(1,2);
	}
	else{
		birth33=birth3.substring(0,2);
	}
	var birth44;
	if(birth4.substring(0,1)=="0"){
		birth44=birth4.substring(1,2);
	}
	else{
		birth44=birth4.substring(0,2);
	}

	var testbirth1=birth2+birth3+birth4;
	var testbirth2=birth1+birth2+birth33+birth44;

	var yyyymm=birth1+birth2+birth3;
	var yyyydd=birth1+birth2+birth4;
	var mmyyyy=birth3+birth1+birth2;
	var ddyyyy=birth4+birth1+birth2;
	
	if(password.indexOf(birth)>-1){
		return true;
	}
	else if(password.indexOf(testbirth1)>-1){
		return true;
	}
	else if(password.indexOf(testbirth2)>-1){
		return true;
	}
	else if(password.indexOf(yyyymm)>-1){
		return true;
	}
	else if(password.indexOf(yyyydd)>-1){
		return true;
	}
	else if(password.indexOf(mmyyyy)>-1){
		return true;
	}
	else if(password.indexOf(ddyyyy)>-1){
		return true;
	}
	return false;
}

function isTooCommon(password)
{
	for (var i=0; ga_reg_password_common[i]!=""; i++){
		if(ga_reg_password_common[i].indexOf(password)>-1){
			return true;
		}
	}
	return false;
}

 
function qiangdu(){
      var pass=$("#pwd").val();
      var i=passwordCheck(pass); 
      $("#pwdLevel").val(120-i*20);
      if($("#mimaqiangdu").attr("src") != vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"){
    	  $("#mimaqiangdu").attr({"src":vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"});
      }
    }

function qiangdu2(){
  	var pass=$("#safePwd").val();
  	var i=passwordCheck(pass);
  	$("#safeLevel").val(120-i*20);
  	if($("#mimaqiangdu").attr("src") != vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"){
	  	$("#mimaqiangdu").attr({"src":vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"});
  	}
}

function checkPwd(s, id){
	var pattern = /^(?!\D+$)(?![^a-zA-Z]+$)\S{6,20}$/;
	var re = new RegExp(pattern);   
    if (re.test(s)) {
    	return true;
    }   
    return false;
}