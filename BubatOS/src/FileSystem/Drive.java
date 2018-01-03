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
	public Inode	[]  inodesTable;
	public Hashtable<String,FileEntry> catalog;
	
	/*KONTENER POMOCNICZY*/
	private ArrayList<Integer> IndexBlockNumbers;
	/*--Constructor--*/
	public Drive(){
		drive = new char[DRIVE_SIZE];
		bitVector = new int[DRIVE_BLOCK_AMOUNT];
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
	public void createFile(String name) throws FileException, OutOfMemoryException{
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
			
			System.out.println("Created!");
		}
		else if(catalog.containsKey(name)){
			//System.out.println("Istnieje juz plik o takiej nazwie");
			throw new FileException("Istnieje juz plik o takiej nazwie");
		}
		else if(freeBlock == -1){
			//System.out.println("Wszystkie bloki są zajęte");
			throw new OutOfMemoryException("Wszystkie bloki są zajęte");
		}
	}
	public void openFile(String name) throws FileException, InterruptedException{
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
				//System.out.println("Plik jest już otwarty");
				throw new FileException("Plik jest już otwarty");
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
		else{
			//System.out.println("Plik o takiej nazwie nie istnieje");
			throw new FileException("Plik o takiej nazwie nie istnieje");
		}
		//return -2; //brak pliku o takiej nazwie
	}
	public void closeFile(String name) throws InterruptedException, FileException{
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
				//System.out.println("Plik jest już zamknięty");
				throw new FileException("Plik jest już zamknięty");
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
		else{
			//System.out.println("Plik o takiej nazwie nie istnieje");
			throw new FileException("Plik o takiej nazwie nie istnieje");
		}
		//return -2;
	}
	//DZIAŁA LEGITNIE, ALE CZY CHODZI O TAKI SPOSÓB???
	//ustalić czy podawać miejsce, od októrego mamy wpisaywać i ile
	public void writeFile(String name, String data) throws OutOfMemoryException, FileException{
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
					//System.out.println("Bład, brak miejsca na dysku");
					throw new OutOfMemoryException("Bład, brak miejsca na dysku");
				}
			}
			else
			{
				//System.out.println("Plik nie jest otwarty");
				throw new FileException("Plik nie jest otwarty");
			}
		}
		else
		{
			//System.out.println("Nie istnieje plik o takiej nazwie");
			throw new FileException("Nie istnieje plik o takiej nazwie");
		}
	}
	public void appendFile(String name, String newData) throws OutOfMemoryException, FileException{
		//wypada ustawić wskaznik na koncu pliku
		if(catalog.containsKey(name))
		{
			FileEntry F = catalog.get(name);
			int k = F.inodeNum;
			if(inodesTable[k].s.isStan() == false || inodesTable[k].stan == true)
			{
				boolean flaga=false; //czy jest miejsce
				
				int acDataSize = inodesTable[k].sizeF;
				int newDataSize = newData.length();
				
				int totalSize = acDataSize + newDataSize;
				int directBlockNum;
				if(totalSize <= 32)
				{
					flaga=true;
					int in=0;
					directBlockNum = inodesTable[k].inode_table[0];
					for(int i=acDataSize;in<newData.length();in++,i++)
					{
						drive[directBlockNum*32+i] = newData.charAt(in);
					}
					inodesTable[k].sizeF += newData.length();
				}
				//plus jeden, bo  
				else if(acDataSize < 32 && ((newDataSize-(32-acDataSize)+32-1)/32)+1 <= FREE_BLOCK_AMOUNT)//+1
				{
					flaga=true;
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
					//}
				}
				else
				{
					/***************/
					int restSize = acDataSize - 32;
					int indexBlockAmount = (restSize+32-1)/32;//liczba wpisów w bloku indeksowym
					//System.out.println("indexBlockAmount "+indexBlockAmount);
					int inDirectBlockNum3 = inodesTable[k].inode_table[1];
					int x = (32*indexBlockAmount) - restSize; //ilosc wolnych wpisow w bloku dyskowym
					if((((newData.length()-x)+32-1)/32) <= FREE_BLOCK_AMOUNT)
					{
						flaga=true;
						if(newData.length() > x)
						{
							//obliczamy ile przeznaczyc blokow dyskowych na append
							int adDriveBlocksAmount = ((newData.length()-x)+32-1)/32;
							//System.out.println("adDriveBlocksAmount "+adDriveBlocksAmount);
							
							//zapis do wolnej przestrzeni dostepnego bloku dyskowego
							int saveFrom = (int)drive[inDirectBlockNum3*32+indexBlockAmount-1];//-1, bo zapisane od zerowego indeksu
							//System.out.println("saveFrom "+saveFrom);
							int in=0;
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
					//System.out.println("Bład, brak miejsca na dysku");
					throw new OutOfMemoryException("Bład, brak miejsca na dysku");
				}
			}
			else
			{
				//System.out.println("Plik nie jest otwarty");
				throw new FileException("Plik nie jest otwarty");
			}
		}
		else
		{
			//System.out.println("Nie istnieje plik o takiej nazwie");
			throw new FileException("Nie istnieje plik o takiej nazwie");
		}
	}
	public String readFile(String name, int amount) throws FileException{
		//marcin
		//wywołanie operacji open()
		//ustawienie wskaznika na poczatku pliku
		
		//czytanie sekwencyjne
		//podczas odczytania wskaznik wedruje na koniec i określa nowa operacje wejscia-wyjscia
		//system przechowuje wskaznik czytania okreslajacy miejsce następnego czytania w pliku
		
		//podamy ile plik ma zawartosci
		//użytkownik nie zawsze czyta całą zawartość
		//wskaznik bieżącej pozycji będzie potrzebny, gdy będzie chciał wznowić czytanie.
		//int currentPositionPtr;
		
		 String zwrot = "";
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
					for(int i=F.currentPositionPtr;i<(amount>(s-F.currentPositionPtr)?s:amount+F.currentPositionPtr);i++)
					{
						content += drive[directBlockNum*32+i];
					}
					if(amount+F.currentPositionPtr >=s ) //na końcu pliku
						F.currentPositionPtr = s;
					else	
						F.currentPositionPtr += amount;
				}
				else
				{
					int directBlockNum = inodesTable[k].inode_table[0];
					int inDirectBlockNum = inodesTable[k].inode_table[1];
					int pom = amount;
					int in=F.currentPositionPtr;
					int in_fin = in;
					if(in<32)
					{
						for(;in<(amount>(32-F.currentPositionPtr)?32:amount+F.currentPositionPtr);in++)
						{
							content += drive[directBlockNum*32+in];
							--pom;
						}
					}
					//wpisanie nr bloku dyskowego do bloku indeksowego
					//tyle razy 
					//int n = (a + b - 1) / b; -->ceiling
					if(pom>(32-in) && in < s)
					{
						int restSize = pom;
						int pom2;
						if(restSize > s-32)
						{
							pom=s-32;
							restSize = pom;
						}
						pom2 = pom;
						int j =(((in)/32)-1);
						int z=j;
						
						int firstLoop = (in-32*(j+1));
						int i;
						
						int condition=(restSize>s-in?(in+s-in)/32:(in+restSize)/32);
						int difference = condition - j;
						for(;j<condition;j++)
						{
							int which = (int)drive[inDirectBlockNum*32+j];
							for(i=firstLoop;i<(difference>1?32:(pom>=(s-in)?(firstLoop+(s-in)):(firstLoop+pom)));i++)
							{
								content += drive[which*32+i];
								--pom2;
							}
							if(firstLoop == 0)
								in+=i;
							else
								in+=(i-firstLoop);
							
							difference--;
							firstLoop=0;
							pom=pom2;
						}
						
					}
					if(amount+in_fin >= s)
						F.currentPositionPtr = s;
					else
						F.currentPositionPtr += amount;
				}
				System.out.println("content: "+content);
				zwrot+=content+"\n";
			}
			else
			{
				//adam
				//System.out.println("Plik nie jest otwarty");
				//zwrot+="Plik nie jest otwarty"+"\n";
				throw new FileException("Plik nie jest otwarty");
			}
		}
		else
		{
			//adam
			//System.out.println("Nie istnieje plik o takiej nazwie");
			//zwrot+="Nie istnieje plik o takiej nazwie";
			throw new FileException("Nie istnieje plik o takiej nazwie");
		}
		return zwrot;
	}
	public void deleteFile(String name) throws FileException{
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
					
					if(indexAmount == 1)
					{
						directBlockNum = inodesTable[k].inode_table[0];
						Arrays.fill(drive, directBlockNum*32, directBlockNum*32+32, (char)0);		
						bitVector[directBlockNum] = 1;
						++FREE_BLOCK_AMOUNT;
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
				//System.out.println("Plik jest wykorzystywany! Nie można go teraz usunąć");
				throw new FileException("Plik jest wykorzystywany! Nie można go teraz usunąć");
			}
		}
		else
		{
			//System.out.println("Plik o tej nazwie nie istnieje");
			throw new FileException("Plik o tej nazwie nie istnieje");
		}
	}
	public void renameFile(String name, String newName) throws FileException{
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
					//System.out.println("Plik jest wykorzystywany! Nie można zmienić nazwy");
					throw new FileException("Plik jest wykorzystywany! Nie można zmienić nazwy");
				}
			}
			else
			{
				//System.out.println("Nowa nazwa już występuje");
				throw new FileException("Nowa nazwa już występuje");
			}
		}
		else{
			//System.out.println("Plik o tej nazwie nie istnieje");
			throw new FileException("Plik o tej nazwie nie istnieje");
		}
	}
	public void createLink(String newName, String name) throws FileException{
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
				//System.out.println("Plik o tej nazwie już istnieje. Nie można utworzyć dowiązania!");
				throw new FileException("Plik o tej nazwie już istnieje. Nie można utworzyć dowiązania!");
			}
		}
		else
		{
			//System.out.println("Plik o tej nazwie nie istnieje");
			throw new FileException("Plik o tej nazwie nie istnieje");
		}
	}
	/*FUNCKCJE KATALOGU*/
	private String timView(int t){
		if (t >= 10)
			return Integer.toString(t);
		else
			return "0"+Integer.toString(t);
	}
	//wypisz zawartość katalogu
	public String ListDirectory(){
		String zwrot = "";
		System.out.println("Directory of root: ");
		
		zwrot += "Directory of root: ";
		
		for(Map.Entry<String, FileEntry> entry : catalog.entrySet()){
			FileEntry F = entry.getValue();
			int k = F.inodeNum;
			System.out.print(inodesTable[k].month+" "+timView(inodesTable[k].day));
			System.out.print(" "+timView(inodesTable[k].hour)+":"+timView(inodesTable[k].minute));
			System.out.print(" "+inodesTable[k].sizeF+"B");
			System.out.print(" "+entry.getKey());
			System.out.print(" "+F.type_of_file);
			System.out.println();
			
			zwrot+=inodesTable[k].month+" "+timView(inodesTable[k].day);
            zwrot+=" "+timView(inodesTable[k].hour)+":"+timView(inodesTable[k].minute);
            zwrot+=" "+inodesTable[k].sizeF+"B";
            zwrot+=" "+entry.getKey();
            zwrot+=" "+F.type_of_file;
            zwrot+="\n";
		}
		return zwrot;
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
	public String printDrive(){
		String zwrot = "";
		
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
				if(drive[i] >=0 && drive[i] <= 32){
					System.out.println("*["+i+"]="+(int)drive[i]);
					zwrot+="*["+i+"]="+(int)drive[i]+"\n";
				}
				else{
					System.out.println("*["+i+"]="+drive[i]);
					 zwrot+="*["+i+"]="+drive[i]+"\n";
				}
			}
			else{
				System.out.println("["+i+"]="+drive[i]);
				zwrot+="["+i+"]="+drive[i]+"\n";
			}
		}
		return zwrot;
	}
	public String printDiskBlock(int nr){
		String zwrot = "";
		boolean check=false;
		System.out.println("Blok dyskowy nr: "+nr);
		zwrot+="Blok dyskowy nr: "+nr+"\n";
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
					if(drive[i] >=0 && drive[i] <= 32){
						System.out.println("*["+i+"]="+(int)drive[i]);
						zwrot+="*["+i+"]="+(int)drive[i]+"\n";
					}
					else{
						System.out.println("*["+i+"]="+drive[i]);
						zwrot+="*["+i+"]="+drive[i]+"\n";
					}
				}
				else{
					System.out.println("["+i+"]="+drive[i]);
					zwrot+="["+i+"]="+drive[i]+"\n";
				}
			}
		}
		else
		{
			//adam
			System.out.println("Numer poza zakresem");
			zwrot+="Numer poza zakresem"+"\n";
		}
		return zwrot;
	}
	public String printIndexBlockNumbers(){
		String zwrot = "";
		for(Integer e : IndexBlockNumbers){
			System.out.print(e +", ");
			zwrot+=e +", ";
		}
		System.out.println();
		zwrot+="\n";
		return zwrot;
	}
	public String printInodeInfo(String name) throws FileException{
		String zwrot = "";
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
			
			zwrot+=name + " I-NODE INFO:"+"\n";
            zwrot+=">"+inodesTable[k].month+" "+timView(inodesTable[k].day);
            zwrot+=" "+timView(inodesTable[k].hour)+":"+timView(inodesTable[k].minute);
            zwrot+=" "+inodesTable[k].sizeF+"B";
            zwrot+=" "+name;
            zwrot+=">I-node nr: "+k+"\n";
            zwrot+=">LinkCounter: "+inodesTable[k].LinkCounter+"\n";
            zwrot+=">I-node table:\n >[0]->blok dyskowy: "+inodesTable[k].inode_table[0]+"\n";
            zwrot+=" >[1]->blok indeksowy: "+
                    (
                            inodesTable[k].inode_table[1] == -1?"brak"
                            :
                            inodesTable[k].inode_table[1]
                    )+"\n";
		}
		else
		{
			//adam
			//System.out.println("Nie ma pliku o podanej nazwie");
			//zwrot+="Nie ma pliku o podanej nazwie"+"\n";
			throw new FileException("Nie ma pliku o podanej nazwie");
		}
		return zwrot;
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
