package com.twitter.spdy;

import java.util.List;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

public class HttpWriter extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpResponse response = (HttpResponse) e.getMessage();
        System.out.println(response.getProtocolVersion() + " " + response.getStatus());
        for (String name : response.headers().names()) {
            List<String> values = response.headers().getAll(name);
            if (values.size() == 0) {
                return;
            }
            System.out.print(name + ": ");
            int i = 0;
            for (; i < values.size() - 1; i++) {
                System.out.print(values.get(i) + ", ");
            }
            System.out.println(values.get(i));
        }
        System.out.print(response.getContent().toString(CharsetUtil.UTF_8));
        System.out.flush();
    }
}
