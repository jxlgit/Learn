package com.jxl.algorithm;

public class KNNData {
	
	/**
	 * 模型数据（原始值）
	 */
	private double[][] modelDataOriginal;
	/**
	 * 预测数据（原始值）
	 */
	private double[] predictionDataOriginal;
	/**
	 * 模型数据（标准化后值）
	 */
	private double[][] modelData;
	/**
	 * 预测数据（标准化后值）
	 */
	private double[] predictionData;
	/**
	 * 计算完成后得到的原始值
	 */
	private double[][] modelDataOriginalCalEnd;
	/**
	 * 提升建议数据
	 */
	private double[] suggestData;
	/**
	 * 提升建议中文描述
	 */
	private String suggest;
	
	public KNNData() {}
	public KNNData(double[][] modelData, double[] predictionData) {
		this.modelDataOriginal = modelData;
		this.predictionDataOriginal = predictionData;
		this.modelData = modelData;
		this.predictionData = predictionData;
	}
	public double[][] getModelDataOriginalCalEnd() {
		return modelDataOriginalCalEnd;
	}
	public void setModelDataOriginalCalEnd(double[][] modelDataOriginalCalEnd) {
		this.modelDataOriginalCalEnd = modelDataOriginalCalEnd;
	}
	public double[][] getModelData() {
		return modelData;
	}
	public void setModelData(double[][] modelData) {
		this.modelData = modelData;
	}
	public double[] getPredictionData() {
		return predictionData;
	}
	public void setPredictionData(double[] predictionData) {
		this.predictionData = predictionData;
	}
	public double[][] getModelDataOriginal() {
		return modelDataOriginal;
	}
	public void setModelDataOriginal(double[][] modelDataOriginal) {
		this.modelDataOriginal = modelDataOriginal;
	}
	public double[] getPredictionDataOriginal() {
		return predictionDataOriginal;
	}
	public void setPredictionDataOriginal(double[] predictionDataOriginal) {
		this.predictionDataOriginal = predictionDataOriginal;
	}
	public double[] getSuggestData() {
		return suggestData;
	}
	public void setSuggestData(double[] suggestData) {
		this.suggestData = suggestData;
	}
	public String getSuggest() {
		return suggest;
	}
	public String getSuggest(String ... str) {
		if (suggestData == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder("");
		sb.append("");
		
		int index = str.length;
		if (str.length > suggestData.length) {
			index = suggestData.length;
		}
		int startSize = sb.length();
		for (int i = 0; i < index; i++) {
			String[] zb = str[i].split(",");
			switch (zb[1]) {
			case "1":
				if (predictionDataOriginal[i] < suggestData[i]) {
					sb.append("增加").append(zb[0]).append("至：").append(suggestData[i]).append(zb[2]).append("，");
				}
				break;
			case "0":
				if (predictionDataOriginal[i] > suggestData[i]) {
					sb.append("减少").append(zb[0]).append("至：").append(suggestData[i]).append(zb[2]).append("，");
				}
				break;
			default:
				break;
			}
		}
		if (startSize == sb.length()) {
			return "";
		} else {
			sb.delete(sb.length() - 1, sb.length());
			sb.append("。");
			return sb.toString();
		}
		
	}
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	

}
