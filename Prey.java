package sim.app.pvpEmo;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class Prey extends Animal implements Steppable{

/**
	 * 
	 */
private static final long serialVersionUID = 1L;
private static int oldAge;
private static double defaultDeathRate;
private double actualDeathRate;
private static int deathRandNum;
private static double agingDeathMod;
private static double hungerDeathMod;
private static int lastMealLow;
private static int lastMealMed;
private static int lastMealHigh;
protected static int repRandNum;
protected int eatingChance;
private static int repAge;
private static double defaultRepRate;
private static double actualRepRate;
private Bag seen;
protected double diseaseRecovery = .25;

	
	Prey(SimState state, SparseGrid2D grid, int num){
	
		int directionNum= state.random.nextInt(3);
		if(directionNum == 0)
			direction = 0;
		else if(directionNum == 1)
			direction = 1;
		else if (directionNum == 2)
			direction = 2;
		else
			direction = 3;
		vP = new VisualProcessor(state);
		map = new ExpectationMap(grid.getWidth(), grid.getHeight(), expectMapDecayRate);
		maxHunger = 30;
		maxSocial = 30;
		maxRep = 200;
		actualRepRate = defaultRepRate;
		actualDeathRate = defaultDeathRate;
		reproductionAge = repAge;
		ID = "R" + num;
		eatingChance = 1;
	}
	protected final static void initializePrey(int maxH, int old,
			double dR, int dRN, double agDM, double hDM, int lmL, int lmM, int lmH, int rA, double dRR, int rRN ){
	
		maxHunger = maxH;
		oldAge = old;
		defaultDeathRate = dR;
		deathRandNum = dRN;
		agingDeathMod = agDM;
		hungerDeathMod = hDM;
		lastMealLow = lmL;
		lastMealMed = lmM;
		lastMealHigh = lmH;
		repAge = rA;
		defaultRepRate = dRR;
		repRandNum = rRN;
		
	}
	public void makeStoppable(Stoppable stopper){
		stop = stopper;
	}
	@Override
	public void step(SimState state) {
	 super.step(state);
	 

	 //Chance of Disease recovery
	 if(this.isDiseased && ((state.schedule.getTime() - diseaseTimestep) > lastMealLow)){
		 	double d = state.random.nextInt(diseaseRandomNum);
			double disease = d/diseaseRandomNum; 
			
			if(disease < diseaseRecovery)
				this.isDiseased = false;
	 }
		
	 
	 //Death Chance
	 if(this.iDie(state)){
		 	anger = new Anger(-1, this);
			sad = new Sadness(-1, this);
			dis = new Disgust(-1, this);
			fear = new Fear (-1, this);
			happy = new Happiness(1, this);
			surprise = new Surprise(0, this);
			//mood = new Mood(anger, sad, dis, fear, happy);
		 System.out.print(", " + ID);
			map.printMaps();
			System.out.print(", " + lastMeal);
			System.out.print(", " + lastSocial);
			System.out.print(", " + directChangeTotal + "\n");
		 return;
	 }
	 //Reproduction Chance
	 else if(this.iReproduce(state)){
		 	anger = new Anger(-1, this);
			sad = new Sadness(-1, this);
			dis = new Disgust(-1, this);
			fear = new Fear (-1, this);
			happy = new Happiness(1, this);
			surprise = new Surprise(0, this);
			//mood = new Mood(anger, sad, dis, fear, happy);
		 System.out.print(", " + ID);
			map.printMaps();
			System.out.print(", " + lastMeal);
			System.out.print(", " + lastSocial);
			System.out.print(", " + directChangeTotal + "\n");
		 return;
	 }
	 //Chance of Eating
	 else if(this.willEat(grid, state)){
		 	anger = new Anger(-1, this);
			sad = new Sadness(-1, this);
			dis = new Disgust(-1, this);
			fear = new Fear (-1, this);
			happy = new Happiness(1, this);
			surprise = new Surprise(0, this);
			//mood = new Mood(anger, sad, dis, fear, happy);
		 System.out.print(", " + ID);
			map.printMaps();
			System.out.print(", " + lastMeal);
			System.out.print(", " + lastSocial);
			System.out.print(", " + directChangeTotal);
			System.out.print(", " + "Prey ate" + "\n");
		return;
	 }
	 //See & process
	 else 
		 this.vision(state, grid);
		
	 	anger = new Anger(-1, this);
		sad = new Sadness(-1, this);
		dis = new Disgust(-1, this);
		fear = new Fear (-1, this);
		happy = new Happiness(1, this);
		surprise = new Surprise(0, this);
		//mood = new Mood(anger, sad, dis, fear, happy);
	//End of Step, print out tests
			System.out.print(", " + ID);
			map.printMaps();
			System.out.print(", " + lastMeal);
			System.out.print(", " + lastSocial);
			System.out.print(", " + directChangeTotal + "\n");
	}
	
	//Method which determines whether or not the Prey will eat on location.
	public boolean willEat(SparseGrid2D grid, SimState state){
		
		if(lastMeal < lastMealLow)
			return false;
		else if(lastMeal < lastMealMed){
			if(state.schedule.getTime()%eatingChance != 0)
				return false;
		}
		
		//Eating Prey on the same location
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
		
		
			int gridNum = grid.getObjectsAtLocationOfObject(this).size();
			
			
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				if(obj.getClass().equals(Food.class) && obj != null){
					this.eat(obj, state);
					return true;
				}// end of if
			}// end of for loop
		
		
		return false;
	}

	//Method for actually eating/ removing food from the grid.
	public void eat(Object p, SimState state){
		
		//System.out.println(p);
			Food food = (Food) p;
			assert(food != null);
			if(food.isDiseased()){
				this.setDisease(true);
				this.diseaseTimestep = state.schedule.getTime();
			}
			//System.out.println(this + " ate " + p);
			food.amount = food.amount - .9;
			if(food.amount <0){
				//amount = 0.0;
				//may be a point where it is being removed, but not stopped.
				food.stop.stop();
				grid.remove(food);
				
			}
			lastMeal = 0;
			
			//System.out.println("Food is removed");
		
	}
	
	public void setDiseased(boolean dis){
		isDiseased = dis;
	}
	
	
	public void reproduce(SimState state){
		
		Prey p = new Prey(state, grid, numPrey + 1);
		numPrey++;
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		Stoppable stop = state.schedule.scheduleRepeating(p);
		p.makeStoppable(stop);
	}
	
	public boolean isDiseased(){
		return isDiseased;
	}

	public boolean iDie(SimState state){
		//older = more likely to die
	 	//if(age>oldAge)
	 		//actualDeathRate = actualDeathRate * agingDeathMod;
	 	
	 	//Last meal, more likely to die
	 	if(lastMeal > lastMealMed)
			actualDeathRate = actualDeathRate * hungerDeathMod;
		/*//System.out.println("deathRate: " + deathRate);
	 	
	 	if(lastMeal > lastMealHigh){
			 stop.stop();
			 numPrey--;
			 grid.remove(this);
			 return true;
		 }*/
	 	
	 	// Death Rate
		double d = state.random.nextInt(deathRandNum);
		double death = d/deathRandNum;
		
		//System.out.println("d: " + d + " death: " + death);
		if(death < actualDeathRate && death != 0){
			this.stop.stop();
			numPrey--;
			grid.remove(this);
			return true;
		}
		return false;
	}
	
	public boolean iReproduce(SimState state){
	// Reproduction Rate
		double r = state.random.nextInt(repRandNum);
		double repo = r/repRandNum;
		if(repo <= actualRepRate && age >= repAge && numPrey<maxPrey){
			this.reproduce(state);
			return true;
			}
		return false;
	}
	public void vision(SimState state, SparseGrid2D grid){
		
		Int2D cord = grid.getObjectLocation(this);
		assert(cord != null);

		seen = vP.sight(cord.x, cord.y, state, direction);
		Bag locations = new Bag();
		if(state.schedule.getTime()%2 != 0)
			map.updateMapsPrey(seen, grid);
		
		//map.printMaps();
		for(int s = 0; s < seen.size(); s++){
			
			Int2D obLoc = grid.getObjectLocation(seen.get(s));
	
			locations.add(obLoc);
			//System.out.println(" at location:" + obLoc);
			//if(j.equals(Prey.class))
				//System.out.println("****" + seen.get(s));
			
		}
			
		this.behaviorProb(locations, seen, state);
		
		//Move every timestep
		super.move(grid, state);
		//System.out.println("Predator Moved");
	}// end of vision
	
	public void behaviorProb(Bag locs, Bag seen, SimState state){
	
		behavior = new BehaviorProcessor(grid);
		double[] newProb = behavior.updateProbPrey(locs, seen, defaultProb, this, state);
		
		actualProb = newProb;
	}
	public double getRepRate(){
		
		return actualRepRate;
	}
	
	public void setRepRate(double repRate){
		actualRepRate = repRate;
	}
	
}
