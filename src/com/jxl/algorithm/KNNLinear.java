package com.jxl.algorithm;

import java.util.Arrays;
import java.util.TreeMap;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * 
 * @Description: TODO(K-近邻算法，线性搜索方式)
 * @author: jxl
 * @date: 2019年4月30日 下午5:20:50
 * @version V1.0
 */
public class KNNLinear {

	// 模型数据
	private static double[][] modelData = {  
											 { 1000, 1 , 4}, 
											 { 3000, 2 , 3}, 
											 { 2000, 3 , 6}, 
											 { 4000, 4 , 1} 
										  };
	
	// 预测数据
	private static double[] predictionData = { 1000, 1};

	public static void main(String[] args) {
		
		KNNData knnData = knnStandAndZoom(modelData, predictionData);
		double[][] data = exp(knnData, 2);
		
		double[] predictionData2 = { 1000, 1, 10};
		KNNData knnData2 = knnStandAndZoom(modelData, predictionData2);
		double[][] data2 = expSuggest(knnData2);
		// 1是增加，0是减少
		// 第一个参数是指标，第二个参数是增加和减少标志，第三个参数是单位
		System.out.println(knnData2.getSuggest("总费用,1,元", "住院天数,1,天"));
		System.out.println(Arrays.toString(knnData2.getSuggestData()));
//		
		System.out.println(Arrays.deepToString(data));
				 
		/**
		 * 效率测试
		 */
		// 构建训练数据
		int modelDatasize = 800;
		double[][] modelData = new double[modelDatasize][5];
		for (int i = 0; i < modelDatasize; i++) {
			modelData[i][0] = Math.random() * modelDatasize;
			modelData[i][1] = Math.random() * modelDatasize;
			modelData[i][2] = Math.random() * modelDatasize;
			modelData[i][3] = Math.random() * modelDatasize;
			modelData[i][4] = Math.random() * modelDatasize;
		}
		// 构建测试数据
		double[] query = new double[5];
		query[0] = Math.random() * modelDatasize * 1.5;
		query[1] = Math.random() * modelDatasize * 1.5;
		query[2] = Math.random() * modelDatasize * 1.5;
		query[3] = Math.random() * modelDatasize * 1.5;
		query[4] = Math.random() * modelDatasize * 1.5;

//		long start = System.currentTimeMillis();
//		double[][] result = exp(modelData, query, 1);
//		System.out.println(Arrays.deepToString(result));
//		long timelinear = System.currentTimeMillis() - start;
//		System.out.println("linear搜索耗时（毫秒）:" + timelinear);
	}

	/**
	 * 对KNN数据进行数据标准化和归一化
	 * 
	 * @param modelData
	 *            模型数据
	 * @param predictionData
	 *            预测数据
	 * @return
	 */
	public static KNNData knnStandAndZoom(double[][] modelData, double[] predictionData) {
		KNNData knnData = new KNNData();
		knnData.setModelDataOriginal(modelData);
		knnData.setPredictionDataOriginal(predictionData);
		boolean isColSame = true;
		if (predictionData.length < modelData[0].length) {
			isColSame = false;
			predictionData = Arrays.copyOf(predictionData, predictionData.length + 1);
		}

		double[][] allData = Arrays.copyOf(modelData, modelData.length + 1);
		allData[modelData.length] = predictionData;

		// 转换为矩阵
		RealMatrix allMatrix = new Array2DRowRealMatrix(allData);
		// 列
		int c = allMatrix.getColumnDimension();
		if (!isColSame) {
			c = c - 1;
		}
		for (int i = 0; i < c; i++) {
			double[] cArrays = allMatrix.getColumn(i);
			double[] cStd = DataHandleUtil.calStandardization(cArrays);
			double[] cEnd = DataHandleUtil.calZoomZeroToOne(cStd);

			for (int j = 0; j < cEnd.length; j++) {
				allMatrix.multiplyEntry(j, i, 0);
				allMatrix.addToEntry(j, i, cEnd[j]);
			}
		}

		double[][] end = allMatrix.getData();

		modelData = Arrays.copyOf(end, end.length - 1);
		if (!isColSame) {
			predictionData = Arrays.copyOf(end[end.length - 1], end[end.length - 1].length - 1);
		} else {
			predictionData = Arrays.copyOf(end[end.length - 1], end[end.length - 1].length);
		}

		knnData.setModelData(modelData);
		knnData.setPredictionData(predictionData);
		return knnData;
	}

