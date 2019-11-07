package com.everis.pryinscription.services;

import javax.naming.ServiceUnavailableException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.pryinscription.dao.InscriptionDao;
import com.everis.pryinscription.documents.Inscription;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.client.model.DBCollectionFindOptions;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InscriptionServiceImpl implements InscriptionService{

	@Autowired
	private InscriptionDao dao;
	
	@Override
	public Flux<Inscription> findAll() {
		return dao.findAll();
	}

	@Override
	public Mono<Inscription> findById(String id) {
		return dao.findById(id);
	}

	@Override
	public Mono<Inscription> save(Inscription inscription) {
		Mongo mongo = new Mongo("localhost", 27017);
		DB db = mongo.getDB("prystudent");
		DBCollection dbCollection = db.getCollection("student");
		DBObject query = new BasicDBObject("numberDocument", inscription.getnDocument());
		Integer result = dbCollection.find(query).count(); 
		if (result>0) {
			db = mongo.getDB("pryinscription");
			dbCollection = db.getCollection("inscription");
			query = BasicDBObjectBuilder.start().add("idCourse", inscription.getIdCourse()).add("nDocument", inscription.getnDocument()).get();
			result = dbCollection.find(query).count();
			if(result>0) {
				return Mono.error(new ServiceUnavailableException("The student registered with id "+inscription.getnDocument()+" has already registered in this course"));
			}else {
				query = new BasicDBObject("idCourse", inscription.getIdCourse());
				result = dbCollection.find(query).count();
				System.out.println(result);
				db = mongo.getDB("prycourse");
				dbCollection = db.getCollection("course");
				query = new BasicDBObject("_id", new ObjectId(inscription.getIdCourse()));
				Integer result2 = dbCollection.find(query).count();
				if(result2>0) {
					query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).get();
					DBObject dbo = dbCollection.findOne(query);
					String maximum = (String) dbo.get("maximum");
					
					Integer max=Integer.parseInt(maximum);
					System.out.println(max+" ");
					if(result==max) {
						return Mono.error(new ServiceUnavailableException("The course registered with id "+inscription.getIdCourse()+" is full"));
					}else {
						query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).add("state", "abierto").get();
						result2 = dbCollection.find(query).count(); 
						query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).add("state", "activo").get();
						result2 = result2 + dbCollection.find(query).count(); 
						query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).add("state", "espera").get();
						result2 = result2 + dbCollection.find(query).count();
						if(result2>0) {
							query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).get();
							dbo = dbCollection.findOne(query);
							String minimum = (String) dbo.get("minimum");
							
							Integer min=Integer.parseInt(minimum);
							System.out.println(min+" ");
							result=result+1;
							if(result>=min) {
								String ndTeacher = (String) dbo.get("nDTeacher");
								query = BasicDBObjectBuilder.start().add("nDTeacher", ndTeacher).add("state", "activo").get();
								result2 = dbCollection.find(query).count();
								if(result2>=2){
									query=new BasicDBObject("_id",new ObjectId(inscription.getIdCourse())); 
									DBObject update= new BasicDBObject("$set",new BasicDBObject("state","espera"));
									DBObject change=dbCollection.findAndModify(query, update);
									db.command(change);
								}else {
									query=new BasicDBObject("_id",new ObjectId(inscription.getIdCourse())); 
									DBObject update= new BasicDBObject("$set",new BasicDBObject("state","activo"));
									DBObject change=dbCollection.findAndModify(query, update);
									db.command(change);
								}
								
							}
							return dao.save(inscription);
						}else {
							return Mono.error(new ServiceUnavailableException("The course registered with id "+inscription.getIdCourse()+" is not enabled"));
						}
					}
					
				}else {
					return Mono.error(new ServiceUnavailableException("There is no course registered with id "+inscription.getIdCourse()));
				}
			}
		}else {
			db = mongo.getDB("pryfamily");
			dbCollection = db.getCollection("family");
			query = new BasicDBObject("numberDocument", inscription.getnDocument());
			result = dbCollection.find(query).count(); 
			if(result>0) {
				db = mongo.getDB("pryinscription");
				dbCollection = db.getCollection("inscription");
				query = BasicDBObjectBuilder.start().add("idCourse", inscription.getIdCourse()).add("nDocument", inscription.getnDocument()).get();
				result = dbCollection.find(query).count();
				if(result>0) {
					return Mono.error(new ServiceUnavailableException("The listener registered with id "+inscription.getnDocument()+" has already registered in this course"));
				}else {
					//System.out.println(lista+" "+cListener);
					
					query = new BasicDBObject("idCourse", inscription.getIdCourse());
					result = dbCollection.find(query).count();
					
					query = new BasicDBObject("nDocument", inscription.getnDocument());
					Integer cListener = 0;
					DBCursor dbcListener = dbCollection.find(query);
					String lista="";
					while(dbcListener.hasNext()) {
						DBObject dboListener=dbcListener.next();
						lista = (String) dboListener.get("idCourse");
						System.out.println("Encontró "+lista);
						db = mongo.getDB("prycourse");
						dbCollection = db.getCollection("course");
						query = BasicDBObjectBuilder.start().add("_id", new ObjectId(lista)).add("state", "activo").get();
						cListener = cListener+dbCollection.find(query).count();
						query = BasicDBObjectBuilder.start().add("_id", new ObjectId(lista)).add("state", "espera").get();
						cListener = cListener+dbCollection.find(query).count();
					}
					System.out.println("Encontró "+cListener);
					
					
					db = mongo.getDB("prycourse");
					dbCollection = db.getCollection("course");
					query = new BasicDBObject("_id", new ObjectId(inscription.getIdCourse()));
					Integer result2 = dbCollection.find(query).count();
					if(result2>0) {
						query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).get();
						DBObject dbo = dbCollection.findOne(query);
						String maximum = (String) dbo.get("maximum");
						
						Integer max=Integer.parseInt(maximum);
						System.out.println(max+" ");
						if(result==max) {
							return Mono.error(new ServiceUnavailableException("The course registered with id "+inscription.getIdCourse()+" is full"));
						}else {
							query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).add("state", "activo").get();
							result2 = dbCollection.find(query).count(); 
							query = BasicDBObjectBuilder.start().add("_id", new ObjectId(inscription.getIdCourse())).add("state", "espera").get();
							result2 = result2 + dbCollection.find(query).count();
							if(result2>0) {
								if(cListener>=3) {
									return Mono.error(new ServiceUnavailableException("The listener is already enrolled in 3 courses"));
								}else {
									return dao.save(inscription);
								}
								
							}else {
								return Mono.error(new ServiceUnavailableException("The course registered with id "+inscription.getIdCourse()+" is not enabled"));
							}
							
						}
					
					}else {
						return Mono.error(new ServiceUnavailableException("There is no course registered with id "+inscription.getIdCourse()));
					}
				}
			}else {
				return Mono.error(new ServiceUnavailableException("There is no student or family member with ID "+inscription.getnDocument()));
			}
			
		}
	}

	@Override
	public Mono<Void> delete(Inscription inscription) {
		// TODO Auto-generated method stub
		return dao.delete(inscription);
	}

	@Override
	public Flux<Inscription> findByIdCourse(String idCourse) {
		// TODO Auto-generated method stub
		return dao.obtenerPoridCourse(idCourse);
	}

	@Override
	public Mono<Inscription> findByStudent(String student, String idCourse) {
		// TODO Auto-generated method stub
		return dao.obtenerPorStudent(student, idCourse);
	}

}
