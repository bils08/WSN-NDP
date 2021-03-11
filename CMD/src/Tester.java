
import java.util.Scanner;


public class Tester{
	private Scanner scanner;
//	private BufferedWriter writer;
	
	
	/**
	 * Method yang akan dijalankan pertama kali saat program dijalankan.
	 * Waktu root akan terlebih dulu disinkronisasikan dengan waktu
	 * milik perangkat yang menjadi antarmuka.
	 */
	public static void main(String[] args) throws Exception{
		int a = 1;
		int e = 1;
		while(a<8) {
			a=a+1;
			if(a<4) {
				e=e+a;
			} else {
				e=e+2;
			}
		}
		System.out.println(e);
	}
}
