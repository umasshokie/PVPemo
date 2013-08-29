package sim.app.pvpEmo;

import javax.swing.JFrame;

import sim.app.*;
import sim.display.*;
import sim.engine.*;
import sim.portrayal.*;
import sim.portrayal.grid.*;
import sim.portrayal.simple.*;

import java.awt.*;



public class PVPEmoWithUI extends GUIState {

	public Display2D display;
	public JFrame displayFrame;
	public int worldWidth = 500;
	public int worldHeight = 500;
	
	SparseGridPortrayal2D worldPortrayal = new SparseGridPortrayal2D();
	
	public PVPEmoWithUI(){ super(new PVPEmo(System.currentTimeMillis())); }
	
	public PVPEmoWithUI(SimState state) {super(state);}
	
	// returns name
	public static String getName() { return "Predator vs. Prey: Agent-Based Simulation";}
	
	
	public static void main(String[] args)
	{
	
		// creates graphical console which allows to 
		// us to stop start, etc.

		//Parameters get assigned here
		//World size
		int gridWidth = Integer.parseInt(args[0]);
		int gridHeight = Integer.parseInt(args[1]);
		
		//Number of Prey and Predator
		int numPrey = Integer.parseInt(args[2]);
		int numPred = Integer.parseInt(args[3]);
		//Expectation Decay Rate
		double expectationMapDecay = Double.parseDouble(args[4]);
		
		PVPEmo.initializeUI(gridWidth, gridHeight, numPrey, numPred, expectationMapDecay);
		
		//Prey Only Parameters
		int preyMaxHunger = Integer.parseInt(args[5]);
		int preyOldAge = Integer.parseInt(args[6]);
		double preyDeathRate = Double.parseDouble(args[7]);
		int preyDeathRandNum = Integer.parseInt(args[8]);
		double preyAgingDeathMod = Double.parseDouble(args[9]);
		double preyHungerDeathMod = Double.parseDouble(args[10]);
		int preyLastMealLow = Integer.parseInt(args[11]);
		int preyLastMealMed = Integer.parseInt(args[12]);
		int preyLastMealHigh = Integer.parseInt(args[13]);
		int preyRepAge = Integer.parseInt(args[14]);
		double preyDefaultRepRate = Double.parseDouble(args[15]);
		int preyRepRandNum = Integer.parseInt(args[16]);
		
		
		// Predator Only Parameters
		int predMaxHunger = Integer.parseInt(args[17]);
		int predOldAge = Integer.parseInt(args[18]);
		double predDeathRate = Double.parseDouble(args[19]);
		int predDeathRandNum = Integer.parseInt(args[20]);
		double predAgingDeathMod = Double.parseDouble(args[21]);
		double predHungerDeathMod = Double.parseDouble(args[22]);
		int predLastMealLow = Integer.parseInt(args[23]);
		int predLastMealMed = Integer.parseInt(args[24]);
		int predLastMealHigh = Integer.parseInt(args[25]);
		int predRepAge = Integer.parseInt(args[26]);
		double predDefaultRepRate = Double.parseDouble(args[27]);
		int predRepRandNum = Integer.parseInt(args[28]);
		
		Prey.initializePrey(preyMaxHunger, preyOldAge, preyDeathRate, preyDeathRandNum, preyAgingDeathMod,
				preyHungerDeathMod, preyLastMealLow, preyLastMealMed, preyLastMealHigh, preyRepAge,
				preyDefaultRepRate, preyRepRandNum);
		
		Predator.initializePred(predMaxHunger, predOldAge, predDeathRate, predDeathRandNum, predAgingDeathMod,
				predHungerDeathMod, predLastMealLow, predLastMealMed, predLastMealHigh, predRepAge,
				predDefaultRepRate, predRepRandNum);
		PVPEmoWithUI vid = new PVPEmoWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}

	public void start(){
		super.start();
		setupPortrayals();
	}


	
	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals()
	{
		PVPEmo sims = (PVPEmo) state;
		
		//tell the portrayals what to portray and how to portray them
		worldPortrayal.setField(sims.world);
		OvalPortrayal2D oval = new OvalPortrayal2D(Color.green);
		RectanglePortrayal2D prey = new RectanglePortrayal2D(Color.white);
		RectanglePortrayal2D predator = new RectanglePortrayal2D(Color.black);
		worldPortrayal.setPortrayalForClass(Food.class, oval);
		worldPortrayal.setPortrayalForClass(Prey.class, prey);
		worldPortrayal.setPortrayalForClass(Predator.class, predator);
		
		//reschedule the displayer
		display.reset();
		
		//redraw the display
		display.repaint();
	
        
		}
	
	   public void init(Controller c)
       {
       super.init(c);
       
       display = new Display2D(worldWidth,worldHeight,this);
       display.setClipping(false);
       
       displayFrame = display.createFrame();
       displayFrame.setTitle("Predator vs. Prey");
       c.registerFrame(displayFrame);
       displayFrame.setVisible(true);
       display.setBackdrop(Color.gray);
       display.attach(worldPortrayal,"Grid");
   
       
       }
	   
	    public Object getSimulationInspectedObject()
        {
        return state;
        }

	    
	    public Inspector getInspector()
        {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
        }
    
    public void quit()
	{
		super.quit();
		
		if(displayFrame!=null) displayFrame.dispose();
		displayFrame=null;
		display = null;
	}
}
