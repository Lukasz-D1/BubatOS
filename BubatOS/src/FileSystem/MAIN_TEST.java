package inodes;
import java.util.*;
import inodes.Drive;
import inodes.FileEntry;
import inodes.Inode;
public class MAIN_TEST {
	public static void main(String[] args) {
		Drive mainDrive = new Drive();
		//Wyswitla wolne bloki dyskowe sposrod 32
		
		//System.out.println(mainDrive.openFile("p1", "txt"));
		mainDrive.createFile("p1");
		mainDrive.createFile("p2");
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
		String xd="aaaaaaa";
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
		mainDrive.writeFile("p1", xd2);
		mainDrive.writeFile("p2", xd2);
		//mainDrive.writeFile("p2", xd_max);//Brak miejsca na dysku
		
		mainDrive.ListDirectory();
		mainDrive.printDrive();
		mainDrive.deleteFile("p2");
		System.out.println("Po usuniêciu");
		mainDrive.ListDirectory();
		mainDrive.printDrive();
		
		mainDrive.createFile("p3");
		mainDrive.writeFile("p3", xd2);
		mainDrive.ListDirectory();
		mainDrive.printDrive();
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
	
//		Calendar cal = Calendar.getInstance();
//		System.out.println(cal.get(Calendar.YEAR));
//		System.out.println(cal.get(Calendar.MONTH));
//		System.out.println(cal.get(Calendar.DAY_OF_MONTH));
//		
//		System.out.println(cal.get(Calendar.HOUR_OF_DAY));
//		System.out.println(cal.get(Calendar.MINUTE));
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
