package sim.app.pvpEmo;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

public class ExpectationMap {
	
	protected double decayRate;
	protected double[][] foodLocationMap;
	protected double[][] predatorLocationMap;
	protected double[][] conspecificLocationMap;
	protected double[][] poisonLocationMap;
	protected int gWidth;
	protected int gHeight;
	protected int surpriseIncrease;
	
	public ExpectationMap(int gridWidth, int gridHeight, double decay){
		foodLocationMap = new double[gridWidth][gridHeight];
		predatorLocationMap = new double[gridWidth][gridHeight];
		conspecificLocationMap = new double[gridWidth][gridHeight];
		poisonLocationMap = new double[gridWidth][gridHeight];
		
		gWidth = gridWidth;
		gHeight = gridHeight;
		
		decayRate = decay;
	}

	protected void updateMapsPred (Bag objects, SparseGrid2D grid){
		
		surpriseIncrease = 0;
		this.decayMaps();
		
		for(int i = 0; i < objects.size(); i++){
			Object o = objects.get(i);
			if(o.getClass().equals(Prey.class)){
				Prey prey = (Prey)o;
				int x = grid.getObjectLocation(prey).x;
				int y = grid.getObjectLocation(prey).y;
				
				if(foodLocationMap[x][y] == 0)
					surpriseIncrease++;
				
				foodLocationMap[x][y] = 1.0;
				
				if(prey.isDiseased){
					if(poisonLocationMap[x][y] == 0)
						surpriseIncrease++;
					
					poisonLocationMap[x][y] = 1.0;
				}
					
			}
			
			else if(o.getClass().equals(Predator.class)){
				Predator pred = (Predator)o;
				int x = grid.getObjectLocation(pred).x;
				int y = grid.getObjectLocation(pred).y;
				
				if(conspecificLocationMap[x][y] == 0)
					surpriseIncrease++;
				
				conspecificLocationMap[x][y] = 1.0;
				
				if(pred.isDiseased){
					if(poisonLocationMap[x][y] == 0)
						surpriseIncrease++;
					poisonLocationMap[x][y] = 1.0;
				}
			}
			
		}
	}// end of update maps Predator
	
	protected void printMaps(){
		int nonZeroF = 0;
		int memoryValueF = 0;
		int nonZeroP = 0;
		int memoryValueP = 0;
		int nonZeroC = 0;
		int memoryValueC = 0;
		int nonZeroX = 0;
		int memoryValueX = 0;
		for(int w = 0; w < gWidth; w++){
			for(int h = 0; h <gHeight; h++){
				
				if(foodLocationMap[w][h] != 0){
					nonZeroF++;
					memoryValueF += foodLocationMap[w][h];
				}
				if(conspecificLocationMap[w][h] != 0){
					nonZeroC++;
					memoryValueC += conspecificLocationMap[w][h];
				}	
				if(predatorLocationMap[w][h] != 0){
					nonZeroP++;
					memoryValueP += predatorLocationMap[w][h];
				}
				if(poisonLocationMap[w][h] != 0){
					nonZeroX++;
					memoryValueX += poisonLocationMap[w][h];
				}
				
			}
		}// end of for loops
		
		double gArea = (double) gWidth * gHeight;
		double foodNonZero = nonZeroF/gArea;
		double foodMemory = memoryValueF/gArea;
		System.out.print(", " + foodNonZero + ", " + foodMemory);
		
		double conspecificNonZero = nonZeroC/gArea;
		double conspecificMemory = memoryValueC/gArea;
		System.out.print(", " + conspecificNonZero + ", " + conspecificMemory);
		
		double predatorNonZero = nonZeroP/gArea;
		double predatorMemory = memoryValueP/gArea;
		System.out.print(", " + predatorNonZero + ", " + predatorMemory);
		
		double poisonNonZero = nonZeroX/gArea;
		double poisonMemory = memoryValueX/gArea;
		System.out.print(", " + poisonNonZero + ", " + poisonMemory);
	}
	
	
	protected void updateMapsPrey (Bag objects, SparseGrid2D grid){
		this.decayMaps();
		
		for(int i = 0; i < objects.size(); i++){
			Object o = objects.get(i);
			if(o.getClass().equals(Food.class)){
				Food food = (Food)o;
				int x = grid.getObjectLocation(food).x;
				int y = grid.getObjectLocation(food).y;
				
				if(foodLocationMap[x][y] == 0)
					surpriseIncrease++;
				foodLocationMap[x][y] = 1.0;
				
				if(food.isDiseased()){
					if(poisonLocationMap[x][y] == 0)
						surpriseIncrease++;
					poisonLocationMap[x][y] = 1.0;
				}
					
			}
			
			else if(o.getClass().equals(Predator.class)){
				Predator pred = (Predator)o;
				int x = grid.getObjectLocation(pred).x;
				int y = grid.getObjectLocation(pred).y;
				
				if(predatorLocationMap[x][y] == 0)
					surpriseIncrease++;
				
				predatorLocationMap[x][y] = 1.0;
			}
			
			else if (o.getClass().equals(Prey.class)){
				Prey prey = (Prey)o;
				int x = grid.getObjectLocation(prey).x;
				int y = grid.getObjectLocation(prey).y;
				
				if(conspecificLocationMap[x][y] == 0)
					surpriseIncrease++;
				conspecificLocationMap[x][y] = 1.0;
			}
			
		}
	}// end of update maps Prey
	
	public void decayMaps(){
		for(int w = 0; w < gWidth; w++){
			for(int h = 0; h < gHeight; h++){
				
				foodLocationMap[w][h] = foodLocationMap[w][h] - decayRate;
				predatorLocationMap[w][h] = predatorLocationMap[w][h] - decayRate;
				conspecificLocationMap[w][h] = conspecificLocationMap[w][h] - decayRate;
				poisonLocationMap[w][h] = poisonLocationMap[w][h] - decayRate;
				
				if(foodLocationMap[w][h] < 0)
					foodLocationMap[w][h] = 0;
				if(predatorLocationMap[w][h] < 0)
					predatorLocationMap[w][h] = 0;
				if(conspecificLocationMap[w][h] < 0)
					conspecificLocationMap[w][h] = 0;
				if(poisonLocationMap[w][h] < 0)
					poisonLocationMap[w][h] = 0;
				
			}
		}
	}
}
