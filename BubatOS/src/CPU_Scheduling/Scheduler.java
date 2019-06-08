package CPU_Scheduling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

import FileSystem.Drive;
import ProcessManagment.Process;
import ProcessManagment.ProcessManagment;
import interpreter.Interpreter;

/* 
 * FUNKCJE DLA INNYCH MODULOW:
 * - ReadyThread(Process)
 * - ReadyThread(Process, boolean IsItSemaphore)
 * */

/*
 * MODU� JESZCZE NIE GOTOWY - TO JEST NAJBARDZIEJ AKTUALNA WERSJA
 * Do zrobienia:
 * - Metoda Go: sekcja wykonywania rozkazow
 * - Watek postojowy: null czy init?
 * - Informowanie planisty o zmianie statusu watku(Waiting,Terminated) - planista generalnie jest przed tym zabezpieczony, ale nie powinien przechowywac w kolejkach procesow niegotowych
 * nawet jesli ich nigdy nie uzyje i skasuje po jakims czasie, bo Pan Doktor moze zechciec zobaczyc co sie znajduje w tych kolejkach
 * Dodatkowe kwestie
 * - czy sa jakies rzeczy do robienia w czasie watku postojowego, rzekomo watek postojowy w Windows sluzy do zarzadzania pamiecia, ale tutaj nie musi
 * - Czy robic: metoda wypisujaca aktualne procesy w kolejkach procesow gotowych??
 * - Czy robic i jak: informacje dla Doktora
 * - Jak wyglada usuwanie watku
 * Informacje:
 * - Jak wlacza sie postojowy watek planista oddaje sterowanie do shella niezaleznie czy kwant czasu sie skonczyl(zapobieganie marnotrawieniu procesora)
 * 
 *  */


public class Scheduler {

	/*
	 * ================================================================================================
	 * POLA KLASY SCHEDULER
	 * ================================================================================================
	 */
	
	
	
	/*
	 * POLA ZAWNETRZNYCH MODU��W
	 */
	
	/* Pole zawierajace wskaznik do interpretera(jesli metody interpretera nie sa statyczne) */
	Interpreter _InterpreterModule;
	
	/* Pole procesu INIT */
	private Process InitProcess;
	
	/*
	 * POLA ZAWNETRZNYCH MODU��W - KONIEC
	 */
	
	/*
	 * STA�E
	 */
	
	/*
	 * InstructionsPerQuantum - Maksymalna ilosc rozkazow do wykonania w kwancie czasu (4 rozkazy)
	 */
	private static final byte InstructionsPerQuantum=4;
	/*
	 * PriorityAmount - Ilosc priorytetow dopuszczalna dla planisty (wliczajac watek postojowy)
	 */
	private static final byte PriorityAmount=8;
	/*
	 * QuantumAmountToBalance - Ilosc kwantow czasu po ktorych procesy powinny przestac byc glodzone
	 */
	private static final byte QuantumAmountToBalance=8;
	/*
	 * QuantumAmountToRunBalanceSetManager - Ilosc kwantow czasu po ktorych planista powinien uruchomic funkcje BalanceSetManager
	 */
	private static final byte QuantumAmountToRunBalanceSetManager=4;
	/*
	 * DefaultQuantumToGive - Domyslna wartosc ilosci kwantow danych dla procesow przed przelaczeniem kontekstu (informacja dla Process Manager)
	 */
	public static final byte DefaultQuantumToGive=2;
	/*
	 * QuantumAmountCounter - Licznik wykonanych kwantow procesora(glownie aby wiedziec kiedy uruchomic funkcje BalanceSetManager)
	 */
	private short QuantumAmountCounter=0;
	
	/* Flaga wywlaszczenia procesu */
	private boolean IsExpropriated=false;
	
	
	
	
	/* Wartosci znaczacych priorytetow nadawane przez konstruktor gdy znana jest liczba priorytetow*/
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL;
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_NORMAL;
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_IDLE;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_NORMAL;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_IDLE;
	public static byte INITPROCESS_THREAD_PRIORITY=0;
	
	
	/*
	 * KiReadySummary - Maska bitowa identyfikujaca niepuste kolejki w tablicy KiDispatcherReadyListHead (przyspieszenie wyszukiwania gotowego procesu na wzor Windows)
	 */
	private boolean[] KiReadySummary;
	/*
	 * KiDispatcherReadyListHead - Kontener przechowujacy kolejki procesow gotowych, tyle kolejek ile mamy priorytetow
	 */
	private Vector<LinkedList<Process>> KiDispatcherReadyListHead;
	
