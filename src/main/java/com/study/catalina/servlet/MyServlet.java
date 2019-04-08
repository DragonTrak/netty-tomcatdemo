package com.study.catalina.servlet;

import com.study.catalina.http.Request;
import com.study.catalina.http.Response;
import com.study.catalina.http.Servlet;

/**
 * @Description TODO
 * @Date 2019/4/8 14:53
 * @Author myt_ac@163.com
 */
public class MyServlet extends Servlet {
    @Override
    public void doGet(Request request, Response response) {
        doPost(request,response);
    }

    @Override
    public void doPost(Request request, Response response) {
        String param = "name";
        String str = request.getParameter(param);
        response.write(param + ":" + str,200);
    }
}
