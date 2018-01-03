package interpreter;

import com.sun.javafx.image.impl.ByteBgraPre;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import java.util.List;
import java.util.Vector;
import jdk.nashorn.internal.runtime.regexp.RegExpMatcher;
import FileSystem.Drive;
import Memory_PC.Memory;
import IPC.Connection;
import IPC.Handler;
import ProcessManagment.Process;
import ProcessManagment.ProcessManagment;

public class Interpreter 
{
    public int commandCounter;
    public int registerA;
    public int registerB;
    private int accu;
    
    public Drive mainDrive;
    public ProcessManagment pm;
    
    public Interpreter(Drive drv, ProcessManagment pm)
    {
        registerA=0;
        registerB=0;
        commandCounter=0;
        
        this.mainDrive = drv;
        this.pm = pm;
    }
    
    /*Pobranie rejestrów*/
    public void getRegister(int registerA, int registerB, int commandCounter)
    {
        this.registerA=registerA;
        this.registerB=registerB;
        this.commandCounter=commandCounter;
    }
    /*Odsy³anie zmienionych wartoœci rejestrów i licznika rozkazów*/
    public int setRegisterA()
    {
        return registerA;
    }
    public int setRegisterB()
    {
        return registerB;
    }
    public int setCommandCounter()
    {
        return commandCounter;
    }
    /*do JP i JZ*/
    public int skok(int commandCounter)
    {
        return commandCounter;
    }
               
