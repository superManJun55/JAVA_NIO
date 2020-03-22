package com.pcitc.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/**
 * 非阻塞式
 * @ClassName: TestNonBlockingNio 
 * @Description: TODO 
 * @author : chen_wenjun
 * @QQ:353376358
 * @date 2020年2月16日 下午5:55:39
 */
public class TestNonBlockingNio {
	
	
	@Test
	public void client() throws Exception {
		//1、获取通道
		SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8798));
		
		//2、切换非阻塞模式
		clientChannel.configureBlocking(false);
		
		//3、分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		Scanner scanner = new Scanner(System.in);
		
		while (scanner.hasNext()) {
			String str = scanner.next();
			//4、发送数据到服务器段
			buf.put((LocalDateTime.now().toString() + " :\t" + str).getBytes());
			
			buf.flip();
			clientChannel.write(buf);
			buf.clear();
		}
		
		
		clientChannel.close();
		
	}
	
	@Test
	public void server() throws Exception{
		
		//1、获取通道
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		
		//2、切换非阻塞模式
		serverChannel.configureBlocking(false);
		
		//3、绑定链接
		serverChannel.bind(new InetSocketAddress("127.0.0.1", 8798));
		
		//4、获取选择器
		Selector seletor = Selector.open();
		
		//5、将通道注册到选择器上，并且监听接收事件
		serverChannel.register(seletor, SelectionKey.OP_ACCEPT);
		
		
		//6、轮询式的获取选择器上已经准备就绪的事件
		while (seletor.select() > 0) {
			//7、获取当前选择器中的所有注册的选择键
			Iterator<SelectionKey> it = seletor.selectedKeys().iterator();
			while (it.hasNext()) {
				//8、获取准备就绪事件
				SelectionKey sk = it.next();
				
				//9、判断具体事件的准备就绪
				if (sk.isAcceptable()) {
					//10、若"接收就绪",获取客户端链接
					SocketChannel accept = serverChannel.accept();
					//11
					accept.configureBlocking(false);
					//12
					accept.register(seletor, SelectionKey.OP_READ);
					
				}else if(sk.isReadable()){
					//13
					SocketChannel channel = (SocketChannel) sk.channel();
					
					ByteBuffer buf = ByteBuffer.allocate(1024);
					
					int len = 0;
					while ((len = channel.read(buf)) > 0 ) {
						buf.flip();
						System.out.println(new String(buf.array(), 0, len));
						buf.clear();
					}
					
				}
				
				it.remove();
			}
			
		}
		
	}
}
