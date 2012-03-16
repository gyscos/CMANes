package learning;

/**
 * General interface, meant to compute the fitness for a neural network with the
 * given parameters.
 * 
 * @author gyscos
 * 
 */
public interface FitnessFinder {

    /**
     * Run a simulation and computes the fitness for a neural network with the
     * given values.
     * 
     * @param values
     * @return
     */
    public Pair<Double, Boolean> getFitness(double[] values);
}
