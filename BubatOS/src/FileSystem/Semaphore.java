package inodes;

import java.util.LinkedList;

public class Semaphore {
private boolean stan;

public String name;
public LinkedList<Thread> WaitingList= new LinkedList <Thread>();
public LinkedList <Thread> ReadyList = new LinkedList<Thread>();
public boolean IsUsed;

public Semaphore( String name) {
	this.stan=true;
	this.name=name;
}

public boolean isStan() {
	return stan;
}

public void P (Thread t) throws InterruptedException
{
	if( stan==true)
	{
		IsUsed=false;
		IsUsed=true;
		System.out.println("Semafor jest juz podniesiony. Proces przechodzi dalej");
		stan=false; // semafor podniesiony, proces opuszcza semafor i wykonuje critical section 
		System.out.println("Proces" + t.getName() +" wykonuje sie");
		t.sleep(2000);
	}
	else
	{
		System.out.println("Semafor jest aktualnie opuszczony. Proces nie moze dzialac dalej.");
		System.out.println("Proces zmienia stan na oczekujacy");
		WaitingList.add(t);
	}
}
//running ->wsk do procesu ktory jest aktualnie wykonywany
public void V(Thread t) throws InterruptedException
{

	if(stan==false)
	{
		if(WaitingList.isEmpty()==false)
		{
			IsUsed=false;
			t=WaitingList.getFirst();
			System.out.println("Proces" + t.getName()+" zmienia status na gotowy");
			ReadyList.add(t);
			WaitingList.removeFirst();
			IsUsed=true;
			stan=true;
			P(t);
		}
		else {
			System.out.println("Brak procesow oczekujacych. Podniesienie semafora");
			stan=true;
			P(t);
		}
	}
	else {System.out.println("Semafor jest juz aktualnie podniesiony");}
}

	
//to dla Adama, ale ogólnie patrz¹c na systemy innych to oni jakoœ tego nie mieli
//i chyba samymi P i V siê moÅ¼na porozumiewaæ z tym
public boolean isIsUsed(Thread t) {
	if(IsUsed==true)
	{
		System.out.println("Plik jest aktualnie uzywany.");
		return IsUsed;
	}
	else{
		System.out.println("Plik nie jest aktualnie uÅ¼ywany");
		return IsUsed;
	}
}

}
