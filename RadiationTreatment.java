import java.util.ArrayList;


public class RadiationTreatment {

	//variables used throughout
	
	//the tumor is made up of two types of cells, oxygenated cells and hypoxic cells
	//oxygenated cells mulitply at a rate of alpha while hypoxic cells do not multiply
	//when dead oxygenated cells try to reproduce, they disintegrate and 
	
	
	//hit or extrapolation number for oxygenated cells
	public static Double no;
	
	//characteristic dose for oxygenated cells
	public static Double Do;
	
	//hit or extrapolation for hypoxic cells
	public static Double na;
	
	//characteristic dose for hypoxic cells;
	public static Double Da;
	
	//the probability of a cell surviving given a dose D is S = 1-(1-e^(D/Do))^n
	//this is derived from the multi-target single-hit model
	
	//initial cell count of the tumor
	public static Double initcellcount;
	//rate at which oxgentated cells 
	public static Double alpha;
	public static Double beta;
	public static Double gamma;
	public static ArrayList<Double> livingoxygenated;
	public static ArrayList<Double> deadoxygenated;
	public static ArrayList<Double> livinghypoxic;
	public static ArrayList<Double> deadhypoxic;
	public static int number;
	public static int D;
	public static void main(String[] args) { 
		for (int i = 0; i < 50; i++){
			afterRadiation();
			number++;
			afterReproduction();
			number++;
			afterOxygenation();
			number++;
		}

	}
	
	public static void afterRadiation(){
		//performs radiation calculations for oxygentated cells cells
		livingoxygenated.add(livingoxygenated.get(number)*(1-Math.pow(1-Math.pow(Math.E, D/Do), no)));
		deadoxygenated.add(livingoxygenated.get(number) + deadoxygenated.get(number) - livingoxygenated.get(number+1));
		
		//performs radiation calculations for hypoxic cells 
		livinghypoxic.add(livinghypoxic.get(number)*(1-Math.pow(1-Math.pow(Math.E, D/Da), na)));
		deadhypoxic.add(livinghypoxic.get(number) + deadhypoxic.get(number) - livinghypoxic.get(number+1));
		
	}
	
	public static void afterReproduction(){
		double time = 1;
		//oxygentetd cells reproduce with rate alpha and then die after reproducing making those cells
		double detlat = gamma*D;
		double truetime = time - deltat;
		
		livingoxygenated.add(livingoxygenated.get(number)*Math.pow(Math.E, alpha*truetime));
		deadoxygenated.add(deadoxygenated.get(number)*Math.pow(Math.E, -alpha*truetime));

		
		//hypoxic cells do not reproduce
		livinghypoxic.add(livinghypoxic.get(number));
		deadhypoxic.add(deadhypoxic.get(number));

	}
	
	public static void afterOxygenation(){
		
	}

}
