package learning.cmaes;

import learning.FitnessFinder;
import learning.Pair;
import learning.Teacher;

public class CMATeacher extends Teacher {

    final double sigma;

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
        cma.options.lowerStandardDeviations = new double[] { sigma / 2 };

        double[] fitness = cma.init();

        int itr = 0;

        for (int counter = 0; counter < 500000; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation();

            // System.out.println("Pop length : " + pop.length);

            for (int i = 0; i < pop.length; ++i) {
                Pair<Double, Boolean> pair = getFitness(pop[i]);

                fitness[i] = pair.a;

                // System.out.println("Fitness : " + pair.a);
                itr++;

                if (pair.b) {
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

        System.out.println("Could not find solution in " + itr + " iterations.");
        return new Result(cma.getMeanX(), iterations, bestFitness);
    }

}
