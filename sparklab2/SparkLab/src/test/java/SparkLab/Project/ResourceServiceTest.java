package SparkLab.Project;

import DatabaseInterface.Project;
import DatabaseInterface.ResourceS;
import DatabaseInterface.User;
import SparkLab.Project.beans.FileUploadRequest;
import SparkLab.Project.beans.HttpClientWrapper;
import SparkLab.Project.beans.ProjectBean;
import SparkLab.Project.beans.UserBean;
import WebAppExceptions.Exception404;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResourceServiceTest {

    private Authentication authentication;
    private SecurityContext securityContext;

    private static ResourceService resourceService=new ResourceService();

    private static UserBean userBean = new UserBean();
    private static ProjectBean projectBean = new ProjectBean();
    private static final ProjectService projectService = new ProjectService();
    private static Project aproject = null;
    private static User user = new User();
    private  static ResourceS resourceS=null;
    private static final UserService userService = new UserService();
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    // Crea gli stub per i componenti utilizzati all'interno dei metodi di test
    //Dato che Brain non è in esecuzione durante il testing allora va creato uno stub 
    //per avere sempre l'ok della risposta http
    @Mock
    private static CloseableHttpResponse httpResponseStub;
    @BeforeAll
    public static void Setup() throws ParseException, IOException {

        // Crea il mock dell'HttpClientWrapper
        HttpClientWrapper httpClientStub = Mockito.mock(HttpClientWrapper.class);

        // Imposta il comportamento dello stub per il metodo execute()
        Mockito.when(httpClientStub.execute(Mockito.any(HttpUriRequest.class))).thenReturn(httpResponseStub);

        // Sostituisci il componente reale con lo stub nel ProjectService
        projectService.setHttpClient(httpClientStub);

        userBean.setName("John");
        userBean.setSurname("Doe");
        userBean.setNickname("johndoe");
        userBean.setEmail("newjhondoemail@gmai.com");
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

    @AfterAll
    public static void TierDown() {

        Response response1 = projectService.deleteProject(aproject.getId());
        Assertions.assertEquals(200, response1.getStatus());
        userService.deleteUser(user.getId());

    }


    /**
     * Test per controllare se il caricamento di una risorsa ha avuto successo
     */
    @Test
    @Order(1)
    public void testHandleFileUpload() {
        // Preparazione

        String description = "Example description";
        String fileName = "test.txt";
        List<Integer> fileData = Arrays.asList(65, 66, 67); // ASCII values of 'A', 'B', 'C'
        FileUploadRequest request = new FileUploadRequest();
        request.setFileName(fileName);
        request.setFileType(MediaType.TEXT_PLAIN);
        request.setFileData(fileData);



        // Esecuzione
        ResponseEntity<String> response = resourceService.handleFileUpload(request, aproject.getId(), description);

        // Verifica
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCodeValue());
        assertEquals("File caricato con successo", response.getBody());
    }

    /**
     * This method test that the resource is associated to a project
     */
    @Test
    @Order(2)
    public void testGetResourceFromProject(){
        Response response= resourceService.getResourceByProjectName(aproject.getId());
        Assertions.assertEquals(200,response.getStatus());
        List<ResourceS> resourceList= (List<ResourceS>) response.getEntity();
        resourceS=resourceList.get(0);
        Assertions.assertEquals(resourceS.getName(),"test.txt");
    }
    /**
     * Test per controllare se il caricamento di una risorsa ha successo quando si passano valori nulli
     */
    @Test
    @Order(3)
    public void testHandleFileUploadWithNullValues() {
        // Preparazione
        String description = null; // Descrizione null
        String fileName = null; // Nome file null
        List<Integer> fileData = null; // Dati file null
        FileUploadRequest request = new FileUploadRequest();
        request.setFileName(fileName);
        request.setFileType(MediaType.TEXT_PLAIN);
        request.setFileData(fileData);

        // Esecuzione
        ResponseEntity<String> response = resourceService.handleFileUpload(request, aproject.getId(), description);

        // Verifica
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusCodeValue());
        assertEquals("Errore nel caricamento del file: dati mancanti", response.getBody());
    }
    /**
     * Test per verificare il recupero di una risorsa specifica da un progetto
     */
    @Test
    @Order(4)
    public void testGetSingleResourceByProjectName() {
        // Preparazione
        String projectId = aproject.getId();
        String resourceId = resourceS.getId();

        // Esecuzione
        Response response = resourceService.getSingleResourceByProjectName(projectId, resourceId);

        // Verifica
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ResourceS resource = (ResourceS) response.getEntity();
        assertEquals(resourceS.getId(), resource.getId());
        assertEquals(resourceS.getName(), resource.getName());
        assertEquals(resourceS.getBody(), resource.getBody());
        // Altre asserzioni per verificare i campi desiderati della risorsa
    }

    /**
     * Test per verificare il recupero della lista delle risorse di un progetto
     */
    @Test
    @Order(5)
    public void testGetResourceByProjectName() {
        // Preparazione
        String projectId = aproject.getId();

        // Esecuzione
        Response response = resourceService.getResourceByProjectName(projectId);

        // Verifica
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<ResourceS> resourceList = (List<ResourceS>) response.getEntity();
        // Altre asserzioni per verificare la lista delle risorse
    }

    @Test
    @Order(6)
    public void deleteResourceTest(){
        Response response= resourceService.getResourceByProjectName(aproject.getId());
        Assertions.assertEquals(200,response.getStatus());
        List<ResourceS> resourceList= (List<ResourceS>) response.getEntity();
        resourceS=resourceList.get(0);
        Assertions.assertEquals(resourceS.getName(),"test.txt");
         // Verifica
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ResourceS resource = (ResourceS) ((List<?>) response.getEntity()).get(0);
        ResponseEntity<String> responseEntity = resourceService.deleteResourceWithId(aproject.getId(), resourceS.getId(),resourceS.getName());
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    }
}




