package com.everis.pryinscription.documents;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inscription")
public class Inscription {

	@Id
	private String id;
	
	@NotEmpty
	String nDocument;
	
	@NotEmpty
	String idCourse;
	
	String nota;
	
	String notaFinal;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getnDocument() {
		return nDocument;
	}

	public void setnDocument(String nDocument) {
		this.nDocument = nDocument;
	}

	public String getIdCourse() {
		return idCourse;
	}

	public void setIdCourse(String idCourse) {
		this.idCourse = idCourse;
	}

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public String getNotaFinal() {
		return notaFinal;
	}

	public void setNotaFinal(String notaFinal) {
		this.notaFinal = notaFinal;
	}

	public Inscription(@NotEmpty String nDocument, @NotEmpty String idCourse) {
		super();
		this.nDocument = nDocument;
		this.idCourse = idCourse;
	}
	

	
}
