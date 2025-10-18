package it.uniba.secapp.model;

import java.time.LocalDateTime;

public class Proposal {
    private Long id;
    private String title;
    private Long createdBy;
    private LocalDateTime createdAt;

    // file
    private String filePath;
    // contenuto letto dal file (solo per view)
    private String fileContent;

    public Proposal() {}
    public Proposal(String title, Long createdBy) {
        this.title = title;
        this.createdBy = createdBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileContent() { return fileContent; }
    public void setFileContent(String fileContent) { this.fileContent = fileContent; }
}
