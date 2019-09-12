package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.OverdriveableRobot;
import exceptions.ItemTooHeavyException;

public class MailPool implements IMailPool {
	public int overdriveCount = 0;
	
	public int getOverdriveCount() {
		return overdriveCount;
	}
	
	private class Item {
		int priority;
		int destination;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			destination = mailItem.getDestFloor();
			this.mailItem = mailItem;
		}
		
		 private Boolean checkHiPriority() {
			 if (priority > 50) {
				 return true;
			 }else {
				 return false;
			 }
		 }
	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> pool;
	private LinkedList<Robot> robots;
	private LinkedList<OverdriveableRobot> odRobots;
	
	public MailPool(int nrobots){
		// Start empty
		pool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
		odRobots = new LinkedList<OverdriveableRobot>();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws ItemTooHeavyException {
		try{
			ListIterator<Robot> i = robots.listIterator();
			while (i.hasNext()) loadRobot(i);
		} catch (Exception e) { 
            throw e; 
        } 
		
		try{
			ListIterator<OverdriveableRobot> i = odRobots.listIterator();
			while (i.hasNext()) loadOdRobot(i);
		} catch (Exception e) { 
            throw e; 
        } 
	}
	
	private void loadRobot(ListIterator<Robot> i) throws ItemTooHeavyException {
		Robot robot = i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<Item> j = pool.listIterator();
		if (pool.size() > 0) {
			try {
			robot.addToHand(j.next().mailItem); // hand first as we want higher priority delivered first
			j.remove();
			if (pool.size() > 0) {
				robot.addToTube(j.next().mailItem);
				j.remove();
			}
			robot.dispatch(); // send the robot off if it has any items to deliver
			i.remove();       // remove from mailPool queue
			} catch (Exception e) { 
	            throw e; 
	        } 
		}
	}
	
	private void loadOdRobot(ListIterator<OverdriveableRobot> i) throws ItemTooHeavyException {
		OverdriveableRobot odRobot = i.next();
		assert(odRobots.isEmpty());
		Item temp;
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<Item> j = pool.listIterator();
		if (pool.size() > 0) {
			try {
			temp = j.next();
			odRobot.addToHand(temp.mailItem); // hand first as we want higher priority delivered first
			if (temp.checkHiPriority()) { // overdrive if item priority > 50
				odRobot.setOverdrive(true);
				// this.overdriveCount++;
			}
			j.remove();
			if ((pool.size() > 0)  && (odRobot.getOverdrive() == false)){
				temp = j.next();
				while (temp.checkHiPriority()) { // Priority item cannot be put into tube
					temp = j.next();
				} 
				odRobot.addToTube(temp.mailItem);
				j.remove();
			}
			odRobot.dispatch(); // send the robot off if it has any items to deliver
			i.remove();       // remove from mailPool queue
			} catch (Exception e) { 
	            throw e; 
	        } 
		}
		// System.out.println(overdriveCount);
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}
	
	@Override
	public void registerWaiting(OverdriveableRobot robot) {
		odRobots.add(robot);
	}

}
