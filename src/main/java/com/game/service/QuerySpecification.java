package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;
import java.util.Date;

public final class QuerySpecification {

    public static Specification<Player> findByName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), contains(name));
    }

    public static Specification<Player> findByTitle(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), contains(title));
    }

    public static Specification<Player> findByRace(Race race) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("race"), race);
    }

    public static Specification<Player> findByProfession(Profession profession) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("profession"), profession);
    }


    public static Specification<Player> findByBirthday(Date after, Date before) {
        if (after == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), before);
        } else if (before == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), after);
        } else
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("birthday"), after, before);
    }

    public static Specification<Player> isBanned(boolean banned) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("banned"), banned);
    }

    public static Specification<Player> findByExperience(Integer minExperience, Integer maxExperience) {
        if (minExperience == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience);
        } else if (maxExperience == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience);
        } else
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("experience"), minExperience, maxExperience);
    }

    public static Specification<Player> findByLevel(Integer minLevel, Integer maxLevel) {
        if (minLevel == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel);
        } else if (maxLevel == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel);
        } else
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("level"), minLevel, maxLevel);
    }

    private static String contains(String param) {
        return MessageFormat.format("%{0}%", param);
    }
}
