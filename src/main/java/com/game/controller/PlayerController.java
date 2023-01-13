package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/players")
    public List<Player> getPlayersList(@RequestParam(required = false) Map<String, String> allParam) {
        return playerService.getPlayersList(allParam);
    }

    @GetMapping("/players/count")
    public Integer getPlayersCount(@RequestParam(required = false) Map<String, String> allParam){
        return playerService.getPlayersCount(allParam);
    }

    @GetMapping("/players/{id:.+}")
    public Player getPlayer(@PathVariable String id){
        return playerService.getPlayer(id);
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player){
        return playerService.createPlayer(player);
    }

    @DeleteMapping("/players/{id:.+}")
    public void deletePlayer(@PathVariable String id){
        playerService.deletePlayer(id);
    }

    @PostMapping("/players/{id:.+}")
    public Player updatePlayer(@RequestBody Player player, @PathVariable String id){
        return playerService.updatePlayer(player, id);
    }

}
