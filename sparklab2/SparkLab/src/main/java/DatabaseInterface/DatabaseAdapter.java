package DatabaseInterface;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface  DatabaseAdapter {

    User getUser(String id);
    ArrayList<ResourceS> getResourceList(String ProjectName);
    Project getProject(String id);
	ResourceS getResource(Integer id);
	Project mostFollowed_greater_than(int followers, String OwnerId);



    User getUserByEmail(String email);

  
    //---------------------------------------- add
    void addUser(User u);
    void addResource(ResourceS u, String projectId);
    void addProject(Project pr);
  



    //------------------------------------- delete
    boolean deleteProject(String projectId);
    boolean deleteUser(String userId);
    boolean deleteResource(String resourceId,String ProjectId);
    boolean deletePlan(String planId);
    boolean deleteComment(String commentId);

    //-------------------------------------- getAll
    public List<Project> getProjectPack(List<String> projIDs);
    List<Project> getAllProject();
    List<User> getAllUser();
    List<ResourceS> getAllResource();
   
    
    //-------------------------------------- edit
    
    void editName(String nickname, String name);
    void editSurname(String nickname, String surname);
    void editMail(String nickname, String mail);
    void editPassword(String nickname, String password);
    void editComment(String projectId, String resourceId, String commentId, String body);
    void editSubType(String projectId, String subId, String type);
    boolean editSubPrice(String projectId, double price);
    void editSubTD(String projectId, String subId, String typeDP);
    void editSubAdvantages(String projectId, String subId, String advantages);
    boolean editProjectName(String ProjectId, String name);
    boolean editProjectDescription(String ProjectId, String description);
    //-------------------------------------- others
     long getCountDocuments(String collection);

    void updateUser(String nickname, String valueToAdd);
    void addRequest(String pg, String username);
    void removeRequest(String pg, String username, boolean esito);

    boolean controlloRichiesta(String projectId, String userId, MongoCollection<Document> coll, MongoCollection<Document> coll2);
    List<Project> getProjectsByName(String searchTerm);

    void addPlan(String projectId, String nickname);

    void addRes(String projectId, ResourceS r);
    List<Comment> getCommentForRes(String projectId, String resourceId);
    void addCommentToRes(String projectId, String resourceId, Comment comment);
    List<User> getUserByNickname(String searchTerm);

    User getUserId(String nickname);

    void addLikeToRes(String prgId,String resId,String username);

    void removeLikeToRes(String prgId, String resId, String username);
    public int countLike(String prgId, String resId);

    public boolean controllaLike(String prgId, String resId, String username);

    public void deleteAllResource();
}
