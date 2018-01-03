package bubatos;

import java.io.IOException;

import CPU_Scheduling.Scheduler;
import FileSystem.Drive;
import IPC.Connection;
import Memory_PC.Memory;
import Memory_PC.PageTab;
import ProcessManagment.ProcessManagment;
import interpreter.Interpreter;
import mborzymowski.shell.*;

public class Main {
	
	public static int debuglevel = 0;

	public static void main(String[] args) {
		
		Drive mainDrive = new Drive();
		
		ProcessManagment pm;
		
		try {
			pm = new ProcessManagment();
			pm.mainProcess.processTab.write(0, "init");
			
			Scheduler sch = new Scheduler(pm.mainProcess, new Interpreter(mainDrive, pm));
			
			Shell sh = new Shell(mainDrive, pm);
			sh.startShell();
		} catch (Exception e) {
			System.out.println("Blad tworzenia watku");
			e.printStackTrace();
		}	
	}

}
