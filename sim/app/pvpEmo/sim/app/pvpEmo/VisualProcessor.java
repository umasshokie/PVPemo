package sim.app.pvpEmo;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class VisualProcessor {
	
	SparseGrid2D world;
	VisualProcessor(SimState state){
		
		PVPEmo pvp = (PVPEmo) state;
		world = pvp.world;
	
	}

	//Takes in the location of an animal and the state of the world
	//returns a bag of items at the locations it can see.
	public Bag sight(int x, int y, SimState state, int direction){
		
		PVPEmo pvp = (PVPEmo) state;
		world = pvp.world;
		
		
		Bag seen = new Bag();
		
		
		
		Bag locs = new Bag();
		//North
		if (direction == 0){
			//Peripheral vision locations
			Int2D tempy = new Int2D(world.tx(x-1),world.ty(y));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1),world.ty(y));
			locs.add(tempy);
			
			//Three Forward Squares Vision Locations
			tempy = new Int2D(world.tx(x-1), world.ty(y-1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x), world.ty(y-1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1), world.ty(y-1));
			locs.add(tempy);
			
			//Five Further Vision Locations
			tempy = new Int2D(world.tx(x-2), world.ty(y-2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x-1), world.ty(y-2));
			locs.add(tempy);
			tempy =  new Int2D(world.tx(x), world.ty(y-2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1),world.ty(y-2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+2),world.ty(y-2));
			locs.add(tempy);
		}
		//South
		else if(direction == 1){
			//Peripheral vision locations
			Int2D tempy = new Int2D(world.tx(x-1),world.ty(y));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1),world.ty(y));
			locs.add(tempy);
			
			//Three Forward Squares Vision Locations
			tempy = new Int2D(world.tx(x-1), world.ty(y+1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x), world.ty(y+1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1), world.ty(y+1));
			locs.add(tempy);
			
			//Five Further Vision Locations
			tempy = new Int2D(world.tx(x-2), world.ty(y+2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x-1), world.ty(y+2));
			locs.add(tempy);
			tempy =  new Int2D(world.tx(x), world.ty(y+2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1),world.ty(y+2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+2),world.ty(y+2));
			locs.add(tempy);
		}
		
		//East
		else if (direction == 2){
			//Peripheral vision locations
			Int2D tempy = new Int2D(world.tx(x),world.ty(y+1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x),world.ty(y-1));
			locs.add(tempy);
			
			//Three Forward Squares Vision Locations
			tempy = new Int2D(world.tx(x+1), world.ty(y+1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1), world.ty(y));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+1), world.ty(y-1));
			locs.add(tempy);
			
			//Five Further Vision Locations
			tempy = new Int2D(world.tx(x+2), world.ty(y+2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+2), world.ty(y+1));
			locs.add(tempy);
			tempy =  new Int2D(world.tx(x+2), world.ty(y));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+2),world.ty(y-1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x+2),world.ty(y-2));
			locs.add(tempy);
		}
		//West
		else if (direction == 3){
			//Peripheral vision locations
			Int2D tempy = new Int2D(world.tx(x),world.ty(y+1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x),world.ty(y-1));
			locs.add(tempy);
			
			//Three Forward Squares Vision Locations
			tempy = new Int2D(world.tx(x-1), world.ty(y+1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x-1), world.ty(y));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x-1), world.ty(y-1));
			locs.add(tempy);
			
			//Five Further Vision Locations
			tempy = new Int2D(world.tx(x-2), world.ty(y+2));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x-2), world.ty(y+1));
			locs.add(tempy);
			tempy =  new Int2D(world.tx(x-2), world.ty(y));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x-2),world.ty(y-1));
			locs.add(tempy);
			tempy = new Int2D(world.tx(x-2),world.ty(y-2));
			locs.add(tempy);
		}
	
		seen = world.getObjectsAtLocations(locs, seen);
		
		return seen;
	
		
	}
}
