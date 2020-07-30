package com.jxl.algorithm;

/**
 * 
 * @Description:    TODO(一元线性回归，梯度下降法)   
 * @author: jxl     
 * @date:   2019年3月11日 上午10:58:34   
 * @version V1.0
 */
public class LinearRegressionOne {
	
	private double[] x_data = {1,2,3,4,5};
	private double[] y_data = {2,4,6,7,10};
	// 学习率learning rate
	private double lr = 0.01;
	// 截距
	private double b = 0;
	// 斜率
	private double k = 0;
	// 最大迭代次数
	private int epochs = 500;
	
	public static void main(String[] args) {
		LinearRegressionOne test1 = new LinearRegressionOne();
		test1.print();
	}
	
	/**
	 * 最小二乘法（计算误差,值越低越好）
	 * 
	 * @return
	 */
	public double compute_error() {
		double total_error = 0;
		for (int i = 0; i < x_data.length; i++) {
			total_error += Math.pow(y_data[i] - (k * x_data[i] + b), 2);
		}
		return total_error;
	}
	
	/**
	 * 梯度下降法计算截距和斜率
	 * 
	 * @param x_data
	 * @param y_data
	 * @param b
	 * @param k
	 * @param lr
	 * @param epochs
	 */
	public void gradient_descent_runner(double[] x_data, double[] y_data, double b, double k, double lr, int epochs) {
		
		for (int i = 0; i < epochs; i++) {
			double m = x_data.length;
			double b_grad = 0d;
			double k_grad = 0d;
			for (int j = 0; j < x_data.length; j++) {
				b_grad += (1d / m) * (((k * x_data[j]) + b) - y_data[j]);
			    k_grad += (1d / m) * x_data[j] * (((k * x_data[j]) + b) - y_data[j]);
			}
			//更新b和k
	        b = b - (lr * b_grad);
	        k = k - (lr * k_grad);
		}
		this.b = b;
		this.k = k;
	}
	
	public void print() {
		gradient_descent_runner(x_data, y_data, b, k, lr, epochs);
		double error = compute_error();
		System.out.println("误差：" + error);
		String str=String.format("线性方程：y = %s * x + %s", k, b);
		System.out.println(str);
		
		double s = k * 0.01 + b;
		System.out.println("测试：" + s);
		
	}
	

}
