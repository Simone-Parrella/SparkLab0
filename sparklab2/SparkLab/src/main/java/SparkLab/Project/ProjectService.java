package SparkLab.Project;

import SparkLab.Project.beans.HttpClientWrapper;
import SparkLab.Project.beans.HttpClientWrapperImpl;
import SparkLab.Project.beans.ProjectBean;
import SparkLab.Project.beans.Query;
import WebAppExceptions.Exception404;
import WebAppExceptions.LocalInfo;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import DatabaseInterface.*;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Consumes("application/json")
@Produces("application/json")
@Path("/ProjectAPI")
public class ProjectService {
    private DatabaseAdapter projectDB= MongoDatabaseAdapter.getIstance();
    private HttpClientWrapper httpClient= new HttpClientWrapperImpl();


    public void setHttpClient(HttpClientWrapper httpClient) {
        this.httpClient = httpClient;
    }

    //CREA IL PROGETTO , PRENDO IL NOME DELL' OWNER E RISALISCO AD ESSO
    @POST
    @Path("/project")
    @Consumes("application/json")
    public void createProject(@RequestBody ProjectBean pb){
        String name= SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = projectDB.getUser(name);
        String idOwner = owner.getId();
        Project prg=new Project(new ObjectId().toString(),idOwner,
                pb.getName(), pb.getDescription(), pb.getProjectAreas());
        owner.addProjectId(prg.getId());
        projectDB.updateUser(owner.getNickname(), prg.getId());
        projectDB.addProject(prg);

//        httpClient = HttpClients.createDefault();
        String url = "http://"+LocalInfo.BRAIN_IP+":"+LocalInfo.BRAIN_PORT + "/update";
        HttpPost poke_brain = new HttpPost(url);

        try {
            HttpResponse res= httpClient.execute(poke_brain);
        } catch (IOException e) {
            System.err.println("Brain not available");
            throw new RuntimeException(e);
        }


    }

    //PRENDO IL PROGETTO IN BASE ALL'ID 
    @GET
    @Path("/project/{id}")
    @Produces({"application/json", "text/html"})
    public Response getProject(@PathParam("id") String id) {
        Project pr = projectDB.getProject(id);
        if (pr == null) {
            throw new Exception404("Unable to load this project id");
        } else return Response.ok(pr).build();
    }

    //EFFETTUO UNA RICHIESTA PER IL PROGETTO AVENDO L'ID E IL NOME(DALLA SESSIONE)
    @POST
    @Path("/project/request/{projectId}")
    @Produces({"application/json", "text/html"})
    public Response sendRequestForProject(@PathParam("projectId") String projectId) {
        Project project = projectDB.getProject(projectId);
        if (project == null) {
            throw new Exception404("Unable to load this project ID");
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            projectDB.addRequest(project.getId(), username);
            return Response.ok(project).build();
        }
    }

    @PUT
    @Path("/project/request/{projectId}/{username}/{decision}")
    @Produces({"application/json", "text/html"})
    public Response respondToRequestForProject(@PathParam("projectId") String projectId,@PathParam("username") String username, @PathParam("decision") boolean decision) {
        Project project = projectDB.getProject(projectId);
        if (project == null) {
            throw new Exception404("Unable to load this project ID");
        } else {
            projectDB.removeRequest(project.getId(), username, decision);
            return Response.ok(project).build();
        }}

    @PUT
    @Path("project/{id}/{name}")
    @Produces({"application/json", "text/html"})
    public Response modifyName(@PathParam("id") String id, @PathParam("name") String name) {
        Project project = projectDB.getProject(id);
        if (project == null) {
            throw new Exception404("Unable to load this project ID");
        } else {
            projectDB.editProjectName(id, name);
            return Response.ok(project).build();
        }
    }

    @PUT
    @Path("project/{id}/mod/{description}")
    @Produces({"application/json", "text/html"})
    public Response modifyDescription(@PathParam("id") String id, @PathParam("description") String description) {
        Project project = projectDB.getProject(id);
        if (project == null) {
            throw new Exception404("Unable to load this project ID");
        } else {
            projectDB.editProjectDescription(id, description);
            return Response.ok(project).build();
        }
    }

    @GET
    @Path("/project")
    @Produces("application/json")
    public List<Project> getAllProject() {
        return projectDB.getAllProject();
    }

    @GET
    @Path("/project/for/{filter}")
    @Produces("application/json")
    public List<Project> getProjectFilter(@PathParam("filter") String filter) {
        return projectDB.getProjectsByName(filter);
    }

    @GET
    @Path("/project/brain/{query}")
    @Produces("application/json")
    public Response getProjectWithBrain(@PathParam("query") String filter) {


        HttpClient httpClient = HttpClients.createDefault();
        Gson gson = new Gson();

        try {

            String url = "http://127.0.0.1:8081/search";
            HttpPost httpPost = new HttpPost(url);
            StringEntity requestEntity = new StringEntity(gson.toJson(new Query(filter)));
            requestEntity.setContentType("application/json");
            httpPost.setEntity(requestEntity);
            HttpResponse response = httpClient.execute(httpPost);


            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);
            HashMap<String,ArrayList<String>> response_json = gson.fromJson(responseBody, HashMap.class);

            if (statusCode != 200) return Response.serverError().build();
            else {

                List<Project> projL = projectDB.getProjectPack(response_json.get("ids"));
                return Response.ok(projL).build();
            }

            } catch(IOException e){

            e.printStackTrace();
            return Response.serverError().entity("Sparklab brain not available").build();

            } finally{

                httpClient.getConnectionManager().shutdown();
            }


    }




    // ELIMINA IL PROGETTO ATTRAVERSO L'ID
    @DELETE
    @Path("/project/{id}")
    public Response  deleteProject(@PathParam("id") String id) {
        if (projectDB.deleteProject(id)) return Response.ok().build();
        else throw new Exception404("unable to find the desired project");
    }

    //EDITA LA DESCRIZIONE DEL PROGETTO AVENDO COME PARAMETRO L'ID
    @POST
    @Path("/{projectId}/description")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProjectDescription(@PathParam("projectId") String projectId, String newDescription) {
        boolean success = projectDB.editProjectDescription(projectId, newDescription);
        if (success) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    //EDITA IL NOME DEL PROGETTO
    @POST
    @Path("/{projectId}/name")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProjectName(@PathParam("projectId") String projectId, String newName) {
        boolean success = projectDB.editProjectName(projectId, newName);
        if (success) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }




}