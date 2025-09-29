package com.quanvm.applyin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProfileUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

}
