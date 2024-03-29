package pole;

//Inverted Pendulum simulation
//by Chuck Anderson, 1998, with code from O'Reilly's "Java by Example"

import java.util.LinkedList;

public class Pole {

    PoleController             controller;

    Thread                     thread;

    // Now for pole simulation
    double                     action;
    double                     pos, posDot, angle, angleDot, angle2, angle2Dot;

    // Constants

    boolean                    useTwoPoles;
    boolean                    useTotalInformation;

    /**
     * Length of main pole
     */
    public static final double poleLength      = 1.;

    /**
     * Length of second pole
     */
    public static final double pole2Length     = 0.1;

    /**
     * Mass of cart without pole
     */
    public static final double cartMass        = 1.;

    /**
     * Mass of main pole
     */
    public static final double poleMass        = poleLength;

    /**
     * Mass of second pole
     */
    public static final double pole2Mass       = pole2Length;

    /**
     * Maximum force applied to the cart
     */
    public static final double forceMag        = 10;
    public static final double g               = 9.8;

    /**
     * Time step
     */
    public static final double tau             = 0.01;

    /**
     * Cart friction
     */
    public static final double fricCart        = 0.0005;

    /**
     * Friction for main pole
     */
    public static final double fricPole        = 0.000002;

    /**
     * Friction for second pole
     */
    public static final double fric2Pole       = 0.000002;

    /**
     * Total mass of cart in single pole situation
     */
    public static final double total1Mass      = cartMass + poleMass;

    /**
     * Total mass of cart in 2 poles configuration
     */
    public static final double total2Mass      = cartMass + poleMass + pole2Mass;

    /**
     * Main pole half length
     */
    public static final double halfPole        = 0.5 * poleLength;

    /**
     * Second pole half length
     */
    public static final double half2Pole       = 0.5 * pole2Length;
    public static final double poleMassLength  = halfPole * poleMass;
    public static final double pole2MassLength = half2Pole * pole2Mass;

    /**
     * Maximum position amplitude. Player lose if position exceeds this value.
     */
    public static final double posLimit        = 2.4;

    /**
     * Angle limit in the single pole case
     */
    public static final double angle1Limit     = 0.2;

    /**
     * Angle limit in the 2 poles case
     */
    public static final double angle2Limit     = 0.63;

    /**
     * Time limit for the simulation. Player wins if he doesn't loose before
     * time limit.
     */
    public static final int    timeLimit       = 100000;

    public static final double fourthirds      = 4. / 3.;

    int                        steps;

    /**
     * List of 100 last positions, to compute FitnessF2.
     */
    LinkedList<Double>         posList         = new LinkedList<Double>();

    /**
     * List of 100 last velocities, to compute FitnessF2
     */
    LinkedList<Double>         posDotList      = new LinkedList<Double>();

    LinkedList<Double>         angleList       = new LinkedList<Double>();
    LinkedList<Double>         angleDotList    = new LinkedList<Double>();

    LinkedList<Double>         angle2List      = new LinkedList<Double>();
    LinkedList<Double>         angle2DotList   = new LinkedList<Double>();

    /**
     * TRUE if the player lost, FALSE if the player hasn't lost (yet)
     */
    public boolean             lost            = false;

    public Pole(boolean useTwoPoles, boolean useTotalInformation) {
        this.useTwoPoles = useTwoPoles;
        this.useTotalInformation = useTotalInformation;
    }

    /**
     * Blocks until simulation ended
     */
    public void end() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Can optionnally override ; called when a game is over.
     */
    public void gameOver() {
    }

