package Memory;

import java.io.IOException;
import java.util.List;

public class Test {
	public static void main(String Args[]) throws IOException {
		PageTab p1=new PageTab("Test.txt",20);
		MassMemory.show();
		List<String> Command=p1.getCommand(0);
		for(String a:Command) {
			System.out.print(a+",");
		}
		System.out.println();
		Command=p1.getCommand(1);
		for(String a:Command) {
			System.out.print(a+",");
		}
		System.out.println();
		Command=p1.getCommand(2);
		for(String a:Command) {
			System.out.print(a+",");
		}
		System.out.println();
		char[][] mem=Memory.getAll();
		System.out.println("Memory:");
		for(int i=0; i!=4;++i) {
			System.out.print(i*16+"-"+(i+1)*16+":");
			for(int j=0; j!=16; ++j) {
				System.out.print(mem[i][j]);
			}
			System.out.println();
		}
 	}
}
