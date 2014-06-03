package com.twitter.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.spdy.SpdyHttpHeaders;
import org.jboss.netty.util.CharsetUtil;

public class HttpReader extends SimpleChannelDownstreamHandler {

    DecoderEmbedder<HttpRequest> requestDecoder =
            new DecoderEmbedder<HttpRequest>(new HttpRequestDecoder());

    private volatile int streamId = 1;

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String string = (String) e.getMessage();
        ChannelBuffer buffer = ChannelBuffers.copiedBuffer(string, CharsetUtil.UTF_8);
        requestDecoder.offer(buffer);
        while (true) {
            HttpRequest request = requestDecoder.poll();
            if (request == null) {
                e.getFuture().setSuccess();
                break;
            }
            SpdyHttpHeaders.setStreamId(request, streamId);
            streamId += 2;
            Channels.write(ctx, Channels.future(ctx.getChannel()), request, e.getRemoteAddress());
        }
    }
}
