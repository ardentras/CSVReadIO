package sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class CSVReadNIO {
	public static void main(String[] args) {
		File inputFile = new File("test5.csv");
		
		if (inputFile.exists()) {
			try {
				ByteBuffer bb = ByteBuffer.allocate((int) inputFile.length());
				ReadableByteChannel rbc = Channels.newChannel(new FileInputStream(inputFile));
				
				rbc.read(bb);
				
				bb.position(0);
				StringBuilder data = new StringBuilder(String.valueOf((char)bb.get()));
				
				while (bb.hasRemaining()) {
					data.append(String.valueOf((char)bb.get()));
				}
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
