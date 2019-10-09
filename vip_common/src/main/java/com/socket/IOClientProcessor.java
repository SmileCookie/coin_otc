package com.socket;

import cn.hutool.core.io.IoUtil;
import org.apache.log4j.Logger;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>@Description: 客户端处理器</p>
 *
 * @author Sue
 * @date 2018/4/20下午2:20
 */
public class IOClientProcessor implements MessageProcessor<IOMessage> {
    private final static Logger log = Logger.getLogger(IOClientProcessor.class.getName());

    private static final String SPLIT = "&&";

    private AioSession<IOMessage> session;

    static AtomicLong num = new AtomicLong(0);

    public static ExecutorService executorService;

    public static ExecutorService getExecutorService(){
        if(executorService == null){
            executorService = Executors.newFixedThreadPool(20);
        }
        return executorService;
    }

    public void execute(String str) {
        String key = str.substring(0, 50).trim();
        String val = str.substring(50);
        IOSocketCache.put(key, val);
        num.incrementAndGet();
    }

    @Override
    public void process(AioSession<IOMessage> aioSession, IOMessage message) {

        try {
            // 服务器响应
            InputStream inputStream = message.getInputStream();
            String requestVal = IoUtil.read(inputStream, "UTF8");

            // 解压缩数据
            final String unCompress = GzipUtil.uncompress(requestVal);

            // 拆分消息 &&
//            String[] valArr = split(requestVal);
//
//            if (valArr.length == 1 || valArr.length == 2) {
////                log.info(">>>>>>> 收到服务端消息：" + valArr[0]);
//
//                // 本地消息缓存:如果长度等于1，则表示只有key，value= ""
//                IOSocketCache.put(valArr[0], valArr.length == 1 ? "" : valArr[1]);
//            }

            getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    execute(unCompress);
                }
            });

            // 关闭流
            inputStream.close();
        } catch (Exception e) {
            log.error("[代理] 客户端消息处理异常", e);
        }

    }

    @Override
    public void stateEvent(AioSession<IOMessage> aioSession, StateMachineEnum stateMachineEnum, Throwable throwable) {
        switch (stateMachineEnum) {
            case NEW_SESSION:
                this.session = aioSession;
                break;
            case SESSION_CLOSED:
                new Thread(){

                    @Override
                    public void run() {
                        int reNum = 0;
                        while (true) {
                            if (IOClient.start()) {
                                break;
                            }
                            try {
                                log.info("[代理] 连接已关闭，正在进行重连, " + ++reNum);
                                Thread.sleep(5000L);
                            } catch (InterruptedException e) {
                                log.error("[代理] 重连失败");
                            }
                        }
                    }
                }.start();
                break;
            default:
                log.info("[代理] 启动监听，状态 : " + stateMachineEnum);
        }
    }

    public AioSession<IOMessage> getSession() {
        return session;
    }

    private static String[] split(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, SPLIT);
        String[] arr = new String[tokenizer.countTokens()];

        int index = 0;
        while (tokenizer.hasMoreTokens()) {
            arr[index++] = tokenizer.nextToken();
        }

        return arr;
    }

}