	/**
	 * 目标管理指导建议计算
	 * 
	 * @param knnData
	 * @param k
	 * @return
	 */
	public static double[][] expSuggest(KNNData knnData) {
		double[][] modelData = knnData.getModelData();
		double[] predictionData = knnData.getPredictionData();
		double[][] modelDataOriginal = knnData.getModelDataOriginal();
		double[] predictionDataOriginal = knnData.getPredictionDataOriginal();

		// 在数组最后一列增加下标
		double[][] modelData_ = new double[modelData.length][modelData[0].length + 1];
		for (int i = 0; i < modelData.length; i++) {
			for (int j = 0; j < modelData[i].length; j++) {
				modelData_[i][j] = modelData[i][j];
			}
			modelData_[i][modelData[i].length] = i;
		}
		modelData = modelData_;

		// 储存距离
		TreeMap<Double, double[]> treeMap = new TreeMap<Double, double[]>();
		for (int i = 0; i < modelData.length; i++) {
			double tdis = distance(predictionData, modelData[i]);
			putTreeMap(tdis, modelData[i], treeMap);
		}

		// 在模型中找到和预测值临近的数据
		double pData = predictionDataOriginal[predictionDataOriginal.length - 1];
		double[] pmData = new double[modelDataOriginal.length + 1];
		for (int i = 0; i < modelDataOriginal.length; i++) {
			pmData[i] = modelDataOriginal[i][modelDataOriginal[i].length - 1];
		}
		pmData[pmData.length - 1] = pData;
		Arrays.sort(pmData);
		double pEnd = -1;
		if (pmData.length > 1) {
			for (int i = 0; i < pmData.length; i++) {
				if (pmData[i] == pData) {
					if (i == 0) {
						pEnd = pmData[i + 1];
					} else {
						pEnd = pmData[i - 1];
					}
				}
			}
		}

		double[][] modelDataOriginalCalEnd = null;
		if (pEnd != -1) {
			// 在模型中找到和预测值相邻的数据
			modelDataOriginalCalEnd = new double[1][modelData[0].length];
			int i = 0;
			boolean isHaveSuggest = false;
			for (Double key : treeMap.keySet()) {
				int index = (int) treeMap.get(key)[modelData[0].length - 1];
				double[] mData = modelDataOriginal[index];
				if (mData[mData.length - 1] == pEnd) {
					modelDataOriginalCalEnd[i] = modelDataOriginal[index];
					i++;
					isHaveSuggest = true;
					break;
				}
			}

			// 设置建议
			if (isHaveSuggest) {
				knnData.setModelDataOriginalCalEnd(modelDataOriginalCalEnd);
				// 设置建议数据(如果有多条数据取其均值)
				double[] suggestData = new double[modelDataOriginalCalEnd[0].length];
				for (int m = 0; m < modelDataOriginalCalEnd[0].length; m++) {
					double sum = 0;
					for (int n = 0; n < modelDataOriginalCalEnd.length; n++) {
						sum += modelDataOriginalCalEnd[n][m];
					}
					suggestData[m] = sum / modelDataOriginalCalEnd.length;
				}
				knnData.setSuggestData(suggestData);
			}

		}

		knnData.setModelData(null);
		knnData.setModelDataOriginal(null);
		modelData = null;
		predictionData = null;
		modelDataOriginal = null;
		predictionDataOriginal = null;
		return modelDataOriginalCalEnd;
	}

	/**
	 * 线性查找方式
	 * 
	 * @param modelData
	 *            历史数据
	 * @param predictionData
	 *            预测数据
	 * @param k
	 *            k值
	 * @return
	 */
	public static double[][] exp(KNNData knnData, int k) {
		double[][] modelData = knnData.getModelData();
		double[] predictionData = knnData.getPredictionData();

		if (predictionData.length == modelData[0].length) {
			predictionData = Arrays.copyOf(predictionData, predictionData.length - 1);
		}

		// 在数组最后一列增加下标
		double[][] modelData_ = new double[modelData.length][modelData[0].length + 1];
		for (int i = 0; i < modelData.length; i++) {
			for (int j = 0; j < modelData[i].length; j++) {
				modelData_[i][j] = modelData[i][j];
			}
			modelData_[i][modelData[i].length] = i;
		}
		modelData = modelData_;

		// 计算距离
		TreeMap<Double, double[]> treeMap = new TreeMap<Double, double[]>();
		for (int i = 0; i < modelData.length; i++) {
			double tdis = distance(predictionData, modelData[i]);
			putTreeMap(tdis, modelData[i], treeMap);
		}

		// 设置计算完成后原始数据
		double[][] modelDataOriginalCalEnd = new double[k][modelData[0].length];
		int i = 0;
		for (Double key : treeMap.keySet()) {
			int index = (int) treeMap.get(key)[modelData[0].length - 1];
			modelDataOriginalCalEnd[i] = knnData.getModelDataOriginal()[index];
			i++;
			if (i >= k) {
				break;
			}
		}
		knnData.setModelDataOriginalCalEnd(modelDataOriginalCalEnd);

		knnData.setModelData(null);
		knnData.setModelDataOriginal(null);
		modelData = null;
		predictionData = null;
		
		return modelDataOriginalCalEnd;
	}

	/**
	 * 线性查找方式
	 * 
	 * @param modelData
	 *            历史数据
	 * @param predictionData
	 *            预测数据
	 * @param k
	 *            k值
	 * @return
	 */
	public static double[][] exp(double[][] modelData, double[] predictionData, int k) {
		if (predictionData.length == modelData[0].length) {
			predictionData = Arrays.copyOf(predictionData, predictionData.length - 1);
		}

		TreeMap<Double, double[]> treeMap = new TreeMap<Double, double[]>();
		for (int i = 0; i < modelData.length; i++) {
			double tdis = distance(predictionData, modelData[i]);
			putTreeMap(tdis, modelData[i], treeMap);
		}

		double[][] dataResult = new double[k][modelData[0].length];
		int i = 0;
		for (Double key : treeMap.keySet()) {
			dataResult[i] = treeMap.get(key);
			i++;
			if (i >= k) {
				break;
			}
		}
		return dataResult;
	}

	/**
	 * 向TreeMap中添加数据，如果出现key重复，需要添加偏移
	 * 
	 * @param tdis
	 *            key
	 * @param data
	 *            value
	 * @param treeMap
	 *            TreeMap
	 */
	private static void putTreeMap(double tdis, double[] data, TreeMap<Double, double[]> treeMap) {
		// 如果距离相同添加偏移
		while (treeMap.get(tdis) != null) {
			tdis += 0.0000001;
		}
		treeMap.put(tdis, data);
	}

	/**
	 * 计算欧几里得距离
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static double distance(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += Math.pow(a[i] - b[i], 2);
		}
		double sqrtSum = Math.sqrt(sum);
		return sqrtSum;
	}

}
