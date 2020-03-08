import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;

import java.net.InetSocketAddress;

import java.net.Socket;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;


public class Server {
	
	private final static int DEFAULT_PORT = 8080;


	private final static String DEFAULT_SERVER_ADDRESS = "localhost";

    private static boolean patternCheck = false;
	private static String data = "";
	private static int port = 8080;
	private static boolean isVerbose = false;
	
	public static void main(String[] args) {
		
		String value;

        Console console = System.console();
		value = console.readLine("");

        //Regex pattern; separate entities grouped within parenthesis
        Pattern pattern = Pattern.compile("(httpfs\\s*)((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s\\\\]{10,1000}))\\s*)((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s\\\\]{10,1000}))\\s*)?((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s\\\\]{10,1000}))\\s*)?");

        // Now create matcher object.
        Matcher m = pattern.matcher(value);

        if (m.find()) {

            patternCheck = true;
            
            var patternLength = m.group().length();
            
            //Ensuring that we don't go over the cap
            if(patternLength > 28) {
            	patternLength = 28;
            }
            
            for(int i = 5; i < patternLength; i++)
            {
            	
            	if(m.group(i) != null) {
            		System.out.println(m.group(i));
	            	if(m.group(i).equals("-v")) 
	            	{
	            		//Is verbose if -v
	            		isVerbose = true;
	            	}
	            	else if (m.group(i).equals("-p")) 
	            	{
	            		//Assign the port if it exists
	            		port = Integer.parseInt(m.group(i + 1));
	            	}
	            	else if (m.group(i).equals("-d")) {
	            		//Assign the path if it exists
	            		data = m.group(i + 1);
	            	}
            	}
            }
        }
		
		get();
    }
    
    public static void get(){
        		
		try {
			//3001
			ServerSocket server = new ServerSocket();
			
			SocketAddress bindAddress = new InetSocketAddress(DEFAULT_SERVER_ADDRESS, port);
			server.bind(bindAddress);    	
			System.out.println("Server listening to port " + port);
			Socket clientSocket  = server.accept();
			System.out.println("Server accepted connection");
			
			InputStream inputStream = clientSocket.getInputStream();
			OutputStream outputStream = clientSocket.getOutputStream();
			
			StringBuilder request = new StringBuilder();
			
			int data = inputStream.read();
			int counter = 0;
			
			while(data != -1) {
				if(((char) data) == '\r' || ((char) data) == '\n') {
					counter++;
					if (counter == 4)
						break;
				} else {
					counter = 0;
				}
				request.append((char) data);
				data = inputStream.read();
			}
			
			System.out.println("\nRequest From client");
			System.out.println(request);
			
			String body = "{\"A2\" : \"get request\"}";
			
			String response = 	"HTTP/1.0 200 ok\r\n"
							 	+ "Content-Length: " + body.length() + "\r\n"
							 	+ "Content-Disposition: inline"+ "\r\n"
							 	+ "Content-Disposition: attachment; filename=\"get.json\"" + "\r\n"
								+ "Content-Type: application/json\r\n\r\n"
			 					+ body;
			
			System.out.println("Response sent to client\n" + response);
			
			outputStream.write(response.getBytes());
			outputStream.flush();
			clientSocket.close();
			server.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static void get2(){
        
    }

    public static void post(){
        
    }
}