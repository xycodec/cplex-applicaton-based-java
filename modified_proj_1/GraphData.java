package modified_proj_1;

import java.util.ArrayList;
import java.util.Random;

public class GraphData {
	public static final int V=40;//�ڵ���
	public static final int E=V*V;//�ߵ���Ŀ(��ʵ����)
	public static int D=V*(V-1);//ҵ��ĸ���,Ҫ����V*(V-1),������һ��,��Ҫ��������ɵ���·ҪԽ��׳/ҵ����������̫��
	
	public static double[][] graph=new double[V][V];//ͼ
	public static double[][] h=new double[V][V];//h[v][v'] demand(v->v'),v!=v',��Ҫ��������ɵ�demand��Ҫ�ظ�
	public static double[] H=new double[V];//H[v] Դ��v����������
	
	public static ArrayList<edge> edge_list=new ArrayList<>();
	
	public static int[][] a=new int[E][V];//=1,e��v�ĳ���
	public static int[][] b=new int[E][V];//=1,e��v�����
	
	public static int E_r;//����ʵ�ʱߵ���Ŀ
	public static void generate_data() {
		Random r=new Random();
		//������һ����(ȷ��ͼ����ͨ��)
		graph[0][1]=r.nextDouble()*200+100;
		graph[1][0]=r.nextDouble()*200+100;
		for(int i=1;i<V;++i) {
			graph[i-1][i]=r.nextDouble()*200+100;
			graph[i][i-1]=r.nextDouble()*200+100;	
		}
		//��������ӱ�
		for(int i=0;i<V;++i) {
			for(int j=0;j<V;++j) {
				if(i==j) continue;
				if(graph[i][j]>0) {//�Ѿ��б���
					graph[i][j]+=r.nextDouble()*100+100;
				}else {//û��
					if(r.nextDouble()>0.3) {//0.7�ĸ������ɱ�
						graph[i][j]=r.nextDouble()*200+100;
					}
				}
			}
		}
		
		//����h[][]
		int tmp=D;
		for(int i=0;i<tmp;++i) {
			double d=r.nextDouble()*30;//ҵ������
			int s=r.nextInt(V);
			int t=r.nextInt(V);
			while(t==s) t=r.nextInt(V);//s,t����һ��
			if(h[s][t]==0)//�������ɸ�����
				h[s][t]=d;//demand(s->t)
			else {//���������ͬ��������
				h[s][t]+=d/2;
				tmp++;
			}
		}
		//����H[]
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
		//����/��ȡ graph[][],h[],H[]
		generate_data();
		
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
