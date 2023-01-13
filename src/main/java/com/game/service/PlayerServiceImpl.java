package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public List<Player> getPlayersList(Map<String, String> allParam) {
        PlayerOrder order = PlayerOrder.ID;
        int pageNumber = 0;
        int pageSize = 3;

        if (allParam.containsKey("order")) order = PlayerOrder.valueOf(allParam.get("order"));
        if (allParam.containsKey("pageNumber")) pageNumber = Integer.parseInt(allParam.get("pageNumber"));
        if (allParam.containsKey("pageSize")) pageSize = Integer.parseInt(allParam.get("pageSize"));

        Pageable page = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, order.getFieldName());

        Specification<Player> specification = getFilterByParameters(allParam);

        return playerRepository.findAll(specification, page).getContent();
    }

    @Override
    public Integer getPlayersCount(Map<String, String> allParam) {
        if (allParam == null) return (int) playerRepository.count();
        return (int) playerRepository.count(getFilterByParameters(allParam));
    }

    @Override
    public Player createPlayer(Player player) {
        Player createPlayer = validateAndCreate(player);
        return playerRepository.save(createPlayer);
    }

    @Override
    public Player getPlayer(String id) {
        idValidation(id);
        try {
            long parsedId = Long.parseLong(id);
            Optional<Player> player = playerRepository.findById(parsedId);
            if (!player.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return player.get();
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public Player updatePlayer(Player player, String id) {
        idValidation(id);
        try {
            long parsedId = Long.parseLong(id);
            Optional<Player> oldPlayer = playerRepository.findById(parsedId);
            if (!oldPlayer.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            Player updatedPlayer = oldPlayer.get();

            if (player.getName() != null) updatedPlayer.setName(player.getName());
            if (player.getTitle() != null) updatedPlayer.setTitle(player.getTitle());
            if (player.getRace() != null) updatedPlayer.setRace(player.getRace());
            if (player.getProfession() != null) updatedPlayer.setProfession(player.getProfession());
            if (player.getBirthday() != null) updatedPlayer.setBirthday(player.getBirthday());
            if (player.getBanned() != null) updatedPlayer.setBanned(player.getBanned());
            if (player.getExperience() != null) {
                updatedPlayer.setExperience(player.getExperience());
                updatedPlayer.setLevel(updatedPlayer.calculateCurrenLevel());
                updatedPlayer.setUntilNextLevel(updatedPlayer.calculateExperienceToNextLevel());
            }

            return playerRepository.save(updatedPlayer);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deletePlayer(String id) {
        idValidation(id);
        try {
            long parsedId = Long.parseLong(id);
            Optional<Player> player = playerRepository.findById(parsedId);
            if (!player.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            playerRepository.delete(player.get());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void idValidation(String id){
        try {
            if (id.contains(".") || id.contains("-") || Long.parseLong(id) == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private Player validateAndCreate(Player request){
        Map<String, String> checkMap = new HashMap<>();
        checkMap.put("name", request.getName());
        checkMap.put("title", request.getTitle());
        checkMap.put("race", request.getRace() == null ? null : request.getRace().name());
        checkMap.put("profession", request.getProfession() == null ? null : request.getProfession().name());
        checkMap.put("birthday", request.getBirthday() == null ? null : String.valueOf(request.getBirthday().getTime()));
        checkMap.put("banned", request.getBanned() == null ? "false" : request.getBanned().toString());
        checkMap.put("experience", String.valueOf(request.getExperience()));

        if (checkMap.containsValue(null)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Player player = new Player();
        player.setName(checkMap.get("name"));
        player.setTitle(checkMap.get("title"));
        player.setRace(Race.valueOf(checkMap.get("race")));
        player.setProfession(Profession.valueOf(checkMap.get("profession")));

        long date = Long.parseLong(checkMap.get("birthday"));
        if (date < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        player.setBirthday(new Date(date));
        player.setBanned(Boolean.valueOf(checkMap.get("banned")));
        player.setExperience(Integer.parseInt(checkMap.get("experience")));

        player.setLevel(player.calculateCurrenLevel());
        player.setUntilNextLevel(player.calculateExperienceToNextLevel());

        return player;
    }

    private Specification<Player> getFilterByParameters(Map<String, String> allParam){
        String name = allParam.get("name");
        String title = allParam.get("title");
        Race race = allParam.get("race") == null ? null : Race.valueOf(allParam.get("race"));
        Profession profession = allParam.get("profession") == null ? null : Profession.valueOf(allParam.get("profession"));
        Date after = allParam.get("after") == null ? null : new Date(Long.parseLong(allParam.get("after")));
        Date before = allParam.get("before") == null ? null : new Date(Long.parseLong(allParam.get("before")));
        Boolean isBanned = allParam.get("banned") == null ? null : Boolean.valueOf(allParam.get("banned"));
        Integer minExperience = allParam.get("minExperience") == null ? null : Integer.parseInt(allParam.get("minExperience"));
        Integer maxExperience = allParam.get("maxExperience") == null ? null : Integer.parseInt(allParam.get("maxExperience"));
        Integer minLevel = allParam.get("minLevel") == null ? null : Integer.parseInt(allParam.get("minLevel"));
        Integer maxLevel = allParam.get("maxLevel") == null ? null : Integer.parseInt(allParam.get("maxLevel"));

        Specification<Player> specification = Specification
                .where(name == null ? null : QuerySpecification.findByName(name))
                .and(title == null ? null : QuerySpecification.findByTitle(title))
                .and(race == null ? null : QuerySpecification.findByRace(race))
                .and(profession == null ? null : QuerySpecification.findByProfession(profession))
                .and(after == null && before == null ? null : QuerySpecification.findByBirthday(after, before))
                .and(isBanned == null ? null : QuerySpecification.isBanned(isBanned))
                .and((minExperience == null && maxExperience == null) ? null : QuerySpecification.findByExperience(minExperience, maxExperience))
                .and(minLevel == null && maxLevel == null ? null : QuerySpecification.findByLevel(minLevel, maxLevel));

        return specification;
    }
}
