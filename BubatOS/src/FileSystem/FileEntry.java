package inodes;

public class FileEntry {
	public enum Types{
		FILE	(0),
		LINK	(1)
		;
		private final int typeCode;
		Types(int typeCode){
			this.typeCode = typeCode;
		}
		public int getTypeCode() {
	        return this.typeCode;
	    }
	}
	//protected static final int MAX_FILE_SIZE = 40; //B
	String name;
	//String ext;
	//String location; //po³a¿enie wskaznik /Users/greg/text.txt
	int inodeNum; //ideks do tablicy i-wêz³ów
	Types type_of_file; //dowi¹zanie czy zwyk³y plik ????????????????? potrzebne przy dowi¹zaniu
	//int adrIndexBlock;
	//boolean stan; //czy otwarty true-uzywany przez proces
	//int size; //B
	//w momencie czytanie umieszczany na poczatku pliku
	//w momencie zapisu na koncu pliku
	int currentPositionPtr;//pozycja czytania w pliku
	//obiekt semafora
	//Semaphore s;
	//protected int [] atrybuty = new int[10];//trwxrwxrwx pierwszy okresla typ(zwyk³y czy katalog)
									//x np.pozwala zmienic biezacy katalog
	//blok indeskowy
}

