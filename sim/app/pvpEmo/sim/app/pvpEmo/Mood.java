package sim.app.pvpEmo;

public class Mood {

	double amount;
	Anger anger;
	Sadness sadness;
	Disgust dis;
	Fear fear;
	Happiness happy;
	
	Mood(Anger a, Sadness sad, Disgust d, Fear f, Happiness h){
		
		anger = a;
		sadness = sad;
		dis = d;
		fear = f;
		happy = h;
		
		double negativeAmount = (anger.amount + sadness.amount + dis.amount + fear.amount)/ 4;
		double comboAmount = (negativeAmount + happy.amount)/2;
		
		
		amount = (amount * .75) + (comboAmount * .25);
		
	}
}
