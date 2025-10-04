package it.uniba.secapp.model;

/**
 * Entità User per Sprint 1.
 * Nota: passwordPlain è solo per MVP.
 * In Sprint 3 lo sostituiremo con passwordHash + salt.
 */
public class User {
    private int id;
    private String email;
    private String passwordPlain; // SOLO Sprint 1

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordPlain() {
        return passwordPlain;
    }
    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
    }
}
