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

    /**
     * Runs the WizardOrderingSolver instances for Phase II input files.
     */
    private static void solveAll() {
        Stopwatch watch = new Stopwatch();
        for (WizardOrderingSolver solver: solvers) {
            solver.preProcess();
            solver.run();
            solver.postProcess();
        }
        System.out.println("All files solved in: " + watch.elapsedTime() + " s\n");
    }

    /**
     * Runs the WizardOrderingSolver instances for staff input files.
     */
    private static void solveAllStaff() {
        for (WizardOrderingSolver solver: staffInputSolvers) {
            //solver.randomAssign();
            solver.preProcess();
            solver.run();
            solver.postProcess();
        }
    }


    /**
     * Only used to solve one of the staff files at a time.
     * @param filename
     */
    private static void solveStaff(String filename) {
        for (WizardOrderingSolver solver: staffInputSolvers) {
            if (solver.getFileName().equals(filename)) {
                solver.preProcess();
                solver.run();
                solver.postProcess();
            }
        }
    }
    /**
     * Initializes WizardOrderingSolver instances for each of the assigned Phase II input files.
     */
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

    /**
     * Initializes WizardOrderingSolver instances for each of the staff input files.
     */
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

    /**
     * Determines if inputFile is one of the files that can be currently solved.
     * @param inputFile
     * @return true if the number of wizards is 180 or fewer and false otherwise.
     */
    private static boolean filterStaffInput(File inputFile) {
        if (inputFile.getName().length() == 11) return true; // staff_60.in / staff_80.in

        int firstNumber = Character.getNumericValue(inputFile.getName().charAt(6));
        return firstNumber < 2;
    }

    /**
     * Main driver. Just run this.
     * @param args
     */
    public static void main (String[] args) {
        start();
        solveAll();
        startStaffFiles();
        solveAllStaff(); // Please comment out this line and uncomment next line if want to run a single staff file.
        //solveStaff("staff_80.in");  //Replace XXX with the number in the staff file if only want to run a single staff file.

    }
}
