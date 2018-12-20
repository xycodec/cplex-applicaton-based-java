package modified_proj_1;

import java.util.ArrayList;
import java.util.Random;

public class GraphData {
	public static final int V=40;//节点数
	public static final int E=V*V;//边的数目(其实多了)
	public static int D=V*(V-1);//业务的个数,要求是V*(V-1),需求数一多,就要求随机生成的链路要越健壮/业务流量不能太大
	
	public static double[][] graph=new double[V][V];//图
	public static double[][] h=new double[V][V];//h[v][v'] demand(v->v'),v!=v',这要求随机生成的demand不要重复
	public static double[] H=new double[V];//H[v] 源于v的总需求量
	
	public static ArrayList<edge> edge_list=new ArrayList<>();
	
	public static int[][] a=new int[E][V];//=1,e是v的出边
	public static int[][] b=new int[E][V];//=1,e是v的入边
	
	public static int E_r;//保存实际边的数目
	public static void generate_data() {
		Random r=new Random();
		//先生成一个树(确保图的连通性)
		graph[0][1]=r.nextDouble()*200+100;
		graph[1][0]=r.nextDouble()*200+100;
		for(int i=1;i<V;++i) {
			graph[i-1][i]=r.nextDouble()*200+100;
			graph[i][i-1]=r.nextDouble()*200+100;	
		}
		//在树上添加边
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(i==j) continue;
				if(graph[i][j]>0) {//已经有边了
					graph[i][j]+=r.nextDouble()*100+100;
				}else {//没边
					if(r.nextDouble()>0.3) {//0.7的概率生成边
						graph[i][j]=r.nextDouble()*200+100;
					}
				}
			}
		}
		
		//生成h[][]
		int tmp=D;
		for(int i=0;i<tmp;++i) {
			double d=r.nextDouble()*30;//业务需求
			int s=r.nextInt(V);
			int t=r.nextInt(V);
			while(t==s) t=r.nextInt(V);//s,t不能一样
			if(h[s][t]==0)//初次生成该需求
				h[s][t]=d;//demand(s->t)
			else {//多次生成相同的需求了
				h[s][t]+=d/2;
				tmp++;
			}
		}
		//生成H[]
		for(int v1=0;v1<V;++v1) {
			for(int v2=0;v2<V;++v2) {
				if(v1!=v2) {
					H[v1]+=h[v1][v2];
				}
			}
		}
		
	}
	
	public static void showInfo() {
		System.out.println("Graph:");
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				System.out.printf("%.2f ",graph[i][j]);
			}
			System.out.println();
		}
		System.out.println("Demands:");
		for(int v1=0;v1<V;++v1) {
			for(int v2=0;v2<V;++v2) {
				if(v1!=v2&&h[v1][v2]!=0) {
					System.out.printf("%d->%d : %f | ",v1,v2,h[v1][v2]);
				}
			}
			System.out.println();
		}
		
		System.out.println("edges:"+E_r);
		int cnt=0;
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
		//生成/读取 graph[][],h[],H[]
		generate_data();
		
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
