package Memory;

public final class Memory {

	private static short addr[] = { -1, -1, -1, -1 }; //adresy logiczne stron znajdujących się w poszczególnych ramkach
	private static byte ws[] = { -1, -1, -1 }; //working set – zbiór roboczy
	private static byte current; //indeks aktualnie rozpatrywanego elemntu zbioru roboczego
	private static char frames[]; //ramki

	Memory() {
		frames = new char[64];
		current = 0;
	}

	static void wite(byte p, byte d, char[] data) {
		for (byte i = 0; i != 4; ++i) {
			if (addr[ws[i]] == p) {
				ws[current] = i;
				++current;
				current %= 3;
				for (byte j = 0; j != data.length + d; ++j) {
					frames[ws[i] * 16 + d + j] = data[j];
				}
				return;
			}
		}
		load(p);
		++current;
		current %= 3;
		for (byte j = 0; j != data.length + d; ++j) {
			frames[ws[current-1] * 16 + d + j] = data[j];
		}
		return;
	}
	
	//funkcja podająca n znaków, zaczynając od strony o adresie logicznym p i offsecie d
	static char[] read(byte p, byte d, byte n) {
		char ret[] = new char[n];
		for (byte i = 0; i != 4; ++i) {
			if (addr[ws[i]] == p) {
				ws[current] = i;
				++current;
				current %= 3;
				for (byte j = d; j != n + d; ++j) {
					ret[j] = frames[ws[i] * 16 + j];
				}
				return ret;
			}
		}
		load(p);
		++current;
		current %= 3;
		for (byte j = d; j != n + d; ++j) {
			ret[j] = frames[ws[current-1] * 16 + j];
		}
		return ret;
	}	
	
	//funkcja podająca aktualny stan pamięci
	char[][] getAll(){
		char[][]ret=new char[4][16];
		for(byte i=0; i!=4; ++i) {
			for(byte j=0; j!=16; ++j) {
				ret[i][j]=frames[i*3+j];
			}
		}
		return ret;
	}
	
	private static void load(short p) {
		byte i = 0;
		while (i != 4) {
			if (absence(i)) {
				ws[current] = i;
			}
		}
		if (addr[ws[current]] != -1) {
			for (i = 0; i != 16; ++i) {
				VirtualMemory.pages[p][i] = frames[ws[current] * 16 + i];
			}
		}
		for (i = 0; i != 16; ++i) {
			frames[ws[current] * 16 + i] = VirtualMemory.pages[p][i];
		}
	}

	
	private static Boolean absence(byte d) {
		byte i = 0;
		while (i != 3) {
			if (ws[i] == d)
				return true;
			else
				++i;
		}
		return false;
	}
}
