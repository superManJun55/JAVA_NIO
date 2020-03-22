package com.pcitc.nio;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.junit.Test;
/**
 * 一、Channel(通道)：用于源节点与目标节点的连接，负责NIO缓冲区的数据传输，Channel本身是不传输数据。
 * 二、通道的主要实现类
 * java.nio.channels.Channel接口：
 * 		|--FileChannel 本地IO
 * 		|--SocketChannel 网络IO
 * 		|--ServerSocketChannel 网络IO
 * 		|--DatagramChannel 网络IO
 * 三、获取通道方式：
 * 1、getChannel()方法
 * 2、JDK1.7中的NIO2中的静态方法open()
 * 3、JDK1.7中的NIO2的Files工具类的newByteChannel()
 * 
 * 四、通道之间的传输
 * transferFrom()
 * transferTo()
 * 
 * 五、分散（Scatter）读取与聚集(Gather)写入
 * 分散读取：将通道中的数据分散到多个缓冲区中
 * 聚集写入：将多个缓冲区的数据写入到通道中
 * 
 * 
 * 六：字符集（Charset）
 * 编码：字符串->字节数组
 * 解码：字节数组->字符串
 * 
 * 
 * @ClassName: TestChannel 
 * @Description: TODO 
 * @author : chen_wenjun
 * @QQ:353376358
 * @date 2020年2月16日 上午11:44:02
 */
public class TestChannel {
	
	
	//字符集
	@Test
	public void test6() throws Exception {
		Charset cs = Charset.forName("GBK");
		
		CharsetEncoder ce = cs.newEncoder();//获取编码器
		CharsetDecoder cd = cs.newDecoder();//获取解码器
		
		CharBuffer cb = CharBuffer.allocate(2014);
		cb.put("陈文军");
		cb.flip();
		
		ByteBuffer buf = ce.encode(cb);//编码
		
		for (int i = 0; i < 6; i++) {
			System.out.println(buf.get());
		}
		
		System.out.println("-------------------------------------");
		buf.flip();
		CharBuffer buf2 = cd.decode(buf);//解码
		System.out.println(buf2.toString());
		
		
	}
	
	//Charset
	@Test
	public void test5() {
		SortedMap<String, Charset> acs = Charset.availableCharsets();
		Set<Entry<String, Charset>> es = acs.entrySet();
		for (Entry<String, Charset> entry : es) {
			System.out.println(entry.getKey() + "------>" + entry.getValue());
		}
		
	}
	
	//分散和聚集
	@Test
	public void test4() throws Exception {
		RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");
		//1、获取通道
		FileChannel inChannel = raf1.getChannel();
		
		//2、分配指定大小的缓冲区
		ByteBuffer buf1 = ByteBuffer.allocate(100);
		ByteBuffer buf2 = ByteBuffer.allocate(1024);
		
		//3、分散读取
		ByteBuffer[] dsts = {buf1, buf2};
		inChannel.read(dsts);
		
		//4、切换为读模式
		buf1.flip();
		buf2.flip();
		
		System.out.println(new String(buf1.array(), 0, buf1.limit()));
		System.out.println("=====================================");
		System.out.println(new String(buf2.array(), 0, buf2.limit()));
		
		//4、聚集写入
		RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
		FileChannel channel = raf2.getChannel();
		channel.write(dsts);
		
		
	}
	
	//通道之间的数据传输
	@Test
	public void test3() throws Exception{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("5.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		
//		inChannel.transferTo(0, inChannel.size(), outChannel);
		
		outChannel.transferFrom(inChannel, 0, inChannel.size());
		
		inChannel.close();
		outChannel.close();
	}
	
	
	//2、使用直接缓冲区完成文件的复制（内存映射文件）
	@Test
	public void test2() throws Exception {
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		
		MappedByteBuffer inMapBuf = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMapBuf = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
		
		byte[] dst = new byte[inMapBuf.limit()];
		inMapBuf.get(dst);
		outMapBuf.put(dst);
		
		inChannel.close();
		outChannel.close();
		
		
	}

	//1、利用通道完成文件的复制
	@Test
	public void test1() {
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		
		
		try {
			fis = new FileInputStream("1.jpg");
			fos = new FileOutputStream("2.jpg");
			
			inChannel = fis.getChannel();
			outChannel = fos.getChannel();
			
			ByteBuffer buf = ByteBuffer.allocate(1024);
			
			while (inChannel.read(buf) != -1) {
				buf.flip();//切换读模式
				outChannel.write(buf);
				buf.clear();//清空缓存区
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (outChannel != null) outChannel.close();
				if (inChannel != null) inChannel.close();
				if (fos != null)  fos.close();
				if (fis != null)  fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
