package SparkLab.Project.beans;


import DatabaseInterface.User;

import java.io.Serializable;

public class CommentBean implements Serializable {
    private String body;
    private String userId;
    private User user;
    public CommentBean(String body, String userId, String resourceId) {
        this.body = body;
        this.userId = userId;
        this.user = null;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setUser(User u) {
        this.user= user;
    }

    public User getUser() {
        return this.user;
    }

}
