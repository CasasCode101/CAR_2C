package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.util.Scanner;


public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        try {
            Authentication authentication = new Authentication(userDao, scanner);
            authentication.start();
        } finally {
            scanner.close();
        }
    }
}
