package com.jxl.thread;

/**


如上代码在不考虑、 内存可见性问题的情况下一定会输出(0,1)？ 答案是不一定，其运行结果可能为(1,0)、(0,1)或(1,1)，因为代码(1)、(2)可能被重排序后执行顺序为(2)、(1)，同理(3)、(4)可能被重排序后执行顺序为(4)、(3)，这样在多线程情况下就会出现非预期的程序执行结果。
那有什么办法能解决上面的问题呢？答案是肯定的，我们可以使用volatile修饰我们定义的变量即可解决重排序和内存可见性问题。
写volatile变量时，可以确保volatile写之前的操作不会被编译器重排序到volatile写之后。读volatile变量时，可以确保volatile读之后的操作不会被编译器重排序到volatile读之前。
 
 */
public class InstructionSortTest {
	static int x = 0, y = 0;
	static int a = 0, b = 0;

	public static void main(String[] args) throws InterruptedException {
		Thread one = new Thread(new Runnable() {
			public void run() {
				a = 1; // (1)
				x = b; // (2)
			}
		});

		Thread other = new Thread(new Runnable() {
			public void run() {
				b = 1; // (3)
				y = a; // (4)
			}
		});
		one.start();
		other.start();
		one.join();
		other.join();
		System.out.println("(" + x + "," + y + ")");
	}
}


