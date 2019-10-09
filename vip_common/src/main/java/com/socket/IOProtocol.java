package com.socket;

import cn.hutool.core.io.IoUtil;
import org.apache.log4j.Logger;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * <p>@Description: 传输协议</p>
 *
 * @author Sue
 * @date 2018/4/20下午2:10
 */
public class IOProtocol implements Protocol<IOMessage> {

    private final static Logger log = Logger.getLogger(IOProtocol.class.getName());

    @Override
    public IOMessage decode(ByteBuffer byteBuffer, AioSession<IOMessage> aioSession, boolean b) {
        //收到的数据组不了业务包，则返回null以告诉框架数据不够
        if (byteBuffer.remaining() < IOMessage.HEADER_LENGHT) {
            return null;
        }

        //读取消息体的长度
        int bodyLength = byteBuffer.getInt();

        //计算本次需要的数据长度
        InputStream inputStream = aioSession.getInputStream(bodyLength - IOMessage.HEADER_LENGHT);
        IOMessage ioMessage = new IOMessage();
        ioMessage.setInputStream(inputStream);

        return ioMessage;
    }

    @Override
    public ByteBuffer encode(IOMessage message, AioSession<IOMessage> aioSession) {

        try {
            byte[] value = message.getValue().getBytes();
            int bodyLen = value.length;

            int allLen = IOMessage.HEADER_LENGHT + bodyLen;

            ByteBuffer buffer = ByteBuffer.allocate(allLen);

            buffer.putInt(allLen);
            buffer.put(value);

            buffer.flip();
            return buffer;
        } catch (Exception e) {
            log.warn("[代理] 协议解码异常", e);
        }

        return null;
    }
}
