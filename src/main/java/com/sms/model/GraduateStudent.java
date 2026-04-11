package com.sms.model;

public class GraduateStudent extends Student {

    private String researchTopic;

    public GraduateStudent(Long id, String name, int age,
                           String email, String department,
                           String researchTopic) {
        super(id, name, age, email, department);
        this.researchTopic = researchTopic;
    }

    @Override
    public String getRole() {
        return "Graduate Student";
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("   Research Topic: " + researchTopic);
    }

    public String getResearchTopic() { return researchTopic; }
    public void setResearchTopic(String t) { this.researchTopic = t; }

    @Override
    public String toString() {
        return "GraduateStudent{name=" + getName()
                + ", research=" + researchTopic + "}";
    }
}