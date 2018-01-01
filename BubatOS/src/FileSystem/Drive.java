package inodes;
import java.io.File;
import java.util.*;
import inodes.FileEntry;
import inodes.FileEntry.Types;

public class Drive {
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
	
	/*KONTENER POMOCNICZY*/
	private ArrayList<Integer> IndexBlockNumbers;
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
		IndexBlockNumbers = new ArrayList<Integer>();
	}
	//np. użytkownik wpisze CR P1 40;
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
				//inicjalizacja zajętego bloku 
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
		System.out.println(freeBlock);
		if(!catalog.containsKey(name) && freeBlock != -1){
			System.out.println("Creating a file...");
			FileEntry file = new FileEntry();
			Inode inode = new Inode();
			Calendar cal = Calendar.getInstance();
			file.name = name;
			file.type_of_file = Types.FILE;
			//zamknięty
			//file.stan = false;
			file.currentPositionPtr=0;//zapis i odczyt od początku pliku
			//następny indeks tablicy
			//file.inodeNum = inodesTable.size();
			file.inodeNum = freeInodeIndex();
			/*I-NODES*/
			inode.month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH );
			inode.day = cal.get(Calendar.DAY_OF_MONTH);
			inode.hour = cal.get(Calendar.HOUR_OF_DAY);
			inode.minute = cal.get(Calendar.MINUTE);
			inode.stan = false;
			inode.s = new Semaphore(name);
			
			inode.LinkCounter = 1;//first link
			inode.sizeF = 0;//B
			//określenie pierwszego numeru bloku dyskowego
			
			inode.inode_table[0] = freeBlock;
			inode.inode_table[1] = -1;//-1 oznacza, że nie jest wykorzystywane adresowanie pośrednie
			catalog.put(name,file);
			inodesTable[file.inodeNum] = inode;
			//inodesTable.add(inode);
			
			System.out.println("Created!");
		}
		else if(catalog.containsKey(name)){
			System.out.println("Istnieje juz plik o takiej nazwie");
		}
		else if(freeBlock == -1){
			System.out.println("Wszystkie bloki są zajęte");
		}
		//if()
		/*if(size < File.MAX_FILE_SIZE){
			
		}else{
			System.out.println("Za duży rozmiar pliku");
		}*/
	}
	//Thread running;
	public void openFile(String name) throws InterruptedException{
		//przegląda katalog i kopiuje odpowiedni wpis katalogowy do tablicy otwartych plików
		//należy sprawdzić czy plik nie jest otwarty przez inny proces
		//jeśli ochrona na to zezwala
		//zwraca wskaznik do wpisu w tej tablicy, który jest używany przez pozostałe operacje
		//po otwarciu pliku kopia i-węzła jest przechowywana w pamięci głównej
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			//if(inodesTable[k].stan == true)
			if(inodesTable[k].s.isStan() == false || inodesTable[k].stan == true)
			{
				System.out.println("Plik jest już otwarty");
				//return -1; //już otwarty
			}
			else
			{
				//inodesTable[k].stan = true; //uzywany, operacje na semaforach???
				//jesli stan semafora true to:
				//opuszczenie semafora
				if(Scheduler.Running !=null)
				{
					inodesTable[k].s.P(Scheduler.Running);
				}
				else{
					//gdy nie ma procesów gotowych, a otwieranie jest wykonywane z poziomu shella
					inodesTable[k].stan = true;
				}
				F.currentPositionPtr=0;
				System.out.println("Pomyślnie otwarto plik");
				//return k; //zwraca numer i-węzła
			}
		}
		else
			System.out.println("Plik o takiej nazwie nie istnieje");
		//return -2; //brak pliku o takiej nazwie
	}
	public void closeFile(String name) throws InterruptedException{
		//usuwa wpis z tablicy otwartych plików
		/*
		 WYKONAĆ JESZCZE OPERACJE:
		 -zmiany stanu pliku(odblokowanie), działanie na semaforach
		 -jak procesor umiera to może np. wykoanć metodę close na pliku
		 i wtedy zmienić jego stan
		 */
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			//if(inodesTable[k].stan == false)
			if(inodesTable[k].s.isStan() == true && inodesTable[k].stan == false){
				System.out.println("Plik jest już zamknięty");
			}
			//dla shella
			else if(inodesTable[k].stan == false){
				System.out.println("Plik jest już zamknięty");
			}
			else
			{
				
				if(inodesTable[k].stan == true)
				{
					inodesTable[k].stan = false; //shell
				}
				else
				{
					inodesTable[k].s.V();
				}
				System.out.println("Pomyślnie zamknięto plik");
				//return k;
			}
		}
		else
			System.out.println("Plik o takiej nazwie nie istnieje");
		//return -2;
	}
	//DZIAŁA LEGITNIE, ALE CZY CHODZI O TAKI SPOSÓB???
	//ustalić czy podawać miejsce, od októrego mamy wpisaywać i ile
	public void writeFile(String name, String data){
		/*
		 WYKONAĆ JESZCZE OPERACJĘ:
		 -sprawdzić stan pliku przed podjęciem akcji
		  
		 */
		//wywołanie operacji open()
		//pisanie sekwencyjne
		//wskaznik za nowo napisanymi danymi
		//dane zapisuje się w pobranym od zarządcy obszarów wolnych bloku indeksowym
		//umieszcza się go w i-tej pozycji
		//system przechowuje wskaznik pisania okreslajacy miejsce w pliku
		//int currentPositionPtr -> ustawiany na koncu pliku;
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			//if(inodesTable[k].stan == true) //może sprawdzanie stanu semafora
			if(inodesTable[k].s.isStan() == false || inodesTable[k].stan == true)
			{
				deleteContent(name);//gwarancja nadpisania danych
				F.currentPositionPtr = 0;
				int dataSize = data.length();//inodesTable.get(k).sizeF;
				if(dataSize <= 32)
				{
					int directBlockNum = inodesTable[k].inode_table[0];
					for(int i=0;i<data.length();i++)
					{
						drive[directBlockNum*32+i] = data.charAt(i);
					}
					//inodesTable[k].sizeF = inodesTable[k].sizeF > data.length() ? inodesTable[k].sizeF : data.length();
					inodesTable[k].sizeF = data.length();
					//closeFile(name);
				}
				//tworzymy blok indeksowy w i-node i
				//bierzemy pod uwage tylko ineksowy[1] i kolejne dyskowe
				//plus jeden, bo jeszcze blok indeksowy wliczamy
				else if(((dataSize+32-1)/32) <= FREE_BLOCK_AMOUNT) //+1
				{
					int restSize = dataSize - 32;
					//liczba służy do ograniczenia wpisów nr bloków indekowych
					int n = (restSize+32-1)/32;
					int directBlockNum = inodesTable[k].inode_table[0];
					int inDirectBlockNum = freeSpaceCheck();
					IndexBlockNumbers.add(inDirectBlockNum);
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
						
						//inodesTable[k].sizeF = inodesTable[k].sizeF > data.length() ? inodesTable[k].sizeF : data.length();
						inodesTable[k].sizeF = data.length();
				}
				else
				{
					System.out.println("Bład, brak miejsca na dysku");
				}
			}
			else
			{
				System.out.println("Plik nie jest otwarty");
			}
		}
		else
		{
			System.out.println("Nie istnieje plik o takiej nazwie");	
		}
	}
	public void appendFile(String name, String newData){
		//wypada ustawić wskaznik na koncu pliku
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			if(inodesTable[k].s.isStan() == false || inodesTable[k].stan == true)
			{
				boolean flaga=false; //czy jest miejsce
				
				//int newDataSize = newData.length();
				int acDataSize = inodesTable[k].sizeF;
				int newDataSize = newData.length();
				
				int totalSize = acDataSize + newDataSize;
				int directBlockNum;
				if(totalSize <= 32)
				{
					flaga=true;
					//System.out.println("OK");///////////////////////////
					int in=0;
					directBlockNum = inodesTable[k].inode_table[0];
					for(int i=acDataSize;in<newData.length();in++,i++)
					{
						drive[directBlockNum*32+i] = newData.charAt(in);
					}
					inodesTable[k].sizeF += newData.length();
					//closeFile(name);
				}
				//plus jeden, bo  
				else if(acDataSize < 32 && ((newDataSize-(32-acDataSize)+32-1)/32)+1 <= FREE_BLOCK_AMOUNT)//+1
				{
					flaga=true;
					//System.out.println("OK2");//////////////////////////
					directBlockNum = inodesTable[k].inode_table[0];
					int in=0;
					for(int i=acDataSize;i<32;i++,in++)
					{
						drive[directBlockNum*32+i] = newData.charAt(in);
						--newDataSize;
					}
					//if(((newDataSize+32-1)/32) <= FREE_BLOCK_AMOUNT+1) 
					//{
					//liczba służy do ograniczenia wpisów nr bloków indekowych
					int n = (newDataSize+32-1)/32;
					//int directBlockNum = inodesTable[k].inode_table[0];
					int inDirectBlockNum = freeSpaceCheck();
					IndexBlockNumbers.add(inDirectBlockNum);
					//tylko zapis
					inodesTable[k].inode_table[1] = inDirectBlockNum;
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
						for(int i=0;in<newData.length();i++,in++)
						{
							drive[from*32+i] = newData.charAt(in);
						}
					}
					/*-----AKTUALIZACJA DANYCH O PLIKU----*/
					Calendar cal = Calendar.getInstance();
					inodesTable[k].month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH );
					inodesTable[k].day = cal.get(Calendar.DAY_OF_MONTH);
					inodesTable[k].hour = cal.get(Calendar.HOUR_OF_DAY);
					inodesTable[k].minute = cal.get(Calendar.MINUTE);
					
					inodesTable[k].sizeF += newData.length();
					//closeFile(name);
					//}
				}
				else
				{
					//System.out.println("OK3");//////////////////////////
					/***************/
					int restSize = acDataSize - 32;
					//System.out.println("acDataSize "+acDataSize);
					//System.out.println("restSize "+restSize);
					int indexBlockAmount = (restSize+32-1)/32;//liczba wpisów w bloku indeksowym
					//System.out.println("indexBlockAmount "+indexBlockAmount);
					int inDirectBlockNum3 = inodesTable[k].inode_table[1];
					//System.out.println("inDirectBlockNum3 "+inDirectBlockNum3);
					int x = (32*indexBlockAmount) - restSize; //ilosc wolnych wpisow w bloku dyskowym
					//System.out.println("x "+x);
					if((((newData.length()-x)+32-1)/32) <= FREE_BLOCK_AMOUNT)
					{
						flaga=true;
						if(newData.length() > x)
						{
							//System.out.println("OK5");
							//obliczamy ile przeznaczyc blokow dyskowych na append
							int adDriveBlocksAmount = ((newData.length()-x)+32-1)/32;
							//System.out.println("adDriveBlocksAmount "+adDriveBlocksAmount);
							
							//zapis do wolnej przestrzeni dostepnego bloku dyskowego
							int saveFrom = (int)drive[inDirectBlockNum3*32+indexBlockAmount-1];//-1, bo zapisane od zerowego indeksu
							//System.out.println("saveFrom "+saveFrom);
							int in=0;
							//System.out.println("32-x "+(32-x));
							for(int i=32-x;i<32;i++,in++)
							{
								drive[saveFrom*32+i]=newData.charAt(in);
							}
							//System.out.println("data l "+newData.length());
							//System.out.println("in "+in);
							/********************/
							for(int j=0, pom=indexBlockAmount;j<adDriveBlocksAmount;j++,pom++)
							{
								//nastepnemu indexBlockAmount przypisujemy adres wolnego bloku dyskowego
								drive[inDirectBlockNum3*32+pom] = (char)freeSpaceCheck();
								//System.out.println("nr nowego bloku dyskowego "+(int)drive[inDirectBlockNum3*32+pom]);
							}
							for(int j=0, pom=indexBlockAmount;j<adDriveBlocksAmount;j++,pom++)
							{
								int from = (int)drive[inDirectBlockNum3*32+pom];
								//System.out.println("from_tutaj "+from);
								//System.out.println("from: "+from);
								for(int i=0;in<newData.length();i++,in++)
								{
									drive[from*32+i] = newData.charAt(in);
								}
							}
						}
						else
						{	
							System.out.println("OK4");
							int saveFrom = (int)drive[inDirectBlockNum3*32+indexBlockAmount-1];//-1, bo zapisane od zerowego indeksu
							System.out.println("saveFrom "+saveFrom);
							int in3=0;
							System.out.println("32-x "+(32-x));
							for(int i=32-x;in3<newData.length();i++,in3++)
							{
								drive[saveFrom*32+i]=newData.charAt(in3);
							}
						}
						/*-----AKTUALIZACJA DANYCH O PLIKU----*/
						Calendar cal = Calendar.getInstance();
						inodesTable[k].month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH );
						inodesTable[k].day = cal.get(Calendar.DAY_OF_MONTH);
						inodesTable[k].hour = cal.get(Calendar.HOUR_OF_DAY);
						inodesTable[k].minute = cal.get(Calendar.MINUTE);
						
						inodesTable[k].sizeF += newData.length();
						//closeFile(name);//usunac
					}
				}
				if(!flaga)
				{
					System.out.println("Bład, brak miejsca na dysku");
				}
			}
			else
			{
				System.out.println("Plik nie jest otwarty");
			}
		}
		else
		{
			System.out.println("Nie istnieje plik o takiej nazwie");	
		}
	}
	public void readFile(String name, int amount){
		//wywołanie operacji open()
		//ustawienie wskaznika na poczatku pliku
		
		//czytanie sekwencyjne
		//podczas odczytania wskaznik wedruje na koniec i określa nowa operacje wejscia-wyjscia
		//system przechowuje wskaznik czytania okreslajacy miejsce następnego czytania w pliku
		
		//podamy ile plik ma zawartosci
		//użytkownik nie zawsze czyta całą zawartość
		//wskaznik bieżącej pozycji będzie potrzebny, gdy będzie chciał wznowić czytanie.
		//int currentPositionPtr;
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			if(inodesTable[k].s.isStan() == false || inodesTable[k].stan == true)
			{
				String content = "";
				int s = inodesTable[k].sizeF;
				
				if(s <= 32)
				{
					int directBlockNum = inodesTable[k].inode_table[0];
					//System.out.println("F.currentPositionPtr "+F.currentPositionPtr);
					//System.out.println((amount>(s-F.currentPositionPtr)?s:amount+F.currentPositionPtr));
					for(int i=F.currentPositionPtr;i<(amount>(s-F.currentPositionPtr)?s:amount+F.currentPositionPtr);i++)
					{
						content += drive[directBlockNum*32+i];
					}
					if(amount+F.currentPositionPtr >=s ) //na końcu pliku
						F.currentPositionPtr = s;
					else	
						F.currentPositionPtr += amount;
					//System.out.println("F.currentPositionPtr new "+F.currentPositionPtr);
				}
				else
				{
					int directBlockNum = inodesTable[k].inode_table[0];
					int inDirectBlockNum = inodesTable[k].inode_table[1];
					int pom = amount;
					//int restSize = s - 32;
					//liczba służy do ograniczenia wpisów nr bloków indekowych
					//int n = (restSize+32-1)/32;
					int in=F.currentPositionPtr;
					int in_fin = in;
					if(in<32)
					{
						for(;in<(amount>(32-F.currentPositionPtr)?32:amount+F.currentPositionPtr);in++)
						{
							content += drive[directBlockNum*32+in];
							--pom;
							//F.currentPositionPtr++;
						}
					}
					//wpisanie nr bloku dyskowego do bloku indeksowego
					//tyle razy 
					//int n = (a + b - 1) / b; -->ceiling
					//
					//System.out.println("pom "+ pom);
					//System.out.println("in "+in);
					if(pom>(32-in) && in < s)
					{
						int restSize = pom;
						//System.out.println("s "+s);
						int pom2;//,n;
						if(restSize > s-32)
						{
							pom=s-32;
							restSize = pom;
							//pom2=pom;
							//n=(restSize+32-1)/32;
						}
						pom2 = pom;
						//n = (restSize+32-1)/32;
						//restSize < 5?n-1:n-2)
						int j =(((in)/32)-1);
						//System.out.println("j "+j);
						int z=j;//żeby tylko raz, bo j będzie inkrementowane
						//System.out.println("(restSize+32-1)/32="+((restSize+32-1)/32));
						//System.out.println("((F.currentPositionPtr)/32)-1="+(((F.currentPositionPtr)/32)-1));
						//System.out.println("(F.currentPositionPtr+s-restSize)/32)-1="+(((F.currentPositionPtr+s-restSize)/32)-1));
						
						int firstLoop = (in-32*(j+1));
						int i;
						//int warunek = (pom>(s-in)?(in-32*(z+1))+s-in:(in-32*(z+1))+pom);
						
						int condition=(restSize>s-in?(in+s-in)/32:(in+restSize)/32);
						int difference = condition - j;
						//int temp_in=in;
						for(;j<condition;j++)
						{
							//System.out.println("OK");
							//System.out.println("condition " + condition);
							//System.out.println("pom " + pom);
							//System.out.println("dif: "+difference);
							
							//restSize>s-F.currentPositionPtr?(F.currentPositionPtr+s-F.currentPositionPtr)/32:(F.currentPositionPtr+restSize)/32
							//(((F.currentPositionPtr-restSize)/32)+1)
							//(F.currentPositionPtr+s-32-restSize)/32) dla 34
							//((F.currentPositionPtr+restSize)/32)
							//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
							int which = (int)drive[inDirectBlockNum*32+j];
							//System.out.println("which: "+which);
							for(i=firstLoop;i<(difference>1?32:(pom>=(s-in)?(firstLoop+(s-in)):(firstLoop+pom)));i++)
							{
								//System.out.print(i+",");
								//for(int i=(in-32*(z+1));i<(pom>(s-in)?((in-32*(z+1))+s-in)>32?32:((in-32*(z+1))+s-in):(in-32*(z+1))+pom);i++)
								content += drive[which*32+i];
								--pom2;
							}
							//System.out.println("_in " + in);
							if(firstLoop == 0)
								in+=i;
							else
								in+=(i-firstLoop);
							
							difference--;
							firstLoop=0;
							pom=pom2;
						}
						
					}
					//System.out.println("amount"+amount);
					if(amount+in_fin >= s)
						F.currentPositionPtr = s;
					else
						F.currentPositionPtr += amount;
					//System.out.println("F.currentPositionPtr new "+F.currentPositionPtr);
				}
				//closeFile(name);
				System.out.println("content: "+content);
			}
			else
			{
				System.out.println("Plik nie jest otwarty");
			}
		}
		else
		{
			System.out.println("Nie istnieje plik o takiej nazwie");	
		}
	}
	public void deleteFile(String name){
		//sprawdzić czy nie występuje w spisie tablicy otwartych plików
			//jeżeli występuje to wywyołać closeFIle()
		//przeszukanie katalogu w celu odnalezienia wpisu
		//likwiduje się wpis katalogowy
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			if(inodesTable[k].s.isStan() == true && inodesTable[k].stan == false)
			{
				if(--inodesTable[k].LinkCounter > 0)
				{
					catalog.remove(name);
				}
				else
				{
					int indexAmount = inodesTable[k].sizeF > 32 ? 2:1;
					
					int directBlockNum,inDirectBlockNum;
					
					//System.out.println(indexAmount);
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
							//System.out.println("from_del:" +from);
							Arrays.fill(drive, from*32, from*32+32, (char)0);
							bitVector[from] = 1;
							++FREE_BLOCK_AMOUNT;
							pom++;
						}
						Arrays.fill(drive, inDirectBlockNum*32, inDirectBlockNum*32+32, (char)0);
						bitVector[inDirectBlockNum] = 1;
						++FREE_BLOCK_AMOUNT;
						
						IndexBlockNumbers.remove(IndexBlockNumbers.indexOf(inDirectBlockNum));
						inodesTable[k]=null;
						catalog.remove(name);
					}
				}
				System.out.println("Plik został usunięty");
			}
			else
			{
				System.out.println("Plik jest wykorzystywany! Nie można go teraz usunąć");
			}
		}
		else
		{
			System.out.println("Plik o tej nazwie nie istnieje");
		}
	}
	public void renameFile(String name, String newName){
		if(catalog.containsKey(name)){
			if(!catalog.containsKey(newName))
			{
				FileEntry F = catalog.get(name);
				int k = F.inodeNum;
				if(inodesTable[k].s.isStan() == true && inodesTable[k].stan == false)
				{
					catalog.get(name).name = newName;
					catalog.put(newName, catalog.remove(name));
				}
				else
				{
					System.out.println("Plik jest wykorzystywany! Nie można zmienić nazwy");
				}
			}
			else
			{
				System.out.println("Nowa nazwa już występuje");
			}
		}
		else
			System.out.println("Plik o tej nazwie nie istnieje");
	}
	public void createLink(String newName, String name){
		if(catalog.containsKey(name)){
			if(!catalog.containsKey(newName)){
				FileEntry newF = new FileEntry();
				FileEntry F = catalog.get(name);
				newF.name = newName;
				newF.inodeNum = F.inodeNum;
				newF.currentPositionPtr = F.currentPositionPtr;
				inodesTable[F.inodeNum].LinkCounter++;
				//semafor pozostaje bez zmian i tymczasowe pole stan również
				newF.type_of_file = Types.LINK;
				catalog.put(newName,newF);
				//pamięteć o odycji delete
				//i o zmianie nazwy
			}
			else
			{
				System.out.println("Plik o tej nazwie już istnieje. Nie można utworzyć dowiązania!");
			}
		}
		else
		{
			System.out.println("Plik o tej nazwie nie istnieje");
		}
	}
