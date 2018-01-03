package interpreter;

import com.sun.javafx.image.impl.ByteBgraPre;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import java.util.List;
import java.util.Vector;
import jdk.nashorn.internal.runtime.regexp.RegExpMatcher;



public class Interpreter 
{
    public int commandCounter;
    public int registerA;
    public int registerB;
    private int accu;
    
    Interpreter()
    {
        registerA=0;
        registerB=0;
        commandCounter=0;
    }
    
    /*Pobranie rejestrów*/
    public void getRegister(int registerA, int registerB, int commandCounter)
    {
        this.registerA=registerA;
        this.registerB=registerB;
        this.commandCounter=commandCounter;
    }
    /*Odsyłanie zmienionych wartości rejestrów i licznika rozkazów, jako tablicę intów.
      Jeżeli potrzebuje ktoś inaczej, to dajcie znać.*/
    public int[] setRegister(int registerA, int registerB)
    {
        int[]registers = null;
        registers[0]=registerA;
        registers[1]=registerB;
        registers[2]=commandCounter;
        return registers;
    }
    /* Założenie jest takie, że dostaję rozkaz jako tablicę stringów (1,2,3 albo 4 elementową)
    i pole zerowe to nazwa rozkazu, a pozostałe pola to argumenty.*/
    public void getCommand(Vector<String> command)
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
        }else if(command.elementAt(0).equals("MU")) // Mnożenie rejestrów
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
        }else if(command.elementAt(0).equals("MV")) //Przypisywanie wartości rejestrom
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
        }else if(command.elementAt(0).equals("MX")) //Mnożenie rejestru razy liczba
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
            //tu musi byc metoda wracająca do odpowiedniego rozkazu.
           skok(command.elementAt(1));
            commandCounter=BasisLibrary.stringToInt(command.elementAt(1));
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("HT")) //Koniec programu
        {
            // jakoś to wszystko wywalac, tylko kurde nie mam pomyslu jak.
            commandCounter=commandCounter+2;
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("CF")) //Tworzenie pliku
        {
            createFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("DF")) //Usuwanie pliku
        {
            deleteFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("OF")) //Otwieranie pliku
        {
            openFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("SF")) //Zamykanie pliku
        {
            closeFile(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("RF")) //Czytanie z pliku
        {
            write(BasisLibrary.stringtoint(command.elementAt(1)),readFile(command.elementAt(1),BasisLibrary.stringToInt(command.elementAt(2))));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("WF")) //Wpisywanie do pliku
        {
            writeFile(command.elementAt(1),read(command.elementAt(2),command.elementAt(3)));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("CP")) //Tworzenie procesu
        {
            Process nowy = pm.fork(init);
            nowy.setParentID(command.elementAt(2));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/    
        }else if(command.elementAt(0).equals("DP")) //Usuwanie procesu
        {
            command.elementAt(1).setStan(terminate);
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/
        }else if(command.elementAt(0).equals("RP")) //Odpalanie procesu
        {
            Scheduler a=new Scheduler();
            a.ReadyThread(nowy);
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("RM")) //Czytanie komunikatu
        {
            x.readMessage(command.elementAt(1));
            commandCounter=commandCounter+3+command.elementAt(1).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("SM")) //Wysłanie komunikatu
        {
            x.sendMessage(command.elementAt(1),command.elementAt(2),command.elementAt(3));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("CC")) //Wysłanie komunikatu
        {
            Connection x= new Connection(command.elementAt(1),command.elementAt(2));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("EC")) //Wysłanie komunikatu
        {
            x.endConnection(command.elementAt(1),command.elementAt(2));
            commandCounter=commandCounter+5+command.elementAt(1).length()+command.elementAt(2).length()+command.elementAt(3).length();
    /*_______________________________________________________________*/        
        }else if(command.elementAt(0).equals("JZ")) //Skok przy zerowej wartości rejestru
        {
            if(accu==0)
            {
                skok(command.elementAt(1));
            }
            commandCounter=BasisLibrary.stringToInt(command.elementAt(1));
        }
    /*_______________________________________________________________*/              
    }
}