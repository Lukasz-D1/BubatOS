package FileSystem;
import java.util.Date;

import semaphore.Semaphore;

public class Inode {
	String month;
	int day;
	int hour;
	int minute;
	
	boolean stan; //czy otwarty true-uzywany przez proces
	public Semaphore s;
	int sizeF;
	int LinkCounter; // licznik trwałych dowiązań
	//zerowy indeks wskazuje bezpośrednio na blok dyskowy z danymi. Następny wskazuje na blok indeksowe.
	int [] inode_table  = new int[2];
}
