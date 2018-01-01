package inodes;
import java.util.Date;
public class Inode {
	String month;
	int day;
	int hour;
	int minute;
	
	boolean stan; //czy otwarty true-uzywany przez proces
	Semaphore s;
	int sizeF;
	int LinkCounter; // licznik trwałych dowiązań
	//zerowy indeks wskazuje bezpośrednio na blok dyskowy z danymi. Następny wskazuje na blok indeksowe.
	int [] inode_table  = new int[2];
}
