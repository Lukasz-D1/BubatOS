package Memory;

public final class MassMemory {
	static byte free=64; // liczba wolnych stron
	static long states=0; // zmienna przechowująca stan każdej strony
	static char pages[][]= new char[64][16]; // tablica stron


	static byte[] load(char data[]) {
		byte i = (byte) ((data.length + 15) / 16);
		if (i > free) {// Błąd – za mało miejsca
			return null;
		}
		free -= i;
		int loaded = 0;
		long currentState = states;
		byte j = 0;
		byte[] ret = new byte[i];
		while (i != loaded) {
			if (currentState % 2 == 0) {
				for (byte k = 0; k != 16; ++k) {
					if (loaded*16 + k == data.length) {
						break;
					}
					pages[j][k] = data[k + 16 * loaded];
				}
				states |= 1 << j;
				ret[loaded++] = j++;
			}
			currentState >>= 1;

		}
		return ret;
	}


	static void clear(byte tab[]) {
		free += (byte) tab.length;
		for (byte i = (byte) tab.length; i != 0; --i) {
			states &= 0 << tab[i];
		}
	}
}
