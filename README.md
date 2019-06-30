# Prova Finale Ingegneria del Software 2019
## Gruppo AM23

- ###   10566951    Mattia Calabresi ([@mattiacalabresi](https://github.com/mattiacalabresi))<br>mattia.calabresi@mail.polimi.it
- ###   10534492    Jacopo Costa ([@JacopoCosta](https://github.com/JacopoCosta))<br>jacopo.costa@mail.polimi.it
- ###   10535602    Stefano Dalla Longa ([@stevedl97](https://github.com/stevedl97))<br>stefano5.dalla@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| RMI | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| GUI | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| CLI | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| Multiple games | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| Persistence | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| Domination or Towers modes | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |


<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

<br>
## Launch parameters:
- ### Server ```<IP> <port>```
    - ```<IP>``` the IP address of the machine on which the server will be run, e.g. ```123.45.67.89```. This field is required.
    - ```<port>``` the port to which the server will be bound, e.g. ```12345```. This field is required.
- ### Client ```<IP> <port> -int <c|g> -conn <s|r>```
    - ```<IP>``` the IP address of the server to connect to, e.g. ```123.45.67.89```. This field is required.
    - ```<port>``` the port of the server to connect to, e.g. ```12345```. This field is required.
    - ```<c|g>``` select the type of interface: ```c``` for CLI, ```g``` for GUI. The flag ```-int``` and its value can be omitted and default value ```g``` will be used.
    - ```<s|r>``` select the type of connection: ```s``` for Socket, ```r``` for RMI. The flag ```-conn``` and its value can be omitted and default value ```s``` will be used.