package it.uniba.secapp.model;

import java.time.Instant;

/**
 * Entità Proposal per Sprint 1.
 * Contiene riferimento all'utente e al file .txt salvato.
 */
public class Proposal {
    private int id;
    private int userId;
    private String title;
    private String bodyTextPath;
    private boolean isPublic;
    private Instant createdAt;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyTextPath() {
        return bodyTextPath;
    }
    public void setBodyTextPath(String bodyTextPath) {
        this.bodyTextPath = bodyTextPath;
    }

    public boolean isPublic() {
        return isPublic;
    }
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