    /* Za³o¿enie jest takie, ¿e dostajê rozkaz jako tablicê stringów (1,2,3 albo 4 elementow¹)
    i pole zerowe to nazwa rozkazu, a pozosta³e pola to argumenty.*/
    public void getCommand(Vector<String> command) throws Exception
    {     
       if(command.elementAt(0).equals("AD")) // Dodawanie rejestrów
       {
           if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("A"))
           {
               registerA=registerA+registerA;
           } else if (command.elementAt(1).equals("A")&&command.elementAt(2).equals("B"))
           {
               registerA=registerA+registerB;
           } else if (command.elementAt(1).equals("B")&&command.elementAt(2).equals("B"))
           {
               registerB=registerB+registerB;
           } else if (command.elementAt(1).equals("B")&&command.elementAt(2).equals("A"))
           {
               registerB=registerA+registerB;
           }
           commandCounter=commandCounter+6;
    /*_______________________________________________________________*/         
       }else if(command.elementAt(0).equals("SB")) //Odejmowanie rejestrów
        {
            if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("A"))
            {
                registerA=0;
            }else if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("B"))
            {
                registerA=registerA-registerB;
            }else if(command.elementAt(1).equals("B")&&command.elementAt(2).equals("B"))
            {
                registerB=0;
            }else if(command.elementAt(1).equals("B")&&command.elementAt(2).equals("A"))
            {
                registerB=registerB-registerA;
            }
            commandCounter=commandCounter+6;
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("MU")) // Mno¿enie rejestrów
        {
            if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("A"))
            {
                registerA=registerA*registerA;
            }else if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("B"))
            {
                registerA=registerA*registerB;
            }else if(command.elementAt(1).equals("B")&&command.elementAt(2).equals("B"))
            {
                registerB=registerB*registerB;
            }else if(command.elementAt(1).equals("B")&&command.elementAt(2).equals("A"))
            {
                registerB=registerB*registerA;
            }
            commandCounter=commandCounter+6;
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("MV")) //Przypisywanie wartoœci rejestrom
        {
            if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("A"))
            {
                registerA=registerA;
            }else if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("B"))
            {
                registerA=registerB;
            }else if(command.elementAt(1).equals("B")&&command.elementAt(2).equals("B"))
            {
                registerB=registerB;
            }else if(command.elementAt(1).equals("B")&&command.elementAt(2).equals("A"))
            {
                registerB=registerA;
            }if(command.elementAt(1).equals("M")&&command.elementAt(2).equals("A"))
            {
                accu=registerA;
            }else if(command.elementAt(1).equals("A")&&command.elementAt(2).equals("M"))
            {
                registerA=accu;
            }else if(command.elementAt(1).equals("M")&&command.elementAt(2).equals("B"))
            {
                accu=registerB;
            }else if(command.elementAt(1).equals("B")&&command.elementAt(2).equals("M"))
            {
                registerB=accu;
            }
            
            commandCounter=commandCounter+6;
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("AX")) //Dodawanie liczby do rejestru
        {
            if(command.elementAt(1).equals("A"))
            {
                registerA=registerA+BasisLibrary.stringToInt(command.elementAt(2));
            }else if(command.elementAt(1).equals("B"))
            {
                registerB=registerB+BasisLibrary.stringToInt(command.elementAt(2));
            }
            commandCounter=commandCounter+5+command.elementAt(2).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("SX")) //Odejmowanie liczby z rejestru
        {
            if(command.elementAt(1).equals("A"))
            {
                registerA=registerA-BasisLibrary.stringToInt(command.elementAt(2));
            }else if(command.elementAt(1).equals("B"))
            {
                registerB=registerB-BasisLibrary.stringToInt(command.elementAt(2));
            }
            commandCounter=commandCounter+5+command.elementAt(2).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("MX")) //Mno¿enie rejestru razy liczba
        {
            if(command.elementAt(1).equals("A"))
            {
                registerA=registerA*BasisLibrary.stringToInt(command.elementAt(2));
            }else if(command.elementAt(1).equals("B"))
            {
                registerB=registerB*BasisLibrary.stringToInt(command.elementAt(2));
            }
            commandCounter=commandCounter+5+command.elementAt(2).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("MO")) //Przypisywanie rejestrowi liczby
        {
            if(command.elementAt(1).equals("A"))
            {
                registerA=BasisLibrary.stringToInt(command.elementAt(2));
            }else if(command.elementAt(1).equals("B"))
            {
                registerB=BasisLibrary.stringToInt(command.elementAt(2));
            }
            commandCounter=commandCounter+5+command.elementAt(2).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("JP")) //Skok do rozkazu o podanym adresie
        {   
            //tu musi byc metoda wracaj¹ca do odpowiedniego rozkazu.
           skok(BasisLibrary.stringToInt(command.elementAt(1)));
            commandCounter=BasisLibrary.stringToInt(command.elementAt(1));
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("HT")) //Koniec programu
        {
            // jakoœ to wszystko wywalac, tylko nie mam pomyslu jak.
            
            commandCounter=commandCounter+2;
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("CF")) //Tworzenie pliku
        {
        	mainDrive.createFile(command.elementAt(1));
            //createFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("DF")) //Usuwanie pliku
        {
        	mainDrive.deleteFile(command.elementAt(1));
            //deleteFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("OF")) //Otwieranie pliku
        {
        	mainDrive.openFile(command.elementAt(1));
            //openFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("SF")) //Zamykanie pliku
        {
        	mainDrive.closeFile(command.elementAt(1));
            //closeFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("RF")) //Czytanie z pliku
        {
        	//TU ZROBIC
            //write(BasisLibrary.stringToInt(command.elementAt(1)),readFile(command.elementAt(1),BasisLibrary.stringToInt(command.elementAt(2))));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("WF")) //Wpisywanie do pliku
        {
            /*String a = new String();
            try {
                a=readString(command.elementAt(2),command.elementAt(3));
            } catch (Exception e) 
            {
                throw new Exception("Poza zakresem");
            } */
            //writeFile(command.elementAt(1),a);
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("CP")) //Tworzenie procesu
        {
        	Process nowy = pm.fork(pm.mainProcess);
        	nowy.setProcessName(command.elementAt(1));
			nowy.setSizeOfFile(Integer.parseInt(command.elementAt(2)));
			nowy.setFileName(command.elementAt(3));
            commandCounter=commandCounter+3+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("DP")) //Usuwanie procesu
        {
            try {
				pm.kill(pm.getProcessByID(Integer.parseInt(command.elementAt(1))));
			} catch (NumberFormatException | InterruptedException e) {
			}
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("RP")) //Odpalanie procesu
        {
           /* Scheduler a=new Scheduler();
            a.ReadyThread(nowy);*/
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("RM")) //Czytanie komunikatu
        {
            Handler.readfromlist(command.elementAt(1)).readMessage(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("SM")) //Wys³anie komunikatu
        {
            //x.sendMessage(command.elementAt(1),command.elementAt(2),command.elementAt(3));
            Handler.getFromList(command.elementAt(1), command.elementAt(2)).sendMessage(command.elementAt(1), command.elementAt(2), command.elementAt(3));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("CC")) //Tworzenie po³¹czenia
        {
            Connection x= new Connection(command.elementAt(1),command.elementAt(2));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("EC")) //Koñczenie po³¹czenia
        {
            //x.endConnection(command.elementAt(1),command.elementAt(2));
            Handler.getFromList(command.elementAt(1), command.elementAt(2)).endConnection(command.elementAt(1), command.elementAt(2));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("JZ")) //Skok przy zerowej wartoœci rejestru
        {
            if(accu==0)
            {
                skok(BasisLibrary.stringToInt(command.elementAt(1)));
            }
            commandCounter=BasisLibrary.stringToInt(command.elementAt(1));
        }
    /*_______________________________________________________________*/              
    }
}
