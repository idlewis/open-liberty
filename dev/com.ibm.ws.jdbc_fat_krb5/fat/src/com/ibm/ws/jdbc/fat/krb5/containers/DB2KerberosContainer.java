/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jdbc.fat.krb5.containers;

import static com.ibm.ws.jdbc.fat.krb5.containers.KerberosContainer.KRB5_KDC;
import static com.ibm.ws.jdbc.fat.krb5.containers.KerberosContainer.KRB5_REALM;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.jdbc.fat.krb5.FATSuite;

import componenttest.custom.junit.runner.FATRunner;

public class DB2KerberosContainer extends Db2Container {

    private static final Class<?> c = DB2KerberosContainer.class;
    private static final Path reuseCache = Paths.get("..", "..", "cache", "db2.properties");
    // NOTE: If this is ever updated, don't forget to push to docker hub, but DO NOT overwrite existing versions
    private static final String IMAGE = "aguibert/krb5-db2:1.0";

    private boolean reused = false;
    private String reused_hostname;
    private int reused_port;

    public DB2KerberosContainer(Network network) {
        super(IMAGE);
        withNetwork(network);
    }

    @Override
    protected void configure() {
        acceptLicense();
        withExposedPorts(50000);
        withEnv("KRB5_REALM", KRB5_REALM);
        withEnv("KRB5_KDC", KRB5_KDC);
        withEnv("DB2_KRB5_PRINCIPAL", "db2srvc@EXAMPLE.COM");
        waitingFor(new LogMessageWaitStrategy()
                        .withRegEx("^.*SETUP SCRIPT COMPLETE.*$")
                        .withStartupTimeout(Duration.ofMinutes(FATRunner.FAT_TEST_LOCALRUN ? 10 : 25)));
        withLogConsumer(DB2KerberosContainer::log);
        withReuse(true);
    }

    private static void log(OutputFrame frame) {
        String msg = frame.getUtf8String();
        if (msg.endsWith("\n"))
            msg = msg.substring(0, msg.length() - 1);
        Log.info(DB2KerberosContainer.class, "[DB2]", msg);
    }

    @Override
    public void start() {
        if (hasCachedContainers()) {
            // If this is a local run and a cache file exists, that means a DB2 container is already running
            // and we can just read the host/port from the cache file
            Log.info(c, "start", "Found existing container cache file. Skipping container start.");
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(reuseCache.toFile()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            reused = true;
            reused_hostname = props.getProperty("db2.hostname");
            reused_port = Integer.valueOf(props.getProperty("db2.port"));
            Log.info(c, "start", "Found existing container at host = " + reused_hostname);
            Log.info(c, "start", "Found existing container on port = " + reused_port);
            return;
        }

        super.start();

        if (FATSuite.REUSE_CONTAINERS) {
            Log.info(c, "start", "Saving DB2 properties for future runs at: " + reuseCache.toAbsolutePath());
            try {
                Files.createDirectories(reuseCache.getParent());
                Properties props = new Properties();
                if (reuseCache.toFile().exists()) {
                    try (FileInputStream fis = new FileInputStream(reuseCache.toFile())) {
                        props.load(fis);
                    }
                }
                props.setProperty("db2.hostname", getContainerIpAddress());
                props.setProperty("db2.port", "" + getMappedPort(50000));
                props.store(new FileWriter(reuseCache.toFile()), "Generated by FAT run");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        if (FATSuite.REUSE_CONTAINERS) {
            Log.info(c, "stop", "Leaving container running so it can be used in later runs");
            return;
        } else {
            Log.info(c, "stop", "Stopping container");
            super.stop();
        }
    }

    @Override
    public Integer getMappedPort(int originalPort) {
        return (reused && originalPort == 50000) ? reused_port : super.getMappedPort(originalPort);
    }

    @Override
    public String getContainerIpAddress() {
        return reused ? reused_hostname : super.getContainerIpAddress();
    }

    @Override
    public String getUsername() {
        return "db2inst1";
    }

    @Override
    public String getPassword() {
        return "password";
    }

    @Override
    public String getDatabaseName() {
        return "testdb";
    }

    @Override
    public Db2Container withUsername(String username) {
        throw new UnsupportedOperationException("Username is hardcoded in container");
    }

    @Override
    public Db2Container withPassword(String password) {
        throw new UnsupportedOperationException("Password is hardcoded in container");
    }

    @Override
    public Db2Container withDatabaseName(String dbName) {
        throw new UnsupportedOperationException("DB name is hardcoded in container");
    }

    private static boolean hasCachedContainers() {
        if (!FATSuite.REUSE_CONTAINERS)
            return false;
        if (!reuseCache.toFile().exists())
            return false;
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(reuseCache.toFile())) {
            props.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return props.containsKey("db2.hostname") &&
               props.containsKey("db2.port");
    }

}
