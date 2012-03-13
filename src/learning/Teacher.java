package learning;

public interface Teacher {
    public double getFitness(double[] values);

    public double[] teach(int weightNb);
}
