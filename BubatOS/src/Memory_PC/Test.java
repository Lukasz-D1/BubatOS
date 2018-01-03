package Memory;

import java.io.IOException;
import java.util.Vector;

public class Test {
	public static void main(String Args[]) throws IOException {
		PageTab p1 = new PageTab("Test.txt", 20);
		// MassMemory.show();
		Vector<String> Command = p1.getCommand(0);
		for (String a : Command) {
			System.out.print(a + ",");
		}
		System.out.println();
		Command = p1.getCommand(1);
		for (String a : Command) {
			System.out.print(a + ",");
		}
		System.out.println();
		Command = p1.getCommand(2);
		for (String a : Command) {
			System.out.print(a + ",");
		}
		System.out.println();
		char[][] mem = Memory.getAll();
		System.out.println("Memory:");
		for (int i = 0; i != 4; ++i) {
			System.out.print(i * 16 + "-" + (i + 1) * 16 + ":");
			for (int j = 0; j != 16; ++j) {
				System.out.print(mem[i][j]);
			}
			System.out.println();
		}
		System.out.println("[end of Memory]");
		PageTab p2 = new PageTab("Test2.txt", 67);
		// MassMemory.show();
		for (byte i = 0; i != 7; ++i) {
			Command = p2.getCommand(i);
			System.out.print("Command nr " + i + " : ");
			for (String a : Command) {
				System.out.print(a + ",");
			}
			System.out.println();
		}
		for (byte i = 6; i >= 0; --i) {
			Command = p2.getCommand(i);
			System.out.print("Command nr " + i + " : ");
			for (String a : Command) {
				System.out.print(a + ",");
			}
			System.out.println();
		}
		System.out.println("Memory:");
		for (int i = 0; i != 4; ++i) {
			System.out.print(i * 16 + "-" + (i + 1) * 16 + ":");
			for (int j = 0; j != 16; ++j) {
				System.out.print(mem[i][j]);
			}
			System.out.println();
		}
		System.out.println("[end of Memory]");
		mem=p2.getProcessMemory();
		System.out.println("p2's memory:");
		for(int i=0; i!=mem.length; ++i) {
			System.out.print("page " + i + ":");
			for(int j=0; j!=16; ++j) {
				System.out.print(mem[i][j]);
			}
			System.out.println();
		}
		p1.finalize();
		//MassMemory.show();
	}
}
