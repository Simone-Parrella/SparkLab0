package SparkLab.Project;

import DatabaseInterface.Project;

import DatabaseInterface.User;
import SparkLab.Project.beans.HttpClientWrapper;
import SparkLab.Project.beans.ProjectBean;
import SparkLab.Project.beans.UserBean;
import WebAppExceptions.Exception404;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ProjectServiceTest {
    private static UserBean userBean = new UserBean();
    private static ProjectBean projectBean = new ProjectBean();
    private static final ProjectService projectService = new ProjectService();
    private static Project aproject = null;
    private static User user = new User();
    private static final UserService userService = new UserService();
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    // Crea gli stub per i componenti utilizzati all'interno dei metodi di test

    //L'annotazione Mock serve per gli stub
    @Mock
    private static CloseableHttpResponse httpResponseStub;
    //il problema è che per creare un progetto bisogna prima creare un utente quindi creiamo sia
    //l'user che il project
    
    //L’annotazione @BeforeAll è utilizzata in JUnit per definire un metodo di setup che verrà eseguito solo una volta prima di tutti i metodi di test all’interno della classe di test.
    @BeforeAll
    public static void Setup() throws ParseException, IOException {
        // Crea il mock dell'HttpClientWrapper
        HttpClientWrapper httpClientStub = Mockito.mock(HttpClientWrapper.class);

        // Imposta il comportamento dello stub per il metodo execute()
        Mockito.when(httpClientStub.execute(Mockito.any(HttpUriRequest.class))).thenReturn(httpResponseStub);

        // Sostituisci il componente reale con lo stub nel ProjectService
        projectService.setHttpClient(httpClientStub);

        userBean.setName("Giovanni");
        userBean.setSurname("Doea");
        userBean.setNickname("giovanni");
        userBean.setEmail("Giovannidoe@example.com");
        userBean.setPassword("password");
        userBean.setBirthday(formatter.parse("2022-10-04"));

        // Esecuzione del metodo da testare
        try (Response response = userService.createUser(userBean)) {
            // Verifica del risultato
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userBean.getNickname(), userBean.getPassword()));

        // Esecuzione del metodo da testare
        Response response = userService.getUserBySessionWithProjects();

        // Verifica del risultato
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Recupera l'oggetto User dal corpo della risposta
        user = (User) response.getEntity();

        // Verifica che l'utente sia stato trovato correttamente
        Assertions.assertNotNull(user);
        assertEquals(userBean.getEmail(), user.getEmail());
        projectBean = new ProjectBean("name-project-testing-1", "descriptionproject1");

        projectService.createProject(projectBean);

        //first of all verify that the project is added to db
        aproject = projectService.getProjectFilter(projectBean.getName()).get(0);
        Assertions.assertEquals(projectBean.getName(), aproject.getName());

    }
    
   // L'annotazione @afterall è una specifica funzionalità di alcuni framework di testing,
    //che consente di eseguire del codice dopo l'esecuzione di tutti gli scenari di test all'interno
    //di un file di specifica.

    @AfterAll
    public static void TierDown() {
        Response response = projectService.deleteProject(aproject.getId());
        Assertions.assertEquals(200, response.getStatus());
        userService.deleteUser(user.getId());
    }
    
    
    //L'annotazione @Test è una specifica funzionalità fornita da framework di testing come JUnit, 
    //TestNG o altri simili, utilizzata per identificare i metodi di test all'interno di una classe.


    @Test
    public void editProjectDescriptionTest() {
        Response response = projectService.editProjectDescription(aproject.getId(), "new description project");
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void sendRequestForProjectTest() {
        // Esegui il login dell'utente
        Authentication authentication = new UsernamePasswordAuthenticationToken(userBean.getNickname(), userBean.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Esegui la chiamata al metodo sendRequestForProject
        Response response = projectService.sendRequestForProject(aproject.getId());

        // Verifica il risultato della chiamata
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Project project = (Project) response.getEntity();
        Assertions.assertNotNull(project);

    }

    @Test
    public void respondToRequestForProjectTest() {

        // Esegui il login dell'utente
        Authentication authentication = new UsernamePasswordAuthenticationToken(userBean.getNickname(), userBean.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Esegui la chiamata al metodo respondToRequestForProject
        Response response = projectService.respondToRequestForProject(aproject.getId(), userBean.getNickname(), true);

        // Verifica il risultato della chiamata
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Project project = (Project) response.getEntity();
        Assertions.assertNotNull(project);

    }

    @Test
    public void sendRequestForNullProjectTest() {
        // Esegui il login dell'utente
        Authentication authentication = new UsernamePasswordAuthenticationToken(userBean.getNickname(), userBean.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);


        // Verifica il risultato della chiamata
        Assertions.assertThrows(Exception404.class, () ->
                projectService.sendRequestForProject(null));

    }

    @Test
    public void respondToNullProjectRequestTest() {

        // Esegui il login dell'utente
        Authentication authentication = new UsernamePasswordAuthenticationToken(userBean.getNickname(), userBean.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Esegui la chiamata al metodo respondToRequestForProject con un ID di progetto null
        Assertions.assertThrows(Exception404.class, () -> projectService.respondToRequestForProject(null, userBean.getNickname(), true));
    }

    @Test
    public void modifyNameTest() {

        // Call the `modifyName` method with the ID of the existing project.
        Response response = projectService.modifyName(aproject.getId(), "New Project Name");

        // Verify that the response status code is 200.
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

       Response response1= projectService.getProject(aproject.getId());
        // Verify that the response status code is 200.
        assertEquals(Response.Status.OK.getStatusCode(), response1.getStatus());

        // Verify that the `Project` object returned by the `modifyName` method has the new name.
        Project returnedProject = (Project) response1.getEntity();
        assertEquals("New Project Name", returnedProject.getName());
    }
    @Test
    public void modifyNameWithNullNameTest() {
        Assertions.assertThrows(Exception404.class,()->projectService.modifyName(null,"x"));
    }

    @Test
    public void modifyDescriptionWithNullDescriptionTest() {

        // Call the `modifyDescription` method with the ID of the new project and a null description.
        Assertions.assertThrows(Exception404.class,()->{projectService.modifyDescription(null,"x");});
    }

    @Test
    public void testEditProjectDescription_ProjectNotFound() {
        // Setup
        String projectId = "non_existing_project_id";
        String newDescription = "New project description";

        // Esecuzione del metodo da testare
        Response response = projectService.editProjectDescription(projectId, newDescription);

        // Verifica del risultato
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEditProjectName_ProjectNotFound() {
        // Setup
        String projectId = "non_existing_project_id";
        String newName = "New project name";

        // Esecuzione del metodo da testare
        Response response = projectService.editProjectName(projectId, newName);

        // Verifica del risultato
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEditProjectDescription() {
        // Setup
        String projectId = aproject.getId();
        String newDescription = "New project description";

        // Esecuzione del metodo da testare
        Response response = projectService.editProjectDescription(projectId, newDescription);

        // Verifica del risultato
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        aproject.setDescription("New project description");

    }

    @Test
    public void testEditProjectName() {
        // Setup
        String projectId = aproject.getId();
        String newName = "New project name";

        // Esecuzione del metodo da testare
        Response response = projectService.editProjectName(projectId, newName);

        // Verifica del risultato
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        aproject.setName("New project name");

    }



}
