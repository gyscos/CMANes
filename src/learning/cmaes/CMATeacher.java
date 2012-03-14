package learning.cmaes;

import learning.FitnessFinder;
import learning.Teacher;

public class CMATeacher extends Teacher {

    double sigma = 0.5;

    public CMATeacher(FitnessFinder finder) {
        this(finder, 0.5);
    }

    public CMATeacher(FitnessFinder finder, double sigma) {
        super(finder);
        this.sigma = sigma;
    }

    @Override
    public double[] teach(int weightNb, int[] iterations, double[] bestFitness, int maxIter) {
        boolean findIter = (iterations != null && iterations.length != 0);
        boolean getFitness = (maxIter == -1);

        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.options.verbosity = -1;
        cma.readProperties();
        cma.setDimension(weightNb);
        cma.setInitialX(0.05);

        cma.setInitialStandardDeviation(sigma);
        cma.options.stopFitness = 1e-14; // optional setting

        double[] fitness = cma.init();

        boolean fit[] = new boolean[1];
        int itr = 0;

        for (int counter = 0; counter < 500000; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation();

            for (int i = 0; i < pop.length; ++i) {
                fitness[i] = getFitness(pop[i], fit);
                itr++;

                if (findIter) {
                    if (fit[0]) {
                        iterations[0] = itr;
                        if (!getFitness)
                            return pop[i];
                    }
                }
                if (getFitness) {
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
