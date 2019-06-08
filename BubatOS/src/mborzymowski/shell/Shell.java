package mborzymowski.shell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import CPU_Scheduling.Scheduler;
import FileSystem.Drive;
import FileSystem.FileException;
import FileSystem.OutOfMemoryException;
import IPC.Connection;
import IPC.Handler;
import Memory_PC.MassMemory;
import Memory_PC.Memory;
import Memory_PC.PageTab;
import bubatos.Core;
import ProcessManagment.*;
import ProcessManagment.Process;


public class Shell{

	/* AREA DLA KONSOLI */
	protected JTextArea area;
	
	/* SCROLL PANEL */
	protected JScrollPane scrl;
	
	/* CMD FIELD */
	protected TextField cmd;
	
	/* GUI KONSOLI */
	protected JFrame frame;
	
	/* AKTUALNY KATALOG */
	protected String drv = "C";
	protected String[] path = new String[1000];
	protected int pcounter = 0;
	
	/* OSTATNIA KOMENDA */
	protected String lastcmd;
	
	/* HISTORIA KOMEND */
	protected List<String> commands = new ArrayList<String>();
	protected int cmdindex;
	
	/* FILE EDIT */
	boolean isfileediting = false;
	List<String> filecontent = new ArrayList<String>();
	String currfilename;
	boolean isfileappending = false;
	
	/* MODULY INNYCH */
	Drive mainDrive;
	ProcessManagment pm;
	Scheduler sch;
	// mem;
	
	/* KONSTRUKTOR */
	public Shell(Drive mainDrive, ProcessManagment pm, Scheduler sch)
	{
		this.cmdindex = 0;
		
		this.mainDrive = mainDrive;
		
		this.pm = pm;
		
		this.sch = sch;
		//this.mem = mem;
	}
	
