<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.world.util.qiniu.QiNiuUtil" %>

 <%
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>照片上传</title>
    <script type="text/javascript" src="${static_domain }/statics/js/admin/webupload/js/webuploader.js"></script>
<style>
    .webuploader-element-invisible {
        position: absolute !important;
        clip: rect(1px 1px 1px 1px); /* IE6, IE7 */
        clip: rect(1px,1px,1px,1px);
    }
    .webuploader-pick {
        position: relative;
        cursor: pointer;
        padding: 10px 15px;
        text-align: center;
        border-radius: 3px;
        height: 24px;
        width:120px;
        opacity: 1;
        line-height: 24px;
    }
   
</style>
<script type="text/javascript">
    $(function() {

        var $ = jQuery;
        // retina is 2 
        var ratio = window.devicePixelRatio || 1;

        var thumbnailWidth = 100 * ratio, thumbnailHeight = 100 * ratio;

        createUploader('step2-1-one', 1,thumbnailWidth,thumbnailHeight)
        createUploader('step2-1-two', 2,thumbnailWidth,thumbnailHeight)
        createUploader('step2-1-three', 3,thumbnailWidth,thumbnailHeight)
        createUploader('step3-1-one', 4,thumbnailWidth,thumbnailHeight)
    })


    /**
     * id:click button ID
     * index: The current page which one image file upload
     * thumbnailWidth,thumbnailHeight:
     */
    function createUploader(id,index,thumbnailWidth,thumbnailHeight) {
        var elId = 'uploader' + index;
        var num = 'one';
        switch (index) {
            case 1:
                num = 'one';
                break;
            case 2:
                num = 'two';
                break;
            case 3:
                num = 'three';
                break;
            case 4:
            num = 'four';
            break;
            default:
                break;
        }

        window[elId] = WebUploader.create({

            // auto upload。
            auto : true,
            // swf path
            swf : '${static_domain }/statics/js/admin//webupload/Uploader.swf',
            // file sever。
            server : 'http://up.qiniu.com/',
            // Select the file button.optional
            // Internal, according to the current running is to create can be input element, also may be the flash.
            pick : '#' + id,
            // Only allows you to select a file, optional.
            accept : {
                title : 'Images',
                extensions : 'gif,jpg,jpeg,bmp,png',
                mimeTypes : 'image/*'
            },
            // Upload a file number
            fileNumLimit : 1,
            // Global Settings, file upload request parameter list, each send send parameters of the object.
            formData: {
                token : $("#uptoken").val()
            }
        });

        // When files added
        window[elId].on('fileQueued', function(file) {

            // Create thumbnails
            window[elId].makeThumb(file, function(error, src) {
                if (error) {
                    $img.replaceWith('<span>不能预览</span>');
                    return;
                }

                $(".pics-box ."+num+" img").eq(0).attr('src',src);
                $(".pics-box ."+num+" img").eq(0).attr('id',file.id);

            }, thumbnailWidth, thumbnailHeight);
        });

        //Local Settings, for each individual file upload request parameter Settings, every time send send parameters of the object
        //reference：https://github.com/fex-team/webuploader/issues/145
        window[elId].on('uploadBeforeSend', function( block, data, headers) {
            data.key = new Date().getTime();
        });

        // File upload success
        window[elId].on('uploadSuccess', function(file) {
            console.log('uploadSuccess')
        });

        // File upload failed
        window[elId].on('uploadError', function(file) {
            console.log('uploadError');
        });

        // Complete finished uploading, success or failure
        window[elId].on('uploadComplete', function(file) {
            //$('#' + file.id).find('.progress').remove();
        });

        window[elId].on('uploadAccept', function(file, response) {
            console.log('uploadAccept');
            console.log(JSON.parse(response._raw));
       
            var domain = $("#domain").val();
            switch (index) {
            case 1:
                $("#imgOne").val(domain+JSON.parse(response._raw).key)
                break;
            case 2:
                $("#imgTwo").val(domain+JSON.parse(response._raw).key)
                break;
            case 3:
                $("#imgThree").val(domain+JSON.parse(response._raw).key)
                break;
            case 4:
                $("#imgFour").val(domain+JSON.parse(response._raw).key)
                break;  
            default:
                break;
            }
        });

        // Until it removed from the file before uploading pictures, upload skip for the first time
        $("#"+id).on('click', function () {
            if (!WebUploader.Uploader.support()) {
                var error = "Upload control does not support your browser!Please try to upgrade the flash version or using Chrome browser engine.<a target = '_blank' href = 'http://se.360.cn'> download page </a>";
                console.log(error);
                return;
            }

            var id = $(".pics-box ."+num+" img").eq(0).attr('id');
            if (undefined != id) {
                window[elId].removeFile(window[elId].getFile(id));
            }
        });

    }

</script>
</head>
<body>
</body>
</html>