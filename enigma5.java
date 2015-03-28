import java.io.*;
import java.lang.Math;

/*
TODO:
-AUFRÄUMEN
-write more errorchecks (rotor + position input via regex!)
-rotor output encryption!
-capital letter support?
-fileinput
-gui
*/

public class enigma4 {
 
    public static void main(String[] args) throws FileNotFoundException, IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("first what do you want to do? type 1 to encrypt a text with standard rotors, 2 to generate and save a random rotor, 3 to encrypt with an external rotor or 4 to verify an external rotor.");
		
		String choice = null;
		int choiceint = -1;
		try {
			choice = br.readLine();
			choiceint = Integer.parseInt(choice);
		} catch (IOException ioe) {
			System.out.println("IO error trying to read it!");
			System.exit(1);
		}
		
		if(choiceint > 4 || choiceint < 1) {
			System.out.println("ERROR; choose one of the available options!");
			System.exit(1);
		}
		
		int firstrotorIn[] = null;
		int secondrotorIn[] = null;
		int thirdrotorIn[] = null;
		
		if(choiceint == 2) {
			int randRotor[] = encrypter.randomRotor();
			if(encrypter.rotorConfirmer(randRotor)) {
				System.out.println("Done!");
				System.exit(1);
			}
			else {
				System.out.println("something went wrong :(");
				System.exit(1);
			}
		}
		else if(choiceint == 3) {
			System.out.println("how many external rotors do you want to use?");
			
			String amount = null;
			int amountint = -1;
			try {
				amount = br.readLine();
				amountint = Integer.parseInt(amount);
			} catch (IOException ioe) {
				System.out.println("IO error trying to read it!");
				System.exit(1);
			}
			
			String rotorPath = null;
			
			switch (amountint) {
				case 3:
					firstrotorIn = new int[26];
					System.out.println("what's the filename or path of this rotor?");
					try {
						rotorPath = br.readLine();
					} catch (IOException ioe) {
						System.out.println("IO error trying to read it!");
						System.exit(1);
					}
					BufferedReader firstrotorbr = new BufferedReader(new InputStreamReader(new FileInputStream(rotorPath)));
					try {
						String line;
						int lineint = 0, increment = 0;
						while ((line = firstrotorbr.readLine()) != null) {
							lineint = Integer.parseInt(line);
							firstrotorIn[increment] = lineint;
							increment++;
						}
					} finally {
						firstrotorbr.close();
					}
				case 2:
					secondrotorIn = new int[26];
					System.out.println("what's the filename or path of this rotor?");
					try {
						rotorPath = br.readLine();
					} catch (IOException ioe) {
						System.out.println("IO error trying to read it!");
						System.exit(1);
					}
					BufferedReader secondrotorbr = new BufferedReader(new InputStreamReader(new FileInputStream(rotorPath)));
					try {
						String line;
						int lineint = 0, increment = 0;
						while ((line = secondrotorbr.readLine()) != null) {
							lineint = Integer.parseInt(line);
							secondrotorIn[increment] = lineint;
							increment++;
						}
					} finally {
						secondrotorbr.close();
					}
				case 1:
				thirdrotorIn = new int[26];
					System.out.println("what's the filename or path of this rotor?");
					try {
						rotorPath = br.readLine();
					} catch (IOException ioe) {
						System.out.println("IO error trying to read it!");
						System.exit(1);
					}
					BufferedReader thirdrotorbr = new BufferedReader(new InputStreamReader(new FileInputStream(rotorPath)));
					try {
						String line;
						int lineint = 0, increment = 0;
						while ((line = thirdrotorbr.readLine()) != null) {
							lineint = Integer.parseInt(line);
							thirdrotorIn[increment] = lineint;
							increment++;
						}
					} finally {
						thirdrotorbr.close();
					}
					break;	
				default:
					System.out.println("something went wrong :(");
					break;
			}
		}
		else if(choiceint == 4) {
			int checkRotor[] = new int[26];
			String checkPath = null;
			System.out.println("what's the filename or path of this rotor?");
			try {
				checkPath = br.readLine();
			} catch (IOException ioe) {
				System.out.println("IO error trying to read it!");
				System.exit(1);
			}
			BufferedReader checkreader = new BufferedReader(new InputStreamReader(new FileInputStream(checkPath)));
			try {
				String line;
				int lineint = 0, increment = 0;
				while ((line = checkreader.readLine()) != null) {
					lineint = Integer.parseInt(line);
					checkRotor[increment] = lineint;
					increment++;
				}
			} finally {
				checkreader.close();
			}
			encrypter.rotorConfirmer(checkRotor);
			System.exit(1);
		}
		
