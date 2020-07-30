package com.jxl.algorithm;

import java.util.Arrays;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Logger;

/**
 * 
 * @Description: TODO(通过目标值按O/E大小分摊数据给科室--使用标准化和归一化算法)
 * 
 * 注意，只有当分母全部相同的时候才能使用AVG，否则只能使用占比类算法,因为这里的AVG算法只是将增量数据等分下去了，没有考虑分子分母情况
 * 
 * @author: jxl
 * @date: 2019年2月21日 下午5:33:22
 * @version V1.0
 */
public class OEAlgorithm {
	
	private static final Logger LOGGER = Logger.getLogger(OEAlgorithm.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		demo();
		test1();
		test2();
		
	}
	
	/**
	 * 非占比类测试
	 */
	private static void test1() {
		System.out.println("======================== 非占比类测试  =============================");
		// 需要分配的份额（注意是增量值，目标值-历史值）
		double assignVal = 2;
		// 每个维度的实际值 （如每列表示一个科室的实际值）
		double[] actualVal = { 5, 5, 7 };
		// 每个维度的变量值（如每行表示一个科室，一行的每个元素表示变量值）
		double[][] variableVal = { { 0.1, 0.1 }, { 0.5, 0.5 }, { 5.5, 0.9 } };
		// 每个变量的相关性，1表示正相关（越大分配得越多）， 0 表示负相关（越大分配得越少）
		double[] relatedVal = { 0, 0 };
		// 每个变量变换的范围 （一行表示一个变量变换范围）
		double[][] varyRange = { { 0, 5 }, { 0, 1 } };
		// 每个变量的权重
		double[] weightsVal = { 0.5, 0.5 };
		// SUM为总量，AVG为均值
		TargetCalType targetCalType = TargetCalType.SUM;

		// 计算主方法
		double[] calEnd = calTargetVal(assignVal, actualVal, variableVal, relatedVal, varyRange, weightsVal,
				targetCalType);
		for (int i = 0; i < calEnd.length; i++) {
			System.out.println("科室" + (i + 1) + ":" + calEnd[i]);
		}
	}
	
	/**
	 * 占比类测试
	 */
	private static void test2() {
		System.out.println("======================== 占比类测试 =============================");
		// 比如全院药占比：其中药品费：1000，总费用：5000，占比为0.2，目标值需要达到0.4
		double sj_sum_fz = 1000;
		double sj_sum_fm = 5000;
		double zb_mb = 0.4;
		
		double[] end = calZB(sj_sum_fz, sj_sum_fm, zb_mb);
		
		// 分子增加的值
		double z = end[0];
		// 分母增加的值
		double m = end[1];
		
		System.out.println("需要分配的药品费" + z);
		System.out.println("需要分配的总费用" + m);
		
		// 需要分配的份额，分子
		double assignVal1 = z;
		// 每个维度的实际值 （如每列表示一个科室的实际值）分子
		double[] actualVal1 = { 500, 200, 300 };
		// 需要分配的份额，分母
		double assignVal2 = m;
		// 每个维度的实际值 （如每列表示一个科室的实际值）分母
		double[] actualVal2 = { 2000, 1500, 1500 };
				
		// 每个维度的变量值（如每行表示一个科室，一行的每个元素表示变量值）
		double[][] variableVal = { { 0.1, 0.1 }, { 0.5, 0.5 }, { 5.5, 0.9 } };
		// 每个变量的相关性，1表示正相关（越大分配得越多）， 0 表示负相关（越大分配得越少）
		double[] relatedVal = { 0, 0 };
		// 每个变量变换的范围 （一行表示一个变量变换范围）
		double[][] varyRange = { { 0, 5 }, { 0, 1 } };
		// 每个变量的权重
		double[] weightsVal = { 0.5, 0.5 };
		// SUM为总量，AVG为均值
		TargetCalType targetCalType = TargetCalType.SUM;

		// 计算主方法
		double[] calEnd1 = calTargetVal(assignVal1, actualVal1, variableVal, relatedVal, varyRange, weightsVal,
				targetCalType);
		double[] calEnd2 = calTargetVal(assignVal2, actualVal2, variableVal, relatedVal, varyRange, weightsVal,
				targetCalType);
		
		for (int i = 0; i < actualVal1.length; i++) {
			System.out.println("实际值科室" + (i + 1) + ":" + actualVal1[i] / actualVal2[i]);
		}
		double a2 = 0;
		double b2 = 0;
		for (int i = 0; i < calEnd1.length; i++) {
			a2 += calEnd1[i];
			b2 += calEnd2[i];
			System.out.println("目标值科室" + (i + 1) + ":" + calEnd1[i] / calEnd2[i]);
		}
		
		System.out.println("全院均值：" + a2/b2);
	}
	
