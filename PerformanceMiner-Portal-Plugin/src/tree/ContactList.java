package demo.data;
 
import demo.data.pojo.Contact;
import demo.tree.dynamic_tree.ContactTreeNode;
 
public class ContactList {
    public final static String Category = "Category";
    public final static String Contact = "Contact";
     
    private ContactTreeNode root;
    public ContactList() {
        root = new ContactTreeNode(null,
            new ContactTreeNode[] {
                new ContactTreeNode(new Contact("Friend"),new ContactTreeNode[] {
                    new ContactTreeNode(new Contact("High School"), new ContactTreeNode[] {
                        new ContactTreeNode(new Contact("Fernando Terrell", "Contact.png")),
                        new ContactTreeNode(new Contact("Stanley Larson", "Contact.png"))
                    },true),
                    new ContactTreeNode(new Contact("University"), new ContactTreeNode[] {
                        new ContactTreeNode(new Contact("Camryn Breanna", "Contact.png")),
                        new ContactTreeNode(new Contact("Juliana Isabela","Contact-gu.png")),
                        new ContactTreeNode(new Contact("Holden Craig", "Contact-g.png"))
                    }),
                    new ContactTreeNode(new Contact("Emma Jones", "Contact-i.png")),
                    new ContactTreeNode(new Contact("Eric Franklin",  "Contact.png")),
                    new ContactTreeNode(new Contact("Alfred Wong", "Contact.png")),
                    new ContactTreeNode(new Contact("Miguel Soto",  "Contact.png"))
                },true),
                new ContactTreeNode(new Contact("Work"),new ContactTreeNode[] {
                    new ContactTreeNode(new Contact("Andrew Willis",  "Contact.png")),
                    new ContactTreeNode(new Contact("Russell Thomas",  "Contact-jq.png")),
                    new ContactTreeNode(new Contact("Donovan Marcus",  "Contact.png"))
                })
            },true
        );
    }
    public ContactTreeNode getRoot() {
        return root;
    }
}