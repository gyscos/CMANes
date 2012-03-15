package learning;

/**
 * General interface, meant to compute the fitness for a neural network with the
 * given parameters.
 * 
 * @author gyscos
 * 
 */
public interface FitnessFinder {
    public Pair<Double, Boolean> getFitness(double[] values);
}
