package learning.de;

import learning.FitnessFinder;
import learning.Teacher;

public class DETeacher extends Teacher {

    public static double getMinFitness(double[] fitness) {
        return fitness[getMinPopFitness(fitness)];
    }

    public static int getMinPopFitness(double[] fitness) {

        double min = fitness[0];
        int minPop = 0;

        for (int i = 0; i < fitness.length; ++i) {
            if (fitness[i] < min) {
                min = fitness[i];
                minPop = i;
            }
        }

        return minPop;

    }

    double params1;

    double params2;

    double params3;

    public DETeacher(FitnessFinder finder, double params1, double params2, double params3) {
        super(finder);

        this.params1 = params1;
        this.params2 = params2;
        this.params3 = params3;
    }

    @Override
    public double[] teach(int weightNb, int[] iterations, double[] bestFitness, int maxIter) {
        boolean findIter = maxIter == -1;

        double[] fitness;
        double[] fitness_nextgeneration;

        DifferentialEvolution DE = new DifferentialEvolution();

        DE.setDimension(weightNb);
        DE.setF(params1);
        DE.setCr(params2);
        DE.setPop_size(weightNb * 8);

        // iteration loop

        // iteration loop
        double[][] pop = DE.init();

        fitness = new double[pop.length];
        fitness_nextgeneration = new double[pop.length];
        boolean fit[] = new boolean[1];
        int itr = 0;

        for (int i = 0; i < pop.length; ++i) {
            fitness[i] = getFitness(pop[i], fit);
            itr++;

            if (findIter) {
                if (fit[0]) {
                    iterations[0] = itr;
                    return pop[i];
                }
            }
        }

        for (int counter = 0; counter < 10000; counter++) {

            // --- core iteration step ---
            double[][] pop_nextgeneration = DE.samplePopulation();

            for (int i = 0; i < pop.length; ++i) {

                if (pop_nextgeneration[i] != pop[i]) {

                    // compute fitness/objective value
                    fitness_nextgeneration[i] = getFitness(pop_nextgeneration[i], fit);
                    itr++;

                    if (findIter && fit[0]) {
                        iterations[0] = itr;
                        return pop[i];
                    }

                    if (fitness_nextgeneration[i] < fitness[i]) {
                        pop[i] = pop_nextgeneration[i];
                        fitness[i] = fitness_nextgeneration[i];
                    }
                }

                if (!findIter && itr == maxIter) {
                    bestFitness[0] = getMinFitness(fitness);
                    return pop[i];
                }

            }
            DE.setPop(pop);
        }

        bestFitness[0] = getMinFitness(fitness);
        return pop[getMinPopFitness(fitness)];
    }
}
