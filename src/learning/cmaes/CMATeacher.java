package learning.cmaes;

import learning.FitnessFinder;
import learning.Teacher;

public class CMATeacher extends Teacher {

    public CMATeacher(FitnessFinder finder) {
        super(finder);
    }

    @Override
    public double[] teach(int weightNb, int[] iterations, double[] bestFitness,int MaxIter, double params1, double params2, double params3) {

        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.options.verbosity = -1;
        cma.readProperties();
        cma.setDimension(weightNb);
        cma.setInitialX(0.05);

        cma.setInitialStandardDeviation(0.5);
        cma.options.stopFitness = 1e-14; // optional setting

        double[] fitness = cma.init();

        
        
        boolean[] fit = new boolean[1];
        int itr = 0;

        for (int counter = 0; counter < 500000; counter++) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation();

            for (int i = 0; i < pop.length; ++i) {
                fitness[i] = getFitness(pop[i], fit);
                itr++;

                
//                if (fit[0]) {
//                    iterations[0] = itr;
//                    
//                    for (int j = 0; j < weightNb; j++) {
//                    	//System.out.println(pop[i][j]);
//                    }
//                    
//                    return pop[i];
//       
//                }
               
              //System.out.println(itr);
                
                //if (itr == MaxIter || Math.abs(cma.getBestRecentFunctionValue() - cma.getWorstRecentFunctionValue()) < 0.0001) {
                
                if (itr == MaxIter) {
                	bestFitness[0] = cma.getBestRecentFunctionValue();
                	return cma.getMeanX();
                }
                
            }
            cma.updateDistribution(fitness);
            
            
            //System.out.println("worst pop " + cma.getWorstRecentFunctionValue());
            //System.out.println("Best pop " + cma.getBestRecentFunctionValue());
            		
            //if ( Math.abs(cma.getBestRecentFunctionValue() - cma.getWorstRecentFunctionValue()) < 0.01){
            //		iterations[0] = 99999999;
            //		return cma.getMeanX();
          
            	
            //}
            
        }

        return cma.getMeanX();
    }

}
