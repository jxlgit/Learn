package com.jxl.thread;

public class ThreadTest extends Thread {
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("这是一个线程！");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 创建线程
		ThreadTest threadTest = new ThreadTest();
		// 启动线程
		threadTest.start();
	}

}
