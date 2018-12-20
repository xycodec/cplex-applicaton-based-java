package project_1;

import java.util.ArrayList;
import java.util.Random;

public class GraphData {
	public static final int V=9;//�ڵ���
	public static final int E=V*V;//�ߵ���Ŀ(��ʵ����)
	public static int D=V*(V-1);//ҵ��ĸ���,Ҫ����V*(V-1),������һ��,��Ҫ��������ɵ���·ҪԽ��׳/ҵ����������̫��
	
	public static double[][] graph=new double[V][V];//ͼ
	public static double[] h=new double[D];//h[d],ҵ��d����������
	public static int[] s=new int[D];//s[d],ҵ��d�����(�ڵ��)
	public static int[] t=new int[D];//t[d],ҵ��d���յ�(�ڵ��)
	
	//public static edge[] edge_set=new edge[E];//�ߵļ���
	public static ArrayList<edge> edge_list=new ArrayList<>();
	
	public static int[][] a=new int[E][V];//=1,e��v�ĳ���
	public static int[][] b=new int[E][V];//=1,e��v�����
	
	public static int E_r;//����ʵ�ʱߵ���Ŀ
	public static void generate_data() {
		Random r=new Random();
		//������һ����
		graph[0][1]=((double)r.nextInt(1000))/1000*200+100;
		graph[1][0]=((double)r.nextInt(1000))/1000*200+100;
		for(int i=1;i<V;++i) {
			graph[i-1][i]=((double)r.nextInt(1000))/1000*200+100;
			graph[i][i-1]=((double)r.nextInt(1000))/1000*200+100;	
		}
		//��������ӱ�
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(i==j) continue;
				if(graph[i][j]>0) {//�Ѿ��б���
					graph[i][j]+=((double)r.nextInt(1000))/1000*100+100;
				}else {//û��
					if(r.nextDouble()>0.3) {//0.7�ĸ������ɱ�
						graph[i][j]=((double)r.nextInt(1000))/1000*200+100;
					}
				}
			}
		}
		
		//����h[],s[],t[]
//		for(int i=0;i<D;++i) {
//			h[i]=r.nextDouble()*70+30;//ҵ������
//			s[i]=r.nextInt(V);
//			int tmp=r.nextInt(V);
//			while(tmp==s[i]) tmp=r.nextInt(V);//s,t����һ��
//			t[i]=tmp;
//		}
		
		int cnt=0;
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(i!=j) {
					s[cnt]=i;
					t[cnt]=j;
					h[cnt]=((double)r.nextInt(1000))/1000*50+50;//ҵ������
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
		//����/��ȡ graph[][],s[],t[],h[]
		generate_data();
		
//		//init edge_set[],����������ʽ
//		for(int i=0;i<V;++i) {
//			for(int j=0;j<V;++j) {
//				if(graph[i][j]>0) {//graph[i][j]>0˵��i->j�б�,��i==j��,graph[i][j]��Ϊ0,Ҳ��ʾû��
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
//					if(edge_set[e].src==v) a[e][v]=1;//e��v�ĳ���,��v��e��Դ�ڵ�
//					else a[e][v]=0;
//					if(edge_set[e].dest==v) b[e][v]=1;//e��v�����,��v��e��Ŀ�Ľڵ�
//					else b[e][v]=0;
//				}else {
//					a[e][v]=0;
//					b[e][v]=0;
//				}
//			}
//		}
		
		//����ArrayList��ʽ,Ч�����Ը���
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(graph[i][j]>0) {//graph[i][j]>0˵��i->j�б�,��i==jʱ,graph[i][j]��Ϊ0,Ҳ��ʾû��
					edge_list.add(new edge(i,j,graph[i][j]));
				}
			}
		}
		
		E_r=edge_list.size();
		for(int e=0;e<E_r;++e) {
			for(int v=0;v<V;++v) {
				if(edge_list.get(e).src==v) a[e][v]=1;//e��v�ĳ���,��v��e��Դ�ڵ�
				else a[e][v]=0;
				if(edge_list.get(e).dest==v) b[e][v]=1;//e��v�����,��v��e��Ŀ�Ľڵ�
				else b[e][v]=0;
			}
		}
		showInfo();
		
	}
	
}
