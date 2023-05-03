package io.project.tgregisterbot.model.contacts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "phone_numbers")
public class PhoneNumbers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phone;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Addresses address;
}