# Project: Wizard Ordering

## FAQ

How to run the solver?

1. Open the project using IntelliJ IDEA.

    a. Start IntelliJ IDEA.

    b. Click "Open".

    c. Select the root directory of this project.

2. Ensure all dependencies are correctly set up.

    a. Go to File > Project Structure > Project Settings > Libraries.

    b. Make sure "guava", "javalib", and "sat4j" have been added. If any library is missing, add them by clicking the '+' sign, followed by Java, and select the corresponding folder inside the "./lib/*" subdirectory.

3. The phase II input files are located in "./src/resources/phase2_inputs/inputsXX/" where XX = 20, 35 or 50 depending on the nuber of wizards.
Their corresponding output files are located in "./src/resources/phase2_outputs/".

4. The staff input files are located in "./src/resources/Staff_Inputs/". The corresponding outputs are located in "./src/resources/Staff_outputs/"

5. To run the program on all the files we've solved so far, simply run the main method located in "./src/main/java/com/wizardordering/WizardOrdering.java".
The expected running time of the largest file we've solved "staff_180.in" is around ~100 minutes, thus expect the program to take up to two hours.
If you need to run a particular staff input, please follow comments in the main method.

## Libraries Used

* Google Guava - https://github.com/google/guava
* Princeton's Algorithm Library - https://github.com/kevin-wayne/algs4
* SAT4J Solver - https://gitlab.ow2.org/sat4j/sat4j

## Authors
* Kyung Geun Kim
* Yoon Kim
* Denny Hung

## Acknowledgements
* Course: CS 170 Fall 2017 Efficient Algorithms and Intractable Problems
* Professors Prasad Raghavendra and Sanjam Garg, and the course staff.
* Google's Guava team for developing Guava
* Professors Joshua Hug, Robert Sedgewick, and Kevin Wayne for making the Princeton library accessible.
* SAT4J development team. Check their work at http://www.sat4j.org/