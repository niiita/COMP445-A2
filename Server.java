import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

import java.net.InetSocketAddress;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class Server {

    private static boolean patternCheck = false;
    private final static String HTTP_METHOD_GET = "GET";
    private final static String HTTP_METHOD_POST = "POST";
    private static File filename;
    //Default path
    private final static String DEFAULT_PATH = "C:\\Users\\Chun\\eclipse-workspace\\COMP445-A2";
    //Default port
	private final static int DEFAULT_PORT = 3001;
    
    private static String path = "";
	private static String requestMethod = "";
	public static int port = 3001;

	private final static String DEFAULT_SERVER_ADDRESS = "localhost";

	private static String data = "";
	private static boolean isVerbose = false;
       
	public static void main(String[] args) {        
        try {
            while (patternCheck != true) {
            	
            	
            	String httpfsUserRequest;
                Console console = System.console();
            	
            	while(patternCheck == false) {
            		httpfsUserRequest = console.readLine("Enter an httpfs option (0 to exit): ");

                    if(httpfsUserRequest.equalsIgnoreCase("0")) {
                		System.exit(0);
                	}
                    
                    //Regex pattern; separate entities grouped within parenthesis
                    Pattern pattern = Pattern.compile("(httpfs\\s*)((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s\\\\]{10,1000}))\\s*)((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s\\\\]{10,1000}))\\s*)?((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s\\\\]{10,1000}))\\s*)?");

                    // Now create matcher object.
                    Matcher m = pattern.matcher(httpfsUserRequest);
                    	
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
            	        else {
            	        	System.out.println("Invalid entry, try again or press 0 to exit.");
            	        }
                }
            	
                ServerSocket server = new ServerSocket(port);
                
                System.out.println("Server listening to port " + port);
                Socket clientSocket  = server.accept();
                
            
                System.out.println(clientSocket);
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
                
                //path = "C:\\Users\\\\Chun\\\\eclipse-workspace\\COMP445-A2";
                //requestMethod = "POST";
                //filename = null;
                
                if(requestMethod.equalsIgnoreCase("GET") && filename == null) {
                	getFileNames(path);
                }
                else if(requestMethod.equalsIgnoreCase("GET") && filename != null) {
                	get(path, filename);
                }
                else if(requestMethod.equalsIgnoreCase("POST")) {
                	post(path, filename);
                }
                    
                clientSocket.close();
                server.close();
        	}
        
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	// Lists all the files in a given directory
	public static void getFileNames(String path) {		
		File directory = new File(path);	
	
		File[] items =  directory.listFiles();
		String line = "";		
		for (int i = 0; i < items.length; i++) {
			if (items[i].isFile())
				line += items[i].getName() + "\r\n";			
			else if (items[i].isDirectory())
				line += "<DIRECTORY>" + items[i].getName() + "\r\n";			
		}
	
		formattedOutputResponse(line);
	}
    
    public static void get(String path, File filename){
            String body = "";
			String request = "";
		if (path == "" || path == null){
            body = "{\"A2\" : \"get request\"}";
			
			String response = 	"HTTP/1.0 200 ok\r\n"
							 	+ "Content-Length: " + body.length() + "\r\n"
							 	+ "Content-Disposition: inline"+ "\r\n"
							 	+ "Content-Disposition: attachment; filename=\"get.json\"" + "\r\n"
								+ "Content-Type: application/json\r\n\r\n"
			 					+ body;
			
			System.out.println("Response sent to client\n" + response);
        }else{
            
            try {  
                BufferedReader in = new BufferedReader(new FileReader(filename));
				String line = "";
				StringBuilder StringBuilder = new StringBuilder();

                while ((line = in.readLine()) != null) {
					String formattedLine = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");

					String[] linesArray = formattedLine.split(",");
                    for (int i = 0; i < linesArray.length; i++) {
						StringBuilder.append(linesArray[i]+",");
						System.out.println(linesArray[i]);
					}				
					body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";
					request = "POST /post?info=info HTTP/1.0\r\n"
							+ "Content-Type:application/json\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;

                    } in.close();
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }			
    }

    public static void post(String path, File filename) {
        String body = "";
        String request = "";

        try {  
            	BufferedReader in = new BufferedReader(new FileReader(filename));
				String line = "";
				StringBuilder StringBuilder = new StringBuilder();

                while ((line = in .readLine()) != null) {
					String formattedLine = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");

					String[] linesArray = formattedLine.split(",");
                    for (int i = 0; i < linesArray.length; i++) {
						StringBuilder.append(linesArray[i]+",");
						System.out.println(linesArray[i]);
					}				
					body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";
					request = "POST /post?info=info HTTP/1.0\r\n"
							+ "Content-Type:application/json\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;

				} in .close();
				
				
			
        } catch (Exception e) {
            e.printStackTrace();
        }

       
    }

    private static void formattedOutputResponse(String response) {

    	if (isVerbose) {
            System.out.println(response);
        } else {
            String[] responseFormatted = response.split("\n\n");

            for (int i = 1; i < responseFormatted.length; i++)
                System.out.println(responseFormatted[i]);
        }
        
    }

}