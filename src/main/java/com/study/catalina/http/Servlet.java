package com.study.catalina.http;

public abstract class Servlet {

	public abstract void doGet(Request request, Response response);

	public abstract void doPost(Request request, Response response);
}
