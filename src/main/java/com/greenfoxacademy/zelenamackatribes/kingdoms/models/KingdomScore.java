package com.greenfoxacademy.zelenamackatribes.kingdoms.models;

import javax.persistence.Column;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "kingdom_score")
public class KingdomScore {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "kingdom_id")
  private Kingdom kingdom;
  private String kingdomName;
  private Integer buildingsScore;
  private Integer troopsScore;
  private Integer resourcesScore;
  private Integer totalScore;

  @Column(name = "is_history")
  private boolean isHistory;

  public KingdomScore(Kingdom kingdom, Integer buildingsScore, Integer troopsScore,
      Integer resourcesScore, Integer totalScore, boolean isHistory) {
    this.kingdom = kingdom;
    this.kingdomName = kingdom.getKingdomName();
    this.buildingsScore = buildingsScore;
    this.troopsScore = troopsScore;
    this.resourcesScore = resourcesScore;
    this.totalScore = totalScore;
    this.isHistory = isHistory;
  }

  public KingdomScore(Kingdom kingdom, boolean isHistory) {
    this(kingdom, 0,0,0,0, isHistory);
  }
}

