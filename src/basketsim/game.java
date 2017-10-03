/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basketsim;

/**
 *
 * @author darrenreifler
 */
class Game {

    Zone zonePaint = new Zone(1, 20, 20, 60, 60);
    Zone zoneMid = new Zone(2, 25, 25, 50, 30);
    Zone zoneOutside = new Zone(3, 25, 55, 20, 9);
    Zone zoneBackCourt = new Zone(4, 50, 49, 1, 1);
    Team homeTeam;
    Team awayTeam;
    Player withBall;
    int time, shotClock = 5;

    Game(Team home, Team away) {
        homeTeam = home;
        awayTeam = away;

        homeTeam.resetPlayerStats();
        awayTeam.resetPlayerStats();

        homeTeam.hasBall = true;
        awayTeam.hasBall = false;
        homeTeam.points = 0;
        awayTeam.points = 0;
        //assign PG to start with ball
        withBall = homeTeam.PG;

        //Assign starting zones to players
        resetZones();

        //assign defenders
        homeTeam.C.defBy = awayTeam.C;
        homeTeam.PF.defBy = awayTeam.PF;
        homeTeam.SF.defBy = awayTeam.SF;
        homeTeam.SG.defBy = awayTeam.SG;
        homeTeam.PG.defBy = awayTeam.PG;

        awayTeam.C.defBy = homeTeam.C;
        awayTeam.PF.defBy = homeTeam.PF;
        awayTeam.SF.defBy = homeTeam.SF;
        awayTeam.SG.defBy = homeTeam.SG;
        awayTeam.PG.defBy = homeTeam.PG;
    }

    public String decision() {
        double pass = 0, shoot = 0, dribble = 1, dc, pc, sc, sum, random;

        //calculate percent chance of each
        switch (withBall.inZone.num) {
            case 1:
                dribble = withBall.inZone.dribbleZone + (float) (withBall.dribble + withBall.quick - withBall.defBy.quick + withBall.str - withBall.defBy.str) / 10;
                pass = withBall.inZone.passZone + (float) (withBall.pass + (withBall.defBy.def - withBall.agg) / 5 + withBall.height - withBall.defBy.height + withBall.quick - withBall.defBy.quick) / 10;
                shoot = withBall.inZone.shootZone + (float) (withBall.shoot + (withBall.agg - withBall.defBy.def) / 5 + withBall.height - withBall.defBy.height + withBall.str - withBall.defBy.str) / 10;
                break;
            case 2:
                dribble = withBall.inZone.dribbleZone + (float) (withBall.dribble + withBall.quick - withBall.defBy.quick + withBall.str - withBall.defBy.str) / 10;
                pass = withBall.inZone.passZone + (float) (withBall.pass + (withBall.defBy.def - withBall.agg) / 5 + +withBall.quick - withBall.defBy.quick) / 10;
                shoot = withBall.inZone.shootZone + (float) (withBall.shoot + (withBall.agg - withBall.defBy.def) / 5 + (withBall.height - withBall.defBy.height + withBall.str - withBall.defBy.str) / 2) / 10;
                break;
            case 3:
                dribble = withBall.inZone.dribbleZone + (float) (withBall.dribble + withBall.quick - withBall.defBy.quick) / 10;
                pass = withBall.inZone.passZone + (float) (withBall.pass + (withBall.defBy.def - withBall.agg) / 5) / 10;
                shoot = withBall.inZone.shootZone + (float) ((withBall.shoot - 75) * 4 + (withBall.agg - withBall.defBy.def) / 5) / 10;
                break;
            case 4:
                dribble = withBall.inZone.dribbleZone + (float) (withBall.dribble + withBall.quick - withBall.defBy.quick) / 10;
                pass = withBall.inZone.passZone + (float) (withBall.pass + (withBall.defBy.def - withBall.agg) / 5) / 10;
                shoot = withBall.inZone.shootZone + (float) (withBall.shoot + (withBall.agg - withBall.defBy.def) / 5) / 100;
                break;
        }

        sum = dribble + pass + shoot;
        dc = dribble / sum;
        pc = dc + pass / sum;
        sc = pc + shoot / sum;
        random = Math.random();
        if (withBall.pos.equals("PG")) {
            pc = pc * 1.07;
        }

        //System.out.println("pass = " + pass + " shoot = " + shoot + " dribble = " +
        //        dribble);
        //Run method based on odds
        if (time == 0) {
            time = shotClock;
            return "shoot";
        } else {
            if (random <= dc) {
                time--;
                return "dribble";
            } else if (random <= pc) {
                time--;
                return "pass";
            } else {
                time = shotClock;
                return "shoot"; //if other two options are not selected default to last option
            }
        }
    }

