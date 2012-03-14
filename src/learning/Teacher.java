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

    public double[] teach(int weightNb, double[] bestFitness, int maxIter) {

        return teach(weightNb, null, bestFitness, maxIter);
    }

    public double[] teach(int weightNb, int... iterations) {

        return teach(weightNb, iterations, null, -1);
    }

    public abstract double[] teach(int weightNb, int[] iterations, double[] bestFitness, int MaxIter);
}
