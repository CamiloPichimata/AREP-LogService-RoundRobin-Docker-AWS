package co.edu.escuelaing.arep;

import static spark.Spark.post;
import static spark.Spark.get;

import java.util.ArrayList;

import org.bson.Document;
import org.json.JSONObject;

import static spark.Spark.port;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * LogService es un servicio REST que recibe una cadena, la almacena en la base de datos 
 * y responde en un objeto JSON con las 10 ultimas cadenas almacenadas en la base de datos 
 * y la fecha en que fueron almacenadas.
 * 
 * @author Camilo Pichimata
 */
public class LogService {
	
	public static void main(String... args){
        port(getPort());
        post("/post", (req,res) -> {
        	System.out.println("Body recibido en el post: " + req.body());
        	JSONObject jsonObject = new JSONObject(req.body());
            postToDb(jsonObject);
            res.status();
        	return null;
        });
        
        get("/get", (req, res) -> {
        	System.out.println("Solicitud get recibida");
        	ArrayList<String[]> listaRegistros = getToDb();
            JSONObject rta = new JSONObject(listaRegistros);
            System.out.println("Respuesta en formato Json: " + rta);
            return null;
        });
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4568;
    }

    private static void postToDb(JSONObject json) {
        // Crea la conexión a la base de datos Mongo
    	MongoClient mongo = new MongoClient("db");
    	// Crea o se conecta a la base de datos
    	MongoDatabase db = mongo.getDatabase("bd-LogService");
    	// Crea o usa una tabla en la base de datos
    	MongoCollection<Document> tableCollection = db.getCollection("registers");
    	// Crea un registro
    	Document document = new Document().append("string", json.getString("string")).append("date", json.getString("date"));
    	// Agrega el registro a la base de datos
    	tableCollection.insertOne(document);
        
    	// Termina la conexión
    	mongo.close();
    }
    
    private static ArrayList<String[]> getToDb() {
    	// Crea la conexión a la base de datos Mongo
    	MongoClient mongo = new MongoClient("db");
    	// Crea o se conecta a la base de datos
    	MongoDatabase db = mongo.getDatabase("bd-LogService");
    	// Crea o usa una tabla en la base de datos
    	MongoCollection<Document> tableCollection = db.getCollection("registers");
    	
    	// Obtiene los datos existentes en la tabla
    	ArrayList<String[]> registers = new ArrayList<>();
    	MongoCursor<Document> cursor = tableCollection.find().iterator();
    	Document tempDoc;
    	while (cursor.hasNext()) {
    		tempDoc = cursor.next();
    		String[] tempList = {tempDoc.getString("string"), tempDoc.getString("date")};
    		registers.add(tempList);
    	}
    	
    	// Termina la conexión
    	mongo.close();
    	
    	// Envía solo los últimos 10 registros
    	if (registers.size() > 10) {
    		registers = (ArrayList<String[]>) registers.subList(registers.size()-10, registers.size());
    	}
    	
    	return registers;
    }
}
