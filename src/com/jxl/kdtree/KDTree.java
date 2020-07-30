package com.jxl.kdtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.TreeMap;

public class KDTree {

	private Node node;

	/**
	 * 构建树
	 * 
	 * @param input
	 *            输入
	 * @return KDTree树
	 */
	public KDTree build(double[][] input) {
		int n = input.length;
		int m = input[0].length;

		ArrayList<double[]> data = new ArrayList<double[]>(n);
		for (int i = 0; i < n; i++) {
			double[] d = new double[m];
			for (int j = 0; j < m; j++)
				d[j] = input[i][j];
			data.add(d);
		}

		node = new Node();
		buildDetail(node, data, m);

		return this;
	}

	/**
	 * 使用递归构建树
	 * 
	 * @param node
	 *            节点
	 * @param data
	 *            数据
	 * @param dimentions
	 *            数据的维度
	 */
	private void buildDetail(Node node, ArrayList<double[]> data, int dimentions) {
		if (data.size() == 1) {
			node.isLeaf = true;
			node.value = data.get(0);
			return;
		}

		// 选择方差最大的维度
		node.partitionDimention = -1;
		double var = -1;
		double tmpvar;
		for (int i = 0; i < dimentions; i++) {
			tmpvar = UtilZ.variance(data, i);
			if (tmpvar > var) {
				var = tmpvar;
				node.partitionDimention = i;
			}
		}
		// 如果方差=0，表示所有数据都相同，判定为叶子节点
		if (var == 0) {
			node.isLeaf = true;
			node.value = data.get(0);
			return;
		}

		// 选择分割的值,取排序后的中间位置数值
		node.partitionValue = UtilZ.median(data, node.partitionDimention);

		// 获取数据的最大值和最小值
		double[][] maxmin = UtilZ.maxmin(data, dimentions);
		node.min = maxmin[0];
		node.max = maxmin[1];

		int size = (int) (data.size() * 0.55);
		ArrayList<double[]> left = new ArrayList<double[]>(size);
		ArrayList<double[]> right = new ArrayList<double[]>(size);

		for (double[] d : data) {
			if (d[node.partitionDimention] < node.partitionValue) {
				left.add(d);
			} else {
				right.add(d);
			}
		}
		Node leftnode = new Node();
		Node rightnode = new Node();
		node.left = leftnode;
		node.right = rightnode;
		buildDetail(leftnode, left, dimentions);
		buildDetail(rightnode, right, dimentions);
	}

	/**
	 * 查找
	 * 
	 * @param input
	 * @return
	 */
	public double[][] query(double[] input, int k) {
		Stack<Node> stack = new Stack<Node>();
		while (!node.isLeaf) {
			if (input[node.partitionDimention] < node.partitionValue) {
				stack.add(node.right);
				node = node.left;
			} else {
				stack.push(node.left);
				node = node.right;
			}
		}
		
		TreeMap<Double, double[]> treeMap = new TreeMap<Double, double[]>();
		
		/**
		 * 首先按树一路下来，得到一个相对较近的距离，再找比这个距离更近的点
		 */
		double distance = UtilZ.distance(input, node.value);
		treeMap.put(distance, node.value);
		double[] nearest = queryRec(input, distance, stack, treeMap);
		//double[] data = nearest == null ? node.value : nearest;
		
		double[][] dataResult = new double[k][node.value.length];
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

	private double[] queryRec(double[] input, double distance, Stack<Node> stack, TreeMap<Double, double[]> treeMap) {
		double[] nearest = null;
		Node node = null;
		double tdis;
		while (stack.size() != 0) {
			node = stack.pop();
			if (node.isLeaf) {
				tdis = UtilZ.distance(input, node.value);
				if (tdis < distance) {
					distance = tdis;
					nearest = node.value;
				}
				
				putTreeMap(tdis, node.value, treeMap);
			} else {
				/*
				 * 得到该节点代表的超矩形中点到查找点的最小距离mindistance 如果mindistance<distance表示有可能在这个节点的子节点上找到更近的点
				 * 否则不可能找到
				 */
				double mindistance = UtilZ.mindistance(input, node.max, node.min);
				if (mindistance < distance) {
					while (!node.isLeaf) {
						if (input[node.partitionDimention] < node.partitionValue) {
							stack.add(node.right);
							node = node.left;
						} else {
							stack.push(node.left);
							node = node.right;
						}
					}
					tdis = UtilZ.distance(input, node.value);
					if (tdis < distance) {
						distance = tdis;
						nearest = node.value;
					}
					
					putTreeMap(tdis, node.value, treeMap);
				}
			}
		}
		return nearest;
	}

	private void putTreeMap(double tdis, double[] data, TreeMap<Double, double[]> treeMap) {
		// 如果距离相同添加偏移
		while (treeMap.get(tdis) != null) {
			tdis += 0.0000001;
		}
		treeMap.put(tdis, data);
	}
	
	/**
	 * 测试
	 * 
	 */
	public void test() {
		// 模型数据
		double[][] modelData = {  { 1, 1 }, { 2, 2 }, { 3, 3 }, { 4, 4 } };
		// 预测数据
		double[] predictionData = { 3, 3 };
				
		// 构建树
		KDTree tree = build(modelData);

		// kd-tree搜索
		double[][] result = tree.query(predictionData, 3);
		System.out.println(Arrays.deepToString(result));
	}
	
	private void test2(int testDatasize,int modelDatasize){
		// 构建训练数据
        double[][] modelData = new double[modelDatasize][5];
        for(int i=0;i<modelDatasize;i++){
        	modelData[i][0]=Math.random()*modelDatasize;
        	modelData[i][1]=Math.random()*modelDatasize;
        	modelData[i][2]=Math.random()*modelDatasize;
        	modelData[i][3]=Math.random()*modelDatasize;
        	modelData[i][4]=Math.random()*modelDatasize;
        }
        
        // 构建测试数据
        double[][] query = new double[testDatasize][5];
        for(int i=0;i<testDatasize;i++){
            query[i][0]= Math.random()*modelDatasize*1.5;
            query[i][1]= Math.random()*modelDatasize*1.5;
            query[i][2]= Math.random()*modelDatasize*1.5;
            query[i][3]= Math.random()*modelDatasize*1.5;
            query[i][4]= Math.random()*modelDatasize*1.5;
        }
        
        // kd-tree搜索
        KDTree tree = build(modelData);
        for(int i=0;i<testDatasize;i++){
            double[][] result = tree.query(query[i], 3);
            System.out.println(Arrays.deepToString(result));
        }
        
        // 线性搜索
//        for(int i=0;i<testDatasize;i++){
//        	double[][] result = KNNLinear.exp(modelData, query[i], 3);
//            System.out.println(Arrays.deepToString(result));
//        }
       
        
	}

	public static void main(String[] args) {
		KDTree kdTree = new KDTree();
		kdTree.test();
//		kdTree.test2(1,100000);
	}
	
	
	
	
	
	
}