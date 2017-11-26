package main.java.com.wizardordering;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Assert;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WizardOrderingSolver {
    // Bi-directional mapping between a wizard ID (int) and a wizard name (String).
    private BiMap<Integer, String> wizIdToName;
    // Bi-directional mapping between a variable ID (int) and pair of Wizard IDs.
    //private BiMap<Integer, WizardVariable> varIdToVar;
    private BiMap<Integer, List<Integer>> varIdToWizID;
    private ISolver solver;
    private File inputFile;
    private String outFileName;
    private Set<String> wizardSet;
    private List<int[]> clausesByConstraints;
    private List<int[]> clausesByImplication;
    private int[] assignments;

    // For statistics
    private int numConstraints;
    private double elapsedTime;

    /**
     * Constructor
     * @param inputFile, the file from where the constraints will be read.
     */
    public WizardOrderingSolver(File inputFile) {
        this.solver = SolverFactory.newDefault();
        this.inputFile = inputFile;
        this.wizardSet = new HashSet<>();
        this.clausesByConstraints = new ArrayList<>();
        this.clausesByImplication = new ArrayList<>();
        this.numConstraints = 0;
    }

    /**
     *  Returns file name.
     * @return filename
     */
    public String getFileName() {
        return this.inputFile.getName();
    }

    /**
     * preProcess
     * Reduces the wizard ordering problem to an instance of 3-SAT.
     */
    public void preProcess() {
        this.generateVariables();
        this.generateConstraintClauses();
        this.generateImplicationClauses();
    }

    /**
     * Given two wizard IDs, return the corresponding variableID.
     * @param first, wizardID
     * @param second, wizardID
     * @return variableID(min(first, second), max(first,second)) * k
     *         where k = (first < second ? 1 : -1)
     */
    private int getVariableIDbyWizard(int first, int second) {
        int varID = 0;
        List<Integer> key = new ArrayList<>();

        if (first < second) {
            key.add(first);
            key.add(second);
            varID = varIdToWizID.inverse().get(key);
        } else {
            key.add(second);
            key.add(first);
            varID = varIdToWizID.inverse().get(key) * -1;
        }
        return varID;
    }

    /**
     * Creates the set of wizard names mapped to a wizardID
     * Generates variables for each pair of wizard IDs, A and B, where A < B.
     * The variableID corresponding to (B, A) is denoted as -1 * variableID
     * corresponding to (A, B).
     * Moreover, +(A, B) == -(B, A) <=> age(A) < age(B)
     *           -(A, B) == +(B, A) <=> age(A) > age(B)
     */
    private void generateVariables() {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(this.inputFile));

            // Read number of wizards and constraints respectively.
            int numWizards = Integer.parseInt(buf.readLine().trim());
            this.numConstraints = Integer.parseInt(buf.readLine().trim());


            for (int i = 0; i < numConstraints; i++) {
                StringTokenizer st = new StringTokenizer(buf.readLine());
                while (st.hasMoreTokens()) {
                    wizardSet.add(st.nextToken());
                    if (wizardSet.size() == numWizards)
                        break;
                }
            }

            // Create mapping between wizard names and integer value.
            int wizId = 1;
            this.wizIdToName = HashBiMap.create(numWizards);
            for (String name : wizardSet) {
                this.wizIdToName.put(wizId++, name);
            }

            // Create mapping between variables and integer value.
            int varId = 1;
            //this.varIdToVar = HashBiMap.create(this.numWizards * (this.numWizards - 1) / 2);
            this.varIdToWizID = HashBiMap.create(numWizards * (numWizards - 1) / 2);
            for (int i = 1; i <= numWizards; i++) {
                for (int j = i + 1; j <= numWizards; j++) {
                    List<Integer> lst = new ArrayList<>();
                    lst.add(i);
                    lst.add(j);
                    this.varIdToWizID.put(varId++, lst);
                }
            }

            buf.close();
        } catch (IOException e) {
            System.out.println("Error reading wizard names.");
            System.exit(-1);
        }
    }

    /**
     * generateConstraintClauses
     * For each constraint read from the input file:
     *      Let a, b, c be the wizardIDs corresponding to the three names read, respectively.
     */
    private void generateConstraintClauses() {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(this.inputFile));

            // Skip to start of constraints.
            buf.readLine();
            buf.readLine();

            for (int i = 0; i < numConstraints; i++) {
                StringTokenizer st = new StringTokenizer(buf.readLine());

                int a = this.wizIdToName.inverse().get(st.nextToken());
                int b = this.wizIdToName.inverse().get(st.nextToken());
                int c = this.wizIdToName.inverse().get(st.nextToken());

                int a_c = getVariableIDbyWizard(a, c);
                int b_c = getVariableIDbyWizard(b, c);
                this.clausesByConstraints.add(new int[]{a_c, -1 * b_c});
                this.clausesByConstraints.add(new int[]{-1 * a_c, b_c});
            }
            Assert.assertEquals(this.numConstraints * 2, this.clausesByConstraints.size());
        } catch (IOException e) {
            System.out.println("Error reading constraints.");
            System.exit(-1);
        }
    }
    /**
     * For each trio of wizard IDs (i, j, k), there exists 3! = 6 valid orderings between them.
     * They are as follows:
     *      Variable(i, j) ^      Variable(j, k) =>      Variable(i, k)     Equation (1)
     * -1 * Variable(i, j) ^ -1 * Variable(j, k) => -1 * Variable(i, k)     Equation (2)
     *      Variable(i, j) ^ -1 * Variable(i, k) => -1 * Variable(j, k)     Equation (3)
     * -1 * Variable(i, j) ^      Variable(i, k) =>      Variable(j, k)     Equation (4)
     * -1 * Variable(i, k) ^      Variable(j, k) => -1 * Variable(i, j)     Equation (5)
     *      Variable(i, k) ^ -1 * Variable(j, k) =>      Variable(i, j)     Equation (6)
     * By boolean algebra, equations (1), (3), and (5) are equivalent to the CNF clause:
     * -1 * Variable(i, j) v -1 * Variable(j, k) v       Variable(i, k)     Equation (7)
     * Similarly, equations (2), (4), and (6) are equivalent to the CNF clause:
     *      Variable(i, j) v      Variable(j, k) v  -1 * Variable(i, k)     Equation (8)
     *
     * Total of (N choose 3) * 2 = N * (N - 1) * (N - 2) / 3 clauses added.
     */
    private void generateImplicationClauses() {
        int numWizards = wizardSet.size();
        for (int i = 1; i <= numWizards; i++) {
            for (int j = i + 1; j <= numWizards; j++) {
                for (int k = j + 1; k <= numWizards; k++) {
                    int i_j = getVariableIDbyWizard(i, j);
                    int j_k = getVariableIDbyWizard(j, k);
                    int i_k = getVariableIDbyWizard(i, k);

                    this.clausesByImplication.add(new int[]{-1 * i_j, -1 * j_k, i_k});
                    this.clausesByImplication.add(new int[]{i_j, j_k, -1 * i_k});
                }
            }
        }
    }

    /**
     * Adds all clauses to the solver.
     */
    private void addAllClauses() {
        try {
            for (int[] clause : this.clausesByConstraints)
                this.solver.addClause(new VecInt(clause));
            for (int[] clause : this.clausesByImplication)
                this.solver.addClause(new VecInt(clause));
        } catch (ContradictionException e) {
            System.out.println("ERROR: Clauses contain contradiction!");
            System.exit(-2);
        }
    }

    /**
     * Runs the solver.
     */
    public void run() {
        try {
            Stopwatch watch = new Stopwatch();
            addAllClauses();

            IProblem problem = solver;

            if (problem.isSatisfiable()) {
                this.elapsedTime = watch.elapsedTime();
                this.assignments = problem.model();
                System.out.println("File " + this.getFileName() + " has been solved!!");
            }
        } catch (TimeoutException e) {
            System.out.println("Error: Timeout exception.");
            System.exit(-3);
        }
    }

    /**
     * PostProcessing method.
     * Builds a Directed Acyclic Graph and topological sorts the wizards.
     */
    public void postProcess() {
        this.printStatistics();
    }

    public void printAssignments() {
        if (getFileName().equals("input5.in")) {
            for (int i = 0; i < this.assignments.length; i++) {
                System.out.print(this.assignments[i] + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * Prints useful statistics.
     */
    public void printStatistics() {
        System.out.println("File name: " + this.inputFile.getName());
        System.out.println("Number of wizards: " + this.wizardSet.size());
        System.out.println("Number of variables: " + this.varIdToWizID.size());
        System.out.println("Number of clauses by constraints: " + this.clausesByConstraints.size());
        System.out.println("Number of clauses by implication: " + this.clausesByImplication.size());
        printAssignments();
        System.out.println("Running time: " + this.elapsedTime + "s\n");
    }
}
