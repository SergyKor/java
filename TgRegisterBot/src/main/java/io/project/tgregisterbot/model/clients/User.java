package io.project.tgregisterbot.model.clients;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long chatId;
    private String userName;
    private String firstName;
    private String lastName;


    private Timestamp registerAt;

}