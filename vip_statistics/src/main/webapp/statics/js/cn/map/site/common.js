var emailValidateSendCnt = 120;

function resetLoginForm(){
	hideMsg("login_form");
	$("#login_username").val("");
	$("#login_password").val("");
	$('#login_submit').attr('disabled', false);
}
function resetRegisterForm(){
	hideMsg("register_form");
	$("#register_username").val("");
	$("#register_password").val("");
	$("#register_fullname").val("");
	$("#register_email").val("");
	$('#register_submit').attr('disabled', false);
}
function checkLoginForm(){
	hideMsg("login_form");
	if($("#login_username").val() == ""){
        showMsg("login_form", "error", common_lang.enter_username);
		$("#login_username").focus();
		return false;
	}
	if($("#login_password").val() == ""){
        showMsg("login_form", "error", common_lang.enter_pw);
		$("#login_password").focus();
		return false;
	}
	$.ajax({
        type: "POST",
        url: context_root + "login",
        data: "userid="+$("#login_username").val()+"&password="+ $("#login_password").val(),
        contentType: "application/x-www-form-urlencoded;",
        dataType: "json",
        beforeSend: function() {
            hideMsg("login_form");
            $('#login_submit').attr('disabled', true);
        },
        error: function(data, status, errThrown){
            showMsg("login_form", "error", status);
			$('#login_submit').attr('disabled', false);
        },
        success: function(data){
 			$('#login_submit').attr('disabled', false);
           if(data.result == "success"){
				if(data.email == 1)
					hasEmail = true;
				if(data.phone == 1)
					hasPhone = true;
				if(data.qq == 1)
					hasQQ = true;
				sms_remain_cnt = data.sms_remain_cnt;
				$.ajax({
					type: "POST",
					url: context_root + "user_nav?output=html",
					contentType: "text/html",
					dataType: "html",
					success: function(data){
						$("#login_block").modal('hide');
						$("#right_side_menu").html(data);
					}
				});
				return;
            }else{
                if(data.message != null && typeof(data.message) != 'undefined'){
                    showMsg("login_form", "error", data.message);
					if(data.error_code != null && data.error_code == "EMAIL"){
						showMailResendBtnOnLogin(data.userid);
					}
				}
                else
                    showMsg("login_form", "error", "Error occurred.");
            }
       },
        statusCode: {
            404: function() {
                //alert('page not found');
            }
        }
    });
	return false;
}
function checkResetPwForm(){
	hideMsg("reset_pw_form");
	if($("#reset_pw_email").val() == ""){
        showMsg("reset_pw_form", "error", common_lang.enter_email);
		$("#reset_pw_email").focus();
		return false;
	}
	$.ajax({
        type: "POST",
        url: context_root + "reset_password",
        data: "email="+$("#reset_pw_email").val(),
        contentType: "application/x-www-form-urlencoded;",
        dataType: "json",
        beforeSend: function() {
            hideMsg("reset_pw_form");
            $('#reset_pw_submit').attr('disabled', true);
        },
        error: function(data, status, errThrown){
            showMsg("reset_pw_form", "error", status);
			$('#reset_pw_submit').attr('disabled', false);
        },
        success: function(data){
 			$('#reset_pw_submit').attr('disabled', false);
			if(data.result == "success"){
				showMsg("reset_pw_form", "success", data.message);
				$('#reset_pw_form .form-group').hide();
				$('#reset_pw_submit').hide();
				$('#reset_pw_close').removeClass("hidden");
				$('#reset_pw_close').click(function(){
					$("#reset_pw_block").modal("hide");
				});
				return;
            }else{
                if(data.message != null && typeof(data.message) != 'undefined'){
                    showMsg("reset_pw_form", "error", data.message);
				}
                else
                    showMsg("reset_pw_form", "error", "Error occurred.");
            }
		},
        statusCode: {
            404: function() {
                //alert('page not found');
            }
        }
    });
	return false;
}
function showMailResendBtnOnLogin(userid){
	var html = '<button id="email_resend" class="btn btn-success btn-sm" type="button" style="margin-left:20px;">'+common_lang.resend+'</button>';
    $("#login_form>.alert").append(html);
	$("#email_resend").click(function(){
		$.ajax({
			type: "POST",
			url: context_root + "email_validate",
			data: "userid="+userid,
			contentType: "application/x-www-form-urlencoded;",
			dataType: "json",
			beforeSend: function() {
				$("#email_resend").attr("disabled", true);
			},
			error: function(data, status, errThrown){
				$("#email_resend").attr("disabled", false);
			},
			success: function(data){
				emailValidateSendCnt = 120;
				setTimeout(countForEmailValidateSend, 1000);
		    },
			statusCode: {
				404: function() {
					//alert('page not found');
				}
			}
		});
	});
}
function countForEmailValidateSend(){
	$("#email_resend").html(emailValidateSendCnt + common_lang.second);
	emailValidateSendCnt--;
	if(emailValidateSendCnt > 0)
		setTimeout(countForEmailValidateSend, 1000);
	else{
		$("#email_resend").html(common_lang.resend);
		$("#email_resend").attr("disabled", false);
	}
}

