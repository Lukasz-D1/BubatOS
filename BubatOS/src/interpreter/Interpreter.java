package interpreter;

import com.sun.javafx.image.impl.ByteBgraPre;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import jdk.nashorn.internal.runtime.regexp.RegExpMatcher;

public class Interpreter 
{
    public int commandCounter;
    public int registerA;
    public int registerB;
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
    public void getCommand(String command[])
    {     
       if(command[0].equals("AD")) // Dodawanie rejestrów
       {
           if(command[1].equals("A")&&command[2].equals("A"))
           {
               registerA=registerA+registerA;
           } else if (command[1].equals("A")&&command[2].equals("B"))
           {
               registerA=registerA+registerB;
           } else if (command[1].equals("B")&&command[2].equals("B"))
           {
               registerB=registerB+registerB;
           } else if (command[1].equals("B")&&command[2].equals("A"))
           {
               registerB=registerA+registerB;
           }
           commandCounter=commandCounter+6;
    /*_______________________________________________________________*/         
       }else if(command[0].equals("SB")) //Odejmowanie rejestrów
        {
            if(command[1].equals("A")&&command[2].equals("A"))
            {
                registerA=0;
            }else if(command[1].equals("A")&&command[2].equals("B"))
            {
                registerA=registerA-registerB;
            }else if(command[1].equals("B")&&command[2].equals("B"))
            {
                registerB=0;
            }else if(command[1].equals("B")&&command[2].equals("A"))
            {
                registerB=registerB-registerA;
            }
            commandCounter=commandCounter+6;
    /*_______________________________________________________________*/
        }else if(command[0].equals("MU")) // Mnożenie rejestrów
        {
            if(command[1].equals("A")&&command[2].equals("A"))
            {
                registerA=registerA*registerA;
            }else if(command[1].equals("A")&&command[2].equals("B"))
            {
                registerA=registerA*registerB;
            }else if(command[1].equals("B")&&command[2].equals("B"))
            {
                registerB=registerB*registerB;
            }else if(command[1].equals("B")&&command[2].equals("A"))
            {
                registerB=registerB*registerA;
            }
            commandCounter=commandCounter+6;
    /*_______________________________________________________________*/
        }else if(command[0].equals("MV")) //Przypisywanie wartości rejestrom
        {
            if(command[1].equals("A")&&command[2].equals("A"))
            {
                registerA=registerA;
            }else if(command[1].equals("A")&&command[2].equals("B"))
            {
                registerA=registerB;
            }else if(command[1].equals("B")&&command[2].equals("B"))
            {
                registerB=registerB;
            }else if(command[1].equals("B")&&command[2].equals("A"))
            {
                registerB=registerA;
            }
            commandCounter=commandCounter+6;
    /*_______________________________________________________________*/
        }else if(command[0].equals("AX")) //Dodawanie liczby do rejestru
        {
            if(command[1].equals("A"))
            {
                registerA=registerA+BasisLibrary.stringToInt(command[2]);
            }else if(command[1].equals("B"))
            {
                registerB=registerB+BasisLibrary.stringToInt(command[2]);
            }
            commandCounter=commandCounter+5+command[2].length();
    /*_______________________________________________________________*/
        }else if(command[0].equals("SX")) //Odejmowanie liczby z rejestru
        {
            if(command[1].equals("A"))
            {
                registerA=registerA-BasisLibrary.stringToInt(command[2]);
            }else if(command[1].equals("B"))
            {
                registerB=registerB-BasisLibrary.stringToInt(command[2]);
            }
            commandCounter=commandCounter+5+command[2].length();
    /*_______________________________________________________________*/
        }else if(command[0].equals("MX")) //Mnożenie rejestru razy liczba
        {
            if(command[1].equals("A"))
            {
                registerA=registerA*BasisLibrary.stringToInt(command[2]);
            }else if(command[1].equals("B"))
            {
                registerB=registerB*BasisLibrary.stringToInt(command[2]);
            }
            commandCounter=commandCounter+5+command[2].length();
    /*_______________________________________________________________*/
        }else if(command[0].equals("MO")) //Przypisywanie rejestrowi liczby
        {
            if(command[1].equals("A"))
            {
                registerA=BasisLibrary.stringToInt(command[2]);
            }else if(command[1].equals("B"))
            {
                registerB=BasisLibrary.stringToInt(command[2]);
            }
            commandCounter=commandCounter+5+command[2].length();
    /*_______________________________________________________________*/
        }else if(command[0].equals("JP")) //Skok do rozkazu o podanym adresie
        {   
            //tu musi byc metoda wracająca do odpowiedniego rozkazu.
           skok(command[1]);
            commandCounter=BasisLibrary.stringToInt(command[1]);
    /*_______________________________________________________________*/
        }else if(command[0].equals("HT")) //Koniec programu
        {
            // jakoś to wszystko wywalac, tylko kurde nie mam pomyslu jak.
            commandCounter=commandCounter+2;
    /*_______________________________________________________________*/    
        }else if(command[0].equals("CF")) //Tworzenie pliku
        {
            createFile(command[1]);
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/    
        }else if(command[0].equals("DF")) //Usuwanie pliku
        {
            deleteFile(command[1]);
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/    
        }else if(command[0].equals("OF")) //Otwieranie pliku
        {
            openFile(command[1]);
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/    
        }else if(command[0].equals("SF")) //Zamykanie pliku
        {
            closeFile(command[1]);
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/    
        }else if(command[0].equals("RF")) //Czytanie z pliku
        {
            write(BasisLibrary.stringtoint(command[1]),readFile(command[1],BasisLibrary.stringToInt(command[2])));
            commandCounter=commandCounter+5+command[1].length()+command[2].length()+command[3].length();
    /*_______________________________________________________________*/    
        }else if(command[0].equals("WF")) //Wpisywanie do pliku
        {
            writeFile(command[1],read(command[2],command[3]));
            commandCounter=commandCounter+5+command[1].length()+command[2].length()+command[3].length();
    /*_______________________________________________________________*/
        }else if(command[0].equals("CP")) //Tworzenie procesu
        {
            Process nowy = init.fork(command[1]);
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/    
        }else if(command[0].equals("DP")) //Usuwanie procesu
        {
            command[1].setStan(terminate);
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/
        }else if(command[0].equals("RP")) //Odpalanie procesu
        {
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/        
        }else if(command[0].equals("RC")) //Czytanie komunikatu
        {
            readMessage(command[1]);
            commandCounter=commandCounter+3+command[1].length();
    /*_______________________________________________________________*/        
        }else if(command[0].equals("SC")) //Wysłanie komunikatu
        {
            sendMessage(command[1],command[2],command[3]);
            commandCounter=commandCounter+5+command[1].length()+command[2].length()+command[3].length();
    /*_______________________________________________________________*/        
        }else if(command[0].equals("JZ")) //Skok przy zerowej wartości rejestru
        {
            if(registerA==0)
            {
                skok(command[1]);
            }
            commandCounter=BasisLibrary.stringToInt(command[1]);
        }
    /*_______________________________________________________________*/              
    }
}
