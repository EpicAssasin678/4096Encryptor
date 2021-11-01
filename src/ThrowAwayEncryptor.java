import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.FileWriter;
import java.lang.Runtime;


public class ThrowAwayEncryptor {

    /**
     * Encryptor has no objext -> all functional programming to accomplish what we need.
     * Different encryption algorithms can be accessed through the main class itself. 
     * TODO: create a thread safe basicEncrypt method using fileReader instead of Scanner
     * TODO: create method for UTF-8/UTF-16 encoding 
     * TODO: make void method for multiple files for folder action (or application) using File[] and decrypt File[] with foreach
     * 
     * For multiple strengths of encryption, can use differing bounds and check for bounds in main method.
     * Option for loading bar?
     */

    int[] rejectMap = {}; //use to increase amount of availble characters to encode 
    String inputPATH;
    File input;
    File output;
    File key;
    FileReader fileReader;
    FileWriter fileWriter;
    FileWriter logWriter;
    FileWriter keyWriter;
    Scanner buffer;
    HashMap<Character, Integer> characterMapping = new HashMap<Character, Integer>();
    int[] hoppingMap;
    String resoluteKey = ""; //used for final output of key and interpretation

    public ThrowAwayEncryptor () {

    }


    public void encrypt (File input, File output) throws Exception {
        //input only files into encryption method
        encrypt(input, output, 127, false, null, null, "UTF-8");
    }
    public void encrypt (File input, File output, int bound) throws Exception {
        encrypt(input, output, bound, false, null, null, "UTF-8");
    }
    public void encrypt (File input, File output, int bound, boolean hoppingFlag) throws Exception {
        encrypt(input, output, bound, hoppingFlag, null, null, "UTF-8");
    }
    public void encrypt (File input, File output, int bound, boolean hoppingFlag, File log) throws Exception {
        encrypt(input, output, bound, hoppingFlag, log, null, "UTF-8");
    }
    public void encrypt (File input, File output, int bound, boolean hoppingFlag, File log, String keyFileName) throws Exception {
        encrypt(input, output, bound, hoppingFlag, log, keyFileName, "UTF-8");
    }


    /**
     * True encryption method.
     * @param input
     * @param output
     * @param bound
     * @param hoppingFlag designates whether to use dictionary hopping algorithm to further encrypt data 
     * @param log
     * @param keyFileName
     * @throws Exception
     * 
     * Will always use the static variables of class instance.
     */
    public void encrypt (File input, File output, int bound, boolean hoppingFlag, File log, String keyFileName, String standardCharset) throws Exception {
        //input only files into encryption method
        Character currentChar;
        Random random = new Random();
        boolean doLog = (log != null);
        //for storing size of stream wihout using costly collection methods each time that degrade performance , depends on charset
        
        buffer = new Scanner(input);
        fileReader = new FileReader(input);
        Charset charset = Charset.forName(standardCharset);

        log = (log == null) ? new File("encryptionlog.txt") : log;
        log.createNewFile();
        key = (keyFileName == null) ? new File("key.txt"): new File(keyFileName);
        key.createNewFile();

        keyWriter = new FileWriter(keyFileName);
        logWriter = new FileWriter(log);

        try { 
            fileWriter = new FileWriter(output, charset);
        } catch (FileNotFoundException exception) {
            output.createNewFile();
            fileWriter = new FileWriter(output, charset);
        }

        while (fileReader.ready()) {
            currentChar = (char) fileReader.read();
            if (characterMapping.containsKey(currentChar)) {
                fileWriter.write(characterMapping.get(currentChar));

                System.out.println((char) characterMapping.get(currentChar).intValue());
                if (doLog) {
                    logWriter.write("\nCharacter encountered already, mapping character " + currentChar + characterMapping.get(currentChar));
                }
            } else {
                //create random character mapping entry
                characterMapping.put(currentChar, random.nextInt(bound) + 33 );
                //write current character to mapping and increase size 
                fileWriter.write(characterMapping.get(currentChar));

                System.out.println("New character mapped: " + (char) characterMapping.get(currentChar).intValue());
                if (doLog) {
                    logWriter.write("\nUpdated character mapping: \n" + characterMapping.toString());
                }
            }
        }
        //keyWriter.write(characterMapping.toString());


        System.out.println("Encryption completed succesfully.");
        keyWriter.write( generateResoluteKey(characterMapping, logWriter) );
        fileWriter.close();
        keyWriter.close();
        if (doLog) {
            logWriter.close();
        }
    }
    
