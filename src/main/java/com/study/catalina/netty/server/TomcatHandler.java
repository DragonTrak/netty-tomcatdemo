package com.study.catalina.netty.server;

import com.study.catalina.http.Request;
import com.study.catalina.http.Response;
import com.study.catalina.http.Servlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.log4j.Logger;

import javax.core.common.config.CustomConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Description TODO
 * @Date 2019/4/8 14:45
 * @Author myt_ac@163.com
 */
public class TomcatHandler extends ChannelInboundHandlerAdapter {
    private Logger LOG = Logger.getLogger(TomcatHandler.class);

    private static final Map<Pattern,Class<?>> servletMapping = new HashMap<Pattern,Class<?>>();

    static {
        CustomConfig.load("web.properties");
        for (String key : CustomConfig.getKeys()) {
            if(key.startsWith("servlet")){
                String name = key.replaceFirst("servlet.", "");
                if(name.indexOf(".") != -1){
                    name = name.substring(0,name.indexOf("."));
                }else{
                    continue;
                }
                String pattern = CustomConfig.getString("servlet." + name + ".urlPattern");
                pattern = pattern.replaceAll("\\*", ".*");
                String className = CustomConfig.getString("servlet." + name + ".className");
                if(!servletMapping.containsKey(pattern)){
                    try {
                        servletMapping.put(Pattern.compile(pattern), Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       if (msg instanceof HttpRequest){
           HttpRequest r = (HttpRequest) msg;
           Request request = new Request(ctx,r);
           Response response = new Response(ctx,r);
           String uri = request.getUri();
           String method = request.getMethod();

           LOG.info(String.format("Uri:%s method %s", uri, method));

           boolean hasPattern = false;
           for (Map.Entry<Pattern, Class<?>> entry : servletMapping.entrySet()) {
               if (entry.getKey().matcher(uri).matches()) {
                   Servlet servlet = (Servlet)entry.getValue().newInstance();
                   if("get".equalsIgnoreCase(method)){
                       servlet.doGet(request, response);
                   }else{
                       servlet.doPost(request, response);
                   }
                   hasPattern = true;
               }
           }

           if(!hasPattern){
               String out = String.format("404 NotFound URL%s for method %s", uri,method);
               response.write(out,404);
               return;
           }
       }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