		System.out.println("now choose two pairs of connected letters for the plugbooard (format: 'ab' *newline* 'cd')");
		String plugin1 = null;
		String plugin2 = null;
		try {
			plugin1 = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read it!");
			System.exit(1);
		}
		try {
			plugin2 = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read it!");
			System.exit(1);
		}
		
		if(plugin1.length() != 2 | plugin2.length() != 2) {
			System.out.println("ERROR; you have to choose two pairs of two lowercase letters!");
			System.exit(1);
		}
		
		for(int x = 0; x < 2; x++) {
			for(int y = 0; y < 2; y++) {
				if(plugin1.charAt(x) == plugin2.charAt(y)) {
					System.out.println("ERROR; the two connected pairs have to be seperate!");
					System.exit(1);
				}
			}
		}
		
		for(int z = 0; z < 2; z++) {
			if(!encrypter.isLowercase(plugin1.charAt(z)) || !encrypter.isLowercase(plugin2.charAt(z))) {
				System.out.println("ERROR; you have to choose lowercase letters!");
				System.exit(1);
			}
		}
		
		System.out.println("now choose the three rotors as well as their starting positions (format: '[1-5] [0-25]' *newline* '[1-5] [0-25]' *newline* '[1-5] [0-25]')");
		int firstr, secondr, thirdr, firstpos = 0, secondpos = 0, thirdpos = 0;
		String rotorin = null;
		try {
			rotorin = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read it!");
			System.exit(1);
		}
		
		firstr = Integer.parseInt(rotorin.substring(0, 1));
		firstpos = Integer.parseInt(rotorin.substring(2, rotorin.length()));
		
		try {
			rotorin = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read it!");
			System.exit(1);
		}
		
		secondr = Integer.parseInt(rotorin.substring(0, 1));
		secondpos = Integer.parseInt(rotorin.substring(2, rotorin.length()));
		
		try {
			rotorin = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to it!");
			System.exit(1);
		}
		
		thirdr = Integer.parseInt(rotorin.substring(0, 1));
		thirdpos = Integer.parseInt(rotorin.substring(2, rotorin.length()));
		
		System.out.println("now enter the text (lowercase, whitespace and punctuation only) that you want to en-/decrypt:");
		String word = null;
		try {
			word = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read it!");
			System.exit(1);
		}
		for(int a = 0; a < word.length(); a++) {
			if(!encrypter.isPunctuation(word.charAt(a)) && !encrypter.isLowercase(word.charAt(a)) && !Character.isWhitespace(word.charAt(a))) {
				System.out.println("ERROR; text was not lowercase, whitespace and punctuation only!");
				System.exit(1);
			} 
		}
		
		String raw = "abcdefghijklmnopqrstuvwxyz";
		int rotor1[] = new int[26];
		int rotor2[] = new int[26];
		char plug1[] = new char[2];
		char plug2[] = new char[2];
		int inverter[] = new int[26];
		
		plug1[0] = plugin1.charAt(0);
		plug1[1] = plugin1.charAt(1);
		plug2[0] = plugin2.charAt(0);
		plug2[1] = plugin2.charAt(1);
		
		for(int a = 0; a < 26; a++) {
			if(a % 2 == 0) {
				rotor1[a] = inverter[a] = 1;
				rotor2[a] = -1;
			}
			else {
				rotor1[a] = inverter[a] = -1;
				rotor2[a] = 1;
			}
		}
		
		int[] rotorone = null;
		int[] rotortwo = null;
		int[] rotorthree = null;
		
