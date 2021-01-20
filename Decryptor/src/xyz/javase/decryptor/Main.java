package xyz.javase.decryptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class Main {

	public static void main(String args[]) {
		
		if (args.length != 1) {
			System.out.println("Please include the key to decrypt with in args[0] \n");
			return;
		}
		
		String key = args[0];
		searchAllDirectories("/", key);
	}
	
	private static void searchAllDirectories(String path, String key) {
		
		List<String> ls = getLS(path);

		for (String dir : ls) {
			File[] files = new File(dir).listFiles();
			if (files.length > 0)
			for (File file : files) {
				if (file.isDirectory()) {
					searchAllDirectories(file.getAbsolutePath() + file.getName(), key);
				} else {

	        		
	        		if (file.getName().contains(".encrypted"))
	        		try {
						decryptedFile(key, file.getAbsolutePath(), file.getAbsolutePath().replace(".encrypted", ""));
					} catch (InvalidKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	
	/**
	 * @param String path
	 * runs the LS command using an exec call
	 * */
	public static List<String> getLS(String path) {
		
		List<String> results = new ArrayList<String>();
		

        
        String s;
        Process p;
        //Try to run the ls command. We are only targeting unix systems because windows defender will block this from running
        //linux will not. We are getting a copy of every file we can *read* 
        try {
            p = Runtime.getRuntime().exec("ls -aF " + path);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                results.add(s);
               
            }
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {}
        

        
        return results;
	}
	
	
	 public static void decryptedFile(String secretKey, String fileInputPath, String fileOutPath)
			   throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException,
			   IllegalBlockSizeException, BadPaddingException {
			  SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
			  Cipher cipher = Cipher.getInstance("AES");
			  cipher.init(Cipher.DECRYPT_MODE, key);

			  File fileInput = new File(fileInputPath);
			  FileInputStream inputStream = new FileInputStream(fileInput);
			  byte[] inputBytes = new byte[(int) fileInput.length()];
			  inputStream.read(inputBytes);

			  byte[] outputBytes = cipher.doFinal(inputBytes);

			  File fileEncryptOut = new File(fileOutPath);
			  FileOutputStream outputStream = new FileOutputStream(fileEncryptOut);
			  outputStream.write(outputBytes);

			  inputStream.close();
			  outputStream.close();
			  
			  System.out.println("File successfully decrypted!");
			  System.out.println("New File: " + fileOutPath);
			  
			  fileInput.delete();
			 }
}
