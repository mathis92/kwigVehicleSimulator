/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author martinhudec
 */

/* HttpsHello.java
 - Copyright (c) 2014, HerongYang.com, All Rights Reserved.
 */
public class AppInit {

    public static void main(String[] args) throws Exception {
        DatabaseConnector dc = new DatabaseConnector();
        dc.testConnection();
    }

}
