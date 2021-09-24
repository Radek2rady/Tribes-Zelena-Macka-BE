package com.greenfoxacademy.zelenamackatribes.utils.services;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelMapperServiceImpl implements ModelMapperService {

  private ModelMapper modelMapper;

  @Autowired
  public ModelMapperServiceImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public <SourceT, DestinationT> DestinationT map(SourceT source, Class<DestinationT> destClass) {
    return modelMapper.map(source, destClass);
  }

  @Override
  public <SourceT, DestinationT> List<DestinationT> mapAll(List<SourceT> sourceList,
      Class<DestinationT> destClass) {
    return sourceList.stream()
        .map(item -> map(item, destClass))
        .collect(Collectors.toList());
  }
}
