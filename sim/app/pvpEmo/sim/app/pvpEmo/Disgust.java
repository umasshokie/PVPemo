package sim.app.pvpEmo;

public class Disgust extends Emotion{

	Disgust(int negative, Animal p){
		super();
		type = negative;
		if(p.isDiseased)
			amount = 1.0;
		else
			amount = 0.0;
		
		/*if(amount != 0.0)
			System.out.println("***************************************************");*/
		//System.out.println("Disgust: " + amount);
	}
}
