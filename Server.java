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
	private static String method = "";
	public static int port = 3001;

	private final static String DEFAULT_SERVER_ADDRESS = "localhost";

	private static String dataInput = "";
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
                    Pattern pattern = Pattern.compile("(httpfs\\s*)((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s]{10,1000}))\\s*)((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s]{10,1000}))\\s*)?((-p)\\s*(\\b([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[6][0-4][0-9][0-9][0-9]|[6][5][0-4][0-9][0-9]|[6][5][5][0-2][0-9]|[6][5][5][3][0-5])\\b\\s*)|(\\s*(-v)\\s*)|(\\s*(-d)\\s*([^\\s]{10,1000}))\\s*)?");

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
            		            		dataInput = m.group(i + 1);
            		            		File test = new File(dataInput);
            		            		if(!test.exists()) {
            		            			System.out.println(errorResponse(404));
            		            			System.exit(0);
            		            		}
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
                System.out.println(request);

                String[] req = request. toString().split(" ", -2);

                String method = req[0].toString();
                String path = req[1].toString();
                System.out.println("path: " + path);
                System.out.println(method + " & " + path);
                String filename = path.substring((path.lastIndexOf("/")+1), (path.length()));
                System.out.println("filename: " + filename);
                
                //path = "C:\\Users\\\\Chun\\\\eclipse-workspace\\COMP445-A2";
                //method = "GET";
                //filename = "gremlin.txt";
                //method = "POST";
                //filename = null;
                
                if(method.equalsIgnoreCase("GET") && (filename.isBlank() || !filename.contains("."))) {
                	getFileNames(dataInput);
                }
                else if(method.equalsIgnoreCase("GET") && !filename.isBlank() && filename.contains(".")) {
                	System.out.println("THIS WORKS");
                	get(dataInput, filename);
                }
                else if(method.equalsIgnoreCase("POST")) {
                	post(dataInput, filename);
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
      
      System.out.println(items);
      String line = "";
      if(items != null) {
	      for (int i = 0; i < items.length; i++) 
	      {
	        if (items[i].isFile())
	          line += items[i].getName() + "\r\n";			
	        else if (items[i].isDirectory())
	          line += "<DIRECTORY>" + items[i].getName() + "\r\n";			
	      }
	      formattedOutputResponse(line);
      }
      else {
    	  System.out.println(errorResponse(404));
      }
    }

             
    
    public static void get(String path, String filename){
            String body = "";
            String response = "";
            
            //If there is no path (i.e. just localhost:3001 is called)
		    if ((path.length()<=1) || path == null){

            body = "{\"A2\" : \"sample body for get request\"}";
			
			response = 	"HTTP/1.0 200 ok\r\n"
                        + "Content-Length: " + body.length() + "\r\n"
                        + "Content-Disposition: inline"+ "\r\n"
                        + "Content-Disposition: attachment; filename=\"default.json\"" + "\r\n"
                        + "Content-Type: application/json\r\n\r\n"
                        + body;
			
            System.out.println("Response sent to client\n" + response);
            
            }else{
            //If there is a path (should be able to read text, for example a json formatted txt file or just some text and return as body)
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

						StringBuilder.append(linesArray[i]+", ");
					}				
					body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";
					response = "HTTP/1.0 200 OK\r\n"
                            + "Content-Length: " + body.length() + "\r\n"
                            + "Content-Disposition: inline"+ "\r\n"
                            + "Content-Disposition: attachment; filename=\""+filename+"\"" + "\r\n"
                            + "Content-Type: application/json\r\n\r\n"
                            + body;

                    } in.close();
                    System.out.println("Response sent to client\n" + response);

                } catch (Exception e) {
                    //Maybe we can create an actual 404 response from server here?
                    System.out.println("Sorry the file you are looking for does not exist");
                    response = 	"HTTP/1.0 404 Not Found\r\n"
							 	+ "User Agent: Concordia\r\n";
                    System.out.println("Response sent to client\n" + response);
                    // e.printStackTrace();
                }
        }			
    }

    public static void post(String path, String filename) {
        String body = "";
        String request = "";

        /*
        if(directory in path exists){
            if(filename already exists){

            }else{
                // append contents to contents already in existing file
            }
            
            //create new file named filename and add content(?)

        }else{
            // create new directories inquired in path
        }
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
        */
       
    }
    
    //Returns an error response
 	private static String errorResponse(int errorStatusCode) {
 		switch(errorStatusCode) {
 			case 403:
 				return "403 Forbidden";
 			case 404:
 				return "404 Not Found";
 			default:
 				return "400 Bad Request";				
 		}			
 	}
    

    private static void formattedOutputResponse(String response) {
    	System.out.println("ISVERBOSE");
    	System.out.println(isVerbose);
    	
    	if (isVerbose) {
            System.out.println(response);
        } 
    	else {
            String[] responseFormatted = response.split("\n\n");

            for (int i = 1; i < responseFormatted.length; i++)
                System.out.println(responseFormatted[i]);
        }
        
    }

}