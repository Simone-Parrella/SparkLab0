package SparkLab.Project.Security;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

@Service
public class MongoSecurityConfig implements UserDetailsService{

	// PARAMETRI DI CONNESSIONE 
    private static final String db = "SparkSecurity";
    private static final String host = "172.31.6.1";
    private static final String port = "27017";
    private final MongoClientURI connectionString;
    

    private final String COLLECTION_USERS = "user";
    private final String ELEMENT_USERNAME = "username";
    private final String ELEMENT_PASSWORD = "password";
    private final String ELEMENT_ROLE = "roles";

    //PARAMETRI DI CONNESSIONE INIZIALIZZATI DAL COSTRUTTORE
    public MongoSecurityConfig() {
        connectionString = new MongoClientURI("mongodb://" + host + ":" + port);
    }
    //Ritorna l'user data la stringa username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoCollection<Document> userCollection = mongoClient.getDatabase(db).getCollection(COLLECTION_USERS);
        Document userDoc = userCollection.find(eq(ELEMENT_USERNAME, username)).first();
        String userName = userDoc.getString(ELEMENT_USERNAME);
        String password = userDoc.getString(ELEMENT_PASSWORD);
        List<String> list = userDoc.getList(ELEMENT_ROLE, String.class);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (String authority : list) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        mongoClient.close();

        User user = new User(userName, password, grantedAuthorities);
        return user;
    }
}