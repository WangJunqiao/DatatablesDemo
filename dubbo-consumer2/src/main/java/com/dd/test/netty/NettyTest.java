package com.dd.test.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

enum Calculator {
	Instance;
	private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
	public Object cal(String expression) throws ScriptException {
		return jse.eval(expression);
	}
}

class Server extends Thread {
	private int port;
	public Server(int port) {
		this.port = port;
	}
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ServerHandler());
						}
					});
			ChannelFuture f = b.bind(port).sync();
			System.out.println("服务器开启："+port);
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}

class ServerHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
		ByteBuf in = (ByteBuf) msg;
		byte[] req = new byte[in.readableBytes()];
		in.readBytes(req);
		String body = new String(req,"utf-8");
		System.out.println("收到客户端消息:"+body);
		String calrResult = null;
		try{
			calrResult = Calculator.Instance.cal(body).toString();
		}catch(Exception e){
			calrResult = "错误的表达式：" + e.getMessage();
		}
		ctx.write(Unpooled.copiedBuffer(calrResult.getBytes()));
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

class ClientHandler extends ChannelInboundHandlerAdapter {
	ChannelHandlerContext ctx;
	/**
	 * tcp链路简历成功后调用
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}
	public boolean sendMsg(String msg){
		System.out.println("客户端发送消息："+msg);
		byte[] req = msg.getBytes();
		ByteBuf m = Unpooled.buffer(req.length);
		m.writeBytes(req);
		ctx.writeAndFlush(m);
		return msg.equals("q")?false:true;
	}
	/**
	 * 收到服务器消息后调用
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"utf-8");
		System.out.println("服务器消息："+body);
	}
	/**
	 * 发生异常时调用
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

class Client implements Runnable{
	static ClientHandler client = new ClientHandler();
	public static void main(String[] args) throws Exception {
		new Thread(new Client()).start();
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while(client.sendMsg(scanner.nextLine()));
	}
	@Override
	public void run() {
		String host = "127.0.0.1";
		int port = 9090;
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(client);
					}
					});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}

public class NettyTest {
	public static void main(String[] args) throws Exception {
		new Server(9090).run();
	}
}
