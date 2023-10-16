package SparkLab.Project;import DatabaseInterface.MongoDatabaseAdapter;
import DatabaseInterface.ResourceS;
import DatabaseInterface.User;
import SparkLab.Project.beans.FileUploadRequest;
import SparkLab.Project.beans.ResPath;

import WebAppExceptions.Exception404;
import jakarta.activation.MimeType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Path("/ResourceAPI")
public class ResourceService {

    MongoDatabaseAdapter resourcedb = MongoDatabaseAdapter.getIstance();


    @GET
    @Path("/resourceBody/{id}/{name}")
    @Produces({"application/json", "image/*", "text/*"})
    public Response getRes(@PathParam("id") String id, @PathParam("name") String name) {
        //File file = new File("../ProjectRes/" + path);
        File file = new File("../ProjectRes/" + id+'/'+name);
        Tika tika = new Tika();
        String mimeType = null;
        try {
            mimeType = tika.detect(file);
            if (file.exists()) {
                return Response.ok(file).type(mimeType).build();
            } else return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    /**
     * this method is a RESTful service that handle the upload of a file
     * first in local in overdirectory , and then upload the resource in db
     * using MongoDbAdapter method
     * @param request the request where is put the file resource
     * @param prgId the project id that the resource is associated
     * @param description the description of the resource
     * @return the response entity of the FileUploadRequest
     */
    @POST
    @Path("/addRes/{id}/{description}")
    public ResponseEntity<String> handleFileUpload(@RequestBody FileUploadRequest request, @PathParam("id") String prgId, @PathParam("description") String description) {
        if (request == null || request.getFileName() == null || request.getFileData() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nel caricamento del file: dati mancanti");
        }
        String fileName = request.getFileName();


        List<Integer> fileData = request.getFileData();

        try {
            // Converti la lista di Integer a 8 bit in un array di byte
            byte[] fileBytes = new byte[fileData.size()];
            for (int i = 0; i < fileData.size(); i++) {
                fileBytes[i] = fileData.get(i).byteValue();
            }

            String obId = new ObjectId().toString();
            String folderPath = "../ProjectRes/" + obId;
            String filePath = folderPath + "/" + fileName;


            File folder = new File(folderPath);
            if (!folder.exists()) {
                System.out.println("fieramente");
                folder.mkdirs(); // Crea la cartella se non esiste
            }

            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileBytes);
            fos.close();

            Tika tika = new Tika();

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String body = obId+"/"+fileName;
            ResourceS resource = new ResourceS(obId,fileName,tika.detect(file),body,description, username);
            resourcedb.addRes(prgId, resource);


            return ResponseEntity.ok("File caricato con successo");
        } catch (IOException e) {
            // Gestisci l'eccezione se si verifica un errore durante la scrittura del file
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante il caricamento del file");
        }
    }

    @GET
    @Path("/resource/{idProject}/{idResource}")
    public Response getSingleResourceByProjectName(
            @PathParam("idProject") String idProject,
            @PathParam("idResource") String res) {
        List<ResourceS> resourceList = resourcedb.getResourceList(idProject);
        if (resourceList == null) {
            throw new Exception404("resource list for this project is empty");
        }
        for (ResourceS s : resourceList) {
            if (s.getId().equals(res)) {
                return Response.ok(s).build();
            }
        }
        return Response.noContent().status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/resourceList/{idProject}")
    public Response getResourceByProjectName(@PathParam("idProject") String idProject) {
        List<ResourceS> resourceList = resourcedb.getResourceList(idProject);
        if (resourceList == null) {
            throw new Exception404("resource list for this project is empty");
        } else {
            return Response.ok(resourceList).build();
        }
    }

    @DELETE
    @Path("/deleteRes/{id}/{ResId}/{fileName}")
    public ResponseEntity<String> deleteResourceWithId(@PathParam("id") String prgId,@PathParam("ResId") String obId, @PathParam("fileName") String fileName) {
        String folderPath = "../ProjectRes/" + obId;
        String filePath = folderPath + "/" + fileName;

        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                resourcedb.deleteResource(prgId, fileName);
                return ResponseEntity.ok("File eliminato con successo");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'eliminazione del file");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File non trovato");
        }
    }






}