	/*
	 * Running - Publiczne pole udostepniane wszystkim modulom, aby mogly operowac na aktualnie wykonywanym przez procesor procesie
	 */
	public static Process Running=null;
	
	/*
	 * STA�E - KONIEC
	 */
	
	/*
	 * ================================================================================================
	 * POLA KLASY SCHEDULER - KONIEC
	 * ================================================================================================
	 */
	
	
	/*
	 * ================================================================================================
	 * KONSTRUKTOR 
	 * ================================================================================================
	 */
	
	@SuppressWarnings("unused")
	public Scheduler(Process init, Drive mainDrive, ProcessManagment pm) {
		
		
		/* Przypisanie modulu interpretera do pola */
		this._InterpreterModule=new Interpreter(mainDrive, pm, this);
		
		/* Przypisanie do pola procesu INIT */
		this.InitProcess=init;
		
		/* Ustawienie procesu aktualnie wykonywanego na INIT*/
		Scheduler.Running=InitProcess;
		
		
		
		/* Ustawienie wartosci domyslnych priorytetow adekwatinie do ilosci priorytetow */
		REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL=PriorityAmount-1;
		VARIABLE_CLASS_THREAD_PRIORITY_IDLE=1;
		VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL=PriorityAmount/2;
		REALTIME_CLASS_THREAD_PRIORITY_IDLE=(byte) (VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL+1);
		VARIABLE_CLASS_THREAD_PRIORITY_NORMAL=(byte) ((VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL-VARIABLE_CLASS_THREAD_PRIORITY_IDLE)/2+VARIABLE_CLASS_THREAD_PRIORITY_IDLE);
		REALTIME_CLASS_THREAD_PRIORITY_NORMAL=(byte) ((REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL-REALTIME_CLASS_THREAD_PRIORITY_IDLE)/2+REALTIME_CLASS_THREAD_PRIORITY_IDLE);
		///////
		
		/* Ustawienie wartosci poczatkowych na masce bitowej */
		if (PriorityAmount>0) {
			/* Utworzenie maski o ilosci bitow odpowiadajacej ilosci priorytetow */
			KiReadySummary = new boolean[PriorityAmount];
			/* Wyzerowanie bitow na masce*/
			for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
				KiReadySummary[iterator]=false;
			}
			/* Ustawienie wartosci true na bicie watku postojowego, poniewaz jest on zawsze dostepny */
			KiReadySummary[0]=true;
		
		
		
		
	
		/* Utworzenie tablicy kolejek procesow gotowych */
		KiDispatcherReadyListHead=new Vector<LinkedList<Process>>(PriorityAmount);
		KiDispatcherReadyListHead.setSize(PriorityAmount);
		/*for (LinkedList<Process> ListPointer: KiDispatcherReadyListHead) {
			ListPointer=new LinkedList<Process>();
		}*/
		
		/*for(int x = 0;x<PriorityAmount;x++)
		{
			KiDispatcherReadyListHead.set(x, new LinkedList<Process>());
		}*/
			 
		for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=INITPROCESS_THREAD_PRIORITY; iterator--) {
			KiDispatcherReadyListHead.add(iterator, new LinkedList<Process>());
		}
		/* INIT */
		KiDispatcherReadyListHead.elementAt(0).add(this.InitProcess);
		/*KiDispatcherReadyListHead.add(0, );*/
		//ListPointer=new LinkedList<Process>();
		//ListPointer.add(this.InitProcess);
		}
		
	}
	/*
	 * ================================================================================================
	 * KONSTRUKTOR - KONIEC
	 * ================================================================================================
	 */
	
	/*
	 * ================================================================================================
	 * METODY KLASY SCHEDULER
	 * ================================================================================================
	 */
	
	/*
	 * ADDTOREADYLIST
	 * Mala funkcja pomocnicza dodajaca gotowy process na koniec odpowiedniej kolejki procesow gotowych
	 */
	private void AddToReadyList(Process process) {
		/* Zabezpieczenie przed bledem */
		if (process.getState()==Process.processState.Ready) {
		byte PriorityNumber=process.schedulingInformations.getPriorityNumber();
		KiDispatcherReadyListHead.elementAt(PriorityNumber).addLast(process);
		process.schedulingInformations.setSchedulersLastQuantumAmountCounter(QuantumAmountCounter); //Zapisanie informacji, przy ktorym kwancie procesora zostal dodany proces, aby prawidlowo go postarzyc
		System.out.println("Dodano do kolejki o priorytecie: "+PriorityNumber+", ID: "+process.getPID());
		
		/* Zmieniamy stan bitu na masce bitowej na true jesli byla ustawiona na false*/
		if (KiReadySummary[PriorityNumber]==false) {
			KiReadySummary[PriorityNumber]=true;
		}
		
		}
		else {
			/* Potencjalna informacja o bledzie */
		}
	}
	/*
	 * ADDTOREADYLIST - KONIEC
	 */
	
	/*
	 * REMOVEFROMREADYLIST
	 * Metoda usuwajaca proces z kolejki procesow gotowych w skutek:
	 * - zatrzymania procesu
	 * - zmiany priorytetu
	 */
	private void RemoveFromReadyList(Process process) {
		
		/* Pobranie wskaznika do kolejki w ktorej dany proces aktualnie sie znajduje */
		byte PriorityNumber=process.schedulingInformations.getPriorityNumber();
		/* Usuniecie procesu z danej kolejki */
		boolean isRemoved= KiDispatcherReadyListHead.elementAt(PriorityNumber).remove(process);
		
		/* Zmiana stanu bitu na masce bitowej w przypadku gdy kolejka jest pusta*/
		if (KiDispatcherReadyListHead.elementAt(PriorityNumber).isEmpty()) {
			KiReadySummary[PriorityNumber]=false;
			}
		
		/* Jesli przez przypadek przed zadaniem usuniecia zmienilismy procesowi priorytet trzeba go odszukac na liscie i usunac, zabezpieczy to przed bledem */
		if (!isRemoved) {
			for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
				/* Sprawdzenie bitu maski bitowej odpowiadajacej za dany numer priorytetu*/
				if(KiReadySummary[iterator]==true) {
					
					KiDispatcherReadyListHead.elementAt(PriorityNumber).remove(process);
					/* Zmiana stanu bitu na masce bitowej w przypadku gdy kolejka jest pusta*/
					if (KiDispatcherReadyListHead.elementAt(iterator).isEmpty()) {
						KiReadySummary[iterator]=false;
						}
					}
			}
		}
	}
	/*
	 * REMOVEFROMREADYLIST - KONIEC
	 */
	
	/*
	 * INFORMSCHEDULERMODIFIEDSTATE
	 */
	public void InformSchedulerModifiedState(Process process) {
		/* Wyzerowanie informacji o zuzytych kwantach*/
		process.schedulingInformations.setUsedQuantumAmount((byte) 0);
		
		
		/* Wykonanie funkcji pomocniczej usuwajacej proces z listy*/
		this.RemoveFromReadyList(process);
		
		/* Ustawienie aktualnie wykonywanego procesu na NULL */
		if (Scheduler.Running.equals(process)) {
		Scheduler.Running=InitProcess;
		}
	}
	/*
	 * INFORMSCHEDULERMODIFIEDSTATE - KONIEC
	 */
	
	/*
	 * FINDREADYTHREAD
	 * Metoda planisty na wyszukanie watku o najwyzszym priotytecie do wykonania przez procesor
	 */
	private Process FindReadyThread() {
		/* Iteracja po masce bitowej w celu sprawdzenia czy na ktoryms bicie nie ma wartosci true aby mozna bylo pobrac z kolejki danego priorytetu gotowy proces*/
		for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
			/* Sprawdzenie bitu maski bitowej odpowiadajacej za dany numer priorytetu*/
			if(KiReadySummary[iterator]==true) {
				/* Pobranie pierwszego procesu z kolejki*/
				Process foundThread=KiDispatcherReadyListHead.elementAt(iterator).getFirst();
				
				/* Sprawdzenie czy nie wystapil blad
				 * - proces ma inny stan niz gotowy
				 * - proces z jakiegos powodu zostal usuniety(null) ale nadal jest dostepny w kolejce
				 * */
				if (foundThread.getState()==Process.processState.Ready) {
					/* Jesli nie ma bledu przekaz zwroc proces planiscie  */
					return foundThread;
				}
				else {
					/* Jesli proces z jakiegos dziwnego powodu nie ma statusu Ready a nadal jest w kolejce to powinien zostac z niej usuniety*/
					KiDispatcherReadyListHead.elementAt(iterator).removeFirst();
					/* Zmiana stanu bitu na masce bitowej w przypadku gdy kolejka jest pusta*/
					if (KiDispatcherReadyListHead.elementAt(iterator).isEmpty()) {
						KiReadySummary[iterator]=false;
						}
					
					/* Zwiekszenie wartosci iteratora aby poraz kolejny przeszukac ta sama kolejke bez koniecznosci przeszukiwania od nowa poprzednich */
					iterator+=1;
					
				}
			}
		}
		return this.InitProcess;
	}
	/*
	 * FINDREADYTHREAD - KONIEC
	 */
	
	/*
	 * READYTHREAD
	 * Metoda udostepniana innym modulom do poinformowania planisty o stanie gotowosci danego procesu
	 */
	public void ReadyThread(Process process) {
		/* Ustawienie odpowiedniego stanu procesu */
		process.setStan(Process.processState.Ready);
		/*(JESZCZE NIE JESTEM PEWNY) Wyzerowanie uzytych kwantow */
		//process.schedulingInformations.setUsedQuantumAmount((byte) 0);
		/* Dodanie procesu do kolejki procesow gotowych */
		this.AddToReadyList(process);
		/* Sprawdzenie czy proces wywlaszcza proces wlasnie uruchomiony */
		this.IsReadyThreadExpropriating(process);
	}
	/*
	 * READYTHREAD - KONIEC
	 */
	
	/*
	 * READYTHREAD (WERSJA DLA MODULU SEMAFORA)
	 * Metoda udostepniona modulowi semafora do poinformowania planisty o stanie gotowosci danego procesu i podwyzszeniu jego priorytetu ze wzgledu na oczekiwanie
	 */
	public void ReadyThread(Process process, boolean isItSemaphore) {
		/* Ustawienie odpowiedniego stanu procesu */
		process.setStan(Process.processState.Ready);
		/* Wyzerowanie uzytych przez proces kwantow, zabezpieczenie przed bledem, poniewaz dostepna ilosc kwantow zostanie zmniejszona w tej metodzie */
		process.schedulingInformations.setUsedQuantumAmount((byte) 0);
		if (isItSemaphore==true) {
			byte temporaryPriorityNumber=(byte) (process.schedulingInformations.getPriorityNumber()+1);
			/* Tymczasowe podwyzszenie priorytetu oczekujacego(wczesniej) na semafor procesu*/
			process.schedulingInformations.setPriorityNumber(temporaryPriorityNumber);
			byte givenQuantumAmount=process.schedulingInformations.getGivenQuantumAmount();
			/*Ilosc jednostek kwantu czasu procesora jest zmniejszana o jeden jesli to semafor podwyzsza tymczasowo priorytet procesowi
			 *Po wykorzystaniu kwantu ilosc jednostek powroci do stanu domyslnego */
			if (givenQuantumAmount>1){
				process.schedulingInformations.setGivenQuantumAmount((byte)(givenQuantumAmount-1));
			}
		}
		/* Dodanie procesu do kolejki procesow gotowych */
		this.AddToReadyList(process);
		/* Sprawdzenie czy proces wywlaszcza proces wlasnie uruchomiony */
		this.IsReadyThreadExpropriating(process);
	}
	/*
	 * READYTHREAD (WERSJA DLA MODULU SEMAFORA) - KONIEC
	 */
	
	/*
	 * ISREADYTHREADEXPRIOPRIATING
	 * FUNKCJA SPRAWDZAJACA CZY DANY PROCES WYWLASZCZA PROCESOR
	 */
	private void IsReadyThreadExpropriating(Process process) {
		/* Jesli aktualnie jest wykonywany watek postojowy to i tak zostanie wywolana metoda ReadyThread zanim procesor wykona instrukcje, wiec
		 * zaznaczamy flage tylko jesli */
		if (Scheduler.Running!=null) {
			if (process.schedulingInformations.getPriorityNumber()>Scheduler.Running.schedulingInformations.getPriorityNumber()) {
				this.IsExpropriated=true;
			}
		}
		else {
			this.IsExpropriated=true;
		}
	}
	/*
	 * ISREADYTHREADEXPRIOPRIATING - KONIEC
	 */
	
	/*
	 * BALANCESETMANAGER
	 * Funkcja wywolywana co jakis czas przez planiste zapobiegajaca glodzeniu procesow o niskim priorytecie
	 * Jezeli czas oczekiwania na kwant czasu przez dany proces przekroczy dopuszczalna wartosc proces ten uzyskuje "na chwile" najwyzszy priorytet w klasie priorytetow
	 * zmiennych i jego ilosc dostepnych jednostek kwantu czasu wydluza sie dwukrotnie, po ich wykorzystaniu proces z powrotem wraca do swoich wartosci domyslnych
	 */
	private void BalanceSetManager() {
		/* Iterujemy kolejki jedynie w klasie priorytetow zmiennych */
		for (byte iterator=VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
			/* Sprawdzenie bitu maski bitowej odpowiadajacej za dany numer priorytetu*/
			if(KiReadySummary[iterator]==true) {
				/* Iteracja po procesach w danej kolejce w celu znalezienia glodzonych watkow */
				Iterator<Process> it=KiDispatcherReadyListHead.elementAt(iterator).iterator();
				while(it.hasNext()){
					Process process=it.next();
					/* Sprawdzenie czy watek zostal zaglodzony */
					if (this.QuantumAmountCounter-process.schedulingInformations.getSchedulersLastQuantumAmountCounter()>=Scheduler.QuantumAmountToBalance) {
						/* Podwyzszenie priorytetu zaglodzonego watku do najwyzszego mozliwego w klasie priorytetow zmiennych */
						process.schedulingInformations.setPriorityNumber(VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL);
						/* Wydluzenie ilosci jednostek kwantu czasu dwukrotnie (na wzor Windows) */
						byte quantumExtended=(byte) (process.schedulingInformations.getGivenQuantumAmount()*2);
						process.schedulingInformations.setGivenQuantumAmount(quantumExtended);
						/* Po zmianie danych dodanie procesu do odpowiadajacej mu aktualnie kolejki */
						this.AddToReadyList(process);
						/* Usuniecie procesu z kolejki z ktorej pochodzil */
						it.remove();
						}
					}
				}
				
			}
		}
	/*
	 * BALANCESETMANAGER - KONIEC
	 */
	
	
	/*
	 * FUNKCJA GO
	 * WYKONYWANIE KWANTU PROCESORA, GLOWNA FUNKCJA
	 */
	public void Go() throws Exception {
		
		/*Zmienna pomocnicza zliczajaca ilosc wykonanych instrukcji w danym kwancie czasu */
		byte InstructionsExecuted=0;
		
		
		boolean WasExpriopriatedDuringQuantum=false;
		/* Jesli cokolwiek w czasie "przerwy" zostalo dodane to i tak planista to zauwazy dzieki metodzie FindReadyThread
		 * takze flaga musi byc wyzerowana*/
		this.IsExpropriated=false;
		
		/* Sprawdzenie czy juz nie czas odpalic BalanceSetManager do zaglodzonych watkow */
		if (this.QuantumAmountCounter%QuantumAmountToRunBalanceSetManager==0) {
			this.BalanceSetManager();
		}
		
		/* Znalezienie procesu o najwyzszym priorytecie do wykonania*/
		Scheduler.Running=this.FindReadyThread();
		
		System.out.println("The chosen one: "+Scheduler.Running.getPID());
		/* Zmiana stanu na running */
		Scheduler.Running.setStan(Process.processState.Running);
		
		/* Petla wykonujaca rozkazy odpowiadajaca jednemu kwantowi czasu procesora */
		while (InstructionsExecuted<Scheduler.InstructionsPerQuantum) {
			
			/* Zabezpieczenie przed przypadkiem kiedy proces zostal ustawiony na stan Waiting lub Terminated 
			 * (Oddzielna instrukcja warunkowa aby zapobiec bledom sprawdzania stanu procesu obiektu null*/
			if (Scheduler.Running!=this.InitProcess && Scheduler.Running!=null &&  Scheduler.Running.getState()!=Process.processState.Running) {
				System.out.println(1);
				/* Usuniecie z listy procesow gotowych */
				this.RemoveFromReadyList(Scheduler.Running);
				/* Znalezienie procesu o najwyzszym priorytecie do wykonania*/
				Scheduler.Running=this.FindReadyThread();
				/* Zmiana stanu na running */
				Scheduler.Running.setStan(Process.processState.Running);
				
			}
			
			if (Scheduler.Running!=null && Scheduler.Running!=this.InitProcess) {
				/* Zapisanie informacji o stanie licznika procesora w momencie ostatniego przydzialu procesora procesowi(postarzanie/odmladzanie) */
				Scheduler.Running.schedulingInformations.setSchedulersLastQuantumAmountCounter(QuantumAmountCounter);
				
			
			/* Sprawdzenie czy watek ktory otrzymal kwant czasu zostal wywlaszczony */
			if (this.IsExpropriated==true) {
				System.out.println("wywlaszczenie");
				WasExpriopriatedDuringQuantum=true;
				/* Zabezpieczenie przed potencjalnym bledem o niskim prawdopodobienstwie */
				if(Scheduler.Running!=null && Scheduler.Running!=this.InitProcess) {
					/* Aktualizacja danych o procesie wywlaszczonym */
					Scheduler.Running.setStan(Process.processState.Ready);
					//Scheduler.Running.schedulingInformations.setSchedulersLastQuantumAmountCounter(QuantumAmountCounter);
					if(Scheduler.Running.schedulingInformations.getUsedQuantumAmount()>=1) {
						this.RemoveFromReadyList(Scheduler.Running);
						this.AddToReadyList(Scheduler.Running);
					} 
				}
				/* Ustawienie procesu wywlaszczajacego na aktualnie wykonywany */
				Scheduler.Running=this.FindReadyThread();
				/* Zmiana stanu na running */
				Scheduler.Running.setStan(Process.processState.Running);
			}
			else {
				/* Czy w innym wypadku cos trzeba robic? Zapewne nie */
			}
			
			
			/*
			 * ======================================
			 * SEKCJA WYKONYWANIA ROZKAZU
			 * ======================================
			 */
			
			/* POBRANIE OSTATNICH ZAPISANYCH STANOW REJESTRU Z BLOKU KONTROLNEGO PROCESU */
			System.out.println("Aktualnie wykonywany proces: "+Scheduler.Running.getPID());
			
			_InterpreterModule.getRegister(Scheduler.Running.getR1(), Scheduler.Running.getR2(), Scheduler.Running.getProgramCounter());
			
			System.out.println("Wyjscie z interpretera1");
			/* WYKONANIE ROZKAZU W INTERPETERZE*/
			_InterpreterModule.getCommand(Scheduler.Running.getProcessTab().getCommand(Scheduler.Running.getProgramCounter())); 
			System.out.println("Wyjscie z interpretera2");
			
			/* Ustawienie stanow rejestrow po wykonaniu rozkazu  */
			Scheduler.Running.setR1(_InterpreterModule.setRegisterA());
			Scheduler.Running.setR2(_InterpreterModule.setRegisterB());
			Scheduler.Running.setProgramCounter(_InterpreterModule.setCommandCounter());   
			
			
			/* ZAPISANIE INFORMACJI DO BLOKU KONTROLNEGO PROCESU
			 * JESLI PROCES WYKONA OSTATNI ROZKAZ NIE POWINIEN BYC OD RAZU USUNIETY TYLKO DOSTAC STATUS TERMINATED(ABY MOZNA BYLO ODCZYTAC Z NIEGO DANE I GO RECZNIE SKASOWAC)
			 * I POWINIEN MIEC AKTUALNE DANE W REJESTRZE PO OSTATNIM ROZKAZIE*/
			
			/*
			 * ======================================
			 * SEKCJA WYKONYWANIA ROZKAZU - KONIEC
			 * ======================================
			 */
			
			
			}
			else {
				System.out.println(3);
				/* Proba znalezienia procesu(na wypadek jakby proces dotychczas wykonywany sie zakonczyl i watek ustawiony bylby na null) */
				Scheduler.Running=this.FindReadyThread();
				if (Scheduler.Running!=null && Scheduler.Running!=this.InitProcess) {
				/* Obnizenie licznika wykonywanych instrukcji, ktory zostanie podbity na koniec petli */
				InstructionsExecuted--;
				/* Zmiana stanu na running */
				Scheduler.Running.setStan(Process.processState.Running);
				/* Ustawienie flagi "niepelnego kwantu" */
				WasExpriopriatedDuringQuantum=true;
				}
				else {
					/* Petla jest przerywana, aby nie marnowac czasu procesora */
				break;
				}
			}
			
			/* Podbicie licznika wykonanych instrukcji */
			InstructionsExecuted++;
			System.out.println(InstructionsExecuted);
		}
		
		/*Sprawdzenie czy nie jest ustawiony watek postojowy, zabezpieczenie przed bledem */
		if (Scheduler.Running!=null && Scheduler.Running!=this.InitProcess) {
			System.out.println(31);
		/* Po uplynieciu kwantu czasu ustawiamy stan procesu ktory wykonal ostatni rozkaz na stan Ready pod warunkiem ze wczesniej jego stan nie zostal zmieniony(np. na Waiting) */
		if (Scheduler.Running.getState()==Process.processState.Running){
			System.out.println(32);
		Scheduler.Running.setStan(Process.processState.Ready);
		}
		else {
			System.out.println(33);
			/* Jezeli proces po wykorzystaniu swojego kwantu czasu zmienil stan (np. na Waiting lub Terminated) powinien zostac usuniety z kolejki procesowy gotowych*/
			this.RemoveFromReadyList(Scheduler.Running);
		}
		
		/* Sprawdzenie czy w trakcie kwantu procesora nastapilo wywlaszczenie, bo jesli bylo to zaden z procesow uczestniczacych nie wyczerpal pelnego kwantu czasu i przysluguje mu nadal pelny */
		if (WasExpriopriatedDuringQuantum==false) {
			System.out.println(4);
			/* Po wykorzystaniu kwantu przez proces aktualizacja informacji w jego procesie kontrolnym */
			Scheduler.Running.schedulingInformations.setSchedulersLastQuantumAmountCounter(QuantumAmountCounter);
			Scheduler.Running.schedulingInformations.setUsedQuantumAmount((byte) (Scheduler.Running.schedulingInformations.getUsedQuantumAmount()+1));
			
			/* Jesli proces wykorzystal ilosc jednostek kwantow czasu nalezy powziac odpowiednie kroki*/
			if (Scheduler.Running.schedulingInformations.getUsedQuantumAmount()==Scheduler.Running.schedulingInformations.getGivenQuantumAmount()) {
				System.out.println(5);
				/* Usuniecie procesu z poczatku kolejki*/
				this.RemoveFromReadyList(Scheduler.Running);
				
				/* Wyzerowanie ilosci uzytych jednostek kwantow czasu*/
				Scheduler.Running.schedulingInformations.setUsedQuantumAmount((byte) 0);
				/* Jesli tymczasowo podwyzszono lub zmniejszono ilosc danych jednostek kwantow czasu dla danego procesu, po ich wykorzystaniu nalezy wrocic do wartosci domyslnych*/
				if (Scheduler.Running.schedulingInformations.getGivenQuantumAmount()!=Scheduler.Running.schedulingInformations.getDefaultGivenQuantumAmount()) {
				Scheduler.Running.schedulingInformations.setGivenQuantumAmount(Scheduler.Running.schedulingInformations.getDefaultGivenQuantumAmount());
				}
				/* Jesli tymczasowo podwyzszono priorytet procesu i proces wykorzystal swoj kwant czasu powinien wrocic do priorytetu bazowego*/
				if (Scheduler.Running.schedulingInformations.getPriorityNumber()!=Scheduler.Running.schedulingInformations.getDefaultPriorityNumber()) {
					Scheduler.Running.schedulingInformations.setPriorityNumber(Scheduler.Running.schedulingInformations.getDefaultPriorityNumber());
				}
				
				/* Zebezpieczenie przed bledem dodania do kolejki procesu o innym stanie niz Ready */
				if (Scheduler.Running.getState()==Process.processState.Ready) {
				/* Dodanie procesu na koniec kolejki o odpowiednim priorytecie */
				this.AddToReadyList(Scheduler.Running);
				}
			}
			
		}
		}
	
		/* Po wykonaniu kwantu czasu uruchomienie watku postojowego */
		Scheduler.Running=this.InitProcess;
		/* Zwiekszenie wartosci licznika wykonanych kwantow przez procesor */
		QuantumAmountCounter++;
	}
	/*
	 * FUNKCJA GO - KONIEC
	 */
	
	
	/*
	 * ================================================================================================
	 * METODY KLASY SCHEDULER - KONIEC
	 * ================================================================================================
	 */
	}
