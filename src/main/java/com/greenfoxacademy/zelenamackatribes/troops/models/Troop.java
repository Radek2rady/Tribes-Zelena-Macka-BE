package com.greenfoxacademy.zelenamackatribes.troops.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import javax.persistence.Entity;
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

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "troops")
public class Troop {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private int level;
  private int hp;
  private int attack;
  private int defence;
  private long startedAt;
  private long finishedAt;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "kingdom_id")
  private Kingdom kingdom;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "academy_id")
  private Building academy;

  public Troop(int level, long startedAt, long finishedAt, Kingdom kingdom, Building academy)
      throws DefaultValuesFileException {
    this.level = level;
    this.hp = level * DefaultVals.getInt("troops.troop.defaultHp");
    this.attack = level * DefaultVals.getInt("troops.troop.defaultAttack");
    this.defence = level * DefaultVals.getInt("troops.troop.defaultDefence");
    this.startedAt = startedAt;
    this.finishedAt = finishedAt;
    this.kingdom = kingdom;
    this.academy = academy;
  }

  public Troop(int level, int hp, int attack, int defence, long startedAt, long finishedAt,
      Kingdom kingdom) {
    this.level = level;
    this.hp = hp;
    this.attack = attack;
    this.defence = defence;
    this.startedAt = startedAt;
    this.finishedAt = finishedAt;
    this.kingdom = kingdom;
    this.academy = null;
  }
}
