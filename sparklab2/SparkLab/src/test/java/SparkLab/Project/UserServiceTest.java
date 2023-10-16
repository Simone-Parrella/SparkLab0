package SparkLab.Project;


import DatabaseInterface.User;

import SparkLab.Project.beans.UserBean;
import WebAppExceptions.Exception404;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {

    private static final UserService userService = new UserService();
    private static SimpleDateFormat  formatter = new SimpleDateFormat("yyyy-MM-dd");

    private static User user =null;
    private static UserBean userBean=new UserBean();

    @BeforeAll
    public static void setup() throws ParseException {
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

    }


    @Test
    public void testAddSubscription(){
        Response response = userService.addSubscription(user.getId());
        Assertions.assertEquals(200,response.getStatus());
    }


    /**
     * Invalid date test for a user
     */
    @Test
    public void testCreateUserWithInvalidDate() throws ParseException {
        // Preparazione dei dati di input
        UserBean userBean = new UserBean();
        userBean.setName("Alice");
        userBean.setSurname("Johnson");
        userBean.setNickname("alicejohnson");
        userBean.setEmail("alicejohnson@example.com");
        userBean.setPassword("password");
        userBean.setBirthday(formatter.parse("3000-30-21")); // invalid data :)

        // Esecuzione del metodo da testare
        Response response = userService.createUser(userBean);

        // Verifica del risultato
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Invalid date", response.getEntity());
    }

    @Test
    public void testCreateUserWithExistingEmail() throws ParseException {
        // Preparazione dei dati di input
        UserBean userBean = new UserBean();
        userBean.setName("Jane");
        userBean.setSurname("Smith");
        userBean.setNickname("janesmith");
        userBean.setEmail("johndoe@example.com"); // email is existent
        userBean.setPassword("password");
        userBean.setBirthday(formatter.parse("1990-01-01"));

        // Esecuzione del metodo da testare
        Response response = userService.createUser(userBean);

        // Verifica del risultato
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("nickname or email already in use", response.getEntity());
    }

    /**
     * test which return user is invalid
     */
    @Test
    public void testGetUserBySession_NonExistingUser() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("non_existing_user", "password"));

        Exception exception = Assertions.assertThrows(Exception404.class, userService::getUserBySessionWithProjects);

        // Verifica il messaggio dell'eccezione
        Assertions.assertEquals("HTTP 404 Not Found", exception.getMessage());
    }

    /**
     * Test in which user has null nickname
     */
    @Test
    public void testGetUserBySession_NullUsername() {
        // Simula il contesto di autenticazione con nome utente null
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, "password"));
        Assertions.assertThrows(Exception.class,()->userService.getUserBySessionWithProjects());
    }

    @Test
    public void testGetUser_ValidId() {


        // Esecuzione del metodo da testare
        Response response = userService.getUser(user.getId());

        // Verifica del risultato
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Recupera l'oggetto User dal corpo della risposta
        User aUser = (User) response.getEntity();

        // Verifica che l'utente sia stato trovato correttamente
        Assertions.assertNotNull(aUser);
        assertEquals(user.getId(), aUser.getId());
    }

    @Test
    public void testGetUser_NullId() {

        // Verifica che il metodo lanci un'eccezione Exception404
        Exception exception = Assertions.assertThrows(Exception404.class, () -> {
            userService.getUser(null);
        });

        // Verifica il messaggio dell'eccezione
        assertEquals("HTTP 404 Not Found", exception.getMessage());
    }


    @AfterAll
    public static void TierDown(){
        userService.deleteUser(user.getId());
    }


}