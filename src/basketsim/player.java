/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basketsim;

import java.util.Scanner;

/**
 *
 * @author darrenreifler
 */
class Player {

    int index, str, quick, def, pass, shoot, dribble, agg, height;
    int shots = 0, shotsMade = 0, rebounds = 0, points = 0, threes = 0, threesMade = 0;
    public String name;
    String pos, teamName;
    Zone inZone;
    Team onTeam = null;
    Player defBy;

    private String firstName, lastName;

    Player(Scanner line) { //constructor to import player from data file
        line.next();  // dump first field, p
        setFirstName(line.next().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
        setLastName(line.next().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
        name = firstName + " " + lastName;
        setTeamName(line.next());
        setPosition(line.next());
        setStrength(line.nextInt());
        setQuickness(line.nextInt());
        setDefense(line.nextInt());
        setPassing(line.nextInt());
        setShooting(line.nextInt());
        setDribble(line.nextInt());
        setAggression(line.nextInt());
        setHeight(line.nextInt());
    }

    Player(String n, String l, int h, int st, int q, int sh, int d, int p, int dr, int a) {
        firstName = n;
        lastName = l;
        name = firstName + " " + lastName;
        height = h;
        str = st;
        quick = q;
        shoot = sh;
        def = d;
        pass = p;
        dribble = dr;
        agg = a;
    }

    public void assignTeam(Team t) {
        onTeam = t;
    }

    public void assignDefender(Player p) {
        defBy = p;
    }

    public Team getTeam() {
        return onTeam;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setPosition(String position) {
        this.pos = position;
    }

    public String getPosition() {
        return pos;
    }

    public void setStrength(int strength) {
        this.str = strength;
    }

    public int getStrength() {
        return str;
    }

    public void setQuickness(int quickness) {
        this.quick = quickness;
    }

    public int getQuickness() {
        return quick;
    }

    public void setDefense(int defense) {
        this.def = defense;
    }

    public int getDefense() {
        return def;
    }

    public void setPassing(int passing) {
        this.pass = passing;
    }

    public int getPassing() {
        return pass;
    }

    public void setShooting(int shooting) {
        this.shoot = shooting;
    }

    public int getShooting() {
        return shoot;
    }

    public void setDribble(int dribble) {
        this.dribble = dribble;
    }

    public int getDribble() {
        return dribble;
    }

    public void setAggression(int aggression) {
        this.agg = aggression;
    }

    public int getAggression() {
        return agg;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void resetStats() {
        shots = 0;
        shotsMade = 0;
        rebounds = 0;
        points = 0;
        threes = 0;
        threesMade = 0;
    }

    @Override
    public String toString() {
        return name;
    } // end toString method
}

