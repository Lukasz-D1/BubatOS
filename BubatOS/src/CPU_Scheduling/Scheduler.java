package CPU_Scheduling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.function.Predicate;

import ProcessManagment.Process;



/*
 * - W deskryptorze procesu pola:
 * private byte PriorityNumber;
 * 	private byte DefaultGivenQuantumAmount;
	private byte GivenQuantumAmount;
	private byte UsedQuantumAmount;
	private short AwaitingQuantumLength;
 *  */


public class Scheduler {

	/*
	 * ================================================================================================
	 * POLA KLASY SCHEDULER
	 * ================================================================================================
	 */
	
	
	/*
	 * STAŁE
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
	
	/*
	 * KOMENTARZ
	 */
	private boolean IsExpropriated=false;
	
	
	/* Wartosci znaczacych priorytetow nadawane przez konstruktor gdy znana jest liczba priorytetow*/
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL;
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_NORMAL;
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_IDLE;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_NORMAL;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_IDLE;
	
	
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
	public Scheduler() {
		//Ustawienie wartosci domyslnych priorytetow adekwatinie do ilosci priorytetow
		REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL=PriorityAmount-1;
		VARIABLE_CLASS_THREAD_PRIORITY_IDLE=1;
		VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL=PriorityAmount/2;
		REALTIME_CLASS_THREAD_PRIORITY_IDLE=(byte) (VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL+1);
		VARIABLE_CLASS_THREAD_PRIORITY_NORMAL=(byte) ((VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL-VARIABLE_CLASS_THREAD_PRIORITY_IDLE)/2+VARIABLE_CLASS_THREAD_PRIORITY_IDLE);
		REALTIME_CLASS_THREAD_PRIORITY_NORMAL=(byte) ((REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL-REALTIME_CLASS_THREAD_PRIORITY_IDLE)/2+REALTIME_CLASS_THREAD_PRIORITY_IDLE);
		///////
		
		//////////////////////
		//Ustawienie maski bitowej
		//Zabezpieczenie przed niemozliwym bledem
		if (PriorityAmount>0) {
		KiReadySummary = new boolean[PriorityAmount];
		for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
			KiReadySummary[iterator]=false;
		}
		KiReadySummary[0]=true;
		}
		//////////////////
		
		
		//Ustawienie kolejki procesow gotowych
		
		KiDispatcherReadyListHead=new Vector<LinkedList<Process>>(PriorityAmount);
		for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
			
