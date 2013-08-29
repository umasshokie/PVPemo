package sim.app.pvpEmo;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class BehaviorProcessor {

	private SparseGrid2D world;
	private int direct;
	private Anger anger1;
	private Anger anger2;
	private Anger anger;
	private Sadness sadness1;
	private Sadness sadness2;
	private Sadness sadness;
	private Disgust disgust1;
	private Disgust disgust2;
	private Disgust disgust;
	private Fear fear1;
	private Fear fear2;
	private Fear fear;
	private Happiness happy1;
	private Happiness happy2;
	private Happiness happy;
	private Surprise surprise1;
	private Surprise surprise2;
	private Surprise surprise;
	private Int2D pLoc2;
	private Int2D pLoc1;
	private Int2D pLoc;
	private Mood mood2;
	private Mood mood1;
	private Mood mood;
	

	//Sends in arguments of a Bag of items, and probability of movement, returns array of new probabilites
	BehaviorProcessor(SparseGrid2D grid){
		world = grid;
	}
	
	// Updates the probabilities of a Predator based on its vision.
	public double[] updateProbPred(Bag locs, Bag seen, double[] oldProb, Predator predator, SimState state, int maxHunger){
		
		assert(world.getObjectLocation(predator) != null);
		
		this.checkDisease(predator, state);

		direct = predator.direction;
		double[] newProb = new double[8];
		
		//Emotions Update
		
		
		
		for(int i = 0; i < oldProb.length; i++){
			//System.out.println("oldProb[i]: " + oldProb[i]);
			newProb[i] = oldProb[i];
		}
	
		this.updateEmotionState(predator, newProb, oldProb);
		
		Bag sLocations = new Bag();
		Bag fLocations = new Bag();
		//System.out.println("Seen.size(): " + seen.size());
		//System.out.println("Locs.size(): " + locs.size());
		assert (seen.size() == locs.size());
		
		//System.out.println("Predator Location: " + predator.grid.getObjectLocation(predator));
		//System.out.println("Predator direction: " + direct);
		//System.out.println("Predator Last Meal: " + predator.lastMeal);
		//System.out.println();
		
		for(int s = 0; s < seen.size(); s++){
			//System.out.println("I saw " + seen.get(s) + "at " + locs.get(s));
			if(seen.get(s).getClass().equals(Prey.class)){
				fLocations.add(locs.get(s));
				predator.lastSeenPrey=0;
			}
			else if (seen.get(s).getClass().equals(Predator.class)){
				sLocations.add(locs.get(s));
				predator.lastSocial=0;
			}
		}

		/*if(fLocations.size() == 1){
			Bag zeroIndexes = new Bag();
			zeroIndexes = this.findEmptySquareLocations(predator.grid.getObjectLocation(predator), fLocations);
			Int2D food = (Int2D) fLocations.get(0);
			newProb[this.probIndex(predator.grid.getObjectLocation(predator), food, predator.direction)] = 100;
			for(int i = 0; i < zeroIndexes.size(); i++){
				newProb[this.probIndex(predator.grid.getObjectLocation(predator), (Int2D) zeroIndexes.get(i), predator.direction)] = 0;
			}
		}*/
		//opposite is reduced by half, goes to location of reward
		//two sides reduced by one fourth, goes to location around reward
		
		this.rewardProbability(fLocations, newProb, oldProb, predator);
		this.rewardProbability(sLocations, newProb, oldProb, predator);
		this.emotions(predator);
		
		return newProb;
}
	
	
	/***************************************PREY***********************************************/
	//Working on predator first	
	public double[] updateProbPrey(Bag locs, Bag seen, double[] oldProb, Prey prey, SimState state){
		
		assert(world.getObjectLocation(prey) != null);
		
		this.checkDisease(prey, state);

		direct = prey.direction;
		double[] newProb = new double[8];
		
		for(int i = 0; i < oldProb.length; i++){
			//System.out.println("oldProb[i]: " + oldProb[i]);
			newProb[i] = oldProb[i];
		}
	
		this.updateEmotionState(prey, newProb, oldProb);
		
		Bag sLocations = new Bag();
		Bag fLocations = new Bag();
		Bag pLocations = new Bag();
		//System.out.println("Seen.size(): " + seen.size());
		//System.out.println("Locs.size(): " + locs.size());
		assert (seen.size() == locs.size());
		
		//System.out.println("Prey Location: " + prey.grid.getObjectLocation(prey));
		//System.out.println("Prey direction: " + direct);
		//System.out.println();
		
		for(int s = 0; s < seen.size(); s++){
			//System.out.println("I saw " + seen.get(s) + "at " + locs.get(s));
			if(seen.get(s).getClass().equals(Food.class))
				fLocations.add(locs.get(s));
			else if (seen.get(s).getClass().equals(Prey.class)){
				sLocations.add(locs.get(s));
				prey.lastSocial = 0;
				prey.lastSeenPrey=0;
			}
			else if(seen.get(s).getClass().equals(Predator.class)){
				pLocations.add(locs.get(s));
				prey.velocity = 2;
				prey.lastSeenPredator=0;
			}
		}

		/*if(fLocations.size() == 1){
			Bag zeroIndexes = new Bag();
			zeroIndexes = this.findEmptySquareLocations(prey.grid.getObjectLocation(prey), fLocations);
			Int2D food = (Int2D) fLocations.get(0);
			newProb[this.probIndex(prey.grid.getObjectLocation(prey), food, prey.direction)] = 100;
			for(int i = 0; i < zeroIndexes.size(); i++){
				newProb[this.probIndex(prey.grid.getObjectLocation(prey), (Int2D) zeroIndexes.get(i), prey.direction)] = 0;
			}
		}*/
			
		//opposite is reduced by half, goes to location of reward
		//two sides reduced by one fourth, goes to location around reward
		
		this.rewardProbability(fLocations, newProb, oldProb, prey);
		this.rewardProbability(sLocations, newProb, oldProb, prey);
		this.avoidanceProbability(pLocations, newProb, oldProb, prey);
		
		
		return newProb;
	
	}
	
	private void checkDisease(Predator predator, SimState state){
		
		//If diseased, will move half of the time
		if(predator.isDiseased){
			int rand = state.random.nextInt(2);
			if(rand == 1)
				predator.velocity = 0;
			else
				predator.velocity = 1;
			
			//Not reproducing
			predator.setRepRate(0);
		}
	}
	
	private void checkDisease(Prey prey, SimState state){
		
		//If diseased will move half of the time
		if(prey.isDiseased){
			int rand = state.random.nextInt(2);
			if(rand == 1)
				prey.velocity = 0;
			else
				prey.velocity = 1;
		}
		
		//Not reproducing
		
		prey.setRepRate(0);
	}
	
	
	// This method gets the opposing cell (and soon to be side cells)
	// of a particular item's location in relation to the animal 
	// Helps with editing opposing probabilities
	private Bag getOpposite(Int2D location, Int2D pLoc, int direct){
		Bag opposites = new Bag();
		
		int x = location.getX();
		int y = location.getY();
		//UH OH DIRECTION!!!!
		int sideX1 = -1;
		int sideX2 = -1;
		int sideY1 = -1;
		int sideY2 = -1;
		
		/*System.out.println("location: " + location);
		System.out.println("pLoc: " + pLoc);
		System.out.println("direct: " + direct);
		System.out.println("x: "+ x);
		System.out.println("y: " + y);*/
/*****************************************NORTH************************************/		
		//Facing North
		if(direct == 0){
			
			//Peripheral vision
			
			//Left Peripheral
			if(y == pLoc.y && x == world.tx(pLoc.x - 1)){
				x = world.tx(pLoc.x + 1);
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			//Right Peripheral
			else if(y == pLoc.y && x == world.tx(pLoc.x + 1)){
				x = world.tx(pLoc.x - 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
		//Three Frontal Visions and FURTHER VISIONS
			
			//Left Front opposite OR TWO LEFT FURTHER VISIONS
			else if((y == world.ty(pLoc.y-1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y-2) && x == world.tx(pLoc.x-2) ||
					(y == world.ty(pLoc.y-2) && x == world.tx(pLoc.x-1)))){
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x);
				sideY1 = world.ty(pLoc.y + 1);
				
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x)) || 
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x))){
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			//Right Front or TWO FURTHER RIGHT VISIONS
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			else {
				System.out.println("Error - North");
			}
		}// end of South
		
/*****************************************SOUTH************************************/				
	
		//Assuming facing south
		else if(direct == 1){
			
		//Peripheral vision
			
			//Left Peripheral
			if(y == pLoc.y && x == world.tx(pLoc.x - 1)){
				x = world.tx(pLoc.x + 1);
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Right Peripheral
			else if(y == pLoc.y && x == world.tx(pLoc.x + 1)){
				x = world.tx(pLoc.x - 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
		//Three Frontal Visions and FURTHER VISIONS
			
			//Right Front opposite OR TWO RIGHT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-2) ||
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-1)))){
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y + 1) && x == world.tx(pLoc.x)) || 
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x))){
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
			//Left Front or TWO FURTHER LEFT VISIONS
			else if((y == world.ty(pLoc.y + 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
			else {
				System.out.println("Error - South");
			}
		}// end of South
		/*****************************************EAST************************************/	
		//East
		else if(direct == 2){
			
		//Peripheral vision
			
			//Right Peripheral
			if(y == world.ty(pLoc.y + 1) && x == pLoc.x){
				y = world.ty(pLoc.y - 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Left Peripheral
			else if(y == world.ty(pLoc.y - 1) && x == pLoc.x){
				y = world.ty(pLoc.y + 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
		//Three Frontal Visions and FURTHER VISIONS
			
			//Left Front opposite OR TWO LEFT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x+1)) || 
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x+2) ||
					(y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x+2)))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y) && x == world.tx(pLoc.x + 1)) || 
					(y == world.ty(pLoc.y) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			//Right Front or TWO FURTHER RIGHT VISIONS
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x + 2)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			else {
				System.out.println("Error - East");
			}
		}// end of East
		
		/*****************************************WEST************************************/	
		
		//WEST
		else {
			
		//Peripheral vision
			
			//Left Peripheral
			if(y == world.ty(pLoc.y + 1) && x == pLoc.x){
				y = world.ty(pLoc.y - 1);
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Right Peripheral
			else if(y == world.ty(pLoc.y - 1) && x == pLoc.x){
				y = world.ty(pLoc.y + 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
		//Three Front Visions and FURTHER VISIONS
			
			//Right Front opposite OR TWO RIGHT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x-2)) ||
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-2))){
			
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y) && x == world.tx(pLoc.x - 1)) || 
					(y == world.ty(pLoc.y) && x == world.tx(pLoc.x - 2))){
				
				x = world.ty(pLoc.x + 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			//Left Front or TWO FURTHER LEFT VISIONS
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x - 1)) ||
					(y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x - 2)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x - 2))){
			
				
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			else {
				System.out.println("Error - West");
			}
		}// end of West
		//Adding locations to the bag
		/*System.out.println("When adding:");
		System.out.println("location: " + location);
		System.out.println("pLoc: " + pLoc);
		System.out.println("direct: " + direct);
		System.out.println("x: "+ x);
		System.out.println("y: " + y);*/
		opposites.add(new Int2D(x,y));
		
		assert(sideX1 >= 0 && sideX2 >=0 && sideY1 >=0 && sideY2 >=0);
		
		opposites.add(new Int2D(sideX1, sideY1));
		opposites.add(new Int2D(sideX2, sideY2));
		
		/*for(int o = 0; o < opposites.size(); o++){
			System.out.println("Opposites[o]: " + opposites.get(o));
		}*/
		
		return opposites;
		
	}// end of opposite method
	
	//Need adjacent Method that finds two adjacent sides
	// Takes argument of location, returns bag of two
	// Adjacent Int2D locations
	private Bag findAdjSquares(Int2D loc, Int2D pLoc, int direction){
		Bag adj = new Bag();
		//System.out.println("FindAdj Location: " + loc);
		//System.out.println("FIndAdj PLOC: " + pLoc);
		int x = loc.x;
		int y = loc.y;
		assert(pLoc != null);
		//For further visual in north direction, reset adj squares to correspond to possible movement square
		//North
		if(direction == 0){
			if(world.ty(loc.y) == world.ty(pLoc.y - 2)){
				if(world.tx(loc.x) == world.tx(pLoc.x - 2) || world.tx(loc.x) == world.tx(pLoc.x - 1)){
					x = world.tx(pLoc.x - 1);
					y = world.ty(pLoc.y - 1);
				}
				else if (world.tx(loc.x) == world.tx(pLoc.x)){
					y = world.ty(pLoc.y - 1);
				}
				else if(world.tx(loc.x) == world.tx(pLoc.x + 1) || world.tx(loc.x) == world.tx(pLoc.x + 2)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y - 1);
				}
			}
		}
		//South
		else if (direction == 1){
			if(world.ty(loc.y) == world.ty(pLoc.y + 2)){
				if(world.tx(loc.x) == world.tx(pLoc.x -2) || world.tx(loc.x) == world.tx(pLoc.x - 1)){
					x = world.tx(pLoc.x - 1);
					y = world.ty(pLoc.y + 1);
				}
				else if (world.tx(loc.x) == world.tx(pLoc.x)){
					y = world.ty(pLoc.y + 1);
				}
				else if (world.tx(loc.x) == world.tx(pLoc.x + 1) || world.tx(loc.x) == world.tx(pLoc.x + 2)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y + 1);
				}
			}
		}
		//East
		else if (direction == 2){
			if (world.tx(loc.x) == world.tx(pLoc.x + 1)){
				x = world.tx(pLoc.x + 1);
			}
			else if(world.tx(loc.x) == world.tx(pLoc.x + 2)){
				if(world.ty(loc.y) == world.ty(pLoc.y - 2) || world.ty(loc.y) == world.ty(pLoc.y - 1)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y - 1);
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y)){
					x = world.tx(pLoc.x + 1);
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y + 1) || world.ty(loc.y) == world.ty(pLoc.y + 2)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y + 1);
				}
			}
			
		}
		//West
		else{
			if (world.tx(loc.x) == world.tx(pLoc.x - 2)){
				if(world.ty(loc.y) == world.ty(pLoc.y - 2) || world.ty(loc.y) == world.ty(pLoc.y - 1)){
					x = world.tx(pLoc.x - 1);
					y = world.ty(pLoc.y - 1);
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y)){
					x = world.tx(pLoc.x - 1);
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y + 1) || world.ty(loc.y) == world.ty(pLoc.y + 2)){
					x = world.tx(pLoc.x - 1);
					y = world.ty(pLoc.y + 1);
				}
			}
		}
	
		//Adding first element to the bag to be location of reward
		adj.add(new Int2D(x, y));
		//System.out.println("X and Y: " + adj.get(0));
		
		//Left location probabilities
		if(world.tx(x) < world.tx(pLoc.x)){
			if(world.ty(y) > world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y)));
			}
			else if(world.ty(y)< world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y)));
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y - 1)));
			}
			else{
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y - 1)));
			}
		}// end of left location possibilities
			
		//Right Location Possibilities
		else if (world.tx(x) > world.tx(pLoc.x)){
			if(world.ty(y) < world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y - 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y)));
			}
			else if (world.ty(y)>world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y)));
			}
			else{
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y - 1)));
			}
		} // end of right location possibilites
			
		//Middle possibilites
		else{
			if(world.ty(y) > world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y + 1)));			
			}
			else if (world.ty(y) < world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y - 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y - 1)));
			}
			else {
				System.out.println("Adjacent Error");
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y)));
			}
		}// end of middle possibilities
	
		/*for(int a = 0; a < adj.size(); a++){
			System.out.println("Adjacent Bag:" + a + " " + adj.get(a));
		}*/
		
		
		return adj;
	}
	
	//This method returns the index of a probability cell in movement
	// Useful for editing probabilites
	public int probIndex(Int2D pLoc, Int2D itemLoc, int direct){
		
		//North
		if(direct == 0){
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 0;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 5;
				else
					return 3;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 2;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 7;
				else
					return 4;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 1;
				else
					return 6;
			}
		}//end of North
		//South
		else if (direct == 1){
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 7;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 2;
				else
					return 4;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 5;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 0;
				else
					return 3;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 6;
				else
					return 1;
			}
		}//end of South
		//East
		else if (direct == 2){
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 5;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 7;
				else
					return 6;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 0;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 2;
				else
					return 1;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 3;
				else
					return 4;
			}
		}//end of East
		else{
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 2;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 0;
				else
					return 1;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 7;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 5;
				else
					return 6;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 4;
				else
					return 3;
			}
		}// end of West
	}// end of method

	//Returns indexes of empty squares
	protected int[] findEmptySquares(Int2D pLoc, Bag usedSquares){
		
		int[] indexes = new int[8];
		
		return indexes;
	}
	
	protected Bag findEmptySquareLocations(Int2D pLoc, Bag usedSquares){
		Bag emptyLocations = new Bag();
		Bag movableLoc = new Bag();
		movableLoc = this.findMovableLocations(pLoc);
		for(int k = 0; k < usedSquares.size(); k++){
			for(int i = 0; i < movableLoc.size(); i++){
				if(!movableLoc.get(i).equals(usedSquares.get(k)))
					emptyLocations.add(movableLoc.get(i));
			}
		}
		return emptyLocations;
	}
	protected void increaseToward(int last, Bag movable, double[]newProb, Int2D pLoc, Bag usedSquares, Animal p, int type){
		
		/*double drive;
		double x = 0;
		Bag emptyLoc = this.findEmptySquareLocations(pLoc, usedSquares);
		int[] emptySq = findEmptySquares(pLoc, usedSquares);
		for(int i = 0; i <emptySq.length; i++)
			x += newProb[i];
		
		int numMov = movable.size();
		
		if(type == 0)
			drive = (double)last/ p.maxHunger;
		else
			drive = (double)last/ p.maxSocial;
		
		double prob = x * drive;
		if(prob == x){
			for(int g = 0; g < emptyLoc.size(); g++){
				Int2D emptySqL =  (Int2D) emptyLoc.get(g);
				int index = this.probIndex(pLoc, emptySqL, direct);
				newProb[index] = 0;
			}
		}
		double g = prob/ numMov;
		for(int j = 0; j < numMov; j++){
			Int2D temp = (Int2D) movable.get(j);
			int index = this.probIndex(pLoc, temp, direct);
			newProb[index] = newProb[index] + g;
		}
		//All adjacent should get g
		int numLeft = 8 - numMov;
		double dec = prob/ numLeft;
		//Find others locations in a bag

		for(int k = 0; k < emptyLoc.size(); k++){
			Int2D emptySqL =  (Int2D) emptyLoc.get(k);
			int index = this.probIndex(pLoc, emptySqL, direct);
			newProb[index] = newProb[index] - dec;
		}
		//Subtract the probability
		//All others get this probability
		double subtractTotal = 0;
		int negativeTotal = 0;
		int[] negativeIndexes = new int[8];
		// Check to make sure none of the probabilities are <0
		for(int h = 0; h< newProb.length; h++){
			if(newProb[h] < 0){
				//If not subtract from others. (which square)
				subtractTotal = subtractTotal + newProb[h];
				newProb[h] = 0;
			}
		}// end of for loop
		double subtractIndividual = subtractTotal/(8 - negativeTotal);
		
		for(int i = 0; i < negativeIndexes.length; i++){
			if(negativeIndexes[i] != 0 && i !=0)
				newProb[negativeIndexes[i]] = newProb[negativeIndexes[i]] - subtractIndividual;
		}
		*/
		
	} // end of method
	
	public void rewardProbability(Bag locations, double[]newProb, double[]oldProb, Animal p){
		
		Bag oldRewardLoc = new Bag();
		Bag usedSquares = new Bag();
		Int2D pLoc = world.getObjectLocation(p);
		double mainDivider = 2.0;
		double adjDivider = 4.0;
		
		//Reward Probability
		for(int f = 0; f< locations.size(); f++){
			
			if(locations.size() > 0){
				
				if((locations.get(0).equals(Food.class) && p.getClass().equals(Prey.class)) 
						|| (locations.get(0).equals(Prey.class) && p.getClass().equals(Predator.class))){
					this.increaseToward(p.lastMeal, this.findMovableLocations(pLoc), newProb, pLoc, usedSquares, p, 0);
					break;
				}
				else if (locations.get(0).equals(p.getClass())){
					this.increaseToward(p.lastSocial, this.findMovableLocations(pLoc), newProb, pLoc, usedSquares, p, 1);
					break;
				}
				//System.out.println("Ploc: " + pLoc);
				/*for(int i = 0; i < newProb.length; i++){
					System.out.println("After: newProb[i]: " + newProb[i]);
				}*/
				
			}
			
			Bag oppositeAll = new Bag();
			//Set of conditions. If hungry
			//Reward locations
			Int2D rewardLoc = (Int2D) locations.get(f);
			
			//System.out.println("Initial rewardLoc: " + rewardLoc);
			//System.out.println("RewardLoc: " + rewardLoc);
			Bag adj = this.findAdjSquares(rewardLoc, pLoc, direct);
			
			assert(rewardLoc.x >=0 && rewardLoc.y >= 0);
			rewardLoc = (Int2D) adj.get(0);
			//System.out.println("RewardLoc after FindAdj: " + rewardLoc);
			Int2D adjLoc1 = (Int2D) adj.get(1);
			Int2D adjLoc2 = (Int2D) adj.get(2);
			//System.out.println("Adjacent Loc1: " + adjLoc1);
			//System.out.println("Adjacent Loc2: " + adjLoc2);
		
			assert(rewardLoc.x >=0 && rewardLoc.y >= 0);
			Bag opp = this.getOpposite(rewardLoc, pLoc, direct);
			Int2D oppositeCell = (Int2D) opp.get(0);
			Int2D adjCell1 = (Int2D) opp.get(1);
			Int2D adjCell2 = (Int2D) opp.get(2);
			
			Bag usedS = new Bag();
			usedS.addAll(adj);
			oppositeAll = this.findEmptySquareLocations(pLoc, usedS);
			
			/*System.out.println("Opposite Cell: " + oppositeCell);
			System.out.println("Opp Adj Cell 1:" + adjCell1);
			System.out.println("Opp Adj Cell 2:" + adjCell2);*/
			double rewardTotal = 0;
			
			for(int k = 0; k< oppositeAll.size(); k++){
				Int2D temp = (Int2D) oppositeAll.get(k);
				int index = this.probIndex(pLoc, temp, direct);
				rewardTotal = newProb[index]/4;
				newProb[index] = newProb[index] - (newProb[index]/4);
			}
			//Indexes
			//int directOppositeIndex = this.probIndex(pLoc, oppositeCell, direct);
			//int adjOpposite1Index = this.probIndex(pLoc, adjCell1, direct);
			//int adjOpposite2Index = this.probIndex(pLoc, adjCell2, direct);
			int rewardIndex = this.probIndex(pLoc, rewardLoc, direct);
			int adj1Index = this.probIndex(pLoc, adjLoc1, direct);
			int adj2Index = this.probIndex(pLoc, adjLoc2, direct);
			
			/*System.out.println("DirectOppositeIndex: " + directOppositeIndex);
			System.out.println("AdjacentOppositeIndex1: " + adjOpposite1Index);
			System.out.println("AdjacentOppositeIndex2: " + adjOpposite2Index);
			System.out.println("RewardIndex: " + rewardIndex);
			System.out.println("Adjacent 1 Index: " + adj1Index);
			System.out.println("Adjacent 2 Index: " + adj2Index);
			
			System.out.println("Adding for largest:" + (newProb[directOppositeIndex]/ mainDivider));
			System.out.println("Adding for adjacent1Opposite: " + (newProb[adjOpposite1Index]/adjDivider));
			System.out.println("Adding for adjacent2Opposite: " + (newProb[adjOpposite2Index]/adjDivider));*/
			
			if(oldRewardLoc.contains(rewardLoc)){
				mainDivider = 1.5;
				adjDivider = 2.0;
				
			}
			/*double opposite = newProb[directOppositeIndex];
			double directOppositeReduction = opposite/mainDivider;
			
			
			double adjOpp1 = newProb[adjOpposite1Index];
			double adjOpp1Reduction = adjOpp1/adjDivider;
			
			double adjOpp2 = newProb[adjOpposite2Index];
			double adjOpp2Reduction = adjOpp2/adjDivider;
			*/
			//variables for redistribution
			//newProb[directOppositeIndex] = oldProb[directOppositeIndex] - (directOppositeReduction);
			//newProb[adjOpposite1Index] = oldProb[adjOpposite1Index] - (adjOpp1Reduction);
			//newProb[adjOpposite2Index] = oldProb[adjOpposite2Index] - (adjOpp2Reduction);
			//updating probability reduction
			
			//add all reductions together, divide by four, half going to location of food source
			//double rewardTotal = directOppositeReduction + adjOpp1Reduction + adjOpp2Reduction;
			double singleReward = rewardTotal/4;
			newProb[rewardIndex] = oldProb[rewardIndex] + (2 * singleReward);
			newProb[adj1Index] = oldProb[adj1Index] + (singleReward);
			newProb[adj2Index] = oldProb[adj2Index] + (singleReward);
			
			oldRewardLoc.add(rewardLoc);
			usedSquares.add(rewardLoc);
			usedSquares.add(oppositeCell);
		}
		/*for(int i = 0; i < newProb.length; i++){
		System.out.println("Before: newProb[i]: " + newProb[i]);
		}*/
		
		
	}// end of method
	
	public void avoidanceProbability(Bag locations, double[]newProb, double[]oldProb, Animal p){
		
		Bag oldRewardLoc = new Bag();
		
		//Reward Probability
		for(int f = 0; f< locations.size(); f++){
			Int2D pLoc = world.getObjectLocation(p);
			
			//Set of conditions. If hungry
			//Reward locations
			Int2D predatorLoc = (Int2D) locations.get(0);
			
	
			//System.out.println("RewardLoc: " + rewardLoc);
			Bag adj = this.findAdjSquares(predatorLoc, pLoc, direct);
			Int2D adjLoc1 = (Int2D) adj.get(1);
			Int2D adjLoc2 = (Int2D) adj.get(2);
			//System.out.println("Adjacent Loc1: " + adjLoc1);
			//System.out.println("Adjacent Loc2: " + adjLoc2);
		
			
			Bag opp = this.getOpposite(predatorLoc, pLoc, direct);
			Int2D oppositeCell = (Int2D) opp.get(0);
			Int2D adjCell1 = (Int2D) opp.get(1);
			Int2D adjCell2 = (Int2D) opp.get(2);
			
			/*System.out.println("Opposite Cell: " + oppositeCell);
			System.out.println("Opp Adj Cell 1:" + adjCell1);
			System.out.println("Opp Adj Cell 2:" + adjCell2);
			*/
			
			//Indexes
			int directOppositeIndex = this.probIndex(pLoc, predatorLoc, direct);
			int adjOpposite1Index = this.probIndex(pLoc, adjLoc1, direct);
			int adjOpposite2Index = this.probIndex(pLoc, adjLoc2, direct);
			int rewardIndex = this.probIndex(pLoc, oppositeCell, direct);
			int adj1Index = this.probIndex(pLoc, adjCell1, direct);
			int adj2Index = this.probIndex(pLoc, adjCell2, direct);
			
			/*System.out.println("DirectOppositeIndex: " + directOppositeIndex);
			System.out.println("AdjacentOppositeIndex1: " + adjOpposite1Index);
			System.out.println("AdjacentOppositeIndex2: " + adjOpposite2Index);
			System.out.println("RewardIndex: " + rewardIndex);
			System.out.println("Adjacent 1 Index: " + adj1Index);
			System.out.println("Adjacent 2 Index: " + adj2Index);
			
			System.out.println("Adding for largest:" + (newProb[directOppositeIndex]/ mainDivider));
			System.out.println("Adding for adjacent1Opposite: " + (newProb[adjOpposite1Index]/adjDivider));
			System.out.println("Adding for adjacent2Opposite: " + (newProb[adjOpposite2Index]/adjDivider));
			*/
			
			//variables for redistribution
			newProb[rewardIndex] = newProb[rewardIndex] + newProb[directOppositeIndex];
			newProb[adj1Index] = newProb[adj1Index] + newProb[adjOpposite1Index];
			newProb[adj2Index] = newProb[adj2Index] + newProb[adjOpposite2Index];
			newProb[directOppositeIndex] = 0;
			newProb[adjOpposite1Index] = 0;
			newProb[adjOpposite2Index] = 0;
			//updating probability reduction
			
			
			oldRewardLoc.add(predatorLoc);
			
		}
		/*for(int i = 0; i < newProb.length; i++){
			System.out.println("Avoidance newProb[i]: " + newProb[i]);
		}*/
	}
	
	public Bag findMovableLocations(Int2D pLoc){
		Bag movable = new Bag();
		
		movable.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y - 1)));
		movable.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y)));
		movable.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y + 1)));
		movable.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y - 1)));
		movable.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y + 1)));
		movable.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y - 1)));
		movable.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y)));
		movable.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y + 1)));
		
		return movable;
	}
	
	public void emotions(Predator p){
		
		//Reproduction
		
		double fearAmount = p.fear.amount;
		double disgustAmount = p.dis.amount;
		double surpriseAmount = p.surprise.amount;
		
		//System.out.println("EatingChance Before: " + p.eatingChance);
		//System.out.println("RepNum Before: " + p.repRandNum);
		int fearRepNum = p.repRandNum*(int)(fearAmount *100);
		int disgustRepNum = p.repRandNum*(int)(disgustAmount * 100);
		int averageRepNum= (fearRepNum + disgustRepNum)/20;
		//System.out.println("AverageRepNum" + averageRepNum);
		if(averageRepNum > 0)
			p.repRandNum = averageRepNum;
		
		
		//Eating
		double eatingChance = 1.0 - disgustAmount;
		p.eatingChance = (int)eatingChance;
		
		//System.out.println("EatingChance After: " + p.eatingChance);
		//System.out.println("RepNum After: " + p.repRandNum);
		
		//Velocity
		if(fearAmount >= .5)
			p.velocity = 2;
		else if(surpriseAmount == 1.0)
			p.velocity = 0;
		//else
			//p.velocity = (int)p.mood.amount * 2;
		//Mood as velocity
	}
	
	public void emotions(Prey p){
		
		//Reproduction
		
		double fearAmount = p.fear.amount;
		double disgustAmount = p.dis.amount;
		
		//System.out.println("EatingChance Before: " + p.eatingChance);
		//System.out.println("RepNum Before: " + p.repRandNum);
		int fearRepNum = p.repRandNum*(int)(fearAmount *100);
		int disgustRepNum = p.repRandNum*(int)(disgustAmount * 100);
		int averageRepNum= (fearRepNum + disgustRepNum)/20;
		//System.out.println("AverageRepNum" + averageRepNum);
		if(averageRepNum > 0)
			p.repRandNum = averageRepNum;
		
		
		//Eating
		double eatingChance = 1.0 - disgustAmount;
		p.eatingChance = (int)eatingChance;
		
		//System.out.println("EatingChance After: " + p.eatingChance);
		//System.out.println("RepNum After: " + p.repRandNum);
	}
	
	public void updateEmotionState(Animal p, double[] newProb, double[] oldProb){
		anger2 = anger1;
		sadness2 = sadness1;
		disgust2 = disgust1;
		fear2 = fear1;
		happy2 = happy1;
		surprise2 = surprise1;
		pLoc2 = pLoc1;
		//mood2 = mood1;
		
		anger1 = anger;
		sadness1 = sadness;
		disgust1 = disgust;
		fear1 = fear;
		happy1 = happy;
		surprise1 = surprise;
		pLoc1 = pLoc;
		//mood1 = mood;
				
		
		anger = p.anger;
		sadness = p.sad;
		disgust = p.dis;
		fear = p.fear;
		happy = p.happy;
		surprise = p.surprise;
		pLoc = world.getObjectLocation(p);
		//mood = p.mood;
		
		//If one of the two locations is a movable location, then figure out the mood
		//Based on positivity or negativity either rewardLocation or avoidanceLocation
		Bag movable = new Bag();
		Bag rewards = new Bag();
		Bag avoid = new Bag();
		movable = this.findMovableLocations(pLoc);
		
		/*for(int i = 0; i < movable.size(); i++){
			if(mood2 != null){
				if(pLoc2.equals((Int2D)movable.get(i)) && (mood2.amount > .5))
					rewards.add(pLoc2);
				else if (pLoc2.equals((Int2D) movable.get(i)) && (mood2.amount < .5))
					avoid.add(pLoc2);
			}
			if(mood1 != null){
				if(pLoc1.equals((Int2D)movable.get(i)) && (mood1.amount > .5))
					rewards.add(pLoc1);
				else if (pLoc1.equals((Int2D) movable.get(i)) && (mood1.amount < .5))
					avoid.add(pLoc1);
			}
		}
		*/
		this.rewardProbability(rewards, newProb, oldProb, p);
		this.avoidanceProbability(avoid, newProb, oldProb, p);
	}
}// end of class
