package com.everis.pryinscription.dao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.pryinscription.documents.Inscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InscriptionDao extends ReactiveMongoRepository<Inscription, String>{

	@Query("{ idCourse : ?0  }")
	public Flux<Inscription> obtenerPoridCourse(String idCourse);
	
	@Query("{ idCourse : ?0 ,  nDocument : ?0 }")
	public Mono<Inscription> obtenerPorStudent(String student, String idCourse);
	
}
