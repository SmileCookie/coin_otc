//package com.world.data.p6spy;
//
//import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
//
///**
// * p6spy 自定义输出, 可以入队列然后异步入库
// * Created by suxinjie on 2017/5/9.
// */
//public class MyStyle implements MessageFormattingStrategy {
//    private static final ThreadLocal<StringBuilder> tl = new ThreadLocal<>();
//
//    @Override
//    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
//        StringBuilder sb = tl.get();
//        if (null == sb) {
//            sb = new StringBuilder();
//            tl.set(sb);
//        }
//        sb.delete(0, sb.length());
//        sb.append("### time ").append(elapsed).append("ms | ").append(category).append(" | connection ").append(connectionId).append("|").append(prepared);
//        if (null == sql || 0 < sql.length()) {
//            sb.append('\n').append("Format SQL : ").append(sql).append(';');
//        }
//        return sb.toString();
//    }
//}
