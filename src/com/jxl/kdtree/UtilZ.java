package com.jxl.kdtree;

import java.util.ArrayList;

public class UtilZ {
	/**
	 * 计算给定维度的方差
	 * 
	 * @param data
	 *            数据
	 * @param dimention
	 *            维度
	 * @return 方差
	 */
	static double variance(ArrayList<double[]> data, int dimention) {
		double vsum = 0;
		double sum = 0;
		for (double[] d : data) {
			sum += d[dimention];
			vsum += d[dimention] * d[dimention];
		}
		int n = data.size();
		return vsum / n - Math.pow(sum / n, 2);
	}

	/**
	 * 取排序后的中间位置数值
	 * 
	 * @param data
	 *            数据
	 * @param dimention
	 *            维度
	 * @return
	 */
	static double median(ArrayList<double[]> data, int dimention) {
		double[] d = new double[data.size()];
		int i = 0;
		for (double[] k : data) {
			d[i++] = k[dimention];
		}
		return findPos(d, 0, d.length - 1, d.length / 2);
	}

	/**
	 * 获取数据的最大值和最小值
	 * 
	 * @param data
	 *            数据
	 * @param dimentions
	 *            维度
	 * @return
	 */
	static double[][] maxmin(ArrayList<double[]> data, int dimentions) {
		double[][] mm = new double[2][dimentions];
		// 初始化 第一行为min，第二行为max
		for (int i = 0; i < dimentions; i++) {
			mm[0][i] = mm[1][i] = data.get(0)[i];
			for (int j = 1; j < data.size(); j++) {
				double[] d = data.get(j);
				if (d[i] < mm[0][i]) {
					mm[0][i] = d[i];
				} else if (d[i] > mm[1][i]) {
					mm[1][i] = d[i];
				}
			}
		}
		return mm;
	}

	/**
	 * 计算欧几里得距离
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	static double distance(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += Math.pow(a[i] - b[i], 2);
		}
		double sqrtSum = Math.sqrt(sum);
		return sqrtSum;
	}

	/**
	 * 在max和min表示的超矩形中的点和点a的最小距离
	 * 
	 * @param a
	 *            点a
	 * @param max
	 *            超矩形各个维度的最大值
	 * @param min
	 *            超矩形各个维度的最小值
	 * @return 超矩形中的点和点a的最小距离
	 */
	static double mindistance(double[] a, double[] max, double[] min) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max[i])
				sum += Math.pow(a[i] - max[i], 2);
			else if (a[i] < min[i]) {
				sum += Math.pow(min[i] - a[i], 2);
			}
		}

		return sum;
	}

	/**
	 * 使用快速排序，查找排序后位置在point处的值 比Array.sort()后去对应位置值，大约快30%
	 * 
	 * @param data
	 *            数据
	 * @param low
	 *            参加排序的最低点
	 * @param high
	 *            参加排序的最高点
	 * @param point
	 *            位置
	 * @return
	 */
	static double findPos(double[] data, int low, int high, int point) {
		int lowt = low;
		int hight = high;
		double v = data[low];
		ArrayList<Integer> same = new ArrayList<Integer>((int) ((high - low) * 0.25));
		while (low < high) {
			while (low < high && data[high] >= v) {
				if (data[high] == v) {
					same.add(high);
				}
				high--;
			}
			data[low] = data[high];
			while (low < high && data[low] < v)
				low++;
			data[high] = data[low];
		}
		data[low] = v;
		int upper = low + same.size();
		if (low <= point && upper >= point) {
			return v;
		}

		if (low > point) {
			return findPos(data, lowt, low - 1, point);
		}

		int i = low + 1;
		for (int j : same) {
			if (j <= low + same.size())
				continue;
			while (data[i] == v)
				i++;
			data[j] = data[i];
			data[i] = v;
			i++;
		}

		return findPos(data, low + same.size() + 1, hight, point);
	}
}