package jama;

import Jama.Matrix;  // 导入Jama包中的Matrix类

public class TestJama {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("调用jama包完成矩阵运算");
		
		double [][] array = {
				{-1, 1, 0},
				{-4, 3, 0},
				{1, 0, 2}
		};
		
		System.out.println("特征分解");
		// http://zhidao.baidu.com/link?url=KZY21A85_YfXLCw-4dZlES5AdgjXkQg4uuogjLsv6WvGV3VM9sBkTOQUofPpEzRqSO0WwlVrBMi8e-9hd4Rhoa
		Matrix A = new Matrix(array); 
		A.eig().getD().print(4, 2);   // 对角元素是特征值，4是列的宽度，2代表小数点后的位数
		A.eig().getV().print(4, 2);   // 特征向量

		System.out.println("矩阵维数");
		int rowNum = A.getRowDimension();  // 矩阵行数
		int colNum = A.getColumnDimension(); 
		System.out.println(rowNum + " " + colNum); 
		
		System.out.println("行列式");
		double detNum = A.det();   // 行列式
		System.out.println(detNum); 
	}

}