    /**
     * Retrieves data from the game. Depends on the configuration.<br />
     * First comes position (and velocity if using complete information).<br />
     * Then comes first angle (and first angular velocity... )<br />
     * Then comes angle for the second pole if using 2 poles (and its angular
     * velocity...)<br />
     * Hence, the length of the array depends on the configuration ; it can be
     * 6, 3, 4 or 2.
     * 
     * @return
     */
    public double[] getData() {
        double[] result;
        if (useTwoPoles) {
            if (useTotalInformation) {
                result = new double[6];
                result[0] = pos;
                result[1] = posDot;
                result[2] = angle;
                result[3] = angleDot;
                result[4] = angle2;
                result[5] = angle2Dot;
            } else {
                result = new double[3];
                result[0] = pos;
                result[1] = angle;
                result[2] = angle2;
            }
        } else {
            if (useTotalInformation) {
                result = new double[4];
                result[0] = pos;
                result[1] = posDot;
                result[2] = angle;
                result[3] = angleDot;
            } else {
                result = new double[2];
                result[0] = pos;
                result[1] = angle;
            }
        }

        return result;
    }

    /**
     * Returns the F1 fitness, that is, how long before the player lost.
     * 
     * @return
     */
    public double getFitnessF1() {
        return steps / 1000d;
    }

    /**
     * F2 fitness, rewarding stability over the last 100 steps.
     * 
     * @return
     */
    public double getFitnessF2() {
        if (steps < 100)
            return 0;

        double sum = 0;

        for (double pos : posList)
            sum += Math.abs(pos);
        for (double posDot : posDotList)
            sum += Math.abs(posDot);

        for (double angleDot : angleDotList)
            sum += Math.abs(angleDot);
        for (double angle2Dot : angle2DotList)
            sum += Math.abs(angle2Dot);

        return 0.75 / sum;
    }

    public double getNormalFitness() {
        return getFitnessF1();
    }

    public double getSpecialFitness() {
        return 0.1 * getFitnessF1() + 0.9 * getFitnessF2();
    }

    public void init() {
        steps = 0;

        // Initialize pole state.
        pos = 0.;
        posDot = 0.;
        angle = 0.;
        angleDot = 0.;
        angle2 = 0.;
        angle2Dot = 0.;
        action = 0;
    }

    public void onStep() {
        if (controller == null)
            return;

        double[] input = getData();
        setAction(controller.getAction(input));
    }

    public void resetPole() {
        pos = 0.;
        posDot = 0.;
        angle = 0.;
        angleDot = 0.;
        angle2 = 0.;
        angle2Dot = 0.;
    }

    public void setAction(double direction) {
        action = direction;
    }

    public void setController(PoleController controller) {
        this.controller = controller;
    }

    public void setData(double... data) {
        if (useTwoPoles) {
            pos = data[0];
            posDot = data[1];
            angle = data[2];
            angleDot = data[3];
            angle2 = data[4];
            angle2Dot = data[5];
        } else {
            pos = data[0];
            posDot = data[1];
            angle = data[2];
            angleDot = data[3];
        }
    }

    public void start() {
        start(true);
    }

    public void start(final boolean toInit) {
        (thread = new Thread() {
            @Override
            public void run() {

                if (toInit)
                    init();

                while (step())
                    continue;

                gameOver();

            }
        }).start();
    }

    public void start(double... data) {
        setData(data);

        start(false);
    }

