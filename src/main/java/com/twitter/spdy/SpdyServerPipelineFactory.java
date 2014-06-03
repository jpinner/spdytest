package com.twitter.spdy;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.spdy.SpdyFrameCodec;
import org.jboss.netty.handler.codec.spdy.SpdyHttpCodec;
import org.jboss.netty.handler.codec.spdy.SpdySessionHandler;
import org.jboss.netty.handler.codec.spdy.SpdyVersion;

public class SpdyServerPipelineFactory implements ChannelPipelineFactory {

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("hexPrinter", new PrintHandler());
        pipeline.addLast("spdyFrameCodec", new SpdyFrameCodec(SpdyVersion.SPDY_3_1));
        pipeline.addLast("spdyPrinter", new PrintHandler());
        pipeline.addLast("spdySessionHandler", new SpdySessionHandler(SpdyVersion.SPDY_3_1, true));
        pipeline.addLast("spdyHttpCodec", new SpdyHttpCodec(SpdyVersion.SPDY_3_1, 1048576));
        pipeline.addLast("echoHandler", new EchoHandler());
        return pipeline;
    }
}
