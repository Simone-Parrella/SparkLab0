package SparkLab.Project;


import jakarta.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.HashSet;
import java.util.Set;

//Sparklab va inserito in una cartella, allo stesso livello di questa cartella deve essere presente una cartella ProjectRes in cui vengono salvate le risorse.
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@ApplicationPath("Sparklab")
public class SparkLabApplication extends ResourceConfig {
    SparkLabApplication(){
       register(UserService.class);
       register(ProjectService.class);
       register(CommentService.class);
       register(ResourceService.class);
   }


    public static void main(String[] args) {
        SpringApplication.run(SparkLabApplication.class, args);

    }

}
