package hust.tools.ngram.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import hust.tools.ngram.datastructure.NGram;

/**
 *<ul>
 *<li>Description: 计算GoodTuring折扣系数
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年8月5日
 *</ul>
 */
public class GoodTuringCounts {
	
	private int n;
	
	/**
	 * 折扣阈值
	 */
	private static int maxK = 7;
	
	/**
	 * n元数量的折扣系数
	 * 第一维度代表n元的长度
	 */
	private double[][] disCoeffs;
	
	/**
	 * n元计数与其长度和属于该计数的所有n元类型数量的映射：
	 * 
	 * 			-1元    计数为r的类型数
	 * n元计数r	-2元    计数为r的类型数
	 * 			-3元    计数为r的类型数
	 * 			    ...
	 */
	private HashMap<Integer, HashMap<Integer, Double>> countOfCounts;
	
	/**
	 * 返回给定元组的出现次数的折扣系数
	 * 对于出现次数大于7的n元不进行打折,对于unigram,大于1的不进行打折
	 * 
	 * @param order	给定元组的长度
	 * @param r		给定元组的出现次数
	 * @return		给定元组的出现次数的折扣系数
	 */
	public double getDiscountCoeff(int r, int order) {
		if(order > disCoeffs.length || order < 1)
			return 1.0;
		
		if(r > disCoeffs[order - 1].length || r < 1)
			return 1.0;
		
		if(order == 1 && r != 1)
			return 1.0;
		
		return disCoeffs[order - 1][r - 1];
	}
	
	/**
	 * 返回给定元组出现次数的n元类型数
	 * @param order 元组的长度
	 * @param r		元组出现次数
	 * @return		给定元组出现次数的n元类型数
	 */
	public double getNr(int r, int order) {
		return countOfCounts.get(r).get(order);
	}
	
	public GoodTuringCounts(HashMap<NGram, Integer> nGramCountMap, int n) {
		this.n = n;
		this.countOfCounts = new HashMap<>();
		this.disCoeffs = new double[n][maxK];
		statisticsNGramCountOfTimesSeen(nGramCountMap);
		
//		System.out.println(countOfCounts);
//		for(int i = 0; i < n; i++) {
//			for(int j = 0; j < maxK; j++)
//				System.out.print(disCoeffs[i][j]+"\t");
//			System.out.println();
//		}
	}
	
	/**
	 * 根据n的大小统计出现r次的n元的数量
	 * @param n				n元的最大小
	 * @param nGramCountMap	n元及其计数的索引
	 * @return				根据n的大小统计出现r次的n元的数量
	 */
	private void statisticsNGramCountOfTimesSeen(HashMap<NGram, Integer> nGramCountMap) {
		for(Entry<NGram, Integer> entry : nGramCountMap.entrySet()) {
			NGram nGram = entry.getKey();
			int order = nGram.length();
			int count = entry.getValue();
			
			if(count < maxK + 2) {
				if(countOfCounts.containsKey(count)) {
					if(countOfCounts.get(count).containsKey(order)) {
						double Nr = countOfCounts.get(count).get(order);
						countOfCounts.get(count).put(order, Nr + 1);
					}else
						countOfCounts.get(count).put(order, 1.0);
				}else {
					HashMap<Integer, Double> map = new HashMap<>();
					map.put(order, 1.0);
					countOfCounts.put(count, map);
				}
			}
			
		}//end for
		
		processCountOfCount();
		
		for(int i = 1; i <= n; i++) {
			//计算n元出现次数0-maxK的折扣系数
			double coeff = 0.0;
			
			double commonTerm = (maxK + 1) * countOfCounts.get(maxK + 1).get(i) / countOfCounts.get(1).get(i);
			for(int j = 1; j <= maxK; j++) {
				double coeff0 = (j + 1) * countOfCounts.get(j + 1).get(i) / (j * countOfCounts.get(j).get(i));
				coeff = (coeff0 - commonTerm) / (1.0 - commonTerm);
				
				if(Double.isInfinite(coeff) || coeff <= 0.0 || coeff0 > 1.0) {
					System.err.println("警告: 折扣系数 "+ i+"-"+j +" 越界: " + coeff+". 默认为1.0");
				    coeff = 1.0;
				}
				
				disCoeffs[i - 1][j - 1] = coeff;
			}
		}
		
	}

