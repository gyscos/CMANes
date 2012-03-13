package neurone;

import java.util.ArrayList;
import java.util.List;

public class ReseauNeurone {
    List<List<Neurone>> neurones = new ArrayList<List<Neurone>>();
    Neurone             output;

    public Neurone addNeurone(int layer, double... weights) {

        Neurone neurone = new Neurone();

        List<Neurone> parents = getLayer(layer - 1);
        if (parents != null)
            for (int i = 0; i < parents.size(); i++)
                neurone.addSynapse(parents.get(i), weights[i], true);

        getLayer(layer).add(neurone);
        return neurone;
    }

    public void addOutputNeurone(int layer, double... weights) {
        output = addNeurone(layer, weights);
    }

    public void addSynapse(int layerFrom, int idFrom, int layerTo, int idTo, double weight, boolean compute) {
        Neurone nFrom = getLayer(layerFrom).get(idFrom);
        Neurone nTo = getLayer(layerTo).get(idTo);

        nTo.addSynapse(nFrom, weight, compute);
    }

    public void clear() {
        neurones.clear();
    }

    public List<Neurone> getLayer(int layer) {
        if (layer < 0)
            return null;

        if (neurones.size() <= layer)
            neurones.add(new ArrayList<Neurone>());

        return neurones.get(layer);
    }

    public double getOutput(double... input) {
        List<Neurone> layer = neurones.get(0);

        for (List<Neurone> list : neurones)
            for (Neurone neurone : list)
                neurone.clear();

        // Feed input layer
        for (int i = 0; i < layer.size(); i++)
            layer.get(i).setValue(input[i]);

        return output.getOutput();
    }

    public int getSize() {
        int sum = 0;
        for (List<Neurone> list : neurones)
            for (Neurone neurone : list)
                sum += neurone.size();
        return sum;
    }

    public void reset() {
        for (List<Neurone> layer : neurones)
            for (Neurone neurone : layer)
                neurone.reset();
    }

    public int setNeurones(int... layerSizes) {
        clear();

        int sum = 0;

        for (int i = 0; i < layerSizes[0]; i++) {
            addNeurone(0);
        }
        // sum += layerSizes[0];

        for (int i = 1; i < layerSizes.length; i++) {
            sum += layerSizes[i] * layerSizes[i - 1];
            for (int j = 0; j < layerSizes[i]; j++) {
                int weightsNb = layerSizes[i - 1];
                double[] weights = new double[weightsNb];
                for (int k = 0; k < weights.length; k++)
                    weights[k] = 1;
                if (i == layerSizes.length - 1)
                    addOutputNeurone(i, weights);
                else
                    addNeurone(i, weights);
            }
        }

        return sum;
    }

    public void setWeights(double[] weights) {

        int layerId = 1;
        int neuroneId = 0;
        int weightId = 0;

        for (int i = 0; i < weights.length; i++) {
            List<Neurone> layer = getLayer(layerId);
            Neurone neurone = layer.get(neuroneId);
            neurone.setWeight(weightId, weights[i]);

            weightId++;
            if (weightId >= neurone.size()) {
                neuroneId++;
                weightId = 0;
            }
            if (neuroneId >= layer.size()) {
                layerId++;
                neuroneId = 0;
            }
        }
    }
}
