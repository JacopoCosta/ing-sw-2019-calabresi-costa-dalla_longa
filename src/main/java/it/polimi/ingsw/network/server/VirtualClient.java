package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageController;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class VirtualClient extends MessageController {
    private final String name;
    private ClientCommunicationInterface communicationInterface;

    public VirtualClient(String name) {
        super();
        this.name = name;
        this.communicationInterface = null;
    }

    public void setCommunicationInterface(ClientCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    public String getName() {
        return name;
    }

    public void sendMessage(Message message) throws ConnectionException {
        if (communicationInterface == null)
            throw new NullPointerException("ClientCommunicationInterface is null");

        communicationInterface.sendMessage(message);

        final List<Player> list = new ArrayList<>();
        list.add(new Player("giovanni"));

        List<Player> players2 = list.stream()
                .filter(p -> !list.stream()
                        .map(p1 -> p.equals(p1))
                        .reduce(false, (a, b) -> a || b)
                )
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (!(object instanceof VirtualClient))
            return false;
        return ((VirtualClient) object).getName().equals(name);
    }
}
