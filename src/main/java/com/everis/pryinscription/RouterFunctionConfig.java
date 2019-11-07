package com.everis.pryinscription;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.pryinscription.controller.InscriptionController;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(InscriptionController controller){
		return RouterFunctions.route(GET("/api/inscription"), controller::listar)
				.andRoute(GET("/api/inscription/{id}"), controller::forcourse)
				.andRoute(GET("/api/inscription/{name}/{id}"), controller::forstudent)
				.andRoute(POST("/api/inscription"), controller::crear)
				.andRoute(PUT("/api/inscription/{id}"), controller::editar)
				.andRoute(DELETE("/api/inscription/{id}"), controller::eliminar);
	}
	
}
