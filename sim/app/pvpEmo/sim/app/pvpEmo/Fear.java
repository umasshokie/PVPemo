package sim.app.pvpEmo;

import sim.app.pvp.Predator;

public class Fear extends Emotion {

	Fear(int negative, Animal p){
		super();
		type = negative;
		if(p.getClass().equals(Predator.class))
			amount = p.lastSeenPrey/p.maxHunger ;
			if(amount >1.0)
				amount = 1.0;
		else if(p.lastSeenPredator == 0)
			amount = 1.0;
		else 
			amount = 1.0 -((double)p.lastSeenPredator/p.maxSeenPredator);
			
			
		if(amount > 1.0)
			amount = 1.0;
		if(amount < 0)
			amount = 0.0;
		//System.out.println("Fear: " + amount);
	}
}