    public String generateResoluteKey(HashMap<Character, Integer> mapping, FileWriter logWriter) throws Exception{

        logWriter.write("\nGenerating resolute key.");
        System.out.println("Generating resoluteKey.");

        mapping.forEach((ch, n) -> {resoluteKey += (int) ch + ":" + n + ",";});

        logWriter.write("\n" + "[" + resoluteKey + "]");
        logWriter.write("\nGenerated resolute key sucessfully.");

        return resoluteKey;
    }

    /**
     * Wipes all instance variables/class variables of the Encryptor class by setting them all to null.
     */
    public void wipe() {
        rejectMap = null;
        inputPATH = null;        
        input = null;
        output = null;
        key = null;
        fileReader = null;
        fileWriter = null;
        logWriter = null;
        keyWriter = null;
        buffer = null;
        characterMapping = new HashMap<Character, Integer>();
        hoppingMap = null;
        resoluteKey = null;
    }

    public void decrypt (File input, File output, File key, File log, boolean throwAway, boolean verbose) throws Exception {
        wipe();
        output.createNewFile(); //wont't occur if it already has the file
        fileReader = new FileReader(input);
        fileWriter = new FileWriter(output);
        logWriter = new FileWriter(log);
        Scanner keyReader = new Scanner(key);
        FileReader keyReader2 = new FileReader(key);

        char currentChar;
        
        if (verbose) {
            //!make estimate of characters to read
            input.getTotalSpace();
        }

        String outputmsg ="";
        String clearscrn = "\033[H\033[2J";
        String progressBar = "%-10s%d" + "%s " + "%s" ;
        String bar = "";
        //System.out.println(String.format(progressBar, "Progress",  i/100, "%", bar));
        System.out.println(outputmsg += "Beggining Decryption.\nInterperating resolute key.");
        //System.out.println(keyReader2.read());
        //System.out.println(keyReader.nextLine());
        //Make mapping based on string of resolutekey.split(",").split(":") and go by pairs of two, i += 2 i = 0, also reverse order for O(1) time complexity decrpytion
        resoluteKey = keyReader.nextLine();
        for (String pair: resoluteKey.split(",") ) {
            String[] mapPair = pair.split(":"); //check for the trailing "," at the end of each resolute key
            characterMapping.put((char) Integer.parseInt(mapPair[1]), Integer.parseInt(mapPair[0]));
        }
        System.out.println(outputmsg += "\nResolute key interperated. Character mapping has been generated.\nDecrypting input.");
        
        while(fileReader.ready()) {
            currentChar = (char) fileReader.read();
            try {
                fileWriter.write(characterMapping.get(currentChar));
            } catch (NullPointerException except) {
                System.out.println("KEY corrupt or incorrect format.");
                except.printStackTrace();
            }
        }
        System.out.println(outputmsg += "\nDecryption complete.");

        logWriter.write(outputmsg);
        keyReader.close();
        logWriter.close();
        fileWriter.close();

        fileReader.close();
        keyReader.close();
        
    }

    public static void main(String[] args) throws Exception {

        ThrowAwayEncryptor encryptor = new ThrowAwayEncryptor();
        encryptor.encrypt(new File("input.txt"), new File("output.txt"), 127, false, new File("log.txt"), "key.txt", "UTF-8");
        encryptor.decrypt(new File("output.txt"), new File("decrypt_output.txt"), new File("key.txt"), new File("decrypt_log.txt"), false, false);

    }
}
