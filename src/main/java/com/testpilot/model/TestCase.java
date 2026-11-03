package com.testpilot.model;

import java.sql.Timestamp;

public class TestCase {
    private int id;
    private int requirementId;
    private String title;
    private String steps;
    private String expectedResult;
    private String source;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRequirementId() { return requirementId; }
    public void setRequirementId(int requirementId) { this.requirementId = requirementId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
