package co.edu.escuelaing.arep;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * LogService es un servicio REST que recibe una cadena, la almacena en la base de datos 
 * y responde en un objeto JSON con las 10 ultimas cadenas almacenadas en la base de datos 
 * y la fecha en que fueron almacenadas.
 * 
 * @author Camilo Pichimata
 */
public class Balancer {
	private static int balancerCount = 0;
	
	public static void main(String... args){
        port(getPort());
        staticFiles.location("/");
        get("/LogService", (req,res) -> {
        	res.redirect("/index.html");
        	return null;
        });
        
        post("/post", (req, res) -> {
        	increaseBalancerCount();
        	sendPost(req.body());
        	return null;
        });
        
        get("/get", (req, res) -> {
        	return sendGet();
        });
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
    
    private static void increaseBalancerCount() {
    	if (balancerCount >= 3) {
    		balancerCount = 0; 
    	} else {
    		balancerCount++;
    	}
    	System.out.println("BalancerCount: " + balancerCount);
    }
    
    private static void sendPost(String data) {
    	try {
			// Se crea la conexión
    		URL url = new URL("http://LogService"+balancerCount+":3500"+balancerCount+"/post");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// Se añade el encabezado de la solicitud
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "Application/json");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			// Se envía la solicitud de publicación
			connection.setDoOutput(true);
			DataOutputStream write = new DataOutputStream(connection.getOutputStream());
			write.writeBytes(data);
			write.flush();
			write.close();
			
			// Response
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'POST' request to URL: " + url);
	        System.out.println("Post data: " + data);
	        System.out.println("Response Code: " + responseCode);
	        
	        BufferedReader in = new BufferedReader(
	        		new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        
	        while ((inputLine = in.readLine()) != null) {
	        	System.out.println("RESPONSE: " + inputLine);
				response.append(inputLine);
			}
	        in.close();
	        System.out.println();
	        System.out.println("RESULTADO:\n" + response.toString());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static String sendGet() {
    	String rta = "";
    	try {
	    	// Se crea la conexión
			URL url = new URL("http://LogService"+balancerCount+":3500"+balancerCount+"/get");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// Se añade el encabezado de la solicitud
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			// Response
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL: " + url);
	        System.out.println("Response Code: " + responseCode);
	        
	        BufferedReader in = new BufferedReader(
	        		new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        
	        while ((inputLine = in.readLine()) != null) {
	        	System.out.println("RESPONSE: " + inputLine);
				response.append(inputLine);
			}
	        in.close();
	        
	        System.out.println();
	        System.out.println("RESULTADO:\n" + response.toString());
	        
	        rta = response.toString();
	        
    	} catch (MalformedURLException e) {
			e.printStackTrace();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return rta;
    }
}
