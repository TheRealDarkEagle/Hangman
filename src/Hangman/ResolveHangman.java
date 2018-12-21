package Hangman;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Character.Subset;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.css.Counter;

public class ResolveHangman {
	
	/**
	 * @TODO: 
	 * Überprüfen ob die Variablen die Global sind auch global sein müssen 
	 * 
	 * Variablen NICHT Static machen! 
	 * 
	 * Kommentare einfügen, wenn mehrere Operation in einer Methode ausgeführt werden, und erklären was gerade passiert
	 * 
	 * beziehungen zwischen den buchstaben einfügen
	 * teste zweiten bis vorletzten buchstabe
	 * schaue davor und danach welche buchstaben dastehen
	 * speichere mögliche variation in datei 
	 * wenn kein wort mehr in liste ist 
	 * gehe auf diese datei zurück 
	 * und schaue nach welche buchstaben man als besten  für die leeren felder einsetzen könnte
	 * 
	 * z.b. Apf_ltasch_ wie oft kam bisher ein buchstabe nach einem f 
	 * wie oft kam bisher ein buchstabe nach einem h 
	 * wie oft kam bisher ein buchstabe vor w 
	 * anhand dessen buchstaben nehmen falls er noch nicht benutzt wurde  
	 *
	 * Genauigkeit erhöhen welche Listenelemente nicht entfernt  bzw entfernt werden
	 * Wenn der buchstabe öfter als 1x vorkommt - teste die restlichen stellen auch -> führt zu einer besseren Genauigkeit der Wörterauswahl 
	 */
	
	private ArrayList<Character> usedChars = new ArrayList<Character>();
	private ArrayList<String> wordList;
	private Map<Character, Integer> map;
	
	/**
	 * Konstruktor
	 * @throws IOException
	 */
	public ResolveHangman() throws IOException {
		this.wordList = this.loadWordsFromFile();
		this.map = new HashMap<Character,Integer>();
	}
	
	/**
	 * Findet den naechsten Buchstaben anhand aller Bekannten Wörter 
	 * @param theWord
	 * @return
	 */
	public char getChar(char[] theWord) {
		Character bestChar = null;
		//Wenn usedChars == empty -> remove alle Einträge mit anderer Länge aus wordList
		if(usedChars.isEmpty()){
			sortFromLength(theWord.length,wordList);
			bestChar =  getBestChar(theWord);
		}else{
			testTheChar(theWord);
			if(!wordList.isEmpty()) {
				bestChar =  getBestChar(theWord);
			}else{
				bestChar = getCharByPossibility(theWord);
			}
		}
		usedChars.add(bestChar);
		return bestChar;
	}
	
	

