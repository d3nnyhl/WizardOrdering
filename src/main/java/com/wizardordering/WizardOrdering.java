package main.java.com.wizardordering;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.File;
import java.io.IOException;


public class WizardOrdering {
    private static final String INPUT_FILES_PATH = "./src/main/resources/phase2_inputs/";
    private static final String STAFF_INPUT_PATH = "./src/main/resources/Staff_Inputs/";
    private static final String INPUT_FILE_EXTENSION = ".in";
    public static void solveAll() {

    }


    public static void main (String[] args) {
        File inputFilesFolder = new File(INPUT_FILES_PATH);
        File[] listOfSubFolders = inputFilesFolder.listFiles();
      //  try {
            for (File subfolder : listOfSubFolders) {
                System.out.println(subfolder.getName());
                File[] listOfInputFiles = subfolder.listFiles();
                for (File inputFile : listOfInputFiles) {
                    String fileName = inputFile.getName();
                    if (inputFile.isFile() && fileName.endsWith(INPUT_FILE_EXTENSION)) {
                        System.out.println(inputFile.getName());
                    }
                }
            }
     //   } catch (IOException e) {
         //   return;
       // }
    }
}
