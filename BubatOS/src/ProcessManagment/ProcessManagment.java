package ProcessManagment;

import java.util.List;
import java.io.IOException;
import java.util.LinkedList;
import FileSystem.Inode;
import Memory_PC.PageTab;
import semaphore.Semaphore;

public class ProcessManagment {
	// Drzewo (lista) ze wszystkimi procesami.
	public List<Process> processList = new LinkedList<Process>();

	// Proces główny startujący wraz z systemem.
	public Process mainProcess;

	@SuppressWarnings("static-access")
	public ProcessManagment() throws IOException {
		// Alokacja pamięci - do dodania. Łata.
		// Tworzenie procesu init - głównego procesu uruchamianego w trakcie startu systemu.
		// Konieczna deklaracja instancji obiektu ProcessManagment w main.
		mainProcess = new Process("init", 4, "");
		PageTab pt = new PageTab("", 4);
		mainProcess.setProcessTab(pt);
		
		// Ustawienie stanu procesu init na Gotowy.
		mainProcess.setStan(mainProcess.state.Running);
		
		//Dodanie procesu do listy procesów.
		processList.add(mainProcess);
	}

	// Tworzenie nowego procesu na zasadach Unixa - kopia rodzica. Funkcja fork.
	// Działanie: Process nowy_proces = process_managment.fork(rodzic);
	// Pierwszym rodzicem zawsze init.
	// Modyfikacja pól stworzonego procesu: nowy_proces.setProcessName("nazwa");
	// Zwrócić uwagę na to, że ID procesu zawsze będzie dobrze nadawane - statyczne pole licznik procesów.
	public Process fork(Process parent) throws IOException {
		Process process;
		if (parent.schedulingInformations.getDefaultPriorityNumber()!=this.mainProcess.schedulingInformations.getPriorityNumber())
		{
			System.out.println("Kopiowanie priorytetu rodzica");
			
		// Tworzenie nowego procesu na zasadzie skopiowania rodzica, ze zmienionymi Parent ID.
		process = new Process(parent);
		}
		else { 
			process = new Process(parent.getProcessName(), parent.getSizeOfFile(), parent.getFileName());
			System.out.println("Priorytet domyslny");
		}
		
		// Ustawienie ID rodzica.
		process.setPPID(parent.getPID());
		
		
		// Dodanie procesu do listy procesów.
		processList.add(process);
		
		// Dodanie procesu do listy dzieci procesu.
		parent.processChildrenList.add(process);
		
		// Zwrócenie procesu umożliwiające wykonywanie operacji w sposób uproszczony.
		return process;
	}
	
	public void stop(Process proToStop) {
		proToStop.setStan(Process.processState.Terminated);
		System.out.println("Ustawiono Terminated");
		this.ps();
		if(proToStop.getPID() == 0)
		{
			System.exit(0);
		}
	}
	
	// Metoda odpowiedzialna za usunięcie danego procesu. Usuwamy go z listy, ustawiamy stan na Terminated.
	// Zmieniamy ID rodzica na proces init.
	// Łata ze zwalnianiem pamięci.
	public void kill(Process proToKill) throws InterruptedException { 
		proToKill.state = Process.processState.Terminated;
		System.out.println("Ustawiono Terminated");
		 for(Process pro : proToKill.processChildrenList){
			 pro.setPPID(0);
		 }
		 
		 for(Process pro : processList){
			 if(pro.getPID() == proToKill.getPID()){
				 processList.remove(pro);
			 }
		 }
		 this.ps();
		 
		 if(proToKill.getPID() == 0)
			{
				System.exit(0);
			}
		 
		 /*
		  * 
		  * Semafory tutaj
		  * 
		  */
		 
		 for(Inode ino : proToKill.fileList)
		 {
			 	ino.s.V();
		 }
	}
	
	// Zwróć proces po nazwie. UWAGA! Nie jest dodawany do listy wszystkich procesów - czy będzie to potrzebne?
	public Process getProcessByName(String name){
		Process process = new Process();
		for(Process pro : processList){
			if(pro.getProcessName().equals(name)){
				process = pro;
			}
		}
		return process;
	}
	
	// Zwróć proces po ID. UWAGA! Nie jest dodawany do listy wszystkich procesów - czy będzie to potrzebne?
	public Process getProcessByID(int id){
		Process process = new Process();
		for(Process pro : processList){
			if(pro.getPID() == id){
				process = pro;
			}
		}
		return process;
	} 	
	
	// Wyświetlanie wszystkich procesów aktualnie istniejących w systemie.
	public void ps(){
		for(Process pro : processList){
			pro.printProcess();
			}
	}
	
}