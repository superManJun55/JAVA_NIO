package com.pcitc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

/**
 * 一、使用NIO完成网络通信的三个核心：
 * 1、通道（Channel）:负责连接
 * 		java.io.channels.Channel
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 * 				
 * 				|-- Pipe.SinkChannel
 * 				|-- Pipe.SourceChannel
 * 
 * 2、缓冲区(Buffer)：负责数据的存储
 * 
 * 3、选择器(Selector)：是SelectableChannel的多路复用器，用于监控SelectableChannelde IO状况
 * 
 * @ClassName: TestBlockingNIO 
 * @Description: TODO 
 * @author : chen_wenjun
 * @QQ:353376358
 * @date 2020年2月16日 下午3:38:17
 */
public class TestBlockingNIO {

	//阻塞式
	
	@Test
	public void client() throws Exception {
		//1、打开一个网络通道
		SocketChannel client = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8980));
		
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		//2、分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		//3、读取本地文件，并发送到服务器
		while (inChannel.read(buf) != -1) {
			buf.flip();
			client.write(buf);
			buf.clear();
		}
		
		inChannel.close();
		client.close();
		
	}
	
	@Test
	public void server() throws Exception{
		
		ServerSocketChannel server = ServerSocketChannel.open();
		
		FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		
		server.bind(new InetSocketAddress("127.0.0.1", 8980));
		
		SocketChannel accept = server.accept();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		while (accept.read(buf) != -1) {
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		
		accept.close();
		outChannel.close();
		server.close();
		
	}
	
}
