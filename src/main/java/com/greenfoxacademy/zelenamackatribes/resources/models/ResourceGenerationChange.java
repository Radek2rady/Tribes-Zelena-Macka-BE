package com.greenfoxacademy.zelenamackatribes.resources.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resource_changes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResourceGenerationChange {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private ResourceType type;
  private Integer amount;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  private Long changeAt;
}
