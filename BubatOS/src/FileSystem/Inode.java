package inodes;
import java.util.Date;
public class Inode {
	//str 800 i rodzia³ 11.7
	//jest rekordem
	//zawiera:
	//id_u¿ytkownika*
	//id grupy u¿ytkowników pliku
	//czas ostatniej modyfikacji i czas ostaniego dostêpu do pliku
	//typ pliku(zwyk³y, katalog)
	//miejsce  15 wskazników do bloków dyskowych z danymi pliku
		//->12 na tablica? -bloki bezpoœredine - adreswy bloków dyskowych z danymi
			//u nas blok wynosi 32B, wiêc mo¿na zaadresowaæ bezpoœrednio 384B
			//ale i tak plik ma ograniczon¹ wielkosæ 40B
		//->3 - bloki poœrednie - u nas nie wystêpuj¹
	//int owners_ids; //mo¿e domyœ³lnie root?
	//int timestamps;
	String month;
	int day;
	int hour;
	int minute;
	
	int type_of_file; //katalog czy zwyk³y plik ?????????????????
	int sizeF;
	int LinkCounter; // licznik trwa³ych dowi¹zañ
	//zerowy indeks wskazuje bezpoœrednio na blok dyskowy z danymi. 2 nastêpne wskazuj¹ na bloki indeksowe.
	int [] inode_table  = new int[2];
}
