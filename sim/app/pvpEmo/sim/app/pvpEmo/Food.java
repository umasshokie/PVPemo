
package sim.app.pvpEmo;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Food implements Steppable {

	protected double amount = 1.0;
	private double diseasePr = .005;
	private int diseaseRandNum = 10000;
	private double repPr = .0005;
	private int repRandNum = 10000;
	private boolean diseased = false;
	private double regrowthRate = .025;
	public SparseGrid2D grid;
	protected Stoppable stop;

	
	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		PVPEmo pvp = (PVPEmo)state;
		
		grid = pvp.world;
		
		//Slowly grows back
		amount += regrowthRate;
		
		//Chance of disease
		this.diseaseChance(pvp);
		
		//Chance of spread
		this.reproductionChance(pvp);
	}

	public void diseaseChance(PVPEmo pvp){
		
		double d = pvp.random.nextInt(diseaseRandNum);
		double disease = d/diseaseRandNum;
		
		assert (disease >= 0);
		
		if(disease < diseasePr)
			diseased = true;
		
	}
	
	public void reproductionChance(PVPEmo pvp){
		
		// reproduction rate
		double rep = pvp.random.nextInt(repRandNum);
		//System.out.println("rep: " + rep);
		double repro = rep/repRandNum;
		//System.out.println("Repro: " + repro);
		assert (repro >= 0);
		
		if(repro < repPr)
			this.spread(grid, pvp);
	}
	
	public boolean isDiseased(){
		return diseased;
	}

	public void spread(SparseGrid2D grid, SimState state){
		
	
		Int2D cord = grid.getObjectLocation(this);
		
		if(cord != null){
		
			Food p = new Food();
		
			int direction = state.random.nextInt(7);
	
			
			if (direction == 0){
				grid.setObjectLocation(p, cord.x, cord.y + 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 1){
				grid.setObjectLocation(p, cord.x, cord.y - 1);	
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 2){
				grid.setObjectLocation(p, cord.x + 1, cord.y);	
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 3){
				grid.setObjectLocation(p, cord.x + 1, cord.y + 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 4){
				grid.setObjectLocation(p, cord.x + 1, cord.y - 1);	
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 5){
				grid.setObjectLocation(p, cord.x - 1, cord.y + 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
			else if (direction == 6){
				grid.setObjectLocation(p, cord.x - 1, cord.y - 1);
				Stoppable stop = state.schedule.scheduleRepeating(p);
				p.makeStoppable(stop);
			}
		}// end of if
	}// end of spread
	
	public void makeStoppable(Stoppable stopper){
		stop = stopper;
	}
	
	public void eat(){
		amount = amount - .7;
		if(amount <0){
			//amount = 0.0;
			//may be a point where it is being removed, but not stopped.
			this.stop.stop();
			grid.remove(this);
			
		}
	}
}// end of class
