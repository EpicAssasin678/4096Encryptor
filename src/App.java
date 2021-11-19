import java.io.InputStream;
import java.lang.Runtime;
import java.util.ArrayList;
public class App {

    /**
     * Author: darkf0x
     * Date: 10/29/21
     * 
     * 4096 Encryptor is an encryption algorthim.
     * TODO: create main object                            
     */


    public static void main(String[] args) throws Exception {
        String progressBar = "%-10s%d" + "%s " + "%s" ;
        String bar =  "";

        for (int i = 0; i < 10; i++) {bar += Character.toString(9649); bar += " ";}
        
        System.out.println(progressBar);
        Runtime runtime = Runtime.getRuntime();
        Process p1; 
        
        int indToUse = 0;
        for (int i = 0;  i <= 10000; i++) {
            System.out.println(String.format(progressBar, "Progress",  i/100, "%", bar));
            try {
                bar = i%1000 == 0 ? bar.substring(0, ((i/1000) * 2)) + (char)9648  +  bar.substring((i/1000 * 2) + 1, 19) : bar; //2011 for old character used 
                System.out.println("\033[H\033[2J"); //could use if for proper use, but not necessary
                System.out.flush();
                ArrayList<Integer> n = new ArrayList<Integer>();
                
            } catch (StringIndexOutOfBoundsException e){
                
            }
            
        }
        
        

        
        
        for (int i = 0; i < 50; i++) {
            System.out.println((char) 9692);
            System.out.println("\033[H\033[2J");
            Thread.sleep(20);
            System.out.println((char) 9693);
            
            System.out.println("\033[H\033[2J");
            Thread.sleep(20);
            System.out.println((char) 9694);            
            System.out.println("\033[H\033[2J");
            Thread.sleep(20);
            System.out.println((char) 9695);
            
            System.out.println("\033[H\033[2J");
            Thread.sleep(20);
        }
    }

}
    /**
             * 
             try {
                 p1 = runtime.exec("cls");
             } catch (Exception e) {
                 
                 e.printStackTrace();
                 
                 break;
             }

        for (int i = 0;  i <= 10000; i++) {
            System.out.println(String.format(progressBar, "Progress",  i/100, "%", bar));
            bar = i%1000 == 0 ? bar + (char)9634  + " ": bar; //2011 for old character used 
            System.out.println("\033[H\033[2J");
            System.out.flush();
        }              
             */