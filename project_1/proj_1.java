package project_1;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import project_1.GraphData;
public class proj_1 {
	public static void main(String[] args) {
		GraphData.init();//数据初始化
		try {
			IloCplex cplex = new IloCplex(); // creat a model
			cplex.setParam(IloCplex.Param.MIP.Strategy.Search, IloCplex.MIPSearch.Dynamic);
			double[] lb = new double[GraphData.E_r*GraphData.D];//下界
			double[] ub = new double[GraphData.E_r*GraphData.D];//上界
			for(int i=0;i<GraphData.E_r;++i) {
				for(int j=0;j<GraphData.D;++j) {
					lb[i*GraphData.D+j]=0;
					ub[i*GraphData.D+j]=GraphData.h[j];//需求量作为需求流量的上界
				}
			}
			//IloNumVar[] x = cplex.numVarArray(GraphData.E_r*GraphData.D, lb, ub);
			IloNumVar[] y = cplex.intVarArray(GraphData.E_r*GraphData.D, 0, 1);
			IloNumVar[] z = cplex.numVarArray(GraphData.E_r*GraphData.D, lb, ub);
			for(int i=0;i<GraphData.E_r;++i) {
				for(int j=0;j<GraphData.D;++j) {
					//x[i*GraphData.D+j].setName("x("+i+","+j+")");
					y[i*GraphData.D+j].setName("y("+i+","+j+")");
					z[i*GraphData.D+j].setName("z("+i+","+j+")");
				}
			}
			IloNumVar a=cplex.numVar(0, 1);//最大链路利用率
			//min{a}
			cplex.addMinimize(a);
			
			for(int e=0;e<GraphData.E_r;++e) {
				for(int d=0;d<GraphData.D;++d) {
					cplex.addEq(z[e*GraphData.D+d], cplex.prod(GraphData.h[d], y[e*GraphData.D+d]));
//					cplex.addLe(z[e*GraphData.D+d],cplex.prod(GraphData.h[d], y[e*GraphData.D+d]));
//					//cplex.addGe(z[e*GraphData.D+d],0);
//					cplex.addLe(z[e*GraphData.D+d],x[e*GraphData.D+d]);
//					cplex.addGe(z[e*GraphData.D+d],cplex.diff(
//							cplex.sum(x[e*GraphData.D+d],cplex.prod(GraphData.h[d], y[e*GraphData.D+d]))
//							, GraphData.h[d]));
				}
			}
			//流量守恒约束
			for(int v=0;v<GraphData.V;++v) {
				for(int d=0;d<GraphData.D;++d) {
					IloNumExpr objExpr=cplex.numExpr();
					for(int e=0;e<GraphData.E_r;++e) {
						objExpr=cplex.sum(objExpr,cplex.prod(GraphData.a[e][v]-GraphData.b[e][v],z[e*GraphData.D+d]));
					}
					if(v==GraphData.s[d]) {
						cplex.addEq(objExpr, GraphData.h[d]);//等式约束
					}else if(v==GraphData.t[d]) {
						//cplex.addEq(objExpr, -1*GraphData.h[d]);//等式约束,冗余
					}else {
						cplex.addEq(objExpr, 0);
						//cplex.addEq(objExpr, cplex.max(0, z[e*GraphData.D+d]));//有max函数用!!!!!
					}
				}
			}
			//链路容量约束
			for(int e=0;e<GraphData.E_r;++e) {
				IloNumExpr objExpr=cplex.numExpr();
				for(int d=0;d<GraphData.D;++d) {
					objExpr=cplex.sum(objExpr,z[e*GraphData.D+d]);
				}
				cplex.addLe(objExpr,cplex.prod(GraphData.edge_list.get(e).capacity,a));//<=约束
			}
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value a = " + cplex.getObjValue());
				double[] val = cplex.getValues(z);//得到的解(E_r*D)
				double[][] router=new double[GraphData.D][GraphData.E_r];
				for(int e=0;e<GraphData.E_r;++e) {
					for(int d=0;d<GraphData.D;++d) {
						router[d][e]=val[e*GraphData.D+d];
					}
				}
//				int cnt=0;
//				for (int j = 0; j < val.length; j++) {
//					if(val[j]>0) {
//						cnt++;
//						System.out.printf("%10s = %9.4f | ",z[j].getName(),val[j]);
//						if(cnt==10) {
//							cnt=0;
//							System.out.println();
//						}
//					}
//				}
				
				for(int d=0;d<GraphData.D;++d) {
					System.out.printf("demand(%d):%d->%d: %.4f:\t",d,GraphData.s[d],GraphData.t[d],GraphData.h[d]);
					for(int e=0;e<GraphData.E_r;++e) {
						if(router[d][e]>0) {
							System.out.printf("router(%d->%d)=%.4f ",GraphData.edge_list.get(e).src,GraphData.edge_list.get(e).dest,router[d][e]);
						}
					}
					System.out.println();
				}
			}
			cplex.end();
			
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}

	}
}
