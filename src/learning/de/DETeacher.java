package learning.de;

import learning.FitnessFinder;
import learning.Teacher;

public class DETeacher extends Teacher {

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

    public DETeacher(FitnessFinder finder) {
        super(finder);
    }

    @Override
    public double[] teach(int weightNb, int... iterations) {

        double[] fitness;
        double[] fitness_nextgeneration;

        DifferentialEvolution DE = new DifferentialEvolution();

        DE.setDimension(weightNb);
        DE.setF(0.5);
        DE.setCr(0.5);
        DE.setPop_size(weightNb * 10);

        // iteration loop

        // iteration loop
        double[][] pop = DE.init();

        fitness = new double[pop.length];
        fitness_nextgeneration = new double[pop.length];
        boolean[] fit = new boolean[1];
        int itr = 0;

        for (int i = 0; i < pop.length; ++i) {
            fitness[i] = getFitness(pop[i], fit);
            itr++;

            if (fit[0]) {
                iterations[0] = itr;
                return pop[i];
            }
        }

        for (int counter = 0; counter < 50; counter++) {

            // --- core iteration step ---
            double[][] pop_nextgeneration = DE.samplePopulation();

            for (int i = 0; i < pop.length; ++i) {

                // compute fitness/objective value
                fitness_nextgeneration[i] = getFitness(pop_nextgeneration[i], fit);
                itr++;

                if (fit[0]) {
                    iterations[0] = itr;
                    return pop_nextgeneration[i];
                }

                // System.out.println("Fitness NextGeneration for agent " + i +
                // " : " +fitness_nextgeneration[i]);

                if (fitness_nextgeneration[i] < fitness[i]) {

                    pop[i] = pop_nextgeneration[i];
                    fitness[i] = fitness_nextgeneration[i];
                }

                System.out.println("Fitness for agent " + i + " : " + fitness[i]);

            }
            DE.setPop(pop);
        }

        return pop[getMinPopFitness(fitness)];
    }
}
