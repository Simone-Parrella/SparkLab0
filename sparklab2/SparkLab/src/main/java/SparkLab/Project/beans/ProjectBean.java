package SparkLab.Project.beans;



import DatabaseInterface.ResourceS;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;


public class ProjectBean implements Serializable {
    private String ownerId;
    private String name;
    private String description;
    private List<String> projectAreas;
    private List<ResourceS> resources;
    private int follower;

    private List<String> request;
    public ProjectBean(){}
    public ProjectBean( String ownerId, String name, String description) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = new ArrayList<>();
        this.resources=new ArrayList<>();
        this.follower=0;
        this.request = new ArrayList<>();
    }

    public ProjectBean(  String name, String description) {
        this.name = name;
        this.description = description;
        this.resources=new ArrayList<>();
        this.projectAreas=new ArrayList<>();
        this.follower=0;
        this.request = new ArrayList<>();
    }

    public ProjectBean(String ownerId, String name, String description,List<String> projectArea,int follower) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = projectArea;
        this.follower=follower;
        this.resources = new ArrayList<>();
        this.request = new ArrayList<>();
    }

    public ProjectBean( String ownerId, String name, String description,List<String> projectAreas,int follower, List<ResourceS> resources, List<String> request) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.projectAreas = projectAreas;
        this.follower=follower;
        this.resources = resources;
        this.request = request;
    }



    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getProjectAreas() {
        return projectAreas;
    }

    public void setProjectAreas(List<String> projectAreas) {
        this.projectAreas = projectAreas;
    }

    public List<ResourceS> getResources() {
        return resources;
    }

    public void setResources(List<ResourceS> resources) {
        this.resources = resources;
    }

    public int getFollower() {return follower;}

    public void addFollower() {
        this.follower++;
    }
    public void setFollower(int follower) {
        this.follower=follower;
    }

    public List<String> getRequest(){
        return this.request;
    }
    public void addRequest(String request) {
        this.request.add(request);
    }
}



