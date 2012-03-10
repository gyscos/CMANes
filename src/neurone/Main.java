package neurone;

import pole.Pole;
import pole.PoleFrame;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class Main {

    public static double getFitness(final ReseauNeurone reseau) {

        Pole pole = new Pole() {
            @Override
            public void gameOver() {
                // start();
            }

            @Override
            public void onStep() {
                double[] input = getData();
                setAction(reseau.getOutput(input));
                // System.out.println("Step : " + reseau.getOutput(input));
            }
        };

        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        return 1 - pole.getFitness();

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        final ReseauNeurone reseau = new ReseauNeurone();
        int weightNb = reseau.setNeurones(6, 2, 1);

        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.readProperties();
        cma.setDimension(weightNb);
        cma.setInitialX(0.05); // in each dimension, also setTypicalX can be
                               // used
        cma.setInitialStandardDeviation(0.2); // also a mandatory setting
        cma.options.stopFitness = 1e-14; // optional setting

        double[] fitness = cma.init();
        // iteration loop

        // iteration loop
        for (int counter = 0; counter < 1000; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation(); // get a new population of
                                                     // solutions
            for (int i = 0; i < pop.length; ++i) { // for each candidate
                                                   // solution i
                // a simple way to handle constraints that define a convex
                // feasible domain
                // (like box constraints, i.e. variable boundaries) via
                // "blind re-sampling"
                // assumes that the feasible domain is convex, the optimum is
                // sufficiently small to prevent quasi-infinite looping here
                reseau.setWeights(pop[i]);
                // compute fitness/objective value
                fitness[i] = getFitness(reseau);
                // System.out.println("Fitness for agent " + i + " : " +
                // fitness[i]);
            }
            cma.updateDistribution(fitness); // pass fitness array to update
                                             // search distribution
            // --- end core iteration step ---

            // output to files and console
            cma.writeToDefaultFiles();
            int outmod = 150;
            if (cma.getCountIter() % (15 * outmod) == 1)
                cma.printlnAnnotation(); // might write file as well
            if (cma.getCountIter() % outmod == 1)
                cma.println();
        }

        System.out.println("Out of loop");

        // evaluate mean value as it is the best estimator for the optimum
        reseau.setWeights(cma.getMeanX());
        cma.setFitnessOfMeanX(getFitness(reseau)); // updates the best ever
                                                   // solution

        // final output
        cma.writeToDefaultFiles(1);
        cma.println();
        cma.println("Terminated due to");
        for (String s : cma.stopConditions.getMessages())
            cma.println("  " + s);
        cma.println("best function value " + cma.getBestFunctionValue()
                + " at evaluation " + cma.getBestEvaluationNumber());

        reseau.setWeights(cma.getBestX());
        PoleFrame frame = new PoleFrame() {
            @Override
            public void onStep() {
                pole.setAction(reseau.getOutput(pole.getData()));
            }
        };
        frame.start();

    }

}