	/**
	 * 填充n元出现次数r的数量的空缺
	 * @param nGramCountOfTimesSeen n元出现次数r的数量
	 * @param n 最大n元长度
	 */
	private void processCountOfCount(){
		for(int i = 1; i <= n; i++) {
			HashMap<Integer, Double> map = new HashMap<>();
			int max_r = 0;
			for(Entry<Integer, HashMap<Integer, Double>> entry : countOfCounts.entrySet()) {
				int r = entry.getKey();
				if(entry.getValue().containsKey(i)) {
					double Nr = entry.getValue().get(i);
					if(countOfCounts.get(r).containsKey(i)) {
						max_r = max_r > entry.getKey() ? max_r : entry.getKey();
						map.put(r, Nr);
					}
				}//end if
			}//end for

			if(map.size() < 2) {
				System.err.println("训练语料过少,无法使用GoodTuring折扣");
				System.err.println(i+"gram:\n"+map);
				System.exit(0);
			}
			
			//线性回归,log(Nr) = a + b*log(r)
			double[] parameters = linearRegression(map);
			
			/**
			 * 防止训练语料过少，最大次数不足maxK
			 */
			if(max_r < maxK + 2) {
				for(int r = 1; r < maxK + 2; r++) {
					double Nr = Math.pow(10, parameters[0] + parameters[1] * Math.log10(r));
					if(countOfCounts.containsKey(r)) {
						if(!countOfCounts.get(r).containsKey(i))
							countOfCounts.get(r).put(i, Nr);
					}else {
						HashMap<Integer, Double> tempMap = new HashMap<>();
						tempMap.put(i, Nr);
						countOfCounts.put(r, tempMap);
					}
				}
			}
		}	
	}
	
	/**
	 * 将Nr转为Zr = 2Nr /(t − q)
	 * @param map
	 */
	@SuppressWarnings("unused")
	private static void convertToZr(HashMap<Integer, Double> map) {
		List<Integer> list = new ArrayList<>();
		for(int i : map.keySet())
			list.add(i);
		Collections.sort(list);
				
		if(list.size() > 2) {
			for(int i = 1; i < list.size() - 1; i++) {
				int r = list.get(i);
				double Zr = 2 * map.get(r) / (list.get(i + 1) - list.get(i - 1));
				map.put(r, Zr);
			}
		}
	}

	/**
	 * 最小二乘法经验方程求解线性回归
	 * @param data 样本数据
	 * @return 参数
	 */
	private static double[] linearRegression(Map<Integer, Double> data) {
		double[] parameters;	//方程参数，0-截距，1-斜率
		double sum_xy = 0.0;	//自变量因变量之积的和
		double sum_xx = 0.0;	//自变量的平方和
		double _x = 0.0;		//自变量的均值
		double _y = 0.0;		//因变量的均值
				
		//统计变量信息
		for(Entry<Integer, Double> entry : data.entrySet()) {
			sum_xy += Math.log10(entry.getKey()) * Math.log10(entry.getValue());
			sum_xx += Math.log10(entry.getKey()) * Math.log10(entry.getKey());
			_x += Math.log10(entry.getKey());
			_y += Math.log10(entry.getValue());
		}
		_x /= data.size();
		_y /= data.size();
		
		/**
		 * 最小二乘法经验拟合方程解参数
		 */
		parameters = new double[2];
		parameters[1] = (sum_xy - data.size() * _x * _y)/(sum_xx - data.size() * _x * _x);
		parameters[0] = _y - parameters[1] * _x;
		
		return parameters;
	}
}
