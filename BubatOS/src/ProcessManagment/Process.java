import java.util.List;
import java.util.LinkedList;
import CPU_Scheduling.Scheduler;

public class Process {
	public String processName;
	private int PID;
	private int PPID;
	
	List<Process> processChildrenList = new LinkedList<Process>();
	
	
	public enum processState{New, Running, Waiting, Ready, Terminated};
	public processState state;
	private static int processCounter = 0;
	//private int processPriority;
	public int r1, r2, programCounter;
	public PageTab processTab;
	public Memory processMemory;
	int base, limit;
	
	
	
	
	
	public String fileName;
	public Process(String name, int parentID) {
		this.PID = processCounter;
		
		if(PID == 0){
			this.processName = "init";
		} else {
			this.processName = name;
		}
		
		
		processCounter++;
		
		this.PPID = parentID;
		this.state = processState.New;
		//this.processPriority = 0;
		this.r1 = 0;
		this.r2 = 0;
		this.programCounter = 0;
		this.base = 0;
		this.limit = 0;		
		/* Nadanie wartosci domyslnych polu odpowiadajacemu za przechowywanie informacji potrzebnych planiscie*/
		this.schedulingInformations = new Process.SchedulingInfo();
		
		System.out.println("Utworzono proces o nazwie: " + processName +  ", i ID numer: " + PID);
	} 
	
	
	public void kill(){
		this.state = processState.Terminated;
		//zwolnij pamiec
	}
	
	public Process fork(String name){
		Process process = new Process(name, this.PID);
		
		processChildrenList.add(process);
		
		return process;
	}
	
	public int getPID() {
		return PID;
	}
	
	public int getPPID() {
		return PPID;
	}
	
	public static int getProcessCounter() {
		return processCounter;
	}
	
	public String getProcessName() {
		return processName;
	}
	
	/*public int getProcessPriority() {
		return processPriority;
	} */
	
	public int getProgramCounter() {
		return programCounter;
	}
	
	public int getR1() {
		return r1;
	}
	
	public int getR2() {
		return r2;
	}
	
	public processState getState() {
		return state;
	}
	
	public void setStan(processState state) {
		this.state = state;
	}
	
	public void setBase(int base) {
		this.base = base;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	/*public void setProcessPriority(int processPriority) {
		this.processPriority = processPriority;
	}*/
	
	public void setR1(int r1) {
		this.r1 = r1;
	}
	
	public void setR2(int r2) {
		this.r2 = r2;
	}
	
	/*
	 * SEKCJA INFORMACJI NA POTRZEBY PLANOWANIA PRZYDZIALU PROCESORA
	 */
	
	public SchedulingInfo schedulingInformations;
	
	public class SchedulingInfo {
	

		private byte PriorityNumber;
		private byte DefaultPriorityNumber;
		private byte DefaultGivenQuantumAmount;
		private byte GivenQuantumAmount;
		private byte UsedQuantumAmount;
		private short AwaitingQuantumLength; //do wyrzucenia
		private short SchedulersLastQuantumAmountCounter;
		
		public short getSchedulersLastQuantumAmountCounter() {
			return SchedulersLastQuantumAmountCounter;
		}

		public void setSchedulersLastQuantumAmountCounter(short schedulersLastQuantumAmountCounter) {
			SchedulersLastQuantumAmountCounter = schedulersLastQuantumAmountCounter;
		}

		public SchedulingInfo(){
			this.PriorityNumber=Scheduler.VARIABLE_CLASS_THREAD_PRIORITY_NORMAL;
			this.DefaultPriorityNumber=PriorityNumber;
			this.DefaultGivenQuantumAmount=Scheduler.DefaultQuantumToGive;
			this.GivenQuantumAmount=this.DefaultGivenQuantumAmount;
			this.UsedQuantumAmount=0;
			this.AwaitingQuantumLength=0;
		}
		/*
		 * SETTERY I GETTERY
		 */
		
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

		public short getAwaitingQuantumLength() {
			return AwaitingQuantumLength;
		}

		public void setAwaitingQuantumLength(short awaitingQuantumLength) {
			AwaitingQuantumLength = awaitingQuantumLength;
		}
		/*
		 * SETTERY I GETTERY - KONIEC
		 */
		
	}
	/*
	 * SEKCJA INFORMACJI NA POTRZEBY PLANOWANIA PRZYDZIALU PROCESORA - KONIEC
	 */
	
}
