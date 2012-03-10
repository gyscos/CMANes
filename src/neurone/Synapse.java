package neurone;

public class Synapse {
    Neurone neurone;
    double  weight;
    boolean compute;

    public Synapse(Neurone neurone, double weight, boolean compute) {
        this.neurone = neurone;
        this.weight = weight;
        this.compute = compute;
    }

    public double getOutput() {
        return weight * neurone.getOutput(compute);
    }
}