	/**
	 * Trennt die Listenwörter voneinander und speichert sie in liste ab
	 * @param sb
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private ArrayList<String> splitWordsFromList(StringBuilder sb) throws FileNotFoundException, IOException {
		ArrayList<String> alSpli = new ArrayList<String>();
		int start = 0;
		int ende = 0;
		for(int i=0;i<sb.length();i++) {
			int zahl = sb.charAt(i);
				//13 = Zeilenumbruch
				if(zahl==13) {
				ende = i;
				String temp = sb.substring(start, ende);
				alSpli.add(temp);
			}
			//10 = Zeilenvorschub
			if(zahl==10) {
				start = i+1;
			}
			if(i==sb.length()-1) {
				//Wenn i am letzten buchstaben angekommen ist
				//ist dies mein letztes wort von anfang bis i+1
				ende = i+1;
				String temp = sb.substring(start, ende);
				alSpli.add(temp);
			}
		}
		return alSpli;
	}

	/**
	 * Läd alle Wörter aus Datei und gibt die aneinanderhängende Sammlunng an splitWordsFromList() weiter
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> loadWordsFromFile() {
		try(InputStream is = new FileInputStream("D:\\Danz Kai Adrian empiriecom\\Hangman\\wortsammlung.txt")){
			byte[] buffer = new byte[1024];
			StringBuilder sb = new StringBuilder();
			
			int bytesRead = is.read(buffer);
			while(bytesRead > 0) {
				sb.append(new String(Arrays.copyOfRange(buffer, 0, bytesRead), "UTF-8"));
				bytesRead = is.read(buffer);
			}
		return (splitWordsFromList(sb));
		} catch (FileNotFoundException e) {
			System.out.println("File wurde nicht gefunden!");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("Es ist ein Fehler aufgetreten...");
			e.printStackTrace();
			return null;
		}
	}
	
	
	

	/**
	 * Prüfe ob zuletzt benutzter Buchstabe im Wort vorhanden war
	 * @param theWord
	 */
	private void testTheChar(char[] theWord) {
		Character lastUsed = usedChars.get(usedChars.size()-1);
		StringBuilder arrayIntoString = new StringBuilder();
		//Wenn letzter benutzter Buchstabe gleich  index 0 von theWord in groß ist groß ist 
		if(theWord[0]==Character.toUpperCase(lastUsed)) {
			Character checkFirstChar = theWord[0];
			lastUsed = checkFirstChar;
			// never 
			charExist(lastUsed=checkFirstChar, theWord);
		}else {
			for(int a=0; a<theWord.length;a++) {
				arrayIntoString = arrayIntoString.append(theWord[a]);
			}
			String hasTheWord = ""+arrayIntoString;
			hasTheWord.toLowerCase();
			if(hasTheWord.contains(lastUsed.toString())){
				charExist(lastUsed, theWord);
			}else {
				charDoesNotExist(lastUsed, theWord);
			}
		}
	}

