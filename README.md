# ZelenaMacka-Tribes-BE

Zelena Macka project team's tribes backend project

## Table of content

**[About](#about)**
- [About game](#about-game)
- [Basic rules](#basic-rules)

**[Enviroment setup](#environment-setup)**  
- [Enviroment variables](#environment-variables)  
- [Recommended IDEA plugins](#recommended-IDEA-plugins)
- [Usage of ModelMapper](#usage-of-modelmapper)
- [Database migration with Flyway](#database-migration-with-flyway)
- [Values file](#values-file)
- [Naming Conventions](#naming-conventions)

**[User session & Security](#user-session--security)**  
- [Authorization](#authorization)  
- [Logging](#logging)
- [Email verification](#email-verification)

**[Endpoints](#endpoints)**  
- User  
  [POST /register](#post-register)  
  [POST /login](#post-login)  
  [POST /images/avatar](#post-imagesavatar)  
- Kingdom  
  [GET /kingdom/resources](#get-kingdomresources)  
  [GET /kingdom/{id}](#get-kingdomid)  
  [POST /kingdom/fight/{id}](#get-kingdomid)  
- Kingdom's buildings  
  [POST /kingdom/buildings](#post-kingdombuildings)  
  [GET /kingdom/buildings](#get-kingdombuildings)  
  [GET /kingdom/buildings/{id}](#get-kingdombuildingsid)  
  [PUT /kingdom/buildings/{id}](#put-kingdombuildingsid)  
  [GET /kingdom/leaderboard](#get-leaderboard)   
- Kingdom's troops  
  [POST /kingdom/troops](#post-kingdomtroops)  
  [GET /kingdom/troops](#get-kingdomtroops)  
  [GET /kingdom/troops/{id}](#get-kingdomtroopsid)  
  [PUT /kingdom/troops/{id}](#put-kingdomtroopsid)
- General chat  
  [POST /message](#post-message)  
  [GET /messages](#get-messages)

## About
*Backend part (REST API) of Tribes strategy game, written in Java using Spring framework.*

- ### About game
  Tribes is a classic medieval-style strategy game in which the players can gather resources,
  spend resources on upgrading abilities of their kingdoms and troops 
  which will help them to be stronger and defeat other players in battle.

- ### Basic rules
  Each player has one  ``kingdom``with contains maximum of one ``townhall`` type building.  
  The player can grow their ``kingdom`` by collecting ``resources`` to build 
  and level up new ``buildings``.  
  While leveling up, ``buildings`` cannot exceed the level of the ``townhall``.  
  Higher level ``buildings`` offer some advantages, for example, a leveled up ``academy``
  would produce stronger ``troops``.

## Environment setup
*Basic environment setup needs to be done for application to work.*  

- ### Environment variables
  ``SERVER_PORT`` for setting port that API should listen to. Default ``8080``  
  ``MYSQL_URL`` URL for database connection in format: ``jdbc:mysql://<domain_address>/tribes?serverTimezone=UTC``  
  ``MYSQL_USER`` your username for access to database  
  ``MYSQL_PASS`` password for your database login  
  ``FLYWAY_URL`` URL for database connection in format: ``jdbc:mysql://<domain_address>/tribes``  
  ``FLYWAY_USER`` your username for access to database (same as MySQL)  
  ``FLYWAY_PASS`` password for your database login (same as MySQL) 
  ``SECRET_KEY`` private key string encrypted using ``HS256`` algorithm  
  ``TRIBES_GAMETICK_LEN`` for storing length of game tick (passage of time) in seconds
  ``CONF_TOKEN_LINK`` link used for email address verification  
  ``EMAIL_SENDER`` email address of the project  
  ``EMAIL_USERNAME`` username for our email address  
  ``EMAIL_PASSWORD`` password for our email address  
  ``EMAIL_SMTP_SERVER`` SMTP server for our email address  
  ``EMAIL_SMTP_PORT`` SMTP port for our email address  

- ### Recommended IDEA plugins
  - #### Checkstyle-IDEA
    - Install the plugin (``Settings``>``Plugins``>``Marketplace``)
    - Go to ``Settings``>``Tools``>``Checkstyle``
    - Use ``+`` to add config from this project found at ``config/checkstyle/checkstyle.xml`` and mark it as active (this setting is saved per project)
    - From this point on, CheckStyle will verify code against these rules as you write.
    - You can also use CheckStyle window from the bottom toolbar in IDEA to scan the whole project and see all errors.

- ### Usage of ModelMapper
  *``ModelMapper`` helps You to transition between entities and DTO objects back and forth*  
  <br>
  - We use ``ModelMapper`` every time when we are creating a DTO object from a model object
  - In a controller, there should be ``@Autowired ModelMapper modelMapper``
    and passing in the constructor parameter (if constructor exists).  
    With that you can transform an entity into DTO with ``modelMapper.map(entityObject, EntityDTO.class)``.
    ModelMapper should take care of the transformation, even when the field is missing 
    or in the case when the field in DTO is a subfield of any field of your entity.
  - If you want to make more difficult transformation, you can add those in the config file 
    using this [cheatsheet](https://www.programmersought.com/article/44676392565/).
  - Or have a look at ours **[ModelMapper howto](https://github.com/green-fox-academy/ZelenaMacka-Tribes-BE/blob/ZM-50/src/main/java/com/greenfoxacademy/zelenamackatribes/readmesManualsTutorials/modelMapper.md)**
  - ``ModelMapperService`` extends functionality of ``ModelMapper`` and can do some basic tasks with
    mapping commonly used objects for You, see
    **[ModelMapperService documentation](https://github.com/green-fox-academy/ZelenaMacka-Tribes-BE/blob/ZM-50/src/main/java/com/greenfoxacademy/zelenamackatribes/readmesManualsTutorials/modelMapperService.md)**

- ### Database migration with Flyway
    *With database migrations, our database is able to evolve with the project.
    We use Flyway for the database migrations. Flyway is an open-sourced tool which
    allows us to implement automated and version-based database migrations.*
    <br><br>
    *First we created a base version of our database, which can be found in the
    ``resources/db.migration`` package in SQL format. See ``V1__init_migration.sql*
    *We can use Java or SQL Scripts to update the database. To add new version scripts:*
    - Create a new class in the ``java../db.migration`` package.
    - Class must follow a strict naming convention: 
      ```
      V + Version # + _ _ + short description
      ```
      ```
      Version # = YearYYY + MonthM + DayD + HourHour + MinMin
      ```
    - Extend Version with BaseMigration class and override the migration method with the changes
    you wish to make to the database.
    - Each update will be added to the ``flyway_schema_history`` and the database can be easily
    restored to earlier versions.
    

- ### Values file
  *the purpose of ``YAML`` file ``values.yml`` stored in ``resources`` folder
  is to hold values and constants that affects gameplay and game logic*  
  <br>
  - To retrieve values from ``values.yml`` file You use ``DefaultVals`` static class.   
    This way You do not need autowire anything and values are available even before 
    spring application lifecycle comes to initialization of services and beans.
  - Current implementation supports non-decimal number values and string values. To retireve them
    You are using corresponding ``DefaultVals.getInt(...)`` and ``DefaultVals.getString(...)``
    methods  
    <br>
    values.yml
    ```
    test:
      values:
        intValue: 100
        stringValue: Hello
    ```
    retrieve intValue:  
    ```
    DefaultVals.getInt("test.values.intValue")
    ```
    retrieve stringValue:
    ```
    DefaultVals.getString("test.values.stringValue")
    ```
  - to keep tree structure of values inside ``values.yml`` file working, You just need to follow 
    two space indentation rule for each sub-branch
  
- ### Naming Conventions
  *We use specific naming conventions when naming ``DTOs`` and Flyway ``Version Scripts``*
  *The formats should follow:*
  ```
  DTO = Entity + Action + Request/Response + DTO
  ```
  ```
  Version Script = V + Version # + _ _ + short description
  ```
  ```
  Version # = YearYYY + MonthM + DayD + HourHour + MinMin
  ```

## User session & Security

- ### Authorization
  - Application uses stateless and sessionless authentication using JWT token.
  - All endpoints except [POST /register](#post-register) and [POST /login](#post-login)
    are secured and cannot be accessed without valid JWT token prefixed with ``Bearer `` prefix inside
    (separated by one space) ``Authorization`` header.
    ``` 
    Bearer <Your_JWT_token> 
    ```
  - JWT token is obtained in response body after successfull login.
  - JWT token contains following content (claims):  
    ``userId`` ID of logged in user  
    ``username`` name of the logged in user  
    ``kingdomId`` ID of user's kingdom  
    ``kingdomName`` name of user's kingdom
  - error responses:  
    ``400`` authentication header missing  
    ``400`` jwt token missing  
    ``400`` jwt token bearer missing  
    ``401`` token signature is not valid  
    ``401`` token has expired  
    ``401`` token unsupported  
    ``400`` unknown JWT exception
    
- ### Logging
  -  In each class where you want to use the logger, create an instance of it, e.g.
     ``private static Logger logger = LogManager.getLogger(BuildingController.class);``
  *the name of the specific class must be in brackets*   
  - Logger and LogManager should be automatically imported. To be sure, check that they are from the
    ``apache.logging.log4j`` class imports:
    ```
    org.apache.logging.log4j.LogManager
    org.apache.logging.log4j.Logger
    ```
  - Then you can use the logger, using the following style:
    ```
    logger.<type of loglevel>("<log message string>")
    ```
    *Available log levels:*
       * OFF,
       * FATAL,
       * ERROR,
       * WARN,
       * INFO,
       * DEBUG,
       * TRACE,
       * ALL
  - all logs are stored in ``logs.log`` file

## Endpoints
- JSON is used for requests and responses.
- If an error occurs, the following ``JSON response body`` and its corresponding ``HTTP code``
  is returned (to the user).
  ```
  {
    "status" : "error",
    "message" : "<message describing what happened>"
  }
  ```

- ### POST /register
  *used to create new user including its kingdom*  
  <br>
  - request body:
    ```
    {
      "username" : "<string>",
      "password" : "<string>",
      "kingdomname" : "<string>",
      "email" : "<string>"
    }
    ```
  - ``201`` OK response body:
    ```
    {
      "id": <number>,
      "username": "<string>",
      "email": "<string>",
      "kingdomId": <number>,
      "avatar": "<URL string>",
      "points": 0
    }
    ```
  - error responses:  
    ``400`` required parameter is missing  
    ``400`` password shorter than 8 characters  
    ``409`` user already exists
  
- ### POST /login
  *used to log in user and obtain JWT Token*  
    <br>
  - request body:
    ```
    {
      "username": "<string>",
      "password" : "<string>",
    }
    ```
  - ``200`` OK response body:
    ```
    {
      "status": "ok",
      "token": "<JWT token string>"
    }
    ```
  - error responses:  
    ``400`` username or password missing  
    ``400`` request body malformed  
    ``401`` username or password wrong

- ### POST /images/avatar
  *uploads or updates user's avatar picture.* ``userId`` *is provided by JWT Token.*  
  <br>
  - request body:
    ```
    standart form-data multipart file
    ```
  - ``201`` OK response
  - error responses:  
    ``400`` file missing  
    ``400`` format not allowed  
    ``400`` file corrupted  
    ``413`` uploaded file too big  
    ``500`` cannot access/create store destination  
    ``500`` disk IO error  
    ``500`` cannot convert given file

- ### GET /kingdom/resources
  *used to get resources of kingdom for given user (contained in JWT token claims)*  
  <br>
  - ``200`` OK response body:
    ```
    {
      "resources": [
        {
          "type": "food",
          "amount": <number>,
          "generation": <number>,
          "updatedAt": <timestamp>
        },
        {
          "type": "gold",
          "amount": <number>,
          "generation": <number>,
          "updatedAt": <timestamp>
        }
      ]
    }
    ```
  - error responses:  
    ``404`` given kingdom not found  
    ``404`` wrong number of resources   
    ``404`` resource not found
    
- ### GET /kingdom/{id}
  *``{id}`` ID of given kingdom*  
  *get the summarized information about given kingdom (requires JWT token just for authentication)*  
  <br>
  - ``200`` OK response body:
    ```
    {
      "id": <number>,
      "name": "<string>",
      "userId": <number>,
      "buildings": [
        {
          "id": <number>,
          "type": "<string>",
          "level": <number>,
          "hp": <number>,
          "startedAt": <timestamp>,
          "finishedAt": <timestamp>
        },
        ...
      ],
      "resources": [
        {
          "type": "<string>",
          "amount": <number>,
          "generation": <number>,
          "updatedAt": <timestamp>
        },
        ...
      ],
      "troops": [
        {
          "id": <number>,
          "level": <number>,
          "hp": <number>,
          "startedAt": <timestamp>,
          "finishedAt": <timestamp>
        },
        ...
      ]
    }
    ```
  - error responses:  
    ``404`` kingdom with given id does not exist

- ### POST /kingdom/fight/{id}
  *``{id}`` ID of kingdom to attack*  
    The initiating kingdom is the one that belongs to the user who is logged in
    (contained in JWT token claims)*  
      <br>
    - ``200`` OK response body:
       ```
        {
          "result": "Congratulation! coolKingdom19 has conquered tomcatKingdom44",
          "playerStatistics": { 
            "lostBuildings": 0,  
            "lostTroops": 3,  
            "earnedGold": 100,  
            "earnedFood": 50  
            },  
            "opponentStatistics": {  
                "lostBuildings": 4,  
                "lostTroops": 3,  
                "lostGold": 100,  
                "lostFood": 50  
            }         
              "id": <number>,
              "type": "<string>",
              "level": <number>,
              "hp": <number>,
              "startedAt": <timestamp>,
              "finishedAt": <timestamp>
            }
        }
        ```
    - error responses:  
      ``400`` parameter is missing  
      ``403`` Kingdom is not able to fight with itself  
      ``403`` A kingdom without troops can't initiate a battle  
      ``403`` Kingdoms cannot battle with another out of range level kingdom  
      ``404`` Id not found    
  
- ### POST /kingdom/buildings
  *used to create new building in kingdom of given user (contained in JWT token claims)*  
  <br>
  - request body:
    ```
    {
      "type": "<string>"
    }
    ```
    ``type`` - building type, allowed types are: ``townhall`` ``farm`` ``academy`` ``mine``  
    <br>
  - ``200`` OK response body:
    ```
    {
      "id": <number>,
      "type": "<string>",
      "level": <number>,
      "hp": <number>,
      "startedAt": <timestamp>,
      "finishedAt": <timestamp>
    }
    ```
  - error responses:  
    ``400`` missing type parameter  
    ``404`` given kingdom not found  
    ``404`` resource not found/wrong number of resources  
    ``406`` invalid building type  
    ``409`` not enough resources in kingdom

- ### GET /kingdom/buildings
  *used to get list of buildings in kingdom of given user (contained in JWT token claims)*  
  <br>
  - ``200`` OK response body:
    ```
    {
      "buildings": [
        {
          "id": <number>,
          "type": "<string>",
          "level": <number>,
          "hp": <number>,
          "startedAt": <timestamp>,
          "finishedAt": <timestamp>
        },
        ...
      ]
    }
    ```
  - error responses:  
    ``404`` if the given kingdom cannot be found

- ### GET /kingdom/buildings/{id}
  *``{id}`` ID of requested building*  
  *get the details of given building for given user and kingdom (contained in JWT token claims)*  
  <br>
  - ``200`` OK response body:
    ```
    {
      "id": <number>,
      "type": "<string>",
      "level": <number>,
      "hp": <number>,
      "startedAt": <timestamp>,
      "finishedAt": <timestamp>
    }
    ```
  - error responses:  
    ``400`` parameter missing  
    ``403`` if the given building does not belong to user that sent request  
    ``404`` the building with given ID is not found

- ### PUT /kingdom/buildings/{id}
  *``{id}`` ID of requested building*  
  *upgrade the level of given building for given user and kingdom (contained in JWT token claims) if kingdom
  has enough resources.*   
  <br>
  
  - ``200`` OK response body:
    ```
    {
      "id": <number>,
      "type": "<string>",
      "level": <number>,
      "hp": <number>,
      "startedAt": <timestamp>,
      "finishedAt": <timestamp>
    }
    ```
  - error responses:  
    ``400`` parameter is missing  
    ``404`` building with given ID not found  
    ``406`` invalid level for building - level cannot be higher than ``townhall`` level   
    ``409`` not enough resources to update the building
  
- ### GET /kingdom/leaderboard 
  *Responses with leaderboard in given ``scoreType`` based on ``pageNo`` and ``PageSize``*   
  <br>
  
  - Url parameters  
    ``pageNo`` is the page number of the page to display    
    ``pageSize`` is the number od outputs on the page   
    ``scoreType`` is ``totalScore`` by default, but one may set it to ``buildingsScore``,
     ``troopsScore`` or ``resourcesScore`` to obtain different types of leaderboards    
    ``isHistory`` true for leaderboard of all time, false for current leaderboard 
  
  - ``200`` OK response body: 
    ```
    {
      "currentPageNumber": <number>,
      "totalPageNumber": <number>,
      "pageSize": <number>,
      "scoreType": "<string>",
      "leaderboard": [
        {
            "kingdomName": "<string>",
            "totalScore": <number>,
            "buildingsScore": <number>,
            "troopsScore": <number>,
            "resourcesScore": <number>
        },
        {
            "kingdomName": "<string>",
            "totalScore": <number>,
            "buildingsScore": <number>,
            "troopsScore": <number>,
            "resourcesScore": <number>
        },
        ...
      ]
    }
    ```
  - error responses:  
    ``404`` pageNo must be positive integer  
    ``404`` pageSize must be a positive integer  
    ``404`` scoreType must be a one of prescribed strings  
    ``404`` not enough kingdoms to display given pageNo  

- ### POST /kingdom/troops
  *Creates a troop in academy inside given kingdom of user (contained in JWT token claims).  
  Troop level is based on academy level.   
  After the academy levels up, only troops created post level up will start at a higher level.*  
  <br>
  - request body: 
    ```
    {
      "buildingId": <number>
    }
    ```
  - ``200`` OK response body:  
    ```
    {
      "id": <number>,
      "level": <number>,
      "hp": <number>,
      "startedAt": <timestamp>,
      "finishedAt": <timestamp>
    }
    ```
  - error responses:  
    ``403`` given building does not belong to current user  
    ``406`` building is not academy / does not exist  
    ``404`` resource not found  
    ``409`` not enough resources to create troop

- ### GET /kingdom/troops
  *Get the troops in kingdom of given user (contained in JWT token claims).*  
  <br>
  - ``200`` OK response body:
    ```
    {
      "troops": [
        {
          "id": <number>,
          "level": <number>,
          "hp": <number>,
          "startedAt": <timestamp>,
          "finishedAt": <timestamp>
        },
        ...
      ]
    }
    ```
  - error responses:  
    ``404`` given kingdom not found

- ### GET /kingdom/troops/{id}
  *``{id}`` ID of requested troop  
  get the troop detail(s) in kingdom of given user. (contained in JWT token claims)*  
  <br>
  - ``200`` OK response body:
    ```
    {
      "id": <number>,
      "level": <number>,
      "hp": <number>,
      "startedAt": <timestamp>,
      "finishedAt": <timestamp>
    }
    ```
  - error responses:  
    ``403`` given troop does not belong to user that sent request  
    ``404`` troop with given ID not found   
    ``404`` given kingdom not found

- ### PUT /kingdom/troops/{id}
  *``{id}`` ID of requested troop  
  upgrade troop level to the level of academy (academy ID given in request body) for given user
  (contained in JWT token claims)*  
  <br>
  - request body:  
    ```
    {
      "buildingId": <number>
    }
    ```
    ``buildingId`` - kingdom's academy (building) ID  
    <br>
  - ``200`` OK response body:
    ```
    {
      "id": <number>,
      "level": <number>,
      "hp": <number>,
      "startedAt": <timestamp>,
      "finishedAt": <timestamp>
    }
    ```
  - error responses:  
    ``400`` parameter is missing or troop level equal or higher than academy
    ``403`` given building (academy) or troop does not belong to user who sent request   
    ``404`` building (academy) with given ID not found
    ``409`` not enough resources to update the troop or academy full

- ### POST /message
  *Sends message to general chat*  
  <br>
  - request body:
    ```
    {
      "message": <string>
    }
    ```
    <br>
  - ``201`` OK response without body  
    <br>
  - error responses:  
    ``400`` malformed request  
    ``400`` message is missing  
    ``400`` message exceeds maximum allowed length   

- ### GET /messages
  *Get messages from general chat no older than 24 hours*  
  <br>
  - ``200`` OK response body:
    ```
    {
      messages: [
        {
          "message": <string>,
          "createdAt": <string>,
          "username": <string>
        },
        ...
      ]
    }
    ```
    
