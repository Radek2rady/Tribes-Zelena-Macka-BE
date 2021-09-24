# ModelMapperService

- ## Usage
  ``@Autowired`` like any other service.
  custom mappings (``PropertyMap``s) needs to be defined in ``<app root>/configurations/Config``
  class inside ``modelMapper()`` bean.
  ```
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addMappings(new MyMapping());
    ...
    return modelMapper;
  }
  ```

- ## Methods
    - ### ``map(<entityObject>, <DestinationDTO>.class)``
      same as ordinary ``modelMapper.map(...)``, just recall for this function

    - ### ``mapAll(List<entityObject>, <DestinationDTO>.class)``
      returns ``List`` of ``DestinationDTO`` objects
      ```
      ...
      List<Entity> entities = myService.getResults();
      List<DestinationDTO> dtos = modelMapperService.mapAll(entities, DestinationDTO.class)
      ```
