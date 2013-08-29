package sim.app.pvpEmo;

public class Happiness extends Emotion{
	Happiness(int positive, Animal p){
		super();
		type = positive;
		//Lack of hunger, reproduction, and social
		double hungerAmount = 1.0 - ((double)p.lastMeal/ p.maxHunger);
		double reproductionAmount = 1.0 - ((double)p.lastRep/p.maxRep);
		double socialAmount = 1.0 - ((double)p.lastSocial/p.maxSocial);
	
		amount = hungerAmount + reproductionAmount + socialAmount;
		
		if(amount > 1.0)
			amount = 1.0;
		if(amount < 0)
			amount = 0.0;
		//System.out.println("Happiness: " + amount);
			
	}
}
