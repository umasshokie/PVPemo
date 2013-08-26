package sim.app.pvpEmo;

public class Anger extends Emotion {
	
	Anger(int negative, Animal p){
		super();
		type = negative;
		amount = (double)p.lastMeal/p.maxHunger;
		if(amount > 1.0)
			amount = 1.0;
		//System.out.print(", Anger: " + amount);
		//Calculates amount by lastMeal/ maxHunger
		//amount = lastMeal/maxHunger;
	}
}
