import java.util.List;
import java.util.LinkedList;

public class Process {
	public String processName;
	private int PID;
	private int PPID;
	
	List<Process> processChildrenList = new LinkedList<Process>();
	
	
	public enum processState{New, Running, Waiting, Ready, Terminated};
	public processState state;
	private static int processCounter = 0;
	private int processPriority;
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
		this.processPriority = 0;
		this.r1 = 0;
		this.r2 = 0;
		this.programCounter = 0;
		this.base = 0;
		this.limit = 0;		
		
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
	
	public int getProcessPriority() {
		return processPriority;
	} 
	
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
	
	public void setProcessPriority(int processPriority) {
		this.processPriority = processPriority;
	}
	
	public void setR1(int r1) {
		this.r1 = r1;
	}
	
	public void setR2(int r2) {
		this.r2 = r2;
	}
	
	
}
