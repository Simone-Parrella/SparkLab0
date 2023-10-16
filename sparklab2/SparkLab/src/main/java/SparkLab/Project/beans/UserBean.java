package SparkLab.Project.beans;

import DatabaseInterface.Project;
import DatabaseInterface.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// A differenza di User questa serve per passare gli oggetti senza id 
//Per id intendo quello del documento di mongo
public class UserBean implements Serializable {
    private String name;
    private String surname;
    private String nickname;
    private String email;
    private String password;
    private Date birthday;
    private List<String> skills;
    private List<String> purchase;
    private List<Project> projects;
    private List<String> projectsReferences;


    public UserBean(){}

    public UserBean( String name, String surname, String nickname, String email,
                String password, Date birthday) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.skills=new ArrayList<>();
        this.projects=new ArrayList<>();
        this.purchase=new ArrayList<>();
        this.projectsReferences = new ArrayList<>();
    }

    public UserBean(String id, String name, String surname, String nickname, String email,
                String password, Date birthday, List<String> skills, List<Project> projects, List<String> purchase, List<String> projectsReferences) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.skills=skills;
        this.projects= projects;
        this.purchase= purchase;
        this.projectsReferences = projectsReferences;

    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<String> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<String> purchase) {
        this.purchase = purchase;
    }



}


