package com.world.web;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class WrapperResponse extends HttpServletResponseWrapper {
	private PrintWriter cachedWriter;
	private CharArrayWriter bufferedWriter;
	private boolean compress = false;
	private boolean hasWriter;

	public boolean isHasWriter() {
		return hasWriter;
	}

	public void setHasWriter(boolean hasWriter) {
		this.hasWriter = hasWriter;
	}

	public boolean isCompress() {
		return this.compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public WrapperResponse(HttpServletResponse response) {
		super(response);

		this.bufferedWriter = new CharArrayWriter();

		this.cachedWriter = new PrintWriter(this.bufferedWriter);
		this.compress = false;
	}

	public WrapperResponse(HttpServletResponse response, boolean compress) {
		super(response);

		this.bufferedWriter = new CharArrayWriter();

		this.cachedWriter = new PrintWriter(this.bufferedWriter);
		this.compress = compress;
	}

	public PrintWriter getWriter() {
		if(!hasWriter){
			hasWriter = true;
		}
		return this.cachedWriter;
	}
	
	public PrintWriter getSrcWriter() throws IOException{
		return super.getWriter();
	}

	public String getContent() {
		return getResult();
	}

	public String getResult() {
		String r = this.bufferedWriter.toString();

		return r;
	}
}