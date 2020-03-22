package com.pcitc.nio;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;

import org.junit.Test;

public class TestPipe {
	
	
	@Test
	public void test() throws Exception{
		
		//1、获取管道
		Pipe pipe = Pipe.open();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//2、将缓冲区的数据写入到管道中
		Pipe.SinkChannel sink = pipe.sink();
		
		buf.put("通过单向管道发送数据".getBytes());
		buf.flip();
		sink.write(buf);
		
		//3、从管道中读取数据
		SourceChannel source = pipe.source();
		buf.flip();
		int len = source.read(buf);
		System.out.println(new String(buf.array(),0,len));
		
		source.close();
		sink.close();
		
	}
}
