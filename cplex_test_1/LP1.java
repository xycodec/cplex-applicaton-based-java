package cplex_test_1;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class LP1 {
 
	public static void main(String[] args) {
		try {
			IloCplex cplex = new IloCplex(); // creat a model
			//x1,x2,x3
			int[] lb = {0, 0, 0};//下界
			int[] ub = {40, Integer.MAX_VALUE, Integer.MAX_VALUE};//上界
			IloNumVar[] x = cplex.intVarArray(3, lb, ub);
			//x1+2*x2+3*x3
			double[] objvals = {1, 2, 3};//优化目标的系数向量
			cplex.addMaximize(cplex.scalProd(x, objvals));
			
			//约束的系数向量
			double[] coeff1 = {-1, 1, 1};
			double[] coeff2 = {1, -3, 1};
			
			cplex.addLe(cplex.scalProd(x, coeff1), 20);//-x1+x2+x3<=20
			cplex.addLe(cplex.scalProd(x, coeff2), 30);//x1-3*x2+x3<=30
			
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);//得到的解
				for (int j = 0; j < val.length; j++)
					cplex.output().println("x" + (j+1) + "  = " + val[j]);
			}
			cplex.end();
			
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
}

