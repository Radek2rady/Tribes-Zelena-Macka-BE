package com.greenfoxacademy.zelenamackatribes.resources.models;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resources")
public class Resource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private ResourceType type;
  private Integer amount;

  @ManyToOne
  @JoinColumn(name = "kingdom_id")
  private Kingdom kingdom;

  private Integer generation;
  private Long updatedAt;

  public Resource(Long id, ResourceType type, Integer amount, Kingdom kingdom) {
    this.id = id;
    this.type = type;
    this.amount = amount;
    this.kingdom = kingdom;
  }
}
