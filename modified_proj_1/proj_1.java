package modified_proj_1;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import modified_proj_1.GraphData;
public class proj_1 {
	
	public static void main(String[] args) {
		GraphData.init();//数据初始化
		try {
			IloCplex cplex = new IloCplex(); //创建一个模型
			//x1,x2,x3....
			double[] lb = new double[GraphData.E_r*GraphData.V];//下界
			double[] ub = new double[GraphData.E_r*GraphData.V];//上界
			for(int i=0;i<GraphData.E_r;++i) {
				for(int j=0;j<GraphData.V;++j) {
					lb[i*GraphData.V+j]=0;
					ub[i*GraphData.V+j]=GraphData.edge_list.get(i).capacity;//链路容量作为需求流量的上界
				}
			}
			IloNumVar[] x = cplex.numVarArray(GraphData.E_r*GraphData.V, lb, ub);
			for(int i=0;i<GraphData.E_r;++i) {
				for(int j=0;j<GraphData.V;++j) {
					x[i*GraphData.V+j].setName("x("+i+","+j+")");
				}
			}
			IloNumVar a=cplex.numVar(0, 1);//最大链路利用率
			//min{a}
			cplex.addMinimize(a);
			
			//流量守恒约束 
			for(int v=0;v<GraphData.V;++v) {
				IloNumExpr objExpr=cplex.numExpr();
				for(int e=0;e<GraphData.E_r;++e) {
					objExpr=cplex.sum(objExpr,cplex.prod(GraphData.a[e][v],x[e*GraphData.V+v]));
				}
				cplex.addEq(objExpr, GraphData.H[v]);
			}
			
			//流量守恒约束
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
			
			//链路容量约束
			for(int e=0;e<GraphData.E_r;++e) {
				IloNumExpr objExpr=cplex.numExpr();
				for(int v=0;v<GraphData.V;++v) {
					objExpr=cplex.sum(objExpr,x[e*GraphData.V+v]);
				}
				cplex.addLe(objExpr, cplex.prod(GraphData.edge_list.get(e).capacity, a));//<=约束
			}

			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value a = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);//得到的解
				int cnt=0;
				for (int j = 0; j < val.length; j++) {
					if(val[j]>0) {
						cnt++;
						System.out.printf("%10s = %9.5f | ",x[j].getName(),val[j]);//打印非零值的Xev
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
