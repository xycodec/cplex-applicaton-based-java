package lib.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
public class Yen_Ksp {
	public static int n=100;//节点数
	public static double[][] edges=new double[n][n];
	public static double[][] edges_tmp=new double[n][n];
	public static ArrayList<Path> ans_path=new ArrayList<>();//K最短路径
	public static Map<Path, Boolean> dev_path_sql=new HashMap<Path, Boolean>();//用于查询偏离路径是否重复
	public static Queue<Path> dev_path_queue = new PriorityQueue<>();//用于存放偏离路径
	
	
	public static boolean[] s=new boolean[n];//待扩展的节点集合,false:未扩展
	public static int[] prev_node=new int[n];//存放前向节点
	public static double[] dist=new double[n];//路径的权重和
	public static final double INF=10000000;
	public static void Dijkstra(int v0){//v0-> otherNode
		for(int i=0;i<n;++i){//初始化
			dist[i]=edges_tmp[v0][i];
			s[i]=false;
			if(i!=v0&&dist[i]<INF) prev_node[i]=v0;
			else prev_node[i]=-1;
		}
		s[v0]=true;
		dist[v0]=0;
		for(int i=0;i<n-1;++i){//从顶点v0确定n-1条最短路径(n-1个顶点)
			double min=INF;
			int u=v0;
			for(int j=0;j<n;++j){//选择当前集合T中具有最短路径的顶点 u
				if(!s[j]&&dist[j]<min){
					u=j;
					min=dist[j];
				}
			}
			s[u]=true;//将顶点u加入到集合s，表示它的最短路径已求得
			for(int k=0;k<n;++k){
				if(!s[k]&&edges_tmp[u][k]<INF&&dist[u]+edges_tmp[u][k]<dist[k]){
					dist[k]=dist[u]+edges_tmp[u][k];
					prev_node[k]=u;
				}
			}
		}
	}
	
	//路径信息存储在path数组中(在前向节点集合中前向查找)
	public static ArrayList<Integer> getPath(int s,int t){
		ArrayList<Integer> v=new ArrayList<>();
		if(s==t) return v;
		v.add(t);
		int tmp=t;
		while(tmp!=s){
			tmp=prev_node[tmp];//tmp的前向节点
			v.add(tmp);
		}
		Collections.reverse(v);
		return v;
	}
	
	public static void set_link(ArrayList<Integer> p1,int s_node) {//p1:待检测的path,s_node:p1的末结点
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
			//前面的path一样,然后就看扩展结点的了
			for(int k=0;k<n;++k) {
				if(k!=s_node&&edges[s_node][k]!=INF) {//从s_node扩展结点k
					if(k==p2.get(len_p)) {//匹配成功，需将s_node->k 设置为INF
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
		Path p=new Path(getPath(s, t), dist[t]);//最短路,即迭代路径
		while(k-->0) {
			if(!ans_path.contains(p)) ans_path.add(p);
			else continue;
			int len=p.path.size();
			ArrayList<Integer> path_tmp=new ArrayList<>();//p的部分迭代路径
			for(int i=0;i<len-1;++i) {
				path_tmp.add(p.path.get(i));
		
				edges_tmp[p.path.get(i)][p.path.get(i+1)]=INF;
				set_link(path_tmp,p.path.get(i));
				
				Dijkstra(s);
				
				edges_tmp[p.path.get(i)][p.path.get(i+1)]=edges[p.path.get(i)][p.path.get(i+1)];
				recover_link(p.path.get(i));
				
				if(dist[t]>=INF) continue;//没有路径了
				Path pp=new Path(getPath(s,t),dist[t]);//修正后的最短路(偏离路径)
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
		//先生成一个树
		edges[0][1]=r.nextDouble()*10+10;
		edges[1][0]=r.nextDouble()*10+10;
		for(int i=1;i<n;++i) {
			edges[i-1][i]=r.nextDouble()*10+10;
			edges[i][i-1]=r.nextDouble()*10+10;	
		}
		//在树上添加边
		for(int i=0;i<n;++i) {
			for(int j=0;j<n;++j) {
				if(i==j) continue;
				if(edges[i][j]>0) {//已经有边了
					//edges[i][j]+=r.nextDouble()*50+50;
					;
				}else {//没边
					if(r.nextDouble()>0) {//0.5的概率生成边
						edges[i][j]=r.nextDouble()*100+500;
					}else {
						edges[i][j]=INF;
					}
				}
			}
		}
		System.out.println("YEN_ksp start:");
		YEN_ksp(0,55,50);//求出结点0到结点55的前50条最短路
		
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














