package com.twitter.spdy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.spdy.SpdyFrameCodec;
import org.jboss.netty.handler.codec.spdy.SpdyHttpCodec;
import org.jboss.netty.handler.codec.spdy.SpdySessionHandler;
import org.jboss.netty.handler.codec.spdy.SpdyVersion;
import org.jboss.netty.handler.ssl.SslHandler;

public class SpdyClientPipelineFactory implements ChannelPipelineFactory {

    private final boolean ssl;

    public SpdyClientPipelineFactory(boolean ssl) {
        this.ssl = ssl;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        if (ssl) {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            SSLEngine engine = context.createSSLEngine();
            engine.setUseClientMode(true);
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline.addLast("hexPrinter", new PrintHandler());
        pipeline.addLast("spdyFrameCodec", new SpdyFrameCodec(SpdyVersion.SPDY_3_1));
        pipeline.addLast("spdyPrinter", new PrintHandler());
        pipeline.addLast("spdySessionHandler", new SpdySessionHandler(SpdyVersion.SPDY_3_1, false));
        pipeline.addLast("spdyTerminator", new SpdyTerminator());
        pipeline.addLast("spdyHttpCodec", new SpdyHttpCodec(SpdyVersion.SPDY_3_1, 1048576));
        pipeline.addLast("httpContentDecompressor", new HttpContentDecompressor());
        pipeline.addLast("httpPrinter", new PrintHandler());
        pipeline.addLast("httpWriter", new HttpWriter());
        pipeline.addLast("httpReader", new HttpReader());
        return pipeline;
    }
}
