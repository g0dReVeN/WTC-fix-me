package Broker;

import java.sql.*;

public class DBController {
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private final String URL = System.getProperty("user.dir") + "/src/main/java/swingy/model/storage/heroDB";

    public DBController() {
        try {
            this.conn = DriverManager.getConnection("jdbc:h2:" + URL, "admin", "admin");

            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql =    /*" DROP TABLE IF EXISTS hero; " +*/ " CREATE TABLE IF NOT EXISTS hero " +
                    "(id IDENTITY NOT NULL PRIMARY KEY, " +
                    "name VARCHAR(8) NOT NULL, " +
                    " class VARCHAR(6) NOT NULL, " +
                    " XP INTEGER NOT NULL, " +
                    " lvl INTEGER NOT NULL, " +
                    " lvlUp INTEGER NOT NULL, " +
                    " maxHP INTEGER NOT NULL, " +
                    " HP INTEGER NOT NULL, " +
                    " atk INTEGER NOT NULL, " +
                    " def INTEGER NOT NULL, " +
                    " wname VARCHAR(20), " +
                    " wlvl INTEGER NOT NULL, " +
                    " wboost INTEGER NOT NULL, " +
                    " aname VARCHAR(20), " +
                    " alvl INTEGER NOT NULL, " +
                    " aboost INTEGER NOT NULL, " +
                    " hname VARCHAR(20), " +
                    " hlvl INTEGER NOT NULL, " +
                    " hboost INTEGER NOT NULL)";
            this.stmt.executeUpdate(sql);
            this.rs = this.stmt.executeQuery("SELECT hero. * FROM hero");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResultSet() throws SQLException {
        return this.rs;
    }

    public void refreshResultSet() throws SQLException {
        this.rs = this.stmt.executeQuery("SELECT hero. * FROM hero");
    }

//    public void updateHero(Profession hero) throws SQLException {
//        this.rs.absolute(this.rs.getRow());
//        this.rs.updateInt("lvl", hero.getLvl());
//        this.rs.updateInt("XP", hero.getXP());
//        this.rs.updateInt("lvl", hero.getLvl());
//        this.rs.updateInt("lvlUp", hero.getLvlUp());
//        this.rs.updateInt("maxHP", hero.getMaxHP());
//        this.rs.updateInt("HP", hero.getHP());
//        this.rs.updateInt("atk", hero.getAtk());
//        this.rs.updateInt("def", hero.getDef());
//        this.rs.updateString("wname", hero.getWeapon().getName());
//        this.rs.updateInt("wlvl", hero.getWeapon().getLvl());
//        this.rs.updateInt("wboost", hero.getWeapon().getBoost());
//        this.rs.updateString("aname", hero.getArmour().getName());
//        this.rs.updateInt("alvl", hero.getArmour().getLvl());
//        this.rs.updateInt("aboost", hero.getArmour().getBoost());
//        this.rs.updateString("hname", hero.getHelm().getName());
//        this.rs.updateInt("hlvl", hero.getHelm().getLvl());
//        this.rs.updateInt("hboost", hero.getHelm().getBoost());
//        this.rs.updateRow();
//    }
//
//    public void createHero(Profession hero, String name) throws SQLException {
//        this.rs.moveToInsertRow();
//        this.rs.updateString("name", name);
//        this.rs.updateString("class", hero.getType());
//        this.rs.updateInt("lvl", hero.getLvl());
//        this.rs.updateInt("XP", hero.getXP());
//        this.rs.updateInt("lvl", hero.getLvl());
//        this.rs.updateInt("lvlUp", hero.getLvlUp());
//        this.rs.updateInt("maxHP", hero.getMaxHP());
//        this.rs.updateInt("HP", hero.getHP());
//        this.rs.updateInt("atk", hero.getAtk());
//        this.rs.updateInt("def", hero.getDef());
//        this.rs.updateString("wname", hero.getWeapon().getName());
//        this.rs.updateInt("wlvl", hero.getWeapon().getLvl());
//        this.rs.updateInt("wboost", hero.getWeapon().getBoost());
//        this.rs.updateString("aname", hero.getArmour().getName());
//        this.rs.updateInt("alvl", hero.getArmour().getLvl());
//        this.rs.updateInt("aboost", hero.getArmour().getBoost());
//        this.rs.updateString("hname", hero.getHelm().getName());
//        this.rs.updateInt("hlvl", hero.getHelm().getLvl());
//        this.rs.updateInt("hboost", hero.getHelm().getBoost());
//        this.rs.insertRow();
//        refreshResultSet();
//        this.rs.last();
//    }

    public void endConn() {
        try {
            this.stmt.close();
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
