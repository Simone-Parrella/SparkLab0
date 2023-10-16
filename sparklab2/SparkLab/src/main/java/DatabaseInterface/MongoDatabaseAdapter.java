package DatabaseInterface;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

/**
 * this class is used to interact with Mongo database. Every transaction with
 * the database is intermediated by an object of this class.
 */
public class MongoDatabaseAdapter implements DatabaseAdapter {

	MongoClientURI connectionString;
	String host = "172.31.6.1";
	String port = "27017";


	private static MongoDatabaseAdapter mongoAdapter= null;

	private MongoDatabaseAdapter() {
		connectionString = new MongoClientURI("mongodb://" + host + ":" + port);
	}



	public static MongoDatabaseAdapter getIstance(){

		if(mongoAdapter== null){
			mongoAdapter= new MongoDatabaseAdapter();

		}
		return mongoAdapter;
	}

	/**
	 * Returns a User object from database, searching it by the given nickname.
	 *
	 * @param nickname the nick of the user you want to search for
	 * @Returns the User
	 */
	@Override
	public User getUser(String nickname) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");
		Document current = coll.find(eq("nickname", nickname)).first();

		if (current == null)
			return null;
		else {
			User u = new User(current.getString("_id"), current.getString("name"), current.getString("surname"),
					current.getString("nickname"), current.getString("email"), current.getString("password"),
					current.getDate("birthday"), (List<String>) current.get("skills"),
					(List<String>) current.get("purchase"), (List<String>) current.get("projectId"));
			MongoCollection<Document> collU = db.getCollection("Project");
			List<String> progetti = u.getProjectId();
			List<String> purchase = u.getPurchase();

			if (progetti == null) {
				List<Project> pr = new ArrayList<>();
			} else {
				List<Project> pr = getProjectPack(progetti);
				for (Project p : pr) {
					u.addProject(p);
				}

			}
			if (purchase == null) {
				List<Project> pu = new ArrayList<>();
			} else {
				List<Project> pu = getProjectPack(purchase);
				for (Project r : pu) {
					u.addPurchaseP(r);
				}

			}

			mongoClient.close();
			return u;
		}
	}

	public User getUserId(String nickname) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");
		Document current = coll.find(eq("_id", nickname)).first();

		if (current == null)
			return null;
		else {
			User u = new User(current.getString("_id"), current.getString("name"), current.getString("surname"),
					current.getString("nickname"), current.getString("email"), current.getString("password"),
					current.getDate("birthday"), (List<String>) current.get("skills"),
					(List<String>) current.get("purchase"), (List<String>) current.get("projectId"));
			MongoCollection<Document> collU = db.getCollection("Project");
			List<String> progetti = u.getProjectId();
			List<String> purchase = u.getPurchase();

			if (progetti == null) {
				List<Project> pr = new ArrayList<>();
			} else {
				List<Project> pr = getProjectPack(progetti);
				for (Project p : pr) {
					u.addProject(p);
				}
			}
			if (purchase == null) {
				List<Project> pu = new ArrayList<>();
			} else {
				List<Project> pu = getProjectPack(purchase);
				for (Project r : pu) {
					u.addPurchaseP(r);
				}

			}

			mongoClient.close();
			return u;
		}
	}

	/**
	 * Returns a user with the given email address if present in the database.
	 * return null otherwise
	 *
	 * @param: email
	 * @Returns: the user
	 */
	@Override
	public User getUserByEmail(String email) {

		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");

		Document current = coll.find(eq("email", email)).first();

		if (current == null)
			return null;
		else {

			User u = new User(current.getString("_id"), current.getString("name"), current.getString("surname"),
					current.getString("nickname"), current.getString("email"), current.getString("password"),
					current.getDate("birthday"));

			mongoClient.close();
			return u;
		}
	}

	public Comment getComment(String id) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Comments");

		Document current = coll.find(eq("_id", id)).first();
		if (current == null)
			return null;
		else {

			Comment c = new Comment(current.getString("_id"), current.getString("body"), current.getString("user_id"));

			mongoClient.close();
			return c;
		}
	}

	public Comment getCommentByprojectName(String id) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Comment");

		Document current = coll.find(eq("project_id", id)).first();
		if (current == null)
			return null;
		else {

			Comment c = new Comment(current.getString("_id"), current.getString("body"), current.getString("user_id"));

			mongoClient.close();
			return c;
		}
	}

	/**
	 * Given a projectName this method returns the whole resource list associated
	 * with the specified project.
	 *
	 * @param: projectName the name of the project you want to take resources list
	 *                     from.
	 * @Returns: the resource List
	 */
	@Override
	public ArrayList<ResourceS> getResourceList(String idProject) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		Document current = coll.find(eq("_id", idProject)).first();
		ArrayList<Document> docuList = (ArrayList<Document>) current.get("resources");
		assert docuList != null;
		ArrayList<ResourceS> res = new ArrayList<ResourceS>();

		for (Document d : docuList) {

			res.add(new ResourceS(d.getString("_id"), d.getString("name"), d.getString("resource_type"),
					d.getString("body"), d.getString("description"), d.getString("userId")));

		}

		mongoClient.close();
		return res;
	}

	/**
	 * Returns a Project object from database, searching it by the given name.
	 *
	 * @param id the id of the project you want to search for
	 * @Returns: the Project
	 */
	@Override
	public Project getProject(String id) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		Document current = coll.find(eq("_id", id)).first();
		if (current == null)
			return null;
		Project pr = new Project(current.getString("_id"), current.getString("owner_id"), current.getString("name"),
				current.getString("description"), (List<String>) current.get("project_area"),
				(int) current.getInteger("follower"), (List<ResourceS>) current.get("resources"),
				(List<String>) current.get("request"), false);

		mongoClient.close();
		return pr;
	}

	public List<Project> getProjectsByName(String searchTerm) {
		if (searchTerm != null) {
			MongoClient mongoClient = new MongoClient(connectionString);
			MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
			MongoCollection<Document> projectCollection = db.getCollection("Project");

			List<Project> projects = new ArrayList<>();
			Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
			Bson filter = Filters.regex("name", pattern);

			for (Document current : projectCollection.find(filter)) {
				projects.add(new Project(current.getString("_id"), current.getString("owner_id"),
						current.getString("name"), current.getString("description"),
						(List<String>) current.get("project_area"), (int) current.getInteger("follower"),
						(List<ResourceS>) current.get("resources"), (List<String>) current.get("request"), false));
			}

			mongoClient.close();
			return projects;
		} else {
			return null;
		}
	}

	/**
	 * Returns a list of all projects whose IDs are in the list provided as a
	 * parameter.
	 *
	 * @param projIDs list of Ids
	 */
	public List<Project> getProjectPack(List<String> projIDs) {
		if (projIDs != null) {
			MongoClient mongoClient = new MongoClient(connectionString);
			MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
			MongoCollection<Document> coll = db.getCollection("Project");
			List<Project> proList = new ArrayList<>();
			Document query = new Document("_id", new Document("$in", projIDs));
			FindIterable<Document> cursor = coll.find(query);
			MongoCursor<Document> iter = cursor.iterator();

			while (iter.hasNext()) {
				Document d = iter.next();
				Project pr = new Project(d.getString("_id"), d.getString("owner_id"), d.getString("name"),
						d.getString("description"), (List<String>) d.get("progect_area"),
						(int) d.getInteger("follower"), (List<ResourceS>) d.get("resources"),
						(List<String>) d.get("request"), false);
				proList.add(pr);
			}

			mongoClient.close();
			return proList;
		} else {
			return null;
		}
	}

	@Override
	public Project mostFollowed_greater_than(int followers, String OwnerId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		Document current = new Document();

		if (coll.find(gt("follower", followers)) != null) {

			current = coll.find(eq("owner_id", OwnerId)).first();
		}
		Project pr = new Project(current.getString("_id"), current.getInteger("owner_id").toString(),
				current.getString("name"), current.getString("descrpition"));

		mongoClient.close();
		return pr;
	}

	public long getCountDocuments(String collection) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection(collection);
		long documentCount = coll.countDocuments();
		mongoClient.close();
		return documentCount;
	}

	
	/**
	 * Modifica il parametro name di User sul DB, filtrando tramite il nickname ,
	 * con il nuovo nome passato dal metodo
	 *
	 * @param nickname univoco per ogni utente e name il nuovo nome da inserire
	 */

	public void editName(String nickname, String name) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");
		coll.updateOne(eq("nickname", nickname), new Document("$set", new Document("name", name)));
		mongoClient.close();
	}

	/**
	 * Modifica il parametro surname di User sul DB, filtrando tramite il nickname ,
	 * con il nuovo surname passato dal metodo
	 *
	 * @param nickname univoco per ogni utente e surname il nuovo cognome da
	 *                 inserire
	 */

	public void editSurname(String nickname, String surname) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");
		coll.updateOne(eq("nickname", nickname), new Document("$set", new Document("surname", surname)));
		mongoClient.close();
	}

	/**
	 * Modifica il parametro mail di User sul DB, filtrando tramite il nickname ,
	 * con la nuova mail passato dal metodo
	 *
	 * @param nickname univoco per ogni utente e mail la nuova mail da inserire
	 */

	public void editMail(String nickname, String mail) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");
		coll.updateOne(eq("nickname", nickname), new Document("$set", new Document("mail", mail)));
		mongoClient.close();
	}

	/**
	 * Modifica il parametro password di User sul DB, filtrando tramite il nickname
	 * , con la nuova password passato dal metodo
	 *
	 * @param nickname univoco per ogni utente e password la nuova password da
	 *                 inserire
	 */

	public void editPassword(String nickname, String password) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");
		coll.updateOne(eq("nickname", nickname), new Document("$set", new Document("password", password)));
		mongoClient.close();
	}

	public boolean addResourceToProject(String projectId, ResourceS resource) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		// first find the project and then add the resource in list
		Document current = coll.find(eq("_id", projectId)).first();
		if (current == null) {
			mongoClient.close();
			return false;
		} else {

			Project project = new Project(current.getString("_id"), current.getString("owner_id"),
					current.getString("name"), current.getString("descrpition"),
					(List<String>) current.get("progect_area"), (int) current.getInteger("follower"),
					(List<ResourceS>) current.get("resources"));

			// retrieve the document from db
			List<Document> documentList = (List<Document>) current.get("resources");

			Document doc = new Document("_id", resource.getId()).append("name", resource.getName())
					.append("resource_type", resource.getResourceType()).append("body", resource.getBody())
					.append("description", resource.getDescription()).append("userId", resource.getUserId())
					.append("like", resource.getLikes()).append("dislike", resource.getDisikes());

			documentList.add(doc);

			// update document in db with the new list of resources
			coll.updateOne(eq("_id", projectId), new Document("$set", new Document("resources", documentList)));
			;
			mongoClient.close();
			return true;
		}
	}

	/**
	 * Modifica il parametro body di Comment sul DB, filtrando tramite il projectId,
	 * resourceId e commentId , con il nuovo body passato dal metodo
	 *
	 * @param projectId id del progetto, resourceId id della risorsa, commentId id
	 *                  del commento associato, body parametro con cui fare l'update
	 */

	public void editComment(String projectId, String resourceId, String commentId, String body) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Comments");
		Bson filter = Filters.and(Filters.eq("project_id", projectId), Filters.eq("resource_id", resourceId),
				Filters.eq("id", commentId));
		coll.updateOne(filter, new Document("$set", new Document("body", body)));
		mongoClient.close();
	}

	/**
	 * Modifica il parametro type di SubscriptionPlan sul DB, filtrando tramite il
	 * projectId e subId , con il nuovo type passato dal metodo
	 *
	 * @param projectId id del progetto, subId id del piano, type nuovo tipo
	 */

	public void editSubType(String projectId, String subId, String type) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Subscription_plan");
		Bson filter = Filters.and(Filters.eq("project_id", projectId), Filters.eq("id", subId));
		coll.updateOne(filter, new Document("$set", new Document("type", type)));
		mongoClient.close();
	}

	/**
	 * Modifica il parametro price di SubscriptionPlan sul DB, filtrando tramite il
	 * projectId e subId , con il nuovo price passato dal metodo subId id del piano,
	 * price nuovo prezzo
	 */

	public boolean editSubPrice(String planId, double price) {
		if (price < 0)
			return false;

		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Subscription_plan");

		// Verifica se l'ID specificato esiste nella collezione Subscription_plan
		Bson filter = Filters.eq("_id", planId);
		Document existingSubscriptionPlan = coll.find(filter).first();
		if (existingSubscriptionPlan == null) {
			return false;
		}

		// Aggiornamento del prezzo del Subscription_plan
		Bson update = Updates.set("price", price);
		coll.updateOne(filter, update);
		mongoClient.close();
		return true;

	}

	/**
	 * Modifica il parametro typeDescription di SubscriptionPlan sul DB, filtrando
	 * tramite il projectId e subId , con il nuovo typeDescription passato dal
	 * metodo
	 *
	 * @param projectId id del progetto, subId id del piano, typeDescription nuova
	 *                  descrizione
	 */

	public void editSubTD(String projectId, String subId, String typeDP) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Subscription_plan");
		Bson filter = Filters.and(Filters.eq("project_id", projectId), Filters.eq("id", subId));
		coll.updateOne(filter, new Document("$set", new Document("typeDescriptionSub", typeDP)));
		mongoClient.close();
	}

	/**
	 * Modifica il parametro advanatages di SubscriptionPlan sul DB, filtrando
	 * tramite il projectId e subId , con il nuovo advanatages passato dal metodo
	 *
	 * @param projectId id del progetto, subId id del piano, advanatages nuovi
	 *                  vantaggi
	 */

	public void editSubAdvantages(String projectId, String subId, String advantages) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Subscription_plan");
		Bson filter = Filters.and(Filters.eq("project_id", projectId), Filters.eq("id", subId));
		coll.updateOne(filter, new Document("$set", new Document("advantages", advantages)));
		mongoClient.close();
	}

	@Override
	public void addUser(User u) {
		if (u != null) {
			MongoClient mongoClient = new MongoClient(connectionString);
			MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
			MongoCollection<Document> coll = db.getCollection("User");
			Document aUser;

			aUser = new Document("_id", u.getId()).append("name", u.getName()).append("surname", u.getSurname())
					.append("nickname", u.getNickname()).append("email", u.getEmail())
					.append("birthday", u.getBirthday()).append("password", u.getPassword())
					.append("skills", u.getSkills()).append("projects", u.getProjects())
					.append("purchase", u.getPurchase()).append("projectId", u.getProjectId());

			coll.insertOne(aUser);
			mongoClient.close();
		} else {
			throw new NullPointerException();
		}

	}

	public void updateUser(String nickname, String valueToAdd) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");

		BasicDBObject updateQuery = new BasicDBObject("nickname", nickname);
		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("projectId", valueToAdd));
		coll.updateOne(updateQuery, updateCommand);

		mongoClient.close();
	}

	public void addUserSecurity(User u) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("SparkSecurity");
		MongoCollection<Document> coll = db.getCollection("user");
		Document aUser;
		List<String> roles = new ArrayList<>();
		roles.add("ROLE_CUSTOMER");

		aUser = new Document().append("_id", u.getId().toString()).append("username", u.getNickname())
				.append("password", "{noop}" + u.getPassword()).append("roles", roles);

		coll.insertOne(aUser);
		mongoClient.close();

	}

	@Override
	public void addResource(ResourceS u, String projectId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");
		Document aResource;
		aResource = new Document("_id", u.getId()).append("name", u.getName())
				.append("resource_type", u.getResourceType()).append("body", u.getBody())
				.append("description", u.getDescription()).append("like", u.getLikes())
				.append("dislike", u.getDisikes());

		coll.updateOne(eq("_id", projectId), Updates.addToSet("resources", aResource));
		mongoClient.close();
	}

	public void addCommentToResource(Comment comment, String resourceId, String projectId) {

	}

	@Override
	public void addProject(Project u) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");
		Document aProject;

		aProject = new Document("_id", u.getId()).append("owner_id", u.getOwnerId()).append("name", u.getName())
				.append("description", u.getDescription()).append("project_area", u.getProjectAreas())
				.append("follower", u.getFollowers()).append("resources", u.getResources())
				.append("request", u.getRequest());

		coll.insertOne(aProject);
		mongoClient.close();
	}


	@Override
	public boolean deleteProject(String projectId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");
		Document aDocument = coll.findOneAndDelete(eq("_id", projectId));
		if (aDocument != null) {
			System.out.println("project with id " + " " + projectId + " deleted");
			mongoClient.close();
			return true;
		} else

			System.out.println("project with id : " + projectId + " is nonexistent ");
		mongoClient.close();
		return false;

	}

	/**
	 * Delete a user using its id.
	 *
	 * @param userId the id of the user
	 * @return:
	 */
	@Override
	public boolean deleteUser(String userId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("User");
		Document adocument = coll.findOneAndDelete(eq("_id", userId));

		if (adocument != null) {
			System.out.println("user with id " + " " + userId + " deleted");
			mongoClient.close();
			return true;
		} else

			System.out.println("user with id : " + userId + " is nonexistent ");
		mongoClient.close();
		return false;
	}

	public boolean deleteResource(String projectId, String resourceId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll1 = db.getCollection("Project");

		// Creazione del filtro per trovare il progetto con l'ID specificato
		BasicDBObject projectFilter = new BasicDBObject("_id", projectId);

		// Esegue la query per trovare il progetto
		Document projectDoc = coll1.find(projectFilter).first();

		if (projectDoc != null) {
			// Verifica se la risorsa esiste nell'array "resources" del progetto
			boolean resourceExists = projectDoc.getList("resources", Document.class).stream()
					.anyMatch(doc -> doc.getString("_id").equals(resourceId));

			if (resourceExists) {
				// Creazione del filtro per rimuovere la risorsa dall'array "resources"
				BasicDBObject resourceFilter = new BasicDBObject("_id", resourceId);
				BasicDBObject update = new BasicDBObject("$pull", new BasicDBObject("resources", resourceFilter));

				// Esegue l'operazione di aggiornamento per rimuovere la risorsa dall'array
				// "resources"
				UpdateResult result = coll1.updateOne(projectFilter, update);

				if (result.getModifiedCount() > 0) {
					System.out.println("Risorsa eliminata con successo.");
					return true;
				} else {
					System.out.println("Errore durante l'eliminazione della risorsa.");
					return false;
				}
			} else {
				System.out.println("La risorsa non è presente nel progetto.");
				return false;
			}
		} else {
			System.out.println("Il progetto non è stato trovato.");
			return false;
		}
	}

	@Override
	public boolean deletePlan(String planId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Subscription_plan");
		Document adocument = coll.findOneAndDelete(eq("_id", planId));

		if (adocument != null) {
			mongoClient.close();
			return true;
		} else
			mongoClient.close();
		return false;
	}

	public boolean deleteComment(String commentId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Comments");
		Document adocument = coll.findOneAndDelete(eq("_id", commentId));

		if (adocument != null) {
			System.out.println("comment with id " + " " + commentId + " deleted");
			mongoClient.close();
			return true;
		} else
			System.out.println("comment with id : " + commentId + " is nonexistent ");
		mongoClient.close();
		return false;
	}

	@Override
	public List<Project> getAllProject() {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> projectCollection = db.getCollection("Project");

		List<Project> projects = new ArrayList<>();

		for (Document current : projectCollection.find()) {
			projects.add(new Project(current.getString("_id"), current.getString("owner_id"), current.getString("name"),
					current.getString("descrpition")));
		}
		mongoClient.close();
		return projects;
	}

	@Override
	public List<User> getAllUser() {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> projectCollection = db.getCollection("User");

		List<User> users = new ArrayList<>();

		for (Document current : projectCollection.find()) {
			users.add(new User(current.getString("_id"), current.getString("name"), current.getString("surname"),
					current.getString("nickname"), current.getString("email"), current.getString("password"),
					current.getDate("birthday")));
		}
		mongoClient.close();
		return users;
	}

	@Override
	public List<ResourceS> getAllResource() {
		return null;
	}


	public List<Comment> getAllComment() {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> projectCollection = db.getCollection("Comments");

		List<Comment> cm = new ArrayList<>();

		for (Document current : projectCollection.find()) {
			cm.add(new Comment(current.getString("_id"), current.getString("body"), current.getString("user_id")));

		}
		mongoClient.close();
		return cm;

	}

	@Override
	public ResourceS getResource(Integer id) {

		return null;
	}

	// ------------------------------------edit method

	public boolean editProjectDescription(String projectId, String newDescription) {
		if (projectId == null || newDescription == null)
			return false;

		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		Document projectDocument = coll.find(eq("_id", projectId)).first();
		if (projectDocument == null) {
			System.out.println("Project with ID " + projectId + " not found");
			mongoClient.close();
			return false;
		}

		projectDocument.put("description", newDescription);

		coll.replaceOne(eq("_id", projectId), projectDocument);

		System.out.println("Project with ID " + projectId + " description updated");
		mongoClient.close();
		return true;
	}

	public boolean editProjectName(String projectId, String newName) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		Document query = new Document("_id", projectId);
		Document update = new Document("$set", new Document("name", newName));

		UpdateResult result = coll.updateOne(query, update);

		mongoClient.close();

		return result.getModifiedCount() > 0;
	}

	public void addRequest(String pg, String nickname) {
		try (MongoClient mongoClient = new MongoClient(connectionString)) {
			MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
			MongoCollection<Document> coll = db.getCollection("Project");
			MongoCollection<Document> coll2 = db.getCollection("User");

			if (controlloRichiesta(pg, nickname, coll, coll2)) {
				BasicDBObject updateQuery = new BasicDBObject("_id", pg);
				BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("request", nickname));
				coll.updateOne(updateQuery, updateCommand);
			}
		} catch (Exception e) {
			// Gestisci l'eccezione o stampa un messaggio di errore
			e.printStackTrace();
		}
	}

	public boolean controlloRichiesta(String projectId, String nickname, MongoCollection<Document> coll,
			MongoCollection<Document> coll2) {
		Document query = coll.find(new Document("_id", projectId)).first();

		if (query != null) {
			String owner = query.getString("owner_id");
			Document query2 = coll2.find(new Document("nickname", nickname)).first();

			if (query2 != null && owner.equals(query2.getString("_id"))) {
				return false; // Utente è il proprietario del progetto
			}

			List<String> user = (List<String>) query.get("request");
			if (user != null && user.contains(nickname)) {
				return false; // La richiesta è già presente
			}

			return true; // La richiesta può essere aggiunta
		}

		return false; // Progetto non trovato
	}

	public void removeRequest(String pg, String nickname, boolean esito) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");
		if (!esito) {
			BasicDBObject updateQuery = new BasicDBObject("_id", pg);
			BasicDBObject updateCommand = new BasicDBObject("$pull", new BasicDBObject("request", nickname));
			coll.updateOne(updateQuery, updateCommand);
			mongoClient.close();
		} else {
			MongoCollection<Document> coll1 = db.getCollection("User");
			BasicDBObject updateQuery = new BasicDBObject("nickname", nickname);
			BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("projectId", pg));
			coll1.updateOne(updateQuery, updateCommand);
			BasicDBObject updateQuery1 = new BasicDBObject("_id", pg);
			BasicDBObject updateCommand1 = new BasicDBObject("$pull", new BasicDBObject("request", nickname));
			coll.updateOne(updateQuery1, updateCommand1);
			mongoClient.close();

		}
	}

	public void addPlan(String projectId, String nickname) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll1 = db.getCollection("User");
		BasicDBObject searchQuery = new BasicDBObject("nickname", nickname).append("purchase",
				new BasicDBObject("$ne", projectId));
		Document existingUser = coll1.find(searchQuery).first();
		if (existingUser != null) {
			BasicDBObject updateQuery = new BasicDBObject("nickname", nickname);
			BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("purchase", projectId));
			coll1.updateOne(updateQuery, updateCommand);
			System.out.println("Progetto aggiunto correttamente.");
		} else {
			System.out.println("Id già presente.");
		}

		mongoClient.close();
	}
	
	public void removePlan(String projectId, String nickname) {
	    MongoClient mongoClient = new MongoClient(connectionString);
	    MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
	    MongoCollection<Document> coll1 = db.getCollection("User");
	    
	    BasicDBObject searchQuery = new BasicDBObject("nickname", nickname)
	        .append("purchase", projectId);
	    
	    Document existingUser = coll1.find(searchQuery).first();
	    
	    if (existingUser != null) {
	        BasicDBObject updateQuery = new BasicDBObject("nickname", nickname);
	        BasicDBObject updateCommand = new BasicDBObject("$pull", new BasicDBObject("purchase", projectId));
	        coll1.updateOne(updateQuery, updateCommand);
	        System.out.println("Progetto rimosso correttamente.");
	    } else {
	        System.out.println("ID non trovato o già rimosso.");
	    }
	    
	    mongoClient.close();
	}


	public void addRes(String projectId, ResourceS r) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll1 = db.getCollection("Project");

		// Creazione del filtro per trovare il progetto con l'ID specificato
		BasicDBObject projectFilter = new BasicDBObject("_id", projectId);

		// Esegue la query per trovare il progetto
		Document projectDoc = coll1.find(projectFilter).first();

		if (projectDoc != null) {
			// Verifica se la risorsa esiste già nell'array "resources" del progetto
			BasicDBObject resourceFilter = new BasicDBObject("_id", r.getId());
			boolean resourceExists = projectDoc.getList("resources", Document.class).stream()
					.anyMatch(doc -> doc.getString("_id").equals(r.getId()));

			if (resourceExists)
				System.out.println("La risorsa esiste già nel progetto.");
			else {

				Document resourceDoc = new Document("_id", r.getId()).append("name", r.getName())
						.append("resource_type", r.getResourceType()).append("body", r.getBody())
						.append("description", r.getDescription()).append("user_id", r.getUserId())
						.append("comment", new ArrayList<>()).append("like", new ArrayList<>());

				// Aggiunta della risorsa all'array "resources" del progetto nel database
				BasicDBObject update = new BasicDBObject("$addToSet", new BasicDBObject("resources", resourceDoc));
				coll1.updateOne(projectFilter, update);
			}
		} else {
			System.out.println("Il progetto non è stato trovato.");
		}
	}

	public void addCommentToRes(String projectId, String resourceId, Comment comment) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");
		// Creazione del filtro per trovare il progetto con l'ID specificato
		Bson filter = Filters.and(eq("_id", projectId), eq("resources._id", resourceId));

		// Creazione del documento per l'oggetto Comment
		Document commentDoc = new Document("_id", comment.getId()).append("body", comment.getBody()).append("userId",
				comment.getUserId());

		// Aggiunta del commento all'array "comment" della risorsa nel progetto
		Bson update = Updates.addToSet("resources.$[resource].comment", commentDoc);
		UpdateOptions options = new UpdateOptions().arrayFilters(Arrays.asList(Filters.eq("resource._id", resourceId)));

		// Aggiornamento del documento nel database
		coll.updateOne(filter, update, options);

		mongoClient.close();
	}

	public List<Comment> getCommentForRes(String projectId, String resourceId) {
		try (MongoClient mongoClient = new MongoClient(connectionString)) {
			MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
			MongoCollection<Document> coll = db.getCollection("Project");

			Bson filter = Filters.and(eq("_id", projectId), eq("resources._id", resourceId));

			Bson projection = Projections.elemMatch("resources", eq("_id", resourceId));

			Document project = coll.find(filter).projection(projection).first();

			if (project != null) {
				Document resource = project.getList("resources", Document.class).get(0);
				List<Document> comments = resource.getList("comment", Document.class);

				List<Comment> result = new ArrayList<>();

				for (Document commentDoc : comments) {
					Comment comment = new Comment(commentDoc.getString("_id"), commentDoc.getString("body"),
							commentDoc.getString("userId"));
					result.add(comment);
				}
				return result;
			}
		}
		return Collections.emptyList();
	}

	public void removeCommentFromRes(String projectId, String resourceId, String commentId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		// Creazione del filtro per trovare il progetto con l'ID specificato
		Bson filter = Filters.and(eq("_id", projectId), eq("resources._id", resourceId));

		// Aggiunta dell'operatore $pull per rimuovere il commento dall'array "comment"
		// della risorsa nel progetto
		Bson update = Updates.pull("resources.$[resource].comment", new Document("_id", commentId));
		UpdateOptions options = new UpdateOptions().arrayFilters(Arrays.asList(Filters.eq("resource._id", resourceId)));

		// Aggiornamento del documento nel database
		coll.updateOne(filter, update, options);

		mongoClient.close();
	}

	public List<User> getUserByNickname(String searchTerm) {
		if (searchTerm != null) {
			MongoClient mongoClient = new MongoClient(connectionString);
			MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
			MongoCollection<Document> userCollection = db.getCollection("User");

			List<User> users = new ArrayList<>();
			Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
			Bson filter = Filters.regex("nickname", pattern);

			for (Document current : userCollection.find(filter)) {
				users.add(new User(current.getString("_id"), current.getString("name"), current.getString("surname"),
						current.getString("nickname"), current.getString("email"), current.getString("password"),
						current.getDate("birthday")));
			}

			mongoClient.close();
			return users;
		} else {
			return null;
		}
	}

	public User getUserByNicknameWithProjects(String nick) {

		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> userCollection = db.getCollection("User");
		Document query = new Document("nickname", nick);
		Document res = userCollection.find(query).first();

		User us = new User(res.get("_id").toString(), res.get("name").toString(), res.get("surname").toString(),
				res.get("nickname").toString(), res.get("email").toString(), res.get("password").toString(),
				res.getDate("birthday"), res.getList("projectId", String.class), true);
		for (String s : us.getProjectId()) {
			System.out.println(s);
		}
		return us;
	}

	public void addLikeToRes(String prgId, String resId, String username) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");
		// Creazione del filtro per trovare il progetto con l'ID specificato
		Bson filter = Filters.and(eq("_id", prgId), eq("resources._id", resId));
		// Aggiunta del commento all'array "like" della risorsa nel progetto
		Bson update = Updates.addToSet("resources.$[resource].like", username);
		UpdateOptions options = new UpdateOptions().arrayFilters(Arrays.asList(Filters.eq("resource._id", resId)));

		// Aggiornamento del documento nel database
		coll.updateOne(filter, update, options);

		mongoClient.close();

	}

	public void removeLikeToRes(String prgId, String resId, String username) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");
		// Creazione del filtro per trovare il progetto con l'ID specificato
		Bson filter = Filters.and(eq("_id", prgId), eq("resources._id", resId));
		// Aggiunta del commento all'array "like" della risorsa nel progetto
		Bson update = Updates.pull("resources.$[resource].like", username);
		UpdateOptions options = new UpdateOptions().arrayFilters(Arrays.asList(Filters.eq("resource._id", resId)));

		// Aggiornamento del documento nel database
		coll.updateOne(filter, update, options);

		mongoClient.close();

	}

	// Aggiornamento del documento nel database
	public int countLike(String prgId, String resId) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		// Creazione del filtro per trovare il progetto con l'ID specificato
		Bson filter = Filters.and(eq("_id", prgId), eq("resources._id", resId));

		Document projectDoc = coll.find(filter).first();
		int likeCount = 0;

		if (projectDoc != null) {
			List<Document> resources = projectDoc.getList("resources", Document.class);

			for (Document resource : resources) {
				if (resource.getString("_id").equals(resId)) {
					List<String> likeList = resource.getList("like", String.class);
					likeCount = likeList.size();
					break;
				}
			}
		}

		mongoClient.close();

		return likeCount;
	}

	public boolean controllaLike(String prgId, String resId, String username) {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll = db.getCollection("Project");

		// Creazione del filtro per trovare il progetto con l'ID specificato
		Bson filter = Filters.and(eq("_id", prgId), eq("resources._id", resId), eq("resources.like", username));

		boolean flag = coll.countDocuments(filter) > 0;

		mongoClient.close();

		return flag;
	}

	public void deleteAllResource() {
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase("DB_SparkLab_G1");
		MongoCollection<Document> coll1 = db.getCollection("Project");

		Bson update = new Document("$unset", new Document("resources", ""));
		coll1.updateMany(new Document(), update);
		System.out.println("fatto\n\n\n");
	}

}
