import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Clients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    private String eMail;
    private Integer phoneNumber;
    @OneToMany(mappedBy = "clients", cascade = CascadeType.ALL)
    private List<Orders> orders = new ArrayList<>();



    public Clients(String name, Integer age, String eMail, Integer phoneNumber) {
        this.name = name;
        this.age = age;
        this.eMail = eMail;
        this.phoneNumber = phoneNumber;
    }

    public Clients() {
    }
    public void addOrders(Orders order) {
        if(! orders.contains(order)) {
            orders.add(order);
            order.setClients(this);
        }
    }


    public void clearOrder(){
        orders.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }


    @Override
    public String toString() {
        return "Clients{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", eMail='" + eMail + '\'' +
                ", phoneNumber=" + phoneNumber +
                '}';
    }
}
