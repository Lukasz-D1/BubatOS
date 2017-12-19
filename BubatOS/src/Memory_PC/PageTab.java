package Memory;

public class PageTab {
	byte[] tab; //tablica indeksów stron, w których są dane procesu
	Memory m;

	PageTab(char data[], Memory m) {
		this.m = m;
		tab = m.vm.load(data);
	}

	protected void finalize() {
		m.vm.clear(tab);
	}
}
