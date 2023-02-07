package com.codenomads.springrestapidemo.model;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Size(min = 3, max = 30)
    private String name;
    @Temporal(TemporalType.DATE)
    private Date birthDate;

}
