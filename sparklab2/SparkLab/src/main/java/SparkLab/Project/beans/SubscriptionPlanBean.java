package SparkLab.Project.beans;

import DatabaseInterface.Project;


import java.io.Serializable;

public class SubscriptionPlanBean implements Serializable {
    private String type;
    private double price;
    private String typeDescriptionSub;
    private String advantages;
    private String projectId;
    private Project project;
    //private List<User> users;



    public SubscriptionPlanBean(String type, double price, String typeDescriptionSub, String advantages, String projectId) {
        this.type = type;
        this.price = price;
        this.typeDescriptionSub = typeDescriptionSub;
        this.advantages = advantages;
        this.projectId = projectId;

    }


    public Project getProject() {
        return project;
    }


    public void setProject(Project project) {
        this.project = project;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type=type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTypeDescriptionSub(String typeDescriptionSub) {
        this.typeDescriptionSub=typeDescriptionSub;
    }
     public void setAdvantages(String advantages) {
        this.advantages=advantages;
     }

    public double getPrice() {
        return price;
    }

    public String getTypeDescriptionSub() {
        return typeDescriptionSub;
    }

    public String getAdvantages() {
        return advantages;
    }

    public String getProjectId() {
        return projectId;
    }




}
