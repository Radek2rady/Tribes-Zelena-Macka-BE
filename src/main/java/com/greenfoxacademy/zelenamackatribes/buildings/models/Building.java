package com.greenfoxacademy.zelenamackatribes.buildings.models;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeServiceImpl;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "buildings")
public abstract class Building {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private BuildingType type;

  private Integer level;
  private Integer hp;
  private Long startedAt;
  private Long finishedAt;

  @ManyToOne
  @JoinColumn(name = "kingdom_id")
  private Kingdom kingdom;

  public Building() {
    this.level = 1;
    this.startedAt = new TimeServiceImpl().getTime();
  }

}