    public Player passChoice() {
        double PG = -1, SG = -1, SF = -1, PF = -1, C = -1;

        if (!"C".equals(withBall.pos)) {
            C = Math.random();//(homeTeam.C.shoot + homeTeam.C.height) / 2 - homeTeam.C.inZone.passZone;
        }
        if (!"PF".equals(withBall.pos)) {
            PF = Math.random();//(homeTeam.PF.shoot + homeTeam.PF.height) / 2 - homeTeam.PF.inZone.passZone;
        }
        if (!"SF".equals(withBall.pos)) {
            SF = Math.random();//homeTeam.SF.shoot - homeTeam.SF.inZone.passZone;
        }
        if (!"SG".equals(withBall.pos)) {
            SG = Math.random();//homeTeam.SG.shoot - homeTeam.SG.inZone.passZone;
        }
        if (!"PG".equals(withBall.pos)) {
            PG = Math.random();//homeTeam.PG.shoot - homeTeam.PG.inZone.passZone;
        }

        if (PG >= SG && PG >= SF && PG >= PF && PG >= C) {
            return withBall.onTeam.PG;
        } else if (SG >= SF && SG >= PF && SG >= C) {
            return withBall.onTeam.SG;
        } else if (SF >= PF && SF >= C) {
            return withBall.onTeam.SF;
        } else if (PF >= C) {
            return withBall.onTeam.PF;
        } else {
            return withBall.onTeam.C;
        }
    }

    public boolean pass(Player p) {
        double success, success2, random;

        success = 0.8 + .15 * withBall.pass / 100;
        success2 = 0.8 + .15 * p.pass / 100;

        random = Math.random();
        if (random > success) {
            withBall = withBall.defBy;
            resetZones();
            return false;
        } else if (random > success2) {
            resetZones();
            withBall = p.defBy;
            return false;
        } else {
            withBall = p;
            return true;
        }
    }

    public boolean dribble() {
        double success;

        success = 0.8 + 0.15 * withBall.dribble / 100;

        if (Math.random() < success) {
            if (withBall.inZone.num == 1) {
                withBall.inZone = zoneMid;
                return true;
            } else if (withBall.inZone.num == 2) {
                if (withBall.agg * (Math.random() + 0.5) > 50) {
                    withBall.inZone = zonePaint;
                } else {
                    withBall.inZone = zoneOutside;
                }
                return true;
            } else if (withBall.inZone.num == 3) {
                withBall.inZone = zoneMid;
                return true;
            } else {
                withBall.inZone = zoneOutside;
                return true;
            }
        } else {
            resetZones();
            withBall = withBall.defBy;
            return false;
        }
    }

    public boolean shoot() {
        double random, success = 0;

        switch (withBall.inZone.num) {
            case 1:
                success = 0.5 + .4 * withBall.shoot / 100;
                break;
            case 2:
                success = 0.3 + .35 * withBall.shoot / 100;
                break;
            case 3:
                success = .2 + .3 * withBall.shoot / 100;
                break;
            case 4:
                success = .01 + .02 * withBall.shoot / 100;
                break;
        }

        random = Math.random();
        if (withBall.inZone.num == 3 || withBall.inZone.num == 4) {
            withBall.shots++;
            withBall.threes++;
            if (random <= success) {
                withBall.onTeam.points = withBall.onTeam.points + 3;
                withBall.shotsMade++;
                withBall.threesMade++;
                withBall.points = withBall.points + 3;
                return true;
            } else {
                return false;
            }
        } else {
            withBall.shots++;
            if (random <= success) {
                withBall.shotsMade++;
                withBall.points = withBall.points + 2;
                withBall.onTeam.points = withBall.onTeam.points + 2;
                return true;
            } else {
                return false;
            }
        }
    }

