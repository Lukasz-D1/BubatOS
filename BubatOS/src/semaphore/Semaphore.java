
package semaphore;

import java.util.LinkedList;
import ProcessManagment.Process;
import CPU_Scheduling.Scheduler;

public class Semaphore {
private boolean stan;

public String name;
public LinkedList<Process> WaitingList= new LinkedList <Process>();
Scheduler scheduler;
public Process IsUsed;


public Semaphore( String name) {
	this.stan=true;
	this.name=name;
}

public boolean isStan() {
	return stan;
}

public void P (Process p) throws InterruptedException
{
	if( stan==true)
	{
		System.out.println("Semafor jest juz podniesiony. Proces przechodzi dalej");
		stan=false; // semafor podniesiony, proces opuszcza semafor i wykonuje critical section 
		System.out.println("Proces" + p.getProcessName() +" wykonuje sie");
		IsUsed=p;
	}
	else
	{
		System.out.println("Semafor jest aktualnie opuszczony. Proces nie moze dzialac dalej.");
		System.out.println("Proces zmienia stan na oczekujacy");
		p.setStan(Process.processState.Waiting);
		WaitingList.add(p);
		scheduler.InformSchedulerModifiedState(p);
	}
}

public void V() throws InterruptedException
{

	if(stan==false)
	{
		if(WaitingList.isEmpty()==false)
		{
			Process p;
			p=WaitingList.getFirst();
			System.out.println("Proces" + p.getProcessName()+" zmienia status na gotowy\n i opuszcza kolejke procesow oczekujacych");
			WaitingList.removeFirst();
			scheduler.ReadyThread(p,true);
			IsUsed=p;
		}
		else {
			System.out.println("Brak procesow oczekujacych. Podniesienie semafora");
			stan=true;
		}
	}
	else {System.out.println("Semafor jest juz aktualnie podniesiony");}
}

public Process getIsUsed() {
	return IsUsed;
}

}


