
public class Main {
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		ProcessManagment p1 = new ProcessManagment();
		
		
		System.out.println(p1.mainProcess.getProcessName());
		
		Process jeden = p1.fork(p1.mainProcess);
		System.out.println(jeden.getState());
		jeden.setProcessName("pierwszy");
		
		Process dwa = p1.fork(jeden);
		dwa.setProcessName("drugi");
		
		Process trzy = p1.fork(jeden);
		trzy.setProcessName("trzeci");
		
		Process cztery = p1.getProcessByName("drugi");
		System.out.println(cztery.getProcessName());
		
		jeden.showProcessChildrenList();
		p1.ps();
	}
}
