/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar;

import org.traccar.database.ConnectionManager;
import org.traccar.database.DataManager;
import org.traccar.database.IdentityManager;
import org.traccar.database.PermissionsManager;
import org.traccar.geocode.GisgraphyReverseGeocoder;
import org.traccar.geocode.GoogleReverseGeocoder;
import org.traccar.geocode.NominatimReverseGeocoder;
import org.traccar.geocode.ReverseGeocoder;
import org.traccar.helper.Log;
import org.traccar.http.WebServer;

public class Context {
    
    private static Config config;
    
    public static Config getConfig() {
        return config;
    }

    private static boolean loggerEnabled;

    public static boolean isLoggerEnabled() {
        return loggerEnabled;
    }
    
    private static IdentityManager identityManager;
    
    public static IdentityManager getIdentityManager() {
        return identityManager;
    }

    private static DataManager dataManager;

    public static DataManager getDataManager() {
        return dataManager;
    }

    private static ConnectionManager connectionManager;

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    private static PermissionsManager permissionsManager;

    public static PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    private static ReverseGeocoder reverseGeocoder;

    public static ReverseGeocoder getReverseGeocoder() {
        return reverseGeocoder;
    }

    private static WebServer webServer;

    public static WebServer getWebServer() {
        return webServer;
    }

    private static ServerManager serverManager;

    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static void init(String[] arguments) throws Exception {

        config = new Config();
        if (arguments.length > 0) {
            config.load(arguments[0]);
        }

        loggerEnabled = config.getBoolean("logger.enable");
        if (loggerEnabled) {
            Log.setupLogger(config);
        }

        dataManager = new DataManager(config);
        identityManager = dataManager;

        connectionManager = new ConnectionManager();
        if (!config.getBoolean("web.old")) {
            permissionsManager = new PermissionsManager();
        }

        if (config.getBoolean("geocoder.enable")) {
            String type = config.getString("geocoder.type");
            String url = config.getString("geocoder.url");
            if (type != null && type.equals("nominatim")) {
                reverseGeocoder = new NominatimReverseGeocoder(url);
            } else if (type != null && type.equals("gisgraphy")) {
                reverseGeocoder = new GisgraphyReverseGeocoder(url);
            } else {
                reverseGeocoder = new GoogleReverseGeocoder();
            }
        }

        if (config.getBoolean("web.enable")) {
            webServer = new WebServer(config);
        }

        serverManager = new ServerManager();

        connectionManager.init(dataManager);
        serverManager.init();
    }

    /**
     * Initialize context for unit testing
     */
    public static void init(IdentityManager identityManager) {
        config = new Config();
        connectionManager = new ConnectionManager();
        Context.identityManager = identityManager;
    }

}
