function CurrencyFormatted(a){var c,b=parseFloat(a);return isNaN(b)&&(b=0),c="",0>b&&(c="-"),b=Math.abs(b),b=parseInt(100*(b+.005)),b/=100,s=new String(b),s.indexOf(".")<0&&(s+=".00"),s.indexOf(".")==s.length-2&&(s+="0"),s=c+s}$(function(){vip.index.init(),$("body").bind("keyup",function(a){"13"==a.keyCode&&$("#doLogins").trigger("click")})}),vip.index={init:function(){vip.user.isLogin()&&($("#login-reg").hide(),$("#enter-admin").show(),$("#myLoginUserName").text($.cookie(vip.cookiKeys.uname)),vip.user.balance())},doLogin:function(){var a=this.formToStr();null!=a&&$.getJSON(vip.vipDomain+"/user/doLogin?callback=?",a,function(a){var b=a.des;a.isSuc?Redirect(b):"验证码错误，请重新输入。"==b||b.indexOf("手机")>=0?Redirect(vip.vipDomain+"/user/login"):Alert(b)})},formToStr:function(){var a="";return $("#nick").val().length<2||$("#nick").val().length>50||"用户名/邮箱"==$("#nick").val()?($("#nick").focus(),null):(a+="&nike="+encodeURIComponent($("#nick").val()),$("#pwd").val().length<6||$("#pwd").val().length>50||"请输入密码"==$("#pwd").val()?($("#pwd").focus(),null):(a+="&pwd="+encodeURIComponent($("#pwd").val()),a+="&remember="+encodeURIComponent($("#remember").val()),a.substring(1,a.length)))},tofocus:function(a){$("#"+a).focus(),Close()},errorTo:function(a){vip.form.error=a}};