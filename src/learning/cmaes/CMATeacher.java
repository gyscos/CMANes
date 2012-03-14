package learning.cmaes;

import learning.FitnessFinder;
import learning.Teacher;

public class CMATeacher extends Teacher {

    public CMATeacher(FitnessFinder finder) {
        super(finder);
    }

    @Override
    public double[] teach(int weightNb, int[] iterations, double[] bestFitness, int maxIter) {
        boolean findIter = maxIter == -1;

        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.options.verbosity = -1;
        cma.readProperties();
        cma.setDimension(weightNb);
        cma.setInitialX(0.05);

        cma.setInitialStandardDeviation(0.5);
        cma.options.stopFitness = 1e-14; // optional setting

        double[] fitness = cma.init();

        boolean fit = false;
        int itr = 0;

        for (int counter = 0; counter < 500000; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation();

            for (int i = 0; i < pop.length; ++i) {
                fitness[i] = getFitness(pop[i], fit);
                itr++;

                if (findIter) {
                    if (fit) {
                        iterations[0] = itr;
                        return pop[i];
                    }
                } else {
                    if (itr == maxIter) {
                        bestFitness[0] = cma.getBestRecentFunctionValue();
                        return cma.getMeanX();
                    }
                }
            }
            cma.updateDistribution(fitness);
        }
        return cma.getMeanX();
    }

}