	/**
	 * Entfernt alle Wörter, welche den zuletzt benutzten Buchstaben beinhalten
	 * @param charExist
	 * @param theWord
	 */
	private void charDoesNotExist(Character charExist,char[] theWord) {
		for(int a =0; a<wordList.size();a++) {
			Character lastUsed = Character.toUpperCase(charExist);
			if(wordList.get(a).contains(lastUsed.toString().toLowerCase()) || 
			   wordList.get(a).contains(lastUsed.toString())) {
					wordList.remove(a);
					a--;
			}
		}
	}

	
	/**
	 * Sucht aus der Liste alle Wörter mit der Länge des gesuchten Wortes heraus und löscht die restlichen 
	 * @param lengthFromWord
	 * @param list
	 */
	private ArrayList<String> sortFromLength(int lengthFromWord, ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			int lengthWordInListe = list.get(i).length();
			if(lengthWordInListe!=lengthFromWord) {
				list.remove(i);
				i--;
			}
		}
		toSmall();
		list.trimToSize();
		return list;
	}
	
	private void toSmall() {
		ArrayList<String> copyOfWordList = new ArrayList<String>();
		for(String s:wordList) {
			copyOfWordList.add(s);
			
		}
		wordList = copyOfWordList;
	}

	/**
	 * Löscht alle Einträge der Liste, indenen der Buchstabe nicht an der selben Stelle vorhanden ist (ausgehend von dem ersten erscheinen des Buchstaben in theWord)
	 * @param charExist
	 * @TODO: Prüfe ob buchstabe vor stelle theWord vorkommt wenn ja remove aus liste  
	 */
	//Prüfe solange bis am letzten buchstaben angekommen 
	private void charExist(Character charExist,char[] theWord) {
		//Finde stelle des Buchstaben in theWord heraus
		for(int i =0;i<theWord.length;i++) {
			Character theWordCharAtIndex = theWord[i];
			if(theWordCharAtIndex==charExist) {
				//teste ob eingesetzte stelle des buchstaben gleich der stelle im wort von wordList ist 
				for(int a=0;a<wordList.size();a++) {
					if(wordList.get(a).charAt(i)!=charExist) {
						wordList.remove(a);
						a--;
					}
				}
			}
		}
		wordList.trimToSize();
	}
		
	//sucht höchsten wert aus map 
	private int getMaxValue() {
		int max=-1;
		for(Map.Entry<Character, Integer> m : map.entrySet()) {
			if(max<m.getValue()) {
				max = m.getValue();
			}
		}
		return max;
	}

	//zählt jeden Buchstaben in Wort-Liste
	private void countAllChars(ArrayList<String> list) {
		map.clear();
		for(String word:list) {
			for(int a=0;a<word.length();a++) {
				word = word.toLowerCase();
				char thisChar = word.charAt(a);
				if(map.containsKey(thisChar)) {
					map.put(thisChar, map.get(thisChar)+1);
				}else {
					map.put(word.charAt(a), 1);
				}
			}
		}
	}
	
	
	private char getBestChar(char[] theWord) {
		countAllChars(wordList);
		char bestChar  = getBestCharFromList();
		return bestChar;
	}
	
	/**
	 * @TODO
	 * FEHLER!!!
	 * 
	 * wenn true funkttioniert noch nicht richtig -> oder die positionsabfrage klappt noch nicht genau ... wer weiß das denn schon so genau >.< AAASDHALSKDHALKSDHALSD
	 * @param list
	 * @param indexOfThisChar
	 * @param searchAfterChar
	 * @param theWord
	 */
	//durchsucht wort nach vorhandenem buchstaben -> zählt den buchstaben davor/danach 
	private void countNextChar(ArrayList<String> list,int indexOfThisChar,boolean searchAfterChar,char[] theWord) {
		map.clear();
		//möchte den eigentlichen Buchstaben NACH DEM buchstaben finden 
		if(searchAfterChar) {
			String thisChar = ""+theWord[indexOfThisChar-1];
			for(String w: list) {
				//Wenn substring 0-länge-1 den buchstaben enthält
				if(w.substring(0, w.length()-2).contains(thisChar.toLowerCase())) {
					//gehe durch wort durch 
					for(int i = w.length()-2;i>-1;i--) {
						//wenn buchstabe gleich zu suchenenden buchstabe ist
						if(w.charAt(i)==thisChar.charAt(0)) {
							//nehme buchstabe aus wort, welcher davor steht 
							char nextChar = w.charAt(i+1);
							//speichere buchstabe in map ab 
							if(map.containsKey(nextChar)) {
								map.put(nextChar, map.get(nextChar)+1);
							}else {
								map.put(nextChar, 1);
							}
						}
					}
				}
			}
			//möchte eigentlichen Buchstaben VOR DEM buchstaben finden 
		}else {
			for(String w:list) {
				//nehme zu vergleichenden buchstaben aus wort 
				char thisChar = theWord[indexOfThisChar+1];
				//wenn substring von 1 - ende von w den buchstaben besitzt 
				if(w.substring(1, w.length()).contains(""+thisChar)) {
					//gehe wort durch 
					for(int i = 1;i<w.length()-1;i++) {
						//wenn buchstabe aus wort gleich dem gesuchten buchstaben ist  
						if(w.charAt(i)==thisChar) {
							//nehme buchstabe vor dem eigentlichen buchstaben 
							char beforeChar = w.charAt(i-1);
							//mache den buchstaben klein 
							String toMakeItLowerCase = ""+beforeChar;
							
							beforeChar = toMakeItLowerCase.toLowerCase().charAt(0);
							//hochzählung des buchstaben 
							if(map.containsKey(beforeChar)) {
								map.put(beforeChar, map.get(beforeChar)+1);
							}else {
								map.put(beforeChar, 1);
							}
						}
					}
				}
			}
		}
	}
	
	//geht durch die bisher gefundenen Buchstaben und gibt den zuletzt gefundenen Buchstaben zurück 
	private int getCharToSearch(char[] theWord) {
		int counter = 0;
		//Der buchstabe vor dem eigentlichen buchstabe muss ein '.' sein !!!
		//solange an stelle counter von theWord ein punkt ist UND links ODER rechts daneben kein punkt ist 
		for(int i = theWord.length-2;i>=1;i--) {
			if(theWord[i]=='.'&&(theWord[i-1]!='.'||theWord[i+1]!='.')&&!usedChars.contains(theWord[i])){
				counter = i; 
			}
		}
		return counter;
	}
	
	//Prüft ob nach dem gefunden buchstaben ein Buchstabe ist
	private boolean isCharAfterChar(char[] theWord, int indexChar) {
		//überarbeiten 
		/**
		 * Hier die prüfung? 
		 * 
		 * was habe ich an diesem punkt? die stelle wo ein . ist 
		 * was möchte ich nun wissen? ob ich den buchstaben vor diesem suche oder den buchstaben danach
		 * 
		 * hier gebe ich an ob ich vor dem buchstaben oder danach suchen soll 
		 * die eigentliche prüfung sollte sein an welcher stelle mein buchstabe noch da ist und den buchstaben der davor steht suche ich 
		 * oder den buchstaben der danach kommt
		 * also brauche ich 2 verscheidene methoden , welche mir dementsprechend die buchstaben herausfinden
		 * momentan bekomme ich hier "nur" zurück ob ich vor oder nach dem eigentlichen buchstaben suchen soll 
		 */
//		String allChars = "abcdefghijklmnopqrstuvwxyzäöüß";
		//solange stelle x . UND davor kein punkt
		if(indexChar==0) {
			return false;
		}else if(indexChar==theWord.length-1) {
			return true;
		}else {
			if(theWord[indexChar-1]!='.') {
				return true;
			}else {
				return false;
			}
		}
		
//		if(indexChar+1!=theWord.length-1) {
//			if(allChars.contains(""+theWord[indexChar])) {
//				return true;
//			}
//		}
//		return false;
	}
	
	//Gibt die vorkommenenden Buchstaben in BuchstabenListe nach vorkommen 
	private char getBestCharFromList() {
		ArrayList<Character> sortedChars = new ArrayList<Character>();
		int max = getMaxValue();
		do {
			for(Map.Entry<Character, Integer> entry : map.entrySet()) {
				if(entry.getValue()==max) {
					if(!usedChars.contains(entry.getKey()))
					sortedChars.add(entry.getKey());
				}
			}
		max--;
		}while(max>0);
		for(int i = 0;i<sortedChars.size();i++) {
			if(!usedChars.contains(sortedChars.get(i))){
				return sortedChars.get(i);
			}
		}
		return 'a';
	}
	