		if(firstrotorIn != null) {
			rotorone = encrypter.iterate(firstrotorIn, firstpos);
		}
		else {
			rotorone = encrypter.iterate(rotor1, firstpos);
		}
		if(secondrotorIn != null) {
			rotortwo = encrypter.iterate(secondrotorIn, secondpos);
		}
		else {
			rotortwo = encrypter.iterate(rotor2, secondpos);
		}
		if(thirdrotorIn != null) {
			rotorthree = encrypter.iterate(thirdrotorIn, firstpos);
		}
		else {
			rotorthree = encrypter.iterate(rotor1, firstpos);
		}
		String codew = encrypter.encryptplugboard(word, plug1, plug2, false);
		codew = encrypter.encryptrotor(codew, raw, rotorone);
		codew = encrypter.encryptrotor(codew, raw, rotortwo);
		codew = encrypter.encryptrotor(codew, raw, rotorthree);
		codew = encrypter.encryptinvert(codew, raw, inverter);
		codew = encrypter.encryptrotor(codew, raw, encrypter.invertRotor(rotorthree));
		codew = encrypter.encryptrotor(codew, raw, encrypter.invertRotor(rotortwo));
		codew = encrypter.encryptrotor(codew, raw, encrypter.invertRotor(rotorone));
		codew = encrypter.encryptplugboard(codew, plug1, plug2, true);
		System.out.println(codew); 
		
		//int test[] = encrypter.randomRotor();
		//encrypter.rotorConfirmer(test);
		
    }

}

class encrypter {
	public static int[] iterate(int[] r, int sum) {
		for(int a = 0; a < sum; a++) {	
			int rotornew[] = new int[26];
			rotornew[0] = r[25];
			for(int e = 1; e < 26; e++) {
				rotornew[e] = r[(e-1)];
			}
			r = rotornew;	
		}
		return r;
	}
	public static String encryptrotor(String s, String alpha, int[] r) {
		String codeword = "";
		char currentword;
		//System.out.println("start:" + s);
		
		for(int c = 0; c < s.length(); c++) {
			currentword = s.charAt(c);
			if(Character.isWhitespace(currentword) || isPunctuation(currentword))  {
				codeword = codeword + currentword;
			}
			else {
				for(int d = 0; d < 26; d++) {
					if(currentword == alpha.charAt(d)) {
						//System.out.println("change:" + r[d]);
						char toAdd = (char) (alpha.charAt(d) + r[d]);
						while(toAdd > 'z') {
							int dif = (int) (toAdd - 'z');
							toAdd = (char) ('a' - 1);
							toAdd += dif;
						}
						while(toAdd < 'a') {
							int dif = (int) ('a' - toAdd);
							toAdd = (char) ('z' + 1);
							toAdd -= dif;
						}
						codeword = codeword + toAdd;
					}
				}
			}
			int rotornew[] = new int[26];
			rotornew[0] = r[25];
			for(int e = 1; e < 26; e++) {
				rotornew[e] = r[(e-1)];
			}
			r = rotornew;
		}
		//System.out.println("finish:" + codeword);
		return codeword;
	}
	
	public static String encryptplugboard(String s, char[] p1, char[] p2, boolean rev) {
		char currentword;
		//System.out.println("start:" + s);
		String codeword = "";
		//if(!rev) {
			for(int a = 0; a < s.length(); a++) {
				currentword = s.charAt(a);
				if(currentword == p1[0]) {
					codeword += p1[1];
				}
				else if (currentword == p1[1]) {
					codeword += p1[0];
				}
				else if (currentword == p2[0]) {
					codeword += p2[1];
				}
				else if (currentword == p2[1]) {
					codeword += p2[0];
				}
				else {
					codeword += currentword;
				}
			}
		//}
		/*
		else if(rev) {
			for(int b = 0; b < s.length(); b++) {
				currentword = s.charAt(b);
				if(currentword == p1[1]) {
					codeword += p1[0];
				}
				else if (currentword == p2[1]) {
					codeword += p2[0];
				}
				else {
					codeword += currentword;
				}
			}
		}
		*/
		//System.out.println("finsh:" + codeword);
		return codeword;
	}
	public static String encryptinvert(String s, String alpha, int[] i) {
		String codeword = "";
		//System.out.println("start:" + s);
		char currentword;
		
		for(int c = 0; c < s.length(); c++) {
			currentword = s.charAt(c);
			if(Character.isWhitespace(currentword) || isPunctuation(currentword))  {
				codeword = codeword + currentword;
			}
			else {
				for(int d = 0; d < 26; d++) {
					if(currentword == alpha.charAt(d)) {
						//System.out.println("change:" + i[d]);
						char toAdd = (char) (alpha.charAt(d) + i[d]);
						while(toAdd > 'z') {
							int dif = (int) (toAdd - 'z');
							toAdd = (char) ('a' - 1);
							toAdd += dif;
						}
						while(toAdd < 'a') {
							int dif = (int) ('a' - toAdd);
							toAdd = (char) ('z' + 1);
							toAdd -= dif;
						}
						codeword = codeword + toAdd;
					}
				}
			}
		}
		//System.out.println("finsh:" + codeword);
		return codeword;
	} 
	
