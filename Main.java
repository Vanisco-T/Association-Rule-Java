import java.util.Scanner;
import java.io.*;

public class Main {

	public static void main(String[] args) {

		float confiance;
		boolean sauter = false;
		String dataset;
		int minsup = 3, minconf = 3;
		
		System.out.println("Enter the filename of the dataset");
		System.out.print(": ");
		dataset = obtainFilename();
		minsup = obtainint("% min-support value (that's as a percentage, Range = [20, 100])");
		while(minsup > 100 && minsup < 1) {
			System.out.println("Please enter a percentage value within the given range [20, 100]");
			minsup = obtainint("% min-support");
		}
		minconf = obtainint("% min-confidence value (that's as a percentage, Range = [50, 100])");
		while(minconf > 100 || minconf < 1) {
			System.out.println("Please enter a percentage value within the given range [50, 100]");
			minconf = obtainint("% min-confidence");
		}
		confiance = ((int)minconf/100);
		Arules objet = new Arules(dataset, minsup, confiance);
		sauter = objet.Apriori();
		if(sauter == true) {
			objet.RegleAssociation();
			System.out.println("\nThanks for waiting");
		}
	}
	public static String obtainFilename() {
		
		boolean verifi = true;
		String chaine = null;
		while(verifi) {

			try {
				Scanner scanit = new Scanner(System.in);
				chaine = scanit.nextLine();
				verifi = false;
			}catch(Exception e) {
				System.out.println("I/O error, please try again");
			}
		}
		return chaine;
	}
	public static int obtainint(String which) {
		
		int val = 0; 
		boolean bool = true;
		while(bool) {
			try {
				System.out.println("Enter the " + which);
				System.out.print(": ");
				Scanner scanit = new Scanner(System.in);
				val = scanit.nextInt();
				bool = false;
			}catch(Exception e) {
				System.out.println("Error : integer only, please try again");
			}
		}
		return val;
	}
}
