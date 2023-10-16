package DatabaseInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.File;

public class Project implements Comparable<Project> {
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private List<String> projectAreas;
    private List<ResourceS> resources;
    private int follower;

    private List<String> request;
    
    //un primo costruttore che non prende i follower nel costruttore e
    // nemmeno i projectAreas che sono solamente allocati
    public Project(String id, String ownerId, String name, String description) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = new ArrayList<>();
        this.follower=0;
        this.resources = new ArrayList<>();
        File folder = new File("basicPage/ProjectRes/"+id);
        try {
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("Cartella creata con successo: " + folder.getAbsolutePath());
            } else {
                System.out.println("La cartella esiste: " + folder.getAbsolutePath());
            }
        } catch (SecurityException e) {
            System.out.println("Impossibile creare la cartella: " + e.getMessage());
        }
        this.request=new ArrayList<>();
    }

    public Project(String id, String ownerId, String name, String description, List<String> projectAreas) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = projectAreas;
        this.follower=0;
        this.resources = new ArrayList<>();
        this.request=new ArrayList<>();
    }

    public Project(String id, String ownerId, String name, String description,List<String> projectAreas,int follower) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = projectAreas;
        this.follower=follower;
        this.resources = new ArrayList<>();
        File folder = new File("basicPage/ProjectRes/"+id);
        try {
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("Cartella creata con successo: " + folder.getAbsolutePath());
            } else {
                System.out.println("La cartella esiste gia: " + folder.getAbsolutePath());
            }
        } catch (SecurityException e) {
            System.out.println("Impossibile creare la cartella: " + e.getMessage());
        }
        this.request=new ArrayList<>();
    }

    public Project(String id, String ownerId, String name, String description,List<String> projectAreas,int follower, List<ResourceS> resources) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = projectAreas;
        this.follower=follower;
        this.resources = resources;
        File folder = new File("basicPage/ProjectRes/"+id);
        try {
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("Cartella creata con successo: " + folder.getAbsolutePath());
            } else {
                System.out.println("La cartella esiste gia: " + folder.getAbsolutePath());
            }
        } catch (SecurityException e) {
            System.out.println("Impossibile creare la cartella: " + e.getMessage());
        }
        this.request=new ArrayList<>();
    }

    public Project(String id, String ownerId, String name, String description,List<String> projectAreas,int follower, List<ResourceS> resources,List<String> request, boolean f) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = projectAreas;
        this.follower=follower;
        this.resources = resources;
        this.request=request;


    }


    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }
    
    public int getFollowers() {
        return follower;
    }

    public String getDescription() {
        return description;
    }

    public List<ResourceS> getResources() {
        return resources;
    }
    
    public List<String> getProjectAreas() {
        return projectAreas;
    }

    public void addProjectArea(String projectArea) {
        this.projectAreas.add(projectArea);
    }

    public void addResource(ResourceS resource) {
        this.resources.add(resource);
    }
    
    public void incrementFollower() {
        // Increment the number of followers
        follower++;
    }

    public void setName(String name){this.name=name;}
    public void setDescription(String description){this.description=description;}


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Project other = (Project) obj;
        return Objects.equals(id, other.id) &&
                Objects.equals(ownerId, other.ownerId) &&
                Objects.equals(name, other.name) &&
                Objects.equals(description, other.description) &&
                follower == other.follower;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, name, description, follower);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", projectAreas=" + projectAreas +
                ", follower=" + follower +
                '}';
    }

    public List<String> getRequest(){
        return this.request;
    }
    public void addRequest(String request) {
        this.request.add(request);
    }

    @Override
    public int compareTo(Project o) {
        return 0;
    }
}