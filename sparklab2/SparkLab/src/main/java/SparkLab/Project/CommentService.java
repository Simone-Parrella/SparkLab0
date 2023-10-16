package SparkLab.Project;
import DatabaseInterface.DatabaseAdapter;
import DatabaseInterface.MongoDatabaseAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import DatabaseInterface.Comment;
import DatabaseInterface.MongoDatabaseAdapter;
import WebAppExceptions.Exception404;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

    @Consumes("application/json")
    @Produces("application/json")
    @Path("CommentAPI")
    public class CommentService {
        private MongoDatabaseAdapter commentDB = MongoDatabaseAdapter.getIstance();

        //CREA IL COMMENTO 
        @POST
        @Path("/comment")
        @Consumes({"application/json", "text/html"})
        public Response createComment(@RequestBody String body) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(body);

                String prgId = jsonNode.get("prgId").asText();
                String resId = jsonNode.get("resId").asText();
                String commentBody = jsonNode.get("body").asText();
                Comment comment = new Comment(new ObjectId().toString(), commentBody, username);
                commentDB.addCommentToRes(prgId, resId, comment);
                return Response.ok().build();
            } catch (Exception e) {
                // Gestisci l'eccezione se si verifica un errore durante l'analisi del JSON
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        @POST
        @Path("/like")
        @Consumes({"application/json", "text/html"})
        public Response addLike(@RequestBody String body) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(body);
                String prgId = jsonNode.get("prgId").asText();
                String resId = jsonNode.get("resId").asText();

                commentDB.addLikeToRes(prgId, resId, username);

                return Response.ok().build();
            } catch (Exception e) {
                // Gestisci l'eccezione se si verifica un errore durante l'analisi del JSON
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        @POST
        @Path("/likeRem")
        @Consumes({"application/json", "text/html"})
        public Response removeLike(@RequestBody String body) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(body);

                String prgId = jsonNode.get("prgId").asText();
                String resId = jsonNode.get("resId").asText();
                commentDB.removeLikeToRes(prgId, resId, username);

                return Response.ok().build();
            } catch (Exception e) {
                // Gestisci l'eccezione se si verifica un errore durante l'analisi del JSON
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }



        @GET
        @Path("/likes/{prgId}/{resId}")
        @Consumes({"application/json", "text/html"})
        public int countLike(@PathParam("prgId") String prgId, @PathParam("resId") String resId) throws JsonProcessingException {
                return commentDB.countLike(prgId, resId);
        }


        @GET
        @Path("/likeControl/{prgId}/{resId}")
        public boolean controlla(@PathParam("prgId") String prgId, @PathParam("resId") String resId) throws JsonProcessingException {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return  commentDB.controllaLike(prgId, resId, username);

        }




        @GET
        @Path("/comment/{prgId}/{resId}")
        @Produces("application/json")
        public List<Comment> getAllComment(@PathParam("prgId") String prgId, @PathParam("resId") String resId) {
            List comment= commentDB.getCommentForRes(prgId, resId);
            if (comment.isEmpty()) {
                throw new Exception404("Unable to load this comment id");
            } else return comment;
        }
        
        @GET
        @Path("/comment/for/{filter}")
        @Produces("application/json")
        public Comment getByProjectId(@PathParam("filter") String filter) {
            return commentDB.getCommentByprojectName(filter);
        }
        
        //	Elimina il commento in base all'id 
        @DELETE
        @Path("/comment/{commentId}")
        public Response deleteComment(@PathParam("commentId") String commentId) {
            if (commentDB.deleteComment(commentId)) return Response.ok().build();

            else throw new Exception404("Comment not found");
        }

    }
