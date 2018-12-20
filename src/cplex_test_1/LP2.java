package cplex_test_1;
import java.util.Random;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
public class LP2 {

	public static void main(String[] args) {
		Random r=new Random();
		try {
			IloCplex cplex = new IloCplex();
			double[] lb = new double[10];//下界
			double[] ub = new double[10];//上界
			double[] k=new double[10];
			IloNumVar a=cplex.numVar(10, 100);
			for(int i=0;i<10;++i) {
				lb[i]=0;
				ub[i]=Double.MAX_VALUE;
			}
			IloNumVar[] x = cplex.numVarArray(10,lb,ub);
			
			cplex.addMinimize(a);
			for(int j=0;j<10;++j) {
				IloNumExpr expr=cplex.numExpr();
				for(int i=0;i<10;++i) {
					k[i]=r.nextDouble()*(10+j)+5;
					expr=cplex.sum(expr,cplex.prod(k[i],x[i]));
				}
				cplex.addLe(expr, cplex.prod(r.nextDouble()*10+5,a));
			}
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);//得到的解
				for (int j = 0; j < val.length; j++)
					cplex.output().println(x[j].getName()+" = "+val[j]);
			}
			cplex.end();

		} catch (IloException e) {
			e.printStackTrace();
		} 

	}

}
