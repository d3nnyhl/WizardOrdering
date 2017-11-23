package main.java.com;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SolverExample {
    public static void main(String [] args) {
        ISolver solver = SolverFactory.newDefault();

        final int MAXVAR = 3;
        final int NBCLAUSES = 4;

        try {
            solver.addClause(new VecInt(new int[] {-1, -2, -3}));
            solver.addClause(new VecInt(new int[] {1}));
            solver.addClause(new VecInt(new int[] {2}));
            //solver.addClause(new VecInt(new int[] {3}));

            IProblem problem = solver;

            if (problem.isSatisfiable()) {
                System.out.println("Satisfiable !");
                int[] vars = problem.model();
                for (int i = 0; i <vars.length; i++) {
                    System.out.print(vars[i] + " ");
                }
            } else {
                System.out.println("Unsatisfiable !");
            }
        } catch (TimeoutException e) {
            System.out.println("Error: Timeout");
        } catch (ContradictionException c) {
            System.out.println("Unsatisfiable !");
        }
    }
}
