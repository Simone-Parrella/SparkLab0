package DatabaseInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceS {
    private String id;
    private String name;
    private String resourceType;
    private String body;
    private String description;
    private int likes;
    private int dislikes;
    private String userId;
    private User user;

    private List<Comment> comment;


    /*
    Costructor with number of like and dislike set to 0
     */
    public ResourceS(String id, String name, String resourceType, String body, String description, String userId) {
        this.id = id;
        this.name = name;
        this.resourceType = resourceType;
        this.body = body;
        this.description = description;
        this.likes=0;
        this.dislikes=0;
        this.userId=userId;
        this.comment = new ArrayList<>();
    }

    public ResourceS(String id, String name, String resourceType, String body, String description,  String userId, int likes, int dislikes, List<Comment> comment) {
        this.id = id;
        this.name = name;
        this.resourceType = resourceType;
        this.body = body;
        this.description = description;
        this.userId = userId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comment=comment;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getResourceType() {
        return resourceType;
    }

    public String getBody() {
        return body;
    }

    public String getDescription() {
        return description;
    }

    
    public String getUserId() {
    	return userId;
    }

    public void addUser(User user) {
    	this.user = user;
    }

    
    public int getLikes() {
        return likes;
    }

    public int getDisikes() {
        return dislikes;
    }

    public void incrementLikes() {
        likes++;
    }

    public void incrementDislikes() {
        dislikes++;
    }


    public void addComment(Comment comment) {
        this.comment.add(comment);
    }

    public List<Comment> getComment() {
        return this.comment;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceS resource = (ResourceS) o;
        return likes == resource.likes &&
                dislikes == resource.dislikes &&
                Objects.equals(id, resource.id) &&
                Objects.equals(name, resource.name) &&
                Objects.equals(resourceType, resource.resourceType) &&
                Objects.equals(body, resource.body) &&
                Objects.equals(description, resource.description);

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", body='" + body + '\'' +
                ", description='" + description + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }
    
    public User getUser() {
    	return user;
    }
}

