package project_1;

import java.util.Random;

public class edge{
	public int src,dest;
	public double capacity;
	
	public edge(int src,int dest,double capacity) {
		this.src=src;
		this.dest=dest;
		this.capacity=capacity;
	}

	public edge() {
		super();
	}
	
//	public static void main(String[] args) {
//		Random r=new Random();
//		System.out.println((double)r.nextInt(10000)/10000*100);
//	}
}