package com.everis.pryinscription.controller;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.pryinscription.documents.Inscription;
import com.everis.pryinscription.services.InscriptionService;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/inscription")
public class InscriptionController {
	
	@Autowired
	private InscriptionService service;
	
	public Mono<ServerResponse> listar(ServerRequest request){
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.findAll(), Inscription.class)
				.switchIfEmpty(ServerResponse.notFound().build());
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
		String idCourse = request.pathVariable("id");
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.findByIdCourse(idCourse), Inscription.class)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
public Mono<ServerResponse> crear(ServerRequest request){
		
		Mono<Inscription> inscription= request.bodyToMono(Inscription.class);
		
		return inscription.flatMap(p->{
			return service.save(p);
		}).flatMap(p->ServerResponse.created(URI.create("api/v2/inscription/".concat(p.getId())))
				.body(fromObject(p)));
	}

public Mono<ServerResponse> editar(ServerRequest request){
	Mono<Inscription> student= request.bodyToMono(Inscription.class);
	String id = request.pathVariable("id");
	
	Mono<Inscription> inscriptionDB = service.findById(id);
	
	return inscriptionDB.zipWith(student, (db,req)->{
		db.setnDocument(req.getnDocument());
		db.setIdCourse(req.getIdCourse());
		db.setNota(req.getNota());
		db.setNotaFinal(req.getNotaFinal());
		return db;
	}).flatMap(p->ServerResponse.created(URI.create("/api/inscription".concat(p.getId())))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(service.save(p),Inscription.class))
			.switchIfEmpty(ServerResponse.notFound().build());
}

public Mono<ServerResponse> eliminar(ServerRequest request){
	String id = request.pathVariable("id");
	Mono<Inscription> inscriptionDB = service.findById(id);
	return inscriptionDB.flatMap(q->service.delete(q).then(ServerResponse.noContent().build()))
			.switchIfEmpty(ServerResponse.notFound().build());
}
}