	public static int[] invertRotor(int[] r) {
		int invr[] = new int[26];
		int change = 0;
		
		for(int a = 0; a < 26; a++) {
			if(a+r[a] < 0) {
				change = a+r[a] + 26;
			}
			else if(a+r[a] > 25) {
				change = a+r[a] - 26;
			}
			else {
				change = a+r[a];
			}
			invr[change] = (-1 * r[a]);
		}
		
		return invr;
	}
	
	public static boolean isPunctuation(char c) {
        return c == ','
            || c == '.'
            || c == '!'
            || c == '?'
            || c == ':'
            || c == ';'
            ;
    }
	public static boolean isLowercase(char c) {
        return c >= 'a'
            && c <= 'z'
            ;
    }
	public static boolean rotorConfirmer(int[] r) {
		System.out.println("Testing...");
		int helper[] = new int[26];
		for(int a = 0; a < 26; a++) {
			helper[a] = 0;
		}
		for(int a = 0; a < 26; a++) {
			int change;
			if(a+r[a] < 0) {
				change = a+r[a] + 26;
			}
			else if(a+r[a] > 25) {
				change = a+r[a] - 26;
			}
			else {
				change = a+r[a];
			}
			helper[change]++;
		}
		for(int a = 0; a < 26; a++) {
			//System.out.println(helper[a]);
			if(helper[a] != 1) {
				System.out.println("rotor not viable.");
				return false;
			}
		}
		System.out.println("rotor viable.");
		return true;
	}
	
	public static int[] randomRotor() {
		int rot[] = new int[26], help[] = new int[26];
		int neu = 0, change = 0;
		
		for(int a = 0; a < 26; a++) {
			help[a] = 0;
		}
		
		//magic happens here;
		
		for(int a = 0; a < 26; a++) {
			
			neu = (int) (Math.random() * (26 + 25) - 25 );
			//System.out.println(neu);
			rot[a] = neu;
			if(a+neu < 0) {
				change = a+neu + 26;
			}
			else if(a+neu > 25) {
				change = a+neu - 26;
			}
			else {
				change = a+neu;
			}
			if(help[change] == 0) {
				help[change]++;
			}
			else {
				while(help[change] != 0) {
					neu = (int) (Math.random() * (26 + 25) - 25 );
					rot[a] = neu;
					if(a+neu < 0) {
						change = a+neu + 26;
					}
					else if(a+neu > 25) {
						change = a+neu - 26;
					}
					else {
					change = a+neu;
					}
				}
				help[change]++;
			}
		}
		/*
		int sum1 = 0, sum2 = 0;
		
		for(int a = 0; a < 26; a++) {
			sum1 += help[a];
			sum2 += rot[a];
			System.out.println(rot[a]);
		}
		System.out.println(sum1 + " " + sum2 % 26);
		*/
		try {
 
			String content = "This is the content to write into file";
 
			File file = new File("/users/nilsg_000/filename.txt");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int a = 0; a < 26; a++) {
				Integer tempInt = new Integer(rot[a]);
				bw.write(tempInt.toString());	
				bw.write("\r\n");	
			}
			bw.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rot;
	}
}