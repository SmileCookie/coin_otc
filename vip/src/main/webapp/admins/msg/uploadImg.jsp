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
    <link href="${static_domain }/statics/js/admin/webupload/css/webuploader.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${static_domain }/statics/js/admin/webupload/js/webuploader.js"></script>


<script type="text/javascript">
    $(function() {
        var $ = jQuery, $list = $('#fileList'),
        // 优化retina, 在retina下这个值是2
                ratio = window.devicePixelRatio || 1,

        // 缩略图大小
                thumbnailWidth = 100 * ratio, thumbnailHeight = 100 * ratio,

        // Web Uploader实例
                uploader;

        var uploadUrl;
        if (window.location.protocol === 'https:') {
            uploadUrl = 'https://up.qbox.me';
        } else {
            uploadUrl = 'http://up.qiniu.com';
        }

        // 初始化Web Uploader
        uploader = WebUploader.create({

            // 自动上传。
            auto : true,
            // swf文件路径
            swf : '${static_domain }/statics/js/admin//webupload/Uploader.swf',
            // 文件接收服务端。
            server : uploadUrl,
            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick : '#filePicker',
            // 只允许选择文件，可选。
            accept : {
                title : 'Images',
                extensions : 'gif,jpg,jpeg,bmp,png'
            },
            // 上传文件个数
            fileNumLimit : 1,
            // 全局设置, 文件上传请求的参数表，每次发送都会发送此对象中的参数。
            formData: {
                token : $("#uptoken").val()

            }
        });

        // 当有文件添加进来的时候
        uploader.on('fileQueued', function(file) {
            var $li = $('<div id="' + file.id + '" class="file-item thumbnail">'
                    + '<img>' + '<div class="info">' + file.name + '</div>'
                    + '</div>'), $img = $li.find('img');

            $list.html($li);

            // 创建缩略图
            uploader.makeThumb(file, function(error, src) {
                if (error) {
                    $img.replaceWith('<span>不能预览</span>');
                    return;
                }

                $img.attr('src', src);
            }, thumbnailWidth, thumbnailHeight);
        });

        //局部设置，给每个独立的文件上传请求参数设置，每次发送都会发送此对象中的参数。。参考：https://github.com/fex-team/webuploader/issues/145
        uploader.on('uploadBeforeSend', function( block, data, headers) {
            var imgName = new Date().getTime()
            imgName = "bitglobal/newsupload/" + imgName;
            data.key = imgName;
        });

        // 文件上传过程中创建进度条实时显示。
        uploader.on('uploadProgress', function(file, percentage) {
            var $li = $('#' + file.id), $percent = $li.find('.progress span');

            // 避免重复创建
            if (!$percent.length) {
                $percent = $('').appendTo($li)
                        .find('span');
            }

            $percent.css('width', percentage * 100 + '%');
        });

        // 文件上传成功，给item添加成功class, 用样式标记上传成功。
        uploader.on('uploadSuccess', function(file) {
            $('#' + file.id).addClass('upload-state-done');
        });

        // 文件上传失败，现实上传出错。
        uploader.on('uploadError', function(file) {
            var $li = $('#' + file.id), $error = $li.find('div.error');

            // 避免重复创建
            if (!$error.length) {
                $error = $('<div class="error"></div>').appendTo($li);
            }

            $error.text('上传失败');
        });

        // 完成上传完了，成功或者失败，先删除进度条。
        uploader.on('uploadComplete', function(file) {
            $('#' + file.id).find('.progress').remove();
        });

        uploader.on('uploadAccept', function(file, response) {
            console.log($('#' + file.id).html());
            console.log(JSON.parse(response._raw));
//            if (response.code == 1) {
//                // 通过return false来告诉组件，此文件上传有错。
//                return false;
//            }
            $("#photoA").attr("src",$("#domain1").val()+JSON.parse(response._raw).key);
            $("#photo").val($("#domain1").val()+JSON.parse(response._raw).key);
        });

        // 先从文件队列中移除之前上传的图片，第一次上传则跳过
        $("#filePicker").on('click', function () {
            if (!WebUploader.Uploader.support()) {
                var error = "上传控件不支持您的浏览器！请尝试升级flash版本或者使用Chrome引擎的浏览器。<a target='_blank' href='http://se.360.cn'>下载页面</a>";
                console.log(error);
                return;
            }

            var id = $list.find("div").attr("id");
            if (undefined != id) {
                uploader.removeFile(uploader.getFile(id));
            }
        });

    });
</script>
    <style>
        /*.webuploader-element-invisible {*/
            /*width:78px;*/
            /*height:60px;*/
            /*position: absolute !important;*/
            /*clip: rect(1px 1px 1px 1px); !* IE6, IE7 *!*/
            /*clip: rect(1px,1px,1px,1px);*/
        /*}*/
    </style>
</head>
<body>
<!--dom结构部分-->
    <div id="uploader-demo" >
        <!--用来存放item-->
        <div id="fileList" class="uploader-list" style="display:none"></div>
        <div id="filePicker">选择图片</div>
    </div>
</body>
</html>