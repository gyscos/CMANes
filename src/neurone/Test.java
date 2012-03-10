package neurone;

import pole.PoleFrame;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        PoleFrame frame = new PoleFrame() {
            @Override
            public void onStep() {
                // pole.setAction(reseau.getOutput(pole.getData()));
            }
        };
        frame.start();

    }

}
