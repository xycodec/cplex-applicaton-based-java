package examples;
/* --------------------------------------------------------------------------
 * File: LPex3.java
 * Version 12.8.0
 * --------------------------------------------------------------------------
 * Licensed Materials - Property of IBM
 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55 5655-Y21
 * Copyright IBM Corporation 2001, 2017. All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * --------------------------------------------------------------------------
 *
 * LPex3.java, example of adding constraints to solve a problem
 *
 *   minimize  c*x
 *   subject to  Hx = d
 *               Ax = b
 *               l <= x <= u
 *   where
 *
 *   H = (  0  0  0  0  0  0  0 -1 -1 -1  0  0 )  d = ( -1 )
 *       (  1  0  0  0  0  1  0  1  0  0  0  0 )      (  4 )
 *       (  0  1  0  1  0  0  1  0  1  0  0  0 )      (  1 )
 *       (  0  0  1  0  1  0  0  0  0  1  0  0 )      (  1 )
 *       (  0  0  0  0  0 -1 -1  0  0  0 -1  1 )      ( -2 )
 *       (  0  0  0 -1 -1  0  1  0  0  0  1  0 )      ( -2 )
 *       ( -1 -1 -1  0  0  0  0  0  0  0  0 -1 )      ( -1 )
 *
 *   A = (  0  0  0  0  0  0  0  0  0  0  2  5 )  b = (  2 )
 *       (  1  0  1  0  0  1  0  0  0  0  0  0 )      (  3 )
 *
 *   c = (  1  1  1  1  1  1  1  0  0  0  2  2 )
 *   l = (  0  0  0  0  0  0  0  0  0  0  0  0 )
 *   u = ( 50 50 50 50 50 50 50 50 50 50 50 50 )
 *
 *  Treat the constraints with A as the complicating constraints, and
 *  the constraints with H as the "simple" problem.
 *
 *  The idea is to solve the simple problem first, and then add the
 *  constraints for the complicating constraints, and solve with dual.
 */

import ilog.concert.*;
import ilog.cplex.*;


public class LPex3 {
  public static void main(String[] args) {
    try {
      int nvars = 12;//维度为12

      IloCplex cplex = new IloCplex();
      IloLPMatrix lp = cplex.addLPMatrix();

      // add empty corresponding to new variables columns to lp
      IloNumVar[] x = cplex.numVarArray(cplex.columnArray(lp, nvars),
                                        0.0, 50.0);//构造向量x,指定下界与上界
      
      // add rows to lp
      double[]   d = {-1.0, 4.0, 1.0, 1.0, -2.0, -2.0, -1.0};//d:7*1
      double[][] valH = {{-1.0, -1.0, -1.0},
                         { 1.0,  1.0,  1.0},
                         { 1.0,  1.0,  1.0,  1.0},
                         { 1.0,  1.0,  1.0},
                         {-1.0, -1.0, -1.0,  1.0},
                         {-1.0, -1.0,  1.0},
                         {-1.0, -1.0, -1.0, -1.0}};
      int[][]    indH = {{7, 8, 9},
                         {0, 5, 7},
                         {1, 3, 6, 8},
                         {2, 4, 9},
                         {5, 6, 10, 11},
                         {3, 4, 10},
                         {0, 1, 2, 11}};
      //Hx=d,前面两个参数是上界与下界,此处上界与下界都是b,即 b=< Hx <=b,->Hx=b,这是一种技巧
      lp.addRows(d, d, indH, valH);//使用intH指定的位置,valH提供的数据,来构造H矩阵
      //System.out.println(lp.toString());//addRows()应该表明按行填充数据
      // add the objective function
      double[] objvals = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0,
                          2.0, 2.0};//优化目标的系数矩阵c
      cplex.addMinimize(cplex.scalProd(x, objvals));//min{c*x}

      // Solve initial problem with the network optimizer,可能是加速运算的东西,去掉也ok
      cplex.setParam(IloCplex.Param.RootAlgorithm, IloCplex.Algorithm.Network);
      cplex.solve();//求解
      System.out.println("After network optimization, objective is "
                         + cplex.getObjValue());

      // add rows from matrix A to lp
      double[]   b = {2.0, 3.0};
      double[][] valA = {{2.0, 5.0},
                         {1.0, 1.0, 1.0}};
      int[][]    indA = {{10, 11},
                         {0, 2, 5}};
      //Ax=b
      lp.addRows(b, b, indA, valA);

      // Because the problem is dual feasible with the rows added, using
      // the dual simplex method is indicated.
      cplex.setParam(IloCplex.Param.RootAlgorithm, IloCplex.Algorithm.Dual);
      if ( cplex.solve() ) {
        System.out.println("Solution status = " + cplex.getStatus());
        System.out.println("Solution value  = " + cplex.getObjValue());

        double[] sol = cplex.getValues(lp);
        for (int j = 0; j < nvars; ++j) {
           System.out.println("Variable " + j + ": Value = " + sol[j]);
        }
      }
      cplex.end();
    }
    catch (IloException e) {
      System.err.println("Concert exception '" + e + "' caught");
    }
  }
}
