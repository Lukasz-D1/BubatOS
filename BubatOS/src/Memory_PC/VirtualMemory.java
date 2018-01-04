package Memory_PC;

public final class VirtualMemory {
	static byte free; //liczba wolnych stron
	static long states; //zmienna przechowuj¹ca stan ka¿dej strony
	static char pages[][]; //tablica stron

	VirtualMemory() {
		pages = new char[64][16];
		states = 0;
		free = 64;
	}

	static byte[] load(char data[]) {
		byte i = (byte) ((data.length + 15) / 16);
		if (i > free) {// B³¹d – za ma³o miejsca
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
	
	static void update(byte ad, char[] data) {
		pages[ad] = data;
		return;
	}
	
	static void clear(byte tab[]) {
		free += (byte) tab.length;
		for (byte i = (byte) tab.length; i != 0; --i) {
			states &= 0 << tab[i];
		}
	}
}