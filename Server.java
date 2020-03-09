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


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class Server {

    private static boolean patternCheck = false;
    private final static String HTTP_METHOD_GET = "GET";
    private final static String HTTP_METHOD_POST = "POST";
    private static File filename;
       
	public static void main(String[] args) {        
        try {
            
                ServerSocket server = new ServerSocket(3001);
                
                System.out.println("Server listening to port " + 3001);
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

                String[] req = request. toString().split(" ", -2);

                String method = req[0].toString();
                String path = req[1].toString();

                System.out.println(method + " & " + path);
                String filename = path.substring((path.lastIndexOf("/")+1), (path.length()));
                System.out.println("filename: " + filename);
                
                    if (method.equals("GET")) {
                        get(path, filename);
                    } else if (method.equals("POST")) {
                        post(path, filename);
                    }       
                    
                clientSocket.close();
                server.close();

        } catch (IOException e) {
            e.printStackTrace();
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

    private static void formattedOutputResponse(String response) {

       String[] responseFormatted = response.split("\n\n");

            for (int i = 1; i < responseFormatted.length; i++)
                System.out.println(responseFormatted[i]);
        
    }

}