//	public void unlinkFile(String location){//int inode, String name, String ext){
//
//		//usuwa dowiązania do pliku
//	}
//	/*FUNCKCJE KATALOGU*/
////	public boolean searchFile(String name, String ext){
////		
////		//sprawdzamy czy plik o podanej nazwie wystpeuje w spisie wpisow katalogowych
////		//
////		return false;
////	}
	private String timView(int t){
		if (t >= 10)
			return Integer.toString(t);
		else
			return "0"+Integer.toString(t);
	}
	//wypisz zawartość katalogu
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
			System.out.print(" "+F.type_of_file);
			//System.out.print(" nr "+k);
			System.out.println();
		}
	}
	/*----POMOCNICZE FUNKCJE----*/
	
	public String printBitVector(){
        String zwrot = "";
        for(int i=0;i < bitVector.length;i++){
            System.out.println("["+i+"]=" + bitVector[i]);
            zwrot+="["+i+"]=" + bitVector[i]+"\n";
        }
        return zwrot;
    }
	public void printDrive(){
		boolean check;
		for(int i=0;i < drive.length;i++){
			check=false;
			for(Integer e : IndexBlockNumbers){
				if(e.equals(i/32)){
					check = true;
					break;
				}
			}
			if(check)
			{
				if(drive[i] >=0 && drive[i] <= 32)
					System.out.println("*["+i+"]="+(int)drive[i]);
				else
					System.out.println("*["+i+"]="+drive[i]);
			}
			else
				System.out.println("["+i+"]="+drive[i]);
		}
	}
	public void printDiskBlock(int nr){
		boolean check=false;
		System.out.println("Blok dyskowy nr: "+nr);
		if(nr<=32 && nr >= 0)
		{
			for(int i=nr*32;i<(nr*32+32);i++)
			{
				for(Integer e : IndexBlockNumbers)
				{
					if(e.equals(nr)){
						check = true;
						break;
					}
				}
				if(check)
				{
					if(drive[i] >=0 && drive[i] <= 32)
						System.out.println("*["+i+"]="+(int)drive[i]);
					else
						System.out.println("*["+i+"]="+drive[i]);
				}
				else{
					System.out.println("["+i+"]="+drive[i]);
				}
			}
		}
		else
		{
			System.out.println("Numer poza zakresem");
		}
	}
	public void printIndexBlockNumbers(){
		for(Integer e : IndexBlockNumbers)
			System.out.print(e +", ");
		System.out.println();
		
	}
	public void printInodeInfo(String name){
		if(catalog.containsKey(name)){
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			System.out.println(name + " I-NODE INFO:");
			System.out.print(">"+inodesTable[k].month+" "+timView(inodesTable[k].day));
			System.out.print(" "+timView(inodesTable[k].hour)+":"+timView(inodesTable[k].minute));
			System.out.print(" "+inodesTable[k].sizeF+"B");
			System.out.println(" "+name);
			System.out.println(">I-node nr: "+k);
			//System.out.println(">Type: "+inodesTable[k].type_of_file);
			System.out.println(">LinkCounter: "+inodesTable[k].LinkCounter);
			System.out.println(">I-node table:\n >[0]->blok dyskowy: "+inodesTable[k].inode_table[0]);
			//int l = inodesTable[k].inode_table[1] == -1?-1:1;
			System.out.println(" >[1]->blok indeksowy: "+
					(
						inodesTable[k].inode_table[1] == -1?"brak"
						:
						inodesTable[k].inode_table[1]
					));
			System.out.println();
		}
		else
		{
			System.out.println("Nie ma pliku o podanej nazwie");
		}
	}
	private void deleteContent(String name){
		//usuwa tylko treść pliku (pozostawia tylko pierwszy blok dyskowy)
		FileEntry F = catalog.get(name);
		int k = F.inodeNum;

			int indexAmount = inodesTable[k].sizeF > 32 ? 2:1;
			int directBlockNum,inDirectBlockNum;
					
			if(indexAmount == 1)
			{
				directBlockNum = inodesTable[k].inode_table[0];
				Arrays.fill(drive, directBlockNum*32, directBlockNum*32+32, (char)-1);
			}
			else if(indexAmount == 2)
			{
				directBlockNum = inodesTable[k].inode_table[0];
				Arrays.fill(drive, directBlockNum*32, directBlockNum*32+32, (char)-1);
						
				inDirectBlockNum = inodesTable[k].inode_table[1];
				inodesTable[k].inode_table[1] = -1;//usuwamy
				int pom=0;
				while((int)drive[inDirectBlockNum*32+pom] != 65535)
				{
					int from = (int)drive[inDirectBlockNum*32+pom];
					Arrays.fill(drive, from*32, from*32+32, (char)0);
					bitVector[from] = 1;
					++FREE_BLOCK_AMOUNT;
					pom++;
				}
				Arrays.fill(drive, inDirectBlockNum*32, inDirectBlockNum*32+32, (char)0);
				bitVector[inDirectBlockNum] = 1;
				++FREE_BLOCK_AMOUNT;
						
				IndexBlockNumbers.remove(IndexBlockNumbers.indexOf(inDirectBlockNum));
			}
			inodesTable[k].sizeF = 0;
	}
}
