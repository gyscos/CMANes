package neurone;

import pole.PoleFrame;

/**
 * Simple class to test manual control of the cart.
 * 
 * @author gyscos
 * 
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        PoleFrame frame = new PoleFrame(false, true);
        frame.start(0, 0, 0.07, 0);
        frame.end();
    }

}
