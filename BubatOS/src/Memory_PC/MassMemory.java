package Memory;

public final class MassMemory {
	static byte free = 64; // liczba wolnych stron
	static long states = 0; // zmienna przechowująca stan każdej strony
	static char pages[][] = new char[64][16]; // tablica stron

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
					if (loaded * 16 + k == data.length) {
						break;
					}
					pages[j][k] = data[k + 16 * loaded];
				}
				states |= (1 << j);
				ret[loaded++] = j;
			}
			currentState >>= 1;
			j++;
		}
		return ret;
	}

	static void clear(byte tab[]) {
		free += (byte) tab.length;
		for (byte i = (byte) (tab.length - 1); i != 0; --i) {
			states &= -2 << tab[i]; // -2==0xfffffffffffffffe
		}
	}

	static public char[][] getPages(int number, int amount) throws Exception {
		char[][] ret = new char[amount][16];
		if (number + amount > 63) {
			throw new Exception("Poza zakresem");
		}
		for (int i = 0; i != amount; ++i) {
			ret[i] = pages[number + i];
		}
		return ret;
	}

	public static char[] getChars(int ad, int amount) throws Exception {
		if (ad + amount > 1023) {
			throw new Exception("Poza zakresem");
		}
		char[] ret = new char[amount];
		for (int i = 0; i != amount; ++i) {
			ret[i] = pages[(ad + i) / 16][(ad + i) % 16];
		}
		return ret;
	}

	// pomocnicza procedura służąca debugowaniu
	static void show() {
		System.out.println("MassMemory:");
		System.out.println("states=" + Long.toBinaryString(states));
		System.out.println("free pages=" + free);
		for (byte i = 0; i != 16; ++i) {
			System.out.print(i + ":");
			for (byte j = 0; j != 16; j++) {
				System.out.print(pages[i][j]);
			}
			System.out.println();
		}
	}
}
