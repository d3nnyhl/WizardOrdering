package main.java.com.wizardordering;

public class WizardVariable {
    private static final int NUM_WIZARDS = 2;
    private final int first;
    private final int second;

    public WizardVariable(int first, int second) {
        this.first = first;
        this.second = second;
    }

    /*
     *  Returns the natural ordering of the wizards by their relative age.
     *  ordering[0]'s age is less than ordering[1]'s age.
     *  @return ordering, the two-element array corresponding to the correct ordering of first and second.
     */
    public int[] getOrdering(boolean assignment) {
        int[] ordering = new int[NUM_WIZARDS];
        if (assignment) {
            ordering[0] = first;
            ordering[1] = second;
        } else {
            ordering[0] = second;
            ordering[1] = first;
        }
        return ordering;
    }
}
