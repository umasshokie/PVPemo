package sim.app.pvpEmo;


public class Surprise extends Emotion{
	
	protected double surpriseRate = .1;
	Surprise(int neutral, Animal p){
		super();
		type = neutral;
		
		amount = amount + (surpriseRate * p.map.surpriseIncrease);
	}
}
