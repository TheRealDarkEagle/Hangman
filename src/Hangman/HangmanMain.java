package Hangman;

import java.io.IOException;
import java.util.Scanner;

public class HangmanMain {
	
	private static Scanner auswahl;

	public static void main (String[] args) throws IOException {
		// true wenn ein Mensch spielt, false wenn der Computer
		System.out.println("#################################################################");
		System.out.println();
		System.out.println();
		System.out.println("           Willkommen zu Hangman! Viel Spaß beim Spielen :)");
		System.out.println();
		System.out.println("Wer soll Spielen? Drücken Sie M fuer Mensch und C für Computer!");
		auswahl = new Scanner(System.in);
		String auswahlDesSpielers = auswahl.nextLine().toLowerCase();
		if(auswahlDesSpielers.length() != 1) {
			throw new IllegalArgumentException("Bitte geben Sie nur einen Buchstaben ein!");
		}
		if(auswahlDesSpielers.equals("m")) {
			new Hangman(true).startGame();	
		}else if(auswahlDesSpielers.equals("c")) {
			new Hangman(false).startGame();
		}
	}
}