    /**
     * Fait une étape. Retourne TRUE si il faut continuer.
     * 
     * @return
     */
    public boolean step() {
        double force = forceMag * action;
        double sinangle = Math.sin(angle);
        double cosangle = Math.cos(angle);
        double sinangle2 = Math.sin(angle2);
        double cosangle2 = Math.cos(angle2);

        double angleDotSq = angleDot * angleDot;
        double angle2DotSq = angle2Dot * angle2Dot;

        double common;
        if (useTwoPoles)
            common = (force + poleMassLength * angleDotSq * sinangle + pole2MassLength * angle2DotSq * sinangle2
                    - fricCart * (posDot < 0 ? -1 : 0)) / total2Mass;
        else
            common = (force + poleMassLength * angleDotSq * sinangle - fricCart * (posDot < 0 ? -1 : 0)) / total1Mass;

        double angleDDot;
        if (useTwoPoles)
            angleDDot = (9.8 * sinangle - cosangle * common
                    - fricPole * angleDot / poleMassLength) /
                    (halfPole * (fourthirds - poleMass * cosangle * cosangle /
                            total2Mass));
        else
            angleDDot = (9.8 * sinangle - cosangle * common
                    - fricPole * angleDot / poleMassLength) /
                    (halfPole * (fourthirds - poleMass * cosangle * cosangle /
                            total1Mass));

        double angle2DDot = (9.8 * sinangle2 - cosangle2 * common
                - fric2Pole * angle2Dot / pole2MassLength) /
                (half2Pole * (fourthirds - pole2Mass * cosangle2 * cosangle2 /
                        total2Mass));

        double posDDot;
        if (useTwoPoles)
            posDDot = common - (poleMassLength * angleDDot * cosangle + pole2MassLength * angle2DDot * cosangle2) /
                    total2Mass;
        else
            posDDot = common - (poleMassLength * angleDDot * cosangle) / total1Mass;

        /*
         * double force = action * forceMag;
         * 
         * double sinangle = Math.sin(angle);
         * double cosangle = Math.cos(angle);
         * 
         * double sinangle2 = Math.sin(angle2);
         * double cosangle2 = Math.cos(angle2);
         * 
         * double mT1 = poleMass * (1 - 3. / 4. * cosangle * cosangle);
         * double mT2 = pole2Mass * (1 - 3. / 4. * cosangle2 * cosangle2);
         * 
         * double fT1 = poleMass * halfPole * angleDot * angleDot * sinangle;
         * fT1 += poleMass * cosangle * 3. / 4. * (fricPole * angleDot /
         * (poleMass * halfPole) + g * sinangle);
         * 
         * double fT2 = pole2Mass * half2Pole * angle2Dot * angle2Dot *
         * sinangle2;
         * fT2 += pole2Mass * cosangle2 * 3. / 4. * (fric2Pole * angle2Dot /
         * pole2Mass / half2Pole + g * sinangle2);
         * 
         * double posDDot = (force - fricCart * Math.signum(posDot) + fT1 + fT2)
         * / (cartMass + mT1 + mT2);
         * 
         * double angleDDot = -3. / (4 * halfPole)
         * (posDDot * cosangle + g * sinangle + fricPole * angleDot / (poleMass
         * * halfPole));
         * double angle2DDot = -3. / (4 * half2Pole)
         * (posDDot * cosangle2 + g * sinangle2 + fric2Pole * angle2Dot /
         * (pole2Mass * half2Pole));
         */
        // Now update state.
        pos += posDot * tau;
        posDot += posDDot * tau;

        angle += angleDot * tau;
        angleDot += angleDDot * tau;

        angle2 += angle2Dot * tau;
        angle2Dot += angle2DDot * tau;

        posList.addLast(pos);
        posDotList.addLast(posDot);

        angleList.addLast(angle);
        angleDotList.addLast(angleDot);

        angle2List.addLast(angle2);
        angle2DotList.addLast(angle2Dot);

        while (posList.size() > 100)
            posList.removeFirst();
        while (posDotList.size() > 100)
            posDotList.removeFirst();

        while (angleList.size() > 100)
            angleList.removeFirst();
        while (angleDotList.size() > 100)
            angleDotList.removeFirst();

        while (angle2List.size() > 100)
            angle2List.removeFirst();
        while (angle2DotList.size() > 100)
            angle2DotList.removeFirst();

        double angleLimit = useTwoPoles ? angle2Limit : angle1Limit;

        if (Math.abs(angle) > angleLimit
                || (useTwoPoles && Math.abs(angle2) > angleLimit)
                || Math.abs(pos) > posLimit) {
            lost = true;
            return false;
        }

        steps++;

        onStep();

        return steps < timeLimit;
    }
}
