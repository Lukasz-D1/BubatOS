package inodes;

public class FileEntry {
	//protected static final int MAX_FILE_SIZE = 40; //B
	String name;
	//String ext;
	//String location; //po³a¿enie wskaznik /Users/greg/text.txt
	int inodeNum; //ideks do tablicy i-wêz³ów
	//int adrIndexBlock;
	boolean stan; //czy otwarty true-uzywany przez proces
	//int size; //B
	//w momencie czytanie umieszczany na poczatku pliku
	//w momencie zapisu na koncu pliku
	//private int currentPositionPtr;
	
	
	//protected int [] atrybuty = new int[10];//trwxrwxrwx pierwszy okresla typ(zwyk³y czy katalog)
									//x np.pozwala zmienic biezacy katalog
	//blok indeskowy
}

