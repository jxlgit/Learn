package com.jxl.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableTest implements Callable<String> {

	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		return "Hello world!";
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		// 创建异步任务
		FutureTask<String> futureTask = new FutureTask<>(new CallableTest());
		// 启动线程
		new Thread(futureTask).start();
		try {
			// 等待任务执行完毕，并返回结果
			String result = futureTask.get();
			System.out.println(result);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
