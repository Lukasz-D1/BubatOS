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
	String name;
	int inodeNum; //indeks do tablicy i-węzłów
	Types type_of_file;
	//w momencie czytanie umieszczany na poczatku pliku
	//w momencie zapisu na koncu pliku
	int currentPositionPtr;//pozycja czytania w pliku
}
