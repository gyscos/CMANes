package neurone;

import pole.Pole;
import pole.PoleController;
import pole.PoleFrame;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class Main {

    public static double getFitness(final ReseauNeurone reseau) {

        Pole pole = new Pole();
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
    public static int getMinIterations(ReseauNeurone reseau, int maxT) {

        // Maximal value known to fail
        int max = 0;

        // Minimal value known to work
        int min = maxT;
        int current = maxT;
        int previous = current;

        while (true) {

            System.out.print("Trying " + current + " ...");
            teachReseau(reseau, current);

            if (isFit(reseau)) {
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
            teachReseau(reseau, min);

        return min;
    }

    public static boolean isFit(ReseauNeurone reseau) {

        Pole pole = new Pole();
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
        reseau.setNeurones(6, 2, 1);

        System.out.println("Min iterations for success : " + getMinIterations(reseau, 500));

        showController(new NeuronePoleController(reseau));
    }

    public static void showController(PoleController controller) {
        PoleFrame frame = new PoleFrame();
        frame.setController(controller);
        frame.start(0, 0, 0.07, 0, 0, 0);
        frame.end();
    }

    public static void teachReseau(ReseauNeurone reseau, int iterations) {
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
                fitness[i] = getFitness(reseau);
            }
            cma.updateDistribution(fitness);
        }

        reseau.setWeights(cma.getMeanX());
        cma.setFitnessOfMeanX(getFitness(reseau));
    }

}
