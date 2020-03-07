import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {
		get();
    }
    
    public static void get(){
        		
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