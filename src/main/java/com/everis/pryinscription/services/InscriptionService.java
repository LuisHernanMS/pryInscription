package com.everis.pryinscription.services;

import com.everis.pryinscription.documents.Inscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InscriptionService {

public Flux<Inscription> findAll();
	
	public Mono<Inscription> findById(String id);
	
	public Mono<Inscription> save(Inscription inscription);
	
	public Mono<Void> delete(Inscription inscription);
	
	public Flux<Inscription> findByIdCourse(String idCourse);
	
	public Mono<Inscription> findByStudent(String student, String idCourse);
	
}
