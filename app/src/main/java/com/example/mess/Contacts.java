package com.example.mess;

public class Contacts {
    public String name, status, image;
    public Contacts()
    {}
        public Contacts(String name , String status, String image)
        {
            this.name=name;
            this.status=status;
            this.image=image;
        }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
