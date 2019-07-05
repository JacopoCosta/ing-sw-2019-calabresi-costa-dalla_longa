# Prova Finale Ingegneria del Software 2019
## Gruppo AM23

- ###   10566951    Mattia Calabresi ([@mattiacalabresi](https://github.com/mattiacalabresi))<br>mattia.calabresi@mail.polimi.it
- ###   10534492    Jacopo Costa ([@JacopoCosta](https://github.com/JacopoCosta))<br>jacopo.costa@mail.polimi.it
- ###   10535602    Stefano Dalla Longa ([@stevedl97](https://github.com/stevedl97))<br>stefano5.dalla@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| RMI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Domination or Towers modes | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

## Launch parameters:
- ### Server
  - Run on terminal: ```java -jar /Deliverables/Adrenaline/Server/server/Server.jar <IP ADDRESS> <PORT>```.
    - ```<IP ADDRESS>``` the IP address of the machine on which the server will be run, e.g. ```123.45.67.89```. This field is required.
    - `<PORT>` the port to which the server will be bound, e.g. ```12345```. This field is required.
    
  - Run with script (Windows only): double click on ```Deliverables/Adrenaline/Server/Server.bat```.
    - To adjust server configuration parameters edit ```Deliverables/Adrenaline/Server/server/config/server.cfg```:
      - ```ip address```: the IP address of the machine on which the server will be run, e.g. ```123.45.67.89```.
      - ```port``` the port to which the server will be bound, e.g. ```12345```.
      
    - To adjust game parameters edit ```Deliverables/Adrenaline/Server/server/config/game.cfg```:
      - ```final frenzy```: whether or not the final frenzy is enabled (0 ```false```, ```true``` otherwise).
      - ```rounds to play```: number of rounds to play before the game ends (minimum ```5```, maximum ```8```).
      - ```board type```: the number of the board to play on (must be either ```1```, ```2```, ```3``` or ```4```).
      - ```turn duration```: the maximum amount in seconds a turn can last until the player is skipped (minimum ```10```, maximum ```180```).
      
    - JSON files located into ```Deliverables/Adrenaline/Server/server/json/ includes```:
      - ```boards.json```: the default location for Boards configuration.
      - ```saved.json```: the default location for saved Games.
      - ```weapons.json```: the default location for Weapons configuration.
      
- ### Client
    - Run on terminal: ```java -jar /Deliverables/Adrenaline/Client/client/Client.jar <IP ADDRESS> <PORT> [-conn (s|r)] [-int (c|g)]```
      - ```<IP ADDRESS>``` the IP address of the server to connect to, e.g. ```123.45.67.89```. This field is required.
      - ```<PORT>``` the port of the server to connect to, e.g. ```12345```. This field is required.
      - ```[-int (c|g)]``` the type of graphical interface: ```c``` for CLI, ```g``` for GUI. The flag ```-int``` and its value can be omitted and default value ```c``` will be used.
      - ```[-conn (s|r)]``` the type of connection: ```s``` for Socket, ```r``` for RMI. The flag ```-conn``` and its value can be omitted and default value ```s``` will be used.
    
    - Run with script (Windows only): double click on ```Deliverables/Adrenaline/Client/Client.bat```.
    
      - To adjust the client configuration parameters edit ```Deliverables/Adrenaline/Client/client/client.cfg```.
        
        - ```ip address``` the IP address of the server to connect to, e.g. ```123.45.67.89```.
        - ```port```: the port of the server to connect to, e.g. ```12345```.
        - ```interface```: the type of graphical interface: ```c``` for CLI, ```g``` for GUI.
        - ```connection```: the type of connection: ```s``` for Socket, ```r``` for RMI.
<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->
