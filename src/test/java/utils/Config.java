package utils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Config {
    public static Map<String, String> config = new HashMap<>();
    public static Properties propMain = new Properties();


    public static Map <String, String> getConfig(){
        if (config.isEmpty()){
            loadConfig();
        }
        return config;
    }
    private static Map <String, String> loadConfig(){
        String environment = System.getProperty("env");
        if (environment==null||!environment.equals("dev")) {
            environment = "qa";
        }
        try{
            FileInputStream fisQa = new FileInputStream("src/test/resources/"+environment+".properties");
            propMain.load(fisQa);
            config.put("ServerUrl", propMain.getProperty("ServerUrl"));
            config.put("portNo", propMain.getProperty("portNo"));
        }catch (Exception e){
        }
        return config;
    }
    public static Integer getPort(){
        Integer port;
        try {
            port = Integer.parseInt(Config.getConfig().get("portNo"));
        }catch (Exception e){
            port=null;
        }
        return port;
    }





}
