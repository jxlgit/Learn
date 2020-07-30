package com.jxl.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * @Description: TODO(数据转换及计算工具类)
 * @author: jxl
 * @date: 2019年4月8日 下午1:30:28
 * @version V1.0
 */
public class DataHandleUtil {

	private static final Logger LOGGER = Logger.getLogger(DataHandleUtil.class);

	public static void main(String[] args) {

		double[][] x_data = { { 2, 3 }, { 4, 3 } };
		polynomialXdata(x_data, 2);

		// double[] x_data2 = {1,2,3,4,5,6,7,8,9,10};
		// double[] result1 = calStandardization(x_data2);
		// System.out.println(Arrays.toString(result1));
		// double[] result2 = calZoomZeroToOne(result1);
		// System.out.println(Arrays.toString(result2));

		// ============== 通过目标值分摊数据给科室 ===============
		// SUM测试
		// double[] y_data = {5,10,20,30,40};
		// double[] upperLimit= {10,0,0,0,0};
		// double assignOld = 105;
		// double assignNew = 1000;
		// double[] y_data_end = assignToSingle(y_data, upperLimit, assignOld,
		// assignNew, TargetCalType.SUM);
		//
		// double ss1 = 0;
		// for (int i = 0; i < y_data_end.length; i++) {
		// ss1 += y_data_end[i];
		// }
		// System.out.println(Arrays.toString(y_data_end));
		// System.out.println(ss1);

		// AVG测试
		double[] y_data1 = { 7, 8, 6, 5, 100 };
		double[] upperLimit1 = { 0, 0, 5, 0, 0 };
		double assignOld1 = 25;
		double assignNew1 = 30;
		double[] y_data_end1 = assignToSingle(y_data1, upperLimit1, assignOld1, assignNew1, TargetCalType.AVG);

		double ss11 = 0;
		for (int i = 0; i < y_data_end1.length; i++) {
			ss11 += y_data_end1[i];
		}
		System.out.println(Arrays.toString(y_data_end1));
		System.out.println(ss11 / y_data1.length);

	}

	/**
	 * 将数组第一列加个1
	 * 
	 * @param x_data
	 *            特征数据
	 * @return
	 */
	public static double[][] initXdata(double[][] x_data) {
		double[][] x_data_ = new double[x_data.length][x_data[0].length + 1];
		for (int i = 0; i < x_data.length; i++) {
			for (int j = 0; j < x_data[i].length + 1; j++) {
				if (j == 0) {
					x_data_[i][j] = 1;
				} else {
					x_data_[i][j] = x_data[i][j - 1];
				}
			}
		}
		return x_data_;
	}

	/**
	 * 将一维数组转为二维数组
	 * 
	 * @param y_data
	 *            标签数据
	 * @return
	 */
	public static double[][] initYdata(double[] y_data) {
		double[][] y_data_ = new double[y_data.length][1];
		for (int i = 0; i < y_data.length; i++) {
			y_data_[i][0] = y_data[i];
		}
		return y_data_;
	}

