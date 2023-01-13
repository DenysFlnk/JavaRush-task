package com.game.service;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayerService {
    List<Player> getPlayersList(Map<String, String> allParam);

    Integer getPlayersCount(Map<String, String> allParam);

    Player createPlayer(Player player);

    Player getPlayer(String id);

    Player updatePlayer(Player player, String id);

    void deletePlayer(String id);

}
