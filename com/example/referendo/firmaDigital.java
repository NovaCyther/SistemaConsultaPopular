package com.example.proyvotaciones;

import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.swing.JFileChooser;

import com.vaadin.ui.Component;


public class firmaDigital {
	KeyPairGenerator keyGen;
	byte[] key;
	public DBManager manejoDB;
	public firmaDigital(){
		manejoDB=new DBManager();
	}
	

	
	public boolean firmar(String filename,String cedula,byte[] textoFirmar){
		byte[] datosFirmados=new byte[1024];
		boolean verificado=false;
		try {

		FileInputStream sigfis = new FileInputStream("C:/Users/lbarboza/Desktop/eclipse/"+filename);
		byte[] privateKeyFromFile = new byte[sigfis.available()];
		sigfis.read(privateKeyFromFile);
		sigfis.close();
		

		
		
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privateKeyFromFile);
		KeyFactory keyFactory=KeyFactory.getInstance("DSA","SUN");
		PrivateKey llavePrivada;
		llavePrivada = keyFactory.generatePrivate(privKeySpec);
		
		Signature dsa= Signature.getInstance("SHA1withDSA","SUN");
		dsa.initSign(llavePrivada);
		
		dsa.update(textoFirmar);

		datosFirmados=dsa.sign();
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
		verificado=confirmarVoto(cedula,textoFirmar,datosFirmados);
		return verificado;
	}
	
	
	public boolean confirmarVoto(String cedula,byte[] textoFirmar,byte[] datos){
		boolean verificado=false;
		try {
			byte[] encKey =manejoDB.getLLavePublica(cedula);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA","SUN");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			
			Signature sig= Signature.getInstance("SHA1withDSA","SUN");
			sig.initVerify(pubKey);
			sig.update(textoFirmar);
			verificado= sig.verify(datos);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return verificado;
	}
	
	public void testKeys(String cedula){
		try {
			byte[] encKey =manejoDB.getLLavePublica(cedula);	
			
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
			KeyFactory keyFactory=KeyFactory.getInstance("DSA","SUN");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			
			FileInputStream sigfis = new FileInputStream("C:/Users/lbarboza/Desktop/firmao.txt");
			byte[] sigToVerify = new byte[sigfis.available()];
			sigfis.read(sigToVerify);
			sigfis.close();
			
			Signature sig= Signature.getInstance("SHA1withDSA","SUN");
			sig.initVerify(pubKey);

			
			FileInputStream fileParaFirmar = new FileInputStream("C:/Users/lbarboza/Desktop/tengoQueFirmarESto.txt");
			BufferedInputStream bufferFile = new BufferedInputStream(fileParaFirmar);
			byte[] buffer =new byte[1024];
			int len;
			while((len=bufferFile.read(buffer))>=0){
				sig.update(buffer,0,len);
			}
			bufferFile.close();
			boolean verifies = sig.verify(sigToVerify);
			
			System.out.println("signature verifies :"+verifies);
		} catch (Exception e) {
		}
	}
	
	public void generarKey(String usuario){
		try {
		PublicKey llavePublica;
		keyGen = KeyPairGenerator.getInstance("DSA","SUN");
		SecureRandom random=SecureRandom.getInstance("SHA1PRNG","SUN");
		keyGen.initialize(1024,random);
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey llavePrivada = pair.getPrivate();
		llavePublica = pair.getPublic();
		
		
		Signature dsa= Signature.getInstance("SHA1withDSA","SUN");
		dsa.initSign(llavePrivada);
		FileInputStream fileParaFirmar = new FileInputStream("C:/Users/lbarboza/Desktop/tengoQueFirmarESto.txt");
		BufferedInputStream bufferFile = new BufferedInputStream(fileParaFirmar);
		byte[] buffer =new byte[1024];
		int len;
		while((len=bufferFile.read(buffer))>=0){
			dsa.update(buffer,0,len);
		}
		bufferFile.close();
		byte[] realSig=dsa.sign();
		FileOutputStream sigfos = new FileOutputStream("C:/Users/lbarboza/Desktop/firmao.txt");
		sigfos.write(realSig);
		sigfos.close();

		key = llavePublica.getEncoded();
		byte[] privatekey = llavePrivada.getEncoded();
		manejoDB.addLLavePublica(key, usuario);
		
		FileOutputStream keyfos = new FileOutputStream("C:/Users/lbarboza/Desktop/"+usuario+"llavePublica.txt");
		keyfos.write(key);
		keyfos.close();

		FileOutputStream keypos = new FileOutputStream("C:/Users/lbarboza/Desktop/"+usuario+"llavePrivada.txt");
		keypos.write(privatekey);
		keypos.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		} 	
	}
	
}
