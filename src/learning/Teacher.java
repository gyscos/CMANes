package learning;

public abstract class Teacher {
    public static class Result {
        public double[] values;
        public int      iterations;
        public double   bestFitness;

        public Result(double[] values, int iterations, double bestFitness) {
            this.values = values;
            this.iterations = iterations;
            this.bestFitness = bestFitness;
        }
    }

    FitnessFinder fitnessFinder;

    public Teacher(FitnessFinder finder) {
        setFitness(finder);
    }

    public double getFitness(double[] values, boolean[] fit) {
        return fitnessFinder.getFitness(values, fit);
    }

    public void setFitness(FitnessFinder finder) {
        fitnessFinder = finder;
    }

    public Result teach(int weightNb) {
        return teach(weightNb, -1);
    }

    public abstract Result teach(int weightNb, int maxIter);
}
