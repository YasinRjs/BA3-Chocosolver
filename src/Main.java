import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
    	Scanner in = new Scanner(System.in);

    	int choice = 0;
    	while (choice != 1 && choice != 2 && choice !=3) {
    		System.out.println("1) Problème d'échiquier");
    		System.out.println("2) Problème du cavalier");
    		System.out.println("3) Problème du musée");
    		
    		System.out.print("Veuillez choisir votre problème (1, 2 ou 3): ");
    		choice = in.nextInt();
    	}
    	in.nextLine();
    	
    	if (choice == 1) {
        	ProblemeEchiquier echiquier = new ProblemeEchiquier();
    	}
    	
    	else if (choice == 2) {
        	System.out.print("Veuillez choisir la taille du plateau : ");
        	int size = in.nextInt();
        	System.out.print("Veuillez choisir un temps limite (en secondes) pour chaque itération d'optimisation : ");
        	String limitTime = in.nextInt() +"s";
        	ProblemeCavalier cavalier = new ProblemeCavalier(size,limitTime);
    	}

    	else if (choice == 3) {
    		System.out.print("Veuillez entrer le fichier à parser : ");
    		String file = in.nextLine();       	
        	Musee musee = new Musee(file);
    		
    	}
    }
}