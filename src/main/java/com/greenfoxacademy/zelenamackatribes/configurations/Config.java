package com.greenfoxacademy.zelenamackatribes.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.zelenamackatribes.chat.mappings.chatMessageToResponseDTO.ChatMessageToResponseDtoPropertyMap;
import com.greenfoxacademy.zelenamackatribes.kingdoms.mappings.leaderboardToResponseDTO.LeaderboardToResponseDTOPropertyMap;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableScheduling
public class Config {

  @Bean
  public BCryptPasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addMappings(new ChatMessageToResponseDtoPropertyMap());
    modelMapper.addMappings(new LeaderboardToResponseDTOPropertyMap());
    return modelMapper;
  }
}
