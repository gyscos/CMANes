package learning;

public abstract class Teacher {
    FitnessFinder fitnessFinder;

    public Teacher(FitnessFinder finder) {
        setFitness(finder);
    }

    public double getFitness(double[] values, boolean... fit) {
        return fitnessFinder.getFitness(values, fit);
    }

    public void setFitness(FitnessFinder finder) {
        fitnessFinder = finder;
    }

    public abstract double[] teach(int weightNb, int[] iterations, double[] bestFitness,int MaxIter, double params1, double params2, double params3);
}
