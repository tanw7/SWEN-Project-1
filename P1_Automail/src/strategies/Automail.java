package strategies;

import automail.IMailDelivery;
import automail.OverdriveableRobot;
import automail.Robot;
import java.util.Properties;

public class Automail {
	      
    public Robot[] robots;
    public OverdriveableRobot[] odRobots;
    public IMailPool mailPool;
    
    public Automail(IMailPool mailPool, IMailDelivery delivery, int numRobots, Properties automailProperties) {
    	// Swap between simple provided strategies and your strategies here
    	    	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	
    	/** Initialize robots */
    	if (Boolean.parseBoolean(automailProperties.getProperty("Overdrive")) == true) {
    		odRobots = new OverdriveableRobot[numRobots];
        	for (int i = 0; i < numRobots; i++) odRobots[i] = new OverdriveableRobot(delivery, mailPool);
    	}else {
        	robots = new Robot[numRobots];
        	for (int i = 0; i < numRobots; i++) robots[i] = new Robot(delivery, mailPool);
    	}
    }
    
}
