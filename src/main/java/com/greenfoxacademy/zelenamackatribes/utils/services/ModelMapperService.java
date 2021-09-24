package com.greenfoxacademy.zelenamackatribes.utils.services;

import java.util.List;

public interface ModelMapperService {

  <SourceT, DestinationT> DestinationT map(SourceT source, Class<DestinationT> destClass);

  <SourceT, DestinationT> List<DestinationT> mapAll(List<SourceT> sourceList,
      Class<DestinationT> destClass);
}
