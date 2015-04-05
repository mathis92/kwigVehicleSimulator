/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

/**
 *
 * @author martinhudec
 */

/* HttpsHello.java
 - Copyright (c) 2014, HerongYang.com, All Rights Reserved.
 */
public class AppInit {

    static DatabaseConnector dc;

    public static void main(String[] args) throws Exception {
        dc = new DatabaseConnector();
        new RouteListGenerator(dc).startGenerator();
    }

}
