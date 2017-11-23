package main.java.com.wizardordering;

import org.junit.Assert;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class WizardOrderingSolver {
    private ISolver solver;
    private File inputFile;
    private String outFileName;
    private Set<String> wizardSet;
    private Set<Constraint> constraintSet;
    private int numWizards;
    private int numConstraints;

    /**
     * Constructor
     */
    public WizardOrderingSolver(File inputFile) {
        this.solver = SolverFactory.newDefault();
        this.inputFile = inputFile;
        //this.outFileName = outFileName;
        this.wizardSet = new HashSet<>();
        this.constraintSet = new HashSet<>();
        this.readInput();
    }


    private void readInput() {

        try {
            BufferedReader buf = new BufferedReader(new FileReader(this.inputFile));
        } catch (IOException e) {
            this.numWizards = 0;
            this.numConstraints = 0;
        }

    }


   private class Constraint {
        String first;
        String second;
        String third;

        Constraint(String first, String second, String third) {
            int cmp = first.compareTo(second);

            if (cmp > 0) {
                this.first = second;
                this.second = first;
            } else {
                this.first = first;
                this.second = second;
            }
            this.third = third;
        }
   }

   private void testConstraint() {
        Constraint c = new Constraint("b", "a", "c");
        Assert.assertEquals(c.first, "a");
        Assert.assertEquals(c.second, "b");

   }

   public static void main(String[] args) {

   }
}
