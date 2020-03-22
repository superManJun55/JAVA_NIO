package com.pcitc.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * NIO是面向缓存的，非阻塞的，选择器；而传统IO是面向流的，阻塞的
 * NIO是JDK1.4以后推出的
 * 
 * NIO的核心在于：通道(Channel)和缓冲区（Buffer）,通道负责传输，缓冲区分则存储
 * 
 * 
 * 一、缓冲区（Buffer）在NIO中负责数据存取，缓冲区就是数组，用于存储不同类型的数据
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 
 * 通过allocate()获取缓冲区
 * 
 * 二、缓冲区存取数据的两个核心方法是：
 * put()  存
 * get()  取
 * 
 * 三、缓冲区的四个核心属性：
 * capacity：容量，表示缓冲区最大存储数据的容量。一旦声明不能改变
 * limit:界限，表示缓冲区中可以操作数据的大小
 * position：位置，表示缓冲区中正在操作数据的位置
 * mark:标记，记录当前position的位置，可以通过reset()恢复到mark的位置
 * 
 * 四、mark <= position <= limit <= capacity
 * 
 * 五、直接缓冲区和非直接缓冲区的区别：
 * 1、非直接缓冲区通过allocate() 方法分配，将缓冲区建立在JVM的内存中
 * 2、直接缓冲区是通过allocateDirect()方法分配，将缓冲区建立在物理内存中，可以提高效率，但是消耗资源较大，数据不安全
 * 
 * @ClassName: TestNio 
 * @Description: TODO 
 * @author : chen_wenjun
 * @QQ:353376358
 * @date 2020年2月15日 下午6:35:53
 */
public class TestNio {
	
	//直接缓冲区
	@Test
	public void test3() {
		ByteBuffer ad = ByteBuffer.allocateDirect(1024);//创建直接缓存区
		
		System.out.println(ad.isDirect());
		System.out.println(ad.isReadOnly());
	}
	
	
	@Test
	public void test2() {
		String str = "wenjun";
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		buffer.put(str.getBytes());
		
		buffer.flip();//切换读模式
		
		byte[] dst = new byte[buffer.limit()];
		buffer.get(dst, 0, 2);
		System.out.println("position:" + buffer.position());
		System.out.println(new String(dst));
		
		buffer.mark();//标记
		
		buffer.get(dst, 2, 2);
		System.out.println("position:" + buffer.position());
		System.out.println(new String(dst));
		
		buffer.reset();//恢复到标记的位置
		System.out.println("position:" + buffer.position());
		
		if (buffer.hasRemaining()) {//查看是否还有剩余的数据
			System.out.println(buffer.remaining());
		}
		
	}
	
	@Test
	public void test1() {
		ByteBuffer buf = ByteBuffer.allocate(1024);//创建指定大小的缓冲区
		String str = "wenjun";
		
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		System.out.println("创建......");
		
		buf.put(str.getBytes());//1、存数据
		System.out.println("PUT......");
		
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		buf.flip();//2、切换为读模式
		System.out.println("FLIP......");
		
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		buf.get(new byte[str.length()]);//3、读缓存区
		System.out.println("GET.......");
		
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		buf.rewind();//4、可重复读
		System.out.println("REWIND......");
		
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		buf.clear();//清空缓存区，但是缓存区的数据依然存在，数据处于被遗忘的状态
		System.out.println("CLEAR......");
		
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		System.out.println((char)buf.get());
	}
}
