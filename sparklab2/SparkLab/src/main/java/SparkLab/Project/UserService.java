package SparkLab.Project;

import WebAppExceptions.Exception404;

import SparkLab.Project.beans.UserBean;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.RequestBody;
import DatabaseInterface.*;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Consumes("application/json")
@Produces("application/json")
@Path("UserAPI")
public class UserService {

	/*
	 * Singleton
	 */

	private MongoDatabaseAdapter userDB = MongoDatabaseAdapter.getIstance();

	//VERIFICA SE L'UTENTE GIA' ESISTE VERIFICANDO L'EMAIL E IL NICKNAME 
	//SE COSI' NON FOSSE LO CREA 
	@POST
	@Path("/users")
	@Consumes({ "application/json", "text/html" })
	public Response createUser(@RequestBody UserBean ub) {

		if (userDB.getUserByEmail(ub.getEmail()) != null || userDB.getUser(ub.getNickname()) != null) {
			System.out.println("bad");
			return Response.status(Response.Status.CONFLICT).entity("nickname or email already in use").build();

		}

		else {
			User user = new User(new ObjectId().toString(), ub.getName(), ub.getSurname(), ub.getNickname(),
					ub.getEmail(), ub.getPassword(), ub.getBirthday());

			if (user.getBirthday().after(new Date()))
				return Response.status(Response.Status.CONFLICT).entity("Invalid date").build();

			userDB.addUser(user);
			userDB.addUserSecurity(user);

			System.out.println("created");
			return Response.ok().build();

		}
	}

	//AGGIUNGE UN' ISCRIZIONE ALL' USER RIFERENDOSI AL NOME
	@POST
	@Path("/users/subP/{pid}")
	@Produces({ "application/json", "text/html" })
	public Response addSubscription(@PathParam("pid") String pid) {
		String nickname = SecurityContextHolder.getContext().getAuthentication().getName();
		User us = userDB.getUser(nickname);
		if (us == null) {
			throw new Exception404("User not found");

		} else
			userDB.addPlan(pid, nickname);
		
		return Response.ok().build();
	}
	
	
	//RIMUOVE UN' ISCRIZIONE ALL' USER 
		@DELETE
		@Path("/users/unsubP/{pid}")
		
		public Response removeSubscription(@PathParam("pid") String pid) {
			String nickname = SecurityContextHolder.getContext().getAuthentication().getName();
			User us = userDB.getUser(nickname);
			if (us == null) {
				throw new Exception404("User not found");

			} else
				userDB.removePlan(pid, nickname);
			
			return Response.ok().build();
		}
		
	//PRENDE L'USER CON I DATI DELLA SESSIONE, SE NON C' E' DA UN MESSAGGIO DI UN USER NOT FOUND 
	@GET
	@Path("/users/sessionWithProjects")
	@Produces({ "application/json", "text/html" })
	public Response getUserBySessionWithProjects() {

		// prende i dati di sessione dell'utente
		String name = SecurityContextHolder.getContext().getAuthentication().getName();

		User us = userDB.getUser(name);
		if (us == null) {
			throw new Exception404("User not found");

		} else
			return Response.ok(us).build();
	}
	// PRENDE L'USER DALL' ID 
	@GET
	@Path("/users/{id}")
	@Produces({ "application/json", "text/html" })
	public Response getUser(@PathParam("id") String id) {

		User us = userDB.getUserId(id);
		if (us == null) {
			throw new Exception404("User not found");
		} else
			return Response.ok(us).build();
	}

	@GET
	@Path("/users")
	@Produces("application/json")
	public List<User> getAllUsers() {
		return userDB.getAllUser();
	}

	@GET
	@Path("/users/for/{filter}")
	@Produces("application/json")
	public List<User> getUserFilter(@PathParam("filter") String filter) {
		return userDB.getUserByNickname(filter);
	}

	@DELETE
	@Path("/users/{userId}")
	public Response deleteUser(@PathParam("userId") String userId) {
		if (userDB.deleteUser(userId))
			return Response.ok().build();

		else
			throw new Exception404("User not found");
	}

}
