package com.jxl.thread;

public class ThreadDeadLockTest {

	// 创建资源
	private static Object resourceA = new Object();
	private static Object resourceB = new Object();

	public static void main(String[] args) {
		// 创建线程A
		Thread threadA = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				synchronized (resourceA) {
					System.out.println("ThreadA：已经得到资源A");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("ThreadA：正在等待获取资源B");
					synchronized (resourceB) {
						System.out.println("ThreadA：已经得到资源B");
					}
				}
			}

		});

		// 创建线程B
		Thread threadB = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				synchronized (resourceB) {
					System.out.println("ThreadB：已经得到资源B");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("ThreadB：正在等待获取资源A");
					synchronized (resourceA) {
						System.out.println("ThreadB：已经得到资源A");
					}
				}
			}

		});
		
		threadA.start();
		threadB.start();
	}
}
