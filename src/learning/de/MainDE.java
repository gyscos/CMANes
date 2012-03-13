package learning.de;

import neurone.NeuronePoleController;
import neurone.ReseauNeurone;
import pole.Pole;
import pole.PoleFrame;

public class MainDE {

    public static double getFitness(final ReseauNeurone reseau) {

        Pole pole = new Pole(false, true);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0);
        pole.end();

        return 1 - pole.getFitness();

    }

    public static int getMinPopFitness(double[] fitness) {

        double min = 1;
        int minPop = 0;

        for (int i = 0; i < fitness.length; ++i) {
            if (fitness[i] < min) {

                min = fitness[i];
                minPop = i;
            }
        }

        return minPop;

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        final ReseauNeurone reseau = new ReseauNeurone();
        int weightNb = reseau.setNeurones(4, 2, 1);
        System.out.println(weightNb);

        DETeacher teacher = new DETeacher() {

            @Override
            public double getFitness(double[] values) {
                reseau.setWeights(values);
                return MainDE.getFitness(reseau);
            }
        };

        teacher.teach(weightNb);

        PoleFrame frame = new PoleFrame(false, true);
        frame.setController(new NeuronePoleController(reseau));
        frame.start(0, 0, 0.07, 0);

    }

}
