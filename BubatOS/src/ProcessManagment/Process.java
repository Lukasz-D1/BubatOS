package ProcessManagment;

import java.util.List;
import java.io.IOException;
import java.util.LinkedList;
import CPU_Scheduling.Scheduler;
import Memory_PC.PageTab;
import FileSystem.Inode;


/* 
 * KWESTIA DO OMOWIENIA:
 * - dziedziczenie priorytetu od procesu macierzystego
 * */

public class Process {
	/*
	Blok kontrolny procesu - PCB
	*/
	
	// Nazwa procesu.
	private String processName; 									
	// ID procesu.
	private int PID; 													
	// ID rodzica procesu.
	private int PPID = 0; 													
	// Lista dzieci procesu.
	List<Process> processChildrenList = new LinkedList<Process>(); 		
	// Dost�pne stany - Nowy, Dzia�aj�cy, Oczekuj�cy, Gotowy, Zako�czony.
	public enum processState {
		New, Running, Waiting, Ready, Terminated						
	}; 
	// Pole odpowiedzialne za przechowywanie aktualnego stanu procesu.
	public processState state; 											
	// Licznik proces�w, kt�ry pomaga w nadawaniu ID.
	private static int processCounter = 0; 								

	// Rejestry.
	private int r1, r2, programCounter; 								
	
	// Tablica stronic.
	public PageTab processTab;											
	// Nazwa pliku z danymi programu - niedopowiedziane przez modu�y.
	private String fileName;			
	
	// Rozmiar pliku.
	private int sizeOfFile;

	// Lista iW�z��w na kt�rych operuje proces.
	
	List<Inode> fileList = new LinkedList<Inode>(); 
	
	// Konstruktor domy�lny.
	public Process() {

	}

	// Konstruktor w�a�ciwy. Przyjmowane parametry: nazwa procesu, ID rodzica (potrzebne do struktury hierarchicznej).
	// Dodatkowo parametr albo pami��, albo nazwa pliku.
	public Process(String name, int sizeOfFile, String fileName) throws IOException {
		
		// Nadawanie ID z pomoc� licznika proces�w.
		this.PID = processCounter;
		processCounter++;
		
		// Nadawanie nazwy.
		this.processName = name;
		this.fileName = fileName;
		this.sizeOfFile = sizeOfFile;
		//this.processTab = new PageTab(fileName, sizeOfFile);
		this.state = processState.New;
 
		this.r1 = 0;
		this.r2 = 0;
		this.programCounter = 0;

		/*
		 * Nadanie wartosci domyslnych polu odpowiadajacemu za przechowywanie
		 * informacji potrzebnych planiscie
		 */
		this.schedulingInformations = new Process.SchedulingInfo();

		System.out.println("Utworzono proces o nazwie: " + processName + ", i ID numer: " + PID);
	}
	
	public Process(Process parent) throws IOException {
		// Nadawanie ID z pomoc� licznika proces�w.
		this.PID = processCounter;
		processCounter++;
		
		// Nadawanie nazwy.
		this.processName = parent.processName;
		this.fileName = parent.fileName;
		this.sizeOfFile = parent.sizeOfFile;
		this.processTab = new PageTab(fileName, sizeOfFile);
		this.state = processState.New;
 
		this.r1 = 0;
		this.r2 = 0;
		this.programCounter = 0;

		/*
		 * Nadanie wartosci domyslnych polu odpowiadajacemu za przechowywanie
		 * informacji potrzebnych planiscie
		 */
		this.schedulingInformations = new Process.SchedulingInfo(parent);

		System.out.println("Utworzono proces o nazwie: " + processName + ", i ID numer: " + PID);

	}

	// Zwr�� nazw� procesu.
	public String getProcessName() {
		return processName;
	}

	// Ustaw nazw� procesu.
	public void setProcessName(String processName) {
		System.out.println("Poprzednia nazwa procesu: " + this.processName);
		this.processName = processName;
		System.out.println("Aktualna nazwa procesu: " + this.processName);
	}
	
	// Zwr�� ID procesu.
	public int getPID() {
		return PID;
	}
	
	// Ustaw nowe ID procesu.
	public void setPID(int ID){
		System.out.println("Poprzednie ID procesu: " + this.PID);
		this.PID = ID;
		System.out.println("Aktualne ID procesu: " + this.PID);
	}
	
	// Zwr�c ID rodzica.
	public int getPPID() {
		return PPID;
	}
	
