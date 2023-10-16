package DatabaseInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class User {
    private String id;
    private String name;
    private String surname;
    private String nickname;
    private String email;
    private String password;
    private Date birthday;
    private List<String> skills;
    private List<String> purchase;
    private List<Project> projects;
    private List<Project> purchaseP;
    private List<String> projectsReferences;
    public User(String id, String name, String surname, String nickname, String email,
                String password, Date birthday) {
        this.id = id;
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
        this.purchaseP = new ArrayList<>();
    }

    public User(){}

    public User(String id, String name, String surname, String nickname, String email,
                String password, Date birthday, List<String> skills) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.skills=skills;
        this.projectsReferences= new ArrayList<>();
        this.purchase=new ArrayList<>();
        this.projects = new ArrayList<>();
        this.purchaseP = new ArrayList<>();
    }

    public User(String id, String name, String surname, String nickname, String email,
                String password, Date birthday, List<String> skills, List<String> purchase,
                List<String> projectsReferences) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.skills=skills;
        this.projects= new ArrayList<Project>();
        this.purchase = purchase;
        this.purchaseP = new ArrayList<Project>();
        if(projectsReferences==null){ this.projectsReferences= new ArrayList<String>();}
        else{this.projectsReferences = projectsReferences;}

    }

   public User(String id, String name, String surname, String nickname, String email,
                String password, Date birthday, List<String> projectsReferences, Boolean f) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.skills= null;
        this.projects= new ArrayList<Project>();
        if(projectsReferences==null){ this.projectsReferences= new ArrayList<String>();}
        else{this.projectsReferences = projectsReferences;}
        if(purchase==null){ this.purchase= new ArrayList<String>();}
        else{this.purchase = purchase;}
        this.purchaseP = new ArrayList<Project>();

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getProjectId() { return projectsReferences; }

    public List<String> getPurchase() {
        return purchase;
    }



    public List<Project> getPurchaseP() {
        return purchaseP;
    }

    public void setPurchase(List<String> purchase) {
        this.purchase = purchase;
    }

    public void setPurchaseP(List<Project> purchase) {
        this.purchaseP = purchase;
    }
    public void print() {
        System.out.println("a");
        for(String s: projectsReferences)
        System.out.println(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) &&
                Objects.equals(surname, user.surname) && Objects.equals(nickname, user.nickname) &&
                Objects.equals(email, user.email) && Objects.equals(password, user.password) &&
                Objects.equals(birthday, user.birthday) && Objects.equals(skills, user.skills) &&
                Objects.equals(purchase, user.purchase);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, nickname, email, password, birthday, skills, purchase);
    }

    public void addSkill(String skill){
        this.skills.add(skill);
    }
    public void addPurchase(String purchase){
        this.purchase.add(purchase);
    }
    public void addProject(Project aproject){
        this.projects.add(aproject);
    }

    public void addPurchaseP(Project aproject){
        this.purchaseP.add(aproject);
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", skills=" + skills +
                ", purchase=" + purchase +
                ", projects=" + projects +
                '}';
    }


    public Project findProjectByID(String idProject){
        Project progetto =null;
        for(Project aproject:projects){
            if(aproject.getId().equals(idProject)) progetto=aproject;
        }
        return progetto;
    }

    public void addProjectId(String id) {
        if(!projectsReferences.contains("id"))
        this.projectsReferences.add(id);
    }
}

