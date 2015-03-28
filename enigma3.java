import java.io.*;

/*
TODO:
-write more errorchecks (rotor + position input via regex!)
-rotor confirmer + random rotor generator + rotor output as file with its own encryption
-capital letter support?
-more rotors
-fileinput
-gui
*/

public class enigma3 {
 
    public static void main(String[] args) {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("first choose two pairs of connected letters for the plugbooard (format: 'ab' *newline* 'cd')");
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
		
		plug1[0] = plugin1.charAt(0);
		plug1[1] = plugin1.charAt(1);
		plug2[0] = plugin2.charAt(0);
		plug2[1] = plugin2.charAt(1);
		
		for(int a = 0; a < 26; a++) {
			if(a % 2 == 0) {
				rotor1[a] = 1;
				rotor2[a] = -1;
			}
			else {
				rotor1[a] = -1;
				rotor2[a] = 1;
			}
		}
		int inverter[] = rotor1;
		
		int[] rotorone = encrypter.iterate(rotor1, firstpos);
		int[] rotortwo = encrypter.iterate(rotor2, secondpos);
		int[] rotorthree = encrypter.iterate(rotor1, thirdpos);
		
		String codew = encrypter.encryptplugboard(word, plug1, plug2, false);
		codew = encrypter.encryptrotor(codew, raw, rotorone);
		codew = encrypter.encryptrotor(codew, raw, rotortwo);
		codew = encrypter.encryptrotor(codew, raw, rotorthree);
		codew = encrypter.encryptinvert(codew, raw, inverter);
		codew = encrypter.encryptrotor(codew, raw, rotorthree);
		codew = encrypter.encryptrotor(codew, raw, rotortwo);
		codew = encrypter.encryptrotor(codew, raw, rotorone);
		codew = encrypter.encryptplugboard(codew, plug1, plug2, true);
		System.out.println(codew);
		
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
}