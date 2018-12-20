package modified_proj_1;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import modified_proj_1.GraphData;
public class proj_1 {
	
	public static void main(String[] args) {
		GraphData.init();//���ݳ�ʼ��
		try {
			IloCplex cplex = new IloCplex(); //����һ��ģ��
			//x1,x2,x3....
			double[] lb = new double[GraphData.E_r*GraphData.V];//�½�
			double[] ub = new double[GraphData.E_r*GraphData.V];//�Ͻ�
			for(int i=0;i<GraphData.E_r;++i) {
				for(int j=0;j<GraphData.V;++j) {
					lb[i*GraphData.V+j]=0;
					ub[i*GraphData.V+j]=GraphData.edge_list.get(i).capacity;//��·������Ϊ�����������Ͻ�
				}
			}
			IloNumVar[] x = cplex.numVarArray(GraphData.E_r*GraphData.V, lb, ub);
			for(int i=0;i<GraphData.E_r;++i) {
				for(int j=0;j<GraphData.V;++j) {
					x[i*GraphData.V+j].setName("x("+i+","+j+")");
				}
			}
			IloNumVar a=cplex.numVar(0, 1);//�����·������
			//min{a}
			cplex.addMinimize(a);
			
			//�����غ�Լ�� 
			for(int v=0;v<GraphData.V;++v) {
				IloNumExpr objExpr=cplex.numExpr();
				for(int e=0;e<GraphData.E_r;++e) {
					objExpr=cplex.sum(objExpr,cplex.prod(GraphData.a[e][v],x[e*GraphData.V+v]));
				}
				cplex.addEq(objExpr, GraphData.H[v]);
			}
			
			//�����غ�Լ��
			for(int v1=0;v1<GraphData.V;++v1) {
				for(int v2=0;v2<GraphData.V;++v2) {
					if(v1==v2) continue;
					IloNumExpr objExpr=cplex.numExpr();
					for(int e=0;e<GraphData.E_r;++e) {
						objExpr=cplex.sum(objExpr,cplex.prod(GraphData.b[e][v2]-GraphData.a[e][v2], x[e*GraphData.V+v1]));
					}
					cplex.addEq(objExpr, GraphData.h[v1][v2]);
				}
			}
			
			//��·����Լ��
			for(int e=0;e<GraphData.E_r;++e) {
				IloNumExpr objExpr=cplex.numExpr();
				for(int v=0;v<GraphData.V;++v) {
					objExpr=cplex.sum(objExpr,x[e*GraphData.V+v]);
				}
				cplex.addLe(objExpr, cplex.prod(GraphData.edge_list.get(e).capacity, a));//<=Լ��
			}

			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value a = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);//�õ��Ľ�
				int cnt=0;
				for (int j = 0; j < val.length; j++) {
					if(val[j]>0) {
						cnt++;
						System.out.printf("%10s = %9.5f | ",x[j].getName(),val[j]);//��ӡ����ֵ��Xev
						if(cnt==10) {
							cnt=0;
							System.out.println();
						}
					}
				}
			}
			cplex.end();
			
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}

	}
}
