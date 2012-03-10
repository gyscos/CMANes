package neurone;

import java.util.ArrayList;
import java.util.List;

public class Neurone {

    public static double sigmoid(double a) {
        return a / (1 + Math.abs(a));
    }

    boolean       ready    = false;

    double        value    = 0;
    double        oldValue = 0;

    List<Synapse> synapses = new ArrayList<Synapse>();

    public void addSynapse(Neurone neurone, double weight, boolean compute) {
        addSynapse(new Synapse(neurone, weight, compute));
    }

    public void addSynapse(Synapse synapse) {
        synapses.add(synapse);
    }

    public void clear() {
        ready = false;
        oldValue = value;
    }

    public void compute() {
        double sum = 0;

        for (Synapse synapse : synapses)
            sum += synapse.getOutput();

        setValue(sigmoid(sum));
    }

    public double getOutput() {
        return getOutput(true);
    }

    public double getOutput(boolean compute) {
        if (!compute)
            return oldValue;

        if (!ready)
            compute();

        return value;
    }

    public void reset() {
        for (Synapse synapse : synapses)
            synapse.weight = 1;
    }

    public void setValue(double value) {
        this.value = value;
        ready = true;
    }

    public void setWeight(int i, double v) {
        synapses.get(i).weight = v;
    }

    public int size() {
        return synapses.size();
    }
}
