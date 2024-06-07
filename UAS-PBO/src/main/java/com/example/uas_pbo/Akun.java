package com.example.uas_pbo;

public class Akun {
    private String Username;
    private String Password;


    public Akun(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword(String password) {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


}

