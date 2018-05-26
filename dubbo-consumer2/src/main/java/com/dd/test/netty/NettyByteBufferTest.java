package com.dd.test.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.nio.ByteBuffer;

/**
 * Created by junqiao.wjq on 2018/5/21.
 */
public class NettyByteBufferTest {

	public static void main(String[] args) {
		PooledByteBufAllocator pooledByteBufAllocator = new PooledByteBufAllocator(true);
		UnpooledByteBufAllocator unpooledByteBufAllocator = new UnpooledByteBufAllocator(true);


		byte[] data = new byte[1024];

		long now = System.currentTimeMillis();
		for (int i = 0; i < 3000000; i ++) {
			ByteBuf buf = pooledByteBufAllocator.directBuffer(102400);
			buf.setBytes(0, data);
			buf.release();
		}
		System.out.println(System.currentTimeMillis() - now);

		now = System.currentTimeMillis();
		for (int i = 0; i < 3000000; i ++) {
			ByteBuf buf = unpooledByteBufAllocator.directBuffer(102400);
			buf.setBytes(0, data);
			buf.release();
		}
		System.out.println(System.currentTimeMillis() - now);
	}
}
