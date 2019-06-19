package it.polimi.ingsw.network.common.util.console;

interface ConsoleExecutor {
    void clear();

    /*
    * NOTE: ANSIPrint("message\n") does NOT perform the same as ANSIPrintln("message"). ANSIPrint() method is more resource
    * consuming and generally less efficient, especially when performed on the Windows platform.
    * */
    void ANSIPrint(String message);

    void ANSIPrintln(String message);
}
