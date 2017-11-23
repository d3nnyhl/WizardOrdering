package main.java.com.wizardordering;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;

import java.util.HashSet;
import java.util.Set;

public class WizardOrderingSolver {
    private ISolver solver;
    private String inFileName;
    private String outFileName;
    private Set<String> wizardList;


    public WizardOrderingSolver(String inFileName, String outFileName) {
        this.solver = SolverFactory.newDefault();
        this.inFileName = inFileName;
        this.outFileName = outFileName;
        this.wizardList = new HashSet<>();
    }

   // public
}
