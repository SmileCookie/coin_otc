一、运行
----
```
npm install
npm run dev
```
二、nginx配置
----
```
server {
    listen 80;
    server_name www.common.com;
    charset UTF-8;
    location /v2 {
        root /Users/zhangguoqiang/workspace/react/demo/trade-redux/build; #替换成自己的目录
        index index.html;
	    if (!-e $request_filename){
            rewrite (.*) /v2/index.html break;
        }
        break;
    }
    location / {
        root /usr/local/var/www;
        default_type text/html;
        index index.jsp index.html index.htm;
        set $lowuri "cn$request_uri";
        proxy_set_header Host $host;
	 proxy_set_header X-Real-IP $remote_addr;
        if (!-e $request_filename){
            set $memcached_key $lowuri;
            memcached_pass memModule;
            error_page 404 = @trymemfile404;
            error_page 405 = @trymemfile404;
        }
        error_page 403 =200 @trymemfile404;
    }
    location @trymemfile404 {
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_pass http://127.0.0.1:8080;
    }
}
```