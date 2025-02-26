package test;

import db.DB;
import service.SystemManager;

import java.sql.Connection;

public class Test01 {
    public static void main(String[] args) {
        SystemManager sm = new SystemManager();

        sm.readQuestions();
    }
}
