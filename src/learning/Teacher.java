package learning;

/**
 * General class designed to train a neural network.
 * 
 * @author gyscos
 * 
 */
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
        setFitnessFinder(finder);
    }

    /**
     * Get the fitness with the given parameters for the neural network.
     * 
     * @param values
     * @return A pair <Fitness, Success>
     */
    public Pair<Double, Boolean> getFitness(double[] values) {
        return fitnessFinder.getFitness(values);
    }

    /**
     * Sets the fitness finder
     * 
     * @param finder
     */
    public void setFitnessFinder(FitnessFinder finder) {
        fitnessFinder = finder;
    }

    /**
     * Train the neural network with the given number of parameters, and finds
     * the minimum number of required iterations.
     * 
     * @param weightNb
     * @return
     */
    public Result teach(int weightNb) {
        return teach(weightNb, -1);
    }

    /**
     * Train the neural network with the given number of parameters, finds
     * the minimum number of required iterations, and computes the final fitness
     * after maxIter iterations.
     * 
     * @param weightNb
     * @param maxIter
     * @return
     */
    public abstract Result teach(int weightNb, int maxIter);
}
