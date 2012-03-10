package neurone;

import pole.Pole;
import pole.PoleController;
import pole.PoleFrame;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class Main {

    public static int getAverageMinIterations(ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation,
            int maxT, int N) {
        int sum = 0;
        for (int i = 0; i < N; i++)
            sum += getMinIterations(reseau, useTwoPoles, useTotalInformation, maxT);
        return sum / N;
    }

    public static double getFitness(final ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation) {

        Pole pole = new Pole(useTwoPoles, useTotalInformation);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        return 1 - pole.getFitness();
    }

    /**
     * Recherche dichotomique
     * 
     * @param reseau
     * @param maxT
     * @return
     */
    public static int getMinIterations(ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation, int maxT) {

        // Maximal value known to fail
        int max = 0;

        // Minimal value known to work
        int min = maxT;
        int current = maxT;
        int previous = current;

        while (true) {

            System.out.print("Trying " + current + " ...");
            teachReseau(reseau, useTwoPoles, useTotalInformation, current);

            if (isFit(reseau, useTwoPoles, useTotalInformation)) {
                System.out.println(" Success !");
                min = current;
                current = (current + max) / 2;
            } else {
                System.out.println(" Failed !");
                max = current;
                current = (current + min) / 2;
            }

            if (previous == current)
                break;
            previous = current;

        }

        // Bring back to working one if need be
        if (current != min)
            teachReseau(reseau, useTwoPoles, useTotalInformation, min);

        return min;
    }

    public static boolean isFit(ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation) {

        Pole pole = new Pole(useTwoPoles, useTotalInformation);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        return !pole.lost;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        final ReseauNeurone reseau = new ReseauNeurone();
        reseau.setNeurones(4, 2, 1);

        boolean useTwoPoles = false;
        boolean useTotalInformation = true;

        System.out.println("Min iterations for success : "
                + getAverageMinIterations(reseau, useTwoPoles, useTotalInformation, 20, 20));

        showController(new NeuronePoleController(reseau), useTwoPoles, useTotalInformation);
    }

    public static void showController(PoleController controller, boolean useTwoPoles, boolean useTotalInformation) {
        PoleFrame frame = new PoleFrame(useTwoPoles, useTotalInformation);
        frame.setController(controller);
        frame.start(0, 0, 0.07, 0, 0, 0);
        frame.end();
    }

    public static void teachReseau(ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation,
            int iterations) {
        int weightNb = reseau.getSize();

        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.options.verbosity = -1;
        cma.readProperties();
        cma.setDimension(weightNb);
        cma.setInitialX(0.05);

        cma.setInitialStandardDeviation(0.2);
        cma.options.stopFitness = 1e-14; // optional setting

        double[] fitness = cma.init();

        for (int counter = 0; counter < iterations; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation();
            for (int i = 0; i < pop.length; ++i) {
                reseau.setWeights(pop[i]);
                fitness[i] = getFitness(reseau, useTwoPoles, useTotalInformation);
            }
            cma.updateDistribution(fitness);
        }

        reseau.setWeights(cma.getMeanX());
        cma.setFitnessOfMeanX(getFitness(reseau, useTwoPoles, useTotalInformation));
    }

}
