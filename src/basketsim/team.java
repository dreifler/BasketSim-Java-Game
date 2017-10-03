/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basketsim;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author darrenreifler
 */
class Team {

    int points = 0;
    boolean hasBall;
    String location, name;
    Player C = null, PF = null, SF = null, SG = null, PG = null;

    Team(String l, String n) {
        location = l;
        name = n;
    }

    public Team(Scanner line) { //constructor to create team from data file
        line.next(); // dump first field, t
        setLocation(line.next().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
        setName(line.next().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addPlayer(Player p) {
        switch (p.getPosition()) {
            case "C":
                C = p;
                p.assignTeam(this);
                break;
            case "PF":
                PF = p;
                p.assignTeam(this);
                break;
            case "SF":
                SF = p;
                p.assignTeam(this);
                break;
            case "SG":
                SG = p;
                p.assignTeam(this);
                break;
            case "PG":
                PG = p;
                p.assignTeam(this);
                break;
        }
    }

    public void addPlayer(Player p, Team t, String Ps) {
        switch (Ps) {
            case "C":
                C = p;
                p.pos = "C";
                break;
            case "PF":
                PF = p;
                p.pos = "PF";
                break;
            case "SF":
                SF = p;
                p.pos = "SF";
                break;
            case "SG":
                SG = p;
                p.pos = "SG";
                break;
            case "PG":
                PG = p;
                p.pos = "PG";
                break;
        }
        p.onTeam = t;
    }

    public void addC(Player p) {
        C = p;
        p.setPosition("C");
        p.assignTeam(this);
    }

    public void addPF(Player p) {
        PF = p;
        p.setPosition("PF");
        p.assignTeam(this);
    }

    public void addSF(Player p) {
        SF = p;
        p.setPosition("SF");
        p.assignTeam(this);
    }

    public void addSG(Player p) {
        SG = p;
        p.setPosition("SG");
        p.assignTeam(this);
    }

    public void addPG(Player p) {
        PG = p;
        p.setPosition("PG");
        p.assignTeam(this);
    }

    public void addC(Player p, Team t) {
        C = p;
        p.setPosition("C");
        p.assignTeam(t);
    }

    public void addPF(Player p, Team t) {
        PF = p;
        p.setPosition("PF");
        p.assignTeam(t);
    }

    public void addSF(Player p, Team t) {
        SF = p;
        p.setPosition("SF");
        p.assignTeam(t);
    }

    public void addSG(Player p, Team t) {
        SG = p;
        p.setPosition("SG");
        p.assignTeam(t);
    }

    public void addPG(Player p, Team t) {
        PG = p;
        p.setPosition("PG");
        p.assignTeam(t);
    }

    public Player getC() {
        return C;
    }

    public Player getPF() {
        return PF;
    }

    public Player getPG() {
        return PG;
    }

    public Player getSF() {
        return SF;
    }

    public Player getSG() {
        return SG;
    }

    public void removeC(Player p) {
        C = null;
        p.assignTeam(null);
    }

    public void removePF(Player p) {
        PF = null;
        p.assignTeam(null);
    }

    public void removeSF(Player p) {
        SF = null;
        p.assignTeam(null);
    }

    public void removeSG(Player p) {
        SG = null;
        p.assignTeam(null);
    }

    public void removePG(Player p) {
        PG = null;
        p.assignTeam(null);
    }
    
    public void resetPlayerStats() {
        PG.resetStats();
        SG.resetStats();
        SF.resetStats();
        PF.resetStats();
        C.resetStats();
    }

    public String getTeamMembers() {
        return "\n" + PG + ", PG\n" + SG + ", SG\n" + SF + ", SF\n" + PF + ", PF\n" + C + ", C\n";
    }

    @Override
    public String toString() {
        return String.format(getLocation() + " " + getName());
    } // end toString method
}

class Zone{
    ArrayList<Player> zonePlayers = new ArrayList<>();
    int passZone, shootZone, dribbleZone, reboundZone;
    int num;
    
    Zone(int n, int dribble, int pass, int shoot, int reb){
        num = n;
        dribbleZone = dribble;
        passZone = pass;
        shootZone = shoot;
        reboundZone = reb;
    }
    
    public void addToZone(Player p){
        zonePlayers.add(p);
    }
    
    public void removeFromZone(Player p){
        zonePlayers.remove(p);
    }
    @Override
    public String toString() {
        if (num == 1)
            return "paint";
        if (num == 2)
            return "perimeter";
        if (num == 3)
            return "three point line";
        else
            return "back court";
    } // end toString method
}

class Scores {
    Team homeTeam;
    Team awayTeam;
    int homeScore = 0;
    int awayScore = 0;
    
    Scores(Team home, Team away, int s1, int s2){
        homeTeam = home;
        awayTeam = away;
        homeScore = s1;
        awayScore = s2;
    }
    @Override
    public String toString() {
        return String.format("%30s: %2d\n%30s: %2d\n", homeTeam, homeScore, awayTeam, awayScore);
    }
}

