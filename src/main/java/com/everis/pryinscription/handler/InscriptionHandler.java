package com.everis.pryinscription.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.pryinscription.documents.Inscription;
import com.everis.pryinscription.services.InscriptionService;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.client.model.DBCollectionFindOptions;

import reactor.core.publisher.Mono;

@Component
public class InscriptionHandler {

	@Autowired
	private InscriptionService service;
	
	public Mono<ServerResponse> listar(ServerRequest request){
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.findAll(), Inscription.class);
	}
	
	public Mono<ServerResponse> forstudent(ServerRequest request){
		String student = request.pathVariable("name");
		String idCourse = request.pathVariable("id");
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.findByStudent(student, idCourse), Inscription.class)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> forcourse(ServerRequest request){
		String idCourse = request.pathVariable("name");
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.findByIdCourse(idCourse), Inscription.class)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> crear(ServerRequest request){
		
		Mono<Inscription> inscription= request.bodyToMono(Inscription.class);
		
		return inscription.flatMap(p->{
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("prystudent");
			DBCollection dbCollection = db.getCollection("student");
			DBObject query = new BasicDBObject("numberDocument", p.getnDocument());
			DBCollectionFindOptions options;
			Integer result = dbCollection.find(query).count(); 
			if (result>0) {
				db = mongo.getDB("prycourse");
				dbCollection = db.getCollection("course");
				query = BasicDBObjectBuilder.start().add("id", p.getIdCourse()).add("state", "abierto").get();
				Integer result2 = dbCollection.find(query).count(); 
				query = BasicDBObjectBuilder.start().add("id", p.getIdCourse()).add("state", "activo").get();
				result2 = result2 + dbCollection.find(query).count(); 
				query = BasicDBObjectBuilder.start().add("id", p.getIdCourse()).add("state", "espera").get();
				result2 = result2 + dbCollection.find(query).count();
				if(result2>0) {
					return service.save(p);
				}else {
					return null;
				}
				
			}else {
				db = mongo.getDB("pryfamily");
				dbCollection = db.getCollection("family");
				query = new BasicDBObject("numberDocument", p.getnDocument());
				result = dbCollection.find(query).count(); 
				if(result>0) {
					db = mongo.getDB("pryinscription");
					dbCollection = db.getCollection("inscription");
					query = new BasicDBObject("idCourse", p.getIdCourse());
					result = dbCollection.find(query).count();
					db = mongo.getDB("prycourse");
					dbCollection = db.getCollection("course");
					query = new BasicDBObject("id", p.getIdCourse());
					
					//Integer minimo = Integer.parseInt(dbCollection.find(query)..toString()); 
					//System.out.println(""+minimo);
					/*if(result<p.ge) {
						
					}*/
					return service.save(p);
				}else {
					return null;
				}
			}	
		}).flatMap(p->ServerResponse.created(URI.create("api/v2/students/".concat(p.getId())))
				.body(fromObject(p)));
	}
	
}
