package learning.de;

import java.util.LinkedList;

import neurone.NeuronePoleController;
import neurone.ReseauNeurone;
import pole.Pole;
import pole.PoleFrame;

public class MainGDE {

    public static double getFitness(final ReseauNeurone reseau) {

        Pole pole = new Pole(false, true);
        pole.setController(new NeuronePoleController(reseau));

        pole.start(0, 0, 0.07, 0);
        pole.end();

        return 1 - pole.getFitness();

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

    /**
     * @param args
     */
    public static void main(String[] args) {

        final ReseauNeurone reseau = new ReseauNeurone();
        int weightNb = reseau.setNeurones(4, 2, 1);
        // int PNI;
        // double eps;

        int PNI = 5;

        // a supprimer
        int cpt_iter = 0;

        double[] GFBEST_PNI = new double[4];
        double[] GFBEST = new double[4];

        int counter_frozen_individual = 0;
        LinkedList<Integer> frozList = new LinkedList<Integer>();

        double[][] fitness;
        double[][] fitness_nextgeneration;

        GroupedDifferentialEvolution GDE = new GroupedDifferentialEvolution();

        GDE.setDimension(weightNb);
        GDE.setF(0.5);
        GDE.setCr(0.5);
        int pop_size = weightNb * 12;
        int group_pop_size = weightNb * 3;

        GDE.setPop_size(pop_size);
        GDE.setGroupPop_size(group_pop_size);

        // iteration loop

        // iteration loop
        double[][][] group_pop = GDE.init();

        fitness = new double[4][group_pop_size];
        fitness_nextgeneration = new double[4][group_pop_size];

        for (int n = 0; n < 4; ++n) {
            for (int i = 0; i < group_pop_size; ++i) {
                reseau.setWeights(group_pop[n][i]);
                fitness[n][i] = getFitness(reseau);
            }
        }

        for (int counter = 0; counter < 20; counter++) {

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

                    reseau.setWeights(pop_nextgeneration[n][i]);

                    fitness_nextgeneration[n][i] = getFitness(reseau);
                    cpt_iter = cpt_iter + 1;

                    if (fitness_nextgeneration[n][i] < fitness[n][i]) {

                        group_pop[n][i] = pop_nextgeneration[n][i];
                        fitness[n][i] = fitness_nextgeneration[n][i];
                    }

                    if (fitness[n][i] < GFBEST_current[n]) {
                        GFBEST_current[n] = fitness[n][i];
                        MinPop[n] = i;
                    }

                }

                System.out.println("Best Fitness groupe " + n + " : " + GFBEST_current[n]);
                System.out.println(cpt_iter);
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
                    GFBEST_PNI = GFBEST_current;

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
                }

            }

            GDE.setPop(group_pop);

        }

        System.out.println("Out of loop");

        int minPop = getMinPopFitness(fitness);
        reseau.setWeights(group_pop[Math.round(minPop / group_pop_size)][minPop - group_pop_size
                * Math.round(minPop / group_pop_size)]);

        // for (int i = 0; i < pop[minPop].length; ++i) {
        // System.out.println("Weight " + i + " : " + pop[minPop][i]);
        // }

        PoleFrame frame = new PoleFrame(false, true);
        frame.setController(new NeuronePoleController(reseau));

        frame.start(0, 0, 0.07, 0);

    }

}
