package Memory;

public class VirtualMemory {
	byte free; //liczba wolnych stron
	long states; //zmienna przechowująca stan każdej strony
	char pages[][]; //tablica stron

	VirtualMemory() {
		pages = new char[64][16];
		states = 0;
		free = 64;
	}

	byte[] load(char data[]) {
		byte i = (byte) ((data.length + 15) / 16);
		if (i > free) {// Błąd – za mało miejsca
			return null;
		}
		free -= i;
		int loaded = 0;
		long current_state = states;
		byte j = 0;
		byte[] ret = new byte[i];
		while (i != loaded) {
			while (j != 64) {
				if (current_state % 2 == 0) {
					for (byte k = 0; k != 16; ++k) {
						pages[j][k] = data[k + 16 * loaded];
					}
					states |= 1 << j;
					ret[loaded++] = j++;
				}
				current_state >>= 1;
			}
		}
		return ret;
	}

	void clear(byte tab[]) {
		free += (byte) tab.length;
		for (byte i = (byte) tab.length; i != 0; --i) {
			states &= 0 << tab[i];
		}
	}
}
