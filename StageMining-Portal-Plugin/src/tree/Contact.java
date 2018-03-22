package demo.data.pojo;
 
public class Contact {
    private final String name;
    private final String category;
 
    public Contact(String category) {
        this.category = category;
        this.name = null;
        this.profilepic = null;
    }
 
    public Contact(String name, String profilepic) {
        this.name = name;
        this.profilepic = profilepic;
        this.category = null;
    }
 
    public String getName() {
        return name;
    }
 
    public String getCategory() {
        return category;
    }
 
    public String getProfilepic() {
        return profilepic;
    }
 
    private final String profilepic;
}