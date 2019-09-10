package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

public interface IRobotFunctionalities {

    public void dispatch();
    public void step() throws ExcessiveDeliveryException; 
	public MailItem getTube();
	public boolean isEmpty();
	public void addToHand(MailItem mailItem) throws ItemTooHeavyException;
	public void addToTube(MailItem mailItem) throws ItemTooHeavyException;
	public void printStatistics();
}
