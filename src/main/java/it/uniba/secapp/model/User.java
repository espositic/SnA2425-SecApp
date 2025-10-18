package it.uniba.secapp.model;

public class User {
    private Long id;
    private String email;
    private String pwdHash;
    private String profileImgPath;

    public User() {}
    public User(String email, String pwdHash, String profileImgPath) {
        this.email = email;
        this.pwdHash = pwdHash;
        this.profileImgPath = profileImgPath;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPwdHash() { return pwdHash; }
    public void setPwdHash(String pwdHash) { this.pwdHash = pwdHash; }
    public String getProfileImgPath() { return profileImgPath; }
    public void setProfileImgPath(String profileImgPath) { this.profileImgPath = profileImgPath; }
}
