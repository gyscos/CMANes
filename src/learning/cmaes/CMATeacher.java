package learning.cmaes;

import learning.Teacher;

public abstract class CMATeacher implements Teacher {

    @Override
    public abstract double getFitness(double[] values);

    @Override
    public double[] teach(int weightNb) {

        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.options.verbosity = -1;
        cma.readProperties();
        cma.setDimension(weightNb);
        cma.setInitialX(0.05);

        cma.setInitialStandardDeviation(0.2);
        cma.options.stopFitness = 1e-14; // optional setting

        double[] fitness = cma.init();

        for (int counter = 0; counter < 1500; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation();
            for (int i = 0; i < pop.length; ++i) {
                fitness[i] = getFitness(pop[i]);
            }
            cma.updateDistribution(fitness);
        }

        return cma.getMeanX();
    }

}
