package Memory;

public class PageTab {
	byte[] tab; // tablica indeksów stron, w których są dane procesu

	PageTab(char data[]) {
		tab = VirtualMemory.load(data);
	}

	protected void finalize() {
		VirtualMemory.clear(tab);
	}
	
	//Poniżej są metody pozwalające pozyskiwać i zapisywać dane z/w pamięci przydzielonej procesowi
	
	//metoda odczytująca amount znaków zaczynając od adresu ad
	char[] read(short ad, short amount) {
		if (ad + amount >= tab.length * 16) { // Gdy odwołano się do znaku o zbyt dużym adresie
			return null;
		}
		char[] ret = new char[amount];
		if ((ad % 16) + amount > 32) { // odczytywanie z trzech stron
			byte p = tab[ad / 16];
			byte d = (byte) (ad % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			byte re = 0;
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
			re += n;
			p = tab[ad / 16 + 1];
			d = 0;
			n = 16;
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
			re += n;
			p = tab[ad / 16 + 2];
			d = 0;
			n = (byte) (amount-re);
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
		} else if ((ad % 16) + amount > 16) { // odczytywanie z dwóch stron
			byte p = tab[ad / 16];
			byte d = (byte) (ad % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			byte re = 0;
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
			re += n;
			p = tab[ad / 16 + 1];
			d = 0;
			n = (byte) (amount-re);
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
		}
		else { //odczytywanie z jednej strony
			byte p = tab[ad / 16];
			byte d = (byte) (ad % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
		}
		return ret;
	}
	
	//metoda zapisująca znaki data zaczynając od adresu ad
	void write(short ad, char[] data) {
		if (ad + data.length >= tab.length * 16) { // Gdy odwołano się do znaku o zbyt dużym adresie
			return;
		}
		char[] ret = new char[data.length];
		if ((ad % 16) + data.length > 32) { // zapisywanie na trzech stronach
			byte p = tab[ad / 16];
			byte d = (byte) (ad % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			byte re = 0;
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
			re += n;
			p = tab[ad / 16 + 1];
			d = 0;
			n = 16;
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
			re += n;
			p = tab[ad / 16 + 2];
			d = 0;
			n = (byte) (data.length-re);
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
		} else if ((ad % 16) + data.length > 16) { // zapisywanie na dwóch stronach
			byte p = tab[ad / 16];
			byte d = (byte) (ad % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			byte re = 0;
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
			re += n;
			p = tab[ad / 16 + 1];
			d = 0;
			n = (byte) (data.length-re);
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
		}
		else { //zapisywanie na jednej stronie
			byte p = tab[ad / 16];
			byte d = (byte) (ad % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
		}
	}
}
