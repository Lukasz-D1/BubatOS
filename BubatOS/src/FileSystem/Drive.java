package inodes;
import java.util.*;
import inodes.FileEntry;
public class Drive {
//	public enum Op{
//		O_RDONLNY	(0),
//		O_WRONLY	(1),
//		O_RDWR		(2)
//		;
//		private final int opCode;
//		Op(int opCode){
//			this.opCode = opCode;
//		}
//		public int getOpCode() {
//	        return this.opCode;
//	    }
//	}
	private static final int DRIVE_SIZE = 1024; //B 32B*32
	private static final int DRIVE_BLOCK_SIZE = 32; //B
	private static final int DRIVE_BLOCK_AMOUNT = 32;
	private static int FREE_BLOCK_AMOUNT = 32;
	
	public char 	[] drive;
	public int 		[] bitVector;
	//public File 	[] openFilesTable;
	//public ArrayList<Inode>  inodesTable;
	public Inode	[]  inodesTable;
	public Hashtable<String,FileEntry> catalog;
	
	/*--Constructor--*/
	public Drive(){
		drive = new char[DRIVE_SIZE];
		bitVector = new int[DRIVE_BLOCK_AMOUNT];
		//openFilesTable =  new File[32];
		//inodesTable = new ArrayList<Inode>(); //max.32
		inodesTable = new Inode[32];
		catalog = new Hashtable<String,FileEntry>(); //max.32
		
		/*--ZEROWANIE BIT VECTORA--*/
		for(int i=0;i<DRIVE_BLOCK_AMOUNT;i++){
			bitVector[i] = 1; //1 oznacza pole wolne
		}
		/*--ZEROWANIE DYSKU--*/
		for(int i=0;i<DRIVE_SIZE;i++){
			drive[i] = (char)0;
		}
		//tworzenie katalogu glownego i sciezki do niego
		//np. /home - montowanie systemu plików 
	}
	//np. u¿ytkownik wpisze CR P1 40;
	/*
	 O_RDONLNY - tylko do odczytu (0)
	 O_WRONLY - tylko do zapisu (1)
	 O_RDWR - do zapisu i odczytu (2)
	 */
	private int freeSpaceCheck(){
		for(int i=0;i<DRIVE_BLOCK_AMOUNT;i++){
			if(bitVector[i] == 1)
			{
				bitVector[i] = 0;
				//inicjalizacja zajêtego bloku 
				Arrays.fill(drive, i*32, i*32+32, (char)(-1));
				--FREE_BLOCK_AMOUNT;
				return i;
			}
		}
		return -1;
	}
	private int freeInodeIndex(){
		for(int i=0;i<32;i++){
			if(inodesTable[i] == null)
			{
				return i;
			}
		}
		return -1;//full
	}
	public void createFile(String name){
		int freeBlock = freeSpaceCheck();
		if(!catalog.containsKey(name) && freeBlock != -1){
			System.out.println("Creating a file...");
			FileEntry file = new FileEntry();
			Inode inode = new Inode();
			Calendar cal = Calendar.getInstance();
			file.name = name;
			
			//wykonywana operacja zapisu, wiêc zajêty
			file.stan = true;
			//nastêpny indeks tablicy
			//file.inodeNum = inodesTable.size();
			file.inodeNum = freeInodeIndex();
			/*I-NODES*/
			inode.month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH );
			inode.day = cal.get(Calendar.DAY_OF_MONTH);
			inode.hour = cal.get(Calendar.HOUR_OF_DAY);
			inode.minute = cal.get(Calendar.MINUTE);
			inode.LinkCounter = 1;//first link
			inode.sizeF = 0;//B
			//okreœlenie pierwszego numeru bloku dyskowego
			
			inode.inode_table[0] = freeBlock;
			catalog.put(name,file);
			inodesTable[file.inodeNum] = inode;
			//inodesTable.add(inode);
			
			System.out.println("Created!");
		}
		else if(catalog.containsKey(name)){
			System.out.println("Istnieje juz plik o takiej nazwie");
		}
		else if(freeBlock == -1){
			System.out.println("Wszystkie bloki s¹ zajête");
		}
		//if()
		/*if(size < File.MAX_FILE_SIZE){
			
		}else{
			System.out.println("Za du¿y rozmiar pliku");
		}*/
	}
	public int openFile(String name, String ext){
		//przegl¹da katalog i kopiuje odpowiedni wpis katalogowy do tablicy otwartych plików
		//nale¿y sprawdziæ czy plik nie jest otwarty przez inny proces
		//jeœli ochorna na to zezwala
		//zwraca wskaznik do wpisu w tej tablicy, który jest u¿ywany przez pozosta³e operacje
		//po otwarciu pliku kopia i-wêz³a jest przechowywana w pamiêci g³ównej
		//OpenFilesTable[0] = ("p1");
		//for(String x : OpenFilesTable){
			//System.out.println(x);
		//}
		int i=0;//potrzebne do okreœlenia num indeksu tablicy
		/*for(String entry : OpenFilesTable){
			if(entry == name){
				break;
			}else{
				i++;
			}
		}*/
		return i;
	}
	public void closeFile(){
		//usuwa wpis z tablicy otwartych plików
		
		/*
		 WYKONAÆ JESZCZE OPERACJE:
		 -zmiany stanu pliku(odblokowanie), dzia³anie na semaforach
		 -jak procesor umiera to mo¿e np. wykoanæ metodê close na pliku
		 i wtedy zmieniæ jego stan
		 */
	}
	//DZIA£A LEGITNIE, ALE CZY CHODZI O TAKI SPOSÓB???
	//ustaliæ czy podawaæ miejsce, od októrego mamy wpisaywaæ i ile
	public void writeFile(String name, String data){
		/*
		 WYKONAÆ JESZCZE OPERACJÊ:
		 -sprawdziæ stan pliku przed podjêciem akcji
		  
		  
		  
		 */
		//wywo³anie operacji open()
		//pisanie sekwencyjne
		//wskaznik za nowo napisanymi danymi
		//dane zapisuje siê w pobranym od zarz¹dcy obszarów wolnych bloku indeksowym
		//umieszcza siê go w i-tej pozycji
		//system przechowuje wskaznik pisania okreslajacy miejsce w pliku
		//int currentPositionPtr;
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			int dataSize = data.length();//inodesTable.get(k).sizeF;
			if(dataSize <= 32)
			{
				int directBlockNum = inodesTable[k].inode_table[0];
				for(int i=0;i<data.length();i++)
				{
					drive[directBlockNum*32+i] = data.charAt(i);
				}
				inodesTable[k].sizeF = data.length();
			}
			//tworzymy blok indeksowy w i-node i 
			else if(((dataSize+32-1)/32) <= FREE_BLOCK_AMOUNT+1) 
			{
				int restSize = dataSize - 32;
				//liczba s³u¿y do okraniczenia wpisów nr bloków indekowych
				int n = (restSize+32-1)/32;
				int directBlockNum = inodesTable[k].inode_table[0];
				int inDirectBlockNum = freeSpaceCheck();
				//if(directBlockNum  != -1)
				//{
					//tylko zapis
					inodesTable[k].inode_table[1] = inDirectBlockNum;
					int in=0;
					for(;in<32;in++)
					{
						drive[directBlockNum*32+in] = data.charAt(in);
					}
					//wpisanie nr bloku dyskowego do bloku indeksowego
					//tyle razy 
					//int n = (a + b - 1) / b; -->ceiling
					//
					for(int j=0;j<n;j++)
					{
						drive[inDirectBlockNum*32+j] = (char)freeSpaceCheck();
					}
					for(int j=0;j<n;j++)
					{
						int from = (int)drive[inDirectBlockNum*32+j];
						//System.out.println("from: "+from);
						for(int i=0;in<data.length();i++,in++)
						{
							drive[from*32+i] = data.charAt(in);
						}
					}
					/*-----AKTUALIZACJA DANYCH O PLIKU----*/
					Calendar cal = Calendar.getInstance();
					inodesTable[k].month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH );
					inodesTable[k].day = cal.get(Calendar.DAY_OF_MONTH);
					inodesTable[k].hour = cal.get(Calendar.HOUR_OF_DAY);
					inodesTable[k].minute = cal.get(Calendar.MINUTE);
					
					inodesTable[k].sizeF = data.length();
					
					
				//}
			}
			else
			{
				System.out.println("B³ad, brak miejsca na dysku");
			}
		}
		else
		{
			System.out.println("Nie istnieje plik o takiej nazwie");	
		}
	}
	public void readFile(String name, String ext){
		//wywo³anie operacji open()
		//czytanie sekwencyjne
		//podczas odczytania wskaznik wedruje na koniec i okreœla nowa operacje wejscia-wyjscia
		//system przechowuje wskaznik czytania okreslajacy miejsce nastêpnego czytania w pliku
		
		//podamy ile plik ma zawartosci
		//u¿ytkownik nie zawsze czyta ca³¹ zawartoœæ
		//wskaznik bie¿¹cej pozycji bêdzie potrzebny, gdy bêdzie chcia³ wznowiæ czytanie.
		//int currentPositionPtr;
	
	}
	public void deleteFile(String name){
		//sprawdziæ czy nie wystêpuje w spisie tablicy otwartych plików
			//je¿eli wystêpuje to wywyo³aæ closeFIle()
		//przeszukanie katalogu w celu odnalezienia wpisu
		//likwiduje siê wpis katalogowy
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			int indexAmount = inodesTable[k].sizeF > 32 ? 2:1;
			
			int directBlockNum,inDirectBlockNum;
			
			System.out.println(indexAmount);
			if(indexAmount == 1)
			{
				directBlockNum = inodesTable[k].inode_table[0];
				Arrays.fill(drive, directBlockNum*32, directBlockNum*32+32, (char)0);		
				bitVector[directBlockNum] = 1;
				++FREE_BLOCK_AMOUNT;
				//F.inodeNum--;
				inodesTable[k]=null;

				catalog.remove(name);
			}
			else if(indexAmount == 2)
			{
				directBlockNum = inodesTable[k].inode_table[0];
				Arrays.fill(drive, directBlockNum*32, directBlockNum*32+32, (char)0);		
				bitVector[directBlockNum] = 1;
				++FREE_BLOCK_AMOUNT;
				
				inDirectBlockNum = inodesTable[k].inode_table[1];
				int pom=0;
				while((int)drive[inDirectBlockNum*32+pom] != 65535)//65535--> -1 z char to int :/
				{
					int from = (int)drive[inDirectBlockNum*32+pom];
					System.out.println("from_del:" +from);
					Arrays.fill(drive, from*32, from*32+32, (char)0);
					++FREE_BLOCK_AMOUNT;
					pom++;
				}
				Arrays.fill(drive, inDirectBlockNum*32, inDirectBlockNum*32+32, (char)0);
				++FREE_BLOCK_AMOUNT;
				
				inodesTable[k]=null;
				catalog.remove(name);
			}
		}
		else
		{
			System.out.println("Plik o tej nazwie nie istnieje");
		}
		
		
	}
	public void renameFile(String name, String ext, String newName, String newExt){
		//umozliwia zmianê nazwy pliku
	}
	public void unlinkFile(String location){//int inode, String name, String ext){

		//usuwa dowi¹zania do pliku
	}
	/*FUNCKCJE KATALOGU*/
	public boolean searchFile(String name, String ext){
		
		//sprawdzamy czy plik o podanej nazwie wystpeuje w spisie wpisow katalogowych
		//
		return false;
	}
	private String timView(int t){
		if (t >= 10)
			return Integer.toString(t);
		else
			return "0"+Integer.toString(t);
	}
	//wypisz zawartoœæ katalogu
	public void ListDirectory(){
		//number of hard links, owner, size, last-modified date and filename
		System.out.println("Directory of root: ");
		for(Map.Entry<String, FileEntry> entry : catalog.entrySet()){
			FileEntry F = entry.getValue();
			int k = F.inodeNum;
			System.out.print(inodesTable[k].month+" "+timView(inodesTable[k].day));
			System.out.print(" "+timView(inodesTable[k].hour)+":"+timView(inodesTable[k].minute));
			System.out.print(" "+inodesTable[k].sizeF+"B");
			System.out.print(" "+entry.getKey());
			System.out.println();
		}
		
	}
	/*----POMOCNICZE FUNKCJE----*/
	
	public void printBitVector(){
		for(int i=0;i < bitVector.length;i++){
			System.out.println("["+i+"]=" + bitVector[i]);
		
		}
	}
	public void printDrive(){
		for(int i=0;i < drive.length;i++){
			System.out.println("["+i+"]="+drive[i]);
		}
	}
	
}

