    public Player Rebound() {
        double rebPG1, rebSG1, rebSF1, rebPF1, rebC1, rebPG2, rebSG2, rebSF2, rebPF2, recC2;
        double sum, random, PG1c, SG1c, SF1c, PF1c, C1c, PG2c, SG2c, SF2c, PF2c, C2c;

        rebPG1 = homeTeam.PG.inZone.reboundZone * (homeTeam.PG.height - 60) / 20 * (homeTeam.PG.quick + homeTeam.PG.str) / 100;
        rebSG1 = homeTeam.SG.inZone.reboundZone * (homeTeam.SG.height - 60) / 20 * (homeTeam.SG.quick + homeTeam.SG.str) / 100;
        rebSF1 = homeTeam.SF.inZone.reboundZone * (homeTeam.SF.height - 60) / 20 * (homeTeam.SF.quick + homeTeam.SF.str) / 100;
        rebPF1 = homeTeam.PF.inZone.reboundZone * (homeTeam.PF.height - 60) / 20 * (homeTeam.PF.quick + homeTeam.PF.str) / 100;
        rebC1 = homeTeam.C.inZone.reboundZone * (homeTeam.C.height - 60) / 20 * (homeTeam.C.quick + homeTeam.C.str) / 100;
        rebPG2 = awayTeam.PG.inZone.reboundZone * (awayTeam.PG.height - 60) / 20 * (awayTeam.PG.quick + awayTeam.PG.str) / 100;
        rebSG2 = awayTeam.SG.inZone.reboundZone * (awayTeam.SG.height - 60) / 20 * (awayTeam.SG.quick + awayTeam.SG.str) / 100;
        rebSF2 = awayTeam.SF.inZone.reboundZone * (awayTeam.SF.height - 60) / 20 * (awayTeam.SF.quick + awayTeam.SF.str) / 100;
        rebPF2 = awayTeam.PF.inZone.reboundZone * (awayTeam.PF.height - 60) / 20 * (awayTeam.PF.quick + awayTeam.PF.str) / 100;
        recC2 = awayTeam.C.inZone.reboundZone * (awayTeam.C.height - 60) / 20 * (awayTeam.C.quick + awayTeam.C.str) / 100;

        sum = rebPG1 + rebSG1 + rebSF1 + rebPF1 + rebC1 + rebPG2 + rebSG2 + rebSF2 + rebPF2 + recC2;
        PG1c = rebPG1 / sum;
        SG1c = PG1c + rebSG1 / sum;
        SF1c = SG1c + rebSF1 / sum;
        PF1c = SF1c + rebPF1 / sum;
        C1c = PF1c + rebC1 / sum;
        PG2c = C1c + rebPG2 / sum;
        SG2c = PG2c + rebSG2 / sum;
        SF2c = SG2c + rebSF2 / sum;
        PF2c = SF2c + rebPF2 / sum;
        C2c = PF2c + recC2 / sum;

        random = Math.random();

        if (random <= PG1c) {
            homeTeam.PG.rebounds++;
            return homeTeam.PG;
        } else if (random <= SG1c) {
            homeTeam.SG.rebounds++;
            return homeTeam.SG;
        } else if (random <= SF1c) {
            homeTeam.SF.rebounds++;
            return homeTeam.SF;
        } else if (random <= PF1c) {
            homeTeam.PF.rebounds++;
            return homeTeam.PF;
        } else if (random <= C1c) {
            homeTeam.C.rebounds++;
            return homeTeam.C;
        } else if (random <= PG2c) {
            awayTeam.PG.rebounds++;
            return awayTeam.PG;
        } else if (random <= SG2c) {
            awayTeam.SG.rebounds++;
            return awayTeam.SG;
        } else if (random <= SF2c) {
            awayTeam.SF.rebounds++;
            return awayTeam.SF;
        } else if (random <= PF2c) {
            awayTeam.PF.rebounds++;
            return awayTeam.PF;
        } else {
            awayTeam.C.rebounds++;
            return awayTeam.C;
        }
    }

