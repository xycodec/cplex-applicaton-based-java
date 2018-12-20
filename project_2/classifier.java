package project_2;

import java.util.Random;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class classifier {
	public static double C=0.2;//惩罚因子
	public static int D=20;//数据的维度
	public static int M=100;//正类数目
	public static int N=100;//负类数目
	public static double[][] x=new double[M+N][D];
	public static double[] y=new double[M+N];
	public static void generate_data() {
		Random r=new Random();
		//生成正类
		for(int i=0;i<M;++i) {
			for(int j=0;j<D;++j) {
				if(r.nextDouble()>0.2) {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10);
				}else {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10)*-1;
				}
				
			}
			y[i]=1;
		}
		//生成负类
		for(int i=M;i<M+N;++i) {
			for(int j=0;j<D;++j) {
				if(r.nextDouble()>0.2) {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10)*-1;
				}else {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10);
				}
			}
			y[i]=-1;
		}
	}
	
	public static void generate_test_data() {
		Random r=new Random();
		//生成正类
		for(int i=0;i<M;++i) {
			for(int j=0;j<D;++j) {
				if(r.nextDouble()>0.1) {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10);
				}else {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10)*-1;
				}
				
			}
			y[i]=1;
		}
		//生成负类
		for(int i=M;i<M+N;++i) {
			for(int j=0;j<D;++j) {
				if(r.nextDouble()>0.3) {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10)*-1;
				}else {
					x[i][j]=(double)r.nextDouble()*r.nextInt(10);
				}
			}
			y[i]=-1;
		}
	}
	
	public static double caculate(double[] w,double b,double[] x) {
		double ans=b;
		for(int i=0;i<D;++i) {
			ans+=w[i]*x[i];
		}
		return ans;
	}
	
	public static void main(String[] args) {
		generate_data();//第一次生成数据作为训练数据
		try {
			IloCplex cplex = new IloCplex(); // creat a model
			cplex.setParam(IloCplex.Param.MIP.Strategy.Search, IloCplex.MIPSearch.Dynamic);
			IloNumVar[] w = cplex.numVarArray(D, Double.MIN_VALUE, Double.MAX_VALUE);
			IloNumVar b = cplex.numVar(Double.MIN_VALUE, Double.MAX_VALUE);
			IloNumExpr objExpr=cplex.numExpr();
			objExpr=cplex.prod(w[0], w[0]);
			for(int i=1;i<D;++i) {
				objExpr=cplex.sum(objExpr, cplex.prod(w[i], w[i]));
			}
			objExpr=cplex.prod(0.5, objExpr);
			
			for(int i=0;i<M+N;++i) {
				IloNumExpr h=cplex.numExpr();//h(xi)
				h=b;
				for(int j=0;j<D;++j) {
					h=cplex.sum(h,cplex.prod(w[j], x[i][j]));
				}
				IloNumExpr tmp=cplex.max(0, cplex.diff(1,cplex.prod(y[i], h)));//Hinge Loss
				objExpr=cplex.sum(objExpr,cplex.prod(C, tmp));//乘上惩罚因子C,并加上去
				cplex.addGe(cplex.prod(y[i], h), cplex.diff(1, tmp));
			}
			
			cplex.addMinimize(objExpr);
			if (cplex.solve()) {
				System.out.println("Solution status = " + cplex.getStatus());
				System.out.println("Solution value object = " + cplex.getObjValue());
			}
			double val_b = cplex.getValue(b);
			System.out.println("b="+val_b);
			double val_w[]=cplex.getValues(w);
			System.out.printf("w=");
			for(int j=0;j<D;++j) {
				System.out.printf("%.4f ",val_w[j]);
			}
			System.out.println();
			generate_test_data();//生成数据作为test case
			int error_num=0;
			for(int i=0;i<M;++i) {//正类
				if(caculate(val_w, val_b, x[i])>0)
					System.out.printf("true ");
				else {
					System.out.printf("false ");
					error_num++;
				}
					
			}
			System.out.println();
			for(int i=M;i<M+N;++i) {//负类
				if(caculate(val_w, val_b, x[i])<0)
					System.out.printf("true ");
				else {
					System.out.printf("false ");
					error_num++;
				}		
			}
			System.out.println();
			System.out.println("error rate: "+error_num+"/200");
			cplex.end();	
			
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}

	}
	
}
