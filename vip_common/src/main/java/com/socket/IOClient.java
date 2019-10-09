package com.socket;

import com.world.config.GlobalConfig;
import org.apache.log4j.Logger;
import org.smartboot.socket.transport.AioQuickClient;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>@Description: 客户端操作</p>
 *
 * @author Sue
 * @date 2018/4/20下午2:22
 */
public class IOClient {

    private final static Logger log = Logger.getLogger(IOClient.class.getName());

    /**
     * 创建客户端业务处理器
     */
    static IOClientProcessor processor;
    /**
     * 构建客户端
     */
    static AioQuickClient aioQuickClient;

    /**
     * 启动客户端
     */
    public static boolean start() {

        if (GlobalConfig.proxyEnable) {
            try {
                if (aioQuickClient != null) {
                    aioQuickClient.shutdown();
                    log.info("[代理] socket客户端断连成功");
                }

                processor = new IOClientProcessor();
                aioQuickClient = new AioQuickClient(GlobalConfig.proxyIp, GlobalConfig.proxyPort, new IOProtocol(), processor);
                aioQuickClient.start();
                log.info("[代理] socket客户端连接成功");

                new Thread(){

                    @Override
                    public void run() {
                        while (true) {
                            try {
                                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> 发送请求：" + num.get() + ", 收到请求" + IOClientProcessor.num.get());
                                Thread.sleep(5000L);
                            }catch (Exception e) {
                                log.error("获取数据异常", e);
                            }
                        }
                    }
                }.start();

                return true;
            } catch (Exception e) {
                log.warn("[代理] socket客户端连接失败", e);
            }
        } else {
            log.info("[代理] 系统没有开启代理服务");
        }

        return false;
    }

    static AtomicLong num = new AtomicLong(0);

    /**
     * 从客户端获取数据
     * @param key
     * @return
     */
    public static String get(String key) {
        if (processor != null && processor.getSession() != null) {
            IOMessage ioMessage = new IOMessage();
            ioMessage.setValue(key);
            try {
                processor.getSession().write(ioMessage);
                num.getAndIncrement();
            } catch (IOException e) {
                log.warn("[代理] 从代理获取缓存信息失败", e);
                return "";
            }
        } else {
            log.warn("[代理] 无法获取socket连接（多次失败，请检查服务）");
        }
        return "";
    }
}
