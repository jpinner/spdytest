package com.twitter.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class PrintHandler extends SimpleChannelHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object msg = e.getMessage();
        System.out.println(">>>");
        if (msg instanceof ChannelBuffer) {
            ChannelBuffer buffer = (ChannelBuffer) msg;
            String hex = ChannelBuffers.hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
            System.out.println(hex);
        } else {
            System.out.println(msg);
        }
        System.out.println("---");
        System.out.flush();
        super.messageReceived(ctx, e);
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object msg = e.getMessage();
        System.out.println("<<<");
        if (msg instanceof ChannelBuffer) {
            ChannelBuffer buffer = (ChannelBuffer) msg;
            String hex = ChannelBuffers.hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
            System.out.println(hex);
        } else {
            System.out.println(msg);
        }
        System.out.println("---");
        System.out.flush();
        super.writeRequested(ctx, e);
    }
}
