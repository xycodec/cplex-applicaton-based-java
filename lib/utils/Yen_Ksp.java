package lib.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
public class Yen_Ksp {
	public static int n=100;//�ڵ���
	public static double[][] edges=new double[n][n];
	public static double[][] edges_tmp=new double[n][n];
	public static ArrayList<Path> ans_path=new ArrayList<>();//K���·��
	public static Map<Path, Boolean> dev_path_sql=new HashMap<Path, Boolean>();//���ڲ�ѯƫ��·���Ƿ��ظ�
	public static Queue<Path> dev_path_queue = new PriorityQueue<>();//���ڴ��ƫ��·��
	
	
	public static boolean[] s=new boolean[n];//����չ�Ľڵ㼯��,false:δ��չ
	public static int[] prev_node=new int[n];//���ǰ��ڵ�
	public static double[] dist=new double[n];//·����Ȩ�غ�
	public static final double INF=10000000;
	public static void Dijkstra(int v0){//v0-> otherNode
		for(int i=0;i<n;++i){//��ʼ��
			dist[i]=edges_tmp[v0][i];
			s[i]=false;
			if(i!=v0&&dist[i]<INF) prev_node[i]=v0;
			else prev_node[i]=-1;
		}
		s[v0]=true;
		dist[v0]=0;
		for(int i=0;i<n-1;++i){//�Ӷ���v0ȷ��n-1�����·��(n-1������)
			double min=INF;
			int u=v0;
			for(int j=0;j<n;++j){//ѡ��ǰ����T�о������·���Ķ��� u
				if(!s[j]&&dist[j]<min){
					u=j;
					min=dist[j];
				}
			}
			s[u]=true;//������u���뵽����s����ʾ�������·�������
			for(int k=0;k<n;++k){
				if(!s[k]&&edges_tmp[u][k]<INF&&dist[u]+edges_tmp[u][k]<dist[k]){
					dist[k]=dist[u]+edges_tmp[u][k];
					prev_node[k]=u;
				}
			}
		}
	}
	
	//·����Ϣ�洢��path������(��ǰ��ڵ㼯����ǰ�����)
	public static ArrayList<Integer> getPath(int s,int t){
		ArrayList<Integer> v=new ArrayList<>();
		if(s==t) return v;
		v.add(t);
		int tmp=t;
		while(tmp!=s){
			tmp=prev_node[tmp];//tmp��ǰ��ڵ�
			v.add(tmp);
		}
		Collections.reverse(v);
		return v;
	}
	
	public static void set_link(ArrayList<Integer> p1,int s_node) {//p1:������path,s_node:p1��ĩ���
		int len=ans_path.size();
		int len_p=p1.size();
		for(int i=0;i<len;++i) {
			ArrayList<Integer> p2=ans_path.get(i).path;
			if(len_p>=p2.size()) continue;
			boolean flag=true;
			for(int j=0;j<len_p;++j) {
				if(p1.get(j)!=p2.get(j)) flag=false;;
			}
			if(flag==false) continue;
			//ǰ���pathһ��,Ȼ��Ϳ���չ������
			for(int k=0;k<n;++k) {
				if(k!=s_node&&edges[s_node][k]!=INF) {//��s_node��չ���k
					if(k==p2.get(len_p)) {//ƥ��ɹ����轫s_node->k ����ΪINF
						edges_tmp[s_node][k]=INF;
					}
				}
			}
		}
	}
	
	public static void recover_link(int s_node) {
		for(int k=0;k<n;++k) {
			edges_tmp[s_node][k]=edges[s_node][k];
		}
	}
	
	
	public static void YEN_ksp(int s,int t,int k) {
		if(s==t||k<=0) return;
		//copy
		for(int i=0;i<n;++i){
			for(int j=0;j<n;++j){
				edges_tmp[i][j]=edges[i][j];
			}
		}
		
		Dijkstra(s);
		Path p=new Path(getPath(s, t), dist[t]);//���·,������·��
		while(k-->0) {
			if(!ans_path.contains(p)) ans_path.add(p);
			else continue;
			int len=p.path.size();
			ArrayList<Integer> path_tmp=new ArrayList<>();//p�Ĳ��ֵ���·��
			for(int i=0;i<len-1;++i) {
				path_tmp.add(p.path.get(i));
		
				edges_tmp[p.path.get(i)][p.path.get(i+1)]=INF;
				set_link(path_tmp,p.path.get(i));
				
				Dijkstra(s);
				
				edges_tmp[p.path.get(i)][p.path.get(i+1)]=edges[p.path.get(i)][p.path.get(i+1)];
				recover_link(p.path.get(i));
				
				if(dist[t]>=INF) continue;//û��·����
				Path pp=new Path(getPath(s,t),dist[t]);//����������·(ƫ��·��)
//				if(!dev_path_sql.containsKey(pp)) {
//					dev_path_queue.add(pp);	
//					dev_path_sql.put(pp.clone(), true);
//				}
				if(!dev_path_queue.contains(pp)) {
					dev_path_queue.add(pp);
				}
			}

			if(dev_path_queue.isEmpty()) break;
			p=dev_path_queue.remove();
			//dev_path_sql.remove(p);
		}
	}
	
	
	public static void main(String[] args) {
		Random r=new Random();
		//������һ����
		edges[0][1]=r.nextDouble()*10+10;
		edges[1][0]=r.nextDouble()*10+10;
		for(int i=1;i<n;++i) {
			edges[i-1][i]=r.nextDouble()*10+10;
			edges[i][i-1]=r.nextDouble()*10+10;	
		}
		//��������ӱ�
		for(int i=0;i<n;++i) {
			for(int j=0;j<n;++j) {
				if(i==j) continue;
				if(edges[i][j]>0) {//�Ѿ��б���
					//edges[i][j]+=r.nextDouble()*50+50;
					;
				}else {//û��
					if(r.nextDouble()>0) {//0.5�ĸ������ɱ�
						edges[i][j]=r.nextDouble()*100+500;
					}else {
						edges[i][j]=INF;
					}
				}
			}
		}
		System.out.println("YEN_ksp start:");
		YEN_ksp(0,55,50);//������0�����55��ǰ50�����·
		
		int len=ans_path.size();
		System.out.println(len);
		for(int i=0;i<len;++i) {
			int len_p=ans_path.get(i).path.size();
			for(int j=0;j<len_p;++j) {
				System.out.printf("%d ",ans_path.get(i).path.get(j));
			}
			System.out.printf("dist: %.3f\n",ans_path.get(i).dist);
		}
		
//		Dijkstra(1);
//		for(int i=0;i<n;++i) {
//			System.out.println(dist[i]);
//		}
		
	}
	
	
}