	/* UTWORZENIE SHELL'A */
	public void startShell()
	{		
		//utw�rz frame
		this.frame = new JFrame();
		
		//text area razem z auto scrollem na sam d� 
		this.area = new JTextArea();
		DefaultCaret caret = (DefaultCaret)this.area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//utworzenie text field dla cmd
		this.cmd = new TextField();
		this.cmd.setBackground(Color.BLACK);
		this.cmd.setForeground(Color.WHITE);
		
		//utworzenie panelu scrollowania dla konsoli
		this.scrl = new JScrollPane(this.area);
		
		//ustawienie wielkosci wyjscia konsoli
		this.scrl.setPreferredSize(new Dimension(780,530));
		
		//dodanie do GUI
		this.frame.add(scrl);
		
		//dodanie do GUI cmd box
		this.frame.add(cmd, BorderLayout.SOUTH);
		
		//ustawienia GUI
		this.frame.pack();
        this.frame.setVisible( true );
        this.frame.setSize(800,600);
        this.frame.setTitle("BubatOS");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        
        //dodatkowe ustawienia dla p�l
        this.area.setEditable(false);
        this.area.setRows(30);
        this.area.setBackground(Color.BLACK);
        this.area.setForeground(Color.WHITE);
        
        //powitanie
        this.printWelcome();
        
        //zrobienie keybinda
        this.cmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//przepisanie komendy
				String komenda = e.getActionCommand();
				//System.out.println(komenda);
				
				//wyczyszczenie pola komend
				cmd.setText(" ");//nwm czemu ale najpierw trzeba wpisac spacje zeby wyczyscic
				cmd.setText("");
				
				//zapisanie komendy
				if(komenda.equals(""))
				{
					lastcmd = "_";
				}
				else
				{
					lastcmd = komenda;
					commands.add(komenda);
				}
				
				lastcmd = lastcmd.toLowerCase();
				
				//przekazanie komendy do funkcji
				execute(komenda.toLowerCase());
				
				cmdindex=0;
			}
		});
        this.cmd.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_UP)
				{
					//cmd.setText(commands.get(commands.size()-(cmdindex)));
					if(commands.size()>cmdindex)
					{
						cmdindex++;
						cmd.setText(commands.get(commands.size()-(cmdindex)));
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					cmdindex--;
					if(cmdindex < 0)
					{
						cmd.setText("");
						cmdindex=0;
					}
					else
					{
						//cmdindex--;
						cmd.setText(commands.get(commands.size()-(cmdindex+1)));
						//System.out.println("sas");
					}
				}
				//System.out.println("Pokzalem index: "+cmdindex);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
	}
	
	/* POWITANIE */
	public void printWelcome()
	{
		area.setFont(new Font("Lucida Console", Font.CENTER_BASELINE, 72));
		echo("", false);
		echo("", false);
		echo("", false);
		echo("     BubatOS", false);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			echo(e.getMessage(), false);
		}
		
		area.setText("");
		area.setFont(new Font("Lucida Console", Font.CENTER_BASELINE, 13));
		echo("");
		
		//focus na cmd 
		this.cmd.requestFocus();
	}
	
	/* FUNKCJA ECHO DOMY�LNA Z KATALOGIEM*/
	public void echo(String text)
	{
		this.echo(text, true);
	}
	
	/*FUNKCJA ECHO BEZ KATALOGU*/
	public void echo(String text, boolean withdir)
	{
		//jezeli with dir to wypisze z katalogiem
		if(withdir)
		{
			//jezeli wpisano juz jakas komende
			if(this.lastcmd != null)
			{
				//pobieramy logi
				String lasttxt = this.area.getText();
				
				//przygotowanie area.getText() na regex
				Pattern p1 = Pattern.compile("\\\\");
				Matcher m1 = p1.matcher(this.lastcmd);
				
				this.lastcmd = m1.replaceAll("\\\\\\\\");
				
				
				//regex check
				Pattern p = Pattern.compile(">\n", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(lasttxt);

				//replace tak by dodalo ostatnio uzyta komende
				String output = m.replaceAll(">" + this.lastcmd +"\n");
				
				//wyczysc ostatnie komendy
				this.lastcmd = "";
				
				//wypisz co trzeba
				if(this.isfileediting | this.isfileappending)
				{
					this.area.setText(output + ">" + text + "\n");
				}
				else
				{
					this.area.setText(output + this.makeStrDir()+ ">" + text + "\n");
				}
			}
			else
			{
				this.area.setText(this.area.getText() + this.makeStrDir() + ">" + text + "\n");
			}
			//this.area.setText(this.area.getText() + this.currentDir + ">" + text + "\n");
		}
		else
		{
			this.area.setText(this.area.getText() + text + "\n");
		}
	}
	
	/* TWORZENIE STRINGU DO WYSWIETLENIA */
	public String makeStrDir()
	{
		if(this.pcounter == 0)
		{
			return this.drv + ":\\";
		}
		else
		{
			String totalpath = "";
			
			for(int x=0;x<this.pcounter;x++)
			{
				if(x<this.pcounter-1)
				{
					totalpath += this.path[x] + "\\";
				}
				else
				{
					totalpath += this.path[x];
				}
				
			}
			
			return this.drv + ":\\" + totalpath;
		}
	}
	
	/* BINDY NA KOMENDY */
	public void execute(String command, boolean isScript)
	{	
		/*if(command.matches("^cd[ ].*") | command.matches("^cd$"))
		{
			if(command.matches("^cd[ ]*"))
			{
				//do nothing
			}
			else
			{
				//TODO poprawic jak bedzie klasa zarzadzania plikami
				
				//usuniecie "cd" zeby miec same katalogi
				Pattern p2 = Pattern.compile("cd[ ]");
				Matcher m2 = p2.matcher(command);
				
				command = m2.replaceAll("");
				
				String[] katalogi = command.split("\\\\");
				
				
				
				System.out.println(command);
				this.path[this.pcounter] = "xd";
				this.pcounter++;
			}
		}
		else */if(command.matches("^createfile[ ][a-zA-Z0-9\\.]*$") | command.matches("^writefile$"))
		{
			if(command.matches("^createfile[ ]*"))
			{
				this.echo("Nie podano nazwy pliku", false);
			}
			else
			{
				String[] args = command.split("[ ]");
				try {
					mainDrive.createFile(args[1]);
					echo("Plik utworzono", false);
				} catch (FileException | OutOfMemoryException e) {
					echo(e.getMessage(), false);
				}
			}
		}
		else if(command.matches("^closefile[ ][a-zA-Z0-9\\.]*$") | command.matches("^closefile$"))
		{
			if(command.matches("^closefile[ ]*"))
			{
				this.echo("Nie podano nazwy pliku", false);
			}
			else
			{
				String[] args = command.split("[ ]");
				try {
					mainDrive.closeFile(args[1]);
					echo("Plik zamknieto", false);
				} catch (FileException | InterruptedException e) {
					echo(e.getMessage(), false);
				}
			}
		}
		else if(command.matches("^openfile[ ][a-zA-Z0-9\\.]*$") | command.matches("^openfile$"))
		{
			if(command.matches("^openfile[ ]*"))
			{
				this.echo("Nie podano nazwy pliku", false);
			}
			else
			{
				String[] args = command.split("[ ]");
				try {
					mainDrive.openFile(args[1]);
					echo("Plik otwarty", false);
				} catch (FileException | InterruptedException e) {
					echo(e.getMessage(), false);
				}
			}
		}
		/* WPISANIE DO PLIKU */
		else if(command.matches("^writefile[ ][a-zA-Z0-9\\.]*$") | command.matches("^writefile$"))
		{
			if(command.matches("^writefile[ ]*"))
			{
				this.echo("Nie podano nazwy pliku", false);
			}
			else
			{
				String args;
				
				Pattern p2 = Pattern.compile("writefile[ ]");
				Matcher m2 = p2.matcher(command);
				
				args = m2.replaceAll("");
				
				System.out.println(args);
				
				String[] filenm = args.split("\\.");
					
				this.currfilename = args;
					
				this.isfileediting = true;
						
				this.filecontent = new ArrayList<String>();

			}
		}
		/* DODANIE DO PLIKU */
		else if(command.matches("^appendfile[ ][a-zA-Z0-9\\.]*$") | command.matches("^appendfile$"))
		{
			if(command.matches("^appendfile[ ]*"))
			{
				this.echo("Nie podano nazwy pliku", false);
			}
			else
			{
				String args;
				
				Pattern p2 = Pattern.compile("appendfile[ ]");
				Matcher m2 = p2.matcher(command);
				
				args = m2.replaceAll("");
				
				System.out.println(args);
				
				String[] filenm = args.split("\\.");		
				
				this.currfilename = args;
					
				this.isfileappending= true;
					
				this.filecontent = new ArrayList<String>();
			}
		}
		/* WYSWIETLENIE PLIKOW */
		else if(command.matches("^dir[ ].*") | command.matches("^dir$"))
		{
			if(command.matches("^dir[ ]*"))
			{
				this.echo(mainDrive.ListDirectory(), false);	
			}
			else
			{
				this.echo("Bledne argumenty", false);
			}
		}
		/* ZMIANA NAZWY PLIKU */
		else if(command.matches("^rename[ ].*") | command.matches("^rename$"))
		{
			if(command.matches("^rename[ ]*[a-zA-Z0-9\\.]*[ ]*[a-zA-Z0-9\\.]*$"))
			{
				String[] args = command.split("[ ]");
				System.out.println(args[0]);
				
				try {
					mainDrive.renameFile(args[1], args[2]);
					echo("Zmiana powiodla sie", false);
				} catch (FileException e) {
					echo(e.getMessage(), false);
				}
				
			}
			else
			{
				this.echo("Bledne argumenty", false);
			}
		}
		/* USUWANIE PLIKU */
		else if(command.matches("^deletefile.*") | command.matches("^deletefile$"))
		{
			if(command.matches("^deletefile[ ]*"))
			{
				this.echo("Nie podano nazwy pliku", false);
			}
			else
			{
				String args;
				
				Pattern p2 = Pattern.compile("deletefile[ ]");
				Matcher m2 = p2.matcher(command);
				
				args = m2.replaceAll("");
				
				System.out.println(args);
				
				try {
					mainDrive.deleteFile(args);
					echo("Plik zostal usuniety", false);
				} catch (FileException e) {
					echo(e.getMessage(), false);
				}
			}
		}
		/* POTOKOWE WPISYWANIE DO PLIKU */
		else if(command.matches("^\\(.*\\)[ ]*>[ ]*[a-zA-Z0-9\\.]*$"))
		{
			String[] args;
			
			args = command.split(">");
			
			String filename;
			String content;
			
			Pattern p2 = Pattern.compile("^[ ]*");
			Matcher m2 = p2.matcher(args[1]);
			
			filename = m2.replaceAll("");
			
			Pattern p3 = Pattern.compile("^\\(|\\)$");
			Matcher m3 = p3.matcher(args[0]);
			
			content = m3.replaceAll("");
			
			String[] lines = content.split("\\\\n");
			
			String dowpisania = "";
			
			for(String e : lines)
			{
				dowpisania+=e+"\n";
			}
			
			System.out.println(content);
			
			try
			{
				mainDrive.writeFile(filename, dowpisania);
				
				echo("Zapisano dane w pliku", false);
			} catch (FileException | OutOfMemoryException e) {
				echo(e.getMessage(), false);
			}
		}
		else if(command.matches("^readfile[ ][a-zA-Z0-9\\\\.]+[ ]+[0-9]+$") | command.matches("^readfile$"))
		{
			if(command.matches("^readfile[ ]*"))
			{
				this.echo("Nie podano nazwy pliku", false);
			}
			else
			{
				String args;
				
				Pattern p2 = Pattern.compile("readfile[ ]");
				Matcher m2 = p2.matcher(command);
				
				args = m2.replaceAll("");
				
				System.out.println(args);
				
				String[] argss = args.split(" ");
				
				System.out.println(argss[0]);
				
				try
				{
					echo(mainDrive.readFile(argss[0], Integer.parseInt(argss[1])), false);
					
				} catch (FileException e) {
					echo(e.getMessage(), false);
				}
			}
		}
		else if(command.matches("^printbitvector$"))
		{
			echo(mainDrive.printBitVector(), false);
		}
		else if(command.matches("^printdrive$"))
		{
			echo(mainDrive.printDrive(), false);
		}
		else if(command.matches("^printdiskblock[ ]+[0-9]+$"))
		{
			String[] args = command.split("[ ]");
			echo(mainDrive.printDiskBlock(Integer.parseInt(args[1])), false);
		}
		else if(command.matches("^createfilelink[ ]+[a-zA-Z0-9]+[ ]+[a-zA-Z0-9]+$"))
		{
			String[] args = command.split("[ ]");
			try {
				mainDrive.createLink(args[1], args[2]);
				echo("Zlinkowano pliki", false);
			} catch (FileException e) {
				// TODO Auto-generated catch block
				echo(e.getMessage(), false);
			}
		}
		else if(command.matches("^printinodeinfo[ ]+[a-zA-Z0-9]+$"))
		{
			String[] args = command.split("[ ]");

			String s;
			try {
				s = mainDrive.printInodeInfo(args[1]);
				echo(s, false);
			} catch (FileException e) {
				echo(e.getMessage(), false);
			}

		}
		/* IPC */
		//createconnection p1 p2 
		//sendmessage p1 p2 message
		//readmessage p1 p2
		//endconnection p1 p2
		else if(command.matches("^createconnection[ ]+[a-zA-Z0-9]+[ ]+[a-zA-Z0-9]+$"))
		{
			String[] args = command.split("[ ]");
			Connection c = new Connection(args[1], args[2], pm);
			Handler.xxx.add(c);
			//echo("Stworzono polaczenie", false);
		}
		else if(command.matches("^sendmessage[ ]+[a-zA-Z0-9]+[ ]+[a-zA-Z0-9]+[ ]+[a-zA-Z0-9/-]+$"))
		{
			String[] args = command.split("[ ]");
			//System.out.println(Handler.xxx.get(0).toString());
			Handler.getFromList(args[1], args[2]).sendMessage(args[1], args[2], args[3]);
			echo("Wyslano wiadomosc", false);
		}
		else if(command.matches("^readmessage[ ]+[a-zA-Z0-9]+$"))
		{
			String[] args = command.split("[ ]");
			String odp = Handler.readfromlist(args[1]).readMessage(args[1]);
			echo("Odczytano: "+odp, false);
		}
		else if(command.matches("^endconnection[ ]+[a-zA-Z0-9]+[ ]+[a-zA-Z0-9]+$"))
		{
			String[] args = command.split("[ ]");
			Handler.getFromList(args[1], args[2]).endConnection(args[1], args[2]);
			echo("Zakonczono polaczenie", false);
		}
		else if(command.matches("^printinodeinfo[ ]+[a-zA-Z0-9]+$"))
		{
			String[] args = command.split("[ ]");

			String s;
			try {
				s = mainDrive.printInodeInfo(args[1]);
				echo(s, false);
			} catch (FileException e) {
				echo(e.getMessage(), false);
			}

		}
		else if(command.matches("^run[ ][a-zA-Z0-9\\.]*") | command.matches("^run$"))
		{
			if(command.matches("^run[ ]*"))
			{
				this.echo("Nie podano sciezki skryptu", false);
			}
			else
			{
				//usuniecie "run" zeby miec sama sciezke
				Pattern p2 = Pattern.compile("run[ ]");
				Matcher m2 = p2.matcher(command);
				
				command = m2.replaceAll("");
				
				this.runScript(command);
			}			
		}
		/* GO */
		else if(command.matches("^go$"))
		{
			try {
				this.sch.Go();
				echo("Uruchomiono", false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				echo(e.getMessage(), false);
			}
		}
		else if(command.matches("^displaymem$"))
		{
			char[][] pam = Memory.getAll();
			
			String memtable = "";
			
			char[][] o = Memory.getAll();
			for (byte i = 0; i != 4; ++i) {
				System.out.print(i * 16 + "-" + (i * 16 + 15) + ":");
				memtable+=i * 16 + "-" + (i * 16 + 15) + ":";
				
				for (byte j = 0; j != 16; ++j) {
					System.out.print(o[i][j]);
					memtable+=o[i][j];
				}
				memtable+="\n";
				System.out.println();
			}
			
			echo(memtable, false);
		}/* PROCESS MANAGMENT*/
		//createprocess nazwa rozmiar sciezka
		//deleteprocess nazwa
		//stopprocess nazwa
		//displaybc nazwa
		else if(command.matches("^deleteprocess[ ]+[0-9]+"))
		{
			String[] args = command.split("[ ]");
			try {
				pm.kill(pm.getProcessByID(Integer.parseInt(args[1])));
				echo("Proces usuniety", false);
			} catch (NumberFormatException | InterruptedException e) {
				echo("Wystapil blad: "+e.getMessage(), false);
			}
		}
		else if(command.matches("^stopprocess[ ]+[0-9]+"))
		{
			String[] args = command.split("[ ]");
			pm.stop(pm.getProcessByID(Integer.parseInt(args[1])));
			echo("Proces zatrzymany", false);
		}
		else if(command.matches("^deletenameprocess[ ]+[0-9]+"))
		{
			String[] args = command.split("[ ]");
			try {
				pm.kill(pm.getProcessByName(args[1]));
				echo("Proces usuniety", false);
			} catch (NumberFormatException | InterruptedException e) {
				echo("Wystapil blad: "+e.getMessage(), false);
			}
		}
		else if(command.matches("^stopnameprocess[ ]+[0-9]+"))
		{
			String[] args = command.split("[ ]");
			pm.stop(pm.getProcessByName(args[1]));
			echo("Proces zatrzymany", false);
		}
		else if(command.matches("^createprocess[ ]+[a-zA-Z0-9]+[ ]+[0-9]+[ ]*[a-zA-Z0-9\\.:\\\\]*$"))
		{
			String[] args = command.split("[ ]");
			if(args.length == 4)
			{
				try {
					Process p1 = pm.fork(pm.mainProcess);
					p1.setProcessName(args[1]);
					p1.setSizeOfFile(Integer.parseInt(args[2]));
					p1.setFileName(args[3]);
					PageTab pt = new PageTab(args[3], Integer.parseInt(args[2]));
					p1.setProcessTab(pt);
					echo("Utworzono proces", false);
					this.sch.ReadyThread(p1);
				} catch (IOException e) {
					echo("Wystapil blad: "+e.getMessage(), false);
				}
			}
			else
			{
				try {
					Process p1 = pm.fork(pm.mainProcess);
					p1.setProcessName(args[1]);
					p1.setSizeOfFile(Integer.parseInt(args[2]));
					echo("Utworzono proces1", false);
					this.sch.ReadyThread(p1);
				} catch (IOException e) {
					echo("Wystapil blad: "+e.getMessage(), false);
				}
			}
		}
		else if(command.matches("^displaymem[ ]+[0-9]+[ ]+[0-9]+$"))
		{
			String[] args = command.split("[ ]");
			try {
				char[] mem = MassMemory.getChars(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				
				String zwr = "Zawartosc: \n";
				
				for(char m1 : mem)
				{
					zwr += m1;
				}
				
				echo(zwr, false);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				echo("Wystapil blad", false);
			}
		}
		else if(command.matches("^ps$"))
		{
			pm.ps();
		}
		else if(command.matches("^displaynamebc[ ]+[a-zA-Z0-9]+$"))
		{	
			String[] args = command.split("[ ]");
			
			//pm.ps();
			
			echo(pm.getProcessByName(args[1]).printProcess(), false);
			
		}
		else if(command.matches("^displaybc[ ]+[0-9]+$"))
		{	
			String[] args = command.split("[ ]");
			
			echo(pm.getProcessByID(Integer.parseInt(args[1])).printProcess(), false);
			
		}
		else if(command.matches("^help$"))
		{
			this.echo("Dostepne komendy: ", false);
			this.echo("    - createfile nazwa: utworz plik", false);
			this.echo("    - deletefile nazwa: usun plik", false);
			this.echo("    - openfile nazwa: otwiera plik", false);
			this.echo("    - closefile nazwa: zamyka plik", false);
			this.echo("    - readfile nazwa iloscznakow: wyswietlenie zawartosci pliku na konsole", false);
			this.echo("    - deletefile: usuwanie pliku", false);
			this.echo("    - renamefile nazwa1 nazwa2: zmiana nazwy pliku", false);
			this.echo("    - appendfile nazwa: dolaczenie pliku", false);
			this.echo("    - writefile nazwa: zapisanie pliku", false);
			this.echo("    - (tresc)>plik: potokowe zapisanie pliku", false);
			this.echo("    - dir: wyswietlenie zawartosci katalogu", false);
			this.echo("    =====================================================", false);
			this.echo("    - printbitvector", false);
			this.echo("    - printdrive", false);
			this.echo("    - printdiskblock numer", false);
			this.echo("    - createfilelink nazwa1 nazwa2", false);
			this.echo("    - printinodeinfo nazwa", false);
			this.echo("    =====================================================", false);
			this.echo("    - createconnection process1 process2: utworzenie socketa komunikacji", false);
			this.echo("    - sendmessage process1 process2 wiadomosc: wyslanie wiadomosci miedzy procesami", false);
			this.echo("    - readmessage process1: odebranie wiadomosci", false);
			this.echo("    - endconnection process1 process2: zakonczenie polaczenia", false);
			this.echo("    =====================================================", false);
			this.echo("    - run skrypt.txt: uruchomienie skryptu", false);
			this.echo("    - go: uruchomienie planisty", false);
			this.echo("    =====================================================", false);
			this.echo("    - displaymem: wyswietla stan pamieci", false);
			this.echo("    - displaymem adres ilosckomorek: wyswietla stan pamieci", false);
			this.echo("    - displaynamebc nazwaprocesu: pokaz blok kontrolny procesu", false);
			this.echo("    - displaybc pid: pokaz blok kontrolny procesu", false);
			this.echo("    =====================================================", false);
			this.echo("    - createprocess name rozmiar opcjonalnasciezka: tworzenie procesu", false);
			this.echo("    - stopprocess PID: zatrzymanie procesu", false);
			this.echo("    - deleteprocess PID: process kill", false);
			this.echo("    - deletenameprocess nazwa: process kill", false);
			this.echo("    - stopnameprocess nazwa: zatrzymanie procesu", false);
			this.echo("    - shutdown", false);
			this.echo("    - help", false);			
		}
		else if(command.matches("[ ]*"))
		{
			//do nothing
		}
		else if(command.matches("^shutdown$"))
		{
			System.exit(1);
		}
		else if(this.isfileappending)
		{
			if(command.matches(":q"))
			{
				this.isfileappending = false;
				try
				{
					String fcont = "";
					
					for(String e : this.filecontent)
					{
						fcont+=e+"\n";
					}
					
					mainDrive.appendFile(this.currfilename, fcont);
					this.currfilename = "";
					this.filecontent = new ArrayList<String>();
				} catch (Exception e) {
					echo(e.getMessage(), false);
				}
				
			}
			else
			{
				filecontent.add(command);
			}
		}
		else if(this.isfileediting)
		{
			if(command.matches(":q"))
			{
				this.isfileediting = false;
				try
				{
					String fcont = "";
					
					for(String e : this.filecontent)
					{
						fcont+=e+"\n";
					}
					
					mainDrive.writeFile(this.currfilename, fcont);
					this.currfilename = "";
					this.filecontent = new ArrayList<String>();
				} catch (FileException | OutOfMemoryException e) {
					echo(e.getMessage(), false);
				}
				
			}
			else
			{
				filecontent.add(command);
			}
		}
		else
		{
			this.echo("Nie znaleziono komendy", false);
			this.echo("Dostepne komendy po wpisaniu help", false);
		}
		
		if(!isScript)
		{
			//sciezka na koniec
			this.echo("");
		}
	}
	
	public void execute(String command)
	{
		this.execute(command, false);
	}
	
	public boolean wpisano(String str)
	{
		if(str.matches("^"+str+"[ ].*") | str.matches("^"+str+"$"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	
	
	
	
	private void runScript(String path)
	{
		String[] skrypt = Core.readFile(path);
		
		if(skrypt != null)
		{
			for(String komenda : skrypt)
			{
				this.execute(komenda, true);
			}
		}
		else
		{
			this.echo("Blad odczytu pliku", false);
		}
	}
}
