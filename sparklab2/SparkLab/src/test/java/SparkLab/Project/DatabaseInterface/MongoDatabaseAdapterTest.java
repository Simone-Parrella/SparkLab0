package SparkLab.Project.DatabaseInterface;



import DatabaseInterface.*;


import org.junit.jupiter.api.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MongoDatabaseAdapterTest {

    private static MongoDatabaseAdapter databaseAdapter;
    private static User user=null;
    private static Project aproject=null;
    private static ResourceS aresourceS=null;
    private static Comment acomment=null;


    /*
  this method generate random id different from the item in a collection
   */
    public static String generateRandomId(List<String> existingIds) {

        String randomId;
        do {
            randomId = UUID.randomUUID().toString();
        } while (existingIds.contains(randomId));
        return randomId;

    }


    @BeforeAll
    public static void setUp() throws ParseException {
        databaseAdapter = MongoDatabaseAdapter.getIstance();
        // Initialize the database adapter instance
    }

    @AfterAll
    public static void tearDown() {
        // Release resources
        databaseAdapter = null;
        user = null;
    }

    @Order(1)
    @Test
    public void addUserTest() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<User> users = databaseAdapter.getAllUser();
        List<String> usersNickname = new ArrayList<>();
        List<String> usersId = new ArrayList<>();
        for (User user1 : users) {
            usersNickname.add(user1.getNickname());
        }
        for (User user1 : users) {
            usersId.add(user1.getId());
        }

        // User that does not exist in the database
        user = new User(generateRandomId(usersId),
                "nomeutente1",
                "surnameutente1",
                generateRandomId(usersNickname),
                "emailutente1",
                "password1",
                formatter.parse("2010-03-03"));

        Assertions.assertDoesNotThrow(() -> databaseAdapter.addUser(user));
    }

    @Order(2)
    @Test
    public void addProjectTest() {
        List<String> projectsId = new ArrayList<>();
        for (Project project : databaseAdapter.getAllProject()) {
            projectsId.add(project.getId());
        }

        aproject = new Project(generateRandomId(projectsId), user.getId(), "nomeprogetto1", "descriptionproject1",
                new ArrayList<>(), 0, new ArrayList<>());

        Assertions.assertDoesNotThrow(() -> databaseAdapter.addProject(aproject));
    }

    @Order(3)
    @Test
    public void addResourceToProjectTest() {
        aresourceS = new ResourceS(UUID.randomUUID().toString(), "nomerisorsa1", "resourceType1",
                "bodyrisorsa1", "descriptionresource1", user.getId(),0,0,new ArrayList<>());

        Assertions.assertTrue(databaseAdapter.addResourceToProject(aproject.getId(), aresourceS));
    }

    @Order(4)
    @Test
    public void addCommentToResourceTest() {
        acomment = new Comment(UUID.randomUUID().toString(), "bodycommento1", user.getId());

        Assertions.assertDoesNotThrow(() -> databaseAdapter.addCommentToRes(aproject.getId(), aresourceS.getId(), acomment));
    }

    @Order(5)
    @Test
    public void validUserTest() {
        Assertions.assertNotNull(databaseAdapter.getUser(user.getNickname()), "user must not be null");
    }

    @Order(6)
    @Test
    public void getNotPresentUserTest() {
        List<String> usersNickname = new ArrayList<>();
        for (User user1 : databaseAdapter.getAllUser()) {
            usersNickname.add(user1.getNickname());
        }
        Assertions.assertNull(databaseAdapter.getUser(generateRandomId(usersNickname)));
    }



    @Order(7)
    @Test
    public void deleteNullUserTest() {
        Assertions.assertFalse(databaseAdapter.deleteUser(null));
    }

    @Order(8)
    @Test
    public void getNullUserTest() {
        Assertions.assertNull(databaseAdapter.getUser(null));
    }

    @Order(9)
    @Test
    public void testAddNullUserTest() {
        Assertions.assertThrows(Exception.class, () -> databaseAdapter.addUser(null));
    }

    @Order(10)
    @Test
    public void addDoubleUserTest() {
        Assertions.assertThrows(Exception.class, () -> databaseAdapter.addUser(user));
    }

    @Order(11)
    @Test
    public void testGetUserByEmailTest() {
        Assertions.assertNotNull(databaseAdapter.getUserByEmail(user.getEmail()));
        Assertions.assertNull(databaseAdapter.getUserByEmail(null));
    }

    @Order(12)
    @Test
    public void getProjectTest() {
        Assertions.assertEquals(aproject, databaseAdapter.getProject(aproject.getId()));
    }

    @Order(13)
    @Test
    public void getNullProjectTest() {
        Assertions.assertNull(databaseAdapter.getProject(null));
    }

    @Order(14)
    @Test
    public void testProjectPackTest() {
        List<String> projectList = new ArrayList<>();
        projectList.add(aproject.getId());
        List<Project> projects = new ArrayList<>();
        projects.add(aproject);
        Assertions.assertEquals(projects, databaseAdapter.getProjectPack(projectList));
    }

    @Order(15)
    @Test
    public void testNullProjectPackTest() {
        Assertions.assertNull(databaseAdapter.getProjectPack(null));
    }

    @Order(16)
    @Test
    public void editProjectDescriptionTest() {
        String newProjectDescription = "new projectDescription";
        Assertions.assertTrue(databaseAdapter.editProjectDescription(aproject.getId(), newProjectDescription));
        aproject.setDescription(newProjectDescription);
        Assertions.assertFalse(databaseAdapter.editProjectDescription(aproject.getId(), null));
        Assertions.assertFalse(databaseAdapter.editProjectDescription(null, null));
    }

    @Order(17)
    @Test
    public void testGetProjectByNameTest() {
        List<String> projectListName = new ArrayList<>();
        for (Project p1 : databaseAdapter.getAllProject()) {
            projectListName.add(p1.getName());
        }
        List<Project> myArrayListProject = new ArrayList<>();
        myArrayListProject.add(aproject);
        Assertions.assertEquals(myArrayListProject, databaseAdapter.getProjectsByName(aproject.getName()));
        Assertions.assertNotEquals(myArrayListProject, databaseAdapter.getProjectsByName(generateRandomId(projectListName)));
        Assertions.assertNull(databaseAdapter.getProjectsByName(null));
    }

    @Order(18)
    @Test
    public void getCommentTest() {
        List<Comment> comments = databaseAdapter.getCommentForRes(aproject.getId(), aresourceS.getId());
        Assertions.assertEquals(1, comments.size());
        Assertions.assertTrue(comments.contains(acomment));

        databaseAdapter.removeCommentFromRes(aproject.getId(), aresourceS.getId(), acomment.getId());

        comments = databaseAdapter.getCommentForRes(aproject.getId(), aresourceS.getId());
        Assertions.assertEquals(0, comments.size());
        Assertions.assertFalse(comments.contains(acomment));

        Comment nullComment = databaseAdapter.getComment(null);
        Assertions.assertNull(nullComment);
    }

    @Order(19)
    @Test
    public void editProjectNameTest() {
        String newName = "New Project Name";
        boolean result = databaseAdapter.editProjectName(aproject.getId(), newName);
        aproject.setName(newName);
        Assertions.assertTrue(result);
    }

    @Order(20)
    @Test
    public void removeRequestTest_EsitoFalse() {
        String request = "Request to be removed";
        boolean esito = false;
        databaseAdapter.removeRequest(aproject.getId(), request, esito);
    }

    @Order(21)
    @Test
    public void removeRequestTest_EsitoTrue() {
        String request = "Request to be moved";
        boolean esito = true;
        databaseAdapter.removeRequest(aproject.getId(), request, esito);
    }

    @Order(22)
    @Test
    public void addResTest() {
        databaseAdapter.addRes(aproject.getId(), aresourceS);
        Assertions.assertEquals(aresourceS, databaseAdapter.getResourceList(aproject.getId()).get(0));
    }



    @Order(23)
    @Test
    public void deleteResTest() {
        Assertions.assertTrue(databaseAdapter.deleteResource(aproject.getId(), aresourceS.getId()));
    }
    @Order(24)
    @Test
    public void deleteTest(){
        Assertions.assertTrue(databaseAdapter.deleteUser(user.getId()));
        Assertions.assertTrue(databaseAdapter.deleteProject(aproject.getId()));
    }
}