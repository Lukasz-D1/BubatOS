package CPU_Scheduling;

import java.util.LinkedList;
import java.util.Vector;
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
	 * InstructionsPerQuantum
	 * Maksymalna ilosc rozkazow do wykonania w kwancie czasu (4 rozkazy)
	 */
	private static final byte InstructionsPerQuantum=4;
	/*
	 * PriorityAmount
	 * Ilosc priorytetow dopuszczalna dla planisty
	 */
	private static final byte PriorityAmount=8;
	
	private static final byte QuantumAmountToBalance=6;
	
	public static final byte DefaultQuantumToGive=2;
	
	
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_TIME_CRITICAL;
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_NORMAL;
	public static byte VARIABLE_CLASS_THREAD_PRIORITY_IDLE;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_TIME_CRITICAL;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_NORMAL;
	public static byte REALTIME_CLASS_THREAD_PRIORITY_IDLE;
	
	
	//Maska bitowa identyfikujaca niepuste kolejki w tablicy KiDispatcherReadyListHead
	private boolean[] KiReadySummary;
	
	private Vector<LinkedList<Process>> KiDispatcherReadyListHead;
	
	public Process Running;
	
	
	
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
		
		Running=null;
		//////////////////////
		//Ustawienie maski bitowej
		//Zabezpieczenie przed niemozliwym bledem
		if (PriorityAmount>0) {
		KiReadySummary = new boolean[PriorityAmount];
		for (byte iterator=PriorityAmount-1; iterator>0; iterator--) {
			KiReadySummary[iterator]=false;
		}
		KiReadySummary[0]=true;
		}
		//////////////////
		
		
		//Ustawienie kolejki procesow gotowych
		
		KiDispatcherReadyListHead=new Vector<LinkedList<Process>>(PriorityAmount);
		for (byte iterator=PriorityAmount-1; iterator>0; iterator--) {
			
			LinkedList<Process> nullList=KiDispatcherReadyListHead.get(iterator);
			nullList=new LinkedList<Process>();
		}
		
		
		//////////////////////
		
	}
	
	Process FindReadyThread() {

		for (byte iterator=PriorityAmount-1; iterator>0; iterator--) {
			if(KiReadySummary[iterator]==true) {
				if (KiDispatcherReadyListHead.get(iterator).getFirst().getState()==Process.processState.Ready) {
					return KiDispatcherReadyListHead.get(iterator).getFirst();
				}
			}
		}
		return null; //null->IDLE (watek postojowy)
	}
	
	public void ReadyThread(Process process, boolean isItSemaphore) {
		
		
	}
	public void ReadyThread(Process process) {
		
			
		}
	private void BalanceSetManager() {
		
	}
}
