package learning.cmaes;

import learning.FitnessFinder;
import learning.Teacher;

public class CMATeacher extends Teacher {

    public CMATeacher(FitnessFinder finder) {
        super(finder);
    }

    @Override
    public double[] teach(int weightNb, int... iterations) {

        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.options.verbosity = -1;
        cma.readProperties();
        cma.setDimension(weightNb);
        cma.setInitialX(0.05);

        cma.setInitialStandardDeviation(0.2);
        cma.options.stopFitness = 1e-14; // optional setting

        double[] fitness = cma.init();

        boolean[] fit = new boolean[1];
        int itr = 0;

        for (int counter = 0; counter < 5000; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation();

            for (int i = 0; i < pop.length; ++i) {
                fitness[i] = getFitness(pop[i], fit);
                itr++;

                if (fit[0]) {
                    iterations[0] = itr;
                    return pop[i];
                }
            }
            cma.updateDistribution(fitness);
        }

        return cma.getMeanX();
    }

}
