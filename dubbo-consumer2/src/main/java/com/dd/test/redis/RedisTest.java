package com.dd.test.redis;

import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * Created by junqiao.wjq on 2018/3/11.
 */
public class RedisTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("127.0.0.1", 6379);
		jedis.set("aaa", "bbb");
		System.out.println(jedis.get("aaa"));

		Random r = new Random();

		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i ++) {
			jedis.set(i + "", i + " xxx");
		}
		System.out.println("Set: " + (System.currentTimeMillis() - start) + " ms");

		long x = 0;
		start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i ++) {
			String resutl = jedis.get(r.nextInt(100000) + "");
			x += resutl.length();
		}
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(x);


	}
}
