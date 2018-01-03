//ZMIANY
// Pozmieniałem wątki na procesy kierując się klasą Łukasza, dodałem też planiste i tu jescze nie jestem w 100% 
//pewnien czy to tak ma byc, ale jeszcze przeprowadze z Kuba dokładną rozmowę, bo akurat kiedy to robiłem to nie mógł gadać. 
// Na moje to już jest tak prawie 100%, ale znając życie jest z 20% zrobione i prędzej dostane depresji niż to zacznie działać
// Jak coś to pisać

package semaphore;

import java.util.LinkedList;
import ProcessManagment.Process;
import CPU_Scheduling.Scheduler;

public class Semaphore {
private boolean stan;

public String name;
public LinkedList<Process> WaitingList= new LinkedList <Process>();
//public LinkedList <Process> ReadyList = new LinkedList<Process>();


public Semaphore( String name) {
	this.stan=true;
	this.name=name;
}

public boolean isStan() {
	return stan;
}

public void P (Process p) throws InterruptedException
{
	Scheduler p1=new Scheduler();
	if( stan==true)
	{
		System.out.println("Semafor jest juz podniesiony. Proces przechodzi dalej");
		stan=false; // semafor podniesiony, proces opuszcza semafor i wykonuje critical section 
		System.out.println("Proces" + p.getProcessName() +" wykonuje sie");
	}
	else
	{
		System.out.println("Semafor jest aktualnie opuszczony. Proces nie moze dzialac dalej.");
		System.out.println("Proces zmienia stan na oczekujacy");
		p.setStan(Process.processState.Waiting);
		WaitingList.add(p);
		p1.ReadyThread(p,true);
	}
}

public void V() throws InterruptedException
{
	Scheduler p1=new Scheduler();
	if(stan==false)
	{
		if(WaitingList.isEmpty()==false)
		{
			Process p;
			p=WaitingList.getFirst();
			System.out.println("Proces" + p.getProcessName()+" zmienia status na gotowy\n i opuszcza kolejke procesow oczekujacych");
			p.setStan(Process.processState.Ready);
			//ReadyList.add(p);
			WaitingList.removeFirst();
			p1.ReadyThread(p,true);
		}
		else {
			System.out.println("Brak procesow oczekujacych. Podniesienie semafora");
			stan=true;
		}
	}
	else {System.out.println("Semafor jest juz aktualnie podniesiony");}
}

}


