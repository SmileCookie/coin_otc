# 比特全球个人开发环境部署指南

## 1. 项目结构

项目分为8个模块 

1. `vip`                WEB项目：主要是主站业务的开发(war)
2. `vip_api`            类似于一个工具类,提供get/post调用
3. `vip_auto_api`       开发者接口(war)
4. `vip_common`         框架封装：主要是对MVC，DAO的封装，同时提供权限功能
5. `vip_entrust_api`    委托相关
6. `vip_msg`            短信邮件相关(war)
7. `vip_statics`        静态资源：js，css等
8. `vip_trans`          交易模块(war)

## 2. 项目依赖

项目运行依赖的的第三方服务：

* [x] Mysql
* [x] MongoDb
* [x] Memcached
* [x] Nginx
* [x] Tomcat
* [x] Maven
* [x] JRE


 `Mysql`,`MongoDb`,`memcached` 都安装在服务器上,开发环境无需本地安装

 `nginx` 本地必须安装

 `Tomcat` 本地可选安装,也可以通过 `jetty` 启动

 `Maven` 本地必须安装,项目用maven构建

## 3. 前期准备

### 3.1 系统约定

所有操作均基于以下约定

* [x] 操作系统：MacOS（我的版本是 `macox sierra 10.12`）
* [x] 开发工具IntelliJ IDEA（我的版本是 `IntelliJ IDEA 15`）
* [x] 不建议将项目下载到 Desktop(Nginx权限不够,无法读取到静态资源)

由于环境不同软件安装过程可能会产生意想不到的问题，不过也不必太过担心，我们有专业的团队为您保驾护航

### 3.2 JRE环境安装

TODO

### 3.3 Maven安装

TODO

### 3.4 Nginx 安装

TODO

## 4 将项目导入Idea(前端略过此步骤)

将 `messi` 项目导入Idea

## 5 为Idea配置Tomcat启动

主站和后台管理是VIP项目，部署在8080端口的Tomcat中
交易模块是VIP_TRANS项目，部署8081端口的Tomcat中

以下步骤为在idea中配置8080端口的Tomcat并运行，8081的配置同8080的配置一样

运行时，分别启动即可，便于代码Debug

### 5.1 创建WEB项目

我们这里的项目是导入的

### 5.2 配置本地Tomcat到Idea

打开Idea的 `preference` 面板(`command` + `,`) ,搜索 `Servers` 关键字找到 `Application Servers` 选项，添加本地tomcat到idea
![](media/14850487703282/14860243568123.jpg)

### 5.3 为项目创建webapp Faclet

打开Idea的 `Project Structure` 面板(`command` + `;`) ,找到 `Modules` 选项，选中main并点击上面的+图标，在下拉菜单中选择Web创建一个Facelet（很幸运，项目导入后已经有了这个Facelet，我们保持默认即可），在右边根据你的项目修改配置（）：
![](media/14850487703282/14860245792421.jpg)

### 5.4 创建Artifact

继续上一步，将选项切换到 `Artifact` （很幸运的告诉你，项目导入后，这里也无需修改，但是我还是要告诉您在条件不具备的情况下怎么去设置）

![](media/14850487703282/14860248235700.jpg)

在弹出的对话框中选择 `main` ,点击 OK 即可

### 5.5 创建运行时配置

1. 打开Run/Debug Configurations面板
![](media/14850487703282/14860249880223.jpg)

2. 点击面板左上角的 `+` 为Tomcat添加配置
![](media/14850487703282/14860250942566.jpg)

3. Server Tab根据自己的需要配置，这里我加了一个启动参数来避免日志乱码
![](media/14850487703282/14860251643783.jpg)

4. Deployment Tab需要着重配置
![](media/14850487703282/14860252333733.jpg)

5. 让Tomcat跑起来

不出意外的话，Idea面板右上角已经出现了Tocmat的图标了
![](media/14850487703282/14860253129261.jpg)

可以通过Run/Debug来启动一下，通过Console查看日志
    
## 6 启动比特全球程序

- 将 `webapp` 下的静态文件 `static` 和 `index.jsp` 复制到 `/usr/local/var/www` 下（nginx静态资源目录）或者 修改nginx静态文件server中的root替换为本机静态目录(`$/messi/vip_statistics/src/main/webapp`)


