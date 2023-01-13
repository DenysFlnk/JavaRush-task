package com.game.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String title;

    @Enumerated(EnumType.STRING)
    private Race race;

    @Enumerated(EnumType.STRING)
    private Profession profession;

    private Date birthday;

    private Boolean banned;

    private Integer experience;

    private Integer level;

    private Integer untilNextLevel;

    public Player() {
    }

    public Player(Long id, String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience, Integer level, Integer untilNextLevel) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.birthday = birthday;
        this.banned = banned;
        this.experience = experience;
        this.level = level;
        this.untilNextLevel = untilNextLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.equals("") || name.length() > 12) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title.length() > 30) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        Calendar after = new GregorianCalendar();
        after.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        Calendar before = new GregorianCalendar();
        before.set(3000, Calendar.DECEMBER, 31, 23,59,59);

        boolean isValid = birthday.after(after.getTime()) && birthday.before(before.getTime());
        if (!isValid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        if (experience < 0 || experience > 10_000_000) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Integer calculateCurrenLevel(){
        return ((int) Math.sqrt(2500 + (200 * getExperience())) - 50) / 100;
    }

    public Integer calculateExperienceToNextLevel(){
        Integer lvl = getLevel();

        return 50 * (lvl + 1) * (lvl + 2) - getExperience();
    }
}
