package Memory;

public class PageTab {
	byte[] tab; // tablica numerów stron, w których są dane procesu

	PageTab(char data[]) {
		tab = MassMemory.load(data);
	}

	protected void finalize() {
		MassMemory.clear(tab);
	}

	// metoda odczytująca amount znaków zaczynając od adresu ad
	char[] read(int j, int amount) {
		if (j + amount >= tab.length * 16) { // Gdy odwołano się do znaku o zbyt dużym adresie
			return null;
		}
		char[] ret = new char[amount];
		if ((j % 16) + amount > 32) { // odczytywanie z trzech stron
			byte p = tab[j / 16];
			byte d = (byte) (j % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			byte re = 0;
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
			re += n;
			p = tab[j / 16 + 1];
			d = 0;
			n = 16;
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
			re += n;
			p = tab[j / 16 + 2];
			d = 0;
			n = (byte) (amount - re);
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
		} else if ((j % 16) + amount > 16) { // odczytywanie z dwóch stron
			byte p = tab[j / 16];
			byte d = (byte) (j % 16);
			byte n = (byte) (16 - d);
			char part[] = Memory.read(p, d, n);
			byte re = 0;
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
			re += n;
			p = tab[j / 16 + 1];
			d = 0;
			n = (byte) (amount - re);
			part = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[re + i] = part[i];
			}
		} else { // odczytywanie z jednej strony
			byte p = tab[j / 16];
			byte d = (byte) (j % 16);
			byte n = (byte) (amount);
			char part[] = Memory.read(p, d, n);
			for (byte i = 0; i < n; ++i) {
				ret[i] = part[i];
			}
		}
		return ret;
	}

	// metoda zapisująca znaki data zaczynając od adresu ad
	void write(int j, char[] data) {
		if (j + data.length >= tab.length * 16) { // Gdy odwołano się do znaku o zbyt dużym adresie
			return;
		}
		if ((j % 16) + data.length > 32) { // zapisywanie na trzech stronach
			byte p = tab[j / 16];
			byte d = (byte) (j % 16);
			byte n = (byte) (16 - d);
			char[] part = new char[n];
			for (byte i = 0; i < n; ++i) {
				part[i] = data[i];
			}
			Memory.write(p, d, part);
			byte wr = 0;
			wr += n;
			p = tab[j / 16 + 1];
			d = 0;
			n = 16;
			part = new char[n];
			for (byte i = 0; i < n; ++i) {
				part[i] = data[wr + i];
			}
			Memory.write(p, d, part);
			wr += n;
			p = tab[j / 16 + 2];
			d = 0;
			n = (byte) (data.length - wr);
			part = new char[n];
			for (byte i = 0; i < n; ++i) {
				part[i] = data[wr + i];
			}
			Memory.write(p, d, part);
		} else if ((j % 16) + data.length > 16) { // zapisywanie na dwóch stronach
			byte p = tab[j / 16];
			byte d = (byte) (j % 16);
			byte n = (byte) (16 - d);
			byte wr = 0;
			char[] part = new char[n];
			for (byte i = 0; i < n; ++i) {
				part[i] = data[i];
			}
			Memory.write(p, d, part);
			wr += n;
			p = tab[j / 16 + 1];
			d = 0;
			n = (byte) (data.length - wr);
			part = new char[n];
			for (byte i = 0; i < n; ++i) {
				part[i] = data[wr + i];
			}
			Memory.write(p, d, part);
		} else { // zapisywanie na jednej stronie
			byte p = tab[j / 16];
			byte d = (byte) (j % 16);
			byte n = (byte) (data.length);
			char[] part = new char[n];
			for (byte i = 0; i != n; ++i) {
				part[i] = data[i];
			}
			Memory.write(p, d, part);
		}
	}
}