//---------------------------------------------------------------- HIERAN WIRD NOCH GEARBEITET!!! ---------------------------------------------------------------------
	
	
	

	/**
	 * 
	 * @TODO: Überarbeitungswürdig... -> eher methoden komplett neu schreiben! 
	 * 
	 * einstieg wenn keine wörter mehr vorhanden 
	 * durchsuche nach zuletzt passendem buchstabe 
	 * durchsuche wortliste nach diesem buchstaben 
	 * zähle vorkommen der buchstaben davor/danach welche noch nicht probiert wurden 
	 * probiere besten buchstaben 
	 * 
	 * Wenn Wortliste leer - finde nächsten besten Buchstaben heraus anhand von Buchstabenzusammenhang 
	 * @throws IOException 
	 * 
	 */
	private char getCharByPossibility(char[] theWord) {
		//allWorts hält eine Liste mit allen Wörtern aus Textdatei
		ArrayList<String> allWords = loadWordsFromFile();
		//Sortiert alle einträge aus der Liste, welche nicht die entpsrechende Länge des Wortes haben 
		ArrayList<String> newWordList = sortFromLength(theWord.length, allWords);
		int searchFromChar = getCharToSearch(theWord);
		//prüfe ob vor oder nach dem buchstaben gesucht werden soll 
		//überarbeite die prüfung welcher buchstabe getestet werden soll 
		if(isCharAfterChar(theWord, searchFromChar)) {
			countNextChar(newWordList, searchFromChar, true, theWord);
		}else {
			countNextChar(newWordList, searchFromChar, false, theWord);
		}
		char bestChar = getBestCharFromList();
		return bestChar;
	}
}