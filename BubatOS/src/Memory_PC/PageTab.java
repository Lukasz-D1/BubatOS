package Memory_PC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class PageTab {
	byte[] tab; // tablica numerów stron, w których są dane procesu
	int size;
	String fileName;

 
	
	//int[][] jumpedIn = new int[4][2]; //zbiór danych o dotychczas wykonanych procesach
	//byte ji = 0; //zmienna pomocnicza do zarządzania powyższą tablicą
	
	/*// Poniższe 3 zmienne służą do wyciągania komend
	int lastCommand = -1;
	byte comP = 0;
	byte comD = 0;*/

	public char[] getProcessMemory() {
		char[] ret = new char[size];
		System.out.println(size+" "+tab.length);
		for (byte i = 0; i != size; ++i) {
			ret[i] = Memory.read(tab[i/16], (byte) (i%16));
		}
		return ret;
	}

	public String getFileName() {
		return fileName;
	}

	public int getSize() {
		return size;
	}

	public PageTab(String fileName, int size) throws IOException {
		if (fileName == "") {
			this.fileName="";
			this.size = size;
			char[] data = new char[] {0};
			tab = MassMemory.load(data);
			return;
		}
		this.fileName = fileName;
		this.size = size;
		FileReader Rr = new FileReader(fileName);
		BufferedReader BRr = new BufferedReader(Rr);
		String str = "";
		String line;
		while ((line = BRr.readLine()) != null) {
			if (line != "HX" && line != "hx") {
				line += "\n";
			}
			str += line;
		}
		BRr.close();
		Rr.close();
		char data[] = str.toCharArray();
		char[] data2 = new char[size]; 
		for (short i = 0; i != data.length; ++i) {
			data2[i] = str.charAt(i);
		}
		for (short i = (short) data.length; i != size; ++i) {
			data2[i] = 0;
		}
		tab = MassMemory.load(data2);
	}

	public void finalize() {
		MassMemory.clear(tab);
	}

	public void save() throws IOException {
		int i = 0;
		FileWriter Wr = new FileWriter(fileName);
		while (i != size) {
			Wr.write(Memory.read(tab[i / 16], (byte) (i % 16)));
			++i;
		}
		Wr.close();
	}

	// funkcja zwracaj¹ca komendê o numerze n
	public Vector<String> getCommand(int n) throws Exception {
		
		System.out.println("Mam: "+n);
		
		char[] odczyt= this.read(0, this.getSize());
		String rozkaz = "";
		
		for(int x = n; x<odczyt.length; x++)
		{
			if(odczyt[x] == '\n')
			{
				x=odczyt.length;
			}
			else if(odczyt[x] == 'H' && odczyt[x+1] == 'T')
			{
				rozkaz+="HT";
				x=odczyt.length;
			}
			else
			{
				rozkaz+=odczyt[x];
			}
		}
		
		Vector<String> ret = new Vector<String>();
		
		String[] rozkazz = rozkaz.split("[ ]");
		
		for(String roz : rozkazz)
		{
			ret.add(roz);
			System.out.println("ROZ:"+roz);
		}
		
		return ret;
		
		/*
		Vector<String> ret = new Vector<String>();
		if (++lastCommand != n) {
			int readCommand = 0;
			for (comP = 0;; ++comP) {
				if (readCommand == n) {
					if (comD == 16) {
						comD = 0;
						++comP;
					}
					break;
				}
				for (comD = 0; comD != 16; ++comD) {
					System.out.println("comP="+comP+" comD="+comD);
					if(comP*16+comD>size) {
						throw new Exception("Poza zakresem");
					}
					if (readCommand == n) {
						if (comD == 16) {
							comD = 0;
							++comP;
						}
						break;
					}
					char c = Memory.read(tab[comP], comD);
					if (c == 10) {
						++readCommand;
					}
				}
				if (readCommand == n) {
					if (comD == 16) {
						comD = 0;
						++comP;
					}
					break;
				}
			}
		}
		String str = "";
		while (true) {
			for (; comD != 16; comD++) {
				if (str == "HX" || str == "hx") {
					ret.add(str);
					comD++;
					if (comD > 15) {
						++comP;
						comD -= 16;
					}
					return ret;
				}
				char c = Memory.read(tab[comP], comD);
				if (c == 10) {
					ret.add(str);
					comD++;
					if (comD > 15) {
						++comP;
						comD -= 16;
					}
					return ret;
				} else if (c == 32) {
					ret.add(str);
					str = "";
				} else {
					str += c;
				}
			}
			comD = 0;
			++comP;
		}*/
	}

	/*
	// Pobiera komendę spod podanego adresu logicznego
		public Vector<String> getCommandFromAddress(int addr) {
			for(int i=0; i!=ji; ++i) {
				System.out.println(i+":"+jumpedIn[i][0]+" "+jumpedIn[i][1]);
			}
			Vector<String> ret = new Vector<String>();
			for(int i=0; i!=ji; ++i) {
				if(addr==jumpedIn[i][1]) {
					comP=(byte) (addr/16);
					comD=(byte) (addr%16);
					System.out.println("I jump into if");
					lastCommand=jumpedIn[i][0];
					char c;
					String str = "";
					while (true) {
						for (; comD != 16; comD++) {
							if (str == "HX" || str == "hx") {
								ret.add(str);
								comD++;
								if (comD > 15) {
									++comP;
									comD -= 16;
								}
								return ret;
							}
							c = Memory.read(tab[comP], comD);
							if (c == 10) {
								ret.add(str);
								comD++;
								if (comD > 15) {
									++comP;
									comD -= 16;
								}
								return ret;
							} else if (c == 32) {
								ret.add(str);
								str = "";
							} else {
								str += c;
							}
						}
						comD = 0;
						++comP;
					}
				}
			}
			lastCommand = 0;
			for (comP = 0;; ++comP) {
				if ((comP * 16 + comD) == addr) {
					if (comD == 16) {
						comD = 0;
						++comP;
					}
					break;
				}
				for (comD = 0; comD != 16; ++comD) {
					if ((comP * 16 + comD) == addr) {
						if (comD == 16) {
							comD = 0;
							++comP;
						}
						++lastCommand;
						break;
					}
					char c = Memory.read(tab[comP], comD);
					if (c == 10) {
						++lastCommand;
					}
				}
				if ((comP * 16 + comD) == addr) {
					if (comD == 16) {
						comD = 0;
						++comP;
					}
					break;
				}
			}
			String str = "";
			while (true) {
				for (; comD != 16; comD++) {
					if (str == "HX" || str == "hx") {
						ret.add(str);
						comD++;
						if (comD > 15) {
							++comP;
							comD -= 16;
						}
						return ret;
					}
					char c = Memory.read(tab[comP], comD);
					if (c == 10) {
						ret.add(str);
						comD++;
						if (comD > 15) {
							++comP;
							comD -= 16;
						}
						ji%=4;
						jumpedIn[ji][1]=addr;
						jumpedIn[ji][0]=lastCommand;
						++ji;
						if(jumpedIn[ji][0]==0 && addr>0) {
							jumpedIn[ji][0]=1;
						}
						return ret;
					} else if (c == 32) {
						ret.add(str);
						str = "";
					} else {
						str += c;
					}
				}
				comD = 0;
				++comP;
			}
		}
		*/

		// metoda read w wersji zwracającej String
		public String readString(int ad, int amount) throws Exception {
			return new StringBuilder().append(read(ad, amount)).toString();
		}

		// metoda write w akceptująca dane w formie String
		public void write(int ad, String data) throws Exception {
			write(ad, data.toCharArray());
		}

		// metoda odczytująca amount znaków zaczynając od adresu ad
		public char[] read(int ad, int amount) throws Exception {
			if (ad + amount > size) {
				throw new Exception("Poza zakresem");
			}
			if (ad + amount >= tab.length * 16) { // Gdy odwołano się do znaku o zbyt dużym adresie
				return null;
			}
			char[] ret = new char[amount];
			if ((ad % 16) + amount > 32) { // odczytywanie z trzech lub więcej stron
				int counter = ((ad % 16) + amount) / 16;
				byte p = tab[ad / 16];
				byte d = (byte) (ad % 16);
				byte n = (byte) (16 - d);
				char[] part = Memory.read(p, d, n);
				byte re = 0;
				for (byte i = 0; i < n; ++i) {
					ret[i] = part[i];
				}
				re += n;
				for (int j = 1; j != counter; ++j) {
					p = tab[ad / 16 + j];
					d = 0;
					n = 16;
					part = Memory.read(p, d, n);
					for (byte i = 0; i < n; ++i) {
						ret[re + i] = part[i];
					}
					re += n;
				}
				p = tab[ad / 16 + counter];
				d = 0;
				n = (byte) (amount - re);
				part = Memory.read(p, d, n);
				for (byte i = 0; i < n; ++i) {
					ret[re + i] = part[i];
				}
			} else if ((ad % 16) + amount > 16) { // odczytywanie z dwóch stron
				byte p = tab[ad / 16];
				byte d = (byte) (ad % 16);
				byte n = (byte) (16 - d);
				char[] part = Memory.read(p, d, n);
				byte re = 0;
				for (byte i = 0; i < n; ++i) {
					ret[i] = part[i];
				}
				re += n;
				p = tab[ad / 16 + 1];
				d = 0;
				n = (byte) (amount - re);
				part = Memory.read(p, d, n);
				for (byte i = 0; i < n; ++i) {
					ret[re + i] = part[i];
				}
			} else { // odczytywanie z jednej strony
				byte p = tab[ad / 16];
				byte d = (byte) (ad % 16);
				byte n = (byte) (amount);
				char[] part = Memory.read(p, d, n);
				for (byte i = 0; i < n; ++i) {
					ret[i] = part[i];
				}
			}
			return ret;
		}

		// metoda zapisująca znaki data zaczynając od adresu ad
		public void write(int ad, char[] data) throws Exception {
			if (ad + data.length > size) {
				throw new Exception("Poza zakresem");
			}
			if (ad + data.length >= tab.length * 16) { // Gdy odwołano się do znaku o zbyt dużym adresie
				throw new Exception("Poza zakresem");
			}
			if ((ad % 16) + data.length > 32) { // zapisywanie na trzech lub więcej stronach
				int counter = ((ad % 16) + data.length) / 16;
				byte p = tab[ad / 16];
				byte d = (byte) (ad % 16);
				byte n = (byte) (16 - d);
				char[] part = new char[n];
				for (byte i = 0; i < n; ++i) {
					part[i] = data[i];
				}
				Memory.write(p, d, part);
				byte wr = 0;
				wr += n;
				for (int j = 1; j != counter; ++j) {
					p = tab[ad / 16 + j];
					d = 0;
					n = 16;
					part = new char[n];
					for (byte i = 0; i < n; ++i) {
						part[i] = data[wr + i];
					}
					Memory.write(p, d, part);
				}
				wr += n;
				p = tab[ad / 16 + counter];
				d = 0;
				n = (byte) (data.length - wr);
				part = new char[n];
				for (byte i = 0; i < n; ++i) {
					part[i] = data[wr + i];
				}
				Memory.write(p, d, part);
			} else if ((ad % 16) + data.length > 16) { // zapisywanie na dwóch stronach
				byte p = tab[ad / 16];
				byte d = (byte) (ad % 16);
				byte n = (byte) (16 - d);
				byte wr = 0;
				char[] part = new char[n];
				for (byte i = 0; i < n; ++i) {
					part[i] = data[i];
				}
				Memory.write(p, d, part);
				wr += n;
				p = tab[ad / 16 + 1];
				d = 0;
				n = (byte) (data.length - wr);
				part = new char[n];
				for (byte i = 0; i < n; ++i) {
					part[i] = data[wr + i];
				}
				Memory.write(p, d, part);
			} else { // zapisywanie na jednej stronie
				byte p = tab[ad / 16];
				byte d = (byte) (ad % 16);
				byte n = (byte) (data.length);
				char[] part = new char[n];
				for (byte i = 0; i != n; ++i) {
					part[i] = data[i];
				}
				Memory.write(p, d, part);
			}
		}
}
