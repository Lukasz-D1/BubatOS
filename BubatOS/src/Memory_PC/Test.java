package Memory;

public class Test {
	public static void main(String Args[]) {
		char[] data1= {'1','2','3','4','5','6','7','8','9'};
		char[] data2 = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't' };
		PageTab p1=new PageTab(data1);
		//MassMemory.show();
		PageTab p2=new PageTab(data2);
		//MassMemory.show();
		System.out.println("I read 1 byte from address 3 in p1:" + p1.read(3, 1)[0]);
		char[] re=p2.read(3, 17);
		System.out.print("I read 17 bytes from address 3 in p2:");
		for(char a:re) {
			System.out.print(a);
		}
		System.out.println();
		char[] data3 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
		PageTab p3=new PageTab(data3);
		//MassMemory.show();
		re=p3.read(0, 30);
		System.out.print("I read 30 bytes from address 0 in p3:");
		for(char a:re) {
			System.out.print(a);
		}
		System.out.println();
		//MassMemory.show();
		char[][] o=Memory.getAll();
		System.out.println("State of all physical memory:");
		for(byte i=0; i!=4; ++i) {
			System.out.print(i*16+"-"+(i*16+15)+":");
			for(byte j=0; j!=16; ++j) {
				System.out.print(o[i][j]);
			}
			System.out.println();
		}
		char[] wrData = {'A', 'B', 'C', 'D', 'E', 'F'};
		p3.write(0, wrData);
		re=p3.read(0, 30);
		System.out.print("I read 30 bytes from address 0 in p3:");
		for(char a:re) {
			System.out.print(a);
		}
		System.out.println();
		o=Memory.getAll();
		System.out.println("State of all physical memory:");
		for(byte i=0; i!=4; ++i) {
			System.out.print(i*16+"-"+(i*16+15)+":");
			for(byte j=0; j!=16; ++j) {
				System.out.print(o[i][j]);
			}
			System.out.println();
		}
		//MassMemory.show();
	}
}
