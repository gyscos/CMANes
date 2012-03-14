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
    public Result teach(int weightNb, int maxIter) {

        boolean findIter = (maxIter == -1);

        int iterations = 0;
        double bestFitness = 0;

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

                if (fit[0]) {
                    iterations = itr;
                    if (findIter)
                        return new Result(pop[i], iterations, bestFitness);
                }
                if (!findIter && itr == maxIter) {
                    bestFitness = cma.getBestRecentFunctionValue();
                    return new Result(cma.getMeanX(), iterations, bestFitness);
                }
            }
            cma.updateDistribution(fitness);
        }
        return new Result(cma.getMeanX(), iterations, bestFitness);
    }

}
