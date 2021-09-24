#  ModelMapper extended functionality

*Basic use cases of mapping objects*

## ModelMapper

- ### 1. Entity and DTO have fields with exact names and types (best case scenario)
  UserEntity
  ```
  class UserEntity {
    private String userName;
    private Date registrationDate;
    private String password;
  }
  ```
  UserDTO
  ```
  class UserDTO {
    private String userName;
    private Date registrationDate;
  }
  ```
  mapping :
  ```
  ...
  ModelMapper modelMapper = new ModelMapper();
  UserDTO userDTO = modelMapper.map(userEntityObject, UserDTO.class);
  ```

- ### 2. Entity and DTO have fields with matching types (and no data processing is required) but some fields have different names
  UserEntity
  ```
  class UserEntity {
    private String userName;
    ...
  }
  ```
  UserDTO
  ```
  class UserDTO {
    private String name;
    ...
  }
  ```
  In that case we need **PropertyMap** that will tell the mapper which field map to which.  
  What you can do inside PropertyMap is very restricted as it uses Embedded Domain Specific Language (EDSL) to define how the values should be    mapped from source to destination. Therefore you can't do many things, even using simple loop or branching may cause the error.  
  We can define it as a class:
  ```
  public class MyPropertyMap extends PropertyMap<UserEntity, UserDTO> {
    @Override
    protected void configure() {
      map().setName(source.getUserName());
      // or map(source.getUserName(), destination.getName())
    }
  }
  ```
  one change here, before mapping, we should tell ModelMapper to use our PropertyMap :
  ```
  ...
  ModelMapper modelMapper = new ModelMapper();
  modelMapper.addMappings(new MyPropertyMap());
  UserDTO userDTO = modelMapper.map(userEntityObject, UserDTO.class);
  ```
  ### OR
  You don't have to create ``MyPropertyMap()`` class and can use *lambda expressions* for that:
  ```
  ...
  ModelMapper modelMapper = new ModelMapper();
  modelMapper
    .createTypeMap(UserEntity.class, UserDTO.class)
    .addMappings(mapper -> {
      mapper.map(source -> source.getUserName(), UserDTO::setName);
      ...
    });
  UserDTO userDTO = modelMapper.map(userEntityObject, UserDTO.class);
  ```

- ### 3. Entity and DTO have fields with types that do not match or data processing is required (and field names can be different too)
  UserEntity
  ```
  class UserEntity {
    ...
    private Date registrationDate;
    ...
  }
  ```
  UserDTO
  ```
  class UserDTO {
    ...
    private String createdAt;
  }
  ```
  We need converter for type conversions or data manipulation:
  ```
  public class MyConverter implements Converter<Date, String> {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
    
    @Override
    public String convert(MappingContext<Date, String> context) {
      return dateFormat.format(context.getSource);
    }
  }
  ```
  Then in PropertyMap we use that converter for given pair of fields:
  ```
  public class MyPropertyMap extends PropertyMap<UserEntity, UserDTO> {
    private final MyConverter myConverter = new MyConverter();
  
    @Override
    protected void configure() {
      using(myConverter).map(source.getRegistrationDate(), destination.getCreatedAt())
    }
  }
  ```
  Again, tell ModelMapper to use our PropertyMap :
  ```
  ...
  ModelMapper modelMapper = new ModelMapper();
  modelMapper.addMappings(new MyPropertyMap());
  UserDTO userDTO = modelMapper.map(userEntityObject, UserDTO.class);
  ```
  ### OR
  lambda equivalent again, You do not need both ``MyConverter()`` and ``MyPropertyMap()`` classes
  if You prefer this code style
  ```
  ...
  ModelMapper modelMapper = new ModelMapper();
  modelMapper
  .createTypeMap(UserEntity.class, UserDTO.class)
  .addMappings(mapper -> {
    mapper
      .using((Converter<LocalDateTime, String>) context -> {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(context.getSource());
      })
      .map(UserEntity::getRegistrationDate, UserDTO::setCreatedAt);
      // ^ method references, same as: .map(item -> item.getRegistrationDate(), ...);
    ...
  });
  UserDTO userDTO = modelMapper.map(userEntityObject, UserDTO.class);
  ```
  
  ### OR
  another possibility is to do all the work in converter - You are using converter for whole class,
  not just for converting one field type. Just be aware that using this way You need to assign even 
  unmodified fields, otherwise nulls are going to be there.
  ```
  ModelMapper modelMapper = new ModelMapper();
  modelMapper
  .createTypeMap(UserEntity.class, UserDTO.class)
  .setConverter(context -> {
    ChatMessageResponseDTO destination = new ChatMessageResponseDTO();
    destination.setCreatedAt(
      DateTimeFormatter
        .ofPattern("yyyy-MM-dd hh:mm:ss")
        .format(context.getSource().getRegistrationDate()));
    ...
    return destination;
  })
  UserDTO userDTO = modelMapper.map(userEntityObject, UserDTO.class);
  ```


