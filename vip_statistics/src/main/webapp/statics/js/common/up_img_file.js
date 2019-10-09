function upfile(data){
    this.iuput_class = $(data.iuput_class);
    this.img_id = $(data.img_id);
    this.file_text = $(data.file_text);
    this.input_id = data.input_id;
    this.qiniu_id = data.qiniu_id;
    this.loading = data.loading;
    this.callbacks = data.callbacks;
    this.relay();
}
upfile.prototype = {
    relay:function(){
        var _this = this;
        $.ajax({
            url: DOMAIN_VIP + '/manage/auth/uploadToken',
            type: "POST",
            dataType: "json",
            success: function (data) {
                if (data.isSuc) {
                    var datar = data.datas;
                    _this.upload_funcs(datar.token,datar.host);
                }
                else {
                    console.log(data);
                }
            },
            error: function (err) {
                console.log(err);
            }
        })
        
    },
    upload_funcs: function(token,hosts) {
        var _this = this;
        var Q2 = new QiniuJsSDK();
        var uploader2 = Q2.uploader({
            runtimes: 'html5,flash,html4',    //上传模式,依次退化
            browse_button: _this.input_id,       //上传选择的点选按钮，**必需**
            uptoken: token, //若未指定uptoken_url,则必须指定 uptoken ,uptoken由其他程序生成
            unique_names: true, // 默认 false，key为文件名。若开启该选项，SDK为自动生成上传成功后的key（文件名）。
            // save_key: true,   // 默认 false。若在服务端生成uptoken的上传策略中指定了 `sava_key`，则开启，SDK会忽略对key的处理
            domain: 'http://qiniu-plupload.qiniudn.com/',   //bucket 域名，下载资源时用到，**必需**
            get_new_uptoken: false,  //设置上传文件的时候是否每次都重新获取新的token
            container: _this.qiniu_id,           //上传区域DOM ID，默认是browser_button的父元素，
            // max_file_size: '2mb',           //最大文件体积限制
            flash_swf_url: '../common/plupload/Moxie.swf',  //引入flash,相对路径
            max_retries: 1,                   //上传失败最大重试次数
            dragdrop: false,                   //开启可拖曳上传
            chunk_size: '0mb',                //分块上传时，每片的体积
            auto_start: true,                 //选择文件后自动上传，若关闭需要自己绑定事件触发上传
            multi_selection: false,
            filters: {
                max_file_size: '2mb',
                prevent_duplicates: false,
                mime_types: [
                    { title: "Image files", extensions: "jpg,jpeg,png" }, // 限定jpg,jpeg,png后缀上传
                ]
                
            },
            init: {
                'BeforeUpload': function (up, file) {
                    // 每个文件上传前,处理相关的事情
                    _this.file_text.show();
                    var img_html = '<div class="user_autn_loading"> <div id="' + _this.loading + '" class="user_autn_loading_1"></div> </div>';
                    _this.img_id.html(img_html);
                },
                'UploadProgress': function (up, file) {
                    // 每个文件上传时,处理相关的事情//进度条
                    $("#" + _this.loading).css("width", file.percent + "%");
                },
                'FileUploaded': function (up, file, info) {
                  
                },
                'Error': function (up, err, errTip) {
                    //上传出错时,处理相关的事情
                    _this.file_text.show();
                    if (err.code == -600 || err.code == -601 || err.code == -700 || err.code == -702) {
                        JuaBox.showWrong(bitbank.L("支持.jpg .jpeg .png格式照片，大小不超过2M"));
                    }
                    else {
                        JuaBox.showWrong(bitbank.L("网络访问出错，请稍后重试。"));
                    }
                },
                'UploadComplete': function () {
                    //队列文件处理完毕后,处理相关的事情
                },
                'Key': function (up, file) {
                    // 若想在前端对每个文件的key进行个性化处理，可以配置该函数
                    // 该配置必须要在 unique_names: false , save_key: false 时才生效
                    // var key = '';
                    // do something with key here
                    // return key
                }
            }
        });
        uploader2.bind('FileUploaded', function (up, file, info) {
            // 每个文件上传成功后,处理相关的事情
            var res = JSON.parse(info.response);
            var sourceLink = res.key; //获取上传成功后的文件的Url
            _this.file_text.hide();
            var img_html = '\<img src=' + hosts+sourceLink + '\>';
            _this.img_id.html(img_html);
            if (_this.callbacks) {
                _this.callbacks(sourceLink);
            }
        });
        $('#' + _this.input_id).on('click', function () {
            uploader2.start();
        });
    }
}