- 修改 `nginx.conf` 配置文件

    ```
    #user  www;
    worker_processes  1;
    
    #error_log  logs/error.log;
    #error_log  logs/error.log  notice;
    #error_log  logs/error.log  info;
    
    #pid        logs/nginx.pid;
    
    
    events {
        worker_connections  1024;
    }
    
    
    http {
        include       mime.types;
        default_type  application/octet-stream;
    
        #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
        #                  '$status $body_bytes_sent "$http_referer" '
        #                  '"$http_user_agent" "$http_x_forwarded_for"';
    
        #access_log  logs/access.log  main;
    
        sendfile        on;
        #tcp_nopush     on;
    
        #keepalive_timeout  0;
        keepalive_timeout  65;
    
        client_max_body_size 100m;
    
        gzip  on;
        gzip_http_version 1.0;
        gzip_comp_level 2;
        gzip_proxied any;
        gzip_min_length  1100;
        gzip_buffers 16 8k;
        gzip_types text/plain  text/css application/x-javascript text/xml   application/xml application/xml+rss text/javascript application/octet-stream;
        gzip_disable "MSIE [1-6].(?!.*SV1)";
    
    
        upstream memModule{
    	server 127.0.0.1:11211;
    	###写入你本机的memcached 地址端口
        }
    
    
    
    	###静态文件目录
    	server {
            listen       80;
            server_name s.common.com;
    
    
    		location / {

                ## 推荐将该root替换为本机静态目录(`$/messi/vip_statistics/src/main/webapp`)
    			root	/usr/local/var/www;
    			default_type       text/html;
    			index  index.jsp index.html index.htm;
    			set $lowuri "cn$request_uri";
    			proxy_set_header   Host             $host;
    			proxy_set_header   X-Real-IP        $remote_addr;
    
    			error_page         403 =200 @trymemfile404;
    		}
         }
    
    	 ####主项目域名
    	 server {
            listen       80;
            server_name www.common.com/;
    		charset UTF-8;
    
    		location / {
    			
    			root	/usr/local/var/www;
    			default_type       text/html;
    			index  index.jsp index.html index.htm;
    			set $lowuri "cn$request_uri";
    			proxy_set_header   Host             $host;
    			proxy_set_header   X-Real-IP        $remote_addr;
    			if (!-e $request_filename){
    				set $memcached_key $lowuri;
    				memcached_pass     memModule;
    				error_page         404 = @trymemfile404;
    				error_page         405 = @trymemfile404;
    			}
    			error_page         403 =200 @trymemfile404;
    		}
    
    		location @trymemfile404 {
    	             proxy_set_header   Host             $host;
    	             proxy_set_header   X-Real-IP        $remote_addr;
    	             proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for;
    	             proxy_pass http://127.0.0.1:8080;
            }
         }
         
        ####主项目域名
        server {
            listen       80;
            server_name t.common.com;
                    charset UTF-8;
    
            location / {
    
                    root    /usr/local/var/www;
                    default_type       text/html;
                    index  index.jsp index.html index.htm;
                    set $lowuri "cn$request_uri";
                    proxy_set_header   Host             $host;
                    proxy_set_header   X-Real-IP        $remote_addr;
                    if (!-e $request_filename){
                            set $memcached_key $lowuri;
                            memcached_pass     memModule;
                            error_page         404 = @trymemfile404;
                            error_page         405 = @trymemfile404;
                    }
                    error_page         403 =200 @trymemfile404;
            }
    
            location @trymemfile404 {
                 proxy_set_header   Host             $host;
                 proxy_set_header   X-Real-IP        $remote_addr;
                 proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for;
                 proxy_pass http://127.0.0.1:8081;
            }
         }
         
    }
    
    ```
    
- 修改本机hosts文件，添加配置信息

    ```
    # 比特全球配置
    127.0.0.1 s.common.com
    127.0.0.1 www.common.com
    127.0.0.1 t.common.com
    ```

- 启动Nginx，访问 [主页](http://www.common.com) 或者 [后台](http://www.common.com/ad_admin/admin_login)


## 7 前端开发启动方式

### 编译并将jar包发布到本地仓库

```
cd  $/messi

mvn clean compile install -Dmaven.test.skip=true
```

### 启动web服务

```
cd $/messi/vip

mvn -Djetty.port=8080 jetty:run
```

### 启动交易服务

```
cd $/messi/vip_trans

mvn -Djetty.port=8081 jetty:run
```

### 静态资源

静态资源需要放在nginx中,具体方式参考 **第六节nginx配置** 部分 ,只需要将静态文件server中的root替换为本机静态目录(`$/messi/vip_statistics/src/main/webapp`)即可
需要注意的是:当本地静态目录发生变化后需要修改nginx配置文件,并重新加载或者重启nginx