function checkRegisterForm(){
	hideMsg("register_form");
	if($("#register_username").val() == ""){
        showMsg("register_form", "error", common_lang.enter_username);
		$("#register_username").focus();
		return false;
	}
	if($("#register_password").val() == ""){
        showMsg("register_form", "error", common_lang.enter_pw);
		$("#register_password").focus();
		return false;
	}
	if($("#register_fullname").val() == ""){
        showMsg("register_form", "error", common_lang.enter_name);
		$("#register_fullname").focus();
		return false;
	}
	if($("#register_email").val() == ""){
        showMsg("register_form", "error", common_lang.enter_email);
		$("#register_email").focus();
		return false;
	}
	var reg_username = $("#register_username").val();
	var reg_password = $("#register_password").val();
	$.ajax({
        type: "POST",
        url: context_root + "register",
        data: "userid="+reg_username+"&password="+ $("#register_password").val()+"&fullname=" +$("#register_fullname").val()+"&email="+$("#register_email").val() ,
        contentType: "application/x-www-form-urlencoded;",
        dataType: "json",
        beforeSend: function() {
            hideMsg("register_form");
            $('#register_submit').attr('disabled', true);
        },
        error: function(data, status, errThrown){
            showMsg("register_form", "error", status);
			$('#register_submit').attr('disabled', false);
        },
        success: function(data){
			$('#register_submit').attr('disabled', false);
            if(data.result == "success"){
				
				$("#register_block").modal('hide');
				$("#register_success_block").modal('show');
				return;
				
            }else{
                if(data.message != null && typeof(data.message) != 'undefined'){
					html = new Array();
					html.push('<ul>');
					for(var i=0;i<data.message.length;i++)
						html.push('<li>'+data.message[i]+'</li>');
					html.push('</ul>');
                    showMsg("register_form", "error", html.join(''));
                }else
                    showMsg("register_form", "error", "Error occurred.");
            }
       },
        statusCode: {
            404: function() {
                //alert('page not found');
            }
        }
    });
	return false;
}

function hideMsg(form_id){
    $("#"+form_id+">.alert").remove();
}
function showMsg(form_id, code, msg){
    hideMsg(form_id);
    $('#'+form_id+' .loading').remove();

    var html = "<div class='alert alert-success'><button data-dismiss='alert' class='close'>&times;</button><strong>"+common_lang.success+"!</strong> "+msg+"</div>";
    if(code != "success"){
        html = "<div class='alert alert-danger'><button data-dismiss='alert' class='close'>&times;</button><strong>"+common_lang.error+"!</strong> "+msg+"</div>";
    }
    $("#"+form_id).prepend(html);

}
$(document).ready(function(){
	$("#alert_jplayer").jPlayer({
		ready: function (event) {
			$(this).jPlayer("setMedia", {
				mp3:context_root + "mp3/alert1.mp3",
				ogg:context_root + "mp3/alert1.ogg"
			});
		},
		ended: function() { // The $.jPlayer.event.ended event
			$(this).jPlayer("play"); // Repeat the media
		},
		swfPath: "./js",
		supplied: "mp3, ogg",
		wmode: "window"
	});
	$("#go_login").click(function(){
		resetLoginForm();
		$("#register_success_block").modal('hide');
		$("#login_block").modal('show');
	});
	$("#forgot_pw_link").click(function(){
		$("#login_block").modal('hide');
		hideMsg("reset_pw_form");
		$('#reset_pw_form .form-group').show();
		$('#reset_pw_submit').show();
		$('#reset_pw_close').addClass("hidden");
		$('#reset_pw_result').hide();

		$("#reset_pw_block").modal('show');
	});
});