	/**
	 * 算法demo
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static void demo() {
		// 全院总费用增加目标值（目标值 - 历史值）如果为负数就为负数，表示降低多少
		double qy_mb_add = 2;
		// 1为总量，2为均值
		int calType = 1;

		// 科室1历史 总费用值
		double ks1_fy_ls = 5;
		// 科室2历史 总费用值
		double ks2_fy_ls = 5;
		// 科室3历史 总费用值
		double ks3_fy_ls = 5;

		// 科室1历史 总费用O/E值
		double ks1_oe_ls = 0.1;
		// 科室2历史 总费用O/E值
		double ks2_oe_ls = 0.5;
		// 科室3历史 总费用O/E值
		double ks3_oe_ls = 5.5;

		// 科室1历史 床位使用率
		double ks1_cw_ls = 0.1;
		// 科室2历史 床位使用率
		double ks2_cw_ls = 0.5;
		// 科室3历史 床位使用率
		double ks3_cw_ls = 0.9;

		// 总费用O/E值 变换区间
		double[] oe_qj = { 0, 5 };
		// 床位使用率 变换区间
		double[] cw_qj = { 0, 1 };

		// 科室1历史 总费用O/E值 变换后值
		double ks1_oe_ls_bh = DataHandleUtil.calStandardAndZoom(ks1_oe_ls, oe_qj, null);
		// 科室2历史 总费用O/E值 变换后值
		double ks2_oe_ls_bh = DataHandleUtil.calStandardAndZoom(ks2_oe_ls, oe_qj, null);
		// 科室3历史 总费用O/E值 变换后值
		double ks3_oe_ls_bh = DataHandleUtil.calStandardAndZoom(ks3_oe_ls, oe_qj, null);

		// 科室1历史 床位使用率 变换后值
		double ks1_cw_ls_bh = DataHandleUtil.calStandardAndZoom(ks1_cw_ls, cw_qj, null);
		// 科室2历史 床位使用率 变换后值
		double ks2_cw_ls_bh = DataHandleUtil.calStandardAndZoom(ks2_cw_ls, cw_qj, null);
		// 科室3历史 床位使用率 变换后值
		double ks3_cw_ls_bh = DataHandleUtil.calStandardAndZoom(ks3_cw_ls, cw_qj, null);

		// 保证所有分配正相关，也就是说越大分配得越多，如果是负相关，则需要用：1-变换后值
		// 由业务得知：OE为负相关，床位使用率为负相关

		// 科室1历史 分配系数，除以2表示权重各占50%
		double ks1_xs = ((1 - ks1_oe_ls_bh) + (1 - ks1_cw_ls_bh)) / 2;
		// 科室2历史 分配系数，除以2表示权重各占50%
		double ks2_xs = ((1 - ks2_oe_ls_bh) + (1 - ks2_cw_ls_bh)) / 2;
		// 科室3历史 分配系数，除以2表示权重各占50%
		double ks3_xs = ((1 - ks3_oe_ls_bh) + (1 - ks3_cw_ls_bh)) / 2;

		System.out.println("科室1系数为：" + ks1_xs);
		System.out.println("科室2系数为：" + ks2_xs);
		System.out.println("科室3系数为：" + ks3_xs);

		switch (calType) {
		case 1:
			// 科室1目标值，历史值 + 分配值（注意分配在可能为负的）
			double ks1_mb_1 = ks1_fy_ls + (qy_mb_add / (ks1_xs + ks2_xs + ks3_xs)) * ks1_xs;
			// 科室2目标值
			double ks2_mb_1 = ks2_fy_ls + (qy_mb_add / (ks1_xs + ks2_xs + ks3_xs)) * ks2_xs;
			// 科室3目标值
			double ks3_mb_1 = ks3_fy_ls + (qy_mb_add / (ks1_xs + ks2_xs + ks3_xs)) * ks3_xs;

			System.out.println("科室1目标值为：" + ks1_mb_1);
			System.out.println("科室2目标值为：" + ks2_mb_1);
			System.out.println("科室3目标值为：" + ks3_mb_1);
			System.out.println("全院历史值为：" + (ks1_fy_ls + ks2_fy_ls + ks3_fy_ls));
			System.out.println("全院目标值为：" + (ks1_mb_1 + ks2_mb_1 + ks3_mb_1));

			break;
		case 2:
			// 得到增量值x, ((科室1历史值 + 科室1分配值) + (科室2历史值 + 科室2分配值) + (科室3历史值 + 科室3分配值)) / 科室个数 =
			// 目标值；其中分配值为我们需要计算的值，通过上面的分配系数可以得到公式
			// 科室1分配值 = 科室1系数 * 增量值x； 科室2分配值 = 科室2系数 * 增量值x； 科室3分配值 = 科室3系数 * 增量值x
			// 固我们能得到 增量值x的公式为：△x = (目标值 * 科室个数 - (科室1历史值 + 科室2历史值 + 科室3历史值)) / (科室1系数 +
			// 科室2系数 + 科室3系数)
			// 注意：因为我们上面用的目标值增量，所以要得到目标值需要使用历史值均值加上目标值增量。
			double x = ((((ks1_fy_ls + ks2_fy_ls + ks3_fy_ls) / 3 + qy_mb_add) * 3)
					- (ks1_fy_ls + ks2_fy_ls + ks3_fy_ls)) / (ks1_xs + ks2_xs + ks3_xs);
			// 科室1目标值，历史值 + 分配值（注意分配在可能为负的）
			double ks1_mb_2 = ks1_fy_ls + x * ks1_xs;
			// 科室2目标值
			double ks2_mb_2 = ks2_fy_ls + x * ks2_xs;
			// 科室3目标值
			double ks3_mb_2 = ks3_fy_ls + x * ks3_xs;

			System.out.println("科室1目标值为：" + ks1_mb_2);
			System.out.println("科室2目标值为：" + ks2_mb_2);
			System.out.println("科室3目标值为：" + ks3_mb_2);
			System.out.println("全院历史值为：" + (ks1_fy_ls + ks2_fy_ls + ks3_fy_ls) / 3);
			System.out.println("全院目标值为：" + (ks1_mb_2 + ks2_mb_2 + ks3_mb_2) / 3);

			break;
		default:
			break;
		}

	}

	/**
	 * 占比类算法，返回分母增加的值及分子增加的值
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
	 * 目标分配算法
	 * 
	 * @param assignVal
	 *            需要分配的份额（注意是增量值，目标值-历史值）
	 * @param actualVal
	 *            每个维度的实际值 （如每列表示一个科室的实际值）
	 * @param variableVal
	 *            每个维度的变量值（如每行表示一个科室，一行的每个元素表示变量值）
	 * @param relatedVal
	 *            每个变量的相关性，1表示正相关（越大分配得越多）， 0 表示负相关（越大分配得越少）
	 * @param varyRange
	 *            每个变量变换的范围 （一行表示一个变量变换范围），如果使用的是数据实际值（actualVal）则数据会经过标准化
	 * @param weightsVal
	 *            每个变量的权重
	 * @param targetCalType
	 *            计算类型，SUM为总量，AVG为均值，如：住院天数均值用AVG，年总费用用SUM
	 * @return
	 */
	public static double[] calTargetVal(double assignVal, double[] actualVal, double[][] variableVal,
			double[] relatedVal, double[][] varyRange, double[] weightsVal, TargetCalType targetCalType) {

		LOGGER.info("需要分配的份额:" + assignVal);
		LOGGER.info("每个维度的实际值 :" + Arrays.toString(actualVal));
		LOGGER.info("每个维度的变量值 :" + Arrays.deepToString(variableVal));
		LOGGER.info("每个变量的相关性 :" + Arrays.toString(relatedVal));
		LOGGER.info("每个变量变换的范围 :" + Arrays.deepToString(varyRange));
		LOGGER.info("每个变量的权重 :" + Arrays.toString(weightsVal));
		LOGGER.info("计算类型 :" + targetCalType);
		
		
		// 数据变换 （包括数据归一化及转为正相关）
		double[][] variableTrVal = new double[variableVal.length][variableVal[0].length];
		for (int i = 0; i < variableVal.length; i++) {
			for (int j = 0; j < variableVal[i].length; j++) {
				// 标准化和归一化
//				double variableTempVal = DataHandleUtil.calStandardAndZoom(variableVal[i][j], varyRange[j], variableVal[i]);
				// 归一化
				double variableTempVal = DataHandleUtil.calZoomZeroToOne(variableVal[i][j], varyRange[j]);
				if (relatedVal[j] == 0) {
					variableTempVal = 1 - variableTempVal;
				}
				variableTrVal[i][j] = variableTempVal;
			}
		}

		// 将变量转换为矩阵
		RealMatrix variableMatrix = new Array2DRowRealMatrix(variableTrVal);
		// 将权重转换为矩阵
		RealMatrix weightsMatrix = new Array2DRowRealMatrix(weightsVal);
		// 将科室实际值转为矩阵
		RealMatrix actualValMatrix = new Array2DRowRealMatrix(actualVal);

		// 计算出系数值矩阵
		RealMatrix coefficientMatrix = variableMatrix.multiply(weightsMatrix);
		// 计算出系数值和
		double[] coefficientDouble = coefficientMatrix.getColumn(0);
		double coefficientSum = 0;
		for (int i = 0; i < coefficientDouble.length; i++) {
			coefficientSum += coefficientDouble[i];
		}

		double[] coefficientTmp = { 0 };
		switch (targetCalType) {
		case SUM:
			// 计算出单位系数所分配的份额
			coefficientTmp[0] = assignVal / coefficientSum;
			break;
		case AVG:
			// 计算出历史值总量
			double actualSum = 0;
			for (int i = 0; i < actualVal.length; i++) {
				actualSum = actualVal[i];
			}
			// 得到增量值x, ((科室1历史值 + 科室1分配值) + (科室2历史值 + 科室2分配值) + (科室3历史值 + 科室3分配值)) / 科室个数 = 目标值；其中分配值为我们需要计算的值，通过上面的分配系数可以得到公式
			// 科室1分配值 = 科室1系数 * 增量值x； 科室2分配值 = 科室2系数 * 增量值x； 科室3分配值 = 科室3系数 * 增量值x
			// 固我们能得到 增量值x的公式为：△x = (目标值 * 科室个数 - (科室1历史值 + 科室2历史值 + 科室3历史值)) / (科室1系数 +科室2系数 + 科室3系数)
			// 注意：因为我们上面用的目标值增量，所以要得到目标值需要使用历史值均值加上目标值增量。
			// double x = ((((ks1_fy_ls + ks2_fy_ls + ks3_fy_ls ) / 3 + qy_mb_add) * 3) - (ks1_fy_ls + ks2_fy_ls + ks3_fy_ls)) / (ks1_xs + ks2_xs + ks3_xs);
			double x = (((actualSum / actualVal.length + assignVal) * actualVal.length) - actualSum) / coefficientSum;
			coefficientTmp[0] = x;
			break;
		default:
			break;
		}

		// 将单位系数分配份额 转为矩阵
		RealMatrix coefficientTmpMatrix = new Array2DRowRealMatrix(coefficientTmp);
		// 计算出每个科室需要分配的份额
		RealMatrix coefficientTmp1Matrix = coefficientMatrix.multiply(coefficientTmpMatrix);
		// 每个科室实际值 加上 分配的份额
		RealMatrix actualEndMatrix = coefficientTmp1Matrix.add(actualValMatrix);
		// 将计算结果转为数组
		double[] calEnddata = actualEndMatrix.getColumn(0);

		return calEnddata;

	}

	

}
