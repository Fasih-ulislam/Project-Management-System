package com.softManager.project_management_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team extends BaseEntity {
    private String name;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "created_by_id")
    private Member createdBy;

    @JsonIgnore
    @OneToMany(mappedBy = "team")
    private Set<Member> members;

    @JsonManagedReference
    @OneToMany(mappedBy = "team")
    private Set<Task> tasks;
}