	// Ustaw nowe ID procesu.
	public void setPPID(int parentID){
		System.out.println("Poprzednie ID rodzica: " + this.PPID);
		this.PPID = parentID;
		System.out.println("Aktualne ID rodzica: " + this.PPID);
	}

	// Zwr�� licznik proces�w.
	public static int getProcessCounter() {
		return processCounter;
	}

	// Zwr�� rejestr odpowiedzialny za licznik rozkaz�w.
	public int getProgramCounter() {
		return programCounter;
	}

	// Zwr�� rejestr A.
	public int getR1() {
		return r1;
	}

	
	// Zwr�� rejestr B.
	public int getR2() {
		return r2;
	}
	
	// Ustaw rejestr A.
	public void setR1(int r1) {
		System.out.println("Poprzedni stan rejestru A: " + this.r1);
		this.r1 = r1;
		System.out.println("Aktualny stan rejestru A: " + this.r1);
	}

	// Ustaw rejestr B.
	public void setR2(int r2) {
		System.out.println("Poprzedni stan rejestru B: " + this.r2);
		this.r2 = r2;
		System.out.println("Aktualny stan rejestru B: " + this.r2);
	}

	// Zwr�� aktualny stan procesu.
	public processState getState() {
		return state;
	}

	// Ustaw nowy stan procesu.
	public void setStan(processState state) {
		this.state = state;
	}
	
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}
	
	// Wypisz informacje o procesie.
	public String printProcess(){
		//marcin z void na String
		System.out.println("Proces o nazwie: " + this.getProcessName() + " ; ID: " + this.getPID() + " ; ID rodzica: " + this.getPPID() + " ; o stanie: " + this.getState());
		char[] mem = this.processTab.getProcessMemory();
		
		String pam = "Zawartosc pamieci: \n";
		for(char pp1 : mem)
		{
			pam+=pp1;
		}
		
		return "Proces o nazwie: " + this.getProcessName() + "\n ID: " + this.getPID() + " \n ID rodzica: " + this.getPPID() + " \n o stanie: " + this.getState() + "o rejestrach R1,R2, program counter: " + this.getR1() + ", " + this.getR2() + ", " + this.getProgramCounter() + " \n rozmiarze w pamieci: " + this.getSizeOfFile() + " \n opcjonalnej sciezce do pliku: " + this.getFileName()+"\n"+pam;
	}
	
	// Wypisz list� proces�w potomnych.
	public void showProcessChildrenList(){
		for(Process pro : this.processChildrenList){
			pro.printProcess();
		}
	}
	
		
	/*
	 * public void setBase(int base) { this.base = base; }
	 * 
	 * public void setLimit(int limit) { this.limit = limit; }
	 */


	/*
	 * public void setProcessPriority(int processPriority) {
	 * this.processPriority = processPriority; }
	 */
	
	/*
	 * public int getProcessPriority() { return processPriority; }
	 */
	
	
	public void setProcessTab(PageTab processTab) {
		this.processTab = processTab;
	}
	
	public PageTab getProcessTab() {
		return processTab;
	}
	
	public void setSizeOfFile(int sizeOfFile) {
		this.sizeOfFile = sizeOfFile;
	}
	
	public int getSizeOfFile() {
		return sizeOfFile;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	/*
	 * 
	 * Semafory tutaj
	 * 
	 */
	
	public void addToInodeList(Inode file){
		fileList.add(file);
	}
	
	
	/*
	 * ================================================================================================
	 * SEKCJA INFORMACJI NA POTRZEBY PLANOWANIA PRZYDZIALU PROCESORA
	 * ================================================================================================
	 */

	/* Pole podklasy SchedulingInfo przechowujace informacje dla planisty */
	public SchedulingInfo schedulingInformations;

	/*
	 * PODKLASA SCHEDULINGINFO
	 */
	public class SchedulingInfo {

		/* Aktualnie uzywany numer priorytetu */
		private byte PriorityNumber;
		/* Priorytet bazowy */
		private byte DefaultPriorityNumber;
		/* Domyslna ilosc przydzielonych jednostek kwantow czasu */
		private byte DefaultGivenQuantumAmount;
		/* Aktualnie przydzielona ilosc jednostek kwantu czasu */
		private byte GivenQuantumAmount;
		/* Ilosc zuzytych jednostek kwantow czasu (zeruje sie po osiagnieciu wartosci GivenQuantumAmount) */
		private byte UsedQuantumAmount;
		/* Wartosc licznika procesora w ktorym proces ostatni raz otrzymal procesor */
		private short SchedulersLastQuantumAmountCounter;

		/*
		 * KONSTRUKTOR 
		 */
		public SchedulingInfo() {
			/* !!!DZIEDZICZENIE!!! */
			
			/* Ustawienie domyslnej wartosci priorytetu 
			 * (nie uzywamy klasy priorytetow czasu rzeczywistego wiec domyslnie jest ustawiony priorytet normalny klasy priorytetow dynamicznych */
		this.DefaultPriorityNumber = Scheduler.VARIABLE_CLASS_THREAD_PRIORITY_NORMAL;
			/* Ustawienie aktualnego priorytetu na wzor wartosci domyslnej */
			this.PriorityNumber = this.DefaultPriorityNumber;
			/* Ustawienie domyslnej wartosci przydzielonych kwantow czasu */
		this.DefaultGivenQuantumAmount = Scheduler.DefaultQuantumToGive;
			/* Ustawienie aktualnej ilosci przydzielonych kwantow czasu na wzor wartosci domyslnej */
			this.GivenQuantumAmount = this.DefaultGivenQuantumAmount;
			/* Ustawienie ilosci wykorzystanych kwantow czasu procseroa na zero */
			this.UsedQuantumAmount = 0;
			
		}
		/*
		 * KONSTRUKTOR - KONIEC
		 */ 
		
		/*
		 * KONSTRUKTOR DZIEDZICZ�CY
		 */ 
		public SchedulingInfo(Process parent) {
			
			/* Ustawienie domyslnej wartosci priorytetu 
			 * (nie uzywamy klasy priorytetow czasu rzeczywistego wiec domyslnie jest ustawiony priorytet normalny klasy priorytetow dynamicznych */
		this.DefaultPriorityNumber = parent.schedulingInformations.getDefaultPriorityNumber();
			/* Ustawienie aktualnego priorytetu na wzor wartosci domyslnej */
			this.PriorityNumber = parent.schedulingInformations.getDefaultPriorityNumber();
			/* Ustawienie domyslnej wartosci przydzielonych kwantow czasu */
		this.DefaultGivenQuantumAmount = parent.schedulingInformations.getDefaultGivenQuantumAmount();
			/* Ustawienie aktualnej ilosci przydzielonych kwantow czasu na wzor wartosci domyslnej */
			this.GivenQuantumAmount = parent.schedulingInformations.GivenQuantumAmount;
			/* Ustawienie ilosci wykorzystanych kwantow czasu procseroa na zero */
			this.UsedQuantumAmount = 0;
		}
		/*
		 * KONSTRUKTOR DZIEDZICZ�CY - KONIEC
		 */ 

		/*
		 * SETTERY I GETTERY
		 */
		public byte getDefaultGivenQuantumAmount() {
			return DefaultGivenQuantumAmount;
		}

		public short getSchedulersLastQuantumAmountCounter() {
			return SchedulersLastQuantumAmountCounter;
		}

		public void setSchedulersLastQuantumAmountCounter(short schedulersLastQuantumAmountCounter) {
			SchedulersLastQuantumAmountCounter = schedulersLastQuantumAmountCounter;
		}

		public byte getDefaultPriorityNumber() {
			return DefaultPriorityNumber;
		}

		public byte getPriorityNumber() {
			return PriorityNumber;
		}

		public void setPriorityNumber(byte priorityNumber) {
			PriorityNumber = priorityNumber;
		}

		public byte getGivenQuantumAmount() {
			return GivenQuantumAmount;
		}

		public void setGivenQuantumAmount(byte givenQuantumAmount) {
			GivenQuantumAmount = givenQuantumAmount;
		}

		public byte getUsedQuantumAmount() {
			return UsedQuantumAmount;
		}

		public void setUsedQuantumAmount(byte usedQuantumAmount) {
			UsedQuantumAmount = usedQuantumAmount;
		}
		
		public void setDefaultPriorityNumber(byte defaultPriorityNumber) {
			DefaultPriorityNumber = defaultPriorityNumber;
		}

		public void setDefaultGivenQuantumAmount(byte defaultGivenQuantumAmount) {
			DefaultGivenQuantumAmount = defaultGivenQuantumAmount;
		}
		/*
		 * SETTERY I GETTERY - KONIEC
		 */

	}
	/*
	 * PODKLASA SCHEDULINGINFO - KONIEC
	 */
	/*
	 * ================================================================================================
	 * SEKCJA INFORMACJI NA POTRZEBY PLANOWANIA PRZYDZIALU PROCESORA - KONIEC
	 * ================================================================================================
	 */

}