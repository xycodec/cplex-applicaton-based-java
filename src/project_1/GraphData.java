package project_1;

import java.util.ArrayList;
import java.util.Random;

public class GraphData {
	public static final int V=9;//节点数
	public static final int E=V*V;//边的数目(其实多了)
	public static int D=V*(V-1);//业务的个数,要求是V*(V-1),需求数一多,就要求随机生成的链路要越健壮/业务流量不能太大
	
	public static double[][] graph=new double[V][V];//图
	public static double[] h=new double[D];//h[d],业务d的流量需求
	public static int[] s=new int[D];//s[d],业务d的起点(节点号)
	public static int[] t=new int[D];//t[d],业务d的终点(节点号)
	
	//public static edge[] edge_set=new edge[E];//边的集合
	public static ArrayList<edge> edge_list=new ArrayList<>();
	
	public static int[][] a=new int[E][V];//=1,e是v的出边
	public static int[][] b=new int[E][V];//=1,e是v的入边
	
	public static int E_r;//保存实际边的数目
	public static void generate_data() {
		Random r=new Random();
		//先生成一个树
		graph[0][1]=((double)r.nextInt(1000))/1000*200+100;
		graph[1][0]=((double)r.nextInt(1000))/1000*200+100;
		for(int i=1;i<V;++i) {
			graph[i-1][i]=((double)r.nextInt(1000))/1000*200+100;
			graph[i][i-1]=((double)r.nextInt(1000))/1000*200+100;	
		}
		//在树上添加边
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(i==j) continue;
				if(graph[i][j]>0) {//已经有边了
					graph[i][j]+=((double)r.nextInt(1000))/1000*100+100;
				}else {//没边
					if(r.nextDouble()>0.3) {//0.7的概率生成边
						graph[i][j]=((double)r.nextInt(1000))/1000*200+100;
					}
				}
			}
		}
		
		//生成h[],s[],t[]
//		for(int i=0;i<D;++i) {
//			h[i]=r.nextDouble()*70+30;//业务需求
//			s[i]=r.nextInt(V);
//			int tmp=r.nextInt(V);
//			while(tmp==s[i]) tmp=r.nextInt(V);//s,t不能一样
//			t[i]=tmp;
//		}
		
		int cnt=0;
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(i!=j) {
					s[cnt]=i;
					t[cnt]=j;
					h[cnt]=((double)r.nextInt(1000))/1000*50+50;//业务需求
					cnt++;
				}
			}
		}
		
	}
	
	public static void showInfo() {
		System.out.println("Graph:");
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				System.out.printf("%.3f ",graph[i][j]);
			}
			System.out.println();
		}
		System.out.println("Demands:");
		int cnt=0;
		for(int i=0;i<D;++i) {
			if(cnt==10) {
				System.out.println();
				cnt=0;
			}
			cnt++;
			System.out.printf("%d->%d: %.3f | ",s[i],t[i],h[i]);
		}
//		System.out.println("a-b:");
//		for(int e=0;e<E_r;++e) {
//			for(int v=0;v<V;++v) {
//				System.out.printf("%d ",a[e][v]-b[e][v]);
//			}
//			System.out.println();
//		}
		System.out.println();
		System.out.println("edges:"+E_r);
		cnt=0;
		for(int i=0;i<E_r;++i) {
			edge e=edge_list.get(i);
			System.out.printf("%4d:(%3d->%3d) | ",i,e.src,e.dest);
			cnt++;
			if(cnt==10) {
				cnt=0;
				System.out.println();
			}
		}
		System.out.println();
		
	}
	public static void init() {
		//生成/读取 graph[][],s[],t[],h[]
		generate_data();
		
//		//init edge_set[],采用数组形式
//		for(int i=0;i<V;++i) {
//			for(int j=0;j<V;++j) {
//				if(graph[i][j]>0) {//graph[i][j]>0说明i->j有边,当i==j是,graph[i][j]恒为0,也表示没边
//					edge_set[i*V+j]=new edge(i,j,graph[i][j]);
//				}else {
//					edge_set[i*V+j]=new edge(i,j,0);
//				}
//			}
//		}
//		
//		//init a[][],b[][]
//		for(int e=0;e<E;++e) {
//			for(int v=0;v<V;++v) {
//				if(edge_set[e].capacity>0) {
//					if(edge_set[e].src==v) a[e][v]=1;//e是v的出边,即v是e的源节点
//					else a[e][v]=0;
//					if(edge_set[e].dest==v) b[e][v]=1;//e是v的入边,即v是e的目的节点
//					else b[e][v]=0;
//				}else {
//					a[e][v]=0;
//					b[e][v]=0;
//				}
//			}
//		}
		
		//采用ArrayList形式,效率明显更高
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(graph[i][j]>0) {//graph[i][j]>0说明i->j有边,当i==j时,graph[i][j]恒为0,也表示没边
					edge_list.add(new edge(i,j,graph[i][j]));
				}
			}
		}
		
		E_r=edge_list.size();
		for(int e=0;e<E_r;++e) {
			for(int v=0;v<V;++v) {
				if(edge_list.get(e).src==v) a[e][v]=1;//e是v的出边,即v是e的源节点
				else a[e][v]=0;
				if(edge_list.get(e).dest==v) b[e][v]=1;//e是v的入边,即v是e的目的节点
				else b[e][v]=0;
			}
		}
		showInfo();
		
	}
	
}
