import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clientID;
    private String name;
    private Integer age;
    private String eMail;
    private Integer phoneNumber;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Clients clients;
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "order_good",joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "good_id"))
    private Set<Goods> goods = new HashSet<>();

    public Orders(Long clientID, String name, Integer age, String eMail, Integer phoneNumber) {
        this.clientID = clientID;
        this.name = name;
        this.age = age;
        this.eMail = eMail;
        this.phoneNumber = phoneNumber;

    }

    public Orders() {
    }

    public void addGoods(Goods good){
        goods.add(good);
        good.getOrders().add(this);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientID() {
        return clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Clients getClients() {
        return clients;
    }

    public void setClients(Clients clients) {
        this.clients = clients;
    }

    public Set<Goods> getGoods() {
        return goods;
    }

    public void setGoods(Set<Goods> goodsSet) {
        this.goods = goods;
    }


    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", clientID=" + clientID +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", eMail='" + eMail + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", clients=" + clients +
                '}';
    }
}
