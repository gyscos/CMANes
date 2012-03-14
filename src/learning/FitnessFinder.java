package learning;

public interface FitnessFinder {
    public Pair<Double, Boolean> getFitness(double[] values);
}
