package inodes;
import java.util.*;
import inodes.Drive;
import inodes.FileEntry;
import inodes.Inode;
public class MAIN_TEST {
	public static void main(String[] args) throws InterruptedException {
		Drive mainDrive = new Drive();
		//Wyswitla wolne bloki dyskowe sposrod 32
		
		//System.out.println(mainDrive.openFile("p1", "txt"));
		mainDrive.createFile("p1");
		//mainDrive.createFile("p2");
		mainDrive.ListDirectory();
//		aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
//		bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
//		cccccccccccccccccccccccccccccccc
//		dddddddddddddddddddddddddddddddd
//		eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
//		ffffffffffffffffffffffffffffffff
//		gggggggggggggggggggggggggggggggg
//		hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
//		iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
//		jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
//		kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk
//		llllllllllllllllllllllllllllllll
//		mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
//		nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
//		oooooooooooooooooooooooooooooooo
//		pppppppppppppppppppppppppppppppp
//		rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
//		ssssssssssssssssssssssssssssssss
		String xd="aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";//40->32->8a do bloku poœredniego
		String xd_max="aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
					+"ccccccccccccccccccccccccccccccccdddddddddddddddddddddddddddddddd"
					+"eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeffffffffffffffffffffffffffffffff"
					+"gggggggggggggggggggggggggggggggghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
					+"iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiijjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"
					+"kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkllllllllllllllllllllllllllllllll"
					+"mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
					+"oooooooooooooooooooooooooooooooopppppppppppppppppppppppppppppppp"
					+"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrssssssssssssssssssssssssssssssss"
					;
		String xd2="aaaa"
				+ "aaaa"
				+ "aaaa"
				+ "aaaa"
				+ "aaaa"
				+ "aaaa"
				+ "aaaa"
				+ "aaaa"
				+ "bbbb"//1 numer w bloku indeksowym 
				+ "bbbb"
				+ "bbbb"
				+ "bbbb"
				+ "bbbb"
				+ "bbbb"
				+ "bbbb"
				+ "bbbb"
				+ "cccc";//2 numer w bloku indeksowym
//		mainDrive.writeFile("p1", xd2);
//		mainDrive.writeFile("p2", xd2);
//		//mainDrive.writeFile("p2", xd_max);//Brak miejsca na dysku
//		
//		mainDrive.ListDirectory();
//		mainDrive.printDrive();
//		mainDrive.deleteFile("p2");
//		System.out.println("Po usuniêciu");
//		mainDrive.ListDirectory();
//		mainDrive.printDrive();
//		
//		mainDrive.createFile("p3");
//		mainDrive.writeFile("p3", xd_max);
//		mainDrive.ListDirectory();
//		mainDrive.printDrive();
//		
//		mainDrive.readFile("p3",0,0);
		
		//TESTOWANIE APPEND
		String ap="bbbbbbbbbbbbbbbbbbbbbbccd78";//27
		String ap2=xd_max;//25
		
		//if(mainDrive.openFile("p1")>=0)
		//{
			
//			String read_amount_test="123456789a123456789b123456789c123456789d123456789e123456789f123456789g";//70
//			String read_amount_test2="123456789a123456789b123456789c";//27
//			String imie="adam_chrzanowski";//16
//			mainDrive.renameFile("p1", "p2");
//			mainDrive.ListDirectory();
//			//otwarcie
//			mainDrive.openFile("p2");
//			mainDrive.writeFile("p2", read_amount_test);
//			mainDrive.readFile("p2", 15);
//			mainDrive.readFile("p2", 15);
//			mainDrive.readFile("p2", 1);
//			mainDrive.readFile("p2", 1);
//			
//			mainDrive.readFile("p2", 1);
//			mainDrive.readFile("p2", 50);
//			mainDrive.readFile("p2", 15);
//			mainDrive.readFile("p2", 2);
//			mainDrive.appendFile("p2", imie);
//			mainDrive.readFile("p2", 4);
//			mainDrive.readFile("p2", 5);
//			mainDrive.readFile("p2", 5);
//			//mainDrive.printDiskBlock(2);
//			//mainDrive.printDiskBlock(3);
//			mainDrive.readFile("p2", 1);
//			mainDrive.readFile("p2", 0);
//			mainDrive.readFile("p2", 2);
//			//zamkniêcie
//			mainDrive.closeFile("p2");
//			//sprawdzenie poprawnoœæi close
//			mainDrive.writeFile("p2", xd);//komunikat, ¿e ju¿ zamkniêty
			
			//mainDrive.createFile("p3");
			//mainDrive.openFile("p3");
			//mainDrive.writeFile("p3", xd);
			//mainDrive.closeFile("p3");
			//mainDrive.ListDirectory();
			//mainDrive.printInodeInfo("p3");
			//mainDrive.printInodeInfo("p2");
			//mainDrive.printDiskBlock(2);
			//mainDrive.printDiskBlock(3);
			
		//TEST DOWI¥ZAÑ
		
		mainDrive.createLink("p2", "p1");
		mainDrive.ListDirectory();
		mainDrive.openFile("p1");
		mainDrive.writeFile("p1", "asd");
		mainDrive.ListDirectory();
		mainDrive.closeFile("p1");
		mainDrive.writeFile("p2", "kada");
		mainDrive.openFile("p2");
		mainDrive.appendFile("p2", "kada");
		mainDrive.ListDirectory();
		mainDrive.readFile("p2", 50);
		mainDrive.closeFile("p1");
		mainDrive.closeFile("p2");//komunikat ju¿ zamkniêty ->OK
		mainDrive.printInodeInfo("p2");
		mainDrive.deleteFile("p1");
		mainDrive.ListDirectory();
		mainDrive.openFile("p2");
		mainDrive.readFile("p2", 2);
		mainDrive.deleteFile("p2");
		mainDrive.closeFile("p2");
		mainDrive.deleteFile("p2");
		mainDrive.ListDirectory();
		
		
		
		
		
		
		
		
		
		
//		}
//		else if(mainDrive.openFile("p1") == -2)
//		{
//			System.out.println("Nie istnieje plik o takiej nazwie");	
//		}
//		else if(mainDrive.openFile("p1") == -1)
//		{
//			System.out.println("Plik jest wykorzystywany przez inny proces");	
//		}
//		if(mainDrive.closeFile("p1")>=0)
//		{
//			
//		}
		
			
//		Hashtable<String,Integer> catalog = new Hashtable<String,Integer>(2);
//	
//		catalog.put("a", 3);
//		catalog.put("g", 2);
//		catalog.put("c", 3);
//		for(Map.Entry m : catalog.entrySet()){
//			System.out.println(m.getKey() + " " + m.getValue());
//		}
//		//Object value = catalog.get("f");
//		//if(catalog.equals("a"))
//		if(catalog.containsKey("f"))
//			System.out.println(catalog.get("a"));
	
//		
//		ArrayList<Integer> d = new ArrayList<Integer>();
//		d.add(44);
//		
//		//if(d.get(null))
//			
//			d.add(d.size(), 55);
//			System.out.println(d);
	}

}
