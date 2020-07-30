package com.jxl.thread;

public class RunnableTest implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("这是一个线程！");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 创建线程
		RunnableTest runnableTest = new RunnableTest();
		// 启动线程
		new Thread(runnableTest).start();
	}

}
