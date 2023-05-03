package io.project.tgregisterbot.model.contacts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Addresses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<PhoneNumbers> phones = new ArrayList<>();
    public void addPhones(PhoneNumbers phoneNumbers){
        if(!phones.contains(phoneNumbers)){
            phones.add(phoneNumbers);
            phoneNumbers.setAddress(this);
        }
    }
}