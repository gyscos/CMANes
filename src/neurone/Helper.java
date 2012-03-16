package neurone;

public class Helper {
    public static double[] interpolate(double from, double to, int steps) {
        // Formula = Y = A.x^2 + B

        double result[] = new double[steps];

        double A = (from - to) / (from * from - to * to);
        double B = from - from * from * A;

        for (int i = 0; i < steps; i++) {
            double ratio = (double) i / (steps - 1);
            double x = from + ratio * (to - from);

            result[i] = A * x * x + B;
        }

        return result;
    }
}
