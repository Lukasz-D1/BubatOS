package Memory;

public class Memory {

	private short addr[] = { -1, -1, -1, -1 }; //adresy logicznej stron znajdujących się w poszczególnych ramkach
	private byte ws[] = { -1, -1, -1 }; //working set – zbiór roboczy
	private byte current; //indeks aktualnie rozpatrywanego elemntu zbioru roboczego
	private char frames[]; //ramki
	VirtualMemory vm;

	Memory() {
		frames = new char[64];
		vm = new VirtualMemory();
		current = 0;
	}

	//funkcja podająca n znaków, zaczynając od strony o adresie logicznym p i offsecie d
	char[] read(byte p, byte d, byte n) {
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
			ret[j] = frames[ws[0] * 16 + j];
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
	
	private void load(short p) {
		byte i = 0;
		while (i != 4) {
			if (absence(i)) {
				ws[current] = i;
			}
		}
		if (addr[ws[current]] != -1) {
			for (i = 0; i != 16; ++i) {
				vm.pages[p][i] = frames[ws[3] * 16 + i];
			}
		}
		for (i = 0; i != 16; ++i) {
			frames[ws[current] * 16 + i] = vm.pages[p][i];
		}
	}


	private Boolean absence(byte d) {
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
