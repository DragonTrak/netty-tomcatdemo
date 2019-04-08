package com.study.catalina.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaders.Values;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class Response {

	private ChannelHandlerContext ctx;
	private HttpRequest request;
	private static Map<Integer,HttpResponseStatus> statusMapping = new HashMap<Integer,HttpResponseStatus>();
	
	static{
		statusMapping.put(200, HttpResponseStatus.OK);
		statusMapping.put(404, HttpResponseStatus.NOT_FOUND);
	}
	
	public Response(ChannelHandlerContext ctx, HttpRequest request){
		this.ctx = ctx;
		this.request = request;
	}
	
	public void write(String outString,Integer status){  
		try{
			FullHttpResponse response = new DefaultFullHttpResponse(
				HTTP_1_1,
				statusMapping.get(status), 
				Unpooled.wrappedBuffer(outString.getBytes("UTF-8")));  
			response.headers().set(CONTENT_TYPE, "text/json");  
			response.headers().set(CONTENT_LENGTH,response.content().readableBytes());  
			response.headers().set(EXPIRES, 0);  
			if (HttpHeaders.isKeepAlive(request)) {  
				response.headers().set(CONNECTION, Values.KEEP_ALIVE);  
			}  
			ctx.write(response);  
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ctx.flush(); 
		}
	}
	
}
