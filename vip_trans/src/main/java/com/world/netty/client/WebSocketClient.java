package com.world.netty.client;

import java.net.URI;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.world.config.GlobalConfig;
import com.world.util.string.MD5;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class WebSocketClient {


	private static String URL = GlobalConfig.getValue("NETTY_WEBSOCKET_SERVER_URL");// 服务域名
	private static String ACCESS_CODE = GlobalConfig.getValue("ACCESS_CODE");// 访问KEY
	// ("url", "ws://192.168.4.22:8580/websocket");
	protected static Logger log = Logger.getLogger(WebSocketClient.class.getName());
	private static Channel[] ch = null;
	private static EventLoopGroup[] group = null;
	private static int nums = 0; //链接服务器台数
	static{
		if (  null != URL && !URL.equals("")) {
			nums = URL.split(",").length;
			group = new EventLoopGroup[nums];
			ch =new Channel[nums];
			try {
				//初始化启动客户端链接
					connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.toString(), e);
			}
		}
	}

	public static void startServer(int num) throws Exception {
		if (URL == null || URL.equals("")) {
			log.error("netty web socket server is not config!");
			return;
		}
		if (ACCESS_CODE == null || StringUtils.isEmpty(ACCESS_CODE)) {
			log.error("ACCESS CODE is not config!");
			return;
		}
		
		String[] urls = URL.split(",");
		if(num>=0){
			serverNum(urls[num],num);
		}else{
			for(int i=0;i<urls.length;i++){
				serverNum(urls[i],i);
			}
		}
	}
	
	public static  void serverNum(final String url,final int i) throws Exception{

		URI uri = new URI(url);
		String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
		final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
		final int port;
		if (uri.getPort() == -1) {
			if ("ws".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("wss".equalsIgnoreCase(scheme)) {
				port = 443;
			} else {
				port = -1;
			}
		} else {
			port = uri.getPort();
		}

		if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
			log.info("Only WS(S) is supported.");
			return;
		}

		final boolean ssl = "wss".equalsIgnoreCase(scheme);
		final SslContext sslCtx;
		if (ssl) {
			sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} else {
			sslCtx = null;
		}

		group[i] = new NioEventLoopGroup();
		try {
			// Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08
			// or V00.
			// If you change it to V00, ping is not supported and remember to
			// change
			// HttpResponseDecoder to WebSocketHttpResponseDecoder in the
			// pipeline.
			final WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory
					.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));

			final Bootstrap b = new Bootstrap();
			b.group(group[i]).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							if (sslCtx != null) {
								p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
							}
							p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), handler);
							p.addFirst(new ChannelInboundHandlerAdapter() {
			                    @Override
			                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			                        super.channelInactive(ctx);
			                        ctx.executor().execute(new Runnable() {
	                    				@Override
	                    				public void run() {
	                    					doConnect(url, i);
	                    				}
	                        		});
			                      }
			                });
						}
					});
			
			ch[i] = b.connect(uri.getHost(), port).sync().channel();
			handler.handshakeFuture().sync();
			log.info("connect:"+host+" port:"+port);
		} catch (Exception e) {
			
			ch[i] = null;
			if (group[i] != null)
				group[i].shutdownGracefully();
			 throw e;
		}
	
	}
	
	private static void doConnect(final String host ,final int i) {
	 	
 		while(true){
 			if(ch==null || ch[i]==null ||ch[i].isActive()==false){
 				try {
	 				serverNum(host,i);
			 		log.info("ReStarted  Client　success!");
 				}catch (Exception e) {
 					try {
 						if(e instanceof java.net.ConnectException){
 							log.error("websocket 服务未启动! ");
 							Thread.sleep(10000);//socket服务器没有开启，等待10秒重新检测
 						}
 						
 					} catch (InterruptedException e1) {
 						// TODO Auto-generated catch block
 						log.error(e1.toString(), e1);
 					}
 					log.error("ReStarted  Client Failed! ");
 				}
 			}else{
 				return;
 			}
 		}
	}

	/**
	 * 资产变动用户通知
	 * @param userId 用户ID
	 * @author zhanglinbo 20160820
	 *
	 */
	public static void noticeAssetUser(String userId) {
		//log.info(System.currentTimeMillis()+" send userId:"+userId);
		if (userId != null && !userId.equals("") && ACCESS_CODE!=null) {
			String asset = UserAsset.getUserAsset(userId);
			String msg = "{\"event\":\"addChannel\",\"channel\":\"userAsset\",\"userIds\":\"" + userId
					+ "\",\"asset\":"+asset+",\"accessCode\":\"" +  getMd5AccessCode() + "\"}";
			sendMsg(msg);
		}
	}

	/**
	 * 发送用户委托记录变动通知
	 * @param userId 用户ID
	 * @deprecated
	 */
	public static void noticeRecordUser(String userId) {
		
		if (userId != null && !userId.equals("")) {
			String msg = "{\"event\":\"addChannel\",\"channel\":\"userRecord\",\"userIds\":\"" + userId
					+ "\",\"accessCode\":\"" + getMd5AccessCode() + "\"}";
			sendMsg(msg);
		}
	}
	/**
	 * 发送用户委托记录变动通知
	 * @param userId 用户ID
	 * @param record 委托记录数据
	 * @param market 市场
	 * @author zhanglinbo 20160820
	 */
	public static void noticeRecordUser(String userId,String record,String market) {
		if (userId != null && !userId.equals("")) {
			String msg = "{\"event\":\"addChannel\",\"channel\":\"userRecord\",\"userIds\":\"" + userId
					+ "\",\"market\":\""+market+"\",\"record\":{"+record+"},\"accessCode\":\"" + getMd5AccessCode() + "\"}";
			sendMsg(msg);
		}
	}
	
	/**
	 * 推送盘口数据到netty服务器
	 * @param dishData 盘口数据
	 * @param symbol 币种标识
	 * @author zhanglinbo 20160820
	 */
	public static void pushDishData(String dishData,String symbol){
		if(dishData != null && !dishData.equals("")){
			String msg = "{\"event\":\"addChannel\",\"channel\":\"pushDishAllData\",\"symbol\":\""+symbol+"\",\"dishData\":\""+dishData+"\",\"accessCode\":\"" + getMd5AccessCode() + "\"}";
			sendMsg(msg);
		}
	}
	
	
	public static void sendMsg(String msg) {
		try {
			if (msg != null && !msg.equals("")) {
				for(int i=0;i<nums;i++){
					if(ch[i]!= null && ch[i].isActive()){
						//log.info("ch["+i+"]" +ch[i].remoteAddress());
						if ("bye".equals(msg.toLowerCase())) {
							ch[i].writeAndFlush(new CloseWebSocketFrame());
							ch[i].closeFuture().sync();
	
						} else if ("ping".equals(msg.toLowerCase())) {
							WebSocketFrame frame = new PingWebSocketFrame(
									Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
							ch[i].writeAndFlush(frame);
						} else {
							WebSocketFrame frame = new TextWebSocketFrame(msg);
							ch[i].writeAndFlush(frame);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	public static String getUserIds() {
		java.util.Random r = new Random();
		String userId = "110491,2000";
		return userId;
	}

	public static void connect() {
		for(int i=0;i<nums;i++){
			if (ch ==null || ch[i]==null || ch[i].isActive() == false) {
				try {
					if (group != null && group[i] != null)
						group[i].shutdownGracefully();
						startServer(i);
				} catch (Exception e) {
					if(e instanceof java.net.ConnectException){
							final int index = i;
							new Thread(new Runnable() {//服务器启动失败，启动一个线程，隔10秒重新检测是否netty已经启动
            				@Override
            				public void run() {
            					while(true){
            			 			if(ch==null || ch[index]==null ||ch[index].isActive()==false){
            			 				try {
    										startServer(index);
    									} catch (Exception e) {
    										log.error("websocket 服务未启动 等待10秒重新检测连接! ", e);
    			 							try {
												Thread.sleep(10000);//socket服务器没有开启，等待10秒重新检测
											} catch (InterruptedException e1) {
												// TODO Auto-generated catch block
												log.error(e1.toString(), e1);
											}
    									}
            			 			}else{//连接成功，退出线程
            			 				log.info("websocket 服务连接成功! ");
            			 				return;
            			 			}
            					}
            				}
                		}).start();
					}else{
						log.error(e.toString(), e);
					}
				}
			}
		}
	}

	public static void stop() {
		for(int i=0;i<nums;i++){
		if (group[i] != null)
			group[i].shutdownGracefully();
		}
	}

	
	/**
	 * 获取访问代码的MD5加密
	 * @return
	 */
	public static String getMd5AccessCode(){
		if (ACCESS_CODE == null || StringUtils.isEmpty(ACCESS_CODE)) {
			log.error("ACCESS CODE is not config!");
			return "";
		}else{
			return MD5.toMD5(ACCESS_CODE);
		}
	}
	public static void main(String[] args) throws Exception {

		for(int i=0;i<100;i++){
			noticeAssetUser("110491");
			Thread.sleep(1000);
		}
		

	}
}
