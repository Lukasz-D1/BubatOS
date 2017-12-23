import java.util.List;
import java.util.LinkedList;

public class ProcessManagment {
	public List<Process> processList = new LinkedList<Process>();
	
	private Process mainProcess;
	
	public ProcessManagment() {
		mainProcess = new Process("init",0);	
		mainProcess.setStan(mainProcess.state.Ready);
		processList.add(mainProcess);
	}
	
	
	
}
