import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
    	
    	
    	//ProblemeEchiquier myProbleme = new ProblemeEchiquier(args);
    	//myProbleme.start();
    	
    	//System.out.println();
    	
/*    	System.out.print("Veuillez choisir la taille de la matrice : ");
    	Scanner in = new Scanner(System.in);
    	int size = in.nextInt();
    	System.out.print("Veuillez choisir un temps limite (en secondes) pour chaque itération d'optimisation : ");
    	in = new Scanner(System.in);
    	String limitTime = in.nextInt() +"s";
    	ProblemeCavalier thisProbleme = new ProblemeCavalier(size,limitTime);
    	thisProbleme.start();
*/
    	Musee test = new Musee();
    	test.solveMusee();
    }
}