	/**
	 * 将多个一维数据合并为二维数据，适用于将多个特征数据进行组合（特征矩阵）
	 * 
	 * @param data
	 *            需要转换的特征数据
	 * @return
	 */
	public static double[][] initXYdata(double[]... data) {
		double[][] xy_data = new double[data[0].length][data.length];
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				xy_data[i][j] = data[j][i];
			}
		}
		return xy_data;
	}

	/**
	 * 计算2个特征的 简单相关系数
	 * 
	 * @param x1
	 *            特征1
	 * @param x2
	 *            特征2
	 * @return
	 */
	public static double simpleRelatedCoeff(double[] x1, double[] x2) {
		double xx1 = 0;
		double xx2 = 0;
		for (int i = 0; i < x1.length; i++) {
			xx1 += x1[i];
			xx2 += x2[i];
		}
		double xAvg1 = xx1 / x1.length;
		double xAvg2 = xx2 / x2.length;

		double sum1 = 0;
		double sum2_1 = 0;
		double sum2_2 = 0;
		for (int i = 0; i < x1.length; i++) {
			sum1 += (x1[i] - xAvg1) * (x2[i] - xAvg2);

			sum2_1 += Math.pow((x1[i] - xAvg1), 2);
			sum2_2 += Math.pow((x2[i] - xAvg2), 2);
		}

		double sum2 = Math.sqrt(sum2_1 * sum2_2);

		double coeff = sum1 / sum2;
		return coeff;
	}

	/**
	 * 数据标准化和归一化
	 * 
	 * @param x
	 *            待计算的值
	 * @param s
	 *            待计算的值所在区间值
	 * @param data
	 *            原始数据，用于标准化
	 * @return
	 */
	public static double calStandardAndZoom(double x, double[] s, double[] data) {
		if (data != null && data.length >= 2) {
			// 标准化
			s = calStandardization(data);
		}
		double x_new = calZoomZeroToOne(x, s);
		return x_new;
	}

	/**
	 * 数据标准化，均值为0和标准差为1，也就是服从标准正态分布
	 * 
	 * @param data
	 *            原始数据值
	 * @return
	 */
	public static double[] calStandardization(double[] data) {
		double[] resultData = new double[data.length];
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			// 求出数组的总和
			sum += data[i];
		}
		// 求出数组的平均数
		double average = sum / data.length;
		double total = 0;
		for (int i = 0; i < data.length; i++) {
			// 求出方差，如果要计算方差的话这一步就可以了
			total += (data[i] - average) * (data[i] - average);
		}
		// 求出标准差
		double standardDeviation = Math.sqrt(total / data.length);
		
		if (standardDeviation == 0d) {
			LOGGER.error("标准差为0，无法进行数据标准化，请检查数据是否全部相同。");
		}

		for (int i = 0; i < data.length; i++) {
			double z = (data[i] - average) / standardDeviation;
			resultData[i] = z;
		}
		return resultData;

	}

	/**
	 * 数据归一化，将数据变换到[0,1]区间，批量转换
	 * 
	 * @param data
	 *            需要缩放的原始数据
	 * @return
	 */
	public static double[] calZoomZeroToOne(double[] data) {
		double[] resultData = new double[data.length];
		int len = data.length;
		
		double[] data_ = Arrays.copyOf(data, data.length);
		Arrays.sort(data_);
		// 区间中最大值
		double max_s = data_[len - 1];
		// 区间中最小值
		double min_s = data_[0];

		for (int i = 0; i < data.length; i++) {
			double x = data[i];
			// 计算结果值
			double x_new = (x - min_s) / (max_s - min_s);
			if (x_new == 1 || x >= max_s) {
				x_new = 0.99999999;
			}
			if (x_new == 0 || x <= min_s) {
				x_new = 0.00000001;
			}
			resultData[i] = x_new;
		}
		return resultData;
	}

	/**
	 * 数据归一化，将数据变换到[0,1]区间，单个自定义转换
	 * 
	 * @param x
	 *            待计算的值
	 * @param s
	 *            待计算的值所在原始区间值
	 * @return
	 */
	public static double calZoomZeroToOne(double x, double[] s) {
		int len = s.length;
		Arrays.sort(s);
		// 区间中最大值
		double max_s = s[len - 1];
		// 区间中最小值
		double min_s = s[0];

		// 计算结果值
		double x_new = (x - min_s) / (max_s - min_s);
		if (x_new == 1 || x >= max_s) {
			x_new = 0.99999;
		}
		if (x_new == 0 || x <= min_s) {
			x_new = 0.00001;
		}
		return x_new;
	}

	/**
	 * 对结果值分配权重
	 * 
	 * @param weights
	 *            权重
	 * @param data
	 *            数据
	 * @return
	 */
	public static double[] calWeights(double[] weights, double[]... data) {
		double[] calEnddata = new double[data[0].length];

		for (int j = 0; j < data[0].length; j++) {
			double sum = 0;
			for (int i = 0; i < data.length; i++) {
				sum += data[i][j] * weights[i];
			}
			calEnddata[j] = sum;
		}
		return calEnddata;
	}

	/**
	 * 多项式展开
	 * 
	 * @param x_data
	 *            特征数据，注意最多支持5个特征，超过5个将计算不出
	 * @param degree
	 *            调节多项式的指数
	 * @return
	 */
	public static double[][] polynomialXdata(double[][] x_data, int degree) {
		double[][] calResult = new double[x_data.length][x_data[0].length];
		for (int m = 0; m < x_data.length; m++) {
			double[] data = x_data[m];

			List<String> xi = new ArrayList<String>();
			int size = data.length;
			switch (size) {
			case 1:
				for (int a = 0; a <= degree; a++) {
					int de = a;
					if (de <= degree) {
						xi.add(a + "");
					}
				}
				break;
			case 2:
				for (int a = 0; a <= degree; a++) {
					for (int b = 0; b <= degree; b++) {
						int de = a + b;
						if (de <= degree) {
							xi.add(a + "," + b);
						}
					}
				}
				break;
			case 3:
				for (int a = 0; a <= degree; a++) {
					for (int b = 0; b <= degree; b++) {
						for (int c = 0; c <= degree; c++) {
							int de = a + b + c;
							if (de <= degree) {
								xi.add(a + "," + b + "," + c);
							}
						}
					}
				}
				break;
			case 4:
				for (int a = 0; a <= degree; a++) {
					for (int b = 0; b <= degree; b++) {
						for (int c = 0; c <= degree; c++) {
							for (int d = 0; d <= degree; d++) {
								int de = a + b + c + d;
								if (de <= degree) {
									xi.add(a + "," + b + "," + c + "," + d);
								}
							}
						}
					}
				}
				break;
			case 5:
				for (int a = 0; a <= degree; a++) {
					for (int b = 0; b <= degree; b++) {
						for (int c = 0; c <= degree; c++) {
							for (int d = 0; d <= degree; d++) {
								for (int e = 0; d <= degree; d++) {
									int de = a + b + c + d + e;
									if (de <= degree) {
										xi.add(a + "," + b + "," + c + "," + d + "," + e);
									}
								}
							}
						}
					}
				}
				break;

			default:
				break;
			}

			double[] end = new double[xi.size()];
			for (int i = 0; i < xi.size(); i++) {
				String[] xi_ = xi.get(i).split(",");
				double cache = 1;
				for (int j = 0; j < xi_.length; j++) {
					double x = Math.pow(data[j], Integer.parseInt(xi_[j]));
					cache = x * cache;
				}
				end[i] = cache;
			}
			
			LOGGER.info("原始值：" + Arrays.toString(data));
			LOGGER.info("指数值：" + xi);
			Arrays.sort(end);
			LOGGER.info("转换后值：" + Arrays.toString(end));
			calResult[m] = end;
			xi = null;
		}
		return calResult;

	}
	
	/**
	 * 占比类算法，返回分母增加的值及分子增加的值，注意返回的是增加的值
	 * 
	 * @param sj_sum_fz 分子实际值
	 * @param sj_sum_fm 分母实际值
	 * @param zb_mb 占比目标值
	 */
	public static double[] calZB(double sj_sum_fz, double sj_sum_fm, double zb_mb) {
		// 占比实际值
		double zb_sj = sj_sum_fz / sj_sum_fm;

		// 占比分配系数，分母和分子的增量通过这个系数等比调节
		double zb_fp = zb_mb + (zb_mb - zb_sj);
		if (zb_fp <= 0) {
			zb_fp = zb_mb / 2;
		}
		if (zb_fp >= 1) {
			zb_fp = zb_mb + (1 - zb_mb) / 2;
		}
		// 分母增加的值
		double m = 0d;
		// 分子增加的值
		double z = 0d;
		
		double zb_fm = (zb_fp - zb_mb);
		if (zb_fm != 0d) {
			m = (zb_mb * sj_sum_fm - sj_sum_fz) / zb_fm;
			z = m * zb_fp;
		}		
		double[] end = {z, m};
		
		return end;
	}

	/**
	 * 通过目标值反推科室值（只能用于目标值反推科室值）
	 * 
	 * 总量截距计算公式： (科室1 + 科室2 + 科室3) + 截距 = 调整前值 
	 * 均值截距计算公式： ((科室1 + 科室2 + 科室3) / 3) + 截距 = 调整前值
	 * 
	 * 系数计算公式：(科室1 + 科室2 + 科室3) * 系数 + 截距 = 调整后值
	 * 
	 * @param y_data
	 *            各科室原始值
	 * @param upperLimit
	 *            各科室固定值
	 * @param assignOld
	 *            调整前值
	 * @param assignNew
	 *            调整后值，需要达到的值，不是分配值
	 * @param targetCalType
	 *            类型
	 * @return
	 */
	public static double[] assignToSingle(double[] y_data, double[] upperLimit, double assignOld, double assignNew,
			TargetCalType targetCalType) {
		double[] y_data_new = new double[y_data.length];

		double sumy = 0;
		double upperLimity = 0;
		double sumall = 0;
		for (int i = 0; i < y_data.length; i++) {
			sumall += y_data[i];
			if (upperLimit[i] > 0) {
				upperLimity += upperLimit[i];
			} else {
				sumy += y_data[i];
			}
		}

		switch (targetCalType) {
		case SUM:
			// 计算截距
			double intercept = assignOld - sumall;
			double x = (assignNew - intercept - upperLimity) / sumy;

			for (int i = 0; i < y_data.length; i++) {
				if (upperLimit[i] > 0) {
					y_data_new[i] = upperLimit[i];
				} else {
					y_data_new[i] = y_data[i] * x;
				}
			}

			break;
		case AVG:
			// 计算截距
			double intercept1 = assignOld - sumall / y_data.length;
			double x1 = (assignNew * y_data.length - intercept1 - upperLimity) / sumy;

			for (int i = 0; i < y_data.length; i++) {
				if (upperLimit[i] > 0) {
					y_data_new[i] = upperLimit[i];
				} else {
					y_data_new[i] = y_data[i] * x1;
				}
			}

			break;
		default:
			break;
		}

		return y_data_new;
	}

}
