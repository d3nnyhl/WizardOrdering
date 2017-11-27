package main.java.com.wizardordering;

import edu.princeton.cs.algs4.Stopwatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WizardOrdering {
    private static final String INPUT_FILES_PATH = "./src/main/resources/phase2_inputs/";
    private static final String STAFF_INPUT_PATH = "./src/main/resources/Staff_Inputs/";
    private static final String INPUT_FILE_EXTENSION = ".in";
    private static List<WizardOrderingSolver> solvers = new ArrayList<>();
    private static List<WizardOrderingSolver> staffInputSolvers =  new ArrayList<>();

    private static void solveAll() {
        Stopwatch watch = new Stopwatch();
        for (WizardOrderingSolver solver: solvers) {
            solver.preProcess();
            solver.run();
            solver.postProcess();
        }
        System.out.println("All files solved in: " + watch.elapsedTime() + " s\n");
    }

    private static void solveAllStaff() {
        for (WizardOrderingSolver solver: staffInputSolvers) {
            solver.preProcess();
            solver.run();
            solver.postProcess();
        }
    }
    private static void start() {
        File inputFilesFolder = new File(INPUT_FILES_PATH);
        File[] listOfSubFolders = inputFilesFolder.listFiles();
        for (File subfolder : listOfSubFolders) {
            File[] listOfInputFiles = subfolder.listFiles();
            Arrays.sort(listOfInputFiles);
            for (File inputFile : listOfInputFiles) {
                String fileName = inputFile.getName();
                if (inputFile.isFile() && fileName.endsWith(INPUT_FILE_EXTENSION)) {
                    solvers.add(new WizardOrderingSolver(inputFile));
                }
            }
        }
    }

    private static void startStaffFiles() {
        File inputFilesFolder = new File(STAFF_INPUT_PATH);
        File[] listOfFiles = inputFilesFolder.listFiles();
        for (File inputFile : listOfFiles) {
            String fileName = inputFile.getName();
            if (inputFile.isFile() && fileName.endsWith(INPUT_FILE_EXTENSION)
                    && filterStaffInput(inputFile)) {
                staffInputSolvers.add(new WizardOrderingSolver(inputFile));
            }
        }
    }

    private static boolean filterStaffInput(File inputFile) {
        if (inputFile.getName().length() == 11) return true; // staff_60.in / staff_80.in

        int firstNumber = Character.getNumericValue(inputFile.getName().charAt(6));
        return firstNumber < 2;
    }

    public static void main (String[] args) {
        start();
        solveAll();
        startStaffFiles();
        solveAllStaff();
    }
}
