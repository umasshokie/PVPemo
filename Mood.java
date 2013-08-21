
public class Mood 
{
	double amount;
	Emotion anger;
	Emotion sadness;
	Emotion disgust;
	Emotion fear;
	Emotion happiness;
	Emotion surprise;
	
	protected Mood(Emotion a, Emotion sad, Emotion d, Emotion f, Emotion h, Emotion s){
		
		assert(a.type == -1 && sad.type == -1 && d.type == -1 && f.type == -1);
		assert(h.type == 1);
		assert(s.type == 0);
		
		anger = a;
		sadness = sad;
		disgust = d;
		fear = f;
		happiness = h;
		surprise = s;
		
	}
	
	protected double calculateAmount(){
		
		double negativesTotal = anger.amount + sadness.amount + disgust.amount + fear.amount;
		
		return amount;
	}
}
