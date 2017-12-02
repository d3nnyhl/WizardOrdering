CS 170 Efficient Algorithms and Intractable Problems
Fall 2017
Professors: Prasad Raghavendra and Sanjam Garg

Project: Wizard Ordering

Authors:
Kyung Geun Kim
Yoon Kim
Denny Hung

How to run the solver?

(1) Open the project using IntelliJ IDEA.

    a. Start IntelliJ IDEA.

    b. Click "Open".

    c. Select the root directory of this project.

(2) Ensure all dependencies are correctly set up.

    a. Go to File > Project Structure > Project Settings > Libraries.

    b. Make sure "guava", "javalib", and "sat4j" have been added.

        i. If any library is missing, add them by clicking the '+' sign, followed by Java, and select the corresponding folder
        inside the "./lib/*" subdirectory.

(3) The phase II input files are located in "./src/resources/phase2_inputs/inputsXX/" where XX = 20, 35 or 50 depending on the nuber of wizards.
Their corresponding output files are located in "./src/resources/phase2_outputs/".

(4) The staff input files are located in "./src/resources/Staff_Inputs/". The corresponding outputs are located in "./src/resources/Staff_outputs/"

(5) To run the program on all the files we've solved so far, simply run the main method located in "./src/main/java/com/wizardordering/WizardOrdering.java".
The expected running time of the largest file we've solved "staff_180.in" is aroung ~100 minutes, thus expect the program to take up to two hours.
If you need to run a particular staff input, please follow comments in the main method.


