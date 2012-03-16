package pole;

/**
 * General interface, used to control the cart.
 * 
 * @author gyscos
 * 
 */
public interface PoleController {

    /**
     * Returns the force applied to the cart when the system configuration is as
     * given in parameter.
     * 
     * @param data
     * @return
     */
    public double getAction(double... data);
}
