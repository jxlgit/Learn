package com.jxl.algorithm;

import java.util.Arrays;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Logger;

/**
 * 
 * @Description: TODO(多元线性回归，标准方程法)
 * @author: jxl
 * @date: 2019年4月8日 上午9:54:28
 * @version V1.0
 */
public class LinearRegressionMore {
	
	private static final Logger LOGGER = Logger.getLogger(DataHandleUtil.class);

	/**
	 * 线性方程系数
	 */
	public double[] coefficient;

	/**
	 * 建立模型，得到系数
	 * 
	 * @param xArr
	 *            特征
	 * @param yArr
	 *            标签
	 * @return 系数
	 */
	public double[] model(double[][] xArr, double[][] yArr) {
		// 转换为矩阵
		RealMatrix xMat = new Array2DRowRealMatrix(xArr);
		RealMatrix yMat = new Array2DRowRealMatrix(yArr);

		RealMatrix xTx = xMat.transpose().multiply(xMat);
		DecompositionSolver lud = new LUDecomposition(xTx).getSolver();
		if (!lud.isNonSingular()) {
			LOGGER.error("矩阵不可逆，特征数据存在线性相关，请检查！" );
			LOGGER.error("不可逆的矩阵为：" + Arrays.deepToString(xTx.getData()));
			return null;
		} else {
			RealMatrix ws = lud.getInverse().multiply(xMat.transpose()).multiply(yMat);
			coefficient = ws.getColumn(0);
			return coefficient;
		}
	}

	/**
	 * 预测（单个）
	 * 
	 * @param x_data_exp
	 *            特征
	 * @return 预测值
	 */
	public double predict(double[] x_data_exp) {
		// 截距
		double val = coefficient[0];
		// 系数
		for (int i = 0; i < x_data_exp.length; i++) {
			val += x_data_exp[i] * coefficient[i + 1];
		}
		return val;
	}

	/**
	 * 预测（单个）
	 * 
	 * @param x_data_exp
	 *            预测值变量
	 * @param coefficient
	 *            模型系数
	 * @return
	 */
	public static double predict(double[] x_data_exp, double[] coefficient) {
		// 截距
		double val = coefficient[0];
		// 系数
		for (int i = 0; i < x_data_exp.length; i++) {
			val += x_data_exp[i] * coefficient[i + 1];
		}
		return val;
	}

	/**
	 * 预测（批量）
	 * 
	 * @param data
	 *            需要预测的数据的特征，一个特征为一个参数
	 * @return
	 */
	public double[] predict(double[]... data) {
		double[] result = new double[data[0].length];
		for (int i = 0; i < data[0].length; i++) {
			double val = coefficient[0];
			for (int j = 0; j < data.length; j++) {
				val += data[j][i] * coefficient[j + 1];
			}
			result[i] = val;
		}
		return result;
	}

}
