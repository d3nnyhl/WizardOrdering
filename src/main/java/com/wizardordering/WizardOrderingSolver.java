package main.java.com.wizardordering;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Topological;
import org.junit.Assert;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import scala.Int;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private String[] solution;

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


                // Ensures all three wizard names are unique.
                Set<Integer> uniqueValues = new HashSet<>();
                uniqueValues.add(a);
                uniqueValues.add(b);
                uniqueValues.add(c);

                if (uniqueValues.size() != 3) {
                    continue;
                }

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
     * Builds a Directed Acyclic Graph and topologically sorts the wizards.
     */
    public void postProcess() {
        Digraph dag = this.generateWizardGraph();
        Topological topo = this.generateToplogicalOrdering(dag);
        this.generateSolution(topo);
        this.printStatistics();
        this.outputSolutionToFile();
    }

    public void printAssignments() {
        if (getFileName().equals("input5.in")) {
            for (int i = 0; i < this.assignments.length; i++) {
                System.out.print(this.assignments[i] + " ");
            }
            System.out.print("\n");
            System.out.println(Arrays.toString(this.solution));
        }
    }

    /**
     * Constructs a Directed Acyclic Graph given the wizard assignments.
     * Following variant holds: +(A, B) == -(B, A) <=> age(A) < age(B)
     *                          -(A, B) == +(B, A) <=> age(A) > age(B)
     * Direction of edges: if age(A) < age(B), A -> B and vice-versa.
     * @return Digraph
     */
    private Digraph generateWizardGraph() {
        Digraph dag = new Digraph(this.wizardSet.size());

        for (int assignment : this.assignments) {
            int[] orderedWizardPair = generateOrderedWizardPair(assignment);
            dag.addEdge(orderedWizardPair[0] - 1, orderedWizardPair[1] - 1);
        }

        return dag;
    }

    /**
     * Given an assignment, returns the correct ordering of wizards,
     * i.e. if assignment = -1 with pair [A, B], then return [B, A] since age(A) > age(B).
     * @param assignment varID
     * @return int[] ordered pairing of wizards
     */
    private int[] generateOrderedWizardPair(int assignment) {
        int[] result = new int[2];
        List<Integer> pair;
        int i = 0, j = 1;

        if (assignment > 0) {
            pair = this.varIdToWizID.get(assignment);
        } else {
            pair = this.varIdToWizID.get(assignment * -1);
            i = 1;
            j = 0;
        }

        result[i] = pair.get(0);
        result[j] = pair.get(1);
        return result;
    }

    /**
     * Given a Directed Acyclic Graph, returns a Topological ordering.
     * @param dag DAG
     * @return Topological topological ordering
     */
    private Topological generateToplogicalOrdering(Digraph dag) {
        return new Topological(dag);
    }

    /**
     * Given a Topological ordering, generates an ordering of wizard names.
     * @param topo topological ordering
     */
    private void generateSolution(Topological topo) {
        if (!topo.hasOrder()) return;

        this.solution = new String[this.wizardSet.size()];
        Iterator<Integer> iter = topo.order().iterator();
        int i = 0;

        while (iter.hasNext()) {
            String wizard = this.wizIdToName.get(iter.next() + 1);
            this.solution[i] = wizard;
            i++;
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

    /**
     * Outputs solution to file.
     */
    public void outputSolutionToFile() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        StringBuilder sbFile = new StringBuilder("./src/main/resources/");

        if (this.inputFile.getName().substring(0, 5).equals("staff")) {
            sbFile.append("Staff_outputs/");
        } else {
            sbFile.append("phase2_outputs/");
        }

        sbFile.append(removeExtension(this.inputFile.getName()));
        sbFile.append(".out");

        try {
            fileWriter = new FileWriter(sbFile.toString());
            bufferedWriter = new BufferedWriter(fileWriter);

            StringBuilder sb = new StringBuilder();

            for (String wizard : this.solution) {
                sb.append(wizard);
                sb.append(" ");
            }

            bufferedWriter.write(sb.toString().trim());
        } catch (IOException e) {
            System.out.println("Error writing wizard names.");
            System.exit(-1);
        } finally {
            try {
                if (bufferedWriter != null) bufferedWriter.close();
                if (fileWriter != null) fileWriter.close();
            } catch (IOException ex) {
                System.out.println("Error closing writers.");
                System.exit(-1);
            }
        }
    }

    /**
     * Removes the extension from a given filename.
     * @param filename filename
     * @return String
     */
    private String removeExtension(String filename) {
        if (filename == null) return null;
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) return filename;
        return filename.substring(0, dotIndex);
    }
}
