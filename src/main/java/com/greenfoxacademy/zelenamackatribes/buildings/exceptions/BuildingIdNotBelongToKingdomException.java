package com.greenfoxacademy.zelenamackatribes.buildings.exceptions;

public class BuildingIdNotBelongToKingdomException extends
    Exception {

  public BuildingIdNotBelongToKingdomException(String errorMessage) {
    super(errorMessage);
  }
}
