package main.java.com.wizardordering;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

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
    private int numWizards;
    private int numConstraints;

    /**
     * Constructor
     */
    public WizardOrderingSolver(File inputFile) {
        this.solver = SolverFactory.newDefault();
        this.inputFile = inputFile;
        this.wizardSet = new HashSet<>();
        this.preProcess();
    }

    public void preProcess() {
        this.generateVariables();
        this.generateConstraintClauses();
        this.generateImplicationClauses();
    }

    /*
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

    /*
     * Creates the set of wizard names mapped to a wizardID
     * Generates variables for each pair of wizard IDs, A and B, where A < B.
     * The variableID corresponding to (B, A) is denoted as -1 * variableID
     * corresponding to (A, B)
     */

    private void generateVariables() {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(this.inputFile));

            // Read number of wizards and constraints respectively.
            this.numWizards = Integer.parseInt(buf.readLine().trim());
            this.numConstraints = Integer.parseInt(buf.readLine().trim());


            for (int i = 0; i < numConstraints; i++) {
                StringTokenizer st = new StringTokenizer(buf.readLine());
                while (st.hasMoreTokens()) {
                    wizardSet.add(st.nextToken());
                    if (wizardSet.size() == this.numWizards)
                        break;
                }
            }

            // Create mapping between wizard names and integer value.
            int wizId = 1;
            this.wizIdToName = HashBiMap.create(this.numWizards);
            for (String name : wizardSet) {
                this.wizIdToName.put(wizId++, name);
            }

            // Create mapping between variables and integer value.
            int varId = 1;
            //this.varIdToVar = HashBiMap.create(this.numWizards * (this.numWizards - 1) / 2);
            this.varIdToWizID = HashBiMap.create(this.numWizards * (this.numWizards - 1) / 2);
            for (int i = 1; i <= this.numWizards; i++) {
                for (int j = i + 1; j <= this.numWizards; j++) {
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

                this.solver.addClause(new VecInt(new int[]{a_c, -1 * b_c}));
                this.solver.addClause(new VecInt(new int[]{-1 * a_c, b_c}));
            }
        } catch (IOException e) {
            System.out.println("Error reading constraints.");
            System.exit(-1);
        } catch (ContradictionException e) {
            System.out.println("Error: Unsatisfiable clauses added.");
            System.exit(-2);
        }
    }

    private void generateImplicationClauses() {
        // TODO: Generate 3-SAT clauses based on transitivity property.
        throw new UnsupportedOperationException();
    }

    public void run() {

    }

    public void postProcess() {
        // TODO: Create a DAG based on assignments by solver.
        throw new UnsupportedOperationException();
    }

    public void printStatistics() {
        System.out.println("File name: " + this.inputFile.getName());
        System.out.println("Number of wizards: " + this.wizardSet.size());
        System.out.println("Number of variables: " + this.varIdToWizID.size());
    }
}
