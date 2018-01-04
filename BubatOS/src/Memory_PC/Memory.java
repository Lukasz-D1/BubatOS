package Memory_PC;

public final class Memory {

	// funkcja podaj�ca aktualny stan pami�ci, jedyna publiczna metoda w tej klasie
	static public char[][] getAll() {
		char[][] ret = new char[4][16];
		for (byte i = 0; i != 4; ++i) {
			for (byte j = 0; j != 16; ++j) {
				ret[i][j] = frames[i * 16 + j];
			}
		}
		return ret;
	}
	
	//metoda zwracaj�ca amount danych od adresu adr
	static public char[] getChars(int adr, int amount) throws Exception{
		if(adr+amount>63) {
			throw new Exception("Poza zakresem");
		}
		char[] ret =new char[amount];
		for(byte i=0; i!=amount; ++i) {
			ret[i]=frames[adr+amount];
		}
		return ret;
	}

	private static short addr[] = { -1, -1, -1, -1 }; // adresy logiczne stron znajduj�cych si� w poszczeg�lnych ramkach
	private static byte ws[] = { -1, -1, -1 }; // working set � zbi�r roboczy
	private static byte current = 0; // indeks aktualnie rozpatrywanego elemntu zbioru roboczego
	// ramki
	private static char frames[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0 };

	// metoda zapisuj�ca, wykorzystywana wewn�trz modu�u zarz�dzania pami�ci�
	static void write(byte p, byte d, char[] data) {
		for (byte i = 0; i != 4; ++i) {
			if (addr[i] == p) {
				ws[current] = i;
				for (byte j = 0; j != data.length; ++j) {
					frames[ws[current] * 16 + d + j] = data[j];
				}
				for (byte j = 0; j != 16; ++j) {
					MassMemory.pages[addr[ws[current]]][j] = frames[ws[current] * 16 + j];
				}
				++current;
				current %= 3;
				return;
			}
		}
		load(p);
		for (byte j = 0; j != data.length + d; ++j) {
			frames[ws[current] * 16 + d + j] = data[j];
		}
		for (byte j = 0; j != 16; ++j) {
			MassMemory.pages[p][j] = frames[ws[current] * 16 + j];
		}
		++current;
		current %= 3;
		return;
	}

	// metoda odczytuj�ca, wykorzystywana wewn�trz modu�u zarz�dzania pami�ci�
	static char[] read(byte p, byte d, byte n) {
		char ret[] = new char[n];
		for (byte i = 0; i != 4; ++i) {
			if (addr[i] == p) {
				ws[current] = i;
				++current;
				current %= 3;
				for (byte j = d; j != n + d; ++j) {
					ret[j - d] = frames[i * 16 + j];
				}
				return ret;
			}
		}
		load(p);
		for (byte j = d; j != n + d; ++j) {
			ret[j - d] = frames[ws[current] * 16 + j];
		}
		++current;
		current %= 3;
		return ret;
	}

	static char read(byte p, byte d) {
		for (byte i = 0; i != 4; ++i) {
			if (addr[i] == p) {
				ws[current] = i;
				++current;
				current %= 3;
				return frames[i * 16 + d];
			}
		}
		load(p);
		char ret = frames[ws[current] * 16 + d];
		++current;
		current %= 3;
		return ret;
	}

	private static void load(short p) {
		byte i = 0;
		while (i != 4) {
			if (absence(i)) {
				ws[current] = i;
				break;
			}
			++i;
		}
		if (ws[current] != -1 && addr[ws[current]] != -1) {
			for (i = 0; i != 16; ++i) {
				MassMemory.pages[addr[ws[current]]][i] = frames[ws[current] * 16 + i];
			}
		}
		addr[ws[current]] = p;
		for (i = 0; i != 16; ++i) {
			frames[ws[current] * 16 + i] = MassMemory.pages[p][i];
		}
	}

	private static Boolean absence(byte n) {
		byte i = 0;
		while (i != 3) {
			if (ws[i] == n)
				return false;
			else
				++i;
		}
		return true;
	}
}