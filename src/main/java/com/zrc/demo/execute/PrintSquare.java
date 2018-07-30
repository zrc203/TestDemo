package com.zrc.demo.execute;

public class PrintSquare {
	public static void main(String[] args) {
//		int n = 3;
//		int[] each = new int[n*n];
//		for(int i = 0;i<n*n;i++) {
//			each[i]=i+1;
//		}
//		int[][] sq = new int[n][n];
//		int index = 0;
//		for(int i = 0;i<n;i++) {
//			for(int j = 0;j<n;j++) {
//				//sq[i][j]=each[index];
//				index++;
//			}
//		}
//		sq[0][0]=each[0];
//		sq[0][1]=each[1];
//		sq[0][2]=each[2];
//		sq[1][2]=each[3];
//		sq[2][2]=each[4];
//		sq[2][1]=each[5];
//		sq[2][0]=each[6];
//		sq[1][0]=each[7];
//		sq[1][1]=each[8];
//		for(int i = 0;i<n;i++) {
//			for(int j = 0;j<n;j++) {
//				System.out.print(sq[i][j]+"\t");
//			}
//			System.out.println();
//		}
		
		draw(0,0,3);
	}
	
	public static void draw(int i,int j,int n) {
		int a=i,b=j;
		while(j<n) {
			System.out.println(i+""+j);
			if(j==n-1)
				break;
			j++;
		}
		while(i<n) {
			i++;
			System.out.println(i+""+j);
			if(i==n-1)
				break;
		}
		while(j>b) {
			j--;
			System.out.println(i+""+j);
			if(j==b)
				break;
		}
		while(i>a) {
			i--;
			if(i==j)
				break;
			System.out.println(i+""+j);
			if(i==a)
				break;
		}
	}
}