			LinkedList<Process> nullList=KiDispatcherReadyListHead.get(iterator);
			nullList=new LinkedList<Process>();
		}
		
		
		//////////////////////
		
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
	void AddToReadyList(Process process) {
		/* Zabezpieczenie przed bledem */
		if (process.getState()==Process.processState.Ready) {
		byte PriorityNumber=process.schedulingInformations.getPriorityNumber();
		KiDispatcherReadyListHead.get(PriorityNumber).addLast(process);
		
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
		
		byte PriorityNumber=process.schedulingInformations.getPriorityNumber();
		boolean isRemoved= KiDispatcherReadyListHead.get(PriorityNumber).remove(process);
		
		/* Zmiana stanu bitu na masce bitowej w przypadku gdy kolejka jest pusta*/
		if (KiDispatcherReadyListHead.get(PriorityNumber).isEmpty()) {
			KiReadySummary[PriorityNumber]=false;
			}
		
		/* Jesli przez przypadek przed zadaniem usuniecia zmienilismy procesowi priorytet trzeba go odszukac na liscie i usunac, zabezpieczy to przed bledem */
		if (!isRemoved) {
			for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
				/* Sprawdzenie bitu maski bitowej odpowiadajacej za dany numer priorytetu*/
				if(KiReadySummary[iterator]==true) {
					
					KiDispatcherReadyListHead.get(iterator).remove(process);
					/* */
					if (KiDispatcherReadyListHead.get(iterator).isEmpty()) {
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
	 * FINDREADYTHREAD
	 * Metoda planisty na wyszukanie watku o najwyzszym priotytecie do wykonania przez procesor
	 */
	private Process FindReadyThread() {
		/* Iteracja po masce bitowej w celu sprawdzenia czy na ktoryms bicie nie ma wartosci true aby mozna bylo pobrac z kolejki danego priorytetu gotowy proces*/
		for (byte iterator=REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL; iterator>=VARIABLE_CLASS_THREAD_PRIORITY_IDLE; iterator--) {
			/* Sprawdzenie bitu maski bitowej odpowiadajacej za dany numer priorytetu*/
			if(KiReadySummary[iterator]==true) {
				/* Pobranie pierwszego procesu z kolejki*/
				Process foundThread=KiDispatcherReadyListHead.get(iterator).getFirst();
				
				/* Sprawdzenie czy nie wystapil blad
				 * - proces ma inny stan niz gotowy
				 * - proces z jakiegos powodu zostal usuniety(null) ale nadal jest dostepny w kolejce
				 * */
				if (foundThread!=null && foundThread.getState()==Process.processState.Ready) {
					/* Jesli nie ma bledu przekaz zwroc proces planiscie  */
					return foundThread;
				}
				else {
					/* Jesli proces z jakiegos dziwnego powodu nie ma statusu Ready a nadal jest w kolejce to powinien zostac z niej usuniety*/
					KiDispatcherReadyListHead.get(iterator).removeFirst();
					/* Zmiana stanu bitu na masce bitowej w przypadku gdy kolejka jest pusta*/
					if (KiDispatcherReadyListHead.get(iterator).isEmpty()) {
						KiReadySummary[iterator]=false;
						}
					
					/* Zwiekszenie wartosci iteratora aby poraz kolejny przeszukac ta sama kolejke bez koniecznosci przeszukiwania od nowa poprzednich */
					iterator+=1;
				}
			}
		}
		return null; //null->IDLE (watek postojowy)
	}
	
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
	 * Metoda udostepniana innym modulom do poinformowania planisty o stanie gotowosci danego procesu
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
				Iterator<Process> it=KiDispatcherReadyListHead.get(iterator).iterator();
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
		/* Wyzerowanie licznika */
		QuantumAmountCounter=0;
		}
	/*
	 * BALANCESETMANAGER - KONIEC
	 */
	
	
	public void Go() {
		//tutaj sprawdz czy sie zmienia jesli nie no to nie rob nic, a jesli tak to jest wywlaszczenie albo skonczyly sie rozkazy
		//bool czy wywlaszczony
		//bo jesli nie to nie szukaj nowego, chyba ze sie skonczy to wtedy zmien
		//jesli zacznie czekac to inny modul zmieni Running na null?
		// mozesz tez sprawdzic czy proces ma nadal status running albo czy nie jest pusty
		//moze sie tez proces skonczyc w trakcie wykonywania i co wtedy
		
		/*Zmienna pomocnicza zliczajaca ilosc wykonanych instrukcji w danym kwancie czasu */
		byte InstructionsExecuted=0;
		
		
		boolean WasExpriopriatedDuringQuantum=false;
		/* Jesli cokolwiek w czasie "przerwy" zostalo dodane to i tak planista to zauwazy dzieki metodzie FindReadyThread
		 * takze flaga musi byc wyzerowana*/
		this.IsExpropriated=false;
		
		Scheduler.Running=this.FindReadyThread();
		Scheduler.Running.setStan(Process.processState.Running);
		
		while (InstructionsExecuted<Scheduler.InstructionsPerQuantum) {
			
			if (this.IsExpropriated==true) {
				WasExpriopriatedDuringQuantum=true;
				Scheduler.Running.setStan(Process.processState.Ready);
				if(Scheduler.Running.schedulingInformations.getUsedQuantumAmount()>=1) {
					this.RemoveFromReadyList(Scheduler.Running);
					this.AddToReadyList(Scheduler.Running);
				}
				Scheduler.Running=this.FindReadyThread();
			}
			else {
				
			}
			
			if (Scheduler.Running!=null) {
			Scheduler.Running.schedulingInformations.setSchedulersLastQuantumAmountCounter(QuantumAmountCounter);
			
			//jesli Used
			//sprawdzenie wywlaszczenia - po petli, gdy instrukcje beda juz wykonane? tak czy siak bedzie wykonywac proces pierwszy w kolejce, jesli zostanie wywlaszczony
			// no to i tak zostanie pierwszy, ale co jesli wykorzysta jeden kwant a ma dwa i zostanie wywlaszczony
			}
			else {
				/* SEKCJA DLA IDLE THREAD(WATEK POSTOJOWY) */
			}
			
			/* Podbicie licznika wykonanych instrukcji */
			InstructionsExecuted++;
		}
		if (WasExpriopriatedDuringQuantum==false) {
			Scheduler.Running.schedulingInformations.setSchedulersLastQuantumAmountCounter(QuantumAmountCounter);
			Scheduler.Running.schedulingInformations.setUsedQuantumAmount((byte) (Scheduler.Running.schedulingInformations.getUsedQuantumAmount()+1));
			if (Scheduler.Running.schedulingInformations.getUsedQuantumAmount()==Scheduler.Running.schedulingInformations.getGivenQuantumAmount()) {
				
			}
			
		}
		QuantumAmountCounter++;
	}
	
	
	/*
	 * ================================================================================================
	 * METODY KLASY SCHEDULER - KONIEC
	 * ================================================================================================
	 */
	}
