package com.twitter.spdy;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.spdy.DefaultSpdyRstStreamFrame;
import org.jboss.netty.handler.codec.spdy.SpdyRstStreamFrame;
import org.jboss.netty.handler.codec.spdy.SpdyStreamStatus;
import org.jboss.netty.handler.codec.spdy.SpdySynStreamFrame;

public class SpdyTerminator extends SimpleChannelDownstreamHandler {

    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        Object msg = e.getMessage();
        if (msg instanceof SpdySynStreamFrame) {
            SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame) msg;
            final int streamId = spdySynStreamFrame.getStreamId();
            ChannelFuture future = Channels.future(ctx.getChannel());
            Channels.write(ctx, future, spdySynStreamFrame, e.getRemoteAddress());
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        SpdyRstStreamFrame spdyRstStreamFrame =
                                new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.CANCEL);
                        Channels.write(ctx, e.getFuture(), spdyRstStreamFrame, e.getRemoteAddress());
                    } else {
                        System.out.println("WRITE FAILED");
                        future.getCause().printStackTrace();
                    }
                }
            });
        } else {
            super.writeRequested(ctx, e);
        }
    }
}
