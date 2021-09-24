package com.greenfoxacademy.zelenamackatribes.utils.services;

import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;

public interface TickService {

  void gameTick() throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException;
}