    public void resetZones() {
        time = shotClock;

        if (Math.random() < 0.7) {
            homeTeam.C.inZone = zonePaint;
        } else {
            homeTeam.C.inZone = zoneMid;
        }
        if (Math.random() < 0.6) {
            homeTeam.PF.inZone = zoneMid;
        } else {
            homeTeam.PF.inZone = zonePaint;
        }
        if (Math.random() < 0.6) {
            homeTeam.SF.inZone = zoneMid;
        } else {
            homeTeam.SF.inZone = zoneOutside;
        }
        if (Math.random() < 0.4) {
            homeTeam.SG.inZone = zoneMid;
        } else {
            homeTeam.SG.inZone = zoneOutside;
        }
        homeTeam.PG.inZone = zoneBackCourt;

        if (Math.random() < 0.7) {
            awayTeam.C.inZone = zonePaint;
        } else {
            awayTeam.C.inZone = zoneMid;
        }
        if (Math.random() < 0.6) {
            awayTeam.PF.inZone = zoneMid;
        } else {
            awayTeam.PF.inZone = zonePaint;
        }
        if (Math.random() < 0.5) {
            awayTeam.SF.inZone = zoneMid;
        } else {
            awayTeam.SF.inZone = zoneOutside;
        }
        if (Math.random() < 0.4) {
            awayTeam.SG.inZone = zoneMid;
        } else {
            awayTeam.SG.inZone = zoneOutside;
        }
        awayTeam.PG.inZone = zoneBackCourt;
    }

    public void playerMovement() {
        double random;
        if (!withBall.pos.equals("C")) {
            random = Math.random();
            if (random < 0.7) {
                homeTeam.C.inZone = zonePaint;
                awayTeam.C.inZone = zonePaint;
            } else if (random < 0.95) {
                homeTeam.C.inZone = zoneMid;
                awayTeam.C.inZone = zoneMid;
            } else {
                homeTeam.C.inZone = zoneOutside;
                awayTeam.C.inZone = zoneOutside;
            }
        }
        if (!withBall.pos.equals("PF")) {
            random = Math.random();
            if (random < 0.5) {
                homeTeam.PF.inZone = zonePaint;
                awayTeam.PF.inZone = zonePaint;
            } else if (random < 0.9) {
                homeTeam.PF.inZone = zoneMid;
                awayTeam.PF.inZone = zoneMid;
            } else {
                homeTeam.PF.inZone = zoneOutside;
                awayTeam.PF.inZone = zoneOutside;
            }
        }
        if (!withBall.pos.equals("SF")) {
            random = Math.random();
            if (random < 0.25) {
                homeTeam.SF.inZone = zonePaint;
                awayTeam.SF.inZone = zonePaint;
            } else if (random < 0.75) {
                homeTeam.SF.inZone = zoneMid;
                awayTeam.SF.inZone = zoneMid;
            } else {
                homeTeam.SF.inZone = zoneOutside;
                awayTeam.SF.inZone = zoneOutside;
            }
        }
        if (!withBall.pos.equals("SG")) {
            random = Math.random();
            if (random < 0.2) {
                homeTeam.SG.inZone = zonePaint;
                awayTeam.SG.inZone = zonePaint;
            } else if (random < 0.6) {
                homeTeam.SG.inZone = zoneMid;
                awayTeam.SG.inZone = zoneMid;
            } else {
                homeTeam.SG.inZone = zoneOutside;
                awayTeam.SG.inZone = zoneOutside;
            }
        }
        if (!withBall.pos.equals("PG")) {
            random = Math.random();
            if (random < 0.1) {
                homeTeam.PG.inZone = zonePaint;
                awayTeam.PG.inZone = zonePaint;
            } else if (random < 0.3) {
                homeTeam.PG.inZone = zoneMid;
                awayTeam.PG.inZone = zoneMid;
            } else {
                homeTeam.PG.inZone = zoneOutside;
                awayTeam.PG.inZone = zoneOutside;
            }
        }
    }
}

