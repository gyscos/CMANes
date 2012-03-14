package learning.de;

import java.util.LinkedList;

import learning.FitnessFinder;
import learning.Teacher;

public class GDETeacher extends Teacher {

    public static double getMinFitness(double[][] fitness) {

        double min = 1;

        for (int n = 0; n < 4; ++n) {
            for (int i = 0; i < fitness[n].length; ++i) {
                if (fitness[n][i] < min) {

                    min = fitness[n][i];

                }
            }
        }

        return min;

    }

    public static int getMinPopFitness(double[][] fitness) {

        double min = 1;
        int minPop = 0;

        for (int n = 0; n < 4; ++n) {
            for (int i = 0; i < fitness[n].length; ++i) {
                if (fitness[n][i] < min) {

                    min = fitness[n][i];
                    minPop = n * fitness[n].length + 1;
                }
            }
        }

        return minPop;

    }

    double params1;

    double params2;

    double params3;

    public GDETeacher(FitnessFinder finder, double params1, double params2, double params3) {
        super(finder);

        this.params1 = params1;
        this.params2 = params2;
        this.params3 = params3;
    }

    @Override
    public double[] teach(int weightNb, int[] iterations, double[] bestFitness, int maxIter) {
        boolean findIter = maxIter == -1;

        int PNI = (int) params3;

        double[] GFBEST_PNI = new double[4];

        int counter_frozen_individual = 0;
        LinkedList<Integer> frozList = new LinkedList<Integer>();

        double[][] fitness;
        double[][] fitness_nextgeneration;

        GroupedDifferentialEvolution GDE = new GroupedDifferentialEvolution();

        GDE.setDimension(weightNb);
        GDE.setF(params1);
        GDE.setCr(params2);
        int pop_size = weightNb * 8;
        int group_pop_size = weightNb * 2;

        GDE.setPop_size(pop_size);
        GDE.setGroupPop_size(group_pop_size);

        // iteration loop

        // iteration loop
        double[][][] group_pop = GDE.init();

        fitness = new double[4][group_pop_size];
        fitness_nextgeneration = new double[4][group_pop_size];

        boolean fit[] = new boolean[1];

        int itr = 0;

        for (int n = 0; n < 4; ++n) {
            for (int i = 0; i < group_pop_size; ++i) {
                fitness[n][i] = getFitness(group_pop[n][i], fit);
                itr++;
                if (findIter && fit[0]) {
                    iterations[0] = itr;
                    return group_pop[n][i];
                }
            }
        }

        for (int counter = 0; counter < 50000; counter++) {

            // --- core iteration step ---
            double[][][] pop_nextgeneration = GDE.samplePopulation();
            double[] GFBEST_current = new double[4];
            int[] MinPop = new int[4];

            double GF = 0;

            for (int n = 0; n < 4; ++n) {
                GFBEST_current[n] = 1;
            }

            for (int n = 0; n < 4; ++n) {
                for (int i = 0; i < group_pop_size; ++i) {

                    if (pop_nextgeneration[n][i] != group_pop[n][i]) {

                        fitness_nextgeneration[n][i] = getFitness(pop_nextgeneration[n][i], fit);
                        itr++;

                        if (findIter && fit[0]) {
                            iterations[0] = itr;
                            return group_pop[n][i];
                        }

                        if (fitness_nextgeneration[n][i] < fitness[n][i]) {

                            group_pop[n][i] = pop_nextgeneration[n][i];
                            fitness[n][i] = fitness_nextgeneration[n][i];
                        }

                        if (fitness[n][i] < GFBEST_current[n]) {
                            GFBEST_current[n] = fitness[n][i];
                            MinPop[n] = i;
                        }
                    }

                    if (!findIter && itr == maxIter) {
                        bestFitness[0] = getMinFitness(fitness);
                        return group_pop[n][i];
                    }

                }

                // System.out.println("Best Fitness groupe " + n + " : " +
                // GFBEST_current[n]);
                // System.out.println(cpt_iter);
            }

            if (counter % PNI == 0) {

                if (counter == 0) {
                    GFBEST_PNI = GFBEST_current;
                } else {

                    GF = 0;
                    for (int n = 0; n < 4; ++n) {
                        GF = GF + GFBEST_PNI[n] - GFBEST_current[n];
                    }
                    GF = GF / 100;

                    int[] LG = GDE.getLG();
                    int[] Lx = GDE.getLx();
                    int counter_group_trapped = 0;

                    for (int n = 0; n < 3; ++n) {
                        if (GFBEST_PNI[n] - GFBEST_current[n] < GF && LG[n] == 0 && counter_group_trapped == 0) {
                            LG[n] = 1;
                            counter_group_trapped = 1;

                            Lx[n * group_pop_size + MinPop[n]] = 1;
                            frozList.addLast(n * group_pop_size + MinPop[n]);

                            if (counter_frozen_individual < pop_size / 10) {
                                counter_frozen_individual = counter_frozen_individual + 1;
                            } else {
                                Lx[frozList.getFirst()] = 0;
                                frozList.removeFirst();
                            }

                        } else {
                            LG[n] = 0;
                        }

                    }

                    GDE.setLG(LG);
                    GDE.setLx(Lx);
                    GFBEST_PNI = GFBEST_current;
                }

            }

            GDE.setPop(group_pop);

        }

        int minPop = getMinPopFitness(fitness);
        return group_pop[Math.round(minPop / group_pop_size)][minPop - group_pop_size
                * Math.round(minPop / group_pop_size)];
    }
}
