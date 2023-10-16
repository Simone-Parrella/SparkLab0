package WebAppExceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


//Pagina per gestire l'erroe 404
//E' un codice di stato standard del protocollo HTTP. Con esso viene indicato che il client è in
//grado di comunicare con il server, ma che quest'ultimo non ha trovato ciò che è stato richiesto oppure
//è stato configurato in modo tale da non poter completare la richiesta
public class Exception404 extends WebApplicationException {

    public Exception404(String message){
     super(Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_HTML).entity("<!DOCTYPE html>\n" +
             "<html lang=\"en\">\n" +
             "<head>\n" +
             "\n" +
             "    <meta charset=\"UTF-8\">\n" +
             "\n" +
             "    <title>ERROR: 404</title>\n" +
             "</head>\n" +
             "<body>\n" +
             "<div class=\"message-container\">\n" +
             "<h2>ERROR CODE: 404</h2>\n" +
             "    </div>\n" +
             "<link rel=\"stylesheet\" type=\"text/css\" href= http://"+ LocalInfo.LOCAL_IP +":"+LocalInfo.APPLICATION_PORT
             +"/error404.css \">\n" +
             message +
             "\n" +
             "</body>\n" +
             "</html>").build());




    }


}
