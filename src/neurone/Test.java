package neurone;

import pole.PoleFrame;

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
