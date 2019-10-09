# APP,前端开发环境配置指南



> 约定: 该环境统称为 `common环境`
> 1. 禁止APP,前端,开发直接连接test2环境
> 2. 禁止开启本地trans服务,连接test2或者APP环境的mongo,memcached
> 3. 本地开发想想连自己本机服务,需全部修改为本地连接,禁止部分连接本地,部分连接其他环境


## APP连接方式

- APP直接在程序中配置common环境的地址和端口即可
- APP想要通过web访问,可以直接请求 `www.common.com`
- APP清除本地hosts中的 `w.vip.com`,`t.vip.com`,`s.vip.com`的配置

- common环境VIP服务: `192.168.2.35:8011`
- common环境TRANS服务: `192.168.2.35:8021`

## 前端连接方式

前端更新代码后,无需启动trans服务
前端需要修改hosts和nginx.conf配置文件

### hosts文件修改:

- 清除本地hosts中的 `w.vip.com`,`t.vip.com`,`s.vip.com`的配置
- 添加一个hosts配置 `127.0.0.1 www.common.com` (本机访问VIP服务的域名解析,随便写)

### nginx.conf文件修改

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
    gzip_types text/plain  text/css application/x-javascript text/xml application/xml application/xml+rss text/javascript application/octet-stream;
    gzip_disable "MSIE [1-6].(?!.*SV1)";


    upstream memModule{
	server 192.168.2.35:11211;
	###写入你本机的memcached 地址端口
    }



	 ####主项目域名
	 server {
        listen       80;
        server_name www.common.com;
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



}
```
