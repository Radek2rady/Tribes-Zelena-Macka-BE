# Tribes

## Rules

### Resources (per minute?, max level?)
- `townhall` generates 10 food + 10 gold per level per minute, can store 1000 food and 1000 gold per level
- `mine` generates 10 gold per level per minute
- `farm` generates 10 food per level per minute
- `troop` eats 1 food per minute

### Troops
- You can buy troops in `barracks` for 10 gold, hp: 10*`barracks lvl` att: 1 def: 1
- Training troops cost 5 gold, the trained troop gain +1 attack or +1 defence
- Queue troops training *optional*: buying or training a troop take 1 minute, 1 barrack can upgrade/create only one per level at a time

### Buildings
- A new building costs 250 gold, upgrading one costs 100*lvl gold
- Upgrading to a certain level is allowed only if the `townhall` already reached that level (max `townhall` level = 20)
- Buildings can have rules to make new content available achieving specific level:
    - Resource generation value increased after upgrade
    - After Barracks upgrade:
        - *optional:* Training queue limit can be increased every 5 levels
        - Defensive buildings have higher attack and defend value after upgrade (incereases by 1 every level)
    - After townhall upgrade:
        - New upgrades are available for other buildings (max level of other buildings is the level of the town hall)
        - Storage limit is higher

## Game logic parameters
|        |Building time  | |Building cost      | |HP     |Effect                                                         |
|--------|-------|---------|--------|------------|-------|---------------------------------------------------------------|
|        |Level 1|Level n  |Level 1 |Level n     |Level n|Level n                                                        |
|Townhall|2:00   |n * 1:00 |200 gold|n * 200 gold|n * 200|can build level n buildings                                    |
|Farm    |1:00   |n * 1:00 |100 gold|n * 100 gold|n * 100|+(n * 5) + 5 food / minute                                     |
|Mine    |1:00   |n * 1:00 |100 gold|n * 100 gold|n * 100|+(n * 5) + 5 gold / minute                                     |
|Academy |1:30   |n * 1:00 |150 gold|n * 100 gold|n * 150|can build level n troops                                       |
|Troop   |0:30   |n * 0:30 |25 gold |n * 25 gold |n * 20 |-(n * 5) food / minute<br>+(n * 10) attack<br>+(n * 5) defense |
